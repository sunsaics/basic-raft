package xyz.imcoder.raft.core.message;

import xyz.imcoder.raft.core.utils.Utils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

/**
 * @Author sunsai
 * @Date 2019/2/1 5:32 PM
 **/
public class MessageWrapper {
    private MessageType messageType;
    private byte[] byteContent;
    private Object objectContent;

    public MessageWrapper(MessageType messageType, Object messageContent) {
        this.messageType = messageType;
        this.objectContent = messageContent;
    }

    public byte[] getTransferContent() {
        if (Objects.isNull(byteContent)) {
            byte[] msgByte = Utils.toByteArray(objectContent);
            int msgType = messageType.ordinal();
            byteContent = Utils.concat(ByteBuffer.allocate(4).putInt(msgType).array(), msgByte);
        }

        return byteContent;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public <T> T getContent() {
        return (T) objectContent;
    }

    public static MessageWrapper buildFromBytes(byte[] byteContent) {
        byte[] msgType = Arrays.copyOfRange(byteContent, 0, 4);
        byte[] msgContent = Arrays.copyOfRange(byteContent, 4, byteContent.length);
        int msgTypeValue = ByteBuffer.wrap(msgType).getInt();
        MessageType messageType = MessageType.valueOf(msgTypeValue);
        MessageWrapper result = null;
        switch (messageType) {
            case VOTE_REQUEST:
                result = new MessageWrapper(messageType, Utils.toObject(msgContent, VoteRequestMessage.class));
                break;
            case VOTE_RESPONSE:
                result = new MessageWrapper(messageType, Utils.toObject(msgContent, VoteResponseMessage.class));
                break;
            default:
                result = new MessageWrapper(MessageType.UNKNOWN, null);
                break;
        }
        return result;
    }

}
