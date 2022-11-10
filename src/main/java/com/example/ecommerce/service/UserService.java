package com.example.ecommerce.service;

import com.example.ecommerce.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ecommerce.form.UserLoginForm;
import com.example.ecommerce.form.UserRegisterForm;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tugou
 * @since 2022-10-16
 */
public interface UserService extends IService<User> {

    public User register(UserRegisterForm userRegisterForm);
    public User login(UserLoginForm userLoginForm);

}
