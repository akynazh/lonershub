package com.jzh.lonershub.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jzh.lonershub.bean.Global;
import com.jzh.lonershub.service.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
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
@EnableScheduling
public class VisitCountInterceptor implements HandlerInterceptor {
    private StringRedisTemplate redisTemplate;
    private GlobalService globalService;
    // 用于页面展示的visitCount值
    public static Long visitCount;

    @Autowired
    public void setGlobalService(GlobalService globalService) {
        this.globalService = globalService;
    }
    @Autowired
    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 每10分钟将redis中的visitCount持久化到数据库一次同时更新静态变量visitCount
    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void setVisitCount() {
        String vc = null;
        if (Boolean.TRUE.equals(redisTemplate.hasKey("visitCount"))) {
            vc = redisTemplate.opsForValue().get("visitCount");
        }
        if (vc != null) {
            UpdateWrapper<Global> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", 1);
            updateWrapper.set("visitCount", Long.valueOf(vc));
            globalService.update(updateWrapper);
            visitCount = Long.valueOf(vc);
        } else { // 如果redis中没有visitCount字段或者该字段已过期
            QueryWrapper<Global> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", 1);
            Global global = globalService.getOne(queryWrapper);
            visitCount = global.getVisitCount();
            if (Boolean.FALSE.equals(redisTemplate.hasKey("visitCount"))) {
                redisTemplate.opsForValue().set("visitCount", String.valueOf(visitCount));
            }
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        redisTemplate.opsForValue().increment("visitCount");
        return true;
    }
}
