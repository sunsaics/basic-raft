package xyz.imcoder.raft.core;

import xyz.imcoder.raft.core.config.ServerConfig;
import xyz.imcoder.raft.core.rpc.RpcClient;
import xyz.imcoder.raft.core.rpc.RpcServer;
import xyz.imcoder.raft.core.rpc.netty.NettyRpcServer;
import xyz.imcoder.raft.core.server.ServerNode;


/**
 * @Author sunsai
 * @Date 2019/1/8 6:20 PM
 **/
public class RaftServer {

    private ServerConfig config;
    private RpcClient rpcClient;
    private RpcServer rpcServer;
    private ServerNode serverNode;
    private RaftTimeEventCreator timeEventCreator;

    public RaftServer(ServerConfig config, RpcClient rpcClient, RpcServer rpcServer) {
        this.config = config;
        this.rpcClient = rpcClient;
        this.rpcServer = rpcServer;
        serverNode = new ServerNode(config, rpcClient, config.clusterServerInfos());
        timeEventCreator = new RaftTimeEventCreator(config, serverNode);
    }

    public void start() throws Exception {
        rpcServer.listen(config, config.clusterServerInfos(), serverNode);
        timeEventCreator.start();
    }

    public static void main(String[] args) {
        ServerConfig config = new ServerConfig();
        config.setId(1);
        config.setPort(9091);
        config.setClusterHosts("127.0.0.1:9092@2,127.0.0.1:9093@3");


        RaftServer raftServer = new RaftServer(config, null, new NettyRpcServer());
        try {
            raftServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
