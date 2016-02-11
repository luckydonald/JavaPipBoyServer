package de.luckydonald.pipboyserver.PipBoyServer;

/**
 * Created by luckydonald on 11.02.16.
 */
public enum EntryType {
    BOOLEAN (1),    // java: bool
    INT8    (2),    // java: byte
    INT32   (3),    // java: int
    FLOAT   (5),    // java: float
    STRING  (6),    // java: String
    LIST    (7),
    DICT    (8);

    private final int type;
    public int getInt() {
        return this.type;
    }
    public byte getByte() {
        return (byte) this.getInt();
    }
    EntryType(int type) {
        this.type = type;
    }
}

