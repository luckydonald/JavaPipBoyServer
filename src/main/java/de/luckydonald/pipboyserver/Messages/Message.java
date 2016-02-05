package de.luckydonald.pipboyserver.Messages;

import sun.plugin2.message.HeartbeatMessage;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Message{
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
            System.out.println("New Message, type "+ type + ".");
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
        //System.out.println(header);

        /*Path file = Paths.get("/tmp/the-file-name.txt");
        try {
            Files.write(file, header.array());
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        //Files.write(file, data, StandardOpenOption.APPEND);
        byte[] bytes = header.array();
        return bytes;
    }
    /*public static void main(String[] args) throws IOException {
        //new Message(2, "HELLOWORLD".getBytes());
        new ConnectionAccepted().toBytes();
    }*/
    public static void printBytes(byte[] bytes){
        //int i = 0;
        for (byte b : bytes) {
            System.out.printf("%02X ", b);
            //if ((i++) % 2 == 1) {
            //    System.out.print(" ");
            //}
        }
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}

