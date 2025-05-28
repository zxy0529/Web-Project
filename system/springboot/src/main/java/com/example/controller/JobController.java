package com.example.controller;

import com.example.common.Result;
import com.example.entity.Job;
import com.example.entity.Job;
import com.example.service.JobService;
import com.example.service.JobService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {

    @Resource
    private JobService jobService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Job job) {
        jobService.add(job);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result update(@RequestBody Job job) {
        jobService.updateById(job);
        return Result.success();
    }

    /**
     * 单个删除
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        jobService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {
        jobService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 单个查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Job job = jobService.selectById(id);
        return Result.success(job);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Job job) {
        List<Job> list = jobService.selectAll(job);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Job job,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Job> pageInfo = jobService.selectPage(job, pageNum, pageSize);
        return Result.success(pageInfo);
    }

}
