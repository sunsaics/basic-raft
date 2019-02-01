package xyz.imcoder.raft.core.message;

/**
 * @Author sunsai
 * @Date 2019/2/1 5:32 PM
 **/
public enum MessageType {
    UNKNOWN,
    COMMIT_REQUEST,
    COMMIT_RESPONSE,
    COPY_REQUEST,
    COPY_RESPONSE,
    HEARTBEAT_REQUEST,
    HEARTBEAT_RESPONSE,
    VOTE_REQUEST,
    VOTE_RESPONSE;

    public static MessageType valueOf(int value) {
        for (MessageType msgType : values()) {
            if (msgType.ordinal() == value) {
                return msgType;
            }
        }
        return UNKNOWN;
    }
}
