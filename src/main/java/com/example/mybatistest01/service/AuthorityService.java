package com.example.mybatistest01.service;

import com.example.mybatistest01.common.api.CommonResult;
import com.example.mybatistest01.vo.invo.LoginUserInvo;
import com.example.mybatistest01.vo.invo.RegisterUserInvo;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthorityService  {
    CommonResult<String> register(RegisterUserInvo registerUserInvo);
    CommonResult<String> login(LoginUserInvo loginUserInvo);
}
