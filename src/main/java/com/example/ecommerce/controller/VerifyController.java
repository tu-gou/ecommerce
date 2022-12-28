package com.example.ecommerce.controller;

import com.example.ecommerce.utils.AESUtil;
import com.example.ecommerce.utils.RsaUtils;
import com.example.ecommerce.utils.SHA1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/verify")
@Slf4j
@ResponseBody
public class VerifyController {

    @PostMapping("/ca")
    @ResponseBody
    public String VerifyKey(String publicKey,String keySign,String CAKey) throws Exception {
        System.out.println(publicKey);
        System.out.println(keySign);
        System.out.println(CAKey);
        if((SHA1.sha1(publicKey)).equals(RsaUtils.decryptByPublicKey(CAKey,keySign))){
            System.out.println("***********验证成功***********");
            return "yes";
        }else{
            return "no";
        }
    }
}
