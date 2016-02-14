package de.luckydonald.pipboyserver.PipBoyServer.input;

import de.luckydonald.utils.ObjectWithLogger;

public class SignedLong extends SignableNumber<Long> {
    /**
     * Constructs a newly allocated {@code Long} object that
     * represents the specified {@code Long} argument.
     *
     * @param value the value to be represented by the
     *              {@code Long} object.
     **/
    public SignedLong(Long value) {
        super(value);
    }

    @Override
    public boolean isSigned() {
        return true;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}