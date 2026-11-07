package com.example.mybatistest01.service;

import com.example.mybatistest01.entity.User;

import java.util.List;

public interface UserService{
    List<User> findAll();
    void insert(User user);
    User getUsersByUserName(User user);
}
