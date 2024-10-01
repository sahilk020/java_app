<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Thank You</title>
<style type="text/css">
@import url('https://fonts.googleapis.com/css?family=Open+Sans:300,400,600,700,800');
body{
  font-family: 'Open Sans', sans-serif;
}
.aeroplane{
  position: absolute;
  bottom: 0;
  right: 0;
  width: 400px;
  z-index: -1;
}
.train{
  position: absolute;
  bottom: 0;
  left: 0;
  width: 406px;
  z-index: -1;
}
.trackBox{
  width: 871px;
    height: 413px;
    background-size: 100%;
    border: 2px solid #3190e9;;
    border-radius: 93px;
    /* background: url(../image/track-bg.png) no-repeat center top; */
    margin: 4% auto 0;
    text-align: center;
}
.trackBox img{
  display: block;
  margin: 0 auto;
}
.trackBox img.irctcLogo{
  padding-top: 47px;
  margin-bottom: 18px;
}
.trackBox h1{
  font-size: 40px;
  /* font-weight: 800; */
  margin-bottom: 0;
  letter-spacing: 3px;
}
.trackBox h2{
  font-size: 25px;
  color: #454545;
  /* font-weight: 800; */
  margin-top: 8px;
  letter-spacing: 3px;
}
.trackBox p{
      font-size: 15px;
    color: #979797;
    font-weight: 600;
}
.trackBox p a{
  color: #357ebd;
  text-decoration: none;
}
.trackBox p a:hover{
  opacity: 0.8;
}
</style>
<script>
  if (self == top) {
    var theBody = document.getElementsByTagName('body')[0];
    theBody.style.display = "block";
  } else {
    top.location = self.location;
  }
</script>

</head>
<body>
<div class="trackBox">
  <img class="irctcLogo" src="../image/66x46.png"/>
  <img src="../image/ok.png"/>
  <h1>THANK YOU !</h1>
  <h2>For Registering With Pay10.</h2>
  <p>To activate your account, please click on the link in activation email which has been sent to you.</p>
  <p>If you have not received the activation email, contact us at <a href="mailto:noreply@pay10.com">noreply@pay10.com</a></p>
 <s:a action="index" style="color:#3190e9;">Back to Login</s:a>
</div>
<!-- <img class="train" src="../image/train.png"/>
<img class="aeroplane" src="../image/aeroplane.png"/> -->
</body>
</html>