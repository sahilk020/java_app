


// Validation to remove space on paste in ORDER Id
$(document).on('paste', '#orderId', function(e) {
  e.preventDefault();
  // prevent copying action
  var withoutSpaces = e.originalEvent.clipboardData.getData('Text');
  //withoutSpaces = withoutSpaces.replace(/\s+/g, '');
  withoutSpaces = withoutSpaces.trim();
  $(this).val(withoutSpaces);
});


//Validation to check Date Format on Type

var currentDate;

function checkFromDate(){
	var dateRegex = /^(0?[1-9]|[12][0-9]|3[01])[\/\-](0?[1-9]|1[012])[\/\-]\d{4}$/;
	
	var fromDateVal = document.getElementById("dateFrom").value;
	if (fromDateVal.trim() != "" && fromDateVal.trim() != null){		  
		if (!fromDateVal.match(dateRegex)) {
			document.getElementById("dateError").style.display = "block";
			document.getElementById("submit").disabled = true;
			return false;
		} else {
			document.getElementById("dateError").style.display = "none";
			document.getElementById("submit").disabled = false;
			return true;
		}	      
	}  else {
			document.getElementById("dateError").style.display = "none";
			document.getElementById("submit").disabled = false;
			return true;
		}
}

function createDate(strDate) {
	var arrDate = strDate.split("-");
	return arrDate[1] + "-" + arrDate[0] + "-" + arrDate[2];
}
function compareDate(){
	var firstDate = new Date(createDate(document.getElementById("dateFrom").value));
	var toDate = new Date(createDate(document.getElementById("dateTo").value));
	currentDate = new Date();
	if(checkFromDate() && checkToDate()) {
		if(firstDate > currentDate){
			document.getElementById("showErr1").style.display = "block";
			document.getElementById("submit").disabled = true;
			return false;
		} else { 
			document.getElementById("showErr1").style.display = "none";
			document.getElementById("submit").disabled = false;		
			return true;
		} 
	}
	if(checkFromDate() && checkToDate()) {
		if(toDate > currentDate){
			document.getElementById("showErr2").style.display = "block";
			document.getElementById("submit").disabled = true;
			return false;
		}
		else{
			document.getElementById("showErr2").style.display = "none";
			document.getElementById("submit").disabled = false;
			return true;
		}
	}
}
	
function checkToDate(){
	var dateRegex = /^(0?[1-9]|[12][0-9]|3[01])[\/\-](0?[1-9]|1[012])[\/\-]\d{4}$/;
	
	var toDateVal = document.getElementById("dateTo").value;
	if (toDateVal.trim() != "" && toDateVal.trim() != null){	   
		if (!toDateVal.match(dateRegex)) {
			document.getElementById("dateError1").style.display = "block";
			document.getElementById("submit").disabled = true;
			return false;
		} else {
			document.getElementById("dateError1").style.display = "none";
			document.getElementById("submit").disabled = false;
			return true;
		}
	} else {
			document.getElementById("dateError1").style.display = "none";
			document.getElementById("submit").disabled = false;
			return true;
	}		 
}  
