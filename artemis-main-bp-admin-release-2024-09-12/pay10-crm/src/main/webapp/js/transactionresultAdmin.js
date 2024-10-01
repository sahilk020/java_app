$(document).ready(function () {



  // Initialize select2
  $(".adminMerchants").select2();
});



$(document).ready(function () {
  $(function () {
    $("#dateFrom").datepicker({
      changeMonth: true,
      changeYear: true,
      prevText: "click for previous months",
      nextText: "click for next months",
      showOtherMonths: true,
      dateFormat: 'dd-mm-yy',
      selectOtherMonths: true,
      minDate: new Date(2018, 1 - 1, 1),
      maxDate: new Date()
    });
    $("#dateTo").datepicker({
      changeMonth: true,
      changeYear: true,
      prevText: "click for previous months",
      nextText: "click for next months",
      showOtherMonths: true,
      dateFormat: 'dd-mm-yy',
      selectOtherMonths: true,
      minDate: new Date(2018, 1 - 1, 1),
      maxDate: new Date()
    });
  });

  $(function () {
    var today = new Date();
    $('#dateTo').val($.datepicker.formatDate('dd-mm-yy', today));
    $('#dateFrom').val($.datepicker.formatDate('dd-mm-yy', today));
    var userType = $("#userTypeName").val();
    var needToShowAcqFields = $("#needToShowAcqFieldsInReport").val();
    if (needToShowAcqFields == 'true' || userType != 'SUBADMIN') {
      renderTable();
    } else {
      renderTableWithoutAcqFields();
        }
    setTimeout(totalAmountofAlltxns, 1000);
  });
  /*added by vijaylakshmi*/
  $("#submit").click(function (env) {


    //  Restrict for empty date field
    var date1 = document.getElementById("dateFrom").value;
    if (date1 == "" || date1 == null) {
      alert("Please select Date");
      return;
    }
    var date2 = document.getElementById("dateTo").value;
    if (date2 == "" || date2 == null) {
      alert("Please select Date");
      return;
    }

    $('#loader-wrapper').show();
    reloadTable();
    setTimeout(totalAmountofAlltxns, 1000);
  });

  $(function () {
    var datepick = $.datepicker;
    var needToShowAcqFields = $("#needToShowAcqFieldsInReport").val();
    var userType = $("#userTypeName").val();
    var tableName = needToShowAcqFields == 'true' || userType != 'SUBADMIN' ? "txnResultDataTable" : "txnResultDataTableWithoutAcqFields";
    var table = $('#' + tableName).DataTable();
    $('#' + tableName).on('click', 'td.my_class1', function () {
      var rowIndex = table.cell(this).index().row;
      var rowData = table.row(rowIndex).data();
      popup(rowData.transactionIdString, rowData.oId, rowData.orderId, rowData.txnType, rowData.pgRefNum, rowData.splitPayment);
    });
  });
});

function renderTable() {
  var monthVal = parseInt(new Date().getMonth()) + 1;
  var merchantEmailId = document.getElementById("merchant").value;
  var table = new $.fn.dataTable.Api('#txnResultDataTable');

  var transFrom = $.datepicker.parseDate('dd-mm-yy', $('#dateFrom').val());
  var transTo = $.datepicker.parseDate('dd-mm-yy', $('#dateTo').val());
  if (transFrom == null || transTo == null) {
    alert('Enter date value');
    return false;
  }

  if (transFrom > transTo) {
    $('#loader-wrapper').hide();
    alert('From date must be before the to date');
    $('#dateFrom').focus();
    return false;
  }
  if (transTo - transFrom > 31 * 86400000) {
    $('#loader-wrapper').hide();
    alert('No. of days can not be more than 31');
    $('#dateFrom').focus();
    return false;
  }
  var token = document.getElementsByName("token")[0].value;
  //$('#loader-wrapper').hide();

  var buttonCommon = {
    exportOptions: {
      format: {
        body: function (data, column, row, node) {
          // Strip $ from salary column to make it numeric
          return column === 0 ? "'" + data : (column === 1 ? "'" + data : data);
        }
      }
    }
  };

  $('#txnResultDataTable').dataTable(
    {
      "footerCallback": function (row, data, start, end, display) {
        var api = this.api(), data;

        // Remove the formatting to get integer data for summation
        var intVal = function (i) {
          return typeof i === 'string' ? i.replace(/[\,]/g, '') * 1 : typeof i === 'number' ? i : 0;
        };

        // Total over all pages
        total = api.column(10).data().reduce(
          function (a, b) {
            return intVal(a) + intVal(b);
          }, 0);

        // Total over this page
        pageTotal = api.column(10, {
          page: 'current'
        }).data().reduce(function (a, b) {
          return intVal(a) + intVal(b);
        }, 0);

        // Update footer
        $(api.column(10).footer()).html(
          '' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));


        // Total over all pages
        total = api.column(11).data().reduce(
          function (a, b) {
            return intVal(a) + intVal(b);
          }, 0);

        // Total over this page
        pageTotal = api.column(11, {
          page: 'current'
        }).data().reduce(function (a, b) {
          return intVal(a) + intVal(b);
        }, 0);

        // Update footer
        $(api.column(11).footer()).html(
          '' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));



      },
      "columnDefs": [{
        className: "dt-body-right",
        "targets": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16]
      }],
      dom: 'BTrftlpi',
      buttons: [
        $.extend(true, {}, buttonCommon, {
          extend: 'copyHtml5',
          exportOptions: {
            columns: [':visible']
          },
        }),
        $.extend(true, {}, buttonCommon, {
          extend: 'csvHtml5',
          title: 'SearchPayment_Transactions_' + (new Date().getFullYear()) + (monthVal > 9 ? monthVal : '0' + monthVal) + (new Date().getDate() > 9 ? new Date().getDate() : '0' + new Date().getDate()) + (new Date().getHours() > 9 ? new Date().getHours() : '0' + new Date().getHours()) + (new Date().getMinutes() > 9 ? new Date().getMinutes() : '0' + new Date().getMinutes()) + (new Date().getSeconds() > 9 ? new Date().getSeconds() : '0' + new Date().getSeconds()),
          exportOptions: {
            columns: [':visible'],
            format: {
              body: function (data, row, column, node) {

                if (row == 11) { // check to remove email special chars
                  var newData = data.replace("&#x40;", "@");

                  return newData;
                }

                return data;

              }
            }

          },
        }),
        {
          extend: 'pdfHtml5',
          orientation: 'landscape',
          pageSize: 'legal',
          //footer : true,
          title: 'Search Transactions',
          exportOptions: {
            columns: [':visible']
          },
          customize: function (doc) {
            doc.defaultStyle.alignment = 'center';
            doc.styles.tableHeader.alignment = 'center';
          }
        },
        // Disabled print button.
        /*{extend : 'print',//footer : true,title : 'Search Transactions',exportOptions : {columns : [':visible']}},*/
        {
          extend: 'colvis',
          columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15]
        }],

      "ajax": {

        "url": "transactionSearchActionAdmin",
        "type": "POST",
        "timeout": 0,
        "data": function (d) {
          return generatePostData(d);
        }
      },
      "fnDrawCallback": function () {
        $("#submit").removeAttr("disabled");
        $('#loader-wrapper').hide();
      },
      "searching": false,
      "ordering": false,
      "processing": true,
      "serverSide": true,
      "paginationType": "full_numbers",
      "lengthMenu": [[10, 25, 50, 100], [10, 25, 50, 100]],
      "order": [[2, "desc"]],

      "columnDefs": [
        {
          "type": "html-num-fmt",
          "targets": 4,
          "orderable": true,
          "targets": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18]
        }
      ],

      "columns": [{
        "data": "transactionIdString",
        "className": "txnId my_class1 text-class",
        "width": "2%"
      }, {
        "data": "pgRefNum",
        "className": "payId text-class"

      }, {
        "data": "merchants",
        "className": "text-class"
      }, {
        "data": "dateFrom",
        "className": "text-class"
      }, {
        "data": "orderId",
        "className": "orderId text-class"
      }, {
        "data": "refundOrderId",
        "className": "orderId text-class"
      }, {
        "data": "mopType",
        "className": "mopType text-class"
      }, {
        "data": "paymentMethods",
        "render": function (data, type, full) {
          return full['paymentMethods'] + ' ' + '-'
            + ' ' + full['mopType'];
        },
        "className": "text-class"
      }, {
        "data": "txnType",
        "className": "txnType text-class",
      }, {
        "data": "status",
        "className": "status text-class"
      }, {
        "data": "amount",
        "className": "text-class",
        "render": function (data) {
          return inrFormat(data);
        }
      }
        , {
        "data": "totalAmount",
        "className": "text-class",
        "visible": false,
        "render": function (data) {
          return inrFormat(data);
        }
      }, {
        "data": "payId",
        "visible": false
      }, {
        "data": "customerEmail",
        "className": "text-class"
      }, {
        "data": "customerPhone",
        "className": "text-class"
      }, {
        "data": "acquirerType",
        "className": "acquirerType text-class"
      }, {
        "data": "ipaddress",
        "className": "ipaddress text-class"
      },
      {
        "data": "cardMask",
        "className": "cardMask text-class"
      },
      {
        "data": "rrn",
        "className": "rrn text-class"
      },
      {
        "data": "splitPayment",
        "className": "rrn text-class"
      },
      {
        "data": "cardHolderType",
        "className": "rrn text-class"
      },
      {
        "data": "transactRef",
        "className": "rrn text-class"
      },
      {
        "data": "customerPhone",
        "visible": false
      }, {
        "data": null,
        "visible": false,
        "className": "displayNone",
        "mRender": function (row) {
          return "\u0027" + row.transactionIdString;
        }
      }, {
        "data": "customerPhone",
        "visible": false,
        "className": "displayNone"
      }, {
        "data": "customerPhone",
        "visible": false,
        "className": "displayNone"
      },
      {
        "data": "oId",
        "visible": false,
        "className": "displayNone"
      }]
    });

  $(document).ready(function () {

    var table = $('#txnResultDataTable').DataTable();
    $('#txnResultDataTable').on('click', '.center', function () {
      var columnIndex = table.cell(this).index().column;
      var rowIndex = table.cell(this).index().row;
      var rowNodes = table.row(rowIndex).node();
      var rowData = table.row(rowIndex).data();
      var txnType1 = rowData.txnType;
      var status1 = rowData.status;

      if ((txnType1 == "SALE" && status1 == "Captured") || (txnType1 == "AUTHORISE" && status1 == "Approved") || (txnType1 == "SALE" && status1 == "Settled")) {
        var payId1 = rowData.pgRefNum;
        var orderId1 = rowData.orderId;
        var txnId1 = Number(rowData.transactionIdString);
        document.getElementById('payIdc').value = payId1;
        document.getElementById('orderIdc').value = orderId1;
        document.getElementById('txnIdc').value = txnId1;
        document.chargeback.submit();
      }
    });
  });
}

function renderTableWithoutAcqFields() {
  var monthVal = parseInt(new Date().getMonth()) + 1;
  var merchantEmailId = document.getElementById("merchant").value;
  var table = new $.fn.dataTable.Api('#txnResultDataTableWithoutAcqFields');

  var transFrom = $.datepicker.parseDate('dd-mm-yy', $('#dateFrom').val());
  var transTo = $.datepicker.parseDate('dd-mm-yy', $('#dateTo').val());
  if (transFrom == null || transTo == null) {
    alert('Enter date value');
    return false;
  }

  if (transFrom > transTo) {
    $('#loader-wrapper').hide();
    alert('From date must be before the to date');
    $('#dateFrom').focus();
    return false;
  }
  if (transTo - transFrom > 31 * 86400000) {
    $('#loader-wrapper').hide();
    alert('No. of days can not be more than 31');
    $('#dateFrom').focus();
    return false;
  }
  var token = document.getElementsByName("token")[0].value;
  //$('#loader-wrapper').hide();

  var buttonCommon = {
    exportOptions: {
      format: {
        body: function (data, column, row, node) {
          // Strip $ from salary column to make it numeric
          return column === 0 ? "'" + data : (column === 1 ? "'" + data : data);
        }
      }
    }
  };

  $('#txnResultDataTableWithoutAcqFields').dataTable(
    {
      "footerCallback": function (row, data, start, end, display) {
        var api = this.api(), data;

        // Remove the formatting to get integer data for summation
        var intVal = function (i) {
          return typeof i === 'string' ? i.replace(/[\,]/g, '') * 1 : typeof i === 'number' ? i : 0;
        };

        // Total over all pages
        total = api.column(10).data().reduce(
          function (a, b) {
            return intVal(a) + intVal(b);
          }, 0);

        // Total over this page
        pageTotal = api.column(10, {
          page: 'current'
        }).data().reduce(function (a, b) {
          return intVal(a) + intVal(b);
        }, 0);

        // Update footer
        $(api.column(10).footer()).html(
          '' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));


        // Total over all pages
        total = api.column(11).data().reduce(
          function (a, b) {
            return intVal(a) + intVal(b);
          }, 0);

        // Total over this page
        pageTotal = api.column(11, {
          page: 'current'
        }).data().reduce(function (a, b) {
          return intVal(a) + intVal(b);
        }, 0);

        // Update footer
        $(api.column(11).footer()).html(
          '' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));



      },
      "columnDefs": [{
        className: "dt-body-right",
        "targets": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13]
      }],
      dom: 'BTrftlpi',
      buttons: [
        $.extend(true, {}, buttonCommon, {
          extend: 'copyHtml5',
          exportOptions: {
            columns: [':visible']
          },
        }),
        $.extend(true, {}, buttonCommon, {
          extend: 'csvHtml5',
          title: 'SearchPayment_Transactions_' + (new Date().getFullYear()) + (monthVal > 9 ? monthVal : '0' + monthVal) + (new Date().getDate() > 9 ? new Date().getDate() : '0' + new Date().getDate()) + (new Date().getHours() > 9 ? new Date().getHours() : '0' + new Date().getHours()) + (new Date().getMinutes() > 9 ? new Date().getMinutes() : '0' + new Date().getMinutes()) + (new Date().getSeconds() > 9 ? new Date().getSeconds() : '0' + new Date().getSeconds()),
          exportOptions: {
            columns: [':visible'],
            format: {
              body: function (data, row, column, node) {

                if (row == 11) { // check to remove email special chars
                  var newData = data.replace("&#x40;", "@");

                  return newData;
                }

                return data;

              }
            }

          },
        }),
        {
          extend: 'pdfHtml5',
          orientation: 'landscape',
          pageSize: 'legal',
          //footer : true,
          title: 'Search Transactions',
          exportOptions: {
            columns: [':visible']
          },
          customize: function (doc) {
            doc.defaultStyle.alignment = 'center';
            doc.styles.tableHeader.alignment = 'center';
          }
        },
        // Disabled print button.
        /*{extend : 'print',//footer : true,title : 'Search Transactions',exportOptions : {columns : [':visible']}},*/
        {
          extend: 'colvis',
          columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]
        }],

      "ajax": {

        "url": "transactionSearchActionAdmin",
        "type": "POST",
        "timeout": 0,
        "data": function (d) {
          return generatePostData(d);
        }
      },
      "fnDrawCallback": function () {
        $("#submit").removeAttr("disabled");
        $('#loader-wrapper').hide();
      },
      "searching": false,
      "ordering": false,
      "processing": true,
      "serverSide": true,
      "paginationType": "full_numbers",
      "lengthMenu": [[10, 25, 50, 100], [10, 25, 50, 100]],
      "order": [[2, "desc"]],

      "columnDefs": [
        {
          "type": "html-num-fmt",
          "targets": 4,
          "orderable": true,
          "targets": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15]
        }
      ],

      "columns": [{
        "data": "transactionIdString",
        "className": "txnId my_class1 text-class",
        "width": "2%"
      }, {
        "data": "pgRefNum",
        "className": "payId text-class"

      }, {
        "data": "merchants",
        "className": "text-class"
      }, {
        "data": "dateFrom",
        "className": "text-class"
      }, {
        "data": "orderId",
        "className": "orderId text-class"
      }, {
        "data": "refundOrderId",
        "className": "orderId text-class"
      }, {
        "data": "txnType",
        "className": "txnType text-class",
      }, {
        "data": "status",
        "className": "status text-class"
      }, {
        "data": "amount",
        "className": "text-class",
        "render": function (data) {
          return inrFormat(data);
        }
      }
        , {
        "data": "totalAmount",
        "className": "text-class",
        "visible": false,
        "render": function (data) {
          return inrFormat(data);
        }
      }, {
        "data": "payId",
        "visible": false

      }, {
        "data": "customerEmail",
        "className": "text-class"
      }, {
        "data": "customerPhone",
        "className": "text-class"
      }, {
        "data": "ipaddress",
        "className": "ipaddress text-class"
      },
      {
        "data": "cardMask",
        "className": "cardMask text-class"
      },
      {
        "data": "rrn",
        "className": "rrn text-class"
      },
      {
        "data": "splitPayment",
        "className": "rrn text-class"
      },
      {
        "data": "cardHolderType",
        "className": "rrn text-class"
      },
      {
        "data": "transactRef",
        "className": "rrn text-class"
      },
      {
        "data": "customerPhone",
        "visible": false
      }, {
        "data": null,
        "visible": false,
        "className": "displayNone",
        "mRender": function (row) {
          return "\u0027" + row.transactionIdString;
        }
      }, {
        "data": "customerPhone",
        "visible": false,
        "className": "displayNone"
      }, {
        "data": "customerPhone",
        "visible": false,
        "className": "displayNone"
      },
      {
        "data": "oId",
        "visible": false,
        "className": "displayNone"
      }]
    });

  $(document).ready(function () {

    var table = $('#txnResultDataTableWithoutAcqFields').DataTable();
    $('#txnResultDataTableWithoutAcqFields').on('click', '.center', function () {
      var columnIndex = table.cell(this).index().column;
      var rowIndex = table.cell(this).index().row;
      var rowNodes = table.row(rowIndex).node();
      var rowData = table.row(rowIndex).data();
      var txnType1 = rowData.txnType;
      var status1 = rowData.status;

      if ((txnType1 == "SALE" && status1 == "Captured") || (txnType1 == "AUTHORISE" && status1 == "Approved") || (txnType1 == "SALE" && status1 == "Settled")) {
        var payId1 = rowData.pgRefNum;
        var orderId1 = rowData.orderId;
        var txnId1 = Number(rowData.transactionIdString);
        document.getElementById('payIdc').value = payId1;
        document.getElementById('orderIdc').value = orderId1;
        document.getElementById('txnIdc').value = txnId1;
        document.chargeback.submit();
      }
    });
  });
}


function reloadTable() {
  var datepick = $.datepicker;
  var transFrom = $.datepicker
    .parseDate('dd-mm-yy', $('#dateFrom').val());
  var transTo = $.datepicker.parseDate('dd-mm-yy', $('#dateTo').val());
  if (transFrom == null || transTo == null) {
    alert('Enter date value');
    return false;
  }

  if (transFrom > transTo) {
    alert('From date must be before the to date');
    $('#loader-wrapper').hide();
    $('#dateFrom').focus();
    return false;
  }
  if (transTo - transFrom > 31 * 86400000) {
    alert('No. of days can not be more than 31');
    $('#loader-wrapper').hide();
    $('#dateFrom').focus();
    return false;
  }
  $("#submit").attr("disabled", true);
  var needToShowAcqFields = $("#needToShowAcqFieldsInReport").val();
  var userType = $("#userTypeName").val();
  var tableObj = needToShowAcqFields == 'true' || userType != 'SUBADMIN' ? $('#txnResultDataTable') : $('#txnResultDataTableWithoutAcqFields');
  var table = tableObj.DataTable();
  table.ajax.reload();
}


function generatePostData(d) {
  var token = document.getElementsByName("token")[0].value;
  var merchantEmailId = document.getElementById("merchant").value;
  var transactionType = document.getElementById("transactionType").value;
  var paymentType = document.getElementById("selectBox3") ? document.getElementById("selectBox3").title : '';
  var status = document.getElementById("selectBox4").title;
  var currency = document.getElementById("currency").value;
  var mopType = document.getElementById("selectBox2") ? document.getElementById("selectBox2").title : '';
  var acquirer = document.getElementById("selectBox1") ? document.getElementById("selectBox1").title : '';
  /*added by vijaylakshmi*/
  var ipAddress = document.getElementById("ipAddress").value;
  var totalAmount = document.getElementById("totalAmount").value;
  var rrn = document.getElementById("rrn").value;
  var startTime = document.getElementById("startTime").value;
  var endTime = document.getElementById("endTime").value;
  startTime = startTime != "00:00" ? moment(startTime, ["h:mmA"]).format("HH:mm") : "00:00";
  endTime = endTime != "00:00" ? moment(endTime, ["h:mmA"]).format("HH:mm") : "23:59";

  if (merchantEmailId == '') {
    merchantEmailId = 'ALL'
  }
  if (transactionType == '') {
    transactionType = 'ALL'
  }
  if (paymentType == '') {
    paymentType = 'ALL'
  }
  if (status == '') {
    status = 'ALL'
  }
  if (currency == '') {
    currency = 'ALL'
  }
  if (mopType == '') {
    mopType = 'ALL'
  }
  if (acquirer == '') {
    acquirer = 'ALL'
  }

  var obj = {
    transactionId: document.getElementById("pgRefNum").value,
    orderId: document.getElementById("orderId").value,
    customerEmail: document.getElementById("customerEmail").value,
    customerPhone: document.getElementById("custPhone").value,
    mopType: mopType,
    acquirer: acquirer,
    merchantEmailId: merchantEmailId,
    transactionType: transactionType,
    paymentType: paymentType,
    status: status,
    currency: currency,
    dateFrom: document.getElementById("dateFrom").value,
    dateTo: document.getElementById("dateTo").value,
    draw: d.draw,
    length: d.length,
    start: d.start,
    ipAddress: ipAddress,
    totalAmount: totalAmount,
    startTime: startTime,
    endTime: endTime,
    token: token,
    rrn: rrn,
    "struts.token.name": "token",
  };

  return obj;
}


function totalAmountofAlltxns() {
  //alert('totalAmountofAlltxns functiona called....');
  var token = document.getElementsByName("token")[0].value;
  var merchantEmailId = document.getElementById("merchant").value;
  var transactionType = document.getElementById("transactionType").value;
  var paymentType = document.getElementById("selectBox3") ? document.getElementById("selectBox3").title : '';
  var status = document.getElementById("selectBox4").title;
  var currency = document.getElementById("currency").value;
  var mopType = document.getElementById("selectBox2") ? document.getElementById("selectBox2").title : '';
  var acquirer = document.getElementById("selectBox1") ? document.getElementById("selectBox1").title : '';
  /*added by vijaylakshmi*/
  var ipAddress = document.getElementById("ipAddress").value;
  var totalAmount = document.getElementById("totalAmount").value;

  var startTime = document.getElementById("startTime").value;
  var endTime = document.getElementById("endTime").value;

  startTime = startTime != "00:00" ? moment(startTime, ["h:mmA"]).format("HH:mm") : "00:00";
  endTime = endTime != "00:00" ? moment(endTime, ["h:mmA"]).format("HH:mm") : "23:59";

  if (merchantEmailId == '') {
    merchantEmailId = 'ALL'
  }
  if (transactionType == '') {
    transactionType = 'ALL'
  }
  if (paymentType == '') {
    paymentType = 'ALL'
  }
  if (status == '') {
    status = 'ALL'
  }
  if (currency == '') {
    currency = 'ALL'
  }
  if (mopType == '') {
    mopType = 'ALL'
  }
  if (acquirer == '') {
    acquirer = 'ALL'
  }
  var obj = {
    transactionId: document.getElementById("pgRefNum").value,
    orderId: document.getElementById("orderId").value,
    customerEmail: document.getElementById("customerEmail").value,
    customerPhone: document.getElementById("custPhone").value,
    mopType: mopType,
    acquirer: acquirer,
    merchantEmailId: merchantEmailId,
    transactionType: transactionType,
    paymentType: paymentType,
    status: status,
    currency: currency,
    dateFrom: document.getElementById("dateFrom").value,
    dateTo: document.getElementById("dateTo").value,
    ipAddress: ipAddress,
    totalAmount: totalAmount,
    startTime: startTime,
    endTime: endTime,
    token: token,
    "struts.token.name": "token",
  }




  $.ajax({
    url: "totalAmountOfAllTxnsActionAdmin",
    timeout: 0,
    type: "POST",
    data: obj,
    success: function (response) {
      var responseObj = response.totalFinalResult;

      $("#totalTxnsAmount").empty();
      $("#totalSettledTxnsAmount").empty();

      var table = document.getElementById("totalTxnsAmount");
      var tr = document.createElement('tr');
      var tr1 = document.createElement('tr');
      var tr2 = document.createElement('tr');
      var tr3 = document.createElement('tr');
      var tr4 = document.createElement('tr');
      var tr5 = document.createElement('tr');
      var tr5a = document.createElement('tr');
      var tr6 = document.createElement('tr');
      var tr7 = document.createElement('tr');
      var tr8 = document.createElement('tr');

      var th1 = document.createElement('th');
      var th2 = document.createElement('th');
      var th3 = document.createElement('th');
      var th4 = document.createElement('th');
      th1.appendChild(document.createTextNode("Transaction Type"));
      th2.appendChild(document.createTextNode("Status"));
      th3.appendChild(document.createTextNode("No Of Transactions"));
      th4.appendChild(document.createTextNode("Total Amount of All Transactions"));
      tr.appendChild(th1);
      tr.appendChild(th2);
      tr.appendChild(th3);
      tr.appendChild(th4);

      table.appendChild(tr);

      var td1a = document.createElement('td');
      var td2a = document.createElement('td');
      var td3a = document.createElement('td');
      var td4a = document.createElement('td');


      td1a.appendChild(document.createTextNode('SALE'));
      td2a.appendChild(document.createTextNode('Captured'));
      td3a.appendChild(document.createTextNode(responseObj.totalSaleSuccCount ? responseObj.totalSaleSuccCount : '0'));
      td4a.appendChild(document.createTextNode(inrFormat(responseObj.totalSaleSuccAmount ? responseObj.totalSaleSuccAmount : '0.00')));
      tr1.appendChild(td1a);
      tr1.appendChild(td2a);
      tr1.appendChild(td3a);
      tr1.appendChild(td4a);
      table.appendChild(tr1);


      var td1b = document.createElement('td');
      var td2b = document.createElement('td');
      var td3b = document.createElement('td');
      var td4b = document.createElement('td');

      td1b.appendChild(document.createTextNode(''));
      td2b.appendChild(document.createTextNode('Pending'));
      td3b.appendChild(document.createTextNode(responseObj.totalSalePendingCount ? responseObj.totalSalePendingCount : '0'));
      td4b.appendChild(document.createTextNode(inrFormat(responseObj.totalSalePendingAmount ? responseObj.totalSalePendingAmount : '0.00')));
      tr2.appendChild(td1b);
      tr2.appendChild(td2b);
      tr2.appendChild(td3b);
      tr2.appendChild(td4b);

      table.appendChild(tr2);

      var td1c = document.createElement('td');
      var td2c = document.createElement('td');
      var td3c = document.createElement('td');
      var td4c = document.createElement('td');

      td1c.appendChild(document.createTextNode(''));
      td2c.appendChild(document.createTextNode('Failed'));
      td3c.appendChild(document.createTextNode(responseObj.totalSalefailCount ? responseObj.totalSalefailCount : '0'));
      td4c.appendChild(document.createTextNode(inrFormat(responseObj.totalSalefailAmount ? responseObj.totalSalefailAmount : '0.00')));
      tr3.appendChild(td1c);
      tr3.appendChild(td2c);
      tr3.appendChild(td3c);
      tr3.appendChild(td4c);



      table.appendChild(tr3);

      var td1e = document.createElement('td');
      var td2e = document.createElement('td');
      var td3e = document.createElement('td');
      var td4e = document.createElement('td');

      td1e.appendChild(document.createTextNode(''));
      td2e.appendChild(document.createTextNode('Cancelled'));
      td3e.appendChild(document.createTextNode(responseObj.totalSaleCancelledCount ? responseObj.totalSaleCancelledCount : '0'));
      td4e.appendChild(document.createTextNode(inrFormat(responseObj.totalSaleCancelledAmount ? responseObj.totalSaleCancelledAmount : '0.00')));
      tr5.appendChild(td1e);
      tr5.appendChild(td2e);
      tr5.appendChild(td3e);
      tr5.appendChild(td4e);

      table.appendChild(tr5);

      var td1ez = document.createElement('td');
      var td2ez = document.createElement('td');
      var td3ez = document.createElement('td');
      var td4ez = document.createElement('td');

      td1ez.appendChild(document.createTextNode(''));
      td2ez.appendChild(document.createTextNode('Invalid'));
      td3ez.appendChild(document.createTextNode(responseObj.totalSaleInvalidCount ? responseObj.totalSaleInvalidCount : '0'));
      td4ez.appendChild(document.createTextNode(inrFormat(responseObj.totalSaleInvalidAmount ? responseObj.totalSaleInvalidAmount : '0.00')));
      tr5a.appendChild(td1ez);
      tr5a.appendChild(td2ez);
      tr5a.appendChild(td3ez);
      tr5a.appendChild(td4ez);

      table.appendChild(tr5a);


      var td1f = document.createElement('td');
      var td2f = document.createElement('td');
      var td3f = document.createElement('td');
      var td4f = document.createElement('td');

      td1f.appendChild(document.createTextNode('REFUND'));
      td2f.appendChild(document.createTextNode('Captured'));
      td3f.appendChild(document.createTextNode(responseObj.totalRefundSuccCount ? responseObj.totalRefundSuccCount : '0'));
      td4f.appendChild(document.createTextNode(inrFormat(responseObj.totalRefundSuccAmount ? responseObj.totalRefundSuccAmount : '0.00')));
      tr6.appendChild(td1f);
      tr6.appendChild(td2f);
      tr6.appendChild(td3f);
      tr6.appendChild(td4f);
      table.appendChild(tr6);

      var td1g = document.createElement('td');
      var td2g = document.createElement('td');
      var td3g = document.createElement('td');
      var td4g = document.createElement('td');

      td1g.appendChild(document.createTextNode(''));
      td2g.appendChild(document.createTextNode('Remaining Other Status'));
      td3g.appendChild(document.createTextNode(responseObj.totalRefundFailCount ? responseObj.totalRefundFailCount : '0'));
      td4g.appendChild(document.createTextNode(inrFormat(responseObj.totalRefundFailAmount ? responseObj.totalRefundFailAmount : '0.00')));
      tr7.appendChild(td1g);
      tr7.appendChild(td2g);
      tr7.appendChild(td3g);
      tr7.appendChild(td4g);

      table.appendChild(tr7);


      var td1h = document.createElement('td');
      var td2h = document.createElement('td');
      var td3h = document.createElement('td');
      var td4h = document.createElement('td');

      td1h.appendChild(document.createTextNode('Total'));
      td2h.appendChild(document.createTextNode('ALL'));
      td3h.appendChild(document.createTextNode(responseObj.totaltxns ? responseObj.totaltxns : '0'));
      td4h.appendChild(document.createTextNode(inrFormat(responseObj.totalTxnAmount ? responseObj.totalTxnAmount : '0.00')));

      tr8.appendChild(td1h);
      tr8.appendChild(td2h);
      tr8.appendChild(td3h);
      tr8.appendChild(td4h);

      table.appendChild(tr8);


      // Settled Related Code 

      var table = document.getElementById("totalSettledTxnsAmount");
      var tr = document.createElement('tr');
      var tr1 = document.createElement('tr');
      var tr2 = document.createElement('tr');
      var tr3 = document.createElement('tr');
      var tr4 = document.createElement('tr');
      var tr5 = document.createElement('tr');
      var tr6 = document.createElement('tr');
      var tr7 = document.createElement('tr');
      var tr8 = document.createElement('tr');
      var tr9 = document.createElement('tr');
      var tr10 = document.createElement('tr');
      var tr12 = document.createElement('tr');
      var tr13 = document.createElement('tr');

      var th1 = document.createElement('th');
      var th2 = document.createElement('th');
      var th3 = document.createElement('th');
      var th4 = document.createElement('th');
      th1.appendChild(document.createTextNode("Transaction Type"));
      th2.appendChild(document.createTextNode("Payment Type"));
      th3.appendChild(document.createTextNode("No Of Transactions"));
      th4.appendChild(document.createTextNode("Total Amount of All Transactions"));
      tr.appendChild(th1);
      tr.appendChild(th2);
      tr.appendChild(th3);
      tr.appendChild(th4);

      table.appendChild(tr);

      var td1a = document.createElement('td');
      var td2a = document.createElement('td');
      var td3a = document.createElement('td');
      var td4a = document.createElement('td');


      td1a.appendChild(document.createTextNode('SALE'));
      td2a.appendChild(document.createTextNode('Credit Card'));
      td3a.appendChild(document.createTextNode(responseObj.totalSaleCCcount ? responseObj.totalSaleCCcount : '0'));
      td4a.appendChild(document.createTextNode(inrFormat(responseObj.totalSaleCCAmount ? responseObj.totalSaleCCAmount : '0.00')));
      tr1.appendChild(td1a);
      tr1.appendChild(td2a);
      tr1.appendChild(td3a);
      tr1.appendChild(td4a);
      table.appendChild(tr1);

      var td1b = document.createElement('td');
      var td2b = document.createElement('td');
      var td3b = document.createElement('td');
      var td4b = document.createElement('td');

      td1b.appendChild(document.createTextNode(''));
      td2b.appendChild(document.createTextNode('Debit Card'));
      td3b.appendChild(document.createTextNode(responseObj.totalSaleDCcount ? responseObj.totalSaleDCcount : '0'));
      td4b.appendChild(document.createTextNode(inrFormat(responseObj.totalSaleDCAmount ? responseObj.totalSaleDCAmount : '0.00')));
      tr2.appendChild(td1b);
      tr2.appendChild(td2b);
      tr2.appendChild(td3b);
      tr2.appendChild(td4b);

      table.appendChild(tr2);

      var td1c = document.createElement('td');
      var td2c = document.createElement('td');
      var td3c = document.createElement('td');
      var td4c = document.createElement('td');

      td1c.appendChild(document.createTextNode(''));
      td2c.appendChild(document.createTextNode('Net Banking'));
      td3c.appendChild(document.createTextNode(responseObj.totalSaleNBcount ? responseObj.totalSaleNBcount : '0'));
      td4c.appendChild(document.createTextNode(inrFormat(responseObj.totalSaleNBAmount ? responseObj.totalSaleNBAmount : '0.00')));
      tr3.appendChild(td1c);
      tr3.appendChild(td2c);
      tr3.appendChild(td3c);
      tr3.appendChild(td4c);

      table.appendChild(tr3);

      var td1d = document.createElement('td');
      var td2d = document.createElement('td');
      var td3d = document.createElement('td');
      var td4d = document.createElement('td');

      td1d.appendChild(document.createTextNode(''));
      td2d.appendChild(document.createTextNode('Wallet'));
      td3d.appendChild(document.createTextNode(responseObj.totalSalewalletcount ? responseObj.totalSalewalletcount : '0'));
      td4d.appendChild(document.createTextNode(inrFormat(responseObj.totalSalewalletAmount ? responseObj.totalSalewalletAmount : '0.00')));
      tr4.appendChild(td1d);
      tr4.appendChild(td2d);
      tr4.appendChild(td3d);
      tr4.appendChild(td4d);

      table.appendChild(tr4);

      var td1e = document.createElement('td');
      var td2e = document.createElement('td');
      var td3e = document.createElement('td');
      var td4e = document.createElement('td');

      td1e.appendChild(document.createTextNode(''));
      td2e.appendChild(document.createTextNode('UPI'));
      td3e.appendChild(document.createTextNode(responseObj.totalSaleUPIcount ? responseObj.totalSaleUPIcount : '0'));
      td4e.appendChild(document.createTextNode(inrFormat(responseObj.totalSaleUPIAmount ? responseObj.totalSaleUPIAmount : '0.00')));
      tr5.appendChild(td1e);
      tr5.appendChild(td2e);
      tr5.appendChild(td3e);
      tr5.appendChild(td4e);

      table.appendChild(tr5);

      var td1x = document.createElement('td');
      var td2x = document.createElement('td');
      var td3x = document.createElement('td');
      var td4x = document.createElement('td');

      td1x.appendChild(document.createTextNode('Total SALE'));
      td2x.appendChild(document.createTextNode('ALL Payments'));
      td3x.appendChild(document.createTextNode(responseObj.totalSaleCount ? responseObj.totalSaleCount : '0'));
      td4x.appendChild(document.createTextNode(inrFormat(responseObj.totalSaleAmount ? responseObj.totalSaleAmount : '0.00')));
      tr12.appendChild(td1x);
      tr12.appendChild(td2x);
      tr12.appendChild(td3x);
      tr12.appendChild(td4x);

      table.appendChild(tr12);

      var td1f = document.createElement('td');
      var td2f = document.createElement('td');
      var td3f = document.createElement('td');
      var td4f = document.createElement('td');


      td1f.appendChild(document.createTextNode('REFUND'));
      td2f.appendChild(document.createTextNode('Credit Card'));
      td3f.appendChild(document.createTextNode(responseObj.totalRfCCcount ? responseObj.totalRfCCcount : '0'));
      td4f.appendChild(document.createTextNode(inrFormat(responseObj.totalRfCCAmount ? responseObj.totalRfCCAmount : '0.00')));
      tr6.appendChild(td1f);
      tr6.appendChild(td2f);
      tr6.appendChild(td3f);
      tr6.appendChild(td4f);
      table.appendChild(tr6);

      var td1g = document.createElement('td');
      var td2g = document.createElement('td');
      var td3g = document.createElement('td');
      var td4g = document.createElement('td');

      td1g.appendChild(document.createTextNode(''));
      td2g.appendChild(document.createTextNode('Debit Card'));
      td3g.appendChild(document.createTextNode(responseObj.totalRfDCcount ? responseObj.totalRfDCcount : '0'));
      td4g.appendChild(document.createTextNode(inrFormat(responseObj.totalRfDCAmount ? responseObj.totalRfDCAmount : '0.00')));
      tr7.appendChild(td1g);
      tr7.appendChild(td2g);
      tr7.appendChild(td3g);
      tr7.appendChild(td4g);

      table.appendChild(tr7);

      var td1h = document.createElement('td');
      var td2h = document.createElement('td');
      var td3h = document.createElement('td');
      var td4h = document.createElement('td');

      td1h.appendChild(document.createTextNode(''));
      td2h.appendChild(document.createTextNode('Net Banking'));
      td3h.appendChild(document.createTextNode(responseObj.totalRfNBcount ? responseObj.totalRfNBcount : '0'));
      td4h.appendChild(document.createTextNode(inrFormat(responseObj.totalRfNBAmount ? responseObj.totalRfNBAmount : '0.00')));
      tr8.appendChild(td1h);
      tr8.appendChild(td2h);
      tr8.appendChild(td3h);
      tr8.appendChild(td4h);

      table.appendChild(tr8);

      var td1i = document.createElement('td');
      var td2i = document.createElement('td');
      var td3i = document.createElement('td');
      var td4i = document.createElement('td');

      td1i.appendChild(document.createTextNode(''));
      td2i.appendChild(document.createTextNode('Wallet'));
      td3i.appendChild(document.createTextNode(responseObj.totalRfwalletcount ? responseObj.totalRfwalletcount : '0'));
      td4i.appendChild(document.createTextNode(inrFormat(responseObj.totalRfwalletAmount ? responseObj.totalRfwalletAmount : '0.00')));
      tr9.appendChild(td1i);
      tr9.appendChild(td2i);
      tr9.appendChild(td3i);
      tr9.appendChild(td4i);

      table.appendChild(tr9);

      var td1j = document.createElement('td');
      var td2j = document.createElement('td');
      var td3j = document.createElement('td');
      var td4j = document.createElement('td');

      td1j.appendChild(document.createTextNode(''));
      td2j.appendChild(document.createTextNode('UPI'));
      td3j.appendChild(document.createTextNode(responseObj.totalRfUPIcount ? responseObj.totalRfUPIcount : '0'));
      td4j.appendChild(document.createTextNode(inrFormat(responseObj.totalRfUPIAmount ? responseObj.totalRfUPIAmount : '0.00')));
      tr10.appendChild(td1j);
      tr10.appendChild(td2j);
      tr10.appendChild(td3j);
      tr10.appendChild(td4j);

      table.appendChild(tr10);

      var td1y = document.createElement('td');
      var td2y = document.createElement('td');
      var td3y = document.createElement('td');
      var td4y = document.createElement('td');

      td1y.appendChild(document.createTextNode('Total Refund'));
      td2y.appendChild(document.createTextNode('ALL Payments'));
      td3y.appendChild(document.createTextNode(responseObj.totalRfCount ? responseObj.totalRfCount : '0'));
      td4y.appendChild(document.createTextNode(inrFormat(responseObj.totalRfAmount ? responseObj.totalRfAmount : '0.00')));
      tr13.appendChild(td1y);
      tr13.appendChild(td2y);
      tr13.appendChild(td3y);
      tr13.appendChild(td4y);

      table.appendChild(tr13);


      var tr11 = document.createElement('tr');
      var td1k = document.createElement('td');
      var td2k = document.createElement('td');
      var td3k = document.createElement('td');
      var td4k = document.createElement('td');

      td1k.appendChild(document.createTextNode('Total'));
      td2k.appendChild(document.createTextNode('ALL'));
      td3k.appendChild(document.createTextNode(responseObj.totalSettleCount ? responseObj.totalSettleCount : '0'));
      td4k.appendChild(document.createTextNode(inrFormat(responseObj.totalSettleAmount ? responseObj.totalSettleAmount : '0.00')));

      tr11.appendChild(td1k);
      tr11.appendChild(td2k);
      tr11.appendChild(td3k);
      tr11.appendChild(td4k);

      table.appendChild(tr11);

    },
    error: function (xhr, textStatus, errorThrown) {

    }
  });
}


function popup(txnId, oId, orderId, txnType, pgRefNum, splitPayment) {

  var token = document.getElementsByName("token")[0].value;

  var myData = {
    token: token,
    "struts.token.name": "token",
    "transactionId": txnId,
    "oId": oId,
    "orderId": orderId,
    "txnType": txnType,
    "pgRefNum": pgRefNum
  }
  $.ajax({
    url: "customerAddressActionAdmin",
    timeout: 0,
    type: "POST",
    data: myData,
    success: function (response) {
      var responseObj = response.aaData;
      var transObj = response.trailData[0];
      var txt = document.createElement("textarea");

      /* Start Billing details  */
      $('#sec1 td').eq(0).text(responseObj.custName ? responseObj.custName : 'Not Available');
      $('#sec1 td').eq(1).text(responseObj.custPhone ? responseObj.custPhone : 'Not Available');

      $('#sec2 td').eq(0).text(responseObj.custCity ? responseObj.custCity : 'Not Available');
      $('#sec2 td').eq(1).text(responseObj.custState ? responseObj.custState : 'Not Available');

      $('#sec7 td').eq(0).text(responseObj.custCountry ? responseObj.custCountry : 'Not Available');
      $('#sec7 td').eq(1).text(responseObj.custZip ? responseObj.custZip : 'Not Available');

      $('#address1 td').text(responseObj.custStreetAddress1 ? responseObj.custStreetAddress1 : 'Not Available');
      $('#address2 td').text(responseObj.custStreetAddress2 ? responseObj.custStreetAddress2 : 'Not Available');
      /* End Billing details  */
      /* Start Payment details  */

      $('#sec3chn td').eq(0).text(responseObj.cardHolderName ? responseObj.cardHolderName : 'Not Available');

      $('#sec3 td').eq(0).text(responseObj.cardMask ? responseObj.cardMask : 'Not Available');
      $('#sec3 td').eq(1).text(responseObj.issuer ? responseObj.issuer : 'Not Available');

      var needToShowAcqFields = $("#needToShowAcqFieldsInReport").val();
      var userType = $("#userTypeName").val();
      if (needToShowAcqFields == 'true'|| userType != 'SUBADMIN') {
        $('#sec4 td').eq(0).text(responseObj.acquirerType ? responseObj.acquirerType : 'Not Available');
        $('#sec4 td').eq(1).text(responseObj.mopType ? responseObj.mopType : 'Not Available');
      }

      $('#sec5 td').eq(0).text(responseObj.pgTdr ? responseObj.pgTdr : 'Not Available');
      $('#sec5 td').eq(1).text(responseObj.pgGst ? responseObj.pgGst : 'Not Available');

      $('#sec6 td').eq(0).text(responseObj.acquirerTdr ? responseObj.acquirerTdr : 'Not Available');
      $('#sec6 td').eq(1).text(responseObj.acquirerGst ? responseObj.acquirerGst : 'Not Available');

      $('#udf6 td').eq(0).text(responseObj.udf6 ? responseObj.udf6 : 'Not Available');

      $('#address5 td').text(responseObj.pgTxnMsg ? responseObj.pgTxnMsg : 'Not Available');
      /* End Payment details  */
      /*Added by CS on 27 Dec for Shipping Details */
      $('#sec8 td').eq(0).text(responseObj.custShipName ? responseObj.custShipName : 'Not Available');
      $('#sec8 td').eq(1).text(responseObj.custShipPhone ? responseObj.custShipPhone : 'Not Available');

      $('#sec9 td').eq(0).text(responseObj.custShipCity ? responseObj.custShipCity : 'Not Available');
      $('#sec9 td').eq(1).text(responseObj.custShipState ? responseObj.custShipState : 'Not Available');

      $('#sec10 td').eq(0).text(responseObj.custShipCountry ? responseObj.custShipCountry : 'Not Available');
      $('#sec10 td').eq(1).text(responseObj.custShipZip ? responseObj.custShipZip : 'Not Available');


      $('#address3 td').text(responseObj.custShipStreetAddress1 ? responseObj.custShipStreetAddress1 : 'Not Available');
      $('#address4 td').text(responseObj.custShipStreetAddress2 ? responseObj.custShipStreetAddress2 : 'Not Available');
      /*End Shipping details */
      /*Start Transaction details */


      $("#transactionDetails").empty();
      var table = document.getElementById("transactionDetails");
      var tr = document.createElement('tr');
      var th1 = document.createElement('th');
      var th2 = document.createElement('th');
      var th3 = document.createElement('th');
      var th4 = document.createElement('th');
      th1.appendChild(document.createTextNode("Order ID"));
      th2.appendChild(document.createTextNode("Transaction Type"));
      th3.appendChild(document.createTextNode("Date"));
      th4.appendChild(document.createTextNode("Status"));
      tr.appendChild(th1);
      tr.appendChild(th2);
      tr.appendChild(th3);
      tr.appendChild(th4);

      table.appendChild(tr);

      var totalRefundamount = 0;
      var totalSaleamount = 0;
      var internalCustip;

      var curAmount;
      var curStatus;
      var curOrderId;
      var curPgRefNum;
      var curArn;
      var curRrn;

      var curTxnType;
      for (var i = 0; i < response.trailData.length; i++) {

        var tr2 = document.createElement('tr');

        var td1 = document.createElement('td');
        var td2 = document.createElement('td');
        var td3 = document.createElement('td');
        var td4 = document.createElement('td');


        if (response.trailData[i].txnType == "REFUND") {
          txt.innerHTML = (response.trailData[i].refundOrderId ? response.trailData[i].refundOrderId : 'Not Available');
          td1.appendChild(document.createTextNode(txt.value));
        }
        if (response.trailData[i].txnType == "SALE") {
          txt.innerHTML = (response.trailData[i].orderId ? response.trailData[i].orderId : 'Not Available');
          td1.appendChild(document.createTextNode(txt.value));
        }

        td2.appendChild(document.createTextNode(response.trailData[i].txnType ? response.trailData[i].txnType : 'Not Available'));
        td3.appendChild(document.createTextNode(response.trailData[i].createDate ? response.trailData[i].createDate : 'Not Available'));
        td4.appendChild(document.createTextNode(response.trailData[i].status ? response.trailData[i].status : 'Not Available'));

        tr2.appendChild(td1);
        tr2.appendChild(td2);
        tr2.appendChild(td3);
        tr2.appendChild(td4);

        table.appendChild(tr2);
        if (response.trailData[i].txnType == "REFUND" && response.trailData[i].status == "Captured") {
          totalRefundamount += (response.trailData[i].amount ? parseInt(response.trailData[i].amount) : 0);
        }

        if (response.trailData[i].txnType == "SALE" && response.trailData[i].status == "Captured") {
          totalSaleamount += (response.trailData[i].amount ? parseInt(response.trailData[i].amount) : 0);
        }

        if (response.trailData[i].transactionId == txnId) {
          curAmount = (response.trailData[i].amount ? response.trailData[i].amount : 0);
          curStatus = (response.trailData[i].currentStatus ? response.trailData[i].currentStatus : 0);
          if (response.trailData[i].txnType == "SALE") {
            curOrderId = (response.trailData[i].orderId ? response.trailData[i].orderId : 0);
          } else {
            curOrderId = (response.trailData[i].refundOrderId ? response.trailData[i].refundOrderId : 0);
          }

          curPgRefNum = (response.trailData[i].pgRefNum ? response.trailData[i].pgRefNum : 0);
          curArn = (response.trailData[i].arn ? response.trailData[i].arn : 0);
          curRrn = (response.trailData[i].rrn ? response.trailData[i].rrn : 0);
          curTxnType = (response.trailData[i].txnType ? response.trailData[i].txnType : 'Not Available');
        }

        internalCustip = (response.trailData[i].internalCustIP ? response.trailData[i].internalCustIP : 0);
      }

      $('#sec14 td').eq(0).text(transObj.acqId ? transObj.acqId : 'Not Available');
      $('#sec14 td').eq(1).text(inrFormat(totalRefundamount ? totalRefundamount + ".00" : 'Not Available'));

      $('#sec16 td').eq(0).text(internalCustip ? internalCustip : 'Not Available');
      $('#sec16 td').eq(1).text(inrFormat(totalSaleamount ? totalSaleamount + ".00" : 'Not Available'));

      if (curTxnType == "SALE") {
        $('#sec16 td').eq(1).css("display", "none");
        $('#sec16h th').eq(1).css("display", "none");
      }
      if (curTxnType == "REFUND") {
        $('#sec16 td').eq(1).css("display", "block");
        $('#sec16h th').eq(1).css("display", "block");
      }


      $('#sec11 td').eq(0).text(inrFormat(curAmount ? curAmount : 'Not Available'));
      $('#sec11 td').eq(1).text(curStatus ? curStatus : 'Not Available');


      txt.innerHTML = (curOrderId ? curOrderId : 'Not Available');

      $('#sec12 td').eq(0).text(txt.value);
      $('#sec12 td').eq(1).text(curPgRefNum ? curPgRefNum : 'Not Available');

      $('#sec13 td').eq(0).text(curArn ? curArn : 'Not Available');
      $('#sec13 td').eq(1).text(curRrn ? curRrn : 'Not Available');




      /*End Transaction details */


      // $('#address6 td').text(responseObj.custShipStreetAddress1 ? responseObj.custShipStreetAddress2 : 'Not Available');

      // $('#auth td').text(responseObj.internalTxnAuthentication ? responseObj.internalTxnAuthentication : 'Not Available');

      if (splitPayment == 'False') {
        $(".udf6Box").hide();
      } else {
        $(".udf6Box").show();
      }

      $('#popup').show();
    },
    error: function (xhr, textStatus, errorThrown) {

    }
  });

};


function validPgRefNum() {

  var pgRefValue = document.getElementById("pgRefNum").value;
  var regex = /^(?!0{16})[0-9\b]{16}$/;
  if (pgRefValue.trim() != "") {
    if (!regex.test(pgRefValue)) {
      document.getElementById("validValue").style.display = "block";
      document.getElementById("submit").disabled = true;
      document.getElementById("download").disabled = true;
    }
    else {
      if (document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validPhoneValue").style.display != "block" && document.getElementById("validOrderIdValue").style.display != "block") {
        document.getElementById("submit").disabled = false;
        document.getElementById("download").disabled = false;
      }
      document.getElementById("validValue").style.display = "none";
    }
  }
  else {
    if (document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validPhoneValue").style.display != "block" && document.getElementById("validOrderIdValue").style.display != "block") {
      document.getElementById("submit").disabled = false;
      document.getElementById("download").disabled = false;
    }
    document.getElementById("validValue").style.display = "none";
  }
}

function validateCustomerEmail(emailField) {

  var reg = /^([A-Za-z0-9_\-\.\+])+\@([A-Za-z0-9_\-\+])+\.([A-Za-z]{2,4})$/;
  if (emailField.value !== "") {
    if (reg.test(emailField.value) == false) {
      document.getElementById("validEamilValue").style.display = "block";
      document.getElementById("submit").disabled = true;
      document.getElementById("download").disabled = true;
    } else {
      if (document.getElementById("validValue").style.display != "block" && document.getElementById("validPhoneValue").style.display != "block" && document.getElementById("validOrderIdValue").style.display != "block") {
        document.getElementById("submit").disabled = false;
        document.getElementById("download").disabled = false;
      }
      document.getElementById("validEamilValue").style.display = "none";
    }
  } else {
    if (document.getElementById("validValue").style.display != "block" && document.getElementById("validPhoneValue").style.display != "block" && document.getElementById("validOrderIdValue").style.display != "block") {
      document.getElementById("submit").disabled = false;
      document.getElementById("download").disabled = false;
    }
    document.getElementById("validEamilValue").style.display = "none";
  }

}

function validateCustomerPhone(phoneField) {
  var phreg = /^([0]|\+91)?[- ]?[56789]\d{9}$/;
  // mobileRegex = /^[789]\d{9}/ --> this regex we are using the sign up page
  if (phoneField.value !== "") {

    if (phreg.test(phoneField.value) == false) {
      document.getElementById("validPhoneValue").style.display = "block";
      document.getElementById("submit").disabled = true;
      document.getElementById("download").disabled = true;
    } else {
      if (document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validValue").style.display != "block" && document.getElementById("validOrderIdValue").style.display != "block") {
        document.getElementById("submit").disabled = false;
        document.getElementById("download").disabled = false;
      }
      document.getElementById("validPhoneValue").style.display = "none";

    }
  } else {
    if (document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validValue").style.display != "block" && document.getElementById("validOrderIdValue").style.display != "block") {
      document.getElementById("submit").disabled = false;
      document.getElementById("download").disabled = false;
    }
    document.getElementById("validPhoneValue").style.display = "none";
  }

}
function valdPhoneNo() {
  var phoneElement = document.getElementById("custPhone");
  var value = phoneElement.value.trim();
  if (value.length > 0) {
    var phone = phoneElement.value;
    var phoneexp = /^[0-9]{8,13}$/;
    if (!phone.match(phoneexp)) {
      document.getElementById('validPhoneValue').innerHTML = "Please enter valid Phone";

      return false;
    } else {
      document.getElementById('validPhoneValue').innerHTML = "";

      return true;
    }
  } else {
    phoneElement.focus();
    document.getElementById('validPhoneValue').innerHTML = "";

    return true;
  }
}

function removeSpaces(fieldVal) {
  setTimeout(function () {
    var nospacepgRefVal = fieldVal.value.replace(/ /g, "");
    fieldVal.value = nospacepgRefVal;
  }, 400);
}

function validateOrderIdvalue(orderId) {
  setTimeout(function () {
    //var orderIdreg =/^[0-9a-zA-Z\b\_-\s\+?.*?]+$/;
    var orderIdreg = /^[0-9a-zA-Z\b\_-\s\+.]+$/;
    if (orderId.value !== "") {
      if (orderIdreg.test(orderId.value) == false) {
        document.getElementById("validOrderIdValue").style.display = "block";
        document.getElementById("submit").disabled = true;
        document.getElementById("download").disabled = true;
      } else {
        if (document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validValue").style.display != "block" && document.getElementById("validPhoneValue").style.display != "block") {
          document.getElementById("submit").disabled = false;
          document.getElementById("download").disabled = false;
        }
        document.getElementById("validOrderIdValue").style.display = "none";

      }
    } else {
      if (document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validValue").style.display != "block" && document.getElementById("validPhoneValue").style.display != "block") {
        document.getElementById("submit").disabled = false;
        document.getElementById("download").disabled = false;
      }
      document.getElementById("validOrderIdValue").style.display = "none";
    }
  }, 400);
}

function validateOrderId(event) {
  //var regex = /^[0-9a-zA-Z\b\_-\s\+?.*?]+$/;
  var regex = /^[0-9a-zA-Z\b\_-\s\+.]+$/;
  var key = String.fromCharCode(!event.charCode ? event.which : event.charCode);
  if (!regex.test(key)) {
    event.preventDefault();
    return false;
  }
}