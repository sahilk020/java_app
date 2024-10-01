<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Webhook</title>

<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
<!--begin::Fonts-->
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
<!--end::Fonts-->
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
	left: 3px;
	bottom: 3px;
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

table .toggle-off.btn {
	padding: 0;
	margin: 0;
}

.col-md-6 {
        width: 50%;
        float: right;
        flex: 0 0 auto;
    }
</style>

<script type="text/javascript">
    $(document).ready(function () {

        // Event listener for payId change to fetch saved details
        $("#payId").change(function () {
            var payId = $(this).val();
            resetFormData();
            getSavedDetails(payId);
        });

        // Event listener for toggle buttons
        $('[id^="pay_in_toggle"], [id^="pay_out_toggle"]').change(function () {
            var isChecked = $(this).prop('checked');
            var type = $(this).attr("id").includes("pay_in_toggle") ? "pay_in" : "pay_out";

            // Uncheck all checkboxes of the same type except the one that was changed
            $('[id^="' + type + '_toggle"]').not(this).prop('checked', false);
        });

        function resetFormData() {
            for (var i = 1; i <= 5; i++) {
                $("#pay_in_url" + i).val('');
                $("#pay_out_url" + i).val('');
                $("#pay_in_toggle" + i).prop('checked', false);
                $("#pay_out_toggle" + i).prop('checked', false);
                $("#pay_in_id" + i).val('');
                $("#pay_out_id" + i).val('');
            }
        }

        // Function to save webhook details via AJAX
        function saveWebhookDetails() {
            var payId = $("#payId").val();
            var webhookDetails = [];

            for (var i = 1; i <= 5; i++) {
                var payInUrl = $("#pay_in_url" + i).val().trim();
                var payOutUrl = $("#pay_out_url" + i).val().trim();
                var payInActive = $("#pay_in_toggle" + i).prop('checked');
                var payOutActive = $("#pay_out_toggle" + i).prop('checked');
                var payInId = $("#pay_in_id" + i).val();
                var payOutId = $("#pay_out_id" + i).val();

                // Turn off toggle if URL is blank
                if (payInUrl === "") {
                    payInActive = false;
                }
                if (payOutUrl === "") {
                    payOutActive = false;
                }

                webhookDetails.push({
                    pay_in_url: payInUrl,
                    pay_out_url: payOutUrl,
                    pay_in_active: payInActive,
                    pay_out_active: payOutActive,
                    pay_in_id: payInId,
                    pay_out_id: payOutId
                });
            }

            var obj = {
                pay_id: payId,
                webhook_details: webhookDetails
            };

            // Assuming the AJAX URL is based on the current domain
            var domain = window.location.origin;
            var saveUrl = domain + "/crmws/api/v1/webhook/saveWebhookDetails";

            $.ajax({
                type: "POST",
                url: saveUrl,
                data: JSON.stringify(obj),
                contentType: "application/json",
                success: function (data, status) {
                    alert("Webhook details saved successfully.");
                    window.location.reload();
                },
                error: function (xhr, status, error) {
                    alert("Error saving webhook details: " + xhr.responseText);
                }
            });
        }

        // When URL is empty, disable toggle
        $('[id^="pay_in_toggle"]').change(function () {
            var index = $(this).attr("id").replace("pay_in_toggle", "");
            var payInUrl = $("#pay_in_url" + index).val().trim();
            if (payInUrl === "") {
                $(this).prop('checked', false);
                alert("Please fill in the Pay-In URL before activating.");
            }
        });

        $('[id^="pay_out_toggle"]').change(function () {
            var index = $(this).attr("id").replace("pay_out_toggle", "");
            var payOutUrl = $("#pay_out_url" + index).val().trim();
            if (payOutUrl === "") {
                $(this).prop('checked', false);
                alert("Please fill in the Pay-Out URL before activating.");
            }
        });

        // Form submission handler
        $("#webhookForm").submit(function (e) {
            e.preventDefault();
            $("#saveBtn").attr("disabled", "disabled");
            saveWebhookDetails();
        });
    });
</script>


<script>
    // Function to fetch saved details based on payId
    function getSavedDetails(payId) {
        if (!payId) {
            return;
        }

        var domain = window.location.origin;
        var eventUrl = domain + "/crmws/api/v1/webhook/webhookData?payId=" + payId;

        $.ajax({
            type: "GET",
            url: eventUrl,
            success: function (returnData) {
                if (returnData && returnData.data.length > 0) {
                    var webhookDetails = returnData.data || [];

                    // Populate each pair of webhook details
                    var payInIndex = 1;
                    webhookDetails.forEach(function (details, index) {
                        if (details.pay_in_url !== null) {
                            var payInUrlId = "#pay_in_url" + (payInIndex);
                            var payInToggleId = "#pay_in_toggle" + (payInIndex);
                            var payInId = "#pay_in_id" + (payInIndex);

                            $(payInUrlId).val(details.pay_in_url || '');
                            $(payInToggleId).prop('checked', details.pay_in_active || false);
                            $(payInId).val(details.id || '');
                            payInIndex = payInIndex + 1;
                        }
                    });

                    var payoutindex = 1;
                    webhookDetails.forEach(function (details, index) {
                        if (details.pay_out_url !== null) {
                            var payOutUrlId = "#pay_out_url" + (payoutindex);
                            var payOutToggleId = "#pay_out_toggle" + (payoutindex);
                            var payOutId = "#pay_out_id" + (payoutindex);

                            $(payOutUrlId).val(details.pay_out_url || '');
                            $(payOutToggleId).prop('checked', details.pay_out_active || false);
                            $(payOutId).val(details.id || '');
                            payoutindex = payoutindex + 1;
                        }
                    });
                } else {
                    alert("No webhook URL's are registered for the selected merchant!")
                }
            },
            error: function (xhr, status, error) {
                console.error("Error fetching webhook data:", status);
            }
        });
    }
</script>


<script>

// Function to fetch saved details based on payId
function getSavedDetails(payId) {
    if (!payId) {
        return;
    }

    var domain = window.location.origin;
    var eventUrl = domain + "/crmws/api/v1/webhook/webhookData?payId=" + payId;

    $.ajax({
        type: "GET",
        url: eventUrl,
        success: function (returnData) {
            if (returnData &&  returnData.data.length>0) {
                var webhookDetails = returnData.data || [];

                // Populate each pair of webhook details
                var payInIndex=1;
                webhookDetails.forEach(function (details, index) {
                   if(details.pay_in_url!==null){
                    var payInUrlId = "#pay_in_url" + (payInIndex);
                   
                   var payInToggleId = "#pay_in_toggle" + (payInIndex);

                   var payInId = "#pay_in_id" + (payInIndex);

                   
                   $(payInUrlId).val(details.pay_in_url || '');
                  // $(payInUrlId).prop("readonly", true);
                   $(payInToggleId).prop('checked', details.pay_in_active || false);
                   $(payInId).val(details.id || '');
                   payInIndex=payInIndex+1;
                   }
                    
                   
                });

                var payoutindex=1;
                webhookDetails.forEach(function (details, index) {
                    if(details.pay_out_url!==null){
                var payOutUrlId = "#pay_out_url" + (payoutindex);
                    var payOutToggleId = "#pay_out_toggle" + (payoutindex);
				var payOutId = "#pay_out_id" + (payoutindex);
              
                    $(payOutUrlId).val(details.pay_out_url || '');
               //   $(payOutUrlId).prop("readonly", true);
                    $(payOutToggleId).prop('checked', details.pay_out_active || false);
                    $(payOutId).val(details.id || '');
                    payoutindex=payoutindex+1;
                    }
                });
            } else {

                alert("No webhook url's is registered for selected merchant!")
                // Clear fields if no data found
                // for (var i = 1; i <= 5; i++) {
                //     $("#pay_in_url" + i).val('');
                //     $("#pay_out_url" + i).val('');
                //     $("#pay_in_toggle" + i).prop('checked', false);
                //     $("#pay_out_toggle" + i).prop('checked', false);
                // }
            }
        },
        error: function (xhr, status, error) {
            console.error("Error fetching webhook data:", status);
        }
    });
}


</script>

</head>

<body>
	<div class="content d-flex flex-column flex-column-fluid"
		id="kt_content">
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
						Webhook</h1>
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
						<li class="breadcrumb-item text-muted">Webhook</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Webhook</li>
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
						<div class="card">
							<div class="card-body">
								<div class="row my-3 align-items-center">
									<!-- <div class="col-md-3 fv-row"> -->
									<div class="col-auto my-2 merchant-text">
										<p class="text-center m-0 w-100">
											<b>Webhook</b>
										</p class="text-sm-center m-0">
									</div>
									<div class="col-auto my-2">

										<p>A webhook in web development is a method of augmenting
											or altering the behaviour of a web page or web application
											with custom callbacks. Webhooks are "user-defined HTTP
											callbacks". They are usually triggered by some event, such as
											pushing code to a repository or a comment being posted to a
											blog. When that event occurs, the source site makes an HTTP
											request to the URL configured for the webhook. Users can
											configure them to cause events on one site to invoke behavior
											on another.</p>
									</div>


									<div class="col-auto my-2 merchant-text">
										<p class="text-center m-0 w-100">
											<b>Merchant*</b>
										</p class="text-sm-center m-0">
									</div>
									<div class="col-auto my-2">
										<s:select name="payId" id="payId"
											class="form-select form-select-solid merchantPayId"
											listKey="payId" listValue="businessName" list="merchants"
											headerKey="" headerValue="Select Merchant"
											onchange="getSavedDetails(this.value);" />

									</div>

									<s:hidden name="saveUri" id="saveUri" value="%{saveEvent}" />
									<s:form id="webhookForm">
										<div class="indent raw col-md-12 formboxRR">
											<div class="col-md-6 addfildn">
												<!-- Pay-in URLs -->
												<div class="rkb parent-float">
													<div class="fl_wrap">
														<label class='fl_label'
															style="padding: 0; font-size: 13px; font-weight: 800; margin-top: 20px;">Pay-in
															URLs*</label>
													</div>
													<!-- First Pay-in URL -->
													<div class="addfildn" style="margin-bottom: 10px;">
														<div class="fl_wrap"
															style="display: flex; align-items: center;">
															<s:textfield id="pay_in_url1" maxlength="255"
																style="flex: 1 0 auto; margin-right: 10px; padding: 5px; font-weight: 500; font-size: 14px; width: 200%; height: 20%;"
																class="form-control form-control-solid"
																name="pay_in_url1" type="text"></s:textfield>
															<label class="switch"
																style="margin-left: 40%; flex: 0 0 auto;"> <input
																type="checkbox" id="pay_in_toggle1"
																name="pay_in_toggle1"> <span
																class="slider round"></span>
															</label>
															<input id="pay_in_id1" class="input-control " type="hidden" disabled/>
														</div>
													</div>

													<!-- Second Pay-in URL -->
													<div class="addfildn" style="margin-bottom: 10px;">
														<div class="fl_wrap"
															style="display: flex; align-items: center;">
															<s:textfield id="pay_in_url2" maxlength="255"
																style="flex: 1 0 auto; margin-right: 10px; padding: 5px; font-weight: 500; font-size: 14px; width: 200%; height: 20%;"
																class="form-control form-control-solid"
																name="pay_in_url2" type="text"></s:textfield>
															<label class="switch"
																style="margin-left: 40%; flex: 0 0 auto;"> <input
																type="checkbox" id="pay_in_toggle2"
																name="pay_in_toggle2"> <span
																class="slider round"></span>
															</label>
															<input id="pay_in_id2" class="input-control" type="hidden" disabled/>
														</div>
													</div>
													<!-- Third Pay-in URL -->
													<div class="addfildn" style="margin-bottom: 10px;">
														<div class="fl_wrap"
															style="display: flex; align-items: center;">
															<s:textfield id="pay_in_url3" maxlength="255"
																style="flex: 1 0 auto; margin-right: 10px; padding: 5px; font-weight: 500; font-size: 14px; width: 200%; height: 20%;"
																class="form-control form-control-solid"
																name="pay_in_url3" type="text"></s:textfield>
															<label class="switch"
																style="margin-left: 40%; flex: 0 0 auto;"> <input
																type="checkbox" id="pay_in_toggle3"
																name="pay_in_toggle3"> <span
																class="slider round"></span>
															</label>
															<input id="pay_in_id3" class="input-control" type="hidden" disabled/>
														</div>
													</div>
													<!-- Fourth Pay-in URL -->
													<div class="addfildn" style="margin-bottom: 10px;">
														<div class="fl_wrap"
															style="display: flex; align-items: center;">
															<s:textfield id="pay_in_url4" maxlength="255"
																style="flex: 1 0 auto; margin-right: 10px; padding: 5px; font-weight: 500; font-size: 14px; width: 200%; height: 20%;"
																class="form-control form-control-solid"
																name="pay_in_url3" type="text"></s:textfield>
															<label class="switch"
																style="margin-left: 40%; flex: 0 0 auto;"> <input
																type="checkbox" id="pay_in_toggle4"
																name="pay_in_toggle4"> <span
																class="slider round"></span>
															</label>
															<input id="pay_in_id4" class="input-control " type="hidden" disabled/>
														</div>
													</div>
													<!-- Fifth Pay-in URL -->
													<div class="addfildn">
														<div class="fl_wrap"
															style="display: flex; align-items: center;">
															<s:textfield id="pay_in_url5" maxlength="255"
																style="flex: 1 0 auto; margin-right: 10px; padding: 5px; font-weight: 500; font-size: 14px; width: 200%; height: 20%;"
																class="form-control form-control-solid"
																name="pay_in_url3" type="text"></s:textfield>
															<label class="switch"
																style="margin-left: 40%; flex: 0 0 auto;"> <input
																type="checkbox" id="pay_in_toggle5"
																name="pay_in_toggle5"> <span
																class="slider round"></span>
															</label>
															<input id="pay_in_id5" class="input-control " type="hidden" disabled/>
														</div>
													</div>
												</div>
											</div>

											<div class="col-md-6 addfildn">
												<!-- Pay-out URLs -->
												<div class="rkb parent-float">
													<div class="fl_wrap">
														<label class='fl_label'
															style="padding: 0; font-size: 13px; font-weight: 800; margin-top: 20px;">Pay-out
															URLs*</label>
													</div>
													<!-- First Pay-out URL -->
													<div class="addfildn" style="margin-bottom: 10px;">
														<div class="fl_wrap"
															style="display: flex; align-items: center;">
															<s:textfield id="pay_out_url1" maxlength="255"
																style="flex: 1 0 auto; margin-right: 10px; padding: 5px; font-weight: 500; font-size: 14px; width: 200%; height: 20%;"
																class="form-control form-control-solid"
																name="pay_out_url1" type="text"></s:textfield>
															<label class="switch"
																style="margin-left: 40%; flex: 0 0 auto;"> <input
																type="checkbox" id="pay_out_toggle1" 
																name="pay_out_toggle1"> <span
																class="slider round"></span>
															</label>
															<input id="pay_out_id1" class="input-control " type="hidden" disabled/>
														</div>
													</div>
													<!-- Second Pay-out URL -->
													<div class="addfildn" style="margin-bottom: 10px;">
														<div class="fl_wrap"
															style="display: flex; align-items: center;">
															<s:textfield id="pay_out_url2" maxlength="255"
																style="flex: 1 0 auto; margin-right: 10px; padding: 5px; font-weight: 500; font-size: 14px; width: 200%; height: 20%;"
																class="form-control form-control-solid"
																name="pay_out_url2" type="text"></s:textfield>
															<label class="switch"
																style="margin-left: 40%; flex: 0 0 auto;"> <input
																type="checkbox" id="pay_out_toggle2"
																name="pay_out_toggle2"> <span
																class="slider round"></span>
															</label>
															<input id="pay_out_id2" class="input-control " type="hidden" disabled/>
														</div>
													</div>
													<!-- Third Pay-out URL -->
													<div class="addfildn" style="margin-bottom: 10px;">
														<div class="fl_wrap"
															style="display: flex; align-items: center;">
															<s:textfield id="pay_out_url3" maxlength="255"
																style="flex: 1 0 auto; margin-right: 10px; padding: 5px; font-weight: 500; font-size: 14px; width: 200%; height: 20%;"
																class="form-control form-control-solid"
																name="pay_out_url3" type="text"></s:textfield>
															<label class="switch"
																style="margin-left: 40%; flex: 0 0 auto;"> <input
																type="checkbox" id="pay_out_toggle3"
																name="pay_out_toggle3"> <span
																class="slider round"></span>
															</label>
															<input id="pay_out_id3" class="input-control " type="hidden" disabled/>
														</div>
													</div>
													<!-- Fourth Pay-out URL -->
													<div class="addfildn" style="margin-bottom: 10px;">
														<div class="fl_wrap"
															style="display: flex; align-items: center;">
															<s:textfield id="pay_out_url4" maxlength="255"
																style="flex: 1 0 auto; margin-right: 10px; padding: 5px; font-weight: 500; font-size: 14px; width: 200%; height: 20%;"
																class="form-control form-control-solid"
																name="pay_out_url3" type="text"></s:textfield>
															<label class="switch"
																style="margin-left: 40%; flex: 0 0 auto;"> <input
																type="checkbox" id="pay_out_toggle4"
																name="pay_out_toggle4"> <span
																class="slider round"></span>
															</label>
															<input id="pay_out_id4" class="input-control " type="hidden" disabled/>
														</div>
													</div>
													<!-- Fifth Pay-out URL -->
													<div class="addfildn" style="margin-bottom: 10px;">
														<div class="fl_wrap"
															style="display: flex; align-items: center;">
															<s:textfield id="pay_out_url5" maxlength="255"
																style="flex: 1 0 auto; margin-right: 10px;  padding: 5px; font-weight: 500; font-size: 14px; width: 200%; height: 20%;"
																class="form-control form-control-solid"
																name="pay_out_url3" type="text"></s:textfield>
															<label class="switch"
																style="margin-left: 40%; flex: 0 0 auto;"> <input
																type="checkbox" id="pay_out_toggle5"
																name="pay_out_toggle5"> <span
																class="slider round"></span>
															</label>
															<input id="pay_out_id5" class="input-control " type="hidden" disabled/>
														</div>
													</div>
												</div>
											</div>
											<!-- Hidden Fields -->
											<s:hidden name="association_id" id="association_id"></s:hidden>
											<s:hidden name="subscriber_id" id="subscriber_id"></s:hidden>

											<!-- Submit Button -->
											<div class="col-md-12 addfildn" style="margin-top: 20px;">
												<div class="rkb">
													<div class="addfildn">
														<div class="fl_wrap">
															<s:submit method="submit" id="saveBtn"
																style="margin-right: 10px;"
																class="btn btn-primary btn-md" value="Save"></s:submit>
														</div>
													</div>
												</div>
											</div>
										</div>
									</s:form>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
</body>

</html>