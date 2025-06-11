package com.example.mapper;

import com.example.dto.DashboardStatsDTO;
import com.example.entity.Application;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
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

    // 按职位名称分组统计投递数，联查 job 表，过滤发布者
    @Select("""
    SELECT j.name AS name, COUNT(*) AS count 
    FROM application a 
    JOIN job j ON a.job_id = j.id 
    WHERE j.publisher_id = #{publisherId}
    GROUP BY j.name
""")
    List<DashboardStatsDTO.JobCount> countByJobName(@Param("publisherId") Integer publisherId);

    // 统计实习岗位投递数 (job.type=1 表示实习)，过滤发布者
    @Select("""
    SELECT COUNT(*) 
    FROM application a 
    JOIN job j ON a.job_id = j.id 
    WHERE j.type = 1 AND j.publisher_id = #{publisherId}
""")
    Integer countIntern(@Param("publisherId") Integer publisherId);

    // 统计全职岗位投递数 (job.type=2)，过滤发布者
    @Select("""
    SELECT COUNT(*) 
    FROM application a 
    JOIN job j ON a.job_id = j.id 
    WHERE j.type = 2 AND j.publisher_id = #{publisherId}
""")
    Integer countFulltime(@Param("publisherId") Integer publisherId);


    List<Application> selectRecent(@Param("publisherId") Integer publisherId);

    // 统计总投递数，过滤发布者
    @Select("SELECT COUNT(*) FROM application a JOIN job j ON a.job_id = j.id WHERE j.publisher_id = #{publisherId}")
    Integer countAllApplications(@Param("publisherId") Integer publisherId);


}
