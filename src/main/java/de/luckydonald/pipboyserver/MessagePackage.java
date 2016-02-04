package de.luckydonald.pipboyserver;

import de.luckydonald.pipboyserver.Messages.Message;

public class MessagePackage {
    /*struct Packet {
        uint32_t size,
        uint8_t channel,
        uint8_t content[size]
    }*/

    public MessagePackage(int channel, Object content) {
        byte[] message;
        if (content instanceof byte[]) {
            message = ((byte[]) content).clone();
        } else if (content instanceof String) {
            message = ((String) content).getBytes();
        } else {
            message = content.toString().getBytes();
        }
        int size = message.length;
    }
}

