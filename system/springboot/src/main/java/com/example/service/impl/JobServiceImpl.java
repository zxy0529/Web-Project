package com.example.service.impl;

import com.example.entity.Job;
import com.example.entity.Job;
import com.example.entity.Job;
import com.example.mapper.JobMapper;
import com.example.mapper.JobMapper;
import com.example.mapper.JobMapper;
import com.example.service.JobService;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 业务层方法
 */
@Service
public class JobServiceImpl implements JobService {

    @Resource
    private JobMapper jobMapper;

    public void add(Job job) {
        Integer currentUserId = Objects.requireNonNull(TokenUtils.getCurrentUser()).getId();
        job.setPublisherId(currentUserId);
        jobMapper.insert(job);
    }

    public void updateById(Job job) {
        jobMapper.updateById(job);
    }

    public void deleteById(Integer id) {
        jobMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            jobMapper.deleteById(id);
        }
    }

    public Job selectById(Integer id) {
        return jobMapper.selectById(id);
    }

    public List<Job> selectAll(Job job) {
        Integer currentPublisherId = Objects.requireNonNull(TokenUtils.getCurrentUser()).getId();
        job.setPublisherId(currentPublisherId);
        return jobMapper.selectAll(job);
    }

    public PageInfo<Job> selectPage(Job job, Integer pageNum, Integer pageSize) {
        Integer currentPublisherId = Objects.requireNonNull(TokenUtils.getCurrentUser()).getId();
        job.setPublisherId(currentPublisherId);
        PageHelper.startPage(pageNum, pageSize);
        List<Job> list = jobMapper.selectAll(job);
        return PageInfo.of(list);
    }

}
