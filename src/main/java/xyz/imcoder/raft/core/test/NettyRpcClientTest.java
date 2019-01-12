package xyz.imcoder.raft.core.test;

import xyz.imcoder.raft.core.message.HeartBeatRequestMessage;
import xyz.imcoder.raft.core.rpc.netty.MsgType;
import xyz.imcoder.raft.core.rpc.netty.NettyClient;

import java.util.concurrent.Future;

/**
 * @Author sunsai
 * @Date 2019/1/12 3:33 PM
 **/
public class NettyRpcClientTest {

    public static void main(String[] args) throws Exception {


        NettyClient nettyClient = new NettyClient();


        HeartBeatRequestMessage heartBeatRequestMessage = new HeartBeatRequestMessage();
        heartBeatRequestMessage.setTerm(1L);
        long start = System.currentTimeMillis();
        Future<Object> response = nettyClient.send("127.0.0.1", 9090, MsgType.HEARTBEAT, heartBeatRequestMessage);
        long mid = System.currentTimeMillis();
        Object res = response.get();
        long end = System.currentTimeMillis();
        System.out.println(mid - start);
        System.out.println(end - start);


    }
}
