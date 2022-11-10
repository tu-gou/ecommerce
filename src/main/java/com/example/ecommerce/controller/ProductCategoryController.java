package com.example.ecommerce.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author tugou
 * @since 2022-10-22
 */
@Controller
@RequestMapping("/productCategory")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private CartService cartService;
    /**
     * 首页数据
     * @param httpSession
     * @return
     */
    @GetMapping("/main")
    public ModelAndView main(HttpSession httpSession){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("main");
        //封装商品分类菜单
        modelAndView.addObject("list",this.productCategoryService.buildProductCategoryMenu());
        //封装一级分类对应的商品信息
        modelAndView.addObject("levelOneProductList",this.productCategoryService.findAllProductByCategoryLevelOne());
        //判断是否为登录用户
        User user = (User)httpSession.getAttribute("user");
        if (user == null) {
            //未登录
            modelAndView.addObject("cartList",new ArrayList<>());
        } else{
            //登录用户
            //查询该用户的购物车记录
            modelAndView.addObject("cartList",this.cartService.findVOListByUserId(user.getId()));
        }
        return modelAndView;
    }

}

