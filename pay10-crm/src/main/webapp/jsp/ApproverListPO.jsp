<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.Format"%>

<html dir="ltr" lang="en-US">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Cash Deposit Approval</title>
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
<script
	src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
<script src="../assets/js/widgets.bundle.js"></script>
<script src="../assets/js/custom/widgets.js"></script>
<script src="../assets/js/custom/apps/chat/chat.js"></script>
<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
<script src="../assets/js/custom/utilities/modals/users-search.js"></script>
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
</style>
<style>
.dt-buttons {
	display: none;
}
</style>


<script type="text/javascript">
	var value = $("#colorPattern td").text();
	if (value < 0) {
		$("#colorPattern td").addClass("red");
	}
</script>

<style>
@media ( min-width : 992px) {
	.col-lg-3 {
		max-width: 30% !important;
	}
}
</style>
</head>

<body>
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
				<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Cash Deposit Approval</h1>
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
					<li class="breadcrumb-item text-muted">Cash Deposit Approval</li>
					<!--end::Item-->
					<!--begin::Item-->
					<li class="breadcrumb-item"><span
						class="bullet bg-gray-200 w-5px h-2px"></span></li>
					<!--end::Item-->
					<!--begin::Item-->
					<li class="breadcrumb-item text-dark">Cash Deposit Approval</li>
					<!--end::Item-->
				</ul>
				<!--end::Breadcrumb-->
			</div>
			<!--end::Page title-->

		</div>
		<!--end::Container-->
	</div>

<div id="kt_content_container" class="container-xxl">
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">
								<!--begin::Input group-->
								<div class="row g-9 mb-8">
									<div class="row g-9 mb-8 justify-content-end">
										<div class="row g-9 mb-8">
											<div>
												<table id="example"
													class="table table-striped table-row-bordered gy-5 gs-7 "
													style="width: 100%">
													<thead>
														<tr class="boxheadingsmall fw-bold fs-6 text-gray-800">
															<!-- <th  style="display: none;" class="col">Merchant Name</th> -->
															<th scope="col">Merchant Name</th>
															<th scope="col">Bank</th>
															<th scope="col">Amount</th>
															<th scope="col">Date</th>
															<th scope="col">Transaction Id</th>
															<th scope="col">Status</th>
															<th scope="col">Currency</th>
															<th scope="col">Remark</th>
															<th scope="col">Action</th>
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
		<script type="text/javascript">
		$(document).ready(function() {

				$(function() {
				renderTable();
			});
		});


		function submitFunction(){
			 debugger
			var	buttonName=document.getElementById("buttonName").value;
			var	payId=document.getElementById("payId").value;
			var	amount=document.getElementById("amount").value;
			var commonRemark=document.getElementById("approverRemark").value;
			var	rejectRemark="NA";
			var	approverRemark="NA";

			if(commonRemark == ""){
				alert("Please enter remark");
				return false;
			}
			if(buttonName=='accept'){
				approverRemark=commonRemark;
			}else{
				rejectRemark=commonRemark;
			}



			var	txnId=document.getElementById("txnId").value;
			var	remark=document.getElementById("remark").value;
			var	status=document.getElementById("status").value;
			var	currency=document.getElementById("currency").value;
			var	createDate=document.getElementById("createDate").value;

			if(approverRemark=="" ){
				 $('#errorMsgRemark').text('Please Enter Remark.');
				 $("#errorMsgRemark").show();
				 setTimeout(function () {
					 $("#errorMsgPayId").hide();
				 }, 4000);
			}
			else{
				$.post("ApproverListPO",
						{
					'cashDepositDTOPO.payId':payId,
					'cashDepositDTOPO.amount':amount,
					'cashDepositDTOPO.approverRemark':approverRemark,
					'cashDepositDTOPO.rejectRemark':rejectRemark,
					'cashDepositDTOPO.txnId':txnId,
					'cashDepositDTOPO.currency':currency,
					'cashDepositDTOPO.remark':remark,
					'cashDepositDTOPO.status':status,
					'cashDepositDTOPO.createDate':createDate,
						}
				,function(result){
					if(buttonName == "accept"){

						alert("Request Approved");
					}else{
						alert("Request Rejected");
					}

					window.location.reload();
				});

		}
		}
		var table='';
		function renderTable() {
			 table=$("#example").DataTable();
			$("#example").DataTable().destroy();
			var token = document.getElementsByName("token")[0].value;
			var buttonCommon = {
				exportOptions : {
					format : {
						body : function(data, column, row, node) {
							return data;
						}
					}
				}
			};
		table=	$('#example').DataTable({
				dom : 'BTftlpi',
				'columnDefs' : [ {
					'searchable' : false
				} ],
				 "ajax": {
                     "url": "CashDepositReport",
                     "type": "POST",

                   },
                   "bProcessing": true,
                   "bLengthChange": true,
                   "bAutoWidth": false,
                   "iDisplayLength": 10,
                   "order": [[3, "desc"]],
                   "aoColumns" : [ {
					"mData" : "businessName"
					//,
					//"visible":false
				}, {
					"mData" : "bank"
				}, {
					"mData" : "amount"
				}, {
					"mData" : "createDate"
				}, {
					"mData" : "txnId"
				}, {
					"mData" : "status"
				},{
					"mData" : "currency"
				}, {
					"mData" : "remark"
				},
		        {
		            "mData": 'action',
		            mRender: function (data, type, row, meta) {
		                return '<button class="btn btn-primary btn-sm mb-2 accept" style="width:80px" data-toggle="modal" data-target="#accept" value="accept" onclick="modalOpen(this)">Accept</button>' +
		                       '<button class="btn btn-danger btn-sm mb-2 accept" style="width:80px" data-toggle="modal" data-target="#accept" value="reject" onclick="modalOpen(this)">Reject</button>';
		              }
		          }

				]
			});

			$('#example tbody').on('click', '.accept', function() {
			      // Get the clicked row data
					debugger
			      var rowData = table.row($(this).closest('tr')).data();
			      // Perform action with the row data
			      console.log("Row Data:", rowData);
			      document.getElementById("payId").value=rowData.payId;
			      document.getElementById("amount").value=rowData.amount;
			      document.getElementById("remark").value=rowData.remark;
			      document.getElementById("txnId").value=rowData.txnId;
			      document.getElementById("status").value=rowData.status;
			      document.getElementById("currency").value=rowData.currency;
			      document.getElementById("createDate").value=rowData.createDate;
			      document.getElementById("bank").value=rowData.bank;

			    });

		}

		  function generatePostData() {

              var obj = {

              }
              return obj;
            }

		  function modalOpen(text) {
			  debugger
			  var buttonName=text.value;
			  var row=text.parentElement._DT_CellIndex.row;

			  document.getElementById("buttonName").value=buttonName;
			    $('#accept').show();

			}
		  function modalClose(){

			  $('#accept').hide();

		  }
	</script>
		<div class="modal" id="accept" tabindex="-1" role="dialog">

		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header" align="center">Approver Remark</div>
				<div class="modal-body">

					 <input type="text" id="approverRemark" name="approverRemark" class="form-control" placeholder="Remark">
					 <input type="hidden" id="payId">
					 <input type="hidden" id="amount">
					 <input type="hidden" id="txnId">
					 <input type="hidden" id="remark">
					 <input type="hidden" id="status">
					<input type="hidden" id="currency">
					 <input type="hidden" id="bank">
					 <input type="hidden" id="createDate">
					 <input type="hidden" id="buttonName">

				</div>

				<div class="mb-4" align="center">
					<button type="button" class="btn btn-primary btn-sm" onclick="submitFunction()">Submit</button>
					<button type="button" class="btn btn-primary btn-sm" onclick="modalClose()">Cancel</button>
				</div>
			</div>
		</div>
	</div>



	</body>

</html>
