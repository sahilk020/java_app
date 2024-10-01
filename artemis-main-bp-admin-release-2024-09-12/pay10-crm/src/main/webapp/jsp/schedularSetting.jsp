<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="s" uri="/struts-tags" %>
    <html dir="ltr" lang="en-US">

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <%-- <meta name="viewport" content="width=device-width, initial-scale=1">
            <link rel='stylesheet' href='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css'>--%>

            <title>Scheduler Setting</title>
            
            
            <!-- <script type="text/javascript" src="../js/jquery.min.js"></script>
    <script src="../js/core/popper.min.js"></script>
    <script src="../js/core/bootstrap-material-design.min.js"></script>
    <script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script> -->
            <%-- <script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.3/js/select2.js">
                </script>--%>
                <%-- <link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.3/css/select2.css"
                    rel="stylesheet" />--%>
                <!-- <script src='https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.3/js/select2.min.js'></script> -->

                <!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
                <!-- <script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
    <link href="../css/Jquerydatatableview.css" rel="stylesheet"/>
    <link href="../css/Jquerydatatable.css" rel="stylesheet"/>
    <link href="../css/default.css" rel="stylesheet" type="text/css"/>
    <link href="../css/jquery-ui.css" rel="stylesheet"/>
    <script src="../js/jquery.dataTables.js"></script>
    <script type="text/javascript" src="../js/dataTables.buttons.js"></script>
    <script type="text/javascript" src="../js/pdfmake.js"></script>
    <script src="../js/jquery.popupoverlay.js"></script>
    <link href="../fonts/css/font-awesome.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="../css/popup.css"/> -->

                <%-- <link rel='stylesheet'
                    href='https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.3/css/select2.min.css'>--%>
                    <%-- <link rel='stylesheet'
                        href='https://cdnjs.cloudflare.com/ajax/libs/select2-bootstrap-css/1.4.6/select2-bootstrap.min.css'>
                        <link rel="stylesheet" href="./style.css">&ndash;%&gt;--%>
                        
                        
                        <link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
                        <!--begin::Fonts-->
                        <link rel="stylesheet"
                            href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
                        <!--end::Fonts-->
                        <!--begin::Vendor Stylesheets(used by this page)-->
                        <link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
                            type="text/css" />
                        <link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet"
                            type="text/css" />
                            
                        <!--end::Vendor Stylesheets-->
                        <!--begin::Global Stylesheets Bundle(used by all pages)-->
                        <link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
                        <link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
                        <link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />

                        <script src="../assets/plugins/global/plugins.bundle.js"></script>
                        <script src="../assets/js/scripts.bundle.js"></script>
                        <link href="../css/select2.min.css" rel="stylesheet" />
                        <script src="../js/jquery.select2.js" type="text/javascript"></script>
                       <%--  <link href="../css/jquery-ui.css" rel="stylesheet"/>
                         <script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script> --%>
                         
                         <script src="../js/jquery.popupoverlay.js"></script>
                          <link rel="stylesheet" type="text/css" href="../css/popup.css"/>
    </head>

    </head>

    <body>
        <div class="content d-flex flex-column flex-column-fluid" id="kt_content">
            <div class="toolbar" id="kt_toolbar">
                <!--begin::Container-->
                <div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
                    <!--begin::Page title-->
                    <div data-kt-swapper="true" data-kt-swapper-mode="prepend"
                        data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
                        class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
                        <!--begin::Title-->
                        <h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
                            Scheduler Setting</h1>
                        <!--end::Title-->
                        <!--begin::Separator-->
                        <span class="h-20px border-gray-200 border-start mx-4"></span>
                        <!--end::Separator-->
                        <!--begin::Breadcrumb-->
                        <ul class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
                            <!--begin::Item-->
                            <li class="breadcrumb-item text-muted"><a href="home"
                                    class="text-muted text-hover-primary">Dashboard</a>
                            </li>
                            <!--end::Item-->
                            <!--begin::Item-->
                            <li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span>
                            </li>
                            <!--end::Item-->
                            <!--begin::Item-->
                            <li class="breadcrumb-item text-muted">Notification Engine
                            </li>
                            <!--end::Item-->
                            <!--begin::Item-->
                            <li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span>
                            </li>
                            <!--end::Item-->
                            <!--begin::Item-->
                            <li class="breadcrumb-item text-dark"> Scheduler Setting
                            </li>
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
                                        <div class="col-auto my-2 merchant-text">
                                            <p class="text-center m-0 w-100">
                                                <b>Acquirer</b>
                                            </p class="text-sm-center m-0">
                                        </div>
                                        <div class="col-lg-2 col-sm-12 col-md-6">
                                            <select name="acquirersList" id="acquirersList"  
                                                class="form-select form-select-solid merchantPayIdselect" data-hide-search="true"
                                                onchange="handleChange();">												
                                                <option value="ALL">ALL</option>
                                                <option value="APBL">APBL</option>
                                                <option value="ATOM">ATOM</option>
                                                <option value="AXISBANK">AXIS BANK</option>
                                                <option value="BOB">BANK OF BARODA</option>
                                                <option value="DIRECPAY">DIRECPAY</option>
                                                <option value="HDFC">HDFC BANK</option>
                                                <option value="ISGPAY">ISGPAY</option>
                                                <option value="KOTAK">KOTAK</option>
                                                <option value="MOBIKWIK">MOBIKWIK</option>
                                                <option value="PAYU">PAYU</option>
                                                <option value="PHONEPE">PHONEPE</option>
                                                <option value="YESBANKCB">YESBANK</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
              



                        <!-- <h2 class="pageHeading">Acceptable Success Rate Settings</h2>
<div class="row businessType">
    <div class="form-group col-md-3 txtnew col-sm-4 col-xs-6">
        <label for="merchant">Acquirer:</label> <br/>

        <select name="acquirersList" id="acquirersList" class="input-control" onchange="handleChange();">

            <option value="ALL">ALL</option>
            <option value="APBL">APBL</option>
            <option value="ATOM">ATOM</option>
            <option value="AXISBANK">AXIS BANK</option>
            <option value="BOB">BANK OF BARODA</option>
            <option value="DIRECPAY">DIRECPAY</option>
            <option value="HDFC">HDFC BANK</option>
            <option value="ISGPAY">ISGPAY</option>
            <option value="KOTAK">KOTAK</option>
            <option value="MOBIKWIK">MOBIKWIK</option>
            <option value="PAYU">PAYU</option>
            <option value="PHONEPE">PHONEPE</option>
            <option value="YESBANKCB">YESBANK</option>
        </select>

    </div>
</div> -->

                        <div class="row my-5">
                            <div class="col">
                                <div class="card">
                                    <div class="card-body">
                                        <div class="row my-3 align-items-center">
                                            <div style="overflow:scroll !important;">
                                                <table width="100%" align="left" cellpadding="0" cellspacing="0"
                                                    class="formbox">
                                                    <tr>
                                                        <td align="left" style="padding:10px;"><br /><br />
                                                            <div class="scrollD">
                                                                <table id="datatable" class="table table-striped table-row-bordered gy-5 gs-7" cellspacing="0"
                                                                    width="100%">
                                                                    <thead>
                                                                        <tr class="fw-bold fs-6 text-gray-800">
                                                                            <th>Merchant</th>
                                                                            <th>Acquirer</th>
                                                                            <th>Payment Type</th>
                                                                            <th>Mop Type</th>
                                                                            <th>Configured Email</th>
                                                                            <th>Success Rate</th>
                                                                            <th>Edit</th>
                                                                            <th>Delete</th>
                                                                        </tr>
                                                                    </thead>
                                                                </table>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </table>

                                            </div>
                                        </div>
                                  
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        </div>

        <div id="popup" style="display:none;">
            <div class="modal-dialog" style="width:400px;">
                <!-- Modal content-->
                <div class="modal-content" >
                    <div class="modal-body"
                        	>
                        <h3 style="text-align:center;" class="pageHeading">New Acceptable Rate Setting</h3>
                        <form method="POST" id="newSettingForm" action="schedularChangeSettingAction">
                            <div id="setting-container">
                                <div>
                                    <label class="d-flex align-items-center fs-6 fw-bold mb-2">Merchant Name</label>

                                    <s:select name="merchant" class="form-select form-select-solid selectMerchant" id="merchantPayId" headerKey="ALL"
                                        headerValue="ALL" list="merchantList" listKey="payId" listValue="businessName"
                                        onchange="handleChange();" autocomplete="off" />
                                </div>

                                <div>
                                    <label class="d-flex align-items-center fs-6 fw-bold mb-2"> Acquirer Name </label>
                                </div>

                                <select name="acquirerName" id="acquirerName" class="form-select form-select-solid">

                                    <option value="ALL">ALL</option>
                                    <option value="APBL">APBL</option>
                                    <option value="ATOM">ATOM</option>
                                    <option value="AXISBANK">AXIS BANK</option>
                                    <option value="BOB">BANK OF BARODA</option>
                                    <option value="DIRECPAY">DIRECPAY</option>
                                    <option value="HDFC">HDFC BANK</option>
                                    <option value="ISGPAY">ISGPAY</option>
                                    <option value="KOTAK">KOTAK</option>
                                    <option value="MOBIKWIK">MOBIKWIK</option>
                                    <option value="PAYU">PAYU</option>
                                    <option value="PHONEPE">PHONEPE</option>
                                    <option value="YESBANKCB">YESBANK</option>
                                </select>



                                <div>
                                    <label class="d-flex align-items-center fs-6 fw-bold mb-2">Payment Type</label>
                                </div>
                                <select name="paymentType" id="paymentType" class="form-select form-select-solid">
                                    <option value="ALL">ALL</option>
                                </select>


                                <div>
                                    <label class="d-flex align-items-center fs-6 fw-bold mb-2">Mop Type</label>
                                </div>
                                <div>
                                    <select name="mopType" id="mopType" class="form-select form-select-solid">
                                        <option value="ALL">ALL</option>
                                    </select>
                                </div>

                                <%-- <div class="txtnew">--%>
                                    <%-- <div class="selectBox" id="selectBox2" onclick="showCheckboxes(event,2)"
                                        title="ALL">--%>
                                        <%-- <select class="input-control">--%>
                                            <%-- <option>ALL</option>--%>
                                                <%-- </select>--%>
                                                    <%-- <div class="overSelect">
                            </div>--%>
                            <%-- </div>--%>
                                <%-- <div id="checkboxes2" onclick="getCheckBoxValue(2)">--%>

                                    <%-- <s:checkboxlist name="mopType" id="mopType" headerValue="ALL" --%>
                                        <%-- headerKey="ALL" list="@com.pay10.commons.util.MopTypeUI@values()" --%>
                                            <%-- listValue="name" listKey="uiName" class="myCheckBox2"
                                                value="mopType" />--%>

                                            <%-- </div>--%>
                                                <%-- </div>--%>



                                                    <div>
                                                        <label>Success Rate</label>
                                                    </div>
                                                    <input style="width:318.4px;" class="successRateInputField"
                                                        name="successRate" type="number" min="1" max="100" required>
                                                    <br />
                                                    <br />
                                                    <div>
                                                        <label>Configured Email</label>
                                                    </div>
                                                    <input style="width:318.4px;" class="emailField" name="email[]"
                                                        pattern="[A-Za-z0-9._%+-]+@[A-Za-z0-9_\-\+]+\.[A-Za-z]{2,4}$"
                                                        required>
                                                    <a href="javascript:;" id="add-new-email" class="add-new-email"
                                                        style="font-weight: bold;">+</a>
                                                    <br />
                                                    <br />
                    </div>


                    <input type="submit" value="Save" id="form-submit-button" class="btn btn-primary  mt-4 submit_btn"
                        style="margin-left: 55px;text-align: center;display: inline-block;margin-right: 5px;">

                    <input type="button" value="Cancel" class="btn btn-danger mt-4 submit_btn" onclick=closePopUp();
                        style="text-align: center; display: inline-block;margin-right: 5px;">

                    </form>

                </div>
            </div>
        </div>
        </div>
        </div>
        
        <script type="text/javascript">
    	$(document).ready(function () {
    		 $(".selectMerchant").select2(); 
    			});
    	 $("#acquirerName").select2(); 
		});

        </script>
        <script type="text/javascript">
            const successRateCol = [];
            const emailAddressCol = [];
            $(document).ready(function () {
                $(function () {
                   debugger
                    renderTable();
                    enableBaseOnAccess();
                });
            });

            function handleChange() {
                reloadTable();
            }

            function decodeVal(text) {
                return $('<div/>').html(text).text();
            }

            function renderTable() {
                var token = document.getElementsByName("token")[0].value;
                var buttonCommon = {
                    exportOptions: {
                        format: {
                            body: function (data, column, row, node) {
                                // Strip $ from salary column to make it numeric
                                if (column == 6) {

                                }
                                return column === 0 ? "'" + data : column === 2 ? data.replace("&#x40;", "@") : data;
                            }
                        }
                    }
                };


                // Activate an inline edit on click of a table cell
                $('#datatable').on('click', 'tbody td.editable', function (e) {
                    // editor.inline( this );
                });


                $('#datatable').dataTable({
                    dom: 'BTftlpi',

                    'columnDefs': [{
                        'searchable': true,
                        'targets': 4,
                        // 'targets': '_all',
                        'createdCell': createdEmailAddressCell
                    },
                    {
                        'searchable': true,
                        'targets': 5,
                        // 'targets': '_all',
                        'createdCell': createdSuccessRateCell
                    }
                    ],

                    buttons: [
                        {
                            text: 'Add',
                            className: 'disabled', 
                            action: addNewRow
                        }
                    ],
                    "ajax": {
                        "url": "schedularSettingAction",
                        "type": "POST",
                        "data": generatePostData
                    },
                    "bProcessing": true,
                    "bLengthChange": true,
                    "bAutoWidth": false,
                    "iDisplayLength": 10,
                    "order": [],
                    "aoColumns": [
                        {
                            "mData": "merchant",
                            "bSortable": false
                        },
                        {
                            "mData": "acquirerName",
                            "bSortable": false
                        }, {
                            "mData": "paymentType",
                            "bSortable": false
                        }, {
                            "mData": "mopType",
                            "bSortable": false
                        },

                        {
                            "mData": "email",
                            "bSortable": false,
                            "sClass": "emailTextClass"
                        },
                        {
                            "mData": "successRate",
                            "bSortable": false,
                            "sClass": "text-successRate-cell"
                        }, {
                            "mData": null,
                            "sClass": "center",
                            "bSortable": false,
                            // "bSortable" : false,
                            "mRender": function () {
                                return '<input type="button" class="btn btn-primary btn-xs editButtonClass " disabled="disabled" id="editScheduler" name="editScheduler" value="Edit"></input>';
                            }
                        },
                        {
                            "mData": null,
                            "sClass": "center",
                            "bSortable": false,
                            // "bSortable" : false,
                            "mRender": function () {
                                return '<input type="button" class="btn btn-danger btn-xs " disabled="disabled" id="deleteScheduler" name="deleteScheduler" value="Delete"></input>';
                            }
                        },
                        {
                            "data": null,
                            "visible": false,
                            "bSortable": false,
                            "className": "displayNone",
                            "mRender": function (row) {
                                return "\u0027" + row.payId;
                            }
                        },
                        {
                            "mData": "payId",
                            "bSortable": false,
                            "visible": false
                        },]
                });
                $(document).ready(function () {

                    var table = $('#datatable').DataTable();
                    $('#datatable tbody').on('click', 'td', function () {
                        var rows = table.rows();
                        var columnVisible = table.cell(this).index().columnVisible;
                        var rowIndex = table.cell(this).index().row;
                        if (columnVisible === 6) {
                            var pageLength = table.page.len();
                            var elem = document.getElementsByClassName("editButtonClass")[rowIndex % pageLength];
                            if (elem.value === "Edit") {

                                if ($("#editScheduler").attr("disabled") == undefined) {
                                    // successRateCol[rowIndex].setAttribute('style',"background-color:grey;");
                                    successRateCol[rowIndex].setAttribute('style', "font-weight:bold;background-color:white;");
                                    emailAddressCol[rowIndex].setAttribute('style', "font-weight:bold;background-color:white;");
                                    successRateCol[rowIndex].setAttribute('contenteditable', true);
                                    emailAddressCol[rowIndex].setAttribute('contenteditable', true);
                                    elem.value = "Save";
                                }
                            } else {
                                var rowData = table.row(rowIndex).data();

                                emailAddressCol[rowIndex].innerText = emailAddressCol[rowIndex].innerText.replace(/\s+/g, '');
                                var email = emailAddressCol[rowIndex].innerText;

                                var successRate = successRateCol[rowIndex].innerText.replace(/^0+/, '');
                                successRate = successRate.replace(/\s+/g, '');
                                successRateCol[rowIndex].innerText = successRate;
                                if (!validateEmail(email)) {
                                    emailAddressCol[rowIndex].error = "Email address is incorrect";
                                    alert("Email address should be valid and multiple emails should be comma separated.");
                                    return
                                }
                                if (!validateSuccessRate(successRate)) {
                                    successRateCol[rowIndex].error = "Invalid success rate";
                                    alert("Success Rate should be Integer between 1 to 100");
                                    return
                                }
                                rowData['email'] = email;
                                rowData['successRate'] = successRate;
                                $.ajax({
                                    url: "schedularChangeSettingAction",
                                    type: "POST",
                                    data: generateSettingPostData(rowData, "UPDATE")
                                });
                                successRateCol[rowIndex].removeAttribute('style');
                                emailAddressCol[rowIndex].removeAttribute('style');
                                successRateCol[rowIndex].setAttribute('contenteditable', false);
                                emailAddressCol[rowIndex].setAttribute('contenteditable', false);
                                elem.value = "Edit";


                            }
                        }
                        else if (columnVisible === 7) {
                            if ($("#deleteScheduler").attr("disabled") == undefined) {
                                var result = confirm("Are you sure you Want to delete?");
                                if (result) {
                                    //Logic to delete the item
                                    var rowData = table.row(rowIndex).data();
                                    $.ajax({
                                        url: "schedularChangeSettingAction",
                                        type: "POST",
                                        data: generateSettingPostData(rowData, "DELETE")
                                    });
                                    window.location.reload();
                                }
                            }
                        }

                    });

                });
            }

            function reloadTable() {
                var tableObj = $('#datatable');
                var table = tableObj.DataTable();
                table.ajax.reload();
            }

            function generatePostData() {
                var testString = null;
                var token = document.getElementsByName("token")[0].value;
                var acquirer = null;
                if (null != document.getElementById("acquirersList")) {
                    acquirer = document.getElementById("acquirersList").value;
                } else {
                    acquirer = 'ALL';
                }
                var obj = {
                    token: token,
                    acquirer: acquirer,
                    settingsChanged: false
                };
                return obj;
            }


            function generateSettingPostData(rowData, action) {

                var token = document.getElementsByName("token")[0].value;
                var obj = {
                    token: token,
                    // settingsChanged : true,
                    acquirerName: rowData['acquirerName'],
                    paymentType: rowData['paymentType'],
                    mopType: rowData['mopType'],
                    merchant: rowData['merchant'],
                    successRate: rowData['successRate'],
                    email: rowData['email'],
                    payId: rowData['payId'],
                    action: action
                };
                return obj;
            }

            function generateNewSettingPostData(form_data, action) {

                var token = document.getElementsByName("token")[0].value;
                var obj = {
                    token: token,
                    action: action,
                    email: ""
                };

                $.each(form_data, function (i, field) {
                    if (field.name === "email[]") {
                        if (obj['email'] === "") obj['email'] = field.value;
                        else obj['email'] = obj['email'] + "," + field.value;
                    }
                    else obj[field.name] = field.value;
                    console.log(field.name + field.value);
                });

                return obj;
            }




            createdSuccessRateCell = function (cell, cellData, rowData, rowIndex, colIndex) {
                successRateCol[rowIndex] = cell;
                createdCell(cell);
            };

            createdEmailAddressCell = function (cell, cellData, rowData, rowIndex, colIndex) {
                emailAddressCol[rowIndex] = cell;
                createdCell(cell);
            };


            createdCell = function (cell) {
                cell.setAttribute('spellcheck', false);
                cell.addEventListener("focus", function (e) {
                    original = e.target.textContent
                });
                cell.addEventListener("blur", function (e) {
                    if (original !== e.target.textContent) {
                        var row = table.row(e.target.parentElement);
                        row.invalidate();
                        // api call
                    }
                })
            };

            function validateEmail(email) {

                var emails = email.split(",");
                // const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

                const re = /^([A-Za-z0-9_\-\.\+])+\@([A-Za-z0-9_\-\+])+\.([A-Za-z]{2,4})$/;
                for (i = 0; i < emails.length; i++) {
                    if (!re.test(String(emails[i]).toLowerCase())) return false;
                }

                return true;
            }


            function validateSuccessRate(successRate) {
                if (Math.sign(successRate) === -1 || successRate % 1 !== 0 || successRate > 100 || successRate < 1 || successRate.includes('.')) {
                    return false;
                }

                return true;
            }

        </script>

        <script type="text/javascript">

            var i = 0;
            document.getElementById('add-new-email').onclick = function () {

                if (i > 3) {
                    alert("Limit exceeded");
                }
                else {
                    var template = `<p>  <input style="width:318.4px;" name="email[]" pattern="[A-Za-z0-9._%+-]+@[A-Za-z0-9_\\-\\+]+\\.[A-Za-z]{2,4}$" required class="remove-new-email-input"> <a href="javascript:;" id="remove-new-email-"+i class="remove-new-email" style="font-weight: bold;" onclick=removeThisEmail(this)>-</a></p>`;
                    var container = document.getElementById('setting-container');
                    var div = document.createElement('div');
                    div.id = "email-" + i;
                    div.innerHTML = template;
                    container.appendChild(div);
                    i++;
                }
            };

            function removeThisEmail(element) {
                var div = document.getElementById(element.id).parentElement.parentElement;
                var container = document.getElementById('setting-container');
                container.removeChild(div);
                i--;
            }









            var Select2Cascade = (function (window, $) {

                function Select2Cascade(parent, child, select2Options) {
                    // getDropDownData();
                    var afterActions = [];
                    var options = select2Options || {};

                    // Register functions to be called after cascading data loading done
                    this.then = function (callback) {
                        afterActions.push(callback);
                        return this;
                    };

                    parent.select2(select2Options).on("change", function (e) {

                        child.prop("disabled", true);

                        var _this = this;

                        var token = document.getElementsByName("token")[0].value;
                        var obj = {
                            token: token,
                            // settingsChanged : true,
                            acquirerName: $(this).val(),
                            dropDown: "PaymentTypeDropDown"
                        };

                        var items;
                        $.ajax({
                            url: "paymentMethodsListDropDownAction",
                            type: "POST",
                            data: obj,
                            success: function (data) {
                                items = data.split(",");
                                var newOptions = '<option value="ALL">ALL</option>';
                                if (data === "None") {
                                    newOptions = '<option value="ALL">None</option>';
                                }
                                for (var i = 0; i < items.length - 1; i = i + 2) {
                                    newOptions += '<option value="' + items[i] + '">' + items[i + 1] + '</option>';
                                }

                                child.select2('destroy').html(newOptions).prop("disabled", false)
                                    .select2(options);

                                $('#mopType').select2('destroy').html('<option value="ALL">ALL</option>').prop("disabled", false)
                                    .select2(options);

                                afterActions.forEach(function (callback) {
                                    callback(parent, child, items);
                                });

                            },
                            error: function (data) {
                                alert("Network error, charging detail may not be saved");
                            }
                        });



                    });
                }

                return Select2Cascade;

            })(window, $);


            var Select2Cascade2 = (function (window, $) {

                function Select2Cascade2(parent, child, select2Options) {
                    // getDropDownData();
                    var afterActions = [];
                    var options = select2Options || {};

                    // Register functions to be called after cascading data loading done
                    this.then = function (callback) {
                        afterActions.push(callback);
                        return this;
                    };

                    parent.select2(select2Options).on("change", function (e) {

                        child.prop("disabled", true);

                        var _this = this;


                        var e = document.getElementById("acquirerName");
                        var selectedAcqName = e.options[e.selectedIndex].value;

                        var e2 = document.getElementById("paymentType");
                        var selectPaymentType = e2.options[e2.selectedIndex].value;


                        console.log(selectPaymentType);
                        var token = document.getElementsByName("token")[0].value;
                        var obj = {
                            token: token,
                            // settingsChanged : true,
                            acquirerName: selectedAcqName,
                            paymentType: selectPaymentType,
                            dropDown: "MopTypeDropDown"
                        };

                        var items;
                        $.ajax({
                            url: "mopTypeListDropDownAction",
                            type: "POST",
                            data: obj,
                            success: function (data) {
                                items = data.split(",");
                                var newOptions = '<option value="ALL">ALL</option>';
                                if (data === "None") {
                                    newOptions = '<option value="ALL">None</option>';
                                }
                                if (items.length > 6) {
                                    newOptions += '<option value="Others">Others</option>';
                                }
                                for (var i = 0; i < items.length - 1; i = i + 2) {
                                    newOptions += '<option value="' + items[i] + '">' + items[i + 1] + '</option>';
                                }

                                child.select2('destroy').html(newOptions).prop("disabled", false)
                                    .select2(options);

                                afterActions.forEach(function (callback) {
                                    callback(parent, child, items);
                                });
                            },
                            error: function (data) {
                                alert("Network error, charging detail may not be saved");
                            }
                        });



                    });
                }

                return Select2Cascade2;

            })(window, $);

            addNewRow = function () {

                // getDropDownData();

                /* var select2Options = { width: 'resolve' };
        
                $('select').select2(select2Options);
                var cascadLoading = new Select2Cascade($('#acquirerName'), $('#paymentType'), select2Options);
                cascadLoading.then( function(parent, child, items) {
                    // Dump response data
                    console.log(items);
                });
        
                var cascadLoading2 = new Select2Cascade2($('#paymentType'), $('#mopType'), select2Options);
                cascadLoading2.then( function(parent, child, items) {
                    // Dump response data
                    console.log(items);
                }); */

                $('#popup').popup('show');


            };
            closePopUp = function () {
                document.getElementById("newSettingForm").reset();
                var container = document.getElementById('setting-container');
                $(".remove-new-email").remove();
                $(".remove-new-email-input").remove();
                i = 0;
                var popupResultElement = document.getElementById("popup-result");
                if (popupResultElement) {
                    container.removeChild(popupResultElement);
                }
                var e = jQuery.Event("keydown"); // or keypress/keydown
                e.keyCode = 27; // for Esc
                $(document).trigger(e)
            };

        </script>
        <script type="text/javascript">
            $("#newSettingForm").submit(function (event) {

                $(this).find(':input[type=submit]').prop('disabled', true);

                var popupResultElement = document.getElementById("popup-result");
                if (popupResultElement) {
                    var container = document.getElementById('setting-container');
                    container.removeChild(popupResultElement);
                }

                event.preventDefault(); //prevent default action
                var post_url = $(this).attr("action"); //get form action url
                var request_method = $(this).attr("method"); //get form GET/POST method

                var form_data = $("#newSettingForm").serializeArray();


                $.ajax({
                    url: post_url,
                    type: request_method,
                    data: generateNewSettingPostData(form_data, "NEW")
                })
                    .done(function (data) {
                        if (typeof data === "string") {
                            var template = `<p style="text-align: center; color: red";font-weight: bold;>Setting Already Exists</p>`;
                            var container = document.getElementById('setting-container');
                            var div = document.createElement('div');
                            div.setAttribute("id", "popup-result");
                            div.innerHTML = template;
                            container.appendChild(div);
                            return;
                        } else {
                            var template = `<p style="text-align: center; color: green";font-weight: bold;>Successfully Added</p>`;
                            var container = document.getElementById('setting-container');
                            var div = document.createElement('div');
                            div.innerHTML = template;
                            container.appendChild(div);
                            window.location.reload();
                            return;
                        }
                    })
            });

            $(document).ready(function () {
                $('#merchantPayId').change(function () {
                    $("#form-submit-button").prop('disabled', false);
                });
                $('#acquirerName').change(function () {
                    $("#form-submit-button").prop('disabled', false);
                });
                $('#mopType').change(function () {
                    $("#form-submit-button").prop('disabled', false);
                });
                $('#paymentType').change(function () {
                    $("#form-submit-button").prop('disabled', false);
                });
            });


            $(".successRateInputField").on('keyup keypress blur change', function (evt) {


                $(this).val($(this).val().replace(/\s+/g, ''));

                var keycode = evt.charCode || evt.keyCode;
                if (keycode === 46) {
                    return false;
                }

                if ($(this).val() > 100) {
                    $(this).val('100');
                    return false;
                }

                if ($(this).val().startsWith("0")) {
                    $(this).val('');
                    return false;
                }

            });

            $(".emailField").on('keyup keypress blur change', function (evt) {

                $(this).val($(this).val().replace(/\s+/g, ''));

            });


            $($(this)).on('paste', function (event) {
                var pastedText = event.originalEvent.clipboardData.getData('Text');

                if (pastedText.indexOf('data:image') !== -1 || pastedText.indexOf('src="data:image"') !== -1 || pastedText.indexOf('base64') !== -1) {
                    e.preventDefault();
                }
            });

            $('#datatable').on('draw.dt', function () {
                enableBaseOnAccess();
            });

            function enableBaseOnAccess() {
                setTimeout(function () {
                    debugger
                    if ($('#schedularSettings').hasClass("active")) {
                        
                        var menuAccess = document.getElementById("menuAccessByROLE").value;
                        var accessMap = JSON.parse(menuAccess);
                        var access = accessMap["schedularSettings"];
                        if (access.includes("Update")) {
                            var edits = document.getElementsByName("editScheduler");
                            for (var i = 0; i < edits.length; i++) {
                                var edit = edits[i];
                                edit.disabled = false;
                            }
                        }
                        if (access.includes("Delete")) {
                            var deletes = document.getElementsByName("deleteScheduler");
                            for (var i = 0; i < deletes.length; i++) {
                                var deleteBtn = deletes[i];
                                deleteBtn.disabled = false;
                            }
                        }
                        if (access.includes("Add")) {
                           
                           // var add = document.getElementsByClassName("dt-buttons")[0];
                            var addbtn = document.getElementsByClassName("btn-secondary disabled")[0];
                           /*  alert("add="+addbtn); */
                            addbtn.classList.remove('disabled');
                        }
                    }
                }, 500);
            }
        </script>

        <style type="text/css">
        .modal-body {
        padding:8px;
        }
            table.dataTable tbody td {
                word-break: break-word;
                vertical-align: top;
            }


            table.dataTable thead .sorting,
            table.dataTable thead .sorting_asc,
            table.dataTable thead .sorting_desc {
                background: none;
            }
        </style>
        <style type="text/css">
            .select2-selection__rendered {
                visibility: hidden;
            }
        </style>


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