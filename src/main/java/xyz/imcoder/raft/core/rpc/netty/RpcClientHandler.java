package xyz.imcoder.raft.core.rpc.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import xyz.imcoder.raft.core.utils.Utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @Author sunsai
 * @Date 2019/1/12 3:10 PM
 **/
public class RpcClientHandler extends SimpleChannelInboundHandler<ByteBuf> {


    private Object willSendMessage;

    private CompletableFuture<Object> futureResponse;

    private MsgType msgType;

    public RpcClientHandler(MsgType msgType, Object obj) {
        willSendMessage = obj;
        this.msgType = msgType;
        futureResponse = new CompletableFuture<>();
    }

    public Future<Object> getResponse() {
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
        futureResponse.complete(Utils.toObject(response));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        byte[] msg = Utils.toByteArray(new RequestMsgWrapper(msgType, willSendMessage));
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
