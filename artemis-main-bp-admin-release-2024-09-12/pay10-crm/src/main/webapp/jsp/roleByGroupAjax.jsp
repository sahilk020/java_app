<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:set var="menuType" value="type"></s:set>
<s:if test="#menuType != 'MERCHANT_EDIT'">
	<div class=" flex-column mb-8 fv-row" id="roleIdDiv">
		<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
			<span class="required">Role</span>
		</label> <!-- <label class="bmd-label-floating">Role</label><br> -->
		<s:select name="roleId" class="form-select form-select-solid" id="roleId"
				headerKey="" headerValue="Select Role" list="roles" listKey="id"
				listValue="roleName" autocomplete="off"  />
				<em id="roleId-error" class="error invalid-feedback" ></em>
				<p class="errorSec errorRole" >Please Select Role</p>
	</div>

</s:if>
<s:if test="#menuType == 'MERCHANT_EDIT'">
	<div class=" flex-column mb-8 fv-row" id="roleIdDiv">

		<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
			<span class="required">Role</span>
		</label>
			<!-- <label class='fl_label'
				style="padding: 0; font-size: 13px; font-weight: 600;">Role</label> -->
			<s:select class="form-select form-select-solid" id="roleId" name="roleId" list="roleList"
				listKey="id" listValue="roleName" autocomplete="off"></s:select>

	</div>
</s:if>
