package de.luckydonald.utils;

import java.util.logging.Logger;

/**
 * Created by luckydonald on 12.02.16.
 */
public class ObjectWithLogger {
    private Logger logger = null;
    public Logger getLogger() {
        if (this.logger == null) {
            this.logger =  Logger.getLogger(this.getClass().getCanonicalName());
        }
        return this.logger;
    }
}
