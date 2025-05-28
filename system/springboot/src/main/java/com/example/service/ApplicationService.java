package com.example.service;
import com.example.entity.Application;

import com.github.pagehelper.PageInfo;

import org.springframework.stereotype.Service;


import java.util.List;

/**
 * 业务层方法
 */
@Service
public interface ApplicationService {


    public void add(Application application);

    public void updateById(Application application);
    public void deleteById(Integer id);

    public void deleteBatch(List<Integer> ids);

    public Application selectById(Integer id);

    public List<Application> selectAll(Application application);

    public PageInfo<Application> selectPage(Application application, Integer pageNum, Integer pageSize);

    public List<Application> selectByPublisher();
    public PageInfo<Application> selectPageByPublisher(Integer pageNum, Integer pageSize);

}
