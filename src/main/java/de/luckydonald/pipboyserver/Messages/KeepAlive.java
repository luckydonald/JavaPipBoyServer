package de.luckydonald.pipboyserver.Messages;

import de.luckydonald.pipboyserver.MESSAGE_CHANNEL;

public class KeepAlive extends Message {
    static {
        TYPE = MESSAGE_CHANNEL.KeepAlive;
    }
    public KeepAlive() {
        super(null);
    }
}
