package de.luckydonald.pipboyserver.PipBoyServer.input;

import de.luckydonald.utils.ObjectWithLogger;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Created by  on
 *
 * @author luckydonald
 * @since 13.02.2016
 **/
public class BinFileReader extends ObjectWithLogger {
    private BufferedInputStream buff;

    public BinFileReader(BufferedInputStream buff) {
        this.buff = buff;
    }
    public static BinFileReader fromFile(String filename)  throws IOException {
        return new BinFileReader(new BufferedInputStream(new FileInputStream(new File(filename))));
    }

    public UnsignedLong uint64_t() throws IOException {
        long i1 = readByte(),
                i2 = readByte(),
                i3 = readByte(),
                i4 = readByte(),
                i5 = readByte(),
                i6 = readByte(),
                i7 = readByte(),
                i8 = readByte();
        long integer = (i8 << 56) + (i7 << 48) + (i6 << 40) + (i5 << 32) + (i4 << 24) + (i3 << 16) + (i2 << 8) + (i1);
        return new UnsignedLong(integer);
    }
    public SignedLong int64_t() throws IOException {
        long i1 = readByte(),
                i2 = readByte(),
                i3 = readByte(),
                i4 = readByte(),
                i5 = readByte(),
                i6 = readByte(),
                i7 = readByte(),
                i8 = readByte();
        long integer = (i8 << 56) + (i7 << 48) + (i6 << 40) + (i5 << 32) + (i4 << 24) + (i3 << 16) + (i2 << 8) + (i1);
        return new SignedLong(integer);
    }

    public UnsignedInteger uint32_t() throws IOException {
        int i1 = readByte(),
            i2 = readByte(),
            i3 = readByte(),
            i4 = readByte();
        int integer = (i4 << 24) + (i3 << 16) + (i2 << 8) + (i1);
        return new UnsignedInteger(integer);
    }
    public SignedInteger int32_t() throws IOException {
        int i1 = readByte(),
            i2 = readByte(),
            i3 = readByte(),
            i4 = readByte();
        int integer = (i4 << 24) + (i3 << 16) + (i2 << 8) + (i1);
        return new SignedInteger(integer);
    }

    public UnsignedByte uint8_t() throws IOException {
        Integer i1 = readByte();
        return new UnsignedByte(i1.byteValue());
    }
    public SignedByte int8_t() throws IOException {
        Integer i1 = readByte();
        return new SignedByte(i1.byteValue());
    }

    public int readByte() throws IOException {
        int read = buff.read();
        if (read == -1) {
            throw new EOFException();
        }
        return read;
    }
    public void readStuff() throws IOException {
        while (buff.available() > 0) {
            int value_type = uint8_t().asInt();
            UnsignedInteger value_id = uint32_t();
            if (value_type == 0) {
                // Primtive
                int primitive_type = uint8_t().asInt();
                switch (primitive_type) {
                    case 0: {
                        SignableInteger integer = int32_t();
                        break;
                    }
                    case 1: {
                        SignableInteger integer = uint32_t();
                        break;
                    }
                    case 2: {
                        //integer = int64_t();
                    }
                }

            }

        }
    }
}
class Value extends ObjectWithLogger {
    UnsignedByte type;
    UnsignedInteger id;

}
class Primitive extends ObjectWithLogger {

}


/*
struct  String {
    uint32_t length;
    char str[length];
};

struct Value {
    uint8_t type;
    uint32_t id;
    switch (type) {
        case 0: // Primitive
            uint8_t primitive;
            switch (primitive) {
                case 0:
                    int32_t integer;
                    break;
                case 1:
                    uint32_t integer;
                    break;
                case 2:
                    int64_t integer;
                    break;
                case 3:
                    float32_t floating_point;
                    break;
                case 4:
                    float64_t floating_point;
                    break;
                case 5:
                    uint8_t boolean;
                    break;
                case 6:
                    String string;
                    break;
            }
        case 1: // Array
            uint32_t count;
            for ( i = 0; i < count; i++) {
                uint32_t index;
                Value v;
            }
            break;
        case 2: // Object
            uint32_t count;
            for ( i = 0; i < count; i++) {
                String key;
                Value v;
            }
            break;
    }
};
 */