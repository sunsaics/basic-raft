package xyz.imcoder.raft.core.message;

/**
 * @Author sunsai
 * @Date 2019/1/8 10:17 PM
 **/
public class VoteResponseMessage implements Message {
    private long term;
    private boolean wimVote;

    public VoteResponseMessage(long term, boolean isWinVote) {
        this.term = term;
        this.wimVote = isWinVote;
    }

    public boolean isWimVote() {
        return wimVote;
    }

    public long getTerm() {
        return term;
    }
}
