<%@page import="com.pay10.commons.util.SaltFactory" %>
    <%@page import="com.pay10.commons.user.User" %>
        <%@page import="com.pay10.commons.util.Currency" %>
            <%@page import="com.pay10.commons.util.Amount" %>
                <%@page import="com.pay10.commons.util.FieldType" %>
                    <%@page import="org.slf4j.Logger" %>
                        <%@page import="org.slf4j.LoggerFactory" %>
                            <%@page import="com.pay10.commons.util.Constants" %>
                                <%@page import="com.pay10.commons.util.PropertiesManager" %>
                                    <%@ page language="java" contentType="text/html; charset=ISO-8859-1"
                                        pageEncoding="ISO-8859-1" %>
                                        <%@taglib prefix="s" uri="/struts-tags" %>
                                            <!DOCTYPE html>
                                            <html>

                                            <head>
                                                <base href="" />
                                                <title>BPGATE</title>
                                                <meta charset="utf-8" />
                                                <meta name="viewport" content="width=device-width, initial-scale=1" />
                                                <link rel="shortcut icon" href="../assets/media/images/paylogo.svg" />
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


                                                <%-- <script src="../js/index.js"></script> --%>
                                                    <script src="../js/jquery.js"></script>
                                                    <script src="../js/jquery.min.js"></script>
                                                    <script src="../assets/plugins/global/plugins.bundle.js"></script>
                                                    <script src="../assets/js/scripts.bundle.js"></script>
                                                    <!--begin::Vendors Javascript(used by this page)-->
                                                    <script
                                                        src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
                                                    <script src="https://cdn.amcharts.com/lib/5/index.js"></script>
                                                    <script src="https://cdn.amcharts.com/lib/5/xy.js"></script>
                                                    <script src="https://cdn.amcharts.com/lib/5/percent.js"></script>
                                                    <script src="https://cdn.amcharts.com/lib/5/radar.js"></script>
                                                    <script
                                                        src="https://cdn.amcharts.com/lib/5/themes/Animated.js"></script>
                                                    <script src="https://cdn.amcharts.com/lib/5/map.js"></script>
                                                    <script
                                                        src="https://cdn.amcharts.com/lib/5/geodata/worldLow.js"></script>
                                                    <script
                                                        src="https://cdn.amcharts.com/lib/5/geodata/continentsLow.js"></script>
                                                    <script
                                                        src="https://cdn.amcharts.com/lib/5/geodata/usaLow.js"></script>
                                                    <script
                                                        src="https://cdn.amcharts.com/lib/5/geodata/worldTimeZonesLow.js"></script>
                                                    <script
                                                        src="https://cdn.amcharts.com/lib/5/geodata/worldTimeZoneAreasLow.js"></script>
                                                    <script
                                                        src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
                                                    <!--end::Vendors Javascript-->
                                                    <!--begin::Custom Javascript(used by this page)-->
                                                    <script src="../assets/js/widgets.bundle.js"></script>
                                                    <script src="../assets/js/custom/widgets.js"></script>
                                                    <script src="../assets/js/custom/apps/chat/chat.js"></script>
                                                    <script
                                                        src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
                                                    <script
                                                        src="../assets/js/custom/utilities/modals/create-app.js"></script>
                                                    <script
                                                        src="../assets/js/custom/utilities/modals/users-search.js"></script>
                                                    <!--end::Custom Javascript-->
                                                    <!--end::Javascript-->



                                                    <script type="text/javascript">

                                                        function sendDefaultCurrency() {
                                                            var token = document.getElementsByName("token")[0].value;
                                                            var dropDownOption = document.getElementById("defaultCurrency").options;
                                                            var dropDown = document.getElementById("defaultCurrency").options.selectedIndex;
                                                            var payId = '<s:property value="#session.USER.payId" />';
                                                            $
                                                                .ajax({
                                                                    url: 'setDefaultCurrency',
                                                                    type: 'post',
                                                                    data: {
                                                                        defaultCurrency: document
                                                                            .getElementById("defaultCurrency").value,
                                                                        token: token
                                                                    },
                                                                    success: function (data) {

                                                                        if (data == null) {
                                                                            Swal.fire({
                                                                                text: "Error updating default currency please try again later!!",
                                                                                icon: "error",
                                                                                buttonsStyling: !1,
                                                                                confirmButtonText: "Ok, got it!",
                                                                                customClass: {
                                                                                    confirmButton: "btn btn-primary"
                                                                                }
                                                                            });
                                                                        } else {
                                                                            Swal.fire({
                                                                                text: data.response,
                                                                                icon: "success",
                                                                                buttonsStyling: !1,
                                                                                confirmButtonText: "Ok, got it!",
                                                                                customClass: {
                                                                                    confirmButton: "btn btn-primary"
                                                                                }
                                                                            });
                                                                        }
                                                                    },
                                                                    error: function (data) {
                                                                        Swal.fire({
                                                                            text: "Error updating default currency please try again later!!",
                                                                            icon: "error",
                                                                            buttonsStyling: !1,
                                                                            confirmButtonText: "Ok, got it!",
                                                                            customClass: {
                                                                                confirmButton: "btn btn-primary"
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                        }
                                                        debugger
                                                       function showBankdetail(){
                                                        var fiatAllowed = "<s:property value='%{#session.USER.fiatAllowed}'/>";
                                                        var cryptoAllowed= "<s:property value='%{#session.USER.cryptoAllowed}'/>";

                                                        if(fiatAllowed=="false"){

                                                            document.getElementById("fietAllowed").style.display = "none";
                                                            document.getElementById("fietAllowedTable").style.display = "none";
                                                        }


                                                        if(cryptoAllowed=="false"){


                                                            document.getElementById("cryptoAllowed").style.display = "none";
                                                            document.getElementById("cryptoAllowedTable").style.display = "none";
                                                        }
                                                       }


	function loadBankDetails(){

			 var currencySelected=$("#currencyForBankDetails").val();
			 console.log("Bank Selected "+currencySelected);
			var payId= '<s:property value="#session.USER.payId" />';

			if(currencySelected==''){
					$("#bankName").text('');
					$("#achName").text('');
					$("#accountNumber").text('');
					$("#ifscCode").text('');

				return false;
			}
			var urls=new URL(window.location.href);
			var domain=urls.origin;
		  	$.ajax({
				type:'GET',
				url:domain+'/crmws/fiatDetails/getFiatDetails',
				data:{
		 			payId:payId,
					currency:currencySelected
				},
				success:function(response){
					if(response.length!=0){
					var details=response[0];
					//console.log(details.bankName);
					//console.log(details.accountHolderName);
					//console.log(details.accountNumber);
					//console.log(details.ifscCode);
				 	$("#bankName").text(details.bankName);
					$("#achName").text(details.accountHolderName);
					$("#accountNumber").text(details.accountNumber);
					$("#ifscCode").text(details.ifscCode);

					 console.log("Bank Details");
					console.log(response);
					}
                    else{
                        $("#bankName").text('');
					$("#achName").text('');
					$("#accountNumber").text('');
					$("#ifscCode").text('');

                    }
				 	},
				error:function(response){
					errorResponse();
			 		console.log(response);
				}
			});

		}
		function errorResponse(){
			Swal.fire({
  					 	icon: "error",
 						title: "Failed",
						text:"Somthing Went Wrong!",
 						showConfirmButton: false,
 						timer: 1500
					});
		}


		function changeCurrrency(){
			console.log("working");
			loadBankDetails();
		}


                                                    </script>
                                                    <style type="text/css">
                                                        .error-text {
                                                            color: #a94442;
                                                            font-weight: bold;
                                                            background-color: #f2dede;
                                                            list-style-type: none;
                                                            text-align: center;
                                                            list-style-type: none;
                                                            margin-top: 10px;
                                                        }

                                                        .error-text li {
                                                            list-style-type: none;
                                                        }

                                                        #response {
                                                            color: green;
                                                        }

                                                        table.product-specbigstripes .borderbtmleftradius {
                                                            border-bottom-left-radius: 0px !important;
                                                        }

                                                        table.product-specbigstripes .borderbtmrightradius {
                                                            border-bottom-right-radius: 0px !important;
                                                        }

                                                        .mopDetailsTableRow{
                                                            border-bottom: 1px solid;
                                                        }



                                                        /*Table Style**/

                                                     </style>
                                            </head>

                                            <body id="kt_body"
                                                class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed toolbar-tablet-and-mobile-fixed aside-enabled aside-fixed"
                                                style="--kt-toolbar-height:55px;--kt-toolbar-height-tablet-and-mobile:55px">
                                                <div class="error-text">
                                                    <s:actionmessage />
                                                </div>
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
                                                                <h1
                                                                    class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
                                                                    My Profile</h1>
                                                                <!--end::Title-->
                                                                <!--begin::Separator-->
                                                                <span
                                                                    class="h-20px border-gray-200 border-start mx-4"></span>
                                                                <!--end::Separator-->
                                                                <!--begin::Breadcrumb-->
                                                                <ul
                                                                    class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
                                                                    <!--begin::Item-->
                                                                    <li class="breadcrumb-item text-muted">
                                                                        <a href="home"
                                                                            class="text-muted text-hover-primary">Dashboard</a>
                                                                    </li>
                                                                    <!--end::Item-->
                                                                    <!--begin::Item-->
                                                                    <li class="breadcrumb-item">
                                                                        <span
                                                                            class="bullet bg-gray-200 w-5px h-2px"></span>
                                                                    </li>
                                                                    <!--end::Item-->
                                                                    <!--begin::Item-->
                                                                    <li class="breadcrumb-item text-dark">My Profile
                                                                    </li>
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

                                                        <s:token />

                                                        <!--begin::Container-->
                                                        <div id="kt_content_container" class="container-xxl">
                                                            <div class="card mb-5 mb-xxl-8">
                                                                <div class="card-body pt-9 pb-0">
                                                                    <!--begin::Details-->
                                                                    <div class="d-flex flex-wrap flex-sm-nowrap">
                                                                        <!--begin: Pic-->
                                                                        <div class="me-7 mb-4" style="display: none;">
                                                                            <div
                                                                                class="symbol symbol-100px symbol-lg-160px symbol-fixed position-relative">
                                                                                <img src="../assets/media/avatars/300-1.jpg"
                                                                                    alt="image" />
                                                                                <!-- <div class="position-absolute translate-middle bottom-0 start-100 mb-6 bg-success rounded-circle border border-4 border-body h-20px w-20px"></div> -->
                                                                            </div>
                                                                        </div>
                                                                        <!--end::Pic-->
                                                                        <!--begin::Info-->
                                                                        <div class="flex-grow-1">
                                                                            <!--begin::Title-->
                                                                            <div
                                                                                class="d-flex justify-content-between align-items-start flex-wrap mb-2">
                                                                                <!--begin::User-->
                                                                                <div class="d-flex flex-column">
                                                                                    <!--begin::Name-->
                                                                                    <div
                                                                                        class="d-flex align-items-center mb-2">
                                                                                        <a href="#"
                                                                                            class="text-gray-900 text-hover-primary fs-2 fw-bold me-1">
                                                                                            <s:property
                                                                                                value="#session.USER.firstName" />
                                                                                            &nbsp;
                                                                                            <s:property
                                                                                                value="#session.USER.lastName" />
                                                                                        </a>
                                                                                        <a href="#">
                                                                                            <!--begin::Svg Icon | path: icons/duotune/general/gen026.svg-->
                                                                                            <span
                                                                                                class="svg-icon svg-icon-1 svg-icon-primary">
                                                                                                <svg xmlns="http://www.w3.org/2000/svg"
                                                                                                    width="24px"
                                                                                                    height="24px"
                                                                                                    viewBox="0 0 24 24">
                                                                                                    <path
                                                                                                        d="M10.0813 3.7242C10.8849 2.16438 13.1151 2.16438 13.9187 3.7242V3.7242C14.4016 4.66147 15.4909 5.1127 16.4951 4.79139V4.79139C18.1663 4.25668 19.7433 5.83365 19.2086 7.50485V7.50485C18.8873 8.50905 19.3385 9.59842 20.2758 10.0813V10.0813C21.8356 10.8849 21.8356 13.1151 20.2758 13.9187V13.9187C19.3385 14.4016 18.8873 15.491 19.2086 16.4951V16.4951C19.7433 18.1663 18.1663 19.7433 16.4951 19.2086V19.2086C15.491 18.8873 14.4016 19.3385 13.9187 20.2758V20.2758C13.1151 21.8356 10.8849 21.8356 10.0813 20.2758V20.2758C9.59842 19.3385 8.50905 18.8873 7.50485 19.2086V19.2086C5.83365 19.7433 4.25668 18.1663 4.79139 16.4951V16.4951C5.1127 15.491 4.66147 14.4016 3.7242 13.9187V13.9187C2.16438 13.1151 2.16438 10.8849 3.7242 10.0813V10.0813C4.66147 9.59842 5.1127 8.50905 4.79139 7.50485V7.50485C4.25668 5.83365 5.83365 4.25668 7.50485 4.79139V4.79139C8.50905 5.1127 9.59842 4.66147 10.0813 3.7242V3.7242Z"
                                                                                                        fill="currentColor" />
                                                                                                    <path
                                                                                                        d="M14.8563 9.1903C15.0606 8.94984 15.3771 8.9385 15.6175 9.14289C15.858 9.34728 15.8229 9.66433 15.6185 9.9048L11.863 14.6558C11.6554 14.9001 11.2876 14.9258 11.048 14.7128L8.47656 12.4271C8.24068 12.2174 8.21944 11.8563 8.42911 11.6204C8.63877 11.3845 8.99996 11.3633 9.23583 11.5729L11.3706 13.4705L14.8563 9.1903Z"
                                                                                                        fill="white" />
                                                                                                </svg>
                                                                                            </span>
                                                                                            <!--end::Svg Icon-->
                                                                                        </a>
                                                                                    </div>
                                                                                    <!--end::Name-->
                                                                                    <!--begin::Info-->
                                                                                    <div
                                                                                        class="d-flex flex-wrap fw-semibold fs-6 mb-4 pe-2">
                                                                                        <a href="#"
                                                                                            class="d-flex align-items-center text-gray-400 text-hover-primary me-5 mb-2">
                                                                                            <!--begin::Svg Icon | path: icons/duotune/communication/com006.svg-->
                                                                                            <span
                                                                                                class="svg-icon svg-icon-4 me-1">
                                                                                                <svg width="18"
                                                                                                    height="18"
                                                                                                    viewBox="0 0 18 18"
                                                                                                    fill="none"
                                                                                                    xmlns="http://www.w3.org/2000/svg">
                                                                                                    <path opacity="0.3"
                                                                                                        d="M16.5 9C16.5 13.125 13.125 16.5 9 16.5C4.875 16.5 1.5 13.125 1.5 9C1.5 4.875 4.875 1.5 9 1.5C13.125 1.5 16.5 4.875 16.5 9Z"
                                                                                                        fill="currentColor" />
                                                                                                    <path
                                                                                                        d="M9 16.5C10.95 16.5 12.75 15.75 14.025 14.55C13.425 12.675 11.4 11.25 9 11.25C6.6 11.25 4.57499 12.675 3.97499 14.55C5.24999 15.75 7.05 16.5 9 16.5Z"
                                                                                                        fill="currentColor" />
                                                                                                    <rect x="7" y="6"
                                                                                                        width="4"
                                                                                                        height="4"
                                                                                                        rx="2"
                                                                                                        fill="currentColor" />
                                                                                                </svg>
                                                                                            </span>
                                                                                            <!--end::Svg Icon-->
                                                                                            <s:property
                                                                                                value="#session.USER.UserType.name()" />
                                                                                        </a>
                                                                                        <a href="#"
                                                                                            class="d-flex align-items-center text-gray-400 text-hover-primary me-5 mb-2">
                                                                                            <!--begin::Svg Icon | path: icons/duotune/general/gen018.svg-->
                                                                                            <span
                                                                                                class="svg-icon svg-icon-4 me-1">
                                                                                                <svg width="24"
                                                                                                    height="24"
                                                                                                    viewBox="0 0 24 24"
                                                                                                    fill="none"
                                                                                                    xmlns="http://www.w3.org/2000/svg">
                                                                                                    <path opacity="0.3"
                                                                                                        d="M18.0624 15.3453L13.1624 20.7453C12.5624 21.4453 11.5624 21.4453 10.9624 20.7453L6.06242 15.3453C4.56242 13.6453 3.76242 11.4453 4.06242 8.94534C4.56242 5.34534 7.46242 2.44534 11.0624 2.04534C15.8624 1.54534 19.9624 5.24534 19.9624 9.94534C20.0624 12.0453 19.2624 13.9453 18.0624 15.3453Z"
                                                                                                        fill="currentColor" />
                                                                                                    <path
                                                                                                        d="M12.0624 13.0453C13.7193 13.0453 15.0624 11.7022 15.0624 10.0453C15.0624 8.38849 13.7193 7.04535 12.0624 7.04535C10.4056 7.04535 9.06241 8.38849 9.06241 10.0453C9.06241 11.7022 10.4056 13.0453 12.0624 13.0453Z"
                                                                                                        fill="currentColor" />
                                                                                                </svg>
                                                                                            </span>
                                                                                            <!--end::Svg Icon-->
                                                                                            <s:property
                                                                                                value="#session.USER.state" />
                                                                                        </a>
                                                                                    </div>
                                                                                    <!--end::Info-->
                                                                                </div>
                                                                                <!--end::User-->
                                                                            </div>
                                                                            <!--end::Title-->
                                                                            <!--begin::Stats-->
                                                                            <div
                                                                                class="d-flex flex-wrap flex-stack flex-column justify-content-start align-items-start">
                                                                                <!--begin::Wrapper-->
                                                                                <%-- <div
                                                                                    class="d-flex flex-column flex-grow-1 pe-8">
                                                                                    <!--begin::Stats-->
                                                                                    <div class="d-flex flex-wrap">
                                                                                        <!--begin::Stat-->
                                                                                        <div
                                                                                            class="border border-gray-300 border-dashed rounded min-w-125px py-3 px-4 me-6 mb-3">
                                                                                            <!--begin::Number-->
                                                                                            <div
                                                                                                class="d-flex align-items-center">
                                                                                                <!--begin::Svg Icon | path: icons/duotune/arrows/arr066.svg-->
                                                                                                <span
                                                                                                    class="svg-icon svg-icon-3 svg-icon-success me-2">
                                                                                                    <svg width="24"
                                                                                                        height="24"
                                                                                                        viewBox="0 0 24 24"
                                                                                                        fill="none"
                                                                                                        xmlns="http://www.w3.org/2000/svg">
                                                                                                        <rect
                                                                                                            opacity="0.5"
                                                                                                            x="13" y="6"
                                                                                                            width="13"
                                                                                                            height="2"
                                                                                                            rx="1"
                                                                                                            transform="rotate(90 13 6)"
                                                                                                            fill="currentColor" />
                                                                                                        <path
                                                                                                            d="M12.5657 8.56569L16.75 12.75C17.1642 13.1642 17.8358 13.1642 18.25 12.75C18.6642 12.3358 18.6642 11.6642 18.25 11.25L12.7071 5.70711C12.3166 5.31658 11.6834 5.31658 11.2929 5.70711L5.75 11.25C5.33579 11.6642 5.33579 12.3358 5.75 12.75C6.16421 13.1642 6.83579 13.1642 7.25 12.75L11.4343 8.56569C11.7467 8.25327 12.2533 8.25327 12.5657 8.56569Z"
                                                                                                            fill="currentColor" />
                                                                                                    </svg>
                                                                                                </span>
                                                                                                <!--end::Svg Icon-->
                                                                                                <div class="fs-2 fw-bold"
                                                                                                    data-kt-countup="true"
                                                                                                    data-kt-countup-value="45000000"
                                                                                                    data-kt-countup-prefix="&#8377;">
                                                                                                    0</div>
                                                                                            </div>
                                                                                            <!--end::Number-->
                                                                                            <!--begin::Label-->
                                                                                            <div
                                                                                                class="fw-semibold fs-6 text-gray-400">
                                                                                                Earnings
                                                                                            </div>
                                                                                            <!--end::Label-->
                                                                                        </div>
                                                                                        <!--end::Stat-->
                                                                                    </div>
                                                                                    <!--end::Stats-->
                                                                            </div> --%>
                                                                            <!--end::Wrapper-->
                                                                            <!--begin::Progress-->
                                                                            <div
                                                                                class="d-flex align-items-center w-200px w-sm-300px flex-column mt-3">
                                                                                <!-- <div
                                                                                    class="d-flex justify-content-between w-100 mt-auto mb-2">
                                                                                    <span
                                                                                        class="fw-semibold fs-6 text-gray-400">Profile
                                                                                        Completion</span>
                                                                                    <span
                                                                                        class="fw-bold fs-6">50%</span>
                                                                                </div> -->
                                                                                <!-- <div
                                                                                    class="h-5px mx-3 w-100 bg-light mb-3">
                                                                                    <div class="bg-success rounded h-5px"
                                                                                        role="progressbar"
                                                                                        style="width: 50%;"
                                                                                        aria-valuenow="50"
                                                                                        aria-valuemin="0"
                                                                                        aria-valuemax="100"></div>
                                                                                </div> -->
                                                                            </div>
                                                                            <!--end::Progress-->
                                                                        </div>
                                                                        <!--end::Stats-->
                                                                    </div>
                                                                    <!--end::Info-->
                                                                </div>
                                                                <!--end::Details-->
                                                                <!--begin::Navs-->
                                                                <ul
                                                                    class="nav nav-stretch nav-line-tabs nav-line-tabs-2x border-transparent fs-5 fw-bold">
                                                                    <s:if
                                                                        test="%{#session.USER.UserType.name()=='SUBUSER'}">
                                                                        <li class="nav-item mt-2">
                                                                            <a class="nav-link text-active-primary ms-0 me-10 py-5 active"
                                                                                data-bs-toggle="tab"
                                                                                href="#personal_details">Personal
                                                                                Details</a>
                                                                        </li>
                                                                    </s:if>
                                                                    <s:else>
                                                                        <li class="nav-item mt-2">
                                                                            <a class="nav-link text-active-primary ms-0 me-10 py-5 active"
                                                                                data-bs-toggle="tab"
                                                                                href="#personal_details">Personal
                                                                                Details</a>
                                                                        </li>
                                                                        <!--begin::Nav item-->
                                                                        <li class="nav-item mt-2">
                                                                            <a class="nav-link text-active-primary ms-0 me-10 py-5"
                                                                                data-bs-toggle="tab"
                                                                                href="#contact_details">Contact
                                                                                Details</a>
                                                                        </li>
                                                                        <!--end::Nav item-->
                                                                        <!--begin::Nav item-->
                                                                        <li class="nav-item mt-2">
                                                                            <a class="nav-link text-active-primary ms-0 me-10 py-5"
                                                                                data-bs-toggle="tab"
                                                                                href="#bank_details" onclick="showBankdetail()">Fiat Details</a>
                                                                        </li>
                                                                        <!--end::Nav item-->
                                                                        <!--begin::Nav item-->
                                                                        <li class="nav-item mt-2">
                                                                            <a class="nav-link text-active-primary ms-0 me-10 py-5"
                                                                                data-bs-toggle="tab"
                                                                                href="#business_details">Business
                                                                                Details</a>
                                                                        </li>
                                                                        <!--end::Nav item-->
                                                                        <s:if
                                                                            test="%{#session.USER.UserType.name()=='MERCHANT'}">
                                                                            <!--begin::Nav item-->
                                                                            <li class="nav-item mt-2">
                                                                                <a class="nav-link text-active-primary ms-0 me-10 py-5"
                                                                                    data-bs-toggle="tab"
                                                                                    href="#integration">Integration</a>
                                                                            </li>

                                                                              <li class="nav-item mt-2">
                                                                            <a class="nav-link text-active-primary ms-0 me-10 py-5"
                                                                                data-bs-toggle="tab"
                                                                                href="#services">Services
                                                                                </a>
                                                                        </li>
                                                                            <!--end::Nav item-->
                                                                        </s:if>


                                                                    </s:else>

                                                                </ul>
                                                                <!--begin::Navs-->
                                                            </div>
                                                        </div>

                                                        <div class="row g-5 g-xxl-8">
                                                            <!--begin::Col-->
                                                            <div class="col-xl-12">
                                                                <!--begin::Feeds Widget 1-->
                                                                <div class="card mb-5 mb-xxl-8">
                                                                    <!--begin::Body-->
                                                                    <div class="card-body pb-0">
                                                                        <div class="tab-content" id="myTabContent">


                                                                            <!-- start for submerchant -->
                                                                            <s:if
                                                                                test="%{#session.USER.UserType.name()=='SUBUSER'}">
                                                                                <div class="tab-pane fade show active"
                                                                                    id="personal_details"
                                                                                    role="tabpanel">
                                                                                    <div class="table-responsive">
                                                                                        <!--begin::Table-->
                                                                                        <table
                                                                                            class="table align-middle gs-0 gy-4">

                                                                                            <!--begin::Table body-->
                                                                                            <tbody>
                                                                                                <tr>
                                                                                                    <td class="w-200px">
                                                                                                        <a
                                                                                                            class="text-gray-400 fw-bold mb-1 fs-6">Business
                                                                                                            Name</a>
                                                                                                    </td>
                                                                                                    <td class="pe-0">
                                                                                                        <div
                                                                                                            class="d-flex">
                                                                                                            <span
                                                                                                                class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                                <s:property
                                                                                                                    value="#session.USER.businessName" />
                                                                                                            </span>
                                                                                                        </div>
                                                                                                    </td>
                                                                                                </tr>
                                                                                                <tr>
                                                                                                    <td class="w-200px">
                                                                                                        <a
                                                                                                            class="text-gray-400 fw-bold mb-1 fs-6">Email
                                                                                                            ID</a>
                                                                                                    </td>
                                                                                                    <td class="pe-0">
                                                                                                        <div
                                                                                                            class="d-flex">
                                                                                                            <span
                                                                                                                class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                                <s:property
                                                                                                                    value="#session.USER.emailId" />
                                                                                                            </span>
                                                                                                        </div>
                                                                                                    </td>
                                                                                                </tr>
                                                                                                <tr>
                                                                                                    <td class="w-200px">
                                                                                                        <a
                                                                                                            class="text-gray-400 fw-bold mb-1 fs-6">First
                                                                                                            Name</a>
                                                                                                    </td>
                                                                                                    <td class="pe-0">
                                                                                                        <div
                                                                                                            class="d-flex">
                                                                                                            <span
                                                                                                                class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                                <s:property
                                                                                                                    value="#session.USER.firstName" />
                                                                                                            </span>
                                                                                                        </div>
                                                                                                    </td>
                                                                                                </tr>
                                                                                                <tr>
                                                                                                    <td class="w-200px">
                                                                                                        <a
                                                                                                            class="text-gray-400 fw-bold mb-1 fs-6">Last
                                                                                                            Name</a>
                                                                                                    </td>
                                                                                                    <td class="pe-0">
                                                                                                        <div
                                                                                                            class="d-flex">
                                                                                                            <span
                                                                                                                class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                                <s:property
                                                                                                                    value="#session.USER.lastName" />
                                                                                                            </span>
                                                                                                        </div>
                                                                                                    </td>
                                                                                                </tr>
                                                                                                <tr>
                                                                                                    <td class="w-200px">
                                                                                                        <a
                                                                                                            class="text-gray-400 fw-bold mb-1 fs-6">Company
                                                                                                            Name</a>
                                                                                                    </td>
                                                                                                    <td class="pe-0">
                                                                                                        <div
                                                                                                            class="d-flex">
                                                                                                            <span
                                                                                                                class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                                <s:property
                                                                                                                    value="#session.USER.companyName" />
                                                                                                            </span>
                                                                                                        </div>
                                                                                                    </td>
                                                                                                </tr>
                                                                                                <tr>


                                                                                                    <td class="w-200px">
                                                                                                        <a
                                                                                                            class="text-gray-400 fw-bold mb-1 fs-6">Business
                                                                                                            Type</a>
                                                                                                    </td>
                                                                                                    <td class="pe-0">
                                                                                                        <div
                                                                                                            class="d-flex">
                                                                                                            <span
                                                                                                                class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                                <s:property
                                                                                                                    value="#session.USER.businessType" />
                                                                                                            </span>
                                                                                                        </div>
                                                                                                    </td>
                                                                                                </tr>
                                                                                             <%--    <tr>
                                                                                                    <td class="w-200px">
                                                                                                        <a
                                                                                                            class="text-gray-400 fw-bold mb-1 fs-6">Default
                                                                                                            Currency</a>
                                                                                                    </td>
                                                                                                    <td class="pe-0">
                                                                                                        <div
                                                                                                            class="d-flex">
                                                                                                            <span
                                                                                                                class="text-danger min-w-50px d-block text-end fw-bold fs-6">Not
                                                                                                                Mapped</span>
                                                                                                        </div>
                                                                                                    </td>
                                                                                                </tr> --%>
                                                                                            </tbody>
                                                                                            <!--end::Table body-->
                                                                                        </table>
                                                                                        <!--end::Table-->
                                                                                    </div>
                                                                                    <div
                                                                                        class="row g-9 mb-8 d-flex justify-content-center">
                                                                                        <!--begin::Col-->
                                                                                      <%--   <div class="col-md-3 fv-row">
                                                                                            <s:select
                                                                                                name="defaultCurrency"
                                                                                                id="defaultCurrency"
                                                                                                list="currencyMap"
                                                                                                data-control="select2"
                                                                                                data-placeholder="Select Currency"
                                                                                                class="form-select form-select-solid"
                                                                                                data-hide-search="true">
                                                                                            </s:select>
                                                                                        </div> --%>
                                                                                        <!-- <div class="col-md-4 fv-row">
                                                                                            <button type="submit"
                                                                                                id="save_currency"
                                                                                                class="btn btn-primary">
                                                                                                <span
                                                                                                    class="indicator-label">Submit</span>
                                                                                                <span
                                                                                                    class="indicator-progress">Please
                                                                                                    wait...
                                                                                                    <span
                                                                                                        class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
                                                                                            </button>
                                                                                        </div> -->
                                                                                    </div>


                                                                                </div>

                                                                            </s:if>
                                                                            <!-- end for submerchant -->
                                                                            <s:else>


                                                                                <div class="tab-pane fade show active"
                                                                                    id="personal_details"
                                                                                    role="tabpanel">
                                                                                    <div class="table-responsive">
                                                                                        <!--begin::Table-->
                                                                                        <table
                                                                                            class="table align-middle gs-0 gy-4">

                                                                                            <!--begin::Table body-->
                                                                                            <tbody>
                                                                                                <tr>
                                                                                                    <td class="w-200px">
                                                                                                        <a
                                                                                                            class="text-gray-400 fw-bold mb-1 fs-6">Business
                                                                                                            Name</a>
                                                                                                    </td>
                                                                                                    <td class="pe-0">
                                                                                                        <div
                                                                                                            class="d-flex">
                                                                                                            <span
                                                                                                                class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                                <s:property
                                                                                                                    value="#session.USER.businessName" />
                                                                                                            </span>
                                                                                                        </div>
                                                                                                    </td>
                                                                                                </tr>
                                                                                                <tr>
                                                                                                    <td class="w-200px">
                                                                                                        <a
                                                                                                            class="text-gray-400 fw-bold mb-1 fs-6">Email
                                                                                                            ID</a>
                                                                                                    </td>
                                                                                                    <td class="pe-0">
                                                                                                        <div
                                                                                                            class="d-flex">
                                                                                                            <span
                                                                                                                class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                                <s:property
                                                                                                                    value="#session.USER.emailId" />
                                                                                                            </span>
                                                                                                        </div>
                                                                                                    </td>
                                                                                                </tr>
                                                                                                <tr>
                                                                                                    <td class="w-200px">
                                                                                                        <a
                                                                                                            class="text-gray-400 fw-bold mb-1 fs-6">First
                                                                                                            Name</a>
                                                                                                    </td>
                                                                                                    <td class="pe-0">
                                                                                                        <div
                                                                                                            class="d-flex">
                                                                                                            <span
                                                                                                                class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                                <s:property
                                                                                                                    value="#session.USER.firstName" />
                                                                                                            </span>
                                                                                                        </div>
                                                                                                    </td>
                                                                                                </tr>
                                                                                                <tr>
                                                                                                    <td class="w-200px">
                                                                                                        <a
                                                                                                            class="text-gray-400 fw-bold mb-1 fs-6">Last
                                                                                                            Name</a>
                                                                                                    </td>
                                                                                                    <td class="pe-0">
                                                                                                        <div
                                                                                                            class="d-flex">
                                                                                                            <span
                                                                                                                class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                                <s:property
                                                                                                                    value="#session.USER.lastName" />
                                                                                                            </span>
                                                                                                        </div>
                                                                                                    </td>
                                                                                                </tr>
                                                                                                <tr>
                                                                                                    <td class="w-200px">
                                                                                                        <a
                                                                                                            class="text-gray-400 fw-bold mb-1 fs-6">Company
                                                                                                            Name</a>
                                                                                                    </td>
                                                                                                    <td class="pe-0">
                                                                                                        <div
                                                                                                            class="d-flex">
                                                                                                            <span
                                                                                                                class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                                <s:property
                                                                                                                    value="#session.USER.companyName" />
                                                                                                            </span>
                                                                                                        </div>
                                                                                                    </td>
                                                                                                </tr>
                                                                                                <s:if
                                                                                                    test="%{#session.USER.UserType.name()!='RESELLER'}">
                                                                                                    <tr>
                                                                                                        <!-- <td
                                                                                                            class="w-200px">
                                                                                                            <a
                                                                                                                class="text-gray-400 fw-bold mb-1 fs-6">Business
                                                                                                                Type</a>
                                                                                                        </td> -->
                                                                                                        <td
                                                                                                            class="pe-0">
                                                                                                            <div
                                                                                                                class="d-flex">
                                                                                                                <span
                                                                                                                    class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                                    <s:property
                                                                                                                        value="#session.USER.businessType" />
                                                                                                                </span>
                                                                                                            </div>
                                                                                                        </td>
                                                                                                    </tr>
                                                                                                </s:if>
                                                                                              <%--   <tr>
                                                                                                    <td class="w-200px">
                                                                                                        <a
                                                                                                            class="text-gray-400 fw-bold mb-1 fs-6">Default
                                                                                                            Currency</a>
                                                                                                    </td>
                                                                                                    <td class="pe-0">
                                                                                                        <div
                                                                                                            class="d-flex">
                                                                                                            <span
                                                                                                                class="text-danger min-w-50px d-block text-end fw-bold fs-6">Not
                                                                                                                Mapped</span>
                                                                                                        </div>
                                                                                                    </td>
                                                                                                </tr> --%>
                                                                                            </tbody>
                                                                                            <!--end::Table body-->
                                                                                        </table>
                                                                                        <!--end::Table-->
                                                                                    </div>
                                                                                    <s:if
                                                                                        test="%{#session.USER.UserType.name()=='RESELLER'}">
                                                                                        <div
                                                                                            class="row g-9 mb-8 d-flex justify-content-center">
                                                                                            <!--begin::Col-->
                                                                                            <div
                                                                                                class="col-md-3 fv-row">
                                                                                                <s:select
                                                                                                    name="defaultCurrency"
                                                                                                    id="defaultCurrency"
                                                                                                    list="currencyMap"
                                                                                                    data-control="select2"
                                                                                                    data-placeholder="Select Currency"
                                                                                                    class="form-select form-select-solid"
                                                                                                    data-hide-search="true"
                                                                                                    disabled="true">
                                                                                                </s:select>
                                                                                            </div>
                                                                                            <!-- <div
                                                                                                class="col-md-4 fv-row">
                                                                                                <button type="submit"
                                                                                                    id="save_currency"
                                                                                                    class="btn btn-primary"
                                                                                                    disabled="true">
                                                                                                    <span
                                                                                                        class="indicator-label">Submit</span>
                                                                                                    <span
                                                                                                        class="indicator-progress">Please
                                                                                                        wait...
                                                                                                        <span
                                                                                                            class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
                                                                                                </button>
                                                                                            </div> -->
                                                                                        </div>

                                                                                    </s:if>
                                                                                    <s:else>
                                                                                        <div
                                                                                            class="row g-9 mb-8 d-flex justify-content-center">
                                                                                            <!--begin::Col-->
                                                                                           <%--  <div
                                                                                                class="col-md-3 fv-row">
                                                                                                <s:select
                                                                                                    name="defaultCurrency"
                                                                                                    id="defaultCurrency"
                                                                                                    list="currencyMap"
                                                                                                    data-control="select2"
                                                                                                    data-placeholder="Select Currency"
                                                                                                    class="form-select form-select-solid"
                                                                                                    data-hide-search="true">
                                                                                                </s:select>
                                                                                            </div> --%>
                                                                                            <!-- <div
                                                                                                class="col-md-4 fv-row">
                                                                                                <button type="submit"
                                                                                                    id="save_currency"
                                                                                                    class="btn btn-primary">
                                                                                                    <span
                                                                                                        class="indicator-label">Submit</span>
                                                                                                    <span
                                                                                                        class="indicator-progress">Please
                                                                                                        wait...
                                                                                                        <span
                                                                                                            class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
                                                                                                </button>
                                                                                            </div> -->
                                                                                        </div>


                                                                                    </s:else>


                                                                                </div>
                                                                            </s:else>
                                                                            <div class="tab-pane fade show"
                                                                                id="contact_details" role="tabpanel">
                                                                                <div class="table-responsive">
                                                                                    <!--begin::Table-->
                                                                                    <table
                                                                                        class="table align-middle gs-0 gy-4">
                                                                                        <!--begin::Table body-->
                                                                                        <tbody>
                                                                                            <tr>
                                                                                                <td class="w-200px">
                                                                                                    <a
                                                                                                        class="text-gray-400 fw-bold mb-1 fs-6">Mobile</a>
                                                                                                </td>
                                                                                                <td class="pe-0">
                                                                                                    <div class="d-flex">
                                                                                                        <span
                                                                                                            class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                            <s:property
                                                                                                                value="#session.USER.mobile" />
                                                                                                        </span>
                                                                                                    </div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td class="w-200px">
                                                                                                    <a
                                                                                                        class="text-gray-400 fw-bold mb-1 fs-6">Telephone
                                                                                                        No.</a>
                                                                                                </td>
                                                                                                <td class="pe-0">
                                                                                                    <div class="d-flex">
                                                                                                        <span
                                                                                                            class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                            <s:property
                                                                                                                value="#session.USER.telephoneNo" />
                                                                                                        </span>
                                                                                                    </div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td class="w-200px">
                                                                                                    <a
                                                                                                        class="text-gray-400 fw-bold mb-1 fs-6">Address</a>
                                                                                                </td>
                                                                                                <td class="pe-0">
                                                                                                    <div class="d-flex">
                                                                                                        <span
                                                                                                            class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                            <s:property
                                                                                                                value="#session.USER.address" />
                                                                                                        </span>
                                                                                                    </div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td class="w-200px">
                                                                                                    <a
                                                                                                        class="text-gray-400 fw-bold mb-1 fs-6">City</a>
                                                                                                </td>
                                                                                                <td class="pe-0">
                                                                                                    <div class="d-flex">
                                                                                                        <span
                                                                                                            class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                            <s:property
                                                                                                                value="#session.USER.city" />
                                                                                                        </span>
                                                                                                    </div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td class="w-200px">
                                                                                                    <a
                                                                                                        class="text-gray-400 fw-bold mb-1 fs-6">State</a>
                                                                                                </td>
                                                                                                <td class="pe-0">
                                                                                                    <div class="d-flex">
                                                                                                        <span
                                                                                                            class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                            <s:property
                                                                                                                value="#session.USER.state" />
                                                                                                        </span>
                                                                                                    </div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td class="w-200px">
                                                                                                    <a
                                                                                                        class="text-gray-400 fw-bold mb-1 fs-6">Country</a>
                                                                                                </td>
                                                                                                <td class="pe-0">
                                                                                                    <div class="d-flex">
                                                                                                        <span
                                                                                                            class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                            <s:property
                                                                                                                value="#session.USER.country" />
                                                                                                        </span>
                                                                                                    </div>
                                                                                                </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td class="w-200px">
                                                                                                    <a
                                                                                                        class="text-gray-400 fw-bold mb-1 fs-6">Postal/ZIP
                                                                                                        Code</a>
                                                                                                </td>
                                                                                                <td class="pe-0">
                                                                                                    <div class="d-flex">
                                                                                                        <span
                                                                                                            class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                            <s:property
                                                                                                                value="#session.USER.postalCode" />
                                                                                                        </span>
                                                                                                    </div>
                                                                                                </td>
                                                                                            </tr>
                                                                                        </tbody>
                                                                                        <!--end::Table body-->
                                                                                    </table>
                                                                                    <!--end::Table-->
                                                                                </div>
                                                                            </div>
                                                                            <div class="tab-pane fade show"
                                                                                id="bank_details" role="tabpanel">
                                                                                <h3 id="cryptoAllowed">Crypto Detail</h3>

																					<div style="margin-top: 10px;">
																													<select
																														id="currencyForBankDetails" width="100%"
																														style="padding: 5px; margin-left: 10px; margin-bottom:20px;" onchange="changeCurrrency()" >
																													</select> <span id="currencyError"
																														style="color: red; display: none;">Please
																														Select Currency</span>
																												</div>
                                                                                <table
                                                                                    class="table align-middle gs-0 gy-4" id="cryptoAllowedTable">
                                                                                    <!--begin::Table body-->
                                                                                    <tbody>
                                                                                        <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">
                                                                                                    Default crypto</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                        <s:property
                                                                                                            value="#session.USER.defaultCrypto" />
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr>
                                                                                        <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">Crypto Address</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                        <s:property
                                                                                                            value="#session.USER.cryptoAddress" />
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr>

                                                                                    </tbody>
                                                                                    <!--end::Table body-->
                                                                                </table>


                                                                                <h3 id="fietAllowed">Fiat Detail</h3>
                                                                                <table
                                                                                    class="table align-middle gs-0 gy-4" id="fietAllowedTable">
                                                                                    <!--begin::Table body-->
                                                                                    <tbody>
                                                                                        <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">Bank
                                                                                                    Name</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                        <p id="bankName"  </p>
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr>
                                                                                        <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">Acc.Holder
                                                                                                    Name</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                        <span id="achName"></span>
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr>
                                                                                        <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">Account
                                                                                                    Number</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                        <span id="accountNumber"></span>
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr>
                                                                                        <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">IFSC
                                                                                                    Code</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                         <span id="ifscCode"></span>
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr>
                                                                                   <!--     <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">Branch
                                                                                                    Name</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                        <span id="branchName"></span>
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr>
                                                                                       --> <%-- <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">Currency</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">INR</span>
                                                                                                </div>
                                                                                            </td>
                                                                                            </tr>
                                                                                            <tr>
                                                                                                <td class="w-200px">
                                                                                                    <a
                                                                                                        class="text-gray-400 fw-bold mb-1 fs-6">PAN
                                                                                                        Card</a>
                                                                                                </td>
                                                                                                <td class="pe-0">
                                                                                                    <div class="d-flex">
                                                                                                        <span
                                                                                                            class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                            <s:property
                                                                                                                value="#session.USER.panCard" />
                                                                                                        </span>
                                                                                                    </div>
                                                                                                </td>
                                                                                            </tr>
                                                                                             <tr>
                                                                                                <td class="w-200px">
                                                                                                    <a
                                                                                                        class="text-gray-400 fw-bold mb-1 fs-6">Country Name
                                                                                                        </a>
                                                                                                </td>
                                                                                                <td class="pe-0">
                                                                                                    <div class="d-flex">
                                                                                                        <span
                                                                                                            class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                            <s:property
                                                                                                                value="#session.USER.bankCountry" />
                                                                                                        </span>
                                                                                                    </div>
                                                                                                </td>
                                                                                            </tr>--%>
                                                                                    </tbody>
                                                                                    <!--end::Table body-->
                                                                                </table>


                                                                            </div>
                                                                            <div class="tab-pane fade show"
                                                                                id="business_details" role="tabpanel">
                                                                                <table
                                                                                    class="table align-middle gs-0 gy-4">
                                                                                    <!--begin::Table body-->
                                                                                    <tbody>

                                                                                        <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">Organisation
                                                                                                    Type</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                        <s:property
                                                                                                            value="#session.USER.organisationType" />
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr>
                                                                                        <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">Website
                                                                                                    URL</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                        <s:property
                                                                                                            value="#session.USER.website" />
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr>
                                                                                        <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">Multicurrency
                                                                                                    Payment
                                                                                                    Required?</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                        <s:property
                                                                                                            value="#session.USER.multiCurrency" />
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr>
                                                                                        <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">Business
                                                                                                    Model</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                        <s:property
                                                                                                            value="#session.USER.businessModel" />
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr>
                                                                                        <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">Operation
                                                                                                    Address</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                        <s:property
                                                                                                            value="#session.USER.operationAddress" />
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr>
                                                                                        <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">Operation
                                                                                                    Address State</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                        <s:property
                                                                                                            value="#session.USER.operationState" />
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr>
                                                                                        <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">Operation
                                                                                                    Address City</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                        <s:property
                                                                                                            value="#session.USER.operationCity" />
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr>


                                                                                        <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">Operation
                                                                                                    Address Pincode</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                        <s:property
                                                                                                            value="#session.USER.operationPostalCode" />
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr>

                                                                                        <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">Date
                                                                                                    of Establishment</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                        <s:property
                                                                                                            value="#session.USER.dateOfEstablishment" />
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr>

                                                                                        <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">CIN</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                        <s:property
                                                                                                            value="#session.USER.cin" />
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr>

                                                                                      <%--   <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">PAN</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                        <s:property
                                                                                                            value="#session.USER.pan" />
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr> --%>

                                                                                       <%--  <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">Name
                                                                                                    on PAN Card</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                        <s:property
                                                                                                            value="#session.USER.panName" />
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr> --%>

                                                                                   <%--      <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">Expected
                                                                                                    number of
                                                                                                    transaction</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                        <s:property
                                                                                                            value="#session.USER.noOfTransactions" />
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr> --%>

                                                                                  <%--       <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">Expected
                                                                                                    amount of
                                                                                                    transaction</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                        <s:property
                                                                                                            value="#session.USER.amountOfTransactions" />
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr> --%>

                                                                                        <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">Disable/Enable
                                                                                                    Transaction
                                                                                                    Email</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                        <s:property
                                                                                                            value="#session.USER.transactionEmailerFlag" />
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr>

                                                                                        <tr>
                                                                                            <!-- <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">TransactionEmail</a>
                                                                                            </td> -->
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                        <s:property
                                                                                                            value="#session.USER.transactionEmailId" />
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr>
                                                                                    </tbody>
                                                                                    <!--end::Table body-->
                                                                                </table>
                                                                            </div>
                                                                            <div class="tab-pane fade show"
                                                                                id="integration" role="tabpanel">
                                                                                <table
                                                                                    class="table align-middle gs-0 gy-4">
                                                                                    <!--begin::Table body-->
                                                                                    <tbody>
                                                                                        <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">Pay
                                                                                                    ID</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1" id="MerchantpayId">
                                                                                                        <s:property
                                                                                                            value="#session.USER.payId" />
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr>
                                                                                        <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">API Key</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                        <%=SaltFactory.getKeySaltProperty((User)
                                                                                                            session
                                                                                                            .getAttribute("USER"))%>
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr>
                                                                                        <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-6">Request
                                                                                                    URL</a>
                                                                                            </td>
                                                                                            <td class="pe-0">
                                                                                                <div class="d-flex">
                                                                                                    <span
                                                                                                        class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                        <%=new
                                                                                                            PropertiesManager()
                                                                                                            .getSystemProperty("RequestURL")%>
                                                                                                    </span>
                                                                                                </div>
                                                                                            </td>
                                                                                        </tr>

                                                                                           <s:if
                                                                                            test="%{#session.USER.UserType.name()!='RESELLER'}">
                                                                                            <tr>
                                                                                                <td class="w-200px">
                                                                                                    <a
                                                                                                        class="text-gray-400 fw-bold mb-1 fs-6">Merchant
                                                                                                        Payment Link</a>
                                                                                                </td>
                                                                                                <td class="pe-0">
                                                                                                    <div class="d-flex">
                                                                                                        <span
                                                                                                            class="text-gray-800 fw-bold fs-6 me-1">
                                                                                                            <s:property
                                                                                                                value="#session.USER.paymentLink" />
                                                                                                        </span>
                                                                                                    </div>
                                                                                                </td>
                                                                                            </tr>
                                                                                        </s:if>
                                                                                    </tbody>
                                                                                    <!--end::Table body-->
                                                                                </table>
                                                                            </div>

                                                                            <div class="tab-pane fade show"
                                                                                id="services" role="tabpanel">
                                                                                <table
                                                                                    class="table align-middle gs-0 gy-4">
                                                                                    <!--begin::Table body-->
                                                                                    <tbody>
                                                                                        <tr>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-5">Currency</a>
                                                                                            </td>
                                                                                             <td class="pe-0">
                                                                                                   <div class="col-md-6 fv-row">
                                                                                                <s:select
                                                                                                        name="defaultCurrency"
                                                                                                        id="defaultCurrency1"
                                                                                                        list="currencyMap"
                                                                                                        onchange="getPayTypeList()"
                                                                                                        data-control="select2"
                                                                                                        data-placeholder="Select Currency"
                                                                                                        class="form-select form-select-solid"
                                                                                                    data-hide-search="true">
                                                                                                </s:select>
                                                                                                </div>
                                                                                            </td>
                                                                                            <td class="w-200px">
                                                                                                <a
                                                                                                    class="text-gray-400 fw-bold mb-1 fs-5">Payment Type</a>
                                                                                            </td>
                                                                                             <td class="pe-0">
                                                                                                   <div class="col-md-6 fv-row">
                                                                                                <select
                                                                                                        name="paymentType"
                                                                                                        id="paymentType"
                                                                                                         onchange="getMOPList()"
                                                                                                        data-control="select2"
                                                                                                         class="form-select form-select-solid"
                                                                                                    data-hide-search="true">
                                                                                                </select>
                                                                                                </div>
                                                                                            </td>

                                                                                        </tr>

                                                                                    </tbody>
                                                                                    <!--end::Table body-->
                                                                                </table>

                                                                                 <table id="mopDetailsTable" class="mopDetailsTableTemplate" style="visibility: hidden;">
                                                                                    <thead id="tableHeadTemplate"  >
                                                                                            <th scope="col">Sr.No.</th>
                                                                                            <th scope="col">Acquirer</th>
                                                                                            <th scope="col">TDR</th>
                                                                                            <th scope="col">Minimum Txn Amount</th>
                                                                                            <th scope="col">Maximum Txn Amount</th>
                                                                                            <th scope="col">TDR Type</th>
                                                                                            <th scope="col">Surcharge</th>
                                                                                            <th scope="col">TDR Tax %</th>

                                                                                     </thead>
                                                                                    <tbody>

                                                                                    </tbody>
                                                                                </table>
                                                                             <div id="mopDetailsTarget">

                                                                            </div>
                                                                        </div>
                                                                        </div>
                                                                    </div>
                                                                    <!--end::Body-->
                                                                </div>
                                                            </div>
                                                            <!--end::Col-->
                                                        </div>
                                                    </div>
                                                    <!--end::Container-->
                                                </div>
                                                <!--end::Post-->
                                                </div>



                                                <script>
                                                    $(document).ready(function(){
														 			var payId='<s:property value="%{#session.USER.payId}"/>';
													console.log("PAYID: "+payId);
												 			loadCurrencyList(payId, function(response){



			});
                                                        $("#mopDetailsCard").css("visibility","hidden");
                                                        getPayTypeList();
                                                      });
                                                   /* "use strict";
                                                    var KTCareersApply = function () {
                                                        var t;
                                                        return {
                                                            init: function () {

                                                                t = document.getElementById("save_currency"),
                                                                    t.addEventListener("click", (function (r) {
                                                                        r.preventDefault(),
                                                                            console.log("validated!"),
                                                                            t.setAttribute("data-kt-indicator", "on"),
                                                                            t.disabled = !0,
                                                                            setTimeout((function () {
                                                                                sendDefaultCurrency(),
                                                                                    t.removeAttribute("data-kt-indicator"),
                                                                                    t.disabled = !1
                                                                            }), 2e3);
                                                                    }
                                                                    ));
                                                            }
                                                        }
                                                    }();
                                                    KTUtil.onDOMContentLoaded((function () {
                                                        KTCareersApply.init()
                                                    }
                                                    ));*/
                                                </script>
                                                <script>


												function loadCurrencyList(payId, callback){
			if(payId==""){
				return false;
			}
			var urls=new URL(window.location.href);
		 	var domain = urls.origin;
			$.ajax({
				type:"GET",
				url: domain+"/crmws/cryptoDetails/getCurrencyList",
				data:{
					payId:payId,
			 	},
				success:function(response){
					console.log("Drop Down1");
					console.log(response);
					var content='<option value="" disable selected>Select Currency</option>';
					console.log(response);
					for(let i in response){
					//	console.log(i+"_"+response[i]);
						content+='<option value="'+i+'">'+response[i]+'</option>'
					}
					if(response.length==0){
						content='<option value"" selected disabled>No Record Found</option>';
					}
					$("#currencyForBankDetails").append(content);
					callback(response);

				},
				error:function(response){
					alert("Something Went Wrong while Fetching Currency");
				}
			});
		}

                                                 function getPayTypeList(){
                                                    $("#mopDetailsTarget").empty();
                                                    var payId='<s:property value="#session.USER.payId" />';
                                                    var selectedCurrency=$("#defaultCurrency1").val();
                                                    console.log(selectedCurrency);
                                                    $("#mopDetailsCard").css("visibility","hidden");
                                                        if(selectedCurrency==null||selectedCurrency==''){
                                                            return false;
                                                        }
                                                  $.ajax({

                                                	    type: "GET",
                                                	    url: "getPaymentTypeOnCurrency",

                                                	    data: {

                                                	      "payid": payId,
                                                	      "currency": selectedCurrency,
                                                	    },
                                                	    success: function(response) {
                                                	    var paymentValue=JSON.parse(JSON.stringify(response.paymentTypeData));
                                                	    console.log(paymentValue);
                                                          //  var paymentValue=Json.response.paymentTypeData;
                                                            var paymentTypeDropDown=$("#paymentType");
                                                            var s="<option value=''>Payment Type</option>";

                                                            // s+="<option value='abc'>Test </option>";
                                                            paymentTypeDropDown.empty();
                                                           //   console.log(paymentValue);
                                                	    	 for (var i = 0; i < paymentValue.length; i++) {
                                                               s+="<option value='"+paymentValue[i]+"'>"+paymentValue[i]+"</option>";
                                                    		}
                                                            paymentTypeDropDown.append(s);
                                                	    }
                                                	  });

                                                  }

                                                  function getMOPList()
                                                  {
                                                    $("#mopDetailsTarget").empty();
                                                    var payId='<s:property value="#session.USER.payId" />';
                                                     var selectedCurrency=$("#defaultCurrency1").val();
                                                    var selectedPayType=$("#paymentType").val();
                                                    if(selectedPayType==''||selectedPayType==null){
                                                        return false;
                                                    }
                                                 $.ajax({

                                                	    type: "GET",
                                                	    url: "getMopDetailsOnCurrency",

                                                	    data: {

                                                	      "payid": payId,
                                                	      "currency": selectedCurrency,
                                                          paymentType:selectedPayType,
                                                	    },
                                                	    success: function(response) {
                                                	        console.log(response);
                                                            var responseList=response.mopList;
                                                        	var mopDetails = JSON.parse(JSON
                                        							.stringify(responseList));
                                                                    addTable(mopDetails)
                                    				    }
                                                	});
                                                  }
function getCryptoDetailList(){
	        var payId='<s:property value="#session.USER.payId" />';
            var selectedCurrency=$("#currencyForBankDetails").val();
				console.log("SElected "+selectedCurrency)
			   if(selectedCurrency==''){
							return false;
						}
		 	var urls = new URL(window.location.href);
			var domain = urls.origin;
		   	$.ajax({
				type:"GET",
				url:domain+"/crmws/cryptoDetails/getCryptoList",
				contentType:'application/json',
				data:
				{
					payId:payId,
					currency:selectedCurrency
				},
				success: function(){
					console.log("SUCCESS");
	 		 	},
				complete:function(response){
					console.log("Crypto Details");
					console.log(response);
			 	},
				error: function(response){
	 				console.log(response);
				}
			});
	 	}

                                                  function addTable(mopDetails){
                                                    var mopDetailsCardTarget=$("#mopDetailsTarget");
                                                    var selectedPayType=$("#paymentType").val();
                                                      for(var i in mopDetails)
                                                    {
                                                        var mopTypeHead=document.createElement('h6');
                                                       // mopTypeHead.className="mopTypeHeadTag";
                                                        mopTypeHead.append(i);
                                                        var table = document.createElement('table');
                                                        table.id='mopDetailsTable'+selectedPayType+i;
                                                        table.className="table table-sm";
                                                        var tableBody=document.createElement('tbody');
                                                    //    tableBody.className="mop-details-table-body";
                                                        var tableHead=document.createElement('thead');
                                                       tableHead.className="mopDetailsTableRow";
                                                     //   mopDetailsCardTarget.append('<div style="display: inline-block;" style="margin-bottom:10px;"><b>Mop Type: &nbsp; </b></div>');
                                                        var tableRow=document.createElement('tr');
                                                        var tableData=document.createElement('td');
                                                        tableRow.innerHTML=($($("#tableHeadTemplate")).html())
                                                        tableHead.append(tableRow);
                                                        table.append(tableHead);
                                                        tableRow=document.createElement('tr');
                                                        var count=1;

                                                    for(var j in mopDetails[i])
                                                    {
                                                        mopDetailsCardTarget.append(mopTypeHead);
                                                        var tableRow="";
                                                        tableRow+="<tr>";
                                                        tableRow+="<td>"+count+"</td>";
                                                       //   console.log(j);
                                                        for(var k=1;k<mopDetails[i][j].length;k++)
                                                        {
                                                            var mopData=mopDetails[i][j][k];
                                                            if(k==6){
                                                                var enableSurcharge=(mopData)? "Yes":"No";
                                                                tableRow+=("<td>"+enableSurcharge+"</td>");
                                                            }

                                                            else{
                                                                if(mopData==null ||mopData=='')
                                                            {
                                                                mopData="";
                                                            }
                                                           // console.log(mopDetails[i][j][k]);
                                                             tableRow+=("<td>"+mopData+"</td>");
                                                            }
                                                        }
                                                        tableRow+="</tr>";
                                                        tableBody.innerHTML+=$(tableRow).html();
                                                        //console.log(tableBody);
                                                        count++;
                                                        table.append(tableBody);


                                                     }
                                                    mopDetailsCardTarget.append(table);

                                                    }
                                                  }

                                                /*
                                              function getPaymentType(currency){
                                                 var payId='<s:property value="#session.USER.payId" />';
                                                 $.ajax({

                                                	    type: "GET",
                                                	    url: "getPaymentTypeOnCurrency",

                                                	    data: {

                                                	      "payid": payId,
                                                	      "currency": currency,
                                                	    },
                                                	    success: function(response) {
var s="";
                                                	    	var accounts = JSON.parse(JSON
                                        							.stringify(response.paymentTypeList));
                                    						for (var i = 0; i < accounts.length; i++) {
                                    						s=s+"<li>"+accounts[i].paymentType+"</li>";
                                    						}

                                    						document.getElementById("paymentType1").innerHTML = s;

                                                	    }
                                                	  });

                                                  }
                                                  */
                                                </script>
                                            </body>


                                            </html>