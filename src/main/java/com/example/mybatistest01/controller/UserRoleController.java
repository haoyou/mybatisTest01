package com.example.mybatistest01.controller;

import com.example.mybatistest01.common.api.CommonResult;
import com.example.mybatistest01.common.api.ResultCode;
import com.example.mybatistest01.entity.UserRole;
import com.example.mybatistest01.service.UserRoleService;
import com.example.mybatistest01.vo.invo.UserRoleInvo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户-角色绑定。
 * <p>
 * 给用户绑角色等于授予权限，所有接口都要求 ADMIN；路径上的 publicrs 前缀是历史遗留，不代表公开。
 */
@Api(tags = "UserRoleController", value = "用户-角色中间表管理")
@RestController
@PreAuthorize("hasRole('ADMIN')")
public class UserRoleController {
    @Autowired
    private UserRoleService userRoleService;

    @PostMapping("publicrs/userRole/save")
    @ResponseBody
    @ApiOperation(value = "新增用户-角色")
    public CommonResult addUserRole(@RequestBody UserRoleInvo userRoleInvo) {
        if (ObjectUtils.isEmpty(userRoleInvo)
                || ObjectUtils.isEmpty(userRoleInvo.getRoleId())
                || ObjectUtils.isEmpty(userRoleInvo.getUserId())) {
            throw new RuntimeException(ResultCode.VALIDATE_FAILED.getMessage());
        }
        UserRole userRole = new UserRole();
        BeanUtils.copyProperties(userRoleInvo, userRole);
        List<UserRole> userRoles = userRoleService.getUserRolesByUserIdAndRoleId(userRole);
        if (!CollectionUtils.isEmpty(userRoles)) {
            return CommonResult.failed(ResultCode.DATA_EXIST_FAILED.getMessage());
        }
        String result = userRoleService.saveRole(userRole);
        return CommonResult.success(ResultCode.SUCCESS, result);
    }

    @PostMapping("publicrs/userRole/search")
    @ResponseBody
    @ApiOperation(value = "根据用户id查询用户角色列表")
    public CommonResult searchRoleById(@RequestBody UserRoleInvo userRoleInvo) {
        if (ObjectUtils.isEmpty(userRoleInvo) || ObjectUtils.isEmpty(userRoleInvo.getUserId())) {
            throw new RuntimeException(ResultCode.VALIDATE_FAILED.getMessage());
        }
        UserRole userRole = new UserRole();
        BeanUtils.copyProperties(userRoleInvo, userRole);
        List<UserRole> roles = userRoleService.getRoles(userRole);
        return CommonResult.success(roles);
    }
}
