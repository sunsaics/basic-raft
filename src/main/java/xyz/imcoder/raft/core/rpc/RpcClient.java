package xyz.imcoder.raft.core.rpc;

import xyz.imcoder.raft.core.message.*;
import xyz.imcoder.raft.core.config.ServerInfo;

import java.util.concurrent.Future;

/**
 * @Author sunsai
 * @Date 2019/1/8 6:05 PM
 **/
public interface RpcClient {
    Future<MessageWrapper> sendMessage(ServerInfo serverInfo, MessageWrapper message) throws Exception;
}
