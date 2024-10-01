<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Modify Menu</title>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<script src="../js/jquery.js"></script>
<script src="../js/commanValidate.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>

<!--  loader scripts -->
<script src="../js/loader/modernizr-2.6.2.min.js"></script>
<script src="../js/loader/main.js"></script>
<link rel="stylesheet" href="../css/loader/normalize.css" />
<link rel="stylesheet" href="../css/loader/main.css" />
<link rel="stylesheet" href="../css/loader/customLoader.css" />

<script type="text/javascript">	

 $(document).ready( function() {
    
	$('#btnEditMenu').click(function() {
		var answer = confirm("Do you want to Update Menu ?");
		if (answer != true) {
			return false;
		}else {
				$('#loader-wrapper').show();
				document.getElementById("frmEditMenu").submit();
        }
        
		      });	        
	   });
</script>

<style>
.btn:focus{
		outline: 0 !important;
	}
</style>

</head>
<body>
   <div id="loader-wrapper" style="width: 100%; height: 100%; display:none;">
	  <div id="loader"></div>
    </div>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0"
		class="txnf">
		<tr>
			<td align="left" valign="top"><div id="saveMessage">
					<s:actionmessage class="success success-text" />
				</div></td>
		</tr>
		<tr>
			<td align="left" valign="top"><div class="addu">
					<s:form action="updateMenuDetailsAction" id="frmEditMenu">
						<div class="card ">
							<div class="card-header card-header-rose card-header-icon">
							  <h4 class="card-title" style="
							  color: #0271bb;
							  font-weight: 500;">Edit Menu</h4>
							</div>
				
							<div class="card-body ">
						<div class="adduT">
							<label class="bmd-label-floating">Menu Name<span style="color:red; margin-left:3px;">*</span></label>
							<br>
							<div class="txtnew">
								<s:textfield name="menuName" id="menuName"
									cssClass="signuptextfield" autocomplete="off" />
							</div>
						</div>
						<div class="adduT">
							<label class="bmd-label-floating">Menu Description<span style="color:red; margin-left:3px;">*</span></label><br>
							<div class="txtnew">
								<s:textfield name="description" id="description"
									cssClass="signuptextfield" autocomplete="off" />
							</div>
						</div>
						<div class="adduT">
							<label class="bmd-label-floating">Is Active ?</label><br>
							<s:checkbox name="isActive" id="isActive" fieldValue="isActive" />
							<label class="labelfont" for="1"></label>

						</div>

						</div>
						<div class="card-footer text-right">
								<div class="adduT" style="padding-top: 10px">
									<s:submit value="Save" method="submit"
										cssClass="btn btn-success btn-md">
									</s:submit>
								</div>
							</div>
						</div>
						<s:hidden name="id" id="id" />
						<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
					
					</s:form>
					<div class="clear"></div>
				</div></td>
		</tr>
		<tr>
			<td align="left" valign="top">&nbsp;</td>
		</tr>
	</table>
</body>
</html>