package org.util;

import java.util.logging.Logger;
import java.util.logging.Level;

public class LogConfig {
    public static void setLogLevel(Level level) {
        Logger root = Logger.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        root.setLevel(level);
    }
}
