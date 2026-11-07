package com.example.mybatistest01.mappers;

import com.example.mybatistest01.entity.Role;
import com.example.mybatistest01.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {
    List<Role> getRoleList();
    void insertRole(Role role);
    Role getRoleById(Long id);
    Role getRoleByName(String roleName);
}
