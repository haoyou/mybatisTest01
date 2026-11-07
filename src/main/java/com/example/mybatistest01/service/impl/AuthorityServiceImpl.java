package com.example.mybatistest01.service.impl;

import com.example.mybatistest01.common.api.CommonResult;
import com.example.mybatistest01.common.api.ResultCode;
import com.example.mybatistest01.dto.AuthorityDto;
import com.example.mybatistest01.entity.User;
import com.example.mybatistest01.service.AuthorityService;
import com.example.mybatistest01.service.UserService;
import com.example.mybatistest01.utils.JwtTokenUtil;
import com.example.mybatistest01.vo.invo.LoginUserInvo;
import com.example.mybatistest01.vo.invo.RegisterUserInvo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult register(RegisterUserInvo registerUserInvo) {
        User user = new User();
        BeanUtils.copyProperties(registerUserInvo,user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User users = userService.getUsersByUserName(user);
        if(users != null){
            throw new RuntimeException("用户名已存在!");
        }
        userService.insert(user);
        return CommonResult.success(ResultCode.SUCCESS);
    }

    @Override
    public CommonResult login(LoginUserInvo loginUserInvo) {
        String token = null;
        User loginUser = new User();
        BeanUtils.copyProperties(loginUserInvo,loginUser);
        AuthorityDto authUserDto = new AuthorityDto(loginUser);
        User user = userService.getUsersByUserName(loginUser);
        if (!passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
            throw new UsernameNotFoundException("用户名或密码不正确!");
        }
        UsernamePasswordAuthenticationToken authenticationToken =new UsernamePasswordAuthenticationToken(authUserDto,null,authUserDto.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        token = jwtTokenUtil.generateToken(authUserDto);
        if (token == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }
}
