package de.luckydonald.utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by luckydonald on 12.02.16.
 */
public class ObjectWithLogger {
    static
    private Logger logger = null;
    public Logger getLogger() {
        if (logger == null) {
            logger =  Logger.getLogger(this.getClass().getCanonicalName());
        }
        logger.setLevel(Level.FINE);
        return logger;
    }
    private static Logger staticLogger;
    public static Logger getStaticLogger() {
        if (staticLogger == null) {
            staticLogger = Logger.getLogger(new Throwable().getStackTrace()[1].getClassName());
        }
        return staticLogger;
    }

    public void addLogConsoleHandler() {
        addLogConsoleHandler(null);
    }
    public void addLogConsoleHandler(Level l) {
        if (l == null) {
            l = Level.ALL;
        }
        // Create and set handler
        Handler systemOut = new ConsoleHandler();
        systemOut.setLevel( l );
        getLogger().addHandler( systemOut );
        getLogger().setLevel( l );

        // Prevent logs from processed by default Console handler.
        getLogger().setUseParentHandlers( false ); // Solution 1
    }
}
