package com.example.mybatistest01.service;

import com.example.mybatistest01.entity.Role;
import com.example.mybatistest01.entity.UserRole;

import java.util.List;

public interface UserRoleService {
    List<UserRole> getRoles(UserRole userRole);
    String saveRole(UserRole userRole);
    List<UserRole> getUserRolesByUserIdAndRoleId(UserRole userRole);
}
