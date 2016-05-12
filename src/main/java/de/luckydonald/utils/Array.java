package de.luckydonald.utils;

import de.luckydonald.utils.ObjectWithLogger;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Created by luckydonald
 *
 * @author luckydonald
 * @since 12.05.2016
 **/
public class Array extends ObjectWithLogger {
    /**
     * Returns a string representation of the contents of the specified array.
     * The string representation consists of a list of the array's elements,
     * enclosed in square brackets (<tt>"[]"</tt>).  Adjacent elements
     * are separated by the characters <tt>", "</tt> (a comma followed
     * by a space).  Elements are converted to strings as by
     * <tt>String.valueOf(byte)</tt>.  Returns <tt>"null"</tt> if
     * <tt>a</tt> is <tt>null</tt>.
     *
     * @param a the array whose string representation to return
     * @return a string representation of <tt>a</tt>
     * @since 1.5
     * @see java.util.Arrays#toString(byte[])
     */
    public static String toString(byte[] a, Function<Object, String> formatter) {
        return toString(ArrayUtils.toObject(a), formatter);
    }
    /**
     * Returns a string representation of the contents of the specified array.
     * The string representation consists of a list of the array's elements,
     * enclosed in square brackets (<tt>"[]"</tt>).  Adjacent elements
     * are separated by the characters <tt>", "</tt> (a comma followed
     * by a space).  Elements are converted to strings as by
     * <tt>String.valueOf(byte)</tt>.  Returns <tt>"null"</tt> if
     * <tt>a</tt> is <tt>null</tt>.
     *
     * @param a the array whose string representation to return
     * @return a string representation of <tt>a</tt>
     * @since 1.5
     * @see java.util.Arrays#toString(byte[])
     */
    public static String toString(Object[] a, Function<Object, String> formatter) {
        if (a == null) {
            return "null";
        }
        int iMax = a.length - 1;
        if (iMax == -1) {
            return "[]";
        }
        if (formatter == null) {
            formatter = Object::toString;
        }
        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(formatter.apply(a[i]));
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }
}