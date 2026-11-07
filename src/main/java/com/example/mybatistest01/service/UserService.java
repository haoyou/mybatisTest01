package com.example.mybatistest01.service;

import com.example.mybatistest01.common.api.CommonPage;
import com.example.mybatistest01.entity.User;

import java.util.List;

public interface UserService{
    List<User> findAll();

    /**
     * 分页查询用户（PageHelper 实现）。
     */
    CommonPage<User> findPage(int pageNum, int pageSize);

    void insert(User user);

    User getUsersByUserName(User user);
}
