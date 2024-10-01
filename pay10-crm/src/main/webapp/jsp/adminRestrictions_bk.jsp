<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<title>Admin Fraud Prevention System</title>

  <!--------------JAVASCRIPT--------------->
  <link rel="stylesheet" href="../css/fonts.css">
  
  <!-- <link href="../css/ui.theme.css" rel="stylesheet"> -->
  
  <link href="../css/bootstrap-timepicker.min.css" rel="stylesheet" />

  <link rel="stylesheet" href="../css/fraudPrevention.css" rel="stylesheet">
  <link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css"
	rel="stylesheet" type="text/css" />
  <script src="../js/jquery.js"></script>
  <script type="text/javascript" src="../js/jquery.min.js"></script>
  <script src="../js/jquery.dataTables.js"></script>
  <script type="text/javascript" src="../js/dataTables.buttons.js"></script>
  <script src="../js/core/popper.min.js"></script>
  <script src="../js/core/bootstrap-material-design.min.js"></script>
  <script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script> 
  <!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
   <script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script> 
   <script src="../js/bootstrap.min.js" type="text/javascript"></script> 
   <script src="../js/moment-with-locales.js"></script>
  <script src="../js/bootstrap-datetimepicker.js"></script>
  <script src="../js/jquery.popupoverlay.js"></script>
  <script src="../js/jquery-ui.js"></script> 
  <script src="../js/bootstrap-timepicker.min.js"></script>
  <script src="../js/jquery.select2.js" type="text/javascript"></script>
<script src="../js/jquery.select2.js" type="text/javascript"></script> 
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<script src="../js/fraudtype.js"></script>
<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
<link href="../js/plugins/tags-input/bootstrap-tagsinput.css" rel="stylesheet">
<script src="../js/plugins/tags-input/bootstrap-tagsinput.js"></script>

<link href="../js/plugins/time-picker/jquery.datetimepicker.min.css" rel="stylesheet">
<script src="../js/plugins/time-picker/jquery.datetimepicker.full.js"></script>
<script src="../js/plugins/time-picker/dayjs.min.js"></script>

<script type="text/javascript">
$(document).ready(function(){
 
  // Initialize select2
  $("#payId").select2();
  $('#txnAmountVelocityListBody input[type=text]').addClass('form-control form-control-solid form-control form-control-solid-solid');
  $('.form-select').select2();
 
});
</script>
<style>
.bootstrap-tagsinput{
    width:100% !important;
}

.bootstrap-tagsinput .tag {
	font-weight: bold;
	color: lightslategray;
}

.fileUpload.btn.btn-orange {
    padding: 0px;
    margin: -10px 8px 12px;
}
span#filename {
    width: 138px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}
.card .card-header{
  white-space:nowrap !important;
}
.card-icon{
  margin-top: 24px;
    margin-right: 10px
}
i#materialIcons {
    font-size: 20px;
    color:#ef1923;
}
i.fa.fa-angle-down.rotate-icon {
    color: black;
    font-size: 22px;
    padding: 9px;
}
i.fa.fa-inr {
    font-size: 20px;
    color: #ef1923;
}

i.fa.fa-credit-card {
    font-size: 20px;
    color: #ef1923;
}
 table{
  border-collapse:unset;
}

.form-select .form-select-solid .actions option:hover{
background-color: var(--kt-component-hover-bg);
    color: var(--kt-component-hover-color);
    transition: color 0.2s ease;
    position: relative;
}
 .form-select-solid option{
 background-color:#ffff;
 }

select option:hover {
    box-shadow: 0 0 10px 100px var(--kt-component-hover-bg); inset;
}

input[type="checkbox"] {
margin-right:10px;
}
input[type="radio"] {
margin-right:10px;
}
</style>
<script>

$(document).on('paste', '#search_ipAddListBody,#search_wlIpAddListBody,#search_issuerCountryListBody,#search_userCountryListBody,#search_emailListBody,#search_cardBinListBody,#search_phoneListBody,#search_cardNoListBody,#search_perCardTxnsListBody,#search_domainListBody,#search_wlDomainListBody,#search_macAddListBody', function(e) {
  e.preventDefault();
  // prevent copying action
  //alert(e.originalEvent.clipboardData.getData('Text'));
  var withoutSpaces = e.originalEvent.clipboardData.getData('Text');
  withoutSpaces = withoutSpaces.replace(/\s+/g, '');
  $(this).val(withoutSpaces);
  // you need to use val() not text()
});
$(document).ready(function(){
    $('input#minTransactionAmount').blur(function(){
    
        var num = parseFloat($(this).val());
        var cleanNum = num.toFixed(2);
        $(this).val(cleanNum);
        if(num/cleanNum < 1){
            $('#error').text('Please enter only 2 decimal places, we have truncated extra points');
            }
        });

    $('input#txnAmountVelocity').blur(function(){
        var num = parseFloat($(this).val());
        var cleanNum = num.toFixed(2);
        $(this).val(cleanNum);
        if(num/cleanNum < 1){
            $('#error2').text('Please enter only 2 decimal places, we have truncated extra points');
            }
    });
    



  });
$(document).on('keyup', 'input[name=minTransactionAmount]', function() {
  var _this = $(this);
  var min = parseInt(_this.attr('min')) || ""; // if min attribute is not defined, 1 is default
  var max = parseInt(_this.attr('max')) || 9999999999.99; // if max attribute is not defined, 100 is default
  var val = parseInt(_this.val()) || (min - 1); // if input char is not a number the value will be (min - 1) so first condition will be true
  if (val < min)
    _this.val(min);
  if (val > max)
    _this.val(max);
});
$(document).on('keyup', 'input[name=maxTransactionAmount]', function() {
  var _this = $(this);
  var min = parseInt(_this.attr('min')) || ""; // if min attribute is not defined, 1 is default
  var max = parseInt(_this.attr('max')) || 9999999999.99; // if max attribute is not defined, 100 is default
  var val = parseInt(_this.val()) || (min - 1); // if input char is not a number the value will be (min - 1) so first condition will be true
  if (val < min)
    _this.val(min);
  if (val > max)
    _this.val(max);
});
$(document).ready(function(){
    $('input#maxTransactionAmount').blur(function(){
        var num = parseFloat($(this).val());
        var cleanNum = num.toFixed(2);
        $(this).val(cleanNum);
        if(num/cleanNum < 1){
            $('#error').text('Please enter only 2 decimal places, we have truncated extra points');
            }
        });
  });
$(document).click(function (e) {

	$("#emailToNotifyFTxn").tagsinput();
    if ($(e.target).is('#myModal')) {

    $('#tagmackAddress').tagsinput('removeAll');
    $('#tagmackAddress .tag.label.label-info').remove();
    if($('#alwaysOnFlag1').prop('checked')){
      $('#alwaysOnFlag1').trigger( "click" );
      var dvTimebased1 = document.getElementById("dvTimebased1");
      dvTimebased1.style.display =  "block";
      
    }
        $('#myModal14').modal('hide');
    
    }

});
$(document).click(function (e) {
  
    if ($(e.target).is('#myModal14')) {

    $('#tagipAddress').tagsinput('removeAll');
    $('#tagipAddress .tag.label.label-info').remove();
    if($('#alwaysOnFlag1').prop('checked')){
      $('#alwaysOnFlag1').trigger( "click" );
      var dvTimebased1 = document.getElementById("dvTimebased1");
      dvTimebased1.style.display =  "block";
      
    }
        $('#myModal').modal('hide');
    
    }

});
$(document).click(function (e) {
  
    if ($(e.target).is('#myModal3')) {

    $('#tagnegativeBin').tagsinput('removeAll');
    $('#tagnegativeBin .tag.label.label-info').remove();
    if($('#alwaysOnFlag3').prop('checked')){
      $('#alwaysOnFlag3').trigger( "click" );
      var dvTimebased3 = document.getElementById("dvTimebased3");
      dvTimebased3.style.display =  "block";
      
    }
        $('#myModal3').modal('hide');
    
    }

});
$(document).click(function (e) {
  
    if ($(e.target).is('#myModal5')) {

    $('#tagemail').tagsinput('removeAll');
    $('#tagemail .tag.label.label-info').remove();
    if($('#alwaysOnFlag4').prop('checked')){
      $('#alwaysOnFlag4').trigger( "click" );
      var dvTimebased4 = document.getElementById("dvTimebased4");
      dvTimebased4.style.display =  "block";
      
    }
        $('#myModal5').modal('hide');
    
    }

});
$(document).click(function (e) {
  
    if ($(e.target).is('#myModal9')) {

    $('#tagphone').tagsinput('removeAll');
    $('#tagphone .tag.label.label-info').remove();
    if($('#alwaysOnFlag9').prop('checked')){
      $('#alwaysOnFlag9').trigger( "click" );
      var dvTimebased9 = document.getElementById("dvTimebased9");
      dvTimebased9.style.display =  "block";
      
    }
        $('#myModal9').modal('hide');
    
    }

});

function tagsAddRule(geoLocationType, elementName, elementId, elementValue, countryNameForState, selectedState){

  $("#tagipAddress").css("display","block");
  $("#tagtagipAddress").remove();
  $("#tagmackAddress").css("display","block");
  $("#tagtagmackAddress").remove();
  $('#tagipAddress .tag.label.label-info').remove();
  $("#tagnegativeBin").css("display","block");
  $("#tagtagnegativeBin").remove();
  $('#tagnegativeBin .tag.label.label-info').remove();
  $("#tagemail").css("display","block");
  $("#tagtagemail").remove();
  $('#tagemail .tag.label.label-info').remove();
  $("#tagphone").css("display","block");
  $("#tagtagphone").remove();
  $('#tagphone .tag.label.label-info').remove();
  $("#tagdomainName").css("display","block");
  $("#tagtagdomainName").remove();
  $('#tagdomainName .tag.label.label-info').remove();

  $("#tagemailToNotify").css("display","block");
  $("#tagtagemailToNotify").remove();
  $('#tagemailToNotify .tag.label.label-info').remove();
  $("#tagemailToNotifyVelocity").css("display","block");
  $("#tagtagemailToNotifyVelocity").remove();
  $('#tagemailToNotifyVelocity .tag.label.label-info').remove();
  
  $("#tagvpa").css("display","block");
  $("#tagtagvpa").remove();
  $('#tagvpa .tag.label.label-info').remove();
  $("#fixedAmountFlag").removeAttr("disabled");

  if (geoLocationType == 'country' || geoLocationType == 'state') {
    var urls = new URL(window.location.href);
    var domain = urls.origin;
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
      if (this.readyState == 4 && this.status == 200) {
        let response = JSON.parse(this.responseText);
        if (geoLocationType == 'country') {
          let liList = "";
          for (let i=0; i < response.length; i++) {
              let country = response[i];
              let countryName = country.name;              
              let li = '<li><label><input type="checkbox" id="'+ elementName + country.code +'" name="'+ elementName + '" value= "' + country.code + '" class="issuerClass"><span>' + countryName + '</span></label></li>';
              liList = liList.concat(li);
          }
          document.getElementById(elementId).innerHTML = liList;
          if (elementValue) {
          let elementValues = elementValue.split(",");
          for (let j=0; j < elementValues.length; j++) {
            document.getElementById(elementName + elementValues[j]).click();             
          }
          }
        }

        if (geoLocationType == 'state') {
          let options = "<option>Select Country</option>";
          for (let i=0; i < response.length; i++) {
              let country = response[i];
              let countryName = country.name;
              
              let option;
              if (countryNameForState == countryName) {
                option = '<option selected value="' + countryName + '">' + countryName + '</option>';
              } else {
                option = '<option value="' + countryName + '">' + countryName + '</option>';
              }              
              options = options.concat(option);
          }
          document.getElementById(elementId).innerHTML = options;
          if (elementValue) {
            if (selectedState && selectedState!= null) {
                getStateByCountry(countryNameForState, true, 'stateNameCityBlocking', 'stateNameCityBlocking', elementValue, selectedState);
            } else {
                getStateByCountry(countryNameForState, false, 'stateCodeUl', 'stateCode', elementValue);
            }
          }
        } 
      }
    };
    xhttp.open("GET", domain +  "/crmws/getCountries", true);
    xhttp.send();
}
}

function getStateByCountry(country, dropdown, elementId, elementName, elementValue, stateNameForCity) {
  var urls = new URL(window.location.href);
    var domain = urls.origin;
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
      if (this.readyState == 4 && this.status == 200) {
        let response = JSON.parse(this.responseText);
        if (dropdown) {
          let options = "<option>Select State</option>";
            for (let i=0; i < response.length; i++) {
                let state = response[i];
                let stateName = state.name;
                let option;
                if (stateNameForCity == stateName) {
                    option = '<option selected="true" value="' + stateName + '">' + stateName + '</option>';
                } else {
                    option = '<option value="' + stateName + '">' + stateName + '</option>';
                }
                options = options.concat(option);
            }
            document.getElementById(elementId).innerHTML = options;
            if (elementValue) {
                getCityByState(stateNameForCity, 'cityUl','city', elementValue);
            }
        } else {
           let liList = "";
            for (let i=0; i < response.length; i++) {
                let state = response[i];
                let stateName = state.name;
                let li = '<li><label><input type="checkbox" id="'+ elementName + state.code +'" name="'+ elementName + '" value= "' + state.code + '" class="issuerClass"><span>' + stateName + '</span></label></li>';
                liList = liList.concat(li);
            }
            document.getElementById(elementId).innerHTML = liList;
            if (elementValue) {
              let elementValues = elementValue.split(",");
              for (let j=0; j < elementValues.length; j++) {
                document.getElementById(elementName + elementValues[j]).click();             
              }
            }
        }
      }
    };
    xhttp.open("GET", domain +  "/crmws/getStates?countryName=" + country , true);
    xhttp.send();
}

function getCityByState(state, elementId, elementName, elementValue) {

    var country = $("#countryNameCityBlocking").val();
    var urls = new URL(window.location.href);
    var domain = urls.origin;
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
      if (this.readyState == 4 && this.status == 200) {
        let response = JSON.parse(this.responseText);
        let liList = "";
        for (let i=0; i < response.length; i++) {
            let city = response[i];
            let li = '<li><label><input type="checkbox" id="'+ elementName + city +'" name="'+ elementName + '" value= "' + city + '" class="issuerClass"><span>' + city + '</span></label></li>';
            liList = liList.concat(li);
        }
        document.getElementById(elementId).innerHTML = liList;
        if (elementValue) {
          let elementValues = elementValue.split(",");
          for (let j=0; j < elementValues.length; j++) {
            document.getElementById(elementName + elementValues[j]).click();             
          }
          }
      }
    };
    xhttp.open("GET", domain +  "/crmws/getCities?countryName=" + country + "&stateName=" + state , true);
    xhttp.send();
}
</script>

<script type="text/javascript">
$(document).ready(function() {  
    $.ajaxSetup({
      beforeSend : function(){
          jQuery('body').toggleClass('loaded');
      },
      complete : function(){
        jQuery('body').toggleClass('loaded');
      }
    });     
});
</script>

<script type="text/javascript">
$(function () { 
  $(".From").flatpickr({
                            minDate: new Date(),
                            dateFormat: "d-m-Y",
                            defaultDate: "today",
                            defaultDate: "today",
                        });
                        $(".To").flatpickr({
                          
                            dateFormat: "d-m-Y",
                            defaultDate: "today",
                            minDate: new Date()
                        });
});  
</script>

<script type="text/javascript">
function TimePickerCtrl($) {
	let date = new Date();
	let time = date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
	  var startTime = $('.startTime').timepicker({  
		    timeFormat: 'HH:mm:ss',
		    interval: 1,  
		    defaultTime: time,  
		    startTime: time  
		    });
	  var endTime = $('.endTime').timepicker({  
		    timeFormat: 'HH:mm:ss',
		    interval: 1,  
		    defaultTime: time,  
		    startTime: time  
		    });
}
$(document).ready(TimePickerCtrl);
</script>

<script type="text/javascript">
function ShowHideDiv1(alwaysOnFlag1) {
        var dvTimebased1 = document.getElementById("dvTimebased1");
        dvTimebased1.style.display = alwaysOnFlag1.checked ? "none" : "block";   
        $('#alwaysOnFlag11').text($('#alwaysOnFlag1').val());
    $("#alwaysOnFlag1").on('change', function() {
      if ($(this).is(':checked')) {
       $(this).attr('value', 'true');
     } else {
      $(this).attr('value', 'false');
      }     
      $('#alwaysOnFlag11').text($('#alwaysOnFlag1').val());
    });
 }
function ShowHideDiv2(alwaysOnFlag2) {
        var dvTimebased2 = document.getElementById("dvTimebased2");
        dvTimebased2.style.display = alwaysOnFlag2.checked ? "none" : "block";   
        $('#alwaysOnFlag22').text($('#alwaysOnFlag2').val());
    $("#alwaysOnFlag2").on('change', function() {
      if ($(this).is(':checked')) {
       $(this).attr('value', 'true');
     } else {
      $(this).attr('value', 'false');
      }     
      $('#alwaysOnFlag22').text($('#alwaysOnFlag2').val());
    });
}
function ShowHideDiv3(alwaysOnFlag3) {
        var dvTimebased3 = document.getElementById("dvTimebased3");
        dvTimebased3.style.display = alwaysOnFlag3.checked ? "none" : "block";   
        $('#alwaysOnFlag33').text($('#alwaysOnFlag3').val());
    $("#alwaysOnFlag3").on('change', function() {
      if ($(this).is(':checked')) {
       $(this).attr('value', 'true');
     } else {
      $(this).attr('value', 'false');
      }     
      $('#alwaysOnFlag33').text($('#alwaysOnFlag3').val());
    });
    }
  function ShowHideDiv4(alwaysOnFlag4) {
        var dvTimebased4 = document.getElementById("dvTimebased4");
        dvTimebased4.style.display = alwaysOnFlag4.checked ? "none" : "block";   
        $('#alwaysOnFlag44').text($('#alwaysOnFlag4').val());
    $("#alwaysOnFlag4").on('change', function() {
      if ($(this).is(':checked')) {
       $(this).attr('value', 'true');
     } else {
      $(this).attr('value', 'false');
      }     
      $('#alwaysOnFlag44').text($('#alwaysOnFlag4').val());
    });
    }
  function ShowHideDiv5(alwaysOnFlag5) {
        var dvTimebased5 = document.getElementById("dvTimebased5");
        dvTimebased5.style.display = alwaysOnFlag5.checked ? "none" : "block";   
        $('#alwaysOnFlag55').text($('#alwaysOnFlag5').val());
    $("#alwaysOnFlag5").on('change', function() {
      if ($(this).is(':checked')) {
       $(this).attr('value', 'true');
     } else {
      $(this).attr('value', 'false');
      }     
      $('#alwaysOnFlag55').text($('#alwaysOnFlag5').val());
    });
    }
  function ShowHideDiv6(alwaysOnFlag6) {
        var dvTimebased6 = document.getElementById("dvTimebased6");
        dvTimebased6.style.display = alwaysOnFlag6.checked ? "none" : "block";   
        $('#alwaysOnFlag66').text($('#alwaysOnFlag6').val());
    $("#alwaysOnFlag6").on('change', function() {
      if ($(this).is(':checked')) {
       $(this).attr('value', 'true');
     } else {
      $(this).attr('value', 'false');
      }     
      $('#alwaysOnFlag66').text($('#alwaysOnFlag6').val());
    });
    }
  function ShowHideDiv7(alwaysOnFlag7) {
        var dvTimebased7 = document.getElementById("dvTimebased7");
        dvTimebased7.style.display = alwaysOnFlag7.checked ? "none" : "block";   
        $('#alwaysOnFlag77').text($('#alwaysOnFlag7').val());
    $("#alwaysOnFlag7").on('change', function() {
      if ($(this).is(':checked')) {
       $(this).attr('value', 'true');
     } else {
      $(this).attr('value', 'false');
      }     
      $('#alwaysOnFlag77').text($('#alwaysOnFlag7').val());
    });
  }
  function ShowHideDiv12(alwaysOnFlag12) {
      var dvTimebased12 = document.getElementById("dvTimebased12");
      dvTimebased12.style.display = alwaysOnFlag12.checked ? "none" : "block";
      $('#alwaysOnFlag1212').text($('#alwaysOnFlag12').val());
      $("#alwaysOnFlag12").on('change', function () {
        if ($(this).is(':checked')) {
          $(this).attr('value', 'true');
        } else {
          $(this).attr('value', 'false');
        }
        $('#alwaysOnFlag1212').text($('#alwaysOnFlag12').val());
      });
    }

function ShowHideDiv14(alwaysOnFlag14) {
        var dvTimebased14 = document.getElementById("dvTimebased14");
        dvTimebased14.style.display = alwaysOnFlag14.checked ? "none" : "block";   
        $('#alwaysOnFlag14').text($('#alwaysOnFlag14').val());
    $("#alwaysOnFlag14").on('change', function() {
      if ($(this).is(':checked')) {
       $(this).attr('value', 'true');
     } else {
      $(this).attr('value', 'false');
      }     
      $('#alwaysOnFlag14').text($('#alwaysOnFlag14').val());
    });
 }

function hideMinMaxAmount(fixAmountFlag) {
  if (fixAmountFlag.checked) {
    document.getElementById("fixAmountTable").style.display="block";
    document.getElementById("minMaxAmountDiv").style.display="none";
    return;
  }
  document.getElementById("fixAmountTable").style.display="none";
  document.getElementById("minMaxAmountDiv").style.display="block";
}
</script>
<script type="text/javascript">
$(document).ready(function () {
    //toggle the component with class accordion_body

    $(".accordion_head").click(function () {
        if ($('.ListBody').is(':visible')) {
            $(".ListBody").slideUp(300);
            $(".plusminus").text('+');
        }
        if ($(this).next(".ListBody").is(':visible')) {
            $(this).next(".ListBody").slideUp(300);
            $(this).children(".plusminus").text('+');
        } else {
            $(this).next(".ListBody").slideDown(300);
            $(this).children(".plusminus").text('-');
        }
    });
});
</script>
<script type="text/javascript">
function isNumberKey(evt){
    var charCode = (evt.which) ? evt.which : event.keyCode
    if (charCode > 31 && (charCode < 48 || charCode > 57))
        return false;
    return true;
}
function isNumberKeyAmount(evt) {
    const charCode = (event.which) ? event.which : event.keyCode;
          if (charCode > 31 &&  (charCode < 48 || charCode > 57) && charCode!=46 ) {
            return false;
      return true;
          }
    }

</script>

<style>
.switch {
  position: relative;
  display: inline-block;
  width: 60px;
  height: 34px;
}

.switch input { 
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  -webkit-transition: .4s;
  transition: .4s;
}

.slider:before {
  position: absolute;
  content: "";
  height: 26px;
  width: 26px;
  left: 4px;
  bottom: 4px;
  background-color: white;
  -webkit-transition: .4s;
  transition: .4s;
}

input:checked + .slider {
  background-color: #f67a1b;
}

input:focus + .slider {
  box-shadow: 0 0 1px #f67a1b;
}

input:checked + .slider:before {
  -webkit-transform: translateX(26px);
  -ms-transform: translateX(26px);
  transform: translateX(26px);
}

/* Rounded sliders */
.slider.round {
  border-radius: 34px;
}

.slider.round:before {
  border-radius: 50%;
}

.ftxnbtn {
    width: 77% !important;
    margin-left: -3px !important;
}
</style>

</head>
<body id="kt_body"
class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed toolbar-tablet-and-mobile-fixed aside-enabled aside-fixed"
style="--kt-toolbar-height:55px;--kt-toolbar-height-tablet-and-mobile:55px">

<div class="content d-flex flex-column flex-column-fluid" id="kt_content">`
  <div class="toolbar" id="kt_toolbar">
    <!--begin::Container-->
    <div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
      <div data-kt-swapper="true" data-kt-swapper-mode="prepend"
        data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
        class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
        <!--begin::Title-->
        <h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
          Restrictions</h1>
        <!--end::Title-->
        <!--begin::Separator-->
        <span class="h-20px border-gray-200 border-start mx-4"></span>
        <!--end::Separator-->
        <!--begin::Breadcrumb-->
        <ul class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
          <!--begin::Item-->
          <li class="breadcrumb-item text-muted"><a href="home"
              class="text-muted text-hover-primary">Dashboard</a></li>
          <!--end::Item-->
          <!--begin::Item-->
          <li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span>
          </li>
          <!--end::Item-->
          <!--begin::Item-->
          <li class="breadcrumb-item text-muted">Fraud Prevention</li>
          <!--end::Item-->
          <!--begin::Item-->
          <li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span>
          </li>
          <!--end::Item-->
          <!--begin::Item-->
          <li class="breadcrumb-item text-dark">Restrictions </li>
          <!--end::Item-->
        </ul>
        <!--end::Breadcrumb-->
      </div>
      <!--end::Page title-->

    </div>
  </div>

  
<!--<div id="dialogBox" title="Fraud rule added successfully"></div>-->
  <table width="100%" border="0" cellspacing="0" cellpadding="0" class="table table-striped table-row-bordered gy-5 gs-7">
    <!-- <tr class="fw-bold fs-6 text-gray-800">
      <td width="21%"><h2>Admin-- Fraud Prevention System</h2></td>
    </tr> -->
    <!-- <tr class="fw-bold fs-6 text-gray-800">
      <td align="center" valign="top">
          <table width="98%" border="0" cellspacing="0" cellpadding="0"> -->
            <div class="post d-flex flex-column-fluid" id="kt_post">
              <!--begin::Container-->
              <div id="kt_content_container" class="container-xxl">
                <div class="row my-5">
                  <div class="col">
                  <div class="card">
                    <div class="col-md-12">
                      
                        <!-- <div class="card-header card-header-rose card-header-text">
                        <div class="card-text">
                          <h4 class="card-title">Account Restrictions</h4>
                        </div>
                        </div> -->
                        <div class="card-body ">
                        <div class="container">
                          <div class="row">   
                              <div class="col-sm-6 col-lg-3">
                              <label class="d-flex align-items-center fs-6 fw-semibold mb-2"> Search Merchant</label><br>
                              <div class="txtnew">
                                <s:select name="payId" headerKey="ALL" headerValue="ALL" class="form-select form-select-solid" id="payId" list="merchantList"
                                  listKey="payId" listValue="businessName" autocomplete="off" onchange="displaySwitch(this);" />
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                      </div>
                      </div>
                      </div>
                      </div>
                      </div>
                          
                  <!-- <div class="form-group col-md-4 txtnew col-sm-4 col-xs-6"  style="margin-bottom:32px;"></div> -->
                </div>
              </td>
            </tr>
        </table>
      </td>
    </tr>
  </table>
    <s:hidden name="token" value="%{#session.customToken}"></s:hidden>
    <input type="hidden" id="ruleId" value="">

  <!-- Dynamically generated content of the restriction page for Admin module -->
  
  
  <!--------------------------------------------------------------------------------------------------------------------->
  
  <div id="myModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" style="display: none !important">
        <div class="modal-dialog" style="width: 600px;">

          <!-- Modal content-->
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Block Customer IPv4 Address</h5>
              <button type="button" class="close" id="fraudRuleModalClose" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true"><i class="fa fa-remove"></i></span>
              </button>
              </div>
            <div id="1" class="modal-body">
              <table class="detailbox table98" cellpadding="20">
                    <tr class="fw-bold fs-6 text-gray-800">
                      <td colspan="2" height="30" align="left">
                        <p>
                          Enter the IPv4 address and IPv6 address you wish to block.<br>
                          <br> e.g. 192.168.100.1<br>
                          <br> Once added, all transactions from the IP address will be blocked.
                        </p>
                      </td>
                    </tr>
                    <tr class="fw-bold fs-6 text-gray-800">
                      <td width="7%">
                        <label for="alwaysOnFlag1">
                          <input type="checkbox" name="alwaysOnFlag1" id="alwaysOnFlag1" value="false" onclick="ShowHideDiv1(this)" />
                          <input type="hidden" name="alwaysOnFlag11" id="alwaysOnFlag11" />
                        </label>
                      </td> 
                      
                      <td width="30%">
                        <label for="always" style="font-size:16px;">Always</label>
                      </td>
                    </tr> 
                    <tr class="fw-bold fs-6 text-gray-800">
                      <td width="8%"><label for="">IP Address *</label></td>              
                      <td width="31.8%"><span class="" id="validate_err" ></span>
                        <input id="ipAddress" type="text" placeholder="192.168.100.1" maxlength="15"
                        name="ipAddress" value=""  class="form-control form-control-solid ipAddress mb-4" data-role="tagsinput" />
                      </td>                             
                    </tr>
                                <!-- New Html Code Time Based Fraud Rule-->
                    
              <table class="detailbox table98" cellpadding="20" id="dvTimebased1"  >
                <tr class="fw-bold fs-6 text-gray-800">
                  <td width="8.6%">
                    <label for="IPdateFrom">Start Date *</label>
                  </td>
                  <td width="30%">
                    <div class="startdatepicker date t-ip">
                    <input class="form-control form-control-solid flatpickr-input From" type="text" id="dateActiveFrom" name="dateActiveFrom" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly" />
                    <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                    </div>
                  </td>
                </tr>

                <tr class="fw-bold fs-6 text-gray-800">
                  <td width="8.6%">
                    <label for="IPdateTo">End Date *</label>
                  </td>
                  <td width="30%">
                    <div class="expiredatepicker date t-ip">
                    <input class="form-control form-control-solid flatpickr-input To" type="text" id="dateActiveTo" name="dateActiveTo" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly"/>
                    <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                    </div>
                  </td>
                </tr>

                <tr class="fw-bold fs-6 text-gray-800">
                  <td width="8.6%">
                    <label for="startTime" class="">Start Time *</label>
                  </td>
                  <td width="30%">
                    <div class="t-ip date">
                    <input type="text" class="form-control form-control-solid wid startTime" id="startTimetime" name="startTime" placeholder="HH:MM:SS" autocomplete="off" />
                    <span class="input-group-addon" style="padding: 5px 12px;">
                    <span class="glyphicon glyphicon-time"></span>
                    </span>
                    </div>
                  </td>
                </tr>
                
                <tr class="fw-bold fs-6 text-gray-800">
                  <td width="8.6%"> 
                    <label for="endTime" class="">End Time *</label>
                  </td>
                  <td width="30%"> 
                    <div class="t-ip date">
                    <input type="text" class="form-control form-control-solid wid endTime" id="endTime" name ="endTime" placeholder="HH:MM:SS" autocomplete="off"    />
                    <span class="input-group-addon" style="padding: 5px 12px;">
                    <span class="glyphicon glyphicon-time"></span>
                    </span>
                    </div>
                  </td>
                </tr>
                
                <tr class="fw-bold fs-6 text-gray-800">
                  <td width="8.6%">
                     <label for="weeks">Weeks *</label>
                  </td>
                  <td width="30%">
                    <ul id="repeatDays" class="checklist-week" data-name="repeatDays">
                      <li>
                        <label>
                          <input type="checkbox" name="repeatDays" id="repeatDays0"  value="SUN" class="ip4ClassWeeks">
                          <span>SUN</span>
                        </label>
                      </li>
                      <li>
                        <label>
                          <input type="checkbox" name="repeatDays" id="repeatDays1" value="MON" class="ip4ClassWeeks">
                          <span>MON</span>
                        </label>
                      </li>
                      <li>
                        <label>
                          <input type="checkbox" name="repeatDays" id="repeatDays2" value="TUE" class="ip4ClassWeeks">
                          <span>TUE</span>
                        </label>
                      </li>
                      <li>
                        <label>
                          <input type="checkbox" name="repeatDays" id="repeatDays3" value="WED" class="ip4ClassWeeks">
                          <span>WED</span>
                        </label>
                      </li>
                      <li>
                        <label>
                          <input type="checkbox"  name="repeatDays" id="repeatDays4" value="THU" class="ip4ClassWeeks">
                          <span>THU</span>
                        </label>
                      </li>
                      <li>
                        <label>
                          <input type="checkbox" name="repeatDays" id="repeatDays5" value="FRI" class="ip4ClassWeeks">
                          <span>FRI</span>
                        </label>
                      </li>
                      <li>
                        <label>
                          <input type="checkbox" name="repeatDays" id="repeatDays6" value="SAT" class="ip4ClassWeeks">
                          <span>SAT</span>
                        </label>
                      </li>
                    </ul>
                  </td>
                </tr>
              </table>
                    <tr class="fw-bold fs-6 text-gray-800">
                      <td colspan="2" >
                        <div style="display: flex;">
                        <button type="submit" value="Block" id="blockIp"
                          onclick="ajaxFraudRequest(ipAddressS)" class="btn btn-primary btn-sm btn-block" style="margin-left: 10%;width:21%;height:100%;margin: 1%;">Block</button>
                          <button type="button" class="btn btn-primary btn-sm btn-block" style="width:21%;height:100%;margin-top:1%;" data-dismiss="modal">Close</button>
                        </div></td>
                    </tr>
                    <tr class="fw-bold fs-6 text-gray-800">
                      <td><p>Note: If Alwayss True Flag is checked then Date, Time, Week does not need to be filled</p> but if it is not then all the required fields will have to be filled</p>. </td>
                    </tr>
              </table>
            </div>
          </div>
        </div>
    </div>
            
  <!------------------------------------------------------------------------------------------------------------------------------->
  
  <div id="myModal2" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog modal-lg">

      <!-- Modal content-->
      <div class="modal-content" id="5"> 
          <div class="modal-header">
            <h5 class="modal-title">Block Card Issuer Country </h5>
            <button type="button" class="close" id="fraudRuleModalClose" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true"><i class="fa fa-remove"></i></span>
            </button>
          </div>
          <div class="modal-body" style="overflow:auto;max-height:520px;">
            <ul id="issuerCountry" class="checklist" data-name="country">
            </ul>
          </div>
                <div class="modal-footer" style="display: flex;">
                  
                  <button type="submit" value="Block" id="blockIssuer" onclick="ajaxFraudRequest(issuerCountry)" class="btn btn-primary btn-sm">Block</button>
                  <button type="button" class="btn btn-primary btn-sm " style="margin-left: 5px;" data-dismiss="modal">Close</button>
                </div>
              </div>
        </div>
      </div>
  
  
  <!--------------------------------------------------------------------------------------------------------------------------->
  
    <div id="myModal3" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
        <div class="modal-dialog" style="width: 600px;">

          <!-- Modal content-->
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Block Card Bin Range</h5>
              <button type="button" class="close" id="fraudRuleModalClose" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true"><i class="fa fa-remove"></i></span>
              </button>
              </div>
            <div class="modal-body">
              <form name="myForm">
              <table class="detailbox table98" cellpadding="20">
                <tr class="fw-bold fs-6 text-gray-800">
                  <td colspan="2" height="30" align="left"><p>
                      Enter the first 6 digits of the card number you wish to block;<br>
                      <br> Once added, all transactions within that card range
                      will be blocked.
                    </p></td>
                </tr>
                <tr class="fw-bold fs-6 text-gray-800">
                <td width="8%"><label for="alwaysOnFlag3">
                 <input type="checkbox" name="alwaysOnFlag3" id="alwaysOnFlag3" value="false" onclick="ShowHideDiv3(this)" />
                <input type="hidden" name="alwaysOnFlag33" id="alwaysOnFlag33" /></label>
                </td>
                
                  <td width="30%"><label for="always" style="font-size:16px;">Always</label></td>
                </tr>
                <tr >
                  <td width="6%"><label>Card range *</label></td>
                  <td width="30%">
                  <span class="" id="validate_crd" style="font-size: 10px;"></span>
                  <input id="negativeBin" type="text" minlength="6" maxlength="6" placeholder="6-digit bin range" name="negativeBin"
                  class="form-control form-control-solid mb-4" data-role="tagsinput" />
                  </td>
                </tr>
                <!-- New Html Code Time Based Fraud Rule-->
                
          <table class="detailbox table98" cellpadding="20" id="dvTimebased3"  >
            <tr class="fw-bold fs-6 text-gray-800">
            <td width="8%">
            <label for="CBdateFrom">Start Date *</label></td>
            <td width="30%">
            <div class="startdatepicker date t-ip">
            <input class="form-control form-control-solid flatpickr-input From" type="text" id="dateActiveFrom4" name="dateActiveFrom" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly"/>
            <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
            </div>
            </td>
            </tr>

            <tr class="fw-bold fs-6 text-gray-800">
            <td width="8%">
            <label for="CBdateTo">End Date *</label></td>
            <td width="30%">
            <div class="expiredatepicker date t-ip">
            <input class="form-control form-control-solid flatpickr-input To" type="text" id="dateActiveTo4" name="dateActiveTo" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly"/>
            <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
            </div>
            </td>
            </tr>

            <tr class="fw-bold fs-6 text-gray-800">
            <td width="8%">
            <label for="CBstartTime" class="">Start Time *</label>
            </td>
            <td width="30%">
            <div class="date t-ip startTime">
            <input type="text" class="form-control form-control-solid wid startTime" id="startTime4" name="startTime" placeholder="HH:MM:SS" autocomplete="off"  />
            <span class="input-group-addon" style="padding: 5px 12px;">
            <span class="glyphicon glyphicon-time"></span>
            </span>
            </div>
            </td>
            </tr>
            <tr class="fw-bold fs-6 text-gray-800">
            <td width="8%"> <label for="CBendTime" class="">End Time *</label></td>
            <td width="30%"> <div class=" date t-ip endTime">
            <input type="text" class="form-control form-control-solid wid endTime" id="endTime4" name ="endTime" placeholder="HH:MM:SS" autocomplete="off"   />
            <span class="input-group-addon" style="padding: 5px 12px;">
            <span class="glyphicon glyphicon-time"></span>
            </span>
            </div></td>
            </tr>
            <tr class="fw-bold fs-6 text-gray-800">
            <td width="8%"><label for="weeks">Weeks *</label></td>
            <td width="30%">
            <ul id="repeatDays" class="checklist-week" data-name="repeatDays">
            <li><label><input type="checkbox" name="repeatDays" id="repeatDays14"  value="SUN" class="binRangeClass"><span>SUN</span></label></li>
            <li><label><input type="checkbox" name="repeatDays" id="repeatDays15" value="MON" class="binRangeClass"><span>MON</span></label></li>
            <li><label><input type="checkbox" name="repeatDays" id="repeatDays16" value="TUE" class="binRangeClass"><span>TUE</span></label></li>
            <li><label><input type="checkbox" name="repeatDays" id="repeatDays17" value="WED" class="binRangeClass"><span>WED</span></label></li>
            <li><label><input type="checkbox" name="repeatDays" id="repeatDays18" value="THU" class="binRangeClass"><span>THU</span></label></li>
            <li><label><input type="checkbox" name="repeatDays" id="repeatDays19" value="FRI" class="binRangeClass"><span>FRI</span></label></li>
            <li><label><input type="checkbox" name="repeatDays" id="repeatDays20" value="SAT" class="binRangeClass"><span>SAT</span></label></li>
            </ul>
            </td>
            </tr>
          </table>

                <tr class="fw-bold fs-6 text-gray-800">
                <td colspan="2">
                  <div style="display: flex;">
                  <button type="submit" value="Block" 
                      onclick="ajaxFraudRequest(negativeBinS)" id="blockCardRange" style="margin-left: 10%;width:21%;height:100%;margin: 1%;" class="btn btn-primary btn-sm btn-block">Block</button>
                      <button type="button" class="btn btn-primary btn-sm btn-block" style="width:21%;height:100%;margin-top:1%;" data-dismiss="modal">Close</button> 
                    </div>
                    </td>
                </tr>
                <tr class="fw-bold fs-6 text-gray-800">
                  <td><p>Note: If Alwayss True Flag is checked then Date, Time, Week does not need to be filled</p> but if it is not then all the required fields will have to be filled</p>. </td>
                </tr>
              </table>
              </form>
            </div>
          </div>
        </div>
    </div>
  
  <!---------------------------------------------------------------------------------------------------------------------->
  
  <div id="myModal4" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"> 
    <div class="modal-dialog modal-lg">
      <!-- Modal content-->
    <div id="2" class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Block Card User Country</h5>
          <button type="button" class="close" id="fraudRuleModalClose" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true"><i class="fa fa-remove"></i></span>
          </button>
        </div>
        <div class="modal-body" style="overflow:auto;max-height:520px;">
          <ul id="userCountry" class="checklist" data-name="userCountry">
          </ul>
        </div>
            <div class="modal-footer" style="display: flex;">
              <button type="submit" value="Block" id="blockUserCountry" onclick="ajaxFraudRequest(userCountry)" class="btn btn-primary btn-sm">Block</button>
              <button type="button" class="btn btn-primary btn-sm " style="margin-left: 5px;" data-dismiss="modal">Close</button>
            </div>
          </div>
        </div>
  </div>
  
  <div id="myModal17" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"> 
    <div class="modal-dialog modal-lg">
      <!-- Modal content-->
    <div id="2" class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Block User State</h5>
          <button type="button" class="close" id="fraudRuleModalClose" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true"><i class="fa fa-remove"></i></span>
          </button>
        </div>
        <div class="modal-body" style="overflow:auto;max-height:520px;">
          <div class="row">
          <div class="col">
            <label class="d-flex align-items-center fs-6 fw-bold mb-2">
              <span class="">Country*</span>
            </label>
         
              <select id="countryName" name="countryName" class="form-select form-select-solid actions" onchange="getStateByCountry(this.value, false, 'stateCodeUl', 'stateCode');">
              </select>
            </div>                             
          </div>
          <ul id="stateCodeUl" class="checklist" data-name="stateCode">
          </ul>
        </div>
            <div class="modal-footer" style="display: flex;">
              <button type="submit" value="Block" id="blockUserState" onclick="saveGeoLocationBlockingData(userState)" class="btn btn-primary btn-sm">Block</button>
              <button type="button" class="btn btn-primary btn-sm " style="margin-left: 5px;" data-dismiss="modal">Close</button>
            </div>
          </div>
        </div>
  </div>
  
  <div id="myModal18" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"> 
    <div class="modal-dialog modal-lg">
      <!-- Modal content-->
    <div id="2" class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Block User City</h5>
          <button type="button" class="close" id="fraudRuleModalClose" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true"><i class="fa fa-remove"></i></span>
          </button>
        </div>
        <div class="modal-body" style="overflow:auto;max-height:520px;">
          <div class="row">
            <div class="col">
              <label class="d-flex align-items-center fs-6 fw-bold mb-2">
                <span class="">Country*</span>
              </label>
              <select id="countryNameCityBlocking" name="countryNameCityBlocking" class="form-select form-select-solid actions" onchange="getStateByCountry(this.value, true, 'stateNameCityBlocking','');">
              </select>
            </div>                             
          </div>
          <div class="row">
            <div class="col">
              <label class="d-flex align-items-center fs-6 fw-bold mb-2">
                <span class="">State*</span>
              </label>
              <select id="stateNameCityBlocking" name="stateNameCityBlocking" class="form-select form-select-solid actions" onchange="getCityByState(this.value, 'cityUl','city');">
              </select>
            </div>                             
          </div>
          <ul id="cityUl" class="checklist" data-name="city">
          </ul>
        </div>
            <div class="modal-footer" style="display: flex;">
              <button type="submit" value="Block" id="blockUserCity" onclick="saveGeoLocationBlockingData(userCity)" class="btn btn-primary btn-sm">Block</button>
              <button type="button" class="btn btn-primary btn-sm " style="margin-left: 5px;" data-dismiss="modal">Close</button>
            </div>
          </div>
        </div>
  </div>
  
  <!-------------------------------------------------------------------------------------------------------------------------->
  
    <div id="myModal5" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
      <div class="modal-dialog" style="width: 600px;">
          <!-- Modal content-->
        <div class="modal-content" >
          <div class="modal-header">
            <h5 class="modal-title">Block Customer Email Address</h5>
            <button type="button" class="close" id="fraudRuleModalClose" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true"><i class="fa fa-remove"></i></span>
            </button>
          </div>
          <div id="5" class="modal-body" >
          
          <table class="detailbox table98" cellpadding="20">  
            <tr class="fw-bold fs-6 text-gray-800">
              <td width="8%"><label for="alwaysOnFlag4">
                <input type="checkbox" name="alwaysOnFlag4" id="alwaysOnFlag4" value="false" onclick="ShowHideDiv4(this)" />
                <input type="hidden" name="alwaysOnFlag44" id="alwaysOnFlag44" /></label>
              </td>
              <td width="31.8%"><label for="always" style="font-size:16px;">Always</label></td>
            </tr>
            <tr class="fw-bold fs-6 text-gray-800">
              <td width="7%"><label for="">Email Address *</label></td>             
              <td width="30%">
              <span class="" id="validate_email" style="font-size: 11px;"></span>
              <input id="email" type="email" name="email" placeholder="user@domain.xyz" class="form-control form-control-solid mb-4" data-role="tagsinput" />
              </td>                             
            </tr>
            
            <!-- New Html Code Time Based Fraud Rule-->
                
          <table class="detailbox table98" cellpadding="20" id="dvTimebased4"  >
            <tr class="fw-bold fs-6 text-gray-800">
              <td width="8.5%">
                <label for="emaildateFrom">Start Date *</label></td>
              <td width="30%">
                <div class="startdatepicker date t-ip">
                  <input class="form-control form-control-solid flatpickr-input From" type="text" id="dateActiveFrom3" name="dateActiveFrom" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly"/>
                  <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                </div>
              </td>
            </tr>

            <tr class="fw-bold fs-6 text-gray-800">
              <td width="8.5%">
                <label for="emaildateTo">End Date *</label>
              </td>
              <td width="30%">
                <div class="expiredatepicker date t-ip">
                  <input class="form-control form-control-solid flatpickr-input To" type="text" id="dateActiveTo3" name="dateActiveTo" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly"/>
                  <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                </div>
              </td>
            </tr>

            <tr class="fw-bold fs-6 text-gray-800">
              <td width="8.5%">
                <label for="EmailstartTime" class="">Start Time *</label>
              </td>
              <td width="30%">
                <div class=" date t-ip startTime">
                  <input type="text" class="form-control form-control-solid wid startTime" id="startTime3" name="startTime" placeholder="HH:MM:SS" autocomplete="off" />
                  <span class="input-group-addon" style="padding: 5px 12px;">
                  <span class="glyphicon glyphicon-time"></span>
                  </span>
                </div>
              </td>
            </tr>
            
            <tr class="fw-bold fs-6 text-gray-800">
              <td width="8.5%"> 
                <label for="EmailendTime" class="">End Time *</label>
              </td>
              <td width="30%"> 
                  <div class=" date t-ip endTime">
                  <input type="text" class="form-control form-control-solid wid endTime" id="endTime3" name ="endTime" placeholder="HH:MM:SS" autocomplete="off" />
                  <span class="input-group-addon" style="padding: 5px 12px;">
                  <span class="glyphicon glyphicon-time"></span>
                  </span>
                </div>
              </td>
            </tr>
            
            <tr class="fw-bold fs-6 text-gray-800">
              <td width="8.5%"><label for="weeks">Weeks *</label></td>
              <td width="30%">
              <ul id="repeatDays" class="checklist-week" data-name="repeatDays">
                <li>
                  <label>
                    <input type="checkbox" name="repeatDays" id="repeatDays21"  value="SUN" class="emailAddClass">
                    <span>SUN</span>
                  </label>
                </li>
                <li>
                  <label>
                    <input type="checkbox" name="repeatDays" id="repeatDays22" value="MON" class="emailAddClass">
                    <span>MON</span>
                  </label>
                </li>
                <li>
                  <label>
                    <input type="checkbox" name="repeatDays" id="repeatDays23" value="TUE" class="emailAddClass">
                    <span>TUE</span>
                  </label>
                </li>
                <li>
                  <label>
                    <input type="checkbox" name="repeatDays" id="repeatDays24" value="WED" class="emailAddClass">
                    <span>WED</span>
                  </label>
                </li>
                <li>
                  <label>
                    <input type="checkbox" name="repeatDays" id="repeatDays25" value="THU" class="emailAddClass">
                    <span>THU</span>
                  </label>
                </li>
                <li>
                  <label>
                    <input type="checkbox" name="repeatDays" id="repeatDays26" value="FRI" class="emailAddClass">
                    <span>FRI</span>
                  </label>
                </li>
                <li>
                  <label>
                    <input type="checkbox" name="repeatDays" id="repeatDays27" value="SAT" class="emailAddClass">
                    <span>SAT</span>
                  </label>
                </li>
              </ul>
              </td>
            </tr>
          </table>

              <tr class="fw-bold fs-6 text-gray-800">
                <td colspan="2">
                  <div style="display: flex;">
                  <button type="submit" value="Block" id="blockEmail" onclick="ajaxFraudRequest(emailS)" class="btn btn-primary btn-sm btn-block" 
                  style="margin-left: 10%;width:21%;height:100%;margin: 1%;">Block</button>
                  <button type="button" class="btn btn-primary btn-sm btn-block" style="width:21%;height:100%;margin-top:1%;" data-dismiss="modal">Close</button>
                </div>
              </td>
              </tr>
              <tr class="fw-bold fs-6 text-gray-800">
                <td><p>Note: If Alwayss True Flag is checked then Date, Time, Week does not need to be filled</p> but if it is not then all the required fields will have to be filled</p>. </td>
              </tr>
            </table>
          </div>
        </div>
      </div>
    </div>
  
  <!---------------------------------------------------------------------------------------------------------------------------->
  
  <div id="myModal14" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" style="display: none !important">
        <div class="modal-dialog" style="width: 600px;">

          <!-- Modal content-->
          <div class="modal-content">
            <div class="modal-header">
              <button type="button" class="close" id="fraudRuleModalClose" data-dismiss="modal"><i class="fa fa-remove"></i></button>
              <!-- <h4 class="modal-title">Modal Header</h4> -->
              </div>
            <div id="1" class="modal-body">
              <table class="detailbox table98" cellpadding="20">
              
                    <tr class="fw-bold fs-6 text-gray-800">
                      <th colspan="2" width="16%" height="30" align="left"
                        style="background: linear-gradient(249deg, #ED1B23,#FBB117);color: #ffffff;/* border-top-right-radius: 13px !important; *//* font-size: 16px; */">Block Customer Mack Address</th>
                    </tr>
                    <tr class="fw-bold fs-6 text-gray-800">
                      <td colspan="2" height="30" align="left">
                        <p>
                          Enter the mack address you wish to block.<br>
                          <br> e.g. 00:00:5e:00:53:af<br>
                          <br> Once added, all transactions from the Mack address will be blocked.
                        </p>
                      </td>
                    </tr>
                    <tr class="fw-bold fs-6 text-gray-800">
                      <td width="7%">
                        <label for="alwaysOnFlag1">
                          <input type="checkbox" name="alwaysOnFlag14" id="alwaysOnFlag14" value="false" onclick="ShowHideDiv14(this)" />
                          <input type="hidden" name="alwaysOnFlag14" id="alwaysOnFlag14" />
                        </label>
                      </td> 
                      
                      <td width="30%">
                        <label for="always" style="font-size:16px;">Always</label>
                      </td>
                    </tr> 
                    <tr class="fw-bold fs-6 text-gray-800">
                      <td width="8%"><label for="">Mack Address *</label></td>              
                      <td width="31.8%"><span class="" id="validate_err" ></span>
                        <input id="mackAddress" type="text" placeholder="00:00:5e:00:53:af" maxlength="15"
                        name="mackAddress" value=""  class="form-control form-control-solid ipAddress" data-role="tagsinput" />
                      </td>                             
                    </tr>
                                <!-- New Html Code Time Based Fraud Rule-->
                    
              <table class="detailbox table98" cellpadding="20" id="dvTimebased14"  >
                <tr class="fw-bold fs-6 text-gray-800">
                  <td width="8.6%">
                    <label for="dateActiveFrom">Start Date *</label>
                  </td>
                  <td width="30%">
                    <div class="startdatepicker date t-ip">
                    <input class="form-control form-control-solid flatpickr-input From" type="text" id="dateActiveFrom14" name="dateActiveFrom" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly" />
                    <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                    </div>
                  </td>
                </tr>

                <tr class="fw-bold fs-6 text-gray-800">
                  <td width="8.6%">
                    <label for="dateActiveTo">End Date *</label>
                  </td>
                  <td width="30%">
                    <div class="expiredatepicker date t-ip">
                    <input class="form-control form-control-solid flatpickr-input To" type="text" id="dateActiveTo14" name="dateActiveTo" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly"/>
                    <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                    </div>
                  </td>
                </tr>

                <tr class="fw-bold fs-6 text-gray-800">
                  <td width="8.6%">
                    <label for="startTime" class="">Start Time *</label>
                  </td>
                  <td width="30%">
                    <div class="t-ip date">
                    <input type="text" class="form-control form-control-solid wid startTime" id="startTime14" name="startTime" placeholder="HH:MM:SS" autocomplete="off" />
                    <span class="input-group-addon" style="padding: 5px 12px;">
                    <span class="glyphicon glyphicon-time"></span>
                    </span>
                    </div>
                  </td>
                </tr>
                
                <tr class="fw-bold fs-6 text-gray-800">
                  <td width="8.6%"> 
                    <label for="endTime" class="">End Time *</label>
                  </td>
                  <td width="30%"> 
                    <div class="t-ip date">
                    <input type="text" class="form-control form-control-solid wid endTime" id="endTime14" name ="endTime" placeholder="HH:MM:SS" autocomplete="off"    />
                    <span class="input-group-addon" style="padding: 5px 12px;">
                    <span class="glyphicon glyphicon-time"></span>
                    </span>
                    </div>
                  </td>
                </tr>
                
                <tr class="fw-bold fs-6 text-gray-800">
                  <td width="8.6%">
                     <label for="weeks">Weeks *</label>
                  </td>
                  <td width="30%">
                    <ul id="repeatDays" class="checklist-week" data-name="repeatDays">
                      <li>
                        <label>
                          <input type="checkbox" name="repeatDays" id="repeatDays70"  value="SUN" class="mackClassWeeks">
                          <span>SUN</span>
                        </label>
                      </li>
                      <li>
                        <label>
                          <input type="checkbox" name="repeatDays" id="repeatDays71" value="MON" class="mackClassWeeks">
                          <span>MON</span>
                        </label>
                      </li>
                      <li>
                        <label>
                          <input type="checkbox" name="repeatDays" id="repeatDays72" value="TUE" class="mackClassWeeks">
                          <span>TUE</span>
                        </label>
                      </li>
                      <li>
                        <label>
                          <input type="checkbox" name="repeatDays" id="repeatDays73" value="WED" class="mackClassWeeks">
                          <span>WED</span>
                        </label>
                      </li>
                      <li>
                        <label>
                          <input type="checkbox"  name="repeatDays" id="repeatDays74" value="THU" class="mackClassWeeks">
                          <span>THU</span>
                        </label>
                      </li>
                      <li>
                        <label>
                          <input type="checkbox" name="repeatDays" id="repeatDays75" value="FRI" class="mackClassWeeks">
                          <span>FRI</span>
                        </label>
                      </li>
                      <li>
                        <label>
                          <input type="checkbox" name="repeatDays" id="repeatDays76" value="SAT" class="mackClassWeeks">
                          <span>SAT</span>
                        </label>
                      </li>
                    </ul>
                  </td>
                </tr>
              </table>
                    <tr class="fw-bold fs-6 text-gray-800">
                      <td colspan="2" >
                        <div style="display: flex;">
                        <button type="submit" value="Block" id="blockMack"
                          onclick="ajaxFraudRequest(mackAddressS)" class="btn btn-primary btn-sm btn-block" style="margin-left: 10%;width:21%;height:100%;margin: 1%;">Block</button>
                          <button type="button" class="btn btn-primary btn-sm btn-block" style="width:21%;height:100%;margin-top:1%;" data-dismiss="modal">Close</button>
                        </div></td>
                    </tr>
                    <tr class="fw-bold fs-6 text-gray-800">
                      <td><p>Note: If Alwayss True Flag is checked then Date, Time, Week does not need to be filled</p> but if it is not then all the required fields will have to be filled</p>. </td>
                    </tr>
              </table>
            </div>
          </div>
        </div>
    </div>
    <div id="myModal15" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" style="display: none !important">
        <div class="modal-dialog" style="width: 600px;">

          <!-- Modal content-->
          <div class="modal-content">
            <div class="modal-header">
              <button type="button" class="close" id="fraudRuleModalClose" data-dismiss="modal"><i class="fa fa-remove"></i></button>
              <!-- <h4 class="modal-title">Modal Header</h4> -->
              </div>
            <div id="1" class="modal-body">
              <table class="detailbox table98" cellpadding="20">
              
                    <tr class="fw-bold fs-6 text-gray-800">
                      <th colspan="2" width="16%" height="30" align="left"
                        style="background: linear-gradient(249deg, #ED1B23,#FBB117);color: #ffffff;/* border-top-right-radius: 13px !important; *//* font-size: 16px; */">Repeated MOP Types</th>
                    </tr>
                    <tr class="fw-bold fs-6 text-gray-800">
                      <td colspan="2" height="30" align="left">
                        <p>
                          Enter the repeated MOP types you wish to notify.<br>
                        </p>
                      </td>
                    </tr>
                    <tr class="fw-bold fs-6 text-gray-800">
                      <td width="8%"><label for="">Time Duration*</label></td>              
                      <td width="31.8%"><span class="" id="validate_err" ></span>
                        <select id="timeDuration" name="timeDuration" class="form-select form-select-solid actions" data-role="tagsinput">
                          <option value="Daily">Daily</option>
                          <option value="Weekly">Weekly</option>
                          <option value="Monthly">Monthly</option>
                        </select>
                      </td>                             
                    </tr>
                    <tr class="fw-bold fs-6 text-gray-800">
                      <td width="7%"><label for="">Email Address *</label></td>             
                      <td width="30%">
                      <span class="" id="validate_email" style="font-size: 11px;"></span>
                      <input id="emailToNotify" type="email" name="emailToNotify" required placeholder="user@domain.xyz" class="form-control form-control-solid" data-role="tagsinput" />
                      </td>                             
                    </tr>
                    <tr class="fw-bold fs-6 text-gray-800">
                      <td width="7%"><label for="">Payment type*</label></td>             
                      <td width="30%">
                        <span class="" id="validate_paymentType" style="font-size: 11px;"></span>
                        <s:select name="paymentType" class="form-control form-control-solid" id="paymentType" headerKey="" headerValue="Select Payment Type"
                          list="@com.pay10.commons.util.PaymentTypeUI@values()" listKey="code" listValue="name"
                          autocomplete="off" />
                      </td>                             
                    </tr>
                    <tr class="fw-bold fs-6 text-gray-800">
                      <td width="7%"><label for="">Percentage*</label></td>             
                      <td width="30%">
                        <span class="" id="validate_percentageOfRepeatedMop" style="font-size: 11px;"></span>
                        <input type="number" min="0" step="0.00" name="percentageOfRepeatedMop" class="form-control form-control-solid" id="percentageOfRepeatedMop" />
                      </td>                             
                    </tr>
                    <tr class="fw-bold fs-6 text-gray-800">
                      <td colspan="2">
                        <div style="display: flex;">
                        <button type="submit" value="Notify" id="notifyRepeatedMopType"
                          onclick="ajaxFraudRequest(repeatedMopTypesS)" class="btn btn-primary btn-sm btn-block" style="margin-left: 10%;width:21%;height:100%;margin: 1%;">Notify</button>
                          <button type="button" class="btn btn-primary btn-sm btn-block" style="width:21%;height:100%;margin-top:1%;" data-dismiss="modal">Close</button>
                        </div></td>
                    </tr>
              </table>
            </div>
          </div>
        </div>
    </div>
  <!--<div id="myModal7" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" style="width: 400px;">


      <div class="modal-content"
        style="background-color: transparent; border-radius: 13px; -webkit-box-shadow: 0px 0px 0px 0px; -moz-box-shadow: 0px 0px 0px 0px; box-shadow: 0px 0px 0px 0px; box-shadow: 0px;">
        <div id="6" class="modal-body"
          style="background-color: #ffffff; border-radius: 13px; -webkit-box-shadow: 0px 0px 0px 0px; -moz-box-shadow: 0px 0px 0px 0px; box-shadow: 0px 0px 0px 0px; box-shadow: 0px;">

          <table class="detailbox table98" cellpadding="20">
            <tr class="fw-bold fs-6 text-gray-800">
              <th colspan="2" width="16%" height="30" align="left"
                style="background: linear-gradient(249deg, #ED1B23,#FBB117);color: #ffffff;">No. of Transaction Allowed</th>
            </tr>
            <tr class="fw-bold fs-6 text-gray-800">
              <td width="15%"><label for="">Minutes *</label></td>
              <td width="30%"><input id="minutesTxnLimit" type="text" placeholder="1" maxlength="15"
                name="" value="" class="form-control form-control-solid" onkeypress="return isNumberKey(event)" /></td>
            </tr>
                      <tr class="fw-bold fs-6 text-gray-800">
            <td width="15%"><label for="">No. of Transactions *</label></td>
              <td width="30%">
              <input id="perCardTransactionAllowedvelo" type="text" placeholder="e.g 10" class="form-control form-control-solid" maxlength="8" onkeypress="return isNumberKey(event)" />
            </td>
            </tr>
            <tr class="fw-bold fs-6 text-gray-800">
            
              <td colspan="2"><input type="submit" value="Block"
                onclick="ajaxFraudRequest(numberOfTransaction)" class="btn btn-primary btn-sm btn-block" style="margin-left: 10%;width:21%;height:100%;margin: 1%;" /></td>
            </tr>

          </table>
        </div>
      </div>
    </div>
  </div>-->
 
     <!--------------------------------------------------------------------------------------------------------------------------->
   
      <div id="myModal8" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"> 
        <div class="modal-dialog" style="width: 400px;">

          <!-- Modal content-->
          <div class="modal-content">
              <div class="modal-header">
                <h5 class="modal-title">Limit Transaction Amount </h5>
                <button type="button" class="close" id="fraudRuleModalClose" data-dismiss="modal" aria-label="Close">
                  <span aria-hidden="true"><i class="fa fa-remove"></i></span>
                </button>
              </div>
            <div id="7" class="modal-body"
              >

              <table class="detailbox table98" cellpadding="20">
                <tr class="fw-bold fs-6 text-gray-800">
                  <td width="7%"><label for="fixedAmountFlag">
                     <input type="checkbox" name="fixedAmountFlag" id="fixedAmountFlag" value="false" onclick="hideMinMaxAmount(this)" />
                    <input type="hidden" name="fixedAmountFlagVal" id="fixedAmountFlagVal" /></label>
                  </td>
                  
                    <td width="30%"><label for="fixedAmountFlag" style="font-size:16px;">Merchant Limit</label></td>
                </tr>
                <tr class="fw-bold fs-6 text-gray-800">
                  <td width="25%"><label for ="">Currency *</label></td>
                  <td width="30%"><s:select name="currency" class="form-select form-select-solid actions" id="currency" list="currencyMap"/></td>
                </tr>
                <table id="fixAmountTable" style="display: none;">
                  <tr class="fw-bold fs-6 text-gray-800">
                    <td width="25%"><label for="">Time Duration*</label></td>              
                    <td width="31.8%"><span class="" id="validate_err" ></span>
                      <select id="timeDuration" name="timeDuration" class="form-select form-select-solid actions" data-role="tagsinput">
                        <option value="Daily">Daily</option>
                        <option value="Weekly">Weekly</option>
                        <option value="Monthly">Monthly</option>
                      </select>
                    </td>                             
                  </tr>
                  <tr class="fw-bold fs-6 text-gray-800">
                    <td width="26%"><label for="">Amount<span style="color:red;">*</span></label></td>
                    <td width="30%"><input type="number" id="transactionAmount" placeholder="10" onpaste="return false" name="transactionAmount" class="form-control form-control-solid"  min="" max="9999999999.99" step="0.01" onkeypress="return isNumberKeyAmount(event)" onkeyup="checkTransactionVal()"/></td>
                    <label id="error" style="color:red"></label>
                  </tr>
                  <tr class="fw-bold fs-6 text-gray-800">
                    <td width="26%"><label for="">Transaction Count<span style="color:red;">*</span></label></td>
                    <td width="30%"><input type="number" id="repetationCount" placeholder="5" onpaste="return false" name="repetationCount" class="form-control form-control-solid"  min="" max="50" onkeypress="return isNumberKeyAmount(event)"/></td>
                    <label id="error" style="color:red"></label>
                  </tr>
                </table>
                <table id="minMaxAmountDiv">
                  <tr class="fw-bold fs-6 text-gray-800">
                    <td width="26%"><label for="">Minimum Amount Limit<span style="color:red;">*</span></label></td>
                    <td width="30%"><input type="number" id="minTransactionAmount" placeholder="10" onpaste="return false" name="minTransactionAmount" class="form-control form-control-solid"  min="" max="9999999999.99" step="0.01" onkeypress="return isNumberKeyAmount(event)" onkeyup="checkTransactionVal()"/></td>
                    <label id="error" style="color:red"></label>
                  </tr>
                  <tr class="fw-bold fs-6 text-gray-800">
                    <td width="26%"><label for="">Maximum Amount Limit <span style="color:red;">*</span></label></td>
                    <td width="30%">
                    <span id="amountError" style="color:red; display:none;">Please enter valid Max Amount</span>
                    <input type="number" id="maxTransactionAmount" placeholder="110" name="maxTransactionAmount" onpaste="return false" class="form-control form-control-solid"  min="" max="9999999999.99" step="0.01" onkeypress="return isNumberKeyAmount(event)" onkeyup="checkTransactionVal()" /></td>
                    <label id="error" style="color:red"></label>
                  </tr>
                </table>
                <tr class="fw-bold fs-6 text-gray-800">
                  <td colspan="2">
                    <div style="display: flex;">
                  <button type="submit" value="Block" id="blockTxnAmt" onclick="ajaxFraudRequest(transactionAmount)" class="btn btn-primary btn-sm btn-block" style="margin-left: 10%;width:21%;height:100%;margin: 1%;">Block</button>
                  <button type="button" class="btn btn-primary btn-sm btn-block" style="width:21%;height:100%;margin-top:1%;" data-dismiss="modal">Close</button> 
                </div>
              </td>
                </tr>
              </table>
            </div>
          </div>
        </div>
      </div>
  
  <!------------------------------------------------------------------------------------------------------------------------->
    
    <div id="myModal9" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
      <div class="modal-dialog" style="width: 600px;">
          <!-- Modal content-->
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Block Card No. </h5>
              <button type="button" class="close" id="fraudRuleModalClose" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true"><i class="fa fa-remove"></i></span>
              </button>
            </div>
            <div id="6" class="modal-body">
              <table class="detailbox table98" cellpadding="20">
                
                <tr class="fw-bold fs-6 text-gray-800">
                  <td width="7%" ><label for="">Sample card no.</label></td>
                  <td width="30%"><span style="font: normal 15px Arial">411111</span>
                  <span style="font: normal 10px Arial">******</span>
                  <span style="font: normal 15px Arial">1111</span>
                  </td>
                </tr>
                <tr class="fw-bold fs-6 text-gray-800">
                  <td width="7%"><label for="alwaysOnFlag6">
                     <input type="checkbox" name="alwaysOnFlag6" id="alwaysOnFlag6" value="false" onclick="ShowHideDiv6(this)" />
                    <input type="hidden" name="alwaysOnFlag66" id="alwaysOnFlag66" /></label>
                  </td>
                  
                    <td width="30%"><label for="always" style="font-size:16px;">Always</label></td>
                </tr> 
                <tr class="fw-bold fs-6 text-gray-800">
                  <td width="7%"><label for="">Card Number *</label></td>
                  <td style="display: flex;
                  flex-flow: nowrap;">
                    <input id="cardIntialDigits" placeholder="Initial 6-digits of card" type="text" maxlength="6" minlength="6" class="form-control form-control-solid sample_cn mb-4" onkeypress="return isNumberKey(event)" onblur="makeCardMaskini()" />
                    <input id="cardLastDigits" placeholder="Last 4-digits of card" type="text" maxlength="4" minlength="4" name="negativeCard" class="form-control form-control-solid mb-4" style="width:50%!important; position:relative; left:10px;" onkeypress="return isNumberKey(event)" onblur="makeCardMasklst()" />
                    <input type="hidden" id="negativeCard" name="negativeCard" value="">
                    <span class="" id="validate_crdIn" style="font-size: 10px;"></span>
                    <span class="" id="validate_crdL" style="font-size: 10px;"></span>
                  </td>
                </tr>
                <!-- New Html Code Time Based Fraud Rule-->
                
          <table class="detailbox table98" cellpadding="20" id="dvTimebased6"  >
            <tr class="fw-bold fs-6 text-gray-800">
              <td width="8%">
                <label for="carddateFrom">Start Date *</label>
              </td>
              <td width="30%">
                <div class="date t-ip">
                <input class="form-control form-control-solid flatpickr-input From" type="text" id="dateActiveFrom5" name="dateActiveFrom" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly"/>
                <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                </div>
              </td>
            </tr>

            <tr class="fw-bold fs-6 text-gray-800">
              <td width="8%">
                <label for="carddateTo">End Date *</label>
              </td>
              <td width="30%">
                <div class="date t-ip">
                <input class="form-control form-control-solid flatpickr-input To" type="text" id="dateActiveTo5" name="dateActiveTo" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly"/>
                <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                </div>
              </td>
            </tr>

            <tr class="fw-bold fs-6 text-gray-800">
              <td width="8%">
                <label for="CardstartTime" class="">Start Time *</label>
              </td>
              <td width="30%">
                <div class=" date t-ip">
                <input type="text" class="form-control form-control-solid wid startTime" id="startTime5" name="startTime" placeholder="HH:MM:SS" autocomplete="off" />
                <span class="input-group-addon" style="padding: 5px 12px;">
                <span class="glyphicon glyphicon-time"></span>
                </span>
                </div>
              </td>
            </tr>
            
            <tr class="fw-bold fs-6 text-gray-800">
              <td width="8%"> 
                <label for="CardendTime" class="">End Time *</label>
              </td>
              <td width="30%"> 
                <div class=" date t-ip">
                <input type="text" class="form-control form-control-solid wid endTime" id="endTime5" name ="endTime" placeholder="HH:MM:SS" autocomplete="off" />
                <span class="input-group-addon" style="padding: 5px 12px;">
                <span class="glyphicon glyphicon-time"></span>
                </span>
                </div>
              </td>
            </tr>
            
            <tr class="fw-bold fs-6 text-gray-800">
              <td width="8%"><label for="weeks">Weeks *</label></td>
              <td width="30%">
                <ul id="repeatDays" class="checklist-week" data-name="repeatDays">
                  <li><label><input type="checkbox" name="repeatDays" id="repeatDays35"  value="SUN" class="blockCardClass"><span>SUN</span></label>
                  </li>
                  <li><label><input type="checkbox" name="repeatDays" id="repeatDays36" value="MON" class="blockCardClass"><span>MON</span></label>
                  </li>
                  <li><label><input type="checkbox" name="repeatDays" id="repeatDays37" value="TUE" class="blockCardClass"><span>TUE</span></label>
                  </li>
                  <li><label><input type="checkbox" name="repeatDays" id="repeatDays38" value="WED" class="blockCardClass"><span>WED</span></label>
                  </li>
                  <li><label><input type="checkbox" name="repeatDays" id="repeatDays39" value="THU" class="blockCardClass"><span>THU</span></label>
                  </li>
                  <li><label><input type="checkbox" name="repeatDays" id="repeatDays40" value="FRI" class="blockCardClass"><span>FRI</span></label>
                  </li>
                  <li><label><input type="checkbox" name="repeatDays" id="repeatDays41" value="SAT" class="blockCardClass"><span>SAT</span></label>
                  </li>
                </ul>
              </td>
            </tr>
          </table>
                <tr class="fw-bold fs-6 text-gray-800">
            
                  <td colspan="2">
                    <div style="display: flex;">
                    <button type="submit" value="Block" id="blockCardNo" onclick="ajaxFraudRequest(negativeCardS)" class="btn btn-primary btn-sm btn-block" id="negativeCradBtn" 
                    style="margin-left: 10%;width:21%;height:100%;margin: 1%;">Block</button>
                    <button type="button" class="btn btn-primary btn-sm btn-block" style="width:21%;height:100%;margin-top:1%;" data-dismiss="modal">Close</button> 
                  </div>
                  </td>
                </tr>
                <tr class="fw-bold fs-6 text-gray-800">
                  <td><p>Note: If Alwayss True Flag is checked then Date, Time, Week does not need to be filled</p> but if it is not then all the required fields will have to be filled</p>. </td>
                </tr>

              </table>
            </div>
          </div>
        </div>
      </div>
      
  <!-------------------------------------------------------------------------------------------------------------------------->
  
    <div id="myModal0" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
      <div class="modal-dialog" style="width: 640px;">
          <!-- Modal content-->
          <div class="modal-content">
         
              <div class="modal-header">
                <h5 class="modal-title">Block Limit no. of Transactions Per Card</h5>
                <button type="button" id="fraudRuleModalClose" class="close" data-dismiss="modal" aria-label="Close">
                  <span aria-hidden="true"><i class="fa fa-remove"></i></span>
                </button>
                </div>
            <div id="6" class="modal-body">

              <table class="detailbox table98" cellpadding="20">
                <tr class="fw-bold fs-6 text-gray-800">
                  <td width="7%"><label for="alwaysOnFlag7">
                     <input type="checkbox" name="alwaysOnFlag7" id="alwaysOnFlag7" value="false" onclick="ShowHideDiv7(this)" />
                    <input type="hidden" name="alwaysOnFlag77" id="alwaysOnFlag77" /></label>
                  </td>
                  <td width="41.8%"><label for="always" style="font-size:16px;">Always</label></td>
                </tr>
                <tr class="fw-bold fs-6 text-gray-800">
                  <td width="7%"><label for="Card Number">Card Number *</label>
                  </td>
                  <td style="display: flex;
                  flex-flow: nowrap;
                  width: 93% !important;">              
                    <input id="precardIntialDigits" name="precardIntialDigits" placeholder="Initial 6-digits of card" type="text" maxlength="6" minlength="6" class="form-control form-control-solid sample_cn mb-4" onkeypress="return isNumberKey(event)" 
                    onblur="iniCardMask()"/>
                    
                    <input id="precardLastDigits" name="precardLastDigits" placeholder="Last 4-digits of card" type="text" maxlength="4" name="prenegativeCard" class="form-control form-control-solid mb-4" style="width:50%!important; position:relative; left:10px;" onkeypress="return isNumberKey(event)" onblur="lastCardMask()" />
                    
                    <input type="hidden" id="prenegativeCard" name="prenegativeCard" value="">
                    <span class="" id="validate_crdInpre" style="font-size: 10px;"></span>
                    <span class="" id="validate_crdLpre" style="font-size: 10px;"></span>
                  </td>
                </tr>
                <tr class="fw-bold fs-6 text-gray-800">
                  <td width="8%"><label for="">Time Duration*</label></td>              
                  <td width="31.8%"><span class="" id="validate_err" ></span>
                    <select id="timeDuration" name="timeDuration" class="form-select form-select-solid actions mb-4" data-role="tagsinput">
                      <option value="Daily">Daily</option>
                      <option value="Weekly">Weekly</option>
                      <option value="Monthly">Monthly</option>
                    </select>
                  </td>                             
                </tr>
                <tr class="fw-bold fs-6 text-gray-800">
                  <td width="7%"><label for="No. of Transactions">No. of Transactions *</label></td>
                  <td width="30%">
                    <input id="perCardTransactionAllowed" type="text" placeholder="e.g 10" class="form-control form-control-solid twidth mb-4"  maxlength="8" onkeypress="return isNumberKey(event)"/> 
                  </td>
                </tr>
                            <!-- New Html Code Time Based Fraud Rule-->
                
              <table class="detailbox table98" cellpadding="20" id="dvTimebased7"  >
                <tr class="fw-bold fs-6 text-gray-800">
                <td width="9%">
                  <label for="carddateFrom">Start Date *</label>
                </td>
                <td width="30%">
                  <div class="startdatepicker date t-ip">
                  <input class="form-control form-control-solid flatpickr-input From" type="text" id="dateActiveFrom7" name="dateActiveFrom" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly"/>
                  <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                  </div>
                </td>
                </tr>

                <tr class="fw-bold fs-6 text-gray-800">
                  <td width="9%">
                  <label for="carddateTo">End Date *</label></td>
                  <td width="30%">
                    <div class="expiredatepicker date t-ip">
                    <input class="form-control form-control-solid flatpickr-input To" type="text" id="dateActiveTo7" name="dateActiveTo" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly"/>
                    <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                    </div>
                  </td>
                </tr>

                <tr class="fw-bold fs-6 text-gray-800">
                  <td width="9%">
                    <label for="CardstartTime" class="">Start Time *</label>
                  </td>
                  <td width="30%">
                    <div class=" t-ip startTime">
                    <input type="text" class="form-control form-control-solid wid startTime" id="startTime7" name="startTime" placeholder="HH:MM:SS" autocomplete="off" />
                    <span class="input-group-addon" style="padding: 5px 12px;">
                    <span class="glyphicon glyphicon-time"></span>
                    </span>
                    </div>
                  </td>
                </tr>
                
                <tr class="fw-bold fs-6 text-gray-800">
                  <td width="9%"> 
                    <label for="CardendTime" class="">End Time *</label>
                  </td>
                  <td width="30%"> <div class=" t-ip endTime">
                    <input type="text" class="form-control form-control-solid wid endTime" id="endTime7" name ="endTime" placeholder="HH:MM:SS" autocomplete="off" />
                    <span class="input-group-addon" style="padding: 5px 12px;">
                    <span class="glyphicon glyphicon-time"></span>
                    </span>
                    </div>
                  </td>
                </tr>
                
                <tr class="fw-bold fs-6 text-gray-800">
                  <td width="9%"><label for="weeks">Weeks *</label></td>
                  <td width="30%">
                    <ul id="repeatDays" class="checklist-week" data-name="repeatDays">
                      <li>
                        <label>
                          <input type="checkbox" name="repeatDays" id="repeatDays42"  value="SUN" class="limitNoClass">
                          <span>SUN</span>
                        </label>
                      </li>
                      <li>
                        <label>
                        <input type="checkbox" name="repeatDays" id="repeatDays43" value="MON" class="limitNoClass">
                        <span>MON</span>
                        </label>
                      </li>
                      <li>
                        <label>
                        <input type="checkbox" name="repeatDays" id="repeatDays44" value="TUE" class="limitNoClass">
                        <span>TUE</span>
                        </label>
                      </li>
                      <li>
                        <label>
                        <input type="checkbox" name="repeatDays" id="repeatDays45" value="WED" class="limitNoClass">
                        <span>WED</span>
                        </label>
                      </li>
                      <li>
                        <label>
                        <input type="checkbox" name="repeatDays" id="repeatDays46" value="THU" class="limitNoClass">
                        <span>THU</span>
                        </label>
                      </li>
                      <li>
                        <label>
                        <input type="checkbox" name="repeatDays"  id="repeatDays47" value="FRI" class="limitNoClass">
                        <span>FRI</span>
                        </label>
                      </li>
                      <li>
                        <label>
                        <input type="checkbox" name="repeatDays" id="repeatDays48" value="SAT" class="limitNoClass">
                        <span>SAT</span>
                        </label>
                      </li>
                    </ul>
                  </td>
                </tr>
              </table>          
                <tr class="fw-bold fs-6 text-gray-800">
                <td colspan="2">
                  <div style="display: flex;">
                <button type="submit" value="Block" id="percardTransactionBtn" onclick="ajaxFraudRequest(perCardTransactionAllowedS)" class="btn btn-primary btn-sm btn-block" style="margin-left:40%;width:21%;height:100%;margin-top:1%;">Block</button>
                <button type="button" class="btn btn-primary btn-sm btn-block" style="width:21%;height:100%;margin-top:1%;" data-dismiss="modal">Close</button> 
              </div>  
              </td>
                </tr>
                <tr class="fw-bold fs-6 text-gray-800">
                  <td><p>Note: If Alwayss True Flag is checked then Date, Time, Week does not need to be filled</p> but if it is not then all the required fields will have to be filled</p>. </td>
                </tr>
              </table>
            </div>
          </div>
      </div>
    </div>

    <div id="myModal12" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
      <div class="modal-dialog">
          <!-- Modal content-->
        <div class="modal-content" >
          <div class="modal-header">
            <h5 class="modal-title">Block Customer Phone Number</h5>
            <button type="button" id="fraudRuleModalClose" class="close" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true"><i class="fa fa-remove"></i></span>
            </button>
            </div>
       
          <div id="5" class="modal-body" >
          
          <table class="detailbox table98" cellpadding="20">
            <tr class="fw-bold fs-6 text-gray-800">
              <td width="8%"><label for="alwaysOnFlag9">
                <input type="checkbox" name="alwaysOnFlag12" id="alwaysOnFlag12" value="false" onclick="ShowHideDiv12(this)" />
                <input type="hidden" name="alwaysOnFlag1212" id="alwaysOnFlag1212" /></label>
              </td>
              <td width="31.8%"><label for="always" style="font-size:16px;">Always</label></td>
            </tr>
            <tr class="fw-bold fs-6 text-gray-800">
              <td width="9%"><label for="">Phone Number <span style="color:red;">*</span></label></td>              
              <td width="30%">
              <span class="" id="validate_phone" style="font-size: 11px;"></span>
              <input id="phone" type="text" name="phone" placeholder="Enter Phone Number" minlength="8" maxlength="13" class="form-control form-control-solid mb-4" data-role="tagsinput" />
              </td>                             
            </tr>
            
            <!-- New Html Code Time Based Fraud Rule-->
                
          <table class="detailbox table98" cellpadding="20" id="dvTimebased12"  >
            <tr class="fw-bold fs-6 text-gray-800">
              <td width="9.5%">
                <label for="phonedateFrom">Start Date<span style="color:red;">*</span></label></td>
              <td width="30%">
                <div class="startdatepicker date t-ip">
                  <input class="form-control form-control-solid flatpickr-input From" type="text" id="dateActiveFrom12" name="dateActiveFrom" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly"/>
                  <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                </div>
              </td>
            </tr>
  
            <tr class="fw-bold fs-6 text-gray-800">
              <td width="9.5%">
                <label for="phonedateTo">End Date<span style="color:red;">*</span></label>
              </td>
              <td width="30%">
                <div class="expiredatepicker date t-ip">
                  <input class="form-control form-control-solid flatpickr-input To" type="text" id="dateActiveTo12" name="dateActiveTo" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly"/>
                  <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                </div>
              </td>
            </tr>
  
            <tr class="fw-bold fs-6 text-gray-800">
              <td width="9.5%">
                <label for="phonestartTime" class="">Start Time<span style="color:red;">*</span></label>
              </td>
              <td width="30%">
                <div class=" t-ip startTime">
                  <input type="text" class="form-control form-control-solid wid startTime" id="startTime12" name="startTime" placeholder="HH:MM:SS" autocomplete="off" />
                  <span class="input-group-addon" style="padding: 5px 12px;">
                  <span class="glyphicon glyphicon-time"></span>
                  </span>
                </div>
              </td>
            </tr>
            
            <tr class="fw-bold fs-6 text-gray-800">
              <td width="9.5%"> 
                <label for="phoneendTime" class="">End Time<span style="color:red;">*</span></label>
              </td>
              <td width="30%"> 
                <div class="  t-ip endTime">
                  <input type="text" class="form-control form-control-solid wid endTime" id="endTime12" name ="endTime" placeholder="HH:MM:SS" autocomplete="off" />
                  <span class="input-group-addon" style="padding: 5px 12px;">
                  <span class="glyphicon glyphicon-time"></span>
                  </span>
                </div>
              </td>
            </tr>
            
            <tr class="fw-bold fs-6 text-gray-800">
              <td width="7.35%"><label for="weeks">Weeks <span style="color:red;">*</span></label></td>
              <td width="30%">
              <ul id="repeatDays" class="checklist-week" data-name="repeatDays">
                <li>
                  <label>
                    <input type="checkbox" name="repeatDays" id="repeatDays49"  value="SUN" class="phoneAddClass">
                    <span>SUN</span>
                  </label>
                </li>
                <li>
                  <label>
                    <input type="checkbox" name="repeatDays" id="repeatDays50" value="MON" class="phoneAddClass">
                    <span>MON</span>
                  </label>
                </li>
                <li>
                  <label>
                    <input type="checkbox" name="repeatDays" id="repeatDays51" value="TUE" class="phoneAddClass">
                    <span>TUE</span>
                  </label>
                </li>
                <li>
                  <label>
                    <input type="checkbox" name="repeatDays" id="repeatDays52" value="WED" class="phoneAddClass">
                    <span>WED</span>
                  </label>
                </li>
                <li>
                  <label>
                    <input type="checkbox" name="repeatDays" id="repeatDays53" value="THU" class="phoneAddClass">
                    <span>THU</span>
                  </label>
                </li>
                <li>
                  <label>
                    <input type="checkbox" name="repeatDays" id="repeatDays54" value="FRI" class="phoneAddClass">
                    <span>FRI</span>
                  </label>
                </li>
                <li>
                  <label>
                    <input type="checkbox" name="repeatDays"  id="repeatDays55" value="SAT" class="phoneAddClass">
                    <span>SAT</span>
                  </label>
                </li>
              </ul>
              </td>
            </tr>
          </table>
  
              <tr class="fw-bold fs-6 text-gray-800">
                <td colspan="2">
                  <div style="display: flex;">
                  <button type="submit" value="Block" id="blockPhone" onclick="ajaxFraudRequest(phoneS)" class="btn btn-primary btn-sm btn-block" 
                  style="margin-left: 10%;width:21%;height:100%;margin: 1%;">Block</button>
                  <button type="button" class="btn btn-primary btn-sm btn-block" style="width:21%;height:100%;margin-top:1%;" data-dismiss="modal">Close</button>
                </div>
              </td>
              </tr>
              <tr class="fw-bold fs-6 text-gray-800">
                <td><p>Note: If Alwayss True Flag is checked then Date, Time, Week does not need to be filled</p> but if it is not then all the required fields will have to be filled</p>. </td>
              </tr>
            </table>
          </div>
        </div>
      </div>
    </div>
      
        
    <!-------------------------------------------------------------------------------------------------------------------------->
    
    <!---->
    <div id="myModal11" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">  
      <div class="modal-dialog">
  
        <!-- Modal content-->
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Transaction Monitoring</h5>
            <button type="button" id="fraudRuleModalClose" class="close" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true"><i class="fa fa-remove"></i></span>
            </button>
            </div>
          <div id="7" class="modal-body">
            <table class="detailbox table98" cellpadding="20">
            <tr class="fw-bold fs-6 text-gray-800">
              <td width="15"><label for="">Type<span
                  style="color: red;">*</span></label></td>
              <td><div class="col-sm-12 col-lg-10">

                  <div class="OtherList"
                    style="margin-left: 10px; margin-top: 5px;">
                    <div>
                      <div id="wwgrp_userType" class="wwgrp">
                        <div id="wwctrl_userType" class="wwctrl">
                            <input type="checkbox" id="notifyVelocityId" name="monitoringType" value="Notify"><label
                              for="Notify">Notify</label><br> 
                              <input type="checkbox" id="blockVelocityId" name="monitoringType" value="Block"><label
                              for="block">Block</label>
                        </div>
                      </div>

                    </div>
                  </div>
                </div></td>
            </tr>
            <tr class="fw-bold fs-6 text-gray-800">
                <td width="15"><label for ="">Currency <span style="color:red;">*</span></label></td>
                <td width="30%"><s:select name="currency" class="form-select form-select-solid actions" id="currencyVelocity" list="currencyMap"/></td>
              </tr>
              <tr class="fw-bold fs-6 text-gray-800">
                <td width="15"><label for ="">Status <span style="color:red;">*</span></label></td>
                <td width="30%">
                <select id="statusVelocity" name="statusVelocity" class="form-select form-select-solid actions mb-2">
                    <option value="ALL">ALL</option>
                    <option value="Success">Success</option>
                    <option value="Pending">Pending</option>
                    <option value="Failed">Failed</option>
                  </select>
                </td>
              </tr>
              <tr class="fw-bold fs-6 text-gray-800">
                <td width="8%"><label for="">Time Duration*</label></td>              
                <td width="31.8%"><span class="" id="validate_err" ></span>
                  <select id="timeDurationVelocity" name="timeDuration" class="form-select form-select-solid actions mb-2">
                    <option value="Daily">Daily</option>
                    <option value="Weekly">Weekly</option>
                    <option value="Monthly">Monthly</option>
                  </select>
                </td>                             
              </tr>
              <tr class="fw-bold fs-6 text-gray-800">
                <td width="15"><label for ="">Rules<span style="color:red;">*</span></label></td>
                <td><div class="col-sm-12 col-lg-10">
                  
                  <div class="OtherList" style="margin-left:10px; margin-top:5px;">
                    <div>
                      <div id="wwgrp_userType" class="wwgrp">
                     <div id="wwctrl_userType" class="wwctrl">
                        <form id="userIdentifier">
                          <input type="radio" name="userIdentifier" id="emailBlock"  value="Email"><label for="emailoption">Email</label><br>
                          <input type="radio" name="userIdentifier" id="phoneBlock" value="Phone"><label for="phoneoption">Phone</label><br>
                          <input type="radio" name="userIdentifier" id="cardBlock" value="Card"><label for="cardoption">Card</label><br>
                          <input type="radio" name="userIdentifier" id="ipAddrBlock" value="ipAddress"><label for="cardoption">IP Address</label><br>
                          <input type="radio" name="userIdentifier" id="vpaBlock" value="VPA"><label for="vpaoption">VPA</label><br>
                        </form>
              </div> </div>
  
                    </div>
                  </div>
                </div></td>
              </tr>
              <tr class="fw-bold fs-6 text-gray-800">
                <td width="15%"><label for="">Transaction Count<span style="color:red;">*</span></label></td>
                <td width="30%"><input type="number" id="repetationCountVelocity" placeholder="5" onpaste="return false" name="repetationCount" class="form-control form-control-solid mb-2"  min="" onkeypress="return isNumberKeyAmount(event)"/></td>
                <label id="error" style="color:red"></label>
              </tr>
              <tr class="fw-bold fs-6 text-gray-800">
                <td width="15%"><label for="">Total Amount</label></td>
                <td width="30%"><input type="number" id="txnAmountVelocity" placeholder="10" name="maxTransactionAmount" class="form-control form-control-solid mb-2" min="" max="9999999999.99" step="0.01" default="0" onkeypress="return isNumberKeyAmount(event)"/></td>
                <label id="error2" style="color:red"></label>
              </tr>
              <tr class="fw-bold fs-6 text-gray-800">
                <td width="7%"><label for="">Email Address To Notify *</label></td>             
                <td width="30%">
                <span class="" id="validate_email" style="font-size: 11px;"></span>
                <input id="emailToNotifyVelocity" type="email" name="emailToNotify" required placeholder="user@domain.xyz" class="form-control form-control-solid mb-2" data-role="tagsinput" />
                </td>                             
              </tr>
              <tr class="fw-bold fs-6 text-gray-800">
                <td colspan="2">
                  <div style="display: flex;">
                <button type="submit" value="Block" id="blockTxnAmtVel"  onclick="ajaxFraudRequest(transactionAmountVelocity)" class="btn btn-primary btn-sm" style="margin-left: 35%;margin-top: 1%; margin-right: 2%">Submit</button>
                <button type="button" class="btn btn-primary btn-sm" style="margin-top:1%;" data-dismiss="modal">Close</button> 
              </div>
            </td>
              </tr>
            </table>
          </div>
        </div>
      </div>
    </div>
    
    <div id="myModal19" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">  
      <div class="modal-dialog">
  
        <!-- Modal content-->
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Repeted MOP Type Blocking</h5>
            <button type="button" id="fraudRuleModalClose" class="close" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true"><i class="fa fa-remove"></i></span>
            </button>
            </div>
          <div id="7" class="modal-body" style="padding-top: 0px;">
            <table class="detailbox table98" cellpadding="20">
           
            <tr class="fw-bold fs-6 text-gray-800">
              <td width="15"><label for="">Type<span
                  style="color: red;">*</span></label></td>
              <td><div class="col-sm-12 col-lg-10">

                  <div class="OtherList"
                    style="margin-left: 10px; margin-top: 5px;">
                    <div>
                      <div id="wwgrp_userType" class="wwgrp">
                        <div id="wwctrl_userType" class="wwctrl">
                            <input type="checkbox" id="notifyMonitoringTypeForRepeatedMop" name="monitoringTypeForRepeatedMop" value="Notify"><label
                              for="Notify">Notify</label><br> 
                              <input type="checkbox" id="blockMonitoringTypeForRepeatedMop" name="monitoringTypeForRepeatedMop" value="Block"><label
                              for="block">Block</label>
                        </div>
                      </div>

                    </div>
                  </div>
                </div></td>
            </tr>
              <tr class="fw-bold fs-6 text-gray-800">
                <td width="8%"><label for="">Time Duration*</label></td>              
                <td width="31.8%"><span class="" id="validate_err" ></span>
                  <select id="timeDurationRepeatedMop" name="timeDuration" class="form-select form-select-solid actions mb-2">
                    <option value="Daily">Daily</option>
                    <option value="Weekly">Weekly</option>
                    <option value="Monthly">Monthly</option>
                  </select>
                </td>                             
              </tr>
              <tr class="fw-bold fs-6 text-gray-800">
                <td width="15%"><label for="">Transaction Count<span style="color:red;">*</span></label></td>
                <td width="30%"><input type="number" id="repetationCountRepeatedMop" placeholder="5" onpaste="return false" name="repetationCount" class="form-control form-control-solid mb-2"  min="" onkeypress="return isNumberKeyAmount(event)"/></td>
                <label id="error" style="color:red"></label>
              </tr>
              <tr class="fw-bold fs-6 text-gray-800">
                <td width="7%"><label for="">Email Address To Notify *</label></td>             
                <td width="30%">
                <span class="" id="validate_email" style="font-size: 11px;"></span>
                <input id="emailToNotifyRepeatedMop" type="email" name="emailToNotify" required placeholder="user@domain.xyz" class="form-control form-control-solid mb-2" data-role="tagsinput" />
                </td>                             
              </tr>
              <tr class="fw-bold fs-6 text-gray-800">
                <td colspan="2">
                  <div style="display: flex;">
                <button type="submit" value="Block" id="blockRepeatedMop"  onclick="saveNotifyFTxn(repeatedMopTypeForSameDetails)" class="btn btn-primary btn-sm btn-block" style="margin-left: 10%;width:21%;height:100%;margin: 1%;">Submit</button>
                <button type="button" class="btn btn-primary btn-sm btn-block" style="width:21%;height:100%;margin-top:1%;" data-dismiss="modal">Close</button> 
              </div>
            </td>
              </tr>
            </table>
          </div>
        </div>
      </div>
    </div>

    
    <div class="">
      <div class=" ">
        <!-- <div class="card-header card-header-rose card-header-text"> -->
        <!-- <div class="card-text"> -->
          <!-- <h4 class="card-title">Admin-- Fraud Prevention System</h4> -->
        <!-- </div>
        </div> -->
        <div class="">
        <div class="container">
        
    <div class="accordion md-accordion" id="accordionEx1" role="tablist" aria-multiselectable="true">
          <div class="row my-5">
            <div class="col">  
      <div class="card card_graph"   id="cardcb1" ><!--onclick="monthlycardupdateTabState(this)"-->
        <div class="card-header " role="tab" id="headingTwo1">
          <a class="collapsed" >
            <div class="card-header card-header-icon card-header-danger" id="cardHeaderPosition">
            <div class="card-icon" id="cardIcon">
              <i class="fa fa-ban" id="materialIcons"></i>
              </div> 
             <h4 class="card-title">Block IP Addresses
             <i   data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo1"
              aria-expanded="false" aria-controls="collapseTwo1" class="fa fa-angle-down rotate-icon"></i>
              <label class="switch" style="float: right; display: none">
                <input type="checkbox" id="onIpBlocking" name="onIpBlocking" onclick="updateConfig(this);">
                <span class="slider round"></span>
              </label>
              </h4>
              </div>
            <!-- <h5 class="mb-0">
            <strong>Monthly Transaction</strong> <i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo1"
            aria-expanded="false" aria-controls="collapseTwo1" class="fa fa-angle-down rotate-icon"></i>
            </h5> -->
          </a>
          </div>
          
          <div id="collapseTwo1" class="collapse active " role="tabpanel" >
<div class="card-body">
    <div class="row">
    <div class="col-md-12 col-xs-12">
      
        <div class="cards">
          <div class="card-header card-header-icon card-header-info">
          <!-- <div class="card-icon" id="cardIcon">
            <i class="material-icons" id="materialIcons">multiline_chart</i>
          </div> -->
          <!-- <h4 class="card-title">Monthly Transaction
           
          </h4><br>
          <br> -->
          <div class="col-md-12" style="align-items: flex-end;">
            <div class="bkn">
            
              <div class="adduT row">
                <table>
                  <tr class="fw-bold fs-6 text-gray-800">
                  <!-- <td id="ipAddListBodyMsg" style="display:block">No blocked IP addresses</td>
                  <td style="width:6%"><div class="adduT row" style="text-align: right;padding: 14px 0 0 0;"> -->
                    <div class="row">
                    <div class="col-md-2">
                  <input type="submit" name="remittSubmit" disabled="disabled" value="Add Rule" onclick="tagsAddRule()"  id="popupButton" class="btn btn-primary btn-md" 
                  data-toggle="modal" data-target="#myModal"/>
                </div>
                <div class="col-md-3">
                  <input   type='search' class="form-control form-control-solid form-control form-control-solid-solid " id="search_ipAddListBody" onkeypress="if(event.keyCode === 32)return false;" placeholder="Search IP Address">
                </div>
                <div class="col-md-2">
                  <input class='btn btn-primary btn-md' id="searchResult"   type='submit' value='Search' onclick="searchData('ipAddListBody','BLOCK_IP_ADDRESS')">
                
                </div>
                <div class="col-md-2">
                  <input class='btn btn-primary btn-md bulkDeleteBtn disabled' type='submit' name="deleteRules" value='Bulk Delete' onclick="bulkDeleteFraudRule('ipAddListBody','BLOCK_IP_ADDRESS')">
                </div>
                
                
                  </div></td>
                </div>
                  </tr>
                </table>
              
              </div>
              
              <div>
                <!-- <p class="accordion_head"><b>Blocked IP Addresses List</b><span class="plusminus">+</span></p>  -->
                <div class="table-responsive" >
                  
                  <table id="ipAddListBody" class="table table-striped table-row-bordered gy-5 gs-7">
                    <thead>
                      <tr class="fw-bold fs-6 text-gray-800">
                        <th>Merchant</th>
                        <th>IP Address</th>
                        <!-- <th>Name</th> -->
                        <th>Start Date</th>
                        <th>End Date</th>
                        <th>Start Time</th>
                        <th>End Time</th>
                        <th>Weeks</th>
                        <th>Edit</th>
                        <th>Delete</th>
                        <th> <input class="form-check-input" type="checkbox" id="select_all" style="margin-right:10px;" value="" onclick="selectall()">
                          <span class="form-check-sign">
                            <span class="check"></span>Action/Select All 
                        
                          </span></th>
                        
                      </tr>
                    </thead>
                  </table>
                </div>
                <div class="card">
                  <div class="row it">
                  <div class="" id="one">
                    <div class="card-body">
                    <%--   <div class="row my-3 align-items-center">
                        <div id="wwgrp_merchantPayId" class="wwgrp">
                            <div id="wwctrl_merchantPayId" class="wwctrl">
                                <select name="payId" id="merchantPayId" class="input-control" style="display: none;" autocomplete="off">
                                    <option value="1079120806142541">testMerchant13</option>
                                </select>
                              </div> 
                            </div>
                      </div> --%>
                      <div id="myid" style="display: none;">
                      <div id="message" colspan="3" align="left" style="display: none;"></div>
                      <div id="message" colspan="3" align="left"> </div>
                    </div>
                    <div class="card-header-rose card-header-text">
                    <div class="card-text">
                        <h4 class="card-title">Add Bulk Rules for IP Address</h4>
                    </div>
                  </div>
                      <div class="row">
                        <div class="col-12">
                          <span class="fw-semibold text-gray-600 fs-6 mb-8 d-block">
                            File should be in csv format<br>
                            File size must not exceed 2MB File name length should be 5-50
                            characters.<br>
                            File should contain 1-10000 records<br>
                            File name can contain alphabets, digits, round brackets,
                            hyphen,underscore, dot &amp; space
                          </span>
                        </div>
                      </div>
                      <form enctype="multipart/form-data" method="post" id="ipBulkUpload" >
                      <div class="row mb-4 align-items-start" style="column-gap: 20px;">
                        <div class="col-lg-3 col-md-5 col-sm-12 my-2 file-group p-0 ">
                          <div class="file-input">
                            <div id="wwgrp_file-input" class="wwgrp">
                            <div id="wwctrl_file-input" class="wwctrl">
                                
                            <input type="file" name="fileName" value="" accept=".csv" id="file-input" class="file-input__input" onchange="readURL(this);" ></div> </div>
                            <!-- <span id="fileCSVErr"></span> -->

                            <label class="file-input__label" for="file-input">
                              <img src="../assets/media/images/folder-svg.svg" alt="">
                              <span class="m-0 blackspan" id="filename"></span>
                              <span>Browse</span>
                            </label>
                          </div>
                          <div class="docErr">Please upload valid file</div>

                        </div>
                        <div class="col-lg-3 col-md-5 col-sm-12 p-0" onclick="download()">
                          <a type="button" id="csvdownload" class="btn-hover-rise my-2 download-btn ">
                            <span class="bluespan">CSV</span>
                            <span class="blackspan">Sample csv file</span>
                          </a>
                        </div>
                        <div class="col-lg-3 col-md-5 col-sm-12 mt-3">
                          <input type="submit" class="btn btn-primary btn-xs"  id="btnBulkUpload" name="btnBulkUpload" disabled onclick="bulkUpload('BLOCK_IP_ADDRESS','ipBulkUpload')"> 
                        </div>
                        <p><p>Note: If Alwayss True Flag is checked then Date, Time, Week does not need to be filled</p> but if it is not then all the required fields will have to be filled</p>.</p> 

                      </div>
                      </form>
                    </div>
                
                  <div id="uploader">
                  <div class="row uploadDoc">
                    <div class="col-sm-4 d-none">
                      <table id="example" class="csv" style="display: none;">
                        <thead>
                          <tr class="fw-bold fs-6 text-gray-800">
                            <th>Merchant</th>
                            <td>IP Address</td>
                            <!-- <th>Name</th> -->
                            <td>Start Date</td>
                            <td>End Date</td>
                            <td>Start Time</td>
                            <td>End Time</td>
                            <td>Week Days</td>
                            <td>Always On</td>
                          </tr> 
                        </thead>
                        <tbody>
                          <tr class="fw-bold fs-6 text-gray-800">
                            <td>1022291114152030</td>
                          <td>192.44.44.88</td>
                          <td></td>
                          <td></td>
                          <td></td>
                          <td></td>
                          <td></td>
                          <td>TRUE</td>
                          </tr>
                          <tr class="fw-bold fs-6 text-gray-800">
                            <td>1022291114152030</td>
                          <td>192.44.44.88</td>
                          <td>13/09/2020</td>
                          <td>25/09/2020</td>
                          <td>11:51:41</td>
                          <td>11:51:42</td>
                          <td>MON TUE WED </td>
                          <td>FALSE</td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                    <div class="col-sm-6">
                    <!--error-->
                    <div class="fileUpload btn btn-orange">
                      <!-- <form enctype="multipart/form-data" method="post" id="ipBulkUpload" >
                        <div style="display:flex">
                      <input type="file" name="fileName" class="upload up form-control form-control-solid form-control form-control-solid-solid" id="up" onchange="readURL(this);" />
                      <input type="submit" class="btn btn-primary btn-xs"  id="btnBulkUpload" name="btnBulkUpload" disabled onclick="bulkUpload('BLOCK_IP_ADDRESS','ipBulkUpload')"> 
                        </div>
                        <div class="docErr">Please upload valid file</div>

                    </form> -->
                    </div><!-- btn-orange -->
                    </div><!-- col-3 -->
                   
                   <!-- <p><p>Note: If Alwayss True Flag is checked then Date, Time, Week does not need to be filled</p> but if it is not then all the required fields will have to be filled</p>.</p>  -->
                  </div><!--row-->
                  </div><!--uploader-->
                  <div class="text-center">
                  <!-- <a class="btn btn-new"><i class="fa fa-plus"></i> Add new</a> -->
                  <!-- <button type="button"  id="btnBulkUpload" onclick="bulkUpload()"><a class="btn btn-next"><i class="fa fa-upload"></i> Bulk Upload</a></button> -->
                  </div>
                  </div><!--one-->
                  </div><!-- row -->
                  
                </div>
                <!-- container -->
                <!-- <table id="ipAddListBody" class="display dataTable no-footer ListBody" aria-describedby="invoiceDataTable_info" role="grid" style="display:none;">
                  <tbody>
                  
                  </tbody>
                </table> -->
              </div>
              
              <div class="clear"></div>
            </div>
          </div>
          </div>
          
        </div>
        
    
    </div> 
    </div>
    </div>
          </div>
            </div>
            </div>
            
    </div>

    <div>
      <!--begin::Container-->
      <div>
        <div class="row my-5">
          <div class="col">
    <div class="card card_graph"   id="cardcb3" ><!---->

      <!-- Card header -->
      <div class="card-header" role="tab" id="headingTwo2">
        <a class="collapsed" >
        <div class="card-header card-header-icon card-header-rose" id="cardHeaderPosition">
          <div class="card-icon" id="cardIcon">
            <i class="fa fa-flag" id="materialIcons"></i>
          </div>
          <h4 class="card-title"> Block Issuer Countries
            <i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo25"
            aria-expanded="false" aria-controls="collapseTwo25" class="fa fa-angle-down rotate-icon"></i>
            <label class="switch" style="float: right; display: none">
                <input type="checkbox" id="onIssuerCountriesBlocking" name="onIssuerCountriesBlocking" onclick="updateConfig(this);">
                <span class="slider round"></span>
              </label>
          </h4>
          </div>
        <!-- <h5 class="mb-0">
          <strong>Payment Comparison </strong><i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo25"
          aria-expanded="false" aria-controls="collapseTwo25" class="fa fa-angle-down rotate-icon"></i>
        </h5> -->
        </a>
      </div>
      <div id="collapseTwo25" class="collapse" role="tabpanel" >
      <div class="card-body">
       <div class="row">
    
    <div class="col-md-12 col-xs-12">
      
      <div class="cards">
        <div class="card-header card-header-icon card-header-rose">
        <!-- <div class="card-icon" id="cardIcon">
          <i class="material-icons" id="materialIcons">pie_chart</i>
        </div>
        <h4 class="card-title"> Payment Type Comparison
         
        </h4><br><br> -->
        <div class="col-md-12" style="align-items: flex-end;">
          
          <div class="bkn row">
          
            <div class="adduT row">
               <table>
                <tr class="fw-bold fs-6 text-gray-800">
                <!-- <td id="issuerCountryListBodyMsg" style="display:block">No blocked issuer countries</td>
                <td style="width:6%">
                  <div class="adduT row" style="text-align: right;padding: 14px 0 0 0;">
                                     -->
                      <div class="col-md-3">
                    <input type="submit" name="remittSubmit" disabled="disabled" value="Add Rule" onclick="tagsAddRule('country', 'country', 'issuerCountry')" id="popupButton2" class="btn btn-primary btn-md" data-toggle="modal" data-target="#myModal2"/>
                  </div>
                  <div class="col-md-3" style="margin-top: 10px;">
                    <input   type='search' class="form-control form-control-solid form-control form-control-solid-solid" id="search_issuerCountryListBody" onkeypress="if(event.keyCode === 32)return false;" placeholder="Search Issuer Country">
                  </div>
                  <div class="col-md-3">
                  <input class='btn btn-primary' id="searchResult2"   type='submit' value='Search' onclick="searchData('issuerCountryListBody','BLOCK_CARD_ISSUER_COUNTRY')">
                
                </div>
                <div class="col-md-3">
                  <input class='btn btn-primary btn-md bulkDeleteBtn disabled' type='submit' name="deleteRules" value='Bulk Delete' onclick="bulkDeleteFraudRule()">
                </div>
                  
                </div>
                </td>
                </tr>
              </table>
            </div> 
            <div id="issLst">
              <!-- <p class="accordion_head"><b>Blocked issuer countries List</b><span class="plusminus">+</span></p> -->
              <div class="scrollD" >
                <table class="display table table-striped table-row-bordered gy-5 gs-7" id="issuerCountryListBody" align="center" cellspacing="0" width="100%" style="text-align:center;">
                  <thead>
                    <tr class="boxheadingsmall fw-bold fs-6 text-gray-800" style="font-size: 11px;">
                      <th style='text-align: center'>Merchant</th>
                      <th style='text-align: center'>Issuer Country</th>
                      <th style='text-align: center'>Edit</th>
                        <th style='text-align: center'>Delete</th>
                      <th style='text-align: center'> <input class="form-check-input" type="checkbox" id="select_all2" style="margin-right:10px;" value="" onclick="selectall()">
                        <span class="form-check-sign">
                          <span class="check"></span>Action/Select All 
                      
                        </span></th>
                      
                    </tr>
                  </thead>
                </table>
              </div>
              
              
              <!-- <table id="issuerCountryListBody"  class="display dataTable no-footer ListBody" aria-describedby="invoiceDataTable_info" role="grid" style="display: none;">
                <tbody>
                  
                 
                </tbody>
              </table> -->
            
            </div>
              <div class="clear"></div> 
          </div>
         
         
          </div>
        </div>
       
      
  
  </div> 
    </div> 
    </div>
    </div>
  </div>
          </div></div></div></div>
    </div>

    <div>
      <!--begin::Container-->
      <div>
        <div class="row my-5">
          <div class="col">
    <div class="card card_graph"   id="cardcb3" ><!---->

      <!-- Card header -->
      <div class="card-header" role="tab" id="headingTwo2">
        <a class="collapsed" >
        <div class="card-header card-header-icon card-header-rose" id="cardHeaderPosition">
          <div class="card-icon" id="cardIcon">
            <i class="fa fa-flag" id="materialIcons"></i>
          </div>
          <h4 class="card-title"> Blocked User Countries
            <i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapseFour"
            aria-expanded="false" aria-controls="collapseFour" class="fa fa-angle-down rotate-icon"></i>
          
            <label class="switch" style="float: right; display: none">
                <input type="checkbox" id="onUserCountriesBlocking" name="onUserCountriesBlocking" onclick="updateConfig(this);">
                <span class="slider round"></span>
              </label>
          </h4>
          </div>
        <!-- <h5 class="mb-0">
          <strong>Payment Comparison </strong><i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo25"
          aria-expanded="false" aria-controls="collapseTwo25" class="fa fa-angle-down rotate-icon"></i>
        </h5> -->
        </a>
      </div>
      <div id="collapseFour" class="collapse" role="tabpanel" >
<div class="card-body">
  <div class="row">
    
  
  
  

    <div class="col-md-12 col-xs-12">
      
      <div class="cards">
        <div class="card-header card-header-icon card-header-rose">
        <!-- <div class="card-icon" id="cardIcon">
          <i class="material-icons" id="materialIcons">pie_chart</i>
        </div>
        <h4 class="card-title"> Payment Type Comparison
         
        </h4><br><br> -->
        <div class="col-md-12" style="align-items: flex-end;">
          
          <div class="bkn row">
          
            <div class="adduT row">
               <table>
                <tr class="fw-bold fs-6 text-gray-800">
                <!-- <td id="issuerCountryListBodyMsg" style="display:block">No blocked issuer countries</td>
                <td style="width:6%">
                  <div class="adduT row" style="text-align: right;padding: 14px 0 0 0;">
                                     -->
                                     <div class="col-md-2">
                    <input type="submit" name="remittSubmit" disabled="disabled" value="Add Rule" onclick="tagsAddRule('country', 'userCountry', 'userCountry')" id="popupButton2" class="btn btn-primary btn-md" data-toggle="modal" data-target="#myModal4"/>
                  </div>
                  <div class="col-md-3" style="margin-top: 10px;">
                    <input   type='search' class="form-control form-control-solid form-control form-control-solid-solid" id="search_userCountryListBody" onkeypress="if(event.keyCode === 32)return false;" placeholder="Search User Country">
                  </div>
                  <div class="col-md-2">
                  <input class='btn btn-primary btn-md' id="searchResult7"   type='submit' value='Search' onclick="searchData('userCountryListBody','BLOCK_USER_COUNTRY')">
                
                </div>
                <div class="col-md-2">
                  <input class='btn btn-primary btn-md bulkDeleteBtn disabled' name="deleteRules" type='submit' value='Bulk Delete' onclick="bulkDeleteFraudRule()">
                </div>
                  
                </div>
                </td>
                </tr>
              </table>
            </div> 
            <div id="issLst">
              <!-- <p class="accordion_head"><b>Blocked issuer countries List</b><span class="plusminus">+</span></p> -->
              <div class="scrollD" >
                <table class="display table table-striped table-row-bordered gy-5 gs-7" id="userCountryListBody" align="center" cellspacing="0" width="100%" style="text-align:center;">
                  <thead>
                    <tr class="boxheadingsmall fw-bold fs-6 text-gray-800" style="font-size: 11px;">
                      <th style='text-align: center'>Merchant</th>
                      <th style='text-align: center'>Issuer Country</th>
                      <th style='text-align: center'>Edit</th>
                        <th style='text-align: center'>Delete</th>
                      <th style='text-align: center'> <input class="form-check-input" type="checkbox" id="select_all3" style="margin-right:10px;" value="" onclick="selectall()">
                        <span class="form-check-sign">
                          <span class="check"></span>Action/Select All 
                      
                        </span></th>
                      
                    </tr>
                  </thead>
                </table>
              </div>
              
              
              <!-- <table id="issuerCountryListBody"  class="display dataTable no-footer ListBody" aria-describedby="invoiceDataTable_info" role="grid" style="display: none;">
                <tbody>
                  
                 
                </tbody>
              </table> -->
            
            </div>
              <div class="clear"></div> 
          </div>
         
         
          </div>
        </div>
       
      
  
  </div> 
    </div> 
    </div>
    </div>
  </div>
          </div></div></div></div>
    </div>

    <div>
      <!--begin::Container-->
      <div>
        <div class="row my-5">
          <div class="col">
    <div class="card card_graph"   id="cardcb17" ><!---->

      <!-- Card header -->
      <div class="card-header" role="tab" id="headingTwo17">
        <a class="collapsed" >
        <div class="card-header card-header-icon card-header-rose" id="cardHeaderPosition">
          <div class="card-icon" id="cardIcon">
            <i class="fa fa-flag" id="materialIcons"></i>
          </div>
          <h4 class="card-title"> Blocked User States
            <i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapse17"
            aria-expanded="false" aria-controls="collapse17" class="fa fa-angle-down rotate-icon"></i>
          
            <label class="switch" style="float: right; display: none">
                <input type="checkbox" id="onStateBlocking" name="onStateBlocking" onclick="updateConfig(this);">
                <span class="slider round"></span>
              </label>
          </h4>
          </div>
        </a>
      </div>
    <div id="collapse17" class="collapse" role="tabpanel" >
<div class="card-body">
  <div class="row">
    <div class="col-md-12 col-xs-12">
      <div class="cards">
        <div class="card-header card-header-icon card-header-rose row">
        <div class="col-md-12" style="align-items: flex-end;">
          
          <div class="bkn row">
          
            <div class="adduT row">
               <table>
                <tr class="fw-bold fs-6 text-gray-800">
                  <div class="col-md-2">
                    <input type="submit" name="remittSubmit" disabled="disabled" onclick="tagsAddRule('state', 'country', 'countryName')" value="Add Rule"  id="popupButton17" class="btn btn-primary btn-md" data-toggle="modal" data-target="#myModal17"/>
                  </div>
                </div>
                </td>
                </tr>
              </table>
            </div> 
            <div id="issLst">
              <!-- <p class="accordion_head"><b>Blocked issuer countries List</b><span class="plusminus">+</span></p> -->
              <div class="scrollD" >
                <table class="display table table-striped table-row-bordered gy-5 gs-7" id="userStateListBody" align="center" cellspacing="0" width="100%" style="text-align:center;">
                  <thead>
                    <tr class="boxheadingsmall fw-bold fs-6 text-gray-800 col-md-12" style="font-size: 11px;">
                      <th style='text-align: center'>Merchant</th>
                      <th style='text-align: center'>User State</th>
                      <th style='text-align: center'>Edit</th>
                        <th style='text-align: center'>Delete</th>
                    </tr>
                  </thead>
                </table>
              </div>
            </div>
              <div class="clear"></div> 
          </div>
         
         
          </div>
        </div>
  </div> 
    </div> 
    </div>
    </div>
  </div>
          </div></div></div></div>
    </div>

    <div>
      <!--begin::Container-->
      <div>
        <div class="row my-5">
          <div class="col">
<div class="card card_graph"   id="cardcb18" ><!---->

      <!-- Card header -->
      <div class="card-header" role="tab" id="headingTwo18">
        <a class="collapsed" >
        <div class="card-header card-header-icon card-header-rose" id="cardHeaderPosition">
          <div class="card-icon" id="cardIcon">
            <i class="fa fa-flag" id="materialIcons"></i>
          </div>
          <h4 class="card-title"> Blocked User Cities
            <i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapse18"
            aria-expanded="false" aria-controls="collapse18" class="fa fa-angle-down rotate-icon"></i>
          
            <label class="switch" style="float: right; display: none">
                <input type="checkbox" id="onCityBlocking" name="onCityBlocking" onclick="updateConfig(this);">
                <span class="slider round"></span>
              </label>
          </h4>
          </div>
        </a>
      </div>
    <div id="collapse18" class="collapse" role="tabpanel" >
<div class="card-body">
  <div class="row">
    <div class="col-md-12 col-xs-12">
      <div class="cards">
        <div class="card-header card-header-icon card-header-rose row">
        <div class="col-md-12" style="align-items: flex-end;">
          
          <div class="bkn">
          
            <div class="adduT row">
               <table>
                <tr class="fw-bold fs-6 text-gray-800">
                  <div class="col-md-2">
                    <input type="submit" name="remittSubmit" disabled="disabled" value="Add Rule" onclick="tagsAddRule('state', 'countryNameCityBlocking', 'countryNameCityBlocking')" id="popupButton18" class="btn btn-primary btn-md" data-toggle="modal" data-target="#myModal18"/>
                  </div>
                </div>
                </td>
                </tr>
              </table>
            </div> 
            <div id="issLst">
              <!-- <p class="accordion_head"><b>Blocked issuer countries List</b><span class="plusminus">+</span></p> -->
              <div class="scrollD" >
                <table class="display table table-striped table-row-bordered gy-5 gs-7" id="userCityListBody" align="center" cellspacing="0" width="100%" style="text-align:center;">
                  <thead>
                    <tr class="boxheadingsmall fw-bold fs-6 text-gray-800" style="font-size: 11px;">
                      <th style='text-align: center'>Merchant</th>
                      <th style='text-align: center'>User City</th>
                      <th style='text-align: center'>Edit</th>
                        <th style='text-align: center'>Delete</th>
                    </tr>
                  </thead>
                </table>
              </div>
            </div>
              <div class="clear"></div> 
          </div>
         
         
          </div>
        </div>
  </div> 
    </div> 
    </div>
    </div>
  </div>
          </div></div></div></div>
    </div>

    <div>
      <!--begin::Container-->
      <div >
        <div class="row my-5">
          <div class="col">
      <div class="card card_graph" id="cardcb4"  ><!--onclick="monthlycardupdateTabState(this)"-->
        <div class="card-header " role="tab" id="headingTwo1">
          <a class="collapsed" >
            <div class="card-header card-header-icon card-header-info" id="cardHeaderPosition">
            <div class="card-icon" id="cardIcon">
              <i class="fa fa-envelope" id="materialIcons"></i>
              </div> 
             <h4 class="card-title">Block Email Addresses<i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapseFive"
              aria-expanded="false" aria-controls="collapseFive" class="fa fa-angle-down rotate-icon"></i>
               <label class="switch" style="float: right; display: none">
                <input type="checkbox" id="onEmailBlocking" name="onEmailBlocking" onclick="updateConfig(this);">
                <span class="slider round"></span>
              </label>
              </h4>
              </div>
            <!-- <h5 class="mb-0">
            <strong>Monthly Transaction</strong> <i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapseSix"
            aria-expanded="false" aria-controls="collapseSix" class="fa fa-angle-down rotate-icon"></i>
            </h5> -->
          </a>
          </div>
          <div id="collapseFive" class="collapse active " role="tabpanel" >
<div class="card-body">
    <div class="row">
    <div class="col-md-12 col-xs-12">
      
        <div class="cards">
          <div class="card-header card-header-icon card-header-info">
          <!-- <div class="card-icon" id="cardIcon">
            <i class="material-icons" id="materialIcons">multiline_chart</i>
          </div> -->
          <!-- <h4 class="card-title">Monthly Transaction
           
          </h4><br>
          <br> -->
          <div class="col-md-12" style="align-items: flex-end;">
            <div class="bkn">
              
              <div class="adduT row">
                   <table>
                  <tr class="fw-bold fs-6 text-gray-800">
                  <!-- <td id="emailListBodyMsg" style="display:block">No blocked Email Addresses</td>
                  <td style="width:6%"><div class="adduT row" style="text-align: right; padding: 14px 0 0 0;"> -->
                    <div class="col-md-2">
                  <input type="submit" name="remittSubmit" disabled="disabled" value="Add Rule" onclick="tagsAddRule()" id="popupButton5" class="btn btn-primary btn-md" 
                  data-toggle="modal" data-target="#myModal5"/>
                    </div><div class="col-md-3" style="margin-top: 10px;">
                      <input   type='search' class="form-control form-control-solid form-control form-control-solid-solid" id="search_emailListBody" onkeypress="if(event.keyCode === 32)return false;" placeholder="Search Email Id">
                    </div>
                    <div class="col-md-2">
                    <input class='btn btn-primary btn-md' id="searchResult3"   type='submit' value='Search' onclick="searchData('emailListBody','BLOCK_EMAIL_ID')">
                    </div>
                    <div class="col-md-2">
                      <input class='btn btn-primary btn-md bulkDeleteBtn disabled' type='submit' name="deleteRules" value='Bulk Delete' onclick="bulkDeleteFraudRule()">
                    </div>
                  
                </div></td>
                  </tr>
                </table>
              </div>
              
              <div>
                 <!-- <p class="accordion_head"><b>Blocked Email Address List</b><span class="plusminus">+</span></p> -->
                 <div class="scrollD" >
                <table id="emailListBody" class="display table table-striped table-row-bordered gy-5 gs-7" align="center" cellspacing="0" width="100%" style="text-align:center;">
                  <thead>
                    <tr class="boxheadingsmall fw-bold fs-6 text-gray-800" style="font-size: 11px;">
                      <th style='text-align: center'>Merchant</th>
                      <th style='text-align: center'>Email Address</th>
                      <!-- <th style='text-align: center'>Name</th> -->
                      <th style='text-align: center'>Start Date</th>
                      <th style='text-align: center'>End Date</th>
                      <th style='text-align: center'>Start Time</th>
                      <th style='text-align: center'>End Time</th>
                      <th style='text-align: center'>Weeks</th>
                      <th style='text-align: center'>Edit</th>
                        <th style='text-align: center'>Delete</th>
                      <th style='text-align: center'> <input class="form-check-input" type="checkbox" id="select_all4" style="margin-right:10px;" value="" onclick="selectall()">
                        <span class="form-check-sign">
                          <span class="check"></span>Action/Select All 
                      
                        </span></th>
                      
                    </tr>
                  </thead>
                </table>
              </div>
              <div class="card ">
                <div class="card-header card-header-rose card-header-text">
                  <div class="card-text">
                  <h4 class="card-title">Add Bulk Rules for Email Address</h4>
                  </div>
                </div>
                <div class="card-body ">
              <div class="container">
                <div class="row it">
                <div class="col-sm-offset-1 col-sm-10" id="one">
                <p>
                Please upload documents only in 'CSV' format.
                </p><br>
                <div class="row">
                  <div class="col-sm-offset-4 col-sm-4 form-group">
                  
                  </div><!--form-group-->
                </div><!--row-->
                <div id="uploader">
                <div class="row uploadDoc">
                  <div class="col-sm-4 ">
                    <table
                      id="example" class="csv2" style="display: none;">
                      <thead>
                        <tr class="fw-bold fs-6 text-gray-800">
                          <th>Merchant</th>
                          <th>Email Address</th>
                          <!-- <th>Name</th> -->
                          <th>Start Date</th>
                          <th>End Date</th>
                          <th>Start Time</th>
                          <th>End Time</th>
                          <th>Week Days</th>
                            <th>Always On</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr class="fw-bold fs-6 text-gray-800">
                          <td>1022291114152030</td>
                        <td>abc@gmail.com</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td>TRUE</td>
                        </tr>

                        <tr class="fw-bold fs-6 text-gray-800">
                          <td>1022291114152030</td>
                        <td>abc@gmail.com</td>
                        <td>13/09/2020</td>
                        <td>25/09/2020</td>
                        <td>11:51:41</td>
                        <td>11:51:42</td>
                        <td>MON TUE WED </td>
                        <td>FALSE</td>
                        </tr>
                      </tbody>
                    </table></div>
                  <div class="col-sm-6">
                  <div class="docErr">Please upload valid file</div><!--error-->
                  <div class="fileUpload btn btn-orange">
                    <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
                    <span class="upl" id="upload">Upload document</span>
                    <form enctype="multipart/form-data" method="post" id="emailBulkUpload" >
                    <div style="display:flex">                      <!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
                
                  <!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
                  <input type="file" name="fileName" class="upload up" id="up" onchange="readURL(this);" />
                  <input type="submit" class="btn btn-primary btn-xs"  id="btnBulkUpload" name="btnBulkUpload" disabled onclick="bulkUpload('BLOCK_EMAIL_ID','emailBulkUpload')"> 
                    </div>
                </form>
                    <!-- <input type="file" class="upload up" id="up" onchange="readURL(this);" /> -->
                  </div><!-- btn-orange -->
                  </div><!-- col-3 -->
                 
                  <p>Note: If Alwayss True Flag is checked then Date, Time, Week does not need to be filled</p> but if it is not then all the required fields will have to be filled</p>
                </div><!--row-->
                </div><!--uploader-->
                <div class="text-center">
                <!-- <a class="btn btn-new"><i class="fa fa-plus"></i> Add new</a> -->
                <!-- <button type="button" id="btnBulkUpload" onclick="bulkUpload()"><a class="btn btn-next"><i class="fa fa-upload"></i> Bulk Upload</a></button> -->
                </div>
                </div><!--one-->
                </div><!-- row -->
                </div>
                </div></div>

                 <!-- <div  class=" dataTable no-footer ListBody" aria-describedby="invoiceDataTable_info" role="grid" style="display:none;overflow:inherit !important;" >
                <input type="text" id="search" placeholder="Type to search">
              <table id="emailListBody">
                  <tbody>
                  
                  </tbody>
                </table>
              </div> -->
              
                
            </div>
        </div>
          </div>
          </div>
          
        </div>
        
    
    </div> 
    </div>
    </div>
          </div> 
          </div></div></div></div> 
      </div>

      <div>
        <!--begin::Container-->
        <div>
          <div class="row my-5">
            <div class="col">
      <div class="card card_graph"   id="cardcb5" ><!--onclick="monthlycardupdateTabState(this)"-->
        <div class="card-header " role="tab" id="headingTwo1">
          <a class="collapsed" >
            <div class="card-header card-header-icon card-header-warning" id="cardHeaderPosition">
            <div class="card-icon" id="cardIcon">
              <i class="fa fa-inr"></i>
              </div> 
             <h4 class="card-title">Limit Transaction Amount<i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapseSeven"
              aria-expanded="false" aria-controls="collapseSeven" class="fa fa-angle-down rotate-icon"></i>
               <label class="switch" style="float: right; display: none">
                <input type="checkbox" id="onLimitTxnAmtBlocking" name="onLimitTxnAmtBlocking" onclick="updateConfig(this);">
                <span class="slider round"></span>
              </label>
              </h4>
              </div>
            <!-- <h5 class="mb-0">
            <strong>Monthly Transaction</strong> <i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapseEight"
            aria-expanded="false" aria-controls="collapseEight" class="fa fa-angle-down rotate-icon"></i>
            </h5> -->
          </a>
          </div>
          <div id="collapseSeven" class="collapse active " role="tabpanel" >
<div class="card-body">
    <div class="row">
    <div class="col-md-12 col-xs-12">
      
        <div class="cards">
          <div class="card-header card-header-icon card-header-info row">
          <!-- <div class="card-icon" id="cardIcon">
            <i class="material-icons" id="materialIcons">multiline_chart</i>
          </div> -->
          <!-- <h4 class="card-title">Monthly Transaction
           
          </h4><br>
          <br> -->
          <div class="col-md-12" style="align-items: flex-end;">
            <div class="bkn">
              
                <div class="adduT row">
                  <div class="col-md-3">
                  <table>
                    
                    <tr class="fw-bold fs-6 text-gray-800">
                     <!-- <td id="txnAmountListBodyMsg" style="display:block">No blocked Transactional Amount</td>
                        <td style="width:6%"><div class="adduT row" style="text-align: right; padding: 14px 0 0 0;"> -->
                        <input type="submit" name="remittSubmit" value="Add Rule" onclick="tagsAddRule()" id="popupButton8" class="btn btn-primary btn-md disabled" 
                      data-toggle="modal" data-target="#myModal8"/>
                      <!-- <input class='btn btn-primary btn-md' id="searchResult"  type='submit' value='Click Here To Search' onclick="searchData('txnAmountListBody','BLOCK_TXN_AMOUNT')"> -->
                        </div></td>
                    </tr>
                  </table>
                </div>
                </div>
                <div>
                  <!-- <p class="accordion_head"><b>Blocked Transactional Amount List</b><span class="plusminus">+</span></p> -->
                  <div class="scrollD" >
                    <table class="display table table-striped table-row-bordered gy-5 gs-7" id="txnAmountListBody" align="center" cellspacing="0" width="100%" style="text-align:center;">
                      <thead>
                        <tr class="boxheadingsmall fw-bold fs-6 text-gray-800" style="font-size: 11px;">
                          <th style='text-align: center'>Merchant</th>
                          <th style='text-align: center'>Currency</th>
                          <th style='text-align: center'>Amount</th>
                          <th style='text-align: center'>Transaction Count</th>
                          <th style='text-align: center'>Min Amount</th>
                          <th style='text-align: center'>Max Amount</th>
                          <th style='text-align: center'>Edit</th>
                        <th style='text-align: center'>Delete</th>
                          

                          
                        </tr>
                      </thead>
                    </table>
                  </div>
    
                  
    
                  <!-- <table id="txnAmountListBody" class="display dataTable no-footer ListBody" aria-describedby="invoiceDataTable_info" role="grid" style="display:none;">
                  <tbody>
  
                 </tbody>
                  </table> -->
                </div>
                <div class="clear"></div>
            </div>
          </div>
          </div>
          
        </div>
        
    
    </div> 
    </div>
    </div>
          </div> 
          </div></div></div></div> 
      </div>

      <div>
        <!--begin::Container-->
        <div>
          <div class="row my-5">
            <div class="col">
      <div class="card card_graph"  id="cardcb6" ><!--onclick="monthlycardupdateTabState(this)"-->
        <div class="card-header " role="tab" id="headingTwo1">
          <a class="collapsed" >
            <div class="card-header card-header-icon card-header-warning" id="cardHeaderPosition">
            <div class="card-icon" id="cardIcon">
              <i class="fa fa-credit-card" id="materialIcons"></i>
              </div> 
             <h4 class="card-title">Block Card Ranges<i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapseEight"
              aria-expanded="false" aria-controls="collapseEight" class="fa fa-angle-down rotate-icon"></i>
               <label class="switch" style="float: right; display: none">
                <input type="checkbox" id="onCardRangeBlocking" name="onCardRangeBlocking" onclick="updateConfig(this);">
                <span class="slider round"></span>
              </label>
              </h4>
              </div>
            <!-- <h5 class="mb-0">
            <strong>Monthly Transaction</strong> <i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapseFive"
            aria-expanded="false" aria-controls="collapseTwo1" class="fa fa-angle-down rotate-icon"></i>
            </h5> -->
          </a>
          </div>
          <div id="collapseEight" class="collapse active " role="tabpanel" >
<div class="card-body">
    <div class="row">
    <div class="col-md-12 col-xs-12">
      
        <div class="cards">
          <div class="card-header card-header-icon card-header-info">
          <!-- <div class="card-icon" id="cardIcon">
            <i class="material-icons" id="materialIcons">multiline_chart</i>
          </div> -->
          <!-- <h4 class="card-title">Monthly Transaction
           
          </h4><br>
          <br> -->
          <div class="col-md-12" style="align-items: flex-end;">
            <div class="bkn">
              
                <div class="adduT row">
                  <table>
                  <tr class="fw-bold fs-6 text-gray-800">
                  <!-- <td id="cardBinListBodyMsg" style="display:block">No blocked card ranges</td>
                  <td style="width:6%">
                    <div class="adduT row" style="text-align: right;padding: 14px 0 0 0;"> -->
                      <div class="col-md-2">
                    <input type="submit" name="remittSubmit" disabled="disabled" value="Add Rule" onclick="tagsAddRule()" id="popupButton3" class="btn btn-primary btn-md" 
                    data-toggle="modal" data-target="#myModal3"/>
                      </div>
                      <div class="col-md-3" style="margin-top: 10px;">
                        <input   type='search' class="form-control form-control-solid form-control form-control-solid-solid" id="search_cardBinListBody" onkeypress="if(event.keyCode === 32)return false;" placeholder="Search Card Bin">
                      </div>
                      <div class="col-md-2">
                      <input class='btn btn-primary btn-md' id="searchResult4"   type='submit' value='Search' onclick="searchData('cardBinListBody','BLOCK_CARD_BIN')">
                      </div>
                      <div class="col-md-2">
                        <input class='btn btn-primary btn-md bulkDeleteBtn disabled' type='submit' name="deleteRules" value='Bulk Delete' onclick="bulkDeleteFraudRule()">
                      </div>
                    </div>
                  </td>
                  </tr>             
                  </table>
                </div>
              <div>
                <!-- <p class="accordion_head"><b>Blocked Card Range List</b><span class="plusminus">+</span></p>  -->
                <div class="scrollD" >
                  <table  class="display table table-striped table-row-bordered gy-5 gs-7" id="cardBinListBody" align="center" cellspacing="0" width="100%" style="text-align:center;">
                    <thead>
                      <tr class="boxheadingsmall fw-bold fs-6 text-gray-800" style="font-size: 11px;">
                        <th style='text-align: center'>Merchant</th>
                        <th style='text-align: center'>Card Range</th>
                        <!-- <th style='text-align: center'>Name</th> -->
                        <th style='text-align: center'>Start Date</th>
                        <th style='text-align: center'>End Date</th>
                        <th style='text-align: center'>Start Time</th>
                        <th style='text-align: center'>End Time</th>
                        <th style='text-align: center'>Weeks</th>
                        <th style='text-align: center'>Edit</th>
                        <th style='text-align: center'>Delete</th>
                        <th style='text-align: center'> <input class="form-check-input" type="checkbox" id="select_all6" style="margin-right:10px;" value="" onclick="selectall()">
                          <span class="form-check-sign">
                            <span class="check"></span>Action/Select All 
                        
                          </span></th>
                        
                      </tr>
                    </thead>
                  </table>
                </div>
  
                <div class="card ">
                  <div class="card-header card-header-rose card-header-text">
                    <div class="card-text">
                    <h4 class="card-title">Add Bulk Rules for Block Cards</h4>
                    </div>
                  </div>
                  <div class="card-body ">
                <div class="container">
                  <div class="row it">
                  <div class="col-sm-offset-1 col-sm-10" id="one">
                  <p>
                  Please upload documents only in 'CSV' format.
                  </p><br>
                  <div class="row">
                    <div class="col-sm-offset-4 col-sm-4 form-group">
                    
                    </div><!--form-group-->
                  </div><!--row-->
                  <div id="uploader">
                  <div class="row uploadDoc">
                    <div class="col-sm-4 ">
                      <table
                        id="example" class="csv4" style="display: none;">
                        <thead>
                          <tr class="fw-bold fs-6 text-gray-800">
                            <th>Merchant</th>
                            <th>Card Range</th>
                            <!-- <th>Name</th> -->
                            <th>Start Date</th>
                            <th>End Date</th>
                            <th>Start Time</th>
                            <th>End Time</th>
                            <th>Week Days</th>
                            <th>Always On</th>
                          </tr>
                        </thead>
                        <tbody>
                          <tr class="fw-bold fs-6 text-gray-800">
                            <td>1022291114152030</td>
                          <td>456785</td>
                          <td></td>
                          <td></td>
                          <td></td>
                          <td></td>
                          <td></td>
                          <td>TRUE</td>
                          </tr>

                          <tr class="fw-bold fs-6 text-gray-800">
                            <td>1022291114152030</td>
                          <td>456785</td>
                          <td>13/09/2020</td>
                          <td>25/09/2020</td>
                          <td>11:51:41</td>
                          <td>11:51:42</td>
                          <td>MON TUE WED </td>
                          <td>FALSE</td>
                          </tr>
                        </tbody>
                      </table></div>
                    <div class="col-sm-6">
                    <div class="docErr">Please upload valid file</div><!--error-->
                    <div class="fileUpload btn btn-orange">
                      <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
                      <span class="upl" id="upload">Upload document</span>
                      <form enctype="multipart/form-data" method="post" id="cardRangeBulkUpload" >
                      <div style="display:flex">                      <!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
                  
                    <!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
                    <input type="file" name="fileName" class="upload up" id="up" onchange="readURL(this);" />
                    <input type="submit" class="btn btn-primary btn-xs"  id="btnBulkUpload" name="btnBulkUpload" disabled onclick="bulkUpload('BLOCK_CARD_BIN','cardRangeBulkUpload')"> 
                      </div>
                  </form>
                      <!-- <input type="file" class="upload up" id="up" onchange="readURL(this);" /> -->
                    </div><!-- btn-orange -->
                    </div><!-- col-3 -->
                    <p>Note: If Alwayss True Flag is checked then Date, Time, Week does not need to be filled</p> but if it is not then all the required fields will have to be filled</p>
                  
                  </div><!--row-->
                  </div><!--uploader-->
                  <div class="text-center">
                  <!-- <a class="btn btn-new"><i class="fa fa-plus"></i> Add new</a> -->
                  <!-- <button type="button" id="btnBulkUpload" onclick="bulkUpload()"><a class="btn btn-next"><i class="fa fa-upload"></i> Bulk Upload</a></button> -->
                  </div>
                  </div><!--one-->
                  </div><!-- row -->
                  </div>
                  </div></div>
                <!-- <table id="cardBinListBody" class="display dataTable no-footer ListBody" aria-describedby="invoiceDataTable_info" role="grid" style="display: none;">
                <tbody>
  
  
                </tbody>
                </table> -->
              </div>
              <div class="clear"></div>
            </div>
          </div>
          
          </div>
          
        </div>
        
    
    </div> 
    </div>
    </div>
          </div>  
            </div></div></div></div>
      </div>

      <div>
        <!--begin::Container-->
        <div>
          <div class="row my-5">
            <div class="col">
      <div class="card card_graph"  id="cardcb8"><!--onclick="monthlycardupdateTabState(this)"-->
        <div class="card-header " role="tab" id="headingTwo1">
          <a class="collapsed" >
            <div class="card-header card-header-icon card-header-success" id="cardHeaderPosition">
            <div class="card-icon" id="cardIcon">
              <i class="fa fa-credit-card"></i>
              </div> 
             <h4 class="card-title"> Block Card Mask<i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapseNine"
              aria-expanded="false" aria-controls="collapseNine" class="fa fa-angle-down rotate-icon"></i>
               <label class="switch" style="float: right; display: none">
                <input type="checkbox" id="onCardMaskBlocking" name="onCardMaskBlocking" onclick="updateConfig(this);">
                <span class="slider round"></span>
              </label>
              </h4>
              </div>
            <!-- <h5 class="mb-0">
            <strong>Monthly Transaction</strong> <i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapseFour"
            aria-expanded="false" aria-controls="collapseFour" class="fa fa-angle-down rotate-icon"></i>
            </h5> -->
          </a>
          </div>
          <div id="collapseNine" class="collapse active " role="tabpanel" >
<div class="card-body">
    <div class="row">
    <div class="col-md-12 col-xs-12">
      
        <div class="cards">
          <div class="card-header card-header-icon card-header-info">
          <!-- <div class="card-icon" id="cardIcon">
            <i class="material-icons" id="materialIcons">multiline_chart</i>
          </div> -->
          <!-- <h4 class="card-title">Monthly Transaction
           
          </h4><br>
          <br> -->
          <div class="col-md-12" style="align-items: flex-end;">
            <div class="bkn">
              
              <div class="adduT row">
                   <table>
                  <tr class="fw-bold fs-6 text-gray-800">
                  <!-- <td id="emailListBodyMsg" style="display:block">No blocked Email Addresses</td>
                  <td style="width:6%"><div class="adduT row" style="text-align: right; padding: 14px 0 0 0;"> -->
                    <div class="col-md-2">
                      <input type="submit" name="remittSubmit" disabled="disabled" value="Add Rule" onclick="tagsAddRule()" id="popupButton0" class="btn btn-primary btn-md" 
                      data-toggle="modal" data-target="#myModal9"/> 
                    </div>
                    <div class="col-md-3" style="margin-top: 10px;">
                    <input   type='search' class="form-control form-control-solid form-control form-control-solid-solid" id="search_cardNoListBody" onkeypress="if(event.keyCode === 32)return false;" placeholder="Search Card No">
                  </div>
                  <div class="col-md-2">
                  <input class='btn btn-primary btn-md' id="searchResult6"   type='submit' value='Search' onclick="searchData('cardNoListBody','BLOCK_CARD_NO')">
                  </div>
                  <div class="col-md-2">
                    <input class='btn btn-primary btn-md bulkDeleteBtn disabled'type='submit' name="deleteRules" value='Bulk Delete' onclick="bulkDeleteFraudRule()">
                  </div>
                </div></td>
                  </tr>
                </table>
              </div>

              <div>
                <div class="scrollD" >
                  <table id="cardNoListBody" class="display table table-striped table-row-bordered gy-5 gs-7" align="center" cellspacing="0" width="100%" style="text-align:center;">
                    <thead>
                      <tr class="boxheadingsmall fw-bold fs-6 text-gray-800" style="font-size: 11px;">
                        <th style='text-align: center'>Merchant</th>
                        <th style='text-align: center'>Card No.</th>
                        <!-- <th style='text-align: center'>Allowed Transaction</th> -->
                        <th style='text-align: center'>Start Date</th>
                        <th style='text-align: center'>End Date</th>
                        <th style='text-align: center'>Start Time</th>
                        <th style='text-align: center'>End Time</th>
                        <th style='text-align: center'>Weeks</th>
                        <th style='text-align: center'>Edit</th>
                        <th style='text-align: center'>Delete</th>
                        <th style='text-align: center'> <input class="form-check-input" type="checkbox" id="select_all7" style="margin-right:10px;" value="" onclick="selectall()">
                          <span class="form-check-sign">
                            <span class="check"></span>Action/Select All 
                        
                          </span></th>
                      <!-- <th style='text-align: center'><input class='btn' id="bulkdelete"  type='submit' value='Bulk Delete' onclick="bulkDeleteFraudRule()"></th> -->
                        
                      </tr>
                    </thead>
                  </table>
                </div>
                <div class="card ">
                  <div class="card-header card-header-rose card-header-text">
                    <div class="card-text">
                    <h4 class="card-title">Add Bulk Rules for Card Mask</h4>
                    </div>
                  </div>
                  <div class="card-body ">
                <div class="container">
                  <div class="row it">
                  <div class="col-sm-offset-1 col-sm-10" id="one">
                  <p>
                  Please upload documents only in 'CSV' format.
                  </p><br>
                  <div class="row">
                    <div class="col-sm-offset-4 col-sm-4 form-group">
                    
                    </div><!--form-group-->
                  </div><!--row-->
                  <div id="uploader">
                  <div class="row uploadDoc">
                    <div class="col-sm-4 ">
                      <table
                        id="example" class="csv5" style="display: none;">
                        <thead>
                          <tr class="fw-bold fs-6 text-gray-800">
                            <th>Merchant</th>
                            <th >Card No.</th>
                            <!-- <th >Allowed Transaction</th> -->
                            <th>Start Date</th>
                            <th>End Date</th>
                            <th>Start Time</th>
                            <th>End Time</th>
                            <th>Week Days</th>
                              <th>Always On</th>
                          </tr>
                        </thead>
                        <tbody>
                          <tr class="fw-bold fs-6 text-gray-800">
                            <td>1022291114152030</td>
                          <td>401200******3714</td>
                          <td></td>
                          <td></td>
                          <td></td>
                          <td></td>
                          <td></td>
                          <td>TRUE</td>
                          </tr>

                          <tr class="fw-bold fs-6 text-gray-800">
                            <td>1022291114152030</td>
                          <td>401200******3714</td>
                          <td>13/09/2020</td>
                          <td>25/09/2020</td>
                          <td>11:51:41</td>
                          <td>11:51:42</td>
                          <td>MON TUE WED </td>
                          <td>FALSE</td>
                          </tr>
                        </tbody>
                      </table></div>
                    <div class="col-sm-6">
                    <div class="docErr">Please upload valid file</div><!--error-->
                    <div class="fileUpload btn btn-orange">
                      <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
                      <span class="upl" id="upload">Upload document</span>
                      <form enctype="multipart/form-data" method="post" id="cardMaskBulkUpload" >
                      <div style="display:flex">                      <!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
                  
                    <!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
                    <input type="file" name="fileName" class="upload up" id="up" onchange="readURL(this);" />
                    <input type="submit" class="btn btn-primary btn-xs" disabled  id="btnBulkUpload" name="btnBulkUpload" onclick="bulkUpload('BLOCK_CARD_NO','cardMaskBulkUpload')"> 
                      </div>
                  </form>
                      <!-- <input type="file" class="upload up" id="up" onchange="readURL(this);" /> -->
                    </div><!-- btn-orange -->
                    </div><!-- col-3 -->
                    <p>Note: If Alwayss True Flag is checked then Date, Time, Week does not need to be filled</p> but if it is not then all the required fields will have to be filled</p>
                  
                  </div><!--row-->
                  </div><!--uploader-->
                  <div class="text-center">
                  <!-- <a class="btn btn-new"><i class="fa fa-plus"></i> Add new</a> -->
                  <!-- <button type="button" id="btnBulkUpload" onclick="bulkUpload()"><a class="btn btn-next"><i class="fa fa-upload"></i> Bulk Upload</a></button> -->
                  </div>
                  </div><!--one-->
                  </div><!-- row -->
                  </div>
                  </div></div>
                
              </div>

          </div>
          </div>
          
        </div>
        
    
    </div> 
    </div>
    </div>
          </div>  
      </div>
            </div></div></div></div>
    </div>

    <div>
      <!--begin::Container-->
      <div>
        <div class="row my-5">
          <div class="col">
    <div class="card card_graph"  id="cardcb9"><!--onclick="monthlycardupdateTabState(this)"-->
      <div class="card-header " role="tab" id="headingTwo1">
        <a class="collapsed" >
          <div class="card-header card-header-icon card-header-success" id="cardHeaderPosition">
          <div class="card-icon" id="cardIcon">
            <i class="fa fa-credit-card"></i>
            </div> 
           <h4 class="card-title">Limit Card Transaction<i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTen"
            aria-expanded="false" aria-controls="collapseTen" class="fa fa-angle-down rotate-icon"></i>
             <label class="switch" style="float: right; display: none">
                <input type="checkbox" id="onLimitCardTxnBlocking" name="onLimitCardTxnBlocking" onclick="updateConfig(this);">
                <span class="slider round"></span>
              </label>
            </h4>
            </div>
          <!-- <h5 class="mb-0">
          <strong>Monthly Transaction</strong> <i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapseFour"
          aria-expanded="false" aria-controls="collapseFour" class="fa fa-angle-down rotate-icon"></i>
          </h5> -->
        </a>
        </div>
        <div id="collapseTen" class="collapse active " role="tabpanel" >
<div class="card-body">
  <div class="row">
  <div class="col-md-12 col-xs-12">
    
      <div class="cards">
        <div class="card-header card-header-icon card-header-info" style="white-space: inherit!important;">
        <!-- <div class="card-icon" id="cardIcon">
          <i class="material-icons" id="materialIcons">multiline_chart</i>
        </div> -->
        <!-- <h4 class="card-title">Monthly Transaction
         
        </h4><br>
        <br> -->
        <div class="col-md-12" style="align-items: flex-end;">
          <div class="bkn">
            
            <div class="adduT row">
                 <table>
                <tr class="fw-bold fs-6 text-gray-800">
                <!-- <td id="emailListBodyMsg" style="display:block">No blocked Email Addresses</td>
                <td style="width:6%"><div class="adduT row" style="text-align: right; padding: 14px 0 0 0;"> -->
                  <div class="col-md-2">
                    <input type="submit" name="remittSubmit" disabled="disabled" value="Add Rule" onclick="tagsAddRule()" id="popupButton0" class="btn btn-primary btn-md" 
                    data-toggle="modal" data-target="#myModal0"/> 
                  </div>
                  <div class="col-md-3" style="margin-top: 10px;">
                    <input   type='search' class="form-control form-control-solid form-control form-control-solid-solid" id="search_perCardTxnsListBody" onkeypress="if(event.keyCode === 32)return false;" placeholder="Search Transaction">
                  </div>
                  <div class="col-md-2">
                  <input class='btn btn-primary btn-md' id="searchResult9"   type='submit' value='Search' onclick="searchData('perCardTxnsListBody','BLOCK_CARD_TXN_THRESHOLD')">
                  </div>
                  <div class="col-md-2">
                    <input class='btn btn-primary btn-md bulkDeleteBtn disabled' type='submit' name="deleteRules" value='Bulk Delete' onclick="bulkDeleteFraudRule()">
                  </div>
              </div></td>
                </tr>
              </table>
            </div>

            <div>
              <div class="scrollD" >
                <table id="perCardTxnsListBody" class="display table table-striped table-row-bordered gy-5 gs-7" align="center" cellspacing="0" width="100%" style="text-align:center;">
                  <thead>
                    <tr class="boxheadingsmall fw-bold fs-6 text-gray-800" style="font-size: 11px;">
                      <th style='text-align: center'>Merchant</th>
                      <th style='text-align: center'>Card No.</th>
                      <th style='text-align: center'>Allowed Transaction</th>
                      <th style='text-align: center'>Start Date</th>
                      <th style='text-align: center'>End Date</th>
                      <th style='text-align: center'>Start Time</th>
                      <th style='text-align: center'>End Time</th>
                      <th style='text-align: center'>Weeks</th>
                      <th style='text-align: center'>Edit</th>
                        <th style='text-align: center'>Delete</th>
                      <th style='text-align: center'> <input class="form-check-input" type="checkbox" id="select_all8" style="margin-right:10px;" value="" onclick="selectall()">
                        <span class="form-check-sign">
                          <span class="check"></span>Action/Select All 
                      
                        </span></th>  
                    </tr>
                  </thead>
                </table>
              </div>
              <div class="card ">
                <div class="card-header card-header-rose card-header-text">
                  <div class="card-text">
                  <h4 class="card-title">Add Bulk Rules for Block Cards Transaction</h4>
                  </div>
                </div>
                <div class="card-body ">
              <div class="container">
                <div class="row it">
                <div class="col-sm-offset-1 col-sm-10" id="one">
                <p>
                Please upload documents only in 'CSV' format.
                </p><br>
                <div class="row">
                  <div class="col-sm-offset-4 col-sm-4 form-group">
                  
                  </div><!--form-group-->
                </div><!--row-->
                <div id="uploader">
                <div class="row uploadDoc">
                  <div class="col-sm-4 ">
                    <table
                      id="example" class="csv6" style="display: none;">
                      <thead>
                        <tr class="fw-bold fs-6 text-gray-800">
                          <th>Merchant</th>
                          <th>Card No</th>
                          <th>Allowed Transaction</th>
                          <th>Start Date</th>
                          <th>End Date</th>
                          <th>Start Time</th>
                          <th>End Time</th>
                          <th>Week Days</th>
                          <th>Always On</th>
                          <th>TimeDurations</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr class="fw-bold fs-6 text-gray-800">
                          <!-- <th>Merchant</th> -->
                          <td>1022291114152030</td>
                        <td>546465******6775</td>
                        <td>1000</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td>TRUE</td>
                        <td>Monthly</td>
                        </tr>

                        <tr class="fw-bold fs-6 text-gray-800">
                          <!-- <th>Merchant</th> -->
                          <td>1022291114152030</td>
                        <td>546465******6775</td>
                        <td>10000</td>
                        <td>13-09-2020</td>
                        <td>25-09-2020</td>
                        <td>11:51:41</td>
                        <td>11:51:42</td>
                        <td>MON TUE WED </td>
                        <td>FALSE</td>
                        <td>Daily</td>
                        </tr>
                      </tbody>
                    </table></div>
                  <div class="col-sm-6">
                  <div class="docErr">Please upload valid file</div><!--error-->
                  <div class="fileUpload btn btn-orange">
                    <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
                    <span class="upl" id="upload">Upload document</span>
                    <form enctype="multipart/form-data" method="post" id="LimitCardBulkUpload" >
                    <div style="display:flex">                      <!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
                
                  <!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
                  <input type="file" name="fileName" class="upload up" id="up" onchange="readURL(this);" />
                  <input type="submit" class="btn btn-primary btn-xs"  id="btnBulkUpload" name="btnBulkUpload" disabled onclick="bulkUpload('BLOCK_CARD_TXN_THRESHOLD','LimitCardBulkUpload')"> 
                    </div>
                </form>
                    <!-- <input type="file" class="upload up" id="up" onchange="readURL(this);" /> -->
                  </div><!-- btn-orange -->
                  </div><!-- col-3 -->
                  <p>Note: If Alwayss True Flag is checked then Date, Time, Week does not need to be filled</p> but if it is not then all the required fields will have to be filled</p>
                
                </div><!--row-->
                </div><!--uploader-->
                <div class="text-center">
                <!-- <a class="btn btn-new"><i class="fa fa-plus"></i> Add new</a> -->
                <!-- <button type="button" id="btnBulkUpload" onclick="bulkUpload()"><a class="btn btn-next"><i class="fa fa-upload"></i> Bulk Upload</a></button> -->
                </div>
                </div><!--one-->
                </div><!-- row -->
                </div>
                </div></div>
              
            </div>

        </div>
        </div>
        
      </div>
      
  
  </div> 
  </div>
  </div>
        </div>  
    </div>
          </div></div></div></div>
  </div>

  <div>
    <!--begin::Container-->
    <div>
      <div class="row my-5">
        <div class="col">
  <div class="card card_graph" id="cardcb10" ><!--onclick="monthlycardupdateTabState(this)"-->
    <div class="card-header " role="tab" id="headingTwo1">
      <a class="collapsed" >
        <div class="card-header card-header-icon card-header-info" id="cardHeaderPosition">
        <div class="card-icon" id="cardIcon">
          <i class="fa fa-mobile" id="materialIcons"></i>
          </div> 
         <h4 class="card-title">Block Phone Number<i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwelve"
          aria-expanded="false" aria-controls="collapseTwelve" class="fa fa-angle-down rotate-icon"></i>
           <label class="switch" style="float: right; display: none">
                <input type="checkbox" id="onPhoneNoBlocking" name="onPhoneNoBlocking" onclick="updateConfig(this);">
                <span class="slider round"></span>
              </label>
          </h4>
          </div>
        <!-- <h5 class="mb-0">
        <strong>Monthly Transaction</strong> <i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapseSix"
        aria-expanded="false" aria-controls="collapseSix" class="fa fa-angle-down rotate-icon"></i>
        </h5> -->
      </a>
      </div>
      <div id="collapseTwelve" class="collapse active " role="tabpanel" >
<div class="card-body">
<div class="row">
<div class="col-md-12 col-xs-12">
  
    <div class="cards">
      <div class="card-header card-header-icon card-header-info">
      <!-- <div class="card-icon" id="cardIcon">
        <i class="material-icons" id="materialIcons">multiline_chart</i>
      </div> -->
      <!-- <h4 class="card-title">Monthly Transaction
       
      </h4><br>
      <br> -->
      <div class="col-md-12" style="align-items: flex-end;">
        <div class="bkn">
          
          <div class="adduT row">
               <table>
              <tr class="fw-bold fs-6 text-gray-800">
              <!-- <td id="emailListBodyMsg" style="display:block">No blocked Email Addresses</td>
              <td style="width:6%"><div class="adduT row" style="text-align: right; padding: 14px 0 0 0;"> -->
                <div class="col-md-2">
              <input type="submit" name="remittSubmit" disabled="disabled" value="Add Rule" onclick="tagsAddRule()"  id="popupButton12" class="btn btn-primary btn-md" 
              data-toggle="modal" data-target="#myModal12"/>
                </div>
                <div class="col-md-3" style="margin-top: 10px;">
                  <input   type='search' class="form-control form-control-solid form-control form-control-solid-solid" id="search_phoneListBody" onkeypress="if(event.keyCode === 32)return false;" placeholder="Search Phone Number">
                </div>
                <div class="col-md-2">
                <input class='btn btn-primary btn-md' id="searchResult5"   type='submit' value='Search' onclick="searchData('phoneListBody','BLOCK_PHONE_NUMBER')">
                </div>
                <div class="col-md-2">
                  <input class='btn btn-primary btn-md bulkDeleteBtn disabled' type='submit' name="deleteRules" value='Bulk Delete' onclick="bulkDeleteFraudRule()">
                </div>
                <!-- <div class="col-md-3" style="margin-top: 10px;">
                <input   type='search' class="input-control" id="search_phoneListBody" placeholder="Search to search">
              </div>
              <div class="col-md-2">
              <input class='btn btn-primary btn-md' id="searchResult"  type='submit' value='Search' onclick="searchData('phoneListBody','BLOCK_PHONE_NUMBER')">
              </div>
              <div class="col-md-2">
                <input class='btn btn-primary btn-md bulkDeleteBtn'  type='submit' value='Bulk Delete' onclick="bulkDeleteFraudRule('phoneListBody','BLOCK_PHONE_NUMBER')">
              </div> -->
            </div></td>
              </tr>
            </table>
          </div>
          
          <div>
             <!-- <p class="accordion_head"><b>Blocked Email Address List</b><span class="plusminus">+</span></p> -->
             <div class="scrollD" >
            <table id="phoneListBody" class="display table table-striped table-row-bordered gy-5 gs-7" align="center" cellspacing="0" width="100%" style="text-align:center;">
              <thead>
                <tr class="boxheadingsmall fw-bold fs-6 text-gray-800" style="font-size: 11px;">
                  <th style='text-align: center'>Merchant</th>
                  <th style='text-align: center'>Phone Number</th>
                  <!-- <th style='text-align: center'>Name</th> -->
                  <th style='text-align: center'>Start Date</th>
                  <th style='text-align: center'>End Date</th>
                  <th style='text-align: center'>Start Time</th>
                  <th style='text-align: center'>End Time</th>
                  <th style='text-align: center'>Weeks</th>
                  <th style='text-align: center'>Edit</th>
                        <th style='text-align: center'>Delete</th>
                  <th style='text-align: center'> <input class="form-check-input" type="checkbox" id="select_all9" style="margin-right:10px;" value="" onclick="selectall()">
                    <span class="form-check-sign">
                      <span class="check"></span>Action/Select All 
                  
                    </span>
                  </th>
                <!-- <th style='text-align: center'></th> -->
                  
                </tr>
              </thead>
            </table>
          </div>
          <div class="card ">
            <div class="card-header card-header-rose card-header-text">
              <div class="card-text">
              <h4 class="card-title">Add Bulk Rules for Phone Number</h4>
              </div>
            </div>
            <div class="card-body ">
          <div class="container">
            <div class="row it">
            <div class="col-sm-offset-1 col-sm-10" id="one">
            <p>
            Please upload documents only in 'CSV' format.
            </p><br>
            <div class="row">
              <div class="col-sm-offset-4 col-sm-4 form-group">
              
              </div><!--form-group-->
            </div><!--row-->
            <div id="uploader">
            <div class="row uploadDoc">
              <div class="col-sm-4 ">
                <table
                  id="example" class="csv7" style="display: none;">
                  <thead>
                    <tr class="fw-bold fs-6 text-gray-800">
                      <th>Merchant</th>
                      <th>Phone Number</th>
                      <!-- <th>Name</th> -->
                      <th>Start Date</th>
                      <th>End Date</th>
                      <th>Start Time</th>
                      <th>End Time</th>
                      <th>Week Days</th>
                        <th>Always On</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr class="fw-bold fs-6 text-gray-800">
                      <td>1022291114152030</td>
                    <td>9988998899</td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td>TRUE</td>
                    </tr>

                    <tr class="fw-bold fs-6 text-gray-800">
                      <td>1022291114152030</td>
                    <td>9988998899</td>
                    <td>13/09/2020</td>
                    <td>25/09/2020</td>
                    <td>11:51:41</td>
                    <td>11:51:42</td>
                    <td>MON TUE WED </td>
                    <td>FALSE</td>
                    </tr>
                  </tbody>
                </table></div>
              <div class="col-sm-6">
              <div class="docErr">Please upload valid file</div><!--error-->
              <div class="fileUpload btn btn-orange">
                <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
                <span class="upl" id="upload">Upload document</span>
                <form enctype="multipart/form-data" method="post" id="phoneBulkUpload" >
                <div style="display:flex">                      <!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
            
              <!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
              <input type="file" name="fileName" class="upload up" id="up" onchange="readURL(this);" />
              <input type="submit" class="btn btn-primary btn-xs"  disabled id="btnBulkUpload" name="btnBulkUpload" onclick="bulkUpload('BLOCK_PHONE_NUMBER','phoneBulkUpload')"> 
                </div>
            </form>
                <!-- <input type="file" class="upload up" id="up" onchange="readURL(this);" /> -->
              </div><!-- btn-orange -->
              </div><!-- col-3 -->
              <p>Note: If Alwayss True Flag is checked then Date, Time, Week does not need to be filled</p> but if it is not then all the required fields will have to be filled</p>
            
            </div><!--row-->
            </div><!--uploader-->
            <div class="text-center">
            <!-- <a class="btn btn-new"><i class="fa fa-plus"></i> Add new</a> -->
            <!-- <button type="button" id="btnBulkUpload" onclick="bulkUpload()"><a class="btn btn-next"><i class="fa fa-upload"></i> Bulk Upload</a></button> -->
            </div>
            </div><!--one-->
            </div><!-- row -->
            </div>
            </div></div>

             <!-- <div  class=" dataTable no-footer ListBody" aria-describedby="invoiceDataTable_info" role="grid" style="display:none;overflow:inherit !important;" >
            <input type="text" id="search" placeholder="Type to search">
          <table id="emailListBody">
              <tbody>
              
              </tbody>
            </table>
          </div> -->
          
            
        </div>
    </div>
      </div>
      </div>
      
    </div>
    

</div> 
</div>
</div>
      </div>
        </div></div></div></div>  
  </div>

  <div>
    <!--begin::Container-->
    <div>
      <div class="row my-5">
        <div class="col">
  <div class="card card_graph"   id="cardcb11"  ><!--onclick="monthlycardupdateTabState(this)"-->
    <div class="card-header " role="tab" id="headingTwo1">
      <a class="collapsed" >
        <div class="card-header card-header-icon card-header-rose" id="cardHeaderPosition">
        <div class="card-icon" id="cardIcon">
          <i class="fa fa-inr"></i>
          </div> 
         <h4 class="card-title">Transaction Monitoring<i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapseEleven"
          aria-expanded="false" aria-controls="collapseEleven" class="fa fa-angle-down rotate-icon"></i>
           <label class="switch" style="float: right; display: none">
                <input type="checkbox" id="onTxnAmountVelocityBlocking" name="onTxnAmountVelocityBlocking" onclick="updateConfig(this);">
                <span class="slider round"></span>
              </label>
          </h4>
          </div>
        <!-- <h5 class="mb-0">
        <strong>Monthly Transaction</strong> <i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapseEight"
        aria-expanded="false" aria-controls="collapseEight" class="fa fa-angle-down rotate-icon"></i>
        </h5> -->
      </a>
      </div>
      <div id="collapseEleven" class="collapse active " role="tabpanel" >
<div class="card-body">
<div class="row">
<div class="col-md-12 col-xs-12">
  
    <div class="cards">
      <div class="card-header card-header-icon card-header-info">
      <!-- <div class="card-icon" id="cardIcon">
        <i class="material-icons" id="materialIcons">multiline_chart</i>
      </div> -->
      <!-- <h4 class="card-title">Monthly Transaction
       
      </h4><br>
      <br> -->
      <div class="col-md-12" style="align-items: flex-end;">
        <div class="bkn">
          
            <div class="adduT row">
              <div class="col-md-4">
              <table>
                <tr class="fw-bold fs-6 text-gray-800">
                 <!-- <td id="txnAmountListBodyMsg" style="display:block">No blocked Transactional Amount</td>
                    <td style="width:6%"><div class="adduT row" style="text-align: right; padding: 14px 0 0 0;"> -->
                    <input type="submit" name="remittSubmit" value="Add Rule" onclick="tagsAddRule()" id="popupButton11" class="btn btn-primary btn-md disabled" 
                  data-toggle="modal" data-target="#myModal11"/>
                  <!-- <input class='btn btn-primary btn-md' id="searchResult"  type='submit' value='Search' onclick="searchData('txnAmountListBody','BLOCK_TXN_AMOUNT')"> -->
                    </div></td>
                </tr>
              </table>
            </div>
            </div>
            <div>
              <!-- <p class="accordion_head"><b>Blocked Transactional Amount List</b><span class="plusminus">+</span></p> -->
              <div class="scrollD" >
                <table class="display table table-striped table-row-bordered gy-5 gs-7" id="txnAmountVelocityListBody" align="center" cellspacing="0" width="100%" style="text-align:center;">
                  <thead>
                    <tr class="boxheadingsmall fw-bold fs-6 text-gray-800" style="font-size: 11px;">
                      <th style='text-align: center'>Merchant</th>
                      <th style='text-align: center'>Currency</th>
                      <th style='text-align: center'>Transaction Amount</th>
                      <th style='text-align: center'>Transaction Count</th>
                      <th style='text-align: center'>User Identifier</th>
                      <th style='text-align: center'>Type</th>
                      <th style='text-align: center'>Status</th>
                      <th style='text-align: center'>Notify emails</th>
                      <th style='text-align: center'>Time Durations</th>
                      <th style='text-align: center'>Edit</th>
                        <th style='text-align: center'>Delete</th>
                    </tr>
                  </thead>
                </table>
              </div>

              

              <!-- <table id="txnAmountListBody" class="display dataTable no-footer ListBody" aria-describedby="invoiceDataTable_info" role="grid" style="display:none;">
              <tbody>

             </tbody>
              </table> -->
            </div>
            <div class="clear"></div>
        </div>
      </div>
      </div>
      
    </div>
    

</div> 
</div>
</div>
      </div> 
        </div></div></div></div> 
  </div>

  <div>
    <!--begin::Container-->
    <div>
      <div class="row my-5">
        <div class="col">
  <div class="card card_graph"   id="cardcb19"  ><!--onclick="monthlycardupdateTabState(this)"-->
    <div class="card-header " role="tab" id="headingTwo1">
      <a class="collapsed" >
        <div class="card-header card-header-icon card-header-rose" id="cardHeaderPosition">
        <div class="card-icon" id="cardIcon">
          <i class="fa fa-inr"></i>
          </div> 
         <h4 class="card-title">Repeated Mop Type Blocking<i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapse19"
          aria-expanded="false" aria-controls="collapse19" class="fa fa-angle-down rotate-icon"></i>
           <label class="switch" style="float: right; display: none">
                <input type="checkbox" id="onBlockRepeatedMopTypeForSameDetails" name="onBlockRepeatedMopTypeForSameDetails" onclick="updateConfig(this);">
                <span class="slider round"></span>
              </label>
          </h4>
          </div>
      </a>
      </div>
      <div id="collapse19" class="collapse active " role="tabpanel" >
<div class="card-body">
<div class="row">
<div class="col-md-12 col-xs-12">
  
    <div class="cards">
      <div class="card-header card-header-icon card-header-info row">
      <div class="col-md-12" style="align-items: flex-end;">
        <div class="bkn">
          
            <div class="adduT row">
              <div class="col-md-3">
              <table>
                <tr class="fw-bold fs-6 text-gray-800">
                    <input type="submit" name="remittSubmit" value="Add Rule" onclick="tagsAddRule()" id="popupButton11" class="btn btn-primary btn-md disabled" 
                  data-toggle="modal" data-target="#myModal19"/>
                    </div></td>
                </tr>
              </table>
            </div>
            </div>
            <div>
              <div class="table-responsive dataTables_wrapper dt-bootstrap4 no-footer" >
                <table class="display table table-striped table-row-bordered gy-5 gs-7" id="repetedMopTypeListBody" align="center" cellspacing="0" width="100%">
                  <thead>
                    <tr class="boxheadingsmall fw-bold fs-6 text-gray-800" style="font-size: 11px;">
                      <th style='text-align: center'>Merchant</th>
                      <th style='text-align: center'>Time Durations</th>
                      <th style='text-align: center'>Type</th>
                      <th style='text-align: center'>Transaction Count</th>
                      <th style='text-align: center'>Notify emails</th>
                      <th style='text-align: center'>Edit</th>
                      <th style='text-align: center'>Delete</th>
                    </tr>
                  </thead>
                </table>
              </div>
            </div>
            <div class="clear"></div>
        </div>
      </div>
      </div>
      
    </div>
    

</div> 
</div>
</div>
      </div>
      
        </div></div></div></div>

  </div>

  <div>
    <!--begin::Container-->
    <div>
      <div class="row my-5">
        <div class="col">
 <%--  <div class="card card_graph" style="display:none;"  id="cardcb1" ><!--onclick="monthlycardupdateTabState(this)"-->
    <div class="card-header " role="tab" id="headingTwo4">
      <a class="collapsed" >
        <div class="card-header card-header-icon card-header-danger" id="cardHeaderPosition">
        <div class="card-icon" id="cardIcon">
          <i class="material-icons" id="materialIcons">block</i>
          </div> 
         <h4 class="card-title">Block Mack Addresses<i   data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo4"
          aria-expanded="false" aria-controls="collapseTwo4" class="fa fa-angle-down rotate-icon"></i>
            <label class="switch" style="float: right; display: none">
                <input type="checkbox" id="onMacBlocking" name="onMacBlocking" onclick="updateConfig(this);">
                <span class="slider round"></span>
              </label>
          </h4>
          </div>
      </a>
      </div>
        </div></div></div></div>


        <div>
          <!--begin::Container-->
          <div>
            <div class="row my-5">
              <div class="col">
      <div id="collapseTwo4" class="collapse active " role="tabpanel" >
      <div class="card-body">
        <div class="row">
          <div class="col-md-12 col-xs-12">
            <div class="cards">
              <div class="card-header card-header-icon card-header-info">
              <div class="col-md-12" style="align-items: flex-end;">
                <div class="bkn">
                  <div class="adduT row">
                    <table>
                      <tr class="fw-bold fs-6 text-gray-800">
                      <div class="col-md-2">
                        <input type="submit" name="remittSubmit" disabled="disabled" value="Add Rule" onclick="tagsAddRule()"  id="popupButton" class="btn btn-primary btn-md" 
                        data-toggle="modal" data-target="#myModal14"/>
                      </div>
                      <div class="col-md-3" style="margin-top: 10px;">
                        <input   type='search' class="input-control " id="search_mackAddListBody" onkeypress="if(event.keyCode === 32)return false;" placeholder="Search MACK Address">
                      </div>
                      <div class="col-md-2">
                        <input class='btn btn-primary btn-md' id="searchResult"   type='submit' value='Search' onclick="searchData('mackAddListBody','BLOCK_MACK_ADDRESS')">
                      
                      </div>
                      <div class="col-md-2">
                        <input class='btn btn-primary btn-md bulkDeleteBtn disabled' type='submit' name="deleteRules" value='Bulk Delete' onclick="bulkDeleteFraudRule('mackAddListBody','BLOCK_MACK_ADDRESS')">
                      </div>
                      </tr>
                    </table>
                  </div>
                  <div>
                    <div class="scrollD" style="overflow-x: scroll!important;">
                      <table id="mackAddListBody" class="display" align="center" cellspacing="0" width="100%" style="text-align:center;">
                        <thead>
                          <tr class="boxheadingsmall" style="font-size: 11px;">
                            <th style='text-align: center'>Merchant</th>
                            <th style='text-align: center'>Mack Address</th>
                            <!-- <th style='text-align: center'>Name</th> -->
                            <th style='text-align: center'>Start Date</th>
                            <th style='text-align: center'>End Date</th>
                            <th style='text-align: center'>Start Time</th>
                            <th style='text-align: center'>End Time</th>
                            <th style='text-align: center'>Weeks</th>
                            <th style='text-align: center'>Action</th>
                            <th style='text-align: center'> <input class="form-check-input" type="checkbox" id="select_all14" style="margin-right:10px;" value="" onclick="selectall()">
                              <span class="form-check-sign">
                                <span class="check"></span>Action/Select All 
                            
                              </span></th>
                            
                          </tr>
                        </thead>
                      </table>
                    </div>
                    <div class="card ">
                      <div class="card-header card-header-rose card-header-text">
                        <div class="card-text">
                        <h4 class="card-title">Add Bulk Rules for Mack Address</h4>
                        </div>
                      </div>
                      <div class="card-body ">
                        <div class="container">
                          <div class="row it">
                            <div class="col-sm-offset-1 col-sm-10" id="fourteen">
                              <p>
                              Please upload documents only in 'CSV' format.
                              </p><br>
                              <div class="row">
                                <div class="col-sm-offset-4 col-sm-4 form-group">
                                </div><!--form-group-->
                              </div><!--row-->
                              <div id="uploader">
                                <div class="row uploadDoc">
                                  <div class="col-sm-4 ">
                                    <table
                                      id="example" class="csv9" style="display: none;">
                                      <thead>
                                        <tr class="fw-bold fs-6 text-gray-800">
                                          <th>Merchant</th>
                                          <td>Mack Address</td>
                                          <!-- <th>Name</th> -->
                                          <td>Start Date</td>
                                          <td>End Date</td>
                                          <td>Start Time</td>
                                          <td>End Time</td>
                                          <td>Week Days</td>
                                          <td>Always On</td>
                                        </tr>
                                      </thead>
                                      <tbody>
                                        <tr class="fw-bold fs-6 text-gray-800">
                                          <td>1022291114152030</td>
                                          <td>00:00:5e:00:53:af</td>
                                          <td></td>
                                          <td></td>
                                          <td></td>
                                          <td></td>
                                          <td></td>
                                          <td>TRUE</td>
                                        </tr>
                                        <tr class="fw-bold fs-6 text-gray-800">
                                          <td>1022291114152030</td>
                                          <td>00:1B:44:11:3A:B7</td>
                                          <td>13/09/2020</td>
                                          <td>25/09/2020</td>
                                          <td>11:51:41</td>
                                          <td>11:51:42</td>
                                          <td>MON TUE WED </td>
                                          <td>FALSE</td>
                                        </tr>
                                      </tbody>
                                    </table>
                                  </div>
                                    <div class="col-sm-6">
                                    <div class="docErr">Please upload valid file</div><!--error-->
                                    <div class="fileUpload btn btn-orange">
                                      <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
                                      <span class="upl" id="upload">Upload document</span>
                                      
                                      <form enctype="multipart/form-data" method="post" id="mackBulkUpload" >
                                        <div style="display:flex">                      <!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
                                          <input type="file" name="fileName" class="upload up" id="up" onchange="readURL(this);" />
                                          <input type="submit" class="btn btn-primary btn-xs"  id="btnBulkUpload" name="btnBulkUpload" disabled onclick="bulkUpload('BLOCK_MACK_ADDRESS','mackBulkUpload')"> 
                                        </div>
                                      </form>
                                    </div><!-- btn-orange -->
                                  </div><!-- col-3 -->
                                  <p>Note: If Alwayss True Flag is checked then Date, Time, Week does not need to be filled</p> but if it is not then all the required fields will have to be filled</p>
                                </div><!--row-->
                              </div><!--uploader-->
                              <div class="text-center">
                            </div>
                          </div><!--one-->
                        </div><!-- row -->
                      </div>
                    </div>
                  </div>
                </div>
                <div class="clear"></div>
                </div>
              </div>
              </div>
            </div>
          </div> 
        </div>
      </div>
      </div> 
              </div></div></div></div>
    </div> --%>
    <%-- <div class="card card_graph"   id="cardcb15" ><!--onclick="monthlycardupdateTabState(this)"-->
    
      <div>
        <!--begin::Container-->
        <div>
          <div class="row my-5">
            <div class="col">
      <div class="card-header " role="tab" id="headingTwo5">
      <a class="collapsed" >
        <div class="card-header card-header-icon card-header-danger" id="cardHeaderPosition">
        <div class="card-icon" id="cardIcon">
          <i class="material-icons" id="materialIcons">block</i>
          </div> 
         <h4 class="card-title">Repeated MOP Types<i   data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo5"
          aria-expanded="false" aria-controls="collapseTwo5" class="fa fa-angle-down rotate-icon"></i>
            <label class="switch" style="float: right; display: none">
                <input type="checkbox" id="onNotifyRepeatedMopType" name="onNotifyRepeatedMopType" onclick="updateConfig(this);">
                <span class="slider round"></span>
              </label>
          </h4>
          </div>
      </a>
      </div>
            </div></div></div></div>

            <div>
              <!--begin::Container-->
              <div>
                <div class="row my-5">
                  <div class="col">      
      <div id="collapseTwo5" class="collapse active " role="tabpanel" >
      <div class="card-body">
        <div class="row">
          <div class="col-md-12 col-xs-12">
            <div class="cards">
              <div class="card-header card-header-icon card-header-info">
              <div class="col-md-12" style="align-items: flex-end;">
                <div class="bkn">
                  <div class="adduT row">
                    <table>
                      <tr class="fw-bold fs-6 text-gray-800">
                      <div class="col-md-2">
                        <input type="submit" name="remittSubmit" disabled="disabled" value="Add Rule" onclick="tagsAddRule()"  id="popupButton" class="btn btn-primary btn-md" 
                        data-toggle="modal" data-target="#myModal15"/>
                      </div>
                      <div class="col-md-3" style="margin-top: 10px;">
                        <input   type='search' class="input-control " id="search_repeatedMopTypeListBody" onkeypress="if(event.keyCode === 32)return false;" placeholder="Search Repeated Mop Types">
                      </div>
                      <div class="col-md-2">
                        <input class='btn btn-primary btn-md' id="searchResult"   type='submit' value='Search' onclick="searchData('repeatedMopTypeListBody','REPEATED_MOP_TYPES')">
                      
                      </div>
                      <div class="col-md-2">
                        <input class='btn btn-primary btn-md bulkDeleteBtn disabled' type='submit' name="deleteRules" value='Bulk Delete' onclick="bulkDeleteFraudRule('repeatedMopTypeListBody','REPEATED_MOP_TYPES')">
                      </div>
                      </tr>
                    </table>
                  </div>
                  <div>
                    <div class="scrollD" style="overflow-x: scroll!important;">
                      <table id="repeatedMopTypeListBody" class="display" align="center" cellspacing="0" width="100%" style="text-align:center;">
                        <thead>
                          <tr class="boxheadingsmall" style="font-size: 11px;">
                            <th style='text-align: center'>Merchant</th>
                            <th style='text-align: center'>Payment Type</th>
                            <th style='text-align: center'>Emails</th>
                            <th style='text-align: center'>Time Duration</th>
                            <th style='text-align: center'>Percentage</th>
                            <th style='text-align: center'>Action</th>
                            <th style='text-align: center'> <input class="form-check-input" type="checkbox" id="select_all15" style="margin-right:10px;" value="" onclick="selectall()">
                              <span class="form-check-sign">
                                <span class="check"></span>Action/Select All 
                            
                              </span></th>
                            
                          </tr>
                        </thead>
                      </table>
                    </div>
                    <div class="card ">
                      <div class="card-header card-header-rose card-header-text">
                        <div class="card-text">
                        <h4 class="card-title">Add Bulk Rules for Repeated MOP Types</h4>
                        </div>
                      </div>
                      <div class="card-body ">
                        <div class="container">
                          <div class="row it">
                            <div class="col-sm-offset-1 col-sm-10" id="fourteen">
                              <p>
                              Please upload documents only in 'CSV' format.
                              </p><br>
                              <div class="row">
                                <div class="col-sm-offset-4 col-sm-4 form-group">
                                </div><!--form-group-->
                              </div><!--row-->
                              <div id="uploader">
                                <div class="row uploadDoc">
                                  <div class="col-sm-4 ">
                                    <table
                                      id="example" class="csv10" style="display: none;">
                                      <thead>
                                        <tr class="fw-bold fs-6 text-gray-800">
                                          <th>Merchant</th>
                                          <td>Payment Type</td>
                                          <td>Email To Notify</td>
                                          <td>Time Duration</td>
                                          <td>Percentage</td>
                                        </tr>
                                      </thead>
                                      <tbody>
                                        <tr class="fw-bold fs-6 text-gray-800">
                                          <td>1022291114152030</td>
                                          <td>CC</td>
                                          <td>test@gmail.com</td>
                                          <td>Monthly</td>
                                          <td>25</td>
                                        </tr>
                                        <tr class="fw-bold fs-6 text-gray-800">
                                          <td>1022291114152030</td>
                                          <td>UP</td>
                                          <td>test@gmail.com:test1@gmail.com</td>
                                          <td>Weekly</td>
                                          <td>35</td>
                                        </tr>
                                      </tbody>
                                    </table>
                                  </div>
                                    <div class="col-sm-6">
                                    <div class="docErr">Please upload valid file</div><!--error-->
                                    <div class="fileUpload btn btn-orange">
                                      <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
                                      <span class="upl" id="upload">Upload document</span>
                                      
                                      <form enctype="multipart/form-data" method="post" id="repeatedMopTypesBulkUpload" >
                                        <div style="display:flex">                      <!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
                                          <input type="file" name="fileName" class="upload up" id="up" onchange="readURL(this);" />
                                          <input type="submit" class="btn btn-primary btn-xs"  id="btnBulkUpload" name="btnBulkUpload" disabled onclick="bulkUpload('REPEATED_MOP_TYPES','repeatedMopTypesBulkUpload')"> 
                                        </div>
                                      </form>
                                    </div><!-- btn-orange -->
                                  </div><!-- col-3 -->
                                  <p>Note: If Alwayss True Flag is checked then Date, Time, Week does not need to be filled</p> but if it is not then all the required fields will have to be filled</p>
                                </div><!--row-->
                              </div><!--uploader-->
                              <div class="text-center">
                            </div>
                          </div><!--one-->
                        </div><!-- row -->
                      </div>
                    </div>
                  </div>
                </div>
                <div class="clear"></div>
                </div>
              </div>
              </div>
            </div>
          </div> 
        </div>
      </div>
      </div> 
                  </div></div></div></div>
    </div> --%>
    
    <!-- First Transaction Alert -->
    <div>
      <!--begin::Container-->
      <div>
        <div class="row my-5">
          <div class="col">
    <div class="card card_graph"   id="cardcb16"  ><!--onclick="monthlycardupdateTabState(this)"-->
    <div class="card-header " role="tab" id="headingTwo1">
      <a class="collapsed" >
        <div class="card-header card-header-icon card-header-rose" id="cardHeaderPosition">
        <div class="card-icon" id="cardIcon">
          <i class="fa fa-inr"></i>
          </div> 
         <h4 class="card-title">First Transaction Alert<i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo6"
          aria-expanded="false" aria-controls="collapseTwo6" onclick="javascript: $('#payIdFTxn').val($('#payId').val())" class="fa fa-angle-down rotate-icon"></i>
          </h4>
          </div>
        <!-- <h5 class="mb-0">
        <strong>Monthly Transaction</strong> <i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapseEight"
        aria-expanded="false" aria-controls="collapseEight" class="fa fa-angle-down rotate-icon"></i>
        </h5> -->
      </a>
      </div>
      <div id="collapseTwo6" class="collapse active " role="tabpanel" >
<div class="card-body">
<div class="row">
<div class="col-md-12 col-xs-12">
  
    <div class="cards">
      <div class="card-header card-header-icon card-header-info">
      <!-- <div class="card-icon" id="cardIcon">
        <i class="material-icons" id="materialIcons">multiline_chart</i>
      </div> -->
      <!-- <h4 class="card-title">Monthly Transaction
       
      </h4><br>
      <br> -->
      <div class="col-md-12" style="align-items: flex-end;">
        <div class="bkn">
            <div>
              <!-- <p class="accordion_head"><b>Blocked Transactional Amount List</b><span class="plusminus">+</span></p> -->
              <div class="scrollD row">
                <table class="display table table-striped table-row-bordered gy-5 gs-7" id="txnAmountVelocityListBody" align="center" cellspacing="0" width="100%" style="text-align:center;">
                  <thead>
                    <tr class="boxheadingsmall fw-bold fs-6 text-gray-800" style="font-size: 11px;">
                      <td>Merchant</td>
                      <td>Amount</td>
                      <td>Notify Emails</td>
                      <td></td>
                    </tr>
                    <tr class="boxheadingsmall" style="font-size: 11px;">
                      <td><input type="text" disabled="disabled" name="payId"placeholder="Merchant Pay Id" id="payIdFTxn" /></td>
                      <td width="20%"><input type="number" id="transactionAmountFTxn" placeholder="10" onpaste="return false" name="transactionAmount" min="" max="9999999999.99" step="0.01" onkeypress="return isNumberKeyAmount(event)" class="form-control form-control-solid"/></td>
                      
                        <td><input id="emailToNotifyFTxn" type="email" name="emailToNotify" style="width: 100% !important;" required placeholder="user@domain.xyz" class="form-control" data-role="tagsinput" /></td>
                      
                      <td width="20%">
                      <div style="display: flex;">
                        <button type="submit" value="Notify" id="notifyRepeatedMopType"
                          onclick="saveNotifyFTxn(notifyFirstTransactions)" class="btn btn-primary btn-block ftxnbtn">Save</button>
                        </div>
                      </td>
                    </tr>
                  </thead>
                </table>
              </div>
            </div>
            <div class="clear"></div>
              </div>
              </div>
             </div>
      
            </div>
          </div> 
        </div>
      </div>
    </div>  
  </div>
          </div></div></div></div>
          
          <!-- Block Mop Limit -->
          
          
          
  <div>
    <!--begin::Container-->
    <div>
      <div class="row my-5">
        <div class="col">
  <div class="card card_graph"   id="cardcb26"  ><!--onclick="monthlycardupdateTabState(this)"-->
    <div class="card-header " role="tab" id="headingTwo1">
      <a class="collapsed" >
        <div class="card-header card-header-icon card-header-rose" id="cardHeaderPosition">
        <div class="card-icon" id="cardIcon">
          <i class="fa fa-inr"></i>
          </div> 
         <h4 class="card-title">Mop Limit Blocking<i  data-toggle="collapse" data-parent="#accordionEx1" href="#collapse26"
          aria-expanded="false" aria-controls="#collapse26" class="fa fa-angle-down rotate-icon"></i>
           <label class="switch" style="float: right; display: none">
                <span class="slider round"></span>
              </label>
          </h4>
          </div>
      </a>
      </div>
      <div id="collapse26" class="collapse active " role="tabpanel" >
<div class="card-body">
<div class="row">
<div class="col-md-12 col-xs-12">
  
    <div class="cards">
      <div class="card-header card-header-icon card-header-info row">
      <div class="col-md-12" style="align-items: flex-end;">
        <div class="bkn">
          
           <!--  <div class="adduT row">
              <div class="col-md-3">
              <table>
                <tr class="fw-bold fs-6 text-gray-800">
                    <input type="submit" name="remittSubmit" value="Add Rule" onclick="tagsAddRule()" id="popupButton11" class="btn btn-primary btn-md disabled" 
                  data-toggle="modal" data-target="#myModal19"/>
                    </div></td>
                </tr>
              </table>
            </div>
            </div> -->
            <div>
              <div class="table-responsive dataTables_wrapper dt-bootstrap4 no-footer" >
                <table class="display table table-striped table-row-bordered gy-5 gs-7" id="MopLimitListBody" align="center" cellspacing="0" width="100%">
                  <thead>
                    <tr class="boxheadingsmall fw-bold fs-6 text-gray-800" style="font-size: 11px;">
                      <th style='text-align: center'>Mop Type</th>
                      <th style='text-align: center'>Daily </th>
                      <th style='text-align: center'>Weekly</th>
                      <th style='text-align: center'>Monthly</th>
                      <th style='text-align: center'>Daily Amount</th>
                      <th style='text-align: center'>Weekly Amount</th>
                      <th style='text-align: center'>Monthly Amount</th>
                      <th style='text-align: center'>Edit</th>
                    </tr>
                  </thead>
                </table>
              </div>
            </div>
            <div class="clear"></div>
        </div>
      </div>
      </div>
      
    </div>
    

</div> 
</div>
</div>
      </div>
      
        </div></div></div></div>

  </div>
  
  <!-- End Block Mop Limit -->
          
    
  </div>
  </div>
  </div>
  </div>
  </div>
  
<script>
$(function () {
   $('#myModal').on('hidden.bs.modal', function () {
      $('.modal-body').find('input, label, ul, li, span').val('');
      document.getElementById("validate_err").style.display = "none";
      document.getElementById("validate_err1").style.display = "none";
      
      //To remove checked week days
       $('.ip4ClassWeeks').each(function() { $(this).prop('checked', false) });
  });
  
  $('#myModal2').on('hidden.bs.modal', function () {
      $('.modal-body').find('ul, li').val('');
      //To remove checked countries
       $('.issuerClass').each(function() { $(this).prop('checked', false) });
  });
  
  $('#myModal3').on('hidden.bs.modal', function () {
      $('.modal-body').find('input, label, ul, li, span').val('');
      document.getElementById("validate_crd").style.display = "none";
      
      //To remove checked week days
       $('.binRangeClass').each(function() { $(this).prop('checked', false) });
  });
  
  $('#myModal4').on('hidden.bs.modal', function () {
      $('.modal-body').find('input, label, ul, li, span').val('');
      //To remove checked countries 
       $('.mackClassWeeks').each(function() { $(this).prop('checked', false) });
  });

  $('#myModal17').on('hidden.bs.modal', function () {
      $('.modal-body').find('input, label, ul, li, span, select').val('');
      document.getElementById("stateCodeUl").innerHTML = "";
  });

  $('#myModal18').on('hidden.bs.modal', function () {
      $('.modal-body').find('input, label, ul, li, span, select').val('');
      document.getElementById("cityUl").innerHTML = "";
  });
  
  $('#myModal5').on('hidden.bs.modal', function () {
      $('.modal-body').find('input, label, ul, li, span').val('');
      document.getElementById("validate_email").style.display = "none";
      
      //To remove checked week days
       $('.emailAddClass').each(function() { $(this).prop('checked', false) });
  });
  
  $('#myModal7').on('hidden.bs.modal', function () {
      $('.modal-body').find('input, span').val('');
  });
  
  $('#myModal8').on('hidden.bs.modal', function () {
      $('.modal-body').find('input, select').val('');
  });
  
  $('#myModal9').on('hidden.bs.modal', function () {
      $('.modal-body').find('input, label, ul, li, span').val('');
      document.getElementById("validate_crdIn").style.display = "none";
      document.getElementById("validate_crdL").style.display = "none";
      
      //To remove checked week days
       $('.blockCardClass').each(function() { $(this).prop('checked', false) });
  });
  
  $('#myModal0').on('hidden.bs.modal', function () {
      $('.modal-body').find('input, label, ul, li, span').val('');
      document.getElementById("validate_crdInpre").style.display = "none";
      document.getElementById("validate_crdLpre").style.display = "none";
      
      //To remove checked week days
       $('.limitNoClass').each(function() { $(this).prop('checked', false) });
  });
  $('#myModal11').on('hidden.bs.modal', function () {
      $('.modal-body').find('input, select').val('');
      $('.modal-body').find('input, check').val('');
    });
    $('#myModal12').on('hidden.bs.modal', function () {
      $('.modal-body').find('input, label, ul, li, span').val('');
      document.getElementById("validate_phone").style.display = "none";

      //To remove checked week days
      $('.phoneAddClass').each(function () { $(this).prop('checked', false) });
    });
  $('#myModal14').on('hidden.bs.modal', function () {
      $('.modal-body').find('input, label, ul, li, span').val('');
      document.getElementById("validate_err").style.display = "none";
      document.getElementById("validate_err1").style.display = "none";
      
      //To remove checked week days
       $('.ip4ClassWeeks').each(function() { $(this).prop('checked', false) });
  });
  $('#myModal15').on('hidden.bs.modal', function () {
    $('.modal-body').find('input, label, ul, li, span').val('');
    document.getElementById("validate_err").style.display = "none";
    document.getElementById("validate_err1").style.display = "none";
  });

  $('#myModal19').on('hidden.bs.modal', function () {
      $('.modal-body').find('input, select').val('');
    });
  
}); 

</script>

<!------Validation to check min and max values in Transaction Amount(IP-40)-----> 
<script>
function checkTransactionVal(){
  var minimumAmt = document.getElementById("minTransactionAmount").value;
  var maximumAmt = document.getElementById("maxTransactionAmount").value;
  
  var minimumInt = parseInt(minimumAmt);
  var maximumInt = parseInt(maximumAmt);

  if(minimumInt != "" && maximumInt != ""){
    if(minimumInt > maximumInt){
      document.getElementById("amountError").style.display = "block";
      return false;
    }else{
      document.getElementById("amountError").style.display = "none";
    }
  }
}
</script>

<!-----------For Logout Dropdown(IP-32)-------------->
<script>
    $('#minTransactionAmount').keypress(function(e){ 
  if (this.value.length == 0 && e.which == 48 ){
   return false;
   }
  });
  $('#maxTransactionAmount').keypress(function(e){ 
  if (this.value.length == 0 && e.which == 48 ){
   return false;
   }
  });
  $('#perCardTransactionAllowed').keypress(function(e){ 
  if (this.value.length == 0 && e.which == 48 ){
   return false;
   }
  });
  $('#txnAmountVelocity').keypress(function(e){ 
  if (this.value.length == 0 && e.which == 48 ){
   return false;
   }
  });
  
$(".dropdown").click(function(){
  var elementVal = document.getElementById("openDropdown");
    if(elementVal.style.display == 'none'){
         elementVal.style.display = 'block';
        }
  else if (elementVal.style.display == ""){
     elementVal.style.display = 'block';
      }
        else{
         elementVal.style.display = 'none';
    }
});
</script>
<!----------To hide logout popup on BLUR------->
<script>
$(document).click(function(e) {
    if (!$(e.target).is("#openDropdown")) {
        if ($('#openDropdown').is(':visible')) {
            document.getElementById("openDropdown").style.display = "none";
        }
    }
    enableBtnAsPerAccess();
});
function enableBtnAsPerAccess() {
  if ($('#adminRestrictions').hasClass("active")) {
    var menuAccess = document.getElementById("menuAccessByROLE").value;
    var accessMap = JSON.parse(menuAccess);
    var access = accessMap["adminRestrictions"];
    if (access.includes("Add")) {
      var addRuleBtns = document.getElementsByName("remittSubmit");
      for (var i=0; i < addRuleBtns.length; i++) {
        var addRuleBtn = addRuleBtns[i];
        addRuleBtn.disabled=false;
        addRuleBtn.classList.remove('disabled');
      }
      
      var addBulkBtns = document.getElementsByName("btnBulkUpload");
      for (var i=0; i < addBulkBtns.length; i++) {
        var addBulkBtn = addBulkBtns[i];
        addBulkBtn.disabled=false;
        addBulkBtn.classList.remove('disabled');
      }
    }
    if (access.includes("Update")) {
        var editRuleBtns = document.getElementsByName("editRules");
        for (var i=0; i < editRuleBtns.length; i++) {
          var editRuleBtn = editRuleBtns[i];
          editRuleBtn.classList.remove('disabled');
          editRuleBtn.disabled=false;
        }
      }
    if (access.includes("Delete")) {
      var deleteRuleBtns = document.getElementsByName("deleteRules");
      for (var i=0; i < deleteRuleBtns.length; i++) {
        var deleteRuleBtn = deleteRuleBtns[i];
        deleteRuleBtn.classList.remove('disabled');
        deleteRuleBtn.disabled=false;
      }
    }
  } 
}
$('#txnAmountVelocityListBody input[type=text]').addClass('form-control form-control-solid form-control form-control-solid-solid');

</script>
<script>
function  download(){
  document.getElementById('uploader').getElementsByTagName('a')[0].click();
  }

</script>


</body>
</html>