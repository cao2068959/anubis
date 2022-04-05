package org.chy.anubis.dynamic.template;

public interface TestCaseExecuter {

    public void run();

    public void setInterceptor(ExecuteInterceptor interceptor);

    public void setTestCase(Object testCase);
}
