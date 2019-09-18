package com.github.kerraway.springmvc.example;

import com.github.kerraway.springmvc.framework.starter.StartApp;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kerraway
 * @date 2019/09/14
 */
@Slf4j
public class Main {

    public static void main(String[] args) {
        logger.info("Hello, Main#main method.");
        StartApp.run(Main.class, args);
    }

}
