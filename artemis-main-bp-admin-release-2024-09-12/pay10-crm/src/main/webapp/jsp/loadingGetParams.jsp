<!DOCTYPE html>
<%@taglib prefix="s" uri="/struts-tags"%>
<html>

<head>

<title>Loading...</title>
<meta http-equiv="refresh" content="1; url=<s:url />"></meta> 
	<!-- <link rel="stylesheet" href="../css/loader/normalize.css">
	<link rel="stylesheet" href="../css/loader/main.css">
	<link rel="stylesheet" href="../css/loader/customLoader.css">
	<script src="../js/loader/modernizr-2.6.2.min.js"></script> -->

<style>
#progress{
  text-align: center;
  margin:300px auto;
}
  
</style>
<script>
function refresh(){
	   //update src attribute with a cache buster query
	   location.reload(true);
	   setTimeout("refresh();",1000)
	}
	</script>
</head>
 
 <body onload="">
 
 
  <div id="loader-wrapper">
  <div class="loader" >
    <div id="progress" >
    <img src="../image/sand-clock-loader.gif">
  </div>
  </div>
  </div>
  
  <!-- <h4>Loader #5</h4> -->
 
<!--  <div class="loader">Loading...</div> -->
<!-- <div id="progress" style="align-content=center;" >
   <img src="ripple.gif"/>
</div> -->

</body>
</html>