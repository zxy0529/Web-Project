package com.example.controller;

import com.example.common.Result;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Publisher;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.service.AdminService;
import com.example.service.PublisherService;
import com.example.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
public class WebController {

    @Resource
    private AdminService adminService;

    @Resource
    private UserService userService;

    @Resource
    private PublisherService publisherService;
    /**
     * 默认请求接口
     */
    @GetMapping("/")
    public Result hello () {
        return Result.success();
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        Account loginAccount = null;
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            loginAccount = adminService.login(account);
        }else if (RoleEnum.USER.name().equals(account.getRole())) {
            loginAccount = userService.login(account);
        }else if (RoleEnum.COMPANY_USER.name().equals(account.getRole())) {
            loginAccount = publisherService.login(account);
        }
        else{
            throw new CustomException("500","非法账户");
        }
        return Result.success(loginAccount);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        userService.add(user);
        return Result.success();
    }
    /*
    *
    * 公司用户注册
    * */
    @PostMapping("/registerPublisher")
    public Result register(@RequestBody Publisher publisher) {
        publisherService.add(publisher);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account) {
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            adminService.updatePassword(account);
        }else if (RoleEnum.USER.name().equals(account.getRole())) {
            userService.updatePassword(account);
        }else if (RoleEnum.COMPANY_USER.name().equals(account.getRole())) {
            publisherService.updatePassword(account);
        }
        return Result.success();
    }

}
