$(document).ready(function () {
    $(function () {
        loadCurrencies();
    });
});

function loadCurrencies() {
    var urls = new URL(window.location.href);
    var domain = urls.origin;
    var selectedMerchant = $("#merchant").val();
    if (selectedMerchant === "ALL MERCHANTS" || selectedMerchant === "") {
        selectedMerchant = ""; //
    }
    $.get(domain + "/crmws/dashboard/getUserCurrency?emailId=" + selectedMerchant, function(response) {
        console.log(response);

        var options = '<option value="All Currency" selected>All Currency</option>';
        for (var i = 0; i < response.length; i++) {
            var el = response[i];
            console.log(el);
            options += '<option value="' + el.name + '" data-symbol="' + el.symbol + '">' + el.name + '</option>';
        }
        $("#currency").html(options);
    });
}

function submit() {
    if($("#merchant").val()){
       // alert("submit :- " + $("#merchant").val());
       // alert("submit of currency as well :- " + $("#currency").val());
       renderTableAcquirer();
    }
}

function renderTableAcquirer() {
    var merchantBalanceTable = $("#merchantBalance").DataTable();
    if ($.fn.DataTable.isDataTable('#merchantBalance')) {
        merchantBalanceTable.destroy();
    }
    var merchantcolumns = [
        { mData: "businessName" },
        { mData: "currency" },
        { mData: "availableBalance" },
        { mData: "totalBalance" },
    ];
    var columnsBasedOnRole = merchantcolumns;
    $('#merchantBalance').DataTable({
        dom: 'BTftlpi',
        scrollY: true,
        scrollX: true,
        ajax: {
            url: "merchantWalletAction",
            type: "POST",
            data: function(d) {
                return $.extend({}, d, {
                    merchant: $("#merchant").val(),
                    currency: $("#currency").val()
                });
            },
            dataSrc: function(json) {
                console.log("Complete data:", json);
                console.table(json.aaData);
                return json.aaData.map(function(item) {
                    return {
                        businessName: item.businessName,
                        currency: item.currency,
                        availableBalance: item.availableBalance,
                        totalBalance: item.totalBalance
                    };
                });
            }
        },
        searching: false,
        ordering: false,
        processing: true,
        serverSide: false,
        paginationType: "full_numbers",
        lengthMenu: [[10, 25, 50, 100], [10, 25, 50, 100]],
        order: [[2, "desc"]],
        columns: columnsBasedOnRole
    });
}

function generateData(d) {
    return $.extend({}, d, {
        merchant: $("#merchant").val(),
        currency: $("#currency").val()
    });
}