
function getMerchant() {
	
	var merchantpayId= document.getElementById("merchants").value;
	
	  $.ajax({
	      
	        type : "GET",
	        url : "getmerchant",
	        timeout : 0,
	        data : {
	          
	            "merchantpayId": merchantpayId,
	            
	          },
	          
	        success : function(data) {
	            var s = '<option value="">Select Merchant</option>';  
	                   for (var i = 0; i < data.merchantarreList.length; i++) {  
	                       s += '<option value="' + data.merchantarreList[i] + '">' + data.merchantarrename[i] + '</option>';  
	                   } 
	              document.getElementById("tomerchant").style.display = "block";

	                   $("#departmentsDropdown").html(s); 
	        }
	     });  
}


function getCurrencyList() {
	
	var merchantpayId= document.getElementById("merchants").value;
	var payId= document.getElementById("departmentsDropdown").value;
	
	  $.ajax({
	      
	        type : "GET",
	        url : "getCurrencyList",
	        timeout : 0,
	        data : {
	          
	            "merchantPayId": merchantpayId,
	            "payId":payId,
	            
	          },
	          
	        success : function(data) {
	            var s = '<option value="">Select Currency</option>';  
	                   for (var i = 0; i < data.currencyarrename.length; i++) {  
	                       s += '<option value="' + data.currencyCodearreList[i] + '">' + data.currencyarrename[i] + '</option>';  
	                   } 
	              document.getElementById("currencyListDropdown").style.display = "block";

	                   $("#currency").html(s); 
	                   
	                 
	        }
	     });  
}

function getDetails(){
	
	   var reseller = document.getElementById("merchants").value;
	    var merchantId =document.getElementById("departmentsDropdown").value;
	    var currency=document.getElementById("currency").value;

	    event.preventDefault();
	    if (reseller == "" || reseller == null) {

	      alert("Please select reseller");
	      return false;
	    }
	    if (merchantId == '' || merchantId == null) {
	      alert("Please Select Merchant");
	      return false;
	    }
	    
	    if (currency == '' || currency == null) {
		      alert("Please Select Currency");
		      return false;
		    }
	       document.getElementById("getchangpaymenttype").submit();
	  }

