package com.sblogjava.config;

import com.sblogjava.interceptor.TokenInterceptor;
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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/api/admin/**")  // 拦截所有管理员接口
                .excludePathPatterns(
                        "/api/admin/users/login",     // 登录接口
                        "/api/admin/users/register",  // 注册接口
                        "/api/admin/users/check-username",  // 检查用户名接口
                        "/api/admin/init/**"          // 初始化接口
                );
    }
}
