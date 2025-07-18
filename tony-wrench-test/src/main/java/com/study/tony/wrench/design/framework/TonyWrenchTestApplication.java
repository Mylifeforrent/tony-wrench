package com.study.tony.wrench.design.framework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.study.tony.wrench"})
public class TonyWrenchTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TonyWrenchTestApplication.class, args);
    }

}
