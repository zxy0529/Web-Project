package com.example.utils;

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.common.Constants;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Publisher;
import com.example.service.AdminService;
import com.example.service.PublisherService;
import com.example.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

/**
 * Token工具类
 */
@Component
public class TokenUtils {
    private static final Logger log = LoggerFactory.getLogger(TokenUtils.class);

    @Resource
    private AdminService adminService;
    @Resource
    private UserService userService;

    @Resource
    private PublisherService publisherService;

    private static AdminService staticAdminService;
    private static UserService staticUserService;
    private static PublisherService staticPublisherService;

    @PostConstruct
    public void init() {
        staticAdminService = adminService;
        staticUserService = userService;
        staticPublisherService = publisherService;
    }
    /**
     * 生成JWT token
     */
    public static String createToken(String data, String sign) {
        return JWT.create().withAudience(data)
                .withExpiresAt(DateUtil.offsetDay(new Date(), 1))
                .sign(Algorithm.HMAC256(sign));
    }

    /**
     * 获取当前登录的用户
     */
    public static Account getCurrentUser() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader(Constants.TOKEN);
            String audience = JWT.decode(token).getAudience().get(0);
            String[] userRole = audience.split("-");
            Integer userId = Integer.valueOf(userRole[0]);
            String role = userRole[1];
            if (RoleEnum.ADMIN.name().equals(role)) {
                return staticAdminService.selectById(userId);
            }else if (RoleEnum.USER.name().equals(role)) {
                return staticUserService.selectById(userId);
            }else if (RoleEnum.COMPANY_USER.name().equals(role)) {
                return staticPublisherService.selectById(userId);
            }
        } catch (Exception e) {
            log.error("获取当前用户出错", e);
        }
        return null;
    }

}
