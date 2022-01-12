package org.chy.anubis.annotation;

import org.apiguardian.api.API;
import org.junit.platform.commons.annotation.Testable;

import java.lang.annotation.*;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testable
public @interface Trial {


}
