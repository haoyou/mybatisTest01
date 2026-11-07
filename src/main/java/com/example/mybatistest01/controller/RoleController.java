package com.example.mybatistest01.controller;

import com.example.mybatistest01.common.api.CommonResult;
import com.example.mybatistest01.common.api.ResultCode;
import com.example.mybatistest01.entity.Role;
import com.example.mybatistest01.service.AuthorityService;
import com.example.mybatistest01.service.RoleService;
import com.example.mybatistest01.service.UserService;
import com.example.mybatistest01.vo.invo.RegisterUserInvo;
import com.example.mybatistest01.vo.invo.RoleInvo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "RoleController",value = "角色管理")
@RestController
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping("publicrs/role/save")
    @ResponseBody
    @ApiOperation(value = "新增角色")
    public CommonResult addRole(@RequestBody  RoleInvo roleInvo){
        if (ObjectUtils.isEmpty(roleInvo) || ObjectUtils.isEmpty(roleInvo.getRoleName())){
            throw new RuntimeException(ResultCode.VALIDATE_FAILED.getMessage());
        }
        Role role = new Role();
        BeanUtils.copyProperties(roleInvo,role);
        Role roleExist = roleService.getRoleByName(roleInvo.getRoleName());
        if (!ObjectUtils.isEmpty(roleExist)){
            return CommonResult.failed(ResultCode.DATA_EXIST_FAILED.getMessage());
        }
        String result = roleService.saveRole(role);
        return CommonResult.success(ResultCode.SUCCESS,result);
    }

    @PostMapping("publicrs/role/search")
    @ResponseBody
    @ApiOperation(value = "根据id查询角色")
    public CommonResult searchRoleById(@RequestBody  RoleInvo roleInvo){
        if (ObjectUtils.isEmpty(roleInvo) || ObjectUtils.isEmpty(roleInvo.getRoleId())){
            throw new RuntimeException(ResultCode.VALIDATE_FAILED.getMessage());
        }
        Role role = new Role();
        BeanUtils.copyProperties(roleInvo,role);
        role = roleService.getRoleById(roleInvo.getRoleId());
        return CommonResult.success(role);
    }

    @PostMapping("publicrs/role/searchAll")
    @ResponseBody
    @ApiOperation(value = "查询所有角色")
    public CommonResult searchAllRole(){
        List<Role> roles= roleService.getRoles();
        return CommonResult.success(roles);
    }
}
