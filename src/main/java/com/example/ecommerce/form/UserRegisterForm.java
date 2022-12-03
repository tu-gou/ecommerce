package com.example.ecommerce.form;


import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
public class UserRegisterForm {//可以对非浏览器提交的数据进行校验
    @NotEmpty(message = "登录名不能为空")
    private String loginName;
    @NotEmpty(message = "用户名不能为空")
    private String userName;
    @NotEmpty(message = "密码不能为空")
    private String password;
    @NotNull(message = "性别不能为空")
    private String gender;
    @NotEmpty(message = "手机不能为空")
    private String mobile;

    private String SHA1LoginName;
    private String SHA1UserName;
    private String SHA1Password;
    private String SHA1Gender;
    private String SHA1Mobile;
}
