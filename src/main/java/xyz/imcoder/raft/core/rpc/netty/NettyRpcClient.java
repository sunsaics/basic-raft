package xyz.imcoder.raft.core.rpc.netty;

import xyz.imcoder.raft.core.message.HeartBeatRequestMessage;
import xyz.imcoder.raft.core.message.HeartBeatResponseMessage;
import xyz.imcoder.raft.core.message.VoteRequestMessage;
import xyz.imcoder.raft.core.message.VoteResponseMessage;
import xyz.imcoder.raft.core.rpc.RpcClient;
import xyz.imcoder.raft.core.server.ServerInfo;

import java.util.concurrent.Future;

/**
 * @Author sunsai
 * @Date 2019/1/12 2:34 PM
 **/
public class NettyRpcClient implements RpcClient {

    private NettyClient nettyClient = new NettyClient();

    @Override
    public HeartBeatResponseMessage heartBeat(ServerInfo serverInfo, HeartBeatRequestMessage message) throws Exception {
        return (HeartBeatResponseMessage) nettyClient.send(serverInfo.getHost(), serverInfo.getPort(), new RequestMsgWrapper(MsgType.HEARTBEAT, message)).get();
    }

    @Override
    public Future<Object> vote(ServerInfo serverInfo, VoteRequestMessage message) throws Exception {
        return nettyClient.send(serverInfo.getHost(), serverInfo.getPort(), new RequestMsgWrapper(MsgType.VOTE, message));
    }

    @Override
    public Object copy(ServerInfo serverInfo, Object object) {
        return null;
    }

    @Override
    public Object commit(ServerInfo serverInfo, Object object) {
        return null;
    }

    @Override
    public Object preVote(ServerInfo serverInfo, Object preVote) {
        return null;
    }
}
