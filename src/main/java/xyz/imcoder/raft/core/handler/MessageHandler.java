package xyz.imcoder.raft.core.handler;

import xyz.imcoder.raft.core.server.ServerInfo;

/**
 * @Author sunsai
 * @Date 2019/1/8 6:15 PM
 **/
public interface MessageHandler {
    void onCopyMessage(ServerInfo fromServerInfo, Object object);
    void onVoteMessage(ServerInfo fromServerInfo, Object object);
    void onPreVoteMessage(ServerInfo fromServerInfo, Object object);
    void onCommitMessage(ServerInfo fromServerInfo, Object message);
}
