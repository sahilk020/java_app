<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Merchant Mapping PO</title>
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
ul.filters {
	list-style: none;
}

ul.filters .fa-check:before {
	content: unset;
}
</style>


<script type="text/javascript">
debugger
		    function getMappingString() {
		        var acquirer = $("#acquirer").val();
		        var channel = $("#channel").val();
		        var merchant = $("#merchant").val();
		        var upDateBy = $("#email").val();
		        var urls = new URL(window.location.href);
		        var domain = urls.origin;
		        $.ajax({
		            url: domain+"/crmws/acquirerPayout/getcurrencyList",
		            type: "GET",
		            data: {
		                acquirer: acquirer,
		                channel: channel,
		                payId: merchant,	
		            },
		            success: function (data) {
		               updateCurrencyCheckboxes(data);
		            },
		            error: function (error) {
		                console.error("Error fetching currency list:", error);
		            }
		        });
		        document.getElementById("btnSave").style.display = "block";
		    }
   	     
function updateCurrencyCheckboxes(currencyList) {
    var container = $("#currencyCheckboxes");
    container.empty();
    var data = new Map(Object.entries(JSON.parse(JSON.stringify(currencyList))));

    for (let [key, value] of data.entries()) {
        var checkbox = $("<input class='form-check-input' type='checkbox' style='margin-bottom: 10px' name='currencies' value='" + key + "'>");
        
        checkbox.prop('checked', value === 'ACTIVE');

        checkbox.appendTo(container);
        var label = $("<label class='form-check-label' style='font-size: 15px; font-weight: bold; margin-left: 10px'>").text(key);
        label.appendTo(container);
        $("<br>").appendTo(container);

    }

    $("#btnSave").off("click").on("click", function () {
        saveData(data);
        
    });
}
debugger
function saveData(currencyData) {
    var payId = $("#merchant").val();
    var acquirer = $("#acquirer").val();
    var channel = $("#channel").val();
    var email = $("#email").val();
    var data = "";

    var atLeastOneChecked = false;

    currencyData.forEach(function (value, key) {
        var isChecked = $("input[name='currencies'][value='" + key + "']").prop('checked');
        var status = isChecked ? "ACTIVE" : "INACTIVE";
        data = data + key + "=" + status + "|";
        if (isChecked) {
            atLeastOneChecked = true;
        }
    });

    if (payId == "" || payId == '' || payId == null) {
        alert("Please select Merchant");
        return false;
    }
    if (acquirer == "" || acquirer == '' || acquirer == null) {
        alert("Please select Acquirer");
        return false;
    }
    if (channel == "" || channel == '' || channel == null) {
        alert("Please select Channel");
        return false;
    }    if (!atLeastOneChecked) {
        alert("Please select at least one currency.");
        return false;
    }

    var urls = new URL(window.location.href);
    var domain = urls.origin;

    $.ajax({
        url: domain + "/crmws/acquirerPayout/savecurrencyList",
       // url:  "http://localhost:9001/acquirerPayout/savecurrencyList",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({
            payId: payId,
            acquirer: acquirer,
            channel: channel,
            upDateBy: email,
            rule: data.slice(0, -1)
        }),
        success: function (response) {
            alert(response.response);
            window.location.reload();
        },
        error: function (error) {
            console.error("Error saving data:", error);
        }
    });
}

		</script>
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
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Pay-out Merchant Mapping</h1>
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
						<li class="breadcrumb-item text-dark">Pay-out Merchant Mapping</li>
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

<div class="card" style=" margin-bottom: 20px;">
										<div class="card-body">
												<div class="row my-5 mb-3">
													
													 <div class="col-md-12">
        <div class="form-check form-check-inline">
            <input class="form-check-input" type="checkbox" id="pgCheckbox" >
            <label class="form-check-label" for="pgCheckbox" >Pay-in</label>
        </div>
        <div class="form-check form-check-inline">
            <input class="form-check-input" type="checkbox" id="poCheckbox" checked disabled>
            <label class="form-check-label" for="poCheckbox">Pay-out</label>
        </div>
    </div>
    </div></div></div>


								<div class="row my-3 align-items-center">
									<div class="col-md-4 fv-row">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">Merchant</span>
										</label>
										<s:select name="merchant" id="merchant" value="merchant"
											headerKey=""
											class="form-select form-select-solid merchantPayId"
											headerValue="Please Select Merchant" list="merchantList"
											listKey="payId" listValue="businessName" autocomplete="off" />
									</div>


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
											onchange="getMappingString()">
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

									<div>
										<div id="currencyCheckboxes"></div>
										<input type="hidden" name="email" id="email"
											value="<s:property value='%{#session.USER.emailId}'/>" />


										<div height="40" valign="bottom" colspan="4" align="center">
											<input type="button" value="Save" 
												class="btn btn-primary mt-4 submit_btn" id="btnSave"
												theme="simple" />
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


	<script type="text/javascript">
	debugger
	$(document)
	.ready(
		function () {
			
			document.getElementById("btnSave").style.display = "none";
			var channel = $("#channel").val();
			var urls = new URL(window.location.href);
		    var domain = urls.origin;
			let acquirerList = [];
			$.get(domain+"/crmws/acquirerPayout/getAcquirerList", {}, function(result) {
			    $("#acquirer").empty(); 
				$("#channel").empty();
			    $("#acquirer").append('<option value="" selected>Please Select Acquirer</option>');
			    for (var i = 0; i < result.length; i++) {
			        $("#acquirer").append("<option value=" + result[i] + ">" + result[i] + "</option>");
			    }
			});
			});
		
	
	function getAcquirer(){
		var urls = new URL(window.location.href);
	    var domain = urls.origin;
		
	$.post(domain+"/crmws/acquirerPayout/getAcquirerList", {}, function(result) {
	    var acquirerList = result.acquirerList; 
	    $("#acquirer").empty(); 

	    $("#acquirer").append('<option value="" selected>Please Select Acquirer</option>');
	    
	    for (var i = 0; i < acquirerList.length; i++) {
	        $("#acquirer").append("<option value=" + acquirerList[i] + ">" + acquirerList[i] + "</option>");
	    }
	});
	}
	

		function getChannel() {

			var acquirer = $("#acquirer").val();
			var merchant = $("#merchant").val();

			if (merchant == "") {
				alert("please select Merchant");
				$("#acquirer").val("");
				return false;
			}
			if (acquirer == "") {
				alert("please select Acquirer");

				return false;
			}
			$("#channel").empty();
			var urls = new URL(window.location.href);
		    var domain = urls.origin;
			$.get(domain+"/crmws/acquirerPayout/getChannelList", {
				acquirer : acquirer
			}, function(result) {
				
				$("#channel").append('<option value="" selected>Please Select Channel</option>');
				
				for (var i = 0; i < result.length; i++) {
					$("#channel").append(
							"<option value="+result[i]+">"
									+ result[i] + "</option>");
				}
			}
						
			);

			getMappingString();
			
		}

	</script>
	
	<script>
		
		document.getElementById('pgCheckbox').addEventListener('change', function () {
			if (this.checked) {
				window.location.href = 'mopSetUpAction'; } });
		</script>
</body>



</html>