package de.luckydonald.pipboyserver.PipBoyServer.input;

import de.luckydonald.utils.ObjectWithLogger;

public class UnsignedLong extends SignableNumber<Long> {
    /**
     * Constructs a newly allocated {@code Long} object that
     * represents the specified {@code Long} argument.
     *
     * @param value the value to be represented by the
     *              {@code Long} object.
     */
    public UnsignedLong(Long value) {
        super(value);
    }
    @Override
    public String toString() {
        return Long.toUnsignedString(this.value);
    }

    @Override
    public boolean isSigned() {
        return false;
    }
}
