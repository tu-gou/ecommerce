package com.example.ecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.ProductCategory;
import com.example.ecommerce.mapper.ProductCategoryMapper;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.service.ProductCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ecommerce.vo.ProductCategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tugou
 * @since 2022-10-22
 */
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    @Autowired
    private ProductCategoryMapper productCategoryMapper ;
    @Autowired
    private ProductMapper productMapper;

    /**
     * 构建商品分类菜单
     * @return
     */
    @Override
    public List<ProductCategoryVO> buildProductCategoryMenu() {
        //查商品分类数据
        List<ProductCategory> productCategoryList=this.productCategoryMapper.selectList(null);

        //数据类型转换成VO
        List<ProductCategoryVO> productCategoryVOList = productCategoryList.stream().map(ProductCategoryVO::new).collect(Collectors.toList());

        //进行父子级菜单的封装
        List<ProductCategoryVO> levelOneList = buildMenu(productCategoryVOList);

        //查找一级分类对应的商品信息
        getLevelOneProduct(levelOneList);
        return levelOneList;
    }

    @Override
    public List<ProductCategoryVO> findAllProductByCategoryLevelOne() {
        QueryWrapper<ProductCategory> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("type",1);
        List<ProductCategory> productCategoryList=this.productCategoryMapper.selectList(queryWrapper);
        List<ProductCategoryVO> productCategoryVOList=productCategoryList.stream().map(ProductCategoryVO::new).collect(Collectors.toList());
        getLevelOneProduct(productCategoryVOList);
        return productCategoryVOList;
    }

    /**
     * 查询一级分类对应的商品信息
     * @param productCategoryVOList
     */
    public void getLevelOneProduct(List<ProductCategoryVO> productCategoryVOList){
        for (ProductCategoryVO productCategoryVO : productCategoryVOList) {
            QueryWrapper<Product> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("categorylevelone_id",productCategoryVO.getId());
            List<Product> productList=this.productMapper.selectList(queryWrapper);
            productCategoryVO.setProductList(productList);
        }
    }


    /**
     * 构建菜单
     * @param productCategoryVOList
     */
    public List<ProductCategoryVO> buildMenu(List<ProductCategoryVO> productCategoryVOList){
        //找到一级菜单
       List<ProductCategoryVO> levelOneList =  productCategoryVOList.stream().filter(c->c.getParentId()==0).collect(Collectors.toList());
        for (ProductCategoryVO productCategoryVO : levelOneList) {
            recursion(productCategoryVOList,productCategoryVO);
        }
        return levelOneList;
    }



    /**
     * 递归分类
     */
    public void recursion(List<ProductCategoryVO> list,ProductCategoryVO productCategoryVO){
        //找到子菜单
       List<ProductCategoryVO> children =  getChildren(list,productCategoryVO);
       productCategoryVO.setChildren(children);
       //继续查找子菜单
        if (children.size() > 0) {
            for(ProductCategoryVO child : children){
                recursion(list,child);
            }
        }
    }

    /**
     * 获取子菜单
     * @param list
     * @param productCategoryVO
     */
    public List<ProductCategoryVO> getChildren(List<ProductCategoryVO> list,ProductCategoryVO productCategoryVO){
        List<ProductCategoryVO> children=new ArrayList<>();
        Iterator<ProductCategoryVO> iterator=list.iterator();
        while(iterator.hasNext()){
            ProductCategoryVO next =iterator.next();
            if(next.getParentId().equals(productCategoryVO.getId())){
                children.add(next);
            }
        }
        return children;
    }
}
