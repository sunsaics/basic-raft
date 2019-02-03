package xyz.imcoder.raft.core.test;

import xyz.imcoder.raft.core.RaftTimeEventGenerator;
import xyz.imcoder.raft.core.config.ServerConfig;
import xyz.imcoder.raft.core.server.ServerNode;

import java.util.ArrayList;

/**
 * @Author sunsai
 * @Date 2019/1/12 7:57 PM
 **/
public class TimeEventCreatorTest {

    public static void main(String[] args) {
        ServerConfig config = new ServerConfig();
        config.setId(1);
        config.setPort(9090);
        config.setClusterHosts("127.0.0.1:9092@2,127.0.0.1:9093@3");


        RaftTimeEventGenerator timeEventCreator = new RaftTimeEventGenerator(config, new ServerNode(config, null, new ArrayList<>()));
        timeEventCreator.start();
    }
}
