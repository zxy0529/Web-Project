package com.example.service.impl;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Application;

import com.example.exception.CustomException;
import com.example.mapper.ApplicationMapper;
import com.example.mapper.JobMapper;
import com.example.service.ApplicationService;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ApplicationServiceImpl implements ApplicationService {
    @Resource
    private ApplicationMapper applicationMapper;

    @Resource
    private JobMapper jobMapper;


    public void add(Application application) {
        Account currentUser = TokenUtils.getCurrentUser();
        Application reserve = applicationMapper.selectByUserIdAndJobId(currentUser.getId(),application.getJobId());
        if(reserve != null) {
            //已经投递过
            throw new CustomException("500","已经投递过，等待审批");
        }
        application.setUserId(currentUser.getId());
        applicationMapper.insert(application);
    }

    /*审批通过后将状态设置为审核*/
    public void updateById(Application application) {
        Account currentUser = TokenUtils.getCurrentUser();

        // 只有管理员可以设置为“审核通过”
        if (currentUser.getRole().equals(RoleEnum.ADMIN.name())) {
            application.setStatus("审核通过");
        }

        // 无论是否通过审核，管理员都可以记录审批时间
        application.setAuditTime(DateUtil.now());

        // 更新 application 表
        applicationMapper.updateById(application);
    }


    public void deleteById(Integer id) {
        applicationMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            applicationMapper.deleteById(id);
        }
    }

    public Application selectById(Integer id) {
        return applicationMapper.selectById(id);
    }

    public List<Application> selectAll(Application application) {
        return applicationMapper.selectAll(application);
    }

    public PageInfo<Application> selectPage(Application application, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Application> list = applicationMapper.selectAll(application);
        return PageInfo.of(list);
    }


    public List<Application> selectByPublisher() {
        Integer currentPublisherId = Objects.requireNonNull(TokenUtils.getCurrentUser()).getId();
        return applicationMapper.selectByPublisherId(currentPublisherId);
    }

    public PageInfo<Application> selectPageByPublisher(Integer pageNum, Integer pageSize) {
        Integer currentPublisherId = Objects.requireNonNull(TokenUtils.getCurrentUser()).getId();
        PageHelper.startPage(pageNum, pageSize);
        List<Application> list = applicationMapper.selectByPublisherId(currentPublisherId);
        return PageInfo.of(list);
    }


}
