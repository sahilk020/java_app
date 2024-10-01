<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Modify Role</title>
<script type="text/javascript" src="../js/jquery.min.js"></script>
<script src="../js/jquery.js"></script>
<script src="../js/commanValidate.js"></script>
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />

<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>

<!--  loader scripts -->
<script src="../js/loader/modernizr-2.6.2.min.js"></script>
<script src="../js/loader/main.js"></script>
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>

<link rel="stylesheet" href="../css/loader/normalize.css" />
<link rel="stylesheet" href="../css/loader/main.css" />
<link rel="stylesheet" href="../css/loader/customLoader.css" />

<style>
.btn:focus {
	outline: 0 !important;
}
</style>

<script type="text/javascript">
$(document)
.ready(
		function() {
			var isActive = document.getElementById("isActive").checked;
			var permissionsChecked = '<s:property value="permissionAccess"/>';
			var permissionsCheckbox = document
					.getElementsByName("permissionAccess");
			for (var i = 0; i < permissionsCheckbox.length; i++) {
				var checkBox = permissionsCheckbox[i];
				var value = checkBox.value;
				var permissionArray = JSON
						.parse(permissionsChecked);
				for (var j = 0; j < permissionArray.length; j++) {
					if (!isActive) {
						document.getElementById(checkBox.id).disabled = true;
					}
					if (permissionArray[j] == value) {
						document.getElementById(checkBox.id).checked = "checked";
					}
				}
			}
			$('#formValidation').submit(function (e) {
				updateRole(e);
			 });
		});
function managePermission(checked) {
	var permissionsCheckbox = document.getElementsByName("permissionAccess");
	if (checked) {
		for (var i = 0; i < permissionsCheckbox.length; i++) {
			var checkBox = permissionsCheckbox[i];
			document.getElementById(checkBox.id).disabled = false;
		}
	} else {
		for (var i = 0; i < permissionsCheckbox.length; i++) {
			var checkBox = permissionsCheckbox[i];
			document.getElementById(checkBox.id).disabled = true;
		}
	}
}
function updateRole(e) {

    var urls = new URL(window.location.href);
	var domain = urls.origin;
	var id = document.getElementsByName("id")[0].value;
	var token = document.getElementsByName("token")[0].value;
	var roleName = document.getElementById("roleName").value;
	var desc = document.getElementById("description").value;
     //var groupId = document.getElementById("userGroupId").value;
     var applicationId = document.getElementById("applicationId").value;
	var isActive = document.getElementById("isActive").checked;
	var permission = "";
	var permissions = document.getElementsByName("permissionAccess");
	for (var i=0; i < permissions.length; i++) {

		var permissionObj = permissions[i];
		if (permissionObj.checked) {
			permission = permission.concat(permissionObj.value).concat(",");
		}
	}
	permission =  permission.substring(0, permission.length - 1);
	if (roleName == null || roleName == '') {
		document.getElementById("roleName-error").innerHTML = "Please enter rolename";
		document.getElementById("roleName").focus();
		e.preventDefault();
		return;
	} else {
		document.getElementById("roleName-error").innerHTML = "";
	}
	if (desc == null || desc == '') {
		document.getElementById("description-error").innerHTML = "Please enter description";
		document.getElementById("description").focus();
		e.preventDefault();
		return;
	} else {
		document.getElementById("description-error").innerHTML = "";
	}
	if (applicationId == null || applicationId == '') {
		document.getElementById("applicationId-error").innerHTML = "Please enter applicationId";
		document.getElementById("applicationId").focus();
		e.preventDefault();
		return;
	} else {
		document.getElementById("applicationId-error").innerHTML = "";
	}
	if (permission == null || permission == '') {
		document.getElementById("permission-error").innerHTML = "Please select the permission";
		e.preventDefault();
		return;
	} else {
		document.getElementById("permission-error").innerHTML = "";
	}
	$.ajax({
		type : "POST",
		url : "updateUserRoleAction",
		data : {
			id : id,
			roleName : roleName,
			description : desc,
			applicationId:applicationId,
			active : isActive,
			permissionAccess: permission,
			token : token,
			"struts.token.name" : "token"
		},
		success:function(response){
		var res=JSON.parse(JSON.stringify(response))
		var id=res.id;
		if(id>0){
		 alert("Role updated successfully.");
         window.location.href = domain+"/crm/jsp/manageRole";
         }
		},
		error : function(status) {
			alert("Failed to update role.");
			//window.location.reload();
		}
	});

}
</script>

</head>
<body>
	<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
		<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
				<!--begin::Page title-->
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
					data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
					class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Modify Role</h1>
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
						<li class="breadcrumb-item text-dark">Modify Role</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

			</div>
			<!--end::Container-->
		</div>

		<div class="post d-flex flex-column-fluid" id="kt_post">
			<div id="kt_content_container" class="container-xxl">
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body ">
								<div class="row g-9 mb-8">

	<s:form id="formValidation">
		<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
		<s:hidden name="id"></s:hidden>
		<div class="card">
								<div class="card-body">	<div class="row g-9 mb-8">
			<div class="col-md-3 addfildn">
				<div class="rkb">
					<div class="addfildn">
						<div class="fl_wrap">
							<label class='fl_label'
								style="padding: 0; font-size: 13px; font-weight: 600;">Role
								Name*</label>
							<s:textfield id="roleName"
								style="margin-top:10px;    font-weight:500; font-size:14px;" class="form-control form-control-solid"
								name="roleName" maxlength="200" readonly="true" type="text" onkeypress="return (event.charCode > 64 && event.charCode < 91) || (event.charCode > 96 && event.charCode < 123) || (event.charCode==32)"></s:textfield>
								<em id="roleName-error" class="error invalid-feedback" style="display: inline;"></em>
						</div>
					</div>
				</div>
			</div>
			<div class="col-md-3 addfildn">
				<div class="rkb">
					<div class="addfildn">
						<div class="fl_wrap">
							<label class='fl_label'
								style="padding: 0; font-size: 13px; font-weight: 600;">Role
								Description*</label>
							<s:textfield id="description"
								style="margin-top:10px;font-weight:500; font-size:14px;" class="form-control form-control-solid"
								name="description" type="text" readonly="true" maxlength="250" onkeypress="return (event.charCode > 64 && event.charCode < 91) || (event.charCode > 96 && event.charCode < 123) || (event.charCode==32)"></s:textfield>
								<em id="description-error" class="error invalid-feedback" style="display: inline;"></em>
						</div>
					</div>
				</div>
			</div>
						<div class="col-md-3 addfildn">
            				<div class="rkb">
            					<div class="addfildn">
            						<div class="fl_wrap">
            							<label class='fl_label'
            								style="padding: 0; font-size: 13px; font-weight: 600;">Application*</label>
            							 <s:select name="aaData"
                                            class="form-select form-select-solid mt-1"
                                            id="applicationId" list="aaData"
                                            listKey="id" listValue="name" autocomplete="off" data-control="select-2"/>
                                        <em id="applicationId-error" class="error invalid-feedback"
                                        style="display: inline;"></em>
            						</div>
            					</div>
            				</div>
            			</div>

			<div class="col-md-1 addfildn">
				<div class="rkb">
					<div class="addfildn">
						<div class="fl_wrap">
							<label class='fl_label'
								style="padding: 0; font-size: 13px; font-weight: 600;">Active</label>
							<s:checkbox id="isActive"
								style="margin-top:10px;    font-weight:500; font-size:14px;"
								name="active" onclick="managePermission(this.checked);"></s:checkbox>
						</div>
					</div>
				</div>
			
		</div></div></div></div>
		<em id="permission-error" class="error invalid-feedback" style="display: inline;"></em>
		<s:iterator value="menus">
			<div class="row">
				<div class="rkb"></div>
			</div>
			<div class="card">
								<div class="card-body">
			<div class="indent formboxRR">
			
				<div class="addfildn" style="padding-left: 0;">
					<div class="col-md-12">
						<div class="rkb">
							<div class="addfildn">
								<div class="fl_wrap">
									<label class='fl_label'
										style="padding: 0; font-size: 13px; font-weight: 600;">
										<s:property value='menuName' />
									</label>
								</div>
								<s:iterator value="permissions">
									<div class="col-md-2 fl_wrap">
										<label class='fl_label'
											style="padding: 0; font-size: 13px; font-weight: 600;">
											<s:property value='permission' />
										</label>
										<s:set var="permissionId" value="id"></s:set>
										<input type="checkbox" value="<s:property value='id' />"
											id="perm-${permissionId}"
											style="margin-top: 10px; font-weight: 500; font-size: 14px;"
											name="permissionAccess" />
									</div>
								</s:iterator>
							</div>
						</div>
					</div>
					<s:iterator value="subMenus">
					<div class="row g-9 mb-8">
						<div class="indent raw col-md-12 rkb">
							<div class="addfildn row">
									<label class='fl_label col-md-4'
										style="font-size: 13px; font-weight: 600;">
										<s:property value='menuName' />
									</label> 
									<%-- <input type="checkbox" id="sub-menu-<s:property value='id' />"
										style="margin-top: 10px; font-weight: 500; font-size: 14px;"
										name="sub-menu-<s:property value='id' />" /> --%>
								<s:iterator value="permissions">
										<label class='fl_label col-md-1 mt-6 p-0'
											style=" font-size: 13px; font-weight: 600; ">
											<s:property value='permission' />
										</label>
										<label class='fl_label col-md-1 mt-6 p-0'>
										<s:set var="permissionId" value="id"></s:set>
										<input type="checkbox" value="<s:property value='id' />"
											id="perm-${permissionId}"
											style="font-weight: 500; font-size: 14px;" 
											name="permissionAccess" />
											</label>
								</s:iterator>
							</div>
						</div></div>
					</s:iterator>
				</div>
			</div></div></div>
		</s:iterator>
		<div class="indent raw col-md-12 formboxRR">
			<div class="col-md-3 addfildn">
				<div class="rkb">
					<div class="addfildn">
						<div class="fl_wrap">
							<s:submit method="submit" onclick="updateRole();" style="margin-right: 10px;"
								class="btn w-100 w-md-50 btn-primary" value="Save">
							</s:submit>
						</div>
					</div>
				</div>
			</div>
		</div>
	</s:form>
								</div></div></div></div></div></div></div>
	</div>
</body>
</html>