<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Chargeback List</title>


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

<style>
button#submitInvoice {
	margin-left: 8%;
	padding: 7px 32px 7px 32px;
}

button#submitFile {
	margin-left: 11%;
}

button#submitPOD {
	margin-left: 10%;
	padding: 7px 44px 7px 44px;
}

.switch {
	position: relative;
	display: inline-block;
	width: 30px;
	height: 17px;
}

i.material-icons {
	margin-left: 5px !important;
	margin-top: 0px !important;
	cursor: pointer;
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

svg {
	margin-top: 1vh !important;
}

.chargebackChatBoxClass::-webkit-scrollbar {
	width: 4px;
	/* Width of the scrollbar */
}

.chargebackChatBoxClass::-webkit-scrollbar-track {
	background-color: #fd7e14;
	/* Background color of the scrollbar track */
}

.chargebackChatBoxClass::-webkit-scrollbar-thumb {
	background-color: #fd7e14;
}

.chargebackChatBoxClass::-webkit-scrollbar-thumb:hover {
	background-color: #fd7e14;
}

/* Firefox */
.chargebackChatBoxClass::-moz-scrollbar {
	width: 4px;
	/* Width of the scrollbar */
}

.chargebackChatBoxClass::-moz-scrollbar-track {
	background-color: #fd7e14;
	;
	/* Background color of the scrollbar track */
}

.chargebackChatBoxClass::-moz-scrollbar-thumb {
	background-color: #fd7e14;
	;
}

.chargebackChatBoxClass::-moz-scrollbar-thumb:hover {
	background-color: #fd7e14;
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
<script type="text/javascript">
        var flag = 0;
        $(document).ready(function () {

          $("#merchant_pay_id").select2();
        });
      </script>
<script>
        $(document).ready(function () {

        	$("#cb_intimation_date").flatpickr({
        		  minDate: "today",
        		  maxDate: new Date(),
        		  dateFormat: 'Y-m-d',
        		  defaultDate: "today",
        		});

        		$("#cb_deadline_date").flatpickr({
        		  minDate: "today",
        		  dateFormat: 'Y-m-d',
        		  defaultDate: new Date().fp_incr(4), // Default date as today + 4 days
        		});

          function convert(str) {
            var date = new Date(str), mnth = ("0" + (date.getMonth() + 1))
              .slice(-2), day = ("0" + date.getDate()).slice(-2);
            //return [date.getFullYear(), mnth, day].join("-");
            return [day, mnth, date.getFullYear()].join("-");
          }



        });
      </script>
<script>

        function getCBReason() {
          debugger
          var cb_reason_code = $("#cb_reason_code").val();


          if (cb_reason_code == "" || cb_reason_code == null) {
            alert("Please Select CB Reasoncode");
            $("#cb_reason").val("");
            return false;
          }

          var urls = new URL(window.location.href);
          var domain = urls.origin;
          $.ajax({
            type: "GET",
            url: domain + "/crmws/getcbReasonDescriptionFromcbReasonCode/" + cb_reason_code,
            // url : "http://localhost:8080/crmws/commentlist?caseId="+ caseId,
            success: function (data, status) {
              $("#cb_reason").val(data.cbReasonDescription);
            },
            error: function (status) {
            }
          });
        }

        function GetDataBasesOnPGRefNumber() {
        	 debugger
        	if(validPgRefNum()){
        	
         
          var pgRefNo = document.getElementById("pgRefNo").value;
          if (pgRefNo == '') {
            error_pgRefNo = 'please Enter pg Ref no.';
            $('#error_pgRefNo').text(error_pgRefNo);
            $('#pgRefNo').addClass('error');
            return false
          }
          var urls = new URL(window.location.href);
          var domain = urls.origin;
          $.ajax({
            type: "GET",
            url: domain + "/crmws/by-pgrefno",
            // url : "http://localhost:8080/crmws/by-pgrefno",
            data: {
              "pgRefNo": pgRefNo
            },
            success: function (data, status) {
              debugger
              if(data!=''){
              $("#currencyName").val(data.currencyName);
              $("#cb_case_id").val(data.cb_case_id);
              $("#pgRefNo").val(data.pgRefNo);
              $("#txn_amount").val(data.txn_amount);
              $("#date_of_txn").val(data.date_of_txn);
              $("#merchant_txn_id").val(data.merchant_txn_id);
              $("#order_id").val(data.order_id);
              $("#bank_txn_id").val(data.bank_txn_id);
              $("#mode_of_payment").val(data.mode_of_payment);
              $("#acq_name").val(data.acq_name);
              $("#settlement_date").val(data.settlement_date);
              $("#customer_phone").val(data.customer_phone);
              $("#customer_name").val(data.customer_name);
              $("#email").val(data.email);
              $("#nemail").val(data.nemail);
              $("#merchant_pay_id").val(data.merchant_pay_id);
              $("#cb_amount").val(data.txn_amount);


              var url = new URL(window.location.href).origin
                + "/crmws/getAllChargebackReasons";


              $.ajax({
                type: "GET",

                url: url,

                success: function (result, status, xhr) {
                  debugger
                  $("#cb_reason_code").append("<option value=" + "" + ">" + "Please Select CB Reason Code" + "</option>");
                  result.forEach((element, index, array) => {
                    $("#cb_reason_code").append("<option value=" + element.cbReasonCode + ">" + element.cbReasonCode + "</option>");

                  });
                },
                error: function (xhr, status, error) {
                  alert("xhr : " + xhr + "\nstatus : " + status + "\nerror : " + error);
                }
              });
              }else{
            	  alert("No Transaction Found For This PGREF NO.");
  			  }
            }

          });
        	}
        }
        $(document).ready(function () {
          $("#cb_reason_code").select2();

          // $('#merchant_pay_id').change(function (event) {
          //   var merchantVal = document.getElementById("merchant_pay_id").value;
          //   var urls = new URL(window.location.href);
          //   var domain = urls.origin;
          //   $.ajax({

          //     type: "GET",
          //     url: domain + "/crmws/pgrefno-by-payid",
          //     // url : "http://localhost:8080/crmws/pgrefno-by-payid",
          //     data: {
          //       "payId": merchantVal,

          //     },
          //     success: function (data, status) {

          //       var s = '<option value="">select Pg Ref No</option>';
          //       for (var i = 0; i < data.length; i++) {
          //         s += '<option value="' + data[i] + '">' + data[i] + '</option>';
          //       }
          //       $("#pgRefNo").html(s);
          //     }
          //   });
          // });

        });

      </script>
</head>

<body id="kt_body"
	class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed toolbar-tablet-and-mobile-fixed aside-enabled aside-fixed"
	style="-kt-toolbar-height: 55px; - -kt-toolbar-height-tablet-and-mobile: 55px">
	<div class="content d-flex flex-column flex-column-fluid"
		id="kt_content">
		<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container"
				class="container-fluid d-flex flex-stack">
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
					data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
					class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Create
						Chargeback</h1>
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
						<li class="breadcrumb-item text-muted">Dispute Management</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Create Chargeback</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->
			</div>
		</div>
		<!--end::Container-->

		<div class="post d-flex flex-column-fluid" id="kt_post">
			<!--begin::Container-->
			<div id="kt_content_container" class="container-xxl">
				<div>
					<div>
						<div class="card">
							<div class="card-body">
								<s:form id="chargebackForm"
									class="form mb-15 fv-plugins-bootstrap5 fv-plugins-framework">
									<div class="row my-3 align-items-center">
										<div class="col-lg-3 my-2" style="display: none;">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">Select merchant</span>
											</label>
											<s:select name="merchant_pay_id" id="merchant_pay_id"
												headerKey=" " headerValue="Select Merchant"
												class="form-select form-select-solid adminMerchants"
												listKey="payId" listValue="businessName" list="merchants" />
											<span id="error_merchant_pay_id" class="error"></span>
										</div>
										<div class="col-lg-3 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">Enter PgRefNo</span>
											</label>
											<!-- <select name="pgRefNo" id="pgRefNo" class="form-select form-select-solid adminMerchants" /> -->
											<!-- <select name="pgRefNo" id="pgRefNo" class="form-select form-select-solid adminMerchants">
                            <option value="" selected>Please Select Value</option>
                          </select> -->
											<input type="text" maxlength="16" name="pgRefNo" id="pgRefNo"
												class="form-control form-control-solid"> <span
												id="error_pgRefNo" class="error"></span>
										</div>

										<div class="col-lg-3 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">&nbsp;</span>
											</label>
											<!-- <select name="pgRefNo" id="pgRefNo" class="form-select form-select-solid adminMerchants" /> -->
											<!-- <select name="pgRefNo" id="pgRefNo" class="form-select form-select-solid adminMerchants">
                            <option value="" selected>Please Select Value</option>
                          </select> -->

											<button id="EnterPgRefNum" onclick="GetDataBasesOnPGRefNumber()"
												class="btn btn-primary" type="button">Submit</button>
										</div>
									</div>
									<div class="row my-3 align-items-center">
										<div class="col-lg-4 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">CB Case Id</span>
											</label>
											<s:textfield type="text" id="cb_case_id" name="cb_case_id"
												readOnly="true" class="form-control form-control-solid"
												autocomplete="false" />
											<span id="error_cb_case_id" class="error"></span>
										</div>
										<div class="col-lg-4 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">Pg Case Id</span>
											</label>

											<s:textfield type="text" id="pg_case_id" name="pg_case_id"
												class="form-control form-control-solid" autocomplete="false" />
											<span id="error_pg_case_id" class="error"></span>
										</div>

										<div class="col-lg-4 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">Transaction Amount</span>
											</label>
											<s:textfield type="text" id="txn_amount" name="txn_amount"
												readOnly="true" class="form-control form-control-solid"
												autocomplete="false" />
											<span id="error_txn_amount" class="error"></span>
										</div>
									</div>
									<div class="row my-3 align-items-center">
										<div class="col-lg-4 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">Date Of Txn</span>
											</label>
											<s:textfield id="date_of_txn" name="date_of_txn" type="text"
												readOnly="true" class="form-control form-control-solid"
												autocomplete="false" />
											<span id="error_date_of_txn" class="error"></span>
										</div>

										<div class="col-lg-4 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">CB Amount</span>
											</label>
											<s:textfield id="cb_amount" name="cb_amount" type="text"
												class="form-control form-control-solid" autocomplete="false"
												readOnly="true" />
											<span id="error_cb_amount" class="error"></span>
										</div>




										<div class="col-lg-4 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">CB Reason Code</span>
											</label> <select id="cb_reason_code" name="cb_reason_code"
												class="form-select form-select-solid adminMerchants"
												onchange="getCBReason()">

											</select> <span id="error_cb_reason_code" class="error"></span>
										</div>
									</div>

									<div class="row my-3 align-items-center">

										<div class="col-lg-4 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">CB Reason</span>
											</label>

											<s:textfield id="cb_reason" name="cb_reason" type="text"
												class="form-control form-control-solid" autocomplete="false"
												readOnly="true" />
											<span id="error_cb_reason" class="error"></span>
										</div>

										<div class="col-lg-4 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">CB Intimation Date</span>
											</label>


											<!--begin::Icon-->
											<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
											<span class="svg-icon svg-icon-2 position-absolute mx-4">
												<svg width="24" height="24" viewBox="0 0 24 24" fill="none"
													xmlns="http://www.w3.org/2000/svg"> <path
													opacity="0.3"
													d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
													fill="currentColor"></path> <path
													d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
													fill="currentColor"></path> <path
													d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
													fill="currentColor"></path> </svg>
											</span>
											<!--end::Svg Icon-->
											<!--end::Icon-->
											<!--begin::Datepicker-->
											<input
												class="form-control form-control-solid ps-12 flatpickr-input"
												placeholder="Select a date" name="cb_intimation_date"
												id="cb_intimation_date" type="text"> <span
												id="error_cb_intimation_date" class="error"></span>

										</div>

										<div class="col-lg-4 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">Mode Of Payment</span>
											</label>
											<s:textfield id="mode_of_payment" name="mode_of_payment"
												type="text" readOnly="true"
												class="form-control form-control-solid" autocomplete="false" />
											<span id="error_mode_of_payment" class="error"></span>

										</div>
									</div>

									<div class="row my-3 align-items-center">
										<div class="col-lg-4 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">Acquirer Name</span>
											</label>
											<s:textfield id="acq_name" name="acq_name" type="text"
												readOnly="true" class="form-control form-control-solid"
												autocomplete="false" />
											<span id="error_acq_name" class="error"></span>
										</div>

										<div class="col-lg-4 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Settlement Date</span>
											</label>
											<s:textfield id="settlement_date" name="settlement_date"
												type="text" class="form-control form-control-solid"
												autocomplete="false" readOnly="true" />
											<span id="error_settlement_date" class="error"></span>
										</div>

										<div class="col-lg-4 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">Customer Name</span>
											</label>
											<s:textfield type="text" id="customer_name"
												name="customer_name" readOnly="true"
												class="form-control form-control-solid" autocomplete="false" />
											<span id="error_customer_name" class="error"></span>
										</div>
									</div>

									<div class="row my-3 align-items-center">
										<div class="col-lg-4 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Customer Phone</span>
											</label>
											<s:textfield id="customer_phone" name="customer_phone"
												type="text" readOnly="true" maxlength="10"
												class="form-control form-control-solid" autocomplete="false" />
											<span id="error_customer_phone" class="error"></span>
										</div>

										<div class="col-lg-4 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">Email</span>
											</label>
											<s:textfield type="text" id="email" name="email"
												readOnly="true" class="form-control form-control-solid"
												autocomplete="false" />
											<span id="error_email" class="error"></span>
										</div>

										<div class="col-lg-4 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">CB Deadline Date</span>
											</label>
											<!--begin::Icon-->
											<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
											<span class="svg-icon svg-icon-2 position-absolute mx-4">
												<svg width="24" height="24" viewBox="0 0 24 24" fill="none"
													xmlns="http://www.w3.org/2000/svg"> <path
													opacity="0.3"
													d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
													fill="currentColor"></path> <path
													d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
													fill="currentColor"></path> <path
													d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
													fill="currentColor"></path> </svg>
											</span>
											<!--end::Svg Icon-->
											<!--end::Icon-->
											<!--begin::Datepicker-->
											<input
												class="form-control form-control-solid ps-12 flatpickr-input"
												placeholder="Select a date" name="cb_deadline_date"
												id="cb_deadline_date" type="text"> <span
												id="error_cb_deadline_date" class="error"></span>
										</div>
									</div>

									<div class="row my-3 align-items-center">
										<div class="col-lg-4 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">Merchant Txn Id</span>
											</label>

											<s:textfield id="merchant_txn_id" name="merchant_txn_id"
												type="text" readOnly="true"
												class="form-control form-control-solid" autocomplete="false" />
											<span id="error_merchant_txn_id" class="error"></span>
										</div>

										<div class="col-lg-4 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">OrderId</span>
											</label>

											<s:textfield id="order_id" name="order_id" type="text"
												readOnly="true" class="form-control form-control-solid"
												autocomplete="false" />
											<span id="error_order_id" class="error"></span>
										</div>

										<div class="col-lg-4 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">Bank Txn Id</span>
											</label>

											<s:textfield id="bank_txn_id" name="bank_txn_id" type="text"
												readOnly="true" class="form-control form-control-solid"
												autocomplete="false" />
											<span id="error_bank_txn_id" class="error"></span>
										</div>
									</div>



									<div class="row my-3 align-items-center">
										<div class="col-lg-4 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">Notification Email</span>
											</label>
											<s:textfield type="text" id="nemail" name="nemail"
												class="form-control form-control-solid" autocomplete="false" />
											<span id="nerror_email" class="error"></span>
										</div>
									</div>
									<div class="row my-3 align-items-center">
										<div class="col-lg-2 my-2">
											<s:if test="%{#session.USER.UserGroup.group == 'ADMIN' || #session.USER.UserGroup.group == 'Sub Admin' || #session.USER.UserGroup.group == 'Risk'}">		
												<button type="button" value="" id="closeCb"
													class="btn btn-primary" onclick="closeChargeback();">Close
													CB</button>
												<s:submit value="submit" id="submit" method="validate();"
													class="hide_accept_chargeback btn btn-primary">
												</s:submit>
											</s:if><s:else>
                        <button type="button" id="status" name="status"
												onclick="acceptChargeback();"
												class="hide_accept_chargeback btn btn-primary">Accept
												Chargeback</button>
                      </s:else>

										</div>

										<!-- <div class="col-lg-2 my-2">
										
										</div> -->
									</div>
									
									<s:textfield id="currencyName" name="currencyName" type="hidden"
												readOnly="true" class="form-control form-control-solid"
												/>
									
								</s:form>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>



		<div class="post d-flex flex-column-fluid" id="kt_post">
			<!--begin::Container-->
			<div id="kt_content_container" class="container-xxl">
				<div>
					<div>
						<div class="card">
							<div class="card-body" id="hideAfterAccepted">

								<div id="uploadDiv" style="width: 70%; margin: 62px auto;">
									<div id="chargeBackFilesDiv"></div>
									<form id="uploadFormPod" enctype="multipart/form-data">
										<input type="hidden" name="id" id="id" /> <input
											type="hidden" name="payId" id="payId" /> <input
											type="hidden" name="caseId" id="caseId" /> <label
											id="fileUpload" class="hide_accept_chargeback">POD
											Upload:</label>&nbsp;&nbsp;&nbsp;&nbsp; <input type="file"
											name="Podfile" id="Podfile"
											accept="image/gif, image/jpeg,image/png,application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/vnd.ms-excel"
											class="hide_accept_chargeback"> <span
											id="documentError" class="error"></span>
										<button type="submit"
											class="btn btn-primary hide_accept_chargeback"
											style="margin-top: 10px;" name="submitPOD" id="submitPOD">Upload
											POD</button>
									</form>



									<form id="uploadFormInvoice" enctype="multipart/form-data">
										<input type="hidden" name="id" id="idd" /> <input
											type="hidden" name="payId" id="payIdd" /> <input
											type="hidden" name="caseId" id="caseIdd" /> <label
											id="fileUpload" class="hide_accept_chargeback">Invoice
											Upload:</label>&nbsp;&nbsp;&nbsp;&nbsp; <input type="file"
											name="invoiceFile" id="invoiceFile"
											accept="image/gif, image/jpeg,image/png,application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/vnd.ms-excel"
											class="hide_accept_chargeback"> <span
											id="documentError" class="error"></span>
										<button type="submit"
											class="btn btn-primary hide_accept_chargeback"
											style="margin-top: 10px;" name="submitInvoice"
											id="submitInvoice">Upload Invoice</button>
									</form>



									<form id="uploadForm" enctype="multipart/form-data">
										<input type="hidden" name="id" id="iddd" /> <input
											type="hidden" name="payId" id="payIddd" /> <input
											type="hidden" name="caseId" id="caseIddd" /> <label
											id="fileUpload" class="hide_accept_chargeback">File
											Upload:</label>&nbsp;&nbsp;&nbsp;&nbsp; <input type="file"
											name="file" id="file"
											accept="image/gif, image/jpeg,image/png,application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/vnd.ms-excel"
											class="hide_accept_chargeback"> <span
											id="documentError" class="error"></span>
										<button type="submit"
											class="btn btn-primary hide_accept_chargeback"
											style="margin-top: 10px;" name="submitFile" id="submitFile">Upload
											Document</button>
									</form>
								</div>





								<div class="container-fluid" id="ChatBox">

									<!-- <div id="chatDiv"> -->
									<div class="row">
										<div class="col-12">
											<label><strong>Comment Box</strong></label>
										</div>
									</div>
									<div class="row">
										<div class="col-12" style="overflow-y: hidden;">
											<div style="padding-top: 30px;">
												<div id="chargeBackChatBox" class="chargebackChatBoxClass"
													style="overflow-y: scroll; height: 100px; overflow-x: hidden;"></div>
											</div>
										</div>
									</div>
									<div class="row">
										<div class="col-12">
											<textarea id="comment" name="comment"
												placeholder="Enter message here..." style="width: 90%;"></textarea>
										</div>
									</div>
									<div class="row">
										<div class="col-2">
											<i class="btn btn-primary" style="cursor: pointer;"
												id="materialSendIcon" onclick="comment();">send</i><br>
											<span id="chatError" class="error"></span>
											<!-- </div> -->
										</div>
									</div>
								</div>

							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<script>


        function comment() {
          var regex = /^[A-Z a-z 0-9]+$/;
          if ($.trim($('#comment').val()).length == 0) {
            chatError = 'Enter Comment';
            $('#chatError').text(chatError);
            $('#comment').addClass('error');
          }
          else {
            if (!regex.test($('#comment').val())) {
              chatError = 'Special characters are not allowed in comment';
              $('#chatError').text(chatError);
              $('#comment').addClass('error');
            }
            else if ($.trim($('#comment').val()).length > 200) {
              chatError = 'Comment should not be greater than 200 characters';
              $('#chatError').text(chatError);
              $('#comment').addClass('error');
            }
            else {
              chatError = '';
              $('#chatError').text(chatError);
              $('#comment').removeClass('error');
            }
          }
          if (chatError != '') {
            return false;
          }
          var date = new Date();
          var user = "<s:property value='%{#session.USER.emailId}'/>";
          var urls = new URL(window.location.href);
          var domain = urls.origin;
          var obj = {

            "comment": $("#comment").val(),
            "caseId": $("#cb_case_id").val(),
            "createdOn": date,
            "createdBy": user,
          }

          $.ajax({
            type: "POST",
            url: domain + "/crmws/commentSave",
            // url :"http://localhost:8080/crmws/commentSave" ,
            data: JSON.stringify(obj),
            contentType: "application/json",
            success: function (data, status) {
              debugger
              alert("Comment Added Successfully");
              window.location.reload();
            },
            error: function (status) {
            }
          });

        }


        function acceptChargeback() {
          var cbCode = $("#cb_reason_code").val();

          if (cbCode == "" || cbCode == null) {
            alert("Please Select CB Reason Code");
            return false;
          }

          var id = '<s:property value="id"/>';
          var urls = new URL(window.location.href);
          var userEmailId = "<s:property value='%{#session.USER.emailId}'/>";
          var userType = "<s:property value='%{#session.USER.UserGroup.group}'/>";
          var domain = urls.origin;
          var obj = {

            "merchant_pay_id": $("#merchant_pay_id").val(),
            "pgRefNo": $("#pgRefNo").val(),
            "cb_case_id": $("#cb_case_id").val(),
            "date_of_txn": $("#date_of_txn").val(),
            "txn_amount": $("#txn_amount").val(),
            "pg_case_id": $("#pg_case_id").val(),
            "merchant_txn_id": $("#merchant_txn_id").val(),
            "order_id": $("#order_id").val(),
            "bank_txn_id": $("#bank_txn_id").val(),
            "cb_amount": $("#cb_amount").val(),
            "cb_reason": $("#cb_reason").val(),
            "cb_reason_code": $("#cb_reason_code").val(),
            "cb_intimation_date": $("#cb_intimation_date").val(),
            "cb_deadline_date": $("#cb_deadline_date").val(),
            "mode_of_payment": $("#mode_of_payment").val(),
            "acq_name": $("#acq_name").val(),
            "settlement_date": $("#settlement_date").val(),
            "customer_name": $("#customer_name").val(),
            "customer_phone": $("#customer_phone").val(),
            "email": $("#email").val(),
            "nemail": $("#nemail").val(),
            "status": "ACCEPTED",
            "userType": userType,
            "updatedBy": userEmailId
          }

          $.ajax({
            type: "PUT",
            url: domain + "/crmws/update/" + id,
            data: JSON.stringify(obj),
            contentType: "application/json",
            success: function (data) {
              debugger
              alert("Chargeback Accepted");
              window.location.reload();

            }
          });
        }


        function closeChargeback() {
          var cbCode = $("#cb_reason_code").val();

          if (cbCode == "" || cbCode == null) {
            alert("Please Select CB Reason Code");
            return false;
          }

          var id = '<s:property value="id"/>';
          var urls = new URL(window.location.href);
          var userEmailId = "<s:property value='%{#session.USER.emailId}'/>";
          var userType = "<s:property value='%{#session.USER.UserGroup.group}'/>";
          var domain = urls.origin;
          var obj = {

            "merchant_pay_id": $("#merchant_pay_id").val(),
            "pgRefNo": $("#pgRefNo").val(),
            "cb_case_id": $("#cb_case_id").val(),
            "date_of_txn": $("#date_of_txn").val(),
            "txn_amount": $("#txn_amount").val(),
            "pg_case_id": $("#pg_case_id").val(),
            "merchant_txn_id": $("#merchant_txn_id").val(),
            "order_id": $("#order_id").val(),
            "bank_txn_id": $("#bank_txn_id").val(),
            "cb_amount": $("#cb_amount").val(),
            "cb_reason": $("#cb_reason").val(),
            "cb_reason_code": $("#cb_reason_code").val(),
            "cb_intimation_date": $("#cb_intimation_date").val(),
            "cb_deadline_date": $("#cb_deadline_date").val(),
            "mode_of_payment": $("#mode_of_payment").val(),
            "acq_name": $("#acq_name").val(),
            "settlement_date": $("#settlement_date").val(),
            "customer_name": $("#customer_name").val(),
            "customer_phone": $("#customer_phone").val(),
            "email": $("#email").val(),
            "nemail": $("#nemail").val(),
            "status": "CLOSED",
            "userType": userType,
            "updatedBy": userEmailId
          }

          $.ajax({
            type: "PUT",
            url: domain + "/crmws/close/" + id,
            data: JSON.stringify(obj),
            contentType: "application/json",
            success: function (data) {
              debugger
              alert("Chargeback Closed");
              window.location.reload();

            }
          });
        }

        $("#submitPOD").click(function (event) {

          //stop submit the form, we will post it manually.
          event.preventDefault();
          var fileInput =
            $('#Podfile').val();



          // Allowing file type
          var allowedExtensions =
            /(\.jpg|\.jpeg|\.png|\.gif|\.doc|\.docx|\.xlsx|\.xls|\.csv|\.pdf)$/i;

          if (!allowedExtensions.exec(fileInput)) {
            alert('Invalid file type');
            $('#Podfile').val('');
            return false;
          } else {
            docUpload("uploadPod");
          }


        });
        $("#submitInvoice").click(function (event) {

          //stop submit the form, we will post it manually.
          event.preventDefault();
          var fileInput =
            $('#invoiceFile').val();



          // Allowing file type
          var allowedExtensions =
            /(\.jpg|\.jpeg|\.png|\.gif|\.doc|\.docx|\.xlsx|\.xls|\.csv|\.pdf)$/i;

          if (!allowedExtensions.exec(fileInput)) {
            alert('Invalid file type');
            $('#invoiceFile').val('');
            return false;
          } else {
            docUpload("uploadInvoice");
          }


        });




        $("#submitFile").click(function (event) {

          //stop submit the form, we will post it manually.
          event.preventDefault();
          var fileInput =
            $('#file').val();



          // Allowing file type
          var allowedExtensions =
            /(\.jpg|\.jpeg|\.png|\.gif|\.doc|\.docx|\.xlsx|\.xls|\.csv|\.pdf)$/i;

          if (!allowedExtensions.exec(fileInput)) {
            alert('Invalid file type');
            $('#file').val('');
            return false;
          } else {
            docUpload("uploadOthers");
          }


        });
        function downloadfile(param) {
          var urls = new URL(window.location.href);
          var domain = urls.origin;
          window.open(domain + '/crmws/downloadFile?' + param);
        }
        function removefile(param) {
          var urls = new URL(window.location.href);
          var domain = urls.origin;
          $.ajax({
            type: "GET",
            url: domain + '/crmws/removeFile?' + param,
            success: function (data, status) {
              debugger
              var response = JSON.stringify(data);
              alert(response);
              window.location.reload();
            },
            error: function (status) {
            }
          });
        }

        debugger

        var urls = new URL(window.location.href);
        var domain = urls.origin;

          // $.ajax({
          //   type: "GET",

          //   url: domain + "/crmws/list?payId=" + $("#merchant_pay_id").val(),
          //   //url :"http://localhost:8080/crmws/list?payId=" + payId,
          //   success: function (data, status) {
          //     var response = JSON.parse(JSON.stringify(data));
          //     if (userType == 'Admin' || userType == 'Sub Admin') {
          //       renderTable(response);
          //     }
          //     else {
          //       renderchargebackTable(response);
          //     }
          //   },
          //   error: function (status) {
          //   }
          // });


      </script>
	<script>
        function getData(id) {

          redirect(id);
        }

        function docUpload(uploadType) {
          var urls = new URL(window.location.href);
          var domain = urls.origin;
          var file = '';

          if (uploadType == 'uploadPod') {

            file = $('#Podfile').val();
          } else if (uploadType == 'uploadInvoice') {
            file = $('#invoiceFile').val();

          } else {
            file = $('#file').val();
          }

          // alert(file)

          if (file.length == 0) {
            alert('Please upload a document');
            $("#file").val('');
            $('#invoiceFile').val('');
            $('#Podfile').val('');
          }

          else {
            var userType="<s:property value='%{#session.USER.UserGroup.group}'/>";
            var payId = $("#merchant_pay_id").val();
            var caseId = $("#cb_case_id").val();
            var form = $('#uploadForm')[0];
            var data = '';
            if (uploadType == 'uploadPod') {
              data = new FormData(uploadFormPod);
            } else if (uploadType == 'uploadInvoice') {
              data = new FormData(uploadFormInvoice);
            } else {
              data = new FormData(uploadForm);
            }

            $.ajax({
              type: "POST",
              url: domain + "/crmws/uploadFile/" + uploadType +"/"+userType,
              enctype: 'multipart/form-data',
              data: data,
              processData: false,
              contentType: false,
              cache: false,
              success: function (data, status) {
                alert("Document uploaded successfully");
                window.location.reload();
              },
              error: function (status) {
                alert("Error while uploading a document");

              }
            });

          }
        }
      </script>
	<script>

        $(document).ready(function () {
          var id = '<s:property value="id"/>';
          var userType = "<s:property value='%{#session.USER.UserGroup.group}'/>";
          var urls = new URL(window.location.href);
          var domain = urls.origin;
          if (userType == 'Merchant') {
            document.getElementById("merchant_pay_id").disabled = true;
            document.getElementById("pg_case_id").disabled = true;
            document.getElementById("cb_amount").disabled = true;
            //document.getElementById("cb_reason_code").disabled = true;
            //document.getElementById("cb_reason").disabled = true;
            $('#submit').hide();


          } else {
            if (id > 0) {
              document.getElementById("merchant_pay_id").disabled = true;
            }
            else {
              document.getElementById("merchant_pay_id").disabled = false;
            }
            $('#status').hide();

          }
          var url = new URL(window.location.href).origin
            + "/crmws/getAllChargebackReasons";
          $.ajax({
            type: "GET",

            url: url,

            success: function (result, status, xhr) {
              debugger
              $("#cb_reason_code").append("<option value=" + "" + ">" + "Please Select CB Reason Code" + "</option>");
              result.forEach((element, index, array) => {
                $("#cb_reason_code").append("<option value=" + element.cbReasonCode + ">" + element.cbReasonCode + "</option>");

              });
            },
            error: function (xhr, status, error) {
              alert("xhr : " + xhr + "\nstatus : " + status + "\nerror : " + error);
            }
          });
          if (id > 0) {

            $.ajax({
              type: "GET",
              url: domain + "/crmws/getById/" + id,
              //url : "http://localhost:8080/crmws/getById/"+id, 
              success: function (data, status) {




                var response = JSON.parse(JSON.stringify(data));

                var s = '<option value="' + response.pgRefNo + '">' + response.pgRefNo + '</option>';
                $("#pgRefNo").html(s);
                // $("#pgRefNo").append("<option value=" + response.pgRef+">" + response.pgRef + "</option>");
                $("#merchant_pay_id").val(response.merchant_pay_id);
                $("#pgRefNo").val(response.pgRefNo);
                $("#cb_case_id").val(response.cb_case_id);
                

                $("#cb_reason_code").val(response.cb_reason_code);
                document.getElementById("select2-cb_reason_code-container").innerHTML=response.cb_reason_code;


                var cb_reason_code = $("#cb_reason_code").val();

                // if (cb_reason_code == "" || cb_reason_code == null) {
                //   alert("Please Select CB Reasoncode");
                //   $("#cb_reason").val("");
                //   return false;
                // }
                var urls = new URL(window.location.href);
                var domain = urls.origin;
                $.ajax({
                  type: "GET",
                  url: domain + "/crmws/getcbReasonDescriptionFromcbReasonCode/" + cb_reason_code,
                  // url : "http://localhost:8080/crmws/commentlist?caseId="+ caseId,
                  success: function (data, status) {
                    $("#cb_reason").val(data.cbReasonDescription);
                  },
                  error: function (status) {
                  }
                });
                $('#date_of_txn').val(response.date_of_txn);
                $("#txn_amount").val(response.txn_amount);
                $("#pg_case_id").val(response.pg_case_id);
                $("#merchant_txn_id").val(response.merchant_txn_id);
                $("#order_id").val(response.order_id);
                $("#bank_txn_id").val(response.bank_txn_id);
                $("#cb_amount").val(response.cb_amount);

                $("#cb_intimation_date").val(response.cb_intimation_date);
                $("#cb_deadline_date").val(response.cb_deadline_date);
                $("#mode_of_payment").val(response.mode_of_payment);
                $("#acq_name").val(response.acq_name);
                $("#settlement_date").val(response.settlement_date);
                $("#customer_name").val(response.customer_name);
                $("#customer_phone").val(response.customer_phone);
                $("#email").val(response.email);
                $("#nemail").val(response.nemail);
                $("#payId").val(response.merchant_pay_id);
                $("#caseId").val(response.cb_case_id);
                $("#id").val(response.id);
                $("#payIdd").val(response.merchant_pay_id);
                $("#caseIdd").val(response.cb_case_id);
                $("#idd").val(response.id);
                $("#payIddd").val(response.merchant_pay_id);
                $("#caseIddd").val(response.cb_case_id);
                $("#iddd").val(response.id);

                $("#status").val(response.status);


                document.getElementById("EnterPgRefNum").style.display = "none";

                var userType = "<s:property value='%{#session.USER.UserGroup.group}'/>";
                if (response.status == "CLOSED") {

                  document.getElementById("submitFile").style.display = "none";
                  document.getElementById("submitInvoice").style.display = "none";
                  document.getElementById("submitPOD").style.display = "none";
                  //document.getElementById("hideAfterAccepted").style.display = "none";

                  document.getElementById("uploadFormPod").style.display = "none";
                  document.getElementById("uploadFormInvoice").style.display = "none";
                  document.getElementById("uploadForm").style.display = "none";

                 // document.getElementsByClassName("ajay").style.display = "none";
                 
                  
                  if (userType == "ADMIN" || userType=="Sub Admin" || userType=="Risk") {

                    document.getElementById("closeCb").style.display = "none";
                    document.getElementById("submit").style.display = "none";
                    document.getElementById("ChatBox").style.display = "none";

                  } else {
                    $("#status").hide();
                    // $("#submitFile").hide();
                    $("#submit").hide();

                    document.getElementById("ChatBox").style.display = "none";



                  }
                }

                debugger
                if (response.status != null) {
                 

                  if ((userType == "ADMIN" || userType=="Sub Admin" || userType=="Risk" )&& response.status!="CLOSED") {

                    document.getElementById("closeCb").style.display = "block";
                    document.getElementById("submit").style.display = "none";
                  }
                }

                debugger
                
                if (response.status == "ACCEPTED") {
                	 document.getElementById("submitFile").style.display = "none";
                     document.getElementById("submitInvoice").style.display = "none";
                     document.getElementById("submitPOD").style.display = "none";
                     //document.getElementById("hideAfterAccepted").style.display = "none";

                     document.getElementById("uploadFormPod").style.display = "none";
                     document.getElementById("uploadFormInvoice").style.display = "none";
                     document.getElementById("uploadForm").style.display = "none";
                     
                     document.getElementById("ChatBox").style.display = "none";
                }
                if (response.status == "ACCEPTED" || response.status == "POD") {
                	
                	
                

                  $("#status").hide();
                  // $("#submitFile").hide();
                  $("#submit").hide();
                  // $("#fileUpload").hide();
                  // $("#file").hide();
                  // $("#comment").hide();
                  // $("#materialSendIcon").hide();
                  // $("#hideAfterAccepted").hide();
                  // $("#ChatBox").hide();

                }



                // if (response.status == "POD" && userType != 'Merchant') {
                //   document.getElementById("status").style.display = "none";
                //   //$('#status').hide();
                // }
                if (response.filePaths != null && response.filePaths != '') {
                  var userEmailId = "<s:property value='%{#session.USER.emailId}'/>";
                  let filePaths = response.filePaths.split(",");
                  for (var i = 0; i < filePaths.length; i++) {
                    let fileName = filePaths[i].replace(/^.*[\\\/]/, '');
                    var url = "fileName=" + fileName + "&id=" + response.id + "&userEmail=" + userEmailId;
                    let fileBtn = fileName + '<i class="material-icons" onclick="downloadfile(\'' + url + '\');">cloud_download </i>&nbsp;' +
                      '<i class="material-icons ajay" onclick="removefile(\'' + url + '\');">cancel </i><br/>';
                    let downloadBtn = fileName + '<i class="material-icons" onclick="downloadfile(\'' + url + '\');">cloud_download </i>&nbsp;<br/>';

                    if (userType == 'Merchant') {
                      $("#chargeBackFilesDiv").append(downloadBtn);
                    }
                    else {
                      $("#chargeBackFilesDiv").append(fileBtn);
                    }
                  }
                }

                if (response.status == "CLOSED") {
                  $(".ajay").hide();
                }
                getComment(response.cb_case_id);
              },
              error: function (status) {
              }
            });


          } else {
            debugger
            document.getElementById("uploadDiv").style.display = "none";
            document.getElementById("ChatBox").style.display = "none";


            document.getElementById("closeCb").style.display = "none";

            //$("#ChatBox").hide();
          }

        });


        function getComment(caseId) {
          debugger
          var urls = new URL(window.location.href);
          var domain = urls.origin;
          $.ajax({
            type: "GET",
            url: domain + "/crmws/commentlist?caseId=" + caseId,
            // url : "http://localhost:8080/crmws/commentlist?caseId="+ caseId,
            success: function (data, status) {
              var response = JSON.parse(JSON.stringify(data));
              response.forEach(element => {
                var chats = '<b>' + element.comment + '</b><p>' + element.createdBy + '</p>'
                  ;
                $("#chargeBackChatBox").append(chats);
              });
            },
            error: function (status) {
            }
          });
        }  
      </script>
	<script>

        function validPgRefNum() {

          var pgRefValue = document.getElementById("pgRefNo").value;
          var regex = /^(?!0{16})[0-9\b]{16}$/;
          
          if(pgRefValue.length < 16){
        	  alert("Please Enter Valid PG REF NUMBER");
        	  return false;
          }else if (!regex.test(pgRefValue)) {
              alert("Special Character not allowed and PG REF NUM Should 16 digit");
              return false;
           }else{
        	  return true;
          }

        }


        $(document).ready(function () {

          $(document).on('click', '#submit', function (e) {
            var regex = /^[A-Z a-z 0-9_]+$/;
            var emailRegex = /^[A-Za-z0-9+_.-]+@(.+)$/;
            e.preventDefault();
            if ($.trim($('#pgRefNo').val()).length == 0 || $('#pgRefNo').val() == '' || $('#pgRefNo') == null) {
              error_pgRefNo = 'please select pg Ref no.';
              $('#error_pgRefNo').text(error_pgRefNo);
              $('#pgRefNo').addClass('error');
            }
            else {
              error_pgRefNo = '';
              $('#error_pgRefNo').text(error_pgRefNo);
              $('#pgRefNo').removeClass('error');
            }

            if ($.trim($('#cb_case_id ').val()).length == 0 || $('#cb_case_id').val() == '' || $('#cb_case_id') == null) {
              error_cb_case_id = 'Enter Cb Case Id';
              $('#error_cb_case_id').text(error_cb_case_id);
              $('#cb_case_id ').addClass('error');
            }
            else {
              error_cb_case_id = '';
              $('#error_cb_case_id').text(error_cb_case_id);
              $('#cb_case_id').removeClass('error');
            }

            if ($.trim($('#txn_amount').val()).length == 0 || $('#txn_amount').val() == '' || $('#txn_amount') == null) {
              error_txn_amount = 'Enter transaction amount';
              $('#error_txn_amount').text(error_txn_amount);
              $('#txn_amount ').addClass('error');
            }
            else {
              error_txn_amount = '';
              $('#error_txn_amount').text(error_txn_amount);
              $('#txn_amount').removeClass('error');
            }

            if ($.trim($('#merchant_txn_id').val()).length == 0 || $('#merchant_txn_id').val() == '' || $('#merchant_txn_id') == null) {
              error_merchant_txn_id = 'Enter Merchant Transaction Id';
              $('#error_merchant_txn_id').text(error_merchant_txn_id);
              $('#merchant_txn_id ').addClass('error');
            }
            else {
              error_merchant_txn_id = '';
              $('#error_merchant_txn_id').text(error_merchant_txn_id);
              $('#merchant_txn_id').removeClass('error');
            }
            if ($.trim($('#order_id').val()).length == 0 || $('#order_id').val() == '' || $('#order_id') == null) {
              error_order_id = 'Enter order Id';
              $('#error_order_id').text(error_order_id);
              $('#order_id ').addClass('error');
            }
            else {
              error_order_id = '';
              $('#error_order_id').text(error_order_id);
              $('#order_id').removeClass('error');
            }

            if ($.trim($('#bank_txn_id').val()).length == 0 || $('#bank_txn_id').val() == '' || $('#bank_txn_id') == null) {
              error_bank_txn_id = 'Enter bank transaction Id';
              $('#error_bank_txn_id').text(error_bank_txn_id);
              $('#bank_txn_id ').addClass('error');
            }
            else {
              error_bank_txn_id = '';
              $('#error_bank_txn_id').text(error_bank_txn_id);
              $('#bank_txn_id').removeClass('error');
            }


            if ($.trim($('#mode_of_payment').val()).length == 0 || $('#mode_of_payment').val() == '' || $('#mode_of_payment') == null) {
              error_mode_of_payment = 'Enter mode of payment';
              $('#error_mode_of_payment').text(error_mode_of_payment);
              $('#mode_of_payment ').addClass('error');
            }
            else {
              error_mode_of_payment = '';
              $('#error_mode_of_payment').text(error_mode_of_payment);
              $('#mode_of_payment').removeClass('error');
            }

            if ($.trim($('#acq_name').val()).length == 0 || $('#acq_name').val() == '' || $('#acq_name') == null) {
              error_acq_name = 'Enter acquirer name';
              $('#error_acq_name').text(error_acq_name);
              $('#acq_name ').addClass('error');
            }
            else {
              error_acq_name = '';
              $('#error_acq_name').text(error_acq_name);
              $('#acq_name').removeClass('error');
            }

            if ($.trim($('#customer_name').val()).length == 0 || $('#customer_name').val() == '' || $('#customer_name') == null) {
              error_customer_name = 'Enter Name';
              $('#error_customer_name').text(error_customer_name);
              $('#customer_name ').addClass('error');
            }
            else {
              error_customer_name = '';
              $('#error_customer_name').text(error_customer_name);
              $('#customer_name').removeClass('error');
            }

            if ($('#date_of_txn').val() == '' || $('#date_of_txn') == null) {
              error_date_of_txn = 'Enter transaction date';
              $('#error_date_of_txn').text(error_date_of_txn);
              $('#date_of_txn ').addClass('error');
            }
            else {
              error_date_of_txn = '';
              $('#error_date_of_txn').text(error_date_of_txn);
              $('#date_of_txn').removeClass('error');
            }

            if ($('#cb_intimation_date').val() == '' || $('#cb_intimation_date') == null) {
              error_cb_intimation_date = 'Enter chargeback intimation date';
              $('#error_cb_intimation_date').text(error_cb_intimation_date);
              $('#cb_intimation_date ').addClass('error');
            }
            else {
              error_cb_intimation_date = '';
              $('#error_cb_intimation_date').text(error_cb_intimation_date);
              $('#cb_intimation_date').removeClass('error');
            }

            if ($('#cb_deadline_date').val() == '' || $('#cb_deadline_date') == null) {
              error_cb_deadline_date = 'Enter chargeback deadline date';
              $('#error_cb_deadline_date').text(error_cb_deadline_date);
              $('#cb_deadline_date ').addClass('error');
            }
            else {
              error_cb_deadline_date = '';
              $('#error_cb_deadline_date').text(error_cb_deadline_date);
              $('#cb_deadline_date').removeClass('error');
            }

            if ($.trim($('#email').val()).length == 0 || $('#email').val() == '' || $('#email') == null) {
              error_email = 'Enter email';
              $('#error_email').text(error_email);
              $('#email ').addClass('error');
            }
            else {
              error_email = '';
              $('#error_email').text(error_email);
              $('#email').removeClass('error');
            }
            if ($.trim($('#nemail').val()).length == 0 || $('#nemail').val() == '' || $('#nemail') == null) {
              nerror_email = 'Enter  notification email';
              $('#nerror_email').text(nerror_email);
              $('#nemail ').addClass('error');
            }
            else {
              if (!emailRegex.test($('#nemail').val())) {
                nerror_email = 'Enter valid email Id';
                $('#nerror_email').text(nerror_email);
                $('#nemail').addClass('error');
              }
              else {
                nerror_email = '';
                $('#nerror_email').text(error_email);
                $('#nemail').removeClass('error');
              }
            }
            if ($.trim($('#cb_reason').val()).length == 0) {
              error_cb_reason = 'Enter chargeback reason';
              $('#error_cb_reason').text(error_cb_reason);
              $('#cb_reason').addClass('error');
            }
            else {
              if (!regex.test($('#cb_reason').val())) {
                error_cb_reason = 'Special characters are not allowed';
                $('#error_cb_reason').text(error_cb_reason);
                $('#cb_reason').addClass('error');
              }
              else {
                error_cb_reason = '';
                $('#error_cb_reason').text(error_cb_reason);
                $('#cb_reason').removeClass('error');
              }
            }
            if ($.trim($('#cb_reason_code').val()).length == 0) {
              error_cb_reason_code = 'Enter chargeback reason code';
              $('#error_cb_reason_code').text(error_cb_reason_code);
              $('#cb_reason_code').addClass('error');
            }
            else {
              if (!regex.test($('#cb_reason_code').val())) {
                error_cb_reason_code = 'Special characters are not allowed';
                $('#error_cb_reason_code').text(error_cb_reason_code);
                $('#cb_reason_code').addClass('error');
              }
              else if ($.trim($('#cb_reason_code').val()).length > 10) {
                error_cb_reason_code = 'Reason code must be of 10 characters';
                $('#error_cb_reason_code').text(error_cb_reason_code);
                $('#cb_reason_code').addClass('error');
              }
              else {
                error_cb_reason_code = '';
                $('#error_cb_reason_code').text(error_cb_reason_code);
                $('#cb_reason_code').removeClass('error');
              }
            }
            if ($.trim($('#pg_case_id').val()).length == 0) {
              error_pg_case_id = 'Enter pg case id';
              $('#error_pg_case_id').text(error_pg_case_id);
              $('#pg_case_id').addClass('error');
            }
            // else {
            //   if (!regex.test($('#pg_case_id').val())) {
            //     error_pg_case_id = 'Special characters are not allowed';
            //     $('#error_pg_case_id').text(error_pg_case_id);
            //     $('#pg_case_id').addClass('error');
            //   }
            else {
              error_pg_case_id = '';
              $('#error_pg_case_id').text(error_pg_case_id);
              $('#pg_case_id').removeClass('error');
            }

            if ($.trim($('#cb_amount').val()).length == 0 || $.trim($('#cb_amount').val()) < 0) {
              error_cb_amount = 'Enter chargeback amount';
              $('#error_cb_amount').text(error_cb_amount);
              $('#cb_amount').addClass('error');
            }
            else {

              var cbAmount = parseFloat($('#cb_amount').val()).toFixed(2);
              var txnAmount = parseFloat($('#txn_amount').val()).toFixed(2);

              if (parseFloat(cbAmount) > parseFloat(txnAmount)) {
                error_cb_amount = 'Chargeback amount must be less than transaction amount';
                $('#error_cb_amount').text(error_cb_amount);
                $('#cb_amount').addClass('error');
              }

              else {
                error_cb_amount = '';
                $('#error_cb_amount').text(error_cb_amount);
                $('#cb_amount').removeClass('error');
              }

            }
            if (error_cb_reason != '' || error_cb_amount != '' || error_pg_case_id != '' ||
              error_cb_reason_code != '' || error_pgRefNo != ''
              || error_cb_case_id != '' || error_txn_amount != '' || error_merchant_txn_id != '' || error_order_id != ''
              || error_bank_txn_id != '' || error_mode_of_payment != '' || error_acq_name != '' || error_customer_name != ''
              || error_date_of_txn != ''
              || error_email != '' || nerror_email != '' || error_cb_intimation_date != '' || error_cb_deadline_date != '') {
              return false;
            }
            else {
              //Disable button Here
              $("#submit").hide();
              validate();
            }
          });
        });

        function validate() {


          var cbCode = $("#cb_reason_code").val();

          if (cbCode == "" || cbCode == null) {
            alert("Please Select CB Reason Code");
            $("#submit").show();
            return false;
          }

          var urls = new URL(window.location.href);
          var userEmailId = "<s:property value='%{#session.USER.emailId}'/>";
          var userType = "<s:property value='%{#session.USER.UserGroup.group}'/>";
          var domain = urls.origin;
          var obj = {
        	"currencyName": $("#currencyName").val(),
            "merchant_pay_id": $("#merchant_pay_id").val(),
            "pgRefNo": $("#pgRefNo").val(),
            "cb_case_id": $("#cb_case_id").val(),
            "date_of_txn": $("#date_of_txn").val(),
            "txn_amount": $("#txn_amount").val(),
            "pg_case_id": $("#pg_case_id").val(),
            "merchant_txn_id": $("#merchant_txn_id").val(),
            "order_id": $("#order_id").val(),
            "bank_txn_id": $("#bank_txn_id").val(),
            "cb_amount": $("#cb_amount").val(),
            "cb_reason": $("#cb_reason").val(),
            "cb_reason_code": $("#cb_reason_code").val(),
            "cb_intimation_date": $("#cb_intimation_date").val(),
            "cb_deadline_date": $("#cb_deadline_date").val(),
            "mode_of_payment": $("#mode_of_payment").val(),
            "acq_name": $("#acq_name").val(),
            "settlement_date": $("#settlement_date").val(),
            "customer_name": $("#customer_name").val(),
            "customer_phone": $("#customer_phone").val(),
            "email": $("#email").val(),
            "nemail": $("#nemail").val(),
            "status": null,
            "userType": userType,
            "emailId": userEmailId
          }

          var type = "POST";
          var id = '<s:property value="id"/>';
          url = domain + "/crmws/save";
          
          $.ajax({
            type: type,
            url: url,
            data: JSON.stringify(obj),
            contentType: "application/json",
            success: function (data, status) {
			debugger
			alert(data.respmessage);
			$("#submit").show();
			window.location.reload();
             /*  if (id > 0) {
                debugger
                alert("Updated successfully");
                window.location.reload();
              } else {
                alert("Created successfully");
                window.location.reload();
              } */

            },
            error: function (status) {
            	$("#submit").show();
            }
          });
        }
      </script>
	<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
	<link rel="stylesheet" type="text/css" href="../css/material-icons.css">
	<link rel="stylesheet" href="../css/material-font-awesome.min.css">
	<!-- <link href="../css/material-dashboard.css?v=2.1.0" rel="stylesheet"> -->
</body>

</html>