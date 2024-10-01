
$(document).ready(function() {
 

  $(function() {
  

    $(document).ready(function() {
     
      $.ajax({

        type: "GET",
        url: "schadularDetails",

        data: {

        },
        success: function(response) {
          var responseObj = JSON.parse(JSON.stringify(response));
          renderTable(responseObj.acquirerSchadular);
        },
      });
    });
  });
});

//function searchdata(){
//	
//	 var acquirer = document.getElementById("acquirer").value;
//	  var paymentType = document.getElementById("paymentMethod").value;
//    $.ajax({
//
//        type: "GET",
//        url: "serachDetails",
//
//        data: {
//        	
//"acquirer":acquirer,
//"paymentType":paymentType,
//        },
//        success: function(response) {
//          var responseObj = JSON.parse(JSON.stringify(response));
//          renderTable(responseObj.searchSchadular);
//        },
//      });
//}
function save() {
  var acquirer = document.getElementById("acquirer").value;
  var Maxtime = document.getElementById("Maxtime").value;

  var Mintime = document.getElementById("Mintime").value;

  if( acquirer == "" ){
	  if( acquirer == "" ){
		 alert('Please Select Acquirer !')
		    return false;

	 }}
  else{
  if (Maxtime == "" || Mintime == "" ) {
	    alert('Enter data value !');
	    return false;
	  }

  }

  $.ajax({

    type: "GET",
    url: "saveAcquirerSchadular",
    timeout : 0,
  data: {

      "Maxtime": Maxtime,
      "acquirer": acquirer,     
      "Mintime": Mintime,

    },
    success : function(data) {
		
			var response = data.response; 
			swal({
			  title: response,
			  type: "success"
			}, function () {
				  window.location.reload();
			});    					
           	 
	},
	error : function(data) {
		window.location.reload();
	}
  });
}

function renderTable(data) {
  var getindex = 0;
  var table = new $.fn.dataTable.Api('#dataTable');

 
   var buttonCommon = {
    exportOptions: {
      format: {
        body: function(data, column, row, node) {
          // Strip $ from salary column to make it numeric
          return (column === 1 ? "'" + data : data);
        }
      }
    }
  };

  $("#datatable").DataTable().destroy();

  $('#datatable').dataTable({

    'data': data,
   
    'columns': [
      {
        'data': 'acquirer',
        'className': 'batch_No text-class'
      },
      {
    	  
        'data': 'id',
        'className': 'tableId  text-class displayNone'
      },
      
{
        'data': 'startTime',
        'className': 'settlement_Date text-class'
      },

      {
        'data': 'endTime',
        'className': 'tds text-class'
      },

      {
			"data" : null,
			"sClass" : "center",
			"bSortable" : false,
			"mRender" : function() {
				return '<button class="btn  btn-primary"  name="payoutDetails" value="Payout" data-toggle="modal"  data-target="#payoutAccept" onclick = "editDetails(this);">Edit</button>';
			}
		},
		{
			"data" : null,
			"sClass" : "center",
			"bSortable" : false,
			"mRender" : function() {
			
				return '<button class="btn btn-primary"   name="merchantDelete" id="myBtn" onclick = "delet(this)" >Delete</button>';
				
			}
		}
		,{
			
			"data" : null,
			"visible" : false,
			"className" : "displayNone",
			"mRender" : function(row) {
	              return "\u0027" + row.id;
	            
	       }
		}
		
 ]
  });
}

function delet(val){
	
	
	var table = $('#datatable').DataTable();
	 $('#datatable tbody').on('click','td',function(){
	
		var rows = table.rows();
		var columnVisible = table.cell(this).index().columnVisible;
		if(columnVisible==5){
		var rowIndex = table.cell(this).index().row;
		var id = table.cell(rowIndex, 1).data();
		
		  
		       												   
					swal({
						title: "Are you sure want to delete this Rule?",
						type: "warning",
						showCancelButton: true,
						confirmButtonColor: "#DD6B55",
						confirmButtonText: "Yes, delete it!",
						closeOnConfirm: false
						}, function (isConfirm) {
							if (!isConfirm) return;		
				    $.ajax({
						type : "POST",
						url : "deleteAcquiereRule",
						timeout : 0,
						data : {
							"id":id
						},
						success : function(data) {
							var response = data.response;
							swal({
							 title: 'Deleted Successful!',
							 type: "success"
							}, function(){
								window.location.reload();
							}); 
						},
						error : function(data) {
							window.location.reload();
						}
				    });
				}); 
				}
		 

	
	  }) 
	
}

function save1() {

	  var acquirer = document.getElementById("acquirer1").value;
	  var Maxtime = document.getElementById("Maxtime1").value;

	  var Mintime = document.getElementById("Mintime1").value;
	  
	  var idedit = document.getElementById("id").value;
	  if (acquirer == "" || Maxtime == "" || Mintime == "" ) {
		    alert('Enter date value');
		    return false;
		  }
	

	  $.ajax({

	    type: "GET",
	    url: "saveAcquirerSchadular1",
	    timeout : 0,
	  data: {

	      "Maxtime": Maxtime,
	      "acquirer": acquirer,     
	      "Mintime": Mintime,	 
	      "idedit": idedit,

	      

	    },
	    success: function(response) {
	    	alert("save successfully")
			window.location.reload();


	     
	    }
	  });
	}
	
	
function openPopUp() {
    $('#data-add').show();
}

function openPopUp2() {
    $('#payoutAccept').show();
}


function closePopup(){

  $('#data-add').hide();

}
function closePopup1(){

 
 $('#payoutAccept').hide();
}

function editDetails(val) {

	
  let tableIndex = $(val).parent().index();
  var row = val;

  var cells = val.parentElement.parentElement.cells;
  
  
  
  /*document.getElementById("acquirer1").value = $('.batch_No').eq(tableIndex + 2).html();
  document.getElementById("Maxtime1").value = $('.settlement_Date').eq(tableIndex + 2).html();
  document.getElementById("Mintime1").value = $('.tds').eq(tableIndex + 2).html();*/
  
  
  document.getElementById("acquirer1").value =cells[0].textContent;
  document.getElementById("Maxtime1").value = cells[2].textContent;
  document.getElementById("Mintime1").value = cells[3].textContent;
  
  
  /*document.getElementById("id").value = $('.tableId').eq(tableIndex + 2).html();*/
  document.getElementById("id").value = cells[1].textContent;
openPopUp2();
}
