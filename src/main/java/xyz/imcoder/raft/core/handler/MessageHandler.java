package xyz.imcoder.raft.core.handler;

import xyz.imcoder.raft.core.message.*;
import xyz.imcoder.raft.core.rpc.RpcResponse;
import xyz.imcoder.raft.core.server.ServerInfo;

import java.util.concurrent.Future;

/**
 * @Author sunsai
 * @Date 2019/1/8 6:15 PM
 **/
public interface MessageHandler {
    Future<MessageWrapper> receiveMessage(ServerInfo serverInfo, MessageWrapper messageWrapper);
}
