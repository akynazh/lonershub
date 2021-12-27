package com.jzh.lonershub.config;

import com.jzh.lonershub.interceptor.EnterInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyConfig implements WebMvcConfigurer {
    private EnterInterceptor enterInterceptor;
    @Autowired
    public void setEnterInterceptor(EnterInterceptor enterInterceptor) {
        this.enterInterceptor = enterInterceptor;
    }

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(enterInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/index", "/login", "/register")
                .excludePathPatterns("/css/**", "/script/**", "/slide/**");
    }
}
