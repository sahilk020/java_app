<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Verify Transaction Data</title>
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<script type="text/javascript" src="../js/jquery.min.js"></script>

<script src="../js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>
<link href="../fonts/css/font-awesome.min.css" rel="stylesheet" />




<script type="text/javascript">
	
	
	$(document).ready(function() {
		$('#example').DataTable({
			dom : 'B',
			buttons : [ 'csv' ]
		});
	});
	function download() {
		var csv = 'ORDER_ID,PG_REF_NUM,ACQ_ID\n';
		var hiddenElement = document.createElement('a');
		hiddenElement.href = 'data:text/csv;charset=utf-8,' + encodeURI(csv);
		hiddenElement.target = '_blank';
		hiddenElement.download = 'VerifyTxn_Data.csv';
		hiddenElement.click();
	}
</script>

<style>

.btn-fl{float: right;clear: right;margin-right: -70px;}
/* .btn-small{padding: 6px!important;} */
.bws-tp{margin-bottom: -5px!important; margin-top: -10px;}
/*.inputfieldsmall {height: 28px!important;padding:4px 80px!important;} */
table #BinRange tbody th, table #BinRange tbody td{text-align:center;}
.inputfieldsmall1 {
    display: inline-block!important;
    /* padding: 4px 15px;
    height: 28px;
    font-size: 11px;
    font-family: 'Titillium Web', sans-serif;
    line-height: 1.428571429;
    color: black;
    margin-bottom: 5px;
    background-color: #fff;
    background-image: none; */
    border: 1px solid #ccc;
    border-radius: 4px;
    -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
    box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
    -webkit-transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
    transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
	}


table.dataTable thead .sorting {
    background: none !important;
}
.sorting {
    background: none !important;
}
.btn:focus{
		outline: 0 !important;
}
#wwctrl_btnUpload{
	text-align: center;
	margin-left:-174px;/* dd */
}
.element {
    display: flex;
}
</style>
</head>
<body >
<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
	
	<!-- <div>
		<table width="100%" align="left" cellpadding="0" cellspacing="0"
			class="txnf">
			<tr>
				<td align="left" colspan="3"><div class="container">
						<div class="row">
							<div class="col-md-7 col-xs-12 text-left">
								<h2 style="text-align:left!important;">Verify Transaction Data</h2>
							</div>
						</div>
					</div></td>
			</tr>
		</table>
	</div> -->
	<!-- <div class="card ">
		<div class="card-header card-header-rose card-header-text">
		  <div class="card-text">
			<h4 class="card-title">Verify Transaction Data<br>( Uplaod File )</h4>

		  </div>
		</div>
		<br> -->
		
			<div class="content flex-column" id="kt_content">
		<!--begin::Toolbar-->
		<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
				<!--begin::Page title-->
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend" data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}" class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Verify Transaction Data</h1>
					<!--end::Title-->
					<!--begin::Separator-->
					<span class="h-20px border-gray-200 border-start mx-4"></span>
					<!--end::Separator-->
					<!--begin::Breadcrumb-->
					<ul class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
						<!--begin::Item-->
						<li class="breadcrumb-item text-muted">
							<a href="home" class="text-muted text-hover-primary">Dashboard</a>
						</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item">
							<span class="bullet bg-gray-200 w-5px h-2px"></span>
						</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-muted">Batch Operations</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item">
							<span class="bullet bg-gray-200 w-5px h-2px"></span>
						</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Verify Transaction Data</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->
				
			</div>
			<!--end::Container-->
		</div>
		<div class="post d-flex flex-column-fluid" id="kt_post">
		<div id="kt_content_container" class="container-xxl">
		<div class="row my-5">
		<div class="col">
		<div class="card">			
		<div class="card-body ">
		
							<!-- <table width="100%" border="0" align="center" cellpadding="0"
		cellspacing="0" class="txnf product-spec"> -->
		<!-- <tr>
			<th colspan="6" align="left">UPLOAD FILE</th>
		</tr> -->
		<!-- <tr> -->
			
			<s:form action="validateTxnDataAction" method="POST"
				enctype="multipart/form-data" style="width:100%">
				
				
				<script type="text/javascript">
					$("body")
							.on(
									"click",
									"#btnUpload",
									function() {
										var allowedFiles = [ ".csv" ];
										var fileUpload = $("#fileUpload");
										var lblError = $("#lblError");
										var regex = new RegExp(
												"([a-zA-Z0-9\s_\\.\-:])+("
														+ allowedFiles
																.join('|')
														+ ")$");
										if (!regex.test(fileUpload.val()
												.toLowerCase())) {
											lblError
													.html("Please upload files having extensions: <b>"
															+ allowedFiles
																	.join(', ')
															+ "</b> only.");
											return false;
										}
										lblError.html('');
										return true;
									});
				</script>
				<!-- <div class="element"> -->
				<div class="row mb-4 align-items-start" style="column-gap: 20px;">
				<div class="col-lg-3 col-md-5 col-sm-12 p-0" onclick="download()">
												<a type="button" id="csvdownload"
													class="btn-hover-rise my-2 download-btn ">
													<span class="bluespan">CSV</span>
													<span class="blackspan">Sample csv file</span>
												</a>
											</div>
				<!-- <div class="col-sm-4 col-lg-3">
					<table class="table table-striped table-row-bordered gy-5 gs-7" id="example" style="display: none;">
						<thead>
							<tr>
								<th>ORDER_ID</th>
								<th>PG_REF_NUM</th>
								<th>ACQ_ID</th>
							</tr>
						</thead>
					</table>Download CSV File Format Example
					</div> -->
					<div class="col-lg-3 col-md-5 col-sm-12 my-2 file-group p-0 ">
												<div class="file-input">
													<s:file name="fileName" id="file-input" accept=".csv"
														class="file-input__input" onchange="getfilename(this)" />
													<span id="fileCSVErr"></span>

													<label class="file-input__label" for="file-input">
														<img src="../assets/media/images/folder-svg.svg" alt="">
														<span class="m-0 blackspan" id="filename"></span>
														<span>Browse</span>
													</label>
												</div>
											</div>
					
				<%-- <div class="col-sm-4 col-lg-3" style="margin-top:10px;">
						<span > 
						<input type="text"	 id="fileUpload" readonly>
							<span > 
							<span class="glyphicon glyphicon-folder-open">
							</span>
								&nbsp;&nbsp;Browse 
							<s:file name="fileName" />
						</span>
						</span>
						</br></br></br>
						<span id="lblError" style="color: red;"></span> <br />
					</div> --%>
					
					
				<div class="col-lg-3 col-md-5 col-sm-12 my-2 file-group p-0 ">
					<s:submit
						value="Upload" name="fileName" id="btnUpload"
						class="btn btn-primary btn-small" />
				</div>
				</div>
			<!-- </div>// -->
			
				<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
			</s:form>
		</div>
		<!-- </tr>
	</table> -->
							</div>
							</div>
						
						</div>
						</div>
						</div>
						</div>
						</div>	
							
							
	
	<script>
		$(document).on(
				'change',
				'.btn-file :file',
				function() {
					var input = $(this), numFiles = input.get(0).files ? input
							.get(0).files.length : 1, label = input.val()
							.replace(/\\/g, '/').replace(/.*\//, '');
					input.trigger('fileselect', [ numFiles, label ]);
				});

		$(document)
				.ready(
						function() {
							$('.btn-file :file')
									.on(
											'fileselect',
											function(event, numFiles, label) {

												var input = $(this).parents(
														'.input-group').find(
														':text'), log = numFiles > 1 ? numFiles
														+ ' files selected'
														: label;

												if (input.length) {
													input.val(log);
												} else {
													if (log)
														alert(log);
												}

											});
						});
	
		
	</script>
</body>
</html>