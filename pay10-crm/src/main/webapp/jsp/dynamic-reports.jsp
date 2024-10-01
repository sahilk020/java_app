<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html dir="ltr" lang="en-US">
    <head>
    <title>Dynamic Report</title>
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
    <script type="text/javascript" src="../js/bootstrap-clockpicker.js"></script>
    <link rel="stylesheet" href="../css/bootstrap-clockpicker.css">
    <style>
        span.clockpicker-span-hours.text-primary{
            color:#999999 !important;
        }
        span.clockpicker-span-minutes.text-primary {
            color: #999999 !important;
        }
        button.btn.btn-sm.btn-default.clockpicker-button.am-button{
            padding: 0.40625rem 0.25rem !important;
            line-height:0.5;
        }
        button.btn.btn-sm.btn-default.clockpicker-button.pm-button{
            padding: 0.40625rem 0.25rem !important;
            line-height:0.5;
        }
        .clockpicker-popover .popover-title{
            font-size:21px !important;
            line-height:23px !important;
        }
        .dataTables_wrapper {
        	position: relative;
        	clear: both;
        	*zoom: 1;
        	zoom: 1;
        	margin-top: -30px;
        }
        table.dataTable thead th {
        	padding: 4px 15px !important;
        }
        .dataTables_length select option:last-child {
        	display: none !important;
        }
		.dataTables_length {
			margin-left: 10px;
		}
		.dataTables_info {
			margin-left: 10px;
		}
		div.dt-buttons {
			margin-left: 10px;
			margin-bottom: 10px;
		}
		thead {
			font-weight: 600 !important;
			font-size: 0.95rem !important;
			color: var(--kt-text-gray-800) !important;
		}
		.table.gs-7 th:first-child, .table.gs-7 td:first-child {
			padding-left: 1.75rem !important;
		}
        /* .dataTables_length select option:last-child {
            display: none !important;
        } */
        .dt-buttons.btn-group.flex-wrap {
            display: none !important;
        }
    </style>
    </head>
    <body id="mainBody">
        <div class="content flex-column" id="kt_content">
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
                        <h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3" id="reportTitleFr"></h1>
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
                            <li class="breadcrumb-item text-muted">Dynamic Reports</li>
                            <!--end::Item-->
                            <!--begin::Item-->
                            <li class="breadcrumb-item"><span
                                class="bullet bg-gray-200 w-5px h-2px"></span></li>
                            <!--end::Item-->
                            <!--begin::Item-->
                            <li class="breadcrumb-item text-dark" id="reportTitle"></li>
                            <!--end::Item-->
                        </ul>
                        <!--end::Breadcrumb-->
                    </div>
                    <!--end::Page title-->
                </div>
                <!--end::Container-->
            </div>
            <!--end::Toolbar-->
            <!--begin::Post-->
            <div class="post d-flex flex-column-fluid" id="kt_post">
                <!--begin::Container-->
                <div id="kt_content_container" class="container-xxl">
                    <form id="dynamicReportFilterForms" class="form mb-15">
                        <div class="row my-5">
                            <div class="col">
                                <div class="card">
                                    <div class="card-body">
                                        <!--begin::Input group-->
                                        <div class="row g-9 mb-8">
                                            <input type="hidden" value='${controlsAsArray}' id="contorlsData"/>
                                            <s:iterator value="componentDetails">

                                                <!--some required hidden fields-->
                                                <s:set var="report_ids" value="report_ids"></s:set>
                                                <input type="hidden" value="<s:property value = 'report_ids'/>"
                                                name="report_ids" id="reportId" />
                                                <input type="hidden" value="<s:property value = 'merge_keys'/>"
                                                name="merge_keys" />
                                                <s:set var="controlTitle" value="control_title"></s:set>
                                                <s:set var="controlId" value="control_id"></s:set>
                                                <s:set var="defaultValue" value="control_default_value"></s:set>

                                                <!--begin::Col-->
                                                <div class="col-md-4 fv-row">
                                                    <s:if test="%{control_type!='button' && control_type!='hidden'}">
                                                        <label class="d-flex align-items-center fs-6 fw-bold mb-2">
                                                            <span class="">${controlTitle}</span>
                                                        </label>
                                                    </s:if>
                                                    <s:if test="%{control_type=='hidden'}">
                                                        <input type="hidden" id="<s:property value =
                                                        'control_id'/>"
                                                        name="<s:property value = 'control_id'/>"
                                                        class="form-control form-control-solid"
                                                        value="${defaultValue}" />
                                                    </s:if>
                                                    <s:if test="%{control_type=='text'}">
                                                        <input type="text" id="<s:property value = 'control_id'/>"
                                                        name="<s:property value = 'control_id'/>"
                                                        class="form-control form-control-solid" value="${defaultValue}" />
                                                    </s:if>
                                                    <s:if test="%{control_type=='select'}">
                                                        <s:if test = "%{control_event != null && control_event == 'change'}">
                                                            <s:select headerKey="" headerValue="Select"
                                                                data-control="select2" class="form-select form-select-solid adminMerchants"
                                                                list="list_of_values"
                                                                listValue="key" listKey="value" name="%{#controlId}"
                                                                id="%{#controlId}" autocomplete="off" onchange="loadDependent(this.id, this.value);"/>
                                                        </s:if>
                                                        <s:else>
                                                            <select data-control="select2" class="form-select form-select-solid adminMerchants"
                                                            name="<s:property value = 'control_id'/>" id="<s:property value = 'control_id'/>" autocomplete="off">
                                                                <option value=""> Please Select</option>
                                                                <s:iterator value="list_of_values">
                                                                    <option value="<s:property value='value' />"><s:property value='key' /></option>
                                                                </s:iterator>
                                                            </select>
                                                        </s:else>
                                                    </s:if>
                                                    <s:if test="%{control_type=='mselect'}">
                                                        <s:select
                                                            data-control="select2" class="form-select form-select-solid"
                                                            list="list_of_values"
                                                            listValue="key" listKey="value" name="%{#controlId}"
                                                            id="%{#controlId}" autocomplete="off"
                                                            multiple="true" />
                                                    </s:if>
                                                    <s:if test="%{control_type=='date'}">
                                                        <div class="position-relative d-flex align-items-center">
                                                            <!--begin::Icon-->
                                                            <!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
                                                            <span class="svg-icon svg-icon-2 position-absolute mx-4">
                                                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none"
                                                                    xmlns="http://www.w3.org/2000/svg"> <path
                                                                    opacity="0.3"
                                                                    d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
                                                                    fill="currentColor" /> <path
                                                                    d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
                                                                    fill="currentColor" /> <path
                                                                    d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
                                                                    fill="currentColor" /> </svg>
                                                            </span>
                                                            <!--end::Svg Icon-->
                                                            <!--end::Icon-->
                                                            <!--begin::Datepicker-->
                                                            <input class="form-control form-control-solid ps-12"
                                                                placeholder="Select a date"
                                                                name="<s:property value = 'control_id'/>"
                                                                id="<s:property value = 'control_id'/>"/>
                                                            <!--end::Datepicker-->
                                                        </div>
                                                    </s:if>
                                                    <s:if test="%{control_type=='time'}">
                                                        <div class="input-group clockpicker">
                                                            <input type="text" class="form-control form-control-solid"
                                                                id="<s:property value = 'control_id'/>"
                                                                name="<s:property value = 'control_id'/>"
                                                                value="00:00" readonly>
                                                            <span class="input-group-addon">
                                                                <span class="glyphicon glyphicon-time"></span>
                                                            </span>
                                                        </div>
                                                    </s:if>
                                                    <s:if test="%{control_type=='radio'}">
                                                        <s:radio class="form-control form-control-solid"
                                                        id="%{#controlId}"
                                                        name="%{#controlId}" list="list_of_values"
                                                        listKey="value" listValue="key" />
                                                    </s:if>
                                                    <s:if test="%{control_type=='check'}">
                                                        <s:checkbox class="form-control form-control-solid" id="%{#controlId}"
                                                        name="%{#controlId}" />
                                                    </s:if>
                                                    <s:if test="%{control_type=='mcheck'}">
                                                        <s:checkboxlist class="form-control form-control-solid" id="%{#controlId}"
                                                        name="%{#controlId}" list="list_of_values"
                                                        listKey="value" listValue="key" />
                                                    </s:if>
                                                </div>
                                            </s:iterator>
                                            <div
                                                class="col-md-8 fv-row d-flex justify-content-center align-items-end justify-content-md-end">
                                                <button type="button" class="btn w-100 w-md-25 btn-primary"
                                                    onclick="fetchReportData();">Search</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
                <!--end::Container-->
            </div>
            <!--end::Post-->
            <div class="row my-5">
                <div class="col">
                    <div class="card">
                        <div class="card-body">
                            <div class="row g-9 mb-8 justify-content-end" style="display: none;" id="actionsDiv">
                                <div class="col-lg-4 col-sm-12 col-md-6" style="padding-bottom: 20px;">
                                    <select name="actions" data-control="select2"
                                        data-placeholder="Actions" id="actions"
                                        class="form-select form-select-solid actions"
                                        data-hide-search="true" onchange="download(this.value);">
                                        <option value="">Actions</option>
                                        <option value="copy">Copy</option>
                                        <option value="csv">CSV</option>
                                        <option value="pdf">PDF</option>
                                        <option value="print">PRINT</option>
                                    </select>
                                </div>
                            </div>
                            <div class="row g-9 mt-9">
                                <table id="dynamicReportDataTable${report_ids}" class="table table-striped table-row-bordered gy-5 gs-7"
                                width="100%"></table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
        <script>
            $(document).ready(function(){
                let controlDetails = $("#contorlsData").val();
                controlDetails = jQuery.parseJSON(controlDetails);
                let controlsArray = controlDetails.data;
                for (let i=0; i < controlsArray.length; i++) {
                    let controlData = controlsArray[i];
                    document.getElementById("reportTitle").innerHTML = controlData.title;
					document.getElementById("reportTitleFr").innerHTML = controlData.title;
                    if (controlData.control_type === 'date') {
                        $("#" + controlData.control_id).flatpickr({
                            showOtherMonths: true,
                            dateFormat: 'Y-m-d',
                            selectOtherMonths: false,
                            defaultDate: 'today',
                            maxDate: new Date()
                        });
                    }
                    if (controlData.control_type === 'select') {
                        $(".adminMerchants").select2();
                    }
                }
            });
        </script>
        <script>
            function fetchReportData() {

                // var saveForm = $("#id").val();
                var saveForm = false;
                var dataString = $("#dynamicReportFilterForms").serialize();
                dataString = '{"' + decodeURI(dataString)
                .replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}';
                dataString = JSON.parse(dataString.replace("%40", "@"));
                let controlDetails = $("#contorlsData").val();
                controlDetails = jQuery.parseJSON(controlDetails);
                let controlsArray = controlDetails.data;

                for (key in dataString) {
                    for (let i=0; i < controlsArray.length; i++) {
                        let controlData = controlsArray[i];
                        if (controlData.control_id === key && controlData.validation_regex !== null) {
                            let regexVal = controlData.validation_regex;
                            let regex = new RegExp(regexVal);
                            if (dataString[key] !== null && dataString[key] !== '' &&  !regex.test(dataString[key])) {
                                alert("Please provide valid value for " + key);
                                document.getElementById(key).focus();
                                return false;
                            }
                            break;
                        }
                    }
                }
                var urls = new URL(window.location.href);
                var domain = urls.origin;
                $.ajax({
                    type: "POST",
                    url: domain + "/crmws/dReport/fetchReport",
                    contentType: 'application/json',
                    data: JSON.stringify(dataString),
                    success: function(result) {
                        if (result.status) {
                            if (!saveForm) {
                                initializeDataTable(result.data);
                            } else {
                                alert(result.message);
                            }
                        } else {
                            alert(result.message);
                        }
                    },
                    error : function(status) {
                        alert("Failed to retrieve data.");
                    }
                });
            }
            function toCamelCase(str) {
                var result = str.toLowerCase();
                result = result.replaceAll("_", " ");
                var tokens = result.split(" ");
                result = "";
                for (let j=0; j < tokens.length; j++) {
                    result += tokens[j].charAt(0).toUpperCase() + tokens[j].substring(1, tokens[j]
                    .length) + "";
                }
                return result;
            }

            "use strict";
            // Class definition
            var KTDatatablesServerSide = function (data) {
                var tableId = "dynamicReportDataTable" + $("#reportId").val();
                var table;
                var dt;
                var filterPayment;
                var initDatatable = function (data) {
                    let columns = [];
                    if (data && data.length > 0) {
                        let headerColumnOrder = Object.keys(data[0]);
                        let columnData = Object.entries(data[data.length - 1]);
                        for (let j = 0; j < columnData.length; j++) {
                            columns.push({
                                data: columnData[j][0],
                                title: columnData[j][1] == null ? toCamelCase(columnData[j][0]) : columnData[j][1]
                            });
                        }
                    }
                    data.pop(data.length - 1);
                    dt = $("#" + tableId).DataTable({
                        dom: 'Brtipl',
                        "columnDefs": [{
                            className: "dt-body-right"
                        }],
                        buttons: [
                                  'copy', 'csv', 'excel', 'pdf', 'print'
                        ],
                        scrollY: true,
                        scrollX: true,
                        "bDestroy": true,
                        "paging": true,
                        "searching": true,
                        "processing": true,
                        "serverSide": false,
                        "paginationType": "full_numbers",
                        "lengthMenu": [[10, 25, 50, 100], [10, 25, 50, 100]],
                        "data" : data,
                        "aoColumns": columns
                    });
                    table = dt.$;
                    dt.on('draw', function () {
                        KTMenu.createInstances();
                    });
                }
                // Search Datatable --- official docs reference: https://datatables.net/reference/api/search()
                var handleSearchDatatable = function () {
                    const filterSearch = document.querySelector('[data-kt-docs-table-filter="search"]');
                    filterSearch.addEventListener('keyup', function (e) {
                        dt.search(e.target.value).draw();
                    });
                }
                // Public methods
                return {
                    init: function (data) {
                        initDatatable(data);
                    }
                }
            }();

            function initializeDataTable(data) {
                $.fn.dataTable.ext.errMode = 'none';
                document.getElementById("actionsDiv").style.display = "";
                KTDatatablesServerSide.init(data);
            }
            $('.clockpicker').clockpicker({
            	placement: 'top',
            	donetext: 'Done',
            	twelvehour: true
            });

            function download(type) {
                if(type=='csv'){
                    document.querySelector('.buttons-csv').click();
                }
                if(type=='copy'){
                    document.querySelector('.buttons-copy').click();
                }
                if(type=='pdf'){
                    document.querySelector('.buttons-pdf').click();
                }
                if(type=='print'){
                    document.querySelector('.buttons-print').click();
                }
            }

            function loadDependent(parentId, parentVal) {
                let allControls = $("#contorlsData").val();
                allControls = jQuery.parseJSON(allControls);
                let controlsArray = allControls.data;
                let childId = "";
                for (let j=0; j < controlsArray.length; j++) {
                    let controlObj = controlsArray[j];
                    if (parentId == controlObj.control_id) {
                        childId = controlObj.control_event_dependent_control_Id;
                    }
                }
                for (let j=0; j < controlsArray.length; j++) {
                    let controlObj = controlsArray[j];
                    let dependValues = [];
                    if (childId === controlObj.control_id) {
                        for (let k = 0; k < controlObj.list_of_values.length; k++) {
                            let dependObj = controlObj.list_of_values[k];
                            if (dependObj.dependentId === parentVal) {
                                dependValues.push(dependObj);
                            }
                        }
                        applyFilterValue(dependValues, childId);
                        break;
                    }
                }
            }

            function applyFilterValue(data, controlId) {
                let options = "<option value=''>ALL</option>";
                for (let j=0; j < data.length; j++) {
                    let optData = data[j];
                    options += "<option value='"+ optData.value +"'>" + optData.key + "</option>";
                }
                document.getElementById(controlId).innerHTML = options;
            }
        </script>
    </body>
</html>