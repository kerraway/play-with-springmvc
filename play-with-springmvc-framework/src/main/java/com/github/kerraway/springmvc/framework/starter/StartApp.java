package com.github.kerraway.springmvc.framework.starter;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @author kerraway
 * @date 2019/09/14
 */
@Slf4j
public class StartApp {

    public static void run(Class<?> clazz, String[] args) {
        logger.info("Hello, StartApp#run method, clazz: {}, args: {}.", clazz.getName(), Arrays.toString(args));
    }

}
