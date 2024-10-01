<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<head>
<title>AuditTrail Report</title>

<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" media="all"
	href="../css/daterangepicker-bs3.css" />
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<script src="../js/jquery.min.js" type="text/javascript"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script src="../js/moment.js" type="text/javascript"></script>
<script src="../js/daterangepicker.js" type="text/javascript"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>
<script src="../js/commanValidate.js"></script>
<script src="../js/jquery.popupoverlay.js"></script>
<script src="../js/dataTables.buttons.js" type="text/javascript"></script>
<script src="../js/pdfmake.js" type="text/javascript"></script>
<script src="../js/diff.js" type="text/javascript"></script>

<script src="../js/jszip.min.js" type="text/javascript"></script>
<script src="../js/vfs_fonts.js" type="text/javascript"></script>
<script src="../js/buttons.colVis.min.js" type="text/javascript"></script>

<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>
<!--  loader scripts -->
<script src="../js/loader/modernizr-2.6.2.min.js"></script>
<script src="../js/loader/main.js"></script>
<link rel="stylesheet" href="../css/loader/normalize.css" />
<link rel="stylesheet" href="../css/loader/main.css" />
<link rel="stylesheet" href="../css/loader/customLoader.css" />
<style>
ins {
	text-decoration: none;
	background-color: #d4fcbc;
	overflow-wrap: break-word;
}

del {
	text-decoration: line-through;
	background-color: #fbb6c2;
	overflow-wrap: break-word;
	color: #555;
}
</style>
<script type="text/javascript">
$(document).ready(function(){
   document.getElementById("loadingInner").style.display = "none";
   document.getElementById("loading").style.display = "none";
});
</script>
</head>
<body id="mainBody">
<div id="loadingInner" display="none">
		<img id="loading-image-inner" src="../image/sand-clock-loader.gif"
			alt="BUSY..." />
	</div>
	<div id="loading" style="text-align: center;">
		<img id="loading-image" style="width: 70px; height: 70px;"
			src="../image/sand-clock-loader.gif" alt="" />
	</div>
	<s:hidden name="diffId" id="diffId" value="%{diffId}" />
	<s:hidden name="diffStatusApiUrl" id="diffStatusApiUrl" value="%{diffStatusApiUrl}" />
	<div id="innerDiv"></div>

<script type="text/javascript">

$(document).ready(function() {
	var count = 0;
	window.setInterval(function(){
		if (count == 1) {
		var id = document.getElementById("diffId").value;
		var diffStatusUrl = document.getElementById("diffStatusApiUrl").value;
			window.location.href = diffStatusUrl + id;
			window.location.reload();
		}
		if (document.getElementById('innerDiv').innerHTML=='') {
			invokeStatusCheckApi();
			count++;
		}
	}, 10000);
});

function invokeStatusCheckApi() {
		document.getElementById("loadingInner").style.display = "block";
		var id = document.getElementById("diffId").value;
		var diffStatusUrl = document.getElementById("diffStatusApiUrl").value;
		$.ajax({
			type : "GET",
			url : diffStatusUrl + id,
			success : function(data, status) {
				document.getElementById("innerDiv").innerHTML = data;
				document.getElementById("loadingInner").style.display = "none";
				setTimeout(setData(diffStatusUrl + id), 10000);
			},
			error : function(status) {
				//alert("Network error please try again later!!");
				return false;
			}
		});
	}
	
	function setData(url) {
		window.open(url);
	}

</script>
</body>
</html>