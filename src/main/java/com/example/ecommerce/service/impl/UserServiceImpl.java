package com.example.ecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.AES;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.EcommerceException;
import com.example.ecommerce.form.UserLoginForm;
import com.example.ecommerce.form.UserRegisterForm;
import com.example.ecommerce.mapper.UserMapper;
import com.example.ecommerce.result.ResponseEnum;
import com.example.ecommerce.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ecommerce.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author tugou
 * @since 2022-10-16
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Autowired
    private UserMapper userMapper;

    //private String inquiry;

    /**
     * 用户注册
     *
     * @param userRegisterForm
     * @return
     */
    @Override
    public User register(UserRegisterForm userRegisterForm) throws NoSuchAlgorithmException {

        //数据存储
        User user = new User();
        user.setLoginName(AESUtil.decrypt(userRegisterForm.getLoginName()));
        user.setPassword(userRegisterForm.getPassword());
        user.setUserName(AESUtil.decrypt(userRegisterForm.getUserName()));
        user.setGender(Integer.parseInt(AESUtil.decrypt(userRegisterForm.getGender())));
        user.setMobile(AESUtil.decrypt(userRegisterForm.getMobile()));
//        System.out.println(user);
        //MAC验证
        boolean a = AESUtil.MACVerify(user.getLoginName(), userRegisterForm.getSHA1LoginName());
        boolean b = AESUtil.MACVerify(AESUtil.decrypt(userRegisterForm.getPassword()), userRegisterForm.getSHA1Password());
        boolean c = AESUtil.MACVerify(user.getUserName(), userRegisterForm.getSHA1UserName());
        boolean d = AESUtil.MACVerify(user.getGender().toString(), userRegisterForm.getSHA1Gender());
        boolean e = AESUtil.MACVerify(user.getMobile(), userRegisterForm.getSHA1Mobile());
        if(!(a&b&c&d&e)){
            log.info("【用户注册】数据传输内容出错");
            throw new EcommerceException(ResponseEnum.USER_REGISTER_DATA_ERROR);
        }

        //用户名是否可用`
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("login_name", user.getLoginName());
        User one = this.userMapper.selectOne(queryWrapper);
        if (one != null) {
            log.info("【用户注册】用户名已存在");//控制台打印错误信息
            throw new EcommerceException(ResponseEnum.USERNAME_EXISTS);
        }

        //手机号码格式校验
        if (!RegexValidateUtil.checkMobile(user.getMobile())) {
            log.info("【用户注册】手机号码输入格式有误");//控制台打印错误信息
            throw new EcommerceException(ResponseEnum.MOBILE_ERROR);
        }


        int insert = this.userMapper.insert(user);
        if (insert != 1) {
            log.info("【用户注册】添加用户失败");
            throw new EcommerceException(ResponseEnum.USER_REGISTER_ERROR);
        }

        return user;
    }
//    @Override
//    public User register(UserRegisterForm userRegisterForm) {
//        //用户名是否可用
//        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
//        queryWrapper.eq("login_name",userRegisterForm.getLoginName());
//        User one =this.userMapper.selectOne(queryWrapper);
//        if(one!=null){
//            log.info("【用户注册】用户名已存在");//控制台打印错误信息
//            throw new EcommerceException(ResponseEnum.USERNAME_EXISTS);
//        }
//
//        //手机号码格式校验
//        if(!RegexValidateUtil.checkMobile(userRegisterForm.getMobile())){
//            log.info("【用户注册】手机号码输入格式有误");//控制台打印错误信息
//            throw new EcommerceException(ResponseEnum.MOBILE_ERROR);
//        }
//
//        //数据存储
//        User user=new User();
//        BeanUtils.copyProperties(userRegisterForm,user);//将userRegisterForm的信息拷贝给user,属性必须完全一致
//        user.setPassword(MD5Util.getSaltMD5(user.getPassword()));
//        int insert = this.userMapper.insert(user);
//        if(insert!=1){
//            log.info("【用户注册】添加用户失败");
//            throw new EcommerceException(ResponseEnum.USER_REGISTER_ERROR);
//        }
//
//        return user;
//    }

    /**
     * 用户登录
     *
     * @param userLoginForm
     * @return
     */
    @Override
    public User login(UserLoginForm userLoginForm) throws NoSuchAlgorithmException {

        userLoginForm.setLoginName(AESUtil.decrypt(userLoginForm.getLoginName()));

        //MAC验证
        boolean a=AESUtil.MACVerify(userLoginForm.getLoginName(),userLoginForm.getSHA1LoginName());
        boolean b= AESUtil.MACVerify(userLoginForm.getPassword(),userLoginForm.getSHA1Password());
        if(a&b){
            log.info("【用户登录】数据传输内容出错");
            throw new EcommerceException(ResponseEnum.USER_REGISTER_DATA_ERROR);
        }

        //判断用户名是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("login_name", userLoginForm.getLoginName());
        User user = this.userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("【用户登录】用户名不存在");
            throw new EcommerceException(ResponseEnum.USERNAME_NOT_EXISTS);
        }
        //判断密码是否正确
//        if (!AESUtil.decrypt(userLoginForm.getPassword()).equals(AESUtil.decrypt(user.getPassword()))) {
//            log.info("【用户登录】密码错误");
//            throw new EcommerceException(ResponseEnum.PASSWORD_ERROR);
//        }

        return user;
    }
//    @Override
//    public User login(UserLoginForm userLoginForm) {
//        //判断用户名是否存在
//        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
//        queryWrapper.eq("login_name",userLoginForm.getLoginName());
//        User user=this.userMapper.selectOne(queryWrapper);
//        if (user == null) {
//            log.info("【用户登录】用户名不存在");
//            throw new EcommerceException(ResponseEnum.USERNAME_NOT_EXISTS);
//        }
//        //判断密码是否正确
//        boolean saltverifyMD5 = MD5Util.getSaltverifyMD5((userLoginForm.getPassword()), user.getPassword());
//        if (!saltverifyMD5) {
//            log.info("【用户登录】密码错误");
//            throw new EcommerceException(ResponseEnum.PASSWORD_ERROR);
//        }
//        return user;
//    }


}
