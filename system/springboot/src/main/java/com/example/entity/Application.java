package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Application {
    private Integer id;
    private Integer userId;
    private Integer jobId;
    private String file;
    private String status;
    private String auditTime;
    private String userName;
    private String jobName;
}
