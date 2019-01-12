package xyz.imcoder.raft.core.message;

/**
 * @Author sunsai
 * @Date 2019/1/12 2:06 PM
 **/
public class HeartBeatResponseMessage implements Message {
    private long term;
    private boolean success;

    public HeartBeatResponseMessage(long term, boolean success) {
        this.term = term;
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public long getTerm() {
        return term;
    }

}
