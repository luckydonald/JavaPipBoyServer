package de.luckydonald.pipboyserver.Messages;

import de.luckydonald.pipboyserver.MESSAGE_CHANNEL;

public class ConnectionRefused extends Message{
    static {
        TYPE = MESSAGE_CHANNEL.ConnectionRefused;
    }
    public ConnectionRefused() {
        super(null);
        this.message = null;
    }
}
