package xyz.imcoder.raft.core.rpc.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import xyz.imcoder.raft.core.handler.MessageHandler;
import xyz.imcoder.raft.core.message.MessageWrapper;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @Author sunsai
 * @Date 2019/1/12 2:45 PM
 **/
@ChannelHandler.Sharable
public class RpcServerHandler extends ChannelInboundHandlerAdapter {

    private MessageHandler messageHandler;

    public RpcServerHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        int length = in.readableBytes();
        byte[] msgByte = new byte[length];
        in.readBytes(msgByte);
        MessageWrapper messageWrapper = MessageWrapper.buildFromBytes(msgByte);
        Future<MessageWrapper> response = messageHandler.receiveMessage(null, messageWrapper);
        try {
            MessageWrapper responseMessage = response.get();
            ctx.write(Unpooled.copiedBuffer(responseMessage.getTransferContent()));
            ctx.flush();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx){
        //将未决消息[4]冲刷至远程节点，并关闭连接
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }

}
