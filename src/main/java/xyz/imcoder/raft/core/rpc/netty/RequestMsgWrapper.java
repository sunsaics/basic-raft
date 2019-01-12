package xyz.imcoder.raft.core.rpc.netty;

import java.io.Serializable;

/**
 * @Author sunsai
 * @Date 2019/1/12 4:11 PM
 **/
public class RequestMsgWrapper implements Serializable {
    private MsgType msgType;
    private Object msgRequest;

    RequestMsgWrapper(MsgType msgType, Object msgRequest) {
        this.msgType = msgType;
        this.msgRequest = msgRequest;
    }

    public MsgType getMsgType() {
        return msgType;
    }

    public Object getMsgRequest() {
        return msgRequest;
    }
}
