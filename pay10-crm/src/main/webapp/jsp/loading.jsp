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
/* #loader:before, #loader:after{
  content: "";
  display: block;
  position: relative;
  left: 50%;
  top: 50%;
  width: 100%;
  height: 100%;
  left: calc(50% - 10px);
  z-index: 1001;

  background:linear-gradient(120deg, #1d28f5, #fbb713) !important;
  animation: squaremove 1s ease-in-out infinite;
} */

.loader{
  width: 80px;
  height: 80px;
  border-radius: 100%;
  position: relative;
  margin: 0 auto;
}

/* LOADER 1 */

#loader:before, #loader-1:after{
  content: "";
  position: absolute;
  top: -10px;
  left: -10px;
  width: 100%;
  height: 100%;
  border-radius: 100%;
  border: 10px solid transparent;
  border-top-color: #1d28f5;
  border-bottom-color: #7bc2f6;

}

#loader:before{
  z-index: 100;
  animation: spin 2s infinite;
}

#loader:after{
  border: 10px solid #ccc;
}

@keyframes spin{
  0%{
    -webkit-transform: rotate(0deg);
    -ms-transform: rotate(0deg);
    -o-transform: rotate(0deg);
    transform: rotate(0deg);
  }

  100%{
    -webkit-transform: rotate(360deg);
    -ms-transform: rotate(360deg);
    -o-transform: rotate(360deg);
    transform: rotate(360deg);
  }
}
/* 
Ok so you have made it this far, that means you are very keen to on my code. 
Anyway I don't really mind it. This is a great way to learn so you actually doing the right thing:)
Follow me @ihatetomatoes
*/

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
  <div class="loader">
    <div id="progress" >
    <!-- <img src="../image/sand-clock-loader.gif"> -->
    <div id="loader"></div>
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