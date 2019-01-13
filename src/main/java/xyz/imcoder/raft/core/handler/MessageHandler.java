package xyz.imcoder.raft.core.handler;

import xyz.imcoder.raft.core.message.*;
import xyz.imcoder.raft.core.rpc.RpcResponse;
import xyz.imcoder.raft.core.server.ServerInfo;

/**
 * @Author sunsai
 * @Date 2019/1/8 6:15 PM
 **/
public interface MessageHandler {
    CopyResponseMessage onCopyMessage(ServerInfo fromServerInfo, CopyRequestMessage message);
    VoteResponseMessage onVoteMessage(ServerInfo fromServerInfo, VoteRequestMessage message);
    RpcResponse onPreVoteMessage(ServerInfo fromServerInfo, Object object);
    CommitResponseMessage onCommitMessage(ServerInfo fromServerInfo, CommitRequestMessage message);
    HeartBeatResponseMessage onHeartBeatMessage(ServerInfo leaderServerInfo, HeartBeatRequestMessage message);
}
