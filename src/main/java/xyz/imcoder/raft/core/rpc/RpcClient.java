package xyz.imcoder.raft.core.rpc;

import xyz.imcoder.raft.core.server.ServerInfo;

/**
 * @Author sunsai
 * @Date 2019/1/8 6:05 PM
 **/
public interface RpcClient {
    Object heartBeat(ServerInfo serverInfo, Object message);

    Object vote(ServerInfo serverInfo, Object vote);

    Object copy(ServerInfo serverInfo, Object object);

    Object commit(ServerInfo serverInfo, Object object);

    Object preVote(ServerInfo serverInfo, Object preVote);
}
