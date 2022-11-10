package com.example.ecommerce.service;

import com.example.ecommerce.entity.Cart;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ecommerce.entity.Orders;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.vo.CartVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tugou
 * @since 2022-10-22
 */
public interface CartService extends IService<Cart> {
    public Boolean add(Cart cart);
    public List<CartVO> findVOListByUserId(Integer userId);
    public Boolean update(Integer id,Integer quantity,Float cost);
    public Boolean delete(Integer id);
    public Orders commit(String userAddress, String address, String remark, User user);

}
