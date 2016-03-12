package de.luckydonald.pipboyserver.Messages;

import de.luckydonald.pipboyserver.MESSAGE_CHANNEL;

public class KeepAlive extends Message {
    final static int type = MESSAGE_CHANNEL.KeepAlive;
    public KeepAlive() {
        super(type, null);
    }
}
