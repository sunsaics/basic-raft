package xyz.imcoder.raft.core.rpc.netty;

import xyz.imcoder.raft.core.message.*;
import xyz.imcoder.raft.core.rpc.RpcClient;
import xyz.imcoder.raft.core.config.ServerInfo;

import java.util.concurrent.Future;

/**
 * @Author sunsai
 * @Date 2019/1/12 2:34 PM
 **/
public class NettyRpcClient implements RpcClient {

    private NettyClient nettyClient = new NettyClient();

    @Override
    public Future<MessageWrapper> sendMessage(ServerInfo serverInfo, MessageWrapper message) throws Exception {
        return nettyClient.send(serverInfo.getHost(), serverInfo.getPort(), message.getTransferContent());
    }
}
