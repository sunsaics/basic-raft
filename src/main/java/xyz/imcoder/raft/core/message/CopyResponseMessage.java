package xyz.imcoder.raft.core.message;

/**
 * @Author sunsai
 * @Date 2019/1/13 10:54 AM
 **/
public class CopyResponseMessage extends HeartBeatResponseMessage{

    public CopyResponseMessage(long term, boolean success) {
        super(term, success);
    }
}
