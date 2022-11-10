package com.example.ecommerce.service;

import com.example.ecommerce.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tugou
 * @since 2022-10-22
 */
public interface ProductService extends IService<Product> {

    public List<Product> findAllByTypeAndProductCategoryId(Integer type,Integer id);
}
