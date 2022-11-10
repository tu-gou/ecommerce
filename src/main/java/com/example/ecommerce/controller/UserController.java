package com.example.ecommerce.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.entity.UserAddress;
import com.example.ecommerce.exception.EcommerceException;
import com.example.ecommerce.form.UserLoginForm;
import com.example.ecommerce.form.UserRegisterForm;
import com.example.ecommerce.result.ResponseEnum;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.OrdersService;
import com.example.ecommerce.service.UserAddressService;
import com.example.ecommerce.service.UserService;
import com.example.ecommerce.utils.RegexValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
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

    /**
     * 用户注册
     * @param userRegisterForm
     * @param bindingResult
     * @return
     */
    @PostMapping("/register")
    public String register(UserRegisterForm userRegisterForm, BindingResult bindingResult) {//封装所有的前台注册产生的错误
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
    public String login(@Valid UserLoginForm userLoginForm, BindingResult bindingResult, HttpSession session){
        //用户信息非空校验
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


