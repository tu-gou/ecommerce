package com.example.ecommerce.vo;

import lombok.Data;

@Data
public class OrderDetailVO {
    private Integer id;
    private Integer quantity;
    private String name;
    private Float price;
    private Float cost;
    private String fileName;
}
