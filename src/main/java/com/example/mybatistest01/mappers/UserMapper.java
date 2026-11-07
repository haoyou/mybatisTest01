package com.example.mybatistest01.mappers;

import com.example.mybatistest01.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> getUserList();
    void insertUser(User user);
    User getUserByUserName(User user);
}
