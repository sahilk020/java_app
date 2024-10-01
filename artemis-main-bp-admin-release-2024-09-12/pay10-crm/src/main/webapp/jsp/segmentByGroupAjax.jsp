<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:set var="menuType" value="type"></s:set>
<s:if test="#menuType != 'MERCHANT_EDIT'">
	<div class="form-group" id="segmentIdDiv">
		<label class="bmd-label-floating">Segment<i style="color: red;">*</i></label><br>
		<div class="txtnew">
			<s:select name="segmentName" class="form-select form-select-solid" id="segmentName"
				headerKey="" headerValue="Select Segment" list="segments" listKey="segment"
				listValue="segment" autocomplete="off" />
				<em id="segmentName-error" class="error invalid-feedback" ></em>
		</div>

		<p class="errorSec errorSegment" >Please Select Segment</p>
	</div>
</s:if>
<s:if test="#menuType == 'MERCHANT_EDIT'">
	<div class="addfildn" id="segmentIdDiv">
		<div class="fl_wrap">
			<label class='fl_label'
				style="padding: 0; font-size: 13px; font-weight: 600;">Segment</label>
			<s:select class="form-select form-select-solid" id="segmentName" name="segmentName" list="segmentList"
				listKey="segment" listValue="segment" autocomplete="off" ></s:select>
		</div>
	</div>
</s:if>