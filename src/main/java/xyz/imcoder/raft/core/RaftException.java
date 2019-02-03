package xyz.imcoder.raft.core;

/**
 * @Author sunsai
 * @Date 2019/2/3 5:14 PM
 **/
public class RaftException extends RuntimeException {
    public RaftException(String message) {
        super(message);
    }

    public RaftException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
