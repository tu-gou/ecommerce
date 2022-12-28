package com.example.ecommerce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.ecommerce.entity.Rsa;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.EcommerceException;
import com.example.ecommerce.mapper.RsaMapper;
import com.example.ecommerce.result.ResponseEnum;
import com.example.ecommerce.utils.RsaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;

@Controller
@RequestMapping("/RSA")
@Slf4j
public class RSAController {

    @Autowired
    RsaMapper rsaMapper;

    @GetMapping("/generation")
    public String keyGeneration(HttpSession httpSession) throws NoSuchAlgorithmException {

        String publicKey=null;
        String privateKey=null;

        User user=(User)httpSession.getAttribute("user");
        if (user == null) {

            log.info("【公钥私钥生成】当前未登录");
            throw new EcommerceException(ResponseEnum.NOT_LOGIN);
        }
        QueryWrapper<Rsa> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user",user.getUserName());
        Rsa rsa =this.rsaMapper.selectOne(queryWrapper);
        if (rsa == null) {
            publicKey = RsaUtils.generateKeyPair().getPublicKey();
            privateKey=RsaUtils.generateKeyPair().getPrivateKey();
            Rsa newRsa =new Rsa();
            newRsa.setUser(user.getUserName());
            newRsa.setPublicKey(publicKey);
            newRsa.setPrivateKey(privateKey);
            int insert=this.rsaMapper.insert(newRsa);
            if(insert!=1){
                log.info("【公钥私钥生成】插入有误");
                throw new EcommerceException(ResponseEnum.RSA_KEY_INSERT_ERROR);
            }else{
                publicKey=rsa.getPublicKey();
                privateKey=rsa.getPrivateKey();
            }
        }
        return publicKey;
    }
}
