package com.example.service.impl;

import com.example.entity.Job;
import com.example.mapper.ApplicationMapper;
import com.example.mapper.JobMapper;
import com.example.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

// RecommendationServiceImpl.java
@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private JobMapper jobMapper;

    @Override
    public List<Job> recommendJobsForUser(Integer userId) {
        // 1. 获取所有预约记录
        List<Map<String, Object>> data = applicationMapper.getAllApplicationData();

        // 2. 构建用户-房间使用记录
        Map<Integer, Set<Integer>> userJobMap = new HashMap<>();
        for (Map<String, Object> record : data) {
            Integer uid = (Integer) record.get("user_id");
            Integer rid = (Integer) record.get("job_id");

            userJobMap.computeIfAbsent(uid, k -> new HashSet<>()).add(rid);
        }

        Set<Integer> targetUserJob = userJobMap.getOrDefault(userId, new HashSet<>());

        //如果用户没有预约记录，执行冷启动策略
        if(targetUserJob.isEmpty()){
            return jobMapper.selectTopNMostReservedJob(3);
        }

        // 3. 计算相似度
        Map<Integer, Double> similarityMap = new HashMap<>();
        for (Map.Entry<Integer, Set<Integer>> entry : userJobMap.entrySet()) {
            Integer otherUserId = entry.getKey();
            if (otherUserId.equals(userId)) continue;

            Set<Integer> otherJob = entry.getValue();
            Set<Integer> intersection = new HashSet<>(targetUserJob);
            intersection.retainAll(otherJob);

            Set<Integer> union = new HashSet<>(targetUserJob);
            union.addAll(otherJob);

            double sim = union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
            similarityMap.put(otherUserId, sim);
        }

        // 4. 获取最相似的前3位用户
        List<Map.Entry<Integer, Double>> topUsers = similarityMap.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(3)
                .toList();

        // 5. 收集推荐房间及权重（相似度越高贡献越大）
        Map<Integer, Double> roomScoreMap = new HashMap<>();
        for (Map.Entry<Integer, Double> entry : topUsers) {
            Integer otherUserId = entry.getKey();
            Double similarity = entry.getValue();
            Set<Integer> otherJob = userJobMap.get(otherUserId);

            for (Integer roomId : otherJob) {
                if (!targetUserJob.contains(roomId)) {
                    roomScoreMap.put(roomId, roomScoreMap.getOrDefault(roomId, 0.0) + similarity);
                }
            }
        }

        if (roomScoreMap.isEmpty()) {
            return Collections.emptyList();
        }

        // 6. 根据分数排序推荐房间ID
        List<Integer> sortedJobIds = roomScoreMap.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 7. 查询房间信息并保持顺序
        List<Job> job = jobMapper.selectByIds(sortedJobIds);
        Map<Integer, Job> roomMap = job.stream().collect(Collectors.toMap(Job::getId, r -> r));

        return sortedJobIds.stream()
                .map(roomMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
