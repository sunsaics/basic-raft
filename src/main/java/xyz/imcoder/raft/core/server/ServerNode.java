package xyz.imcoder.raft.core.server;

import xyz.imcoder.raft.core.Status;
import xyz.imcoder.raft.core.config.ServerConfig;
import xyz.imcoder.raft.core.handler.MessageHandler;
import xyz.imcoder.raft.core.handler.TimeEventHandler;
import xyz.imcoder.raft.core.log.Log;
import xyz.imcoder.raft.core.message.*;
import xyz.imcoder.raft.core.rpc.RpcClient;
import xyz.imcoder.raft.core.rpc.RpcResponse;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @Author sunsai
 * @Date 2019/1/8 6:03 PM
 **/
public class ServerNode implements MessageHandler, TimeEventHandler {

    private long currentTerm = 0;

    private AtomicInteger voteFor = new AtomicInteger(0);

    private ArrayList<Log> logs;

    private AtomicLong commitIndex = new AtomicLong(0);

    private AtomicLong lastApplied = new AtomicLong(0);

    private Map<Integer, Long> nextIndex;

    private Map<Integer, Long> matchIndex;

    private Status status = Status.FOLLOWER;

    private RpcClient rpcClient;

    private long lastReceiveLeaderHeartbeatTime = 0L;

    private ServerConfig config;

    private ServerInfo selfServerInfo;

    private List<ServerInfo> serverInfos;

    private Map<Integer, ServerInfo> clusterServerMap = null;

    private long _zeroIndexOffset = 0;

    public ServerNode(ServerConfig config, RpcClient rpcClient, List<ServerInfo> clusterServerInfoList) {
        this.rpcClient = rpcClient;
        this.config = config;
        this.serverInfos = clusterServerInfoList;
        clusterServerMap = serverInfos.stream().collect(Collectors.toMap(ServerInfo::getServerNodeId, x->x));
        selfServerInfo = config.getSelfServerInfo();
        nextIndex = new ConcurrentHashMap<>();
        matchIndex = new ConcurrentHashMap<>();
    }

    @Override
    public CopyResponseMessage onCopyMessage(ServerInfo fromServerInfo, CopyRequestMessage message) {
        HeartBeatResponseMessage heartBeatResponseMessage = onHeartBeatMessage(fromServerInfo, message);
        if (heartBeatResponseMessage.isSuccess()) {
            long newMinIndex = Long.MAX_VALUE;
            for (int i = 1; i <= message.getEntries().size(); i ++) {
                long newIndex = message.getPrevLogIndex() + i;
                Log newLog = message.getEntries().get(i - 1);
                if (newLog.getIndex() < newMinIndex) {
                    newMinIndex = newLog.getIndex();
                }
                Log oldLog = getLog(newIndex);
                if (Objects.nonNull(oldLog)) {
                    if (oldLog.getTerm() != newLog.getTerm()) {
                        removeLogFromIndex(newIndex);
                        break;
                    }
                } else {
                    setLog(newIndex, newLog);
                }
            }
            if (message.getLeaderCommit() > commitIndex.get()) {
                commitIndex.set(Long.min(newMinIndex, message.getLeaderCommit()));
            }
        }
        return new CopyResponseMessage(heartBeatResponseMessage.getTerm(), heartBeatResponseMessage.isSuccess());
    }

    @Override
    public VoteResponseMessage onVoteMessage(ServerInfo fromServerInfo, VoteRequestMessage voteMessage) {
        // 如果自己的选票还没有投出，则可以投票
        return new VoteResponseMessage(currentTerm, checkCanVoteFor(voteMessage));
    }

    @Override
    public RpcResponse onPreVoteMessage(ServerInfo fromServerInfo, Object object) {
        return null;
    }

    @Override
    public CommitResponseMessage onCommitMessage(ServerInfo fromServerInfo, CommitRequestMessage message) {
        return null;
    }

    @Override
    public HeartBeatResponseMessage onHeartBeatMessage(ServerInfo leaderServerInfo, HeartBeatRequestMessage message) {
        System.out.println("onHeartBeatMessage");
        if (status == Status.CANDIDATE) {
            onChangeStatus(Status.FOLLOWER, Status.CANDIDATE);
        } if (status == Status.FOLLOWER) {
            voteFor.set(0);
        }
        boolean isSuccess = true;
        if (message.getTerm() < currentTerm) {
            isSuccess = false;
        }
        if (message.getPrevLogIndex() == -1) {
            isSuccess = true;
        } else {
            Log preLog = getLog(message.getPrevLogIndex());
            if (Objects.isNull(preLog)) {
                isSuccess = false;
            } else if( preLog.getTerm() != message.getPrevLogTerm()) {
                isSuccess = false;
            }
        }

        if(isSuccess) {
            currentTerm = message.getTerm();
            applyLogCheck();
        }

        lastReceiveLeaderHeartbeatTime = System.currentTimeMillis();
        return new HeartBeatResponseMessage(currentTerm, isSuccess);
    }



    @Override
    public void onHeartbeatTimeoutCheck() {
        System.out.println("onHeartbeatTimeoutCheck");
        long now = System.currentTimeMillis();
        boolean isTimeout = (now - lastReceiveLeaderHeartbeatTime) > config.getHeartbeatTimeout();
        if (isTimeout && status == Status.FOLLOWER) {
            Random random = new Random();
            try {
                Thread.sleep(random.nextInt(150) + 150);
                startVote();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onSendHeartbeatCheck() {
        System.out.println("onSendHeartbeatCheck");
        System.out.println("nodeId:" + selfServerInfo.getServerNodeId() + "  is " + status) ;
        if (status != Status.LEADER) {
            return;
        }
        // 如果是leader的话，才发送
        sendHeartBeatMessage();
    }

    private void setLog(long index, Log log) {
        long realIndex = getRealIndex(index);
        logs.set((int)realIndex, log);
    }

    private void removeLogFromIndex(long beginIndex) {
        logs.removeIf(x -> x.getIndex() >= beginIndex);
    }

    private Log getLog(long index) {
        long realIndex = getRealIndex(index);
        if (logs.size() <= realIndex) {
            return null;
        }
        return logs.get((int)realIndex);
    }

    private long getRealIndex(long index) {
        return index - _zeroIndexOffset;
    }

    /**
     * 应用日志到状态机
     */
    private void applyLogCheck() {
        if (commitIndex.get() > lastApplied.get()) {
            long willApplyIndex = lastApplied.incrementAndGet();
            applyLogToStateMachine(willApplyIndex);
        }
    }

    private Log getLastLog() {
        if (logs.isEmpty()) {
            return null;
        }
        return logs.get(logs.size() - 1);
    }

    private void applyLogToStateMachine(long applyIndex) {
        Log log = getLog(applyIndex);
        System.out.println("apply to state machine : index=" + log.getIndex() + ", term=" + log.getTerm() + ", content=" + log.getContent()) ;
    }

    private boolean checkCanVoteFor(VoteRequestMessage voteMessage) {
        System.out.println("selfNodeId=" + selfServerInfo.getServerNodeId() + ", status=" + status + ", receiveCandidateId=" + voteMessage.getCandidateId() + ", voteFor=" + voteFor);
        if (!selfIsNewer(voteMessage)
                &&status == Status.FOLLOWER
                && voteFor.compareAndSet(0, voteMessage.getCandidateId())) {

            return true;
        }
        return false;
    }

    private boolean selfIsNewer(VoteRequestMessage voteMessage) {
        if (currentTerm > voteMessage.getTerm()) {
            return true;
        }

        Log lastLog = getLastLog();
        if (Objects.nonNull(lastLog)) {
            if (lastLog.getTerm() > voteMessage.getLastCommitTerm()) {
                return true;
            }
            if (lastLog.getIndex() > voteMessage.getLastLogIndex()) {
                return true;
            }
        }
        return false;
    }

    private void startVote() {
        // 选举自己
        if (changeToCandidate()) {
            int winVoteCount = 1;
            currentTerm ++;
            List<Future<Object>> responseFutures = new ArrayList<>(serverInfos.size());
            for (ServerInfo serverInfo: serverInfos) {
                Future<Object> response = null;
                try {
                    response = rpcClient.vote(serverInfo, new VoteRequestMessage(currentTerm, selfServerInfo.getServerNodeId(), 1L,1L));
                    responseFutures.add(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            int winMinVoteCount = (serverInfos.size() + 1) / 2 + 1;
            long afterSendVote = System.currentTimeMillis();

            Set<VoteResponseMessage> alreadyVoteMessageSet = new HashSet<>();
            while (true) {
                long current = System.currentTimeMillis();
                if ((current - afterSendVote) > config.getVoteTimeout()) {
                    onChangeStatus(Status.FOLLOWER, Status.CANDIDATE);
                    break;
                }

                if (alreadyVoteMessageSet.size() == responseFutures.size()) {
                    onChangeStatus(Status.FOLLOWER, Status.CANDIDATE);
                    break;
                }
                // todo 这段代码写的不好，需要重构
                for (Future<Object> future: responseFutures) {
                    if (future.isDone()) {
                        try {
                            VoteResponseMessage msg = (VoteResponseMessage) future.get();
                            if (!alreadyVoteMessageSet.contains(msg)) {
                                alreadyVoteMessageSet.add(msg);
                                if (msg.isWimVote()) {
                                    winVoteCount = winVoteCount + 1;
                                    if (winVoteCount >= winMinVoteCount) {
                                        onChangeStatus(Status.LEADER, Status.CANDIDATE);
                                        break;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private boolean changeToCandidate() {
        if (voteFor.compareAndSet(0, selfServerInfo.getServerNodeId())) {
            onChangeStatus(Status.CANDIDATE, status);
            return true;
        }
        return false;
    }

    private void onChangeStatus(Status newStatus, Status oldStatus) {
        System.out.println("nodeId:" + selfServerInfo.getServerNodeId() + " currentStatus = " + status + " newStatus = " + newStatus);
        if (status == oldStatus) {
            synchronized (this) {
                if (status == oldStatus) {
                    status = newStatus;
                }
            }
        }
        if (status != newStatus) {
            return;
        }
        if (status == Status.FOLLOWER) {
            voteFor.set(0);
        } else if (status == Status.CANDIDATE) {

        } else if (status == Status.LEADER) {
            voteFor.set(0);
            sendHeartBeatMessage();
        }
    }

    private void sendHeartBeatMessage() {
        for (ServerInfo serverInfo: serverInfos) {
            HeartBeatRequestMessage message = new HeartBeatRequestMessage();
            message.setTerm(currentTerm);
            message.setLeaderCommit(commitIndex.get());
            message.setLeaderId(selfServerInfo.getServerNodeId());
            try {
                HeartBeatResponseMessage response = rpcClient.heartBeat(serverInfo, message);
                if (response.isSuccess()) {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
