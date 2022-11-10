package com.example.ecommerce.vo;

import lombok.Data;

@Data
public class CartVO {

    private Integer id;
    private Integer productId;
    private Integer quantity;
    private Float cost;
    protected String name;
    private String fileName;
    private Float price;
    private Integer stock;
}
