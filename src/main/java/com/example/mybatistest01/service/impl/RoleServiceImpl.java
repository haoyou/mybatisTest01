package com.example.mybatistest01.service.impl;

import com.example.mybatistest01.common.api.CommonResult;
import com.example.mybatistest01.common.api.ResultCode;
import com.example.mybatistest01.entity.Role;
import com.example.mybatistest01.mappers.RoleMapper;
import com.example.mybatistest01.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public List<Role> getRoles() {
        return roleMapper.getRoleList();
    }

    @Override
    public Role getRoleById(Long id) {
        return roleMapper.getRoleById(id);
    }

    @Override
    public String saveRole(Role role) {
        roleMapper.insertRole(role);
        return ResultCode.SUCCESS.getMessage();
    }

    @Override
    public Role getRoleByName(String roleName) {
        return roleMapper.getRoleByName(roleName);
    }
}
