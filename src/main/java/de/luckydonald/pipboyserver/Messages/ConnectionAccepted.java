package de.luckydonald.pipboyserver.Messages;

import de.luckydonald.pipboyserver.MESSAGE_CHANNEL;

public class ConnectionAccepted extends Message {
    public ConnectionAccepted() {
        super("{\"lang\": \"de\", \"version\": \"1.1.30.0\"}".getBytes()
        );
    }
    @Override
    public MESSAGE_CHANNEL getType() {
        return MESSAGE_CHANNEL.ConnectionAccepted;
    }
}
