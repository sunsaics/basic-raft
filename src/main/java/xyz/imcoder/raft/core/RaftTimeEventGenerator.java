package xyz.imcoder.raft.core;

import xyz.imcoder.raft.core.config.ServerConfig;
import xyz.imcoder.raft.core.handler.TimeEventHandler;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * raft时间事件生成器
 * @Author redocmi
 * @Date 2019/1/12 2:59 PM
 **/
public class RaftTimeEventGenerator {



    private ServerConfig config;
    private TimeEventHandler handler;

    private Timer heartBeatTimeoutCheck;

    private Timer sendHeartBeatMessage;

    public RaftTimeEventGenerator(ServerConfig config, TimeEventHandler handler) {
        this.config = config;
        this.handler = handler;
    }

    public void start() {
        heartBeatTimeoutCheck = new Timer();
        heartBeatTimeoutCheck.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    handler.onHeartbeatTimeoutCheckEvent();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, config.getHeartbeatTimeout(), config.getHeartbeatTimeout());

        sendHeartBeatMessage = new Timer();

        sendHeartBeatMessage.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    handler.onSendHeartbeatCheckEvent();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, config.getHeartbeatTimeout() / 3, config.getHeartbeatTimeout() / 3);

    }

    public void shutdown() {
        if (Objects.nonNull(sendHeartBeatMessage)) {
            sendHeartBeatMessage.cancel();
        }
        if (Objects.nonNull(heartBeatTimeoutCheck)) {
            heartBeatTimeoutCheck.cancel();
        }
    }

}
