package com.example.service;

import com.example.entity.Job;

import java.util.List;

public interface RecommendationService {
    List<Job> recommendJobsForUser(Integer userId);
}
