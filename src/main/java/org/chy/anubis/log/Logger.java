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

    /**
     * 提示信息
     * @param msg
     */
    public static void tip(String msg) {
        System.out.println(font(msg, 34));
    }

    public static void error(String msg) {
        logger.error(msg, false);
    }

    /**
     * 有样式的字体
     *
     * @param content
     * @param color   31:红色 32:绿色 33:黄色 34: 蓝色
     * @return
     */
    private static String font(String content, int color) {
        StringBuilder result = new StringBuilder("\033[");
        result.append(color).append(";2m").append(content).append("\033[0m");
        return result.toString();
    }
}
