package de.luckydonald.pipboyserver;

public enum MESSAGE_CHANNEL {
    KeepAlive(0),
    // Send if no data update is sent. Once per second.

    ConnectionAccepted(1),
    // The size of the packets are always zero.

    ConnectionRefused(2),
    // Signals the game is busy and your are not allowed to logon.
    // The size of the packets are always zero.

    DataUpdate(3),
    // This channel contains binary data the second packet
    // of the server contains the whole database.
    // Future packets do only updates to database.
    // The database ist is a array of items while lists or dicts
    // reference to the indexes of the array.

    LocalMapUpdate(4),

    Command(5),

    CommandResult(6);

    public static MESSAGE_CHANNEL get(int value) {
        for (MESSAGE_CHANNEL enumItem :  values()) {
            if (enumItem.value == value) {
                return enumItem;
            }
        }
        throw new ArrayIndexOutOfBoundsException("Could not find that enum.");
    }

    public final int value;

    MESSAGE_CHANNEL(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public byte toByte() {
        return ((byte) value);
    }

    public static void main(String[] args) {
        MESSAGE_CHANNEL foo = MESSAGE_CHANNEL.Command;
        System.out.println(foo.toString() + " | " + foo.name() + " | " + foo.value);
    }

}
