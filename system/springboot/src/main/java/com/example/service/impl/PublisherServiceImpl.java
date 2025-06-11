package com.example.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Publisher;
import com.example.exception.CustomException;
import com.example.mapper.PublisherMapper;
import com.example.service.PublisherService;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublisherServiceImpl implements PublisherService {

    @Resource
    private PublisherMapper publisherMapper;

    public void add(Publisher publisher) {
        Publisher dbPublisher = publisherMapper.selectByUsername(publisher.getUsername());
        /*异常判断*/
        if (ObjectUtil.isNotNull(dbPublisher)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        if (ObjectUtil.isEmpty(publisher.getPassword())) {
            publisher.setPassword(Constants.USER_DEFAULT_PASSWORD);
        }
        if (ObjectUtil.isEmpty(publisher.getName())) {
            publisher.setName(publisher.getUsername());
        }
        publisher.setRole(RoleEnum.COMPANY_USER.name());
        publisherMapper.insert(publisher);
    }

    public void updateById(Publisher publisher) {
        publisherMapper.updateById(publisher);
    }

    public void deleteById(Integer id) {
        publisherMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            publisherMapper.deleteById(id);
        }
    }

    public Publisher selectById(Integer id) {
        return publisherMapper.selectById(id);
    }

    public List<Publisher> selectAll(Publisher publisher) {
        return publisherMapper.selectAll(publisher);
    }

    public PageInfo<Publisher> selectPage(Publisher publisher, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Publisher> list = publisherMapper.selectAll(publisher);
        return PageInfo.of(list);
    }

    /**
     * 登录
     */
    public Publisher login(Account account) {
        Publisher dbPublisher = publisherMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbPublisher)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!dbPublisher.getPassword().equals(account.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 生成token
        String token = TokenUtils.createToken(dbPublisher.getId() + "-" + dbPublisher.getRole(), dbPublisher.getPassword());
        dbPublisher.setToken(token);
        return dbPublisher;
    }

    /**
     * 修改密码
     */
    public void updatePassword(Account account) {
        Publisher dbPublisher = publisherMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbPublisher)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbPublisher.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        dbPublisher.setPassword(account.getNewPassword());
        publisherMapper.updateById(dbPublisher);
    }

}