package com.example.service;

import com.example.entity.Job;
import com.example.entity.Job;
import com.example.entity.Job;
import com.example.mapper.JobMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;

import java.util.List;

public interface JobService {

    public void add(Job job);

    public void updateById(Job job);

    public void deleteById(Integer id);
    public void deleteBatch(List<Integer> ids);

    public Job selectById(Integer id);

    public List<Job> selectAll(Job job);

    public PageInfo<Job> selectPage(Job job, Integer pageNum, Integer pageSize);
}
