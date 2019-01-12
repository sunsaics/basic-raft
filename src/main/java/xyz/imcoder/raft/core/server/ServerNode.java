package xyz.imcoder.raft.core.server;

import xyz.imcoder.raft.core.Status;
import xyz.imcoder.raft.core.config.ServerConfig;
import xyz.imcoder.raft.core.handler.MessageHandler;
import xyz.imcoder.raft.core.handler.TimeEventHandler;
import xyz.imcoder.raft.core.log.Log;
import xyz.imcoder.raft.core.message.HeartBeatRequestMessage;
import xyz.imcoder.raft.core.message.HeartBeatResponseMessage;
import xyz.imcoder.raft.core.message.VoteRequestMessage;
import xyz.imcoder.raft.core.message.VoteResponseMessage;
import xyz.imcoder.raft.core.rpc.RpcClient;
import xyz.imcoder.raft.core.rpc.RpcResponse;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Author sunsai
 * @Date 2019/1/8 6:03 PM
 **/
public class ServerNode implements MessageHandler, TimeEventHandler {

    private long currentTerm = 0;

    private AtomicInteger voteFor = new AtomicInteger(0);

    private Log[] logs;

    private long commitIndex = 0;

    private long lastApplied = 0;

    private Map<Integer, Long> nextIndex;

    private Map<Integer, Long> matchIndex;

    private Status status = Status.FOLLOWER;

    private RpcClient rpcClient;

    private long lastReceiveLeaderHeartbeatTime = 0L;

    private ServerConfig config;

    private ServerInfo selfServerInfo;

    private List<ServerInfo> serverInfos;

    private Map<Integer, ServerInfo> clusterServerMap;

    public ServerNode(ServerConfig config, RpcClient rpcClient, List<ServerInfo> clusterServerInfoList) {
        this.rpcClient = rpcClient;
        this.config = config;
        this.serverInfos = clusterServerInfoList;
        clusterServerMap = serverInfos.stream().collect(Collectors.toMap(ServerInfo::getServerNodeId, x->x));
        selfServerInfo = config.getSelfServerInfo();
        nextIndex = new ConcurrentHashMap<>();
        matchIndex = new ConcurrentHashMap<>();
    }

    public void start() {

    }

    @Override
    public RpcResponse onCopyMessage(ServerInfo fromServerInfo, Object object) {

        return null;
    }

    @Override
    public VoteResponseMessage onVoteMessage(ServerInfo fromServerInfo, VoteRequestMessage voteMessage) {
        // 如果自己的选票还没有投出，则可以投票
        return new VoteResponseMessage(currentTerm, checkCanVoteFor(voteMessage));
    }

    private boolean checkCanVoteFor(VoteRequestMessage voteMessage) {
        System.out.println("selfNodeId=" + selfServerInfo.getServerNodeId() + ", status=" + status + ", receiveCandidateId=" + voteMessage.getCandidateId() + ", voteFor=" + voteFor);
        if (status == Status.FOLLOWER && voteFor.compareAndSet(0, voteMessage.getCandidateId())) {
            return true;
        }
        return false;
    }


    @Override
    public RpcResponse onPreVoteMessage(ServerInfo fromServerInfo, Object object) {
        return null;
    }

    @Override
    public RpcResponse onCommitMessage(ServerInfo fromServerInfo, Object message) {
        return null;
    }

    @Override
    public HeartBeatResponseMessage onHeartBeatMessage(ServerInfo leaderServerInfo, HeartBeatRequestMessage message) {
        System.out.println("onHeartBeatMessage");
        if (status == Status.CANDIDATE) {
            onChangeStatus(Status.FOLLOWER, Status.CANDIDATE);
        } if (status == Status.FOLLOWER) {
            voteFor.set(0);
            currentTerm = message.getTerm();
        }
        lastReceiveLeaderHeartbeatTime = System.currentTimeMillis();
        return new HeartBeatResponseMessage(currentTerm, true);
    }

    @Override
    public void onHeartbeatTimeoutCheck() {
        System.out.println("onHeartbeatTimeoutCheck");
        long now = System.currentTimeMillis();
        boolean isTimeout = (now - lastReceiveLeaderHeartbeatTime) > config.getHeartbeatTimeout();
        if (isTimeout && status == Status.FOLLOWER) {
            startVote();
        }
    }

    private void startVote() {
        // 选举自己
        if (changeToCandidate()) {
            int winVoteCount = 1;
            List<Future<Object>> responseFutures = new ArrayList<>(serverInfos.size());
            for (ServerInfo serverInfo: serverInfos) {
                Future<Object> response = null;
                try {
                    response = rpcClient.vote(serverInfo, new VoteRequestMessage(currentTerm + 1, selfServerInfo.getServerNodeId(), 1L,1L));
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
            currentTerm = currentTerm + 1;
            sendHeartBeatMessage();
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

    private void sendHeartBeatMessage() {
        for (ServerInfo serverInfo: serverInfos) {
            HeartBeatRequestMessage message = new HeartBeatRequestMessage();
            message.setTerm(currentTerm);
            message.setLeaderCommit(commitIndex);
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
