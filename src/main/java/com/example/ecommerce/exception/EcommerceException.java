package com.example.ecommerce.exception;

import com.example.ecommerce.result.ResponseEnum;
import org.springframework.beans.factory.annotation.Autowired;

public class EcommerceException extends RuntimeException {


    private ResponseEnum responseEnum;

    public ResponseEnum getResponseEnum() {
        return responseEnum;
    }

    public void setResponseEnum(ResponseEnum responseEnum) {
        this.responseEnum = responseEnum;
    }

    public EcommerceException(ResponseEnum responseEnum){
        super(responseEnum.getMessage());
        this.responseEnum=responseEnum;
    }
}
