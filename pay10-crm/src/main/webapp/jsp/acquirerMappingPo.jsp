<%@ taglib uri="/struts-tags" prefix="s" %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Acquirer Mapping</title>
<link
	href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
	type="text/css" />
<script src="../js/loader/main.js"></script>
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
<script src="../js/commanValidate.js"></script>
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>


<style type="text/css">
.dt-buttons {
	margin-top: 35px !important;
}

.svg-icon {
	margin-top: 1vh !important;
}

.dt-buttons {
	display: none;
}
</style>

<style>
@media ( min-width : 992px) {
	.col-lg-3 {
		max-width: 30% !important;
	}
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
									<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Acquirer Mapping</h1>
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
										<li class="breadcrumb-item text-muted">Acquirer Mapping</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item">
											<span class="bullet bg-gray-200 w-5px h-2px"></span>
										</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item text-dark">Acquirer Mapping</li>
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
                                         <div class="row g-9 mb-8">
                                         
                                         <form id="AcquirerMapping">

											<div class="row my-3 align-items-center">
												<div class="col-lg-3 my-2">

												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">Merchant Name</label>
												<div class="txtnew">
													<s:select name="payId"
														class="form-select form-select-solid" id="payId" headerValue="Select Merchant" headerKey="Select Merchant" list="payIdList"/>
												</div>

											</div>
												<div class="col-lg-3 my-2">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="required">Acquirer</span>
													</label> 
													<select id="acquirer"
														class="form-select form-select-solid adminMerchants">
														<option value="Select Acquirer">Select Acquirer</option>
														<option value="PAY10">PAY10</option>
														<option value="HTPAY">HTPAY</option>
														<option value="QUOMO">QUOMO</option>
														<option value="BTSEPAY">BTSEPAY</option>
													</select>

												</div>

													<div class="col-lg-3 my-2">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="required">Currency</span>
													</label> 
													
													<s:select name="currency" listKey="code" listValue="name"
														class="form-select form-select-solid" id="currency" headerValue="Select Currency" headerKey="Select Currency" list="currencyList"/>

												</div>
												
												<div class="col-lg-3 my-2">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="required">Type</span>
													</label> 
													<select id="type"
														class="form-select form-select-solid adminMerchants">
														<option value="Select Type">Select Type</option>
														<option value="Flat">Flat</option>
														<option value="Percentage">Percentage</option>
													</select>

												</div>
												
												<div class="col-lg-3 my-2">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="required">Value</span>
													</label> 
													<input type="number" id="value" min="0" 
														class="form-control adminMerchants" >

												</div>
												
												<div class="col-lg-1 my-2">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="">&nbsp;</span>
													</label>
													<button type="button" class="btn btn-primary"
														id="SubmitButton" onclick="sumbitButton()">Submit</button>
												</div>

											</div>
										</form>
                                         

									</div>



								<div id="kt_content_container" class="container-xxl">
									<div class="row my-5">
										<div class="col">
											<div class="card">
												<div class="card-body">
													<!--begin::Input group-->
													<div class="row g-9 mb-8">
														<!--begin::Col-->
														<div class="col">


															<div class="row g-9 mb-8">
																<div
																	class="table-responsive dataTables_wrapper dt-bootstrap4 no-footer">
																	<table id="example"
																	class="table table-striped table-row-bordered gy-5 gs-7 dataTable no-footer">
																		<thead>
																			<tr class="fw-bold fs-6 text-gray-800">
																				<th class="min-w-90px">Pay Id</th>
																				<th class="min-w-90px">Merchant Name</th>
																				<th class="min-w-90px">Acquirer</th>
																				<th class="min-w-90px">Currency</th>
																				<th class="min-w-90px">Value</th>
																				<th class="min-w-90px">Type</th>
																				<th class="min-w-90px">Create Date</th>
																			</tr>
																		</thead>
																		<tfoot>
																			<tr class="fw-bold fs-6 text-gray-800">
																				<th class="min-w-90px"></th>
																				<th class="min-w-90px"></th>
																				<th class="min-w-90px"></th>
																				<th class="min-w-90px"></th>
																				<th class="min-w-90px"></th>
																				<th class="min-w-90px"></th>
																				<th class="min-w-90px"></th>
																			</tr>
																		</tfoot>
																	</table>
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
</div>
</div>
</div>
</div>
	</div>

                  </div>
					


<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
 
	<script type="text/javascript">

function validateDecimalInput() {
	
	var input=$("#value").val();
      // Regular expression to match numbers with up to 4 decimal places
      var decimalRegex = /^\d+(\.\d{1,4})?$/;

      // Check if the input value matches the regex pattern
      if (decimalRegex.test(input)) {
        // Valid input
        return false;
      } else {
        // Invalid input
       return true;
      }
    }
$('#example')

						.dataTable(

								{
									
									"columnDefs" : [ {
										className : "dt-body-right",
										"targets" : [ 1, 2, 3, 4, 5, 6, 7]
									} ],

									dom : 'Brtipl',
									buttons : [
											{
												extend : 'print',
												exportOptions : {
													columns : ':visible'
												}
											},
											{
												extend : 'pdfHtml5',
												orientation : 'landscape',
												pageSize : 'legal',
												//footer : true,
												title : 'Search Transactions',
												exportOptions : {
													columns : [ ':visible' ]
												},
												customize : function(doc) {
													doc.defaultStyle.alignment = 'center';
													doc.styles.tableHeader.alignment = 'center';
													doc.defaultStyle.fontSize = 8;
												}
											},
											{
												extend : 'copy',
												exportOptions : {
													columns : [ 0, 1, 2, 3, 4,
															5, 6 ]
												}
											},
											{
												extend : 'csv',
												exportOptions : {
													columns : [ 0, 1, 2, 3, 4,
															5, 6]
												}
											},
											{
												extend : 'pdf',
												exportOptions : {
													columns : [ 0, 1, 2, 3, 4,
															5, 6 ]
												}
											}, 'colvis', 'excel', 'print', ],
									scrollY : true,
									scrollX : true,
									searchDelay : 500,
									processing : false,
									destroy : true,

									serverSide : true,
									order : [ [ 5, 'desc' ] ],
									stateSave : true,

									"ajax" : {

										"url" : "CommissionReport",
										"type" : "POST",
										"timeout" : 0,
										"data" : function(d) {
											return generatePostData(d);
										}
									},
									"fnDrawCallback" : function() {
										$("#submit").removeAttr("disabled");
										$('#loader-wrapper').hide();
									},
									"searching" : false,
									"ordering" : false,
									"processing" : true,
									"serverSide" : true,
									"paginationType" : "full_numbers",
									"lengthMenu" : [ [ 10, 25, 50, 100 ],
											[ 10, 25, 50, 100 ] ],
									"order" : [ [ 2, "desc" ] ],

									"columnDefs" : [ {
										"type" : "html-num-fmt",
										"targets" : 4,
										"orderable" : true,
										"targets" : [ 0, 1, 2, 3, 4, 5, 6]
									} ],

									"columns" : [

											{
												"data" : "payId",
											},
											{
												"data" : "merchantName",
											},
											{
												"data" : "acquirer",
											},
											{
												"data" : "currency",
											},
											{
												"data" : "value",
												
											},
											{
												"data" : "type"
											},
											{
												"data" : "createDate"
											} ]

								});
function generatePostData(d) {
	var token = document.getElementsByName("token")[0].value;
    var obj = {
		draw : d.draw,
		length : d.length,
		start : d.start,
		token : token,
		"struts.token.name" : "token",
    }
    return obj;
  }


	$(document).ready(function(){
		//renderTable();
	});
	
		function sumbitButton() {
			  document.getElementById("SubmitButton").disabled = true;
			debugger
			var payid = document.getElementById("payId").value;
			var acquirer = document.getElementById("acquirer").value;
			var currency = document.getElementById("currency").value;
			var type = document.getElementById("type").value;
			var value = document.getElementById("value").value;

			if (payid == 'Select Merchant') {
				alert("Please Select Merchant");
				document.getElementById("SubmitButton").disabled = false;
				return false;
			} else if (acquirer == 'Select Acquirer') {
				alert("Please Select Acquirer");
				document.getElementById("SubmitButton").disabled = false;
				return false;
			} else if (currency == 'Select Currency') {
				alert("Please Select Currency");
				document.getElementById("SubmitButton").disabled = false;
				return false;
			}else if(type=='Select Type'){
				alert("Please Select Type");
				document.getElementById("SubmitButton").disabled = false;
				return false;
			}
			else if(value<=0){
				alert("Please Enter valid value");
				document.getElementById("SubmitButton").disabled = false;
				return false;
			}

			if(validateDecimalInput()){
				alert("You Cannot provide more than 4 decimal in value");
				document.getElementById("SubmitButton").disabled = false;
				return false;
			}
			
		$.post("AcquirerMappingSave", {
				payId : payid,
				acquirer : acquirer,
				currency : currency,
				type:type,
				value:value,
					
			}, function(result) {
				if (result != null) {
<<<<<<< HEAD
					alert("Mapping Inserted/Updated Sucessfully!");
=======
					alert(result.response + " Inserted or Updated !");
>>>>>>> payout_flow
					document.getElementById("SubmitButton").disabled = false;
					window.location.reload();
				}else{
					alert("There is some problem !");
					document.getElementById("SubmitButton").disabled = false;
				}
			});

		}

	</script>

</body>