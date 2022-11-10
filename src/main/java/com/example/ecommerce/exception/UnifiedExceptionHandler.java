package com.example.ecommerce.exception;


import com.example.ecommerce.result.ResponseEnum;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

@RestControllerAdvice
public class UnifiedExceptionHandler {//自定义统一的异常处理器捕获异常

    @ExceptionHandler(value=EcommerceException.class)
    public ModelAndView handlerException(EcommerceException ecommerceException){
        ModelAndView modelAndView=new ModelAndView();
        ResponseEnum responseEnum=ecommerceException.getResponseEnum();
        switch (responseEnum.getCode()){
            case 0:
                modelAndView.setViewName("register");
                modelAndView.addObject("userInfoEmpty",responseEnum.getMessage());
                break;
            case 1:
                modelAndView.setViewName("register");
                modelAndView.addObject("mobileError",responseEnum.getMessage());
                break;
            case 2:
                modelAndView.setViewName("register");
                modelAndView.addObject("userNameExists",responseEnum.getMessage());
                break;
            case 4:
                modelAndView.setViewName("login");
                modelAndView.addObject("userNameError",responseEnum.getMessage());
                break;
            case 5:
                modelAndView.setViewName("login");
                modelAndView.addObject("passwordError",responseEnum.getMessage());
                break;
            case 6:
                modelAndView.setViewName("login");
                break;
        }
        return modelAndView;
    }
}
