package com.example.ecommerce.utils;

import java.security.NoSuchAlgorithmException;

public class HybridDecrypt {
    public static  String privateKey= "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIGNeblYs5reAUZq43H63ycnSR/lZxxHH2oPW8v9tfYZFrPc3QkTFzaLLSg6kMyfY2NukwI8Fg9TVbY4JQWHeZis5Yq7EEY9vvL51fMcZS3Y5ygIBvytaUwjKBCj9XZssq2RmKWckmd6yMxhBBYWdvnmfG7XyEetKj0UzZhANbxhAgMBAAECgYBKC+SwzUQKli1AZEOPmAYHyOqAsouWnAgWhKTBGUfxWzGgp/ImB6VS5YVv4tl0Ov2BjWe09Ubeh5ELz1dJq78RdL69IBqJdvaQ55SW+ZUrugUueoKYjY5pRRzANBlxYLpVGHebSTTAhd/1I6Vl44hGiV0mzrdGUz+HJj/E6kT4aQJBAM113qSOwdLaTWPBVQiwQ+xvTceG+oflSGJcRPG659joPcMLbGhWsiGT7slBh+UHJPINV2VjwF0zch5J4qEKNtsCQQCha5YP3YKaYLfOOJptID6iQUGvRuu+Gxao/DN2dUtvjqM8idXXU8Rds0CqdHu77Si+/d+Agwekx2oixGwqjMhzAkAroiKcU+z5uH7C9qX2aGikZ6be/t1pytmqeenyZD0kpX2oVF67cd32n5IQj6gqjW/dL9Qlph+OK4TKxeopRSANAkEAnddqAVGE6BgzI3/N4W9qT5an+BDNbDNo0Qzr9aV0gj1j+Up+w/OxTD5/uS314Cljcn8lEbEOxF4KtryDWIgZaQJAOcfeOfLw5kErDLHXqrzcRCpk2UiBVDZ3VFVb+hRA2gnY7X7VLT8zEmlsEbU15iB1EnojRbnRL801KI/0TEFt/Q==";
    public static  String publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCBjXm5WLOa3gFGauNx+t8nJ0kf5WccRx9qD1vL/bX2GRaz3N0JExc2iy0oOpDMn2NjbpMCPBYPU1W2OCUFh3mYrOWKuxBGPb7y+dXzHGUt2OcoCAb8rWlMIygQo/V2bLKtkZilnJJnesjMYQQWFnb55nxu18hHrSo9FM2YQDW8YQIDAQAB";
    public static void setPublicKey(String pk){
        publicKey=pk;
    }
    public static void setPrivateKey(String sk){
        privateKey=sk;
    }
    /**
     * 验证消息真实性
     * @param cipher1 对称加密的密文
     * @param mac 密文的消息鉴别码
     */
    public static boolean verify(String cipher1,String mac) throws NoSuchAlgorithmException {
        return mac.equals(SHA1.sha1(cipher1));
    }
    /**
     * 混合解密
     * @param cipher1 对称加密的密文
     * @param cipher2 对称加密的密钥经公钥加密后的密文
     * @return plainText 解密结果
     */
    public static String decrypt(String cipher1,String cipher2) throws Exception {
        System.out.println("cipher1是："+cipher1);
        System.out.println("cipher2是："+cipher2);
        String sk=RsaUtils.decryptByPrivateKey(privateKey,cipher2);
        System.out.println("对称加密密钥是："+sk);
//        String plainText=AESUtil.decrypt(cipher1,sk);
        String plainText=AESUtil.decrypt(cipher1,sk);
        return plainText;
    }
}
