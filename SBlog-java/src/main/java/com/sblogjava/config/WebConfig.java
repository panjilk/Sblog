package com.sblogjava.config;

import com.sblogjava.interceptor.RateLimitInterceptor;
import com.sblogjava.interceptor.TokenInterceptor;
import com.sblogjava.interceptor.VisitLogInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TokenInterceptor tokenInterceptor;

    @Autowired
    private VisitLogInterceptor visitLogInterceptor;

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 限流拦截器 - 对所有API请求进行限流
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/**")  // 对所有API请求限流
                .excludePathPatterns(
                        "/api/admin/users/login",     // 登录接口
                        "/api/admin/users/register",  // 注册接口
                        "/api/admin/visit-log/record" // 访问日志记录接口（已在前端限流）
                )
                .order(1);  // 最高优先级

        // 访问日志拦截器 - 记录前台页面访问
        registry.addInterceptor(visitLogInterceptor)
                .addPathPatterns("/**")  // 匹配所有请求
                .excludePathPatterns(
                        "/api/**",           // 排除所有API请求
                        "/admin/**",         // 排除管理后台
                        "/static/**",
                        "/assets/**",        // Vite构建后的资源目录
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/favicon.ico",
                        "/error"
                )
                .order(2);

        // Token验证拦截器
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/api/admin/**")  // 拦截所有管理员接口
                .excludePathPatterns(
                        "/api/admin/users/login",     // 登录接口
                        "/api/admin/users/register",  // 注册接口
                        "/api/admin/users/check-username",  // 检查用户名接口
                        "/api/admin/init/**",         // 初始化接口
                        "/api/admin/visit-log/record" // 访问日志记录接口（允许未登录用户访问）
                )
                .order(3);
    }
}
