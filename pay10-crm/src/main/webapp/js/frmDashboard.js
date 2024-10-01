$(document).ready(function() {
  // document.getElementById("loadingInner").style.display = "none";

  $(function() {
	  
	  var fromDate = new Date();
	  fromDate.setMonth(fromDate.getMonth() -1);
	  fromDate.setDate(fromDate.getDate() - 1);
	  
		$("#dateFrom").flatpickr({
			minDate : fromDate,
			maxDate: new Date(),
			dateFormat : 'Y-m-d',
			defaultDate: "today"
			
		});
		$("#dateTo").flatpickr({
			minDate : fromDate,
			maxDate: new Date(),
			dateFormat : 'Y-m-d',
			defaultDate: "today"
		});
  });

  $(function() {
   
    $(document).ready(function() {
      var fromDate = document.getElementById("dateFrom").value;
      var toDate = document.getElementById("dateTo").value;
      $.ajax({

        type: "GET",
        url: "getMerchantFrmDetails",

        data: {
          "dateFrom": fromDate,
          "dateTo": toDate,
        },
        success: function(response) {
          console.log(response)
          var responseObj = JSON.parse(JSON.stringify(response));
          renderTable(responseObj.aaData);
        },
      });
    });
  });
});

function search() {

  var fromDate = document.getElementById("dateFrom").value;
  var toDate = document.getElementById("dateTo").value;
  if (fromDate > toDate) {
	    alert('From date must be before the to date');
	    $('#loader-wrapper').hide();
	    $('#dateFrom').focus();
	    return false;
	  }

  $.ajax({

    type: "GET",
    url: "getMerchantFrmDetails",

    data: {
    	"dateFrom": fromDate,
        "dateTo": toDate,
    },
    success: function(response) {
    var responseObj = JSON.parse(JSON.stringify(response));
    renderTable(responseObj.aaData);

    }
  });
}

function renderTable(data) {
  var buttonCommon = {
    exportOptions: {
      format: {
        body: function(data, column, row, node) {
          // Strip $ from salary column to make it numeric
          return (column === 1 ? "'" + data : data);
        }
      }
    }
  };

  $("#datatable").DataTable().destroy();

  $('#datatable').dataTable({
	    'data': data,

	    dom: 'BTrftlpi',
	    buttons: [
	      $.extend(true, {}, buttonCommon, {
	        extend: 'copyHtml5',
	        exportOptions: {
	          columns: [0, 1, 2, 3,4]
	        },
	      }),
	      $.extend(true, {}, buttonCommon, {
	        extend: 'csvHtml5',
	        title: 'Frm_Dashboard_Report',
	        exportOptions: {
	         columns: [0, 1, 2, 3,4],
	        },
	      }),
	      {
	        extend: 'pdfHtml5',
	        orientation: 'landscape',
	        pageSize: 'legal',
	        // footer : true,
	        title: 'Frm_Dashboard_Report',
	        exportOptions: {
	         columns: [0, 1, 2, 3,4]
	        },
	        customize: function(doc) {
	          doc.defaultStyle.alignment = 'center';
	          doc.styles.tableHeader.alignment = 'center';
	        }
	      },

	      {
	        extend: 'colvis',
	        columns: [0, 1, 2, 3,4]
	      },
	],

	"aoColumns": [{
	  "mData": null,
	  render: (data, type, row, meta) => meta.row + 1
	},
	    
	      {
	        'data': 'merchantByVolume'
	      },
	      {
	        'data': 'merchantByCount'
	      },
	      {
		        'data': 'frmBreachValue'
		      },
	      {
	        'data': 'leastPerformerTSR'
	      }]
	  });
}