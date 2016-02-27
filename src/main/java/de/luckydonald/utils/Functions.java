package de.luckydonald.utils;

import de.luckydonald.utils.ObjectWithLogger;

/**
 * Created by  on
 *
 * @author luckydonald
 * @since 18.02.2016
 **/
public class Functions extends ObjectWithLogger {
    public static String getMethodName () {
        return getMethodName(0);
    }
    public static String getMethodName (int offset) {
        StackTraceElement ste[] = Thread.currentThread().getStackTrace();
        String thisClass = "";
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