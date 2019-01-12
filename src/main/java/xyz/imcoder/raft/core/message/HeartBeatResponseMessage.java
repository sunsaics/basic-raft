package xyz.imcoder.raft.core.message;

/**
 * @Author sunsai
 * @Date 2019/1/12 2:06 PM
 **/
public class HeartBeatResponseMessage implements Message {
    private long term;
    private boolean success;

    public void setTerm(long term) {
        this.term = term;
    }

    public boolean isSuccess() {
        return success;
    }

    public long getTerm() {
        return term;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
