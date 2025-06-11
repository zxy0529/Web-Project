package com.example.controller;

import com.example.common.Result;
import com.example.dto.DashboardStatsDTO;
import com.example.entity.Application;
import com.example.mapper.ApplicationMapper;
import com.example.service.ApplicationService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 前端请求接口
 */
@RestController
@RequestMapping("/application")
public class ApplicationController {

    @Resource
    private ApplicationService applicationService;

    @Resource
    private ApplicationMapper applicationMapper;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Application application) {
        applicationService.add(application);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result update(@RequestBody Application application) {
        applicationService.updateById(application);
        return Result.success();
    }

    /**
     * 单个删除
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        applicationService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {
        applicationService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 单个查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Application application = applicationService.selectById(id);
        return Result.success(application);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Application application) {
        List<Application> list = applicationService.selectAll(application);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Application application,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Application> pageInfo = applicationService.selectPage(application, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    @GetMapping("/selectMyPage")
    public Result myApplications(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam Integer userId,
            @RequestParam(required = false) String jobName
    ) {
        int offset = (pageNum - 1) * pageSize;
        List<Application> list = applicationMapper.selectMy(offset, pageSize, userId, jobName);
        int total = applicationMapper.selectMyTotal(userId, jobName);
        return Result.success(Map.of("list", list, "total", total));
    }


    @GetMapping("/selectByPublisher")
    public Result selectByPublisher(@RequestParam(defaultValue = "1") Integer pageNum,
                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Application> pageInfo = applicationService.selectPageByPublisher(pageNum, pageSize);
        return Result.success(pageInfo);
    }

    @GetMapping("/stats")
    public Result getDashboardStats() {
        DashboardStatsDTO stats = applicationService.getDashboardStats();
        return Result.success(stats);
    }


}
