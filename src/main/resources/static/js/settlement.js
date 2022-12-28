$(function(){
    //计算总价
    var array = $(".qprice");
    var totalCost = 0;
    for(var i = 0;i < array.length;i++){
        var val = parseInt($(".qprice").eq(i).html().substring(1));
        totalCost += val;
    }
    $("#totalprice").html("￥"+totalCost);
    //settlement2使用
    $("#settlement2_totalCost").val(totalCost);
});

function addQuantity(obj){
    let index = $(".car_btn_2").index(obj);
    let quantity = parseInt($(".car_ipt").eq(index).val());
    let stock = parseInt($(".productStock").eq(index).val());
    if(quantity == stock){
        alert("库存不足！");
        return false;
    }
    quantity++;
    let price = parseFloat($(".productPrice").eq(index).val());
    let cost = quantity * price;
    let id = parseInt($(".id").eq(index).val());
    $.ajax({
       url:"/cart/update/"+id+"/"+quantity+"/"+cost,
       type:"POST",
       success:function (data) {
            if(data == "success"){
                $(".qprice").eq(index).text('￥'+cost);
                $(".car_ipt").eq(index).val(quantity);

                let array = $(".qprice");
                let totalCost = 0;
                for(let i = 0;i < array.length;i++){
                    let val = parseInt($(".qprice").eq(i).html().substring(1));
                    totalCost += val;
                }
                $("#totalprice").html("￥"+totalCost);
            }
       }
    });
}

function subQuantity(obj){
    let index = $(".car_btn_1").index(obj);
    let quantity = parseInt($(".car_ipt").eq(index).val());
    if(quantity == 1){
        alert("至少选择一件商品！");
        return false;
    }
    quantity--;
    let price = parseFloat($(".productPrice").eq(index).val());
    let cost = quantity * price
    let id = parseInt($(".id").eq(index).val());
    $.ajax({
        url:"/cart/update/"+id+"/"+quantity+"/"+cost,
        type:"POST",
        success:function (data) {
            if(data == "success"){
                $(".qprice").eq(index).text('￥'+cost);
                $(".car_ipt").eq(index).val(quantity);

                let array = $(".qprice");
                let totalCost = 0;
                for(let i = 0;i < array.length;i++){
                    let val = parseInt($(".qprice").eq(i).html().substring(1));
                    totalCost += val;
                }
                $("#totalprice").html("￥"+totalCost);
            }
        }
    });
}

function removeCart(obj){
    let index = $(".delete").index(obj);
    let id = parseInt($(".id").eq(index).val());
    if(confirm("是否确定删除?")){
        window.location.href = "/cart/delete/"+id;
    }
}

function settlement2() {
    var totalCost = $("#totalprice").text();
    if(totalCost == "￥0"){
        alert("购物车为空，不能结算！");
        return false;
    }
    window.location.href="/cart/confirm";
}

/**
 * 随机生成字符串
 * @param len 指定生成字符串长度
 */
function getRandomString(len){
    let _charStr = 'abacdefghjklmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ0123456789',
        min = 0,
        max = _charStr.length-1,
        _str = '';                    //定义随机字符串 变量
    //判断是否指定长度，否则默认长度为15
    //len = len || 15;
    //循环生成字符串
    for(var i = 0, index; i < len; i++){
        index = (function(randomIndexFunc, i){
            return randomIndexFunc(min, max, i, randomIndexFunc);
        })(function(min, max, i, _self){
            let indexTemp = Math.floor(Math.random()*(max-min+1)+min),
                numStart = _charStr.length - 10;
            if(i==0&&indexTemp >=numStart){
                indexTemp = _self(min, max, i, _self);
            }
            return indexTemp ;
        }, i);
        _str += _charStr[index];
    }
    return _str;
}

window.key=getRandomString(16);

function encrypt(data) {
    // 定义前端Key秘钥-需要注意 跟后端解密秘钥保持一致
    //let key = 'uUXsN6okXYqsh0BB';
    let sKey = CryptoJS.enc.Utf8.parse(key);
    let encrypted = CryptoJS.AES.encrypt(data, sKey, {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7
    });
    return CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(encrypted.ciphertext.toString()));
}


var publicKey="";
var keySign="";
var CAKey="";
var ePublicKey="";
var eKeySign="";
var eCAKey="";

function settlementPost(){
    // let request = new XMLHttpRequest();
    // request.open("POST","http://172.20.74.30:80/confirm");
    // let request2= new XMLHttpRequest();
    // request.open("POST","/cart/commit");
    // // request.setRequestHeader("Origin","http://172.20.74.88:8080");
    // let formData = new FormData();
    // formData.append("totalCost",parseInt($("#totalprice").text().replace(/[^\d]/g, " ")));
    // formData.append("userNumber",$("#cardNumber").val());
    // formData.append("userPassword",$("#cardPassword").val());
    // formData.append("payee","");
    // request.send(formData);
    // let formData2 = new FormData();
    // formData2.append("userAddress",$("input[name='userAddress']:checked").val());
    // formData2.append("address",$("input[name='address']").val());
    // formData2.append("remark",$("input[name='remark']").val());
    // request2.send(formData2);
    // request.onreadystatechange=function(){
    //     if(request.readyState==4&&request.status==200){
    //         window.open("http://172.20.74.30:80/confirm2",'_blank');
    //     }
    // }
    //PI
    let totalCost=encrypt($("#totalprice").text().replace(/[^\d]/g, ""));
    let cardNumber= encrypt($("input[name='cardNumber']").val());
    let cardPassword=encrypt($("input[name='cardPassword']").val());
    let payee=encrypt("111111");
    //OI
    let userAddress=encrypt($("input[name='userAddress']:checked").val());
    let address= encrypt($("input[name='address']").val());
    let remark =encrypt($("input[name='remark']").val());
    //H(PI)
    let HPI=sha1(totalCost+cardNumber+cardPassword+payee);
    //H(OI)
    let HOI = sha1(userAddress+address+remark);
    //OP=H(OI)||H(PI)->H(OP)
    let HOP= sha1(HOI+HPI);
    //获取银行公钥
    // $.ajax(
    //     {
    //         url:"/RSA/generation",//CA
    //         data:{
    //             "userName":$("#userName")//能标识用户身份的信息
    //         },
    //         type:"post",
    //         dataType: "json",
    //         contentType:"application/json",
    //         success:function (data){
    //             window.publicKey=data;
    //         }
    //     }
    // )
    // let request2=new XMLHttpRequest();
    // request2.open("GET","RSA/generation");
    // request2.send();
    // request2.onreadystatechange=function (){
    //     if(request2.readyState==4&&request2.status==200){
    //         window.publicKey=request2.responseText
    //     }
    // }





    let request2=new XMLHttpRequest();
    request2.open("POST","http://10.144.78.44:8080/getSign");//CA的url
    let formData2 =new FormData();
    formData2.append("id","10001");
    request2.send(formData2);
    request2.onreadystatechange=function (){
        if(request2.readyState==4&&request2.status==200){
            let text=request2.responseText;
            text=JSON.parse(text);
            publicKey=text.pk;
            keySign=text.pkSign;
            CAKey=text.caPk;
            let request4=new XMLHttpRequest();
            request4.open("POST","/verify/ca");
            let formData4=new FormData();
            formData4.append("publicKey",publicKey);
            formData4.append("keySign",keySign);
            formData4.append("CAKey",CAKey);
            request4.send(formData4);
            request4.onreadystatechange=function (){
                if(request4.readyState==4&&request4.status==200){
                    let result1 = request4.responseText;
                    //sign(H(OP))
                    let signHOP=RSA_encrypt(HOP,publicKey);

                    //加密对称加密密钥
                    let encryptKey=RSA_encrypt(key,publicKey);

                    //获取商家密钥
                    let request3=new XMLHttpRequest();
                    request3.open("POST","http://10.144.78.44:8080/getSign");//CA的url
                    let formData3 =new FormData();
                    formData3.append("id","10002");
                    request3.send(formData3);
                    request3.onreadystatechange=function (){
                        if(request3.readyState==4&&request3.status==200){
                            let text1=request3.responseText;
                            text1=JSON.parse(text1);
                            ePublicKey=text1.pk;
                            eKeySign=text1.pkSign;
                            eCAKey=text1.caPk;
                            let request5=new XMLHttpRequest();
                            request5.open("POST","/verify/ca");
                            let formData5=new FormData();
                            formData5.append("publicKey",ePublicKey);
                            formData5.append("keySign",eKeySign);
                            formData5.append("CAKey",eCAKey);
                            request5.send(formData5);
                            request5.onreadystatechange=function (){
                                if(request5.readyState==4&&request5.status==200){
                                    let result2 = request5.responseText;
                                    //sign(H(OP))
                                    let eSignHOP=RSA_encrypt(HOP,ePublicKey);

                                    //加密对称加密密钥
                                    let eEncryptKey=RSA_encrypt(key,ePublicKey);

                                    //向银行发送数据
                                    // $.ajax({
                                    //     url: "http://10.144.254.182:80/confirm",   //请求的地址
                                    //     data: { // 提交数据 银行:PI H(OI) sign(H(OP))
                                    //         "totalCost":totalCost, // 前者为字段名，后者为数据
                                    //         "userNumber":cardNumber,
                                    //         "userPassword":cardPassword,
                                    //         "payee":payee,
                                    //         "HOI":HOI,
                                    //         "signHOP":signHOP,
                                    //         "encryptKey":encryptKey
                                    //     },
                                    //     cache: "false",   //设置为false将不会从浏览器中加载请求信息
                                    //     async: "true",    //true所有请求均为异步请求
                                    //     dataType: "json", //请求返回数据的格式
                                    //     type: "post",      //请求方式
                                    //     success: function (data) {  //请求成功后的回调方法
                                    //         alert(data);
                                    //         if(data == "yes"){
                                    //
                                    //         }else if(data == "no"){
                                    //             window.alert("支付失败");
                                    //             window.location.reload();
                                    //         }
                                    //     }
                                    //
                                    // });
                                    let request6 = new XMLHttpRequest();
                                    // request6.open("POST","http://10.144.254.182:80/confirm");
                                    request6.open("POST","http://10.144.235.206:80/confirm");
                                    let formData6 = new FormData();
                                    formData6.append("totalCost",totalCost);
                                    formData6.append("userNumber",cardNumber);
                                    formData6.append("userPassword",cardPassword);
                                    formData6.append("payee",payee);
                                    formData6.append("HOI",HOI);
                                    formData6.append("signHOP",signHOP);
                                    formData6.append("encryptKey",encryptKey);
                                    request6.send(formData6);
                                    request6.onreadystatechange=function (){
                                        if(request6.readyState==4&&request6.status==200){
                                            let result3=request6.responseText;
                                            if(result3=="yes"){
                                                let request = new XMLHttpRequest();
                                                request.open("POST","/cart/commit");
                                                let formData = new FormData();
                                                formData.append("userAddress",userAddress);
                                                formData.append("address",address);
                                                formData.append("remark",remark);
                                                formData.append("HPI",HPI);
                                                formData.append("eSignHOP",eSignHOP);
                                                formData.append("eEncryptKey",eEncryptKey);
                                                request.send(formData);
                                                request.onreadystatechange=function(){
                                                    if(request.readyState==4&&request.status==200){
                                                        // window.location.replace("/cart/commit1");
                                                        alert("感谢您在本店购物！您的订单已提交成功！");
                                                        window.location.replace("/productCategory/main");
                                                    }
                                                }
                                            }else if(result3=="cardNum_error"){
                                                alert("银行卡号错误！");
                                            }else if(result3=="balance_error"){
                                                alert("余额不足！");
                                            }else if(result3=="no"){
                                                window.alert("支付失败");
                                                window.location.reload();
                                            }
                                        }
                                    }


                                    if(result2=="no"){
                                        alert("公钥验证失败");
                                    }
                                }
                            }
                        }
                    }
                    if(result1=="no"){
                        alert("公钥验证失败");
                    }
                }
            }
        }
    }

    // $.ajax({
    //     url:"http://192.168.193.35:8080/getSign",
    //     type:"post",
    //     data:{
    //         "id":"10001"
    //     },
    //     dataType: "json",
    //     success:function(data){
    //         window.publicKey=data.pk;
    //         window.keySign=data.pkSign;
    //     }
    // })


















    // //sign(H(OP))
    // let signHOP=RSA_encrypt(HOP,publicKey);
    //
    // //加密对称加密密钥
    // let encryptKey=RSA_encrypt(key,publicKey);
    //
    // //获取商家密钥
    // let request3=new XMLHttpRequest();
    // request3.open("POST","http://192.168.193.35:8080/getSign");//CA的url
    // let formData3 =new FormData();
    // formData3.append("id","10002");
    // request3.send(formData3);
    // request3.onreadystatechange=function (){
    //     if(request3.readyState==4&&request3.status==200){
    //         let text1=request3.responseText;
    //         text1=JSON.parse(text1);
    //         ePublicKey=text1.pk;
    //         eKeySign=text1.pkSign;
    //         eCAKey=text1.caPk;
    //         let request5=new XMLHttpRequest();
    //         request5.open("POST","/verify/ca");
    //         let formData5=new FormData();
    //         formData5.append("publicKey",ePublicKey);
    //         formData5.append("keySign",eKeySign);
    //         formData5.append("CAKey",eCAKey);
    //         request5.send(formData5);
    //         request5.onreadystatechange=function (){
    //             if(request5.readyState==4&&request5.status==200){
    //                 let result = request5.responseText;
    //                 if(result=="no"){
    //                     alert("公钥验证失败");
    //                 }
    //             }
    //         }
    //     }
    // }
    //
    // // $.ajax({
    // //     url:"http://192.168.193.35:8080/getSign",
    // //     type:"post",
    // //     data:{
    // //         "id":"10001"
    // //     },
    // //     dataType: "json",
    // //     success:function(data){
    // //         window.publicKey=data.pk;
    // //         window.keySign=data.pkSign;
    // //     }
    // // })
    //
    // //sign(H(OP))
    // let eSignHOP=RSA_encrypt(HOP,ePublicKey);
    //
    // //加密对称加密密钥
    // let eEncryptKey=RSA_encrypt(key,ePublicKey);
    //
    // //向银行发送数据
    // $.ajax({
    //     url: "http://192.168.193.64:8080/confirm",   //请求的地址
    //     data: { // 提交数据 银行:PI H(OI) sign(H(OP))
    //         "totalCost":totalCost, // 前者为字段名，后者为数据
    //         "userNumber":cardNumber,
    //         "userPassword":cardPassword,
    //         "payee":payee,
    //         "HOI":HOI,
    //         "signHOP":signHOP,
    //         "encryptKey":encryptKey
    //     },
    //     cache: "false",   //设置为false将不会从浏览器中加载请求信息
    //     async: "true",    //true所有请求均为异步请求
    //     dataType: "json", //请求返回数据的格式
    //     type: "post",      //请求方式
    //     success: function (data) {  //请求成功后的回调方法
    //        if(data == "yes"){
    //            let request = new XMLHttpRequest();
    //            request.open("POST","/cart/commit");
    //            let formData = new FormData();
    //            formData.append("userAddress",userAddress);
    //            formData.append("address",address);
    //            formData.append("remark",remark);
    //            formData.append("HPI",HPI);
    //            formData.append("signHOP",eSignHOP);
    //            formData.append("encryptKey",eEncryptKey);
    //            request.send(formData);
    //            request.onreadystatechange=function(){
    //                if(request.readyState==4&&request.status==200){
    //                window.location.href="/cart/commit";
    //                }
    //            }
    //     }else if(data == "no"){
    //            window.alert("支付失败");
    //            window.location.reload();
    //        }
    //            }
    //
    // });


    }