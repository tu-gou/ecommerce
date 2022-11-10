package com.example.ecommerce.service;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.ProductCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ecommerce.vo.ProductCategoryVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tugou
 * @since 2022-10-22
 */
public interface ProductCategoryService extends IService<ProductCategory> {

    public List<ProductCategoryVO> buildProductCategoryMenu();
    public List<ProductCategoryVO> findAllProductByCategoryLevelOne();
}
