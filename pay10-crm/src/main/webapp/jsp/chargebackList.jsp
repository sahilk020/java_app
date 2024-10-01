<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
  <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
  <%@ taglib uri="/struts-tags" prefix="s" %>
    <html>

    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
      <title>Chargeback List</title>


      <link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
      <!--begin::Fonts-->
      <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
      <!--end::Fonts-->
      <!--begin::Vendor Stylesheets(used by this page)-->
      <link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
      <link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />

      <!--end::Vendor Stylesheets-->
      <!--begin::Global Stylesheets Bundle(used by all pages)-->
      <link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
      <link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
      <link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />

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
        .dt-buttons.btn-group.flex-wrap {
    display: none !important;
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
    </head>

    <body id="kt_body"
      class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed toolbar-tablet-and-mobile-fixed aside-enabled aside-fixed"
      style="--kt-toolbar-height:55px;--kt-toolbar-height-tablet-and-mobile:55px">
      <div class="content d-flex flex-column flex-column-fluid" id="kt_content">
        <div class="toolbar" id="kt_toolbar">
          <!--begin::Container-->
          <div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
            <div data-kt-swapper="true" data-kt-swapper-mode="prepend"
              data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
              class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
              <!--begin::Title-->
              <h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
                Chargeback List</h1>
              <!--end::Title-->
              <!--begin::Separator-->
              <span class="h-20px border-gray-200 border-start mx-4"></span>
              <!--end::Separator-->
              <!--begin::Breadcrumb-->
              <ul class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
                <!--begin::Item-->
                <li class="breadcrumb-item text-muted"><a href="home"
                    class="text-muted text-hover-primary">Dashboard</a></li>
                <!--end::Item-->
                <!--begin::Item-->
                <li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span>
                </li>
                <!--end::Item-->
                <!--begin::Item-->
                <li class="breadcrumb-item text-muted">Chargeback</li>
                <!--end::Item-->
                <!--begin::Item-->
                <li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span>
                </li>
                <!--end::Item-->
                <!--begin::Item-->
                <li class="breadcrumb-item text-dark">Chargeback List</li>
                <!--end::Item-->
              </ul>
              <!--end::Breadcrumb-->
            </div>
            <!--end::Page title-->

          </div>
        </div>
        <div class="post d-flex flex-column-fluid" id="kt_post">
          <!--begin::Container-->
          <div id="kt_content_container" class="container-xxl">
            <div class="row my-5">
              <div class="col">
                <div class="card">
                  <div class="card-body">
                    <s:form action="chargeBackFormAction" id="form">
                      <s:hidden name="id" id="id" />
                      <div class="row">
                        <div class="col-3">
                          <button type="button" class="btn btn-primary form-control" name="create" id="create"
                            onClick="redirect()">Create Chargeback</button>
                        </div>
                        <%-- <div class="col-3">
                          <input type="file" name="file" id="file" class="form-control"
                            accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
                        </div>
                        <div class="col-3">
                          <button type="button" class="btn btn-primary form-control" name="submitFile" id="submitFile"
                            onClick="saveFile()">Submit Document</button>
                        </div>
                        <div class="col-3">
														<span class=""></span> <a href="../assets/Chargeback_Bulkupload_Sample.xlsx"
															download="Chargeback_Bulkupload_Sample"
															class="btn btn-primary btn-xs" id="sampleFileDownload">Sample File Download</a>
												</div> --%>
                      </div>
                      <span id="excelError" class="error"></span>
                    </s:form>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div style="overflow: scroll !important;">
          <div class="post d-flex flex-column-fluid" id="kt_post">

            <div class="post d-flex flex-column-fluid" id="kt_post">
              <!--begin::Container-->
              <div id="kt_content_container" class="container-xxl">
            <div class="row my-5">
              <div class="col">
                <div class="card">
                  <div class="card-body">
                    <div class="row g-9 mb-8 justify-content-end">

                      <div class="col-lg-4 col-sm-12 col-md-6">
                        <select name="currency" data-control="select2" data-placeholder="Actions" id="actions11"
                          class="form-select form-select-solid actions" data-hide-search="true"
                          onchange="myFunction();">
                          <option value="">Actions</option>
                          <option value="copy">Copy</option>
                          <option value="csv">CSV</option>
                          <!-- <option value="pdf">PDF</option> -->
                        </select>
                      </div>
                     <div class="col-lg-4 col-sm-12 col-md-6">
															<div class="dropdown1">
																<button
																	class="form-select form-select-solid actions dropbtn1">Customize
																	Columns</button>
													<div class="dropdown-content1">
														<a class="toggle-vis" data-column="2">Merchant</a>
														<a class="toggle-vis" data-column="3">Pay ID</a> 
														<a class="toggle-vis" data-column="4">CB Case Id</a> 
														<a class="toggle-vis" data-column="5">Txn Amount</a> 
														<a	class="toggle-vis" data-column="6">PG Case ID</a> 
														<a class="toggle-vis" data-column="7">Merchant Txn ID</a> 
														<a class="toggle-vis" data-column="8">Order ID</a> 
														<a class="toggle-vis" data-column="9">PG Ref No</a> 
														<a class="toggle-vis" data-column="10">Bank Txn ID</a> 
														<a class="toggle-vis" data-column="11">CB Amount</a> 
														<a class="toggle-vis" data-column="12">CB Reason</a> 
														<a class="toggle-vis" data-column="13">CB Reason Code</a> 
														<a class="toggle-vis" data-column="14">CB Intimation Date</a> 
														<a class="toggle-vis" data-column="15">CB Deadline Date</a> 
														<a class="toggle-vis" data-column="16">Mode Of Payment</a> 
														<a class="toggle-vis" data-column="17">Acquirer Name</a> 
														<a class="toggle-vis" data-column="18">Settlement Date</a> 
														<a class="toggle-vis" data-column="19">Date Of Txn</a> 
														<a class="toggle-vis" data-column="20">Customer Name</a> 
														<a class="toggle-vis" data-column="21">Customer Phone</a> 
														<a class="toggle-vis" data-column="22">Email</a> 
														<a class="toggle-vis" data-column="23">Notification Email</a> 
														<a class="toggle-vis" data-column="24">Status</a> 
													</div>
												</div>
														</div>
                    </div>

                    <s:if test="%{#session.USER.UserGroup.group =='Merchant'}">


                     
                        <table id="chargebackdataTable" class="table table-striped table-row-bordered gy-5 gs-7"
                          cellspacing="0" width="100%">
                          <thead>
                            <tr class="boxheadingsmall fw-bold fs-6 text-gray-800">
                              <th>Serial No.</th>
                              <th></th>
                              <th>Merchant</th>
                              <th>PayId</th>
                              <th>CB CaseId</th>
                              <th>Txn Amt</th>
                              <th>PG CaseId</th>
                              <th>MerchantTxnId</th>
                              <th>OrderId</th>
                              <th>PgRefNo</th>
                              <th>BankTxnId</th>
                              <th>CbAmount</th>
                              <th>CbReason</th>
                              <th>CbReasonCode</th>
                              <th>Cb Intimation Date</th>
                              <th>Cb Deadline Date</th>
                              <th>ModeOfPayment</th>
                              <th>AcquirerName</th>
                              <th>Settlement Date</th>
                              <th>DateOfTxn</th>
                              <th>CustomerName</th>
                              <th>CustomerPhone</th>
                              <th>Email</th>
                              <th>Notification Email</th>
                              <th>Status</th>
                              <th>Action</th>
                            </tr>
                          </thead>
                          <tbody></tbody>
                        </table>
                     
                    </s:if>


                    <s:else>


                     
                        <table id="datatable" class="table table-striped table-row-bordered gy-5 gs-7" cellspacing="0"
                          width="100%">
                          <thead>
                            <tr class="boxheadingsmall fw-bold fs-6 text-gray-800">
                              <th>Serial No.</th>
                              <th></th>
                              <th>Merchant</th>
                              <th>PayId</th>
                              <th>CB CaseId</th>
                              <th>Txn Amt</th>
                              <th>PG CaseId</th>
                              <th>MerchantTxnId</th>
                              <th>OrderId</th>
                              <th>PgRefNo</th>
                              <th>BankTxnId</th>
                              <th>CbAmount</th>
                              <th>CbReason</th>
                              <th>CbReasonCode</th>
                              <th>Cb Intimation Date</th>
                              <th>Cb Deadline Date</th>
                              <th>ModeOfPayment</th>
                              <th>AcquirerName</th>
                              <th>Settlement Date</th>
                              <th>DateOfTxn</th>
                              <th>CustomerName</th>
                              <th>CustomerPhone</th>
                              <th>Email</th>
                              <th>Notification Email</th>
                              <th>Merchant Favour</th>
                              <th>Bank Favour</th>
                              <th>Status</th>
                              <th>Action</th>
                            </tr>
                          </thead>
                          <tbody></tbody>
                        </table>
                   

                    </s:else>
                  </div>

                </div>
              </div>
            </div>
            </div>
            </div>
          </div>
        </div>


		<div class="modal" id="myModal" role="dialog">
			<div class="modal-dialog">

				<!-- Modal content-->
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title">CB Close</h4>
						<button type="button" class="close" onclick="closePopup()" data-dismiss="modal">&times;</button>
					</div>
					<div class="modal-body">
						<h5>Who are you favoring?</h5>
						<br>
            			<div class="btn-group-toggle" data-toggle="buttons">
               				 <label class="btn btn-default">
                    			<input type="radio" name="favour" checked="checked" value="MERCHANT_FAVOUR" autocomplete="off"> MERCHANT
                			</label>
                			<label class="btn btn-default">
                    			<input type="radio" name="favour" value="BANK_ACQ_FAVOUR" autocomplete="off"> CUSTOMER
                			</label>
                			<input type="hidden" id="id">
                			<input type="hidden" id="cbCaseId">
                			<input type="hidden" id="pgRefNo">
           	 			</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-primary" onclick="editPopup()" data-dismiss="modal">Submit</button>
						<button type="button" class="btn btn-primary" onclick="closePopup()" data-dismiss="modal">Close</button>
					</div>
				</div>

			</div>
		</div>


	</div>
      <script type="text/javascript">

        $(document).ready(function () {
          var userType = "<s:property value='%{#session.USER.UserGroup.group}'/>";
          console.log(userType)
          if (userType == 'Admin' || userType == 'Sub Admin'|| userType == 'Risk') {
            renderTable();
          } else {

            $('#create').hide();
            $('#file').hide();
            $('#submitFile').hide();
            $('#sampleFileDownload').hide();
            
            renderchargebackTable();
          }
        });

        function renderTable(data) {
            // Ensure table is defined before using it
            var table = $("#datatable").DataTable();

            // Destroy any existing DataTable instance
            table.destroy();

            // Initialize the DataTable with new data
            $('#datatable').dataTable({
                dom: 'fBTtlpi',
                buttons: [
                    {
                        extend: 'csv',
                        exportOptions: {
                            columns: function(idx, data, node) {
                                // Get all visible columns
                                var visibleColumns = table.columns(':visible').indexes().toArray();
                                // Exclude the last visible column
                                if (visibleColumns.includes(idx) && idx !== visibleColumns[visibleColumns.length - 1]) {
                                    return true;
                                }
                                return false;
                            }
                        }
                    },
                    {
                        extend: 'copy',
                        exportOptions: {
                            columns: function(idx, data, node) {
                                // Get all visible columns
                                var visibleColumns = table.columns(':visible').indexes().toArray();
                                // Exclude the last visible column
                                if (visibleColumns.includes(idx) && idx !== visibleColumns[visibleColumns.length - 1]) {
                                    return true;
                                }
                                return false;
                            }
                        }
                    }
                ],
                scrollX: true,
                scrollY: true,
                "aaData": data,
                "bProcessing": true,
                "bLengthChange": true,
                "bAutoWidth": false,
                "iDisplayLength": 10,
                "order": [[0, "asc"]],
                "aoColumns": [
                    { "mData": null, render: (data, type, row, meta) => meta.row + 1 },
                    { "mData": "id", "visible": false, "className": "displayNone" },
                    { "mData": "merchantName" },
                    { "mData": "merchant_pay_id" },
                    { "mData": "cb_case_id" },
                    { "mData": "txn_amount" },
                    { "mData": "pg_case_id" },
                    { "mData": "merchant_txn_id" },
                    { "mData": "order_id" },
                    { "mData": "pgRefNo" },
                    { "mData": "bank_txn_id" },
                    { "mData": "cb_amount" },
                    { "mData": "cb_reason" },
                    { "mData": "cb_reason_code" },
                    { "mData": "cb_intimation_date" },
                    { "mData": "cb_deadline_date" },
                    { "mData": "mode_of_payment" },
                    { "mData": "acq_name" },
                    { "mData": "settlement_date" },
                    { "mData": "date_of_txn" },
                    { "mData": "customer_name" },
                    { "mData": "customer_phone" },
                    { "mData": "email" },
                    { "mData": "nemail" },
                    { "mData": "cbClosedInFavorMerchant" },
                    { "mData": "cbClosedInFavorBank" },
                    { "mData": "status" },
                    {
                        "mData": null,
                        "sClass": "center",
                        "mRender": function (data) {
                            if (data.status == 'CLOSED') {
                                return '<div class="row" style="display:none;"><button class="btn btn-primary btn-xs" name="close" id="close" onclick="editPopup()">Close</button>&nbsp;<button class="btn btn-primary btn-xs" name="pod" id="pod">Pod</button></div>';
                            } else if (data.status == 'POD') {
                                return '<div class="row"><button class="btn btn-primary btn-xs" name="close" id="close" onclick="openPopup(' + data.id + ',' + data.cb_case_id + ',' + data.pgRefNo + ')">Close</button>&nbsp;<a class="btn btn-primary btn-xs" name="download" onclick="downloadFile(' + data.id + ',' + data.cb_case_id + ',' + data.merchant_pay_id + ')" id="download">Download</a></div>';
                            } else {
                                return '<div class="row"><button class="btn btn-primary btn-xs" name="close" id="close" onclick="openPopup(' + data.id + ',' + data.cb_case_id + ',' + data.pgRefNo + ')">Close</button></div>';
                            }
                        }
                    }
                ]
            });
        }

      </script>
      <script>

	  function openPopup(id,cbCaseId,pgRefNo){
		  $("#id").val(id);
		  $("#cbCaseId").val(cbCaseId);
		  $("#pgRefNo").val(pgRefNo);
		  $("#myModal").show();
	  }

	  function closePopup(){
		  $("#id").val("");
		  $("#cbCaseId").val("");
		  $("#pgRefNo").val("");
		  $("#myModal").hide();
	  }

	  function downloadFile(id,cbCaseId,payId){
			//call here from db
			debugger
			var urls = new URL(window.location.href);
	        var domain = urls.origin;
			 $.ajax({
		            type: "GET",
		            url: domain + "/crmws/downloadDoc/" + cbCaseId,
		            xhrFields: {
                        responseType: 'blob' // Important
                    },
                    success: function(data, status, xhr) {
                        // Get the filename from the Content-Disposition header
                        var filename = "";
                        var disposition = xhr.getResponseHeader('Content-Disposition');
                        if (disposition && disposition.indexOf('attachment') !== -1) {
                            var matches = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/.exec(disposition);
                            if (matches != null && matches[1]) {
                                filename = matches[1].replace(/['"]/g, '');
                            }
                        }

                        var blob = new Blob([data], { type: xhr.getResponseHeader('Content-Type') });
                        var url = window.URL.createObjectURL(blob);

                        var a = document.createElement('a');
                        a.style.display = 'none';
                        a.href = url;
                        a.download = filename;
                        document.body.appendChild(a);
                        a.click();
                        window.URL.revokeObjectURL(url);
                    },
                    error: function(xhr, status, error) {
                        console.error('Error downloading file:', error);
                    }
		          });
	  }
      
      function editPopup() {
       
		var cbClosedInFavorMerchant="";
        var cbClosedInFavorBank="";
        
        var id=$("#id").val();
		var cbCaseId=$("#cbCaseId").val();
		var pgRefNo=$("#pgRefNo").val();
		
		var favour=$('input[name="favour"]:checked').val();
		if(favour=="MERCHANT_FAVOUR"){
			cbClosedInFavorMerchant="MERCHANT_FAVOUR";
		}else{
			cbClosedInFavorBank="BANK_ACQ_FAVOUR";
		}
		
        //update here for close api
        var urls = new URL(window.location.href);
        var userEmailId = "<s:property value='%{#session.USER.emailId}'/>";
        var userType = "<s:property value='%{#session.USER.UserGroup.group}'/>";
        var domain = urls.origin;

        var obj = {
                "cbClosedInFavorMerchant": cbClosedInFavorMerchant,
                "cbClosedInFavorBank": cbClosedInFavorBank,
                "pgRefNo": pgRefNo,
                "cb_case_id": cbCaseId,
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


      
        function renderchargebackTable(data) {

          $("#chargebackdataTable").DataTable().destroy();
          $('#chargebackdataTable').dataTable({
            dom: 'BTftlpi',
            
            scrollX:true,
            scrollY:true,
            'columnDefs': [{
               "searchable": true, "targets": 0 
            }],
            "aaData": data,
            "bProcessing": true,
            "bLengthChange": true,
            "bAutoWidth": false,
            "iDisplayLength": 10,
            "order": [[0, "asc"]],
            "aoColumns": [{
              "mData": null,
              render: (data, type, row, meta) => meta.row + 1
            }, {
              "mData": "id",
              "visible": false,
              "className": "displayNone"
            }, {
              "mData": "merchantName"
            }, {
              "mData": "merchant_pay_id"
            }, {
              "mData": "cb_case_id"
            }, {
              "mData": "txn_amount"
            }, {
              "mData": "pg_case_id"
            }, {
              "mData": "merchant_txn_id"
            }, {
              "mData": "order_id"
            }, {
              "mData": "pgRefNo"
            }, {
              "mData": "bank_txn_id"
            }, {
              "mData": "cb_amount"
            }, {
              "mData": "cb_reason"
            }, {
              "mData": "cb_reason_code"
            }, {
              "mData": "cb_intimation_date"
            }, {
              "mData": "cb_deadline_date"
            }, {
              "mData": "mode_of_payment"
            }, {
              "mData": "acq_name"
            }, {
              "mData": "settlement_date"
            }, {
              "mData": "date_of_txn"
            }, {
              "mData": "customer_name"
            }, {
              "mData": "customer_phone"
            }, {
              "mData": "email"
            }, {
              "mData": "nemail"
            }, {
              "mData": "status"
            }, {
              "mData": null,
              "sClass": "center",
              "mRender": function (data) {

                return '<h1><button class="btn btn-info btn-xs"  name="edit" id="edit" onclick="getData(' + data.id + ')">Edit</button></h1>';

              }
            }]
          });

        } 
      </script>
      <script type="text/javascript">
        function redirect(id) {
          $("#id").val(id);
          $('#form').submit();
        }

      </script>

      <script>
        $(document).ready(function () {
          var urls = new URL(window.location.href);
          var domain = urls.origin;
          var userType = "<s:property value='%{#session.USER.UserGroup.group}'/>";
          var payId = "<s:property value='%{#session.USER.payId}'/>";
          if (userType != 'Merchant') {
            payId = '';
          }

          $.ajax({
            type: "GET",

            url: domain + "/crmws/list?payId=" + payId,
            //url :"http://localhost:8080/crmws/list?payId=" + payId,
            success: function (data, status) {
              debugger
              var response = JSON.parse(JSON.stringify(data));
              if (userType == 'Admin' || userType == 'Sub Admin') {
                renderTable(response);
              }
              else {
                renderchargebackTable(response);
              }
            },
            error: function (status) {
            }
          });

        });
      </script>
      <script>
        function getData(id) {

          redirect(id);
        }

        function saveFile() {
          var urls = new URL(window.location.href);
          var domain = urls.origin;
          var file = $('#file').val();
          if (!(/\.(xlsx|xls|xlsm)$/i).test(file)) {
            alert('Please upload valid excel file .xlsx, .xlsm, .xls only.');
            $(file).val('');
          }
          else {

            var form = $('#form')[0];
            var data = new FormData(form);

            $.ajax({
              url: domain + "/crmws/uploadsheet",
              //url: "http://localhost:8080/crmws/uploadsheet",
              type: 'POST',
              enctype: 'multipart/form-data',
              data: data,
              processData: false,
              contentType: false,
              cache: false,
              success: function (data) {
                alert(data.respmessage);
                window.location.reload();
              },
              error: function (data, textStatus, jqXHR) {

                if (data.responseText && JSON.parse(data.responseText).respmessage) {
                  var responseText = JSON.parse(data.responseText)
                  alert(responseText.respmessage);
                } else {
                  alert("Error while uploading file");
                }
                window.location.reload();

              }
            });
          }
        }


        function myFunction() {
			var x = document.getElementById("actions11").value;
			if (x == 'csv') {
				document.querySelector('.buttons-csv').click();
			}
			if (x == 'copy') {
				document.querySelector('.buttons-copy').click();
			}
			/* if (x == 'pdf') {
				document.querySelector('.buttons-pdf').click();
			} */

		}

        $('a.toggle-vis').on('click', function(e) {
			debugger
			e.preventDefault();
			table = $('#datatable').DataTable();
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
      <script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
    </body>

    </html>