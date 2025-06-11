package com.example.service;

import com.example.entity.Notice;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface NoticeService {
    public void add(Notice notice);

    public void updateById(Notice notice);

    public void deleteById(Integer id);
    public void deleteBatch(List<Integer> ids);

    public Notice selectById(Integer id);

    public List<Notice> selectAll(Notice notice);

    public PageInfo<Notice> selectPage(Notice notice, Integer pageNum, Integer pageSize);
}
