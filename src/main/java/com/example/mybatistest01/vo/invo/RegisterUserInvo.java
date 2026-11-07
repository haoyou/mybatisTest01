package com.example.mybatistest01.vo.invo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
@Data
@Getter
@Setter
public class RegisterUserInvo implements Serializable {

    @ApiModelProperty("地址")
    @NotBlank(message = "邮箱地址不能为空")
    private String address;

    @ApiModelProperty("密码")
    @NotBlank(message = "密码不能为空")
    private String password;

    @ApiModelProperty("手机号码")
    @NotBlank(message = "手机号码不能为空")
    private String mobile;

    @ApiModelProperty("身份证")
    @NotBlank(message = "身份证不能为空")
    private String idCard;

    @ApiModelProperty("生日")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date birthday;

    @ApiModelProperty("性别")
    private String sex;

    @ApiModelProperty("用户姓名")
    @NotBlank(message = "用户名不能为空")
    private String username;

}
