<%@taglib prefix="s" uri="/struts-tags"%>
<html lang="en" dir="ltr">
    <head><base href=""/>
		<title>BPGATE</title>
		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1" />
		<link rel="shortcut icon" href="../assets/media/images/paylogo.svg" />
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
		<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />

		<!--end::Global Stylesheets Bundle-->
	</head>
<body>
    <div class="signin d-flex align-items-center" style="height: 100vh;">
        <div class="container text-center">
            <div class="row justify-content-center">
                <div class="col-lg-8 col-md-6 col-sm-12">
                    <div class="card my-4">
                        <div class="card-body my-3 p-lg-15 p-sm-8 p-md-8">
                            <img src="../assets/media/images/paylogo.svg" alt="Pay10 Logo" style="height:100px;">
                            <div class="row">
                                <div class="col p-sm-8 p-md-8">
                                    <div class="d-flex flex-column align-items-center justify-content-center">
                                        <div class="fs-2hx fw-bold text-gray-800 text-center text-primary">
                                            <span class="text-danger">You have successfully logged out</span>
                                        </div>
                                        <h1 class="fw-semibold text-gray-800 text-center lh-lg">click here to login again</h1>
                                        <s:a action="index">
										<span class="lh-lg login-text">Login</span>
									</s:a>
                                        <!-- <a href="signin.html" class="lh-lg">Login</a> -->
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script src="../assets/plugins/global/plugins.bundle.js"></script>
	<script src="../assets/js/scripts.bundle.js"></script>

</body>
</html>