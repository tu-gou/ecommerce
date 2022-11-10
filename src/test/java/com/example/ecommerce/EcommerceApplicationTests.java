package com.example.ecommerce;

import com.example.ecommerce.service.ProductCategoryService;
import com.example.ecommerce.vo.ProductCategoryVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class EcommerceApplicationTests {

    @Autowired
    private ProductCategoryService service;


    @Test
    void contextLoads() {
        List<ProductCategoryVO> productCategoryVOList=this.service.buildProductCategoryMenu();
        int i=0;
    }

}
