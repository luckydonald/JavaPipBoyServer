import de.luckydonald.pipboyserver.PipBoyServer.input.*;
import de.luckydonald.utils.ObjectWithLogger;
import org.junit.Before;
import org.junit.Test;

import static java.lang.Integer.toUnsignedString;
import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by  on
 *
 * @author luckydonald
 * @since 13.02.2016
 **/
public class BinFileReaderTest extends ObjectWithLogger {
    byte[] input = {115, 75, -61, -74, 105, 110, 103, 0};
    int[] expected_int8 = {115, 75, -61, -74, 105, 110, 103, 0};
    int[] expected_uint8 = {115, 75, 195, 182, 105, 110, 103, 0};
    long[] expected_int32 = {-1228715149L, 6778473L};
    long[] expected_uint32 = {3066252147L, 6778473L};
    long[] expected_int64 = {29113322918071155L};
    long[] expected_uint64 = {29113322918071155L};
    BinFileReader binFileReader = null;

    @Before
    public void setup() throws IOException {
        this.binFileReader = new BinFileReader(new BufferedInputStream(new ByteArrayInputStream(this.input)));
        //73 4B C3 B6 69 6E 67 00
        // 115, 75, -61, -74, 105, 110, 103, 0
        // 115, 75, 195, 182, 105, 110, 103, 0
        //  3066252147, 6778473
        // -1228715149, 6778473
        // 29113322918071155
        // 29113322918071155
        // "sköing\0"
    }
    @Test
    public void test_int8_t() throws IOException {
        for (int i = 0; i < expected_int8.length; i++) {
            SignedByte result = binFileReader.int8_t();
            Integer exp = expected_int8[i];
            assertEquals("Binary int8_t read " + Integer.toHexString(exp) + " asInt()", exp, result.asInt());
            assertEquals("Binary int8_t read " + Integer.toHexString(exp) + " getSignedValue()", (Byte)exp.byteValue(), result.getSignedValue());
        }
    }
    @Test
    public void test_uint8_t() throws IOException {
        for (int i = 0; i < expected_int8.length; i++) {
            UnsignedByte result = binFileReader.uint8_t();
            Integer exp = expected_int8[i];
            assertEquals("Binary uint8_t read " + Integer.toHexString(exp) + " asInt()", (Integer)Byte.toUnsignedInt(exp.byteValue()), result.asInt());
            assertEquals("Binary uint8_t read " + Integer.toHexString(exp) + " getSignedValue()", (Byte)exp.byteValue(), result.getSignedValue());
        }
    }
    @Test
    public void test_int32_t() throws IOException {
        for (int i = 0; i < expected_int32.length; i++) {
            SignedInteger result = binFileReader.int32_t();
            Long exp = expected_int32[i];
            assertEquals("Binary int32_t read " + exp + " asInt()", exp, result.asLong());
            assertEquals("Binary int32_t read " + exp + " getSignedValue()", (Integer)exp.intValue(), result.getSignedValue());
        }
    }
    @Test
    public void test_uint32_t() throws IOException {
        for (int i = 0; i < expected_uint32.length; i++) {
            UnsignedInteger result = binFileReader.uint32_t();
            Long exp = expected_uint32[i];
            assertEquals("Binary uint32_t read " + exp + " asInt()", exp, result.asLong());
            assertEquals("Binary uint32_t read " + exp + " getSignedValue()", (Integer)exp.intValue(), result.getSignedValue());
        }
    }
    @Test
    public void test_int64_t() throws IOException {
        for (int i = 0; i < expected_int64.length; i++) {
            SignedLong result = binFileReader.int64_t();
            Long exp = expected_int64[i];
            assertEquals("Binary int64_t read " + exp + " getSignedValue()", exp, result.getSignedValue());
        }
    }
    @Test
    public void test_uint64_t() throws IOException {
        for (int i = 0; i < expected_uint64.length; i++) {
            UnsignedLong result = binFileReader.uint64_t();
            Long exp = expected_uint64[i];
            assertEquals("Binary int64_t read " + exp + " getSignedValue()", exp, result.getSignedValue());
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
        assertEquals("Binary uint32_t read 6B 73 68 6F asLong() = 1869116267", expected1, result1.asLong());
        assertEquals("Binary uint32_t read 6B 73 68 6F getSignedValue() = 1869116267", (Integer)expected1.intValue(), result1.getSignedValue());
        byte[] input2 = {32, 75, -61, -68}; // 20 4B C3 BC
        Long expected2 = 3166915360L;//3166915360
        binFileReader = new BinFileReader(new BufferedInputStream(new ByteArrayInputStream(input2)));
        UnsignedInteger result2 = binFileReader.uint32_t();
        assertEquals("Binary uint32_t read 20 4B C3 BC asLong() = 3166915360", expected2, result2.asLong());
        assertEquals("Binary uint32_t read 20 4B C3 BC getSignedValue() = -1128051936", (Integer)expected2.intValue(), result2.getSignedValue());
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
        assertEquals("Binary int32_t read 6B 73 68 6F asLong() = 1869116267", expected1, result1.asLong());
        assertEquals("Binary int32_t read 6B 73 68 6F getSignedValue() = 1869116267", (Integer)expected1.intValue(), result1.getSignedValue());
        byte[] input2 = {32, 75, -61, -68}; // 20 4B C3 BC
        Long expected2 = -1128051936L;//-1128051936
        binFileReader = new BinFileReader(new BufferedInputStream(new ByteArrayInputStream(input2)));
        SignedInteger result2 = binFileReader.int32_t();
        assertEquals("Binary int32_t read 20 4B C3 BC asLong() = -1128051936", expected2, result2.asLong());
        assertEquals("Binary int32_t read 20 4B C3 BC getSignedValue() = -1128051936", (Integer)expected2.intValue(), result2.getSignedValue());
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
            assertEquals("Binary int8_t read " + Integer.toHexString(exp) + " asInt()", exp, result.asInt());
            assertEquals("Binary int8_t read " + Integer.toHexString(exp) + " getSignedValue()", (Byte)exp.byteValue(), result.getSignedValue());
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
            assertEquals("Binary uint8_t read " + Integer.toHexString(exp) + " asInt()", (Integer)Byte.toUnsignedInt(exp.byteValue()), result.asInt());
            assertEquals("Binary uint8_t read " + Integer.toHexString(exp) + " getSignedValue()", (Byte)exp.byteValue(), result.getSignedValue());
        }
    }

}