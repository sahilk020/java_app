<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Status Inquiry</title>

<link href="../assets/plugins/custom/datatables/datatables.bundle.css"
  rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css"
  rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet"
  type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
  type="text/css" />
<script src="../js/loader/main.js"></script>
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
<script src="../assets/js/widgets.bundle.js"></script>
<script src="../assets/js/custom/widgets.js"></script>
<script src="../assets/js/custom/apps/chat/chat.js"></script>
<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
<script src="../assets/js/custom/utilities/modals/users-search.js"></script>
</head>

<body class="bodyColor post d-flex flex-column-fluid">


  <!--begin::Toolbar-->
  <div class="toolbar" id="kt_toolbar">
    <!--begin::Container-->
    <div id="kt_toolbar_container"
      class="container-fluid d-flex flex-stack">
      <!--begin::Page title-->
      <div data-kt-swapper="true" data-kt-swapper-mode="prepend"
        data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
        class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
        <!--begin::Title-->
        <h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Status
          Inquiry</h1>
        <!--end::Title-->
        <!--begin::Separator-->
        <span class="h-20px border-gray-200 border-start mx-4"></span>
        <!--end::Separator-->
        <!--begin::Breadcrumb-->
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
          <li class="breadcrumb-item text-muted">Analytics</li>
          <!--end::Item-->
          <!--begin::Item-->
          <li class="breadcrumb-item"><span
            class="bullet bg-gray-200 w-5px h-2px"></span></li>
          <!--end::Item-->
          <!--begin::Item-->
          <li class="breadcrumb-item text-dark">Status Inquiry</li>
          <!--end::Item-->
        </ul>
        <!--end::Breadcrumb-->
      </div>
      <!--end::Page title-->

    </div>
    <!--end::Container-->
  </div>
  <!--end::Toolbar-->
  <!--begin::Post-->
  <div class="post d-flex flex-column-fluid" id="kt_post">
    <!--begin::Container-->
    <div id="kt_content_container" class="container-xxl">
      <div class="row my-5">
        <div class="col">
          <div class="card">
            <div class="card-body">
              <s:form id="statusInquiryForm">
                <s:token />
                <div class="row">
                  <div class="col-md-4 fv-row">
                    <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
                      PG REF NO</label>
                    <s:textfield name="pgRefNo" id="pgRefNo"
                      class="form-control form-control-solid" maxlength="50"
                      autocomplete="off" />
                  </div>
          <div class="col-md-4 fv-row">
                  <button type="button" id="btnCheckStatus" name="btnCheckStatus" class="btn btn-primary" style="margin-top: 9%; padding-bottom: 1%;"
                    onclick="javascript: statusInquiry();">Check Status</button>
        </div>
                </div>
              </s:form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="clear"></div>
  <script type="text/javascript">
  function statusInquiry() {
    var urls = new URL(window.location.href);
    var domain = urls.origin;
    var apiUrl = domain + "/pgws/enquiry/process";
    $
        .ajax({
            url: apiUrl,
            timeout: 0,
            type: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            data: JSON.stringify({
                PG_REF_NUM: $("#pgRefNo").val()
            }),
            success: function (data) {
                var message = 'Status of specified transaction is "' + data.STATUS + '" Message is "' + data.RESPONSE_MESSAGE + '"';
                alert(message);
                window.location.reload();
            },
            error: function (data) {
            }
        });
  }
  
  </script>
</body>
</html>