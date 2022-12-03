package com.example.ecommerce.form;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

@Data
public class UserLoginForm {

    @NotEmpty(message = "登录名不能为空")
    private String loginName;

    @NotEmpty(message = "密码不能为空")
    private String password;

    private String SHA1LoginName;
    private String SHA1Password;
}
