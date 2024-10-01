$('#merchants')
	.change(
		function(event) {
			/*   $('#loader-wrapper').show(); */

			var merchants = document
				.getElementById("merchants").value;
			var urls = new URL(window.location.href);
			var domain = urls.origin;

			$
				.ajax({
					type: "GET",
					                    url: domain
					                        + "/crmws/acquirer/getMappedAcquirerForTdr",
//					url: "http://localhost:8080/acquirer/getMappedAcquirerForTdr",

					timeout: 0,
					data: {

						"emailId": merchants,

					},

					success: function(data, status) {
						var acquirer = JSON.stringify(data);
						var acquirerList = JSON
							.parse(acquirer);
						var s = '<option value="">Select Acquirer</option>';
//						var s;
						for (var i = 0; i < acquirerList.length; i++) {

							s += '<option value="' + acquirerList[i] + '">'
								+ acquirerList[i]
								+ '</option>';
						}
						document
							.getElementById("acquirerDropdown").style.display = "block";

						$("#acquirer").html(s);
					}
				});

				$('#acquirer')
						.change(
						function(event) {

			var acquirer = document
				.getElementById("acquirer").value;
				console.log("Print Acquirer : "+acquirer)
			var emailId = document
				.getElementById("merchants").value;
				console.log("Print EmailId : "+emailId)
			var urls = new URL(window.location.href);
			var domain = urls.origin;

			$
				.ajax({
					type: "GET",
					                    url: domain
					                        + "/crmws/TdrAndSurcharge/getCurrencyList",

					timeout: 0,
					data: {

						"acquirer" : acquirer,
						"emailId": emailId,


					},

					success: function(data, status) {
						// var array= $.map(data,function(element){
						// 	console.log(element.);
						// 	return element.value;
						// });

						// var currency = JSON.stringify(data);
						// var currencyList = JSON
						// 	.parse(currency);
						// 	console.log("Currency :::"+currency);

						console.log(data);
						var s= '<option value="">Select Currency</option>';

						for (let key in data) {

							s += '<option value="' + key + '">'
								+ data[key]
								+ '</option>'
								console.log("Object ::::"+key + " Map "+data[key])
						}
						document
							.getElementById("currencyListDropdown").style.display = "block";

						$("#currency").html(s);
					}
				});
			});
			var currency = $("select#currency").val();

			var paymentRegion = $("select#paymentRegion").val();
			var cardHolderType = $("select#cardHolderType")
				.val();
			if (acquirer == null || acquirer == "") {

				$('#loader-wrapper').hide();
				return false;
			} else if (merchants == null || merchants == "") {

				$('#loader-wrapper').hide();
				return false;
			} else if (paymentRegion == null
				|| paymentRegion == "") {

				$('#loader-wrapper').hide();
				return false;
			} else if (cardHolderType == null
				|| cardHolderType == "") {
				$('#loader-wrapper').hide();
				return false;
			}
			 else if (currency == null
						|| currency == "") {
						$('#loader-wrapper').hide();
						return false;
					}
			document.getElementById("chargingdetailform")
				.submit();
		});

$('#acquirer').change(function(event) {
	/*  $('#loader-wrapper').show(); */
	var acquirer = document.getElementById("acquirer").value;
	var merchants = document.getElementById("merchants").value;
	var paymentRegion = $("select#paymentRegion").val();
	var cardHolderType = $("select#cardHolderType").val();
	var currency = $("select#currency").val();

	if (acquirer == null || acquirer == "") {

		/*  $('#loader-wrapper').hide(); */
		return false;
	} else if (merchants == null || merchants == "") {

		/*   $('#loader-wrapper').hide(); */
		return false;
	} else if (paymentRegion == null || paymentRegion == "") {

		/*    $('#loader-wrapper').hide(); */
		return false;
	} else if (cardHolderType == null || cardHolderType == "") {
		/*  $('#loader-wrapper').hide(); */
		return false;
	}
	else if (currency == null || currency == "") {
		/*  $('#loader-wrapper').hide(); */
		return false;
	}
	document.getElementById("chargingdetailform").submit();
});

$('#paymentRegion').change(function(event) {
	/*  $('#loader-wrapper').show(); */
	var acquirer = document.getElementById("acquirer").value;
	var merchants = document.getElementById("merchants").value;
	var paymentRegion = $("select#paymentRegion").val();
	var cardHolderType = $("select#cardHolderType").val();
	var currency = $("select#currency").val();

	if (acquirer == null || acquirer == "") {

		/*  $('#loader-wrapper').hide(); */
		return false;
	} else if (merchants == null || merchants == "") {

		/* $('#loader-wrapper').hide(); */
		return false;
	} else if (paymentRegion == null || paymentRegion == "") {

		/*   $('#loader-wrapper').hide(); */
		return false;
	} else if (cardHolderType == null || cardHolderType == "") {
		/*  $('#loader-wrapper').hide(); */
		return false;
	}
	else if (currency == null || currency == "") {
		/*  $('#loader-wrapper').hide(); */
		return false;
	}
	document.getElementById("chargingdetailform").submit();
});

$('#cardHolderType').change(
	function(event) {
		/* 	$('#loader-wrapper').show(); */
		var merchants = $("select#merchants").val();
		var acquirer = $("select#acquirer").val();
		var paymentRegion = $("select#paymentRegion").val();
		var cardHolderType = $("select#cardHolderType").val();
		var currency = $("select#currency").val();

		if (acquirer == null || acquirer == "" || merchants == null
			|| merchants == "" || paymentRegion == null
			|| paymentRegion == "" || cardHolderType == null
			|| cardHolderType == ""|| currency == null
			|| currency == "") {
			$('#loader-wrapper').hide();
			return false;
		}
		document.getElementById("chargingdetailform").submit();
	});

$('#currency').change(
		function(event) {
			/* 	$('#loader-wrapper').show(); */
			var merchants = $("select#merchants").val();
			var currency = $("select#currency").val();
			var acquirer = $("select#acquirer").val();
			var paymentRegion = $("select#paymentRegion").val();
			var cardHolderType = $("select#cardHolderType").val();
			if (acquirer == null || acquirer == "" || merchants == null
				|| merchants == "" || paymentRegion == null
				|| paymentRegion == "" || cardHolderType == null
				|| cardHolderType == "" || currency == null
				|| currency == "") {
				$('#loader-wrapper').hide();
				return false;
			}
			document.getElementById("chargingdetailform").submit();
		});

var flag = false;
function editRows(btn) {
	 if (flag) {
	        alert('Please save the current row to proceed');
	        return;
	    }
	btn.getInnerHTML = "SAVE";
	$(btn).closest('tr').find('input,select').prop('disabled', false);
	$(".selectField").prop('disabled', true);
	flag = true;

}
function cancelRows(btn) {
	

	flag = false;
	$(btn).closest('tr').find('input,select').prop('disabled', true);
}
function cloneRow(btn) {
	
	if (flag == true) {
		alert("Please Save Row Either Cancel Row Before Add Rows")
	} else {
		var date = new Date();
		var year = date.getFullYear();
		var month = date.getMonth();
		var day = date.getDate();
		var hour = date.getHours();
		var minute = date.getMinutes();
		var second = date.getSeconds();
		var millisecond = date.getMilliseconds();

		var uniqueNumber = ("" + year + month + day + hour + minute + second + millisecond);

		var row = btn.closest('tr');
		row.cells[0].children[0].value = uniqueNumber;
		var tbody = btn.closest('tbody');
		var clone = row.cloneNode(true);
		clone.id = new Date().getMilliseconds();
		tbody.appendChild(clone);
	}

}
function deleteRow(btn) {
	
	if (flag == true) {
		alert("Please Save Row Either Cancel Row Before Delete Rows")
	} else {
		btn.closest('tr').remove();

	}

}

$(document).ready(function () {
	$(".enableSurcharge").click(function (e) {
		if (e.target.checked) {
			var resp = confirm("Do you really want to enable surcharge?");
			console.log(resp);
			if (!resp) {
				e.preventDefault();
				e.stopPropagation();
				return false;
			}
		}
		return true;
	});
});
