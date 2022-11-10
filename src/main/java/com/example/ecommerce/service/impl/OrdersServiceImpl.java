package com.example.ecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.ecommerce.entity.OrderDetail;
import com.example.ecommerce.entity.Orders;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.mapper.OrderDetailMapper;
import com.example.ecommerce.mapper.OrdersMapper;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.service.OrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ecommerce.vo.OrderDetailVO;
import com.example.ecommerce.vo.OrdersVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tugou
 * @since 2022-10-22
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<OrdersVO> findAllByUserId(Integer id) {
        QueryWrapper<Orders> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",id);
        List<Orders> ordersList = this.ordersMapper.selectList(queryWrapper);
        List<OrdersVO> ordersVOList=new ArrayList<>();
        for (Orders orders : ordersList) {
            OrdersVO ordersVO=new OrdersVO();
            BeanUtils.copyProperties(orders,ordersVO);
            QueryWrapper<OrderDetail> queryWrapper1=new QueryWrapper<>();
            queryWrapper1.eq("order_id",orders.getId());
            List<OrderDetail> orderDetailList=this.orderDetailMapper.selectList(queryWrapper1);
            List<OrderDetailVO> orderDetailVOList=new ArrayList<>();
            for (OrderDetail orderDetail : orderDetailList) {
                OrderDetailVO orderDetailVO=new OrderDetailVO();
                BeanUtils.copyProperties(orderDetail,orderDetailVO);
                Product product = this.productMapper.selectById(orderDetail.getProductId());
                BeanUtils.copyProperties(product,orderDetailVO);
                orderDetailVOList.add(orderDetailVO);
            }
            ordersVO.setOrderDetailList(orderDetailVOList);
            ordersVOList.add(ordersVO);
        }
        return ordersVOList;
    }
}
