<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
  <%@ taglib prefix="s" uri="/struts-tags" %>
    <%@taglib prefix="s" uri="/struts-tags" %>
      <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
      <html>

      <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Add Company Profile</title>
        <link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
        <link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
        <link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
        <link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
        <link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />
        <script src="../js/loader/main.js"></script>
        <script src="../assets/plugins/global/plugins.bundle.js"></script>
        <script src="../assets/js/scripts.bundle.js"></script>
        <script src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
        <script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
        <script src="../assets/js/widgets.bundle.js"></script>
        <script src="../assets/js/custom/widgets.js"></script>
        <script src="../assets/js/custom/apps/chat/chat.js"></script>
        <script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
        <script src="../assets/js/custom/utilities/modals/create-app.js"></script>
        <script src="../assets/js/custom/utilities/modals/users-search.js"></script>
        <script src="../js/commanValidate.js"></script>

        <script language="JavaScript">
          $(document).ready(function () {
            $('#btnEditUser').click(function () {

              var state = document.getElementById("state").value;
              if (state == 'Select State') {
                alert("Please select the State");
                $("form").submit(function (e) {
                  e.preventDefault();
                });
                event.preventDefault();
                return false;
              } else {
                /* $('#loader-wrapper').show(); */
                document.getElementById("frmAddUser").submit();
              }

            });
          });

        </script>
        <style type="text/css">
          .error-text {
            color: #a94442;
            font-weight: bold;
            background-color: #f2dede;
            list-style-type: none;
            text-align: center;
            list-style-type: none;
            margin-top: 10px;
          }

          .error-text li {
            list-style-type: none;
          }

          #response {
            color: green;
          }

          .btn:focus {
            outline: 0 !important;
          }
          .errorMessage{
          color:red !important;
          }
        </style>
      </head>

      <body class="bodyColor">
        <!-- <div id="loader-wrapper" style="width: 100%; height: 100%; display:none;">
	  <div id="loader"></div>
    </div> -->

        <div class="content flex-column" id="kt_content">
          <div class="toolbar" id="kt_toolbar">
            <!--begin::Container-->
            <div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
              <!--begin::Page title-->
              <div data-kt-swapper="true" data-kt-swapper-mode="prepend"
                data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
                class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
                <!--begin::Title-->
                <h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Add Company Profile</h1>
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
                  <li class="breadcrumb-item text-muted">Merchant Setup</li>
                  <!--end::Item-->
                  <!--begin::Item-->
                  <li class="breadcrumb-item">
                    <span class="bullet bg-gray-200 w-5px h-2px"></span>
                  </li>
                  <!--end::Item-->
                  <!--begin::Item-->
                  <li class="breadcrumb-item text-dark">Add Company Profile</li>
                  <!--end::Item-->
                </ul>
                <!--end::Breadcrumb-->
              </div>
              <!--end::Page title-->

            </div>
            <!--end::Container-->
          </div>



          <div class="post d-flex flex-column-fluid" id="kt_post">
            <!--begin::Container-->
            <div id="kt_content_container" class="container-xxl">
              <div class="row my-5">
                <div class="col">
                  <div class="card">
                    <div class="card-body">

                      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="txnf">
                        <!-- <tr>
    <td align="left"><h2>Add Sub-admin</h2></td>
  </tr> -->

                        <s:if test="%{responseObject.responseCode=='000'}">
                          <tr>
                            <td align="left" valign="top">
                              <div id="saveMessage">
                                <s:actionmessage class="success success-text" />
                              </div>
                            </td>
                          </tr>

                        </s:if>
                        <s:else>
                          <div class="error-text">
                            <s:actionmessage />
                          </div>
                        </s:else>

                        <tr>
                          <td align="left" valign="top">
                            <div class="addu">
                              <s:form action="addCompanyProfile" id="frmAddUser">
                                <s:token />

<div class="row">
                                <div class="col-md-4 fv-row">
                                  <label class="d-flex align-items-center fs-6 fw-bold mb-2"> Company
                                    Name</label>
                                  <s:textfield name="companyName" id="companyName"
                                    class="form-control form-control-solid" autocomplete="off"
                                    onkeypress="noSpace(event,this);return isCharacterKey(event);" />

                                </div>
                                <div class="col-md-4 fv-row">
                                  <label class="d-flex align-items-center fs-6 fw-bold mb-2"> Phone</label>
                                  <s:textfield name="mobile" class="form-control form-control-solid" autocomplete="off"
                                    onkeypress="javascript:return isNumber (event)" maxlength="10" />
                                </div>
                                <div class="col-md-4 fv-row">
                                  <label class="d-flex align-items-center fs-6 fw-bold mb-2"> Email</label>
                                  <s:textfield name="emailId" class="form-control form-control-solid"
                                    autocomplete="off" />
                                </div>
                              </div>
                              <div class="row">
                                <div class="col-md-4 fv-row">
                                  <label class="d-flex align-items-center fs-6 fw-bold mb-2"> Telephone
                                    Number</label>
                                  <s:textfield name="telephoneNo" class="form-control form-control-solid"
                                    autocomplete="off" />
                                </div>
                                <div class="col-md-4 fv-row">
                                  <label class="d-flex align-items-center fs-6 fw-bold mb-2"> Fax</label>
                                  <s:textfield name="fax" class="form-control form-control-solid" autocomplete="off" />
                                </div>
                                <div class="col-md-4 fv-row">
                                  <label class="d-flex align-items-center fs-6 fw-bold mb-2"> Address</label>
                                  <s:textfield name="address" class="form-control form-control-solid"
                                    autocomplete="off" />
                                </div>
                              </div>
                              <div class="row">
                                <div class="col-md-4 fv-row">
                                  <label class="d-flex align-items-center fs-6 fw-bold mb-2"> City</label>
                                  <s:textfield name="city" class="form-control form-control-solid" autocomplete="off" />
                                </div>
                                <div class="col-md-4 fv-row">
                                  <label class="d-flex align-items-center fs-6 fw-bold mb-2"> State</label>
                                  <s:select list="@com.pay10.commons.util.States@values()" value="defaultState"
                                    name="state" class="form-select form-select-solid" id="state" listKey="name"
                                    listValue="name"
                                    style="height:46px; width:98% !important; margin-left:2px; border-radius:5px; ">
                                  </s:select>
                                </div>
                                <div class="col-md-4 fv-row">
                                  <label class="d-flex align-items-center fs-6 fw-bold mb-2"> Country</label>
                                  <s:select list="@com.pay10.commons.util.BinCountryMapperType@values()" name="country"
                                    class="form-select form-select-solid" id="country" listKey="name" listValue="name"
                                    style="height:46px; width:98% !important; margin-left:2px; border-radius:5px; ">
                                  </s:select>
                                </div>
                              </div>
                              <div class="row">
                                <div class="col-md-4 fv-row">
                                  <label class="d-flex align-items-center fs-6 fw-bold mb-2"> Postal Code</label>
                                  <s:textfield name="postalCode" class="form-control form-control-solid"
                                    autocomplete="off" />
                                </div>
                                <div class="col-md-4 fv-row">
                                  <label class="d-flex align-items-center fs-6 fw-bold mb-2"> HSN/SAC Code</label>
                                  <s:textfield name="hsnSacCode" class="form-control form-control-solid"
                                    autocomplete="off" />
                                </div>
                                <div class="col-md-4 fv-row">
                                  <label class="d-flex align-items-center fs-6 fw-bold mb-2"> CIN Number</label>
                                  <s:textfield name="cin" class="form-control form-control-solid" autocomplete="off" />
                                </div>
                              </div>
                              <div class="row">
                                <div class="col-md-4 fv-row">
                                  <label class="d-flex align-items-center fs-6 fw-bold mb-2"> Tan Number</label>
                                  <s:textfield name="tanNumber" class="form-control form-control-solid"
                                    autocomplete="off" />
                                </div>
                                <div class="col-md-4 fv-row">
                                  <label class="d-flex align-items-center fs-6 fw-bold mb-2"> Company GST
                                    Number</label>
                                  <s:textfield name="companyGstNo" class="form-control form-control-solid"
                                    autocomplete="off" />
                                </div>
                              </div>
                             
                                <br>
                                <h4 style="color: BLUE;">Bank Details</h4>
                                <br>
                                <div class="row">
                                <div class="col-md-4 fv-row">
                                  <label class="d-flex align-items-center fs-6 fw-bold mb-2"> Bank Name</label>
                                  <s:textfield name="bankName" class="form-control form-control-solid"
                                    autocomplete="off" />
                                </div>
                                <div class="col-md-4 fv-row">
                                  <label class="d-flex align-items-center fs-6 fw-bold mb-2"> IFSC Code</label>
                                  <s:textfield name="ifscCode" class="form-control form-control-solid"
                                    autocomplete="off" />
                                </div>
                                <div class="col-md-4 fv-row">
                                  <label class="d-flex align-items-center fs-6 fw-bold mb-2"> Account Holder
                                    Name</label>
                                  <s:textfield name="accHolderName" class="form-control form-control-solid"
                                    autocomplete="off" />
                                </div>
                                </div>
                                <div class="row">
                                <div class="col-md-4 fv-row">
                                  <label class="d-flex align-items-center fs-6 fw-bold mb-2"> Currency</label>
                                  <s:textfield name="currency" class="form-control form-control-solid"
                                    autocomplete="off" />
                                </div>

                                <div class="col-md-4 fv-row">
                                  <label class="d-flex align-items-center fs-6 fw-bold mb-2"> Branch Name</label>
                                  <s:textfield name="branchName" class="form-control form-control-solid"
                                    autocomplete="off" />
                                </div>
                                <div class="col-md-4 fv-row">
                                  <label class="d-flex align-items-center fs-6 fw-bold mb-2"> Account Number</label>
                                  <s:textfield name="accountNo" class="form-control form-control-solid"
                                    autocomplete="off" />
                                </div>
</div>
<div class="row">
                                <div class="col-md-4 fv-row">
                                  <label class="d-flex align-items-center fs-6 fw-bold mb-2"> PAN Card
                                    Number</label>
                                  <s:textfield name="panCard" class="form-control form-control-solid"
                                    autocomplete="off" />
                                </div>
                                <div class="col-md-4 fv-row">
                                  <label class="d-flex align-items-center fs-6 fw-bold mb-2"> PAN Card Holder
                                    Name</label>
                                  <s:textfield name="panName" class="form-control form-control-solid"
                                    autocomplete="off" />
                                </div>
</div>
                                <div class="card-footer text-right">

                                  <s:submit id="btnEditUser" name="btnEditUser" value="Save" method="submit"
                                    class="btn  btn-block btn-primary disabled"> </s:submit>
                                </div>

                                <s:hidden name="token" value="%{#session.customToken}"></s:hidden>
                            </div>
                    </div>

                    <s:hidden name="token" value="%{#session.customToken}"></s:hidden>
                    </s:form>
                    <div class="clear"></div>
                  </div>
                  </td>
                  </tr>

                  <tr>
                    <td align="left" valign="top">&nbsp;</td>
                  </tr>
                  </table>
                </div>
              </div>

            </div>
          </div>
        </div>
        </div>
        </div>

        <script type="text/javascript">
          $(document).ready(function () {
            if ($('#addCompanyProfile').hasClass("active")) {
              var menuAccess = document.getElementById("menuAccessByROLE").value;
              var accessMap = JSON.parse(menuAccess);
              var access = accessMap["addCompanyProfile"];
              if (access.includes("Add")) {
                $("#btnEditUser").removeClass("disabled");
              }
            }
          });
        </script>


      </body>

      </html>