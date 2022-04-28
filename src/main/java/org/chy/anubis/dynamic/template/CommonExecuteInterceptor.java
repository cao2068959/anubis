package org.chy.anubis.dynamic.template;

import org.chy.anubis.log.Logger;

/**
 * 通用的执行拦截器, 用来打印执行时间或其他信息
 */
public class CommonExecuteInterceptor implements ExecuteInterceptor<BaseInterceptorInfo> {


    @Override
    public BaseInterceptorInfo before() {
        Logger.tip("==========> 测试用例执行开始");
        long startMills = System.currentTimeMillis();
        long startNanos = System.nanoTime();
        BaseInterceptorInfo result = new BaseInterceptorInfo();
        result.setStartTime(startMills);
        result.setStartNanos(startNanos);
        return result;
    }

    @Override
    public void after(BaseInterceptorInfo baseInterceptorInfo) {
        long startTime = baseInterceptorInfo.getStartTime();
        long diff = System.currentTimeMillis() - startTime;
        //纳秒为0, 那么使用纳秒计数
        if (diff == 0) {
            diff = System.nanoTime() - baseInterceptorInfo.getStartNanos();
            Logger.tip("==========> 测试用例执行结束,花费时间[ " + diff + "纳秒 ]");
            return;
        }
        Logger.tip("==========> 测试用例执行结束,花费时间[ " + diff + "毫秒 ]");
    }
}
