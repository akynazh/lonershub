package com.jzh.lonershub.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class EnterInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Object successLoner = session.getAttribute("successLoner");
        if (successLoner != null) {
            return true;
        }
        response.addCookie(new Cookie("errorMsg", "请先进行登录"));
        request.getRequestDispatcher("/").forward(request, response);
        return false;
    }
}
