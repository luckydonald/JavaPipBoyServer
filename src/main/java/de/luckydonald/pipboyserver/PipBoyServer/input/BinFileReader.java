package de.luckydonald.pipboyserver.PipBoyServer.input;

import de.luckydonald.pipboyserver.PipBoyServer.Database;
import de.luckydonald.pipboyserver.PipBoyServer.exceptions.AlreadyInsertedException;
import de.luckydonald.pipboyserver.PipBoyServer.exceptions.AlreadyTakenException;
import de.luckydonald.pipboyserver.PipBoyServer.types.*;
import de.luckydonald.utils.ObjectWithLogger;

import java.io.*;
import java.util.ArrayList;

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
    public Float float32_t() throws IOException {
        int i1 = readByte(),
            i2 = readByte(),
            i3 = readByte(),
            i4 = readByte();
        int integer = (i4 << 24) + (i3 << 16) + (i2 << 8) + (i1);
        return Float.intBitsToFloat(integer);
    }
    public Double float64_t() throws IOException {
        long i1 = readByte(),
                i2 = readByte(),
                i3 = readByte(),
                i4 = readByte(),
                i5 = readByte(),
                i6 = readByte(),
                i7 = readByte(),
                i8 = readByte();
        long integer = (i8 << 56) + (i7 << 48) + (i6 << 40) + (i5 << 32) + (i4 << 24) + (i3 << 16) + (i2 << 8) + (i1);
        return Double.longBitsToDouble(integer);
    }
    public String string_t() throws IOException {
        /*struct  String {
            uint32_t length;
            char str[length];
        };
        */

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        UnsignedInteger length = uint32_t();
        for (long i = 0; i < length.asLong(); i++) {
            int c;
            c = readByte();
            b.write(c);
        }
        return new String(b.toByteArray(), "UTF-8");
    }

    public int readByte() throws IOException {
        int read = buff.read();
        if (read == -1) {
            throw new EOFException();
        }
        return read;
    }

    public ArrayList<DBEntry> readAll(Database db) throws IOException {
        ArrayList<DBEntry> entries = new ArrayList<>();
        for ( ;; ) {
            try {
                entries.add(readNextEntry());
            } catch (EOFException ignore) {
                break;
            }
        }
        return entries;
    }
    public DBEntry readNextEntry() throws IOException {
        int value_type = uint8_t().asInt();
        UnsignedInteger value_id = uint32_t();
        if (value_type == 0) {
            // Primitive
            int primitive_type = uint8_t().asInt();
            switch (primitive_type) {
                case 0: {
                    SignedInteger integer = int32_t();
                    return new DBInteger32(integer.getSignedValue());
                }
                case 1: {
                    UnsignedInteger integer = uint32_t();
                    return new DBInteger32(integer.getSignedValue());
                }
                case 2: {
                    SignedLong integer = int64_t();
                    return new DBInteger32(integer.getSignedValue().intValue());
                }
                case 3: {
                    Float integer = float32_t();
                    return new DBFloat(integer);
                }
                case 4: {
                    Double integer = float64_t();
                    return new DBFloat(integer.floatValue());
                }
                case 5: {
                    int integer = uint8_t().asInt();
                    return new DBBoolean(integer != 0);
                }
                case 6: {
                    String string = string_t();
                    return new DBString(string);
                }
                default: {
                    throw new InvalidObjectException("unknown primitive type: " + primitive_type);
                }
            }

        } else if (value_type == 1) {
            // array/list
            UnsignedInteger list_count = uint32_t();
            DBList list = new DBList();
            for ( long i = 0; i < list_count.asLong(); i++) {
                UnsignedInteger element_index = uint32_t();
                DBEntry element_value = this.readNextEntry();
                try {
                    list.append(element_value);
                } catch (AlreadyInsertedException | AlreadyTakenException e) {
                    e.printStackTrace();
                }
                System.out.println("Got List entrie, index is " + element_index + ", " +
                                   "Element is" + element_value.toSimpleString(false)
                );

            }
            return list;
        } else if (value_type == 2) {
            // object/dict
            UnsignedInteger dict_count = uint32_t();
            DBDict dict = new DBDict();
            for (long i = 0; i < dict_count.asLong(); i++) {
                String dict_key = string_t();
                DBEntry dict_value = readNextEntry();
                try {
                    dict.add(dict_key, dict_value);
                } catch (AlreadyInsertedException | AlreadyTakenException e) {
                    e.printStackTrace();
                }
            }
            return dict;
        } else {
            throw new InvalidObjectException("unknown type: " + value_type);
        }
    }
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
struct  String {
    uint32_t length;
    char str[length];
};
 */