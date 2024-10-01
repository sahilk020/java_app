<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html>
<head>

<style>

.nav {
	    margin-bottom: 18px;
	    margin-left: 0;
		list-style: none;
	}

.nav > li > a {
		display: block;
	}

.nav-tabs{
		*zoom: 1;
	}

.nav-tabs:before,
.nav-tabs:after {
		display: table;
		content: "";
	}

.nav-tabs:after {
		clear: both;
	}

.nav-tabs > li {
		float: left;
	}

.nav-tabs > li > a {
		padding-right: 12px;
		padding-left: 12px;
		margin-right: 2px;
		line-height: 14px;
	}

.nav-tabs {
		border-bottom: 1px solid #ddd;
	}

.nav-tabs > li {
		margin-bottom: -1px;
	}

.nav-tabs > li > a {
		padding-top: 8px;
	    padding-bottom: 8px;
		line-height: 18px;
		border: 1px solid transparent;
		-webkit-border-radius: 4px 4px 0 0;
		-moz-border-radius: 4px 4px 0 0;
		border-radius: 4px 4px 0 0;
	}

.nav-tabs > li > a:hover {
		border-color: #eeeeee #eeeeee #dddddd;
	}

.nav-tabs > .active > a,
.nav-tabs > .active > a:hover {
		color: #555555;
		cursor: default;
		background-color: #ffffff;
		border: 1px solid #ddd;
	    border-bottom-color: transparent;
	}

li {
		line-height: 18px;
	}

.tab-content.active{
		display: block;
	}

.tab-content.hide{
				display: none;
}
.nav-tabs>li>a:hover{border-top: 0px solid transparent;}
.uploadButton{
    border: none;
	background-color: #496cb6;
    border-radius: 5px;
    width: 25%;
    font-size: 18px;
	color:white;
}
.heading{
   text-align: center;
    color: black;
    font-weight: bold;
    font-size: 22px;
}
.txtnew {
    /* display: inline-block; */
    /* max-width: 100%; */
    display: block;
    text-align: left;
}
.form-control{
	margin-left: 0 !important;
	width: 100% !important;
}
.divalignment{
	  margin-top: -30px !important;
  }
  
.case-design{
	  text-decoration:none;
	  cursor: default !important;
}
 .my_class:hover{
	  color: white !important;
 }
.multiselect {
    width: 210px;
	display:block;
	margin-left:-20px;	
 }
  .selectBox {
  position: relative;
 }

#checkboxes {
  display: none;
  border-radius: 5px;
  border: 1px #dadada solid;
  height:170px;
  overflow-y: scroll;
  position:Absolute;
  background:#fff;
  z-index:1;
  margin-left:2px;
  margin-right:10px;
}

#checkboxes label {
  width: 74%;
}
#checkboxes input {
  width:18%;

}
#acqCheckbox {
  display: none;
  border-radius: 5px;
  border: 1px #dadada solid;
  height:300px;
  overflow-y: scroll;
  position:Absolute;
  background:#fff;
  z-index:1;
  margin-left:2px;
  margin-right:10px;
}



#acqCheckbox label {
  width: 74%;
}
#acqCheckbox input {
  width:18%;

}
.selectBox select {
  width: 95%;
  
}
.overSelect {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
}
.download-btn {
	background-color:#496cb6;
	display: block;
    width: 100%;
    height: 30px;
    padding: 3px 4px;
    font-size: 14px;
    line-height: 1.42857143;
    color: #fff;
    border: 1px solid #ccc;
    border-radius: 4px;
	margin-top:30px;
}
.form-control{
	margin-left: 0!important;
	width: 100% !important;
}
.padding10{
	padding: 10px;
}
.disabled {
    color:#fff;
	border-color: #a0a0a0;
	background-color: #a0a0a0;
}
label{
	margin-bottom: -10px !important;
}	
.aboveHead{
	display: block;
    text-align: left;
}	
.recon-btn{
	background-color: #496cb6;
    width: 45%;
    padding: 3px 4px;
    font-size: 15px;
    line-height: 1.42857143;
    color: #fff;
    border: 1px solid #ccc;
    border-radius: 4px;
}
#mprReconSummaryDatatable_info{
	margin-left: -23% !important;
}
#mprReconSummaryDatatable_length{
	margin-left: -13% !important;
}
</style>

<title>Refund Status Enquiry Report</title>
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link rel="stylesheet" href="../css/loader.css">
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<script src="../js/jquery.js"></script>
<script type="text/javascript" src="../js/moment.js"></script>
<script type="text/javascript" src="../js/daterangepicker.js"></script>
<link href="../css/loader.css" rel="stylesheet" type="text/css" />
<script src="../js/jquery.popupoverlay.js"></script>
<link href="../fonts/css/font-awesome.min.css" rel="stylesheet">
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<script src="../js/jquery.min.js"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>  
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>
<script type="text/javascript" src="../js/pdfmake.js"></script> 
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>
	
<script type="text/javascript">
$(document).ready(function(){
   document.getElementById("loading").style.display = "none";
  // Initialize select2
  $("#merchant").select2();
});
</script>

	
<script>
	$(document).ready(function() {
		$('.nav-tabs > li > a').click(function(event){
		event.preventDefault();//stop browser to take action for clicked anchor

		//get displaying tab content jQuery selector
		var active_tab_selector = $('.nav-tabs > li.active > a').attr('href');

		//find actived navigation and remove 'active' css
		var actived_nav = $('.nav-tabs > li.active');
		actived_nav.removeClass('active');

		//add 'active' css into clicked navigation
		$(this).parents('li').addClass('active');

		//hide displaying tab content
		$(active_tab_selector).removeClass('active');
		$(active_tab_selector).addClass('hide');

		//show target tab content
		var target_tab_selector = $(this).attr('href');
		$(target_tab_selector).removeClass('hide');
		$(target_tab_selector).addClass('active');
	     });
	  });
</script>

<script type="text/javascript">
$(function() {
	
	 table = $('#statusEnquiryDatatable').DataTable({
		
		dom: 'BTrftlpi',
	               destroy : true,
	               buttons : [
							{
								extend : 'copyHtml5',
								exportOptions : {
									columns : [':visible']
								}
							},
							{
								extend: 'csvHtml5',
								title : 'Refund Status Enquiry Report',
								exportOptions : {
									columns : [':visible']
								},
							},
							{
								extend : 'pdfHtml5',
								orientation : 'landscape',
								title : 'Refund Status Enquiry Report',
								exportOptions : {
									columns : [':visible']
								}
							},
							{
								extend : 'print',
								title : 'Refund Status Enquiry Report',
								exportOptions : {
									columns : [':visible']
								}
							}
						],
				"searching": false,
				"paging": true,
                "lengthMenu": [ [10, 25, 50, 100, -1], [10, 25, 50, 100, "All"] ],
                "pagingType": "full_numbers",
                "pageLength": 10,
	});
	
    $('#Submit').on('click', function() {   
	    
		 var dateFrom = document.getElementById("dateFrom").value;
		 var dateTo = document.getElementById("dateTo").value;
		 var token  = document.getElementsByName("token")[0].value;
		 document.getElementById("loading").style.display = "block";
         table = $('#statusEnquiryDatatable').DataTable({
			 "columnDefs": [
				        {
							"className": "dt-center", 
						"targets": "_all"
						}
				],
            dom: 'BTrftlpi',			
		               buttons : [
							{
								extend : 'copyHtml5',
								exportOptions : {
									columns : [':visible']
								}
							},
							{
								extend: 'csvHtml5',
								title : 'Refund Status Enquiry Report',
								exportOptions : {
									
									columns : [':visible']
								},
							},
							{
								extend : 'pdfHtml5',
								orientation : 'landscape',
								title : 'Refund Status Enquiry Report',
								exportOptions : {
									columns : [':visible']
								}
							},
							{
								extend : 'print',
								title : 'Refund Status Enquiry Report',
								exportOptions : {
									columns : [':visible']
								}
							}
						],
			"searching": false,
			"paging": true,
            "lengthMenu": [ [10, 25, 50, 100, -1], [10, 25, 50, 100, "All"] ],
            "pagingType": "full_numbers",
            "pageLength": 10,
			destroy: true,                                  
			ajax: function (data, callback, settings) {
                           $.ajax({
                                       "url": "refundStatusEnquiryReportAction",
                                       "type": "POST",
                                       "data": {
                                          
                                             "dateFrom": dateFrom,
											 "dateTo":dateTo,
                                            "struts.token.name": "token"
                                        },
                                        success:function(data){
										callback(data);
										document.getElementById("loading").style.display = "none";
                                        
										 
                                        },
										error:function(data) {
											  alert("data");
											  document.getElementById("loading").style.display = "none";
											  
										  }
                                        });
                                  },
				  
		"columns": [
				    { "data": "createDate" },
					{ "data": "totalProcess" },
		            { "data": "totalCapture" },
					{ "data": "totalPending" },
					{ "data": "totalOthers" },
		            
		    
		        ]
        });
    });
});
</script>

<style>
.messageClass{
	text-align: center;
    color: black;
    background: gainsboro;
    font-size: 14px;
}
#loading {width: 100%;height: 100%;top: 0px;left: 0px;position: fixed;display: block; z-index: 99}
#loading-image {position: absolute;top: 40%;left: 55%;z-index: 100; width:10%;}
</style>
	
</head>
<body id="mainBody">
     
	<div id="loading" style="text-align: center;">
		<img id="loading-image" style="width:70px;height:70px;" src="../image/sand-clock-loader.gif" alt="Sending SMS..." />
	</div> 
	
	<h2 class="pageHeading">Refund Status Enquiry Report</h2>
	<h6 class="messageClass" id="responseMessage"></h6>
	<br>
	<br>
	 <table class="table98 padding0">
        
        <tr>
          <td align="center">&nbsp;</td>
          <td height="10" align="center">
        
    </td>
        </tr>
 
  <tr>
          <td align="center">&nbsp;</td>
          <td height="10" align="center">
    

      <section id="mprReconReport" class="tab-content active">
        <div>
            <br/>
               <div class="container">

					<div class="row padding10">
						
						
					
						

				
						<div class="form-group  col-md-2 col-sm-3 txtnew col-xs-6">
							<label for="dateFrom"  class="aboveHead">From Date:</label> <br />
							<s:textfield type="text" readonly="true" id="dateFrom"
								name="dateFrom" class="form-control" onchange="handleChange();" 
								autocomplete="off"/>
						</div>
						
						<div class="form-group  col-md-2 col-sm-3 txtnew col-xs-6">
							<label for="dateFrom"  class="aboveHead">To Date:</label> <br />
							<s:textfield type="text" readonly="true" id="dateTo"
								name="dateTo" class="form-control" onchange="handleChange();" 
								autocomplete="off"/>
						</div>
					
					</div>
					
					<div>
						<div class="form-group col-md-2 col-sm-3 txtnew col-xs-6 col-md-offset-5">
							   <button class="download-btn" id="Submit">Submit</button>
						</div>
						
					</div>
					
				</div>
            </div>
			
			<table>
		    <tr>
				<div class="scrollD">
					<table id="statusEnquiryDatatable" align="center" class="display" cellspacing="0" width="100%">
							<thead>
								<tr class="boxheadingsmall" style="font-size: 11px;">
									<th style="text-align:center;" data-orderable="false">Date Processed</th>
									<th style="text-align:center;" data-orderable="false">Total Processed</th>
									<th style="text-align:center;" data-orderable="false">Total Captured</th>
									<th style="text-align:center;" data-orderable="false">Total Pending</th>
									<th style="text-align:center;" data-orderable="false">Total Failed / Rejected</th>
									
								</tr>
							</thead>
							<tbody id="tableContent">
									
							</tbody>
							
							
					</table>
				</div>
						
			</tr>
		    </table>
        </section>
       

		
      </td>
    </table>
<script type="text/javascript">
	 function handleChange() {
     }
	$(document).ready(function() {
		
		$(function() {
			$("#dateFrom").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : false,
				maxDate : new Date()
			});
			$("#dateTo").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : false,
				maxDate : new Date()
			});
		});
		$(function() {
			var today = new Date();
			$('#dateTo').val($.datepicker.formatDate('dd-mm-yy', today));
			$('#dateFrom').val($.datepicker.formatDate('dd-mm-yy', today));
			

		});
	});
	
			
</script>
<script type="text/javascript">
	 function handleChange() {
     }
	$(document).ready(function() {
		
		$(function() {
			$("#createDate").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : false,
				maxDate : new Date()
			});
			
		});
		$(function() {
			var today = new Date();
			$('#createDate').val($.datepicker.formatDate('dd-mm-yy', today));
			//$('#dateFrom').val($.datepicker.formatDate('dd-mm-yy', today));
			

		});
	});
	
			
</script>

<script>
function Validate(event) {
	  var regex = /^\d+$/;
	    var key = String.fromCharCode(!event.charCode ? event.which : event.charCode);
	    if (!regex.test(key)) {
	       event.preventDefault();
	       return false;
	    }
} 
</script>
	
<script>
function disableMsg(){
	 document.getElementById("responseMessage").style.display = "none";
}

function enableMsg(){
	document.getElementById("responseMessage").style.display = "block";
}
</script>

</body>
</html>