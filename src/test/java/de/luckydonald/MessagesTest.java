package de.luckydonald;

import de.luckydonald.pipboyserver.Messages.KeepAlive;
import de.luckydonald.utils.ObjectWithLogger;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by  on
 *
 * @author luckydonald
 * @since 12.05.2016
 **/
public class MessagesTest extends ObjectWithLogger {
    private byte[] expected_keepAlive = {0x00, 0x00, 0x00, 0x00, 0x00};

    @Test
    public void testKeepAlive_Content() throws Exception {
        KeepAlive msg = new KeepAlive();
        assertArrayEquals("toBytes()", expected_keepAlive, msg.toBytes());
    }
}