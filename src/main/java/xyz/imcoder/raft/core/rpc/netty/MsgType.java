package xyz.imcoder.raft.core.rpc.netty;

/**
 * @Author sunsai
 * @Date 2019/1/12 3:58 PM
 **/
public enum MsgType {
    UNKNOW(0, "unknow"),
    HEARTBEAT(1, "心跳")
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
        return UNKNOW;
    }

    public int getCode() {
        return code;
    }

}
