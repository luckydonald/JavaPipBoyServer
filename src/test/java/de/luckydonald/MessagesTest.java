package de.luckydonald;

import de.luckydonald.pipboyserver.MESSAGE_CHANNEL;
import de.luckydonald.pipboyserver.Messages.KeepAlive;
import de.luckydonald.pipboyserver.Messages.ConnectionAccepted;
import de.luckydonald.pipboyserver.Messages.ConnectionRefused;
import de.luckydonald.pipboyserver.Messages.DataUpdate;
import de.luckydonald.pipboyserver.Messages.LocalMapUpdate;
//import de.luckydonald.pipboyserver.Messages.CommandResult;
import de.luckydonald.pipboyserver.PipBoyServer.Database;
import de.luckydonald.pipboyserver.PipBoyServer.types.*;
import de.luckydonald.utils.Array;
import de.luckydonald.utils.ObjectWithLogger;
import org.junit.Test;

import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * Created by luckydonald
 *
 * @author luckydonald
 * @since 12.05.2016
 **/
public class MessagesTest extends ObjectWithLogger {
    private byte[] expected_keepAlive = {0x00, 0x00, 0x00, 0x00, 0x00};
    private byte[] expected_connectionAccepted = {
            0x25, 0x00, 0x00, 0x00, 0x01,
            0x7B, 0x22, 0x6C, 0x61, 0x6E, 0x67, 0x22, 0x3A, 0x20, 0x22, 0x64, 0x65, 0x22, 0x2C, 0x20, 0x22, 0x76, 0x65,
            0x72, 0x73, 0x69, 0x6F, 0x6E, 0x22, 0x3A, 0x20, 0x22, 0x31, 0x2E, 0x31, 0x2E, 0x33, 0x30, 0x2E, 0x30, 0x22,
            0x7D
    };
    private byte[] expected_connectionRefused = {0x00, 0x00, 0x00, 0x00, 0x02};
    private byte[] expectedDataUpdate = {
            0x33, 0x00, 0x00, 0x00, 0x03,
            0x03, 0x0a, 0x00, 0x00, 0x00, 0x2a, 0x00, 0x00, 0x00, 0x07, 0x0b, 0x00, 0x00, 0x00, 0x02, 0x00, 0x01, 0x00,
            0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x08, 0x0c, 0x00, 0x00, 0x00, 0x02, 0x00, 0x05, 0x00, 0x00, 0x00, 0x66,
            0x6f, 0x6f, 0x00, 0x06, 0x00, 0x00, 0x00, 0x68, 0x65, 0x6c, 0x6c, 0x6f, 0x00, 0x00, 0x00
    };
    private byte[] expectedDataUpdate_add = {
            0x27, 0x00, 0x00, 0x00, 0x03,
            0x08, 0x0C, 0x00, 0x00, 0x00, 0x02, 0x00, 0x03, 0x00, 0x00, 0x00, 0x64, 0x65, 0x6C, 0x65, 0x74, 0x65, 0x6D,
            0x65, 0x5F, 0x31, 0x00, 0x04, 0x00, 0x00, 0x00, 0x64, 0x65, 0x6C, 0x65, 0x74, 0x65, 0x6D, 0x65, 0x5F, 0x32,
            0x00, 0x00, 0x00
    };
    private byte[] expectedDataUpdate_delete = {
            0x11, 0x00, 0x00, 0x00, // length 17,
            0x03, // type 3: DataUpdate
            0x08, // type 8: dict
            0x0c, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x00, 0x03, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00
    };
    private byte[] expectedDataUpdate_list = {
            0x0B, 0x00, 0x00, 0x00, // length 11,
            0x03, // type 3: DataUpdate
            0x07, // type 7: list
            0x0D, 0x00, 0x00, 0x00, // id 13
            0x01, 0x00,  // length: 1
            0x0A, 0x00, 0x00, 0x00 // id[0] = 10
    };

    @Test
    public void testKeepAlive_Content() throws Exception {
        KeepAlive msg = new KeepAlive();
        assertArrayEquals("toBytes()", expected_keepAlive, msg.toBytes());
    }
    @Test
    public void testKeepAlive_Type() throws Exception {
        assertEquals("TYPE", MESSAGE_CHANNEL.KeepAlive, new KeepAlive().getType());
    }

    @Test
    public void testConnectionAccepted_Content() throws Exception {
        ConnectionAccepted msg = new ConnectionAccepted();
        assertArrayEquals("toBytes()", expected_connectionAccepted, msg.toBytes());
    }
    @Test
    public void testConnectionAccepted_Type() throws Exception {
        assertEquals("TYPE", MESSAGE_CHANNEL.ConnectionAccepted,  new ConnectionAccepted().getType());
    }

    @Test
    public void testConnectionRefused_Content() throws Exception {
        ConnectionRefused msg = new ConnectionRefused();
        assertArrayEquals("toBytes()", expected_connectionRefused, msg.toBytes());
    }
    @Test
    public void testConnectionRefused_Type() throws Exception {
        assertEquals("TYPE", MESSAGE_CHANNEL.ConnectionRefused, new ConnectionRefused().getType());
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
        package3.add("foo", db.get(5));
        package3.add("hello", db.get(6));

        updates[0] = package1;
        updates[1] = package2;
        updates[2] = package3;

        DataUpdate msg = new DataUpdate(updates);
        Function<Object,String> func = this::formatByte;
        byte[] message = msg.toBytes(); // content will change after call!
        assertEquals("toBytes() (strEquals)", Array.toString(expectedDataUpdate, func),  Array.toString(message, func));
        assertArrayEquals("toBytes() (arrayEquals)", expectedDataUpdate, message);
        package3.add("deleteme_2", db.get(4));
        package3.add("deleteme_1", db.get(3));
        msg = new DataUpdate(package3);
        message = msg.toBytes();
        System.out.println(Array.toString(message, func));
        assertEquals("add toBytes() (strEquals)", Array.toString(expectedDataUpdate_add, func),  Array.toString(message, func));
        assertArrayEquals("add toBytes() (arrayEquals)", expectedDataUpdate_add, message);
        package3.remove(3);
        package3.remove(4);
        msg = new DataUpdate(package3);
        message = msg.toBytes();
        System.out.println(Array.toString(message, func));
        assertEquals("del toBytes() (strEquals)", Array.toString(expectedDataUpdate_delete, func),  Array.toString(message, func));
        assertArrayEquals("del toBytes() (arrayEquals)", expectedDataUpdate_delete, message);

        DBList list = new DBList();
        db.add(list);
        assertEquals("List id", 13, (int) list.getID());

        list.append(package1);

        msg = new DataUpdate(list);
        message = msg.toBytes();

        assertEquals("list toBytes() (strEquals)", Array.toString(expectedDataUpdate_list, func),  Array.toString(message, func));
        assertArrayEquals("list toBytes() (arrayEquals)", expectedDataUpdate_list, message);
    }

    @Test
    public void testDataUpdate_Type() throws Exception {
        assertEquals("TYPE", MESSAGE_CHANNEL.DataUpdate, new DataUpdate(new DBBoolean(false)).getType());
    }

    @Test
    public void testLocalMapUpdate_Content() throws Exception {
        DBEntry[] updates = new DBEntry[3];

    }

    @Test
    public void testLocalMapUpdate_Type() throws Exception {
        assertEquals("TYPE", MESSAGE_CHANNEL.LocalMapUpdate, new LocalMapUpdate().getType());
    }

    /*@Test
    public void testCommandResult_Type() throws Exception {
        assertEquals("TYPE", MESSAGE_CHANNEL.CommandResult, new CommandResult().getType());
    }*/


    private String formatByte(Object o) {
        if (o instanceof Byte) {
            return String.format("%02X", (byte)o);
        }
        return "";
    }
}