package com.learning.springboot.integration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.learning.springboot.integration.dao.mapper")
public class InterationApplication {
    public static void main(String[] args) {
        SpringApplication.run(InterationApplication.class, args);
    }
}
