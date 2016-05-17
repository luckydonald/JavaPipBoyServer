package de.luckydonald;

import de.luckydonald.pipboyserver.MESSAGE_CHANNEL;
import de.luckydonald.pipboyserver.Messages.KeepAlive;
import de.luckydonald.pipboyserver.Messages.ConnectionAccepted;
import de.luckydonald.pipboyserver.Messages.ConnectionRefused;
import de.luckydonald.pipboyserver.Messages.DataUpdate;
import de.luckydonald.pipboyserver.Messages.LocalMapUpdate;
import de.luckydonald.pipboyserver.PipBoyServer.Database;
import de.luckydonald.pipboyserver.PipBoyServer.types.*;
import de.luckydonald.utils.Array;
import de.luckydonald.utils.ObjectWithLogger;
import org.junit.Test;

import java.util.Arrays;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * Created by  on
 *
 * @author luckydonald
 * @since 12.05.2016
 **/
public class MessagesTest extends ObjectWithLogger {
    private byte[] expected_keepAlive = {0x00, 0x00, 0x00, 0x00, 0x00};
    private byte[] expected_connectionAccepted = {
            0x25, 0x00, 0x00, 0x00, 0x01, 0x7B, 0x22, 0x6C, 0x61, 0x6E, 0x67, 0x22, 0x3A, 0x20, 0x22, 0x64, 0x65, 0x22,
            0x2C, 0x20, 0x22, 0x76, 0x65, 0x72, 0x73, 0x69, 0x6F, 0x6E, 0x22, 0x3A, 0x20, 0x22, 0x31, 0x2E, 0x31, 0x2E,
            0x33, 0x30, 0x2E, 0x30, 0x22, 0x7D
    };
    private byte[] expected_connectionRefused = {0x00, 0x00, 0x00, 0x00, 0x02};
    private byte[] expectedDataUpdate = {
            0x33, 0x00, 0x00, 0x00, 0x03, 0x03, 0x0a, 0x00, 0x00, 0x00, 0x2a, 0x00, 0x00, 0x00, 0x07, 0x0b, 0x00, 0x00,
            0x00, 0x02, 0x00, 0x01, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x08, 0x0c, 0x00, 0x00, 0x00, 0x02, 0x00,
            0x05, 0x00, 0x00, 0x00, 0x66, 0x6f, 0x6f, 0x00, 0x06, 0x00, 0x00, 0x00, 0x68, 0x65, 0x6c, 0x6c, 0x6f, 0x00,
            0x00, 0x00
    };
    private byte[] expectedDataUpdate_delete = {
            0x08, 0x0c, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x00, 0x03, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00
    };
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
    public void testKeepAlive_Content() throws Exception {
        KeepAlive msg = new KeepAlive();
        assertArrayEquals("toBytes()", expected_keepAlive, msg.toBytes());
    }

    @Test
    public void testConnectionAccepted_Content() throws Exception {
        ConnectionAccepted msg = new ConnectionAccepted();
        assertArrayEquals("toBytes()", expected_connectionAccepted, msg.toBytes());
    }
    @Test
    public void testConnectionRefused_Content() throws Exception {
        ConnectionRefused msg = new ConnectionRefused();
        assertArrayEquals("toBytes()", expected_connectionRefused, msg.toBytes());
    }
    @Test
    public void testDataUpdate_Content() throws Exception {
        DBEntry[] updates = new DBEntry[3];
        Database db = new Database();
        db.add(new DBString("Placeholder with ID 0"));
        db.add(new DBString("Placeholder with ID 1"));
        db.add(new DBString("Placeholder with ID 2"));
        db.add(new DBString("Placeholder with ID 3"));
        db.add(new DBString("Placeholder with ID 4"));
        db.add(new DBString("Placeholder with ID 5"));
        db.add(new DBString("Placeholder with ID 6"));
        db.add(new DBString("Placeholder with ID 7"));
        db.add(new DBString("Placeholder with ID 8"));
        db.add(new DBString("Placeholder with ID 9"));
        DBInteger32 package1 = new DBInteger32(42);
        db.add(package1);
        assertEquals("package1 id", 10, (int) package1.getID());
        DBList package2 = new DBList();
        db.add(package2);
        assertEquals("package2 id", 11, (int) package2.getID());
        package2.append(1);
        package2.append(2);
        DBDict package3 = new DBDict();
        db.add(package3);
        assertEquals("package3 id", 12, (int) package3.getID());
        //package3.add("deleteme_1", db.get(3));
        //package3.add("deleteme_2", db.get(4));
        package3.add("foo", db.get(5));
        package3.add("hello", db.get(6));
        //package3.remove(3);
        //package3.remove(4);
        updates[0] = package1;
        updates[1] = package2;
        updates[2] = package3;
        DataUpdate msg = new DataUpdate(updates);
        Function<Object,String> func = this::formatByte;
        byte[] message = msg.toBytes(); // content will change after call!
        assertEquals("toBytes() (strEquals)", Array.toString(expectedDataUpdate, func),  Array.toString(message, func));
        assertArrayEquals("toBytes() (arrayEquals)", expectedDataUpdate, message);
    }

    private String formatByte(Object o) {
        if (o instanceof Byte) {
            return String.format("%02X", (byte)o);
        }
        return "";
    }
}