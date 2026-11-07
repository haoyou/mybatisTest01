/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mybatistest01.controller;

import com.example.mybatistest01.common.api.CommonResult;
import com.example.mybatistest01.common.api.ResultCode;
import com.example.mybatistest01.entity.User;
import com.example.mybatistest01.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author <a href="mailto:chenxilzx1@gmail.com">theonefx</a>
 */
@RestController
@Api(tags = "UserController",value = "用户管理")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // http://127.0.0.1:8080/hello?name=lisi
    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    @ResponseBody
    public String hello(@RequestParam(name = "name", defaultValue = "unknown user") String name) {
        return "Hello " + name;
    }

    /**
     * 查看全部用户，只有 admin 角色可以访问。
     * 权限不通过时由 RestAccessDeniedHandler 返回 JSON 403。
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/user",method = RequestMethod.GET)
    @ResponseBody
    public List<User> getUserInfo(){
        return userService.findAll();
    }

    /**
     * 新增用户，仅 ADMIN 可调用。
     * <p>
     * 与公开的 /publicrs/auth/register 不同，这里不做"自助注册"，因此：
     * 用户名、密码必填；密码会用 BCrypt 编码后入库（否则存进去的明文永远登录不上）；
     * 用户名重复直接拒绝。
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/user/insert",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<String> insertUserInfo(@RequestBody User user){
        if (user == null
                || !StringUtils.hasText(user.getUsername())
                || !StringUtils.hasText(user.getPassword())) {
            return CommonResult.validateFailed("用户名和密码不能为空");
        }
        if (userService.getUsersByUserName(user) != null) {
            return CommonResult.failed(ResultCode.DATA_EXIST_FAILED.getMessage());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.insert(user);
        return CommonResult.success("success");
    }

    @RequestMapping(value = "publicrs/user/test",method = RequestMethod.POST)
    @ResponseBody
    public String testPublicrs(){
        return "publicrs method";
    }

}
