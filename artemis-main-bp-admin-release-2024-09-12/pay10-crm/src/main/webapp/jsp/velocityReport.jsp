<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s" %>
  <%@page import="java.util.Calendar" %>
    <%@page import="java.text.SimpleDateFormat" %>
      <%@page import="java.util.Date" %>
        <%@page import="java.util.Locale" %>
          <%@page import="java.text.NumberFormat" %>
            <%@page import="java.text.DecimalFormat" %>
              <%@page import="java.text.Format" %>


                <html dir="ltr" lang="en-US">

                <head>
                  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
                  <title>Velocity Report</title>

                  <link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
                    type="text/css" />
                  <link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet"
                    type="text/css" />
                  <link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
                  <link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
                  <link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />
                  <!-- <script src="../js/loader/main.js"></script> -->
                  <script src="../assets/plugins/global/plugins.bundle.js"></script>
                  <script src="../assets/js/scripts.bundle.js"></script>
                  <script src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
                  <script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
                  <script src="../assets/js/widgets.bundle.js"></script>
                  <script src="../assets/js/custom/widgets.js"></script>
                  <script src="../assets/js/custom/apps/chat/chat.js"></script>
                  <script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
                  <script src="../assets/js/custom/utilities/modals/create-app.js"></script>
                  <script src="../assets/js/custom/utilities/modals/users-search.js"></script>
                  <script src="../js/commanValidate.js"></script>

                  <style type="text/css">
                    [class^="Acquirer"] .wwlbl .label {
                      color: #666;
                      font-size: 12px;
                      padding: 2px 0 8px;
                      display: block;
                      margin: 0;
                      text-align: left;
                      font-weight: 600;
                    }

                    [class^="Acquirer"] .wwlbl+br {
                      display: none;
                    }

                    .card-list-toggle {
                      cursor: pointer;
                      padding: 8px 12px;
                      border: 1px solid #496cb6;
                      position: relative;
                      background: linear-gradient(60deg, #425185, #4a9b9b);
                    }

                    .card-list-toggle:before {
                      position: absolute;
                      right: 10px;
                      top: 7px;
                      content: "\f078";
                      font-family: 'FontAwesome';
                      font-size: 15px;
                    }

                    .dt-buttons {
                      margin-top: 35px !important;
                    }

                    div#example_filter {
                      margin-top: 35px !important;
                    }

                    .dt-button-collection a.dt-button.buttons-columnVisibility {
                      background: unset;
                      background-color: rgb(143, 162, 214) !important;
                    }

                    .dt-button-collection a.dt-button.buttons-columnVisibility.active {
                      background-color: #2d4c5c !important;
                    }

                    .card-list-toggle.active:before {
                      content: "\f077";
                    }

                    .card-list {
                      display: none;
                    }

                    .acquirerRemoveBtn {
                      float: left;
                    }

                    [class*="AcquirerList"]>div {
                      clear: both;
                      border-bottom: 1px solid #ccc;
                      padding: 0 8px 5px;
                      margin: 0 -8px 8px;
                    }

                    [class*="AcquirerList"]>div:last-child {
                      border-bottom: none;
                      padding-bottom: 0;
                      margin-bottom: 0;
                    }

                    [class^="AcquirerList"] input[disabled] {
                      color: #bbb;
                      display: none;
                    }

                    [class^="AcquirerList"] input[disabled]+label {
                      color: #bbb;
                      display: none;
                    }

                    [class*="OtherList"]>div {
                      clear: both;
                      border-bottom: 1px solid #ccc;
                      padding: 0 8px 5px;
                      margin: 0 -8px 8px;
                    }

                    [class*="OtherList"]>div:last-child {
                      border-bottom: none;
                      padding-bottom: 0;
                      margin-bottom: 0;
                    }

                    [class^="OtherList"] input[disabled] {
                      color: #bbb;
                      display: none;
                    }

                    [class^="OtherList"] input[disabled]+label {
                      color: #bbb;
                      display: none;
                    }

                    .sweet-alert .sa-icon {
                      margin-bottom: 30px;
                    }

                    .sweet-alert .lead.text-muted {
                      font-size: 14px;
                    }

                    .sweet-alert .btn {
                      font-size: 12px;
                      padding: 8px 30px;
                      margin: 0 5px;
                    }

                    table.product-spec.disabled {
                      cursor: not-allowed;
                      opacity: 0.5;
                    }

                    table.product-spec.disabled .btn {
                      pointer-events: none;
                    }

                    .merchantFilter {
                      padding: 15px 0;
                      width: 200px;
                    }

                    .AcquirerList input[type="radio"] {
                      vertical-align: top;
                      float: left;
                      margin: 2px 5px 0 0;
                    }

                    .AcquirerList label {
                      vertical-align: middle;
                      display: block;
                      font-weight: normal;
                    }

                    < !-- .Acquirer1 input[type="radio"] {
                      vertical-align: top;
                      float: left;
                      margin: 2px 5px 0 0;
                    }

                    .Acquirer1 label {
                      vertical-align: middle;
                      display: block;
                      font-weight: normal;
                    }

                    -->.boxtext td div input[type="radio"] {
                      vertical-align: top;
                      float: left;
                      margin: 2px 5px 0 0;
                    }

                    .boxtext td div label {
                      vertical-align: middle;
                      display: block;
                      font-weight: normal;
                    }

                    #onus_section .checkbox,
                    #offus_section .checkbox {
                      margin: 0;
                    }

                    .checkbox .wwgrp input[type="checkbox"] {
                      margin-left: 0;
                    }

                    .checkbox label .wwgrp input[type="checkbox"] {
                      margin-left: -20px;
                    }

                    .select2-container {
                      width: 200px !important;
                    }

                    .btn:focus {
                      outline: 0 !important;
                    }

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

                    @media (min-width : 768px) {

                      .navbar>.container .navbar-brand,
                      .navbar>.container-fluid .navbar-brand {
                        margin-left: 0px !important;
                      }
                    }

                    .dropdown-menu>li>a:focus,
                    .dropdown-menu>li>a:hover {
                      color: #ffffff !important;
                      text-decoration: none;
                      background-color: #496cb6 !important;
                    }

                    input#reportrange {
                      border: unset;
                    }
                  </style>
                  <% DecimalFormat d=new DecimalFormat("0.00"); Date d1=new Date(); SimpleDateFormat df=new
                    SimpleDateFormat("dd-MM-YYYY"); String currentDate=df.format(d1); %>


                    <script type="text/javascript">
                      $(function () {
                        var start = null;
                        var end = null;
                        debugger;
                        const dateRange = '<%=(String)request.getAttribute("dateRange")%>';
                        if (dateRange != 'null') {
                          //07/13/2022 - 07/15/2022
                          const a = dateRange.split(" ");
                          const startdate = a[0].split("/");
                          const enddate = a[2].split("/");
                          start = moment().set({ 'month': parseInt(startdate[0]) - 1, 'date': parseInt(startdate[1]), 'year': parseInt(startdate[2]) });
                          end = moment().set({ 'month': parseInt(enddate[0]) - 1, 'date': parseInt(enddate[1]), 'year': parseInt(enddate[2]) });

                        } else {
                          start = moment().subtract(29, 'days');
                          end = moment();
                        }


                        function cb(start, end) {
                          $('#reportrange span').html(
                            start.format('DD/MM/YYYY') + ' - '
                            + end.format('DD/MM/YYYY'));
                        }

                        $('#reportrange').daterangepicker(
                          {
                            startDate: start,
                            endDate: end,
                            maxDate: new Date(),
                            dateLimit: {
                              days: 30
                            },

                            ranges: {
                              'Today': [moment(), moment()],
                              'Yesterday': [moment().subtract(1, 'days'),
                              moment().subtract(1, 'days')],
                              'Last 7 Days': [moment().subtract(6, 'days'),
                              moment()],
                              'Last 30 Days': [moment().subtract(29, 'days'),
                              moment()],
                              'This Month': [moment().startOf('month'),
                              moment().endOf('month')],
                              'Last Month': [
                                moment().subtract(1, 'month').startOf('month'),
                                moment().subtract(1, 'month').endOf('month')]
                            }
                          }, cb);

                        cb(start, end);

                      });
                    </script>

                    <script type="text/javascript">

                      $(document).ready(function () {
                        $(function () {
                          renderTable();
                        });
                      });

                      function renderTable() {
                        $("#example").DataTable().destroy();
                        var token = document.getElementsByName("token")[0].value;
                        var buttonCommon = {
                          exportOptions: {
                            format: {
                              body: function (data, column, row, node) {
                                return data;
                              }
                            }
                          }
                        };
                        $('#example').DataTable({
                          dom: 'BTftlpi',
                          'columnDefs': [{
                            'searchable': false,
                            'targets': [15]
                          }],

                          buttons: [
                            $
                              .extend(
                                true,
                                {},
                                buttonCommon,
                                {
                                  extend: 'copyHtml5',
                                  exportOptions: {
                                    columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15]
                                  }
                                }),
                            $
                              .extend(
                                true,
                                {},
                                buttonCommon,
                                {
                                  extend: 'csvHtml5',
                                  exportOptions: {
                                    columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15]
                                  }
                                }),
                            {
                              extend: 'pdfHtml5',
                              title: 'Velocity Report',
                              orientation: 'landscape',
                              exportOptions: {
                                columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15]
                              },
                              customize: function (doc) {
                                doc.defaultStyle.alignment = 'center';
                                doc.styles.tableHeader.alignment = 'center';
                              }
                            },
                            // Disabled print button.
                            /* {extend : 'print',title : 'Merchant List',exportOptions : {columns: [':visible :not(:last-child)']}}, */
                            {
                              extend: 'colvis',
                              //collectionLayout: 'fixed two-column',
                              columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15]
                            }],
                          "ajax": {
                            "url": "velocityReportDetails",
                            "type": "POST",
                            "data": generatePostData
                          },
                          "bProcessing": true,
                          "bLengthChange": true,
                          "bAutoWidth": false,
                          "iDisplayLength": 10,
                          "order": [[0, "asc"]],
                          "aoColumns": [
                            {
                              "mData": "txnId"
                            },
                            {
                              "mData": "pgRefNo"
                            },
                            {
                              "mData": "merchantName"
                            },
                            {
                              "mData": "date"
                            },
                            {
                              "mData": "orderId"
                            },
                            {
                              "mData": "refundOrderId"
                            },
                            {
                              "mData": "mopType"
                            },
                            {
                              "mData": "paymentType"
                            },
                            {
                              "mData": "txnType"
                            },
                            {
                              "mData": "status"
                            },
                            {
                              "mData": "baseAmount"
                            },
                            {
                              "mData": "customerEmail"
                            },
                            {
                              "mData": "customerPhone"
                            },
                            {
                              "mData": "ipAddress"
                            },
                            {
                              "mData": "cardMask"
                            },
                            {
                              "mData": "ruleType"
                            }]
                        });
                      }

                      function generatePostData() {
                        var dateRange = $("#reportrange").val();
                        var merchant = $("#merchant").val();
                        var status = $("#status").val();
                        var ruleType = $("#ruleType").val();
                        if (dateRange.includes("-")) {
                          dateRange = dateRange.replace("-", "").trim();
                          dateRange = dateRange.replace(/  +/g, ' ').trim();
                        }
                        var obj = {
                          dateRange: dateRange,
                          merchant: merchant,
                          status: status,
                          ruleType: ruleType
                        }
                        return obj;
                      }
                    </script>
                    <style>
                      @media (min-width: 992px) {
                        .col-lg-3 {
                          max-width: 30% !important;
                        }
                      }
                    </style>

                    <script>
                      $(document).ready(function(){
                        $("#ruleType").select2();
                        $("#status").select2();
                        $("#merchant").select2();
                        
                      });
                    </script>
                </head>

                <body>
                  <!--begin::Toolbar-->
                  <div class="toolbar" id="kt_toolbar">
                    <!--begin::Container-->
                    <div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
                      <!--begin::Page title-->
                      <div data-kt-swapper="true" data-kt-swapper-mode="prepend"
                        data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
                        class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
                        <!--begin::Title-->
                        <h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Velocity Report</h1>
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
                          <li class="breadcrumb-item text-muted">Fraud Report</li>
                          <!--end::Item-->
                          <!--begin::Item-->
                          <li class="breadcrumb-item">
                            <span class="bullet bg-gray-200 w-5px h-2px"></span>
                          </li>
                          <!--end::Item-->
                          <!--begin::Item-->
                          <li class="breadcrumb-item text-dark"> Velocity Report</li>
                          <!--end::Item-->
                        </ul>
                        <!--end::Breadcrumb-->
                      </div>
                      <!--end::Page title-->

                    </div>
                    <!--end::Container-->
                  </div>
                  <!--end::Toolbar-->
                  <div style="overflow: auto !important;">
                    <div id="kt_content_container" class="container-xxl">
                      <div class="row my-5">
                        <div class="col">
                          <div class="card">
                            <div class="card-body">
                              <!--begin::Input group-->
                              <div class="row g-9 mb-8">
                                <!--begin::Col-->
                                <div class="col">

                                  <div class="card-body ">
                                    <div class="row">
                                      <div class="col-lg-3 col">
                                        <label class="d-flex align-items-center fs-6 fw-semibold mb-2">Select Date </label><br>
                                        <div
                                          style="border:1px solid #0c0c0c;background: #fff; cursor: pointer; font-size: 14px; position: relative;display: inline-flex;">
                                          <input type="text" id="reportrange" name="dateRange"
                                            class="form-select form-select-solid">&nbsp;
                                          <i class="fa fa-calendar mt-3"></i>&nbsp;
                                          <i class="fa fa-caret-down mt-1"></i>
                                          <span id="dateRange" style="font-size: 14px; color: black;margin-top: 5px;">
                                          </span>
                                        </div>
                                      </div>
                                      <div class=" col">
                                        <label class="d-flex align-items-center fs-6 fw-semibold mb-2">Merchant </label><br>

                                        <s:select name="merchant" id="merchant" value="merchant" headerKey="All"
                                          class="form-select form-select-solid"
                                          headerValue="ALL" list="merchantList" listKey="businessName"
                                          listValue="businessName" autocomplete="off" />

                                      </div>

                                      <div class="col">
                                        <label class="d-flex align-items-center fs-6 fw-semibold mb-2">Status </label><br>

                                        <s:select name="status" id="status" value="status" headerKey="All"
                                          class=" form-select form-select-solid"
                                          headerValue="ALL" list="statuss" autocomplete="off" />

                                      </div>
                                      <div class="col">
                                        <label class="d-flex align-items-center fs-6 fw-semibold mb-2">Rule Type </label><br>
                                        <s:select name="ruleType" id="ruleType" value="ruleType" headerKey="All"
                                          class="form-select form-select-solid"
                                          headerValue="ALL" list="ruleTypee" autocomplete="off" />

                                      </div>
                                      <div class="col mt-2">


                                        <button type="button" class="btn btn-primary submitbtn mt-11" id="SubmitButton"
                                          onclick="javascript: renderTable();">Submit</button>


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


                    <div id="kt_content_container" class="container-xxl">
                      <div class="row my-5">
                        <div class="col">
                          <div class="card">
                            <div class="card-body">
                              <!--begin::Input group-->
                              <div class="row g-9 mb-8">
                                <!--begin::Col-->
                                <div class="col">


                                  <div class="row g-9 mb-8 justify-content-end">
                                  <div class="col-lg-4 col-sm-12 col-md-6">
										<select name="currency" data-control="select2"
											data-placeholder="Actions" id="actions11"
											class="form-select form-select-solid actions"
											data-hide-search="true" onchange="myFunction();">
											<option value="">Actions</option>
											<option value="copy">Copy</option>
											<option value="csv">CSV</option>
											<option value="pdf">PDF</option>
										</select>
									</div>
                                    <div class="col-lg-4 col-sm-12 col-md-6">
                                      <div class="dropdown1">
                                            <button class="form-select form-select-solid actions dropbtn1">Customize Columns</button>
                                            <div class="dropdown-content1">
                                        <a class="toggle-vis" data-column="0">TxnId</a>
                                        <a class="toggle-vis" data-column="1">PgRefNo</a>
                                        <a class="toggle-vis" data-column="2">Merchant Name</a>
                                        <a class="toggle-vis" data-column="3">Date</a>
                                        <a class="toggle-vis" data-column="4">OrderId</a>
                                        <a class="toggle-vis" data-column="5">Refund OrderId</a>
										<a class="toggle-vis" data-column="6">Mop Type</a>
										<a class="toggle-vis" data-column="7">Payment Type</a>
										<a class="toggle-vis" data-column="8">Txn Type</a>
										<a class="toggle-vis" data-column="9">Status</a>
										<a class="toggle-vis" data-column="10">Base Amount</a>
										<a class="toggle-vis" data-column="11">Customer Email</a>
										<a class="toggle-vis" data-column="12">Customer Phone</a>
										<a class="toggle-vis" data-column="13">IP Address</a>
										<a class="toggle-vis" data-column="14">Card Mask</a>
										<a class="toggle-vis" data-column="15">Rule Type</a>
                                        </div>
                                        </div>
                                    </div>
                                  </div>
                                  <div class="row g-9 mb-8">
                                    <div class="table-responsive dataTables_wrapper dt-bootstrap4 no-footer">
                                  <table id="example" class="display table table-striped table-row-bordered gy-5 gs-7"
                                    style="width: 100%">
                                    <thead>
                                      <tr class="boxheadingsmall fw-bold fs-6 text-gray-800">
                                        <th>TxnId</th>
                                        <th>PgRefNo</th>
                                        <th>Merchant Name</th>
                                        <th>Date</th>
                                        <th>OrderId</th>
                                        <th>Refund OrderId</th>
                                        <th>Mop Type</th>
                                        <th>Payment Type</th>
                                        <th>Txn Type</th>
                                        <th>Status</th>
                                        <th>Base Amount</th>
                                        <th>Customer Email</th>
                                        <th>Customer Phone</th>
                                        <th>IP Address</th>
                                        <th>Card Mask</th>
                                        <th>Rule Type</th>
                                      </tr>
                                    </thead>
                                  </table>
                                    </div></div>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <style>
                    .dt-buttons {
                      display: none;
                    }
                  </style>
                  <script type="text/javascript">
                  
                  function myFunction() {
                		var x = document.getElementById("actions11").value;
                		if(x=='csv'){
                			document.querySelector('.buttons-csv').click();
                		}
                		if(x=='copy'){
                			document.querySelector('.buttons-copy').click();
                		}
                		if(x=='pdf'){
                			document.querySelector('.buttons-pdf').click();
                		}

                		// document.querySelector('.buttons-excel').click();
                		// document.querySelector('.buttons-print').click();


                	}
                  
$('a.toggle-vis').on('click', function (e) {
    debugger
    e.preventDefault();
    table = $('#example').DataTable();
    // Get the column API object
    var column1 = table.column($(this).attr('data-column'));
    // Toggle the visibility
    column1.visible(!column1.visible());
    if($(this)[0].classList[1]=='activecustom'){
        $(this).removeClass('activecustom');
    }
    else{
        $(this).addClass('activecustom');
    }
});

</script>
                </body>

                </html>