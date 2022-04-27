package org.chy.anubis.log;

import org.chy.anubis.treasury.log.ILogger;

public class Logger {

    static ILogger logger = new LoggerImp();

    public static void info(String msg) {
        logger.info(msg);
    }

    public static void waring(String msg) {
        logger.warning(msg);
    }

    public static void error(String msg) {
        logger.error(msg, false);
    }
}
