package com.example.mybatistest01.service.impl;

import com.example.mybatistest01.common.api.ResultCode;
import com.example.mybatistest01.entity.Role;
import com.example.mybatistest01.entity.UserRole;
import com.example.mybatistest01.mappers.RoleMapper;
import com.example.mybatistest01.mappers.UserRoleMapper;
import com.example.mybatistest01.service.RoleService;
import com.example.mybatistest01.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public List<UserRole> getRoles(UserRole userRole) {
        return userRoleMapper.getUserRoleListByUserId(userRole.getUserId());
    }

    @Override
    public String saveRole(UserRole userRole) {
        userRoleMapper.insertUserRole(userRole);
        return ResultCode.SUCCESS.getMessage();
    }

    @Override
    public List<UserRole> getUserRolesByUserIdAndRoleId(UserRole userRole) {
        return userRoleMapper.getUserRoleListByUserIdAndRoleId(userRole);
    }
}
