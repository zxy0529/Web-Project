package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User extends Account {

    /** 主键ID */
    private Integer id;

    private String username;
    private String password;
    private String name;
    private String avatar;
    private String role;
    private String phone;
    private String email;
}
