package com.example.mapper;

import com.example.entity.Company;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CompanyMapper {

    int insert(Company company);

    void updateById(Company company);

    void deleteById(Integer id);

    @Select("select * from `company` where id = #{id}")
    Company selectById(Integer id);

    List<Company> selectAll(Company company);
}
