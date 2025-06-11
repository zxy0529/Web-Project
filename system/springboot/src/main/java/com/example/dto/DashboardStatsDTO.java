package com.example.dto;

import com.example.entity.Application;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private Integer total;
    private Integer intern;
    private Integer fulltime;
    private List<JobCount> byJobName;
    private List<Application> recent;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobCount {
        private String name;
        private Integer count;
    }
}
