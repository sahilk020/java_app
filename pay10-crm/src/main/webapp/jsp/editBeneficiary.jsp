<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Edit Beneficiary</title>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<script src="../js/jquery.minshowpop.js"></script>
<script src="../js/jquery.formshowpop.js"></script>
<script src="../js/commanValidate.js"></script>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" media="all" href="../css/daterangepicker-bs3.css" />
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
<script src="../js/jszip.min.js" type="text/javascript"></script>
<script src="../js/vfs_fonts.js" type="text/javascript"></script>
<script src="../js/buttons.colVis.min.js" type="text/javascript"></script>

<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>
<script language="JavaScript">	
$(document).ready( function () {

});
	
</script>
<script type="text/javascript">
	$(document).ready(function() {

	});

	function saveBeneficiary() {
			
			var custId = document.getElementById("custId").value;
			var beneficiaryCd = document.getElementById("beneficiaryCd").value;
			var srcAccountNo = document.getElementById("srcAccountNo").value;
			var beneName = document.getElementById("beneName").value;
			var beneAccountNo = document.getElementById("beneAccountNo").value;
			var ifscCode = document.getElementById("ifscCode").value;
			var bankName = document.getElementById("bankName").value;
			var paymentType = document.getElementById("paymentType").value;
			var beneType = document.getElementById("beneType").value;
			var currencyCode = document.getElementById("currencyCode").value;
			var acquirer = document.getElementById("acquirer").value;
			var status = document.getElementById("status").value;;
			
			if ( custId == null ||  custId.trim() == ""){
					alert ("Enter Cust Id !");
					return false;
			}
			if ( beneficiaryCd == null ||  beneficiaryCd.trim() == ""){
					alert ("Enter Beneficiary Cd !");
					return false;
			}
			if ( srcAccountNo == null ||  srcAccountNo.trim() == ""){
					alert ("Enter Nodal account number !");
					return false;
			}
			if ( beneName == null ||  beneName.trim() == ""){
					alert ("Enter Beneficiary Name !");
					return false;
			}
			if ( beneAccountNo == null ||  beneAccountNo.trim() == ""){
					alert ("Enter Beneficiary Account number !");
					return false;
			}
			if ( ifscCode == null ||  ifscCode.trim() == ""){
					alert ("Enter IFSC code !");
					return false;
			}
			if ( bankName == null ||  bankName.trim() == ""){
					alert ("Enter Bank Name !");
					return false;
			}if ( paymentType == null ||  paymentType.trim() == "ALL" ||  paymentType.trim() == "" ){
					alert ("Select Payment Type !");
					return false;
			}
			if ( beneType == null ||  beneType.trim() == "ALL" ||  beneType.trim() == "" ){
					alert ("Select Beneficiary Type !");
					return false;
			}
			if ( acquirer == null ||  acquirer.trim() == "ALL" ||  acquirer.trim() == "" ){
					alert ("Select Beneficiary Type !");
					return false;
			}
			if ( currencyCode == null ||  currencyCode.trim() == "ALL" ||  currencyCode.trim() == "" ){
					alert ("Select Currency Code !");
					return false;
			}
			if ( status == null ||  status.trim() == "ALL" ||  status.trim() == "" ){
				alert ("Select Status !");
				return false;
			}	
			
			
			var token  = document.getElementsByName("token")[0].value;
		$.ajax({
			type: "POST",
			url:"beneficiarySaveAction",
			data:{"param":"edit","beneficiaryCd":beneficiaryCd,"custId":custId,"ifscCode":ifscCode,"paymentType":paymentType,"beneType":beneType,
				"currencyCode":currencyCode,"acquirer":acquirer, "srcAccountNo":srcAccountNo, "status":status, "beneName":beneName, 
				"bankName":bankName, "beneAccountNo":beneAccountNo, "token":token,"struts.token.name": "token",},
			success:function(data){
				var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
				alert(response);
				document.getElementById("btnEditUser").style.display="none";
		    },
			error:function(data){
				alert("Network error, Beneficiary not be saved");
			}
		});
			
		};
		
	
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
  height: 64px;
}
.adduT{
  margin-bottom: 0 !important;
}
.addu{
   height: 700px !important;
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
</style>
<style type="text/css">
.cust {width: 24%!important; margin:0 5px !important; /*font: bold 10px arial !important;*/}
.samefnew{
	width: 24%!important;
    margin: 0 5px !important;
    /*font: bold 10px arial !important;*/
}
.btn {padding: 3px 7px!important; font-size: 12px!important; }
.samefnew-btn{
    width: 15%;
    float: left;
    font: bold 11px arial;
    color: #333;
    line-height: 22px;
    margin-left: 5px;
}
/*tr td.my_class{color:#000 !important; cursor: default !important; text-decoration: none;}*/
tr td.my_class{
	cursor: pointer;
}
tr td.my_class:hover{
	cursor: pointer !important;
}

tr th.my_class:hover{
	color: #fff !important;
}

.cust .form-control, .samefnew .form-control{
	margin:0px !important;
	width: 100%;
}
.select2-container{
	width: 100% !important;
}
.clearfix:after{
	display: block;
	visibility: hidden;
	line-height: 0;
	height: 0;
	clear: both;
	content: '.';
}
#popup{
	position: fixed;
	top:0px;
	left: 0px;
	background: rgba(0,0,0,0.7);
	width: 100%;
	height: 100%;
	z-index:999; 
	display: none;
}
.innerpopupDv{
	    width: 600px;
    margin: 80px auto;
    background: #fff;
    padding: 3px 10px;
    border-radius: 10px;
}
.btn-custom {
    margin-top: 5px;
    height: 27px;
    border: 1px solid #5e68ab;
    background: #5e68ab;
    padding: 5px;
    font: bold 12px Tahoma;
    color: #fff;
    cursor: pointer;
    border-radius: 5px;
}
#loader-wrapper .loader-section.section-left, #loader-wrapper .loader-section.section-right{
	background: rgba(225,225,225,0.6) !important;
	width: 50% !important;
}
.invoicetable{
	float: none;
}
.innerpopupDv h2{
	    font-size: 12px;
    padding: 5px;
}
.text-class{
	text-align: center !important;
}
.odd{
	background-color: #e6e6ff !important;
}
 
</style>
</head>
<body>
<table width="100%" border="0" cellspacing="0" cellpadding="0" class="txnf">
  <!-- <tr>
    <td align="left"><h2>Edit Beneficiary</h2></td>
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
        <div >
		  <s:token/>
		  <div class="card ">
			<div class="card-header card-header-rose card-header-icon">
			  <div class="card-icon">
				<i class="material-icons">mail_outline</i>
			  </div>
			  <h4 class="card-title" style="
			  color: #0271bb;
			  font-weight: 500;">Edit Beneficiary</h4>
			</div>

			<div class="card-body ">
          <div class="adduT">
			<label class="bmd-label-floating">Cust Id<span style="color:red; margin-left:3px;">*</span></label><br>
			  <!-- Cust Id<span style="color:red; margin-left:3px;">*</span><br> -->
            <s:textfield name="custId" id = "custId" cssClass="signuptextfield" autocomplete="off" />
            </div>
			
            <div class="adduT">
				<label class="bmd-label-floating">Beneficiary Cd<span style="color:red; margin-left:3px;">*</span></label><br>
				<!-- Beneficiary Cd<span style="color:red; margin-left:3px;">*</span><br> -->
              <s:textfield	name="beneficiaryCd" id = "beneficiaryCd" cssClass="signuptextfield" autocomplete="off" />
            </div>
			
            <div class="adduT">
				<label class="bmd-label-floating">Nodal Account No<span style="color:red; margin-left:3px;">*</span></label><br>
				<!-- Nodal Account No<span style="color:red; margin-left:3px;">*</span><br> -->
              <s:textfield name="srcAccountNo" id="srcAccountNo" cssClass="signuptextfield" autocomplete="off" />
            </div>
			
			<div class="adduT">
				<label class="bmd-label-floating">Beneficiary Name<span style="color:red; margin-left:3px;">*</span></label><br>
				<!-- Beneficiary Name<span style="color:red; margin-left:3px;">*</span><br> -->
              <s:textfield name="beneName" id="beneName" cssClass="signuptextfield" autocomplete="off" />
            </div>
			
			<div class="adduT">
				<label class="bmd-label-floating">Beneficiary Account No<span style="color:red; margin-left:3px;">*</span></label><br>
				<!-- Beneficiary Account No<span style="color:red; margin-left:3px;">*</span><br> -->
              <s:textfield name="beneAccountNo" id="beneAccountNo" cssClass="signuptextfield" autocomplete="off" />
            </div>
			<div class="adduT">
				<label class="bmd-label-floating">IFSC Code<span style="color:red; margin-left:3px;">*</span></label><br>
				<!-- IFSC Code<span style="color:red; margin-left:3px;">*</span><br> -->
              <s:textfield name="ifscCode" id="ifscCode" cssClass="signuptextfield" autocomplete="off" />
            </div>
			<div class="adduT">
				<label class="bmd-label-floating">Bank Name<span style="color:red; margin-left:3px;">*</span></label><br>
				<!-- Bank Name<span style="color:red; margin-left:3px;">*</span><br> -->
              <s:textfield name="bankName" id="bankName" cssClass="signuptextfield" autocomplete="off" />
            </div>
            <div class="adduT">
				<label class="bmd-label-floating">Payment Type<span style="color:red; margin-left:3px;">*</span></label><br>
				<!-- Payment Type<span style="color:red; margin-left:3px;">*</span><br> -->
			  <s:select headerKey="" headerValue="ALL" class="form-control"
						list="@com.pay10.commons.util.NodalPaymentTypes@values()"
						listValue="name" listKey="code" name="paymentType"
						id="paymentType" autocomplete="off" value=""/>
            </div>

			 <div class="adduT">
				<label class="bmd-label-floating">Beneficiary Type<span style="color:red; margin-left:3px;">*</span></label><br>
				 <!-- Beneficiary Type<span style="color:red; margin-left:3px;">*</span><br> -->
               <s:select headerKey="" headerValue="ALL" class="form-control"
						list="@com.pay10.commons.util.BeneficiaryTypes@values()"
						listValue="name" listKey="code" name="beneType"
						id="beneType" autocomplete="off" value=""/>
            </div>
			<div class="adduT">
				<label class="bmd-label-floating">Currency Code<span style="color:red; margin-left:3px;">*</span></label><br>
				<!-- Currency Code<span style="color:red; margin-left:3px;">*</span><br> -->
               <s:select headerKey="" headerValue="ALL" class="form-control"
						list="@com.pay10.commons.util.CurrencyTypes@values()"
						listValue="name" listKey="code" name="currencyCode"
						id="currencyCode" autocomplete="off" value=""/>
            </div>
            
            <div class="adduT">
				<label class="bmd-label-floating">Status<span style="color:red; margin-left:3px;">*</span></label><br>
				<!-- Status<span style="color:red; margin-left:3px;">*</span><br> -->
				 <s:select headerKey="" headerValue="" class="form-control"
					list="#{'ACTIVE':'ACTIVE','INACTIVE':'INACTIVE'}" name="status" id = "status" />
            </div>
            				
			</div>
			<div class="card-footer text-right">
            <div class="adduT" style="padding-top:10px">
              <s:submit id="btnEditUser" name="btnEditUser" value="Save"
								type="button" cssClass="btn btn-success btn-md btnSbmt" onclick="saveBeneficiary()"> </s:submit>
			</div>
		</div>
		</div>
            
            <s:hidden name="token" value="%{#session.customToken}"></s:hidden>
      

		  </div>
          </div>
          
          
          </td>
  </tr>
</table>


</body>
</html>