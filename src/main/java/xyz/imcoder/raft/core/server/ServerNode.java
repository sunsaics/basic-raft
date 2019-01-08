package xyz.imcoder.raft.core.server;

import xyz.imcoder.raft.core.Status;
import xyz.imcoder.raft.core.config.ServerConfig;
import xyz.imcoder.raft.core.handler.MessageHandler;
import xyz.imcoder.raft.core.handler.TimeoutEventHandler;
import xyz.imcoder.raft.core.log.Log;
import xyz.imcoder.raft.core.rpc.RpcClient;
import xyz.imcoder.raft.core.rpc.RpcResponse;

/**
 * @Author sunsai
 * @Date 2019/1/8 6:03 PM
 **/
public class ServerNode implements MessageHandler, TimeoutEventHandler {

    private long currentTerm;

    private long voteFor;

    private Log[] logs;

    private long commitIndex;

    private long lastApplied = 0;

    private long[] nextIndex;

    private long[] matchIndex;

    private Status status = Status.FOLLOWER;

    private RpcClient rpcClient;

    private long lastReceiveLeaderHeartbeatTime = 0L;

    private ServerConfig config;

    private long selfNodeId;

    private ServerInfo selfServerInfo;

    private ServerInfo[] serverInfos;

    public ServerNode(ServerConfig config, RpcClient rpcClient, ServerInfo[] serverInfos) {
        this.rpcClient = rpcClient;
        this.config = config;
        this.serverInfos = serverInfos;
    }

    public void start() {

    }

    public RpcResponse onCopyMessage(ServerInfo fromServerInfo, Object object) {

        return null;
    }

    public RpcResponse onVoteMessage(ServerInfo fromServerInfo, Object object) {
        return null;
    }

    public RpcResponse onPreVoteMessage(ServerInfo fromServerInfo, Object object) {
        return null;
    }

    public RpcResponse onCommitMessage(ServerInfo fromServerInfo, Object message) {
        return null;
    }

    public void onHeartbeatTimeoutCheck() {
        long now = System.currentTimeMillis();
        boolean isTimeout = (now - lastReceiveLeaderHeartbeatTime) > config.getHeartbeatTimeout();
        if (isTimeout) {
            // 选举自己
            status = Status.CANDIDATE;
            voteFor = selfNodeId;
            for (ServerInfo serverInfo: serverInfos) {
                Object response = rpcClient.vote(serverInfo, new Object());
            }
        }
    }

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
