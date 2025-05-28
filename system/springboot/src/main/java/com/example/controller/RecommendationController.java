package com.example.controller;

import com.example.common.Result;
import com.example.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// RecommendationController.java
@RestController
@RequestMapping("/recommend")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/job/{userId}")
    public Result recommendRooms(@PathVariable Integer userId) {
        return Result.success(recommendationService.recommendJobsForUser(userId));
    }
}