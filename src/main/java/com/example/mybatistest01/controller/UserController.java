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

import com.example.mybatistest01.entity.User;
import com.example.mybatistest01.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
//    @Value("${jwt.tokenHeader}")
//    private String tokenHeader;
//    @Value("${jwt.tokenHead}")
//    private String tokenHead;

    // http://127.0.0.1:8080/hello?name=lisi
    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    @ResponseBody
    public String hello(@RequestParam(name = "name", defaultValue = "unknown user") String name) {
        return "Hello " + name;
    }

    @RequestMapping(value = "/user",method = RequestMethod.GET)
    @ResponseBody
    public List<User> getUserInfo(){
        return userService.findAll();
    }
    @RequestMapping(value = "/user/insert",method = RequestMethod.POST)
    @ResponseBody
    public String insertUserInfo(@RequestBody User user){
        userService.insert(user);
        return "success";
    }

    @RequestMapping(value = "/user/test",method = RequestMethod.GET)
    @ResponseBody
    public void testStream(){
        userService.testStream();
    }

    @RequestMapping(value = "publicrs/user/test",method = RequestMethod.POST)
    @ResponseBody
    public String testPublicrs(){
        return "publicrs method";
    }





}
