package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Job {
    private Integer id;
    private Integer publisherId;
    private String company;
    private String name;
    private String salary;
    private String description;
    private String requirement;
    private String base;
    private Integer type;
}
