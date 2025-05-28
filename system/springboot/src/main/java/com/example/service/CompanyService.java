package com.example.service;

import com.example.entity.Company;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface CompanyService {
    public void add(Company company);

    public void updateById(Company company);

    public void deleteById(Integer id);
    public void deleteBatch(List<Integer> ids);

    public Company selectById(Integer id);

    public List<Company> selectAll(Company company);

    public PageInfo<Company> selectPage(Company company, Integer pageNum, Integer pageSize);
}
