<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add Acquirer</title>


<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
<!--begin::Fonts-->
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
<!--end::Fonts-->
<!--begin::Vendor Stylesheets(used by this page)-->
<link
	href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css"
	rel="stylesheet" type="text/css" />

<!--end::Vendor Stylesheets-->
<!--begin::Global Stylesheets Bundle(used by all pages)-->
<link href="../assets/plugins/global/plugins.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
	type="text/css" />

<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>





<%-- <link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />

<!-- <link href="../css/default.css" rel="stylesheet" type="text/css" /> -->

<script type="text/javascript" src="../js/jquery.min.js"></script>
<script src="../js/jquery.minshowpop.js"></script>
<script src="../js/jquery.formshowpop.js"></script>
<script src="../js/commanValidate.js"></script>
<!--  loader scripts -->
<script src="../js/loader/modernizr-2.6.2.min.js"></script>
<script src="../js/loader/main.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
 
<!--<link rel="stylesheet" href="../css/loader/normalize.css" />
<link rel="stylesheet" href="../css/loader/main.css" />
<link rel="stylesheet" href="../css/loader/customLoader.css" /> -->
<script src="../assets/js/scripts.bundle.js"></script> --%>
<script language="JavaScript">	
$(document).ready( function () {
	
	$('#btnEditUser').click(function() {	
	    var fname = document.getElementById('fname').value;
	    var lname = document.getElementById('lname').value;
	    var bname = document.getElementById('bname').value;
	    var EmailIdInpt = document.getElementById('EmailIdInpt').value;
	 

	    if(fname){
	      $('.frstNameError').hide();
	    }else{
	      $('.frstNameError').show();
	      return false;
	    }

	    if(lname){
	      $('.lstNameError').hide();
	    }else{
	      $('.lstNameError').show();
	      return false;
	    }

	    if(bname){
	      $('.businessNameError').hide();
	    }else{
	      $('.businessNameError').show();
	      return false;
	    }

	    if(isValidEmail()){
	      $('.emailIdError').hide();
	    }else{
	      $('.emailIdError').show();
	      return false;
	    }
		/* $('#loader-wrapper').show(); */

		      });
});
$(function() {
	$("#userGroupId").change(function() {
		var groupId = this.value;
		var token  = document.getElementsByName("token")[0].value;
		$.ajax({
			type : "POST",
			url : "roleByGroup",
			data : {
				groupId : groupId,
				token:token,
				"struts.token.name": "token"
			},
			success : function(data, status) {
				document.getElementById("roleIdDiv").innerHTML=data;
			},
			error : function(status) {
				//alert("Network error please try again later!!");
				return false;
			}
		});
	});
});
</script>
<!-- search -->
<script type="text/javascript">
	$(document).ready(function () {
	$("#userGroupId").select2();

	
		});
</script>

<style type="text/css">.error-text{color:#a94442;font-weight:bold;background-color:#f2dede;list-style-type:none;text-align:center;list-style-type: none;margin-top:10px;
}.error-text li { list-style-type:none; }
#response{color:green;}
.errorMessage{
  display: none;
}
.errorInpt{
      font: 400 11px arial ;
      color: red;
      display: none;
      margin-left: 7px;
}
.fixHeight{
  height: 10px;
}
.adduT{
  margin-bottom: 0 !important;
}
.btnSbmt{
  padding: 5px 10px !important;
    margin-right: 26px !important;
}
.actionMessage {
    border: 1px solid transparent;
    border-radius: 0 !important;
    width: 100% !important;
    margin: 0 !important;

}
.btn:focus{
		outline: 0 !important;
	}
</style>
</head>
<body>

<div class="content d-flex flex-column flex-column-fluid" id="kt_content">

<div class="toolbar" id="kt_toolbar">
							<!--begin::Container-->
							<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
								<!--begin::Page title-->
								<div data-kt-swapper="true" data-kt-swapper-mode="prepend" data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}" class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
									<!--begin::Title-->
									<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Add New Acquirer</h1>
									<!--end::Title-->
									<!--begin::Separator-->
									<span class="h-20px border-gray-200 border-start mx-4"></span>
									<!--end::Separator-->
									<!--begin::Breadcrumb-->
									<ul class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
										<!--begin::Item-->
										<li class="breadcrumb-item text-muted">
											<a href="home" class="text-muted text-hover-primary">Dashboard</a>
										</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item">
											<span class="bullet bg-gray-200 w-5px h-2px"></span>
										</li>
										<!--end::Item-->
                                        <!--begin::Item-->
										<li class="breadcrumb-item text-muted">Manage Acquirers</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item">
											<span class="bullet bg-gray-200 w-5px h-2px"></span>
										</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item text-dark">Add New Acquirer</li>
										<!--end::Item-->
									</ul>
									<!--end::Breadcrumb-->
								</div>
								<!--end::Page title-->
								
							</div>
							<!--end::Container-->
						</div>
   <!-- <div id="loader-wrapper" style="width: 100%; height: 100%; display:none;">
	  <div id="loader"></div>
    </div> -->
    <div class="post d-flex flex-column-fluid" id="kt_post">
							<!--begin::Container-->
							<div id="kt_content_container" class="container-xxl">								
                                <div class="row my-5">
                                    <div class="col">
                                      <div class="card">
                                        <div class="card-body">
                                         <div class="row g-9 mb-8">
<table width="100%" border="0" cellspacing="0" cellpadding="0" class="txnf">
  <!-- <tr>
    <td align="left"><h2>Add New Acquirer</h2></td>
  </tr> -->
  
  
  
  <s:if test="%{responseObject.responseCode=='000'}">
   <tr>
    <td align="left" valign="top"><div id="saveMessage">
        <s:actionmessage class="success success-text" />
      </div></td>
  </tr>
  
  </s:if>
<s:else><div class="error-text"><s:actionmessage/></div></s:else>

  <tr>
    <td align="left" valign="top"><div class="addu">
    <div class="post d-flex flex-column-fluid" id="kt_post">

                <!--begin::Container-->

                <div id="kt_content_container" class="container-xxl">
        <s:form action="addAcquirer" id="frmAddUser" >
          <s:token/>
          <div class="card ">
<!--             <div class="card-header card-header-rose card-header-icon"> -->
<!--               <div class="card-icon"> -->
<!--                 <i class="material-icons">mail_outline</i> -->
<!--               </div> -->
<!--               <h4 class="card-title" style="
<!--               color: #0271bb; -->
<!--               font-weight: 500;">Add New Acquirer</h4> -->
<!--             </div> -->
<div class="post d-flex flex-column-fluid" id="kt_post">

                <!--begin::Container-->

                <div id="kt_content_container" class="container-xxl">

                    <div class="row my-5">

                        <div class="col">

                            <div class="card">

                                <div class="card-body">
            <div class="card-body ">
              <div class="row g-9 mb-8 justify-content-center">
              <div class="col-6">
                <label  class="d-flex align-items-center fs-6 fw-semibold mb-2"> First Name<span style="color:red; margin-left:3px;">*</span></label>
                <s:textfield name="firstName"  class="  form-control form-control-solid" id = "fname"  autocomplete="off" onkeypress="noSpace(event,this);return isCharacterKey(event);"  onkeyup="removeError()"/>
              <!-- </div> -->
              <br>
                    <!-- <div class="fixHeight"> -->
                    <p id="fNameError" class="errorInpt frstNameError" style="margin-left: 3px !important;">Please Enter First Name.</p>
                    <!-- </div> -->
                    <br>
             <!--  <div class="row g-9 mb-8 justify-content-end"> -->
                <label class="d-flex align-items-center fs-6 fw-semibold mb-2"> Last Name<span style="color:red; margin-left:3px;">*</span></label>
                <s:textfield	name="lastName" id = "lname"  class="  form-control form-control-solid"  autocomplete="off"  onkeypress="noSpace(event,this);return isCharacterKey(event);" onkeyup="secondError()"/>
             <!--  </div> -->
             <br>
              <!-- <div class="row g-9 mb-8 justify-content-end"> -->
              <p id="lNameError" class="errorInpt lstNameError">Please Enter Last Name.</p>
              <!-- </div> -->
              <br>
             <!--  <div class="row g-9 mb-8 justify-content-end"> -->
                <label  class="d-flex align-items-center fs-6 fw-semibold mb-2"> Business Name<span style="color:red; margin-left:3px;">*</span></label>
                <s:textfield name="businessName" id="bname"   class="form-control form-control-solid" autocomplete="off" onkeyup="thirdError()"/>
              <!-- </div> -->
              <br>
             <!--  <div class="row g-9 mb-8 justify-content-end"> -->
              <p id="businessError" class="errorInpt businessNameError">Please Enter Business Name.</p>
              <!-- </div> -->
              <br>
               <!--  <div class="row g-9 mb-8 justify-content-end"> -->
                  <label  class="d-flex align-items-center fs-6 fw-semibold mb-2"> Email Id<span style="color:red; margin-left:3px;">*</span></label>
                  <s:textfield name="emailId" id="EmailIdInpt"  class="form-control form-control-solid" autocomplete="off" onkeypress="isValidEmail()" onkeyup="fourthError()"/>
              <!--   </div> -->
              <br>
               <!--  <div class="row g-9 mb-8 justify-content-end"> -->
                <p id="emailError" class="errorInpt emailIdError">Please Enter Valid Email Id.</p>
               <!--  </div> -->
               <br>

				<!-- <div class="row g-9 mb-8 justify-content-end"> -->
					<label class="d-flex align-items-center fs-6 fw-semibold mb-2">User Group</label>
					<div class="txtnew">
						<s:select name="userGroupId" class="form-select form-select-solid" id="userGroupId"
								headerKey="" headerValue="Select User Group" list="userGroups"
								listKey="id" listValue="group" autocomplete="off" />
					</div>
			  <!--  </div> -->
			  <br>
				
				<div class="row g-9 mb-8 justify-content-end" id="roleIdDiv">
			   </div>
			   </div>
				</div>
              
            </div>
            <div class="card-footer text-right">
            
              <s:submit  id="btnEditUser" name="btnEditUser" value="Save" class="btn  btn-primary"> </s:submit>
            </div>

      
            
           
          </div>
          </div>
          </div>
          </div>
          </div>
          </div>
          </div>
          
            
            <s:hidden name="token" value="%{#session.customToken}"></s:hidden>
        </s:form>
        <div class="clear"></div>
          </div>
          
          
          </td>
  </tr>
  <tr>
    <td align="left" valign="top">&nbsp;</td>
  </tr>
</table>
</div>
</div>
</div>
</div>
</div>
</div>
</div>

</div>
<script type="text/javascript">
  function isValidEmail() {
    var emailexp = /^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[_A-Za-z0-9-]+)$/;
    var emailValue = document.getElementById("EmailIdInpt").value;
      if (emailValue.trim() && emailValue.match(emailexp)) {
		  document.getElementById("emailError").style.display = "none";
              return true;
      } else{
          document.getElementById("emailError").style.display = "block";
      }
  }
</script>

<script>
function removeError(){
	var firstValue = document.getElementById("fname").value;
	if(firstValue.length >0){
		document.getElementById("fNameError").style.display = "none";
	}
	else{
		document.getElementById("fNameError").style.display = "block";
	}
}

function secondError(){
	var lastValue = document.getElementById("lname").value;
	if(lastValue.length > 0){
		document.getElementById("lNameError").style.display = "none";
	}
	else{
		document.getElementById("lNameError").style.display = "block";
	}
}

function thirdError(){
	var businessValue = document.getElementById("bname").value;
	if(businessValue.length > 0){
		document.getElementById("businessError").style.display = "none";
	}
	else{
		document.getElementById("businessError").style.display = "block";
	}
}
</script>
</body>
</html>