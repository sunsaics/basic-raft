package xyz.imcoder.raft.core.test;

import xyz.imcoder.raft.core.config.ServerConfig;
import xyz.imcoder.raft.core.rpc.netty.NettyRpcServer;
import xyz.imcoder.raft.core.server.ServerNode;

import java.util.ArrayList;

/**
 * @Author sunsai
 * @Date 2019/1/12 3:31 PM
 **/
public class NettyRpcServerTest {

    public static void main(String[] args) throws Exception {

        ServerConfig config = new ServerConfig();
        config.setId(1);
        config.setPort(9090);
        config.setClusterHosts("127.0.0.1:9092@2,127.0.0.1:9093@3");

        NettyRpcServer server = new NettyRpcServer();

        System.out.println("server start");
        server.listen(config, new ArrayList<>(), new ServerNode(config, null, new ArrayList<>()));
    }
}
