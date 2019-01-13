package xyz.imcoder.raft.core.log;

import java.util.Arrays;

/**
 * @Author sunsai
 * @Date 2019/1/8 6:07 PM
 **/
public class Log {
    private long term;
    private long index;
    private byte[] content;

    public Log(long term, long index, byte[] content) {
        this.term = term;
        this.index = index;
        this.content = Arrays.copyOf(content, content.length);
    }

    public long getTerm() {
        return term;
    }

    public long getIndex() {
        return index;
    }

    public byte[] getContent() {
        return content;
    }
}
