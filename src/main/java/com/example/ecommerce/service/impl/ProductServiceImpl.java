package com.example.ecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Product> findAllByTypeAndProductCategoryId(Integer type, Integer id) {
        QueryWrapper queryWrapper=new QueryWrapper<>();
        String column=null;
        switch (type){
            case 1:
                column="categorylevelone_id";
                break;
            case 2:
                column="categoryleveltwo_id";
                break;
            case 3:
                column="categorylevelthree_id";
                break;
        }
        queryWrapper.eq(column,id);
        return this.productMapper.selectList(queryWrapper);
    }
}
