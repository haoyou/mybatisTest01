package com.example.mybatistest01.vo.invo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Data
@Getter
@Setter
public class LoginUserInvo implements Serializable {



    @ApiModelProperty("密码")
    @NotBlank(message = "密码不能为空")
    private String password;

    @ApiModelProperty("用户姓名")
    @NotBlank(message = "用户名不能为空")
    private String username;

}
