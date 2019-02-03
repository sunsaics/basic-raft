package xyz.imcoder.raft.core.rpc;

import xyz.imcoder.raft.core.config.ServerConfig;
import xyz.imcoder.raft.core.handler.MessageHandler;
import xyz.imcoder.raft.core.config.ServerInfo;

import java.util.List;

/**
 * @Author sunsai
 * @Date 2019/1/8 6:14 PM
 **/
public interface RpcServer {
    void listen(ServerConfig config, List<ServerInfo> allServerList, MessageHandler messageHandler) throws Exception;


}
