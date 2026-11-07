package com.example.mybatistest01.mappers;

import com.example.mybatistest01.entity.User;
import com.example.mybatistest01.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRoleMapper {
    List<UserRole> getUserRoleListByUserId(Long userId);

    List<UserRole> getUserRoleListByUserIdAndRoleId(UserRole userRole);
    void insertUserRole(UserRole userRole);
}
