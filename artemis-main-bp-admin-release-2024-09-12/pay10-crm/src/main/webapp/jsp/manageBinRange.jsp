<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Bin Range Summary</title>

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

<script type="text/javascript">
	$(document).ready(function() {
		$(function() {
			renderTable();
		});
	});
	
	function decodeVal(text) {
		return $('<div/>').html(text).text();
	}
	function renderTable() {
		var token = document.getElementsByName("token")[0].value;
		$('#BinRange').dataTable({
			dom : 'BTftlpi',
			buttons : [ {
				extend : 'copyHtml5',
				title : 'BinRange',
				exportOptions : {
					columns : [':visible' ]
				}
			}, {
				extend : 'csvHtml5',
				title : 'BinRange',
				exportOptions : {
					columns : [ ':visible' ]
				}
			} ],
			"ajax" : {
				"url" : "binRangeAction",
				"type" : "POST",
				"data" : function(d) {
					return generatePostData(d);
				}
			},
			"searching" : false,
			"processing" : true,
			"serverSide" : true,
			"paginationType" : "full_numbers",
			"lengthMenu" : [[10, 25, 50,100], [10, 25, 50,100]],
			"order" : [],
			"aoColumns" : [ {
				"mData" : "binCodeHigh"
			},{
				"mData" : "binCodeLow"
			},{
				"mData" : "binRangeHigh"
			},{
				"mData" : "binRangeLow"
			}, {
				"mData" : "mopType"
			}, {
				"mData" : "cardType"
			}, {
				"mData" : "issuerBankName"
			}, {
				"mData" : "issuerCountry"
			} ]
		});
	}

	$(document).ready(function() {
		$('#example').DataTable({
			dom : 'B',
			buttons : [ 'csv' ]
		});

		$("#submit").click(function(env) {
		//alert("working");

		var binRangeHigh = document.getElementById("binRangeHigh").value;
		var binRangeLow = document.getElementById("binRangeLow").value;
		console.log(binRangeHigh);
		console.log(binRangeLow);
			if(binRangeLow != "" && binRangeHigh == ""){
				alert("Please Enter BinRangeHigh");
				return false;
			}

			if(binRangeHigh != "" && binRangeLow == ""){
				alert("Please Enter BinRangeLow");
				return false;
			}
			$('#loader-wrapper').show();
			reloadTable();
	});
	});

	function reloadTable() {
		var tableObj = $('#BinRange');
		var table = tableObj.DataTable();
		table.ajax.reload();
	}
	function generatePostData(d) {
		var token = document.getElementsByName("token")[0].value;
		var cardType = document.getElementById("paymentMethods").value;
		var moptype = document.getElementById("mopType").value;
		var binRangeHigh = document.getElementById("binRangeHigh").value;
		var binRangeLow = document.getElementById("binRangeLow").value;
		var countryType = document.getElementById("countryType").value;
		if(cardType==''){
			cardType='ALL'
		}
		if(moptype==''){
			moptype='ALL'
		}
		if(countryType ==''){
			countryType='ALL'
		}
		var obj = {
			cardType : cardType,
			mopType : moptype,
			countryType : countryType,
			binRangeHigh :binRangeHigh,
			binRangeLow : binRangeLow,
			draw : d.draw,
			length : d.length,
			start : d.start,
			token : token,
			"struts.token.name" : "token",
		};

		return obj;
	}
	function isNumberKeyAmount(evt) {
		const charCode = (event.which) ? event.which : event.keyCode;
          if (charCode > 31 &&  (charCode < 48 || charCode > 57) && charCode!=46 ) {
            return false;
			return true;
          }
}
</script>
<style>
.dt-buttons.btn-group.flex-wrap {
    display: none;
}
.btn-fl{float: right;clear: right;margin-right: -70px;}
.btn-small{padding: 6px!important;}
.bws-tp{margin-bottom: -5px!important; margin-top: -10px;}
/*.inputfieldsmall {height: 28px!important;padding:4px 80px!important;} */
table #BinRange tbody th, table #BinRange tbody td{text-align:center;}
.inputfieldsmall1 {
    display: inline-block!important;
    /* padding: 4px 15px;
    height: 28px;
    font-size: 11px;
    font-family: 'Titillium Web', sans-serif;
    line-height: 1.428571429;
    color: black;
    margin-bottom: 5px;
    background-color: #fff;
    background-image: none; */
    border: 1px solid #ccc;
    border-radius: 4px;
    -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
    box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
    -webkit-transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
    transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
	}
/* .form-control{
	margin-left: 0px !important;
} */

table.dataTable thead .sorting {
    background: none !important;
}
.sorting {
    background: none !important;
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
									<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Bin Range Details</h1>
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
										<li class="breadcrumb-item text-muted">Batch Operation</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item">
											<span class="bullet bg-gray-200 w-5px h-2px"></span>
										</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item text-dark">Bin Range Details</li>
										<!--end::Item-->
									</ul>
									<!--end::Breadcrumb-->
								</div>
								<!--end::Page title-->

							</div>
							<!--end::Container-->
						</div>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">

  <tr>
    <td align="left" valign="top"><div id="saveMessage">
        <s:actionmessage class="success success-text" />
      </div></td>
  </tr>

		<tr><td align="center" valign="top">

		<table width="100%" border="0"
				align="center" cellpadding="0" cellspacing="0" class="txnf">

				<!-- <tr> -->

				<tr>


					<td colspan="5">
						<!-- <h2 style="margin-top: 0px;margin-bottom: 25px;">Bin Range Details</h2> -->

<div class="post d-flex flex-column-fluid" id="kt_post">
			<div id="kt_content_container" class="container-xxl">
				<div class="row my-5">
					<div class="col">
						<div class="card">
							  <div class="card-body ">
						<div class="col-md-12">
							<div class="card ">
							  <div class="card-header card-header-rose card-header-text">
								<!-- <div class="card-text">
								  <h4 class="card-title">Bin Range Details</h4>
								</div> -->
							  </div>


						<div class="container">
						<div class="row">
							<div class="col-sm-6 col-lg-3">

							<label class="d-flex align-items-center fs-6 fw-semibold mb-2">Card Type</label>
							<s:select headerKey="ALL" headerValue="ALL" class="form-select form-select-solid"
							list="@com.pay10.commons.util.CardsType@values()"
							name="paymentMethods" id="paymentMethods"
							 autocomplete="off" value="code"
							listKey="code" listValue="name" />
							</div>
							<br>

							<div class="col-sm-6 col-lg-3">
								<label class="d-flex align-items-center fs-6 fw-semibold mb-2">Mop Type</label>
								<s:select headerKey="ALL" headerValue="ALL" class="form-select form-select-solid"
								list="@com.pay10.commons.util.BinRangeMopType@values()"
								name="mopType" id="mopType"
								autocomplete="off" value="code" listKey="code" listValue="name" />

								<input type="hidden" id="acquirerType" name="acquirerType"/>
							</div>
							<br>
							<div class="col-sm-6 col-lg-3">
								<label class="d-flex align-items-center fs-6 fw-semibold mb-2" >Bin Range Low</label>
								<div class="txtnew">
									<s:textfield id="binRangeLow" class="  form-control form-control-solid" name="binRangeLow"
										type="text" value="" autocomplete="off"   onkeypress="return isNumberKeyAmount(event)"
									ondrop="return false;" minlength="1" maxlength="24" ></s:textfield>
								</div>


							</div>
							<br>
							<div class="col-sm-6 col-lg-3">
								<label class="d-flex align-items-center fs-6 fw-semibold mb-2">Bin Range High</label>
								<div class="txtnew">
									<s:textfield id="binRangeHigh" class="  form-control form-control-solid" name="binRangeHigh"
										type="text" value="" autocomplete="off"
										onkeypress="return isNumberKeyAmount(event)"
									ondrop="return false;" minlength="1" maxlength="24" ></s:textfield>
								</div>


							</div>
						</div>
						<br>
						<div class="row">
							<div class="col-sm-6 col-lg-3">
								<label class="d-flex align-items-center fs-6 fw-semibold mb-2" >Card Issuer Region</label>
								<s:select headerKey="ALL" headerValue="ALL" class="form-select form-select-solid"
								list="#{'DOMESTIC':'DOMESTIC','INTERNATIONAL':'INTERNATIONAL'}" name="countryType" id = "countryType" />

							</div>
							<br>
							<div class="col-sm-6 col-lg-3">

								<div class="txtnew mt-4">
									<input type="button" id="submit" disabled="disabled" value="Search"
										class="btn btn-primary  mt-4 submit_btn">

								</div>
							</div>



						</div>
						</div>

						</div>
						</div>
					</div></div></div></div>








					<%-- 	<div class="row g-9 mb-8 justify-content-end">
                                            <div class="col-lg-4 col-sm-12 col-md-6">
                                              <select name="currency" data-control="select2" data-placeholder="Actions"
                                                class="form-select form-select-solid actions" data-hide-search="true">
                                                <option value="">Actions</option>
                                                <option value="copy">Copy</option>
                                                <option value="csv" >CSV</option>


                                              </select>
                                            </div>
                                            <div class="col-lg-4 col-sm-12 col-md-6">
                                              <select name="currency" data-control="select2" data-placeholder="Customize Columns"
                                                class="form-select form-select-solid actions" data-hide-search="true">
                                               <option value="">Customize Columns</option>
                                                <option value="">Bin Code High</option>
                                                <option value="">Bin Code Low</option>
                                                <option value="">Bin Range High</option>
                                                <option value="">Bin Range Low</option>
                                                 <option value="">Mop Type</option>
                                                 <option value="">Card Type</option>
                                                 <option value="">Issuer Bank Name</option>
                                                  <option value="">Issuer Country</option>


                                              </select>
                                            </div>
                                          </div> --%>
						</div>
						</div>
					</td>




				</tr>
				<!-- <tr>
					<td colspan="5" align="left" valign="top">&nbsp;</td>
				</tr> -->

				<tr>

					<td align="left" valign="top" style="padding: 0px;" colspan="5">
					<div class="post d-flex flex-column-fluid" id="kt_post">
			<div id="kt_content_container" class="container-xxl">
				<div class="row my-5">
					<div class="col">
				<div class="card">
				<div class="card-body">
				<div class="row g-9 mb-8 justify-content-end">
												<div class="col-lg-2 col-sm-12 col-md-6">
													<select name="currency" data-control="select2"
														data-placeholder="Actions" id="actions11"
														class="form-select form-select-solid actions dropbtn1"
														data-hide-search="true" onchange="myFunctions();">
														<option value="">Actions</option>
														<option value="copy">Copy</option>
														<option value="csv">CSV</option>
														<!-- <option value="pdf">PDF</option>
														<option value="print">PRINT</option> -->
													</select>
												</div>
												<div class="col-lg-4 col-sm-12 col-md-6">
													<div class="dropdown1">
														<button
															class="form-select form-select-solid actions dropbtn1">Customize
															Columns</button>
														<div class="dropdown-content1">
															<a class="toggle-vis" data-column="0">Bin Code High</a>
															 <a	class="toggle-vis" data-column="1">Bin Code Low</a>
															  <a class="toggle-vis" data-column="2">Bin Range High</a>
															  <a class="toggle-vis" data-column="3">Bin Range Low</a>
															  <a class="toggle-vis" data-column="4">Mop Type</a>
															   <a class="toggle-vis" data-column="5">Card Type</a>
															    <a class="toggle-vis" data-column="6">Issuer Bank Name</a>
															     <a class="toggle-vis" data-column="7">Issuer Country</a>

														</div>
													</div>
												</div>
											</div>







						<div class="scrollD">
							<table id="BinRange" align="center"  class="table table-striped table-row-bordered gy-5 gs-7" cellspacing="0" width="100%" style="text-align:center;">
								<thead>
									<tr class="fw-bold fs-6 text-gray-800" style="font-size: 11px;">
										<th style='text-align: center' >Bin Code High</th>
										<th style='text-align: center'>Bin Code Low</th>
										<th style='text-align: center'>Bin Range High</th>
										<th style='text-align: center'>Bin Range Low</th>
										<th style='text-align: center'>Mop Type</th>
										<th style='text-align: center'>Card Type</th>
										<th style='text-align: center'>Issuer Bank Name</th>
										<th style='text-align: center'>Issuer Country</th>
									</tr>
								</thead>
							</table>
						</div>
						</div></div></div></div></div></div>
					</td>
				</tr>


			</table></td></tr>
	</table>
<!-- <div class="post d-flex flex-column-fluid" id="kt_post">
			<div id="kt_content_container" class="container-xxl">
				<div class="row my-5">
					<div class="col">
				<div class="card">
				<div class="card-body">
	<div class="card "> -->
		<!-- <div class="card-header card-header-rose card-header-text">
		  <div class="card-text">
			<h4 class="card-title">Bin Range Upload</h4>

			<a href="../assets/Batch_operation_sample_file.csv" download="Batch Operation Sample" class="btn btn-primary" type="button">Download Sample File</a>
		  </div>

		</div> -->

		<!-- <div class="card-body "> -->


							<!-- <table width="100%" border="0" align="center" cellpadding="0"
		cellspacing="0" class="txnf product-spec"> -->
		<!-- <tr>
			<th colspan="6" align="left">UPLOAD FILE</th>
		</tr> -->
		<!-- <tr> -->

			<s:form action="binRangeManeger" method="POST"
			enctype="multipart/form-data" style="width:100%">
				<div class="row">

				<script type="text/javascript">
					$("body")
							.on(
									"click",
									"#btnUpload",
									function() {
										var allowedFiles = [ ".csv" ];
										var fileUpload = $("#fileUpload");
										var lblError = $("#lblError");
										var regex = new RegExp(
												"([a-zA-Z0-9\s_\\.\-:])+("
														+ allowedFiles
																.join('|')
														+ ")$");
										if (!regex.test(fileUpload.val()
												.toLowerCase())) {
											lblError
													.html("Please upload files having extensions: <b>"
															+ allowedFiles
																	.join(', ')
															+ "</b> only.");
											return false;
										}
										lblError.html('');
										return true;
									});
				</script>
				<div class="col-sm-6 col-lg-3">
					<table
						id="example" style="display: none;">
						<thead>
							<tr>
								<th>Bin Code High</th>
								<th>Bin Code Low</th>
								<th>Bin Range High</th>
								<th>Bin Range Low</th>
								<th>Card Type</th>
								<th>Group Code</th>
								<th>Issuer Bank Name</th>
								<th>Issuer Country</th>
								<th>Mop Type</th>
								<th>Rfu1</th>
								<th>Rfu2</th>
							</tr>
						</thead>
					</table>
				</div>
					<div class="row">

				<!-- <div class="col-sm-6 col-lg-3 bws-tp" style="margin-top:10px;">

						<span class="input-group-btn">
						<input type="text"
							class="  form-control form-control-solid" id="fileUpload" readonly>
							<span
							class="file-input btn-success btn-file btn-small btn-fl">
							<span
								class="glyphicon glyphicon-folder-open">
							</span>
								&nbsp;&nbsp;Browse <s:file name="fileName" />
						</span>
						</span></br></br></br>
						<span id="lblError" style="color: red;"></span> <br />
					</div> -->


				<!-- <div class="col-sm-6 col-lg-3">
					<s:submit
						value="Upload" name="fileName" id="btnUpload"
						class="btn btn-primary  mt-4 submit_btn" />
				</div> -->

				<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
			</div>
			</div>
			</s:form>

		<!-- </tr>
	</table> -->

							<!-- </div> -->

	<!-- <div>
		<table width="100%" align="left" cellpadding="0" cellspacing="0"
			class="txnf">
			<tr>
				<td align="left" colspan="3"><div class="container">
						<div class="row">
							<div class="col-md-7 col-xs-12 text-left">
								<h2 style="text-align:left!important;">Bin Range Upload</h2>
							</div>
						</div>
					</div></td>
			</tr>
		</table>
	</div>
	<table width="100%" border="0" align="center" cellpadding="0"
		cellspacing="0" class="txnf product-spec">
		<tr>
			<th colspan="6" align="left">UPLOAD REFUND FILE</th>
		</tr>
		<tr>
			<td width="46%" height="50" align="left" valign="bottom"><table
					id="example" style="display: none;">
					<thead>
						<tr>
							<th>Bin Code High</th>
							<th>Bin Code Low</th>
							<th>Bin Range High</th>
							<th>Bin Range Low</th>
							<th>Card Type</th>
							<th>Group Code</th>
							<th>Issuer Bank Name</th>
							<th>Issuer Country</th>
							<th>Mop Type</th>
							<th>Product Name</th>
							<th>Rfu1</th>
							<th>Rfu2</th>
						</tr>
					</thead>
				</table> Simple CSV File Format</td>
			<s:form action="binRangeManeger" method="POST"
				enctype="multipart/form-data">
				<script type="text/javascript">
					$("body")
							.on(
									"click",
									"#btnUpload",
									function() {
										var allowedFiles = [ ".csv" ];
										var fileUpload = $("#fileUpload");
										var lblError = $("#lblError");
										var regex = new RegExp(
												"([a-zA-Z0-9\s_\\.\-:])+("
														+ allowedFiles
																.join('|')
														+ ")$");
										if (!regex.test(fileUpload.val()
												.toLowerCase())) {
											lblError
													.html("Please upload files having extensions: <b>"
															+ allowedFiles
																	.join(', ')
															+ "</b> only.");
											return false;
										}
										lblError.html('');
										return true;
									});
				</script>
				<td width="29%" align="center" valign="middle"><div
						class="input-group bws-tp" style="margin-top: 10px;">
						<span > <input type="text"
							class="inputfieldsmall1" id="fileUpload" readonly><span
							class="file-input btn-success btn-file btn-small btn-fl" style="background: linear-gradient(60deg, #425185, #4a9b9b);"> <span
								class="glyphicon glyphicon-folder-open"></span>
								&nbsp;&nbsp;Browse <s:file name="fileName" />
						</span>
						</span></br></br></br>
						<span id="lblError" style="color: red;margin-left: -190%;"></span> <br />
					</div></td>
				<td width="25%" align="center" valign="middle"><s:submit
						value="Upload" name="fileName" id="btnUpload"
						class="btn btn-primary  mt-4 submit_btn" /></td>
				<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
			</s:form>
		</tr>
	</table> -->
	<!-- </div>
	</div></div></div></div></div></div> -->

</div>
	
	
	<script>
		$(document).on(
				'change',
				'.btn-file :file',
				function() {
					var input = $(this), numFiles = input.get(0).files ? input
							.get(0).files.length : 1, label = input.val()
							.replace(/\\/g, '/').replace(/.*\//, '');
					input.trigger('fileselect', [ numFiles, label ]);
				});

		$(document)
				.ready(
						function() {
							$('.btn-file :file')
									.on(
											'fileselect',
											function(event, numFiles, label) {

												var input = $(this).parents(
														'.input-group').find(
														':text'), log = numFiles > 1 ? numFiles
														+ ' files selected'
														: label;

												if (input.length) {
													input.val(log);
												} else {
													if (log)
														alert(log);
												}

											});
						});
	
		$(document).ready(function() {
			if ($('#manageBinRange').hasClass("active")) {
				var menuAccess = document.getElementById("menuAccessByROLE").value;
				var accessMap = JSON.parse(menuAccess);
				var access = accessMap["manageBinRange"];
				if (access.includes("Add")) {
					$("#submit").removeAttr("disabled");
				}
			}
		});
	</script>
	<script type="text/javascript">
			$('a.toggle-vis').on('click', function(e) {
				/* debugger */
				e.preventDefault();
				table = $('#BinRange').DataTable();
				// Get the column API object
				var column1 = table.column($(this).attr('data-column'));
				// Toggle the visibility
				column1.visible(!column1.visible());
				if ($(this)[0].classList[1] == 'activecustom') {
					$(this).removeClass('activecustom');
				} else {
					$(this).addClass('activecustom');
				}
			});
		</script>
	
	<script type="text/javascript">
		function myFunctions() {
			debugger
			var x = document.getElementById("actions11").value;
			if (x == 'csv') {
				document.querySelector('.buttons-csv').click();
			}
			if (x == 'copy') {
				document.querySelector('.buttons-copy').click();
			}
			if (x == 'pdf') {
				document.querySelector('.buttons-pdf').click();
			}

			// document.querySelector('.buttons-excel').click();
			// document.querySelector('.buttons-print').click();

		}
	</script>
	<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
	
	
</body>
</html>