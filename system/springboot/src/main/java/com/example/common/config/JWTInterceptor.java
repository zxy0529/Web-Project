package com.example.common.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.jwt.JWTUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.exception.CustomException;
import com.example.service.AdminService;
import com.example.service.PublisherService;
import com.example.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT拦截器
 */
@Component
public class JWTInterceptor implements HandlerInterceptor {

    @Resource
    private AdminService adminService;
    @Resource
    private UserService userService;

    @Resource
    private PublisherService publisherService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 获取请求头中的token
        String token = request.getHeader(Constants.TOKEN);
        // 如果请求头中没有token，则从请求参数中获取
        if (ObjectUtil.isNull(token)) {

            request.getParameter(Constants.TOKEN);
        }

        // 如果请求头和请求参数中都没有token，则抛出异常
        if (ObjectUtil.isNull(token)) {
            throw new CustomException(ResultCodeEnum.TOKEN_INVALID_ERROR);
        }
        Account account = null;
        try {
            // 解析token，获取用户id和角色
            String audience = JWT.decode(token).getAudience().get(0);
            String userId = audience.split("-")[0];
            String role = audience.split("-")[1];

            // 根据角色获取用户信息
            if (RoleEnum.ADMIN.name().equals(role)) {
                account = adminService.selectById(Integer.valueOf(userId));
            }else if (RoleEnum.USER.name().equals(role)) {
                account = userService.selectById(Integer.valueOf(userId));
            }else if (RoleEnum.COMPANY_USER.name().equals(role)) {
                account = publisherService.selectById(Integer.valueOf(userId));
            }
        } catch (Exception e) {
            // 解析token失败，抛出异常
            throw new CustomException(ResultCodeEnum.TOKEN_CHECK_ERROR);
        }

        // 如果没有获取到用户信息，抛出异常
        if (ObjectUtil.isNull(account)) {

            throw new CustomException(ResultCodeEnum.TOKEN_CHECK_ERROR);
        }
        try {

            // 验证token
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(account.getPassword())).build();
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            // 验证token失败，抛出异常
            throw new CustomException(ResultCodeEnum.TOKEN_CHECK_ERROR);
        }
        return true;
    }

}
