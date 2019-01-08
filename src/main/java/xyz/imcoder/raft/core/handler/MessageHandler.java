package xyz.imcoder.raft.core.handler;

import xyz.imcoder.raft.core.rpc.RpcResponse;
import xyz.imcoder.raft.core.server.ServerInfo;

/**
 * @Author sunsai
 * @Date 2019/1/8 6:15 PM
 **/
public interface MessageHandler {
    RpcResponse onCopyMessage(ServerInfo fromServerInfo, Object object);
    RpcResponse onVoteMessage(ServerInfo fromServerInfo, Object object);
    RpcResponse onPreVoteMessage(ServerInfo fromServerInfo, Object object);
    RpcResponse onCommitMessage(ServerInfo fromServerInfo, Object message);
}
