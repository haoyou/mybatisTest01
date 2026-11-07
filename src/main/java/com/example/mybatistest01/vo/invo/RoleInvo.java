package com.example.mybatistest01.vo.invo;

import com.fasterxml.jackson.annotation.JsonAlias;
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
public class RoleInvo implements Serializable {

    @ApiModelProperty("角色Id")
    @JsonAlias("roleid")
    private Long roleId;

    @ApiModelProperty("角色名称")
    @JsonAlias("rolename")
    private String roleName;
}
