<%@page import="com.pay10.commons.util.SaltFactory"%>
<%@page import="com.pay10.commons.user.User"%>
<%@page import="com.pay10.commons.util.Currency"%>
<%@page import="com.pay10.commons.util.Amount"%>
<%@page import="com.pay10.commons.util.FieldType"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="com.pay10.commons.util.Constants"%>
<%@page import="com.pay10.commons.util.PropertiesManager"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>



<!DOCTYPE html
	PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Merchant Account Details</title>


<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/custom.css" rel="stylesheet" type="text/css" />
<script src="../js/continents.js" type="text/javascript"></script>
<script src="../js/jquery.minshowpop.js"></script>
<script src="../js/jquery.formshowpop.js"></script>
<script src="../js/jquery.js"></script>
<script src="../js/follw.js"></script>
<script src="../js/jquery.popupoverlay.js"></script>
<script src="../js/commanValidate.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<!--  loader scripts -->
<script src="../js/loader/modernizr-2.6.2.min.js"></script>
<script src="../js/loader/main.js"></script>
<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<link rel="stylesheet" href="../css/loader/normalize.css" />
<link rel="stylesheet" href="../css/loader/main.css" />
<link rel="stylesheet" href="../css/loader/customLoader.css" />

<link rel="stylesheet" href="../css/bootstrap-clockpicker.css">
<script type="text/javascript" src="../js/bootstrap-clockpicker.js"></script>
<link rel="stylesheet" href="../css/merchantAccountSetup.css" />
<script type="text/javascript" src="../js/merchantAccountSetup.js"></script>
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<script type="text/javascript" src="../js/sweetalert.js"></script>
<link rel="stylesheet" href="../css/sweetalert.css">
<link href="https://fonts.googleapis.com/icon?family=Material+Icons"
	rel="stylesheet">


<style>
.mytable thead {
	background: linear-gradient(60deg, #f7a600, #f71d00);
	color: #fff;
}

.switch {
	position: relative;
	display: inline-block;
	width: 60px;
	height: 34px;
}

.switch input {
	opacity: 0;
	width: 0;
	height: 0;
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
	height: 26px;
	width: 26px;
	left: 4px;
	bottom: 4px;
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
	-webkit-transform: translateX(26px);
	-ms-transform: translateX(26px);
	transform: translateX(26px);
}

/* Rounded sliders */
.slider.round {
	border-radius: 34px;
}

.slider.round:before {
	border-radius: 50%;
}

.copyClass {
	margin-bottom: 20px;
	display: inline-block;
}

#newAPIKey {
	border-bottom: 1px solid black;
}

.copyClass .material-icons {
	left: auto;
	right: 10px;
}

.table-bordered {
	text-align: center;
	margin-top: 10px;
}

.table-bordered th {
	border-bottom: 2px solid black;
}

.reset-api-key-head {
	margin-left: 20px;
}

#newAPIKey {
	width: 250px;
}

@
keyframes shake { 0% {
	margin-left: 0rem;
}

25%
{
margin-left


:



0
.5rem


;
}
75%
{
margin-left


:



-0
.5rem


;
}
100%
{
margin-left


:



0
rem


;
}
}
.crypto-chain-error-message {
	color: red;
	display: none;
	margin-left: 10px;
}

.crypto-address-error-message {
	color: red;
	display: none;
	margin-left: 10px;
}

.blockChain-error {
	animation: shake 0.2s ease-in-out 0s 2;
	box-shadow: 0 0 0.6rem #ff0000;
}

.cryptoAddress-error {
	animation: shake 0.2s ease-in-out 0s 2;
	box-shadow: 0 0 0.6rem #ff0000;
}

.error-message-new-key {
	color: red;
	display: none;
	margin-left: 10px;
}

.error-message-date {
	color: red;
	display: none;
	margin-left: 10px;
}

.crypto-currency-error {
	animation: shake 0.2s ease-in-out 0s 2;
	box-shadow: 0 0 0.6rem #ff0000;
}
</style>

<script type="text/javascript">
		$(function () {
			$("#userGroupId").change(function () {
				var groupId = this.value;
				var token = document.getElementsByName("token")[0].value;
				$.ajax({
					type: "POST",
					url: "roleByGroup",
					data: {
						groupId: groupId,
						type: "MERCHANT_EDIT",
						token: token,
						"struts.token.name": "token"
					},
					success: function (data, status) {
						document.getElementById("roleIdDiv").innerHTML = data;
					},
					error: function (status) {
						//alert("Network error please try again later!!");
						return false;
					}
				});
			});
		});
		$(function () {
			$("#industryCategory").change(function () {
				var industry = this.value;
				var token = document.getElementsByName("token")[0].value;
				if (industry == 'select' || industry == '-1') {
					$("#subcategorydiv").hide();
					var subCategoryText = document.getElementById("subcategory");
					subCategoryText.value = "";
					return false;
				}
				$.ajax({
					type: "POST",
					url: "industrySubCategory",
					data: {
						industryCategory: industry,
						token: token,
						"struts.token.name": "token"
					},
					success: function (data, status) {
						var subCategoryListObj = data.subCategories;
						var subCategoryList = subCategoryListObj[0].split(',');
						var radioDiv = document.getElementById("radiodiv");
						radioDiv.innerHTML = "";
						for (var i = 0; i < subCategoryList.length; i++) {
							var subcategory = subCategoryList[i];
							var radioOption = document.createElement("INPUT");
							radioOption.setAttribute("type", "radio");
							radioOption.setAttribute("value", subcategory);
							radioOption.setAttribute("name", "subcategory");
							var labelS = document.createElement("SPAN");
							labelS.innerHTML = subcategory;
							radioDiv.appendChild(radioOption);
							radioDiv.appendChild(labelS);
						}
						$('#popup').popup({
							'blur': false,
							'escape': false
						}).popup('show');
					},
					error: function (status) {
						alert("Network error please try again later!!");
					}
				});
			});
		});

		function loadSubcategory() {
			var industry = document.getElementById("industryCategory").value;
			var token = document.getElementsByName("token")[0].value;

			$.ajax({
				type: "POST",
				url: "industrySubCategory",
				data: {
					industryCategory: industry,
					token: token,
					"struts.token.name": "token"
				},
				success: function (data, status) {
					var subCategoryListObj = data.subCategories;
					var subCategoryList = subCategoryListObj[0].split(',');
					var radioDiv = document.getElementById("radiodiv");
					radioDiv.innerHTML = "";
					for (var i = 0; i < subCategoryList.length; i++) {
						var subcategory = subCategoryList[i];
						var radioOption = document.createElement("INPUT");
						radioOption.setAttribute("type", "radio");
						radioOption.setAttribute("value", subcategory);
						radioOption.setAttribute("name", "subcategory");
						var labelS = document.createElement("SPAN");
						labelS.innerHTML = subcategory;
						radioDiv.appendChild(radioOption);
						radioDiv.appendChild(labelS);
					}

				},
				error: function (status) { }
			});
		}

		function selectSubcategory() {
			var checkedRadio = $('input[name="subcategory"]:checked').val();
			if (null == checkedRadio) {
				document.getElementById("radioError").innerHTML = "Please select a subcategory";
				return false;
			}
			document.getElementById("radioError").innerHTML = "";
			var subCategoryDiv = document.getElementById("subcategorydiv");
			var subCategoryText = document.getElementById("subcategory");
			subCategoryText.value = checkedRadio;
			subCategoryDiv.style.display = "block";
			$('#popup').popup('hide');
			//validation for required field
		}

		$(document)
			.ready(
				function () {
					//debugger
					var paymentLink=document.getElementById("paymentLink").value
					var data=document.getElementById("payId").value;
					document.getElementById("paymentLink").value=paymentLink+data;

					var data1 = document.getElementById("endTime").value;
					var data = document.getElementById("auto").checked
					if (data == true) {
						document.getElementById("autoRefundHideAndShow").style.display = "block";

					} else {
						document.getElementById("autoRefundHideAndShow").style.display = "none";

					}
					document.getElementById("autoRefund").checked = data
					if (data1 == null || data1 == "") {
						document.getElementById("endTime").value = "00:00";

					}
					//toggleEnable("ALL");
				});

		$(document)
			.ready(
				function () {

					var bankStatus = document
						.getElementById("bankVerificationStatus").value;
					if (bankStatus == "true") {
						document.getElementById("btnValidate").style.display = 'none';
					} else {
						document.getElementById("btnValidate").style.display = 'block';
					}

					/* //get data from session
					var splitPayment=document.getElementById("splitPayment");
					alert(splitPayment);

									if (splitPayment.checked == true) {
										$('#btnAddMore').show();
										$('.productIdclass').show();

										getAllAccountListForSettle();
									} else {
										$('#btnAddMore').hide();
										$('.productIdclass').hide();

									} */

					loadSubcategory();
					$("#subcategory").click(function () {
						$('#popup').popup({
							'blur': false,
							'escape': false
						}).popup('show');
					});
					loadNotificationApi();
				});

		//call to get all bank account for split payment and show in table.
		function getAllAccountListForSettle() {
			debugger
			var emailId = document.getElementById('emailId').value;
			$
				.post(
					"splitPaymentSettleFetch", {
					'bankSettle.email': emailId,
				},
					function (result) {
						//bind all data in table here to show all bank account for split payment
						var accounts = JSON.parse(JSON
							.stringify(result.bankSettles));
						for (var i = 0; i < accounts.length; i++) {
							if (accounts[i].deletedFlag == false) {
								$('.banksettleaccounts')
									.find('tbody')
									.append(
										"<tr><td style='display:none;'>" +
										accounts[i].id +
										"</td><td>" +
										accounts[i].productId +
										"</td><td>" +
										accounts[i].accountNumber +
										"</td><td>" +
										accounts[i].ifscCode +
										"</td><td>" +
										accounts[i].bankName +
										"</td><td>" +
										accounts[i].accountHolderName +
										"</td><td><input type='button' class='btn btn-danger deleteBtn' value='Delete Account' onClick='deleteAccount(" +
										accounts[i].id +
										");'></td></tr>");
							}

						}

					});
		}

		function loadNotificationApi() {
			var notificationApiFlag = '<s:property value="user.notificationApiEnableFlag"/>';
			if (notificationApiFlag == 'true') {
				document.getElementById('notificaionApiDiv').style.display = 'block';
			} else {
				document.getElementById('notificaionApiDiv').style.display = 'none';
			}
		}

		function copyPaymentLink(copyLinkElement) {

			document.getElementById("paymentLinkBtn").disabled = !document
				.queryCommandSupported('copy');
			var copiedLink = document.getElementById(copyLinkElement);
			copiedLink.select();
			document.execCommand('copy');
			return;
		}

		function deleteAccount(selectedId) {
			var text = "Are you sure you want to delete Account ?";

			if (confirm(text) == true) {

				$.post("splitPaymentSettleDelete", {
					'bankSettle.id': selectedId,
				}, function (result) {

					if (result.msg == "delete successfully") {
						$('.banksettleaccounts').find('tbody').empty();
						getAllAccountListForSettle();
						alert("Account Deleted Successfully");
					} else {
						alert("Account Deleted Failed");
					}

				});
			}

		}
	</script>
<script>
		var saveBtnValidation = false;
	</script>

<script type="text/javascript">
		function CollapseAll(theClass, id) {
			var alldivTags = new Array();
			alldivTags = document.getElementsByTagName("div");
			for (i = 0; i < alldivTags.length; i++) {
				if (alldivTags[i].className == theClass && alldivTags[i].id != id) {
					$('#' + alldivTags[i].id).slideUp('slow');
					document.getElementById('Head' + alldivTags[i].id).className = 'acordion-gray';
				}
			}
			if (document.getElementById('Head' + id).className
				.search('acordion-open') != -1) {
				document.getElementById('Head' + id).className = 'acordion-gray';
			} else {
				document.getElementById('Head' + id).className = 'acordion-open acordion-gray';
			}
		}

		function destlayer() { //v6.0
			var i, p, v, obj, args = destlayer.arguments;
			for (i = 0; i < (args.length - 2); i += 3)
				if ((obj = MM_findObj(args[i])) != null) {
					v = args[i + 2];
					if (obj.style) {
						obj = obj.style;
						v = (v == 'show') ? 'visible' : (v == 'hide') ? 'hidden' :
							v;
					}
					obj.visibility = v;
				}
		}
		$(document).ready(function () {
			//toggleEnable("ALL");
		});

		function toggleEnable(flag, toggleAction) {
			if (flag == 'ALL') {
				document.getElementById('processingmode').disabled = true;
				document.getElementById('comments').disabled = true;
				document.getElementById('whiteListIpAddress').disabled = true;

				document.getElementById('payId').disabled = true;
				document.getElementById('userId').disabled = true;
				document.getElementById('firstName').disabled = true;
				document.getElementById('lastName').disabled = true;
				document.getElementById('companyName').disabled = true;
				document.getElementById('website').disabled = true;
				document.getElementById('contactPerson').disabled = true;
				document.getElementById('emailId').disabled = true;
				document.getElementById('businessType').disabled = true;
				document.getElementById('merchantType').disabled = true;
				document.getElementById('noOfTransactions').disabled = true;
				document.getElementById('amountOfTransactions').disabled = true;
				//document.getElementById('resellerId').disabled = true;
				document.getElementById('productDetail').disabled = true;
				document.getElementById('registrationDate').disabled = true;

				document.getElementById('mobile').disabled = true;
				document.getElementById('telephoneNo').disabled = true;
				document.getElementById('fax').disabled = true;
				document.getElementById('address').disabled = true;
				document.getElementById('city').disabled = true;
				document.getElementById('state').disabled = true;
				document.getElementById('country').disabled = true;
				document.getElementById('postalCode').disabled = true;

				document.getElementById('bankName').disabled = true;
				document.getElementById('ifscCode').disabled = true;
				document.getElementById('accHolderName').disabled = true;
				document.getElementById('currency').disabled = true;
				document.getElementById('branchName').disabled = true;
				document.getElementById('panCard').disabled = true;
				document.getElementById('accountNo').disabled = true;
				document.getElementById('defaultCurrency').disabled = true;
				document.getElementById('btnSave').style.visibility = "hidden";
				document.getElementById('btnCancel').style.visibility = "hidden";
			} else {
				if (flag == 'Action') {
					document.getElementById('processingmode').disabled = toggleAction;
					document.getElementById('comments').disabled = toggleAction;
					document.getElementById('whiteListIpAddress').disabled = toggleAction;
					if (toggleAction == false) {
						document.getElementById('btnEdit').style.visibility = "hidden";
						document.getElementById('btnSave').style.visibility = "visible";
						document.getElementById('btnCancel').style.visibility = "visible";
					} else {
						document.getElementById('btnEdit').style.visibility = "visible";
						document.getElementById('btnSave').style.visibility = "hidden";
						document.getElementById('btnCancel').style.visibility = "hidden";
					}

				} else if (flag == 'MerchantDetails') {
					document.getElementById('payId').disabled = false;
					document.getElementById('userId').disabled = false;
					document.getElementById('firstName').disabled = false;
					document.getElementById('lastName').disabled = false;
					document.getElementById('companyName').disabled = false;
					document.getElementById('website').disabled = false;
					document.getElementById('contactPerson').disabled = false;
					document.getElementById('emailId').disabled = false;
					document.getElementById('businessType').disabled = false;
					document.getElementById('merchantType').disabled = false;
					document.getElementById('noOfTransactions').disabled = false;
					document.getElementById('amountOfTransactions').disabled = false;
					//document.getElementById('resellerId').disabled = false;
					document.getElementById('productDetail').disabled = false;
					document.getElementById('registrationDate').disabled = false;
				} else if (flag == 'ContactDetails') {
					document.getElementById('mobile').disabled = false;
					document.getElementById('telephoneNo').disabled = false;
					document.getElementById('fax').disabled = false;
					document.getElementById('address').disabled = false;
					document.getElementById('city').disabled = false;
					document.getElementById('state').disabled = false;
					document.getElementById('country').disabled = false;
					document.getElementById('postalCode').disabled = false;
				} else if (flag == 'BankDetails') {
					document.getElementById('bankName').disabled = false;
					document.getElementById('ifscCode').disabled = false;
					document.getElementById('accHolderName').disabled = false;
					document.getElementById('currency').disabled = false;
					document.getElementById('branchName').disabled = false;
					document.getElementById('panCard').disabled = false;
					document.getElementById('accountNo').disabled = false;
					document.getElementById('defaultCurrency').disabled = false;
				}
			}

		}


		function saveAction(event) {

			var state = document.getElementById("state").value;
			var bankName = document.getElementById("bankName").value;
			var accHolderName = document.getElementById("accHolderName").value;
			var accountNo = document.getElementById("accountNo").value;
			var ifscCode = document.getElementById("ifscCode").value;
		//	var cryptoAddress = document.getElementById("cryptoAddress").value;
			// var defaultCrypto = document.getElementById("defaultCrypto").value;
			var firstName = document.getElementById("firstName").value;
			var lastName = document.getElementById("lastName").value;
			var industryCategory=$("#industryCategory").val();
			console.log("CATE: "+industryCategory);
			event.preventDefault();
            			if(!firstName){
            				alert("Please Enter First Name");
            				event.preventDefault();
            				return false;
            			}
						if(!firstName.trim()){
            				alert("Whitespace not allowed in First Name");
            				event.preventDefault();
            				return false;
            			}

            			var firstRegex = /^[a-z ,.'-]+$/i;

            			if(!firstRegex.test(firstName)){
            				alert("Enter Valid First Name");
            				event.preventDefault();
            				return false;
            			}

            			if(!lastName){
            				alert("Please Enter Last Name");
            				event.preventDefault();
            				return false;
            			}

						if(!lastName.trim()){
            				alert("Whitespace not allowed in Last Name");
            				event.preventDefault();
            				return false;
            			}

            			if(!firstRegex.test(lastName)){
            				alert("Please Enter Valid Last Name");
            				event.preventDefault();
            				return false;
            			}

			// checkbox validation
			/* 	var yesBankNetbankingPrimary = document
						.getElementById("accountList[Yes Bank].PrimaryNetbankingStatus").checked;
				var direcpayNetbankingPrimary = document
						.getElementById("accountList[DIRECPAY].PrimaryNetbankingStatus").checked;
				var hdfcAcquirer = document
						.getElementById("accountList[HDFC Bank].primaryStatus").checked;
				var direcpayAcquirer = document
						.getElementById("accountList[DIRECPAY].primaryStatus").checked;
				var walletmobikwikAcquirer = document
						.getElementById("accountList[WALLET MOBIKWIK].primaryStatus").checked;
				var yesBankAcquirer = document
						.getElementById("accountList[Yes Bank].primaryStatus").checked;
				var americanExpress = document
						.getElementById("accountList[AMERICAN EXPRESS].primaryStatus").checked;
				var processingmodeDropdown = document.getElementById("processingmode").value;
				if (yesBankAcquirer == true && processingmodeDropdown == 'AUTH_CAPTURE') {
					alert("For YesBank ,please select PaymentMode as SALE");
				} else if (hdfcAcquirer == true && yesBankAcquirer == true) {
					alert("You must Select HDFC or YesBank Acquirer");
					hdfcAcquirer = false;
					yesBankAcquirer = false;
				} else if (direcpayAcquirer == true && yesBankAcquirer == true) {
					alert("You must Select Direcpay or YesBank Acquirer");
					direcpayAcquirer = false;
					yesBankAcquirer = false;
				} else if (yesBankNetbankingPrimary == true
						&& direcpayNetbankingPrimary == true) {
					alert("You must Select Direcpay Netbanking Primary or YesBank Netbanking Primary");
				} else if (hdfcAcquirer == false && direcpayAcquirer == false
						&& walletmobikwikAcquirer == false && yesBankAcquirer == false
						&& americanExpress == false) {
					alert("Please select at least one Acquirer");
				} else {
					document.merchantSaveAction.submit();
				} */
			//
			/* var state = document.getElementById("state").value;
			if(state == 'Select State'){
				alert("Please select the State");
					$("form").submit(function(e){
						e.preventDefault();
					});
					event.preventDefault();
				return false;
			}
			if(state != 'Select State'){
				document.merchantSaveAction.submit();
				$('#loader-wrapper').show();
			} */
			if (state == 'Select State') {
				if (state == 'Select State') {
					alert("Please select the State");
				}

				$("form").submit(function (e) {
					e.preventDefault();
				});

				event.preventDefault();
				return false;
			}

			var fiatElem = $("#fiatId")[0];
			var cryptoElem = $("#cryptoId")[0];
			// console.log("Fiat: "+fiatElem.checked);
			// console.log("Crypto: "+cryptoElem.checked);
			// if(!(fiatElem.checked || cryptoElem.checked)){
			// 	alert("Please select atleast one channel");
			// 	$("form").submit(function (e) {
			// 		e.preventDefault();
			// 	});
			// 	event.preventDefault();
			// 	return false;
			// }

			var bankVerify = $("#bankVerificationStatus").val();
			if (fiatElem.checked && (bankVerify == "false" || bankVerify == "false")) {
				alert("Please Enter Fiat Detail ");
				$("form").submit(function (e) {
					e.preventDefault();
				});
				event.preventDefault();
				return false;
			}

		/*	if(cryptoElem.checked){
				if(cryptoAddress=='' || cryptoAddress==undefined || defaultCrypto=='' || defaultCrypto==undefined){
					alert("Please Enter Crypto Detail ");
					$("form").submit(function (e) {
						e.preventDefault();
					});
					event.preventDefault();
					return false;
				}
			}

			var settlementPer = $("#settlementDays1").val();
            if (settlementPer < 1) {
                alert("Settlement Days Should not less then 1");
                $("form").submit(function (e) {
                    e.preventDefault();
                });
                event.preventDefault();
                return false;
            }
		*/


			if (state != 'Select State') {
				// 			if (saveBtnValidation==false) {

				// 			document.getElementById("btnValidate").disabled = false;
				// 			document.getElementById("btnSave").disabled = true;
				// 			alert("Please validate the Bank Details");

				// 			event.preventDefault();
				// 			return false;

				// 			}

				//alert("click ok and Wait for Submission")
				document.merchantSaveAction.submit();
				$('#loader-wrapper').show();
				//document.getElementById("btnSave").disabled = false;

			}


		}
	</script>

<script>

		function cancelChanges() {
			$('#loader-wrapper').show();
			window.location.reload();
		}
	</script>

<script type="text/javascript">
		function showDivs(prefix, chooser) {
			for (var i = 0; i < chooser.options.length; i++) {
				var div = document
					.getElementById(prefix + chooser.options[i].value);
				div.style.display = 'none';
			}

			var selectedvalue = chooser.options[chooser.selectedIndex].value;

			if (selectedvalue == "PL") {
				displayDivs(prefix, "PL");
			} else if (selectedvalue == "PF") {
				displayDivs(prefix, "PF");
			} else if (selectedvalue == "PR") {
				displayDivs(prefix, "PR");
			} else if (selectedvalue == "CSA") {
				displayDivs(prefix, "CSA");
			} else if (selectedvalue == "LLL") {
				displayDivs(prefix, "LLL");
			} else if (selectedvalue == "RI") {
				displayDivs(prefix, "RI");
			} else if (selectedvalue == "AP") {
				displayDivs(prefix, "AP");
			} else if (selectedvalue == "T") {
				displayDivs(prefix, "T");
			}

		}

		function displayDivs(prefix, suffix) {

			var div = document.getElementById(prefix + suffix);
			div.style.display = 'block';
		}

		window.onload = function () {
			document.getElementById('select1').value = 'a'; //set value to your default

		}
	</script>

<script>
		var _validFileExtensions = [".jpg", ".pdf", ".png"];

		function Validate(oForm) {
			var arrInputs = oForm.getElementsByTagName("input");
			for (var i = 0; i < arrInputs.length; i++) {
				var oInput = arrInputs[i];
				if (oInput.type == "file") {
					var sFileName = oInput.value;
					if (sFileName.length > 0) {
						var blnValid = false;
						for (var j = 0; j < _validFileExtensions.length; j++) {
							var sCurExtension = _validFileExtensions[j];
							if (sFileName.substr(
								sFileName.length - sCurExtension.length,
								sCurExtension.length).toLowerCase() == sCurExtension
									.toLowerCase()) {
								blnValid = true;
								break;
							}
						}

						if (!blnValid) {
							alert("Sorry, " + sFileName +
								" is invalid, allowed extensions are: " +
								_validFileExtensions.join(", "));
							return false;
						} else {
							alert("Upload Successfully");
						}
					}
				}
			}
			return true;
		}
	</script>
<script>
		var _validFileExtensions = [".jpg", ".gif", ".png"];

		function Validatelogo(oForm) {
			var arrInputs = oForm.getElementsByTagName("input");
			for (var i = 0; i < arrInputs.length; i++) {
				var oInput = arrInputs[i];
				if (oInput.type == "file") {
					var sFileName = oInput.value;
					if (sFileName.length > 0) {
						var blnValid = false;
						for (var j = 0; j < _validFileExtensions.length; j++) {
							var sCurExtension = _validFileExtensions[j];
							if (sFileName.substr(
								sFileName.length - sCurExtension.length,
								sCurExtension.length).toLowerCase() == sCurExtension
									.toLowerCase()) {
								blnValid = true;
								break;
							}
						}

						if (!blnValid) {
							alert("Sorry, " + sFileName +
								" is invalid, allowed extensions are: " +
								_validFileExtensions.join(", "));
							return false;
						} else {
							alert("Upload Successfully");
						}
					}
				}
			}

			return true;
		}
	</script>
<!--<script>
$(document)
.ready(
function() {

var token = document.getElementsByName("token")[0].value;
$
.ajax({
url : 'checkFileExistAdmin',
type : "POST",
data : {
payId : document
		.getElementById("payId").value,

token : token,
"struts.token.name" : "token",
},

success : function(data) {

var fileList = new Array;
filelist = data.fileName;
if (filelist != null) {
	filelist = filelist.split(",");
	for (i = 0; i < filelist.length; i++) {
		document
				.getElementById(filelist[i]).style.visibility = "visible";

	}
}

},

});

});
</script>-->

<script
	src="https://cdnjs.cloudflare.com/ajax/libs/jquery/2.0.2/jquery.js"
	integrity="sha256-H+K7U5CnXl1h5ywQfKtSj8PCmoN9aaq30gDh27Xc0jk="
	crossorigin="anonymous"></script>
<script type="text/javascript"
	src="https://cdn.jsdelivr.net/npm/jquery-validation@1.19.3/dist/jquery.validate.js">
		</script>

<style>
.errorMessage {
	color: #ff0000;
	text-align: left;
	/* margin-top:-15px;
margin-left: 2px; */
}

input[type="radio"] {
	margin-left: 10px !important;
}

#radiodiv span {
	font-size: 14px;
	margin-left: 4px;
}

.textFL_merch:hover {
	color: #000 !important;
	background: #fff !important;
}

.textFL_merch5:hover {
	color: #000 !important;
	background: #fff !important;
}

.btn {
	font-size: 10pt !important;
}

.success-text {
	/* color:#ff0000!important; */
	margin: 4px auto 15px !important;
}

.tranjuctionSet {
	width: 98%;
	float: left;
	font: bold 12px arial;
	color: #666;
	margin: -10px 0px 10px 10px;
	padding-top: 4px;
	line-height: 22px;
	background: #fff;
	border: 1px solid #e4e4e4;
	/* height: 65px; */
}

.tranjuctionCon {
	width: 98% !important;
}

.trans-top {
	margin-top: 10px;
}

.trans-btm {
	margin-bottom: 10px;
}

.rkb-mg {
	margin: 0px !important;
}

#radioError {
	color: #ff0000 !important;
	margin-left: 13px;
	font-size: 11px;
}

.MerchBx {
	margin-top: 15px !important;
	margin-bottom: -15px !important;
}

button, input {
	border: none;
}

.margin-email {
	margin-top: 11px !important;
}

.error-text {
	color: #a94442;
	font-weight: bold;
	background-color: #f2dede;
	list-style-type: none;
	text-align: center;
	list-style-type: none;
}

.error-text li {
	list-style-type: none;
}

#response {
	color: green;
}

.actionMessage li {
	padding: 0px !important;
	margin-bottom: -10px !important;
}

.selctInpt:focus {
	background-color: #FFF;
	outline: 0;
	border: none;
	box-shadow: none;
}

.FlowupLabels .fl_input {
	top: 10px !important;
	bottom: 5px !important;
	color: #000 !important;
}

.FlowupLabels .fl_wrap {
	color: #000 !important;
}

.btn:focus {
	outline: 0 !important;
}

#wwerr_resellerId {
	position: absolute;
	left: 100px;
	top: 0px;
}

#wwerr_ifscCode {
	position: absolute;
	left: 100px;
	top: 0px;
}

#wwerr_currency {
	position: absolute;
	left: 100px;
	top: 0px;
}

#wwerr_transactionEmailId {
	margin-top: 3%;
	margin-left: -1%;
}

#wwerr_accHolderName {
	position: absolute;
	left: 119px;
	top: 0px;
}

#wwerr_mCC {
	position: absolute;
	top: 0px;
	left: 166px;
}

#wwerr_whiteLabelUrl {
	position: absolute;
	left: 100px;
	top: 0px;
}

.table-encryption-key-card {
	width: 80%;
	height: auto;
	position: relative;
	margin: 0 auto;
}

/* #whitelabelborder{
border:none;
background: none;
} */
.dt-buttons {
	margin-top: 35px !important;
}

span.clockpicker-span-hours.text-primary {
	color: #999999 !important;
}

span.clockpicker-span-minutes.text-primary {
	color: #999999 !important;
}

button.btn.btn-sm.btn-default.clockpicker-button.am-button {
	padding: 0.40625rem 0.25rem !important;
	line-height: 0.5;
}

button.btn.btn-sm.btn-default.clockpicker-button.pm-button {
	padding: 0.40625rem 0.25rem !important;
	line-height: 0.5;
}

.clockpicker-popover .popover-title {
	font-size: 21px !important;
	line-height: 23px !important;
}
</style>
<!-- Create a new service to reset setting -->
<script type="text/javascript">
		function resetAll() {
			var text = "Do you really want to reset all setting ?";
			if (confirm(text) == true) {
				var payId = document.getElementById('payId').value;
				var emailId = document.getElementById('emailId').value;

				$.post("resetAllSetting", {
					payId: payId,
					merchantEmail: emailId,
				}, function (result) {
					alert(result.message);
				});

			}
		}
	</script>

<script type="text/javascript">
		window.addEventListener("load", function () {

			var status = document.getElementById('splitPayment').checked;

			if (status == true) {
				document.getElementById('splitPayment').checked = true;
				$('#btnAddMore').show();
				$('.productIdclass').show();
				getAllAccountListForSettle();
				$(".mytable").show();

			} else {
				document.getElementById('splitPayment').checked = false;
				$('#btnAddMore').hide();
				$('.productIdclass').hide();
			}

			//Added By Sweety
			var tncStatus = document.getElementById('tnc').checked;
			if (tncStatus == true) {
				document.getElementById('tnc').checked = true;
				//document.getElementById('tnc').value=true;
				$("#paytextdiv").css("display", "block");
				$("#invoicetextdiv").css("display", "block");
			} else {
				document.getElementById('tnc').checked = false;
				//document.getElementById('tnc').value=false;
				$("#paytextdiv").css("display", "none");
				$("#invoicetextdiv").css("display", "none");
			}
		});
	</script>

</head>

<body>
	<!--  <div id="loader-wrapper" style="width: 100%; height: 100%; display:none;">
<div id="loader"></div>
</div> -->

	<div id="popup" style="display: none;">
		<div class="modal-dialog" style="width: 400px;">
			<!-- Modal content -->
			<div class="modal-content"
				style="background-color: transparent; border-radius: 13px; -webkit-box-shadow: 0px 0px 0px 0px; -moz-box-shadow: 0px 0px 0px 0px; box-shadow: 0px 0px 0px 0px; box-shadow: 0px;">
				<div id="1" class="modal-body"
					style="background-color: #ffffff; border-radius: 13px; -webkit-box-shadow: 0px 0px 0px 0px; -moz-box-shadow: 0px 0px 0px 0px; box-shadow: 0px 0px 0px 0px; box-shadow: 0px;">

					<table class="detailbox table98" cellpadding="20">
						<tr>
							<th colspan="2" width="16%" height="30" align="left"
								style="background-color: #496cb6; color: #ffffff; border-top-right-radius: 13px; border-top-left-radius: 13px; border-bottom-right-radius: 13px; border-bottom-left-radius: 13px; padding: 0px 0px 0px 8px; font-size: 14px;">
								Select sub category</th>
						</tr>
						<tr>
							<td colspan="2" height="30" align="left">
								<div id="radiodiv"></div>
							</td>
						</tr>
						<tr>
							<td colspan="2" height="30" align="left">
								<div id="radioError"></div>
							</td>
						</tr>
						<tr>
							<td colspan="2"><input type="submit" value="Done"
								onclick="selectSubcategory()" class="btn btn-success btn-sm"
								style="margin-left: 38%; width: 21%; height: 100%; margin-top: 1%;" />
							</td>
						</tr>
					</table>

				</div>
			</div>
		</div>
	</div>

	<div class="toolbar" id="kt_toolbar">
		<!--begin::Container-->
		<div id="kt_toolbar_container"
			class="container-fluid d-flex flex-stack">
			<!--begin::Page title-->
			<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
				data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
				class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
				<!--begin::Title-->
				<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
					Merchant Account</h1>
				<!--end::Title-->
				<!--begin::Separator-->
				<span class="h-20px border-gray-200 border-start mx-4"></span>
				<!--end::Separator-->
				<!--begin::Breadcrumb-->
				<ul
					class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
					<!--begin::Item-->
					<li class="breadcrumb-item text-muted"><a href="index.html"
						class="text-muted text-hover-primary">Dashboard</a></li>
					<!--end::Item-->
					<!--begin::Item-->
					<li class="breadcrumb-item"><span
						class="bullet bg-gray-200 w-5px h-2px"></span></li>
					<!--end::Item-->
					<!--begin::Item-->
					<li class="breadcrumb-item text-muted">Merchant Setup</li>
					<!--end::Item-->
					<!--begin::Item-->
					<li class="breadcrumb-item"><span
						class="bullet bg-gray-200 w-5px h-2px"></span></li>
					<!--end::Item-->
					<!--begin::Item-->
					<li class="breadcrumb-item text-dark">Merchant Account</li>
					<!--end::Item-->
				</ul>
				<!--end::Breadcrumb-->
			</div>
			<!--end::Page title-->

		</div>
		<!--end::Container-->
	</div>
	<div class="post d-flex flex-column-fluid" id="kt_post">
		<div id="kt_content_container" class="container-xxl">
			<div class="row my-5">
				<div class="col">
					<div class="card">
						<div class="card-body">
							<table width="100%" border="0" align="center" cellpadding="0"
								cellspacing="0">
								<tr>
									<td align="left" valign="middle"><s:form
											action="merchantSaveAction" method="post" autocomplete="off"
											class="FlowupLabels" theme="css_xhtml" validate="true">
											<s:hidden name="token" value="%{#session.customToken}">
											</s:hidden>
											<table width="100%" border="0" cellspacing="0"
												cellpadding="0">
												<div class="col-md-12"
													style="background: #202F4B; padding-top: 10px;">
													<div class="col-sm-6 col-lg-3">
														<h4 style="margin-top: 15px; color:white;">Merchant Details</h4>
													</div>
													<div class="col-sm-6 col-lg-3">
														<h4 style="margin-top: 15px; color:white;">
															<s:property value="user.businessName" />
														</h4>
													</div>
													<div class="col-sm-6 col-lg-3"
														style="display: inline-flex;">
														<s:submit id="btnSave" name="btnSave"
															style="margin-right: 10px;background-color: blue;"
															class="btn btn-danger btn-md" value="Save"
															onclick="saveAction(event);">
														</s:submit>
														<button type="button" id="btnCancel" name="btnCancel"
															class="btn btn-danger btn-md" style="background-color: red;" onClick="cancelChanges();">Cancel</button>
														<button type="button" id="btnReset" name="btnReset"
															style="background-color: orange;"
															class="btn btn-danger btn-md" onClick="resetAll();">Reset
															All</button>
													</div>
													<!-- <div class="col-sm-6 col-lg-3">
<button type="button" id="btnCancel" name="btnCancel"
		class="btn btn-danger btn-md"
		onClick="cancelChanges();">Cancel</button>
</div> -->
												</div>
												<!-- <tr>
<td align="left" valign="top" style="font: bold 14px verdana; color: #fff; padding:6px 2px 2px 4px;background-color:#0271BB;">
<table width="100%" border="0" cellpadding="0" cellspacing="0">
<tr>
<td align="left" width="50%" valign="middle">Merchant Details</td>
<td align="left" width="25%" valign="middle"><s:property value="user.businessName" /></td>
<td width="25%" align="center" valign="middle"><s:submit id="btnSave" name="btnSave"
		class="btn btn-success btn-md" value="Save"
		onclick="saveAction(event);">
	</s:submit></td>
<td width="25%" align="left" valign="middle">
	<button type="button" id="btnCancel" name="btnCancel"
		class="btn btn-danger btn-md"
		onClick="cancelChanges();">Cancel</button>
</td>

</tr>
</table>
</td>
</tr> -->
												<tr>

													<s:if test="%{responseObject.responseCode=='101'}">
														<tr>
															<td align="left" valign="top">
																<div id="saveMessage">
																	<s:actionmessage class="success success-text" />
																</div>
															</td>
														</tr>

													</s:if>
												<tr>
													<s:else>
														<td align="left"><class="error-text"> <s:actionmessage
																theme="simple" /></td>
													</s:else>


												</tr>
												<tr>
													<td align="center" valign="top">
														<table width="98%" border="0" cellspacing="0"
															cellpadding="0">
															<tr>
																<td align="center" valign="middle">
																	<div id="page-content">
																		<div class="acordion-gray" id="HeadActionDiv">
																			<H4 class="pagehead"
																				onClick="CollapseAll('hide-this','ActionDiv');$('#ActionDiv').slideToggle('slow');">
																				Action</H4>
																		</div>
																		<div id="ActionDiv">
																			<div class="indent">
																				<table width="100%" border="0" cellpadding="7"
																					cellspacing="0" class="formboxRR">
																					<tr>
																						<td align="left" valign="top" class="nfbxf">
																							<div class="addfildn">

																								<div class="col-sm-6 col-lg-3 addfildn">
																								<span style="font-weight: bold; color: #000000d0; font-family:'Poppins', sans-serif;" >Status</span><br />
																									<s:select data-control="select2"
																										class="form-select form-select-solid" style="margin-bottom: 020px;"
																										headerValue="ALL"
																										list="@com.pay10.commons.util.UserStatusType@values()"
																										id="status" name="userStatus"
																										value="%{user.userStatus}" />
																								</div>
																								<div class="col-sm-6 col-lg-3 addfildn">
																								<span  style="color: #000000d0; font-weight: bold; font-family:'Poppins', sans-serif;">Processing Mode</span><br />
																									<s:select data-control="select2"
																										class="form-select form-select-solid"
																										headerValue="ALL"
																										list="@com.pay10.commons.util.ModeType@values()"
																										id="processingmode" name="modeType"
																										value="%{user.modeType}" />
																								</div>
																								<div class="clear"></div>


																								<div class="col-sm-12 col-lg-6">
																									<div
																										class="fl_wrap form-control form-control-solid">
																										<!--style="height: 110px;top: 22px;"-->
																										<label class='fl_label'>Comments(Maximum
																											200 Characters)</label>
																										<s:textarea id="comments" class="fl_input"
																											rows="5" name="comments" type="text"
																											value="%{user.comments}" autocomplete="off"
																											style="height:85px; resize: none; margin-top:10px; outline-width:0"
																											theme="simple" maxlength="200">
																										</s:textarea>
																										<!-- <label class='fl_label' style="margin-top:16% !important; margin-left:76% !important;">Maximum 200 Characters</label> -->
																									</div>

																								</div>

																								<div class="clear"></div>


																								<div class="clear"></div>
																							</div>
																						</td>
																					</tr>
																				</table>
																			</div>
																		</div>

																		<div class="acordion-gray "
																			id="HeadMerchantDetailsDiv">
																			<H4 class="pagehead"
																				onClick="CollapseAll('hide-this','MerchantDetailsDiv');$('#MerchantDetailsDiv').slideToggle('slow');">
																				Merchant Details</H4>
																		</div>
																		<div id="MerchantDetailsDiv">
																			<div class="indent">
																				<table width="100%" border="0" cellspacing="0"
																					cellpadding="7" class="formboxRR">
																					<tr>
																						<td align="left" valign="top">&nbsp;</td>
																					</tr>
																					<tr>
																					<tr>
																						<td align="left" valign="top">
																							<div class="addfildn ">
																								<div class="rkb">
																									<div class="addfildn">
																										<div
																											class="fl_wrap form-control form-control-solid">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Pay
																												ID</label>
																											<s:textfield id="payId"
																												style="margin-top:10px;    font-weight:500; font-size:14px;"
																												name="payId" type="text"
																												value="%{user.payId}" readonly="true">
																											</s:textfield>
																										</div>
																									</div>

																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">First
																												Name</label>
																											<div class="txtnew">
																												<s:textfield id="firstName"
																													style="margin-top:10px; font-weight:normal; font-size:14px;"
																													name="firstName" type="text"
																													value="%{user.firstName}"
																													autocomplete="off"
																													onKeyPress="return ValidateAlpha(event);">
																												</s:textfield>

																											</div>

																										</div>
																									</div>

																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Last
																												Name</label>
																											<s:textfield id="lastName"
																												style="margin-top:10px; font-weight:normal; font-size:14px;"
																												name="lastName" type="text"
																												value="%{user.lastName}" autocomplete="off"
																												onKeyPress="return ValidateAlpha(event);">
																											</s:textfield>
																										</div>
																									</div>

																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Company
																												Name</label>
																											<s:textfield id="companyName"
																												style="margin-top:10px; font-weight:normal; font-size:14px;"
																												name="companyName" type="text"
																												value="%{user.companyName}"
																												autocomplete="off">
																											</s:textfield>
																										</div>
																									</div>
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Registration
																												Date</label>
																											<s:textfield id="registrationDate"
																												style="margin-top:10px; font-weight:normal; font-size:14px;width: -webkit-fill-available;"
																												value="%{rDate}"
																												type="text"
																												readonly="true">
																											</s:textfield>
																											<s:hidden name="registrationDate" value="%{user.registrationDate}"></s:hidden>
																										</div>
																									</div>
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Activation
																												Date</label>
																											<s:textfield id="activationDate"
																												style="margin-top:10px; font-weight:normal; font-size:14px;width: -webkit-fill-available;"
																												type="text"
																												value="%{aDate}"
																												readonly="true">
																											</s:textfield>
																											<s:hidden name="activationDate" value="%{user.activationDate}"></s:hidden>
																										</div>
																									</div>
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">User
																												Group</label>
																											<s:select class="selctInpt" id="userGroupId"
																												name="userGroupId" list="userGroups"
																												listKey="id" listValue="group"
																												autocomplete="off"
																												style="margin-top:10px; font-weight:normal; font-size:14px; border: none; width:100% !important;">
																											</s:select>
																										</div>
																									</div>
																									<div class="addfildn" id="roleIdDiv">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Role</label>
																											<s:select data-control="select2"
																												class="form-select form-select-solid"
																												id="roleId" name="roleId" list="roleList"
																												listKey="id" listValue="roleName"
																												autocomplete="off" disabled="true"
																												style="margin-top:10px; font-weight:normal; font-size:14px; border: none; width:100% !important;">
																											</s:select>
																										</div>
																									</div>

																									<!-- Text Field Added start Here -->
																									<div class="addfildn" id="paytextdiv"
																										style="display: none;">

																										<s:textarea id="paytext"
																											label="Terms & Condition for Payment Page"
																											style="width:450px; height:200px;"
																											name="paytext" value="%{user.payText}" />

																									</div>

																									<div class="clear"></div>
																								</div>

																								<div class="rkb">
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Business
																												Name</label>
																											<s:textfield id="businessName"
																												style="margin-top:10px; font-weight:normal; font-size:14px;width: -webkit-fill-available;"
																												name="businessName" type="text"
																												value="%{user.businessName}" readonly="true"
																												autocomplete="off"
																												onKeyPress="return ValidateAlpha(event);">
																											</s:textfield>
																										</div>
																									</div>


																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Reseller
																												ID (Agent)</label>
																											<s:textfield id="resellerId"
																												style="margin-top:10px;    font-weight:500; font-size:14px;"
																												onkeypress="validReseller()"
																												name="resellerId" type="text"
																												readonly="true"
																												value="%{user.AgentId}">
																											</s:textfield>
																										</div>
																									</div>

																									<div class="addfildn">
																										<div class="fl_wrap" id="industryCategory1">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Industry
																												Category</label>
																											<s:select data-control="select2"
																												class="form-select form-select-solid"
																												id="industryCategory"
																									  			name="industryCategory"
																												list="industryTypesList"
																												value="%{user.industryCategory}"
																										 		autocomplete="off" aria-readonly="true"

																												aria-disabled="true"
																								 				style="margin-top:10px; font-weight:normal; font-size:14px; border: none; width:100% !important;">
																											</s:select>
																										</div>
																									</div>
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Industry
																												Sub Category</label>
																											<div id="subcategorydiv"
																												style="display: block;" disabled="true">
																												<s:textfield id="subcategory"
																													name="industrySubCategory"
																													value="%{user.industrySubCategory}"
																													placeholder="Sub category"
																													autocomplete="off" readonly="true"
																													style="margin-top:10px; width: 45% !important; font-weight:normal; font-size:14px;">
																												</s:textfield>

																											</div>
																										</div>
																										<%--
																											<tr>
																						<td height="50"
																							align="left"
																							id="tdIndustryType"
																							style="display: block;">
																							<s:select
																								name="industryCategory"
																								id="industryCategory"
																								headerKey="select"
																								headerValue="-Select Industry Type-"
																								list="industryCategory"
																								value="{'-Select Industry Type-'}"
																								class="signupdropdwn"
																								autocomplete="off">
																							</s:select>
																						</td>
																					</tr>
																					--%>

																										<!-- <div class="addfildn">
						<div class="fl_wrap"> -->
																										<!--   <label class='fl_label'>Industry Sub Category</label> -->
																										<%-- <s:textfield
																						class="fl_input"
																						id="industrySubCategory"
																						name="industrySubCategory"
																						type="text"
																						value="%{user.industrySubCategory}"
																						autocomplete="off"
																						onKeyPress="return ValidateAlpha(event);">
																						</s:textfield>
																						--%>
																										<%--
																							<tr>
																							<td>
																								<div id="subcategorydiv"
																									style="display: none;">
																									<s:textfield
																										id="subcategory"
																										name="industrySubCategory"
																										value="%{user.industrySubCategory}"
																										class="signuptextfield"
																										placeholder="Sub category"
																										autocomplete="off" />
																								</div>

																							</td>
															</tr> --%>
																										<!-- </div>
							</div>
						-->

																										<div class="addfildn margin-email">
																											<div class="fl_wrap">
																												<label class='fl_label'
																													style="padding: 0; font-size: 13px; font-weight: 600;">Email
																													ID</label>
																												<s:textfield id="emailId" name="emailId"
																													type="text" value="%{user.emailId}"
																													readonly="true"
																													style="font-weight: normal; font-size: 14px; margin-top:10px;     width: -webkit-fill-available;">
																												</s:textfield>
																											</div>
																										</div>
																										<div class="addfildn">
																											<div class="fl_wrap">
																												<label class='fl_label'
																													style="padding: 0; font-size: 13px; font-weight: 600;">Request
																													URL</label> <br>
																												<%=new
																		PropertiesManager().getSystemProperty("RequestURL")%>
																											</div>
																										</div>

																										<div class="addfildn">
																											<div class="fl_wrap">
																												<label class='fl_label'
																													style="padding: 0; font-size: 13px; font-weight: 600;">Salt</label>
																												<br>
																												<s:property value="salt" />
																											</div>
																										</div>

																										<div class="addfildn">
																											<div class="fl_wrap">
																												<label class='fl_label'
																													style="padding: 0; font-size: 13px; font-weight: 600;">Merchant
																													Hosted Encryption Key</label> <br>
																												<s:property
																													value="merchantHostedEncryptionKey" />
																											</div>
																										</div>
																									</div>
																								</div>
																								<div id="reset-key-card">
																									<hr
																										style="height: 1px; width: 100%; color: #000; background-color: #000;">
																									<div class="reset-api-key-head">
																										<div class="copyClass">
																											<input id="newAPIKey" type="text" disabled
																												placeholder="New API Key" /> <span
																												onclick="CopyToClipboard('newAPIKey')"><i
																												class="material-icons">content_copy</i></span> <input
																												id="newSalt" hidden /> <input
																												id="newKeySalt" hidden />
																										</div>
																										<div>
																											<div style="display: inline-block;">
																												<button type="button"
																													class="btn btn-primary"
																													id="getEncryptionKey"
																													onClick="getEncryptionkey()">Reset</button>
																												<div id="errorMessage"
																													class="error-message-new-key">Please
																													Click Reset To Generate New Key</div>
																											</div>
																											<div style="display: inline-block;">
																												<div class="container"
																													style="display: inline-block;">
																													<input type='date' id='datePicker' /> <input
																														type="time" id="time" disabled />

																												</div>
																												<div id="errorMessage"
																													class="error-message-date">Please
																													Pick Valid Date</div>
																											</div>

																											<div style="display: inline-block;">
																												<button id="applyNewEncryption"
																													type="button" class="btn btn-primary"
																													onclick="applyNewEncryptionKey()">Apply</button>
																											</div>
																										</div>
																									</div>

																									<div class="table-encryption-key-card">
																										<table class="table table-bordered table-dark"
																											id="encryptionKeyTable">
																											<thead>
																												<th hidden>ID</th>
																												<th>Sr. No.</th>
																												<th>API Key</th>
																												<th hidden>Salt</th>
																												<th hidden>Key Salt</th>
																												<th>Start Date</th>
																												<th>End Date</th>
																												<th>Status</th>
																											</thead>

																										</table>
																									</div>
																								</div>
																								<div class="addfildn" id="invoicetextdiv"
																									style="display: none;">
																									<s:textarea
																										label="Terms & Condition for Invoice"
																										style="width:450px; height:200px;"
																										id="invoicetext" name="invoicetext"
																										value="%{user.invoiceText}" />
																								</div>
																							</div> <!-- Text Field Added start Here -->
																							<div class="row rkb"></div>
																							<div class="clear"></div> <%-- <div class="addfildn" id="roleIdDiv"
							style="margin-left: -450px;">
							<label
								class="d-flex align-items-center fs-6 fw-bold mb-2">

								<s:checkbox name="tncStatus" id="tnc"
									value="%{user.tncStatus}" />&nbsp; Terms &
								Condition applicable for Payment & Invoice
							</label>
							<!-- <input type="checkbox" id="tnc" name="tncStatus"> &nbsp; I accept All terms & conditions.* -->

						</div> --%>
																						</td>
																					</tr>
																					<tr>
																						<td>
																							<!--  added check box here for ekyc --> 
																							<div style="margin-left: 20px;">
									<label class="d-flex fs-6 fw-bold mb-2">

										<s:checkbox name="ekycFlag"
											id="ekycFlag"
											value="%{user.ekycFlag}" />&nbsp;
										E-KYC Flag
									</label>
								</div>
								 <!-- End ekyc tag here -->
																						</td>
																					</tr>

																				</table>
																			</div>
																		</div>

																		<div class="acordion-gray" id="HeadDocumentDetailsDiv"
																			style="display: none">
																			<H4 class="pagehead"
																				onClick="CollapseAll('hide-this','DocumentDetailsDiv');$('#DocumentDetailsDiv').slideToggle('slow');">
																				Acquirer Details</H4>
																		</div>
																		<div id="DocumentDetailsDiv" style="display: none">
																			<div class="indent">
																				<table width="100%" border="0" cellspacing="0"
																					cellpadding="7" class="formboxRR">
																					<tr>
																						<td align="left" valign="top"
																							style="padding: 10px;"><s:set value="0"
																								var="listCounter" /> <s:iterator
																								value="%{user.accounts}" status="counter">
																								<table width="90%" border="0" align="center"
																									cellpadding="0" cellspacing="0"
																									class="borderbox">
																									<tr>
																										<td colspan="3" align="left" height="10">
																											<table width="100%" border="0"
																												cellspacing="0" cellpadding="0">
																												<tr>
																													<!--  modified for validation between acquirer primary status check -->
																													<td width="16%" align="left"
																														valign="middle" class="blbxf"
																														style="padding: 8px;"><s:property
																															value="%{acquirerName}" /></td>
																													<td width="30%" align="left"
																														valign="middle" class="blbxf"><s:hidden
																															name="accountList[%{#counter.index}].acquirerName"
																															value="%{acquirerName}">
																														</s:hidden></td>


																												</tr>
																											</table>
																										</td>
																									</tr>
																									<s:hidden
																										name="accountList[%{#counter.index}].acquirerPayId"
																										value="%{acquirerPayId}" />
																									<tr>
																										<td align="center"
																											style="border-right: 1px solid #e4e4e4">
																											<s:iterator value="%{accountCurrencySet}"
																												status="currencyCounter">
																												<table width="99%" border="0"
																													cellspacing="0" cellpadding="0">
																													<tr>
																														<td align="left">
																															<div class="MerchBx">
																																<div class="invoC">
																																	<div class="fl_wrap">
																																		<label class='fl_label'>Currency
																																			Code</label>
																																		<s:textfield readonly="true"
																																			class="fl_input"
																																			id="%{#currencyCounter.count}"
																																			name="accountCurrencyList[%{#listCounter}].currencyName"
																																			type="text" value="%{currencyName}">
																																		</s:textfield>
																																	</div>
																																</div>
																																<div class="invoCP">
																																	<div class="fl_wrap">
																																		<label class='fl_label'>Mapped
																																			Payment Types</label>
																																		<s:textfield class="fl_input"
																																			id="%{#currencyCounter.count}"
																																			name="accountCurrencyList[%{#listCounter}].mappedPaymentTypes"
																																			type="text"
																																			value="%{mappedPaymentTypes}">
																																		</s:textfield>
																																	</div>
																																</div>



																																<s:hidden
																																	name="accountCurrencyList[%{#listCounter}].acqPayId"
																																	value="%{acqPayId}" />
																																<s:set var="listCounter"
																																	value="%{#listCounter+1}" />


																																<div class="clear"></div>
																															</div>
																														</td>
																													</tr>
																												</table>
																											</s:iterator>
																										</td>
																									</tr>
																									<tr>
																										<td height="30" align="center"
																											style="border-right: 1px solid #e4e4e4">
																											&nbsp;</td>
																									</tr>

																								</table>
																							</s:iterator>
																					</tr>
																				</table>
																			</div>
																		</div>
																		<div class="acordion-gray" id="HeadContactDetailsDiv">
																			<H4 class="pagehead"
																				onClick="CollapseAll('hide-this','ContactDetailsDiv');$('#ContactDetailsDiv').slideToggle('slow');">
																				Contact Details</H4>
																		</div>
																		<div id="ContactDetailsDiv">
																			<div class="indent">
																				<table width="100%" border="0" cellspacing="0"
																					cellpadding="7" class="formboxRR">
																					<tr>
																						<td>&nbsp;</td>
																					</tr>
																					<tr>
																						<td align="left" valign="top">
																							<div class="addfildn">
																								<div class="rkb">
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Contact</label>
																											<%-- <s:textfield id="mobile" name="mobile"
																												type="text" value="%{user.mobile}"
																												autocomplete="off"
																												style="font-weight: normal; font-size: 14px; margin-top:10px;"
																												onkeypress="javascript:return isNumber (event)"
																												pattern=".{10,10}" maxlength="10"
																												onkeyup="checkMobLength()">
																											</s:textfield> --%>

																											<s:textfield id="mobile" name="mobile"
																												type="text" value="%{user.mobile}"
																												autocomplete="off"
																												style="font-weight: normal; font-size: 14px; margin-top:10px;"
																												maxlength="100">
																											</s:textfield>
																										</div>
																										<span id="mobileValid"
																											style="color: red; display: none;">Please
																											Enter valid Contact</span>
																									</div>
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Telephone
																												No</label>
																											<s:textfield id="telephoneNo"
																												style="font-weight: normal; font-size:    14px; margin-top:10px;"
																												maxlength="10" name="telephoneNo"
																												type="text" value="%{user.telephoneNo}"
																												autocomplete="off"
																												onkeypress="javascript:return isNumber (event)">
																											</s:textfield>
																										</div>
																									</div>
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">City</label>
																											<s:textfield id="city" name="city"
																												style="font-weight: normal; font-size:    14px; margin-top:10px;"
																												type="text" value="%{user.city}"
																												onKeyPress="return ValidateAlpha(event);">
																											</s:textfield>
																										</div>
																									</div>
																									<div class="addfildn" style="margin-left: 5px;">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">
																												Country</label>
																											<s:select
																												list="@com.pay10.commons.util.BinCountryMapperType@values()"
																												value="defaultCountry" name="country"
																												id="country" class=" selctInpt" type="text"
																												style="margin-top:10px; font-weight:normal; font-size:14px; border: none; width:100% !important;"
																												listKey="name" listValue="name">
																											</s:select>
																										</div>
																									</div>
																									<div class="clear"></div>
																								</div>


																								<div class="rkb">
																									<!--<div class="addfildn"
										style="float: left; margin-bottom: 23px;">
										Active SMS Service:
										<div class="txtnew"
											style="float: left; margin: 0 10px 0 0">
											<s:checkbox name="transactionSmsFlag"
												value="%{user.transactionSmsFlag}" />
											<br>

										</div>
									</div>-->
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Address</label>
																											<s:textfield id="address"
																												style="font-weight: normal; font-size:14px; margin-top:10px; width:100%; word-wrap: break-word !important;"
																												name="address" type="text"
																												value="%{user.address}">
																											</s:textfield>
																										</div>
																									</div>
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">
																												State</label>
																											<%-- <s:select
														list="@com.pay10.commons.util.States@values()"
														value="defaultState"
														name="state" id="state"
														data-control="select2"
														class="form-select form-select-solid"
														type="text"
														style="margin-top:10px; font-weight:normal; font-size:14px; border: none; width:100% !important;"
														listKey="name"
														listValue="name">
													</s:select> --%>
																											<s:textfield id="state"
																												style="font-weight: normal; font-size:14px; margin-top:10px; width:100%; word-wrap: break-word !important;"
																												name="state" type="text"
																												value="%{user.state}"
																												onKeyPress="return ValidateAlpha(event);">
																											</s:textfield>
																										</div>
																									</div>
																									<div class="addfildn">
																										<br />
																										<div class="fl_wrap"
																											style="margin-top: -24px;">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Postal
																												Code</label>
																											<s:textfield id="postalCode"
																												style="font-weight: normal; font-size:14px; margin-top:10px;"
																												name="postalCode" type="text"
																												value="%{user.postalCode}"
																												autocomplete="off"
																												onkeypress="return ValidateMerchantAccountSetup(event);">
																											</s:textfield>
																										</div>
																									</div>


																									<div class="clear"></div>
																								</div>



																								<div class="clear"></div>
																							</div>
																						</td>
																					</tr>

																				</table>
																			</div>
																		</div>

																		<!-----Start Setting Tab---->

																		<div class="acordion-gray" id="HeadSettingDetailsDiv">
																			<H4 class="pagehead"
																				onClick="CollapseAll('hide-this','SettingDetailsDiv');
				$('#SettingDetailsDiv').slideToggle('slow');">
																				Notification Settings</H4>
																		</div>

																		<div id="SettingDetailsDiv">
																			<div class="indent">
																				<table width="99%" border="0" align="center"
																					cellpadding="0" cellspacing="0" class="formboxRR">
																					<tr>
																						<td height="30" align="left" valign="middle">
																							&nbsp;</td>
																					</tr>
																					<tr>
																						<td align="left" valign="middle">
																							<div class="tranjuctionSet">

																								<div class="rkb rkb-mg">
																									<div class="fl_wrap"
																										style="float: left; clear: right; margin-bottom: 23px;">
																										Active SMS Service:
																										<div class="txtnew"
																											style="float: left; margin: 0 10px 0 0">
																											<s:checkbox name="transactionSmsFlag"
																												value="%{user.transactionSmsFlag}" />
																											<br>

																										</div>
																									</div>
																								</div>
																							</div>
																							<div class="tranjuctionCon">
																								<h4>Notification Engine</h4>
																								<div class="trans-top">
																									<!-- <div class="rkb m-margin custo"></div> -->
																									<div class="col-sm-12 col-lg-6 addfildn">
																										Customer Authentication Emailer
																										<div
																											style="float: left; margin: 0px 7px 0px -1px">
																											<s:checkbox
																												name="transactionAuthenticationEmailFlag"
																												value="%{user.transactionAuthenticationEmailFlag}" />
																										</div>
																									</div>

																									<!-- <div class="rkb m-margin custo"> -->
																									<div class="col-sm-12 col-lg-6 addfildn">
																										Customer Transaction Emailer
																										<div
																											style="float: left; margin: 0px 7px 0px -1px">
																											<s:checkbox
																												name="transactionCustomerEmailFlag"
																												value="%{user.transactionCustomerEmailFlag}" />
																										</div>
																										<div class="clear"></div>
																									</div>
																									<!-- </div> -->
																									<!-- <div class="rkb m-margin custo"> -->
																									<div class="col-sm-12 col-lg-6 addfildn">
																										Customer Refund Emailer
																										<div
																											style="float: left; margin: 0px 7px 0px -1px">
																											<s:checkbox
																												name="refundTransactionCustomerEmailFlag"
																												value="%{user.refundTransactionCustomerEmailFlag}" />
																										</div>
																										<div class="clear"></div>
																									</div>
																									<!-- </div> -->
																									<!-- <div class="rkb m-margin ment"> -->
																									<div class="col-sm-12 col-lg-6 addfildn">
																										Merchant Transaction Emailer
																										<div style="float: left; margin: 0 7px 0 0">
																											<s:checkbox name="transactionEmailerFlag"
																												value="%{user.transactionEmailerFlag}" />
																										</div>
																									</div>
																									<!-- </div> -->
																									<!-- <div class="rkb m-margin ment"> -->
																									<div class="col-sm-12 col-lg-6 addfildn">
																										Merchant Refund Emailer
																										<div
																											style="float: left; margin: 0 7px 0 -1px;">
																											<s:checkbox
																												name="refundTransactionMerchantEmailFlag"
																												value="%{user.refundTransactionMerchantEmailFlag}" />
																										</div>
																									</div>
																									<div class="col-sm-12 col-lg-6 addfildn"
																										style="visibility: hidden;">
																										Merchant Refund Emailer
																										<div
																											style="float: left; margin: 0 7px 0 -1px;">
																											<s:checkbox
																												name="refundTransactionMerchantEmailFlag"
																												value="%{user.refundTransactionMerchantEmailFlag}" />
																										</div>
																									</div>
																									<!-- </div> -->

																									<!-- <div class="rkb m-margin ment"> -->
																									<div class="col-sm-12 col-lg-6 addfildn">
																										Transaction Failed Alert
																										<div style="float: left; margin: 0 7px 0 -1px">
																											<s:checkbox name="transactionFailedAlertFlag"
																												value="%{user.transactionFailedAlertFlag}" />
																										</div>
																									</div>
																									<!-- </div> -->

																									<!-- <div class="rkb m-email"> -->
																									<div class="col-sm-12 col-lg-12">
																										<div class="col-sm-12 col-lg-6 addfildn">
																											<div class="fl_wrap">
																												<label class='fl_label'
																													style="padding: 0; font-size: 13px; font-weight: 600;">Merchant
																													Transaction Email</label>
																												<s:textfield type="text"
																													id="transactionEmailId"
																													name="transactionEmailId"
																													value="%{user.transactionEmailId}"
																													style="font-weight: normal; font-size:14px; width:100%;"
																													onkeyup="validtransactionEmail()">
																												</s:textfield>

																												<span id="transactionError"
																													style="color: red; display: none;">Please
																													enter valid Email</span>
																											</div>
																										</div>
																										<!-- </div> -->

																										<!-- <div class="rkb m-email"> -->
																										<div class="col-sm-12 col-lg-6 addfildn">
																											<div class="fl_wrap">

																												<label class='fl_label'
																													style="padding: 0; font-size: 13px; font-weight: 600;">Merchant
																													Transaction SMS</label>
																												<s:textfield type="text" id="transactionSms"
																													name="transactionSms"
																													value="%{user.transactionSms}"
																													style="font-weight: normal; font-size:14px; margin-top:10px; width:100% !important; word-wrap: break-word; overflow-wrap: break-word;">
																												</s:textfield>
																											</div>
																										</div>
																									</div>
																									<!-- </div> -->

																									<div class="clear"></div>
																								</div>

																								<div class="clear"></div>
																							</div>





																						</td>
																					</tr>
																				</table>
																			</div>
																		</div>

																		<!-----End Setting Tab---->

																		<!-----Start Transaction Setting Tab---->

																		<div class="acordion-gray"
																			id="HeadTransactionSettingDetailsDiv">
																			<H4 class="pagehead"
																				onClick="CollapseAll('hide-this','TransactionSettingDetailsDiv');
				$('#TransactionSettingDetailsDiv').slideToggle('slow');">
																				Transaction Settings</H4>
																		</div>

																		<div id="TransactionSettingDetailsDiv">
																			<div class="indent">
																				<table width="99%" border="0" align="center"
																					cellpadding="0" cellspacing="0" class="formboxRR">
																					<tr>
																						<td height="30" align="left" valign="middle">
																							&nbsp;</td>
																					</tr>
																					<tr>
																						<td align="left" valign="middle">
																							<%-- 	<div class="tranjuctionSet">
										<!-- <div class="rkb rkb-mg"> -->
										<div class="col-sm-6 col-lg-3 addfildn"
											style="display: inline-flex;">

											Default Reporting Currency
											<div class="txtnew" style="">
												<s:select name="defaultCurrency"
													id="defaultCurrency"
													list="currencyMap"
													data-control="select2"
													class="form-select form-select-solid" />
											</div>
										</div>
										<!-- </div> -->

									</div> --%>
																							<div class="tranjuctionSet">
																								<!-- <div class="rkb rkb-mg"> -->
																								<div class="col-sm-6 col-lg-3 addfildn">
																									Payment Message Slab
																									<div class="txtnew" style="">
																										<s:select name="paymentMessageSlab"
																											id="paymentMessageSlab" headerKey="1"
																											list="#{'0':'0','1':'1','2':'2','3':'3','4':'4','5':'5'}"
																											style="height:35px" data-control="select2"
																											class="form-select form-select-solid"
																											value="%{user.paymentMessageSlab}">
																										</s:select>
																									</div>
																								</div>
																								<!-- </div> -->

																							</div>


																							<div class="tranjuctionCon">
																								<h4>Transactional Flag</h4>
																								<div class="trans-top">

																									<div class="col-sm-6 col-lg-3 tranjuctionCon4">

																										<div>
																											<label
																												style="float: right; margin: 0px 45px 24px -1px;">Merchant
																												Hosted Page</label>
																											<s:checkbox name="merchantHostedFlag"
																												value="%{user.merchantHostedFlag}" />
																										</div>
																									</div>
																									<!-- Added By Sweety -->
																									<div class="col-sm-6 col-lg-3 tranjuctionCon4">

																										<div>
																											<label
																												style="float: right; margin: 0px 73px 24px -1px;">Merchant
																												S2S Flag</label>
																											<s:checkbox name="merchantS2SFlag"
																												value="%{user.merchantS2SFlag}" />
																										</div>
																									</div>
																									<!-- Ended -->
																									<div class="col-sm-6 col-lg-3 tranjuctionCon4">

																										<div>
																											<label
																												style="float: right; margin: 0px 88px 24px -1px;">Iframe
																												Payment</label>
																											<s:checkbox name="iframePaymentFlag"
																												value="%{user.iframePaymentFlag}" />
																										</div>
																									</div>
																									<!-- <div class="col-sm-6 col-lg-3 tranjuctionCon4">

																										<div>
																											<label
																												style="float: right; margin: 0px 126px 24px -1px;">Surcharge</label>
																											<s:checkbox name="surchargeFlag"
																												value="%{user.surchargeFlag}" />
																										</div>
																									</div> -->
																									<div class="col-sm-6 col-lg-3 tranjuctionCon4">

																										<div>
																											<label
																												style="float: right; margin: 0px 77px 24px -1px;">Retry
																												Transaction</label>
																											<s:checkbox
																												name="retryTransactionCustomeFlag"
																												value="%{user.retryTransactionCustomeFlag}" />
																										</div>
																										<div class="clear"></div>
																									</div>
																									<div class="col-sm-6 col-lg-3 tranjuctionCon4">

																										<div class="d-flex">

																											<s:select name="attemptTrasacation"
																												id="attemptTrasacation" headerKey="1"
																												list="#{'1':'1','2':'2','3':'3','4':'4','5':'5'}"
																												style="height:35px" data-control="select2"
																												class="form-select form-select-solid"
																												value="%{user.attemptTrasacation}">
																											</s:select>
																											<label
																												style=" margin: 44px 10px; margin-top: -3px;">Number
																												of Retry</label>
																										</div>
																									</div>


																									<div class="clear"></div>
																								</div>

																								<div class="clear"></div>
																							</div>

																							<div class="col-sm-12 col-lg-6 tranjuctionCon">
																								<h2></h2>
																								<div class="trans-top">
																									<div class="tranjuctionCon4">

																										<div>
																											<label
																												style="float: right; margin: 0px 87px 24px -1px;">Express
																												Pay</label>
																											<s:checkbox name="expressPayFlag"
																												value="%{user.expressPayFlag}" />
																										</div>
																									</div>
																									<div class="col-sm-12 col-lg-6 tranjuctionCon4">

																										<div>
																											<label style="margin-right: 5px;">Card
																												Save Param</label>
																											<s:select name="cardSaveParam"
																												id="cardSaveParam" headerKey="1"
																												list="#{'CUST_EMAIL':'CUST_EMAIL','CUST_ID':'CUST_ID','CUST_PHONE':'CUST_PHONE'}"
																												data-control="select2"
																												class="form-select form-select-solid"
																												value="%{user.cardSaveParam}">
																											</s:select>
																										</div>
																									</div>
																								</div>
																								<div class="clear"></div>
																							</div>

																							<div class=" tranjuctionCon">
																								<h4>Order Id Flag</h4>
																								<div class="trans-top">

																									<div class="col-sm-6 col-lg-3 tranjuctionCon4">

																										<div>
																											<label
																												style="float: right; margin: 0px 45px 24px -1px;">Skip
																												OrderId for Refund</label>
																											<s:checkbox name="skipOrderIdForRefund"
																												value="%{user.skipOrderIdForRefund}" />
																										</div>
																										<div class="clear"></div>
																									</div>

																									<!------Allow Duplicates checkboxes added---->
																									<div class="col-sm-6 col-lg-3 tranjuctionCon4">

																										<div>
																											<label
																												style="float: right; margin: 0px 63px 24px -1px;">Allow
																												Duplicate Sale</label>
																											<s:checkbox name="allowSaleDuplicate"
																												id="allowSaleDuplicate" headerKey="1"
																												class="textFL_merch5"
																												value="%{user.allowSaleDuplicate}">
																											</s:checkbox>
																										</div>
																									</div>

																									<div class="col-sm-6 col-lg-3 tranjuctionCon4">

																										<div>
																											<label
																												style="float: right; margin: 0px 45px 24px -1px;">Allow
																												Duplicate Refund</label>
																											<s:checkbox name="allowRefundDuplicate"
																												id="allowRefundDuplicate" headerKey="1"
																												class="textFL_merch5"
																												value="%{user.allowRefundDuplicate}">
																											</s:checkbox>
																										</div>
																									</div>

																									<div class="col-sm-6 col-lg-3 tranjuctionCon4">

																										<div>
																											<label
																												style="float: right; margin: 0px 63px 24px -1px;">
																												Allow Sale In Refund</label>
																											<s:checkbox name="allowSaleInRefund"
																												id="allowSaleInRefund" headerKey="1"
																												class="textFL_merch5"
																												value="%{user.allowSaleInRefund}">
																											</s:checkbox>
																										</div>
																									</div>

																									<div class="col-sm-6 col-lg-3 tranjuctionCon4">

																										<div>
																											<label
																												style="float: right; margin: 0px 66px 24px -1px;">
																												Allow Refund In Sale</label>
																											<s:checkbox name="allowRefundInSale"
																												id="allowRefundInSale" headerKey="1"
																												class="textFL_merch5"
																												value="%{user.allowRefundInSale}">
																											</s:checkbox>
																										</div>
																									</div>

																									<div class="clear"></div>
																								</div>

																								<div class="clear"></div>
																							</div> <!-- modified by abhishek autorefund --> <!-- Added by Deep Singh Code start Here-->
																							<div class=" tranjuctionCon">
																								<h4>Channel</h4>
																								<div class="trans-top">
																									<div class="col-sm-6 col-lg-3 tranjuctionCon4">
																										<div>
																											<label
																												style="float: right; margin: 0px 210px 24px -1px;">Fiat</label>
																											<s:checkbox id="fiatId" name="fiatAllowed"
																												 checked="checked" disabled="true"
																												onchange="showHideDiv(this, true)" />
																										</div>
																										<div class="clear"></div>
																									</div>
																									<!------Allow Duplicates checkboxes added---->

																									<!-- CRYPTO CHECKBOX -->
																									<div class="col-sm-6 col-lg-3 tranjuctionCon4" style="display:none;">
																										<div>
																											<label
																												style="float: right; margin: 0px 154px 24px -1px;">Crypto</label>
																											<s:checkbox id="cryptoId"
																												onchange="showHideDiv(this, true)"
																												name="cryptoAllowed"
																												value="%{user.cryptoAllowed}"
																												class="textFL_merch5">
																											</s:checkbox>
																										</div>
																									</div>
																									<div class="clear"></div>
																								</div>
																								<div class="clear"></div>
																							</div> <!-- Added by Deep Singh Code end Here --> <!-- Added by Deep Singh Code start Here-->
																							<div class=" tranjuctionCon">
																								<h4></h4>
																								<div class="trans-top">
																									<div class="col-sm-6 col-lg-3 tranjuctionCon4">
																										<div>
																											<label
																												style="float: right; margin: 0px 185px 24px -1px;">Pay-in</label>
																											<input type="checkbox" id="pgId"
																												readonly="readonly" disabled="disabled"
																												name="user.PGFlag" checked="checked">
																										</div>
																										<div class="clear"></div>
																									</div>
																									<!------Allow Duplicates checkboxes added---->
																									<div class="col-sm-6 col-lg-3 tranjuctionCon4">
																										<div>
																											<label
																												style="float: right; margin: 0px 178px 24px -1px;">Pay-out</label>
																											<!-- <input type="checkbox" id="poId" name="user.POFlag"> -->
																											<s:checkbox name="user.POFlag" id="poId"
																												value="%{user.POFlag}" />
																										</div>
																									</div>
																									<div class="clear"></div>
																								</div>
																								<div class="clear"></div>
																							</div> <!-- Added by Deep Singh Code end Here -->




																							<div class=" tranjuctionCon">
																								<h4>Settlement Settings</h4>
																								<div class="trans-top">



																									<div class="col-sm-6 col-lg-3 tranjuctionCon9" style="display: none">

																										<div class="addfildn">
																											<div class="fl_wrap">
																												<label class='fl_label'
																													style="padding: 0; font-size: 13px; font-weight: 600;">Txn
																													Settlement Days:</label>
																												<s:textfield type="text"
																													id="settlementDays1" name="settlementDays1"
																													value="%{user.settlementDays}"
																													autocomplete="off"
																													style="font-weight: normal; font-size:14px; margin-top:10px;"
																													OnKeypress="javascript:return isAlphaNumeric(event,this.value)">
																												</s:textfield>
																											</div>
																										</div>
																									</div>


																									<div class="col-sm-6 col-lg-3 tranjuctionCon9">

																										<div class="addfildn">
																											<div class="fl_wrap">
																												<label class='fl_label'
																													style="padding: 0; font-size: 13px; font-weight: 600;">Liability
																													Hold (In %):</label>
																												<s:textfield type="text" id="liabilityHold"
																													name="liabilityHold"
																													value="%{user.liabilityHold}"
																													autocomplete="off"
																													style="font-weight: normal; font-size:14px; margin-top:10px;"
																													OnKeypress="javascript:return isAlphaNumeric(event,this.value)">
																												</s:textfield>
																											</div>
																										</div>
																									</div>


																									<div class="clear"></div>
																								</div>

																								<div class="clear"></div>
																							</div>

















																							<div class="tranjuctionCon">
																								<h4 class="trans-btm">Merchant Auto Refund</h4>
																								<div class="clear"></div>
																								<div class="col-sm-6 col-lg-3 addfildn">
																									<label class='fl_label'
																										style="padding: 0; font-size: 17px; font-weight: 600;">
																										Auto Refund</label> <br />
																									<s:checkbox name="autoFlag" id="auto"
																										style="display:none;" value="%{user.autoFlag}" />
																									<label class="switch"> <input
																										type="checkbox" id="autoRefund"
																										onchange="hideAndShowForAuto(this)"> <span
																										class="slider round"></span>
																									</label>
																								</div>
																								<div class="col-sm-6 col-lg-3 addfildn "
																									id="autoRefundHideAndShow">
																									<label class='fl_label'
																										style="padding: 0; font-size: 17px; font-weight: 600;">
																										Timer for AutoRefund (HH:MM)</label> <br />
																									<div
																										class="tranjuctionCon5 col-sm-12 col-lg-12">
																										<div class="addfildn">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600; border: 1px solid black">
																												<div class=" input-group clockpicker">
																													<s:textfield type="text" id="endTime"
																														name="autoMin" value="%{user.autoMin}"
																														autocomplete="off"
																														style="font-weight: normal; font-size:14px; margin-top:10px;"
																														maxlength="5"
																														onkeypress="return event.charCode >= 48 && event.charCode <= 57"
																														readonly="true">
																													</s:textfield>
																											</label>
																											<%-- <s:textfield
														type="text" id="endTime"
														name="autoMin"
														value="%{user.autoMin}"
														autocomplete="off"
														style="font-weight: normal; font-size:14px; margin-top:10px;"
														maxlength="5"
														onkeypress="return event.charCode >= 48 && event.charCode <= 57">
														</s:textfield> --%>
																										</div>

																									</div>
																								</div>
																							</div> <!-- modified by shashi extraRefundTextField -->
																							<div class="tranjuctionCon">
																								<h4 class="trans-btm">Refund Settings</h4>
																								<div class="clear"></div>
																								<div class="tranjuctionCon5">
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Extra
																												Refund Amount</label>
																											<s:textfield type="text"
																												id="extraRefundLimit"
																												name="extraRefundLimit"
																												value="%{user.extraRefundLimit}"
																												autocomplete="off"
																												style="font-weight: normal; font-size:14px; margin-top:10px;"
																												OnKeypress="javascript:return isAlphaNumeric(event,this.value)">
																											</s:textfield>
																										</div>
																									</div>
																								</div>

																								<div class="tranjuctionCon5">
																									<div class="addfildn">
																										<div class="fl_wrap"
																											style="border: none !important; padding-left: 28px;">
																											<label class='fl_label'
																												style="padding: 0px 29px 0px 0px; font-weight: 600; margin-top: 12px; margin-left: 43px;">Enable
																												Auto Refund Post Settlement</label>
																											<s:checkbox
																												name="enableAutoRefundPostSettlement"
																												value="%{user.enableAutoRefundPostSettlement}" />
																										</div>
																									</div>
																								</div>
																							</div>

																							</div>
																							<div class="tranjuctionCon"
																								style="display: none;">
																								<h4 class="trans-btm">Amex Seller Id and
																									MCC</h4>
																								<div class="clear"></div>
																								<div class="tranjuctionCon5">
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Seller
																												Id</label>
																											<s:textfield type="text" id="amexSellerId"
																												name="amexSellerId"
																												style="font-weight: normal; font-size:14px; margin-top:10px;"
																												value="%{user.amexSellerId}"
																												autocomplete="off"
																												OnKeypress="javascript:return isAlphaNumeric(event,this.value)">
																											</s:textfield>
																										</div>
																									</div>
																								</div>
																								<div class="tranjuctionCon2">&nbsp;</div>
																								<div class="tranjuctionCon5">
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Merchant
																												category code</label>
																											<s:textfield type="text" id="mCC"
																												style="font-weight: normal; font-size:14px; margin-top:10px;"
																												name="mCC" value="%{user.mCC}"
																												autocomplete="off"
																												OnKeypress="javascript:return isNumber(event,this.value)">
																											</s:textfield>
																										</div>
																									</div>
																								</div>
																							</div> <!-- White label url for merchant hosted -->
																							<div class="tranjuctionCon">
																								<h4 class="trans-btm">White Label URL</h4>
																								<div class="clear"></div>
																								<div class="tranjuctionCon5">
																									<div class="addfildn">
																										<div class="fl_wrap"
																											style="border: none !important">
																											<label class='fl_label'
																												style="padding: 14px 20px; font-weight: 600;">Enable
																												White Label URL</label>
																											<s:checkbox name="enableWhiteLabelUrl"
																												value="%{user.enableWhiteLabelUrl}" />
																										</div>
																									</div>
																								</div>
																								<div class="tranjuctionCon2">&nbsp;</div>
																								<div class="tranjuctionCon5">
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Domain
																												Name </label>
																											<s:textfield type="text" id="whiteLabelUrl"
																												style="font-weight: normal; font-size:14px; margin-top:10px;"
																												name="whiteLabelUrl"
																												value="%{user.whiteLabelUrl}"
																												autocomplete="off"></s:textfield>
																										</div>
																									</div>
																								</div>
																							</div> <!-- Notification API's for Merchant Txns -->
																							<div class="tranjuctionCon">
																								<h4 class="trans-btm">Notification API</h4>
																								<div class="clear"></div>
																								<div class="tranjuctionCon5">
																									<div class="addfildn">
																										<div class="fl_wrap"
																											style="border: none !important">
																											<label class='fl_label'
																												style="padding: 0px 29px 0px 0px; font-weight: 600; margin-top: 12px; margin-left: 23px;">Enable
																												Notification API</label>
																											<s:checkbox name="notificationApiEnableFlag"
																												value="%{user.notificationApiEnableFlag}"
																												id="notificationApiEnable" />
																										</div>
																									</div>
																								</div>
																								<div class="tranjuctionCon2">&nbsp;</div>
																								<div class="tranjuctionCon5"
																									id="notificaionApiDiv">
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">API
																												URL </label>
																											<s:textfield type="text" id="notificaionApi"
																												style="width: 100%;font-weight: normal; font-size:14px; margin-top:10px;"
																												name="notificaionApi"
																												value="%{user.notificaionApi}"
																												autocomplete="off"></s:textfield>
																										</div>
																									</div>
																								</div>
																							</div> <!-- Web Store Api  -->
																							<div class="tranjuctionCon">
																								<h4 class="trans-btm">WebStore API</h4>
																								<div class="clear"></div>
																								<div class="tranjuctionCon5">
																									<div class="addfildn">
																										<div class="fl_wrap"
																											style="border: none !important">
																											<label class='fl_label'
																												style="padding: 0px 29px 0px 0px; font-weight: 600; margin-top: 16px; margin-left: 23px;">Enable
																												WebStore API</label>
																											<s:checkbox name="webStoreApiEnableFlag"
																												value="%{user.webStoreApiEnableFlag}"
																												id="webStoreApiEnableFlag"
																												style="margin-top:5px;
																		" />

																											<s:hidden name="uuId" id="uuId"
																												value="%{user.uuId}" />
																										</div>
																									</div>
																								</div>




																							</div> <!--  --> <!-- Payment Link for Merchant -->
																							<div class="tranjuctionCon">
																								<h4 class="trans-btm">Merchant Payment Link</h4>

																								<div class="tranjuctionCon2">&nbsp;</div>
																								<div class="">
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Payment
																												Link </label> <input id="paymentLink"
																												onkeydown="document.getElementById('paymentLinkBtn').focus();"
																												type="text" style="display: block"
																												class="textFL_merch"
																												value=<%=new
										PropertiesManager().getSystemProperty("merchantPaymentLinkFormURL")%>>


																											<!-- <%=new PropertiesManager().getSystemProperty("merchantPaymentLinkFormURL")%><s:property value="user.payId"/> -->
																											<!-- <div align="left" valign="middle" class="bluelinkbig" style="font-size:12px;"><button id="invoiceLinkBtn" onclick="copyInvoiceLink('paymentLink')"  class="btn-custom" value="X">Copy Link</button></div>	-->
																										</div>
																										<!-- <div align="left" valign="middle" class="bluelinkbig" style="font-size:12px;"> -->
																										<a id="paymentLinkBtn"
																											onclick="copyPaymentLink('paymentLink')"
																											class="btn-custom" style="color: white"
																											value="X">Copy Link</a>
																										<!-- </div> -->
																									</div>
																								</div>
																							</div>
																						</td>
																					</tr>
																				</table>
																			</div>
																		</div>

																		<!-----End Transaction Setting Tab---->

																		<!--------------------------------------Naming Convention Tab Starts From Here----------------------->
																		<div class="acordion-gray"
																			id="HeadNamingConventionDetails">
																			<h4 class="pagehead"
																				onClick="CollapseAll('hide-this','NamingConventionDetails');$('#NamingConventionDetails').slideToggle('slow');">
																				Naming Convention</h4>
																		</div>
																		<div id="NamingConventionDetails">
																			<div class="indent">

																				<table width="100%" border="0" cellspacing="0"
																					cellpadding="7" class="formboxRR">
																					<tr>
																						<td>&nbsp;</td>
																					</tr>
																					<tr>
																						<td align="left" valign="top">
																							<div class="addfildn">
																								<div class="rkb">
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Settlement</label>
																											<s:textfield type="text"
																												id="settlementNamingConvention"
																												style="font-weight: normal; font-size:14px; margin-top:10px;
											width:100% !important;"
																												name="settlementNamingConvention"
																												value="%{user.settlementNamingConvention}"
																												autocomplete="off"
																												OnKeypress="javascript:return isAlphaNumericUnderScore(event,this.value)">
																											</s:textfield>

																										</div>

																									</div>

																									<div class="clear"></div>
																								</div>

																								<div class="rkb">
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Refund
																												Validation</label>
																											<s:textfield type="text"
																												id="refundValidationNamingConvention"
																												name="refundValidationNamingConvention"
																												value="%{user.refundValidationNamingConvention}"
																												autocomplete="off"
																												style="font-weight: normal; font-size:14px; margin-top:10px; width:100% !important;"
																												OnKeypress="javascript:return isAlphaNumericUnderScore(event,this.value)">
																											</s:textfield>
																										</div>

																									</div>

																									<div class="clear"></div>
																								</div>

																								<div class="clear"></div>
																							</div>
																						</td>
																					</tr>

																				</table>
																			</div>
																		</div>
																		<!------------------------------------------------------------------------------------------------------->
																		<div id='divLoader' style='display: none;'>
																			<img src="../image/giphy.gif" width="160px;"
																				height="160px;" alt="">
																		</div>
																		<div id="HeadCryptoDetailsDivContainer">

																			<div class="acordion-gray" id="HeadCryptoDetailsDiv">
																				<H4 class="pagehead"
																					onClick="CollapseAll('hide-this','CryptoDetailsDiv');$('#CryptoDetailsDiv').slideToggle('slow');">
																					Crypto Details</H4>
																			</div>

																			<div id="CryptoDetailsDiv" style="display: none;">
																				<div class="indent">
																					<table width="100%" border="0" cellspacing="0"
																						cellpadding="7" class="formboxRR">
																						<tr>
																							<td align="left" valign="top">
																								<div class="addfildn">
																									<div class="rkb">
																										<div class="addfildn">
																											<div class="fl_wrap">
																												<label class='fl_label'
																													style="padding: 0; font-size: 13px; font-weight: 600;">
																													Address </label>
																												<s:textfield type="text"
																													style="font-weight: normal; font-size:13px; margin-top:10px;"
																													id="cryptoAddress" name="cryptoAddress"
																													value="" autocomplete="off"
																													OnKeypress="javascript:return isAlphaNumeric(event,this.value)">
																												</s:textfield>
																											</div>
																											<div id="validateCryptoAddress"
																												class="crypto-address-error-message">Please
																												Enter Address</div>

																										</div>
																									</div>
																									<div class="rkb">
																										<div class="addfildn">
																											<div class="fl_wrap">
																												<label class='fl_label'
																													style="padding: 0; font-size: 13px; font-weight: 600;">
																													Block Chain </label>
																												<s:textfield type="text"
																													style="font-weight: normal; font-size:13px; margin-top:10px;"
																													id="blockChain" name="cryptoBlockChain"
																													value="" autocomplete="off"
																													OnKeypress="javascript:return isAlphaNumeric(event,this.value)">
																												</s:textfield>
																											</div>
																											<div id="validateCryptoBlockChain"
																												class="crypto-chain-error-message">Please
																												Enter Block Chain</div>
																										</div>
																									</div>
																									<div class="rkb">
																										<div class="addfildn">
																											<div class="fl_wrap">
																												<label class='fl_label'
																													style="padding: 0; font-size: 13px; font-weight: 600;">
																													Currency Type </label>
																												<div style="margin-top: 5px;">
																													<select id="cryptoCurrency"
																														name="cryptoCurrency" width='100%'
																														style="padding: 10px; margin-left: 5px; border-color: #666;">
																													</select>
																												</div>
																											</div>
																										</div>
																									</div>
																									<td>
																										<div style="margin-left: 5px;">
																											<input type="text" id="cryptoDetailId"
																												value="" style="visibility: hidden;">
																											<button type="button" id="saveCryptoDetails"
																												class="btn w-100 w-md-50 btn-primary">Save</button>
																											<button type="button" id="clearCryptoDetails"
																												class="btn w-100 w-md-50 btn-danger"
																												style="visibility: hidden;"
																												onclick="clearCryptoFields()">Clear</button>
																										</div>
																									</td>
																									<div class="clear"></div>
																									<div class="clear"></div>

																								</div>
																							</td>

																						</tr>

																					</table>
																				</div>
																				<div>
																					<table id="cryptoTable"
																						class="table table-bordered ">
																						<thead>
																							<th hidden>PayId</th>
																							<th hidden>Id</th>
																							<th>Sr No</th>
																							<th>Address</th>
																							<th>BlockChain</th>
																							<th>Created On</th>
																							<th>Action</th>
																						</thead>

																					</table>
																				</div>
																			</div>
																		</div>

																		<div id="HeadBankDetailsDivContainer">
																			<div class="acordion-gray" id="HeadBankDetailsDiv">
																				<H4 class="pagehead"
																					onClick="CollapseAll('hide-this','BankDetailsDiv');$('#BankDetailsDiv').slideToggle('slow');">
																					Fiat Details</H4>
																			</div>
																			<div id="BankDetailsDiv">
																				<div class="indent">
																					<% Logger logger=Logger.getLogger("MerchantAccountSetup");
							String currencyCodeAlpha="" ;
							String formattedAmount="" ;
							try {
								User sessionUser=(User) session.getAttribute(Constants.USER.getValue());
								String currencyNumeric=sessionUser.getDefaultCurrency();
								currencyCodeAlpha=Currency.getAlphabaticCode(currencyNumeric);
							} catch (Exception exception) {
								logger.error("Exception on Merchant Account Setup page " + exception);
								response.sendRedirect(" error");
							} %>
																					<table width="100%" border="0" cellspacing="0"
																						cellpadding="7" class="formboxRR">
																						<!-- <tr>
									<td>
										<div class="rkb rkb-mg">
											<div class="addfildn"
												style="float: left; clear: right; margin-bottom: 23px;">
												Split Payment:
												<div class="txtnew"
													style="float: left; margin: 0 10px 0 0">
													<s:checkbox
														name="splitPaymentStatus"
														id="splitPayment"
														value="%{user.splitPaymentStatus}" />
													<br>

												</div>
											</div>
										</div>
									</td>
								</tr> -->
																						<tr>
																							<td align="left" valign="top">
																								<div class="addfildn">
																									<div class="rkb">

																										<div class="addfildn" style="display: none;">
																											<div class="fl_wrap">
																												<input id="bankDetailsId" type="text">
																											</div>
																										</div>

																										<div class="addfildn">
																											<div class="fl_wrap">
																												<label class='fl_label'
																													style="padding: 0; font-size: 13px; font-weight: 600;">Bank
																													Name</label>
																												<s:textfield type="text" id="bankName"
																													name="bankName" value=""
																													style="font-weight: normal; font-size:14px; margin-top:10px;"
																													onKeyPress="javascript:return isAlphaNumeric(event)"
																													onkeyup="bankNameValidation()"
																													onchange="showValidateBtn()">
																												</s:textfield>
																											</div>
																											<span id="bankNameId"
																												style="color: red; display: none;">Please
																												Enter valid Bank Name</span>
																										</div>

																										<div class="addfildn">
																											<div class="fl_wrap">
																												<label class='fl_label'
																													style="padding: 0; font-size: 13px; font-weight: 600;">AC.
																													Holder Name</label>
																												<s:textfield type="text" id="accHolderName"
																													name="accHolderName"
																													value=""
																													style="font-weight: normal; font-size:14px; margin-top:10px;"
																													onKeyPress="javascript:return isAlphaNumeric(event)"
																													onkeyup="acHolderNameValidation()"
																													onchange="showValidateBtn()">
																												</s:textfield>
																											</div>
																											<span id="accHolderNameId"
																												style="color: red; display: none;">Please
																												Enter valid AC. Holder Name</span>
																										</div>

																										<!-- <div class="addfildn"
													style="display: none;">
													<div class="fl_wrap">
														<label class='fl_label'
															style="padding: 0; font-size: 13px; font-weight: 600;">bankVerificationStatus</label>
														<s:textfield type="text"
															style="font-weight: normal; font-size:14px; margin-top:10px;"
															id="bankVerificationStatus"
															name="bankVerificationStatus"
															value=""
															autocomplete="off">
														</s:textfield>
													</div>
												</div> -->

																										<!-- onpaste="return false;" -->

																										<div class="addfildn">
																											<div class="fl_wrap">
																												<label class='fl_label'
																													style="padding: 0; font-size: 13px; font-weight: 600;">Account
																													No</label>
																												<s:textfield type="text"
																													style="font-weight: normal; font-size:14px; margin-top:10px;"
																													id="accountNo" name="accountNo"
																													value=""
																													autocomplete="off"
																													onkeypress="return isNumberKey(event)"
																													pattern=".{9,18}" maxlength="18"
																													onkeyup="accountNoValidation()"
																													onchange="showValidateBtn()">
																												</s:textfield>
																											</div>
																											<span id="accountNoId"
																												style="color: red; display: none;">Please
																												Enter valid Account No.</span>
																										</div>




																										<div class="clear"></div>
																									</div>

																									<div class="rkb">
																										<div class="addfildn">
																											<div class="fl_wrap">
																												<label class='fl_label'
																													style="padding: 0; font-size: 13px; font-weight: 600;">IFSC
																													Code</label>
																												<s:textfield type="text"
																													style="font-weight: normal; font-size:14px; margin-top:10px;"
																													id="ifscCode" name="ifscCode"
																													value="" autocomplete="off"
																													onkeypress="return ValidateMerchantAccountSetup(event);"
																													onkeyup="ifscCodeValidation()"
																													onchange="showValidateBtn()">
																												</s:textfield>
																											</div>
																											<span id="ifscCodeId"
																												style="color: red; display: none;">Please
																												Enter valid IFSC Code.</span>
																										</div>


																										<!-- <div class="addfildn">
													<div class="fl_wrap">
														<label class='fl_label'
															style="padding: 0; font-size: 13px; font-weight: 600;">Branch
															Name</label>
														<s:textfield type="text"
															style="font-weight: normal; font-size:14px; margin-top:10px;"
															id="branchName"
															name="branchName"
															value=""
															autocomplete="off"
															OnKeypress="javascript:return isAlphaNumeric(event,this.value);">
														</s:textfield>
													</div>

													<span id="branchNameId"
														style="color: red; display: none;">Please
														Enter valid Branch Name</span>
												</div> -->

																										<div class="addfildn">
																											<div class="fl_wrap">
																												<label class='fl_label'
																													style="padding: 0; font-size: 13px; font-weight: 600;">Currency
																												</label>
																												<div style="margin-top: 10px;">
																													<select type="text"
																														id="currencyForBankDetails" width="100%"
																														style="padding: 5px; margin-left: 10px;">
																													</select> <span id="currencyError"
																														style="color: red; display: none;">Please
																														Select Currency</span>
																												</div>
																											</div>
																										</div>

																										<!-- <div class="addfildn">
													<div class="fl_wrap">
														<label class='fl_label'
															style="padding: 0; font-size: 13px; font-weight: 600;">Bank
															Currency</label>
														<s:textfield type="text"
															style="font-weight: normal; font-size:14px; margin-top:10px;"
															id="currency"
															name="currency"
															value="%{user.currency}"
															autocomplete="off"
															onkeypress="return lettersSpaceOnly(event, this) ;">
														</s:textfield>
													</div>
												</div> -->
																										<!-- <div class="addfildn">
													<div class="fl_wrap">
														<label class='fl_label'
															style="padding: 0; font-size: 13px; font-weight: 600;">Pan
															Card</label>
														<s:textfield type="text"
															style="font-weight: normal; font-size:14px; margin-top:10px;"
															id="panCard"
															name="panCard"
															value="%{user.panCard}"
															autocomplete="off"
															onkeypress="return ValidateMerchantAccountSetup(event);">
														</s:textfield>
													</div>
												</div> -->

																										<!-- <div
													class="addfildn productIdclass">
													<div class="fl_wrap">
														<label class='fl_label'
															style="padding: 0; font-size: 13px; font-weight: 600;">Product
															Id</label>
														<s:textfield type="text"
															style="font-weight: normal; font-size:14px; margin-top:10px;"
															id="productId"
															name="productId"
															onkeypress="return isAlphaNumeric(event);"
															autocomplete="off">
														</s:textfield>
													</div>
													<span id="productIdError"
														style="color: red; display: none;">Please
														Enter Product Id.</span>
												</div>
-->
																										<!-- <div class="addfildn">
													<div class="fl_wrap">
														<label class='fl_label'
															style="padding: 0; font-size: 13px; font-weight: 600;">
															Bank Country</label>
														<s:select
															list="@com.pay10.commons.util.BinCountryMapperType@values()"
															value="%{user.bankCountry}"
															name="bankCountry"
															id="bankCountry"
															class=" selctInpt"
															type="text"
															style="margin-top:10px; font-weight:normal; font-size:14px; border: none; width:100% !important;"
															listKey="name"
															listValue="name">
														</s:select>
													</div>
												</div> -->

																										<!--  modified by shashi for Default Currency -->
																										<!--<div class="addfildn">

									Default Reporting Currency<br />

										<s:select name="defaultCurrency"
											id="defaultCurrency" list="currencyMap"
											style="width:100px; display:inline;"
											class="form-control" />
-->
																									</div>

																									<div class="clear"></div>
																								</div>

																								<div class="clear"></div>

																								<div>

																									<div class="col-sm-6 col-lg-3"
																										style="display: inline-flex; margin-left: 10px;">

																										<input disabled="disable"  type="button" id="btnValidate"
																											name="btnValidate" variant="primary"
																											class="btn btn-primary btn-md" value="Save"
																											onClick="validate();">
																									</div>


																									<div class="col-sm-6 col-lg-3"
																										style="display: inline-flex;">

																										<input type="button" id="clearbankdetailbtn"
																											name="clearbankdetailbtn" variant="primary"
																											class="btn btn-primary btn-md" value="Clear"
																											onClick="resetFields();"
																											style="display: none;">
																									</div>

																									<!--  deepak-->

																								</div> <!-- <div class="col-sm-6 col-lg-3"
													style="display: inline-flex;">

													<input type="button"
														id="btnAddMore"
														name="btnAddMore"
														variant="primary"
														style="margin-right: 10px;"
														class="btn btn-primary btn-md"
														value="Add More"
														onClick="resetFields()">
												</div> -->

																								<div class="container-fluid">
																									<!-- formboxRR banksettleaccounts  -->
																									<table class="table table-bordered table-dark"
																										id="bankDetailsTableId">
																										<thead>
																											<tr>
																												<th style="display: none;">ID</th>
																												</th>
																												<th>Sr. No.</th>
																												<th>Bank Name</th>
																												<th>Account Number</th>
																												<th>IFSC Code</th>
																												<th>Holder Name</th>
																												<th>Action</th>
																												<th style="display: none;">Product ID</th>
																											</tr>
																										</thead>
																										<tbody align="center">

																										</tbody>

																									</table>
																								</div> <span id="validSucessMsg" class="alert"
																								style="color: green; display: none;">Verification
																									Successful</span> <span id="nameMsg" class="alert"
																								style="color: red; display: none;">Please
																									fill the all Mandatory Bank Details</span> <span
																								id="bankNameMsg" class="alert"
																								style="color: red; display: none;">Invalid
																									Bank Name</span> <span id="invalidAccNoMsg"
																								class="alert" style="color: red; display: none;">Invalid
																									Account Number</span> <span id="invalidIfscCodeMsg"
																								class="alert" style="color: red; display: none;">Invalid
																									IFSC Code</span> <span id="errorMsg" class="alert"
																								style="color: red; display: none;">Error</span>
																								</div>
																								</div>
																							</td>
																						</tr>

																					</table>
																				</div>
																			</div>
																		</div>


																		<div class="acordion-gray" id="HeadBusinessDetailsDiv">
																			<H4 class="pagehead"
																				onClick="CollapseAll('hide-this','BusinessDetailsDiv');$('#BusinessDetailsDiv').slideToggle('slow');">
																				Business Details</H4>
																		</div>

																		<div id="BusinessDetailsDiv">
																			<div class="indent">
																				<table width="99%" border="0" align="center"
																					cellpadding="0" cellspacing="0" class="formboxRR">
																					<tr>
																						<td height="30" align="left" valign="middle">&nbsp;
																						</td>
																					</tr>
																					<tr>
																						<td align="left" valign="middle">
																							<div class="addfildn">
																								<div class="rkb">
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Organisation
																												Type</label>
																											<s:select headerValue="Select Title"
																												headerKey=""
																												style="margin-top:10px; font-weight:normal; font-size:14px; border: none; width:100% !important;"
																												type="text" data-control="select2"
																												class="form-select form-select-solid"
																												id="organisationType"
																												name="organisationType"
																												value="%{user.organisationType}"
																												autocomplete="off"
																												list="#{'Proprietship':'Proprietship','Individual':'Individual','Partnership':'Partnership','Private Limited':'Private Limited','Public Limited':'Public Limited','LLP':'LLP','NGO':'NGO','Educational Institutes':'Educational Institutes','Trust':'Trust','Society':'Society','Freelancer':'Freelancer'}">
																											</s:select>

																										</div>
																									</div>
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Multicurrency
																												Payments Required?</label>

																											<s:select headerValue="Select" headerKey=""
																												style="margin-top:10px; font-weight:normal; font-size:14px; border: none; width:100% !important;"
																												type="text" data-control="select2"
																												class="form-select form-select-solid"
																												id="multiCurrency" name="multiCurrency"
																												value="%{user.multiCurrency}"
																												autocomplete="off"
																												list="#{'YES':'YES','NO':'NO'}">
																											</s:select>
																										</div>
																									</div>
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Operation
																												Address</label>
																											<s:textfield
																												style="font-weight: normal; font-size:14px; margin-top:10px;"
																												ttype="text" id="operationAddress"
																												name="operationAddress"
																												value="%{user.operationAddress}"
																												autocomplete="off"
																												OnKeypress="javascript:return isAlphaNumeric(event,this.value);">
																											</s:textfield>
																										</div>
																									</div>
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Operation
																												Address City</label>
																											<s:textfield
																												style="font-weight: normal; font-size:14px; margin-top:10px;"
																												type="text" id="operationCity"
																												name="operationCity"
																												value="%{user.operationCity}"
																												autocomplete="off"
																												onKeyPress="return ValidateAlpha(event);">
																											</s:textfield>
																										</div>
																									</div>
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Date
																												of Establishment</label>
																											<s:textfield
																												style="font-weight: normal; font-size:14px; margin-top:10px;"
																												type="text" id="dateOfEstablishment"
																												name="dateOfEstablishment"
																												value="%{user.dateOfEstablishment}"
																												autocomplete="off"
																												onkeydown="return DateFormat(this, event.keyCode)"
																												maxlength="10">
																											</s:textfield>
																										</div>
																									</div>
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">PAN</label>
																											<s:textfield
																												style="font-weight: normal; font-size:14px; margin-top:10px;"
																												type="text" id="pan" name="pan"
																												value="%{user.pan}" autocomplete="off"
																												OnKeypress="javascript:return isAlphaNumeric(event,this.value);">
																											</s:textfield>
																										</div>
																									</div>
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Expected
																												number of transaction</label>
																											<s:textfield
																												style="font-weight: normal; font-size:14px; margin-top:10px;"
																												type="text" id="noOfTransactions"
																												name="noOfTransactions"
																												value="%{user.noOfTransactions}"
																												autocomplete="off"
																												onkeypress="javascript:return isNumber (event)">
																											</s:textfield>
																										</div>
																									</div>

																									<div class="clear"></div>
																								</div>
																								<div class="rkb">
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Website
																												URL</label>
																											<s:textfield
																												style="font-weight: normal; font-size:14px; margin-top:10px;"
																												type="text" id="website" name="website"
																												value="%{user.website}">
																											</s:textfield>
																										</div>
																									</div>
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Business
																												Model</label>
																											<s:textfield
																												style="font-weight: normal; font-size:14px; margin-top:10px;"
																												type="text" id="businessModel"
																												name="businessModel"
																												value="%{user.businessModel}"
																												autocomplete="off"
																												OnKeypress="javascript:return isAlphaNumeric(event,this.value);">
																											</s:textfield>
																										</div>
																									</div>
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Operation
																												Address State</label>
																											<s:textfield class="fl_input" type="text"
												id="operationState" name="operationState"
												value="%{user.operationState}" autocomplete="off"
												onKeyPress="return ValidateAlpha(event);"></s:textfield>

																											<!--<s:select id="operationState"
																												name="operationState" data-control="select2"
																												class="form-select form-select-solid"
																												type="text"
																												style="margin-top:10px; font-weight:normal; font-size:14px; border: none; width:100% !important;"
																												list="@com.pay10.commons.util.States@values()"
																												value="defaultOperationState" listKey="name"
																												listValue="name" />-->
																										</div>
																									</div>
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Operation
																												Address Pincode</label>
																											<s:textfield
																												style="font-weight: normal; font-size:14px; margin-top:10px;"
																												type="text" id="operationPostalCode"
																												name="operationPostalCode"
																												value="%{user.operationPostalCode}">
																											</s:textfield>
																										</div>
																									</div>
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">CIN</label>
																											<s:textfield
																												style="font-weight: normal; font-size:14px; margin-top:10px;"
																												type="text" id="cin" name="cin"
																												value="%{user.cin}" autocomplete="off"
																												OnKeypress="javascript:return isAlphaNumeric(event,this.value);">
																											</s:textfield>
																										</div>
																									</div>
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Name
																												on PAN Card</label>
																											<s:textfield
																												style="font-weight: normal; font-size:14px; margin-top:10px;"
																												type="text" id="panName" name="panName"
																												value="%{user.panName}" autocomplete="off"
																												onpaste="return false"
																												OnKeypress="javascript:return isAlphaNumeric(event);">
																											</s:textfield>
																										</div>
																									</div>
																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Expected
																												amount of transaction</label>
																											<s:textfield
																												style="font-weight: normal; font-size:14px; margin-top:10px;"
																												type="text" id="amountOfTransactions"
																												name="amountOfTransactions"
																												value="%{user.amountOfTransactions}"
																												onkeypress="javascript:return isNumber1 (event)"
																												autocomplete="off">
																											</s:textfield>
																										</div>
																									</div>

																									<div class="addfildn">
																										<div class="fl_wrap">
																											<label class='fl_label'
																												style="padding: 0; font-size: 13px; font-weight: 600;">Merchant
																												Tax No</label>
																											<s:textfield
																												style="font-weight: normal; font-size:14px; margin-top:10px;"
																												type="text" id="merchantGstNo"
																												name="merchantGstNo"
																												value="%{user.merchantGstNo}"
																												OnKeypress="javascript:return isAlphaNumeric(event,this.value)"
																												autocomplete="off">
																											</s:textfield>
																										</div>
																									</div>
																									<%-- <div class="addfildn">
											<div class="fl_wrap">
												<label class='fl_label'>Language
													of payment
													page</label>
												<s:select
													list="@com.pay10.pg.core.util.LocaleLanguageType@values()"
													name="defaultLanguage"
													value="%{user.defaultLanguage}"
													listKey="code"
													listValue="name"
													class="form-control1 pull-right">
												</s:select>
											</div>
									</div> --%>
																									<div class="clear"></div>
																								</div>
																						</td>
																					</tr>
																				</table>
																			</div>
																		</div>


																		<!--Security Section-->

																		<div class="acordion-gray" id="HeadSecurityDiv">
																			<H4 class="pagehead"
																				onClick="CollapseAll('hide-this','SecurityDiv');$('#SecurityDiv').slideToggle('slow');">
																				Security</H4>
																		</div>

																		<div id="SecurityDiv">
																			<div class="indent">
																				<table width="99%" border="0" align="center"
																					cellpadding="0" cellspacing="0" class="formboxRR">
																					<tr>
																						<td height="30" align="left" valign="middle">
																							&nbsp;</td>
																					</tr>
																					<tr>
																						<td align="left" valign="middle">
																							<div class="tranjuctionCon">
																								<h4 class="trans-btm">2FA Security</h4>
																								<div class="clear"></div>
																								<div class="tranjuctionCon5">
																									<div class="addfildn">
																										<div class="fl_wrap"
																											style="border: none !important">
																											<label class='fl_label'
																												style="padding: 0px 29px 0px 0px; font-weight: 600; margin-top: 16px; margin-left: 23px;">Enable
																												2FA</label>
																											<s:checkbox style="margin-top: 5px;"
																												name="tfaFlag" value="%{user.tfaFlag}"
																												id="tfa" />
																										</div>
																									</div>
																								</div>
																							</div>
																						</td>
																						<td align="left" valign="middle">
																							<div class="tranjuctionCon">
																								<h4 class="trans-btm">2FA Reset</h4>
																								<div class="clear"></div>
																								<div class="tranjuctionCon5">
																									<div class="addfildn">
																										<div class="fl_wrap"
																											style="border: none !important">
																											<button id="resetTFA"
																												class="btn w-100 w-md-50 btn-primary"
																												type="button"
																												onclick="resetTFAuthentication()">Reset</button>
																										</div>
																										<Span></span>
																									</div>
																								</div>
																							</div>
																						</td>
																					</tr>
																				</table>
																			</div>
																		</div>

																		<!--Security Section-->
																		<!-- Web Store Api  -->


																		<!-- <div class="acordion-gray" id="HeadUploadedDocumentsDiv">
				<h4 class="pagehead"
					onClick="CollapseAll('hide-this','UploadedDocumentsDiv');$('#UploadedDocumentsDiv').slideToggle('slow');">
					Uploaded Documents</h4>
			</div>
			<div id="UploadedDocumentsDiv">
				<div class="indent">
					<div style="width: 100%;">
						<s:hidden name="onBoardDocListString" id="onBoardList"
							value="%{user.onBoardDocListString}"></s:hidden>
						<div id="merchantDownloadAllDocs"></div>
						<div id="merchantDocumentsDiv">
							<div class="uploadDocsTables" id="pvtPubLtdOrg">

								<div class="udtDiv">
									<table class="udtTable">
										<tr>
											<td class="uploadDocTableTd">
												Articles Of
												Association (AOA)</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="pvtltdAOA">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">
												Memorandum
												Of Association (MOA)</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="pvtltdMOA">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">Board
												resolution (Sample Attached)
											</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="pvtltdBR">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">List of
												Directors from MCA (Sample
												Attached)</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="pvtltdLDMCA">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">
												Certification
												Of Incorporation</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="pvtltdCOI">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">PAN of
												the
												Company.</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="pvtltdPANOC">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">GST
												Registration Certificate</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="pvtltdGSTRC">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">
												Undertaking
												of product/services to be sold
												(Sample
												Attached)</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="pvtltdUPSS">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">Current
												Address proof of the Company
												(Bank
												Statement/Utility Bill/Rent
												Agreement)</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="pvtltdCAPOC">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">
												Certificate
												of Commencement of Business (for
												public
												limited companies)</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="pvtltdCCOB">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">Service
												Agreement with PG</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="pvtltdSADINERO">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">
												Cancellation
												Cheque (in which account fund to
												be settled)</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="pvtltdCANCHQE">
											</td>
										</tr>


									</table>
								</div>
							</div>
							<div class="uploadDocsTables" id="parLlpOrg">

								<div class="udtDiv">
									<table class="udtTable">
										<tr>
											<td class="uploadDocTableTd">
												Partnership
												Deed</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="pardeePD">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">
												Certification
												Of Incorporation</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="pardeeCOI">
											</td>
										</tr>


										<tr>
											<td class="uploadDocTableTd">PAN of
												the
												Firm</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="pardeePANOF">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">GST
												Registration Certificate</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="pardeeGSTRC">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">
												Undertaking
												of product/services to be sold
												(Sample
												Attached)</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="pardeeUPSS">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">Current
												Address proof of the Company
												(Bank
												Statement/Utility Bill/Rent
												Agreement)</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="pardeeCAPOC">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">Service
												Agreement with PG</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="pardeeSADINERO">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">
												Cancellation
												Cheque (in which account fund to
												be settled)</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="pardeeCANCHQE">
											</td>
										</tr>
									</table>
								</div>
							</div>
							<div class="uploadDocsTables" id="proIndOrg">

								<div class="udtDiv">
									<table class="udtTable">
										<tr>
											<td class="uploadDocTableTd">Shop
												Establishment Certificate / MSME
												Registration
												Certificate/ Trade License etc
											</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="proshpSEC_MSME_TL"></td>
										</tr>
										<tr>
											<td class="uploadDocTableTd">GST
												Registration Certificate</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="proshpGSTRC">
											</td>
										</tr>
										<tr>
											<td class="uploadDocTableTd">
												Undertaking
												of product/services to be sold
												(Sample
												Attached)</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="proshpUPSS">
											</td>
										</tr>
										<tr>
											<td class="uploadDocTableTd">Current
												Address proof of bussiness
												Address (Bank
												Statement/Utility Bill/Rent
												Agreement)</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="proshpCAPBA">
											</td>
										</tr>
										<tr>
											<td class="uploadDocTableTd">Service
												Agreement with PG</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="proshpSADINERO">
											</td>
										</tr>
										<tr>
											<td class="uploadDocTableTd">
												Cancellation
												Cheque (in which account fund to
												be settled)</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="proshpCANCHQE">
											</td>
										</tr>
										<tr>
											<td class="uploadDocTableTd">Last 3
												Month's Current account Bank
												statement</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="proshpCABS">
											</td>
										</tr>

									</table>
								</div>
							</div>
							<div class="uploadDocsTables" id="tseigngoOrg">

								<div class="udtDiv">
									<table class="udtTable">

										<tr>
											<td class="uploadDocTableTd">Offices
												-Utility Bills or Rental
												Agreement if
												premises are on rent</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="tseigOFCUBRA">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">Company
												PAN
												card</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="tseigCOMPAN">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">
												Certificate
												issued under Companies Act or
												registration
												with Charity Commissioners
												office</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="tseigCICARCCO">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">Trust
												Deed /
												Memorandum Of Understanding /
												Society Deed /
												Government Certificate etc</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="tseigTDMOUSDGC">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">
												Resolution
												Deed on organization's
												letterhead</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="tseigRDOL">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">2 years
												ITR
												with audited balance sheet OR 1
												year current
												account statement</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="tseig2YRITR">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">In case
												of
												new establishment 6 month
												account statements
												of signing authority with one
												undertaking</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="tseigASOU">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">Bank
												Account
												Details on your company letter
												head</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="tseigBADCLH">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">Voided
												check</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="tseigVOIDCHQE">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">Service
												Agreement with PG</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="tseigSADINERO">
											</td>
										</tr>


									</table>
								</div>
							</div>
							<div class="uploadDocsTables" id="freelancersOrg">

								<div class="udtDiv">
									<table class="udtTable">
										<tr>
											<td class="uploadDocTableTd">Aadhar
												Card</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="frelanAC">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">Pan
												Card</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="frelanPC">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">Driving
												License/Passport</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="frelanDLORPASS">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">Address
												Proof : Nothing old than 3
												months - Utility
												Bill/Bank Statement/Insurance
												Cover</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="frelanAP">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">
												Cancellation
												Cheque (in which account fund to
												be settled)</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="frelanCANCHQE">
											</td>
										</tr>

										<tr>
											<td class="uploadDocTableTd">Service
												Agreement with PG</td>
											<td class="uploadDocTableTd"><input
													type="text"
													readonly="readonly"
													class="merchantDocName"
													id="frelanSADINERO">
											</td>
										</tr>


									</table>
								</div>
							</div>

						</div>
					</div>

				</div>
			</div> -->
																	</div>


																</td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
										</s:form> <s:hidden name="response" id="response"
											value="%{responseObject.responseCode}" /> <br />
										<table width="98%" align="center" border="0" cellspacing="0">
											<tr>
												<td align="center" valign="top">
													<div id="page-content" class="documentstop"
														style="padding-top: 10px;">



														<script type="text/javascript">
							document
								.getElementById('HeadActionDiv').className = 'acordion-gray acordion-open';
						</script>

													</div>
												</td>
											</tr>
										</table></td>
								</tr>
								<tr>
									<td align="center" valign="top">&nbsp;</td>
								</tr>

							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<script>
		$(document).on(
			'change',
			'.btn-file :file',
			function () {
				var input = $(this), numFiles = input.get(0).files ? input
					.get(0).files.length : 1, label = input.val()
						.replace(/\\/g, '/').replace(/.*\//, '');
				input.trigger('fileselect', [numFiles, label]);
			});

		$(document).ready(function () {
			var rDate="<s:property value='%{rDate}'/>";
			console.log("REG Date :"+rDate);
			var aDate="<s:property value='%{aDate}'/>";
			console.log("ACT Date :"+aDate);

			showHideDiv(document.getElementById("fiatId"));
			showHideDiv(document.getElementById("cryptoId"));
			var google2FAKeyFlag="<s:property value='%{google2FAKey}'/>";
		  	//If Two Factor Authentication is enabled only then show reset API key option
			if(google2FAKeyFlag=="true"){
				//console.log("api key true");
				$("#resetTFA").prop("disabled",true);
			}
			else{
				//console.log("api key false");
		 		$("#resetTFA").prop("disabled",false);
			}
  			//console.log("Flag "+google2FAKeyFlag);
			var tfaFlagCheck="<s:property value='%{user.tfaFlag}'/>";
			var cryptoFlag="<s:property value='%{user.cryptoAllowed}'/>"
			console.log(tfaFlagCheck);
			//console.log(typeof(tfaFlagCheck));
 			var payId='<s:property value="%{user.payId}"/>';
			console.log(payId);

			if(tfaFlagCheck=="true"){
				loadResetApiKeyTable(payId);
				$("#reset-key-card").show();
			}
			else{
				$("reset-key-card").hide();
			}
			loadCurrencyList(payId, function(response){

				console.log("Getting after Load CurrencyList");
		 		loadCurrencyForFiat(response);
				 loadBankDetails();
				console.log(response);
				getCryptoDetailList(payId);
			});
		/*	if(cryptoFlag=="true"){
				$("#cryptoTable").show();
			}
			else{
				$("#cryptoTable").show();
			}*/
			//Fetching crypto List

			$('.btn-file :file').on('fileselect', function (event, numFiles, label) {
				var input = $(this).parents(
					'.input-group').find(
						':text'), log = numFiles > 1 ? numFiles
							+ ' files selected'
							: label;

				if (input.length) {
					input.val(log);
				} else {
					if (log)
						alert(log);
				}

			});
			var notification_api = document.getElementById("notificationApiEnable");
			notification_api.addEventListener("change", function (e) {
				if (notification_api.checked) {
					document
						.getElementById('notificaionApiDiv').style.display = 'block';
				} else {
					document
						.getElementById('notificaionApiDiv').style.display = 'none';
				}

			});

		});

	</script>
	<script src="../js/main.js"></script>
	<script src="https://code.jquery.com/jquery-3.6.0.js"
		integrity="sha256-H+K7U5CnXl1h5ywQfKtSj8PCmoN9aaq30gDh27Xc0jk="
		crossorigin="anonymous"></script>

	<!--Date Time Picker-->
	<script>
		function showHideDiv(elem, checkClicked=false) {
			console.log($(elem)[0].checked);
			console.log($("#HeadBankDetailsDivContainer"));
			console.log($("#HeadCryptoDetailsDivContainer"));

			var fiatElem = $("#fiatId")[0];
			var cryptoElem = $("#cryptoId")[0];
			// if(checkClicked && !fiatElem.checked && !cryptoElem.checked){
			//     alert("Please check atleast one channel");
			//    $(elem).prop('checked', true);
			// 	console.log($(elem))
			// }
			if ($(elem)[0].id == "fiatId") {
				if ($(elem)[0].checked)
					$("#HeadBankDetailsDivContainer").show();
				else
					$("#HeadBankDetailsDivContainer").hide();
			} else {
				if ($(elem)[0].checked)
					$("#HeadCryptoDetailsDivContainer").show();
				else
					$("#HeadCryptoDetailsDivContainer").hide();
			}
		}

		function checkMobLength() {
			var mobPattern = /^[7-9][0-9]{9}/;
			var mobileNo = document.getElementById("mobile").value;
			var mobileNo = mobileNo.trim();
			if (!mobileNo.match(mobPattern)) {
				document.getElementById("mobileValid").style.display = "block";
				document.getElementById("btnSave").disabled = true;
			} else {
				document.getElementById("mobileValid").style.display = "none";
				document.getElementById("btnSave").disabled = false;
			}
		}

		function validReseller() {
			var regex = /^[0-9\b]+$/;
			var key = String.fromCharCode(!event.charCode ? event.which
				: event.charCode);
			if (!regex.test(key)) {
				event.preventDefault();
				return false;
			}
		}
		function validtransactionEmail() { //CALL THIS FUNCTION IN ONKEYUP---FOR MERCHANT TRANSACTION EMAIL
			var emailregex = /^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[_A-Za-z0-9-]+)$/;
			var transactionEmail = document
				.getElementById("transactionEmailId").value;
			if (transactionEmail.trim() !== "") {
				if (!transactionEmail.match(emailregex)) {
					document.getElementById("transactionError").style.display = "block";
					document.getElementById("btnSave").disabled = true;
				} else {
					document.getElementById("transactionError").style.display = "none";
					document.getElementById("btnSave").disabled = false;
				}
			} else {
				document.getElementById("transactionError").style.display = "none";
				document.getElementById("btnSave").disabled = false;
			}
		}

		function bankNameValidation() {
			var bankNamePattern = /^[a-z\s]+$/i;
			var bankName = document.getElementById("bankName").value;
			var bankName = bankName.trim();
			if (!bankName.match(bankNamePattern)) {
				document.getElementById("bankNameId").style.display = "block";
				document.getElementById("btnSave").disabled = true;
			} else {
				document.getElementById("bankNameId").style.display = "none";
				document.getElementById("btnSave").disabled = false;
			}
		}
		function acHolderNameValidation() {
			var accHolderNamePattern = /^[a-z\s]+$/i;
			var accHolderName = document.getElementById("accHolderName").value;
			var accHolderName = accHolderName.trim();
			if (!accHolderName.match(accHolderNamePattern)) {
				document.getElementById("accHolderNameId").style.display = "block";
				document.getElementById("btnSave").disabled = true;
			} else {
				document.getElementById("accHolderNameId").style.display = "none";
				document.getElementById("btnSave").disabled = false;
			}
		}
		function accountNoValidation() {
			var accountNoPattern = /[0-9]{9,18}/;
			var accountNo = document.getElementById("accountNo").value;
			var accountNo = accountNo.trim();
			if (!accountNo.match(accountNoPattern)) {
				document.getElementById("accountNoId").style.display = "block";
				document.getElementById("btnSave").disabled = true;
			} else {
				document.getElementById("accountNoId").style.display = "none";
				document.getElementById("btnSave").disabled = false;
			}
		}
		function ifscCodeValidation() {
			var ifscCodePattern = /^[A-Z]{4}[A-Z0-9]{7}$/;
			var ifscCode = document.getElementById("ifscCode").value;
			var ifscCode = ifscCode.trim();
			if (!ifscCode.match(ifscCodePattern)) {
				document.getElementById("ifscCodeId").style.display = "block";
				document.getElementById("btnSave").disabled = true;
			} else {
				document.getElementById("ifscCodeId").style.display = "none";
				document.getElementById("btnSave").disabled = false;
			}
		}

		// function productIdValidation() {

		// 	var productId = document.getElementById("productId").value.trim();

		// 	if (productId == "") {
		// 		document.getElementById("productIdError").style.display = "block";
		// 		document.getElementById("btnSave").disabled = true;
		// 	} else {
		// 		document.getElementById("productIdError").style.display = "none";
		// 		document.getElementById("btnSave").disabled = false;
		// 		btnValidationCall();
		// 		document.getElementById("btnSave").disabled = true;
		// 	}
		// }

		/** <Deepak Bind> Bank Details Validation */
		function hideLoader() {
			document.getElementById('divLoader').style.display = 'none';
		}
		function showLoader() {
			document.getElementById('divLoader').style.display = 'block';
		}
		function showValidateBtn() {
			document.getElementById("btnValidate").disabled = false;
		}
		/*  function hideMsg(){
			$('span.alert').hide(3500);

			}  */
		function validate() {
			var bankName = $("#bankName").val();
			var id=$("#bankDetailsId").val();
			var ifscCode = $("#ifscCode").val();
			var accHolderName = $("#accHolderName").val();
			var accountNumber=$("#accountNo").val();
			var currencySelected=$("#currencyForBankDetails").val();
			console.log("Selected currency For Bank: "+currencySelected);
			// if (bankName == "" || bankName == undefined) {
			// 	alert("Enter Provide Bank Name");
			// 	return false;
			// }
			// if (ifscCode == "" || ifscCode == undefined) {
			// 	alert("Enter Provide IFSC Code");
			// 	return false;
			// }
			// if (accHolderName == "" || accHolderName == undefined) {
			// 	alert("Enter Provide Account Holder Name");
			// 	return false;
			// }
			 if(!validateAccountNumber(accountNumber)||!validateIfscCode(ifscCode)||!validateName("acName",accHolderName)||!validateName("bankName",bankName)){
				return false;
			}
			if(currencySelected===''||currencySelected==null){
				$("#currencyError").show();
				return false;
			}
			else{
				$("#currencyError").hide();
			}
			document.getElementById("btnValidate").disabled = true;
			//document.getElementById("btnSave").disabled = true;

		//	var status = document.getElementById('splitPayment').checked;

			saveBankDetails(id,bankName,ifscCode,accHolderName,accountNumber,currencySelected);
			// if (status == true) {
			// 	productIdValidation();
			// }
			// if (status == false) {
			// 	btnValidationCall();
			// }
		}


		function validateAccountNumber(accountNumber){
			let regex = new RegExp(/^[0-9]{9,18}$/);

				if(regex.test(accountNumber)==true){
					$("#accountNoId").hide();
					return true;
				}
				else{
			 		$("#accountNoId").show();
					return false;
				}
		}
		function validateIfscCode(ifscCode){
			let regex=new RegExp(/^[A-Za-z]{4}[a-zA-Z0-9]{7}$/);
			if(regex.test(ifscCode)==true){
				$("#ifscCodeId").hide();
				return true;
			}
			else{
				$("#ifscCodeId").show();
				return false;
			}
		}
		function validateName(type,accountName){
			let regex=new RegExp(/^[A-Za-z ]+$/);
			if(regex.test(accountName)==true){
				if(type=="acName"){
					$("#accHolderNameId").hide();
				}
				else{
					$("#bankNameId").hide();
				}
			 	return true;
			}
			else{
			 	 	 if(type=="acName"){
					$("#accHolderNameId").show();
				}
				else{
					$("#bankNameId").show();
				}

			 	return false;
			}
		}

		function loadBankDetails(){

			currencySelected=$("#currencyForBankDetails").val();
			payId=$("#payId").val();

			if(currencySelected==''){
				return false;
			}
			var urls=new URL(window.location.href);
			var domain=urls.origin;
			var table=$("#bankDetailsTableId");
			var tBody=document.createElement('tbody');
			showLoader();
			$.ajax({
				type:'GET',
				url:domain+'/crmws/fiatDetails/getFiatDetails',
				data:{
		 			payId:payId,
					currency:currencySelected
				},
				success:function(response){
					console.log("Length ::"+response.length);
					if(response.length == 1){
						document.getElementById("btnValidate").disabled = true;
					}else if(response.length == 0){
						document.getElementById("btnValidate").disabled = false;
					}
					var count=1;
					$(tBody).empty();
					$("#bankDetailsTableId tbody").empty();
					for(var i in response){
			 			 	var data=response[i];
							console.log(response[i]);
							var row='<tr scope="row">';
							row+='<td style="display:none;">'+data.id+'</td>';
							row+='<td>'+(count++)+'</td>';
							row+='<td>'+data.bankName+'</td>';
							row+='<td>'+data.accountNumber+'</td>';
							row+='<td>'+data.ifscCode+'</td>';
							row+='<td>'+data.accountHolderName+'</td>';
						 	row+='<td><div style="display:inline-block;"><button type="button" onclick="editBankDetail(this)" class="editCryptoDetail"><i class="fa fa-edit" style="color:black;"></i></button></div>&nbsp;<div style="display:inline-block;"><button type="button" class="deleteBankDetail" onclick="deleteBankDetail(this)" ><i class="fa fa-trash"  style="color:red;"></i></button></div></td>';
							row+='<td style="display:none;">'+data.currency+'</td>'
							row+='</tr>';
							tBody.innerHTML+=$(row).html();
						}

						if(response.length==0){
							tBody.innerHTML='<tr><td colspan=6>No Matching Record Found!</td></tr>';
						}
						hideLoader();
						console.log(tBody);
					$("#bankDetailsTableId").append(tBody);

					},
				error:function(response){
					hideLoader();
					console.log(response);
				}
			});

		}



		function saveBankDetails(id,bankName,ifscCode,accHolderName,accountNumber,currency){
			showLoader();
			var payId=$("#payId").val();
			var urls=new URL(window.location.href);
			var domain=urls.origin;
			$.ajax({
				type:'POST',
				url:domain+'/crmws/fiatDetails/saveFiatDetails',
				contentType:'application/json',
				data:JSON.stringify({
					id:id,
					payId:payId,
					bankName:bankName,
					ifscCode:ifscCode,
					accountHolderName:accHolderName,
					accountNumber:accountNumber,
					currency:currency
				}),
				success:function(response){
					hideLoader();
					$("#clearbankdetailbtn").hide();
					saveResponse("Bank Details Saved successfully");
					loadBankDetails();
					resetFields();



				},
				error:function(response){
					hideLoader();
					errorResponse();
					console.log(response);


				}
			});
		}

		function deleteBankDetail(row){
		 	var id=$($(row).closest('tr').find('td')[0]).html();
			console.log(id);
		 	var urls=new URL(window.location.href);
			var domain=urls.origin;
			showLoader();
			$.ajax({
				type:'POST',
				url:domain+'/crmws/fiatDetails/deleteFiatDetails',
				data:{
					id:id
		 		},
				success:function(response){
					loadBankDetails();
					hideLoader();
					saveResponse("Deleted SuccessFully");

				},
				error:function(response){
					hideLoader();
					errorResponse();
					console.log(response);
				}
			});

		}
		function editBankDetail(e){
			var id=$($(e).closest('tr').find('td')[0]).html();
			var bankName=$($(e).closest('tr').find('td')[2]).html();
			var accountNumber=$($(e).closest('tr').find('td')[3]).html();
			var ifscCode=$($(e).closest('tr').find('td')[4]).html();
			var acHolderName=$($(e).closest('tr').find('td')[5]).html();
			var currency=$($(e).closest('tr').find('td')[7]).html();

			$("#bankDetailsId").val(id);
			$("#bankName").val(bankName);
			$("#ifscCode").val(ifscCode);
			$("#accHolderName").val(acHolderName);
			$("#accountNo").val(accountNumber);
			$("#currencyForBankDetails").val(currency);

			$("#clearbankdetailbtn").show();

		}


		function resetTFAuthentication(){
		 		var emailIdData=$("#emailId").val();
			 	var token = document.getElementsByName("token")[0].value;
		 	$.ajax({
				type:"POST",
				url:"merchantResetTFA",
				data:{
					emailId:emailIdData,
			 		token: token,
					"struts.token.name": "token"
				},
				success:function(response){
                    Swal.fire({
  					 	icon: "success",
 						title: "2FA Reset Succesfully Done",
 						showConfirmButton: false,
 						timer: 1500
					});
					$("#resetTFA").prop('disabled',true);
					console.log(response);
				},
				error:function(response){
					Swal.fire({
  						icon: "error",
  						title: "Oops...",
  						text: "2FA Reset Failed",
 						showConfirmButton: false,
 						timer: 1500
					});
                 	console.log(response);
				}


			});
		}
		function btnValidationCall() {
			var data = {
				"name": $('#accHolderName').val(),
				"bankName": $('#bankName').val(),
				"accountNo": $('#accountNo').val(),
				"ifscCode": $('#ifscCode').val(),
			}
			//var url = "http://localhost:8080/crmws/pennyDrop";
			var urls = new URL(window.location.href);
			var domain = urls.origin;
			//alert(domain);
			//$('span.alert').delay(3700);
			// $('span.alert').hide(3000);

			showLoader();
			$
				.ajax({

					type: "POST",
					url: domain + "/crmws/pennyDrop",
					//url : url,
					//url : "lineChartAction",
					contentType: 'application/json',
					data: JSON.stringify(data),
					success: function (response) {
						var message;
						/* var statusErrorMap = {
							'1000' : "Verification Successful",
							'1002' : "Invalid Account Holder Name",
							'1003' : "Invalid Bank Name",
							'1004' : "Invalid Account Number",
							'1005' : "Invalid IFSC Code",
							'1001' : "Error"
						};
							*/
						var respObj = JSON.parse(response);
						//alert(response);
						if (respObj.statusCode == 1000) {
							document.getElementById("nameMsg").style.display = "none";
							document.getElementById("bankNameMsg").style.display = "none";
							document.getElementById("invalidAccNoMsg").style.display = "none";
							document.getElementById("invalidIfscCodeMsg").style.display = "none";
							document.getElementById("errorMsg").style.display = "none";

							//save details in banksettle table after verification completed when splitPayment is checked
							var splitPayment = document
								.getElementById("splitPayment");

							if (splitPayment.checked == true) {
								$('.banksettleaccounts').find('tbody')
									.empty();
								saveBankSettleDetail();
							}

							hideLoader();
							//hideMsg();
							$('span.alert').delay(500);
							$('span.alert').hide(5000);
							document.getElementById("btnValidate").disabled = true;
							document.getElementById("validSucessMsg").style.display = "block";
							document
								.getElementById("bankVerificationStatus").value = true;

							document.getElementById("btnSave").disabled = false;
							return saveBtnValidation = true;
						}
						if (respObj.statusCode == 1002) {
							document.getElementById("validSucessMsg").style.display = "none";
							document.getElementById("bankNameMsg").style.display = "none";
							document.getElementById("invalidAccNoMsg").style.display = "none";
							document.getElementById("invalidIfscCodeMsg").style.display = "none";
							document.getElementById("errorMsg").style.display = "none";
							$('span.alert').delay(500);
							$('span.alert').hide(5000);
							//hideMsg();
							document.getElementById("btnValidate").disabled = true;
							hideLoader();
							document.getElementById("nameMsg").style.display = "block";

							document.getElementById("btnSave").disabled = false;
						}
						if (respObj.statusCode == 1003) {
							document.getElementById("nameMsg").style.display = "none";
							document.getElementById("validSucessMsg").style.display = "none";
							document.getElementById("invalidAccNoMsg").style.display = "none";
							document.getElementById("invalidIfscCodeMsg").style.display = "none";
							document.getElementById("errorMsg").style.display = "none";
							$('span.alert').delay(500);
							$('span.alert').hide(5000);
							//hideMsg();
							document.getElementById("btnValidate").disabled = true;
							hideLoader();
							document.getElementById("bankNameMsg").style.display = "block";

							document.getElementById("btnSave").disabled = false;
						}
						if (respObj.statusCode == 1004) {
							document.getElementById("bankNameMsg").style.display = "none";
							document.getElementById("nameMsg").style.display = "none";
							document.getElementById("validSucessMsg").style.display = "none";
							document.getElementById("invalidIfscCodeMsg").style.display = "none";
							document.getElementById("errorMsg").style.display = "none";
							//hideMsg();
							$('span.alert').delay(2000);
							$('span.alert').hide(5000);
							document.getElementById("btnValidate").disabled = true;
							hideLoader();
							//hideMsg();
							document.getElementById("invalidAccNoMsg").style.display = "block";

							document.getElementById("btnSave").disabled = false;
						}
						if (respObj.statusCode == 1005) {
							document.getElementById("invalidAccNoMsg").style.display = "none";
							document.getElementById("bankNameMsg").style.display = "none";
							document.getElementById("nameMsg").style.display = "none";
							document.getElementById("validSucessMsg").style.display = "none";
							document.getElementById("errorMsg").style.display = "none";
							//hideMsg();
							$('span.alert').delay(500);
							$('span.alert').hide(5000);
							document.getElementById("btnValidate").disabled = true;
							hideLoader();
							document.getElementById("invalidIfscCodeMsg").style.display = "block";

							document.getElementById("btnSave").disabled = false;
						}
						if (respObj.statusCode == 1001) {
							document.getElementById("invalidAccNoMsg").style.display = "none";
							document.getElementById("bankNameMsg").style.display = "none";
							document.getElementById("nameMsg").style.display = "none";
							document.getElementById("validSucessMsg").style.display = "none";
							document.getElementById("invalidIfscCodeMsg").style.display = "none";
							//hideMsg();
							$('span.alert').delay(500);
							$('span.alert').hide(5000);
							document.getElementById("btnValidate").disabled = true;
							hideLoader();
							document.getElementById("errorMsg").style.display = "block";

							document.getElementById("btnSave").disabled = false;
						}

					},
				});
		}
		document.addEventListener("DOMContentLoaded",function(){
			var day=new Date();
		  	day.setDate(day.getDate()+1);
			console.log(day);
		  //	=day.toISOString().slice(0,10);
			  var timeFormat=day.toDateString().slice(4,16);

			 console.log(timeFormat);
			//console.log(typeof(timeFormat));
			$("#datePicker").attr("min",timeFormat);
	 		$("#datePicker").val(timeFormat);
			$("#time").val("00:00");
		});

				function CopyToClipboard(id)
				{
					var r = document.createRange();
					r.selectNode(document.getElementById(id));
					window.getSelection().removeAllRanges();
					window.getSelection().addRange(r);
					document.execCommand('copy');
					window.getSelection().removeAllRanges();
				}
				function loadResetApiKeyTable(payId){
					var urls = new URL(window.location.href);
					var domain = urls.origin;
					var table=$("#encryptionKeyTable");
					var tableBody=document.createElement('tbody');
					 $.ajax({
						type:"GET",
						url:domain+"/crmws/resetEncryptionKey/getEncryptionList",
						data:{
							payId:payId
						},
					 	success:function(response){
							tableBody.innerHTML='<tr><td colspan=6>Loading...<td></tr>';
						},
						complete:function(response){
							console.log(response.responseJSON);
							var responseData=response.responseJSON;
							var count=1;
							$(tableBody).empty();
							$("#encryptionKeyTable tbody").empty();
			 				for(var i in responseData){
							var	row='<tr>';
								console.log(i);
								row+="<td hidden>"+responseData[i].id+"</td>";
								row+="<td>"+(count++)+"</td>";
								row+="<td>"+responseData[i].encryptionKey+"</td>";
								row+="<td hidden>"+responseData[i].salt+"</td>";
								row+="<td hidden>"+responseData[i].keySalt+"</td>";
								row+="<td>"+responseData[i].startDate+"</td>";
								var endDate="";
								if(responseData[i].endDate=="null"){
								 	row+="<td>"+endDate+"</td>";
								}
								else{
									row+="<td>"+responseData[i].endDate+"</td>";
								}
								row+="<td>"+responseData[i].status+"</td>";
								row+='</tr>';
								tableBody.innerHTML+=$(row).html();
							}
							if(responseData.length==0)
							{
								tableBody.innerHTML='<tr><td colspan=6>No Matching Record Found!<td></tr>';
							}
	 							table.append(tableBody);
				 		},
						error:function(response){
							console.log(response.status);

						}
					});
				}




		//this is used to save bandSettle detail in table
		function saveBankSettleDetail() {

			var bankName = document.getElementById("bankName").value;
			var accHolderName = document.getElementById("accHolderName").value;
			var accountNo = document.getElementById("accountNo").value;
			var ifscCode = document.getElementById("ifscCode").value;
			var productId = document.getElementById("productId").value;
			var panCard = document.getElementById("panCard").value;
			var emailId = document.getElementById('emailId').value;

			$.post("splitPaymentSettleSave", {
				'bankSettle.bankName': bankName,
				'bankSettle.accountHolderName': accHolderName,
				'bankSettle.accountNumber': accountNo,
				'bankSettle.ifscCode': ifscCode,
				'bankSettle.productId': productId,
				'bankSettle.panCard': panCard,
				'bankSettle.email': emailId,
			}, function (result) {
				if (result.msg != "insert successfully") {
					alert("Productid/Account Number already Exist.");
				}
				getAllAccountListForSettle();
			});
		}
		function saveResponse(msg){
			Swal.fire({
  					 	icon: "success",
 						title: "Done",
						text:msg,
 						showConfirmButton: false,
 						timer: 1500
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



		function loadCurrencyList(payId, callback){
			if(payId==""){
				return false;
			}
			var urls=new URL(window.location.href);
			var currencyName=$("#cryptoCurrency").val();
			var domain = urls.origin;
			$.ajax({
				type:"GET",
				url: domain+"/crmws/cryptoDetails/getCurrencyList",
				data:{
					payId:payId,
			 	},
				success:function(response){
					var content='<option value="" disable selected>Select Currency</option>';
					console.log(response);
					for(let i in response){
					//	console.log(i+"_"+response[i]);
						content+='<option value="'+i+'">'+response[i]+'</option>'
					}
					if(response.length==0){
						content='<option value"" selected disabled>No Record Found</option>';
					}
					$("#cryptoCurrency").append(content);
					callback(response);

				},
				error:function(response){
					alert("Something Went Wrong while Fetching Currency");
				}
			});
		}

		function loadCurrencyForFiat(response){
			var option='<option value="" disabled selected>Select Currency</option>';
			for(let i in response){
				option+='<option value="'+i+'">'+response[i]+'</option>';
			}
			$("#currencyForBankDetails").append(option);

		}


		$("#saveCryptoDetails").click(function(){
			console.log("clicked!");
		 	var clearCryptoDetails=$("#clearCryptoDetails");
			clearCryptoDetails.css("visibility","hidden");
			var urls = new URL(window.location.href);
			var domain = urls.origin;
			var payId=$("#payId").val();
			var currencyName=$("#cryptoCurrency").val();
			var cryptoId=$("#cryptoDetailId").val();
			console.log("ID Crypto: "+cryptoId);
			var address=$("#cryptoAddress").val();
			var blockChain=$("#blockChain").val();
			var validateAddress=$("#validateCryptoAddress");
			var validateBlockChain=$("#validateCryptoBlockChain");

			if(!validateCrypto(address,blockChain,validateAddress,validateBlockChain,currencyName)){
				return false;
			}

				$.ajax({
				type:"POST",
				url:domain+"/crmws/cryptoDetails/saveCryptoDetails",
				contentType: "application/json",
				// accepts: "application/json",
				data:JSON.stringify({
					payId:payId,
					id:cryptoId,
					address:address,
					blockchain:blockChain,
					currency:currencyName
				}),
				success : function(response){
					clearCryptoFields();
					saveResponse("Crypto Details saved Successfully!");
					getCryptoDetailList(payId);
					console.log(response);
				},
				error:function(response){
					console.log(response);
					errorResponse();
					getCryptoDetailList(payId);

				}

			});
	});

	$("#currencyForBankDetails").change(function(){
		console.log("Clicked");
		loadBankDetails();
	});

	function validateCrypto(address,blockChain,validateAddress,validateBlockChain,currency){
			if(address===""){
				console.log("Address is empty");
				validateAddress.show();
				$("#cryptoAddress").addClass("cryptoAddress-error");
				return false;
		 	}
			else{
				validateAddress.hide();
				$("#cryptoAddress").removeClass("cryptoAddress-error");

		 	}
			if(currency==''||currency==null){
				$("#cryptoCurrency").addClass("crypto-currency-error");
				return false;
			}
			else{
				$("#cryptoCurrency").removeClass("crypto-currency-error");

			}
			if(blockChain===""){
				console.log("BlockChain is empty");
				$("#blockChain").addClass("blockChain-error");
				validateBlockChain.show();
				return false;
				}
			else{
				 validateBlockChain.hide();
				$("#blockChain").removeClass("blockChain-error");
					return true;
				}
	}
	$("#cryptoCurrency").change(function(){

		getCryptoDetailList($("#payId").val());
	});


		function getCryptoDetailList(payId){
		 	var urls = new URL(window.location.href);
			var domain = urls.origin;
			var cryptoTable=$("#cryptoTable");
			var currencyName=$("#cryptoCurrency").val();
			var tableBody=document.createElement('tbody');
			tableBody.id="cryptoTableBody";
			console.log(tableBody);
			showLoader();
		  	$.ajax({
				type:"GET",
				url:domain+"/crmws/cryptoDetails/getCryptoList",
				contentType:'application/json',
				data:
				{
					payId:payId,
					currency:currencyName
				},
				success: function(){
					console.log("Success");
	 				tableBody.innerHTML='<tr><td colspan=4>Loading...<td></tr>';
				},
				complete:function(response){
				 	var count=1;
					$(tableBody).empty();
					 $("#cryptoTable tbody").empty();
					var responseData=response.responseJSON;
					for(var i in responseData){
						console.log(responseData[i].id);
						console.log(responseData[i].payId);
						var row='<tr>';
							row+='<td hidden>'+responseData[i].payId +'</td>';
							row+='<td hidden>'+responseData[i].id +'</td>';
							row+='<td>'+(count++)+'</td>';
							row+='<td>'+responseData[i].address +'</td>';
							row+='<td>'+responseData[i].blockchain +'</td>';
							row+='<td>'+responseData[i].createdOn +'</td>';
							row+='<td><div style="display:inline-block;"><button type="button" onclick="editCrypto(this)" class="editCryptoDetail"><i class="fa fa-edit" style="color:black;"></i></button></div>&nbsp;&nbsp;&nbsp;<div style="display:inline-block;"><button type="button" class="deleteCryptoDetail" onclick="deleteCrypto(this)" ><i class="fa fa-trash"  style="color:red;"></i></button></div></td>';
							row+='</tr>';
							tableBody.innerHTML+=$(row).html();

					}
					if(responseData.length==0)
					{
						hideLoader();
						tableBody.innerHTML='<tr><td colspan=4>No Matching Record Found!<td></tr>';
					}
					cryptoTable.append(tableBody);
					hideLoader();
				//	console.log(responseData);
				},
				error: function(response){
					hideLoader();
					console.log(response);
				}
			});
	 	}

		function editCrypto(e){
			console.log("clicked");
			 var id=$($(e).closest('tr').find('td')[1]).html();
			var address=$($(e).closest('tr').find('td')[3]).html();
			var blockChain=$($(e).closest('tr').find('td')[4]).html();
			$("#clearCryptoDetails").css("visibility","visible");
			$("#cryptoAddress").removeClass("cryptoAddress-error");
			$("#blockChain").removeClass("blockChain-error");
			$("#validateCryptoAddress").hide();
			$("#validateCryptoBlockChain").hide();
		 	var setId=$("#cryptoDetailId");
			var setAddress=$("#cryptoAddress");
			var setBlockChain=$("#blockChain");
			setId.val(id);
			setAddress.val(address);
			setBlockChain.val(blockChain);
			console.log(id);
			console.log(address);
			console.log(blockChain);
		}

		function deleteCrypto(e){
			console.log("deleting");
			var urls = new URL(window.location.href);
			var domain = urls.origin;
			var currencyName=$("#cryptoCurrency").val();
			var cryptoId=$($(e).closest('tr').find('td')[1]).html();
			console.log(cryptoId);
			showLoader();
			$.ajax({
				type:"POST",
				url:domain+"/crmws/cryptoDetails/deleteCryptoDetail",
				data:{
					id:cryptoId,
					currency:currencyName
				},
				success:function(response){
					console.log(response);
					var payId=$("#payId").val();
					getCryptoDetailList(payId);
					saveResponse("Deleted Successfully");
					hideLoader();
				},
				error:function(response){
					errorResponse();
					console.log(response);
					hideLoader();
				}
			});
		}

		function clearCryptoFields(){
			$("#cryptoDetailId").val("");
			$("#cryptoAddress").val("");
			$("#blockChain").val("");
	 		$("#cryptoCurrency").val("");
		}


	</script>
	<script type="text/javascript">
		$('.clockpicker').clockpicker({
			placement: 'top',
			donetext: 'Done',
			twelvehour: false
		});
		$(".main-panel").scroll(function () {
			debugger
			$('.popover').hide();
		});
	</script>

	<script type="text/javascript">
		$(document).ready(function () {
			document.getElementById('industryCategory1').style.pointerEvents = 'none';

			$('#splitPayment').on('change', function () {
				if (this.checked == true) {
					$('#btnAddMore').show();
					$('.productIdclass').show();
					$(".mytable").show();

				} else {
					$('#btnAddMore').hide();
					$('.productIdclass').hide();
					$(".mytable").hide();
				}

			});
		});

		//Added By Sweety
		$('#tnc').on('change', function () {
			if (this.checked == true) {
				//both text field need to visible
				document.getElementById('tnc').value = true;
				$("#paytextdiv").css("display", "block");
				$("#invoicetextdiv").css("display", "block");
			} else {
				//both text field need to invisible
				document.getElementById('tnc').value = false;
				$("#paytextdiv").css("display", "none");
				$("#invoicetextdiv").css("display", "none");
			}

		});

		function resetFields() {
			$("#bankDetailsId").val("");
			document.getElementById("bankName").value = "";
			document.getElementById("accHolderName").value = "";
			document.getElementById("accountNo").value = "";
			document.getElementById("ifscCode").value = "";
	//		document.getElementById("productId").value = "";
	//		document.getElementById("panCard").value = "";
			document.getElementById("currencyForBankDetails").value = "";
	//		document.getElementById("branchName").value = "";

			//enable if add more button is clicked validation button is enable
			document.getElementById("btnValidate").style.display = 'block';
			document.getElementById("btnValidate").disabled = false;

		}
		function getEncryptionkey(){
			var payId=$("#payId").val();
			//console.log(payId);
			var url=new URL(window.location.href);
			var domain=url.origin;
			$.ajax({
				type:"GET",
				url:domain+'/crmws/resetEncryptionKey/getEncryptionKey',
				data:{payId:payId},
				success:function(response){
						console.log(response);

						$("#newSalt").val(response[0]);
						$("#newKeySalt").val(response[1]);
						$("#newAPIKey").val(response[2]);

				},
				error:function(response){
					console.log(response);

				}
			});

		}
		function applyNewEncryptionKey(){
			var url=new URL(window.location.href);
			var domain=url.origin;
			var getDate=$("#datePicker").val();
			console.log(typeof(getDate));
		 	//var date=new Date(getDate[0],getDate[1]-1,getDate[2]);
			var time=$("#time").val();
		 	var getEncKey=$("#newAPIKey").val();
			var payId=$("#payId").val();
			var salt=$("#newSalt").val();
			var keySalt=$("#newKeySalt").val();
			var tableBody=$("#encryptionKeyTable body");
			if(!validateEncKeyAndDate(getEncKey,getDate))
				{
					return false;
				}
			$.ajax({
				type:"PUT",
				url:domain+"/crmws/resetEncryptionKey/setResetEncryptionList",
				data:{
					payId:payId,
					encyKey:getEncKey,
					salt:salt,
					keySalt:keySalt,
					date:getDate,
					time:time
				},
				success:function(response){

					$("#newAPIKey").val('');
		 			Swal.fire({
  					 	icon: "success",
 						title: "Encryption Key Added Successfully!",

 						showConfirmButton: false,
 						timer: 1500
					});
					loadResetApiKeyTable(payId);
				},
				error:function(response){

					Swal.fire({
  					 	icon: "error",
 						title: "Somthing Went Wrong!",
						showConfirmButton: false,
 						timer: 1500
					});

					console.log(response);
				}

			});
		}

		function validateEncKeyAndDate(key,date){
				if(key===""){
					$(".error-message-new-key").show();
					return false;
		 		}
				else{
					$(".error-message-new-key").hide();
				}
				if(date==""){

					$(".error-message-date").show();
					return false;
				}
				else{
					$(".error-message-date").hide();
					return true;
				}
		}






	</script>

	<style type="text/css">
@
-moz-document url-prefix () { #allowRefund2 {
	margin-left: 42.6% !important;
}
}
</style>
	<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
</body>

</html>