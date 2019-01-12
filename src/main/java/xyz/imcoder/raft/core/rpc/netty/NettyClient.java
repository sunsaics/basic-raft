package xyz.imcoder.raft.core.rpc.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;

/**
 * @Author sunsai
 * @Date 2019/1/12 3:15 PM
 **/
public class NettyClient {

    private EventLoopGroup group = new NioEventLoopGroup();

    public Future<Object> send(String host, int port, Object msg) throws Exception {
        Bootstrap b = new Bootstrap();
        b.group(group);// 指定EventLoopGropu以处理客户端事件，需要适用于NIO的实现
        b.channel(NioSocketChannel.class);
        b.remoteAddress(new InetSocketAddress(host, port));
        //  b.option(ChannelOption.SO_BACKLOG, 1024);
        RpcClientHandler handler = new RpcClientHandler(msg);

        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(handler);
            }
        });

        // 绑定端口，待待同步
        ChannelFuture f = b.connect().sync(); // 异步绑定服务器，调用
        // sync()方法阻塞等待直到绑定完成
        // 等待服务器监听，端口关闭
        f.channel().closeFuture().sync(); // sync会直到绑定操作结束为止。
        return handler.getResponse();
    }
}
