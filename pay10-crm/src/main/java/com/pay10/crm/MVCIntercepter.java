package com.pay10.crm;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//Added
@Component
public class MVCIntercepter implements HandlerInterceptor {
    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       // log.info("preHandle >>>>>>>>>>>>>>>>>>>>>");
        return true;
    }
    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
       // log.info("postHandle >>>>>>>>>>>>>>>>>>>>>");
        response.addHeader("X-FRAME-OPTIONS", "DENY");
        response.setHeader("Cache-control","no-cache, no-store");
        response.setHeader("Pragma","no-cache");
        response.setHeader("Content-Security-Policy","script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline';object-src 'self'");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception exception) throws Exception {
        response.addHeader("X-FRAME-OPTIONS", "DENY");
        response.setHeader("Cache-control","no-cache, no-store");
        response.setHeader("Pragma","no-cache");
        response.setHeader("Content-Security-Policy","script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline';object-src 'self'");
        //response.setHeader("Content-Security-Policy","script-src ‘self’; object-src ‘self’");
       // log.info("Response header added.");
    }
}
