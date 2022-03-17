package org.chy.anubis.dynamic.execute;

public interface Executer {

    public void run();

    public void setInterceptor(ExecuteInterceptor interceptor);

}
