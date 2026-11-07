package com.example.mybatistest01.service.impl;

import com.example.mybatistest01.entity.User;
import com.example.mybatistest01.mappers.UserMapper;
import com.example.mybatistest01.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
@Service
public class UserServiceImpl implements UserService {
     @Autowired
     private UserMapper userMapper;
    @Override
    public List<User> findAll() {
        return userMapper.getUserList();
    }

    @Override
    public void insert(User user) {
        userMapper.insertUser(user);
    }

    @Override
    public void testStream() {

    }

    @Override
    public User getUsersByUserName(User user) {
        return userMapper.getUserByUserName(user);
    }
}
