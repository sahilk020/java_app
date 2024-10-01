<!DOCTYPE html
	PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
	<html>

	<head>
		<title>MOP Configuration</title>


		<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
		<!--begin::Fonts-->
		<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
		<!--end::Fonts-->
		<!--begin::Vendor Stylesheets(used by this page)-->
		<!-- <link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
						type="text/css" /> -->
		<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
		<!--end::Vendor Stylesheets-->
		<!--begin::Global Stylesheets Bundle(used by all pages)-->
		<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />
		<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
		<link href="https://getbootstrap.com/docs/5.3/assets/css/docs.css" rel="stylesheet">
		<title>Bootstrap Example</title>
		<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

		<script src="../assets/plugins/global/plugins.bundle.js"></script>
		<script src="../assets/js/scripts.bundle.js"></script>
		<script src="../js/jquery.popupoverlay.js"></script>


		<link href="../css/select2.min.css" rel="stylesheet" />
		<script src="../js/jquery.select2.js" type="text/javascript"></script>

		<script type="text/javascript">
			function EnableDisableTextBox(ddlModels) {
				var selectedValue = ddlModels.options[ddlModels.selectedIndex].value;
				var txtOther = document.getElementById("txtOther");
				txtOther.disabled = selectedValue == "Other"  ? false : true;
				if (!txtOther.disabled) {
					txtOther.focus();
				}
			}



			$(document).ready(function(){

				$("#mopConfigurationsTable").DataTable({
					retrieve : true
				});

				$("#ddlModel").on("change",function(){
					var GetValue=$("#ddlModel").val();
					$("#myTextbox").val(GetValue);
				});
				// $("#mopConfigurationsTable").DataTable();

			});
			function getAllMops(){
				var domain = window.origin;
				var acquirer = $("#acquirer").val();
				var currency = $("#currency").val();
				var paymentType = $("#paymentType").val();
				var settings = {

					"url": domain +"/crmws/getAllMop?acquirer="+acquirer +"&currency="+ currency+"&paymentType="+paymentType,
					"method": "GET",
					"timeout": 0,
				};

				$.ajax(settings).done(function (response) {
					$('#mopConfigurationsTable').DataTable().clear().destroy();
					// $('#mopConfigurationsTable tbody').empty();
					$("#mopConfigurationsTable").DataTable({
						"retrieve" : true,
						"data": response,
						"columns": [
							{ 'render': function (data, type, full, meta){
            					 return '<input type="checkbox" name="checkboxname" id="checkbox1" value="' + $('<div/>').text(data).html() + '">';

       				  } },
							{ "data": "acquirer" },
							{ "data": "currency" },
							{ "data": "currencyCode" },
							{ "data": "paymentType" },
							{ "data": "mopType" },
							{ "data": "bankId" },
							{ "data": "bankName" },
							{ "mRender": function(data, type, full) {
								return '<input type="button" class="disabled btn btn-info btn-sm update-btn" value="Update" id="datatableid" href=#/' + full[0] + '>'  + '</input>';
							},
							}
						]
					});
				});


			}

			$(document).on('click','input[name="checkboxname"]',function(){
				if($(this).is(':checked')){
					console.log("button checked");
					$(this).closest('tr').find("#datatableid").removeClass('disabled');
				//	$("#datatableid").addClass('disabled',false)
				}else{
					//$('a :[type="button"]').prop('disabled',true)
					console.log("button unchecked");
					$(this).closest('tr').find("#datatableid").addClass('disabled');
				}
			});




		</script>
		<style>
			.dt-buttons.btn-group.flex-wrap{
				display: none;
			}
			svg {
				margin-top: 6px;
			}
			#checkboxes {
	display: none;
	border: 1px #DADADA solid;
	height:180px;
	overflow-y: scroll;
	position:absolute;
	background:#fff;
	z-index:1;
	padding: 10px;
}
#checkboxes1 label {
  width: 74%;
}
#checkboxes1 input {
  width:18%;
}
div#checkboxes1{
position: absolute;
    overflow-y: auto;
    border: 1px solid #dadada;
    display: block;
    height: 180px;
    background: #ffff;
    z-index: 1;
    padding: 10px;
    width: 31%;
	display: none;
}

.required {
  color: red;
}
		</style>




	</head>

	<body class="p-3 m-0 border-0 bd-example m-0 border-0">




			<div class="post d-flex flex-column-fluid" id="kt_post">
				<!--begin::Container-->
				<div id="kt_content_container" class="container-xxl">
					<h1 style="font-weight: bolder; font-size: medium;">MOP Configuration</h1>

					<hr>
					<div class="row">

						<div class="row">
							<div class="col-md-4">
								<label for="inputState" class="form-label" style="font-weight: bold;">Acquirer</label>
								<select class="form-select form-select-solid" id="acquirer" class="form-select" >
									<option selected>Select Acquirer</option>
									<option value="QUOMO">QUOMO</option>
									<option value="DEMO">DEMO</option>
									<option value="SBI">SBI</option>

								</select>
							  </div>
							  <div class="col-md-4">
								<label for="inputState" class="form-label" style="font-weight: bold;">Currency</label>
								<select class="form-select form-select-solid" id="currency" class="form-select" >
									<option selected>Select Currency</option>
									<option value="INR">INR</option>
									<option value="MYR">MYR</option>
									<option value="PHP">PHP</option>
									<option value="VND">VND</option>

								</select>
							  </div>
							  <div class="col-md-4">
								<label for="inputState" class="form-label" style="font-weight: bold;">Payment Type</label>
								<select class="form-select form-select-solid" id="paymentType" class="form-select">
									<option selected>All</option>
									<option value="CC">CC</option>
									<option value="DC">DC</option>
									<option value="NB">NB</option>
								</select>
							  </div>
						  </div>

					</div>


					<div class=" mt-4">
						<div class="row g-3 ">
							<div class="col-auto">
								<button type="button" class="btn btn-primary mb-3" data-bs-toggle="modal" data-bs-target="#staticBackdrop">Add New MOP</button>
					  		</div>
							<div class="col-auto">
						  		<button type="submit" class="btn btn-primary mb-3" onclick="getAllMops()">Submit</button>
							</div>
						</div>
					</div>

					<!-- MODAL -->
					<div class="modal fade" id="staticBackdrop" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
						<div class="modal-dialog modal-xl">
						  <div class="modal-content">
							<div class="modal-header">
							  <h1 class="modal-title fs-5" id="staticBackdropLabel">New MOP Configuration</h1>
							  <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
							</div>
							<br>
							<div class="modal-body">

								<div class="row">

									<div class="row">
										<div class="col-md-4">
											<label for="inputState" class="form-label" style="font-weight: bold;">Acquirer<span class="required"></span></label>
											<select class="form-select form-select-solid" id="inputState" class="form-select" >
												<option selected>Select Acquirer</option>
												<option value="QUOMO">QUOMO</option>
												<option value="DEMO">DEMO</option>
												<option value="SBI">SBI</option>

											</select>
										  </div>
										  <div class="col-md-4">
											<label for="inputState" class="form-label" style="font-weight: bold;">Currency<span class="required"></span></label>
											<select id="ddlModel" class="form-select form-select-solid" id="inputState" class="form-select" >
												<option selected>Select Currency</option>
												<option value="356">INR</option>
												<option value="458">MYR</option>
												<option value="608">PHP</option>
												<option value="704">VND</option>

											</select>
										  </div>
										  <div class="col-md-4">
											<label for="inputState" class="form-label" style="font-weight: bold;">Payment Type<span class="required"></span></label>
											<select class="form-select form-select-solid" id="inputState" class="form-select">
												<option selected>All</option>
												<option value="CC">CC</option>
												<option value="DC">DC</option>
												<option value="NB">NB</option>
											</select>
										  </div>
									  </div>

									  <div class="row mt-4">

										<div class="col-md-4">
											<label for="inputState" class="form-label" style="font-weight: bold;">Currency Code</label>
											<input class="form-control form-control-solid" type="text" id="myTextbox" disabled="disabled" />
										  </div>

										<div class="col-md-4">
											<label for="inputState" class="form-label" style="font-weight: bold;">MOP Type<span class="required"></span></label>
											<select class="form-select form-select-solid" id="inputState" class="form-select" onchange = "EnableDisableTextBox(this)">
												<option selected>Select MOP</option>
												<option value="VISA">VISA</option>
												<option value="MASTER CARD">MASTER CARD</option>
												<option value="RUPAY">RUPAY</option>
												<option value="Other">Other</option>

											</select>
										  </div>

										  <div class="col-md-4">
											<label for="inputState" class="form-label" style="font-weight: bold;">Other MOP</label>
											<input class="form-control form-control-solid" type="text" id="txtOther" disabled="disabled" />
										  </div>
									  </div>

									  <div class="row mt-4">

										<div class="col-md-4">
											<label for="inputState" class="form-label" style="font-weight: bold;">Bank Name<span class="required"></span></label>
											<input class="form-control form-control-solid" type="text"  />
										  </div>

										  <div class="col-md-4">
											<label for="inputState" class="form-label" style="font-weight: bold;">Bank Code<span class="required"></span></label>
											<input class="form-control form-control-solid" type="text" />
										  </div>
									  </div>


								</div>





							</div>
							<div class="modal-footer">
							  <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
							  <button type="button" class="btn btn-primary">ADD NEW MOP</button>
							</div>
						  </div>
						</div>
					  </div>


<div class="row my-5">
	 <div class="col">
             <div class="card">


				<div class="card-body">

					<div class="scrollD">
						<table  class="table table-striped table-row-bordered gy-5 gs-7"  id="mopConfigurationsTable" align="center" cellspacing="0" width="100%">
							<thead>
								<tr class="boxheadingsmall fw-bold fs-6 text-gray-800" style="font-size: 11px;">
									<th style="text-align: center;">S No.</th>
									<th style="text-align: center;">Acquirer</th>
									<th style="text-align: center;">Currency</th>
									<th style="text-align: center;">Currency Code</th>
									<th style="text-align: center;">Payment Type</th>
									<th style="text-align: center;">MOP Type</th>
									<th style="text-align: center;">Bank MOP Type</th>
									<th style="text-align: center;">Bank name</th>
									<th style="text-align: center;">Action</th>

								</tr>
							</thead>
							<tfoot>
								<tr class="boxheadingsmall">
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
								</tr>
							</tfoot>
						</table>
					</div>

				</div>

			</div>
	 </div>

</div>



		<script src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/index.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/xy.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/percent.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/radar.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/themes/Animated.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/map.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/geodata/worldLow.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/geodata/continentsLow.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/geodata/usaLow.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/geodata/worldTimeZonesLow.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/geodata/worldTimeZoneAreasLow.js"></script>
		<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
		<!--end::Vendors Javascript-->
		<!--begin::Custom Javascript(used by this page)-->
		<script src="../assets/js/widgets.bundle.js"></script>
		<script src="../assets/js/custom/widgets.js"></script>
		<script src="../assets/js/custom/apps/chat/chat.js"></script>
		<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
		<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
		<script src="../assets/js/custom/utilities/modals/users-search.js"></script>





	</body>

	</html>