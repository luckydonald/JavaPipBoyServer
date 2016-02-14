package de.luckydonald.pipboyserver.PipBoyServer.input;

import de.luckydonald.utils.ObjectWithLogger;

public class SignedByte extends SignableByte{

    /**
     * Constructs a newly allocated {@code Byte} object that
     * represents the specified {@code Byte} argument.
     *
     * @param value the value to be represented by the
     *              {@code Byte} object.
     */
    public SignedByte(Byte value) {
        super(value);
    }

    @Override
    public boolean isSigned() {
        return false;
    }

    @Override
    public Integer asInt() {
        return (int) this.value;
    }
}