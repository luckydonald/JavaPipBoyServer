package de.luckydonald.pipboyserver.PipBoyServer.exceptions;

/**
 * Created by luckydonald on 12.02.16.
 */
public class KeyDoesNotExistsException extends IndexOutOfBoundsException {
    /**
     * Constructs an <code>KeyDoesNotExistsException</code> with no
     * detail message.
     */
    public KeyDoesNotExistsException() {
    }

    /**
     * Constructs an <code>KeyDoesNotExistsException</code> with the
     * specified detail message.
     *
     * @param s the detail message.
     */
    public KeyDoesNotExistsException(String s) {
        super(s);
    }
}
