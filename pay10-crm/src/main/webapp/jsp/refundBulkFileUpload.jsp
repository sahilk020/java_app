<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Upload Bulk Refund File</title>


<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
		<!--begin::Fonts-->
		<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
		<!--end::Fonts-->
		<!--begin::Vendor Stylesheets(used by this page)-->
		<!-- <link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
						type="text/css" /> -->
		<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
		<!--end::Vendor Stylesheets-->
		<!--begin::Global Stylesheets Bundle(used by all pages)-->
		<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />

		<script src="../assets/plugins/global/plugins.bundle.js"></script>
		<script src="../assets/js/scripts.bundle.js"></script>
		<script src="../js/jquery.popupoverlay.js"></script>


		<link href="../css/select2.min.css" rel="stylesheet" />
		<script src="../js/jquery.select2.js" type="text/javascript"></script>

<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery.popupoverlay.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>


<%-- 
<link href="../css/jquery-ui.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="../js/jquery.min.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery.popupoverlay.js"></script>
<link rel="stylesheet" type="text/css" href="../css/popup.css" />
<link href="../css/bootstrap-toggle.min.css" rel="stylesheet">
<script src="../js/bootstrap-toggle.min.js"></script>
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>
<!--  loader scripts -->
<script src="../js/loader/modernizr-2.6.2.min.js"></script>
<script src="../js/loader/main.js"></script>
<link rel="stylesheet" href="../css/loader/normalize.css" />
<link rel="stylesheet" href="../css/loader/main.css" />
<link rel="stylesheet" href="../css/loader/customLoader.css" />
<!--------PDF scripts----->
<script src="../js/html2canvas.min.js"></script>
<script src="../js/jspdf.debug.js"></script> --%>

<style>
.switch {
  position: relative;
  display: inline-block;
  width: 30px;
  height: 17px;
}

.switch input {
  display: none;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  -webkit-transition: .4s;
  transition: .4s;
}

.slider:before {
  position: absolute;
  content: "";
  height: 13px;
  width: 13px;
  left: 2px;
  bottom: 2px;
  background-color: white;
  -webkit-transition: .4s;
  transition: .4s;
}

input:checked+.slider {
  background-color: #2196F3;
}

input:focus+.slider {
  box-shadow: 0 0 1px #2196F3;
}

input:checked+.slider:before {
  -webkit-transform: translateX(13px);
  -ms-transform: translateX(13px);
  transform: translateX(13px);
}
/* Rounded sliders */
.slider.round {
  border-radius: 17px;
}

.slider.round:before {
  border-radius: 50%;
}

.mycheckbox {
  /* Your style here */
  
}

.switch {
  display: table-cell;
  vertical-align: middle;
  padding: 10px;
}

input.cmn-toggle-jwr:checked+label:after {
  margin-left: 1.5em;
}

table .toggle.btn {
  min-width: 48px;
  min-height: 28px;
}

table .btn {
  /* margin-bottom: 4px; */
  /* margin-right: 5px; */
  /* padding: 1px 12px;
    font-size: 11px; */
  
}

table .toggle-off.btn {
  padding: 0;
  margin: 0;
}
</style>


<script type="text/javascript">
  
</script>

<style>
#loading {
  width: 100%;
  height: 100%;
  top: 0px;
  left: 0px;
  position: fixed;
  display: block;
  z-index: 99
}

#loading-image {
  position: absolute;
  top: 40%;
  left: 55%;
  z-index: 100;
  width: 10%;
}
.error {
    font-family: "Times New Roman";
    color: red;
    width: 100%;
    margin-top: 8px;
    }

</style>
</head>
<body >

<div class="content d-flex flex-column flex-column-fluid"
					id="kt_content">
					<div class="post d-flex flex-column-fluid" id="kt_post">
							<!--begin::Container-->
							<div id="kt_content_container" class="container-xxl">								
                                <div class="row my-5">
                                    <div class="col">
                                      <div class="card">
                                         <div class="card-body"> 
<div class="toolbar" id="kt_toolbar">
				<!--begin::Container-->
				<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
					<!--begin::Page title-->
					<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
						data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
						class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
						<!--begin::Title-->
						<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Upload Bulk Refund File</h1>
						<!--end::Title-->
						<!--begin::Separator-->
						<span class="h-20px border-gray-200 border-start mx-4"></span>
						<!--end::Separator-->
						<!--begin::Breadcrumb-->
						<ul class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
							<!--begin::Item-->
							<li class="breadcrumb-item text-muted"><a href="home"
									class="text-muted text-hover-primary">Dashboard</a>
							</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span>
							</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item text-muted">Account & Finance</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span>
							</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item text-dark">Upload Bulk Refund File</li>
							<!--end::Item-->
						</ul>
						<!--end::Breadcrumb-->
					</div>
					<!--end::Page title-->

				</div>
				<!--end::Container-->
			</div>




<s:form action="refundBulkFileUpload" id="form">
<s:hidden name="id" id = "id" />
<input type="hidden" name="email" id="email" value="<s:property value='%{#session.USER.emailId}'/>" />		

    <input type="file" name="file" id="file" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
  <button type="button" class="btn btn-primary btn-xs" style="margin-top:20px;"
      name="submitFile" id="submitFile" onClick="saveFile()">Bulk Refund Upload</button>
      <span id= "excelError" class ="error"></span>
</s:form>
   </div>
   </div></div>
   </div></div>
   </div>
   </div>
  
  
  <script type="text/javascript">`
  
    $(document).ready(function() {  
     scrollX=true;
     scrollY=true;
    });
  </script>
 

<script>


 function saveFile(){
	 var urls = new URL(window.location.href);
	 var domain = urls.origin;
   var file= $('#file').val();
   if (!(/\.(xlsx|xls|xlsm)$/i).test(file)) {
       alert('Please upload valid excel file .xlsx, .xlsm, .xls only.');
       $(file).val('');
   }
   else{
     
   var form = $('#form')[0];
   var data = new FormData(form);

   $.ajax({
      url: domain+"/crmws/refund/upload/bulk",
      type: 'POST',
      enctype: 'multipart/form-data',
      data: data,
        processData: false,
          contentType: false,
          cache: false,
          success: function (data) {
              alert(data);
              window.location.reload();
          },
          error: function(data, textStatus, jqXHR) {
        	  
  			if(data.responseText && JSON.parse(data.responseText).respmessage){
  				var responseText =JSON.parse(data.responseText) 
  				alert(responseText.respmessage);
  			}else{
  				alert("Error while uploading Refund Bulk file");
  			}
  			window.location.reload();
  			
  		}
    });  
   }
 }
</script>
</body>
</html>