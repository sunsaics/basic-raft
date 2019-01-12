package xyz.imcoder.raft.core.server;

/**
 * @Author sunsai
 * @Date 2019/1/8 6:12 PM
 **/
public class ServerInfo {
    private int serverNodeId;
    private String host;
    private int port;

    public ServerInfo(int serverNodeId, int port, String host) {
        this.serverNodeId = serverNodeId;
        this.port = port;
        this.host = host;
    }

    public int getServerNodeId() {
        return serverNodeId;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

}
