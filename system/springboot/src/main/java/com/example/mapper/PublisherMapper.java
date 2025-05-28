package com.example.mapper;

import com.example.entity.Publisher;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PublisherMapper {

    int insert(Publisher publisher);

    void updateById(Publisher publisher);

    void deleteById(Integer id);

    @Select("select * from `publisher` where id = #{id}")
    Publisher selectById(Integer id);

    @Select("select * from `publisher` where username = #{username}")
    Publisher selectByUsername(String username);

    List<Publisher> selectAll(Publisher publisher);

}
