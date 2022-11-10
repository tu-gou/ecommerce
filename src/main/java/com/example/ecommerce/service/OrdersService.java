package com.example.ecommerce.service;

import com.example.ecommerce.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ecommerce.vo.OrdersVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tugou
 * @since 2022-10-22
 */
public interface OrdersService extends IService<Orders> {

    public List<OrdersVO> findAllByUserId(Integer id);
}
