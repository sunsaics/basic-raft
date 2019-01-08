package xyz.imcoder.raft.core.message;

/**
 * @Author sunsai
 * @Date 2019/1/8 10:15 PM
 **/
public class VoteRequestMessage {
    private long term;
    private int candidateId;
    private long lastLogIndex;
    private long lastCommitTerm;

    public void setTerm(long term) {
        this.term = term;
    }

    public long getTerm() {
        return term;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public long getLastCommitTerm() {
        return lastCommitTerm;
    }

    public long getLastLogIndex() {
        return lastLogIndex;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public void setLastCommitTerm(long lastCommitTerm) {
        this.lastCommitTerm = lastCommitTerm;
    }

    public void setLastLogIndex(long lastLogIndex) {
        this.lastLogIndex = lastLogIndex;
    }

}
