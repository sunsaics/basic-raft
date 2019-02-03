package xyz.imcoder.raft.core.handler;

/**
 * @Author sunsai
 * @Date 2019/1/8 9:28 PM
 **/
public interface TimeEventHandler {

    /**
     * follow心跳检查
     */
    void onHeartbeatTimeoutCheckEvent();

    /**
     * leader发送heartbeat
     */
    void onSendHeartbeatCheckEvent();
}
