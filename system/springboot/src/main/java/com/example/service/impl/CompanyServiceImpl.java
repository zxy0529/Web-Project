package com.example.service.impl;

import com.example.entity.Company;
import com.example.mapper.CompanyMapper;
import com.example.service.CompanyService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 业务层方法
 */
@Service
public class CompanyServiceImpl implements CompanyService {

    @Resource
    private CompanyMapper companyMapper;

    public void add(Company company) {
        companyMapper.insert(company);
    }

    public void updateById(Company company) {
        companyMapper.updateById(company);
    }

    public void deleteById(Integer id) {
        companyMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            companyMapper.deleteById(id);
        }
    }

    public Company selectById(Integer id) {
        return companyMapper.selectById(id);
    }

    public List<Company> selectAll(Company company) {
        return companyMapper.selectAll(company);
    }

    public PageInfo<Company> selectPage(Company company, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Company> list = companyMapper.selectAll(company);
        return PageInfo.of(list);
    }

}
