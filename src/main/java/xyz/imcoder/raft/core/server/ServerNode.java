package xyz.imcoder.raft.core.server;

import xyz.imcoder.raft.core.Status;
import xyz.imcoder.raft.core.config.ServerConfig;
import xyz.imcoder.raft.core.handler.MessageHandler;
import xyz.imcoder.raft.core.handler.TimeoutEventHandler;
import xyz.imcoder.raft.core.log.Log;
import xyz.imcoder.raft.core.message.VoteRequestMessage;
import xyz.imcoder.raft.core.message.VoteResponseMessage;
import xyz.imcoder.raft.core.rpc.RpcClient;
import xyz.imcoder.raft.core.rpc.RpcResponse;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author sunsai
 * @Date 2019/1/8 6:03 PM
 **/
public class ServerNode implements MessageHandler, TimeoutEventHandler {

    private long currentTerm;

    private AtomicInteger voteFor = new AtomicInteger(0);

    private Log[] logs;

    private long commitIndex;

    private long lastApplied = 0;

    private long[] nextIndex;

    private long[] matchIndex;

    private Status status = Status.FOLLOWER;

    private RpcClient rpcClient;

    private long lastReceiveLeaderHeartbeatTime = 0L;

    private ServerConfig config;

    private int selfNodeId;

    private ServerInfo selfServerInfo;

    private ServerInfo[] serverInfos;

    public ServerNode(ServerConfig config, RpcClient rpcClient, ServerInfo[] serverInfos) {
        this.rpcClient = rpcClient;
        this.config = config;
        this.serverInfos = serverInfos;
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
        VoteResponseMessage response = new VoteResponseMessage();
        response.setTerm(currentTerm);
        if (checkCanVoteFor(voteMessage)) {
            //
            response.setWimVote(true);
        } else {
            response.setWimVote(false);
        }
        return response;
    }

    private boolean checkCanVoteFor(VoteRequestMessage voteMessage) {
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
    public void onHeartbeatTimeoutCheck() {
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
            List<Future<VoteResponseMessage>> responseFutures = new ArrayList<>(serverInfos.length);
            for (ServerInfo serverInfo: serverInfos) {
                Future<VoteResponseMessage> response = rpcClient.vote(serverInfo, new VoteRequestMessage());
                responseFutures.add(response);
            }
            int winMinVoteCount = (serverInfos.length + 1) / 2 + 1;
            long afterSendVote = System.currentTimeMillis();

            Set<VoteResponseMessage> alreadVoteMessageSet = new HashSet<>();
            while (true) {

                long current = System.currentTimeMillis();
                if ((current - afterSendVote) > config.getVoteTimeout()) {
                    onChangeStatus(Status.FOLLOWER, Status.CANDIDATE);
                    break;
                }

                for (Future<VoteResponseMessage> future: responseFutures) {
                    if (future.isDone()) {
                        try {
                            VoteResponseMessage msg = future.get();
                            if (!alreadVoteMessageSet.contains(msg)) {
                                alreadVoteMessageSet.add(msg);
                                if (msg.isWimVote()) {
                                    winVoteCount = winVoteCount + 1;
                                    if (winVoteCount >= winMinVoteCount) {
                                        onChangeStatus(Status.LEADER, Status.CANDIDATE);
                                        break;
                                    }
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private boolean changeToCandidate() {
        if (voteFor.compareAndSet(0, selfNodeId)) {
            onChangeStatus(Status.CANDIDATE, status);
            return true;
        }
        return false;
    }

    private void onChangeStatus(Status newStatus, Status oldStatus) {
        status = newStatus;
        if (status == Status.FOLLOWER) {
            voteFor.set(0);
        } else if (status == Status.CANDIDATE) {

        } else if (status == Status.LEADER) {
            voteFor.set(0);
            sendHeartBeat();
        }
    }

    private void sendHeartBeat() {

    }

    @Override
    public void onSendHeartbeatCheck() {
        if (status != Status.LEADER) {
            return;
        }
        // 如果是leader的话，才发送
        for (ServerInfo serverInfo: serverInfos) {
            rpcClient.heartBeat(serverInfo, new Object());
        }
    }
}
