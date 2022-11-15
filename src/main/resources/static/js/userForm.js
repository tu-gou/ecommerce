function registerPost(){
    let request =new XMLHttpRequest();
    request.open("POST","/user/register");
    let formData =new FormData();
    formData.append("loginName",$(".l_user").val().toString());
    formData.append("password",$(".l_pwd").val().toString());
    formData.append("userName",$("#l_userName").val().toString());
    formData.append("gender",$("input[name='gender']:checked").val());
    formData.append("mobile",$(".l_tel").val().toString());
    request.send(formData);
}

function loginPost(){
    let request=new XMLHttpRequest();
    request.open("POST","/user/login");
    let formData = new FormData();
    formData.append("loginName",$(".loginName").val().toString());
    formData.append("password",$("#loginName").val().toString());
    request.send(formData);
}