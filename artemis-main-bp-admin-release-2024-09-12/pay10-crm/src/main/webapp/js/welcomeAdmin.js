
$(document).ready(function () {

    $(function () {
        loadData();
    });

    $("#buttonWeekly").click(function (env) {
        if(!$("#currency").val()){
            console.log("Currency not selected")
            return;
        }
        var currentDate = new Date();
        var first = currentDate.getDate();
        var last = first - 6;
        var dateTo = new Date(currentDate.setDate(first));
        var dateFrom = new Date(currentDate.setDate(last));
        dateFrom = getFormatedDate(dateFrom);
        dateTo = getFormatedDate(dateTo);
        $("#transactionTypePayment").val("SALE");
        statistics(dateFrom, dateTo);
        printSaleReportDetails(dateFrom, dateTo);
        $("#dateFromSettlement").val(dateFrom);
        $("#dateToSettlement").val(dateTo);
        $("#dateFromFunnel").val(dateFrom);
        $("#dateToFunnel").val(dateTo);
        getSettledData();
        getHitsData();
    });

    $("#buttonMonthly").click(function (env) {
        if(!$("#currency").val()){
            console.log("Currency not selected")
            return;
        }
        var currentDate = new Date();
        var first = currentDate.getDate();
        var last = first - 30;
        var dateTo = new Date(currentDate.setDate(first));
        var dateFrom = new Date(currentDate.setDate(last));
        dateFrom = getFormatedDate(dateFrom);
        dateTo = getFormatedDate(dateTo);
        $("#transactionTypePayment").val("SALE");
        statistics(dateFrom, dateTo);
        printSaleReportDetails(dateFrom, dateTo);
        $("#dateFromSettlement").val(dateFrom);
        $("#dateToSettlement").val(dateTo);
        $("#dateFromFunnel").val(dateFrom);
        $("#dateToFunnel").val(dateTo);
        getSettledData();
        getHitsData();
    });


    $("#buttonYearly").click(function (env) {
        if(!$("#currency").val()){
            console.log("Currency not selected")
            return;
        }
        var currentDate = new Date();
        var first = currentDate.getDate();
        var last = first - 365;
        var dateTo = new Date(currentDate.setDate(first));
        var dateFrom = new Date(currentDate.setDate(last));
        dateFrom = getFormatedDate(dateFrom);
        dateTo = getFormatedDate(dateTo);
        $("#transactionTypePayment").val("SALE");
        statistics(dateFrom, dateTo);
        printSaleReportDetails(dateFrom, dateTo);
        $("#dateFromSettlement").val(dateFrom);
        $("#dateToSettlement").val(dateTo);
        $("#dateFromFunnel").val(dateFrom);
        $("#dateToFunnel").val(dateTo);
        getSettledData();
        getHitsData();
    });

});

function loadData() {
    if(!$("#currency").val()){
        console.log("Currency not selected")
        return;
    }

    var month = new Date().getMonth() + 1;
        month = month < 10 ? "0" + month : month;
        var year = new Date().getFullYear();
        var date = new Date().getDate();
        date = date < 10 ? "0" + date : date;
        var first = year + "-" + month + "-" + date;
        $("#transactionTypePayment").val("SALE");
        statistics(first, first);
        $("#selectDateHourly").val(first);
        $("#dateFromSettlement").val(first);
        $("#dateToSettlement").val(first);
        $("#dateFromFunnel").val(first);
        $("#dateToFunnel").val(first);
        getHourlyData();
        printSaleReportDetails(first, first);
        getSettledData();
        getHitsData();
}

function loadMerchantSpecific() {
    if (document.getElementById("buttonMonthly").classList.contains("active")) {
        document.getElementById("buttonMonthly").click();
    } else if (document.getElementById("buttonYearly").classList.contains("active")) {
        document.getElementById("buttonYearly").click();
    } else if (document.getElementById("buttonWeekly").classList.contains("active")) {
        document.getElementById("buttonWeekly").click();
    } else if (document.getElementById("customRange").classList.contains("active")) {
        loadCustomDateDashboard($("#customRange").val());
    } else {
        loadData();
    }
}

function loadCustomDateDashboard(dateRange) {
    if(!$("#currency").val()){
        console.log("Currency not selected")
        return;
    }
    if (document.getElementById("customRange").style.display == 'none') {
        return;
    }
    dateRange = dateRange.split("-");
    let dateFrom = dateRange[0].replaceAll("/", "-");
    let dateTo = dateRange[1].replaceAll("/", "-");
    dateFrom = mmdd_to_yymm(dateFrom).replace(" ", "");
    dateTo = mmdd_to_yymm(dateTo).replace(" ", "");
    $("#transactionTypePayment").val("SALE");
        statistics(dateFrom, dateTo);
        printSaleReportDetails(dateFrom, dateTo);
        $("#dateFromSettlement").val(dateFrom);
        $("#dateToSettlement").val(dateTo);
        $("#dateFromFunnel").val(dateFrom);
        $("#dateToFunnel").val(dateTo);
        getSettledData();
        getHitsData();
}

function getFormatedDate(date) {
    var date1 = new Date(date);
    var month = date1.getMonth() + 1;
    month = month < 10 ? "0" + month : month;
    var year = date1.getFullYear();
    var date = date1.getDate();
    date = date < 10 ? "0" + date : date;
    var first = year + "-" + month + "-" + date;
    return first;
}

var currentPeriod = "day";
var expanded = false;

function showCheckboxes(e) {
    var checkboxes = document.getElementById("checkboxes");
    if (!expanded) {
        checkboxes.style.display = "block";
        expanded = true;
    } else {
        checkboxes.style.display = "none";
        expanded = false;
    }


    e.stopPropagation();

}
function showCheckboxes1(e) {
    var checkboxes1 = document.getElementById("checkboxes1");
    if (!expanded) {
        checkboxes1.style.display = "block";
        expanded = true;
    } else {
        checkboxes1.style.display = "none";
        expanded = false;
    }


    e.stopPropagation();

}
function showCheckboxes2(e) {
    var checkboxes2 = document.getElementById("checkboxes2");
    if (!expanded) {
        checkboxes2.style.display = "block";
        expanded = true;
    } else {
        checkboxes2.style.display = "none";
        expanded = false;
    }


    e.stopPropagation();

}
function showCheckboxes3(e) {
    var checkboxes3 = document.getElementById("checkboxes3");
    if (!expanded) {
        checkboxes3.style.display = "block";
        expanded = true;
    } else {
        checkboxes3.style.display = "none";
        expanded = false;
    }


    e.stopPropagation();

}

var allSelectedAquirer;
function getCheckBoxValue() {
    var allInputCheckBox = document.getElementsByClassName("myCheckBox");

    var allSelectedAquirer = [];
    for (var i = 0; i < allInputCheckBox.length; i++) {

        if (allInputCheckBox[i].checked) {
            allSelectedAquirer.push(allInputCheckBox[i].value);
        }
    }
    document.getElementById('selectBox').setAttribute('title', allSelectedAquirer.join());

    if (allSelectedAquirer.join().length > 28) {
        var res = allSelectedAquirer.join().substring(0, 27);
        document.querySelector("#selectBox option").innerHTML = res + '...............';
    } else if (allSelectedAquirer.join().length == 0) {
        document.querySelector("#selectBox option").innerHTML = 'ALL';
    } else {
        document.querySelector("#selectBox option").innerHTML = allSelectedAquirer.join();
    }
}
function getCheckBoxValue1() {
    var allInputCheckBox = document.getElementsByClassName("myCheckBox1");

    var allSelectedAquirer = [];
    for (var i = 0; i < allInputCheckBox.length; i++) {

        if (allInputCheckBox[i].checked) {
            allSelectedAquirer.push(allInputCheckBox[i].value);
        }
    }
    document.getElementById('selectBox1').setAttribute('title', allSelectedAquirer.join());

    if (allSelectedAquirer.join().length > 28) {
        var res = allSelectedAquirer.join().substring(0, 27);
        document.querySelector("#selectBox1 option").innerHTML = res + '...............';
    } else if (allSelectedAquirer.join().length == 0) {
        document.querySelector("#selectBox1 option").innerHTML = 'ALL';
    } else {
        document.querySelector("#selectBox1 option").innerHTML = allSelectedAquirer.join();
    }
}
function getCheckBoxValue2() {
    var allInputCheckBox = document.getElementsByClassName("myCheckBox2");

    var allSelectedAquirer = [];
    for (var i = 0; i < allInputCheckBox.length; i++) {

        if (allInputCheckBox[i].checked) {
            allSelectedAquirer.push(allInputCheckBox[i].value);
        }
    }
    document.getElementById('selectBox2').setAttribute('title', allSelectedAquirer.join());

    if (allSelectedAquirer.join().length > 28) {
        var res = allSelectedAquirer.join().substring(0, 27);
        document.querySelector("#selectBox2 option").innerHTML = res + '...............';
    } else if (allSelectedAquirer.join().length == 0) {
        document.querySelector("#selectBox2 option").innerHTML = 'ALL';
    } else {
        document.querySelector("#selectBox2 option").innerHTML = allSelectedAquirer.join();
    }
}

function getCheckBoxValue3() {
    var allInputCheckBox = document.getElementsByClassName("myCheckBox3");

    var allSelectedAquirer = [];
    for (var i = 0; i < allInputCheckBox.length; i++) {

        if (allInputCheckBox[i].checked) {
            allSelectedAquirer.push(allInputCheckBox[i].value);
        }
    }
    document.getElementById('selectBox3').setAttribute('title', allSelectedAquirer.join());

    if (allSelectedAquirer.join().length > 28) {
        var res = allSelectedAquirer.join().substring(0, 27);
        document.querySelector("#selectBox3 option").innerHTML = res + '...............';
    } else if (allSelectedAquirer.join().length == 0) {
        document.querySelector("#selectBox3 option").innerHTML = 'ALL';
    } else {
        document.querySelector("#selectBox3 option").innerHTML = allSelectedAquirer.join();
    }
}
$(document).ready(function () {
    $(document).click(function () {
        expanded = false;
        $('#checkboxes').hide();
    });
    $('#checkboxes').click(function (e) {
        e.stopPropagation();
    });

    $(document).click(function () {
        expanded = false;
        $('#checkboxes1').hide();
    });
    $('#checkboxes1').click(function (e) {
        e.stopPropagation();
    });
    $(document).click(function () {
        expanded = false;
        $('#checkboxes2').hide();
    });
    $('#checkboxes2').click(function (e) {
        e.stopPropagation();
    });
    $(document).click(function () {
        expanded = false;
        $('#checkboxes3').hide();
    });

    $('#checkboxes3').click(function (e) {
        e.stopPropagation();
    });



});


function handleChange() {
    lineChart();
    //statisticsSettled();
    var currentDate = new Date();
    var first = currentDate.getDate();
    var last = currentDate.getDate() + 1;
    if (currentPeriod == 'day')
        if (currentPeriod == 'weak')
            var last = first - 6;
    if (currentPeriod == 'month')
        var last = first - 30;
    if (currentPeriod == 'year')
        var last = first - 365;
    var dateFrom = new Date(currentDate.setDate(first));
    var dateTo = new Date(currentDate.setDate(last));
    statistics(dateFrom, dateTo);
    /*if (currentPeriod == 'day')
       else
       statistics(dateTo, dateFrom);*/
    if (currentPeriod == 'custom') {
        var currentDate = new Date();
        //var first = $("#dateFrom").val();
        //var last = $("#dateTo").val();
        var dateFrom = document.getElementById("dateFrom").value;
        var dateTo = document.getElementById("dateTo").value;
        var dateFrom2 = ddmm_to_mmdd(dateFrom);
        var dateTo2 = ddmm_to_mmdd(dateTo);
        var dateFrom = ddmm_to_yymm(dateFrom);
        var dateTo = ddmm_to_yymm(dateTo);

        const date1 = new Date(dateFrom);
        const date2 = new Date(dateTo);


        //const date1 = new Date(ddmm_to_mmdd(first));
        //const date2 = new Date(ddmm_to_mmdd(last));


        //Find difference between two dates endp
        if (date1 > date2) {

            //$('#loader-wrapper').hide();
            alert('From date must be before the to date');
            //$('#customDateModal').hide();
            $('#dateFrom').focus();
            $('#buttonCustom').addClass('btnActive');
            //statistics(date1, date2);
            return false;

        }

        statistics(dateFrom, dateTo);

    }
}
function getMonthlyData() {
    $('#threeMonth').removeClass('btnActive');
    $('#oneMonth').addClass('btnActive');
    $('.monthlyGet').removeClass('btnActive');
    lineChart();

}
function getThreeMonthData() {

    $('#oneMonth').removeClass('btnActive');
    $('#threeMonth').addClass('btnActive');
    $('.monthlyGet').removeClass('btnActive');
    monthlylineChart();
}
function getCustomMonthData() {
    $('.monthlyGet').addClass('btnActive');
    $('#oneMonth').removeClass('btnActive');
    $('#threeMonth').removeClass('btnActive');
    customMonthlyLineChart();
}
function getHourlyData() {
    $('.todayDataGet').addClass('btnActive');

    var dateFromTime = "00:00";
    var dateToTime = "23:59";
    var regex = /(\d+)\:(\d+)/;

    dateFromParts = dateFromTime.match(regex)
    dateToParts = dateToTime.match(regex);

    dateFromHours = dateFromParts[1] < 13 ? parseInt(dateFromParts[1], 10) : parseInt(dateFromParts[1], 10) + 12;
    dateToHours = dateFromParts[1] < 13 ? parseInt(dateToParts[1], 10) : parseInt(dateToParts[1], 10) + 12;
    const date1 = new Date(dateFromHours);
    const date2 = new Date(dateToHours);
    if (date1 > date2) {

        alert('From time must be before  to time');
        $('#timepicker').focus();
        return false;
    } else {
        todayLineChart();
        $("#showToday").show();
    }

}
function getPaymentTypeData() {
    $('.paymentDataGet').addClass('btnActive');
    var month = new Date().getMonth() + 1;
    month = month < 10 ? "0" + month : month;
    var year = new Date().getFullYear();
    var date = new Date().getDate();
    date = date < 10 ? "0" + date : date;
    var first = year + "-" + month + "-" + date;
    if (document.getElementById("dateFromPie").value == null || document.getElementById("dateFromPie").value == '') {
        document.getElementById("dateFromPie").value = first;
        document.getElementById("dateToPie").value = first;
    }
    statisticsPie();
}
function getSettledData() {
    $('.settledDataGet').addClass('btnActive');
    var dateFrom = document.getElementById("dateFromSettlement").value;
    var dateTo = document.getElementById("dateToSettlement").value;
    const date1 = new Date(dateFrom);
    const date2 = new Date(dateTo);
    var dateFrom = ddmm_to_yymm(dateFrom);
    var dateTo = ddmm_to_yymm(dateTo);
    //Find difference between two dates endp
    if (date1 > date2) {

        alert('From date must be before the to date');
        $('#dateFromSettlement').focus();
        return false;
    } else {
        statisticsSettled();
    }


}
function getHitsData() {
    $('.funnelDataGet').addClass('btnActive');
    var dateFrom = new Date(document.getElementById('dateFromFunnel').value),
        dateTo = new Date(document.getElementById('dateToFunnel').value),
        oneDay = 24 * 60 * 60 * 1000; // hours*minutes*seconds*milliseconds


    statisticsFunnel();
}

function statistics(dateFrom, dateTo) {
    var token = document.getElementsByName("token")[0].value;
    var urls = new URL(window.location.href);
    var domain = urls.origin;
    let emailIdVal = document.getElementById("merchant").value;
    emailIdVal = emailIdVal == "ALL MERCHANTS" ? "ALL" : emailIdVal;
    $
        .ajax({
            url: domain + "/crmws/dashboard/dashboardTransactionData",
            timeout: 0,
            type: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            data: JSON.stringify({
                dateFrom: dateFrom,
                dateTo: dateTo,
                emailId: emailIdVal,
                currency: document.getElementById("currency").value,
                token: token,
                "struts.token.name": "token",
            }),
            success: function (data) {
                let saleAmount = Number(data.totalSaleAmount);
                saleAmount = saleAmount.toFixed(2);
                document.getElementById("dvApprovedAmount").innerHTML = $('#currency').find(":selected").data('symbol')+' ' + inrFormat(saleAmount);
                let refundAmount = Number(data.totalRefundAmount);
                refundAmount = refundAmount.toFixed(2);
                document.getElementById("dvRefundedAmount").innerHTML = $('#currency').find(":selected").data('symbol')+' ' + inrFormat(refundAmount);
                let cancelledAmount = Number(data.totalCancelledAmount);
                cancelledAmount = cancelledAmount.toFixed(2);
                document.getElementById("dvTotalCancelled").innerHTML = $('#currency').find(":selected").data('symbol')+' ' + inrFormat(cancelledAmount);
                let failedAmount = Number(data.totalFailedAmount);
                failedAmount = failedAmount.toFixed(2);
                document.getElementById("dvTotalFailed").innerHTML = $('#currency').find(":selected").data('symbol')+' ' + inrFormat(failedAmount);
                let amount = Number(data.totalFraudAmount);
                amount = amount.toFixed(2);
                document.getElementById("dvTotalFraud").innerHTML = $('#currency').find(":selected").data('symbol')+' ' + inrFormat(amount);

                let settledAmount = Number(data.totalSettleAmount);
                settledAmount = settledAmount.toFixed(2);
                document.getElementById("dvSettledAmount").innerHTML = $('#currency').find(":selected").data('symbol')+' ' + inrFormat(settledAmount);

                var currentMonth = new Date().getMonth() + 1;
                currentMonth = currentMonth < 10 ? "0" + currentMonth : currentMonth;
                var currentYear = new Date().getFullYear();
                var currentDate = new Date().getDate();
                currentDate = currentDate < 10 ? "0" + currentDate : currentDate;
                var fDate = currentYear + "-" + currentMonth + "-" + currentDate;
                document.getElementById("dateFromMonth").value = fDate;
                document.getElementById("dateToMonth").value = fDate;
                customMonthlyLineChart();
            },
            error: function (data) {

            }
        });

}

function statisticsRefund(dateFrom, dateTo) {
    var token = document.getElementsByName("token")[0].value;
    $
        .ajax({
            url: "statisticsActionRefund",
            timeout: 0,
            type: "POST",
            data: {
                dateFrom: dateFrom,
                dateTo: dateTo,
                emailId: document.getElementById("merchant").value,
                currency: document.getElementById("currency").value,
                token: token,
                "struts.token.name": "token",
            },
            success: function (data) {
                //document.getElementById("dvTotalRefunded").innerHTML =  data.statistics.totalRefunded;
                //document.getElementById("dvTotalRefunded").innerHTML = inrFormat(data.statistics.totalRefunded);
                document.getElementById("dvRefundedAmount").innerHTML = $('#currency').find(":selected").data('symbol')+' ' + inrFormat(data.statistics.refundedAmount);
                statisticsAll(dateFrom, dateTo);
            },
            error: function (data) {

            }
        });

}


function statisticsAll(dateFrom, dateTo) {
    var token = document.getElementsByName("token")[0].value;
    $
        .ajax({
            url: "statisticsAction",
            timeout: 0,
            type: "POST",
            data: {
                dateFrom: dateFrom,
                dateTo: dateTo,
                emailId: document.getElementById("merchant").value,
                currency: document.getElementById("currency").value,
                token: token,
                "struts.token.name": "token",
            },
            success: function (data) {

                let failed = data.statistics.totalFailed == null ? "0" : data.statistics.totalFailed;
                document.getElementById("dvTotalFailed").innerHTML = $('#currency').find(":selected").data('symbol')+' ' + inrFormat(failed);
                //document.getElementById("dvTotalRejected").innerHTML = data.statistics.totalRejectedDeclined;
                //document.getElementById("dvTotalDropped").innerHTML = data.statistics.totalDropped;
                document.getElementById("dvTotalCancelled").innerHTML = $('#currency').find(":selected").data('symbol')+' ' + inrFormat(data.statistics.totalCancelled);
                document.getElementById("dvTotalFraud").innerHTML = $('#currency').find(":selected").data('symbol')+' ' + inrFormat(data.statistics.totalFraud);
                //document.getElementById("dvTotalInvalid").innerHTML = data.statistics.totalInvalid;



                // Below method for calculating the Total Settled Amount
                // statisticsSettledAmount(dateFrom,dateTo);
            },
            error: function (data) {

            }
        });

}

function statisticsSettledAmount(dateFrom, dateTo) {
    var token = document.getElementsByName("token")[0].value;
    $
        .ajax({
            url: "statisticsSettledAmountAction",
            timeout: 0,
            type: "POST",
            data: {
                dateFrom: dateFrom,
                dateTo: dateTo,
                emailId: document.getElementById("merchant").value,
                currency: document.getElementById("currency").value,
                token: token,
                "struts.token.name": "token",
            },
            success: function (data) {

                document.getElementById("dvTotalSettledAmount").innerHTML = inrFormat(data.statistics.totalSettledAmount);

            },
            error: function (data) {

            }
        });

}


function lineChart() {
    var acquirer = [];
    var inputElements = document.getElementsByName('acquirer');
    for (var i = 0; inputElements[i]; ++i) {
        if (inputElements[i].checked) {
            acquirer.push(inputElements[i].value);

        }
    }
    var acquirerString = acquirer.join();
    var token = document.getElementsByName("token")[0].value;
    var paymentMethods = document.getElementById("paymentMethods").value;
    var mopType = document.getElementById("mopType").value;
    var transactionType = document.getElementById("transactionTypeMonthly").value;
    var acquirerString = acquirer.join();

    if (paymentMethods == '') {
        paymentMethods = 'ALL'
    }
    if (mopType == '') {
      mopType = 'ALL'
    }
    if (transactionType == '') {
        transactionType = 'ALL'
    }

    if (acquirerString == '') {
        acquirerString = 'ALL'
    }
    // create the loading chart
    /*var chart = new Highcharts.chart({
      chart: {
        title: {
          text: 'Monthly Transaction',

        },
        renderTo: 'colouredRoundedLineChart'
      },
      xAxis: {
        title: {
          text: 'Date',
          x: -20
        }

      }
    });
    chart.setTitle({ text: "Fetching data.." });
    chart.showLoading();*/

    var month = new Date().getMonth() + 1;
    var year = new Date().getFullYear();
    var date = new Date().getDate();
    var dateFrom = year + '-' + ((('' + month).length < 2) ? ('0' + month) : month) + '-01';
    var dateTo = year + '-' + ((('' + month).length < 2) ? ('0' + month) : month) + '-' + date;

    $.ajax({
        url: "lineChartAction",
        timeout: 0,
        type: "POST",
        data: {
            emailId: document.getElementById("merchant").value,
            currency: document.getElementById("currency").value,
            dateFrom: dateFrom,
            dateTo: dateTo,
            mopType: mopType,
            transactionType: transactionType,
            paymentType: paymentMethods,
            acquirer: acquirerString,
            token: token,
            "struts.token.name": "token",
        },
        success: function (data) {
            var today = new Date();
            var initDate = new Date(today.getFullYear(), today.getMonth(), 01);
            //  var initDate = new Date(today.getMonth()+ 1 +'-01-'+today.getFullYear());
            //$('#dateToMonth').val($.datepicker.formatDate('dd-mm-yy', today));
            //$('#dateFromMonth').val($.datepicker.formatDate('dd-mm-yy', initDate));


            var a = [];
            var b = [];
            var c = [];
            var d = [];
            //var e = [];
            var dateArray = [];
            var pieChartList = data.pieChart;
            // const date1 = new Date(document.getElementById("dateFromMonth").value);
            // const date2 = new Date(document.getElementById("dateToMonth").value);
            // const diffTime = Math.abs(date2 - date1);
            // const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

            for (var i = new Date(dateFrom).getDate() - 1; i < new Date(dateTo).getDate(); i++) {
                var piechart = pieChartList[i];
                var success = parseInt(piechart.totalSuccess);
                var refund = parseInt(piechart.totalRefunded);
                var failled = parseInt(piechart.totalFailed);
                var cancelled = parseInt(piechart.totalCancelled);
                //var timeout = parseInt(piechart.totalTimeouts);
                //var error = parseInt(piechart.totalErrors);
                a.push(success);
                b.push(refund);
                c.push(failled);
                d.push(cancelled);
                //d.push(timeout);
                //e.push(error);
                var txnDateMonth = new Date(piechart.txndate).toLocaleString('default', { month: 'short' })
                dateArray.push((piechart.txndate).split('-')[2] + '-' + txnDateMonth + '-' + (piechart.txndate).split('-')[0]);

            }

            for (var i = new Date(dateFrom).getDate(); i <= new Date(dateTo).getDate(); i++) {
                dateArray.push(i.toString());
            }
            //console.log(dateArray);
            //return;
            $(function () {
                var colors = ['#2BB88E', '#2369AE', '#DF2938', '#064E67'];
                /*$('#colouredRoundedLineChart').highcharts(
                  {
                    title: {
                      text: 'Monthly Transaction',
                      x: -20
                      //center
                    },

                    allButtonsEnabled: true,
                    subtitle: {
                      text: '',
                      x: -20
                    },
                    xAxis: {
                      title: {
                        text: 'Date'
                      },
                      categories: dateArray
                    },
                    yAxis: {
                      title: {
                        text: 'Number of Transactions'
                      },
                      plotLines: [{
                        value: 0,
                        width: 1,
                        color: '#808080'
                      }]
                    },
                    credits: {
                      enabled: false
                    },
                    tooltip: {
                      valueSuffix: ''
                    },
                    exporting: {
                      sourceWidth: 700,
                      sourceHeight: 400,
                      scale: 1
                    },
                    legend: {
                      layout: 'vertical',
                      align: 'right',
                      verticalAlign: 'middle',
                      borderWidth: 0
                    },
                    series: [{
                      name: 'Total Success',
                      data: a,
                      color: colors[0]
                    }, {
                      name: 'Total Refunded',
                      data: b,
                      color: colors[1]
                    }, {
                      name: 'Total Failed',
                      data: c,
                      color: colors[2]
                    },
                     {
                      name: 'Total Cancelled',
                      data: d,
                      color: colors[3]
                    },
                    // {
                    //  name: 'Errors',
                    //  data: e,
                    //  color: colors[4]
                  //  }
                    ],
                    responsive: {
                      rules: [{
                        condition: {
                          maxWidth: 500
                        },
                        chartOptions: {
                          legend: {
                            align: 'center',
                            verticalAlign: 'bottom',
                            layout: 'horizontal'
                          }
                        }
                      }]
                    }
                  });*/
            });

        },
        error: function (data) {
        }
    });

}
function monthlylineChart() {

    //document.getElementById("loading").style.display = "block";
    var token = document.getElementsByName("token")[0].value;

    // create the loading chart
    var chart = new Highcharts.chart({
        chart: {
            title: {
                text: 'Monthly Transaction',

            },
            renderTo: 'colouredRoundedLineChart'
        },
        xAxis: {
            title: {
                text: 'Date',
                x: -20
            }

        }
    });
    chart.setTitle({ text: "Fetching data.." });
    chart.showLoading();

    var currentMonth = new Date().getMonth() + 1;
    var currentYear = new Date().getFullYear();
    var prevMonthDate = new Date().setMonth(currentMonth - 3);
    var prevMonth = new Date(prevMonthDate).getMonth() + 1;
    var prevYear = new Date(prevMonthDate).getFullYear();
    var currentDate = new Date().getDate();

    var dateFrom = prevYear + '-' + ((('' + prevMonth).length < 2) ? ('0' + prevMonth) : prevMonth) + '-01';
    var dateTo = currentYear + '-' + ((('' + currentMonth).length < 2) ? ('0' + currentMonth) : currentMonth) + '-' + ((('' + currentDate).length < 2) ? ('0' + currentDate) : currentDate);

    $.ajax({
        url: "monthlyLineChartAction",
        timeout: 0,
        type: "POST",
        data: {
            emailId: document.getElementById("merchant").value,
            currency: document.getElementById("currency").value,
            dateFrom: dateFrom,
            dateTo: dateTo,
            token: token,
            "struts.token.name": "token",
        },
        success: function (data) {
            var a = [];
            var b = [];
            var c = [];
            var d = [];
            var e = [];
            var dateArray = [];
            var pieChartList = data.pieChart;

            //Find difference between two dates start

            const date1 = new Date(dateFrom);
            const date2 = new Date(dateTo);
            const diffTime = Math.abs(date2 - date1);
            const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

            //Find difference between two dates end

            for (var i = 0; i < diffDays + 1; i++) {
                var piechart = pieChartList[i];
                if (piechart == undefined) break;
                var success = parseInt(piechart.totalSuccess);
                var refund = parseInt(piechart.totalRefunded);
                var failled = parseInt(piechart.totalFailed);
                //  var timeout = parseInt(piechart.totalTimeouts);
                //  var error = parseInt(piechart.totalErrors);
                a.push(success);
                b.push(refund);
                c.push(failled);
                //  d.push(timeout);
                //  e.push(error);
                var txnDateMonth = new Date(piechart.txndate).toLocaleString('default', { month: 'short' })
                dateArray.push((piechart.txndate != undefined) ? (txnDateMonth + '-' + (piechart.txndate).split('-')[0]) : piechart.txndate);
                //  console.log(dateArray);

            }

            $(function () {
                $('#colouredRoundedLineChart').highcharts(
                    {
                        title: {
                            text: 'Monthly Transaction',
                            x: -20
                            //center
                        },

                        allButtonsEnabled: true,
                        subtitle: {
                            text: '',
                            x: -20
                        },
                        xAxis: {
                            title: {
                                text: 'Date'
                            },
                            categories: dateArray
                        },
                        yAxis: {
                            title: {
                                text: 'Number of Transactions'
                            },
                            plotLines: [{
                                value: 0,
                                width: 1,
                                color: '#808080'
                            }]
                        },
                        credits: {
                            enabled: false
                        },
                        tooltip: {
                            valueSuffix: ''
                        },
                        legend: {
                            layout: 'vertical',
                            align: 'right',
                            verticalAlign: 'middle',
                            borderWidth: 0
                        },
                        series: [{
                            name: 'Total Success',
                            data: a
                        }, {
                            name: 'Total Refunded',
                            data: b
                        }, {
                            name: 'Total Failed',
                            data: c
                        }
                            //,{
                            //  name: 'Timeout',
                            //  data: d
                            //}, {
                            //  name: 'Errors',
                            //  data: e
                            //}

                        ],
                        responsive: {
                            rules: [{
                                condition: {
                                    maxWidth: 500
                                },
                                chartOptions: {
                                    legend: {
                                        align: 'center',
                                        verticalAlign: 'bottom',
                                        layout: 'horizontal'
                                    }
                                }
                            }]
                        }
                    });
            });

        },
        error: function (data) {
        }
    });

}

function ddmm_to_mmdd(date) {
    var dateArr = date.split('-');
    return dateArr[1] + '-' + dateArr[0] + '-' + dateArr[2];
}

function ddmm_to_yymm(date) {
    var dateArr = date.split('-');
    return dateArr[2] + '-' + dateArr[1] + '-' + dateArr[0];
}

function mmdd_to_yymm(date) {
    var dateArr = date.split('-');
    return dateArr[2] + '-' + dateArr[0] + '-' + dateArr[1];
}

function retrieveSalesDetails(dateRange) {
    dateRange = dateRange.split("-");
    let fromDate = dateRange[0].replaceAll("/", "-");
    let toDate = dateRange[1].replaceAll("/", "-");
    fromDate = mmdd_to_yymm(fromDate).replace(" ", "");
    toDate = mmdd_to_yymm(toDate).replace(" ", "");
    printSaleReportDetails(fromDate, toDate);
}

function printSaleReportDetails(fromDate, toDate) {
    var urls = new URL(window.location.href);
    var domain = urls.origin;
    let emailIdVal = document.getElementById("merchant").value;
    emailIdVal = emailIdVal == "ALL MERCHANTS" ? "ALL" : emailIdVal;
    $
        .ajax({
            url: domain + "/crmws/dashboard/dashboardTransactionData",
            timeout: 0,
            type: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            data: JSON.stringify({
                dateFrom: fromDate,
                dateTo: toDate,
                emailId: emailIdVal,
                currency: document.getElementById("currency").value,
                token: token,
                "struts.token.name": "token",
            }),
            success: function (data) {
                let saleAmount = Number(data.totalSaleAmount);
                saleAmount = saleAmount.toFixed(2);
                document.getElementById("totalSalesAmount").innerHTML = $('#currency').find(":selected").data('symbol')+' ' + inrFormat(saleAmount);

                let refundAmount = Number(data.totalRefundAmount);
                refundAmount = refundAmount.toFixed(2);
                document.getElementById("totalRefundedAmount").innerHTML = $('#currency').find(":selected").data('symbol')+' ' + inrFormat(refundAmount);

                let settleAmount = Number(data.totalSettleAmount);
                settleAmount = settleAmount.toFixed(2);
                document.getElementById("totalSettledAmount").innerHTML = $('#currency').find(":selected").data('symbol')+' ' + inrFormat(settleAmount);

                let failedAmount = Number(data.totalFailedAmount);
                failedAmount = failedAmount.toFixed(2);
                document.getElementById("totalFailedAmount").innerHTML = $('#currency').find(":selected").data('symbol')+' ' + inrFormat(failedAmount);

                let cancelledAmount = Number(data.totalCancelledAmount);
                cancelledAmount = cancelledAmount.toFixed(2);
                document.getElementById("totalCancelledAmount").innerHTML = $('#currency').find(":selected").data('symbol')+' ' + inrFormat(cancelledAmount);

                let amount = Number(data.totalFraudAmount);
                amount = amount.toFixed(2);
                document.getElementById("totalFraudAmount").innerHTML = $('#currency').find(":selected").data('symbol')+' ' + inrFormat(amount);
                getPaymentTypeData();
            },
            error: function (data) {

            }
        });
}

function customMonthlyLineChart() {
    //Find difference between two dates start
    var acquirer = [];
    var inputElements = document.getElementsByName('acquirer');
    for (var i = 0; inputElements[i]; ++i) {
        if (inputElements[i].checked) {
            acquirer.push(inputElements[i].value);

        }
    }
    var acquirerString = acquirer.join();
    var paymentMethods = document.getElementById("paymentMethods").value;
    var mopType = document.getElementById("mopType").value;
    var transactionType = document.getElementById("transactionTypeMonthly").value;
    var acquirerString = acquirer.join();

    if (paymentMethods == '') {
        paymentMethods = 'ALL'
    }
    if (transactionType == '') {
        transactionType = 'ALL'
    }
    if (mopType == '') {
      mopType = 'ALL'
    }

    if (acquirerString == '') {
        acquirerString = 'ALL'
    }
    var dateFrom = document.getElementById("dateFromMonth").value;
    var dateTo = document.getElementById("dateToMonth").value;

    const date1 = new Date(dateFrom);
    const date2 = new Date(dateTo);
    const diffTime = Math.abs(date2 - date1);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    //Find difference between two dates endp
    if (date1 > date2) {
        alert('From date must be before the to date');
        $('#dateFromMonth').focus();
        return false;
    }

    var token = document.getElementsByName("token")[0].value;
    $.ajax({
        url: "customMonthlyLineChartAction",
        timeout: 0,
        type: "POST",
        data: {
            emailId: document.getElementById("merchant").value,
            currency: document.getElementById("currency").value,
            dateFrom: dateFrom,
            dateTo: dateTo,
            mopType: mopType,
            transactionType: transactionType,
            paymentType: paymentMethods,
            acquirer: acquirerString,
            token: token,
            "struts.token.name": "token",
        },
        success: function (data) {
            initChartsWidget4(data);
        },
        error: function (data) {
        }
    });

}

function todayLineChart() {

    var token = document.getElementsByName("token")[0].value;
    var acquirer = [];
    var inputElements = document.getElementsByName('acquirerHourly');
    for (var i = 0; inputElements[i]; ++i) {
        if (inputElements[i].checked) {
            acquirer.push(inputElements[i].value);

        }
    }
    var acquirerString = acquirer.join();
    var paymentMethods = document.getElementById("paymentMethodsHourly").value;
    var mopType = document.getElementById("mopTypeHourly").value;
    var transactionType = document.getElementById("transactionTypeHourly").value;
    var acquirerString = acquirer.join();

    if (paymentMethods == '') {
        paymentMethods = 'ALL'
    }
    if (transactionType == '') {
        transactionType = 'ALL'
    }
    if (mopType == '') {
      mopType = 'ALL'
    }

    if (acquirerString == '') {
        acquirerString = 'ALL'
    }
    var dateFrom = new Date();
    var dateTo = new Date();
    var dateFromTime = "00:00";
    var dateToTime = "23:59";
    var today = new Date();
    var time = today.getHours() + ":" + today.getMinutes() + ":" + today.getSeconds();
    var selectDateHourly = document.getElementById('selectDateHourly').value;
    var selectDateHourly = ddmm_to_yymm(selectDateHourly);
    var regex = /(\d+)\:(\d+)/;

    dateFromParts = dateFromTime.match(regex)
    dateToParts = dateToTime.match(regex);

    dateFromHours = parseInt(dateFromParts[1], 10);
    dateToHours = parseInt(dateToParts[1], 10);

    const date1 = new Date(dateFromHours);
    const date2 = new Date(dateToHours);


    //Find difference between two dates endp
    if (date1 > date2) {
        //$('#loader-wrapper').hide();
        alert('From time must be before  to time');
        $('#timepicker').focus();
        return false;
    }
    if (selectDateHourly == (dateFrom.getFullYear()) + '-' + ((('' + (dateFrom.getMonth() + 1)).length < 2) ? ('0' + (dateFrom.getMonth() + 1)) : (dateFrom.getMonth() + 1)) + '-' + ((('' + dateFrom.getDate()).length < 2) ? ('0' + dateFrom.getDate()) : dateFrom.getDate())) {
        dateFrom = selectDateHourly + ' ' + dateFromTime + ':00';
        dateTo = selectDateHourly + ' ' + time
    }
    else {
        dateFrom = selectDateHourly + ' ' + dateFromTime + ':00';
        dateTo = selectDateHourly + ' ' + dateToTime + ':59';

    }
    var urls = new URL(window.location.href);
    var domain = urls.origin;
    let emailIdVal = document.getElementById("merchant").value;
    emailIdVal = emailIdVal == "ALL MERCHANTS" ? "ALL" : emailIdVal;
    $.ajax({
        url: domain + "/crmws/dashboard/hourlyChartData",
        timeout: 0,
        type: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        data: JSON.stringify({
            emailId: emailIdVal,
            currency: document.getElementById("currency").value,
            dateFrom: dateFrom,
            dateTo: dateTo,
            selectDateHourly: selectDateHourly,
            mopType: mopType,
            txnType: transactionType,
            paymentType: paymentMethods,
            acquirer: acquirerString,
            token: token,
            "struts.token.name": "token",
        }),
        success: function (data) {
            loadHourlyTransactionsChart(data);
        },
        error: function (data) {
        }
    });

}




function intChart(val1, val2, val3, val4, val5) {
    Highcharts.chart('chartBox', {
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
            type: 'pie'
        },
        title: {
            text: 'Payment Types comparison'
        },
        tooltip: {
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        accessibility: {
            point: {
                valueSuffix: '%'
            }
        },
        exporting: {
            sourceWidth: 700,
            sourceHeight: 400,
            scale: 1
        },
        plotOptions: {
            pie: {
                //    borderWidth: 0,
                // borderColor: null,
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    format: '<b>{point.name}</b>: {point.percentage:.1f} %'

                },
                showInLegend: true
            }
        },
        series: [{
            name: 'Share',
            colorByPoint: true,
            data: [{
                name: 'Credit Card',
                y: parseFloat(val1),
                //sliced: false,
                //selected: true
            }, {
                name: 'Debit Card',
                y: parseFloat(val2)
            }, {
                name: 'UPI',
                y: parseFloat(val3)
            }, {
                name: 'Wallet',
                y: parseFloat(val4)
            }, {
                name: 'Net Banking',
                y: parseFloat(val5)
            }]

        }]
    }, function (chart) { // on complete
        if (val1 == 0.00) {
            chart.renderer.text('No Data Available', 160, 200)
                .css({
                    color: '#4572A7',
                    fontSize: '12px'
                })
                .add();
        }
        else {
            chart.renderer.text('', 160, 200)
            .css({
                color: '#4572A7',
                fontSize: '16px'
            })
            .add();
        }
    });
}

function statisticsPie() {

    var paymentMethods = document.getElementById("paymentMethodsPie").value;
    var transactionType = document.getElementById("transactionTypePayment").value;
    if (transactionType == null || transactionType == '') {
      alert("Transaction type is mandatory");
      return false;
    }

    // var emailId = document.getElementById("merchant").value;
    var merchantEmailId = document.getElementById("merchant").value;
    var dateFrom = document.getElementById("dateFromPie").value;
    var dateTo = document.getElementById("dateToPie").value;
    var currency = document.getElementById("currency").value;

    //var paymentMethods = "ALL";
    var mopTypes = "ALL"
    if ($("#mopTypePie").val() !== null) {
        mopTypes = $('select#mopTypePie').val().toString();
    }

    if (merchantEmailId == '' || merchantEmailId == 'ALL MERCHANTS') {
        merchantEmailId = 'ALL'
    }
    if (paymentMethods == '') {
        paymentMethods = 'ALL'
    }
    if (transactionType == '') {
        transactionType = 'ALL'
    }
    var token = document.getElementsByName("token")[0].value;
    var urls = new URL(window.location.href);
    var domain = urls.origin;
    let emailIdVal = document.getElementById("merchant").value;
    emailIdVal = emailIdVal == "ALL MERCHANTS" ? "ALL" : emailIdVal;
    $
        .ajax({
            url: domain + "/crmws/dashboard/pieChartData",
            timeout: 0,
            type: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            data: JSON.stringify({
                paymentType: paymentMethods,
                dateFrom: ddmm_to_yymm(dateFrom),
                dateTo: ddmm_to_yymm(dateTo),
                currency: currency,
                //merchantEmailId: "ALL",
                emailId: emailIdVal,
                token: token,
                mopType: mopTypes,
                txnType: transactionType,
                acquirer: 'ALL'
            }),

            success: function (data) {
                initPieChart(data, '#kt_chart_widgets_22_chart_1', '#kt_chart_widgets_22_chart_1', true, paymentMethods, mopTypes);
            },
            error: function (data) {
            }
        });

}

function loadCurrencies(){
    var urls = new URL(window.location.href);
    var domain = urls.origin;

    $.get(domain + "/crmws/dashboard/getUserCurrency?emailId="+$("#merchant").val(),
     function(response) {
        console.log(response)
        var s= '<option value="" selected>Select Currency</option>';
        for (var i=0;i<response.length;i++) {
            var el = response[i];
            console.log(el);
            s += '<option value="' + el.code + '" data-symbol="'+el.symbol+'">' + el.name + '</option>'
        }
        $("#currency").html(s);
    });
}

function hitsChart(_hitsChart) {

    var colors = ['#2369AE', '#2BB88E', '#DF2938', '#FCB414', '#064E67'];
    var series = [];
    if (_hitsChart.totalTxnCount != undefined && _hitsChart.totalTxnCount > 0) {
        series = [{
            name: 'Count',
            colorByPoint: true,
            data: [{
                name: 'Hits',
                y: parseFloat(_hitsChart.totalTxnCount),
                sliced: false,
                selected: true,
                color: colors[0],



            }, {
                name: 'Success',
                y: parseFloat(_hitsChart.successTxnCount),
                color: colors[1]
            }, {
                name: 'Failed',
                y: parseFloat(_hitsChart.failedTxnCount),
                color: colors[2]
            },
            // {
            //  name: 'Timeout',
            //  y: parseFloat(_hitsChart.timeoutTxnCount),
            //  color: colors[3]
            // },
            {
                name: 'Cancelled',
                y: parseFloat(_hitsChart.cancelledTxnCount),
                color: colors[4]
            }, {
                name: 'Invalid',
                y: parseFloat(_hitsChart.invalidTxnCount),
                color: colors[5]
            }]
        }]
    }
    Highcharts.setOptions({
        lang: {
            decimalPoint: '.',
            thousandsSep: ','
        }
    });
    Highcharts.chart('funnel', {
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
            type: 'funnel'
        },
        title: {
            text: 'Hits Vs Captured'
        },
        exporting: {
            sourceWidth: 700,
            sourceHeight: 400,
            scale: 1
        },
        tooltip: {
            //valueSuffix: ''
            //pointFormat: '<span>{point.y:.f}</span>',
            pointFormatter: function () {
                //console.log(this.y);
                return inrFormat(this.y);
            }
        },


        accessibility: {
            point: {
                valueSuffix: ''
            }
        },
        plotOptions: {
            series: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    //format: '<b>{point.name}</b> ({point.y:,.0f})',
                    pointFormatter: function () {
                        //console.log(this.y);
                        return "<span style='color:{point.color}'></span> " + this.point.name + "( <b>" + inrFormat(this.y) + "" + "</b> )<br/>";
                        //return inrFormat(this.y)  ;
                    },
                    softConnector: true

                },
                center: ['40%', '50%'],
                neckWidth: '30%',
                neckHeight: '25%',
                width: '80%',
                showInLegend: true
            }
        },

        series: series
    }, function (chart) { // on complete
        if (_hitsChart.totalTxnCount == 0.00) {
            chart.renderer.text('No Data Available', 235, 200)
                .css({
                    color: '#4572A7',
                    fontSize: '16px'
                })
                .add();
        }
        else {
            chart.renderer.text('', 230, 120)
                .css({
                    color: '#4572A7',
                    fontSize: '16px',
                    textAlign: 'center',
                })
                .add();
        }

    });
}

function statisticsFunnel() {


    //var acquirerString = acquirer.join();
    // var emailId = document.getElementById("merchant").value;
    var merchantEmailId = document.getElementById("merchant").value;
    var currency = document.getElementById("currency").value;
    //var dateFrom = document.getElementById("dateFromFunnel").value;
    //var dateTo = document.getElementById("dateToFunnel").value;
    var dateFrom = document.getElementById("dateFromFunnel").value;
    var dateTo = document.getElementById("dateToFunnel").value;

    //var dateFrom3 = ddmm_to_yymm(dateFrom);
    //var dateTo3 = ddmm_to_yymm(dateTo);


    const date1 = new Date(dateFrom);
    const date2 = new Date(dateTo);
    const diffTime = Math.abs(date2 - date1);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    //Find difference between two dates endp
    if (date1 > date2) {
        //$('#loader-wrapper').hide();
        alert('From date must be before the to date');
        $('#dateFromFunnel').focus();
        return false;
    }
    var funnelChatData = "ALL";
    var acquirer = [];
    var inputElements = document.getElementsByName('acquirerFunnel');
    for (var i = 0; inputElements[i]; ++i) {
        if (inputElements[i].checked) {
            acquirer.push(inputElements[i].value);

        }
    }
    var acquirerString = acquirer.join();
    var paymentMethods = document.getElementById("paymentMethodsFunnel").value;
    var mopType = document.getElementById("mopTypeFunnel").value;
    var transactionType = document.getElementById("transactionTypeFunnel").value;
    var acquirerString = acquirer.join();

    if (mopType == '') {
      mopType = 'ALL'
    }
    if (paymentMethods == '') {
        paymentMethods = 'ALL'
    }
    if (transactionType == '') {
        transactionType = 'ALL'
    }

    if (acquirerString == '') {
        acquirerString = 'ALL'
    }

    if (merchantEmailId == '' || merchantEmailId == 'ALL MERCHANTS') {
        merchantEmailId = 'ALL'
    }
    if (funnelChatData == '') {
        funnelChatData = 'ALL'
    }

    var token = document.getElementsByName("token")[0].value;
    $
        .ajax({
            url: "funnelChartDataAction",
            timeout: 0,
            type: "POST",
            data: {

                merchantEmailId: merchantEmailId,
                currency: currency,
                token: token,
                dateFrom: ddmm_to_yymm(dateFrom),
                dateTo: ddmm_to_yymm(dateTo),
                mopType: mopType,
                transactionType: transactionType,
                paymentType: paymentMethods,
                acquirer: acquirerString,


            },
            success: function (data) {



                if (funnelChatData == 'ALL') {
                    loadHitsCapturedChart(data.funnelChatData);
                    // hitsChart();
                }
                else {
                }

            },
            error: function (data) {
            }
        });

}
function settledAmount(data) {
    loadSettlementChart(data);
}

function statisticsSettled() {

    var merchantEmailId = document.getElementById("merchant").value;
    var currency = document.getElementById("currency").value;
    var dateFrom = document.getElementById("dateFromSettlement").value;
    var dateTo = document.getElementById("dateToSettlement").value;

    const date1 = new Date(dateFrom);
    const date2 = new Date(dateTo);
    const diffTime = Math.abs(date2 - date1);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    //Find difference between two dates endp
    if (date1 > date2) {

        alert('From date must be before the to date');
        $('#dateFromSettlement').focus();
        return false;
    }
    if (diffDays > 365) {
        alert("No. of days can not be more than 365'");
        return;
    }
    // var dateFrom = document.getElementById("dateFromSettlement").value;
    // var dateTo = document.getElementById("dateToSettlement").value;
    var settlementData = "ALL";
    var acquirer = [];
    var inputElements = document.getElementsByName('acquirersettled');
    for (var i = 0; inputElements[i]; ++i) {
        if (inputElements[i].checked) {
            acquirer.push(inputElements[i].value);

        }
    }
    var acquirerString = acquirer.join();
    var paymentMethods = document.getElementById("paymentMethodsSettled").value;
    var acquirerString = acquirer.join();

    if (paymentMethods == '') {
        paymentMethods = 'ALL'
    }

    if (acquirerString == '') {
        acquirerString = 'ALL'
    }
    if (merchantEmailId == '' || merchantEmailId == 'ALL MERCHANTS') {
        merchantEmailId = 'ALL'
    }
    if (settlementData == '') {
        settlementData = 'ALL'
    }

    var urls = new URL(window.location.href);
    var domain = urls.origin;
    var token = document.getElementsByName("token")[0].value;
    let emailIdVal = document.getElementById("merchant").value;
    emailIdVal = emailIdVal == "ALL MERCHANTS" ? "ALL" : emailIdVal;
    $
        .ajax({
            url: domain + "/crmws/dashboard/settlementChartData",
            timeout: 0,
            type: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            data: JSON.stringify({

                dateFrom: dateFrom,
                dateTo: dateTo,
                currency: currency,
                token: token,
                emailId: emailIdVal,
                //mopType : mopType,
                //transactionType : transactionType,
                paymentType: paymentMethods,
                acquirer: "",
            }),
            success: function (data) {

                if (settlementData == 'ALL') {
                    settledAmount(data);

                }
                else {
                }

            },
            error: function (data) {
            }
        });

}

function arrangeAcord() {
    //ajax call 
    $.ajax({
        type: "POST",
        dataType: "JSON",
        url: 'dashBoardPreferenceSetAction',
        //timeout : 0,
        data: {
            emailId: document.getElementById("merchant").value,
            token: token

        },
        success: function (data) {
            var acord = [];
            var order = data.preferenceSetConstant;  // [{order:0, id: "card3"},{order:1, id: "card5"},{order:2, id: "card1"},{order:3, id: "card2"},{order:4, id: "card4"}];

            $.each(order, function (k, v) {
                acord.push($("#" + v.id));
                $("#" + v.id).remove();
            });
            //$("#sortable").html("");
            $.each(acord, function (k, v) {
                $("#sortable").append(v);
            });

        }
    });
}
