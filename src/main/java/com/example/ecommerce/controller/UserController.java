package com.example.ecommerce.controller;


import com.baomidou.mybatisplus.core.assist.ISqlRunner;
import com.baomidou.mybatisplus.core.conditions.SharedString;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.entity.UserAddress;
import com.example.ecommerce.exception.EcommerceException;
import com.example.ecommerce.form.UserLoginForm;
import com.example.ecommerce.form.UserRegisterForm;
import com.example.ecommerce.mapper.UserMapper;
import com.example.ecommerce.result.ResponseEnum;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.OrdersService;
import com.example.ecommerce.service.UserAddressService;
import com.example.ecommerce.service.UserService;
import com.example.ecommerce.utils.AESUtil;
import com.example.ecommerce.utils.GenUtil;
import com.example.ecommerce.utils.RegexValidateUtil;
import com.example.ecommerce.utils.SHA1;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author tugou
 * @since 2022-10-16
 */
@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private CartService cartService;
    @Autowired
    private UserAddressService userAddressService;

    @Autowired
    private UserMapper userMapper;

    private String inquiry;


    /**
     * 用户注册
     * @param userRegisterForm
     * @param bindingResult
     * @return
     */
    @PostMapping("/register")
    public String register(@Valid UserRegisterForm userRegisterForm, BindingResult bindingResult) throws NoSuchAlgorithmException {//封装所有的前台注册产生的错误
//        System.out.println(userRegisterForm);
        //用户信息非空校验
        if (bindingResult.hasErrors()) {
            log.info("【用户注册】用户信息不能为空");//控制台打印错误信息
            throw new EcommerceException(ResponseEnum.USER_INFO_NULL);
        }

        User register=this.userService.register(userRegisterForm);
        if (register == null) {
            log.info("【用户注册】添加用户失败");
            throw new EcommerceException(ResponseEnum.USER_REGISTER_ERROR);
        }
        return "redirect:/login";
    }

    /**
     * 用户登录
     * @param userLoginForm
     * @param bindingResult
     * @return
     */
    @PostMapping("/login")
    public String login(@Valid UserLoginForm userLoginForm, BindingResult bindingResult, HttpSession session) throws NoSuchAlgorithmException {
        //用户信息非空校验
        System.out.println(userLoginForm);
        if (bindingResult.hasErrors()) {
            log.info("【用户登录】用户信息不能为空");//控制台打印错误信息
            throw new EcommerceException(ResponseEnum.USER_INFO_NULL);
        }
        User login = this.userService.login(userLoginForm);
//        ModelAndView modelAndView=new ModelAndView();
//        modelAndView.setViewName("main");
//        return modelAndView;
        session.setAttribute("user" ,login);
        return "redirect:/productCategory/main";
    }






    /**
     * 用户登录认证
     */
    @PostMapping("/authentication0")
    @ResponseBody
    public String Auth0(/*@Valid UserLoginForm userLoginForm,*/ String loginName,String SHA1LoginName,HttpServletResponse response,HttpSession httpSession) throws NoSuchAlgorithmException {
//        user_account user=mapper.getUser(username,password);
//        String ack;
//        if(user==null){
//            ack="no";
        UserLoginForm userLoginForm=new UserLoginForm();
        userLoginForm.setLoginName(loginName);
        userLoginForm.setSHA1LoginName(SHA1LoginName);
        String userName=AESUtil.decrypt(userLoginForm.getLoginName(),"uUXsN6okXYqsh0BB");
        String password="";
        String ack;
        //判断用户名是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("login_name", userName);
        User user = this.userMapper.selectOne(queryWrapper);
        if (user == null) {
            ack="no";
            log.info("【用户登录】用户名不存在");
            throw new EcommerceException(ResponseEnum.USERNAME_NOT_EXISTS);
        } else{
            password=user.getPassword();
            userLoginForm.setPassword(password);
            userLoginForm.setSHA1Password(SHA1.sha1(password));
            Cookie cookie0=new Cookie("username",userName);
            Cookie cookie1=new Cookie("password",password);
            response.addCookie(cookie0);
            response.addCookie(cookie1);
            ack= GenUtil.generate();
            inquiry=ack;
        }


        User login = this.userService.login(userLoginForm);
        httpSession.setAttribute("user" ,login);

        return ack;
    }
    @PostMapping("/authentication1")
    @ResponseBody
    public String Auth1(HttpServletRequest request, String digest) throws NoSuchAlgorithmException {
        System.out.println(digest);
        Cookie[] cookies=request.getCookies();
        String hashcode="";
        for(Cookie cookie:cookies){
//            if(cookie.getName().equals("password")){
//                hashcode=mapper.getHashcode(cookie.getValue());
//            }
            if (cookie.getName().equals("username")) {

                //hashcode=cookie.getValue();
                String name =cookie.getValue();
                QueryWrapper<User> queryWrapper=new QueryWrapper<>();
                queryWrapper.eq("login_name",name);
                User user = this.userMapper.selectOne(queryWrapper);
                String password=user.getPassword();
                hashcode=SHA1.sha1(password);
                System.out.println("6666666666666666666666666666");
            }
        }

        String digest2= SHA1.sha1(hashcode+inquiry);
//        System.out.println(hashcode+inquiry);
//        System.out.println(digest2);
        if(digest2.equals(digest)){
            return "yes";
        }else{
            return "no";
        }
    }






    /**
     * 返回当前用户的订单列表
     * @return
     */
    @GetMapping("/orderList")
    public ModelAndView ordersList(HttpSession httpSession){
        //判断是否为登录用户
        User user=(User)httpSession.getAttribute("user");
        if (user == null) {

            log.info("【更新购物车】当前未登录");
            throw new EcommerceException(ResponseEnum.NOT_LOGIN);
        }
        ModelAndView modelAndView =new ModelAndView();
        modelAndView.setViewName("orderList");
        modelAndView.addObject("orderList",this.ordersService.findAllByUserId(user.getId()));
        modelAndView.addObject("cartList",this.cartService.findVOListByUserId(user.getId()));
        return modelAndView;
    }

    @GetMapping("/addressList")
    public ModelAndView addressList(HttpSession httpSession){
        //判断是否为登录用户
        User user=(User)httpSession.getAttribute("user");
        if (user == null) {

            log.info("【更新购物车】当前未登录");
            throw new EcommerceException(ResponseEnum.NOT_LOGIN);
        }
        ModelAndView modelAndView =new ModelAndView();
        modelAndView.setViewName("userAddressList");
        QueryWrapper<UserAddress> queryWrapper=new QueryWrapper();
        queryWrapper.eq("user_id",user.getId());
        modelAndView.addObject("addressList",this.userAddressService.list(queryWrapper));
        modelAndView.addObject("cartList",this.cartService.findVOListByUserId(user.getId()));
        return modelAndView;
    }
}


