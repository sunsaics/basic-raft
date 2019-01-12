package xyz.imcoder.raft.core.test;

import xyz.imcoder.raft.core.RaftServer;
import xyz.imcoder.raft.core.config.ServerConfig;
import xyz.imcoder.raft.core.rpc.netty.NettyRpcClient;
import xyz.imcoder.raft.core.rpc.netty.NettyRpcServer;

/**
 * @Author sunsai
 * @Date 2019/1/12 8:24 PM
 **/
public class RaftServerTest2 {

    public static void main(String[] args) throws Exception {
        ServerConfig config = new ServerConfig();
        config.setId(2);
        config.setPort(9092);
        config.setClusterHosts("127.0.0.1:9091@1,127.0.0.1:9093@3");

        NettyRpcServer server = new NettyRpcServer();
        NettyRpcClient nettyRpcClient = new NettyRpcClient();
        RaftServer raftServer = new RaftServer(config, nettyRpcClient, server);

        raftServer.start();
    }
}
