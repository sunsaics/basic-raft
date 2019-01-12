package xyz.imcoder.raft.core.rpc.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import xyz.imcoder.raft.core.message.VoteRequestMessage;
import xyz.imcoder.raft.core.message.VoteResponseMessage;
import xyz.imcoder.raft.core.rpc.RpcClient;
import xyz.imcoder.raft.core.server.ServerInfo;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;

/**
 * @Author sunsai
 * @Date 2019/1/12 2:34 PM
 **/
public class NettyRpcClient implements RpcClient {




    @Override
    public Object heartBeat(ServerInfo serverInfo, Object message) {
        return null;
    }

    @Override
    public Future<VoteResponseMessage> vote(ServerInfo serverInfo, VoteRequestMessage message) {
        return null;
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
