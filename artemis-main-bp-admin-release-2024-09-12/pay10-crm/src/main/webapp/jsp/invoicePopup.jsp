<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Invoice Details</title>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/custom.css" rel="stylesheet" type="text/css" />
<script>
	if (self == top) {
		var theBody = document.getElementsByTagName('body')[0];
		theBody.style.display = "block";
	} else {
		top.location = self.location;
	}
	function copyInvoiceLink(copyLinkElement){
		document.getElementById("invoiceShortLinkBtn").disabled = !document.queryCommandSupported('copy');
		document.getElementById("invoiceLinkBtn").disabled = !document.queryCommandSupported('copy');
		var copiedLink = document.getElementById(copyLinkElement);
		copiedLink.select();
		document.execCommand('copy');
	}
</script>
<script>
	function closebtn(){
		$('.messi').remove();
	}
	function tabChangePopUp(id){
					debugger
					if(id=="link110"){
						document.getElementById("customerInfo").classList.add("active");
						document.getElementById("paymentInfo").classList.remove("active");
						document.getElementById("productInfo").classList.remove("active");

						document.getElementById("link110").classList.add("active");
						document.getElementById("link111").classList.remove("active");
						document.getElementById("link113").classList.remove("active");
					}
					if(id=="link111"){
						document.getElementById("productInfo").classList.add("active");
						document.getElementById("paymentInfo").classList.remove("active");
						document.getElementById("customerInfo").classList.remove("active");

						document.getElementById("link111").classList.add("active");
						document.getElementById("link110").classList.remove("active");
						document.getElementById("link113").classList.remove("active");
					}
					if(id=="link113"){
						
						document.getElementById("paymentInfo").classList.add("active");
						document.getElementById("customerInfo").classList.remove("active");
						document.getElementById("productInfo").classList.remove("active");

						document.getElementById("link113").classList.add("active");
						document.getElementById("link111").classList.remove("active");
						document.getElementById("link110").classList.remove("active");
					}
				}
</script>

<style>
	table.invoicetable th {
    font-weight: 500;
    background-color: #fcfcfc;
    color: #000000;
	font-size: 11px;
}
table.invoicetable td {
    font-size: 11px;
    background-color: #fff;
    color: #000000;
    font-weight: 500;
}
.btn-custom{
    margin-top: 5px;
	height:27px;
	border: 1px solid #f36f27;
    width: 73px;
    padding: 5px;
    background:url(../image/textF.jpg) repeat-x bottom #f4882d;
    font: bold 12px Tahoma;
    color: #fff;
    cursor: pointer;
    border-radius: 5px;
    z-index: 999;
    position: relative;
}

.innerpopupDv {
    width: 60%;
    margin: 60px auto;
}
@media only screen and (max-width: 768px) {
  .innerpopupDv{
    width: 100%;
  }
}
/* Style the Image Used to Trigger the Modal */


/* The Modal (background) */
.messi {
	position:fixed;
	top: 0;
    right: 0;
    bottom: 0;
    left: 0;
  display: none; /* Hidden by default */
  /* Stay in place */
 
  /* left: 0;
  top: 0; */
  width: 100%; /* Full width */
  height: 100%; /* Full height */
  overflow: hidden; /* Enable scroll if needed */
  outline: 0;
    opacity: 1 !important;
  
}
#popup {
    background: rgba(0,0,0,0.7);
    width: 100%;
    /* height: 200%; */
    z-index: 999;
    display: none;
}

/* Modal Content (Image) */
.modal-content {
  margin: auto;
  display: block;
  width: 80%;
  max-width: 900px;
}
.my_class {
    color: #2ea3f2;
    text-decoration: underline;
    cursor: pointer !important;
   
}
/* Caption of Modal Image (Image Text) - Same Width as the Image */
#caption {
  margin: auto;
  display: block;
  width: 80%;
  max-width: 700px;
  text-align: center;
  color: #ccc;
  padding: 10px 0;
  height: 150px;
}

/* Add Animation - Zoom in the Modal */
.modal-content, #caption {
  animation-name: zoom;
  animation-duration: 0.6s;
}

@keyframes zoom {
  from {transform:scale(0)}
  to {transform:scale(1)}
}

/* The Close Button */
.close {
  position: absolute;
  top: 15px;
  right: 35px;
  color: #f1f1f1;
  font-size: 40px;
  font-weight: bold;
  transition: 0.3s;
}

.close:hover,
.close:focus {
  color: #bbb;
  text-decoration: none;
  cursor: pointer;
}

/* 100% Image Width on Smaller Screens */
@media only screen and (max-width: 700px){
  .modal-content {
    width: 100%;
  }
}
.nav-pills.nav-pills-rose .nav-item .nav-link.active, .nav-pills.nav-pills-rose .nav-item .nav-link.active:focus, .nav-pills.nav-pills-rose .nav-item .nav-link.active:hover {
    background: linear-gradient(45deg, #234B7A, #ffffff);
    /* box-shadow: 0 4px 20px 0px rgb(0 0 0 / 14%), 0 7px 10px -5px #aca353; */
    color: #ffff;
	}
	.nav-pills.nav-pills-icons .nav-item .nav-link {
    border-radius: 4px;
}
	.nav-pills .nav-item i {
    display: block;
    font-size: 30px;
    padding: 15px 0;
	text-align:center;
}
.nav-link.active i {
    color: #fdf8f8 !important;
}
.nav-pills .nav-item .nav-link {
    line-height: 24px;
    text-transform: uppercase;
    font-size: 12px;
    font-weight: 500;
    min-width: 100px;
    text-align: center;
    color: #555;
    transition: all .3s;
    border-radius: 30px;
    padding: 10px 15px;
}
.card .card-header .card-title1,
				.card .card-header .card-title1 .card-label {
					font-weight: 500;
					font-size: 1.275rem;
					
				}
</style>
</head>
<body>
	
	<div class="">
    
    
		<!-- Navbar -->
	   
		<!-- End Navbar -->
		<!-- <div class="content"> -->
		  <div class="container">
		   
			<div class="">
			  
			  
				<div class="card ">
					<div class="card-header" style="display: inline-flex;padding: 1rem;">
						<h4 class="card-title1">Customer Billing Information</h4>
						<button style="position: relative;border: none;background: none;top: 2px;" id="closeBtn1" onclick="closebtn()"><i class="fa fa-times" aria-hidden="true"></i></button>
					</div>
				
				  <div class="card-body ">
					<div class="row">
						<div class="col-md-12 ml-auto mr-auto">
					  <div class="col-lg-4 col-md-6">
						<!--
								  color-classes: "nav-pills-primary", "nav-pills-info", "nav-pills-success", "nav-pills-warning","nav-pills-danger"
							  -->
						<ul class="nav nav-pills nav-pills-rose nav-pills-icons flex-column"  role="tablist">
						  <li class="nav-item" id="listitem">
							<a class="nav-link active" data-toggle="tab" href="#link110" role="tablist" onclick="tabChangePopUp('link110')" id="customerInfo">
							  <i class="fa fa-user"></i> Customer Information
							</a>
						  </li>
						  <li class="nav-item" id="listitem">
							<a class="nav-link" data-toggle="tab" href="#link111" role="tablist" onclick="tabChangePopUp('link111')" id="productInfo">
							  <i class="fa fa-archive"></i>Product Information 
							</a>
						  </li>
						  <!-- <s:if test="%{invoice.invoiceType=='PROMOTIONAL PAYMENT'}">
							<li class="nav-item" id="listitem">
							<a class="nav-link" data-toggle="tab" href="#link112" role="tablist">
							  <i class="material-icons">person</i> Sender Information
							</a>
						  </li>
						</s:if> -->
						  <li class="nav-item" id="listitem">
							<a class="nav-link" data-toggle="tab" href="#link113" role="tablist" onclick="tabChangePopUp('link113')" id="paymentInfo">
							  <i class="fa fa-link"></i> Links
							</a>
						  </li>
						</ul>
					  </div>
					  <div class="col-md-8">
						<div class="tab-content">
						  <div class="tab-pane active" id="link110">
							
							<h4 style="margin-left:10px;font-weight:600">Customer Information</h4>
								   <div class="card-body" style="    overflow: auto; ">
								   
									   <table width="100%" class="invoicetable">				
										 <tr>
										   <th  align="left" valign="middle">Invoice No</th>
										   <th  align="left" valign="middle">Name</th>
										  
										  </tr>
									   
										  <tr id="sec11">
										   <td align="left" valign="middle"><s:property value="%{invoice.invoiceNo}"  /></td>
										   <td align="left" valign="middle"><s:property value="%{invoice.name}"  /></td>
										  
										 </tr>
										 <tr>
										   <th  align="left" valign="middle">City</th>
										   <th  align="left" valign="middle">Country</th>
										  
										  </tr>
										   <tr id="sec12">
										   <td align="left" valign="middle"><s:property value="%{invoice.city}"  /></td>
										   <td align="left" valign="middle"><s:property value="%{invoice.country}"  /></td>
										   
										 </tr>
										 <tr>
											<th  align="left" valign="middle">State</th>
											<th  align="left" valign="middle">Zip</th>
										   
										   </tr>
											<tr id="sec13">
											<td align="left" valign="middle"><s:property value="%{invoice.state}"  /></td>
											<td align="left" valign="middle"><s:property value="%{invoice.zip}"  /></td>
											
										  </tr>
										 <tr id="sec14h">
											<th  align="left" valign="middle">Phone</th>
											<th  align="left" valign="middle">Email</th>
	
										  </tr>
										  <tr id="sec14">
											<td align="left" valign="middle"><s:property value="%{invoice.phone}"  /></td>
											<td align="left" valign="middle"><s:property value="%{invoice.email}"  /></td>				
										  </tr>
										  <tr id="sec16h">
											<th  align="left" valign="middle">Address</th>
											<!-- <th  align="left" valign="middle">SALE Amount.</th> -->
										  </tr>
										  <tr id="sec16">
											<td align="left" valign="middle"><s:property value="%{invoice.address}" /></td>
											<!-- <td align="left" valign="middle"></td>														 -->
										  </tr>
										
								   </table>
								  
									 <!-- <label for="cardMask" class="bmd-label-floating">Card Mask</label>
									 <input type="email" class="form-control" id="cardMask"> -->
								   
								   </div> 
						  </div>
						  <div class="tab-pane" id="link111">
							 <h4 style="margin-left:10px;font-weight:600">Product Information</h4>
								<div class="card-body" style="    overflow: auto; ">
								
									<table width="100%" class="invoicetable">
									<!-- <tr>
										<th  height="25" colspan="4" align="left" valign="middle">Card Holder Name</th>
									   
									   </tr> -->
									   <tr id="sec3chn">
										<td height="25" colspan="4" align="left" valign="middle"></td>
									   
									  </tr>
									  
									  <tr>
										<th  align="left" valign="middle">Name</th>
										<th  align="left" valign="middle">Description</th>
									   
									   </tr>
									
									   <tr id="sec3">
										<td align="left" valign="middle"> <s:property value="%{invoice.productName}" /></td>
										<td align="left" valign="middle"> <s:property value="%{invoice.productDesc}" /></td>
									   
									  </tr>
									  <tr>
										<th  align="left" valign="middle">Quantity</th>
										<th  align="left" valign="middle">GST%</th>
									   
									   </tr>
										<tr id="sec4">
										<td align="left" valign="middle"> <s:property value="%{invoice.quantity}" /></td>
										<td align="left" valign="middle"> <s:property value="%{invoice.gst}" /></td>
										
									  </tr>
									  <tr>
										<th align="left" valign="middle">GstAmount</th>
										<th align="left" valign="middle">Amount</th>
										
										</tr>
									  <tr id="sec5">
										<td align="left" valign="middle"> <s:property value="%{invoice.gstAmount}" /></td>
										<td align="left" valign="middle" id="invoiceAmount"> <s:property value="%{invoice.amount}" /></td>
										
										</tr>
										<tr>
										<th align="left" valign="middle">All prices are in</th>
										<th align="left" valign="middle">Expires on</th>
										 
										
										</tr>
									  <tr id="sec6">
										<td align="left" valign="middle"><s:property value="%{currencyName}" /></td>
										<td align="left" valign="middle"><s:property value="%{invoice.expiryTime}" /></td>
										
										</tr>
										<tr>
										<th align="left" valign="middle">Total Amount</th>
										</tr>
										<tr id="address5">
											<td align="left" valign="middle"><s:property value="%{invoice.totalAmount}" /></td>
										</tr>
								</table>
								</div> 
							</div>

							   <div class="tab-pane" id="link113">
								<h4 style="margin-left:10px;font-weight:600">Payment Links</h4>
								   <div class="card-body" style="    overflow: auto; ">
								   
									   <table width="100%" class="invoicetable">
										  <tr>
											<td  align="left" valign="middle" class="bluelinkbig" style="font-size:12px; width:80%;" >
											<input id="invoiceLink" onkeydown="document.getElementById('invoiceLinkBtn').focus();" type="text" style="display:block" class="textFL_merch" value=<s:property value="%{invoice.invoiceUrl}"/>></input></td>
											<td align="left" valign="middle" class="bluelinkbig" style="font-size:12px;"><button id="invoiceLinkBtn" onclick="copyInvoiceLink('invoiceLink')"  href="#" class="btn-custom" value="X">Copy Link</button></td>									
										 </tr>
										   <tr>
											<td align="left" valign="middle" class="bluelinkbig" style="font-size:12px;width:80%;">
											<input id="invoiceShortLink" onkeydown="document.getElementById('invoiceLinkBtn').focus();" type="text" style="display:block" class="textFL_merch" value=<s:property value="%{invoice.shortUrl}" />></input></td>
											<td align="left" valign="middle" class="bluelinkbig" style="font-size:12px;"><button id="invoiceShortLinkBtn" onclick="copyInvoiceLink('invoiceShortLink')" href="#" class="btn-custom" value="X">Copy Link</button></td>
										</tr>  <!-- <tr>
											 <th height="25" colspan="4" align="left" valign="middle"><span>Address</span></th>
	 
										   </tr>
										   <tr id="address6">
											 <td height="25" colspan="4" align="left" valign="middle"></td>
	 
										   </tr> -->
								   </table>
		   
									 <!-- <label for="cardMask" class="bmd-label-floating">Card Mask</label>
									 <input type="email" class="form-control" id="cardMask"> -->
								   
								   </div>
								
								 
								 
								
							   </div>
						</div>
						
					
					  </div>
					</div>
				  </div>
				  <div class="card-footer " style="float: right;">
					<button class="btn btn-danger" id="closeBtn" onclick="closebtn()">Close<div class="ripple-container"></div></button>
					<!-- <button type="submit" class="btn btn-fill btn-rose" id="closeBtn">Close</button> -->
				  </div>
				</div>
			  </div>
			</div>
			
		  </div>
		<!-- </div> -->
	   
	  
	</div>

</body>
</html>