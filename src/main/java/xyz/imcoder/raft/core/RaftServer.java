package xyz.imcoder.raft.core;

import xyz.imcoder.raft.core.config.ServerConfig;
import xyz.imcoder.raft.core.rpc.RpcClient;
import xyz.imcoder.raft.core.rpc.RpcServer;
import xyz.imcoder.raft.core.server.ServerInfo;
import xyz.imcoder.raft.core.server.ServerNode;

import java.util.ArrayList;

/**
 * @Author sunsai
 * @Date 2019/1/8 6:20 PM
 **/
public class RaftServer {

    private ServerConfig config;
    private RpcClient rpcClient;
    private RpcServer rpcServer;
    private ServerNode serverNode;

    public RaftServer(ServerConfig config, RpcClient rpcClient, RpcServer rpcServer) {
        this.config = config;
        this.rpcClient = rpcClient;
        this.rpcServer = rpcServer;
        serverNode = new ServerNode();
    }

    public void start() {
        rpcServer.listen(config, new ArrayList<ServerInfo>(), serverNode);
    }
}
