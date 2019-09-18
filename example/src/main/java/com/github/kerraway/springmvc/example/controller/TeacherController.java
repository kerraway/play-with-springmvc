package com.github.kerraway.springmvc.example.controller;

import com.github.kerraway.springmvc.framework.web.mvc.Controller;
import com.github.kerraway.springmvc.framework.web.mvc.RequestMapping;
import com.github.kerraway.springmvc.framework.web.mvc.RequestParam;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * @author kerraway
 * @date 2019/09/18
 */
@Slf4j
@Controller
public class TeacherController {

    @RequestMapping("/teachers/count")
    public String countTeachers(@RequestParam("name") String name, @RequestParam("age") int age) {
        logger.info("TeacherController.countTeachers, name: {}, age: {}.", name, age);
        return String.format("teachers count, name: %s, age: %d, count: %d.", name, age, new Random().nextInt(100));
    }

}
