package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Publisher extends Account {
    private Integer id;

    private String username;
    private String password;
    private String name;
    private String avatar;
    private String role;
    private String phone;
    private String email;
}
