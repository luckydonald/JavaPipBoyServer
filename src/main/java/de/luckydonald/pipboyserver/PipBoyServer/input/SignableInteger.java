package de.luckydonald.pipboyserver.PipBoyServer.input;

import de.luckydonald.utils.ObjectWithLogger;

/**
 * Created by  on
 *
 * @author luckydonald
 * @since 13.02.2016
 **/
abstract public class SignableInteger extends SignableNumber<Integer> {

    /**
     * Constructs a newly allocated {@code Integer} object that
     * represents the specified {@code Integer} argument.
     *
     * @param value the value to be represented by the
     *              {@code Integer} object.
     */
    public SignableInteger(Integer value) {
        super(value);
    }

    @Override
    public String toString() {
        return asLong().toString();
    }

    abstract public Long asLong();
}