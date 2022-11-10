package com.example.ecommerce.result;

public enum ResponseEnum {
     USER_INFO_NULL(0,"用户信息不能为空"),
     MOBILE_ERROR(1,"手机号码输入格式有误"),
     USERNAME_EXISTS(2,"用户名已存在"),
    USER_REGISTER_ERROR(3,"添加用户失败"),
    USERNAME_NOT_EXISTS(4,"用户名不存在"),
    PASSWORD_ERROR(5,"密码错误"),
    PARAMETER_NULL(6,"参数为空"),
    NOT_LOGIN(7,"未登录"),
    CART_ADD_ERROR(8,"添加购物车失败"),
    PRODUCT_NOT_EXISTS(9,"商品不存在"),
    PRODUCT_STOCK_ERROR(10,"商品库存不足"),
    CART_UPDATE_ERROR(11,"更新购物车失败"),
    CART_UPDATE_PARAMETER_ERROR(12,"更新购物车参数异常"),
    CART_UPDATE_STOCK_ERROR(13,"更新商品库存失败"),
    CART_REMOVE_ERROR(14,"删除购物车失败"),
    ORDERS_CREATE_ERROR(15,"创建订单主表失败"),
    ORDER_DETAIL_CREATE_ERROR(15,"创建订单详情失败"),
    USER_ADDRESS_ADD_ERROR(17,"添加新地址失败"),
    USER_ADDRESS_SET_DEFAULT_ERROR(18,"默认地址修改失败");


    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    //状态码
    private Integer code;
    //对应消息
    private String message;

    ResponseEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
