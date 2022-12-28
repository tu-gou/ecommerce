// const publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCBjXm5WLOa3gFGauNx+t8nJ0kf5WccRx9qD1vL/bX2GRaz3N0JExc2iy0oOpDMn2NjbpMCPBYPU1W2OCUFh3mYrOWKuxBGPb7y+dXzHGUt2OcoCAb8rWlMIygQo/V2bLKtkZilnJJnesjMYQQWFnb55nxu18hHrSo9FM2YQDW8YQIDAQAB";
//
// const privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIGNeblYs5reAUZq43H63ycnSR/lZxxHH2oPW8v9tfYZFrPc3QkTFzaLLSg6kMyfY2NukwI8Fg9TVbY4JQWHeZis5Yq7EEY9vvL51fMcZS3Y5ygIBvytaUwjKBCj9XZssq2RmKWckmd6yMxhBBYWdvnmfG7XyEetKj0UzZhANbxhAgMBAAECgYBKC+SwzUQKli1AZEOPmAYHyOqAsouWnAgWhKTBGUfxWzGgp/ImB6VS5YVv4tl0Ov2BjWe09Ubeh5ELz1dJq78RdL69IBqJdvaQ55SW+ZUrugUueoKYjY5pRRzANBlxYLpVGHebSTTAhd/1I6Vl44hGiV0mzrdGUz+HJj/E6kT4aQJBAM113qSOwdLaTWPBVQiwQ+xvTceG+oflSGJcRPG659joPcMLbGhWsiGT7slBh+UHJPINV2VjwF0zch5J4qEKNtsCQQCha5YP3YKaYLfOOJptID6iQUGvRuu+Gxao/DN2dUtvjqM8idXXU8Rds0CqdHu77Si+/d+Agwekx2oixGwqjMhzAkAroiKcU+z5uH7C9qX2aGikZ6be/t1pytmqeenyZD0kpX2oVF67cd32n5IQj6gqjW/dL9Qlph+OK4TKxeopRSANAkEAnddqAVGE6BgzI3/N4W9qT5an+BDNbDNo0Qzr9aV0gj1j+Up+w/OxTD5/uS314Cljcn8lEbEOxF4KtryDWIgZaQJAOcfeOfLw5kErDLHXqrzcRCpk2UiBVDZ3VFVb+hRA2gnY7X7VLT8zEmlsEbU15iB1EnojRbnRL801KI/0TEFt/Q==";
// /**
//  * 随机生成字符串
//  * @param len 指定生成字符串长度
//  */
// function getRandomString(len){
//     let _charStr = 'abacdefghjklmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ0123456789',
//         min = 0,
//         max = _charStr.length-1,
//         _str = '';                    //定义随机字符串 变量
//     //判断是否指定长度，否则默认长度为15
//     len = len || 15;
//     //循环生成字符串
//     for(var i = 0, index; i < len; i++){
//         index = (function(randomIndexFunc, i){
//             return randomIndexFunc(min, max, i, randomIndexFunc);
//         })(function(min, max, i, _self){
//             let indexTemp = Math.floor(Math.random()*(max-min+1)+min),
//                 numStart = _charStr.length - 10;
//             if(i==0&&indexTemp >=numStart){
//                 indexTemp = _self(min, max, i, _self);
//             }
//             return indexTemp ;
//         }, i);
//         _str += _charStr[index];
//     }
//     return _str;
// }
// /**
//  * 混合加密
//  * @param plainText 加密的明文
//  * @param pk 使用的公钥
//  * @return formData 返回的表单数据
//  */
// function Hybrid_Enc(plainText,pk){
//     console.log("要加密的明文是:"+plainText);
//     let sk=getRandomString(16);
//     console.log("对称加密的密钥是："+sk);
//     let cipher2=RSA_encrypt(sk,pk);
//     sk = CryptoJS.enc.Utf8.parse(sk);
//     let encrypted = CryptoJS.AES.encrypt(plainText, sk, {
//         mode: CryptoJS.mode.ECB,
//         padding: CryptoJS.pad.Pkcs7
//     });
//     let cipher1=CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(encrypted.ciphertext.toString()));
//     let mac=sha1(cipher1);
//     console.log("对称加密后的密文："+cipher1);
//     console.log("消息鉴别码："+mac);
//     console.log("对称加密密钥经公钥加密:"+cipher2);
//     let formData=new FormData();
//     formData.append("cipher1",cipher1);
//     formData.append("mac",mac);
//     formData.append("cipher2",cipher2);
//     return formData;
// }
// function test(){
//     let plainText=document.getElementById("content").value;
//     let data=Hybrid_Enc(plainText,publicKey);
//     let request=new XMLHttpRequest();
//     request.open("POST","/test");
//     request.send(data);
//     request.onreadystatechange=function(){
//         if(request.status==200&&request.readyState==4){
//             let ack=request.responseText;
//             if(ack.includes("yes")){
//                 window.alert("成功");
//             }else{
//                 window.alert("失败");
//             }
//         }
//     }
// }