package de.luckydonald.pipboyserver.Messages;

import de.luckydonald.pipboyserver.MESSAGE_CHANNEL;
import de.luckydonald.utils.ObjectWithLogger;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Message extends ObjectWithLogger{
    public static MESSAGE_CHANNEL TYPE;
    private byte[] content;
    ByteArrayOutputStream message = new ByteArrayOutputStream();

    public Message() {
        this(null);
    }
    public Message(byte[] content) {
        this.content = content;
        if(TYPE != KeepAlive.TYPE) {
            getLogger().fine("created new Message, type " + TYPE + " (" + this.getClass().getSimpleName() + ").");
        }
    }

    public byte[] toBytes() {
        int length = 0;
        if (this.content != null) {
            length = this.content.length;
        }
        ByteBuffer header = ByteBuffer.allocate(4+1 + length);
        header.order(ByteOrder.LITTLE_ENDIAN);
        header.putInt(length);
        header.put(TYPE.toByte());
        if (this.content != null) {
            header.put(this.content);
        }
        return header.array();
    }
    public static void printBytes(byte[] bytes){
        for (byte b : bytes) {
            System.out.printf("%02X ", b);
        }
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}

