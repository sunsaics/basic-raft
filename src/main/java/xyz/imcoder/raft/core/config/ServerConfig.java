package xyz.imcoder.raft.core.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author sunsai
 * @Date 2019/1/8 6:15 PM
 **/
public class ServerConfig {

    private long heartbeatTimeout = 1000;

    private long voteTimeout = 500;

    public int port;

    private int id;

    private String clusterHosts;

    public void setPort(int port) {
        this.port = port;
    }

    public void setClusterHosts(String clusterHosts) {
        this.clusterHosts = clusterHosts;
    }

    public void setHeartbeatTimeout(long heartbeatTimeout) {
        this.heartbeatTimeout = heartbeatTimeout;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setVoteTimeout(long voteTimeout) {
        this.voteTimeout = voteTimeout;
    }

    public long getVoteTimeout() {
        return voteTimeout;
    }

    public long getHeartbeatTimeout() {
        return heartbeatTimeout;
    }

    public ServerInfo getSelfServerInfo() {
        ServerInfo serverInfo = new ServerInfo(id, port, "0.0.0.0");
        return serverInfo;
    }

    public List<ServerInfo> clusterServerInfos() {
        String[] parts = clusterHosts.split(",");
        List<ServerInfo> serverInfos = new ArrayList<>();
        for (String part: parts) {
            String[] host = part.split(":");
            String[] id = host[1].split("@");
            serverInfos.add(new ServerInfo(Integer.valueOf(id[1]), Integer.valueOf(id[0]), host[0]));
        }
        return serverInfos;
    }
}
