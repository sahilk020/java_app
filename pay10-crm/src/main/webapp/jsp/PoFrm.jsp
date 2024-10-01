<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>PO FRM</title>
    <link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css"
        rel="stylesheet" type="text/css" />
    <link href="../assets/plugins/custom/datatables/datatables.bundle.css"
        rel="stylesheet" type="text/css" />
    <link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet"
        type="text/css" />
    <link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
    <link href="../assets/css/custom/custom-style.css" rel="stylesheet"
        type="text/css" />
    <script src="../js/loader/main.js"></script>
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
    <link href="../css/select2.min.css" rel="stylesheet" />
    <script src="../js/jquery.select2.js" type="text/javascript"></script>






</head>

<body>
    <div class="content d-flex flex-column flex-column-fluid" id="kt_content">
        <!--begin::Toolbar-->
        <div class="toolbar" id="kt_toolbar">
            <!--begin::Container-->
            <div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
                <!--begin::Page title-->
                <div data-kt-swapper="true" data-kt-swapper-mode="prepend"
                    data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
                    class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
                    <!--begin::Title-->
                    <h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Pay-out
                        FRM</h1>
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
                        <li class="breadcrumb-item text-dark">Pay-out FRM</li>
                        <!--end::Item-->
                    </ul>
                    <!--end::Breadcrumb-->
                </div>
                <!--end::Page title-->

            </div>
            <!--end::Container-->
        </div>


        <div style="overflow: auto !important;">

            <div id="kt_content_container" class="container-xxl">
                <div class="row my-5">
                    <div class="col">
                        <div class="card">


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




                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="post d-flex flex-column-fluid" id="kt_post">
            <!--begin::Container-->
            <div id="kt_content_container" class="container-xxl">
                <div class="row my-5">
                    <div class="col">
                        <div class="card">
                            <div class="card-body">
                                <div class="row my-3 g-9 align-items-center ">
                                            <div class="col-lg-4 my-2">
                                                <label
                                                    class="d-flex align-items-center fs-6 fw-bold mb-2">
                                                    <span
                                                        class="required">Merchant</span>
                                                </label>
                                                <s:select name="merchant" id="merchant"
                                                    value="merchant" headerKey=""
                                                    class="form-select form-select-solid merchantPayId"
                                                    headerValue="Please Select Merchant"
                                                    list="merchantList" listKey="payId"
                                                    listValue="businessName"
                                                    autocomplete="off" />

                                            </div>


                                            <div class="col-lg-4 my-2">
                                                <label for="Channel"
                                                    class="d-flex align-items-center fs-6 fw-bold mb-2"><span
                                                        class="required">Channel</span></label>

                                                <select id="Channel"
                                                    class="form-select form-select-solid merchantPayId" onchange="getCurrencyList()">
                                                    <option value="">Please Select
                                                        Channel</option>
                                                    <option value="FIAT">FIAT</option>
                                                </select>
                                            </div>

                                            <div class="col-lg-4 my-2" id="currencyListDropdown">
                                                <label for="Currrency"
                                                    class="d-flex align-items-center fs-6 fw-bold mb-2"><span
                                                        class="required">Currency</span></label>

                                                        <select name="selectedCurrency"
														class="form-select form-select-solid" id="currency"
														headerKey=" " headerValue="Select Currency"
														list="currencyList" listKey="code" listValue="name" data-control="select2" onchange="getPayoutFrmData()"
														>
                                                        <option value="">Please Select Currency</option>
                                                    </select>
                                            </div>
                                        </div>
                                <div
                                    class="row my-3 g-9 align-items-center">
                                    <div class="col-lg-4 my-2">
                                        <label
                                            class="d-flex align-items-center fs-6 fw-bold mb-2"
                                            for="email">Min. Amount Per Transaction : <i id="currencySymbol" style="font-size: large; font-weight: bolder; color: black;"></i></label>
                                        <input type="number" id="minTicketSize" min="0"
                                            class="form-control form-control-solid" placeholder="Min. Amount Per Transaction"
                                            name="minTicketSize" />
                                    </div>

                                    <div class="col-lg-4 my-2">
                                        <label
                                            class="d-flex align-items-center fs-6 fw-bold mb-2"
                                            for="email">Max. Amount Per Transaction : <i id="currencySymbol" style="font-size: large; font-weight: bolder; color: black;"></i></label>
                                        <input type="number" id="maxTicketSize" min="0"
                                            class="form-control form-control-solid" placeholder="Max. Ticket Size"
                                            name="maxTicketSize" />
                                    </div>
                                    <div class="col-lg-4 my-2">
                                    </div>
                                </div>

                                <div
                                    class="row my-3 g-9 align-items-center">
                                    <div class="col-lg-4 my-2">
                                        <label
                                            class="d-flex align-items-center fs-6 fw-bold mb-2"
                                            for="email"> Max. Amount Limit Per Day : <i id="currencySymbol" style="font-size: large; font-weight: bolder; color: black;"></i></label>
                                        <input type="number" id="dailyLimit" min="0"
                                            class="form-control form-control-solid" placeholder="Daily Transaction Limit"
                                            name="dailyLimit" />
                                            <label class="d-flex align-items-center fs-6 fw-bold mb-2">
                                                <span class="">Remaining : <i id="displayDailyQuota" style="color: rgb(43, 43, 43);"></i></span>
                                            </label>
                                    </div>

                                    <div class="col-lg-4 my-2">
                                        <label
                                            class="d-flex align-items-center fs-6 fw-bold mb-2"
                                            for="email">Max. Amount Limit Per Week : <i id="currencySymbol1" style="font-size: large; font-weight: bolder; color: black;"></i></label>
                                        <input type="number" id="weeklyLimit" min="0"
                                            class="form-control form-control-solid" placeholder="Weekly Transaction Limit"
                                            name="weeklyLimit" />
                                            <label class="d-flex align-items-center fs-6 fw-bold mb-2">
                                                <span class="">Remaining : <i id="displayWeeklyQuota" style="color: rgb(43, 43, 43);"></i></span>
                                            </label>
                                    </div>
                                    <div class="col-lg-4 my-2">
                                        <label
                                            class="d-flex align-items-center fs-6 fw-bold mb-2"
                                            for="email">Max. Amount Limit Per Month : <i id="currencySymbol2" style="font-size: large; font-weight: bolder; color: black;"></i></label>
                                        <input type="number" id="monthlyLimit" min="0"
                                            class="form-control form-control-solid" placeholder="Monthly Transaction Limit"
                                            name="monthlyLimit" />
                                            <label class="d-flex align-items-center fs-6 fw-bold mb-2">
                                                <span class="">Remaining : <i id="displayMonthlyQuota" style="color: rgb(43, 43, 43);"></i></span>
                                            </label>
                                    </div>
                                </div>

                                <div
                                    class="row my-3 g-9 align-items-center">
                                    <div class="col-lg-4 my-2">
                                        <label
                                            class="d-flex align-items-center fs-6 fw-bold mb-2"
                                            for="email">Max. Transaction Count Per Day</label>
                                        <input type="number" id="dailyVolume" min="0"
                                            class="form-control form-control-solid" placeholder="Daily Volume"
                                            name="dailyVolume" />
                                            <label class="d-flex align-items-center fs-6 fw-bold mb-2">
                                                <span class="">Remaining : <i id="displayDailyVolumeQuota" style="color: rgb(43, 43, 43);"></i></span>
                                            </label>
                                    </div>

                                    <div class="col-lg-4 my-2">
                                        <label
                                            class="d-flex align-items-center fs-6 fw-bold mb-2"
                                            for="email">Max. Transaction Count Per Week</label>
                                        <input type="number" id="weeklyVolume" min="0"
                                            class="form-control form-control-solid" placeholder="Weekly Volume"
                                            name="weeklyVolume" />
                                            <label class="d-flex align-items-center fs-6 fw-bold mb-2">
                                                <span class="">Remaining : <i id="displayWeeklyVolumeQuota" style="color: rgb(43, 43, 43);"></i></span>
                                            </label>
                                    </div>
                                    <div class="col-lg-4 my-2">
                                        <label
                                            class="d-flex align-items-center fs-6 fw-bold mb-2"
                                            for="email">Max. Transaction Count Per Month</label>
                                        <input type="number" id="monthlyVolume" min="0"
                                            class="form-control form-control-solid" placeholder="Monthly Volume"
                                            name="monthlyVolume" />
                                            <label class="d-flex align-items-center fs-6 fw-bold mb-2">
                                                <span class="">Remaining : <i id="displayMonthlyVolumeQuota" style="color: rgb(43, 43, 43);"></i></span>
                                            </label>
                                    </div>
                                </div>
                                <div
                                    class="row my-3 g-9 align-items-center">
                                    <div class="col-lg-4 my-2">
                                        <button type="button" class="btn btn-primary"
                                            id="SubmitButton"
                                            onclick="saveData()">Save</button>
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

        $(document).ready(function(){

        })

        $(".merchantPayId").select2();

        function saveData() {
            debugger
            document.getElementById("SubmitButton").disabled = true;

            var merchant = $("#merchant").val();
            var channel = $("#Channel").val();
            var currrency = $("#currency").val();





            var minTicketSize =$("#minTicketSize").val();
            var maxTicketSize = $("#maxTicketSize").val();
            var dailyLimit = $("#dailyLimit").val();
            var weeklyLimit = $("#weeklyLimit").val();
            var monthlyLimit = $("#monthlyLimit").val();
            var dailyVolume = $("#dailyVolume").val();
            var weeklyVolume = $("#weeklyVolume").val();
            var monthlyVolume = $("#monthlyVolume").val();



            if (merchant == "" || merchant == undefined || merchant == null) {
                alert("Please Select Merchant");
                document.getElementById("SubmitButton").disabled = false;
                return false;
            }

            if (channel == "" || channel == undefined || channel == null) {
                alert("Please Select Channel");
                document.getElementById("SubmitButton").disabled = false;
                return false;
            }

            if (currrency == "") {
                alert("Please Select Currency");
                document.getElementById("SubmitButton").disabled = false;
                return false;
            }

            if (minTicketSize == "" || minTicketSize == undefined || minTicketSize == null) {
                alert("Please Enter Min. Amount Per Transaction");
                document.getElementById("SubmitButton").disabled = false;
                return false;
            }
            var minTicketSize1 = parseInt(minTicketSize,10);
            if (minTicketSize1 < 1) {
                alert("Please Enter Min. Amount Per Transaction more than 1");
                document.getElementById("SubmitButton").disabled = false;
                return false;
            }
            if (maxTicketSize == "" || maxTicketSize == undefined || maxTicketSize == null) {
                alert("Please Enter Max. Amount Per Transaction");
                document.getElementById("SubmitButton").disabled = false;
                return false;
            }

            var maxTicketSize1 = parseInt(maxTicketSize,10);
            if (maxTicketSize1 < 1) {
                alert("Please Enter Max. Amount Per Transaction more than 1");
                document.getElementById("SubmitButton").disabled = false;
                return false;
            }

            if (minTicketSize1 > maxTicketSize1) {
                alert("Max. Amount Per Transaction Should Be Greater Than Min. Amount Per Transaction");
                document.getElementById("SubmitButton").disabled = false;
                return false;
            }

            if (dailyLimit == "" || dailyLimit == undefined || dailyLimit == null) {
                alert("Please Enter Max. Amount Limit Per Day");
                document.getElementById("SubmitButton").disabled = false;
                return false;
            }

            var dailyLimit1 = parseInt(dailyLimit,10);
            if (dailyLimit1 < 1) {
                alert("Please Enter Max. Amount Limit Per Day more than 1");
                document.getElementById("SubmitButton").disabled = false;
                return false;
            }
            if (weeklyLimit == "" || weeklyLimit == undefined || weeklyLimit == null) {
                alert("Please Enter Max. Amount Limit Per Week");
                document.getElementById("SubmitButton").disabled = false;
                return false;
            }

            var weeklyLimit1 = parseInt(weeklyLimit,10);
            if (weeklyLimit1 < 1) {
                alert("Please Enter Max. Amount Limit Per Week more than 1");
                document.getElementById("SubmitButton").disabled = false;
                return false;
            }
            if (dailyLimit1 > weeklyLimit1) {
                alert(" Max. Amount Limit Per Week Should Be Greater Than  Max. Amount Limit Per Day");
                document.getElementById("SubmitButton").disabled = false;
                return false;
            }
            if (monthlyLimit == "" || monthlyLimit == undefined || monthlyLimit == null) {
                alert("Please Enter  Max. Amount Limit Per Month");
                document.getElementById("SubmitButton").disabled = false;
                return false;
            }

            var monthlyLimit1 = parseInt(monthlyLimit,10);
            if (monthlyLimit1 < 1) {
                alert("Please Enter  Max. Amount Limit Per Month more than 1");
                document.getElementById("SubmitButton").disabled = false;
                return false;
            }
            if (weeklyLimit1 > monthlyLimit1) {
                alert(" Max. Amount Limit Per Month Limit Should Be Greater Than  Max. Amount Limit Per Week");
                document.getElementById("SubmitButton").disabled = false;
                return false;
            }
            if (dailyVolume == "" || dailyVolume == undefined || dailyVolume == null) {
                alert("Please Enter Max. Transaction Count Per Day");
                document.getElementById("SubmitButton").disabled = false;
                return false;
            }

            var dailyVolume1 = parseInt(dailyVolume,10);
            if (dailyVolume1 < 1) {
                alert("Please Enter Max. Transaction Count Per Day more than 1");
                document.getElementById("SubmitButton").disabled = false;
                return false;
            }
            if (weeklyVolume == "" || weeklyVolume == undefined || weeklyVolume == null) {
                alert("Please Enter Max. Transaction Count Per Week");
                document.getElementById("SubmitButton").disabled = false;
                return false;
            }

            var weeklyVolume1 = parseInt(weeklyVolume,10);
            if (weeklyVolume1 < 1) {
                alert("Please Enter Max. Transaction Count Per Week more than 1");
                document.getElementById("SubmitButton").disabled = false;
                return false;
            }

            if (dailyVolume1 > weeklyVolume1) {
                alert("Max. Transaction Count Per Week Should Be Greater Than Max. Transaction Count Per Day");
                document.getElementById("SubmitButton").disabled = false;
                return false;
            }

            if (monthlyVolume == "" || monthlyVolume == undefined || monthlyVolume == null) {
                alert("Please Enter Max. Transaction Count Per Month");
                document.getElementById("SubmitButton").disabled = false;
                return false;
            }

            var monthlyVolume1 = parseInt(monthlyVolume,10);
            if (monthlyVolume1 < 1) {
                alert("Please Enter Max. Transaction Count Per Month more than 1");
                document.getElementById("SubmitButton").disabled = false;
                return false;
            }
            if (weeklyVolume1 > monthlyVolume1) {
                alert("Max. Transaction Count Per Month Should Greater Than Max. Transaction Count Per Week");
                document.getElementById("SubmitButton").disabled = false;
                return false;
            }

            payload = {
                "payId": merchant,
                "channel": channel,
                "currencyCode": currrency,
                "minTicketSize": minTicketSize,
                "maxTicketSize": maxTicketSize,
                "dailyLimit": dailyLimit,
                "weeklyLimit": weeklyLimit,
                "monthlyLimit": monthlyLimit,
                "dailyVolume": dailyVolume,
                "weeklyVolume": weeklyVolume,
                "monthlyVolume": monthlyVolume

            }



            // 											var url ="http://localhost:8080/crmws/Payout/FRM";



            var urls = new URL(window.location.href);
            var domain = urls.origin;


            $.ajax({
                type: "POST",
                url: domain + "/crmws/Payout/FRM",
                //url :"http://localhost:8080/Payout/FRM" ,
                data: JSON.stringify(payload),
                contentType: "application/json",
                success: function (data, status) {
                    debugger
                    alert(data.respmessage);
                    document.getElementById("SubmitButton").disabled = false;
                    location.reload();


                },
                error: function (status) {
                    alert("Error while saving FRM");
                    document.getElementById("SubmitButton").disabled = false;
                }
            });


        }

        function getCurrencyList(){
            var urls = new URL(window.location.href);
            var domain = urls.origin;
            var merchant = $("#merchant").val();
            console.log("Merchant :"+merchant);

            payload ={
                "payId" : merchant,
            }

           $.ajax({
            type:"GET",
            url : domain+"/crmws/CurrencyNumber/getCurrencyList?payId="+merchant,
            contentType : "application/json",
            success : function(data){
                console.log(data);

                var s= '<option value="">Select Currency</option>';
				for (let key in data) {

					s += '<option value="' + key + '">' + data[key] + '</option>'
					console.log("Object ::::"+key + " Map "+data[key])

				}
				document.getElementById("currencyListDropdown").style.display = "block";

				$("#currency").html(s);
            }
           })
        }


        function getPayoutFrmData(){

            if(currency == ""){
                alert("Please Select Currency");
                return false;
            }

            getCurrencySymbol();
                         $("#minTicketSize").val("");
						$("#maxTicketSize").val("");
                        $("#dailyLimit").val("");
						$("#weeklyLimit").val("");
						$("#monthlyLimit").val("");
						$("#dailyVolume").val("");
						$("#weeklyVolume").val("");
						$("#monthlyVolume").val("");
                        $("#displayDailyQuota").text("");
                        $("#displayWeeklyQuota").text("");
                        $("#displayMonthlyQuota").text("");
                        $("#displayDailyVolumeQuota").text("");
                        $("#displayWeeklyVolumeQuota").text("");
                        $("#displayMonthlyVolumeQuota").text("");


            var urls = new URL(window.location.href);
            var domain = urls.origin;
            var merchant = $("#merchant").val();
            var channel = $("#Channel").val();
            var currrency = $("#currency").val();
            console.log("Currency : "+currrency);

            payload = {
                "payId": merchant,
                "channel": channel,
                "currencyCode": currrency
            }

            console.log(payload);
            $.ajax({
                type: "POST",
                url: domain + "/crmws/Payout/getPayoutFrm",

                data: JSON.stringify(payload),
                contentType: "application/json",
                success: function (data) {

                    console.log(data);
                    console.log(data.displayDailyQuota);
                    console.log(data.displayWeeklyQuota);
                    console.log(data.displayMonthlyQuota);
                    var displayDailyVolumeQuota1 = data.displayDailyVolumeQuota;
                    console.log(displayDailyVolumeQuota1);


                        $("#minTicketSize").val(data.minTicketSize);
						$("#maxTicketSize").val(data.maxTicketSize);
                        $("#dailyLimit").val(data.dailyLimit);
						$("#weeklyLimit").val(data.weeklyLimit);
						$("#monthlyLimit").val(data.monthlyLimit);
						$("#dailyVolume").val(data.dailyVolume);
						$("#weeklyVolume").val(data.weeklyVolume);
						$("#monthlyVolume").val(data.monthlyVolume);
                        $("#displayDailyQuota").text(data.displayDailyQuota);
                        $("#displayWeeklyQuota").text(data.displayWeeklyQuota);
                        $("#displayMonthlyQuota").text(data.displayMonthlyQuota);
                        $("#displayDailyVolumeQuota").text(data.displayDailyVolumeQuota);
                        $("#displayWeeklyVolumeQuota").text(data.displayWeeklyVolumeQuota);
                        $("#displayMonthlyVolumeQuota").text(data.displayMonthlyVolumeQuota);


                },

            });


        }

        function getCurrencySymbol(){
            var urls = new URL(window.location.href);
            var domain = urls.origin;
            var currency = $("#currency").val();
            console.log("Currency :"+currency);


            if(currency == ""){
                alert("Please Select Currency");
                return false;
            }
            payload ={
                "currency" : currency,
            }

           $.ajax({
            type:"GET",
            url : domain+"/crmws/CurrencyNumber/getCurrencySymbol?currency="+currency,
            contentType : "application/json",
            success : function(data){
                console.log(data);

                $("#currencySymbol").text(data);
                $("#currencySymbol1").text(data);
                $("#currencySymbol2").text(data);

            }
           })
        }

        function getPayoutFrmRemainingQuota(){

        var urls = new URL(window.location.href);
        var domain = urls.origin;
        var merchant = $("#merchant").val();
        var channel = $("#Channel").val();
        var currrency = $("#currency").val();

        payload = {
            "payId": merchant,
            "channel": channel,
            "currencyCode": currrency
        }

        console.log(payload);
        $.ajax({
            type: "POST",
            url: domain + "/crmws/Payout/getPayoutFrmRemainingQuota",

            data: JSON.stringify(payload),
            contentType: "application/json",
            success: function (data) {

                console.log(data);

            $("#minTicketSize").val(data.dailyVolume);
            $("#maxTicketSize").val(data.maxTicketSize);
            $("#dailyLimit").val(data.dailyLimit);
            $("#weeklyLimit").val(data.weeklyLimit);
            $("#monthlyLimit").val(data.monthlyLimit);
            $("#dailyVolume").val(data.dailyVolume);
            $("#weeklyVolume").val(data.weeklyVolume);
            $("#monthlyVolume").val(data.monthlyVolume);

    },

});


}

    </script>

    <script>
        document.getElementById('pgCheckbox').addEventListener('change', function () {
            if (this.checked) {
                window.location.href = 'frmAction';
            }
        });
    </script>
</body>

</html>