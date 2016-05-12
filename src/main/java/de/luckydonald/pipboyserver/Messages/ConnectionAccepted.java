package de.luckydonald.pipboyserver.Messages;

import de.luckydonald.pipboyserver.MESSAGE_CHANNEL;

public class ConnectionAccepted extends Message {
    static {
        TYPE = MESSAGE_CHANNEL.ConnectionAccepted;
    }
    public ConnectionAccepted() {
        super("{\"lang\": \"de\", \"version\": \"1.1.30.0\"}".getBytes()
        );
    }
}
