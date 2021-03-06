package xyz.imcoder.raft.core.test;

import xyz.imcoder.raft.core.RaftServer;
import xyz.imcoder.raft.core.config.ServerConfig;
import xyz.imcoder.raft.core.rpc.netty.NettyRpcClient;
import xyz.imcoder.raft.core.rpc.netty.NettyRpcServer;

/**
 * @Author sunsai
 * @Date 2019/1/12 8:25 PM
 **/
public class RaftServerTest3 {

    public static void main(String[] args) throws Exception {
        ServerConfig config = new ServerConfig();
        config.setId(3);
        config.setPort(9093);
        config.setClusterHosts("127.0.0.1:9091@1,127.0.0.1:9092@2");
        NettyRpcServer server = new NettyRpcServer();
        NettyRpcClient nettyRpcClient = new NettyRpcClient();
        RaftServer raftServer = new RaftServer(config, nettyRpcClient, server);
        raftServer.start();
    }
}
