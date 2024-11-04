package com.learning.springboot.framework.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.learning.springboot.framework.exception.ClientException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.learning.springboot.framework.constants.JwtConstant.JWT_KEY;

public class JwtInterceptor implements HandlerInterceptor {

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final Map<String, Set<String>> ROLE_ACCESS_MAP = new HashMap<>() {{
        put("装备部", Set.of("/api/sloc/equipment/**"));
        put("秘书部", Set.of("/api/sloc/admin/**", "/api/sloc/bonus/**"));
        put("管理层", Set.of(
                "/api/sloc/admin/**",
                "/api/sloc/bonus/**",
                "/api/sloc/equipment/**",
                "/api/sloc/project/**"));
        put("超级管理员", Set.of("/**"));
    }};


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 放行OPTIONS请求
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true"); // 允许凭证
        return true;

//        // 从Header中获取token
//        String token = request.getHeader("Authorization");
//        if (token != null && token.startsWith("Bearer ")) {
//            token = token.substring(7);
//        }
//
//        if (token == null || token.isEmpty()) {
//            System.out.println(request.getRequestURI() + " 用户未登录");
//            // TODO 用户未登录 重定向到界面http://localhost:63342/SLOC/frontend/login.html登录
//            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "用户未登录，请先登录。");
//            return false;
//        }
//
//        try {
//            JWT.require(Algorithm.HMAC256(JWT_KEY))
//                    .withIssuer("sloc")
//                    .build()
//                    .verify(token);
//
//            // 进行细粒度权限放行校验
//            String role = JWT.decode(token).getClaim("role").asString();
//            String requestUri = request.getRequestURI();
//
//            Set<String> allowedPaths = ROLE_ACCESS_MAP.get(role);
//            if (allowedPaths != null && allowedPaths.stream().anyMatch(path -> pathMatcher.match(path, requestUri))) {
//                return true;
//            }
//
//            sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "无权限访问该资源。");
//            return false;
//        } catch (com.auth0.jwt.exceptions.TokenExpiredException e) {
//            // TODO 用户凭证过期 重定向到界面http://localhost:63342/SLOC/frontend/login.html登录
//            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "登录已过期，请重新登录。");
//            return false;
//        } catch (Exception e) {
//            // TODO 用户凭证无效 重定向到界面http://localhost:63342/SLOC/frontend/login.html登录
//            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "无效的Token，请重新登录。");
//            return false;
//        }
    }

    // 设置错误响应
    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"status\":" + statusCode + ",\"message\":\"" + message + "\"}");
        response.getWriter().flush();
    }
}
