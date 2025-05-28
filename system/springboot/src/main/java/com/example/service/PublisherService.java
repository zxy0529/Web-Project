package com.example.service;

import com.example.entity.Account;
import com.example.entity.Publisher;
import com.github.pagehelper.PageInfo;


import java.util.List;

/**
 * 业务层方法
 */

public interface PublisherService {



    public void add(Publisher publisher);

    public void updateById(Publisher publisher);
    public void deleteById(Integer id);

    public void deleteBatch(List<Integer> ids);

    public Publisher selectById(Integer id);

    public List<Publisher> selectAll(Publisher publisher);

    public PageInfo<Publisher> selectPage(Publisher publisher, Integer pageNum, Integer pageSize);

    /**
     * 登录
     */
    public Publisher login(Account account);

    /**
     * 修改密码
     */
    public void updatePassword(Account account);
}
