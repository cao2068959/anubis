package org.chy.anubis.treasury.log;

/**
 * 日志接口, 在远程仓库也有一份
 */
public interface ILogger {

    void info(String msg);

    void warning(String msg);

    void error(String msg, boolean stop);
}
