$(document).ready(function() {
	$(function() {
		loadAcquirer();
        getCurrency('ALL');

	});
});

function loadAcquirer() {
	var html = "<div id='wwgrp_name' class='wwgrp'> <div id='wwctrl_name' class='wwctrl'>";
	var merchantVal = document.getElementById("merchant_selector").value;
    if(merchantVal === 'ALL'){
		 $.ajax({
			url: '/crm/jsp/AcquirerTypeList',  // Adjust the URL according to your Struts 2 configuration
			type: 'GET',
			dataType: 'json',
			success: function(response) {
				console.log('Response:', response);
				var i=1;
                html += '<input type="checkbox" name="name" value="ALL" id="name-0" class="myCheckBox hidden"  headerKey="ALL" checked/> <label for="name-0" class="checkboxLabel">All</label>';
				 $.each(response, function(index, acquirerCode) {
					html +='<input type="checkbox" name="name" value="'+acquirerCode+'" id="name-'+i+'" class="myCheckBox" class="myCheckBox" onclick="getCurrency(this.value)" /> <label for="name-'+i+'"class="checkboxLabel">'+acquirerCode+'</label>';
					i++;
				});
				html +="</div> </div>";
				$("#checkboxes").html(html);
			},
			error: function(xhr, status, error) {
				console.error('Error occurred:', status, error);
			}
			});
        return;
    }
	$.ajax({
		type: "GET",
		url: "getdata",
		timeout: 0,
		data: {
            "type":1,
			"payId": merchantVal,
			"struts.token.name": "token",
		},
		success: function(data) {
			{
				let acquirerSize = data.acquirerList ? data.acquirerList.length : 0;
                console.log(data.acquirerList);
                html += '<input type="checkbox" name="name" value="ALL" id="name-0" class="myCheckBox hidden"  headerKey="ALL" checked/> <label for="name-0" class="checkboxLabel">All</label>';
				for (var itm = 0; itm < acquirerSize; itm++) {
                  //  acqHtml1 = acqHtml1 +"<div> <label> <input type='checkbox' name='acquirerType' value='" + data.acquirerList[itm] + "' class='myCheckBox'> " + data.acquirerList[itm] + " </label> </div>";
					html +='<input type="checkbox" name="name" value="'+data.acquirerList[itm]+'" id="name-'+itm+'" class="myCheckBox" class="myCheckBox" headerValue="ALL" headerKey="ALL" onclick="getCurrency(this.value)"/> <label for="name-'+itm+'"class="checkboxLabel">'+data.acquirerList[itm]+'</label>';
				}
				html +="</div> </div>";
				$("#checkboxes").html(html);
			}
		},
		error: function(data) {
			window.location.reload();
		}
	});
}

var allSelectedAquirer = [];
var allCurrencies = {};
var currencyValues = {};
function getCurrency(acquirer) {
    var typ = 2;
    var merchantVal = document.getElementById("merchant_selector").value;
    $.ajax({
        type: "GET",
        url: "getdata",
        timeout: 0,
        data: {
            "type": typ,
            "payId": merchantVal,
            "Acquirer1": acquirer,
            "struts.token.name": "token",
        },
        success: function(d) {
            var data = d.currencyMap;
            var currencyHtml = "";
            currencyHtml += '<option value="ALL" data-symbol="ALL">ALL</option>';
            allCurrencies[acquirer] = Object.values(data);
            console.log('First Time ' + allCurrencies[acquirer]);
            for (let key in data) {
// if(acquirer === 'ALL'){
// document.getElementById('name-0').checked = true; // Unchecks the checkbox
// currencyHtml += `<option value="${data[key]}"
// data-symbol="${data[key]}">${data[key]}</option>`;
// currencyValues[data[key]] = key;
// }else{
                if(acquirer === 'ALL'){
                	   document.getElementById('name-0').checked = true; // Unchecks the checkbox
                	    currencyHtml += `<option value="${key}" data-symbol="${data[key]}">${data[key]}</option>`;
                	    currencyValues[data[key]] = key;
                	}else{
                    console.log("Acquiere Not ALL");
                    var checkbox = document.querySelector(`input[value="${acquirer}"]`);
                    if (checkbox && !checkbox.checked) {
                        console.log("Checkbox Checking");
                        var index = allSelectedAquirer.indexOf(data[key]);
                        if (index > -1) {
                            for (let aq in allCurrencies) {
                                console.log('Into the Loopy ' + allCurrencies[aq].includes(data[key]) + ' ,For this ac :- ' + aq + " //// " + allCurrencies[aq]);
                                if(!allCurrencies[aq].includes(data[key])){
                                    allSelectedAquirer.splice(index, 1);
                                }
                            }
                        }
                        console.log('What is the Acquirer Length :- ' + allSelectedAquirer.length);
                        if(allSelectedAquirer.length == 1){
                            getAll();
                        }
                    } else {
                        if (!allSelectedAquirer.includes(data[key])) {
                            allSelectedAquirer.push(data[key]);
                        }
                    }
                }
               }
            if (acquirer !== 'ALL') {
                document.getElementById('name-0').checked = false; // Unchecks the checkbox
                for (let value of allSelectedAquirer) {
                    currencyHtml += `<option value="${currencyValues[value]}" data-symbol="${value}">${value}</option>`;
                }
            }
            $("#currency").html(currencyHtml);
        },
        error: function() {
            window.location.reload();
        }
    });
}

function getAll(){
    getCurrency("ALL");
}
