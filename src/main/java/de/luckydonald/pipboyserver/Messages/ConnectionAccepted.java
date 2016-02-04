package de.luckydonald.pipboyserver.Messages;

import de.luckydonald.pipboyserver.Constants;
import de.luckydonald.pipboyserver.MESSAGE_CHANNEL;
import de.luckydonald.pipboyserver.Messages.Message;

public class ConnectionAccepted extends Message {
    public ConnectionAccepted() {
        super(MESSAGE_CHANNEL.ConnectionAccepted, "{\"lang\": \"de\", \"version\": \"1.1.30.0\"}".getBytes());
    }
}
