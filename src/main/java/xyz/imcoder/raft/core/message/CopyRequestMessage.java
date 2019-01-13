package xyz.imcoder.raft.core.message;

import xyz.imcoder.raft.core.log.Log;

import java.util.List;

/**
 * @Author sunsai
 * @Date 2019/1/13 10:54 AM
 **/
public class CopyRequestMessage extends HeartBeatRequestMessage{

    private List<Log> entries;

    public List<Log> getEntries() {
        return entries;
    }

    public void setEntries(List<Log> entries) {
        this.entries = entries;
    }
}
