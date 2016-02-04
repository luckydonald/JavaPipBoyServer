package de.luckydonald.pipboyserver.Messages;

import de.luckydonald.pipboyserver.MESSAGE_CHANNEL;

public class ConnectionRefused extends Message{
    public ConnectionRefused() {
        super(MESSAGE_CHANNEL.ConnectionRefused, null);
        this.message = null;
    }
}
