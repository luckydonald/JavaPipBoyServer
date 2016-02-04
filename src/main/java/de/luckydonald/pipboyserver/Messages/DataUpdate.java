package de.luckydonald.pipboyserver.Messages;

import de.luckydonald.pipboyserver.PipBoyServer.DBEntry;

import java.nio.ByteBuffer;

public class DataUpdate extends Message{
    static final int TYPE = 3;
    public DataUpdate(byte[] data) {
        super(TYPE, data);
    }
    public DataUpdate(DBEntry e) {
        this(e.getBytes());
    }
}

class DATA_UPDATE_TYPES {
    public static final int BOOL    = 0;  // 1: true if non zero
    public static final int INT_8   = 1;  // 1: signed
    public static final int UINT_8  = 2;  // 1: unsigned
    public static final int INT_32  = 3;  // 4: signed
    public static final int UINT_32 = 4;  // 4: unsigned
    public static final int FLOAT   = 5;  // 4: float
    public static final int STRING  = 6;  // n: null terminated, dynamic length
    public static final int ARRAY   = 7;  // 2: element count; then $n 4 byte nodeId
    public static final int OBJECT  = 8;  // 2: element count; then $n 4 byte nodeId with null terminated string following; then 2: removed element count; then $n 4 byte removed nodeId with null terminated string following

}


/*
struct Entry {
  uint8_t type; // 8 bits
  uint32_t id;
  switch (type) {
    case 0:
      uint8_t boolean;
      break;
    case 1:
      sint8_t integer;
      break;
    case 2:
      uint8_t integer;
      break;
    case 3:
      sint32_t integer;
      break;
    case 4:
      uint32_t integer;
      break;
    case 5:
      float32_t floating_point;
      break;
    case 6:
      char_t *string; // zero-terminated
      break;
    case 7: // list
      uint16_t count;
      uint32_t references[count];
      break;
    case 8:
      uint16_t insert_count;
      DictEntry[insert_count];
      uint16_t remove_count;
      uint32_t references[remove_count];
      break;
  }
};
struct DictEntry {
      uint32_t reference;
      char_t *name; // zero-terminated
};
 */