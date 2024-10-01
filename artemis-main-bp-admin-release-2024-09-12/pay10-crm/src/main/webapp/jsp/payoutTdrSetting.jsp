<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Payout TDR Setting</title>
<!-- <link href="../css/bootstrap.min.css" rel="stylesheet"> -->
<link href="../fonts/css/font-awesome.min.css" rel="stylesheet">
</link>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<link href="../css/loader.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="../js/jquery.min.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css"
	integrity="sha384-...." crossorigin="anonymous">

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
	<script src="../assets/plugins/global/plugins.bundle.js"></script>
	<script src="../assets/js/scripts.bundle.js"></script>
	<link href="../css/select2.min.css" rel="stylesheet" />
	<script src="../js/jquery.select2.js" type="text/javascript"></script>
	<script src="../js/mapping.js"></script>

	<style>
/* Add some styling to make the table and buttons look better */
table {
	width: 100%;
	border-collapse: collapse;
	margin-bottom: 20px;
}

th, td {
	padding: 10px;
	text-align: left;
}

th {
	background-color: #f2f2f2;
}

.edit-btn, .save-btn, .cancel-btn, .clone-btn, .delete-btn {
	padding: 5px 10px;
	margin: 2px;
	cursor: pointer;
	border: none;
	border-radius: 4px;
	font-size: 12px;
}

.edit-btn {
	background-color: #3498db;
	color: #fff;
}

.save-btn {
	background-color: #2ecc71;
	color: #fff;
}

.cancel-btn {
	background-color: #e74c3c;
	color: #fff;
}

.clone-btn {
	background-color: #27ae60;
	color: #fff;
}

.delete-btn {
	background-color: #e74c3c;
	color: #fff;
}

/* Styling for checkboxes */
.form-check-input {
	margin-top: 5px;
}

/* Optional: Add some margin to the buttons in the table */
.edit-btn, .save-btn, .cancel-btn, .clone-btn, .delete-btn {
	margin: 2px;
}
</style>


	<style>
ul.filters {
	list-style: none;
}

ul.filters .fa-check:before {
	content: unset;
}

.table-container {
	overflow-x: auto;
}

.table-header {
	background-color: #202F4B;
}
</style>
</head>

<body>
	<div class="content d-flex flex-column flex-column-fluid"
		id="kt_content">
		<!--begin::Toolbar-->
		<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container"
				class="container-fluid d-flex flex-stack">
				<!--begin::Page title-->
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
					data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
					class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
					Pay-out TDR And Surcharge Setting
					</h1>
					<!--end::Title-->
					<!--begin::Separator-->
					<span class="h-20px border-gray-200 border-start mx-4"></span>
					<!--end::Separator-->
					<!--begin::Breadcrumb-->
					<ul
						class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
						<!--begin::Item-->
						<li class="breadcrumb-item text-muted"><a href="home"
							class="text-muted text-hover-primary">Dashboard</a></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-muted">Merchant Billing</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Pay-out TDR And Surcharge Setting</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->
			</div>
			<!--end::Container-->
		</div>

		<div class="post d-flex flex-column-fluid" id="kt_post">
			<!--begin::Container-->
			<div id="kt_content_container" class="container-xxl">
				<div class="row my-5">
					<div class="col">

						<div class="card" style="margin-bottom: 20px;">
							<div class="card-body">
								<div class="row my-5 mb-3">

									<div class="col-md-12">
										<div class="form-check form-check-inline">
											<input class="form-check-input" type="checkbox"
												id="pgCheckbox"> <label class="form-check-label"
												for="pgCheckbox">Pay-in</label>
										</div>
										<div class="form-check form-check-inline">
											<input class="form-check-input" type="checkbox"
												id="poCheckbox" checked disabled> <label
												class="form-check-label" for="poCheckbox">Pay-out</label>
										</div>
									</div>
								</div>
							</div>
						</div>

						<div class="card">
							<div class="card-body">

								<div class="row my-3 align-items-center">
									<div class="col-md-4 fv-row">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">Merchant</span>
										</label>
										<s:select name="merchant" id="merchant" value="merchant"
											headerKey=""
											class="form-select form-select-solid merchantPayId"
											headerValue="Please Select Merchant" list="merchantList"
											listKey="payId" listValue="businessName" autocomplete="off"
											onchange="getAcquirer()" />
									</div>

									<input type="hidden" name="email" id="email"
										value="<s:property value='%{#session.USER.emailId}'/>" />


									<div class="col-md-4 fv-row">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">Acquirer</span>
										</label>
										<!--end::Label-->
										<select name="acquirer" id="acquirer" headerKey=""
											class="form-select form-select-solid merchantPayId"
											autocomplete="off" onchange="getChannel()"></select>
									</div>

									<div class="col-md-4 fv-row">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">Channel</span>
										</label>
										<!--end::Label-->

										<select id="channel" headerKey=""
											class="form-select form-select-solid" autocomplete="off"
											onchange="getCurrency()">
										</select>

									</div>
								</div>
							</div>
						</div>
					</div>
				</div>

				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">

								<div class="row my-3 align-items-center">

									<div id="currencyDiv">
										<!-- currency -->

									</div>
								</div>
							</div>

						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<script type="text/javascript">
		var table;
		var header = {
				"payId": "Pay ID",
				"acquirerName": "Acquirer Name",
				"channel": "Channel",
				"minTransactionAmount": "Min. Amount Per Transaction",
				"maxTransactionAmount": "Max. Amount Per Transaction",
				"fromDate": "From Date",
				"bankPreference": "Acquirer Preference",
				"bankTdr": "Acquirer TDR",
				"bankMinTdrAmt": "Acquirer Min. Amount Per Transaction",
				"bankMaxTdrAmt": "Acquirer Max. Amount Per Transaction",
				"merchantPreference": "Merchant Preference",
				"merchantTdr": "Merchant TDR",
				"merchantMinTdrAmt": "Merchant Min. Amount Per Transaction",
				"merchantMaxTdrAmt": "Merchant Max. Amount Per Transaction",
				"enableSurcharge": "Enable Surcharge",
				"igst": "Local Tax Rate",
				"currency": "Currency"
			}

		function saveRow(button, index) {
    var dataArray = [];
    var inputData = {};
    var row = $(button).closest("tr");


    // Find all input fields within the current row
    var inputFields = $(row).find('input, select');

    // Loop through each input field and store its value in the object
    inputFields.each(function () {
        var fieldName = $(this).attr('name');
        var fieldValue;

        if ($(this).is(':checkbox')) {
            fieldValue = $(this).is(':checked');
        } else {
            fieldValue = $(this).val();
        }

        inputData[fieldName] = fieldValue;

    });

    // Set the value of the 'updatedBy' field to the email obtained from the hidden input
    inputData['updatedBy'] = $("#email").val();
	inputData['status'] = 'ACTIVE';



	// MIN & MAX
	if(inputData.minTransactionAmount ==""){
		alert("Min. Amount Per Transaction cannot be Empty");
		return false;
	}
	if(inputData.maxTransactionAmount ==""){
		alert("Max. Amount Per Transaction cannot be Empty");
		return false;
	}
	if(inputData.bankTdr ==""){
		alert("Acquirer TDR cannot be Empty");
		return false;
	}
	if(inputData.bankMinTdrAmt ==""){
		alert("Acquirer Min. Amount Per Transaction cannot be Empty");
		return false;
	}
	if(inputData.bankMaxTdrAmt ==""){
		alert("Acquirer Max. Amount Per Transaction cannot be Empty");
		return false;
	}

	if(inputData.merchantTdr ==""){
		alert("Merchant TDR cannot be Empty");
		return false;
	}
	if(inputData.merchantMinTdrAmt ==""){
		alert("Merchant Min. Amount Per Transaction cannot be Empty");
		return false;
	}
	if(inputData.merchantMaxTdrAmt ==""){
		alert("Merchant Max. Amount Per Transaction cannot be Empty");
		return false;
	}
	if(parseFloat(inputData.minTransactionAmount)<=0){
		alert("Min. Amount Per Transaction cannot be less than or equal to 0");
		return false;
	}

	if(parseFloat(inputData.maxTransactionAmount<=0)){
		alert("Max. Amount Per Transaction cannot be less than or equal to 0");
		return false;
	}

	if(parseFloat(inputData.maxTransactionAmount) < parseFloat(inputData.minTransactionAmount)){
		alert("Min. Amount Per Transaction cannot be greater than Max. Amount Per Transaction");
		return false;
	}

	// Acquirer TDR & MIN,MAX
	if(parseFloat(inputData.bankTdr)<=0){
		alert("Acquirer TDR Amount cannot be less than or equal to 0");
		return false;
	}

	if(parseFloat(inputData.bankMinTdrAmt)<=0){
		alert("Acquirer Min. Amount Per Transaction cannot be less than or equal to 0");
		return false;
	}

	if(parseFloat(inputData.bankMaxTdrAmt<=0)){
		alert("Acquirer Max. Amount Per Transaction cannot be less than or equal to 0");
		return false;
	}

	if(parseFloat(inputData.bankMaxTdrAmt) < parseFloat(inputData.bankMinTdrAmt)){
		alert("Acquirer Min. Amount Per Transaction cannot be greater than Acquirer Max. Amount Per Transaction");
		return false;
	}

	// Merchant TDR & MIN,MAX
	if(parseFloat(inputData.merchantTdr)<=0){
		alert("Merchant TDR Amount cannot be less than or equal to 0");
		return false;
	}

	if(parseFloat(inputData.merchantMinTdrAmt)<=0){
		alert("Merchant Min. Amount Per Transaction cannot be less than or equal to 0");
		return false;
	}

	if(parseFloat(inputData.merchantMaxTdrAmt<=0)){
		alert("Merchant Max. Amount Per Transaction cannot be less than or equal to 0");
		return false;
	}

	if(parseFloat(inputData.merchantMaxTdrAmt) < parseFloat(inputData.merchantMinTdrAmt)){
		alert("Merchant Min. Amount Per Transaction cannot be greater than Merchant Max. Amount Per Transaction");
		return false;
	}


    dataArray.push(inputData);
    var urls = new URL(window.location.href);
	var domain = urls.origin;
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: domain+"/crmws/TdrSettingPayout/saveTdrListDetail",
        data: JSON.stringify(dataArray[0]),
        success: function (response) {
            $($($(row).find("td")[20]).find("button")).click();
            alert(response);
			location.reload();
        },
        error: function (xhr, status, error) {
            alert("Error while saving data");
            console.error("Error saving data:", status, error);

        }
    });
}

		$(document).ready(function() {
			// Your initialization code here...
		});

		function getAcquirer() {
			var merchant = $("#merchant").val();
			if (merchant === "") {
				alert("Please select Merchant");
				return false;
			}
			var urls = new URL(window.location.href);
			var domain = urls.origin;
			$
					.ajax({
						url : domain+"/crmws/TdrSettingPayout/getAcquirerList",
						type : "GET",
						dataType : "json",
						data : {
							payId : merchant
						},
						success : function(result) {
							$("#acquirer")
									.html(
											'<option value="" selected>Please Select Acquirer</option>');
							for (var i = 0; i < result.length; i++) {
								$("#acquirer").append(
										"<option value='" + result[i] + "'>"
												+ result[i] + "</option>");
							}
						},
						error : function(xhr, status, error) {
							console.error("Error fetching Acquirer list:",
									status, error);
						}
					});
		}

		function getChannel() {
			var acquirer = $("#acquirer").val();
			var merchant = $("#merchant").val();
			if (merchant === "") {
				alert("Please select Merchant");
				$("#acquirer").val("");
				return false;
			}
			if (acquirer === "") {
				alert("Please select Acquirer");
				return false;
			}
			var urls = new URL(window.location.href);
			var domain = urls.origin;
			$
					.ajax({
						url : domain+"/crmws/TdrSettingPayout/getChannelList",
						type : "GET",
						dataType : "json",
						data : {
							acquirer : acquirer,
							payId : merchant
						},
						success : function(result) {
							$("#channel")
									.html(
											'<option value="" selected>Please Select Channel</option>');
							for (var i = 0; i < result.length; i++) {
								$("#channel").append(
										"<option value='" + result[i] + "'>"
												+ result[i] + "</option>");
							}
						},
						error : function(xhr, status, error) {
							console.error("Error fetching Channel list:",
									status, error);
						}
					});
		}

		function getCurrency() {
			var channel = $("#channel").val();
			var acquirer = $("#acquirer").val();
			var merchant = $("#merchant").val();

			if (merchant === "") {
				alert("Please select Merchant");
				$("#channel").val("");
				return false;
			}

			if (acquirer === "") {
				alert("Please select Acquirer");
				$("#channel").val("");
				return false;
			}

			if (channel === "") {
				alert("Please select Channel");
				return false;
			}
			var urls = new URL(window.location.href);
			var domain = urls.origin;
			function preferredOrder(obj, order) {
									var newObject = {};
									for(var i = 0; i < order.length; i++) {
										if(obj.hasOwnProperty(order[i])) {
											newObject[order[i]] = obj[order[i]];
										}
									}
									return newObject;
								}
			$
					.ajax({
						url : domain+"/crmws/TdrSettingPayout/getTdrListDetail",
						type : "GET",
						dataType : "json",
						data : {
							acquirer : acquirer,
							payId : merchant,
							channel : channel
						},
						success : function(res) {
							$("#currencyDiv").empty();
							var data = new Map();
							if (res.length > 0) {
								for (var i = 0; i < res.length; i++) {
									var head = res[i]["currency"];
									if(!data.has(head)){
										data.set(head, []);
									}
									var arr = data.get(head);
									arr.push(res[i]);
								}
								for (let [k, v] of data) {
									result = v;
									var headerValue = k;
									// Create a new table for each unique header value
									var currencyDiv = $("<div class='col-md-12 fv-row'></div>");
									var tableContainer = $("<div class='table-container'></div>");
									var table = $("<table class='table table-striped table-row-bordered  gs-7'></table>");

									var thead = $("<thead></thead>");

									var graRow = $("<tr class='gra'></tr>");
									thead.append(graRow);

									var currencyRow = $("<tr class='header-row table-header'></tr>");
									var colspan = 33;
									currencyRow
											.append("<td colspan='" + colspan + "' style='color: white; font-size: x-large; font-weight: bold;'>"
													+ headerValue + "</td>");

									thead.append(currencyRow);
									thead.append("<tr></tr>");

									var remainingKeysRow = $("<tr class='header-row'></tr>");
									v[0] = preferredOrder(v[0],["id","payId","acquirerName","channel","minTransactionAmount","maxTransactionAmount","fromDate","bankPreference","bankTdr","bankMinTdrAmt","bankMaxTdrAmt","merchantPreference","merchantTdr","merchantMinTdrAmt","merchantMaxTdrAmt","enableSurcharge","igst","status","tdrStatus","paymentRegion","type","transactionType","currency"]);
										console.log("Data 1:"+JSON.stringify(v[0]));

									for (var key in v[0]) {
										if ( key !== 'status' && key !== 'tdrStatus' && key !== 'paymentRegion' && key !== 'type' && key !== 'transactionType' && key !== 'updatedAt' && key !== 'tDate' && key !== 'fDate') {
											if (key === 'updatedBy') {
												// Skip 'updatedBy' in the header row, it will be added separately at the end
											} else {
												if(key == 'id'){
													remainingKeysRow.append("<th class='selectField'> </th>");

												}else{
													remainingKeysRow.append("<th style='font-weight: bold' class='selectField'>" + header[key] + "</th>");
												}
											}
										}
									}

									remainingKeysRow
											.append("<th style='font-weight: bold' class='selectField'>Edit</th>");
									remainingKeysRow
											.append("<th style='font-weight: bold' class='selectField'>Save</th>");
									remainingKeysRow
											.append("<th style='font-weight: bold' class='selectField'>Cancel</th>");
									remainingKeysRow
											.append("<th style='font-weight: bold' class='selectField'>Add Row</th>");
									remainingKeysRow
											.append("<th style='font-weight: bold' class='selectField'>Delete Row</th>");

									remainingKeysRow.append("<th class='selectField' style='display: none;'>updatedBy</th>");

									remainingKeysRow.append("<th class='selectField' style='display: none;'>status</th>");


									thead.append(remainingKeysRow);

									table.append(thead);



									for(var i=0;i<result.length;i++){
										result[i] = preferredOrder(result[i],["id","payId","acquirerName","channel","minTransactionAmount","maxTransactionAmount","fromDate","bankPreference","bankTdr","bankMinTdrAmt","bankMaxTdrAmt","merchantPreference","merchantTdr","merchantMinTdrAmt","merchantMaxTdrAmt","enableSurcharge","igst","status","tdrStatus","paymentRegion","type","transactionType","currency"]);
										console.log("Data :"+JSON.stringify(result[i]));
										var row = $("<tr></tr>");
										for ( var key in result[i]) {
											if ( key !== 'status' && key !== 'tdrStatus' && key !== 'paymentRegion' && key !== 'type' && key !== 'transactionType' && key !== 'updatedAt' && key !== 'updatedBy' && key !== 'status' && key !== 'tDate' && key !== 'fDate') {
												if (key === 'bankPreference'
														|| key === 'merchantPreference') {
													// Dropdown for bankPreference
													var dropdownOptions = "<select class='selectField' name='" + key + "' disabled>";
													dropdownOptions += "<option value='FLAT' "
															+ (result[i][key] === 'FLAT' ? 'selected'
																	: '')
															+ ">FLAT</option>";
													dropdownOptions += "<option value='PERCENTAGE' "
															+ (result[i][key] === 'PERCENTAGE' ? 'selected'
																	: '')
															+ ">PERCENTAGE</option>";
													dropdownOptions += "</select>";
													row.append("<td>"
															+ dropdownOptions
															+ "</td>");
												} else if (key === 'fDate'
														|| key === 'tDate'
														|| key === 'fromDate'
														|| key === 'updatedAt') {
													// Input fields with datetime-local type


													row.append("<td><input type='datetime-local' class='selectField' name='" + key + "' value='" + result[i][key] + "' disabled></td>");
												} else if (key === 'minTransactionAmount'
														|| key === 'maxTransactionAmount'
														|| key === 'bankTdr'
														|| key === 'bankMinTdrAmt'
														|| key === 'bankMaxTdrAmt'
														|| key === 'merchantTdr'
														|| key === 'merchantMinTdrAmt'
														|| key === 'merchantMaxTdrAmt') {
													// Input fields with number type
													row
															.append("<td><input type='number' class='selectField' name='" + key + "' value='" + result[i][key] + "' disabled></td>");
												} else if (key === 'enableSurcharge') {
													// Checkbox for enableSurcharge
													var isChecked = result[i][key] ? 'checked'
															: '';
													var checkboxOptions = "<input type='checkbox' class='selectField enableSurcharge' disabled name='" + key + "' " + isChecked + ">";
													row.append("<td>"
															+ checkboxOptions
															+ "</td>");
												}else if (key==="id"){

												row
												.append("<td><input type='text' disabled  style='display: none;' class='selectField' name='" + key + "' value='" + result[i][key] + "'></td>");
											}
												else

												{
													row
															.append("<td><input type='text' disabled class='selectField' name='" + key + "' value='" + result[i][key] + "'></td>");
												}
											}

										}

										row
												.append("<td><button class='btn btn-primary btn-xs ml-2 edit-btn'>Edit</button></td>");
										row
												.append("<td><button class='btn btn-primary btn-xs ml-2 save-btn' disabled onclick='saveRow(this, "
														+ i
														+ ")'>Save</button></td>");
										row
												.append("<td><button class='btn btn-primary btn-xs ml-2 cancel-btn' disabled>Cancel</button></td>");

										row
												.append("<td><button class='btn btn-primary btn-xs ml-2 clone-btn'><i class='fas fa-plus'></i></button></td>");
										row
												.append("<td><button class='btn btn-primary btn-xs ml-2 delete-btn'><i class='fas fa-minus'></i></button></td>");
										row.append("<td><input type='hidden' disabled class='selectField' name='" + key + "' value='" + result[i][key] + "'></td>");
										row.append("<td><input type='hidden' disabled class='selectField' name='status' value='ACTIVE'></td>");

										table.append(row);
										tableContainer.append(table);
										currencyDiv.append(tableContainer);
										$("#currencyDiv").append(currencyDiv);
									}
								}
								$(".enableSurcharge").click(function (e) {
									if (e.target.checked) {
										var resp = confirm("Do you really want to enable surcharge?");
										console.log(resp);
										if (!resp) {
											e.preventDefault();
											e.stopPropagation();
											return false;
										}
									}
									return true;
								});
							}
						},
						error : function(xhr, status, error) {
							console.error("Error fetching Currency list:",
									status, error);
						}
					});

			$("#currencyDiv").on(
					"click",
					".edit-btn",
					function() {
						if($(".edit-btn[disabled]").length>0){
						    console.log($(".edit-btn[disabled]"))
							alert("Please save the current row to proceed");
							return;
						}
						var row = $(this).closest("tr");

						row.find(".selectField").prop("disabled", true);
						row.find("select").prop("disabled", false);
						row.find("input[type='datetime-local']").prop(
								"disabled", false);
						row.find("input[type='number']")
								.prop("disabled", false);
						row.find("input[type='checkbox']").prop("disabled",
								false);
						row.find(".edit-btn").prop("disabled", true);

						row.find(".save-btn, .cancel-btn").prop("disabled",
								false);
					});

			$("#currencyDiv").on(
					"click",
					".cancel-btn",
					function() {
						var row = $(this).closest("tr");

						row.find(".selectField").prop("disabled", true);
						row.find("select").prop("disabled", true);
						row.find("input[type='datetime-local']").prop(
								"disabled", true);
						row.find("input[type='checkbox']").prop("disabled",
								true);

						row.find(".edit-btn").prop("disabled", false);

						row.find(".save-btn, .cancel-btn").prop("disabled",
								true);
					});
		}
debugger
$("#currencyDiv").on("click", ".clone-btn", function() {
    var clonedRow = $(this).closest("tr").clone();
	$($(clonedRow).find("td")).find("input").attr("disabled", true);
	$($(clonedRow).find("td")).find("select").attr("disabled", true);
    $($(clonedRow).find("td")[18]).find("button").attr("disabled", false);
	$($(clonedRow).find("td")[19]).find("button").attr("disabled", true);
	$($(clonedRow).find("td")[20]).find("button").attr("disabled", true);
	$($($(clonedRow).find("td")[0]).find("input")[0]).val("0");
    $(this).closest("table").append(clonedRow);
});

    
		$("#currencyDiv").on("click", ".delete-btn", function() {
			$(this).closest("tr").remove();
		});
	</script>

	<script>
		document.getElementById('pgCheckbox').addEventListener('change',
				function() {
					if (this.checked) {
						window.location.href = 'tdrSetting';
					}
				});
	</script>


</body>



</html>