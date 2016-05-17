package de.luckydonald.pipboyserver;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by  on
 *
 * @author luckydonald
 * @since 17.05.2016
 **/
public class MESSAGE_CHANNELTest {

    @Test
    public void testEnums() {
        assertEquals("KeepAlive",          0x00, MESSAGE_CHANNEL.KeepAlive.toByte());
        assertEquals("ConnectionAccepted", 0x01, MESSAGE_CHANNEL.ConnectionAccepted.toByte());
        assertEquals("ConnectionRefused",  0x02, MESSAGE_CHANNEL.ConnectionRefused.toByte());
        assertEquals("DataUpdate",         0x03, MESSAGE_CHANNEL.DataUpdate.toByte());
        assertEquals("LocalMapUpdate",     0x04, MESSAGE_CHANNEL.LocalMapUpdate.toByte());
        assertEquals("Command",            0x05, MESSAGE_CHANNEL.Command.toByte());
        assertEquals("CommandResult",      0x06, MESSAGE_CHANNEL.CommandResult.toByte());
    }

    @Test
    public void testGet() throws Exception {

    }

    @Test
    public void testGetValue() throws Exception {

    }

    @Test
    public void testToByte() throws Exception {

    }

    @Test
    public void testMain() throws Exception {

    }
}