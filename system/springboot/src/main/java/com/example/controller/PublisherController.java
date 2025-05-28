package com.example.controller;

import com.example.common.Result;
import com.example.entity.Publisher;
import com.example.service.PublisherService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前端请求接口
 */
@RestController
@RequestMapping("/publisher")
public class PublisherController {

    @Resource
    private PublisherService publisherService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Publisher publisher) {
        publisherService.add(publisher);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result update(@RequestBody Publisher publisher) {
        publisherService.updateById(publisher);
        return Result.success();
    }

    /**
     * 单个删除
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        publisherService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {
        publisherService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 单个查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Publisher publisher = publisherService.selectById(id);
        return Result.success(publisher);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Publisher publisher) {
        List<Publisher> list = publisherService.selectAll(publisher);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Publisher publisher,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Publisher> pageInfo = publisherService.selectPage(publisher, pageNum, pageSize);
        return Result.success(pageInfo);
    }

}
