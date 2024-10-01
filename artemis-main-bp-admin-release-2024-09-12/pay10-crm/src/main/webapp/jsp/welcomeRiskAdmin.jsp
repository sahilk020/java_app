<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html>
<html lang="en">

<head>
<title>Risk Admin Dashboard</title>
<script src="../js/jquery.min.js" type="text/javascript"></script>
<script src="../js/commanValidate.js"></script>
<script src="../js/welcomeAdmin.js"></script>
<script src="../js/aquirerDashBoard.js"></script>
<script type="text/javascript">
  function getMopType(value,id){
        var merchantemail = document.getElementById("merchant").value;
  var paytype=value;



  $.ajax({
      type : "GET",
      url : "GetMoptype",
      timeout : 0,
      data : {
          "merchantemail":"ALL",
          "payment":paytype,
          "struts.token.name": "token",
        },
      success : function(data) {
        debugger
      var mopresult = [];

        mopresult = data.moplist;
             var count=0;
             $('#'+id).html("");
             const countriesDropDown = document.getElementById(id);
             for (let key in mopresult) {
              if(count==0){
                 let option = document.createElement("option");
                  option.setAttribute('value', "ALL");
                  if (id == 'mopTypePie') {
                      option.setAttribute('disabled', "disabled");
                  }

                  let optionText = document.createTextNode("MOP TYPE");
                  option.appendChild(optionText);

                  countriesDropDown.appendChild(option);
                  count++

              }
                let option = document.createElement("option");
                option.setAttribute('value', data.moplist[key].code);

                let optionText = document.createTextNode(data.moplist[key].name);
                option.appendChild(optionText);

                countriesDropDown.appendChild(option);
              }
             let option = document.createElement("option");
             option.setAttribute('value', "Others");

             let optionText = document.createTextNode("Others");
             option.appendChild(optionText);

             countriesDropDown.appendChild(option);
      }
    });
  }

  </script>
</head>

<body id="kt_body" class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed toolbar-tablet-and-mobile-fixed aside-enabled aside-fixed" style="--kt-toolbar-height:55px;--kt-toolbar-height-tablet-and-mobile:55px">
  <!--begin::Content-->
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
          <h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Sub-Admin
            Dashboard</h1>
          <!--end::Title-->
        </div>

        <div class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1" style="margin-left: auto;">
                    <s:select headerKey="ALL MERCHANTS" headerValue="ALL MERCHANTS" data-control="select2"
                          class="form-select form-select-solid payment_Methods" style="width: 105% !important"
                          data-hide-search="true" list="merchantList" autocomplete="off" listValue="businessName" listKey="emailId"
                          name="merchant" id="merchant" onchange="loadMerchantSpecific();">
                        </s:select>

                    <div id="wwgrp_currency" class="wwgrp">
                      <div id="wwcntrl_currency" class="wwctrl">
                        <select data-control="select2" class="form-select form-select-solid payment_Methods" style="width: 105% !important"
                          data-hide-search="true" autocomplete="off" name="currency" id="currency" onchange="loadMerchantSpecific();">
                          <option value="">Select Currency</option>
                          <s:iterator value="currencyCodeList" status="currencyCode">
                              <option value='<s:property value="code"/>' <s:if test="code==userDefaultCurrency">selected</s:if>  data-symbol='<s:property value="symbol"/>'><s:property value="name"/></option>
                          </s:iterator>
                        </select>
                      </div>
                    </div>
                  </div>

          <div class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1" data-kt-buttons="true">
                  <a
                    class="btn btn-sm  fw-bold btn-active btn-active-primary px-4"
                    id="buttonWeekly" onclick="javascript: document.getElementById('customRange').style.display='none'; document.getElementById('customRangeBtn').style.display='block';">Week</a>
                   <a
                    class="btn btn-sm  fw-bold btn-active btn-active-primary px-4 me-1"
                    id="buttonMonthly" onclick="javascript: document.getElementById('customRange').style.display='none'; document.getElementById('customRangeBtn').style.display='block';">Month</a>
                    <a
                    class="btn btn-sm  fw-bold btn-active btn-active-primary px-4 me-1"
                    id="buttonYearly" onclick="javascript: document.getElementById('customRange').style.display='none'; document.getElementById('customRangeBtn').style.display='block';">Year</a>
                    <input type="button" id="customRangeBtn" class="btn btn-sm fw-bold btn-active btn-active-primary px-4" value="Custom" onclick="javascript: document.getElementById('customRange').style.display='block'; document.getElementById('customRangeBtn').style.display='none';"/>
                    <input style="display: none;" type="text" name="customRange" id="customRange" class="btn btn-sm fw-bold btn-active btn-active-primary px-4" onchange="loadCustomDateDashboard(this.value);" />
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
        <!--begin::Row-->
        <div class="row g-5 g-xl-10 mb-5 mb-xl-10">
          <!--begin::Col-->
          <div class="col-lg-6">
            <div class="card card-flush h-xl-100">
              <!--begin::Heading-->
              <div
                class="card-header rounded bgi-no-repeat bgi-size-cover bgi-position-y-top bgi-position-x-center align-items-start h-250px"
                style="background-image: linear-gradient(339deg, #1730ed, #f4ad08)"
                data-theme="light">
                <!--begin::Title-->
                <h3
                  class="card-title align-items-start flex-column text-white pt-15">
                  <span class="fw-bold fs-2x mb-3">Welcome
                    <s:property value="%{#session.USER.businessName}" /></span>
                  <div class="fs-4 text-white">
                    <span class="opacity-75">Check your latest updates.</span>
                  </div>
                </h3>
                <!--end::Title-->
              </div>
              <!--end::Heading-->
              <!--begin::Body-->
              <div class="card-body mt-n20">
                <!--begin::Stats-->
                <div class="mt-n20 position-relative">
                  <!--begin::Row-->
                  <div class="row g-3 g-lg-6">
                    <!--begin::Col-->
                    <div class="col-lg-4 col-md-4 col-sm-12">
                      <!--begin::Items-->
                      <div
                        class="bg-gray-100 bg-opacity-70 rounded-2 px-5 py-5 d-flex flex-column align-items-center">
                        <!--begin::Symbol-->
                        <div
                          class="symbol symbol-30px mb-8 d-flex justify-content-center align-items-center">
                          <span class="symbol-label"> <!--begin::Svg Icon | path: icons/duotune/medicine/med005.svg-->
                            <span class="svg-icon svg-icon-1 svg-icon-primary">
                              <svg width="24" height="24" viewBox="0 0 24 24"
                                fill="none" xmlns="http://www.w3.org/2000/svg">
                                        <path
                                  d="M16.925 3.90078V8.00077L12.025 10.8008V5.10078L15.525 3.10078C16.125 2.80078 16.925 3.20078 16.925 3.90078ZM2.525 13.5008L6.025 15.5008L10.925 12.7008L6.025 9.90078L2.525 11.9008C1.825 12.3008 1.825 13.2008 2.525 13.5008ZM18.025 19.7008V15.6008L13.125 12.8008V18.5008L16.625 20.5008C17.225 20.8008 18.025 20.4008 18.025 19.7008Z"
                                  fill="currentColor"></path>
                                        <path opacity="0.3"
                                  d="M8.52499 3.10078L12.025 5.10078V10.8008L7.125 8.00077V3.90078C7.125 3.20078 7.92499 2.80078 8.52499 3.10078ZM7.42499 20.5008L10.925 18.5008V12.8008L6.02499 15.6008V19.7008C6.02499 20.4008 6.82499 20.8008 7.42499 20.5008ZM21.525 11.9008L18.025 9.90078L13.125 12.7008L18.025 15.5008L21.525 13.5008C22.225 13.2008 22.225 12.3008 21.525 11.9008Z"
                                  fill="currentColor"></path>
                                      </svg>
                          </span> <!--end::Svg Icon-->
                          </span>
                        </div>
                        <!--end::Symbol-->
                        <!--begin::Stats-->
                        <div class="m-0">
                          <!--begin::Number-->
                          <span
                            class="text-black-500 fw-bold text-center d-block fs-1 lh-1 ls-n1 mb-1" id="dvApprovedAmount">&#8377;<s:property value="%{#statistics.approvedAmount}" /></span>
                          <!--end::Number-->
                          <!--begin::Desc-->
                          <span class="text-gray-500 fw-semibold text-center s">Total
                            Captured</span>
                          <!--end::Desc-->
                        </div>
                        <!--end::Stats-->
                      </div>
                      <!--end::Items-->
                    </div>
                    <!--end::Col-->
                    <!--begin::Col-->
                    <div class="col-lg-4 col-md-4 col-sm-12">
                      <!--begin::Items-->
                      <div
                        class="bg-gray-100 bg-opacity-70 rounded-2 px-5 py-5 d-flex flex-column align-items-center">
                        <!--begin::Symbol-->
                        <div class="symbol symbol-30px mb-8">
                          <span class="symbol-label"> <!--begin::Svg Icon | path: icons/duotune/finance/fin001.svg-->
                            <span class="svg-icon svg-icon-1 svg-icon-primary">
                              <svg width="24" height="24" viewBox="0 0 24 24"
                                fill="none" xmlns="http://www.w3.org/2000/svg">
                                        <path opacity="0.3"
                                  d="M17.9061 13H11.2061C11.2061 12.4 10.8061 12 10.2061 12C9.60605 12 9.20605 12.4 9.20605 13H6.50606L9.20605 8.40002V4C8.60605 4 8.20605 3.6 8.20605 3C8.20605 2.4 8.60605 2 9.20605 2H15.2061C15.8061 2 16.2061 2.4 16.2061 3C16.2061 3.6 15.8061 4 15.2061 4V8.40002L17.9061 13ZM13.2061 9C12.6061 9 12.2061 9.4 12.2061 10C12.2061 10.6 12.6061 11 13.2061 11C13.8061 11 14.2061 10.6 14.2061 10C14.2061 9.4 13.8061 9 13.2061 9Z"
                                  fill="currentColor"></path>
                                        <path
                                  d="M18.9061 22H5.40605C3.60605 22 2.40606 20 3.30606 18.4L6.40605 13H9.10605C9.10605 13.6 9.50605 14 10.106 14C10.706 14 11.106 13.6 11.106 13H17.8061L20.9061 18.4C21.9061 20 20.8061 22 18.9061 22ZM14.2061 15C13.1061 15 12.2061 15.9 12.2061 17C12.2061 18.1 13.1061 19 14.2061 19C15.3061 19 16.2061 18.1 16.2061 17C16.2061 15.9 15.3061 15 14.2061 15Z"
                                  fill="currentColor"></path>
                                      </svg>
                          </span> <!--end::Svg Icon-->
                          </span>
                        </div>
                        <!--end::Symbol-->
                        <!--begin::Stats-->
                        <div class="m-0">
                          <!--begin::Number-->
                          <span
                            class="text-black-500 fw-bold text-center d-block fs-1 lh-1 ls-n1 mb-1" id="dvRefundedAmount">&#8377;<s:property value="%{statistics.totalRefunded}" /></span>
                          <!--end::Number-->
                          <!--begin::Desc-->
                          <span class="text-gray-500 fw-semibold text-center ">Total
                            Refund</span>
                          <!--end::Desc-->
                        </div>
                        <!--end::Stats-->
                      </div>
                      <!--end::Items-->
                    </div>
                    <!--end::Col-->
                    <!--begin::Col-->
                    <div class="col-lg-4 col-md-4 col-sm-12">
                      <!--begin::Items-->
                      <div
                        class="bg-gray-100 bg-opacity-70 rounded-2 px-5 py-5 d-flex flex-column align-items-center">
                        <!--begin::Symbol-->
                        <div
                          class="symbol symbol-30px mb-8 d-flex justify-content-center align-items-center">
                          <span class="symbol-label"> <!--begin::Svg Icon | path: icons/duotune/medicine/med005.svg-->
                            <span class="svg-icon svg-icon-1 svg-icon-primary">
                              <svg width="24" height="24" viewBox="0 0 24 24"
                                fill="none" xmlns="http://www.w3.org/2000/svg">
                                        <path
                                  d="M16.925 3.90078V8.00077L12.025 10.8008V5.10078L15.525 3.10078C16.125 2.80078 16.925 3.20078 16.925 3.90078ZM2.525 13.5008L6.025 15.5008L10.925 12.7008L6.025 9.90078L2.525 11.9008C1.825 12.3008 1.825 13.2008 2.525 13.5008ZM18.025 19.7008V15.6008L13.125 12.8008V18.5008L16.625 20.5008C17.225 20.8008 18.025 20.4008 18.025 19.7008Z"
                                  fill="currentColor"></path>
                                        <path opacity="0.3"
                                  d="M8.52499 3.10078L12.025 5.10078V10.8008L7.125 8.00077V3.90078C7.125 3.20078 7.92499 2.80078 8.52499 3.10078ZM7.42499 20.5008L10.925 18.5008V12.8008L6.02499 15.6008V19.7008C6.02499 20.4008 6.82499 20.8008 7.42499 20.5008ZM21.525 11.9008L18.025 9.90078L13.125 12.7008L18.025 15.5008L21.525 13.5008C22.225 13.2008 22.225 12.3008 21.525 11.9008Z"
                                  fill="currentColor"></path>
                                      </svg>
                          </span> <!--end::Svg Icon-->
                          </span>
                        </div>
                        <!--end::Symbol-->
                        <!--begin::Stats-->
                        <div class="m-0">
                          <!--begin::Number-->
                          <span
                            class="text-black-500 fw-bold text-center d-block fs-1 lh-1 ls-n1 mb-1" id="dvSettledAmount">&#8377;<s:property value="%{#statistics.totalSettleAmount}" /></span>
                          <!--end::Number-->
                          <!--begin::Desc-->
                          <span class="text-gray-500 fw-semibold text-center s">Total
                            Settled</span>
                          <!--end::Desc-->
                        </div>
                        <!--end::Stats-->
                      </div>
                      <!--end::Items-->
                    </div>
                    <!--end::Col-->
                    <!--begin::Col-->
                    <div class="col-lg-4 col-md-4 col-sm-12">
                      <!--begin::Items-->
                      <div
                        class="bg-gray-100 bg-opacity-70 rounded-2 px-5 py-5 d-flex flex-column align-items-center">
                        <!--begin::Symbol-->
                        <div class="symbol symbol-30px mb-8">
                          <span class="symbol-label"> <!--begin::Svg Icon | path: icons/duotune/general/gen020.svg-->
                            <span class="svg-icon svg-icon-1 svg-icon-primary">
                              <svg width="24" height="24" viewBox="0 0 24 24"
                                fill="none" xmlns="http://www.w3.org/2000/svg">
                                        <path
                                  d="M22 12C22 12.2 22 12.5 22 12.7L19.5 10.2L16.9 12.8C16.9 12.5 17 12.3 17 12C17 9.5 15.2 7.50001 12.8 7.10001L10.2 4.5L12.7 2C17.9 2.4 22 6.7 22 12ZM11.2 16.9C8.80001 16.5 7 14.5 7 12C7 11.7 7.00001 11.5 7.10001 11.2L4.5 13.8L2 11.3C2 11.5 2 11.8 2 12C2 17.3 6.09999 21.6 11.3 22L13.8 19.5L11.2 16.9Z"
                                  fill="currentColor"></path>
                                        <path opacity="0.3"
                                  d="M22 12.7C21.6 17.9 17.3 22 12 22C11.8 22 11.5 22 11.3 22L13.8 19.5L11.2 16.9C11.5 16.9 11.7 17 12 17C14.5 17 16.5 15.2 16.9 12.8L19.5 10.2L22 12.7ZM10.2 4.5L12.7 2C12.5 2 12.2 2 12 2C6.7 2 2.4 6.1 2 11.3L4.5 13.8L7.10001 11.2C7.50001 8.8 9.5 7 12 7C12.3 7 12.5 7.00001 12.8 7.10001L10.2 4.5Z"
                                  fill="currentColor"></path>
                                      </svg>
                          </span> <!--end::Svg Icon-->
                          </span>
                        </div>
                        <!--end::Symbol-->
                        <!--begin::Stats-->
                        <div class="m-0">
                          <!--begin::Number-->
                          <span
                            class="text-black-500 fw-bold text-center d-block fs-1 lh-1 ls-n1 mb-1" id="dvTotalCancelled">&#8377;<s:property value="%{statistics.totalCancelled}" /></span>
                          <!--end::Number-->
                          <!--begin::Desc-->
                          <span class="text-gray-500 fw-semibold text-center ">Cancelled
                            Payments</span>
                          <!--end::Desc-->
                        </div>
                        <!--end::Stats-->
                      </div>
                      <!--end::Items-->
                    </div>
                    <!--end::Col-->
                    <!--begin::Col-->
                    <div class="col-lg-4 col-md-6 col-sm-12">
                      <!--begin::Items-->
                      <div
                        class="bg-gray-100 bg-opacity-70 rounded-2 px-5 py-5 d-flex flex-column align-items-center">
                        <!--begin::Symbol-->
                        <div class="symbol symbol-30px mb-8">
                          <span class="symbol-label"> <!--begin::Svg Icon | path: icons/duotune/general/gen013.svg-->
                            <span class="svg-icon svg-icon-1 svg-icon-primary">
                              <svg width="24" height="24" viewBox="0 0 24 24"
                                fill="none" xmlns="http://www.w3.org/2000/svg">
                                        <path
                                  d="M17.302 11.35L12.002 20.55H21.202C21.802 20.55 22.202 19.85 21.902 19.35L17.302 11.35Z"
                                  fill="currentColor"></path>
                                        <path opacity="0.3"
                                  d="M12.002 20.55H2.802C2.202 20.55 1.80202 19.85 2.10202 19.35L6.70203 11.45L12.002 20.55ZM11.302 3.45L6.70203 11.35H17.302L12.702 3.45C12.402 2.85 11.602 2.85 11.302 3.45Z"
                                  fill="currentColor"></path>
                                      </svg>
                          </span> <!--end::Svg Icon-->
                          </span>
                        </div>
                        <!--end::Symbol-->
                        <!--begin::Stats-->
                        <div class="m-0">
                          <!--begin::Number-->
                          <span
                            class="text-black-500 fw-bold text-center d-block fs-1 lh-1 ls-n1 mb-1" id="dvTotalFailed">&#8377;<s:property value="%{statistics.totalFailed}" /></span>
                          <!--end::Number-->
                          <!--begin::Desc-->
                          <span class="text-gray-500 fw-semibold text-center ">Failed
                            Payments</span>
                          <!--end::Desc-->
                        </div>
                        <!--end::Stats-->
                      </div>
                      <!--end::Items-->
                    </div>
                    <div class="col-lg-4 col-md-6 col-sm-12">
                      <!--begin::Items-->
                      <div
                        class="bg-gray-100 bg-opacity-70 rounded-2 px-5 py-5 d-flex flex-column align-items-center">
                        <!--begin::Symbol-->
                        <div class="symbol symbol-30px mb-8">
                          <span class="symbol-label"> <!--begin::Svg Icon | path: icons/duotune/general/gen013.svg-->
                            <span class="svg-icon svg-icon-1 svg-icon-primary">
                              <svg width="24" height="24" viewBox="0 0 24 24"
                                fill="none" xmlns="http://www.w3.org/2000/svg">
                                        <path opacity="0.3"
                                  d="M11 6.5C11 9 9 11 6.5 11C4 11 2 9 2 6.5C2 4 4 2 6.5 2C9 2 11 4 11 6.5ZM17.5 2C15 2 13 4 13 6.5C13 9 15 11 17.5 11C20 11 22 9 22 6.5C22 4 20 2 17.5 2ZM6.5 13C4 13 2 15 2 17.5C2 20 4 22 6.5 22C9 22 11 20 11 17.5C11 15 9 13 6.5 13ZM17.5 13C15 13 13 15 13 17.5C13 20 15 22 17.5 22C20 22 22 20 22 17.5C22 15 20 13 17.5 13Z"
                                  fill="currentColor"></path>
                                        <path
                                  d="M17.5 16C17.5 16 17.4 16 17.5 16L16.7 15.3C16.1 14.7 15.7 13.9 15.6 13.1C15.5 12.4 15.5 11.6 15.6 10.8C15.7 9.99999 16.1 9.19998 16.7 8.59998L17.4 7.90002H17.5C18.3 7.90002 19 7.20002 19 6.40002C19 5.60002 18.3 4.90002 17.5 4.90002C16.7 4.90002 16 5.60002 16 6.40002V6.5L15.3 7.20001C14.7 7.80001 13.9 8.19999 13.1 8.29999C12.4 8.39999 11.6 8.39999 10.8 8.29999C9.99999 8.19999 9.20001 7.80001 8.60001 7.20001L7.89999 6.5V6.40002C7.89999 5.60002 7.19999 4.90002 6.39999 4.90002C5.59999 4.90002 4.89999 5.60002 4.89999 6.40002C4.89999 7.20002 5.59999 7.90002 6.39999 7.90002H6.5L7.20001 8.59998C7.80001 9.19998 8.19999 9.99999 8.29999 10.8C8.39999 11.5 8.39999 12.3 8.29999 13.1C8.19999 13.9 7.80001 14.7 7.20001 15.3L6.5 16H6.39999C5.59999 16 4.89999 16.7 4.89999 17.5C4.89999 18.3 5.59999 19 6.39999 19C7.19999 19 7.89999 18.3 7.89999 17.5V17.4L8.60001 16.7C9.20001 16.1 9.99999 15.7 10.8 15.6C11.5 15.5 12.3 15.5 13.1 15.6C13.9 15.7 14.7 16.1 15.3 16.7L16 17.4V17.5C16 18.3 16.7 19 17.5 19C18.3 19 19 18.3 19 17.5C19 16.7 18.3 16 17.5 16Z"
                                  fill="currentColor"></path>
                                      </svg>
                          </span> <!--end::Svg Icon-->
                          </span>
                        </div>
                        <!--end::Symbol-->
                        <!--begin::Stats-->
                        <div class="m-0">
                          <!--begin::Number-->
                          <span
                            class="text-black-500 fw-bold text-center d-block fs-1 lh-1 ls-n1 mb-1" id="dvTotalFraud">&#8377;<s:property value="%{statistics.totalFraud}" /></span>
                          <!--end::Number-->
                          <!--begin::Desc-->
                          <span class="text-gray-500 fw-semibold text-center ">Fraud
                            Payments</span>
                          <!--end::Desc-->
                        </div>
                        <!--end::Stats-->
                      </div>
                      <!--end::Items-->
                    </div>
                    <!--end::Col-->
                  </div>
                  <!--end::Row-->
                </div>
                <!--end::Stats-->
              </div>
              <!--end::Body-->
            </div>
          </div>
          <!--begin::Col-->

        <div class="col-xl-6">
            <!--begin::Chart widget 24-->
            <div class="card card-flush h-md-100">
              <!--begin::Header-->
              <div class="card-header">
                <!--begin::Title-->
                <h3 class="card-title align-items-start flex-column">
                  <span class="card-label fw-bold text-dark">Hits V/S
                    Captured</span>
                </h3>
                <!--end::Title-->
              </div>
              <!--end::Header-->
              <!--begin::Body-->
              <div class="card-body ">
                <div class="row align-items-center">
                  <div class="col-lg-2 col-md-12 col-sm-12 my-3">
                    <span class="p-2">Date</span>
                  </div>
                  <div class="col-lg-5 col-md-6 col-sm-12 my-3">
                    <div class="position-relative d-flex align-items-center">
                      <span class="svg-icon svg-icon-2 position-absolute mx-4">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none"
                          xmlns="http://www.w3.org/2000/svg">
                                  <path opacity="0.3"
                            d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
                            fill="currentColor" />
                                  <path
                            d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
                            fill="currentColor" />
                                  <path
                            d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
                            fill="currentColor" />
                                </svg>
                      </span> <input class="form-control form-control-solid ps-12"
                        placeholder="From" name="dateFromFunnel" id="dateFromFunnel" />
                    </div>
                  </div>
                  <div class="col-lg-5 col-md-6 col-sm-12 my-3">
                    <div class="position-relative d-flex align-items-center">
                      <span class="svg-icon svg-icon-2 position-absolute mx-4">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none"
                          xmlns="http://www.w3.org/2000/svg">
                                  <path opacity="0.3"
                            d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
                            fill="currentColor" />
                                  <path
                            d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
                            fill="currentColor" />
                                  <path
                            d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
                            fill="currentColor" />
                                </svg>
                      </span> <input class="form-control form-control-solid ps-12"
                        placeholder="To" name="dateToFunnel" id="dateToFunnel" />
                    </div>
                  </div>
                </div>
                <div
                  class="row d-flex justify-content-around align-items-center">
                  <div class="my-3 col-lg-6 col-md-6 col-sm-12 filter-types">
                    <select id="transactionTypeFunnel" name="txntype" data-control="select2"
                      class="form-select form-select-solid" data-hide-search="true">
                      <option value="">TXN Type</option>
                      <option value="SALE">SALE</option>
                      <option value="REFUND">REFUND</option>
                    </select>
                  </div>


                  <div class="my-3 col-lg-6 col-md-6 col-sm-12 filter-types">
                    <s:select data-control="select2"
                          class="form-select form-select-solid" headerValue="Payment Type" headerKey="ALL"
                          data-hide-search="true" list="@com.pay10.commons.util.PaymentType@values()" listValue="name" listKey="code"
                          name="paymentMethod" id="paymentMethodsFunnel" onchange="getMopType(this.value,'mopTypeFunnel')">
                        </s:select>
                  </div>
                  <div class="my-3 col-lg-6 col-md-6 col-sm-12 filter-types">
                    <select id="mopTypeFunnel" name="mopType" data-control="select2"
                    class="form-select form-select-solid" data-hide-search="true" >
                    </select>
                  </div>

                  <div
                    class="my-3 col-lg-6 col-md-6 col-sm-12 filter-types d-flex align-items-center justify-content-lg-end justify-content-md-center">
                    <button class="btn btn-primary rounded w-100 funnelDataGet" onclick="getHitsData();">View</button>
                  </div>
                </div>
                <!--begin::Chart-->
                <div id="kt_charts_widget_42" class="min-h-auto w-100 ps-4 pe-6"
                  style="height: 500px"></div>
                <!--end::Chart-->
              </div>
              <!--end: Card Body-->
            </div>
            <!--end::Chart widget 24-->
          </div>
                  <!--end::Col-->
        </div>
        <!--end::Row-->
        <!--begin::Row-->
        <div class="row gx-5 gx-xl-10 mb-5 mb-xl-10">
          <div class="col-lg-6">
            <!--begin::Engage widget 10-->
            <div class="card card-flush h-xl-100">
              <!--begin::Header-->
              <div class="card-header pt-7">
                <!--begin::Title-->
                <h4 class="card-title align-items-start flex-column">
                  <span class="card-label fw-bold text-gray-800">Sales
                    Report</span> <span class="text-gray-400 mt-1 fw-semibold fs-7">Total
                    sales details</span>
                </h4>
                <!--end::Title-->
                <!--begin::Toolbar-->
                <div class="card-toolbar">
                    <input type="text" name="saleDetailsRange" id="saleDetailsRange" class="form-control form-control-solid saleDateRange" onchange="retrieveSalesDetails(this.value);" />
                </div>
                <!--end::Toolbar-->
              </div>
              <!--end::Header-->
              <!--begin::Body-->
              <div class="card-body py-3">
                <!--begin::Table container-->
                <div class="my-5">
                  <!--begin::Item-->
                  <div class="d-flex flex-stack">
                    <!--begin::Section-->
                    <div class="d-flex align-items-center me-5">
                      <!--begin::Symbol-->
                      <div class="symbol symbol-40px me-4">
                        <span class="symbol-label bg-light-info"> <!--begin::Svg Icon | path: icons/duotune/abstract/abs025.svg-->
                          <span class="svg-icon svg-icon-2x svg-icon-info"> <svg
                              width="24" height="24" viewBox="0 0 24 24" fill="none"
                              xmlns="http://www.w3.org/2000/svg">
                                      <path
                                d="M16.925 3.90078V8.00077L12.025 10.8008V5.10078L15.525 3.10078C16.125 2.80078 16.925 3.20078 16.925 3.90078ZM2.525 13.5008L6.025 15.5008L10.925 12.7008L6.025 9.90078L2.525 11.9008C1.825 12.3008 1.825 13.2008 2.525 13.5008ZM18.025 19.7008V15.6008L13.125 12.8008V18.5008L16.625 20.5008C17.225 20.8008 18.025 20.4008 18.025 19.7008Z"
                                fill="currentColor"></path>
                                      <path opacity="0.3"
                                d="M8.52499 3.10078L12.025 5.10078V10.8008L7.125 8.00077V3.90078C7.125 3.20078 7.92499 2.80078 8.52499 3.10078ZM7.42499 20.5008L10.925 18.5008V12.8008L6.02499 15.6008V19.7008C6.02499 20.4008 6.82499 20.8008 7.42499 20.5008ZM21.525 11.9008L18.025 9.90078L13.125 12.7008L18.025 15.5008L21.525 13.5008C22.225 13.2008 22.225 12.3008 21.525 11.9008Z"
                                fill="currentColor"></path>
                                    </svg>
                        </span> <!--end::Svg Icon-->
                        </span>
                      </div>
                      <!--end::Symbol-->
                      <!--begin::Content-->
                      <div class="me-5">
                        <!--begin::Title-->
                        <a href="#"
                          class="text-gray-800 fw-bold text-hover-primary fs-6">Total
                          Sales</a>
                        <!--end::Title-->
                        <!--begin::Desc-->
                        <span
                          class="text-gray-400 fw-semibold fs-7 d-block text-start ps-0">Total
                          sales amount received</span>
                        <!--end::Desc-->
                      </div>
                      <!--end::Content-->
                    </div>
                    <!--end::Section-->
                    <!--begin::Wrapper-->
                    <div class="text-gray-400 fw-bold fs-7 text-end">
                      <!--begin::Number-->
                      <span class="text-gray-800 fw-bold fs-6 d-block" id="totalSalesAmount">&#8377;2,000,000</span>
                      <!--end::Number-->
                      Received
                    </div>
                    <!--end::Wrapper-->
                  </div>
                  <!--end::Item-->
                  <!--begin::Separator-->
                  <div class="separator separator-dashed my-5"></div>
                  <!--end::Separator-->
                  <!--begin::Item-->
                  <div class="d-flex flex-stack">
                    <!--begin::Section-->
                    <div class="d-flex align-items-center me-5">
                      <!--begin::Symbol-->
                      <div class="symbol symbol-40px me-4">
                        <span class="symbol-label bg-light-success"> <!--begin::Svg Icon | path: icons/duotune/medicine/med005.svg-->
                          <span class="svg-icon svg-icon-2x svg-icon-success">
                            <svg width="24" height="24" viewBox="0 0 24 24"
                              fill="none" xmlns="http://www.w3.org/2000/svg">
                                      <path opacity="0.3"
                                d="M17.9061 13H11.2061C11.2061 12.4 10.8061 12 10.2061 12C9.60605 12 9.20605 12.4 9.20605 13H6.50606L9.20605 8.40002V4C8.60605 4 8.20605 3.6 8.20605 3C8.20605 2.4 8.60605 2 9.20605 2H15.2061C15.8061 2 16.2061 2.4 16.2061 3C16.2061 3.6 15.8061 4 15.2061 4V8.40002L17.9061 13ZM13.2061 9C12.6061 9 12.2061 9.4 12.2061 10C12.2061 10.6 12.6061 11 13.2061 11C13.8061 11 14.2061 10.6 14.2061 10C14.2061 9.4 13.8061 9 13.2061 9Z"
                                fill="currentColor"></path>
                                      <path
                                d="M18.9061 22H5.40605C3.60605 22 2.40606 20 3.30606 18.4L6.40605 13H9.10605C9.10605 13.6 9.50605 14 10.106 14C10.706 14 11.106 13.6 11.106 13H17.8061L20.9061 18.4C21.9061 20 20.8061 22 18.9061 22ZM14.2061 15C13.1061 15 12.2061 15.9 12.2061 17C12.2061 18.1 13.1061 19 14.2061 19C15.3061 19 16.2061 18.1 16.2061 17C16.2061 15.9 15.3061 15 14.2061 15Z"
                                fill="currentColor"></path>
                                    </svg>
                        </span> <!--end::Svg Icon-->
                        </span>
                      </div>
                      <!--end::Symbol-->
                      <!--begin::Content-->
                      <div class="me-5">
                        <!--begin::Title-->
                        <a href="#"
                          class="text-gray-800 fw-bold text-hover-primary fs-6">Total
                          Refunded</a>
                        <!--end::Title-->
                        <!--begin::Desc-->
                        <span
                          class="text-gray-400 fw-semibold fs-7 d-block text-start ps-0">Total
                          refunded amount</span>
                        <!--end::Desc-->
                      </div>
                      <!--end::Content-->
                    </div>
                    <!--end::Section-->
                    <!--begin::Wrapper-->
                    <div class="text-gray-400 fw-bold fs-7 text-end">
                      <!--begin::Number-->
                      <span class="text-gray-800 fw-bold fs-6 d-block" id="totalRefundedAmount">&#8377;4,600,000</span>
                      <!--end::Number-->
                      Paid
                    </div>
                    <!--end::Wrapper-->
                  </div>
                  <!--end::Item-->
                  <!--begin::Separator-->
                  <div class="separator separator-dashed my-5"></div>
                  <!--end::Separator-->
                  <!--begin::Item-->
                  <div class="d-flex flex-stack">
                    <!--begin::Section-->
                    <div class="d-flex align-items-center me-5">
                      <!--begin::Symbol-->
                      <div class="symbol symbol-40px me-4">
                        <span class="symbol-label bg-light-danger"> <!--begin::Svg Icon | path: icons/duotune/abstract/abs036.svg-->
                          <span class="svg-icon svg-icon-2x svg-icon-danger">
                            <svg width="24" height="24" viewBox="0 0 24 24"
                              fill="none" xmlns="http://www.w3.org/2000/svg">
                                      <path
                                d="M22 12C22 12.2 22 12.5 22 12.7L19.5 10.2L16.9 12.8C16.9 12.5 17 12.3 17 12C17 9.5 15.2 7.50001 12.8 7.10001L10.2 4.5L12.7 2C17.9 2.4 22 6.7 22 12ZM11.2 16.9C8.80001 16.5 7 14.5 7 12C7 11.7 7.00001 11.5 7.10001 11.2L4.5 13.8L2 11.3C2 11.5 2 11.8 2 12C2 17.3 6.09999 21.6 11.3 22L13.8 19.5L11.2 16.9Z"
                                fill="currentColor"></path>
                                      <path opacity="0.3"
                                d="M22 12.7C21.6 17.9 17.3 22 12 22C11.8 22 11.5 22 11.3 22L13.8 19.5L11.2 16.9C11.5 16.9 11.7 17 12 17C14.5 17 16.5 15.2 16.9 12.8L19.5 10.2L22 12.7ZM10.2 4.5L12.7 2C12.5 2 12.2 2 12 2C6.7 2 2.4 6.1 2 11.3L4.5 13.8L7.10001 11.2C7.50001 8.8 9.5 7 12 7C12.3 7 12.5 7.00001 12.8 7.10001L10.2 4.5Z"
                                fill="currentColor"></path>
                                    </svg>
                        </span> <!--end::Svg Icon-->
                        </span>
                      </div>
                      <!--end::Symbol-->
                      <!--begin::Content-->
                      <div class="me-5">
                        <!--begin::Title-->
                        <a href="#"
                          class="text-gray-800 fw-bold text-hover-primary fs-6">Settled
                          Amount</a>
                        <!--end::Title-->
                        <!--begin::Desc-->
                        <span
                          class="text-gray-400 fw-semibold fs-7 d-block text-start ps-0">Total
                          amount settled</span>
                        <!--end::Desc-->
                      </div>
                      <!--end::Content-->
                    </div>
                    <!--end::Section-->
                    <!--begin::Wrapper-->
                    <div class="text-gray-400 fw-bold fs-7 text-end">
                      <!--begin::Number-->
                      <span class="text-gray-800 fw-bold fs-6 d-block" id="totalSettledAmount">&#8377;560,000</span>
                      <!--end::Number-->
                      Paid
                    </div>
                    <!--end::Wrapper-->
                  </div>
                  <!--end::Item-->
                  <!--begin::Separator-->
                  <div class="separator separator-dashed my-5"></div>
                  <!--end::Separator-->
                  <!--begin::Item-->
                  <div class="d-flex flex-stack">
                    <!--begin::Section-->
                    <div class="d-flex align-items-center me-5">
                      <!--begin::Symbol-->
                      <div class="symbol symbol-40px me-4">
                        <span class="symbol-label bg-light-primary"> <!--begin::Svg Icon | path: icons/duotune/abstract/abs020.svg-->
                          <span class="svg-icon svg-icon-2x svg-icon-primary">
                            <svg width="24" height="24" viewBox="0 0 24 24"
                              fill="none" xmlns="http://www.w3.org/2000/svg">
                                      <path
                                d="M17.302 11.35L12.002 20.55H21.202C21.802 20.55 22.202 19.85 21.902 19.35L17.302 11.35Z"
                                fill="currentColor"></path>
                                      <path opacity="0.3"
                                d="M12.002 20.55H2.802C2.202 20.55 1.80202 19.85 2.10202 19.35L6.70203 11.45L12.002 20.55ZM11.302 3.45L6.70203 11.35H17.302L12.702 3.45C12.402 2.85 11.602 2.85 11.302 3.45Z"
                                fill="currentColor"></path>
                                    </svg>
                        </span> <!--end::Svg Icon-->
                        </span>
                      </div>
                      <!--end::Symbol-->
                      <!--begin::Content-->
                      <div class="me-5">
                        <!--begin::Title-->
                        <a href="#"
                          class="text-gray-800 fw-bold text-hover-primary fs-6">Total
                          Failed</a>
                        <!--end::Title-->
                        <!--begin::Desc-->
                        <span
                          class="text-gray-400 fw-semibold fs-7 d-block text-start ps-0">Failed
                          transaction amount</span>
                        <!--end::Desc-->
                      </div>
                      <!--end::Content-->
                    </div>
                    <!--end::Section-->
                    <!--begin::Wrapper-->
                    <div class="text-gray-400 fw-bold fs-7 text-end">
                      <!--begin::Number-->
                      <span class="text-gray-800 fw-bold fs-6 d-block" id="totalFailedAmount">&#8377;<s:property value="%{statistics.totalFailed}" /></span>
                      <!--end::Number-->
                      Failed Payment
                    </div>
                    <!--end::Wrapper-->
                  </div>
                  <div class="separator separator-dashed my-5"></div>
                  <div class="d-flex flex-stack">
                    <!--begin::Section-->
                    <div class="d-flex align-items-center me-5">
                      <!--begin::Symbol-->
                      <div class="symbol symbol-40px me-4">
                        <span class="symbol-label bg-light-warning"> <!--begin::Svg Icon | path: icons/duotune/technology/teh008.svg-->
                          <span class="svg-icon svg-icon-2x svg-icon-warning">
                            <svg width="24" height="24" viewBox="0 0 24 24"
                              fill="none" xmlns="http://www.w3.org/2000/svg">
                                      <path opacity="0.3"
                                d="M11 6.5C11 9 9 11 6.5 11C4 11 2 9 2 6.5C2 4 4 2 6.5 2C9 2 11 4 11 6.5ZM17.5 2C15 2 13 4 13 6.5C13 9 15 11 17.5 11C20 11 22 9 22 6.5C22 4 20 2 17.5 2ZM6.5 13C4 13 2 15 2 17.5C2 20 4 22 6.5 22C9 22 11 20 11 17.5C11 15 9 13 6.5 13ZM17.5 13C15 13 13 15 13 17.5C13 20 15 22 17.5 22C20 22 22 20 22 17.5C22 15 20 13 17.5 13Z"
                                fill="currentColor"></path>
                                      <path
                                d="M17.5 16C17.5 16 17.4 16 17.5 16L16.7 15.3C16.1 14.7 15.7 13.9 15.6 13.1C15.5 12.4 15.5 11.6 15.6 10.8C15.7 9.99999 16.1 9.19998 16.7 8.59998L17.4 7.90002H17.5C18.3 7.90002 19 7.20002 19 6.40002C19 5.60002 18.3 4.90002 17.5 4.90002C16.7 4.90002 16 5.60002 16 6.40002V6.5L15.3 7.20001C14.7 7.80001 13.9 8.19999 13.1 8.29999C12.4 8.39999 11.6 8.39999 10.8 8.29999C9.99999 8.19999 9.20001 7.80001 8.60001 7.20001L7.89999 6.5V6.40002C7.89999 5.60002 7.19999 4.90002 6.39999 4.90002C5.59999 4.90002 4.89999 5.60002 4.89999 6.40002C4.89999 7.20002 5.59999 7.90002 6.39999 7.90002H6.5L7.20001 8.59998C7.80001 9.19998 8.19999 9.99999 8.29999 10.8C8.39999 11.5 8.39999 12.3 8.29999 13.1C8.19999 13.9 7.80001 14.7 7.20001 15.3L6.5 16H6.39999C5.59999 16 4.89999 16.7 4.89999 17.5C4.89999 18.3 5.59999 19 6.39999 19C7.19999 19 7.89999 18.3 7.89999 17.5V17.4L8.60001 16.7C9.20001 16.1 9.99999 15.7 10.8 15.6C11.5 15.5 12.3 15.5 13.1 15.6C13.9 15.7 14.7 16.1 15.3 16.7L16 17.4V17.5C16 18.3 16.7 19 17.5 19C18.3 19 19 18.3 19 17.5C19 16.7 18.3 16 17.5 16Z"
                                fill="currentColor"></path>
                                    </svg>
                        </span> <!--end::Svg Icon-->
                        </span>
                      </div>
                      <!--end::Symbol-->
                      <!--begin::Content-->
                      <div class="me-5">
                        <!--begin::Title-->
                        <a href="#"
                          class="text-gray-800 fw-bold text-hover-primary fs-6">Total
                          Cancelled</a>
                        <!--end::Title-->
                        <!--begin::Desc-->
                        <span
                          class="text-gray-400 fw-semibold fs-7 d-block text-start ps-0">Cancelled
                          transaction amount</span>
                        <!--end::Desc-->
                      </div>
                      <!--end::Content-->
                    </div>
                    <!--end::Section-->
                    <!--begin::Wrapper-->
                    <div class="text-gray-400 fw-bold fs-7 text-end">
                      <!--begin::Number-->
                      <span class="text-gray-800 fw-bold fs-6 d-block" id="totalCancelledAmount">&#8377;45,200,000</span>
                      <!--end::Number-->
                      Cancelled Payment
                    </div>
                    <!--end::Wrapper-->
                  </div>
                  <div class="separator separator-dashed my-5"></div>
                  <div class="d-flex flex-stack">
                    <!--begin::Section-->
                    <div class="d-flex align-items-center me-5">
                      <!--begin::Symbol-->
                      <div class="symbol symbol-40px me-4">
                        <span class="symbol-label"> <span
                          class="svg-icon svg-icon-2x svg-icon-info"> <svg
                              width="24" height="24" viewBox="0 0 24 24" fill="none"
                              xmlns="http://www.w3.org/2000/svg">
                                      <path
                                d="M16.925 3.90078V8.00077L12.025 10.8008V5.10078L15.525 3.10078C16.125 2.80078 16.925 3.20078 16.925 3.90078ZM2.525 13.5008L6.025 15.5008L10.925 12.7008L6.025 9.90078L2.525 11.9008C1.825 12.3008 1.825 13.2008 2.525 13.5008ZM18.025 19.7008V15.6008L13.125 12.8008V18.5008L16.625 20.5008C17.225 20.8008 18.025 20.4008 18.025 19.7008Z"
                                fill="currentColor"></path>
                                      <path opacity="0.3"
                                d="M8.52499 3.10078L12.025 5.10078V10.8008L7.125 8.00077V3.90078C7.125 3.20078 7.92499 2.80078 8.52499 3.10078ZM7.42499 20.5008L10.925 18.5008V12.8008L6.02499 15.6008V19.7008C6.02499 20.4008 6.82499 20.8008 7.42499 20.5008ZM21.525 11.9008L18.025 9.90078L13.125 12.7008L18.025 15.5008L21.525 13.5008C22.225 13.2008 22.225 12.3008 21.525 11.9008Z"
                                fill="currentColor"></path>
                                    </svg>
                        </span>
                        </span>
                      </div>
                      <!--end::Symbol-->
                      <!--begin::Content-->
                      <div class="me-5">
                        <!--begin::Title-->
                        <a href="#"
                          class="text-gray-800 fw-bold text-hover-primary fs-6">Total
                          Fraud</a>
                        <!--end::Title-->
                        <!--begin::Desc-->
                        <span
                          class="text-gray-400 fw-semibold fs-7 d-block text-start ps-0">Fraud
                          payments</span>
                        <!--end::Desc-->
                      </div>
                      <!--end::Content-->
                    </div>
                    <!--end::Section-->
                    <!--begin::Wrapper-->
                    <div class="text-gray-400 fw-bold fs-7 text-end">
                      <!--begin::Number-->
                      <span class="text-gray-800 fw-bold fs-6 d-block" id="totalFraudAmount">&#8377;0</span>
                      <!--end::Number-->
                      Fraud Payment
                    </div>
                    <!--end::Wrapper-->
                  </div>
                  <!--end::Item-->

                </div>
                <!--end::Table container-->
              </div>
              <!--begin::Body-->
            </div>
            <!--end::Engage widget 10-->
          </div>
          <!--end::Col-->
          <!--begin::Col-->
          <div class="col-lg-6">
            <!--begin::Tables widget 16-->
            <div class="card card-flush h-xl-100">
              <!--begin::Header-->
              <div class="card-header pt-5">
                <!--begin::Title-->
                <h3 class="card-title align-items-start flex-column">
                  <span class="card-label fw-bold text-gray-800">Monthly
                    Transactions</span> <span class="text-gray-400 mt-1 fw-semibold fs-6">Monthly
                    transaction analysis</span>
                </h3>
                <!--end::Title-->

              </div>
              <!--end::Header-->
              <!--begin::Body-->
              <div class="card-body">
                <div class="row trans-filter justify-content-between">
                  <div class="col-lg-12 col-md-12 col-sm-12 filter-dates">
                    <div class="row align-items-center my-5"
                      style="background-color: #fdf8f8;">
                      <div
                        class="col-lg-2 my-md-3 my-lg-0 my-sm-5 my-5  px-5 col-md-2 col-sm-12">
                        <p class="m-0">Date</p>
                      </div>
                      <div
                        class="col-lg-5 my-md-3 my-lg-0 my-sm-5 my-5 col-md-5 col-sm-12">
                        <div class="position-relative d-flex align-items-center">
                          <span class="svg-icon svg-icon-2 position-absolute mx-4">
                            <svg width="24" height="24" viewBox="0 0 24 24"
                              fill="none" xmlns="http://www.w3.org/2000/svg">
                                      <path opacity="0.3"
                                d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
                                fill="currentColor" />
                                      <path
                                d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
                                fill="currentColor" />
                                      <path
                                d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
                                fill="currentColor" />
                                    </svg>
                          </span> <input class=" form-control form-control-solid ps-12"
                            placeholder="From" name="dateFromMonth" id="dateFromMonth" />
                        </div>
                      </div>
                      <div
                        class="col-lg-5 my-md-3 my-lg-0 my-sm-5 my-5 col-md-5 col-sm-12">
                        <div class="position-relative d-flex align-items-center">
                          <span class="svg-icon svg-icon-2 position-absolute mx-4">
                            <svg width="24" height="24" viewBox="0 0 24 24"
                              fill="none" xmlns="http://www.w3.org/2000/svg">
                                      <path opacity="0.3"
                                d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
                                fill="currentColor" />
                                      <path
                                d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
                                fill="currentColor" />
                                      <path
                                d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
                                fill="currentColor" />
                                    </svg>
                          </span> <input class="form-control form-control-solid ps-12"
                            placeholder="To" name="dateToMonth" id="dateToMonth" />
                        </div>
                      </div>
                    </div>
                    <div class="row align-items-center my-5"
                      style="background-color: #fdf8f8;">
                      <div
                        class="my-md-3 my-lg-0 my-sm-5 my-5 col-lg-6 col-md-6 col-sm-12">
                        <select name="transactionType" id="transactionTypeMonthly" data-control="select2"
                          class="form-select form-select-solid"
                          data-hide-search="true">
                          <option value="">TXN Type</option>
                          <option value="SALE">SALE</option>
                          <option value="REFUND">REFUND</option>
                        </select>
                      </div>
                      <div class="my-md-3 my-lg-0 my-sm-5 my-5 col-lg-6 col-md-6 col-sm-12">
                        <s:select data-control="select2"
                          class="form-select form-select-solid" headerValue="Payment Type" headerKey="ALL"
                          data-hide-search="true" list="@com.pay10.commons.util.PaymentType@values()" listValue="name" listKey="code"
                          name="paymentMethod" id="paymentMethods" onchange="getMopType(this.value,'mopType')">
                        </s:select>
                      </div>
                    </div>
                    <div class="row align-items-center my-5"
                      style="background-color: #fdf8f8;">
                      <div
                        class="my-md-3 my-lg-0 my-sm-5 my-5 col-lg-6 col-md-6 col-sm-12">
                        <select id="mopType" name="mopType" data-control="select2"
                        class="form-select form-select-solid" data-hide-search="true" >
                          </select>
                      </div>
                      <div
                        class="my-md-3 my-lg-0 my-sm-5 my-5 col-lg-6 col-md-6 col-sm-12 pe-0">
                        <button class="btn btn-primary rounded w-100 monthlyGet" onclick="getCustomMonthData();">View</button>
                      </div>
                    </div>
                  </div>
                </div>
                <!--begin::Chart-->
                <div id="kt_charts_widget_3_chart" style="height: 350px"></div>
                <!--end::Chart-->
              </div>
              <!--end: Card Body-->
            </div>
            <!--end::Tables widget 16-->
          </div>
          <!--end::Col-->
        </div>
        <!--end::Row-->
        <!--begin::Row-->
        <div class="row g-5 g-xl-10 mb-5 mb-xl-10">
          <!--begin::Col-->
          <div class="col-xl-6">
            <!--begin::Chart Widget 35-->
            <div class="card card-flush h-md-100">
              <!--begin::Header-->
              <div class="card-header pt-5 mb-6">
                <!--begin::Title-->
                <h3 class="card-title align-items-start flex-column">
                  <span class="card-label fw-bold text-gray-800">Payment
                    Type Comparison</span>
                </h3>
              </div>
              <!--end::Header-->
              <!--begin::Body-->
              <div class="card-body pt-5">
                <!--begin::Chart container-->
                <div class="row align-items-center">
                  <div class="col-md-12 col-sm-12">
                    <span class="p-2">Date</span>
                  </div>
                  <div class="col-md-12 my-5 col-sm-12">
                    <div class="position-relative d-flex align-items-center">
                      <span class="svg-icon svg-icon-2 position-absolute mx-4">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none"
                          xmlns="http://www.w3.org/2000/svg">
                                  <path opacity="0.3"
                            d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
                            fill="currentColor" />
                                  <path
                            d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
                            fill="currentColor" />
                                  <path
                            d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
                            fill="currentColor" />
                                </svg>
                      </span> <input class="form-control form-control-solid ps-12"
                        placeholder="From" name="dateFromPie" id="dateFromPie" />
                    </div>
                  </div>
                  <div class="col-md-12 my-5 col-sm-12">
                    <div class="position-relative d-flex align-items-center">
                      <span class="svg-icon svg-icon-2 position-absolute mx-4">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none"
                          xmlns="http://www.w3.org/2000/svg">
                                  <path opacity="0.3"
                            d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
                            fill="currentColor" />
                                  <path
                            d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
                            fill="currentColor" />
                                  <path
                            d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
                            fill="currentColor" />
                                </svg>
                      </span> <input class="form-control form-control-solid ps-12"
                        placeholder="To" name="dateToPie" id="dateToPie" />
                    </div>
                  </div>
                </div>
                <div class="row my-5">
                  <div class="my-3 col-md-12 col-sm-12 filter-types">
                    <select name="txntype" data-control="select2" id="transactionTypePayment"
                      class="form-select form-select-solid" data-hide-search="true">
                      <option value="">TXN Type</option>
                      <option value="SALE" selected>SALE</option>
                      <option value="REFUND">REFUND</option>
                    </select>
                  </div>
                  <div class="my-3 col-md-12 col-sm-12 filter-types">
                    <s:select data-control="select2"
                          class="form-select form-select-solid" headerValue="Payment Type" headerKey="ALL"
                          data-hide-search="true" list="@com.pay10.commons.util.PaymentType@values()" listValue="name" listKey="code"
                          name="paymentMethod" id="paymentMethodsPie" onchange="getMopType(this.value,'mopTypePie')">
                        </s:select>
                  </div>
                </div>
                <div class="row my-5">
                  <div class="my-3 col-md-6 col-sm-12 filter-types">
                    <select id="mopTypePie" name="mopType" data-control="select2"
                    class="form-select form-select-solid" data-hide-search="true" multiple="true">
                      </select>
                  </div>
                  <div
                    class="my-3 col-md-6 col-sm-12 filter-types d-flex align-items-center justify-content-lg-end justify-content-md-center">
                    <button class="btn btn-primary rounded w-100 paymentDataGet" onclick="getPaymentTypeData();">View</button>
                  </div>
                </div>
                <div
                  class="d-flex justify-content-between flex-column mx-auto mx-md-0 pt-3 pb-10">
                  <!--begin::Title-->
                  <!--end::Title-->
                  <!--begin::Chart-->
                  <div id="kt_chart_widgets_22_chart_1" class="mx-auto my-4"></div>
                  <!--end::Chart-->
                </div>
                <!--begin::Chart-->
                  <div id="kt_chart_widgets_22_chart_2" class="mx-auto my-4"></div>
                  <!--end::Chart-->
              </div>
              <!--end::Body-->
            </div>
            <!--end::Chart Widget 33-->
          </div>
          <div class="col-xl-6">
            <!--begin::Chart Widget 35-->
            <div class="card card-flush h-md-100">
              <!--begin::Header-->
              <div class="card-header pt-5 mb-6">
                <!--begin::Title-->
                <h3 class="card-title align-items-start flex-column">
                  <span class="card-label fw-bold text-gray-800">Acquirer Wise Payment
                    Type Comparison</span>
                </h3>
              </div>
              <!--end::Header-->
              <!--begin::Body-->
              <div class="card-body pt-5">
                <!--begin::Chart container-->
                <div class="row align-items-center">
                  <div class="col-md-12 col-sm-12">
                    <span class="p-2">Date</span>
                  </div>
                  <div class="col-md-12 my-5 col-sm-12">
                    <div class="position-relative d-flex align-items-center">
                      <span class="svg-icon svg-icon-2 position-absolute mx-4">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none"
                          xmlns="http://www.w3.org/2000/svg">
                                  <path opacity="0.3"
                            d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
                            fill="currentColor" />
                                  <path
                            d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
                            fill="currentColor" />
                                  <path
                            d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
                            fill="currentColor" />
                                </svg>
                      </span> <input class="form-control form-control-solid ps-12"
                        placeholder="From" name="dateFromAcquirerPie" id="dateFromAcquirerPie" />
                    </div>
                  </div>
                  <div class="col-md-12 my-5 col-sm-12">
                    <div class="position-relative d-flex align-items-center">
                      <span class="svg-icon svg-icon-2 position-absolute mx-4">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none"
                          xmlns="http://www.w3.org/2000/svg">
                                  <path opacity="0.3"
                            d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
                            fill="currentColor" />
                                  <path
                            d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
                            fill="currentColor" />
                                  <path
                            d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
                            fill="currentColor" />
                                </svg>
                      </span> <input class="form-control form-control-solid ps-12"
                        placeholder="To" name="dateToAcquirerPie" id="dateToAcquirerPie" />
                    </div>
                  </div>
                </div>
                <div class="row my-5">
                  <div class="my-3 col-md-6 col-sm-12 filter-types">
                    <s:select data-control="select2" name="acquirerPT" id="acquirerPT" headerValue="Acquirer"
                              headerKey="ALL" list="@com.pay10.commons.util.AcquirerTypeUI@values()"
                              listValue="name" listKey="code" class="form-select form-select-solid" value="acquirer" />

                  </div>
                  <div
                    class="my-3 col-md-6 col-sm-12 filter-types d-flex align-items-center justify-content-lg-end justify-content-md-center">
                    <button class="btn btn-primary rounded w-100 paymentDataGet" onclick="pieChartByAcquirers();">View</button>
                  </div>
                </div>
                <div
                  class="d-flex justify-content-between flex-column mx-auto mx-md-0 pt-3 pb-10">
                  <!--begin::Title-->
                  <!--end::Title-->
                  <!--begin::Chart-->
                  <div id="kt_chart_widgets_012_chart" class="mx-auto my-4"></div>
                  <!--end::Chart-->
                </div>
              </div>
              <!--end::Body-->
            </div>
            <!--end::Chart Widget 33-->
          </div>
          <!--end::Col-->
        </div>
        <div class="row g-5 g-xl-10 mb-5 mb-xl-10">
          <!--begin::Col-->
          <div class="col-xl-8">
            <!--begin::Tables widget 14-->
            <div class="card card-flush h-md-100">
              <!--begin::Header-->
              <div class="card-header py-5">
                <!--begin::Title-->
                <h3 class="card-title align-items-start flex-column">
                  <span class="card-label fw-bold text-dark">Hourly
                    Transactions</span>
                </h3>
                <!--end::Title-->
              </div>
              <!--end::Header-->
              <!--begin::Body-->
              <div class="card-body ">
                <div class="row align-items-center">
                  <div class="col-lg-2 col-md-12 col-sm-12 my-3">
                    <span class="p-2">Date</span>
                  </div>
                  <div class="col-lg-5 col-md-6 col-sm-12 my-3">
                    <div class="position-relative d-flex align-items-center">
                      <span class="svg-icon svg-icon-2 position-absolute mx-4">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none"
                          xmlns="http://www.w3.org/2000/svg">
                                  <path opacity="0.3"
                            d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
                            fill="currentColor" />
                                  <path
                            d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
                            fill="currentColor" />
                                  <path
                            d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
                            fill="currentColor" />
                                </svg>
                      </span> <input class="form-control form-control-solid ps-12"
                        placeholder="" name="selectDateHourly" id="selectDateHourly" />
                    </div>
                  </div>
                  <!--<div class="col-lg-5 col-md-6 col-sm-12 my-3">
                    <div class="position-relative d-flex align-items-center">
                      <span class="svg-icon svg-icon-2 position-absolute mx-4">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none"
                          xmlns="http://www.w3.org/2000/svg">
                                  <path opacity="0.3"
                            d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
                            fill="currentColor" />
                                  <path
                            d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
                            fill="currentColor" />
                                  <path
                            d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
                            fill="currentColor" />
                                </svg>
                      </span> <input class="form-control form-control-solid ps-12"
                        placeholder="To" name="dateto" id="hourly_to" />
                    </div>
                  </div>-->
                </div>

                <div
                  class="row d-flex justify-content-around align-items-center">
                  <div class="my-3 col-lg-6 col-md-6 col-sm-12 filter-types">
                    <select name="transactionType" id="transactionTypeHourly" data-control="select2"
                      class="form-select form-select-solid" data-hide-search="true">
                      <option value="">TXN Type</option>
                      <option value="SALE">SALE</option>
                      <option value="REFUND">REFUND</option>
                    </select>
                  </div>
                  <div class="my-3 col-lg-6 col-md-6 col-sm-12 filter-types">
                    <s:select data-control="select2"
                    class="form-select form-select-solid" headerValue="Payment Type" headerKey="ALL"
                    data-hide-search="true" list="@com.pay10.commons.util.PaymentType@values()" listValue="name" listKey="code"
                    name="paymentmethod" id="paymentMethodsHourly" onchange="getMopType(this.value,'mopTypeHourly')">
                  </s:select>
                  </div>
                  <div class="my-3 col-lg-6 col-md-6 col-sm-12 filter-types">
                    <select id="mopTypeHourly" name="mopType" data-control="select2"
                    class="form-select form-select-solid" data-hide-search="true" >
                      </select>
                  </div>
                  <div
                    class="my-3 col-lg-6 col-md-6 col-sm-12 filter-types d-flex align-items-center justify-content-lg-end justify-content-md-center">
                    <button class="btn btn-primary rounded w-100 todayDataGet" onclick="getHourlyData();">View</button>
                  </div>
                </div>
                <!--begin::Chart-->
                <div id="kt_charts_widget_40" class="min-h-auto w-100 ps-4 pe-6"
                  style="height: 300px"></div>
                <!--end::Chart-->
              </div>
              <!--end: Card Body-->
            </div>
            <!--end::Tables widget 14-->
          </div>
          <!--end::Col-->
        </div>
        <!--end::Row-->
        <!--begin::Row-->
        <div class="row gx-5 gx-xl-10">
          <!--begin::Col-->
          <div class="col-xl-6 mb-5">
            <!--begin::Chart widget 31-->
            <div class="card card-flush h-xl-100">
              <!--begin::Header-->
              <div class="card-header">
                <!--begin::Title-->
                <h3 class="card-title align-items-start flex-column">
                  <span class="card-label fw-bold text-gray-800">Settlement</span>
                </h3>
                <!--end::Toolbar-->
              </div>
              <!--end::Header-->
              <!--begin::Body-->
              <div class="card-body pt-5">
                <!--begin::Chart-->
                <div class="row align-items-center">
                  <div class="col-md-12 col-sm-12">
                    <span class="p-2">Date</span>
                  </div>
                  <div class="col-md-12 my-5 col-sm-12">
                    <div class="position-relative d-flex align-items-center">
                      <span class="svg-icon svg-icon-2 position-absolute mx-4">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none"
                          xmlns="http://www.w3.org/2000/svg">
                                  <path opacity="0.3"
                            d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
                            fill="currentColor" />
                                  <path
                            d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
                            fill="currentColor" />
                                  <path
                            d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
                            fill="currentColor" />
                                </svg>
                      </span> <input class="form-control form-control-solid ps-12"
                        placeholder="From" name="dateFromSettlement" id="dateFromSettlement" />
                    </div>
                  </div>
                  <div class="col-md-12 my-5 col-sm-12">
                    <div class="position-relative d-flex align-items-center">
                      <span class="svg-icon svg-icon-2 position-absolute mx-4">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none"
                          xmlns="http://www.w3.org/2000/svg">
                                  <path opacity="0.3"
                            d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
                            fill="currentColor" />
                                  <path
                            d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
                            fill="currentColor" />
                                  <path
                            d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
                            fill="currentColor" />
                                </svg>
                      </span> <input class="form-control form-control-solid ps-12"
                        placeholder="To" name="dateToSettlement" id="dateToSettlement" />
                    </div>
                  </div>
                </div>
                <div class="row my-5">
                  <div class="my-3 col-lg-12 col-md-6 col-sm-12 filter-types">
                    <s:select data-control="select2"
                          class="form-select form-select-solid" headerValue="Payment Type" headerKey="ALL"
                          data-hide-search="true" list="@com.pay10.commons.util.PaymentType@values()" listValue="name" listKey="code"
                          name="paymentMethod" id="paymentMethodsSettled">
                        </s:select>
                  </div>
                  <div
                    class="my-3 col-lg-12 col-md-6 col-sm-12 filter-types d-flex align-items-center justify-content-lg-end justify-content-md-center">
                    <button id="getDataButton" class="btn btn-primary rounded w-100 settledDataGet" onclick="getSettledData();">View</button>
                  </div>
                </div>
                <div id="kt_charts_widget_41_chart" class="w-100 h-400px"></div>
                <!--end::Chart-->
              </div>
              <!--end::Body-->
            </div>
            <!--end::Chart widget 31-->
          </div>
          <!--end::Col-->
          <!--begin::Col-->
          <!--<div class="col-lg-6 mb-5 hits-section hourly-trans-section">-->
                    <!--begin::Chart widget 8-->
                    <!--<div class="card card-flush h-xl-100">-->
                      <!--begin::Header-->
                    <!--  <div class="card card-xl-stretch mb-5 mb-xl-8">-->
                        <!--begin::Header-->
                     <!-- <div class="card-header border-0 pt-5">
                          <h3 class="card-title align-items-start flex-column">
                            <span class="card-label fw-bold fs-3 mb-1">Daily Sales Status</span>
                            <span class="text-muted fw-semibold fs-7">890,344 sales completed</span>
                          </h3>-->
                          <!--begin::Toolbar-->

                          <!--end::Toolbar-->
                       <!-- </div>-->
                        <!--end::Header-->
                        <!--begin::Body-->
                        <!--<div class="card-body">-->
                          <!--begin::Chart-->
                          <!--<div id="kt_charts_widget_2_chart" style="height: 350px;"></div>-->
                          <!--end::Chart-->
                       <!-- </div>-->
                        <!--end::Body-->
                     <!-- </div>-->
                      <!--end::Header-->
                      <!--begin::Body-->
                      <!--end::Body-->
                 <!--   </div>-->
                    <!--end::Chart widget 8-->
                 <!-- </div>-->
          <!--end::Col-->
        </div>
        <!--end::Row-->
      </div>
      <!--end::Container-->
    </div>
    <!--end::Post-->
  </div>
  <!--end::Content-->

  <!--begin::Global Javascript Bundle(used by all pages)-->
    <script src="../assets/plugins/global/plugins.bundle.js"></script>
    <script src="../assets/js/scripts.bundle.js"></script>
    <!--end::Global Javascript Bundle-->

  <!--begin::Vendors Javascript(used by this page)-->
    <script src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>

    <script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
    <!--end::Vendors Javascript-->

    <script src="../assets/amcharts/index.js"></script>
    <script src="../assets/amcharts/xy.js"></script>
    <script src="../assets/amcharts/percent.js"></script>
    <script src="../assets/amcharts/radar.js"></script>
    <script src="../assets/amcharts/themes/Animated.js"></script>
    <script src="../assets/amcharts/map.js"></script>
    <script src="../assets/amcharts/geodata/worldLow.js"></script>
    <script src="../assets/amcharts/geodata/continentsLow.js"></script>
    <script src="../assets/amcharts/geodata/usaLow.js"></script>
    <script src="../assets/amcharts/geodata/worldTimeZonesLow.js"></script>
    <script src="../assets/amcharts/geodata/worldTimeZoneAreasLow.js"></script>

  <!--begin::Custom Javascript(used by this page)-->
    <script src="../assets/js/widgets.bundle.js"></script>
    <script src="../assets/js/custom/widgets.js"></script>
    <script src="../assets/js/custom/apps/chat/chat.js"></script>
    <script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
    <script src="../assets/js/custom/utilities/modals/create-app.js"></script>
    <script src="../assets/js/custom/utilities/modals/users-search.js"></script>
    <!--end::Custom Javascript-->

  <script>
    $("#selectDateHourly").flatpickr({
      maxDate : new Date()
    });
    $("#dateFromPie").flatpickr({
      maxDate : new Date()
    });
    $("#dateToPie").flatpickr({
      maxDate : new Date()
    });
    $("#dateFromSettlement").flatpickr({
      maxDate : new Date()
    });
    $("#dateToSettlement").flatpickr({
      maxDate : new Date()
    });
    $("#dateFromFunnel").flatpickr({
      maxDate : new Date()
    });
    $("#dateToFunnel").flatpickr({
      maxDate : new Date()
    });
    $("#dateFromMonth").flatpickr({
      maxDate : new Date()
    });
    $("#dateToMonth").flatpickr({
      maxDate : new Date()
    });
    $("#saleDetailsRange").daterangepicker({
      maxDate : new Date()
    });
    $("#customRange").daterangepicker({
      maxDate : new Date()
    });
    $("#dateFromAcquirerPie").flatpickr({
      maxDate : new Date()
    });
    $("#dateToAcquirerPie").flatpickr({
      maxDate : new Date()
    });
    $("#kt_daterangepicker_1").daterangepicker();
    $(document).ready(function() {
    	$('#merchant').select2({
        width: "105%"
      });
      $('#acquirerPT').select2();
    });
  </script>
</body>
</html>