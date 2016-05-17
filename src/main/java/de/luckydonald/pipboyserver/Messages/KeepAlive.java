package de.luckydonald.pipboyserver.Messages;

import de.luckydonald.pipboyserver.MESSAGE_CHANNEL;

public class KeepAlive extends Message {
    public KeepAlive() {
        super(null);
    }

    @Override
    public MESSAGE_CHANNEL getType() {
        return MESSAGE_CHANNEL.KeepAlive;
    }
}
