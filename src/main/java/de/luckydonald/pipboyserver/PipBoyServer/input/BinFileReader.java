package de.luckydonald.pipboyserver.PipBoyServer.input;

import de.luckydonald.pipboyserver.PipBoyServer.Database;
import de.luckydonald.pipboyserver.PipBoyServer.exceptions.AlreadyInsertedException;
import de.luckydonald.pipboyserver.PipBoyServer.exceptions.AlreadyTakenException;
import de.luckydonald.pipboyserver.PipBoyServer.types.*;
import de.luckydonald.utils.ObjectWithLogger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by  on
 *
 * @author luckydonald
 * @since 13.02.2016
 **/
public class BinFileReader extends ObjectWithLogger {
    private ArrayList<BinFileReadLogger> loggerz = new ArrayList<>();
    private BufferedInputStream buff;
    protected ByteArrayOutputStream buffStore = new ByteArrayOutputStream();
    long pos;

    public BinFileReader(BufferedInputStream buff) {
        this.buff = buff;
    }

    public BinFileReader(URL url) throws IOException {
        this(from(url));
    }

    public BinFileReader(File file) throws IOException {
        this(from(file));
    }

    private static BufferedInputStream from(File file)  throws IOException {
        return new BufferedInputStream(new FileInputStream(file));
    }

    private static BufferedInputStream from(URL url) throws IOException {
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        int code = uc.getResponseCode();
        if (code != 200) {
            getStaticLogger().warning("HTTP Status is " + code);
        }
        return new BufferedInputStream(uc.getInputStream());
    }

    public UnsignedLong uint64_t() throws IOException {
        long posBefore = this.pos;
        long i1 = readByte(),
                i2 = readByte(),
                i3 = readByte(),
                i4 = readByte(),
                i5 = readByte(),
                i6 = readByte(),
                i7 = readByte(),
                i8 = readByte();
        long integer = (i8 << 56) + (i7 << 48) + (i6 << 40) + (i5 << 32) + (i4 << 24) + (i3 << 16) + (i2 << 8) + (i1);
        UnsignedLong result = new UnsignedLong(integer);
        getLogger().fine("uint64_t:  " + result + " (" + posBefore + "-" + pos + ")");
        return result;
    }
    public SignedLong int64_t() throws IOException {
        long posBefore = this.pos;
        long i1 = readByte(),
                i2 = readByte(),
                i3 = readByte(),
                i4 = readByte(),
                i5 = readByte(),
                i6 = readByte(),
                i7 = readByte(),
                i8 = readByte();
        long integer = (i8 << 56) + (i7 << 48) + (i6 << 40) + (i5 << 32) + (i4 << 24) + (i3 << 16) + (i2 << 8) + (i1);
        SignedLong result = new SignedLong(integer);
        getLogger().fine("int64_t:   " + result + " (" + posBefore + "-" + pos + ")");
        return result;
    }

    public UnsignedInteger uint32_t() throws IOException {
        long posBefore = this.pos;
        int i1 = readByte(),
            i2 = readByte(),
            i3 = readByte(),
            i4 = readByte();
        int integer = (i4 << 24) + (i3 << 16) + (i2 << 8) + (i1);
        UnsignedInteger result = new UnsignedInteger(integer);
        getLogger().fine("uint32_t:  " + result + " (" + posBefore + "-" + pos + ")");
        return result;
    }
    public SignedInteger int32_t() throws IOException {
        long posBefore = this.pos;
        int i1 = readByte(),
            i2 = readByte(),
            i3 = readByte(),
            i4 = readByte();
        int integer = (i4 << 24) + (i3 << 16) + (i2 << 8) + (i1);
        SignedInteger result = new SignedInteger(integer);
        getLogger().fine("int32_t:   " + result + " (" + posBefore + "-" + pos + ")");
        return result;
    }

    public UnsignedByte uint8_t() throws IOException {
        long posBefore = this.pos;
        Integer i1 = readByte();
        UnsignedByte result = new UnsignedByte(i1.byteValue());
        getLogger().fine("uint8_t:   " + result + " (" + posBefore + "-" + pos + ")");
        return result;
    }
    public SignedByte int8_t() throws IOException {
        long posBefore = this.pos;
        Integer i1 = readByte();
        SignedByte result = new SignedByte(i1.byteValue());
        getLogger().fine("int8_t:    " + result + " (" + posBefore + "-" + pos + ")");
        return result;
    }
    public Float float32_t() throws IOException {
        long posBefore = this.pos;
        int i1 = readByte(),
            i2 = readByte(),
            i3 = readByte(),
            i4 = readByte();
        int integer = (i4 << 24) + (i3 << 16) + (i2 << 8) + (i1);
        Float result = Float.intBitsToFloat(integer);
        getLogger().fine("float32_t: " + result + " (" + posBefore + "-" + pos + ")");
        return result;
    }
    public Double float64_t() throws IOException {
        long posBefore = this.pos;
        long i1 = readByte(),
                i2 = readByte(),
                i3 = readByte(),
                i4 = readByte(),
                i5 = readByte(),
                i6 = readByte(),
                i7 = readByte(),
                i8 = readByte();
        long integer = (i8 << 56) + (i7 << 48) + (i6 << 40) + (i5 << 32) + (i4 << 24) + (i3 << 16) + (i2 << 8) + (i1);
        Double result = Double.longBitsToDouble(integer);
        getLogger().fine("float64_t: " + result + " (" + posBefore + "-" + pos + ")");
        return result;
    }
        public String string_t() throws IOException {
            return string_t(true);
        }
        public String string_t(boolean hasLength) throws IOException {
        /*struct  String {
            uint32_t length;   // present only if hasLength = true
            char str[length];  // ends with \0 if not hasLength
        };
        */
        long posBefore = this.pos;
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        if (hasLength) {
            UnsignedInteger length = uint32_t();
            for (long i = 0; i < length.asLong(); i++) {
                int c;
                c = readByte();
                b.write(c);
            }
        } else {
            int c;
            while ((c = readByte()) != 0) {
                b.write(c);
            }
        }
        String result = new String(b.toByteArray(), "UTF-8");
        getLogger().fine("string_t:  " + result + " (" + posBefore + "-" + pos + ")");
        return result;
    }

    public synchronized int readByte() throws IOException {
        int read = buff.read();
        if (read == -1) {
            throw new EOFException();
        }
        buffStore.write(read);
        pos++;
        return read;
    }

    public ArrayList<DBEntry> readAll(Database db) throws IOException {
        ArrayList<DBEntry> entries = new ArrayList<>();
        for ( ;; ) {
            try {
                entries.add(readNextEntry(db));
            } catch (EOFException ignore) {
                break;
            }
        }
        return entries;
    }
    public DBEntry readNextEntry(Database db) throws IOException {
        long posBefore = this.pos;
        BinFileReadLogger logger = new BinFileReadLogger(posBefore);
        getLoggerz().add(logger);
        int value_type = uint8_t().asInt();
        UnsignedInteger value_id = uint32_t();
        DBEntry d;
        if (value_type == 0) {
            // Primitive
            getLogger().fine("Primitive " + value_id + " start: " + (posBefore));
            d = this.readPrimitive(db, value_id);
            getLogger().fine("Primitive " + value_id + " ended: " + pos + " > " + d.toSimpleString());
        } else if (value_type == 1) {
            // array/list
            getLogger().fine("List " + value_id + " start: " + (posBefore));
            d = this.readList(db, value_id);
            getLogger().fine("List " + value_id + " ended: " + pos + " > " + d.toSimpleString());
        } else if (value_type == 2) {
            // object/dict
            getLogger().fine("Dict " + value_id + " start: " + (posBefore));
            d = this.readDict(db, value_id);
            getLogger().fine("Dict " + value_id + " ended: " + pos + " > " + d.toSimpleString());
        } else {
            throw new InvalidObjectException("unknown type: " + value_type + ", position: " + pos);
        }
        jumpOut(logger);
        return d;
    }

    public void jumpOut(BinFileReadLogger logger) {
        logger.setEndPosition(pos);
    }

    private DBEntry readDict(Database db, UnsignedInteger value_id) throws IOException {
        BinFileReadLogger logger = new BinFileReadLogger(pos);
        getLoggerz().add(logger);
        UnsignedInteger dict_count = uint32_t();
        DBDict dict = (DBDict) db.add(value_id.getSignedValue(),new DBDict());
        for (long i = 0; i < dict_count.asLong(); i++) {
            String dict_key = string_t();
            DBEntry dict_value = readNextEntry(db);
            try {
                dict.add(dict_key, dict_value);
            } catch (AlreadyInsertedException | AlreadyTakenException e) {
                e.printStackTrace();
            }
        }
        jumpOut(logger);
        return dict;
    }

    private DBList readList(Database db, UnsignedInteger value_id) throws IOException {
        BinFileReadLogger logger = new BinFileReadLogger(pos);
        getLoggerz().add(logger);
        UnsignedInteger list_count = uint32_t();
        DBList list = (DBList) db.add(value_id.getSignedValue(), new DBList());
        for ( long i = 0; i < list_count.asLong(); i++) {
            UnsignedInteger element_index = uint32_t();
            DBEntry element_value = this.readNextEntry(db);
            try {
                list.append(element_value);
            } catch (AlreadyInsertedException | AlreadyTakenException e) {
                e.printStackTrace();
            }
            if (!element_index.asLong().equals(i)) {
                getLogger().severe("List index does not fit!" +
                        "List index is " + element_index + ", i is " + i + ", " +
                        "and the element is " + element_value.toSimpleString(false) + ". " +
                        "Current Position: " + pos
                );
            }

        }
        jumpOut(logger);
        return list;
    }

    private DBEntry readPrimitive(Database db, UnsignedInteger value_id) throws IOException {
        BinFileReadLogger logger = new BinFileReadLogger(pos);
        getLoggerz().add(logger);
        int primitive_type = uint8_t().asInt();
        DBEntry d;
        switch (primitive_type) {
            case 0: {
                SignedInteger integer = int32_t();
                d = db.add(value_id.getSignedValue(), new DBInteger32(integer.getSignedValue()));
                break;
            }
            case 1: {
                UnsignedInteger integer = uint32_t();
                d = db.add(value_id.getSignedValue(), new DBInteger32(integer.getSignedValue()));
                break;
            }
            case 2: {
                SignedLong integer = int64_t();
                d = db.add(value_id.getSignedValue(), new DBInteger32(integer.getSignedValue().intValue()));
                break;
            }
            case 3: {
                Float integer = float32_t();
                d = db.add(value_id.getSignedValue(), new DBFloat(integer));
                break;
            }
            case 4: {
                Double integer = float64_t();
                d = db.add(value_id.getSignedValue(), new DBFloat(integer.floatValue()));
                break;
            }
            case 5: {
                int integer = uint8_t().asInt();
                d = db.add(value_id.getSignedValue(), new DBBoolean(integer != 0));
                break;
            }
            case 6: {
                String string = string_t();
                if ("Klassisches Radio".equals(string)) {
                    System.out.println("Klassisches Radio here!");
                }
                d = db.add(value_id.getSignedValue(), new DBString(string));
                break;
            }
            default: {
                getLogger().info("Position: " + pos);
                throw new InvalidObjectException("unknown primitive type: " + primitive_type);
            }
        }
        jumpOut(logger);
        return d;

    }

    public ArrayList<BinFileReadLogger> getLoggerz() {
        return loggerz;
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