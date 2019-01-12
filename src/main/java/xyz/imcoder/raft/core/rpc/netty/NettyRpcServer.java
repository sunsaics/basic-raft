package xyz.imcoder.raft.core.rpc.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import xyz.imcoder.raft.core.config.ServerConfig;
import xyz.imcoder.raft.core.handler.MessageHandler;
import xyz.imcoder.raft.core.rpc.RpcServer;
import xyz.imcoder.raft.core.server.ServerInfo;

import java.util.List;

/**
 * @Author sunsai
 * @Date 2019/1/12 2:35 PM
 **/
public class NettyRpcServer implements RpcServer {

    @Override
    public void listen(ServerConfig config, List<ServerInfo> allServerList, MessageHandler messageHandler) throws Exception {

        final RpcServerHandler serverHandler = new RpcServerHandler(messageHandler);
        //创建EventLooproup
        EventLoopGroup workgroup = new NioEventLoopGroup();
        //创建EventLooproup
        EventLoopGroup bossgroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossgroup,bossgroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer(){
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            //EchoServerHandler被标记赤@Shareable，所以我们可以同时使用同样的实你还
                            ch.pipeline().addLast(serverHandler);
                        }

                    });

            //绑定端口，待待同步
            //异步绑定服务器，调用 sync()方法阻塞等待直到绑定完成
            ChannelFuture f = b.bind(config.port).sync();
            //等待服务器监听，端口关闭
            //sync会直到绑定操作结束为止。
            f.channel().closeFuture().sync();

            //使用指定的端口设置套装字地址
            //b.localAddress(new InetSocketAddress(port));

        }finally{
            //关闭EventLoopGroup，释放所有的资源
            workgroup.shutdownGracefully().sync();
            workgroup.shutdownGracefully().sync();
        }
    }

}
