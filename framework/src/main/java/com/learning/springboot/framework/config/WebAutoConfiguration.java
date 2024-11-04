package com.learning.springboot.framework.config;

import com.learning.springboot.framework.handle.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;

public class WebAutoConfiguration {
    /**
     * 构建全局异常拦截器组件 Bean
     */
    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}
