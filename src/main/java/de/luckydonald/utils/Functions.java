package de.luckydonald.utils;

import de.luckydonald.utils.ObjectWithLogger;

/**
 * Created by  on
 *
 * @author luckydonald
 * @since 18.02.2016
 **/
public class Functions extends ObjectWithLogger {
    /**
     * Forwards the call to the {@link #getMethodName(int)} method to get the caller name without specifying {@code offset = 0}<br>
     * {@code
     *   void foo() {
     *      getMethodName() // == "foo"
     *      }
     * }
     *
     * @implNote This calls {@code getMethodName(1)}, because {@code 0} would be <b>this</b> method.
     *           If you call {@link #getMethodName(int) it} directly, use {@code 0} as parameter.
     * @return the name.
     */
    public static String getMethodName () {
        return getMethodName(1); // because 0 is this function.
        // if you're calling getMethodName(int offset) you can use 0 instead.
    }

    /**
     * get the name of the method. {@code 0} is the calling method (<i>"you"</i>),
     * {@code 1} is the method which called <i>you</i>.<br>
     * {@code
     *   void foo() {
     *      getMethodName(0) // == "foo"
     *      }
     * }
     * @param offset how far in the stack we should go.
     * @return Name of the method.
     */
    public static String getMethodName (int offset) {
        StackTraceElement ste[] = Thread.currentThread().getStackTrace();
        int foundThisMethod = -1;
        for ( StackTraceElement s : ste ) {

            if ( foundThisMethod == (2+offset) ) {
                // 0: getStackTrace
                // 1: getMethodName
                // 2: <caller>
                // 2+offset: wanted result
                return s.getMethodName();
            } else if (foundThisMethod != -1 ) {
                foundThisMethod++;
            }
            foundThisMethod = (s.getMethodName().equals( "getStackTrace" ) ? 1 : foundThisMethod);
        }
        return "";
    }

}