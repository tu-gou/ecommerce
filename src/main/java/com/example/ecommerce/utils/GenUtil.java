package com.example.ecommerce.utils;

import java.security.SecureRandom;

public class GenUtil {
    public static String generate(){
        byte[] bytes=new byte[8];
        new SecureRandom().nextBytes(bytes);
        StringBuilder result0=new StringBuilder();
        for(byte item:bytes){
            result0.append(String.format("%02x", item));
        }
        return result0.toString();
    }
}
