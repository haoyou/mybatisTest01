package com.example.mybatistest01.entity;

import com.example.mybatistest01.annotation.CreateTime;
import com.example.mybatistest01.annotation.IdKeyGenerator;
import com.example.mybatistest01.annotation.UpdateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRole {
    @IdKeyGenerator
    private long id;
    @ApiModelProperty("用户id")
    private long userId;
    @ApiModelProperty("角色id")
    private long roleId;
    private String createBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @CreateTime
    private Date createTime;
    private String updateBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @UpdateTime
    private Date updateTime;

}
