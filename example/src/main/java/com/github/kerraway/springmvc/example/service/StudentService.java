package com.github.kerraway.springmvc.example.service;

import com.github.kerraway.springmvc.framework.bean.Bean;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * @author kerraway
 * @date 2019/09/18
 */
@Bean
@Slf4j
public class StudentService {

    public String countStudents(String name, int age) {
        logger.info("StudentService.countStudents, name: {}, age: {}.", name, age);
        return String.format("students count, name: %s, age: %d, count: %d.", name, age, new Random().nextInt(100));
    }

}
