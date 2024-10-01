<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@taglib prefix="s" uri="/struts-tags" %>
	<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>White Listed IP</title>
	<!-- <link href="../css/bootstrap.min.css" rel="stylesheet"> -->
	<link href="../fonts/css/font-awesome.min.css" rel="stylesheet">
</link>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<link href="../css/loader.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="../js/jquery.min.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>


<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
<!--begin::Fonts-->
<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
<!--end::Fonts-->
<!--begin::Vendor Stylesheets(used by this page)-->
<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
<!--end::Vendor Stylesheets-->
<!--begin::Global Stylesheets Bundle(used by all pages)-->
<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>
<script src="../js/mapping.js"></script>

<style>

.extend-input{
  border: 1px solid #000000;
  width: 180px;
  border-radius: 4px;
  box-sizing: border-box;
  font-family: Graphik,-apple-system,system-ui,"Segoe UI",Roboto,Oxygen,Ubuntu,Cantarell,"Fira Sans","Droid Sans","Helvetica Neue",sans-serif;
  font-size: 20px;
  line-height: 1.15;
  overflow: visible;
  padding: 10px 14px;
  text-align: center;
  font-size: 20px;
}

.extend-button{
  border: 1px solid #ffffff;
  background-color: #FFFFFF;
  width: 30px;
  padding: 1px 2px;
  font-weight:900;


}



.button-26,.button-27 {
  appearance: button;
  background-color: #083fcb;
  border: 1px solid #083fcb;
  border-radius: 4px;
  box-sizing: border-box;
  color: #FFFFFF;
  cursor: pointer;
  font-family: Graphik,-apple-system,system-ui,"Segoe UI",Roboto,Oxygen,Ubuntu,Cantarell,"Fira Sans","Droid Sans","Helvetica Neue",sans-serif;
  font-size: 14px;
  line-height: 1.15;
  overflow: visible;
  padding: 12px 16px;
  position: relative;
  text-align: center;
  text-transform: none;
  transition: all 80ms ease-in-out;
  user-select: none;
  -webkit-user-select: none;
  touch-action: manipulation;
  width: fit-content;
}

.button-26:disabled,.button-27:disabled {
  opacity: .5;
}

.button-26:focus,.button-27:focus {
  outline: 0;
}

.button-26:hover,.button-27:hover {
  background-color: #052575;
  border-color: #083fcb;
}

.button-26:active,.button-27:active {
  background-color: #083fcb;
  border-color: #083fcb;
}
</style>

	</head>

	<body>
		<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
			<!--begin::Toolbar-->
			<div class="toolbar" id="kt_toolbar">
				<!--begin::Container-->
				<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
					<!--begin::Page title-->
					<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
						data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
						class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
						<!--begin::Title-->
						<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">WhiteListing IP</h1>
						<!--end::Title-->
						<!--begin::Separator-->
						<span class="h-20px border-gray-200 border-start mx-4"></span>
						<!--end::Separator-->
						<ul
						class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
						<!--begin::Item-->
						<li class="breadcrumb-item text-muted"><a href="home"
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
						<li class="breadcrumb-item text-dark">WhiteListing IP</li>
						<!--end::Item-->
					</ul>

					</div>
					<!--end::Page title-->
				</div>
				<!--end::Container-->
			</div>
			<div class="post d-flex flex-column-fluid" id="kt_post">
				<!--begin::Container-->
				<div id="kt_content_container" class="container-xxl">
					<div class="transactions"></div>
					<s:form theme="simple" id="" name="" action="">
						<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
							<tr>
								<td colspan="4" class="txtnew" align="left" style="padding: 10px;">
									<div id="err" class="actionMessage" style="display: none; margin-bottom: 10px;">
										<ul>
											<li>Please select merchant to proceed</li>
										</ul>
									</div>

									<div id="success" class="success success-text" style="display: none;">Mappings saved successfully</div>
								</td>
							</tr>
							<tr>
								<td>
									<div class="wd70">
										<table>
											<div class="card">
												<div class="card-body">
													<div class="row my-5 mb-4">
														<!--begin::Col-->

														<div class="col-md-4 fv-row">
															<label class="d-flex align-items-center fs-6 fw-bold mb-2">
																<span class="">Merchant</span>
															</label>
															<s:select label="Merchant" name="merchantEmailId"
																class="form-select form-select-solid" id="merchants"
																headerKey="" headerValue="Select Merchant"
																list="listMerchant" listKey="emailId"
																listValue="businessName" value="%{merchantEmailId}"
																onchange="activeIpButton()" autocomplete="off"
																data-control="select2" />

														</div>
														<div class="col-md-4 fv-row">
															<label class="d-flex align-items-center fs-6 fw-bold mb-2">
																<span class="">IP Address</span>
															</label>
															<input class="form-control form-control-solid" disabled placeholder="Enter the single IP Address like (192.45.12.90)" id="ip" name="ipaddress">

														</div>
														<div class="col-md-4 fv-row">
															<label class="d-flex align-items-center fs-6 fw-bold mb-2">
																<br>
															</label>
															<button class="btn btn-primary" type="button" disabled id="extend" onclick="ValidateIPaddress()">ADD New IP</button>

														</div>

													</div>
												</div>
											</div>



								<td align="left">&nbsp;</td>
							</tr>
						</table>

						<div class="clear"></div>
				</div>
				</td>
				</tr>
				</table>
				<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
				</s:form>
				<div class="ml-4">
					<h5>List of Whitelisted IP Address</h5>
					<hr>
					<table id="ipAddressTable">
						<thead style="background-color: #e4e5e7; font-size:12px; font-weight: bolder; text-align-last: center;">
							<th width="100" height="40" hidden>ID</th>
							<th width="100" height="40">Sr. No.</th>
							<th width="1000" height="40" >IP Address</th>
							<th width="180" height="40">Action</th>

						</thead>
						<tbody style="text-align-last: center;">

						</tbody>
					</table>
				</div>
			</div>

		</div>


	<script type="text/javascript">

		$(document).ready(function () {
          var urls = new URL(window.location.href);
          var domain = urls.origin;
       //   var payId = "<s:property value='%{#session.USER.payId}'/>";
	var email = document.getElementById("merchants").value;
	console.log("merchant: "+email);
          $.ajax({
            type: "GET",
            url: domain + "/crmws/list?payId=" + email,
            success: function (data) {
              var response = JSON.parse(JSON.stringify(data));
              console.log(response);
            },

          });

        });

			function activeIpButton(){
				var merchant = document.getElementById("merchants").value;
				console.log(merchant);
				if(merchant == ""){
					document.getElementById("extend").disabled = true;
					document.getElementById("ip").disabled = true;
				}else{
					document.getElementById("extend").disabled = false;
					document.getElementById("ip").disabled = false;

				}
				getMethod();

			}




				function getMethod(){
				  var merchant = document.getElementById("merchants").value;
				  var urls = new URL(window.location.href);
          		  var domain = urls.origin;
				  var tableBody = $("#ipAddressTable tbody");
				  var tableRow="";
				  tableBody.empty();

				  $.ajax({
					type: "GET",
					url: domain + "/crmws/getWhiteListedIpForUi?emailId="+encodeURIComponent(merchant),
					success : function (data) {
						var count=1;
						for(var m in data){
							// console.log(m);
							// console.log(data[m]);
							tableRow+='<tr>';
							tableRow+='<td style="font-size: 15px; font-weight: 200px;" hidden>'+m+'</td><td>'+(count++)+'</td><td style="font-size: 15px; font-weight: 200px;">'+data[m]+'</td>';
							tableRow+='<td width="180" height="50" ><button class="btn" onClick="deleteMethod(this)"><i class="fa fa-trash" style="font-size:20px;color:red"></i></button></td>';
					 		tableRow+='</tr>';
						}
						tableBody.append(tableRow);
						console.log(data);

					},
				});


				}


			function deleteMethod(cont){
				var id = $(cont).closest('tr').find('td:first').html();

				var urls = new URL(window.location.href);
          		var domain = urls.origin;

				  Swal.fire({
  					title: "Do you want to delete this Whitelisted IP Address ?",
 					showDenyButton: true,
  					showCancelButton: true,
					showConfirmButton : false,
  					// confirmButtonText: "Delete",
  					denyButtonText: `Delete`
					}).then((result) => {
  						if (result.isDenied) {
   							 Swal.fire("Deleted!", "", "success");
								$.ajax({
									type: "DELETE",
									url : domain +"/crmws/deleteWhiteListedIp?id="+id,
									success : function (response) {
							 			getMethod();
									},
								});

  						} else {
							getMethod();
 					 	}

				    });
			}

			function saveIpAddress(ipaddress) {
			    var uiIpAddress = ipaddress;
			    var emailId = document.getElementById("merchants").value;
			    var createdBy = "<s:property value='%{#session.USER.payId}'/>";
			    console.log("IP :" + uiIpAddress);
			    console.log("EmailId :" + emailId);
			    console.log("Created By :" + createdBy);

			    var urls = new URL(window.location.href);
			    var domain = urls.origin;

			    // Check if the IP address already exists
			    $.ajax({
			        type: "GET",
			        url: domain + "/crmws/checkIpExists?ip=" + encodeURIComponent(uiIpAddress) + "&payId=" + encodeURIComponent(emailId),
			        success: function(response) {
			            console.log(response);
			            if (response.exists) {
			                Swal.fire("Duplicate Entry!", "IP Address already exists in the database.", "warning");
			            } else {
			                // IP address doesn't exist, proceed to save
			                Swal.fire({
			                    title: "Do you want to save this IP Address for whitelisting?",
			                    showCancelButton: true,
			                    showConfirmButton: true,
			                    confirmButtonText: "Save",
			                }).then((result) => {
			                    if (result.isConfirmed) {
			                        // Save the IP address
			                        $.ajax({
			                            type: "POST",
			                            url: domain + "/crmws/saveWhiteListedIp",
			                            data: {
			                                emailId: emailId,
			                                ip: uiIpAddress,
			                                createdBy: createdBy
			                            },
			                            success: function(response) {
			                                console.log(response);
			                                var ipAddress = $("#ip");
			                                console.log(ipAddress);
			                                ipAddress.val('');
			                                getMethod();
			                                Swal.fire("Saved Successfully!", "", "success");
			                            },
			                            error: function(response) {
			                                console.log(response);
			                                getMethod();
			                                Swal.fire("Error while saving IP Address", "", "error");
			                            },
			                        });
			                    } else {
			                        getMethod();
			                    }
			                });
			            }
			        },
			        error: function(response) {
			            console.log(response);
			            Swal.fire("Error", "An error occurred while checking the IP address.", "error");
			        },
			    });
			}


			function ValidateIPaddress() {
				var testIpAddress = document.getElementById("ip").value;
  				if (/^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(testIpAddress))
				{
				 console.log("Going save");
				 saveIpAddress(testIpAddress);
   				 return (true)
 				 }
 				 alert("You have entered an invalid IP address!")
 				 return (false)
			}


		</script>
		<script type="text/javascript">
			// Initialize select2
			$("#merchants").select2();
			function toggleAjaxLoader(){
			$('body').toggleClass('loaded');
			}
		</script>
	</body>

</html>