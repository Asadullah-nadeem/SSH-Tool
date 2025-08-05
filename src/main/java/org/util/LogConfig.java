package org.util;

import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.util.logging.Logger;


public class LogConfig {
    public static void setLogLevel(Level level) {
        Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        root.setLevel(level);
    }
}