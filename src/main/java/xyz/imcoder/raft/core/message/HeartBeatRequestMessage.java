package xyz.imcoder.raft.core.message;

/**
 * @Author sunsai
 * @Date 2019/1/12 2:03 PM
 **/
public class HeartBeatRequestMessage implements Message {
    private long term;
    private int leaderId;
    private long prevLogIndex;
    private long prevLogTerm;
    private long leaderCommit;

    public long getTerm() {
        return term;
    }

    public void setTerm(long term) {
        this.term = term;
    }

    public int getLeaderId() {
        return leaderId;
    }

    public long getLeaderCommit() {
        return leaderCommit;
    }

    public long getPrevLogIndex() {
        return prevLogIndex;
    }

    public long getPrevLogTerm() {
        return prevLogTerm;
    }

    public void setLeaderCommit(long leaderCommit) {
        this.leaderCommit = leaderCommit;
    }

    public void setLeaderId(int leaderId) {
        this.leaderId = leaderId;
    }

    public void setPrevLogIndex(long prevLogIndex) {
        this.prevLogIndex = prevLogIndex;
    }

    public void setPrevLogTerm(long prevLogTerm) {
        this.prevLogTerm = prevLogTerm;
    }

}
