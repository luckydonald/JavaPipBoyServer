package de.luckydonald.pipboyserver.Messages;

import de.luckydonald.pipboyserver.Messages.Message;

public class KeepAlive extends Message {
    final static int type = 0;
    public KeepAlive() {
        super(type, null);
    }
}
