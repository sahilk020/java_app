// --------------------------added by vijaylakshmi----------------------------------------------->
function pieChartByAcquirers() {
  var acquirer = document.getElementById("acquirerPT").value; //acquirer.join();
  var dateFrom = document.getElementById("dateFromAcquirerPie").value;
  var dateTo = document.getElementById("dateToAcquirerPie").value;
  //var paymentMethods = "ALL";

  if (acquirer == '') {
    acquirer = 'ALL'
  }

  var urls = new URL(window.location.href);
  var domain = urls.origin;
  
  $.ajax({
      //url: "https://secure.pay10.com/crmws/dashboard/getPichartData",
      //url: "https://uat.pay10.com/crmws/dashboard/getPichartData",
      //url: "http://localhost:8080/crmws/dashboard/getPichartData",
      url : domain + "/crmws/dashboard/getPichartData",
      type: "POST",
      headers:{
      'Accept': 'Application/json',
      'Content-Type': 'application/json'
      },
      data:JSON.stringify({
        dateFrom: ddmm_to_yymm(dateFrom),
        dateTo: ddmm_to_yymm(dateTo),
          acquirer : acquirer
      }),

      success: function (resData) {
        //document.getElementById("loading").style.display = "none";
        var seriesArray = [];
        var labelArray = [];
                if(resData.status === true){
                    for (var i = resData.data.length - 1, response; response = resData.data[i]; i--) {
                      //alert("resData "+response.toString)
                        seriesArray.push(response.txnCount);
            labelArray.push(response.mopType);
                    }
          initAcquirerWisePieChart(seriesArray, labelArray, '#kt_chart_widgets_012_chart', '#kt_chart_widgets_012_chart', true);
        }else{
            alert(resData.message);
        }
        //document.getElementById("loading").style.display = "none"

        },
        error : function(data) {
        //document.getElementById("loading").style.display = "none"
        }
    });

}
function showPopupDataInTable(sectionName) {
  var acquirer = document.getElementById("acquirerPT").value; //acquirer.join();
  var dateFrom = document.getElementById("dateFromPT").value;
  var dateTo = document.getElementById("dateToPT").value;
  //var paymentMethods = "ALL";

  if (acquirer == '') {
    acquirer = 'ALL'
  }
// var  dd = '<%=request.getScheme()%>'+'://'+'<%=request.getServerName()%>'+':'+'<%=request.getServerPort()%>'+'/crmws/dashboard/getPichartData';
 //   alert(dd);
  
  var urls = new URL(window.location.href);
  var domain = urls.origin;
  
  $.ajax({
        //url: "https://secure.pay10.com/crmws/dashboard/getPiChartPopup",
        //url: "https://uat.pay10.com/crmws/dashboard/getPiChartPopup",
        //url: "http://localhost:8080/crmws/dashboard/getPiChartPopup",
        url : domain + "/crmws/dashboard/getPiChartPopup",
      type: "POST",
      headers:{
      'Accept': 'Application/json',
      'Content-Type': 'application/json'
      },
      data:JSON.stringify({
        dateFrom: dateFrom,
        dateTo: dateTo,
          acquirer : acquirer,
          mopType: sectionName
      }),

      success: function (resData) {
        document.getElementById("loading").style.display = "none";
        //var cancel = document.getElementById("btnCancel").style.display= "none";
        var arr = [];
                console.log(resData);
        
                if(resData.status === true)
                {//build and deploy crm.war
                    $('#showData').dataTable({
                    destroy: true,
                    data: resData.data,
                    "columns" : [ {
                                                                            "data" : "txnID",
                                                                              },  {
                                                                                "data" : "pgRefNo",
                                                                              },{
                                                                                "data" : "amount",
                                                                              }, {
                                                                                "data" : "date",

                                                                              },{
                                                                                "data" : "mopType",
                                                                              }, ],

                                                                 });
        }
        $('#acquierPTPopup').show();
        document.getElementById("loading").style.display = "none"

        },
        error : function(data) {
        document.getElementById("loading").style.display = "none"
        }
    });

}
function onPieChartSectionClick(event){
    console.log("Pie chart section clicked");
    console.log(this.name);
//build and deploy and test
    showPopupDataInTable(this.name);

}

function closePopup(){
  $('#acquierPTPopup').hide();
}

function ddmm_to_yymm(date) {
    var dateArr = date.split('-');
    return dateArr[2] + '-' + dateArr[1] + '-' + dateArr[0];
}