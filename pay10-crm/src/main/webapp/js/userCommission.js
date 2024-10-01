
function getMa() {
	
	var smaPayId= document.getElementById("smaPayId").value;
	
	  $.ajax({
	      
	        type : "GET",
	        url : "getMa",
	        timeout : 0,
	        data : {
	          
	            "smaPayId": smaPayId,
	            
	          },
	          
	        success : function(data) {
	            var s = '<option value="">Select MA</option>';  
	                   for (var i = 0; i < data.maArreList.length; i++) {  
	                       s += '<option value="' + data.maArreList[i] + '">' + data.maArrename[i] + '</option>';  
	                   } 
	                  document.getElementById("toMa").style.display = "block";

	                   $("#maDropdown").html(s); 
	        }
	     });  
}


function getAgent() {
	
	var maPayId= document.getElementById("maDropdown").value;
	
	  $.ajax({
	      
	        type : "GET",
	        url : "getAgent",
	        timeout : 0,
	        data : {
	          
	            "maPayId": maPayId,
	            
	          },
	          
	        success : function(data) {
	            var s = '<option value="">Select Agent</option>';  
	                   for (var i = 0; i < data.agentarreList.length; i++) {  
	                       s += '<option value="' + data.agentarreList[i] + '">' + data.agentarrename[i] + '</option>';  
	                   } 
	              document.getElementById("toAgent").style.display = "block";

	                   $("#agentDropdown").html(s); 
	        }
	     });  
}

function getMerchant() {
	
	var agentPayId= document.getElementById("agentDropdown").value;
	
	  $.ajax({
	      
	        type : "GET",
	        url : "getMerchant",
	        timeout : 0,
	        data : {
	          
	            "agentPayId": agentPayId,
	            
	          },
	          
	        success : function(data) {
	            var s = '<option value="">Select Merchant</option>';  
	                   for (var i = 0; i < data.merchantarreList.length; i++) {  
	                       s += '<option value="' + data.merchantarreList[i] + '">' + data.merchantarrename[i] + '</option>';  
	                   } 
	              document.getElementById("tomerchant").style.display = "block";

	                   $("#merchantDropdown").html(s); 
	        }
	     });  
}

function getCurrencyList() {
	
	var smaPayId= document.getElementById("smaPayId").value;
	var maPayId= document.getElementById("maDropdown").value;
	var agentPayId= document.getElementById("agentDropdown").value;
	var merchantPayId= document.getElementById("merchantDropdown").value;
	
	  $.ajax({
	      
	        type : "GET",
	        url : "getUserCurrencyList",
	        timeout : 0,
	        data : {
	          
	            "smaPayId": smaPayId,
	            "maPayId": maPayId,
	            "agentPayId": agentPayId,
                 "merchantPayId": merchantPayId,
	            
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
	
	   var smaPayId= document.getElementById("smaPayId").value;
	   var maPayId= document.getElementById("maDropdown").value;
	   var agentPayId= document.getElementById("agentDropdown").value;
	   var merchantPayId= document.getElementById("merchantDropdown").value;
	   var currency=  document.getElementById("currency").value;

	    event.preventDefault();
	    if (smaPayId == "" || smaPayId == null) {

	      alert("Please select SMA");
	      return false;
	    }
	    if (maPayId == '' || maPayId == null) {
	      alert("Please Select MA");
	      return false;
	    }
	    if (agentPayId == "" || agentPayId == null) {

		      alert("Please select Agent");
		      return false;
		    }
		    if (merchantPayId == '' || merchantPayId == null) {
		      alert("Please Select Merchant");
		      return false;
		    }
		    if (currency == '' || currency == null) {
			      alert("Please Select Currency");
			      return false;
			    }
	       document.getElementById("getchangpaymenttype").submit();
	  }

