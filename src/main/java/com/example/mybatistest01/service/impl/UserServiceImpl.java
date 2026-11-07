package com.example.mybatistest01.service.impl;

import com.example.mybatistest01.common.api.CommonPage;
import com.example.mybatistest01.entity.User;
import com.example.mybatistest01.mappers.UserMapper;
import com.example.mybatistest01.service.UserService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
     @Autowired
     private UserMapper userMapper;

    @Override
    public List<User> findAll() {
        return userMapper.getUserList();
    }

    /**
     * PageHelper 会在下一次 Mapper 查询前拦截并改写 SQL（自动加 limit 与 count）。
     * 因此 startPage 必须紧挨着查询语句，中间不能插入别的查询。
     */
    @Override
    public CommonPage<User> findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> users = userMapper.getUserList();
        return CommonPage.restPage(users);
    }

    @Override
    public void insert(User user) {
        userMapper.insertUser(user);
    }

    @Override
    public User getUsersByUserName(User user) {
        return userMapper.getUserByUserName(user);
    }
}
