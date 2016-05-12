package de.luckydonald.piboyserver.PipBoyServer.input;

import de.luckydonald.pipboyserver.PipBoyServer.Database;
import de.luckydonald.pipboyserver.PipBoyServer.input.*;
import de.luckydonald.pipboyserver.PipBoyServer.types.DBEntry;
import de.luckydonald.utils.ObjectWithLogger;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
// import java.util.logging.Level;

/**
 * @author luckydonald
 * @since 13.02.2016
 **/
public class BinFileReaderTest extends ObjectWithLogger {
    private byte[] input = {115, 75, -61, -74, 105, 110, 103, 0};
    private int[] expected_int8 = {115, 75, -61, -74, 105, 110, 103, 0};
    private int[] expected_uint8 = {115, 75, 195, 182, 105, 110, 103, 0};
    private long[] expected_int32 = {-1228715149L, 6778473L};
    private long[] expected_uint32 = {3066252147L, 6778473L};
    private long[] expected_int64 = {29113322918071155L};
    private long[] expected_uint64 = {29113322918071155L};
    private float[] expected_float32 = {(float) -5.82023631068295799195766448975E-6, (float)9.49866380536723574519558603963E-39};
    private double[] expected_float64 = {1.0427274872209864735496543959E-306};
    private String[] expected_string = {"sKöing"}; // HTML sK&uuml;ing&#0; OR sK&#195;&#182;ing&#0;
    //73 4B C3 B6 69 6E 67 00
    // 115, 75, -61, -74, 105, 110, 103, 0
    // 115, 75, 195, 182, 105, 110, 103, 0
    //  3066252147, 6778473
    // -1228715149, 6778473
    // 29113322918071155
    // 29113322918071155
    // -0.000006, -0.000000
    // -1.042727e-303
    // "sKöing\0" //sK&#195;&#182;ing
    BinFileReader binFileReader = null;

    @Before
    public void setUp() throws IOException {
        this.binFileReader = new BinFileReader(new BufferedInputStream(new ByteArrayInputStream(this.input)));
        /*this.binFileReader.addLogConsoleHandler(Level.FINER).setFilter(
                record -> record.getSourceMethodName().equals("readNextEntry")
        );*/
    }
    @Test
    public void test_int8_t() throws IOException {
        for (Integer exp : expected_int8) {
            SignedByte result = binFileReader.int8_t();
            assertEquals("int8_t " + Integer.toHexString(exp) + " asInt()", exp, result.asInt());
            assertEquals("int8_t " + Integer.toHexString(exp) + " getSignedValue()", (Byte) exp.byteValue(), result.getSignedValue());
            assertEquals("int8_t " + Integer.toHexString(exp) + " toString()", ((Byte) exp.byteValue()).toString(), result.toString());
        }
    }
    @Test
    public void test_uint8_t() throws IOException {
        for (Integer exp : expected_uint8) {
            UnsignedByte result = binFileReader.uint8_t();
            assertEquals("uint8_t " + Integer.toHexString(exp) + " asInt()", (Integer) Byte.toUnsignedInt(exp.byteValue()), result.asInt());
            assertEquals("uint8_t " + Integer.toHexString(exp) + " getSignedValue()", (Byte) exp.byteValue(), result.getSignedValue());
            assertEquals("uint8_t " + Integer.toHexString(exp) + " toString()", Integer.toString((((exp < 0 ? exp + 256: exp)))), result.toString());
        }
    }
    @Test
    public void test_int32_t() throws IOException {
        for (Long exp : expected_int32) {
            SignedInteger result = binFileReader.int32_t();
            assertEquals("int32_t " + exp + " asInt()", exp, result.asLong());
            assertEquals("int32_t " + exp + " getSignedValue()", (Integer) exp.intValue(), result.getSignedValue());
            assertEquals("int32_t " + exp + " toString()", exp.toString(), result.toString());
        }
    }
    @Test
    public void test_uint32_t() throws IOException {
        for (Long exp : expected_uint32) {
            UnsignedInteger result = binFileReader.uint32_t();
            assertEquals("uint32_t " + exp + " asInt()", exp, result.asLong());
            assertEquals("uint32_t " + exp + " getSignedValue()", (Integer) exp.intValue(), result.getSignedValue());
            assertEquals("uint32_t " + exp + " toString()", Long.toString((((exp < 0 ? exp + 256: exp)))), result.toString());
        }
    }
    @Test
    public void test_int64_t() throws IOException {
        for (Long exp : expected_int64) {
            SignedLong result = binFileReader.int64_t();
            assertEquals("int64_t " + exp + " getSignedValue()", exp, result.getSignedValue());
            assertEquals("int64_t " + exp + " toString()", exp.toString(), result.toString());
        }
    }
    @Test
    public void test_uint64_t() throws IOException {
        for (Long exp : expected_uint64) {
            UnsignedLong result = binFileReader.uint64_t();
            assertEquals("uint64_t " + exp + " getSignedValue()", exp, result.getSignedValue());
            assertEquals("uint64_t " + exp + " toString()", Long.toString(exp < 0 ? exp + 256: exp), result.toString());
        }
    }
    @Test
    public void test_float32_t() throws IOException {
        for (Float exp : expected_float32) {
            Float result = binFileReader.float32_t();
            assertEquals("float32_t", exp, result);
            assertEquals("float32_t toString()", exp.toString(), result.toString());
        }
    }
    @Test
    public void test_float64_t() throws IOException {
        for (Double exp : expected_float64) {
            Double result = binFileReader.float64_t();
            assertEquals("float64_t", exp, result);
            assertEquals("float64_t", exp.toString(), result.toString());
        }
    }
    @Test
    public void test_string_t_endsWithNull() throws IOException {
        for (String exp : expected_string) {
            String result = binFileReader.string_t(false);
            assertEquals("string_t(length=false)", exp, result);
            assertEquals("string_t(length=false) toString()", exp.toString(), result.toString());
        }
    }
    @Test
    public void test_string_t_length() throws IOException {
        int string_lenght = this.input.length - 1; // ignore the \0 at the end.
        byte[] foo = new byte[string_lenght+4];
        // Insert the string length at first 4 positions. Is LittleEndian!
        foo[0] = (byte)(string_lenght);
        foo[1] = (byte)(string_lenght >>> 8);
        foo[2] = (byte)(string_lenght >>> 16);
        foo[3] = (byte)(string_lenght >>> 24);
        System.arraycopy(this.input, 0, foo, 4, string_lenght); // ignore the \0 at the end
        this.binFileReader = new BinFileReader(new BufferedInputStream(new ByteArrayInputStream(foo)));
        for (String exp : expected_string) {
            String result = binFileReader.string_t(true);
            assertEquals("string_t(length=true)", exp, result);
            assertEquals("string_t(length=true) toString()", exp.toString(), result.toString());
        }
    }

    @Test
    public void test_manuel_uint32_t() throws IOException {
        //6B 73 68 6F
        //107 115 104 111
        //29547 28520
        //1869116267

        byte[] input1 = {107,115,104,111}; //6B 73 68 6F
        Long expected1 = 1869116267L;  //1869116267
        BinFileReader binFileReader = new BinFileReader(new BufferedInputStream(new ByteArrayInputStream(input1)));
        UnsignedInteger result1 = binFileReader.uint32_t();
        assertEquals("uint32_t6B 73 68 6F asLong() = 1869116267", expected1, result1.asLong());
        assertEquals("uint32_t6B 73 68 6F getSignedValue() = 1869116267", (Integer)expected1.intValue(), result1.getSignedValue());
        byte[] input2 = {32, 75, -61, -68}; // 20 4B C3 BC
        Long expected2 = 3166915360L;//3166915360
        binFileReader = new BinFileReader(new BufferedInputStream(new ByteArrayInputStream(input2)));
        UnsignedInteger result2 = binFileReader.uint32_t();
        assertEquals("uint32_t20 4B C3 BC asLong() = 3166915360", expected2, result2.asLong());
        assertEquals("uint32_t20 4B C3 BC getSignedValue() = -1128051936", (Integer)expected2.intValue(), result2.getSignedValue());
    }

    @Test
    public void test_manuel_int32_t() throws IOException {
        //6B 73 68 6F
        //107 115 104 111
        //29547 28520
        //1869116267

        byte[] input1 = {107,115, 104, 111}; //6B 73 68 6F
        Long expected1 = 1869116267L;  //1869116267
        BinFileReader binFileReader = new BinFileReader(new BufferedInputStream(new ByteArrayInputStream(input1)));
        SignedInteger result1 = binFileReader.int32_t();
        assertEquals("int32_t6B 73 68 6F asLong() = 1869116267", expected1, result1.asLong());
        assertEquals("int32_t6B 73 68 6F getSignedValue() = 1869116267", (Integer)expected1.intValue(), result1.getSignedValue());
        byte[] input2 = {32, 75, -61, -68}; // 20 4B C3 BC
        Long expected2 = -1128051936L;//-1128051936
        binFileReader = new BinFileReader(new BufferedInputStream(new ByteArrayInputStream(input2)));
        SignedInteger result2 = binFileReader.int32_t();
        assertEquals("int32_t20 4B C3 BC asLong() = -1128051936", expected2, result2.asLong());
        assertEquals("int32_t20 4B C3 BC getSignedValue() = -1128051936", (Integer)expected2.intValue(), result2.getSignedValue());
    }
    @Test
    public void test_manuel_int8_t() throws IOException {
        //73 4B C3 BC
        //115, 75, -61, -68
        byte[] input = {115, 75, -61, -68};
        int[] expected = {115, 75, -61, -68};
        BinFileReader binFileReader = new BinFileReader(new BufferedInputStream(new ByteArrayInputStream(input)));
        for (int i = 0; i < input.length; i++) {
            SignedByte result = binFileReader.int8_t();
            Integer exp = expected[i];
            assertEquals("int8_t " + Integer.toHexString(exp) + " asInt()", exp, result.asInt());
            assertEquals("int8_t " + Integer.toHexString(exp) + " getSignedValue()", (Byte)exp.byteValue(), result.getSignedValue());
        }
    }
    @Test
    public void test_manuel_uint8_t() throws IOException {
        //73 4B C3 BC
        //115, 75, -61, -68
        byte[] input = {115, 75, -61, -68};
        int[] expected = {0x73, 0x4B, 0xC3, 0xBC};
        BinFileReader binFileReader = new BinFileReader(new BufferedInputStream(new ByteArrayInputStream(input)));
        for (int i = 0; i < input.length; i++) {
            UnsignedByte result = binFileReader.uint8_t();
            Integer exp = expected[i];
            assertEquals("uint8_t " + Integer.toHexString(exp) + " asInt()", (Integer)Byte.toUnsignedInt(exp.byteValue()), result.asInt());
            assertEquals("uint8_t " + Integer.toHexString(exp) + " getSignedValue()", (Byte)exp.byteValue(), result.getSignedValue());
        }
    }

    @Test
    public void test_main_offline() throws IOException {
        File offline = new File("OfflineData.bin");
        if(offline.exists() && !offline.isDirectory()) {
            this.getLogger().info("OfflineData.bin file exists.");
            if (GraphicsEnvironment.isHeadless()) {
                this.binFileReader = new BinFileReader(offline);
            } else {
                this.binFileReader = new BinFileReaderGui(offline);
            }
            do_read_from_bin();
            System.out.println("");
        } else {
            getLogger().warning("No OfflineData.bin file!");
        }
    }

    @Test
    public void test_main_online() throws IOException {
        URL online = new URL("http://luckydonald.github.io/OfflineData.bin");
        if (GraphicsEnvironment.isHeadless()) {
            this.binFileReader = new BinFileReader(online);
        } else {
            this.binFileReader = new BinFileReaderGui(online);
        }
        do_read_from_bin();
    }

    public void do_read_from_bin() throws IOException {
        Database db = new Database();
        DBEntry foo = binFileReader.readNextEntry(db);
        ArrayList<BinFileReadLogger> loggerz = binFileReader.getLoggerz();
        long endPos = -1;
        this.getLogger();
        for (BinFileReadLogger log : loggerz) {
            endPos = log.getEndPosition();
            //System.out.println(log);
        }
        if (!GraphicsEnvironment.isHeadless()) {
            ((BinFileReaderGui) binFileReader).updateHex();
        }
        System.out.println(foo.toSimpleString());
        if (endPos < 499796) {
            //Le fail.
            this.getLogger().warning("Didn't read the complete file. Read " + endPos + " of 499796 bytes.");
        }
    }
}