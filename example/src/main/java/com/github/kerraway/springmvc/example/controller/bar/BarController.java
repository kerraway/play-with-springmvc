package com.github.kerraway.springmvc.example.controller.bar;

import com.github.kerraway.springmvc.framework.web.mvc.Controller;
import com.github.kerraway.springmvc.framework.web.mvc.RequestMapping;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kerraway
 * @date 2019/09/18
 */
@Slf4j
@Controller
public class BarController {

    @RequestMapping("/bar")
    public String bar() {
        logger.info("BarController.bar");
        return "bar";
    }

}
