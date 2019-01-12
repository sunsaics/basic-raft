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
        Future<Object> response = nettyClient.send("127.0.0.1", 9091, MsgType.HEARTBEAT, heartBeatRequestMessage);
        Object res = response.get();
        System.out.println(res);
    }
}
