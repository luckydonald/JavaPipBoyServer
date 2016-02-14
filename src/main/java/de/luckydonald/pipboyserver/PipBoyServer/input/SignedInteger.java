package de.luckydonald.pipboyserver.PipBoyServer.input;

import de.luckydonald.utils.ObjectWithLogger;

public class SignedInteger extends SignableInteger {
    /**
     * Constructs a newly allocated {@code Integer} object that
     * represents the specified {@code Integer} argument.
     *
     * @param value the value to be represented by the
     *              {@code Integer} object.
     **/
    public SignedInteger(Integer value) {
        super(value);
    }

    @Override
    public boolean isSigned() {
        return true;
    }

    @Override
    public Long asLong() {
        return new Long(this.value);
    }
}