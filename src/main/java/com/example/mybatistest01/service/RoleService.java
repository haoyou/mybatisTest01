package com.example.mybatistest01.service;

import com.example.mybatistest01.common.api.CommonResult;
import com.example.mybatistest01.entity.Role;

import java.util.List;

public interface RoleService {
    List<Role> getRoles();
    Role getRoleById(Long id);
    String saveRole(Role role);
    Role getRoleByName(String roleName);
}
