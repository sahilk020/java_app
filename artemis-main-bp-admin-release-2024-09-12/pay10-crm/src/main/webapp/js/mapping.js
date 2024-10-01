function getMapping() {
	var merchantId = document.getElementById("merchants").value;
	var acquirer = document.getElementById("acquirer").value;

	if (merchantId == "" && acquirer == "") {
		return false;
	}
	if (acquirer == "" || merchantId == "") {
		document.getElementById("err").style.display = "block";
		return false;
	}

	var transactionRegionCheckBoxes = document.getElementsByClassName("transaction-region");
	for (let index = 0; index < transactionRegionCheckBoxes.length; index++) {
		const element = transactionRegionCheckBoxes[index];
		element.checked = false;
	}

	if (merchantId != "" && acquirer != "") {
		var token = document.getElementsByName("token")[0].value;
		$.ajax({
			type : "POST",
			url : "mopSetUpDisplay",
			data : {
				merchantEmailId : merchantId,
				acquirer : acquirer,
				token : token,
				"struts.token.name" : "token"

			},


			success : function(data) {
				refresh();
				var mainDiv = document.getElementById('id+checkBoxes');
				mainDiv.style.display = "block";
				var map = data.mappedString;

				var accountCurrencyArray = data.currencyString;
				if (map == null) {
					var mainDiv = document.getElementById('id+checkBoxes');
					mainDiv.style.display = "none";
					document.getElementById("paymentCheck").style.display = "none";
					if (data.response != null) {
						alert(data.response);
						return false;
					} else {
						alert("Network error please try again later!!");
					}
				}

				if(map.charAt(0) == ","){
					map = map.substr(1);
				}

				else if (map == "") {
					return false;
				}
				for (j = 0; j < accountCurrencyArray.length; j++) {
					selectCurrency(accountCurrencyArray[j]);
				}
				var tokens = map.split(",");
				for (i = 0; i < tokens.length; i++) {
					var token = tokens[i];
					selectCheckBoxes(token);
				}
			}
		});
	}
}

function selectCurrency(accountCurrencyObj) {
	var currencyCode = accountCurrencyObj.currencyCode;
	var currencyCheckBoxId = "";
	currencyCheckBoxId = currencyCheckBoxId.concat("id+", currencyCode);
	var fieldsDivId = ("boxdiv" + currencyCheckBoxId);
	var currencyCheckbox = document.getElementById(currencyCheckBoxId);

	if (currencyCheckbox == null) {
		return;
	}
	var fieldsDiv = document.getElementById(fieldsDivId);

	var passwordTextBox = document.getElementById("idpassword+"
			.concat(currencyCode));
	var txnKeyTextBox = document.getElementById("idtxnkey+"
			.concat(currencyCode));
	var merchantIdTextBox = document.getElementById("idmerchantid+"
			.concat(currencyCode));

	var adf1IdTextBox = document.getElementById("idadf1+"
			.concat(currencyCode));
	var adf2IdTextBox = document.getElementById("idadf2+"
			.concat(currencyCode));
	var adf3IdTextBox = document.getElementById("idadf3+"
			.concat(currencyCode));
	var adf4IdTextBox = document.getElementById("idadf4+"
			.concat(currencyCode));
	var adf5IdTextBox = document.getElementById("idadf5+"
			.concat(currencyCode));
	var adf6IdTextBox = document.getElementById("idadf6+"
			.concat(currencyCode));
	var adf7IdTextBox = document.getElementById("idadf7+"
			.concat(currencyCode));
	var adf8IdTextBox = document.getElementById("idadf8+"
			.concat(currencyCode));
	var adf9IdTextBox = document.getElementById("idadf9+"
			.concat(currencyCode));
	var adf10IdTextBox = document.getElementById("idadf10+"
			.concat(currencyCode));
	var adf11IdTextBox = document.getElementById("idadf11+"
			.concat(currencyCode));
	var internationalCheckBox = document.getElementById("idinternational+"
			.concat(currencyCode));
	var domesticCheckBox = document.getElementById("iddomestic+"
			.concat(currencyCode))

	var threeDFlag = document.getElementById("id3dflag+".concat(currencyCode));

	passwordTextBox.value = accountCurrencyObj.password;
	txnKeyTextBox.value = accountCurrencyObj.txnKey;
	merchantIdTextBox.value = accountCurrencyObj.merchantId;

	adf1IdTextBox.value = accountCurrencyObj.adf1;
	adf2IdTextBox.value = accountCurrencyObj.adf2;
	adf3IdTextBox.value = accountCurrencyObj.adf3;
	adf4IdTextBox.value = accountCurrencyObj.adf4;
	adf5IdTextBox.value = accountCurrencyObj.adf5;
	adf6IdTextBox.value = accountCurrencyObj.adf6;
	adf7IdTextBox.value = accountCurrencyObj.adf7;
	adf8IdTextBox.value = accountCurrencyObj.adf8;
	adf9IdTextBox.value = accountCurrencyObj.adf9;
	adf10IdTextBox.value = accountCurrencyObj.adf10;
	adf11IdTextBox.value = accountCurrencyObj.adf11;
	internationalCheckBox.checked = accountCurrencyObj.international;
	domesticCheckBox.checked = accountCurrencyObj.domestic;

	currencyCheckbox.checked = true;
	if(accountCurrencyObj.directTxn){
		threeDFlag.checked = true;
	}
	fieldsDiv.style.display = "block";
}

function selectCheckBoxes(token) {
	var splittedToken = token.split("-");

	var masterCheckBoxId = "";
	masterCheckBoxId = masterCheckBoxId.concat(splittedToken[0],splittedToken[1], "box")
	console.log("masterCheckBoxId :::"+masterCheckBoxId);
	console.log("splittedToken[1]::::"+splittedToken[1]);


	var masterCheckbox = document.getElementById(masterCheckBoxId);
	masterCheckbox.checked = true;
	var masterDiv = document.getElementById(splittedToken[0]+splittedToken[1]);
	masterDiv.style.display = "block";
	if (splittedToken.length == 4 || splittedToken.length == 3 ) {
		var mopDivId = "".concat(splittedToken[0],splittedToken[1], "-", splittedToken[2]);
		var mopDiv = document.getElementById(mopDivId);
		console.log("mopDivId::::"+mopDivId)
		if(mopDiv!=null)
			mopDiv.style.display = "block";
		var mopCheckBoxId =  "".concat(splittedToken[0], "-", splittedToken[1], "-", splittedToken[2]);
		var mopCheckBox = document.getElementById(mopCheckBoxId);

		console.log("mopCheckBoxId:::"+mopCheckBoxId);
		mopCheckBox.checked = true;
	}
	var txnCheckbox = document.getElementById(token);
	console.log(txnCheckbox)
	if (null != txnCheckbox) {
		txnCheckbox.checked = true;
	} else {
		alert("Conflict with mapping token fetched and present checkboxes");
	}

}

// to Hide all the checkboxes and disable if no mapping present
function refresh() {
	var creditCard = document.getElementById('Credit Cardbox');
	if (creditCard != null) {
		creditCard.checked = false;
		deselectAllCheckboxesWithinDiv('Credit Card');
		hideAllCheckboxesWithinDiv('Credit Card');
	}

	var debitCard = document.getElementById('Debit Cardbox');
	if (debitCard != null) {
		debitCard.checked = false;
		deselectAllCheckboxesWithinDiv('Debit Card');
		hideAllCheckboxesWithinDiv('Debit Card');
	}

	var prepaidCard = document.getElementById('Prepaid Cardbox');
	if (prepaidCard != null) {
		prepaidCard.checked = false;
		deselectAllCheckboxesWithinDiv('Prepaid Card');
		hideAllCheckboxesWithinDiv('Prepaid Card');
	}

	var netBanking = document.getElementById('Net Bankingbox');
	if (netBanking != null) {
		netBanking.checked = false;
		deselectAllCheckboxesWithinDiv('Net Banking');
		hideAllCheckboxesWithinDiv('Net Banking');
	}

	var wallet = document.getElementById('Walletbox');
	if (wallet != null) {
		wallet.checked = false;
		deselectAllCheckboxesWithinDiv('Wallet');
		hideAllCheckboxesWithinDiv('Wallet');
	}

	var autoDebit = document.getElementById('AutoDebitbox');
	if (autoDebit != null) {
		autoDebit.checked = false;
		deselectAllCheckboxesWithinDiv('AutoDebit');
		hideAllCheckboxesWithinDiv('AutoDebit');
	}

	var upi = document.getElementById('UPIbox');
	if (upi != null) {
		upi.checked = false;
		deselectAllCheckboxesWithinDiv('UPI');
		hideAllCheckboxesWithinDiv('UPI');
	}

	var emi = document.getElementById('EMIbox');
	if (emi != null) {
        emi.checked = false;
        deselectAllCheckboxesWithinDiv('EMI');
        hideAllCheckboxesWithinDiv('EMI');
    }
	var mainDiv = document.getElementById('id+checkBoxes');
	mainDiv.style.display = "none";

}

function hideAllCheckboxesWithinDiv(eleId) {
	var ele = document.getElementById(eleId);
	ele.style.display = "none";
}

function deselectAllCheckboxesWithinDiv(eleId) {
	var collection = document.getElementById(eleId).getElementsByTagName(
			'INPUT');

	for (var x = 0; x < collection.length; x++) {
		if (collection[x].type.toUpperCase() == 'CHECKBOX')
			collection[x].checked = false;
	}
}

function selectAllCheckboxesWithinDiv(eleId) {
	var checkbox = document.getElementById(eleId+'selectAllButton').checked;
	if (checkbox) {
		var collection = document.getElementById(eleId).getElementsByTagName(
				'INPUT');

		for (var x = 0; x < collection.length; x++) {
			if (collection[x].type.toUpperCase() == 'CHECKBOX')
				collection[x].checked = true;
		}
	} else {
		deselectAllCheckboxesWithinDiv(eleId);
	}
}
