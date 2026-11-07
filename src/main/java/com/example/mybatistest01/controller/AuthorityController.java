package com.example.mybatistest01.controller;

import com.example.mybatistest01.common.api.CommonResult;
import com.example.mybatistest01.service.AuthorityService;
import com.example.mybatistest01.service.UserService;
import com.example.mybatistest01.vo.invo.LoginUserInvo;
import com.example.mybatistest01.vo.invo.RegisterUserInvo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "AuthorityController",value = "授权认证管理")
@RestController
public class AuthorityController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthorityService authorityService;

    @PostMapping("publicrs/auth/register")
    @ResponseBody
    @ApiOperation(value = "注册用户")
    public CommonResult<String> register(@RequestBody @Validated RegisterUserInvo registerUserInvo){
        return authorityService.register(registerUserInvo);
    }

    @PostMapping("publicrs/auth/login")
    @ResponseBody
    @ApiOperation(value = "用户登录")
    public CommonResult<String> register(@RequestBody @Validated LoginUserInvo loginUserInvo){
        return authorityService.login(loginUserInvo);
    }
}
