package com.example.controller;

import com.example.common.Result;
import com.example.entity.Company;
import com.example.service.CompanyService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/company")
public class CompanyController {

        @Resource
        private CompanyService companyService;

        /**
         * 新增
         */
        @PostMapping("/add")
        public Result add(@RequestBody Company company) {
            companyService.add(company);
            return Result.success();
        }

        /**
         * 修改
         */
        @PutMapping("/update")
        public Result update(@RequestBody Company company) {
            companyService.updateById(company);
            return Result.success();
        }

        /**
         * 单个删除
         */
        @DeleteMapping("/delete/{id}")
        public Result delete(@PathVariable Integer id) {
            companyService.deleteById(id);
            return Result.success();
        }

        /**
         * 批量删除
         */
        @DeleteMapping("/delete/batch")
        public Result delete(@RequestBody List<Integer> ids) {
            companyService.deleteBatch(ids);
            return Result.success();
        }

        /**
         * 单个查询
         */
        @GetMapping("/selectById/{id}")
        public Result selectById(@PathVariable Integer id) {
            Company company = companyService.selectById(id);
            return Result.success(company);
        }

        /**
         * 查询所有
         */
        @GetMapping("/selectAll")
        public Result selectAll(Company company) {
            List<Company> list = companyService.selectAll(company);
            return Result.success(list);
        }

        /**
         * 分页查询
         */
        @GetMapping("/selectPage")
        public Result selectPage(Company company,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
            PageInfo<Company> pageInfo = companyService.selectPage(company, pageNum, pageSize);
            return Result.success(pageInfo);
        }
}
