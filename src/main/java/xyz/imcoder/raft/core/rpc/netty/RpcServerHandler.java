package xyz.imcoder.raft.core.rpc.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import xyz.imcoder.raft.core.handler.MessageHandler;
import xyz.imcoder.raft.core.message.HeartBeatRequestMessage;
import xyz.imcoder.raft.core.utils.Utils;

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
        Object request = Utils.toObject(msgByte);
        RequestMsgWrapper requestWrapper = (RequestMsgWrapper) request;
        ctx.write(Unpooled.copiedBuffer(Utils.toByteArray(distributionMessage(requestWrapper))));
//        ctx.write(Unpooled.copiedBuffer(Utils.toByteArray(requestWrapper)));
        ctx.flush();
    }

    /**
     * 分发消息
     * @return
     */
    private Object distributionMessage(RequestMsgWrapper requestMsgWrapper) {
        Object response = null;
        if (requestMsgWrapper.getMsgType() == MsgType.HEARTBEAT) {
            response = messageHandler.onHeartBeatMessage(null, (HeartBeatRequestMessage) requestMsgWrapper.getMsgRequest());
        }

        return response;
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
