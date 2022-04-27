package org.chy.anubis.log;

import org.chy.anubis.exception.InterruptException;
import org.chy.anubis.treasury.log.ILogger;

public class LoggerImp implements ILogger {


    @Override
    public void info(String msg) {
        System.out.println(font(msg, 32));

    }

    @Override
    public void warning(String msg) {
        System.out.println(font(msg, 33));
    }

    @Override
    public void error(String msg, boolean stop) {
        System.out.println(font(msg, 31));
        if (stop) {
            throw new InterruptException();
        }
    }

    /**
     * 有样式的字体
     *
     * @param content
     * @param color  31:红色 32:绿色 33:黄色
     * @return
     */
    private String font(String content, int color) {
        StringBuilder result = new StringBuilder("\033[");
        result.append(color).append(";2m").append(content).append("\033[0m");
        return result.toString();
    }


}
