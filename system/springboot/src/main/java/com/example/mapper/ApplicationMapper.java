package com.example.mapper;

import com.example.entity.Application;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface ApplicationMapper {

    int insert(Application application);

    void updateById(Application application);

    void deleteById(Integer id);

    @Select("select * from `application` where id = #{id}")
    Application selectById(Integer id);

    List<Application> selectAll(Application application);

 
    @Select("select * from `application` where user_id = #{userId} and job_id = #{jobId} and status = '未审核'")
    Application selectByUserIdAndJobId(@Param("userId") Integer userId,@Param("jobId") Integer jobId);

    @Select("SELECT user_id, job_id FROM application WHERE status = '审核通过'")
    List<Map<String, Object>> getAllApplicationData();

    List<Application> selectMy(@Param("offset") int offset,
                               @Param("pageSize") int pageSize,
                               @Param("userId") Integer userId,
                               @Param("jobName") String jobName);

    int selectMyTotal(@Param("userId") Integer userId,
                      @Param("jobName") String jobName);

    List<Application> selectByPublisherId(Integer publisherId);


}
