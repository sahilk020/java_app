<%@page import="com.pay10.crm.actionBeans.TransactionStatusBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">

<head>
<title>Create Menu</title>

<link href="../assets/plugins/global/plugins.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
	type="text/css" />
<script src="../js/loader/main.js"></script>
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<script src="../assets/js/widgets.bundle.js"></script>
<script src="../assets/js/custom/widgets.js"></script>
<script src="../assets/js/custom/apps/chat/chat.js"></script>
<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
<script src="../assets/js/custom/utilities/modals/users-search.js"></script>
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>
<script src="../js/jquery.min.js" type="text/javascript"></script>
<link href="../js/plugins/tags-input/bootstrap-tagsinput.css" rel="stylesheet">
<script src="../js/plugins/tags-input/bootstrap-tagsinput.js"></script>
<style type="text/css">
.dt-buttons.btn-group.flex-wrap {
	display: none;
}

.error {
	color: red
}
.form-control1 {
    display: block;
    width: 62%;
    padding: 0.775rem 1rem;
    font-size: .9rem;
    font-weight: 500;
    line-height: 1.1;
    color: #5E6278;
    background-color: #ffffff;
    background-clip: padding-box;
    border: 1px solid #E4E6EF;
    appearance: none;
    border-radius: 0;
    box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.075);
    transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
}
body{
  font-family: 'Arial';
  font-size: 12px;
}
main{
  position: absolute;
  top: 0px;
  left: 0px;
  right: 0px;
  bottom: 0px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}

	.container {
  width: 500px;
  background: #f0f0f0;
  padding: 15px 15px 10px 15px;
  border-radius: 15px;
}
.container ul {
  list-style: none;
  margin: 0px;
  padding: 0px;
  display: inline-block;
}
.container ul li {
  display: inline-block;
  background: #555;
  color: white;
  padding: 3px 5px 3px 10px;
  border-radius: 15px;
  margin-right: 5px;
  margin-bottom: 5px;
}
.container ul li a {
  color: white;
  text-decoration: none;
  margin-left: 5px;
  font-size: 10px;
  background: #333;
  width: 15px;
  height: 15px;
  border-radius: 50%;
  display: inline-flex;
  text-align: center;
  align-items: center;
  justify-content: center;
}
.container ul li a:hover {
  color: red;
}
.bootstrap-tagsinput .tag {
    margin-right: 2px;
     color: #5E6278 !important;
}
.bootstrap-tagsinput{
	width: 100% !important;
	    overflow-x: auto;
        overflow-y: auto;
        height: 35px;

}

</style>
<script>
var perm='<s:property value="permission"/>';
   $("#permission").val(perm);

$(window).on('load', function() {
             $("#permission").tagsinput('add', perm);
});

</script>
</head>

<body>

	<div class="content d-flex flex-column flex-column-fluid"
		id="kt_content">

		<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container"
				class="container-fluid d-flex flex-stack">
				<!--begin::Page title-->
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
					data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
					class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Create Menu
					</h1>
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
						<li class="breadcrumb-item text-muted">Shield</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Create Menu</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

			</div>
			<!--end::Container-->
		</div>

		<s:form action="" id="addForm">
			<s:token />
			<div class="post d-flex flex-column-fluid" id="kt_post">
				<!--begin::Container-->
				<div id="kt_content_container" class="container-xxl">
					<!--begin::Input group-->
					<div class="card">
						<div class="card-body">
							<div class="row g-9 mb-8">
								<!--begin::Col-->

								<div class="col-md-4">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="">Create Menu<b class=error>*</b></span>
									</label>

									<div class="txtnew">
										<s:textfield id="menuName" name="menuName"
											class="form-control form-control-solid" placeholder="Enter menu name"
											autocomplete="off"/>
										<span id='menuNameError' class='error'></span>
									</div>

								</div>

								<div class="col-md-4">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="">Title<b class=error>*</b></span>
									</label>

									<div class="txtnew">
										<s:textfield id="title" name="title"
											class="form-control form-control-solid"
											placeholder="Enter title" autocomplete="off" />
										<span id='titleError' class='error'></span>
									</div>

								</div>
								<div class="col-md-4">
                                <!--begin::Label-->
                                <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
                                    <span class="">Parent<b class=error>*</b></span>
                                </label>

                                 <div class="txtnew">
                                    <s:textfield id="parentId" name="parentId"
                                        class="form-control form-control-solid"
                                        placeholder="Enter parent Id" autocomplete="off" />
                                    <span id='parentError' class='error'></span>
                                </div>
							</div>


						</div>

						<div class="row g-9 mb-8">
							<!--begin::Col-->

							<div class="col-md-4">
								<!--begin::Label-->
								<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
									<span class="">Link<b class=error>*</b></span>
								</label>

								<div class="txtnew">
									<s:textfield id="link" name="link"
										class="form-control form-control-solid" placeholder="Enter proper link"
										autocomplete="off"/>
									<span id='linkError' class='error'></span>
								</div>

							</div>

							<div class="col-md-4">
								<!--begin::Label-->
								<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
									<span class="">Application<b class=error>*</b></span>
								</label>

								<div class="txtnew">
                                    <s:select name="aaData"
                                        class="form-select form-select-solid mt-1"
                                        id="applicationId" list="aaData"
                                        listKey="id" listValue="name" autocomplete="off" data-control="select-2"/>
                                    <span id='applicationError' class='error'></span>
                                </div>

							</div>
							<div class="col-lg-4">
							<!--begin::Label-->
							<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
								<span class="">Action<b class=error>*</b></span>
							</label>

                                 <div class="txtnew">
							<!-- <s:textfield id="actionName" name="actionName"
                                class="form-control form-control-solid" placeholder="Enter action name"
                                autocomplete="off" style="margin-bottom: 10px;"/>
								<span id='actionError' class='error'></span> -->


								<input id="permission" type="text" name="permission"  class="form-control form-control-solid mb-4" data-role="tagsinput" />

								<!-- <ul id="list"></ul>
								<input type="text" id="txt" placeholder="type and Enter ...">		 -->
							</div>
							<!-- <span>	<i class="fa fa-plus" style="font-size:26px" id="add"></i></span>
							<span><i class="fa fa-minus" style="font-size:26px" id="remove"></i></span>			 -->
						</div>
	                    <s:hidden name="id" id="id" />
	             <div class="col-md-2"
									style="margin-left:600px; margin-top: 30px;">

									<button type="button" class="btn btn-primary"
										onclick="updateMenu();">submit</button>
								</div>

					</div>
					</div>
				</div>
				</div>
			</div>
		</s:form>
	</div>
	<script type="text/javascript">

function updateMenu() {
debugger
	var aa;
	aa=$('#permission').val();
	var url = "";
    var urls = new URL(window.location.href);
    var domain = urls.origin;
    var data = {
            id: $("#id").val(),
            menuName : $("#menuName").val(),
            title : $("#title").val(),
            parentId : $("#parentId").val(),
            link : $("#link").val(),
            applicationId : $("#applicationId").val(),
            permission: aa,
    }

    $.ajax({

        type : "POST",
        url : "updateMenuById",
        data : data,
        success : function(response) {

                alert("Menu Details Updated successfully");
                 window.location.href =domain+"/crm/jsp/allMenus";


        }

    });

}


var txt = document.getElementById('txt');
var list = document.getElementById('list');
var items = [];

txt.addEventListener('keypress', function(e) {
  if (e.key === 'Enter') {
    let val = txt.value;
    if (val !== '') {
      if (items.indexOf(val) >= 0) {
        alert('Tag name is a duplicate');
      } else {
        items.push(val);
        render();
        txt.value = '';
        txt.focus();
      }
    } else {
      alert('Please type a tag Name');
    }
  }
});

function render() {
  list.innerHTML = '';
  items.map((item, index) => {
    list.innerHTML += `<li><span>${item}</span><a href="javascript: remove(${index})">X</a></li>`;
  });
}

function remove(i) {
  items = items.filter(item => items.indexOf(item) != i);
  render();
}

window.onload = function() {
  render();
  txt.focus();
}
	</script>
</body>
</html>
