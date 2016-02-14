package de.luckydonald.pipboyserver.PipBoyServer.input;

import de.luckydonald.utils.ObjectWithLogger;

public abstract class SignableNumber<T>  extends ObjectWithLogger {
    final T value;

    /**
     * Constructs a newly allocated {@code <T>} object that
     * represents the specified {@code <T>} argument.
     *
     * @param value the value to be represented by the
     *              {@code <T>} object.
     */
    public SignableNumber(T value) {
        this.value = value;
    }

    abstract public boolean isSigned();

    public T getSignedValue() {
        return this.value;
    }
}

