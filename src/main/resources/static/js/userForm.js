// function addScript(url) {
//     var script = document.createElement('script');
//     script.setAttribute('type', 'text/javascript');
//     script.setAttribute('src', url);
//     document.getElementsByTagName('head')[0].appendChild(script);
// }

function encrypt(data) {
    // 定义前端Key秘钥-需要注意 跟后端解密秘钥保持一致
    let key = 'uUXsN6okXYqsh0BB';
    key = CryptoJS.enc.Utf8.parse(key);
    let encrypted = CryptoJS.AES.encrypt(data, key, {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7
    });
    return CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(encrypted.ciphertext.toString()));
}

function registerPost() {
    //addScript("./crypto-js.min");
    let request = new XMLHttpRequest();
    request.open("POST", "/user/register");
    let formData = new FormData();

    //AES加密
    let loginName = encrypt($(".l_user").val());
    let password = encrypt($(".l_pwd").val());
    let userName = encrypt($("#l_userName").val());
    let gender = encrypt($("input[name='gender']:checked").val());
    let mobile = encrypt($(".l_tel").val());

    //SHA1散列
    let SHA1LoginName = sha1(loginName);
    let SHA1Password = sha1(password);
    let SHA1UserName =sha1(userName);
    let SHA1Gender = sha1(gender);
    let SHA1Mobile = sha1(mobile);


    formData.append("loginName", loginName);
    formData.append("password", password);
    formData.append("userName", userName);
    formData.append("gender", gender);
    formData.append("mobile", mobile);

    formData.append("SHA1LoginName",SHA1LoginName);
    formData.append("SHA1Password",SHA1Password);
    formData.append("SHA1UserName",SHA1UserName);
    formData.append("SHA1Gender",SHA1Gender);
    formData.append("SHA1Mobile",SHA1Mobile);


    request.send(formData);
    request.onreadystatechange = function () {
        if (request.readyState == 4 && request.status == 200) {
            window.location.href = "/login";
        }
    }
}

function loginPost() {

    //addScript("js/crypto-js.min.js");

    let request = new XMLHttpRequest();
    //request.open("POST", "/user/login");
    request.open("POST","/user/authentication0")
    let formData = new FormData();

    let loginName = encrypt($(".l_user").val());
    let password = encrypt($("#password").val());

    let SHA1LoginName=sha1(loginName);
    let SHA1Password=sha1(password);


    formData.append("loginName", loginName);
    formData.append("password", password);

    formData.append("SHA1LoginName",SHA1LoginName);
    formData.append("SHA1Password",SHA1Password);



    request.send(formData);
    request.onreadystatechange = function () {
        if (request.readyState == 4 && request.status == 200) {
            let data=request.responseText;
            console.log(data);
            if(data.includes("no")){
                window.alert("用户名或密码错误!");
                window.location.reload();
            }else{
                let temp=SHA1Password+data;
                let digest=sha1(temp);
                let request2=new XMLHttpRequest();
                request2.open('POST','/authentication1')
                let formData2=new FormData();
                formData2.append('digest',digest);
                request2.send(formData2);
                request2.onreadystatechange=function(){
                    let data2=request2.responseText;
                    if(data2.includes("no")){
                        window.alert("登录失败,请重试");
                        window.location.reload();
                    }else{
                        window.location.replace("/productCategory/main");
                    }
                }
            }
        }
    }


    // $.ajax({
    //     url:"/user/login",
    //     type:"post",
    //     data:JSON.stringify({"loginName":$(".l_user").val(),"password": $("#password").val()}),
    //     dataType:"json",
    //     contentType:"application/json",
    //     success:function (data) {
    //         alert("-------------")
    //     }
    // })
    //

    // /**
    //  * 处理 ajax 302 错误问题
    //  */
    // $.ajaxSetup({
    //     //请求方式，默认为get
    //     type:'post',
    //     //请求成功后触发
    //     success: function () {
    //         console.log('成功')
    //     },
    //     //请求失败遇到异常触发
    //     error: function () {
    //         console.log('失败')
    //     },
    //     //完成请求后触发。即在success或error触发后触发
    //     complete: function (XMLHttpRequest,status) {
    //         if(status === 'error'){
    //             // 关键在这里，如果返回值为error那么久直接跳转到指定页面
    //             // 这里使用top可以解决iframe嵌套之后，父页面不跳转的情况
    //             top.location.href = "/productCategory/main";
    //         }
    //     },
    // })
}