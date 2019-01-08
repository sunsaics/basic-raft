package xyz.imcoder.raft.core.rpc;

import xyz.imcoder.raft.core.message.VoteRequestMessage;
import xyz.imcoder.raft.core.message.VoteResponseMessage;
import xyz.imcoder.raft.core.server.ServerInfo;

import java.util.concurrent.Future;

/**
 * @Author sunsai
 * @Date 2019/1/8 6:05 PM
 **/
public interface RpcClient {
    Object heartBeat(ServerInfo serverInfo, Object message);

    Future<VoteResponseMessage> vote(ServerInfo serverInfo, VoteRequestMessage message);

    Object copy(ServerInfo serverInfo, Object object);

    Object commit(ServerInfo serverInfo, Object object);

    Object preVote(ServerInfo serverInfo, Object preVote);
}
