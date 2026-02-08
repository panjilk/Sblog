package com.sblogjava.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sblogjava.common.Result;
import com.sblogjava.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Token 验证拦截器
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 处理跨域预检请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 获取 Authorization header
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            sendErrorResponse(response, 401, "未登录或 token 无效");
            return false;
        }

        // 提取 token
        String token = authorization.replace("Bearer ", "");

        // 验证 token
        if (!jwtUtil.validateToken(token)) {
            sendErrorResponse(response, 401, "token 已过期或无效");
            return false;
        }

        // token 有效，放行
        return true;
    }

    private void sendErrorResponse(HttpServletResponse response, int code, String message) throws Exception {
        response.setStatus(code);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.error(code, message);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(result));
    }
}
