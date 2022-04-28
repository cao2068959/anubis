package org.chy.anubis.dynamic.template;

/**
 * 动态执行的拦截器
 */
public interface ExecuteInterceptor<T> {

    /**
     * 执行前
     */
    T before();

    /**
     * 执行后
     */
    public void after(T t);
}
