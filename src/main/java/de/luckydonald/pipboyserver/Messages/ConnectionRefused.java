package de.luckydonald.pipboyserver.Messages;

import de.luckydonald.pipboyserver.MESSAGE_CHANNEL;

public class ConnectionRefused extends Message{
    public ConnectionRefused() {
        super(null);
        this.message = null;
    }
    @Override
    public MESSAGE_CHANNEL getType() {
        return MESSAGE_CHANNEL.ConnectionRefused;
    }
}
