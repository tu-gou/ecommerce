package com.example.ecommerce.mapper;

import com.example.ecommerce.entity.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tugou
 * @since 2022-10-22
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

}
