package de.luckydonald.pipboyserver.Messages;

import de.luckydonald.utils.ObjectWithLogger;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Message extends ObjectWithLogger{
    private int type;
    private byte[] content;
    ByteArrayOutputStream message = new ByteArrayOutputStream();

    public Message(int type) {
        this(type, null);
    }
    public Message(int type, byte[] content) {
        this.type = type;
        this.content = content;
        if(type != KeepAlive.type) {
            getLogger().fine("created new Message, type " + type + " (" + this.getClass().getSimpleName() + ").");
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
        header.put((byte) this.type);
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

