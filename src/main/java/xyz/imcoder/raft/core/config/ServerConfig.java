package xyz.imcoder.raft.core.config;

/**
 * @Author sunsai
 * @Date 2019/1/8 6:15 PM
 **/
public class ServerConfig {

    private long heartbeatTimeout = 500;

    private long voteTimeout = 1000;


    public long getVoteTimeout() {
        return voteTimeout;
    }

    public long getHeartbeatTimeout() {
        return heartbeatTimeout;
    }
}
