package xyz.imcoder.raft.core.rpc.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import xyz.imcoder.raft.core.message.MessageWrapper;
import xyz.imcoder.raft.core.utils.Utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;


/**
 * @Author sunsai
 * @Date 2019/1/12 3:10 PM
 **/
public class RpcClientHandler extends SimpleChannelInboundHandler<ByteBuf> {


    private byte[] willSendMessage;

    private CompletableFuture<MessageWrapper> futureResponse;

    public RpcClientHandler(byte[] obj) {
        willSendMessage = obj;
        futureResponse = new CompletableFuture<>();
    }

    public Future<MessageWrapper> getResponse() {
        return futureResponse;
    }

    /**
     * 接收到数据时，调用该方法
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        int length = in.readableBytes();
        byte[] response = new byte[length];
        in.readBytes(response);
        futureResponse.complete(MessageWrapper.buildFromBytes(response));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        byte[] msg = willSendMessage;
        ctx.writeAndFlush(Unpooled.copiedBuffer(msg));
    }

    /**
     * 发生异常时，记录异常信息同时关闭Channel
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }
}
