package com.example.ecommerce.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.Orders;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.EcommerceException;
import com.example.ecommerce.result.ResponseEnum;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.UserAddressService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author tugou
 * @since 2022-10-22
 */
@Controller
//@CrossOrigin(origins = {"*"})
@RequestMapping("/cart")
@Slf4j
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private UserAddressService userAddressService;


    /**
     * 添加购物车
     * @return
     */
    @GetMapping("/add/{productId}/{price}/{quantity}")
    public String add(
            @PathVariable("productId") Integer productId,
            @PathVariable("price") Float price,
            @PathVariable("quantity") Integer quantity,
            HttpSession httpSession
    ){
        if (productId == null || price == null || quantity == null) {

            log.info("【添加购物车】参数为空");
            throw new EcommerceException(ResponseEnum.PARAMETER_NULL);
        }
        //判断是否为登录用户
        User user=(User)httpSession.getAttribute("user");
        if (user == null) {

            log.info("【添加购物车】当前未登录");
            throw new EcommerceException(ResponseEnum.NOT_LOGIN);
        }
        Cart cart=new Cart();
        cart.setUserId(user.getId());
        cart.setProductId(productId);
        cart.setQuantity(quantity);
        cart.setCost(price * quantity);
        Boolean add = this.cartService.add(cart);
        if (!add) {
            log.info("【添加购物车】添加失败");
            throw new EcommerceException(ResponseEnum.CART_ADD_ERROR);
        }
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("settlement1");
        modelAndView.addObject("cartList",this.cartService.findVOListByUserId(user.getId()));
        return "redirect:/cart/get";
    }

    /**
     * 查看购物车
     * @param httpSession
     * @return
     */
    @GetMapping("/get")
    public ModelAndView getCart(HttpSession httpSession){
        //判断是否为登录用户
        User user=(User)httpSession.getAttribute("user");
        if (user == null) {

            log.info("【添加购物车】当前未登录");
            throw new EcommerceException(ResponseEnum.NOT_LOGIN);
        }
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("settlement1");
        modelAndView.addObject("cartList",this.cartService.findVOListByUserId(user.getId()));
        return modelAndView;
    }

    /**
     * 更新购物车
     * @param id
     * @param quantity
     * @param cost
     * @return
     */
    @PostMapping("/update/{id}/{quantity}/{cost}")
    @ResponseBody
    public String update(
            @PathVariable("id") Integer id,
            @PathVariable("quantity") Integer quantity,
            @PathVariable("cost") Float cost,
            HttpSession httpSession
    ){
        if (id == null || cost == null || quantity == null) {

            log.info("【更新购物车】参数为空");
            throw new EcommerceException(ResponseEnum.PARAMETER_NULL);
        }
        //判断是否为登录用户
        User user=(User)httpSession.getAttribute("user");
        if (user == null) {

            log.info("【更新购物车】当前未登录");
            throw new EcommerceException(ResponseEnum.NOT_LOGIN);
        }
        if(this.cartService.update(id,quantity,cost)){
            return "success";
        }
        return "fail";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id,HttpSession httpSession){
        if (id == null) {

            log.info("【更新购物车】参数为空");
            throw new EcommerceException(ResponseEnum.PARAMETER_NULL);
        }
        //判断是否为登录用户
        User user=(User)httpSession.getAttribute("user");
        if (user == null) {

            log.info("【更新购物车】当前未登录");
            throw new EcommerceException(ResponseEnum.NOT_LOGIN);
        }
        Boolean delete = this.cartService.delete(id);
        if(delete){
            return "redirect:/cart/get";
        }
        return null;
    }


    /**
     * 确认订单
     * @param httpSession
     * @return
     */
    @GetMapping("/confirm")
    public ModelAndView comfirm(HttpSession httpSession){
        User user=(User)httpSession.getAttribute("user");
        if (user == null) {

            log.info("【更新购物车】当前未登录");
            throw new EcommerceException(ResponseEnum.NOT_LOGIN);
        }
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("settlement2");
        modelAndView.addObject("cartList",this.cartService.findVOListByUserId(user.getId()));
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("user_id",user.getId());
        modelAndView.addObject("addressList",this.userAddressService.list(queryWrapper));
        return modelAndView;
    }

    /**
     * 提交订单
     * @param userAddress
     * @param address
     * @param remark
     * @param httpSession
     * @return
     */
    @PostMapping("/commit")
    public ModelAndView commit(String userAddress,
                               String address,
                               String remark,
                               String HPI,
                               String eSignHOP,
                               String eEncryptKey,
                               HttpSession httpSession) throws Exception {
        if (userAddress == null) {

            log.info("【更新购物车】参数为空");
            throw new EcommerceException(ResponseEnum.PARAMETER_NULL);
        }
        //判断是否为登录用户
        User user=(User)httpSession.getAttribute("user");
        if (user == null) {

            log.info("【更新购物车】当前未登录");
            throw new EcommerceException(ResponseEnum.NOT_LOGIN);
        }
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("settlement3");
        Orders orders=this.cartService.commit(userAddress,address,remark,HPI,eSignHOP,eEncryptKey,user);
        if(orders!=null){
            modelAndView.addObject("orders",orders);
            modelAndView.addObject("cartList",this.cartService.findVOListByUserId(user.getId()));
            return modelAndView;
        }
        return null;

    }

}

