package xyz.imcoder.raft.core.handler;

import xyz.imcoder.raft.core.message.HeartBeatRequestMessage;
import xyz.imcoder.raft.core.message.HeartBeatResponseMessage;
import xyz.imcoder.raft.core.message.VoteRequestMessage;
import xyz.imcoder.raft.core.message.VoteResponseMessage;
import xyz.imcoder.raft.core.rpc.RpcResponse;
import xyz.imcoder.raft.core.server.ServerInfo;

/**
 * @Author sunsai
 * @Date 2019/1/8 6:15 PM
 **/
public interface MessageHandler {
    RpcResponse onCopyMessage(ServerInfo fromServerInfo, Object object);
    VoteResponseMessage onVoteMessage(ServerInfo fromServerInfo, VoteRequestMessage message);
    RpcResponse onPreVoteMessage(ServerInfo fromServerInfo, Object object);
    RpcResponse onCommitMessage(ServerInfo fromServerInfo, Object message);
    HeartBeatResponseMessage onHeartBeatMessage(ServerInfo leaderServerInfo, HeartBeatRequestMessage message);
}
