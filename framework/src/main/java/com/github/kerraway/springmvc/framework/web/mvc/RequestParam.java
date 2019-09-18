package com.github.kerraway.springmvc.framework.web.mvc;

import java.lang.annotation.*;

/**
 * @author kerraway
 * @date 2019/09/15
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestParam {

    String value();

}
