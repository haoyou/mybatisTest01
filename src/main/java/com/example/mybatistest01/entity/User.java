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
public class User {
    @IdKeyGenerator
    private long id;
    @ApiModelProperty("地址")
    private String address;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("手机号码")
    private String mobile;
    @ApiModelProperty("身份证")
    private String idCard;
    @ApiModelProperty("生日")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date birthday;
    @ApiModelProperty("性别")
    private String sex;
    @ApiModelProperty("用户姓名")
    private String username;
    @ApiModelProperty("状态")
    private String status;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @CreateTime
    private Date createTime;

    private String updateBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @UpdateTime
    private Date updateTime;

}
