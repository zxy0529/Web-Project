package com.example.mapper;

import com.example.entity.Job;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface JobMapper {
    int insert(Job job);

    void updateById(Job job);

    void deleteById(Integer id);

    @Select("select * from `job` where id = #{id}")
    Job selectById(Integer id);

    List<Job> selectAll(Job job);

    @Select("""
        SELECT r.* 
        FROM job r 
        JOIN (
            SELECT job_id, COUNT(*) AS cnt 
            FROM application
            GROUP BY job_id 
            ORDER BY cnt DESC 
            LIMIT #{limit}
        ) t ON r.id = t.job_id
        """)
    List<Job> selectTopNMostReservedJob(@Param("limit") int limit);

    @Select("<script>" +
            "SELECT * FROM rooms WHERE id IN " +
            "<foreach item='id' collection='list' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<Job> selectByIds(List<Integer> ids);
}
