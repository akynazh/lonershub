package com.jzh.lonershub.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @version 1.0
 * @description
 * @Author Jiang Zhihang
 * @Date 2022/1/21 23:30
 */
@Component
public class VisitCountInterceptor implements HandlerInterceptor {
    final StringRedisTemplate redisTemplate;

    @Autowired
    public VisitCountInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        redisTemplate.opsForValue().increment("visit");
        return true;
    }
}
