package de.luckydonald.pipboyserver;

public class MESSAGE_CHANNEL {
    public static final int KeepAlive = 0,
                            // Send if no data update is sent. Once per second.

                            ConnectionAccepted = 1,
                            // The size of the packets are always zero.

                            ConnectionRefused = 2,
                            // Signals the game is busy and your are not allowed to logon.
                            // The size of the packets are always zero.

                            DataUpdate = 3,
                            // This channel contains binary data the second packet
                            // of the server contains the whole database.
                            // Future packets do only updates to database.
                            // The database ist is a array of items while lists or dicts
                            // reference to the indexes of the array.

                            LocalMapUpdate = 4,

                            Command = 5,

                            CommandResult = 6;
}
