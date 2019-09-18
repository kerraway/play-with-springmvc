package com.github.kerraway.springmvc.example.controller;

import com.github.kerraway.springmvc.framework.web.mvc.Controller;
import com.github.kerraway.springmvc.framework.web.mvc.RequestMapping;
import com.github.kerraway.springmvc.framework.web.mvc.RequestParam;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * @author kerraway
 * @date 2019/09/15
 */
@Slf4j
@Controller
public class StudentController {

    @RequestMapping("/students/count")
    public String countStudents(@RequestParam("name") String name, @RequestParam("age") int age) {
        logger.info("StudentController.countStudents, name: {}, age: {}.", name, age);
        return String.format("students count, name: %s, age: %d, count: %d.", name, age, new Random().nextInt(100));
    }

    @RequestMapping("/students")
    public String getStudent(@RequestParam("name") String name) {
        logger.info("StudentController.getStudent, name: {}.", name);
        return String.format("student, name: %s, age: %d.", name, 10 + new Random().nextInt(10));
    }

}
