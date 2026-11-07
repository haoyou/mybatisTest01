package com.example.mybatistest01.vo.invo;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class UserRoleInvo implements Serializable {

    @ApiModelProperty("角色Id")
    @JsonAlias("roleid")
    private Long roleId;

    @ApiModelProperty("用户Id")
    @JsonAlias("userid")
    private Long userId;
}
