package xyz.imcoder.raft.core.rpc.netty;

/**
 * @Author sunsai
 * @Date 2019/1/12 3:58 PM
 **/
public enum MsgType {
    UNKNOWN(0, "unknown"),
    HEARTBEAT(1, "心跳"),
    PRE_VOTE(2, "prevote"),
    VOTE(3, "vote"),
    COPY(4, "COPY"),
    COMMIT(5, "COMMIT")
    ;

    private int code;
    private String message;
    MsgType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static MsgType createByCode(int code) {
        for (MsgType type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return UNKNOWN;
    }

    public int getCode() {
        return code;
    }

}
