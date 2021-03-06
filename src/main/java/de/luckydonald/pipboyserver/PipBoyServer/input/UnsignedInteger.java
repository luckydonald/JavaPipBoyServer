package de.luckydonald.pipboyserver.PipBoyServer.input;


public class UnsignedInteger extends SignableInteger {
    /**
     * Constructs a newly allocated {@code Integer} object that
     * represents the specified {@code Integer} argument.
     *
     * @param value the value to be represented by the
     *              {@code Integer} object.
     */
    public UnsignedInteger(Integer value) {
        super(value);
    }

    @Override
    public boolean isSigned() {
        return false;
    }

    /**
     * @see Integer#toUnsignedLong(int)
     */
    @Override
    public Long asLong() {
        return Integer.toUnsignedLong(this.value);
    }
}