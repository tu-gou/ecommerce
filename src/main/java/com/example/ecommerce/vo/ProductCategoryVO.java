package com.example.ecommerce.vo;


import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.ProductCategory;
import lombok.Data;

import java.util.List;

@Data
public class ProductCategoryVO {
    private Integer id;
    private  String name;
    private List<ProductCategoryVO> children;//子菜单
    private Integer parentId;
    private List<Product> productList;


    public ProductCategoryVO(ProductCategory productCategory){
        this.id= productCategory.getId();
        this.name= productCategory.getName();
        this.parentId=productCategory.getParentId();
    }
}
