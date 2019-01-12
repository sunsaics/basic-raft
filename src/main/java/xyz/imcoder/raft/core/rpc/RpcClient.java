package xyz.imcoder.raft.core.rpc;

import xyz.imcoder.raft.core.message.HeartBeatRequestMessage;
import xyz.imcoder.raft.core.message.HeartBeatResponseMessage;
import xyz.imcoder.raft.core.message.VoteRequestMessage;
import xyz.imcoder.raft.core.message.VoteResponseMessage;
import xyz.imcoder.raft.core.server.ServerInfo;

import java.util.concurrent.Future;

/**
 * @Author sunsai
 * @Date 2019/1/8 6:05 PM
 **/
public interface RpcClient {
    HeartBeatResponseMessage heartBeat(ServerInfo serverInfo, HeartBeatRequestMessage message) throws Exception;

    Future<Object> vote(ServerInfo serverInfo, VoteRequestMessage message) throws Exception;

    Object copy(ServerInfo serverInfo, Object object) throws Exception;

    Object commit(ServerInfo serverInfo, Object object) throws Exception;

    Object preVote(ServerInfo serverInfo, Object preVote) throws Exception;
}
