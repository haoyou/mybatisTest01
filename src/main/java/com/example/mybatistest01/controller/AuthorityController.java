package com.example.mybatistest01.controller;

import com.example.mybatistest01.common.api.CommonResult;
import com.example.mybatistest01.service.AuthorityService;
import com.example.mybatistest01.service.UserService;
import com.example.mybatistest01.vo.invo.LoginUserInvo;
import com.example.mybatistest01.vo.invo.RegisterUserInvo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public CommonResult<String> login(@RequestBody @Validated LoginUserInvo loginUserInvo){
        return authorityService.login(loginUserInvo);
    }

    /**
     * 返回当前 token 对应的用户与权限，用于自测 / 排查"为什么 403"。
     * 未登录时由 EntryPoint 直接返回 401，进不到这里。
     */
    @GetMapping("whoami")
    @ResponseBody
    @ApiOperation(value = "查看当前登录用户及其权限")
    public CommonResult<Map<String, Object>> whoami(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return CommonResult.unauthorized(null);
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("username", userDetails.getUsername());
        data.put("authorities", authorities);
        return CommonResult.success(data);
    }
}
