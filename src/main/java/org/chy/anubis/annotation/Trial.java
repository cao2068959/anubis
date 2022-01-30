package org.chy.anubis.annotation;

import org.apiguardian.api.API;
import org.chy.anubis.enums.CaseSourceType;
import org.junit.platform.commons.annotation.Testable;

import java.lang.annotation.*;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testable
public @interface Trial {

    /**
     * 这个算法的名称
     * @return
     */
    String value();

    /**
     * 算法来源
     * @return
     */
    CaseSourceType caseSourceType() default CaseSourceType.LEETCODE;

    /**
     * 每次执行测试用例的数量
     * @return
     */
    int limit() default 15;

    /**
     * 从第几个算法开始 执行
     * @return
     */
    int startIndex() default 0;

    /**
     * 要排除的用例
     * @return
     */
    String[] excludeCaseName() default {};

    /**
     * 指定要运行的用例的名称, 如果这里指定了那么  limit以及startIndex 将会失效
     * @return
     */
    String[] runCaseName() default {};



}
