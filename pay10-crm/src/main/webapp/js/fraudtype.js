//var ipRuleColumns = ['ipAddListBody','payId','ipAddress','dateActiveFrom','dateActiveTo','startTime','endTime','repeatDays'];
// var wlIpRuleColumns = ['wlIpAddListBody','payId','whiteListIpAddress','dateActiveFrom','dateActiveTo','startTime','endTime','repeatDays'];
// var  domainRuleColumns = ['domainListBody','payId','domainName','dateActiveFrom','dateActiveTo','startTime','endTime','repeatDays'];
// var emailRuleColumns = ['emailListBody','payId','email','dateActiveFrom','dateActiveTo','startTime','endTime','repeatDays'];
// var  userCountryColumns = ['userCountryListBody','payId','userCountry'];
// var issuerCountryColumns = ['issuerCountryListBody','payId','issuerCountry'];
// var perMerchantTxnsColumns = ['noOfTxnsListBody','payId','minutesTxnLimit', 'perCardTransactionAllowed'];
// var cardBinColumns = ['cardBinListBody','payId','negativeBin','dateActiveFrom','dateActiveTo','startTime','endTime','repeatDays'];
// var cardNoColumns = ['cardNoListBody','payId','negativeCard','dateActiveFrom','dateActiveTo','startTime','endTime','repeatDays'];
// var txnAmountColumns = ['txnAmountListBody','payId','currency', 'minTransactionAmount', 'maxTransactionAmount'];
// var perCardTxnsColumns = ['perCardTxnsListBody','payId','negativeCard','perCardTransactionAllowed','dateActiveFrom','dateActiveTo','startTime','endTime','repeatDays'];
$(document).ready(
    function () {
        $('input:file').change(
            function () {
                if ($(this).val()) {
                    $('input:submit').attr('disabled', false);
                }
            }
        );
    });
$(document).ready(function () {
    getDefaultCurrency();
    $('#select_all').click();
    $('#select_all').click();
    $('#select_all2').click();
    $('#select_all2').click();
    $('#select_all3').click();
    $('#select_all3').click();
    $('#select_all4').click();
    $('#select_all4').click();
    $('#select_all6').click();
    $('#select_all6').click();
    $('#select_all7').click();
    $('#select_all7').click();
    $('#select_all8').click();
    $('#select_all8').click();
    $('#select_all9').click();
    $('#select_all9').click();
    $('#select_all10').click();
    $('#select_all10').click();
    $('table.csv').DataTable({
        dom: 'B',
        buttons: [
            {
                extend: 'csv',
                text: 'GET SAMPLE CSV',
                filename: 'IP_ADDRESS',
            }]
    });
    $('table.csv2').DataTable({
        dom: 'B',
        buttons: [
            {
                extend: 'csv',
                text: 'GET SAMPLE CSV',
                filename: 'EMAIL_ADDRESS',
            }]
    });
    $('table.csv4').DataTable({
        dom: 'B',
        buttons: [
            {
                extend: 'csv',
                text: 'GET SAMPLE CSV',
                filename: 'CARD_RANGE',
            }]
    });
    $('table.csv5').DataTable({
        dom: 'B',
        buttons: [
            {
                extend: 'csv',
                text: 'GET SAMPLE CSV',
                filename: 'CARD_MASK',
            }]
    });
    $('table.csv6').DataTable({
        dom: 'B',
        buttons: [
            {
                extend: 'csv',
                text: 'GET SAMPLE CSV',
                filename: 'CARD_NUMBER_TRANSACTION',
            }]
    });
    $('table.csv7').DataTable({
        dom: 'B',
        buttons: [
            {
                extend: 'csv',
                text: 'GET SAMPLE CSV',
                filename: 'PHONE_NUMBER',
            }]
    });
    $('table.csv9').DataTable({
        dom: 'B',
        buttons: [
            {
                extend: 'csv',
                text: 'GET SAMPLE CSV',
                filename: 'MACK_ADDRESS',
            }]
    });
    $('table.csv10').DataTable({
        dom: 'B',
        buttons: [
            {
                extend: 'csv',
                text: 'GET SAMPLE CSV',
                filename: 'REPEATED_PAYMENT_TYPE',
            }]
    });
    $('table.csv20').DataTable({
        dom: 'B',
        buttons: [
            {
                extend: 'csv',
                text: 'GET SAMPLE CSV',
                filename: 'VPA_ADDRESS',
            }]
    });
});

var fileTypes = ['csv'];  //acceptable file types
function readURL(input) {
    debugger
    if (input.files && input.files[0]) {
        var extension = input.files[0].name.split('.').pop().toLowerCase(),  //file extension from input file
            isSuccess = fileTypes.indexOf(extension) > -1;  //is extension in acceptable types

        if (isSuccess) { //yes
            //document.getElementById('filenamevpa').innerHTML = input.files[0].name;

            var reader = new FileReader();
            reader.onload = function (e) {
                if (extension == 'csv') {

                    $(input).closest('.fileUpload').find(".icon").attr('src', 'https://www.cortechslabs.com/wp-content/uploads/2017/06/CSV-icon-new.png');
                }

                else {
                    //debugger;
                    //console.log('here=>'+$(input).closest('.uploadDoc').length);
                    $(input).val('');
                    document.getElementById("up").value = "";
                    //$("#up").replaceWith($("#up").val('').clone(true));
                    $(input).closest('.uploadDoc').find(".docErr").slideUp('slow');


                }
            }

            reader.readAsDataURL(input.files[0]);
        }
        else {
            //debugger;
            //console.log('here=>'+$(input).closest('.uploadDoc').find(".docErr").length);
            $(input).closest('.uploadDoc').find(".docErr").fadeIn();
            document.getElementById("up").value = "";
            $(input).val('');
            setTimeout(function () {
                $('.docErr').fadeOut('slow');

            }, 9000);
        }
    }
}
$(document).ready(function () {

    $(document).on('change', '.up', function () {
        var id = $(this).attr('id'); /* gets the filepath and filename from the input */
        var profilePicValue = $(this).val();
        var fileNameStart = profilePicValue.lastIndexOf('\\'); /* finds the end of the filepath */
        profilePicValue = profilePicValue.substr(fileNameStart + 1).substring(0, 20); /* isolates the filename */
        //var profilePicLabelText = $(".upl"); /* finds the label text */
        if (profilePicValue != '') {
            //console.log($(this).closest('.fileUpload').find('.upl').length);
            $(this).closest('.fileUpload').find('.upl').html(profilePicValue); /* changes the label text */
        }
    });


    $(document).on("click", "a.btn-check", function () {
        if ($(".uploadDoc").length > 1) {
            $(this).closest(".uploadDoc").remove();
        } else {
            alert("You have to upload at least one document.");
        }
    });
});

var ajaxValidationFlag = false;


var numberOfTransaction = 'BLOCK_NO_OF_TXNS';
var transactionAmount = 'BLOCK_TXN_AMOUNT';
var ipAddressS = 'BLOCK_IP_ADDRESS';
var emailS = 'BLOCK_EMAIL_ID';
var userCountry = 'BLOCK_USER_COUNTRY';
var userState = 'BLOCK_USER_STATE';
var userCity = 'BLOCK_USER_CITY';
var issuerCountry = 'BLOCK_CARD_ISSUER_COUNTRY';
var negativeBinS = 'BLOCK_CARD_BIN';
var negativeCardS = 'BLOCK_CARD_NO';
var perCardTransactionAllowedS = 'BLOCK_CARD_TXN_THRESHOLD';
var phoneS = "BLOCK_PHONE_NUMBER";
var transactionAmountVelocity = 'BLOCK_TXN_AMOUNT_VELOCITY';
var repeatedMopTypeForSameDetails = 'BLOCK_REPEATED_MOP_TYPE_FOR_SAME_DETAIL';
var mackAddressS = 'BLOCK_MACK_ADDRESS';
var repeatedMopTypesS = 'REPEATED_MOP_TYPES';
var notifyFirstTransactions = 'FIRST_TRANSACTIONS_ALERT';
//Added By Sweety
var vpaAddressS = 'BLOCK_VPA_ADDRESS';

function editFraudRule(fraudData) {
    let id = fraudData['id'];
    let fraudType = fraudData['fraudType'];
    let always = fraudData['alwaysOnFlag'];
    $("#ruleId").val(id);
    switch (fraudType) {
        case "BLOCK_IP_ADDRESS":
            let ipAddress = fraudData['ipAddress'];
            let ipAddressArr = ipAddress.split(",");
            $("#ipAddress").val(ipAddress);
            let element = $("#ipAddress");
            for (let i = 0; i < ipAddressArr.length; i++) {
                element.tagsinput('add', ipAddressArr[i]);
            }
            document.getElementById("validate_err").style.display = "none";
            if (always) {
                document.getElementById('alwaysOnFlag1').click();
            } else {
                $("#dateActiveFrom").val(fraudData['dateActiveFrom']);
                $("#dateActiveTo").val(fraudData['dateActiveTo']);
                $("#startTime").val(fraudData['startTime']);
                $("#endTime").val(fraudData['endTime']);
                let repeatDays = fraudData['repeatDays'];
                for (let i = 0; i <= 6; i++) {
                    let element = document.getElementById("repeatDays" + i);
                    if (repeatDays.includes(element.value)) {
                        element.click();
                    }
                }
            }
            break;

        case "BLOCK_EMAIL_ID":
            let emails = fraudData['email'];
            $("#email").val(emails);
            $("#email").tagsinput('add', emails);
            document.getElementById("validate_email").style.display = "none";
            if (always) {
                document.getElementById('alwaysOnFlag4').click();
            } else {
                $("#dateActiveFrom3").val(fraudData['dateActiveFrom']);
                $("#dateActiveTo3").val(fraudData['dateActiveTo']);
                $("#startTime3").val(fraudData['startTime']);
                $("#endTime3").val(fraudData['endTime']);
                let repeatDays = fraudData['repeatDays'];
                for (let i = 21; i <= 27; i++) {
                    let element = document.getElementById("repeatDays" + i);
                    if (repeatDays.includes(element.value)) {
                        element.click();
                    }
                }
            }
            break;
        case "BLOCK_TXN_AMOUNT":
            let fixedAmountFlag = fraudData['fixedAmountFlag'];
            if (fixedAmountFlag) {
                document.getElementById("fixedAmountFlag").click();
                $("#timeDuration").val(fraudData['blockTimeUnits']);
                $("#transactionAmount").val(fraudData['transactionAmount']);
                $("#repetationCount").val(fraudData['repetationCount']);
            } else {
                $("#minTransactionAmount").val(fraudData['minTransactionAmount']);
                $("#maxTransactionAmount").val(fraudData['maxTransactionAmount']);
            }
            $("#fixedAmountFlag").attr("disabled", "disabled");
            break;
        case "BLOCK_CARD_BIN":
            let negativeBin = fraudData['negativeBin'];
            $("#negativeBin").val(negativeBin);
            $("#negativeBin").tagsinput('add', negativeBin);
            document.getElementById("validate_crd").style.display = "none";
            if (always) {
                document.getElementById('alwaysOnFlag3').click();
            } else {
                $("#dateActiveFrom4").val(fraudData['dateActiveFrom']);
                $("#dateActiveTo4").val(fraudData['dateActiveTo']);
                $("#startTime4").val(fraudData['startTime']);
                $("#endTime4").val(fraudData['endTime']);
                let repeatDays = fraudData['repeatDays'];
                for (let i = 14; i <= 20; i++) {
                    let element = document.getElementById("repeatDays" + i);
                    if (repeatDays.includes(element.value)) {
                        element.click();
                    }
                }
            }
            break;
        case "BLOCK_CARD_NO":
            let negativeCard = fraudData['negativeCard'];
            let firstSix = negativeCard.substring(0, negativeCard.length - 10);
            let lastFour = negativeCard.substring(12, negativeCard.length);
            $("#cardIntialDigits").val(firstSix);
            $("#cardLastDigits").val(lastFour);
            if (always) {
                document.getElementById('alwaysOnFlag6').click();
            } else {
                $("#dateActiveFrom5").val(fraudData['dateActiveFrom']);
                $("#dateActiveTo5").val(fraudData['dateActiveTo']);
                $("#startTime5").val(fraudData['startTime']);
                $("#endTime5").val(fraudData['endTime']);
                let repeatDays = fraudData['repeatDays'];
                for (let i = 35; i <= 41; i++) {
                    let element = document.getElementById("repeatDays" + i);
                    if (repeatDays.includes(element.value)) {
                        element.click();
                    }
                }
            }
            break;
        case "BLOCK_PHONE_NUMBER":
            let phones = fraudData['phone'];
            $("#phone").val(phones);
            $("#phone").tagsinput('add', phones);
            document.getElementById("validate_phone").style.display = "none";
            if (always) {
                document.getElementById('alwaysOnFlag12').click();
            } else {
                $("#dateActiveFrom12").val(fraudData['dateActiveFrom']);
                $("#dateActiveTo12").val(fraudData['dateActiveTo']);
                $("#startTime12").val(fraudData['startTime']);
                $("#endTime12").val(fraudData['endTime']);
                let repeatDays = fraudData['repeatDays'];
                for (let i = 49; i <= 55; i++) {
                    let element = document.getElementById("repeatDays" + i);
                    if (repeatDays.includes(element.value)) {
                        element.click();
                    }
                }
            }
            break;
        case "BLOCK_TXN_AMOUNT_VELOCITY":
            let monitoringTypes = fraudData['monitoringType'].split(",");
            for (let i = 0; i < monitoringTypes.length; i++) {
                if (monitoringTypes[i] == "Notify") {
                    document.getElementById("notifyVelocityId").click();
                }
                if (monitoringTypes[i] == "Block") {
                    document.getElementById("blockVelocityId").click();
                }
            }
            $("#currencyVelocity option").each(function () {
                if (this.innerHTML == fraudData['currency']) {
                    this.selected = true;
                }
            });
            $("#statusVelocity").val(fraudData['statusVelocity']);
            $("#timeDurationVelocity").val(fraudData['blockTimeUnits']);
            let userIdentifier = fraudData['userIdentifier'].split(",");
            for (let j = 0; j < userIdentifier.length; j++) {
                if (userIdentifier[j] == "Email") {
                    document.getElementById("emailBlock").click();
                }
                if (userIdentifier[j] == "Phone") {
                    document.getElementById("phoneBlock").click();
                }
                if (userIdentifier[j] == "Card") {
                    document.getElementById("cardBlock").click();
                }
                if (userIdentifier[j] == "ipAddress") {
                    document.getElementById("ipAddrBlock").click();
                }
                if (userIdentifier[j] == "VPA") {
                    document.getElementById("vpaBlock").click();
                }
            }
            $("#repetationCountVelocity").val(fraudData['repetationCount']);
            $("#txnAmountVelocity").val(fraudData['maxTransactionAmount']);
            let emailToNotifyVelocity = fraudData['emailToNotify'];
            $("#emailToNotifyVelocity").val(emailToNotifyVelocity);
            $("#emailToNotifyVelocity").tagsinput('add', emailToNotifyVelocity);
            break;
        case "BLOCK_REPEATED_MOP_TYPE_FOR_SAME_DETAIL":
            let monitoringTypesRepeatedMop = fraudData['monitoringType'].split(",");
            for (let i = 0; i < monitoringTypesRepeatedMop.length; i++) {
                if (monitoringTypesRepeatedMop[i] == "Notify") {
                    document.getElementById("notifyMonitoringTypeForRepeatedMop").click();
                }
                if (monitoringTypesRepeatedMop[i] == "Block") {
                    document.getElementById("blockMonitoringTypeForRepeatedMop").click();
                }
            }
            $("#repetationCountRepeatedMop").val(fraudData['repetationCount']);
            $("#timeDurationRepeatedMop").val(fraudData['blockTimeUnits']);
            $("#currencyVelocity option").each(function () {
                if (this.innerHTML == fraudData['currency']) {
                    this.selected = true;
                }
            });
            let emailToNotifyRepeatedMop = fraudData['emailToNotify'];
            $("#emailToNotifyRepeatedMop").val(emailToNotifyRepeatedMop);
            $("#emailToNotifyRepeatedMop").tagsinput('add', emailToNotifyRepeatedMop);
            break;
        case "BLOCK_VPA_ADDRESS":
            var vpaAddress = getFieldValue("vpaAddress");
            let oldVpas = vpaAddress.split(",");
            let elementVpa = $("#vpaAddress");
            for (var j = 0; j < oldVpas.length; j++) {
                elementVpa.tagsinput('remove', oldVpas[j]);
            }
            let vpa = fraudData['vpaAddress'];
            let vpaArr = vpa.split(",");
            $("#vpaAddress").val(vpa);
            for (let i = 0; i < vpaArr.length; i++) {
                elementVpa.tagsinput('add', vpaArr[i]);
            }
            document.getElementById("validate_err").style.display = "none";
            if (always) {
                document.getElementById('alwaysOnFlag20').click();
            } else {
                if (document.getElementById('alwaysOnFlag20').checked) {
                    document.getElementById('alwaysOnFlag20').click();
                }
                $("#dateActiveFromVpa").val(fraudData['dateActiveFrom']);
                $("#dateActiveToVpa").val(fraudData['dateActiveTo']);
                $("#startTimeVpa").val(fraudData['startTime']);
                $("#endTimeVpa").val(fraudData['endTime']);
                let repeatDays = fraudData['repeatDays'];
                for (let i = 77; i <= 83; i++) {
                    let element = document.getElementById("repeatDays" + i);
                    if (repeatDays.includes(element.value)) {
                        element.click();
                    }
                }
            }
            break;

    }
}

function deleteFraudRule(ruleId) {
    var selectedCurrency = JSON.stringify($("#currencyId").val());

    var confirmationFlag = confirm("Do you want to delete this rule");
    if (!confirmationFlag) {
        return false;
    }
    $.ajaxSetup({
        beforeSend: function () {
            jQuery('body').toggleClass('loaded');
        },
        complete: function () {
            jQuery('body').toggleClass('loaded');
        }
    });
    $.ajax({
        url: 'deleteFraudRule',
        type: 'post',
        data: {
            token: document.getElementsByName("token")[0].value,
            payId: fraudFieldValidate('payId', null), //TODO for merchant module
            ruleId: ruleId,
            currency: selectedCurrency
        },
        success: function (data) {

            if ((data.response) != null) {
                alert(data.response);
                window.location.reload();
            } else {
                alert("Try again, Something went wrong!")
            }
        },
        error: function (data) {
            alert(data.response);
        }
    });
}
var payidVal;
var lastSearchKey = "";
function searchData(inputId, ruleId) {

    //first will get table Id,then Rule ID
    var searchKey = '';
    var currencyId = $("#currencyId").val();
    if (currencyId == null || currencyId == "") {
        alert("Please select currency type");
        return false;
    }
    $('input[type=search]').each(function () {

        if ($("#search_" + inputId).val()) {
            searchKey = $("#search_" + inputId).val();
        }


    });

    if (lastSearchKey != "" || searchKey != "") {
        debugger
        $.ajax({
            url: 'rulesSearchAction',
            type: 'post',
            data: {
                token: document.getElementsByName("token")[0].value,
                rule: ruleId,
                currency: JSON.stringify(currencyId),
                searchString: searchKey,
                payId: (payidVal == 'ALL') ? payidVal : getFieldValue('payId')
            },
            success: function (data) {

                if (data.fraudRuleList.length > 0) {

                    $('#select_all').prop("checked", false);
                    $('#select_all2').prop("checked", false);
                    $('#select_all3').prop("checked", false);
                    $('#select_all4').prop("checked", false);
                    $('#select_all6').prop("checked", false);
                    $('#select_all7').prop("checked", false);
                    $('#select_all8').prop("checked", false);
                    $('#select_all9').prop("checked", false);
                    $('#select_all10').prop("checked", false);
                    $('#select_all14').prop("checked", false);
                    $('#select_all17').prop("checked", false);
                    $('#select_all18').prop("checked", false);
                    $('#select_all20').prop("checked", false);
                    lastSearchKey = searchKey;
                    //debugger;
                    // if(searchKey == ""){

                    //  setTimeout(function(){

                    //    $('#select_all').click();

                    //  },1000);

                    // }

                    let blockIpAddData = [];
                    let issuerCountryAddData = [];
                    let userCountryAddData = [];
                    let userStateAddData = [];
                    let userCityAddData = [];
                    let emailListAddData = [];
                    let txnAmtAddData = [];
                    let cardRangeAddData = [];
                    let perCardTxnAddData = [];
                    let phoneListAddData = [];
                    let cardMaskAddData = [];
                    let txnAmtVelocityAddData = [];
                    let blockMackAddData = [];
                    let notifyRepeatedMopTypeData = [];
                    let repeatedMopTypeAddData = [];
                    let blockVpaAddData = [];
                    for (var i = 0; i < data.fraudRuleList.length; i++) {
                        if (data.fraudRuleList[i].fraudType == 'BLOCK_IP_ADDRESS') {
                            data.fraudRuleList[i]['editBtn'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule();editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal'/>";
                            data.fraudRuleList[i]['deleteBtn'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
                            data.fraudRuleList[i]['isSelected'] = "<input type='checkbox' onclick='checkboxclickcheck()' class='checkbox' name='BLOCK_IP_ADDRESS" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
                            blockIpAddData.push(data.fraudRuleList[i]);
                        }
                        else if (data.fraudRuleList[i].fraudType == 'BLOCK_CARD_ISSUER_COUNTRY') {
                            let country = "country";
                            let issuerCountry = "issuerCountry";
                            data.fraudRuleList[i]['editBtnForIssuerCountry'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule(" + JSON.stringify(country) + "," + JSON.stringify(country) + "," + JSON.stringify(issuerCountry) + "," + JSON.stringify(data.fraudRuleList[i]['issuerCountry']) + ");editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal2'/>";
                            data.fraudRuleList[i]['deleteBtnForIssuerCountry'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
                            data.fraudRuleList[i]['isSelectedIssuerCountry'] = "<input type='checkbox' onclick='checkboxclickcheck2()' class='checkbox2' name='BLOCK_CARD_ISSUER_COUNTRY" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
                            issuerCountryAddData.push(data.fraudRuleList[i]);
                        }
                        else if (data.fraudRuleList[i].fraudType == 'BLOCK_USER_COUNTRY') {
                            let country = "country";
                            let userCountry = "userCountry";
                            data.fraudRuleList[i]['editBtnForUserCountry'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule(" + JSON.stringify(country) + "," + JSON.stringify(userCountry) + "," + JSON.stringify(userCountry) + "," + JSON.stringify(data.fraudRuleList[i]['userCountry']) + ");editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal4'/>";
                            data.fraudRuleList[i]['deleteBtnForUserCountry'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
                            data.fraudRuleList[i]['isSelectedUserCountry'] = "<input type='checkbox' onclick='checkboxclickcheck3()' class='checkbox3' name='BLOCK_USER_COUNTRY" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
                            userCountryAddData.push(data.fraudRuleList[i]);
                        }
                        else if (data.fraudRuleList[i].fraudType == 'BLOCK_EMAIL_ID') {
                            data.fraudRuleList[i]['editBtnEmail'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule();editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal5'/>";
                            data.fraudRuleList[i]['deleteBtnEmail'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
                            data.fraudRuleList[i]['isSelectedEmail'] = "<input type='checkbox' onclick='checkboxclickcheck4()' class='checkbox4' name='BLOCK_EMAIL_ID" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
                            emailListAddData.push(data.fraudRuleList[i]);
                        }
                        else if (data.fraudRuleList[i].fraudType == 'BLOCK_TXN_AMOUNT') {
                            data.fraudRuleList[i]['editBtnTxnAmt'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule();editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal8'/>";
                            data.fraudRuleList[i]['deleteBtnTxnAmt'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
                            txnAmtAddData.push(data.fraudRuleList[i]);
                        }
                        else if (data.fraudRuleList[i].fraudType == 'BLOCK_CARD_BIN') {
                            data.fraudRuleList[i]['editBtnCardRange'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule();editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal3'/>";
                            data.fraudRuleList[i]['deleteBtnCardRange'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
                            data.fraudRuleList[i]['isSelectedCardRange'] = "<input type='checkbox' onclick='checkboxclickcheck6()' class='checkbox6' name='BLOCK_CARD_BIN" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
                            cardRangeAddData.push(data.fraudRuleList[i]);
                        }

                        else if (data.fraudRuleList[i].fraudType == 'BLOCK_CARD_NO') {
                            data.fraudRuleList[i]['editBtnCardNumber'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule();editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal9'/>";
                            data.fraudRuleList[i]['deleteBtnCardNumber'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
                            data.fraudRuleList[i]['isSelectedCardNumber'] = "<input type='checkbox' onclick='checkboxclickcheck7()' class='checkbox7' name='BLOCK_CARD_NO" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
                            cardMaskAddData.push(data.fraudRuleList[i]);
                        }
                        else if (data.fraudRuleList[i].fraudType == 'BLOCK_CARD_TXN_THRESHOLD') {
                            data.fraudRuleList[i]['editBtnCardTxnAmt'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule();editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal0'/>";
                            data.fraudRuleList[i]['deleteBtnCardTxnAmt'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
                            data.fraudRuleList[i]['isSelectedTxnThreshold'] = "<input type='checkbox' onclick='checkboxclickcheck8()' class='checkbox8' name='BLOCK_CARD_TXN_THRESHOLD" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
                            perCardTxnAddData.push(data.fraudRuleList[i]);
                        }
                        else if (data.fraudRuleList[i].fraudType == 'BLOCK_PHONE_NUMBER') {
                            data.fraudRuleList[i]['editBtnPhone'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule();editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal12'/>";
                            data.fraudRuleList[i]['deleteBtnPhone'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
                            data.fraudRuleList[i]['isSelectedPhone'] = "<input type='checkbox' onclick='checkboxclickcheck9()' class='checkbox9' name='BLOCK_PHONE_NUMBER" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
                            phoneListAddData.push(data.fraudRuleList[i]);
                        }
                        else if (data.fraudRuleList[i].fraudType == 'BLOCK_TXN_AMOUNT_VELOCITY') {
                            data.fraudRuleList[i]['editBtnTxnAmtVelocity'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule();editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal11'/>";
                            data.fraudRuleList[i]['deleteBtnTxnAmtVelocity'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='true' name='deleteRules' type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
                            txnAmtVelocityAddData.push(data.fraudRuleList[i]);
                        }
                        else if (data.fraudRuleList[i].fraudType == 'BLOCK_MACK_ADDRESS') {
                            data.fraudRuleList[i]['deleteBtn'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
                            data.fraudRuleList[i]['isSelectedMack'] = "<input type='checkbox' onclick='checkboxclickcheck()' class='checkbox' name='BLOCK_MACK_ADDRESS" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
                            blockMackAddData.push(data.fraudRuleList[i]);
                        } else if (data.fraudRuleList[i].fraudType == repeatedMopTypesS) {
                            data.fraudRuleList[i]['editBtnRepeatedMopType'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule();editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal19'/>";
                            data.fraudRuleList[i]['deleteBtnRepeatedMopType'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
                            data.fraudRuleList[i]['isSelectedMopType'] = "<input type='checkbox' onclick='checkboxclickcheck()' class='checkbox' name='REPEATED_MOP_TYPES" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
                            notifyRepeatedMopTypeData.push(data.fraudRuleList[i]);
                        } else if (data.fraudRuleList[i].fraudType == userState) {
                            let state = "state";
                            let country = "country";
                            let countryName = "countryName";
                            let countryVal = data.fraudRuleList[i].country;
                            data.fraudRuleList[i]['editBtnBlockState'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule(" + JSON.stringify(state) + "," + JSON.stringify(country) + "," + JSON.stringify(countryName) + "," + JSON.stringify(data.fraudRuleList[i]['stateCode']) + "," + JSON.stringify(countryVal) + "," + null + ");editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal17'/>";
                            data.fraudRuleList[i]['deleteBtnBlockState'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
                            data.fraudRuleList[i]['isSelectedBlockState'] = "<input type='checkbox' onclick='checkboxclickcheck17()' class='checkbox17' name='BLOCK_USER_STATE" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
                            userStateAddData.push(data.fraudRuleList[i]);
                        } else if (data.fraudRuleList[i].fraudType == userCity) {
                            let state = "state";
                            let countryNameCityBlocking = "countryNameCityBlocking";
                            let countryValue = data.fraudRuleList[i].country;
                            let stateValue = data.fraudRuleList[i].state;
                            data.fraudRuleList[i]['editBtnBlockCity'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule(" + JSON.stringify(state) + "," + JSON.stringify(countryNameCityBlocking) + "," + JSON.stringify(countryNameCityBlocking) + "," + JSON.stringify(data.fraudRuleList[i]['city']) + "," + JSON.stringify(countryValue) + "," + JSON.stringify(stateValue) + ");editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal18'/>";
                            data.fraudRuleList[i]['deleteBtnBlockCity'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
                            data.fraudRuleList[i]['isSelectedBlockCity'] = "<input type='checkbox' onclick='checkboxclickcheck18()' class='checkbox18' name='BLOCK_USER_CITY" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
                            userCityAddData.push(data.fraudRuleList[i]);
                        } else if (data.fraudRuleList[i].fraudType == repeatedMopTypeForSameDetails) {
                            data.fraudRuleList[i]['editBtnBlockRepeatedMop'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule();editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal19'/>";
                            data.fraudRuleList[i]['deleteBtnBlockRepeatedMop'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
                            repeatedMopTypeAddData.push(data.fraudRuleList[i]);
                        }

                        else if (data.fraudRuleList[i].fraudType == 'BLOCK_VPA_ADDRESS') {
                            data.fraudRuleList[i]['editBtnBlockVpaAddress'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule();editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal20'/>";
                            data.fraudRuleList[i]['deleteBtnBtnBlockVpaAddress'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
                            data.fraudRuleList[i]['isSelectedVpaAddress'] = "<input type='checkbox' onclick='checkboxclickcheck20()' class='checkbox20' name='BLOCK_VPA_ADDRESS" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
                            blockVpaAddData.push(data.fraudRuleList[i]);
                        }



                        debugger

                        if (ruleId == 'BLOCK_IP_ADDRESS') {
                            $('#ipAddListBody').DataTable().clear().destroy();
                            generateBlockIdAddressTable(blockIpAddData);
                        } else if (ruleId == 'BLOCK_CARD_ISSUER_COUNTRY') {
                            $('#issuerCountryListBody').DataTable().clear().destroy();
                            generateBlockIssuerCountryTable(issuerCountryAddData);
                        } else if (ruleId == 'BLOCK_USER_COUNTRY') {
                            $('#userCountryListBody').DataTable().clear().destroy();
                            generateBlockUserCountryTable(userCountryAddData);
                        } else if (ruleId == 'BLOCK_EMAIL_ID') {
                            $('#emailListBody').DataTable().clear().destroy();
                            generateBlockEmailAddressTable(emailListAddData);
                        } else if (ruleId == 'BLOCK_TXN_AMOUNT') {
                            $('#txnAmountListBody').DataTable().clear().destroy();
                            generateBlockTxnAmtTable(txnAmtAddData);
                        } else if (ruleId == 'BLOCK_CARD_BIN') {
                            $('#cardBinListBody').DataTable().clear().destroy();
                            generateBlockCardRangeTable(cardRangeAddData);
                        } else if (ruleId == 'BLOCK_PHONE_NUMBER') {
                            $('#phoneListBody').DataTable().clear().destroy();
                            generateBlockPhoneNumberTable(phoneListAddData);
                        } else if (ruleId == 'BLOCK_CARD_NO') {
                            $('#cardNoListBody').DataTable().clear().destroy();
                            generateBlockCardMaskTable(cardMaskAddData);
                        } else if (ruleId == 'BLOCK_CARD_TXN_THRESHOLD') {
                            $('#perCardTxnsListBody').DataTable().clear().destroy();
                            generateBlockCardTxnAmtTable(perCardTxnAddData);
                        } else if (ruleId == 'BLOCK_TXN_AMOUNT_VELOCITY') {
                            $('#txnAmountVelocityListBody').DataTable().clear().destroy();
                            generateBlockTxnAmtVelocityTable(txnAmtVelocityAddData);
                        } else if (ruleId == 'BLOCK_MACK_ADDRESS') {
                            $('#mackAddListBody').DataTable().clear().destroy();
                            generateBlockMackAddressTable(blockMackAddData);
                        } else if (ruleId == repeatedMopTypesS) {
                            $('#repeatedMopTypeListBody').DataTable().clear().destroy();
                            generateNotifyRepeatedMopTypeTable(notifyRepeatedMopTypeData);
                        } else if (data.fraudRuleList[i].fraudType == userState) {
                            $('#userStateListBody').DataTable().clear().destroy();
                            generateBlockUserStateTable(userStateAddData);
                        } else if (data.fraudRuleList[i].fraudType == userCity) {
                            $('#repeatedMopTypeListBody').DataTable().clear().destroy();
                            generateBlockUserCityTable(userCityAddData);
                        } else if (data.fraudRuleList[i].fraudType == repeatedMopTypeForSameDetails) {
                            $('#repetedMopTypeListBody').DataTable().clear().destroy();
                            generateNotifyRepeatedMopTypeSameDataTable(repeatedMopTypeAddData);
                        }
                        else if (data.fraudRuleList[i].fraudType == vpaAddressS) {
                            $('#vpaAddListBody').DataTable().clear().destroy();
                            generateBlockVpaAddressTable(blockVpaAddData);
                        }
                    }

                }
                else {
                    alert("There are no results that match your request. Please try again.");
                }
            },

        })

    }
}


function checkboxclickcheck() {
    if ($('.checkbox:checked').length >= 2) {
        $('.bulkDeleteBtn').prop('disabled', false);
        $('.deleteBtnForRule').prop('disabled', true);
    } else {
        $('.bulkDeleteBtn').prop('disabled', true);
        $('.deleteBtnForRule').prop('disabled', false);
    }
}
function checkboxclickcheck1() {
    if ($('.checkbox1:checked').length >= 2) {
        $('.bulkDeleteBtn').prop('disabled', false);
        $('.deleteBtnForRule').prop('disabled', true);
    } else {
        $('.bulkDeleteBtn').prop('disabled', true);
        $('.deleteBtnForRule').prop('disabled', false);
    }
}
function checkboxclickcheck2() {
    if ($('.checkbox2:checked').length >= 2) {
        $('.bulkDeleteBtn').prop('disabled', false);
        $('.deleteBtnForRule').prop('disabled', true);
    } else {
        $('.bulkDeleteBtn').prop('disabled', true);
        $('.deleteBtnForRule').prop('disabled', false);
    }
}
function checkboxclickcheck3() {
    if ($('.checkbox3:checked').length >= 2) {
        $('.bulkDeleteBtn').prop('disabled', false);
        $('.deleteBtnForRule').prop('disabled', true);
    } else {
        $('.bulkDeleteBtn').prop('disabled', true);
        $('.deleteBtnForRule').prop('disabled', false);
    }
}
function checkboxclickcheck4() {
    if ($('.checkbox4:checked').length >= 2) {
        $('.bulkDeleteBtn').prop('disabled', false);
        $('.deleteBtnForRule').prop('disabled', true);
    } else {
        $('.bulkDeleteBtn').prop('disabled', true);
        $('.deleteBtnForRule').prop('disabled', false);
    }
}
function checkboxclickcheck6() {
    if ($('.checkbox6:checked').length >= 2) {
        $('.bulkDeleteBtn').prop('disabled', false);
        $('.deleteBtnForRule').prop('disabled', true);
    } else {
        $('.bulkDeleteBtn').prop('disabled', true);
        $('.deleteBtnForRule').prop('disabled', false);
    }
}
function checkboxclickcheck7() {
    if ($('.checkbox7:checked').length >= 2) {
        $('.bulkDeleteBtn').prop('disabled', false);
        $('.deleteBtnForRule').prop('disabled', true);
    } else {
        $('.bulkDeleteBtn').prop('disabled', true);
        $('.deleteBtnForRule').prop('disabled', false);
    }
}
function checkboxclickcheck8() {
    if ($('.checkbox8:checked').length >= 2) {
        $('.bulkDeleteBtn').prop('disabled', false);
        $('.deleteBtnForRule').prop('disabled', true);
    } else {
        $('.bulkDeleteBtn').prop('disabled', true);
        $('.deleteBtnForRule').prop('disabled', false);
    }
}
function checkboxclickcheck9() {
    if ($('.checkbox9:checked').length >= 2) {
        $('.bulkDeleteBtn').prop('disabled', false);
        $('.deleteBtnForRule').prop('disabled', true);
    } else {
        $('.bulkDeleteBtn').prop('disabled', true);
        $('.deleteBtnForRule').prop('disabled', false);
    }
}
function checkboxclickcheck10() {
    if ($('.checkbox10:checked').length >= 2) {
        $('.bulkDeleteBtn').prop('disabled', false);
        $('.deleteBtnForRule').prop('disabled', true);
    } else {
        $('.bulkDeleteBtn').prop('disabled', true);
        $('.deleteBtnForRule').prop('disabled', false);
    }
}

function checkboxclickcheck14() {
    if ($('.checkbox14:checked').length >= 2) {
        $('.bulkDeleteBtn').prop('disabled', false);
        $('.deleteBtnForRule').prop('disabled', true);
    } else {
        $('.bulkDeleteBtn').prop('disabled', true);
        $('.deleteBtnForRule').prop('disabled', false);
    }
}

function checkboxclickcheck15() {
    if ($('.checkbox15:checked').length >= 2) {
        $('.bulkDeleteBtn').prop('disabled', false);
        $('.deleteBtnForRule').prop('disabled', true);
    } else {
        $('.bulkDeleteBtn').prop('disabled', true);
        $('.deleteBtnForRule').prop('disabled', false);
    }
}

function checkboxclickcheck17() {
    if ($('.checkbox17:checked').length >= 2) {
        $('.bulkDeleteBtn').prop('disabled', false);
        $('.deleteBtnForRule').prop('disabled', true);
    } else {
        $('.bulkDeleteBtn').prop('disabled', true);
        $('.deleteBtnForRule').prop('disabled', false);
    }
}

function checkboxclickcheck18() {
    if ($('.checkbox18:checked').length >= 2) {
        $('.bulkDeleteBtn').prop('disabled', false);
        $('.deleteBtnForRule').prop('disabled', true);
    } else {
        $('.bulkDeleteBtn').prop('disabled', true);
        $('.deleteBtnForRule').prop('disabled', false);
    }
}
function checkboxclickcheck20() {
    if ($('.checkbox20:checked').length >= 2) {
        $('.bulkDeleteBtn').prop('disabled', false);
        $('.deleteBtnForRule').prop('disabled', true);
    } else {
        $('.bulkDeleteBtn').prop('disabled', true);
        $('.deleteBtnForRule').prop('disabled', false);
    }
}
function selectall() {
    $('#select_all').on('click', function () {
        if (this.checked) {

            $('.checkbox').each(function () {
                this.checked = true;
            });
            $('.bulkDeleteBtn').prop('disabled', false);
            $('.deleteBtnForRule').prop('disabled', true);
        } else {

            $('.checkbox').each(function () {
                this.checked = false;

            });
            $('.bulkDeleteBtn').prop('disabled', true);
            $('.deleteBtnForRule').prop('disabled', false);
        }
    });
    $('.checkbox').on('click', function () {

        if ($('.checkbox:checked').length == $('.checkbox').length) {

            $('#select_all').prop('checked', true);

        } else {
            $('#select_all').prop('checked', false);

        }
    });
    $('#select_all2').on('click', function () {
        if (this.checked) {

            $('.checkbox2').each(function () {
                this.checked = true;
            });
            $('.bulkDeleteBtn').prop('disabled', false);
            $('.deleteBtnForRule').prop('disabled', true);
        } else {

            $('.checkbox2').each(function () {
                this.checked = false;

            });
            $('.bulkDeleteBtn').prop('disabled', true);
            $('.deleteBtnForRule').prop('disabled', false);
        }
    });
    $('.checkbox2').on('click', function () {

        if ($('.checkbox2:checked').length == $('.checkbox2').length) {


            $('#select_all2').prop('checked', true);

        } else {

            $('#select_all2').prop('checked', false);

        }
    });
    $('#select_all3').on('click', function () {
        if (this.checked) {

            $('.checkbox3').each(function () {
                this.checked = true;
            });
            $('.bulkDeleteBtn').prop('disabled', false);
            $('.deleteBtnForRule').prop('disabled', true);
        } else {

            $('.checkbox3').each(function () {
                this.checked = false;

            });
            $('.bulkDeleteBtn').prop('disabled', true);
            $('.deleteBtnForRule').prop('disabled', false);
        }
    });
    $('.checkbox3').on('click', function () {

        if ($('.checkbox3:checked').length == $('.checkbox3').length) {


            $('#select_all3').prop('checked', true);

        } else {

            $('#select_all3').prop('checked', false);

        }
    });
    $('#select_all4').on('click', function () {
        if (this.checked) {

            $('.checkbox4').each(function () {
                this.checked = true;
            });
            $('.bulkDeleteBtn').prop('disabled', false);
            $('.deleteBtnForRule').prop('disabled', true);
        } else {

            $('.checkbox4').each(function () {
                this.checked = false;

            });
            $('.bulkDeleteBtn').prop('disabled', true);
            $('.deleteBtnForRule').prop('disabled', false);
        }
    });
    $('.checkbox4').on('click', function () {

        if ($('.checkbox4:checked').length == $('.checkbox4').length) {


            $('#select_all4').prop('checked', true);

        } else {

            $('#select_all4').prop('checked', false);

        }
    });
    $('#select_all6').on('click', function () {
        if (this.checked) {

            $('.checkbox6').each(function () {
                this.checked = true;
            });
            $('.bulkDeleteBtn').prop('disabled', false);
            $('.deleteBtnForRule').prop('disabled', true);
        } else {

            $('.checkbox6').each(function () {
                this.checked = false;

            });
            $('.bulkDeleteBtn').prop('disabled', true);
            $('.deleteBtnForRule').prop('disabled', false);
        }
    });
    $('.checkbox6').on('click', function () {

        if ($('.checkbox6:checked').length == $('.checkbox6').length) {


            $('#select_all6').prop('checked', true);

        } else {

            $('#select_all6').prop('checked', false);

        }
    });
    $('#select_all7').on('click', function () {
        if (this.checked) {

            $('.checkbox7').each(function () {
                this.checked = true;
            });
            $('.bulkDeleteBtn').prop('disabled', false);
            $('.deleteBtnForRule').prop('disabled', true);
        } else {

            $('.checkbox7').each(function () {
                this.checked = false;

            });
            $('.bulkDeleteBtn').prop('disabled', true);
            $('.deleteBtnForRule').prop('disabled', false);
        }
    });
    $('.checkbox7').on('click', function () {

        if ($('.checkbox7:checked').length == $('.checkbox7').length) {


            $('#select_all7').prop('checked', true);

        } else {

            $('#select_all7').prop('checked', false);

        }
    });
    $('#select_all8').on('click', function () {
        if (this.checked) {

            $('.checkbox8').each(function () {
                this.checked = true;
            });
            $('.bulkDeleteBtn').prop('disabled', false);
            $('.deleteBtnForRule').prop('disabled', true);
        } else {

            $('.checkbox8').each(function () {
                this.checked = false;

            });
            $('.bulkDeleteBtn').prop('disabled', true);
            $('.deleteBtnForRule').prop('disabled', false);
        }
    });
    $('.checkbox8').on('click', function () {

        if ($('.checkbox8:checked').length == $('.checkbox8').length) {


            $('#select_all8').prop('checked', true);

        } else {

            $('#select_all8').prop('checked', false);

        }
    });
    $('#select_all9').on('click', function () {
        if (this.checked) {

            $('.checkbox9').each(function () {
                this.checked = true;
            });
            $('.bulkDeleteBtn').prop('disabled', false);
            $('.deleteBtnForRule').prop('disabled', true);
        } else {

            $('.checkbox9').each(function () {
                this.checked = false;

            });
            $('.bulkDeleteBtn').prop('disabled', true);
            $('.deleteBtnForRule').prop('disabled', false);
        }
    });
    $('.checkbox9').on('click', function () {

        if ($('.checkbox9:checked').length == $('.checkbox9').length) {


            $('#select_all9').prop('checked', true);

        } else {

            $('#select_all9').prop('checked', false);

        }
    });
    $('#select_all10').on('click', function () {
        if (this.checked) {

            $('.checkbox10').each(function () {
                this.checked = true;
            });
            $('.bulkDeleteBtn').prop('disabled', false);
            $('.deleteBtnForRule').prop('disabled', true);
        } else {

            $('.checkbox10').each(function () {
                this.checked = false;

            });
            $('.bulkDeleteBtn').prop('disabled', true);
            $('.deleteBtnForRule').prop('disabled', false);
        }
    });
    $('.checkbox10').on('click', function () {

        if ($('.checkbox10:checked').length == $('.checkbox2').length) {


            $('#select_all10').prop('checked', true);

        } else {

            $('#select_all10').prop('checked', false);

        }
    });

    $('#select_all14').on('click', function () {
        if (this.checked) {
            $('.checkbox14').each(function () {
                this.checked = true;
            });
            $('.bulkDeleteBtn').prop('disabled', false);
            $('.deleteBtnForRule').prop('disabled', true);
        } else {
            $('.checkbox14').each(function () {
                this.checked = false;
            });
            $('.bulkDeleteBtn').prop('disabled', true);
            $('.deleteBtnForRule').prop('disabled', false);
        }
    });

    $('.checkbox14').on('click', function () {
        if ($('.checkbox14:checked').length == $('.checkbox14').length) {
            $('#select_all14').prop('checked', true);
        } else {
            $('#select_all14').prop('checked', false);
        }
    });

    $('#select_all15').on('click', function () {
        if (this.checked) {
            $('.checkbox15').each(function () {
                this.checked = true;
            });
            $('.bulkDeleteBtn').prop('disabled', false);
            $('.deleteBtnForRule').prop('disabled', true);
        } else {
            $('.checkbox15').each(function () {
                this.checked = false;
            });
            $('.bulkDeleteBtn').prop('disabled', true);
            $('.deleteBtnForRule').prop('disabled', false);
        }
    });

    $('.checkbox15').on('click', function () {
        if ($('.checkbox15:checked').length == $('.checkbox15').length) {
            $('#select_all15').prop('checked', true);
        } else {
            $('#select_all15').prop('checked', false);
        }
    });

    $('#select_all17').on('click', function () {
        if (this.checked) {
            $('.checkbox17').each(function () {
                this.checked = true;
            });
            $('.bulkDeleteBtn').prop('disabled', false);
            $('.deleteBtnForRule').prop('disabled', true);
        } else {
            $('.checkbox17').each(function () {
                this.checked = false;
            });
            $('.bulkDeleteBtn').prop('disabled', true);
            $('.deleteBtnForRule').prop('disabled', false);
        }
    });

    $('.checkbox17').on('click', function () {
        if ($('.checkbox17:checked').length == $('.checkbox17').length) {
            $('#select_all17').prop('checked', true);
        } else {
            $('#select_all17').prop('checked', false);
        }
    });

    $('#select_all18').on('click', function () {
        if (this.checked) {
            $('.checkbox18').each(function () {
                this.checked = true;
            });
            $('.bulkDeleteBtn').prop('disabled', false);
            $('.deleteBtnForRule').prop('disabled', true);
        } else {
            $('.checkbox18').each(function () {
                this.checked = false;
            });
            $('.bulkDeleteBtn').prop('disabled', true);
            $('.deleteBtnForRule').prop('disabled', false);
        }
    });

    $('.checkbox18').on('click', function () {
        if ($('.checkbox18:checked').length == $('.checkbox18').length) {
            $('#select_all18').prop('checked', true);
        } else {
            $('#select_all18').prop('checked', false);
        }
    });

    $('#select_all20').on('click', function () {

        if (this.checked) {

            $('.checkbox20').each(function () {
                this.checked = true;
            });
            $('.bulkDeleteBtn').prop('disabled', false);
            $('.deleteBtnForRule').prop('disabled', true);
        } else {

            $('.checkbox20').each(function () {
                this.checked = false;

            });
            $('.bulkDeleteBtn').prop('disabled', true);
            $('.deleteBtnForRule').prop('disabled', false);
        }
    });
    $('.checkbox20').on('click', function () {

        if ($('.checkbox20:checked').length == $('.checkbox20').length) {


            $('#select_all20').prop('checked', true);

        } else {

            $('#select_all20').prop('checked', false);

        }
    });
}

function bulkDeleteFraudRule() {
    var confirmationFlag = confirm("Are you Sure you want to delete the selected Rule");
    if (!confirmationFlag) {
        return false;
    }



    //  var ids = [];
    //var ids = new Array();
    var arr = new Array();

    $(".checkbox:checked").each(function () {
        //    if(".checkbox:checked.length" >= 2){
        //      alert("hii");
        //  $("#bulkDeleteBtn").attr("disabled", true)
        //    }else{
        //      $("#bulkDeleteBtn").attr("disabled", false)
        //    }
        arr.push($(this).val());

    });
    $(".checkbox1:checked").each(function () {
        arr.push($(this).val());

    });
    $(".checkbox2:checked").each(function () {
        arr.push($(this).val());

    });
    $(".checkbox3:checked").each(function () {
        arr.push($(this).val());

    });
    $(".checkbox4:checked").each(function () {
        arr.push($(this).val());

    });
    $(".checkbox6:checked").each(function () {
        arr.push($(this).val());

    });
    $(".checkbox7:checked").each(function () {
        arr.push($(this).val());

    });
    $(".checkbox8:checked").each(function () {
        arr.push($(this).val());

    });
    $(".checkbox9:checked").each(function () {
        arr.push($(this).val());
    });
    $(".checkbox10:checked").each(function () {
        arr.push($(this).val());
    });

    $(".checkbox14:checked").each(function () {
        arr.push($(this).val());
    });

    $(".checkbox15:checked").each(function () {
        arr.push($(this).val());
    });

    $(".checkbox17:checked").each(function () {
        arr.push($(this).val());
    });

    $(".checkbox18:checked").each(function () {
        arr.push($(this).val());
    });

    $(".checkbox20:checked").each(function () {
        arr.push($(this).val());
    });
    var currencySelected = $("#currencyId").val();
    currencySelected = JSON.stringify(currencySelected);

    if (arr == "") {
        alert("no selected rule");
    } else {
        ids = arr.join();
        $.ajax({
            type: "POST",
            url: "bulkRulesDeleteAction",
            data: {
                token: document.getElementsByName("token")[0].value,
                payId: fraudFieldValidate('payId', null), //TODO for merchant module
                ruleIdList: ids,
                currency: currencySelected
            },

            success: function (data) {
                if ((data.response) != null) {
                    alert(data.response);
                    window.location.reload();
                }
                else {
                    alert("Try again, Something went wrong!")
                }
            }
        });

    }
    return false;
}
/*Code end here for Bulk delete of table for each rule */
/*Code start for Bulk upload of table for each rule */
function bulkUpload(ruleId, formId) {

    event.preventDefault();
    var selectedCurrency = $("#currencyId").val();
    var form = $('#' + formId)[0];
    var data = new FormData(form);
    var selectedCurrency = $("#currencyId").val();
    var file = document.getElementById("vpaFile").value;
    // var file = $('#file').val();
    if (file == '' || file == null) {
        alert('Please upload valid excel file');
        $(file).val('');
    }
    else {
        data.append("payId", fraudFieldValidate('payId', null));
        data.append("rule", ruleId);
        data.append("fileName", file);
        data.append("currency", JSON.stringify(selectedCurrency));
        data.append(file, $("#vpaFile").prop("files")[0]);
        data.append("token", document.getElementsByName("token")[0].value);
        return $.ajax({
            type: "POST",
            enctype: 'multipart/form-data',
            url: 'bulkRulesAddAction',
            data: data,
            processData: false,
            contentType: false,
            cache: false,
            currency: selectedCurrency,
            async: true,
            success: function (data) {
                if ((data.response) != null) {
                    alert(data.response);
                    window.location.reload();
                } else {
                    alert("Try again, Something went wrong!")
                }
            },
            error: function (data) {
                alert(data.response);
            }
        });


    }
}
var issuerCoutries = [];
var userCoutries = [];

// function fillDynamicValuesBlock(rule, ruleColumns, fname, headerNames){

//  var tableHtml='<tr><td>';
//  var fraudType = rule['fraudType'];
//  var header =  "<tr class='boxheadingsmall' style='text-align:center;background-color: #496cb6;color: white'><th>"+headerNames[0]+"</th></tr>";
//  if(document.getElementById(ruleColumns[0]).childNodes[1].childNodes[1] == null){
//    $('#'+ruleColumns[0]).append(header);
//    document.getElementById(ruleColumns[0]+'Msg').style="display:none";
//  }else{}
//  var myValue = rule[ruleColumns[1]].split(', ');
//  for(var i=0; i<myValue.length; i++){
//    tableHtml+='<span class="tag">'+myValue[i]+'</span>';


//  }
//  $('#'+ruleColumns[0]).append(tableHtml);

//  function checkmap(){
//    for(var i=0; i<myValue.length; i++){
//      $('input[name="'+fname+'"][value="'+myValue[i]+'"]').attr('checked', true);
//    }
//  }
//  if(headerNames[0] == "Card Issuer Country"){
//    issuerCoutries = myValue;
//  } else if(headerNames[0] == "User Country"){
//    userCoutries = myValue;
//  }
// }

// function fillDynamicValues(rule,ruleColumns, styleMargin, headerNames){

//  var tableHtml='<tr';
//  var fraudType = rule['fraudType'];
//  var col1 ;
//  var col2 ;
//  var col3 ;
//  var col4 ;
//  var col5 ;
//  var col6 ;
//  var col7 ;
//  var col8 ;
//  col1 = (headerNames[0]!=null) ? headerNames[0] : '';
//  col2 = (headerNames[1]!=null) ? headerNames[1] : '';
//  col3 = (headerNames[2]!=null) ? headerNames[2] : '';
//  col4 = (headerNames[3]!=null) ? headerNames[3] : '';
//  col5 = (headerNames[4]!=null) ? headerNames[4] : '';
//  col6 = (headerNames[5]!=null) ? headerNames[5] : '';
//  col7 = (headerNames[6]!=null) ? headerNames[6] : '';
//  col8 = (headerNames[7]!=null) ? headerNames[7] : '';

//  var header =  "<tr class='boxheadingsmall' style='text-align:center;background-color: #496cb6;color: white' role='row'><th style='width:30%;text-align:center'>"+col1+"</th><th style='width:30%;text-align:center'>"+col2+"</th><th style='width:30%;text-align:center'>"+col3+"</th><th style='width:30%;text-align:center'>"+col4+"</th><th style='width:30%;text-align:center'>"+col5+"</th><th style='width:30%;text-align:center'>"+col6+"</th><th style='width:30%;text-align:center'>"+col7+"</th><th style='width:30%;text-align:center'>"+col8+"</th><th style='width:30%;text-align:center'>Action</th></tr>";
//    if(document.getElementById(ruleColumns[0]).childNodes[1].childNodes[1] == null){
//      $('#'+ruleColumns[0]).append(header);
//      document.getElementById(ruleColumns[0]+'Msg').style="display:none";
//    }else{
//      }
//      for(var i=1; i<ruleColumns.length; i++){
//        var noOfRows = document.getElementById(ruleColumns[0]).childNodes[1].childNodes.length-2;
//        if(noOfRows %2 ==0){
//            tableHtml+=' class="even"><td style="text-align:center;width:30%" role="row">'+rule[ruleColumns[i]]+'</td>';
//        }else{
//          tableHtml+=' class="odd"><td style="text-align:center;width:40%" role="row" >'+rule[ruleColumns[i]]+'</td>';
//        }
//      }
//  tableHtml+="<td><input class='btn btn-primary btn-xs editRules' style='margin-left:"+styleMargin+"%'  type='submit' value='Delete' onclick='deleteFraudRule("+rule['id']+")'/>";
//  $('#'+ruleColumns[0]).append(tableHtml);
// }

// function writeFraudTable(item, index){
//  var rule = item;
//  var fraudType = rule['fraudType'];

//  switch(fraudType){
//    case numberOfTransaction:{
//      fillDynamicValues(rule,perMerchantTxnsColumns,'170',['Merchant','Minutes','No. of Transactions'])
//    }
//    break;
//    case transactionAmount:{
//      fillDynamicValues(rule,txnAmountColumns,'195',['Merchant','Currency', 'Min. Amount', 'Max. Amount'])
//    }
//    break;
//    case ipAddressS :{
//      fillDynamicValues(rule,ipRuleColumns,'118',['Merchant','Ip Address','Start Date','End Date','Start Time','End Time','Week'])
//    }
//    break;
//    case whiteListIpAddressS :{
//      fillDynamicValues(rule,wlIpRuleColumns,'118',['Merchant','WhiteList Ip Address','Start Date','End Date','Start Time','End Time','Week'])
//    }
//    break;
//    case domainNameS :{
//      fillDynamicValues(rule,domainRuleColumns,'118',['Merchant','Domain Name','Start Date','End Date','Start Time','End Time','Week'])
//    }
//    break;
//    case emailS:{
//      fillDynamicValues(rule,emailRuleColumns,'118',['Merchant','Email Address','Start Date','End Date','Start Time','End Time','Week'])
//    }
//    break;
//    case userCountry:{
//      fillDynamicValues(rule,userCountryColumns,'184',['Merchant','User Country'])
//    }
//    break;
//    case issuerCountry:{
//      fillDynamicValues(rule,issuerCountryColumns,'184',['Merchant','Issuer Country'])
//    }
//    break;
//    case negativeBinS:{
//      fillDynamicValues(rule,cardBinColumns,'118',['Merchant','Card Range','Start Date','End Date','Start Time','End Time','Week'])
//    }
//    break;
//    case negativeCardS:{
//      fillDynamicValues(rule,cardNoColumns,'115',['Merchant','Card No.','Start Date','End Date','Start Time','End Time','Week'])
//    }
//    break;
//    case perCardTransactionAllowedS:{
//      fillDynamicValues(rule,perCardTxnsColumns,'1',['Merchant','Card No.','Allowed Transaction','Start Date','End Date','Start Time','End Time', 'Week'])
//    }
//    break;
//    default:{
//      alert('Something went wrong');
//    }
//  }
// }

var columnsRules = ['ipAddListBody', 'wlIpAddListBody', 'emailListBody', 'userCountryListBody', 'issuerCountryListBody', 'cardBinListBody', 'cardNoListBody', 'txnAmountListBody', 'perCardTxnsListBody', 'phoneRuleColumns', 'txnAmountVelocityColumns', 'repeatedMopTypeListBody', 'vpaAddListBody']
var ipRuleColumns = [
    { "data": "payId" },
    { "data": "currency" },
    { "data": "ipAddress" },
    { "data": "dateActiveFrom" },
    { "data": "dateActiveTo" },
    { "data": "startTime" },
    { "data": 'endTime' },
    { "data": 'repeatDays' },
    { "data": 'editBtn' },
    { "data": "deleteBtn" },
    { "data": "isSelected" }

];
var issuerCountryColumns = [
    { "data": "payId" },
    { "data": "currency" },

    { "data": "issuerCountry" },
    { "data": "editBtnForIssuerCountry" },
    { "data": "deleteBtnForIssuerCountry" },
    { "data": "isSelectedIssuerCountry" }
];
var userCountryColumns = [
    { "data": "payId" },
    { "data": "currency" },
    { "data": "userCountry" },
    { "data": "editBtnForUserCountry" },
    { "data": "deleteBtnForUserCountry" },
    { "data": "isSelectedUserCountry" }
];
var userStateColumns = [
    { "data": "payId" },
    { "data": "currency" },
    { "data": "stateCode" },
    { "data": "editBtnBlockState" },
    { "data": "deleteBtnBlockState" }
];
var userCityColumns = [
    { "data": "payId" },
    { "data": "currency" },
    { "data": "city" },
    { "data": "editBtnBlockCity" },
    { "data": "deleteBtnBlockCity" }
];

var emailRuleColumns = [
    { "data": "payId" },
    { "data": "currency" },
    { "data": "email" },
    { "data": "dateActiveFrom" },
    { "data": "dateActiveTo" },
    { "data": "startTime" },
    { "data": 'endTime' },
    { "data": 'repeatDays' },
    { "data": "editBtnEmail" },
    { "data": "deleteBtnEmail" },
    { "data": "isSelectedEmail" }
];

var txnAmountColumns = [
    { "data": "payId" },
    { "data": "currency" },
    { "data": "transactionAmount" },
    { "data": "repetationCount" },
    { "data": "minTransactionAmount" },
    { "data": "maxTransactionAmount" },
    { "data": "editBtnTxnAmt" },
    { "data": "deleteBtnTxnAmt" }

];
var cardBinColumns = [
    { "data": "payId" },
    { "data": "currency" },
    { "data": "negativeBin" },
    { "data": "dateActiveFrom" },
    { "data": "dateActiveTo" },
    { "data": "startTime" },
    { "data": 'endTime' },
    { "data": 'repeatDays' },
    { "data": "editBtnCardRange" },
    { "data": "deleteBtnCardRange" },
    { "data": "isSelectedCardRange" }
];



var cardNoColumns = [
    { "data": "payId" },
    { "data": "currency" },
    { "data": "negativeCard" },
    { "data": "dateActiveFrom" },
    { "data": "dateActiveTo" },
    { "data": "startTime" },
    { "data": 'endTime' },
    { "data": 'repeatDays' },
    { "data": "editBtnCardNumber" },
    { "data": "deleteBtnCardNumber" },
    { "data": "isSelectedCardNumber" },

];
var perCardTxnsColumns = [
    { "data": "payId" },
    { "data": "currency" },
    { "data": "negativeCard" },
    { "data": "perCardTransactionAllowed" },
    { "data": "dateActiveFrom" },
    { "data": "dateActiveTo" },
    { "data": "startTime" },
    { "data": 'endTime' },
    { "data": 'repeatDays' },
    { "data": "editBtnCardTxnAmt" },
    { "data": "deleteBtnCardTxnAmt" },
    { "data": "isSelectedTxnThreshold" }
]
var phoneRuleColumns = [
    { "data": "payId" },
    { "data": "currency" },
    { "data": "phone" },
    { "data": "dateActiveFrom" },
    { "data": "dateActiveTo" },
    { "data": "startTime" },
    { "data": 'endTime' },
    { "data": 'repeatDays' },
    { "data": "editBtnPhone" },
    { "data": "deleteBtnPhone" },
    { "data": "isSelectedPhone" }
]

var txnAmountVelocityColumns = [
    { "data": "payId" },
    { "data": "currency" },
    { "data": "maxTransactionAmount" },
    { "data": "repetationCount" },
    { "data": "userIdentifier" },
    { "data": "monitoringType" },
    { "data": "statusVelocity" },
    { "data": "emailToNotify" },
    { "data": "blockTimeUnits" },
    { "data": "editBtnTxnAmtVelocity" },
    { "data": "deleteBtnTxnAmtVelocity" }
]

var mackRuleColumns = [
    { "data": "payId" },
    { "data": "mackAddress" },
    { "data": "dateActiveFrom" },
    { "data": "dateActiveTo" },
    { "data": "startTime" },
    { "data": 'endTime' },
    { "data": 'repeatDays' },
    { "data": "deleteBtn" },
    { "data": "isSelectedMack" }

]

var mackRuleColumns = [
    { "data": "payId" },
    { "data": "currency" },
    { "data": "mackAddress" },
    { "data": "dateActiveFrom" },
    { "data": "dateActiveTo" },
    { "data": "startTime" },
    { "data": 'endTime' },
    { "data": 'repeatDays' },
    { "data": "deleteBtn" },
    { "data": "isSelectedMack" }

]

var notifyMopTypeRuleColumns = [
    { "data": "payId" },
    { "data": "currency" },
    { "data": "paymentType" },
    { "data": "emailToNotify" },
    { "data": "blockTimeUnits" },
    { "data": "percentageOfRepeatedMop" },
    { "data": "editBtnRepeatedMopType" },
    { "data": "deleteBtnRepeatedMopType" },
    { "data": "isSelectedMopType" }
];

var notifyMopTypeSameDetailsRuleColumns = [
    { "data": "payId" },
    { "data": "currency" },
    { "data": "blockTimeUnits" },
    { "data": "monitoringType" },
    { "data": "repetationCount" },
    { "data": "emailToNotify" },
    { "data": "editBtnBlockRepeatedMop" },
    { "data": "deleteBtnBlockRepeatedMop" }
];

var vpaRuleColumns = [
    { "data": "payId" },
    { "data": "currency" },
    { "data": "vpaAddress" },
    { "data": "createDate" },
    // { "data": "dateActiveTo" },
    // { "data": "startTime" },
    // { "data": 'endTime' },
    //  { "data": 'repeatDays' },
    { "data": 'editBtnBlockVpaAddress' },
    { "data": "deleteBtnBtnBlockVpaAddress" },
    { "data": "isSelectedVpaAddress" }

];

let ruleCountByBlockType = {
}
var downloadDataForVpa = [];
function fetchFraudRuleList(payIdValue, currencyValue) {
    currencyValue = JSON.stringify(currencyValue);
    //  var currencySelected=$("#currencyId").val();
    console.log("Selected Currency" + currencyValue);
    payidVal = payIdValue;
    //  console.log(payIdValue);
    $.ajax({
        url: 'ruleListAction',
        type: 'post',
        data: {
            token: document.getElementsByName("token")[0].value,
            payId: (payIdValue == 'ALL') ? payIdValue : getFieldValue('payId'),
            currency: currencyValue,
        },
    }).done(function (data) {
        //console.log(data);
        let blockIpAddData = [];
        let issuerCountryAddData = [];
        let userCountryAddData = [];
        let userStateAddData = [];
        let userCityAddData = [];
        let emailListAddData = [];
        let txnAmtAddData = [];
        let cardRangeAddData = [];
        let cardMaskAddData = [];
        let perCardTxnAddData = [];
        let phoneListAddData = [];
        let txnAmtVelocityAddData = [];
        let blockMackAddData = [];
        let notifyRepeatedMopTypeData = [];
        let repeatedMopTypeAddData = [];
        let blockVpaAddData = [];

        for (var i = 0; i < data.fraudRuleList.length; i++) {
            if (data.fraudRuleList[i].fraudType == 'BLOCK_IP_ADDRESS') {
                data.fraudRuleList[i]['editBtn'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule(); editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal'/>";
                data.fraudRuleList[i]['deleteBtn'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick=\"deleteFraudRule('" + data.fraudRuleList[i]['id'] + "')\"/>"
                data.fraudRuleList[i]['isSelected'] = "<input type='checkbox' onclick='checkboxclickcheck()' class='checkbox' name='BLOCK_IP_ADDRESS" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
                blockIpAddData.push(data.fraudRuleList[i]);
            }
            else if (data.fraudRuleList[i].fraudType == 'BLOCK_CARD_ISSUER_COUNTRY') {
                let country = "country";
                let issuerCountry = "issuerCountry";
                data.fraudRuleList[i]['editBtnForIssuerCountry'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule(" + JSON.stringify(country) + "," + JSON.stringify(country) + "," + JSON.stringify(issuerCountry) + "," + JSON.stringify(data.fraudRuleList[i]['issuerCountry']) + ");editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal2'/>";
                data.fraudRuleList[i]['deleteBtnForIssuerCountry'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick=\"deleteFraudRule('" + data.fraudRuleList[i]['id'] + "')\"/>"
                data.fraudRuleList[i]['isSelectedIssuerCountry'] = "<input type='checkbox' onclick='checkboxclickcheck2()' class='checkbox2' name='BLOCK_CARD_ISSUER_COUNTRY" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
                issuerCountryAddData.push(data.fraudRuleList[i]);
            }
            else if (data.fraudRuleList[i].fraudType == 'BLOCK_USER_COUNTRY') {
                let country = "country";
                let userCountry = "userCountry";
                data.fraudRuleList[i]['editBtnForUserCountry'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule(" + JSON.stringify(country) + "," + JSON.stringify(userCountry) + "," + JSON.stringify(userCountry) + "," + JSON.stringify(data.fraudRuleList[i]['userCountry']) + ");editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal4'/>";
                data.fraudRuleList[i]['deleteBtnForUserCountry'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick=\"deleteFraudRule('" + data.fraudRuleList[i]['id'] + "')\"/>"
                data.fraudRuleList[i]['isSelectedUserCountry'] = "<input type='checkbox' onclick='checkboxclickcheck3()' class='checkbox3' name='BLOCK_USER_COUNTRY" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
                userCountryAddData.push(data.fraudRuleList[i]);
            }
            else if (data.fraudRuleList[i].fraudType == 'BLOCK_EMAIL_ID') {
                data.fraudRuleList[i]['editBtnEmail'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule();editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal5'/>";
                data.fraudRuleList[i]['deleteBtnEmail'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick=\"deleteFraudRule('" + data.fraudRuleList[i]['id'] + "')\"/>"
                data.fraudRuleList[i]['isSelectedEmail'] = "<input type='checkbox' onclick='checkboxclickcheck4()' class='checkbox4' name='BLOCK_EMAIL_ID" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
                emailListAddData.push(data.fraudRuleList[i]);
            }
            else if (data.fraudRuleList[i].fraudType == 'BLOCK_TXN_AMOUNT') {
                data.fraudRuleList[i]['editBtnTxnAmt'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule();editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal8'/>";
                data.fraudRuleList[i]['deleteBtnTxnAmt'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick=\"deleteFraudRule('" + data.fraudRuleList[i]['id'] + "')\"/>"
                txnAmtAddData.push(data.fraudRuleList[i]);
            }
            else if (data.fraudRuleList[i].fraudType == 'BLOCK_CARD_BIN') {
                data.fraudRuleList[i]['editBtnCardRange'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule();editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal3'/>";
                data.fraudRuleList[i]['deleteBtnCardRange'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick=\"deleteFraudRule('" + data.fraudRuleList[i]['id'] + "')\"/>"
                data.fraudRuleList[i]['isSelectedCardRange'] = "<input type='checkbox' onclick='checkboxclickcheck6()' class='checkbox6' name='BLOCK_CARD_BIN" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
                cardRangeAddData.push(data.fraudRuleList[i]);
            }

            else if (data.fraudRuleList[i].fraudType == 'BLOCK_CARD_NO') {
                data.fraudRuleList[i]['editBtnCardNumber'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule();editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal9'/>";
                data.fraudRuleList[i]['deleteBtnCardNumber'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick=\"deleteFraudRule('" + data.fraudRuleList[i]['id'] + "')\"/>"
                data.fraudRuleList[i]['isSelectedCardNumber'] = "<input type='checkbox' onclick='checkboxclickcheck7()' class='checkbox7' name='BLOCK_CARD_NO" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
                cardMaskAddData.push(data.fraudRuleList[i]);
            }
            else if (data.fraudRuleList[i].fraudType == 'BLOCK_CARD_TXN_THRESHOLD') {
                data.fraudRuleList[i]['editBtnCardTxnAmt'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule();editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal0'/>";
                data.fraudRuleList[i]['deleteBtnCardTxnAmt'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick=\"deleteFraudRule('" + data.fraudRuleList[i]['id'] + "')\"/>"
                data.fraudRuleList[i]['isSelectedTxnThreshold'] = "<input type='checkbox' onclick='checkboxclickcheck8()' class='checkbox8' name='BLOCK_CARD_TXN_THRESHOLD" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
                perCardTxnAddData.push(data.fraudRuleList[i]);
            }
            else if (data.fraudRuleList[i].fraudType == 'BLOCK_PHONE_NUMBER') {
                data.fraudRuleList[i]['editBtnPhone'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule();editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal12'/>";
                data.fraudRuleList[i]['deleteBtnPhone'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick=\"deleteFraudRule('" + data.fraudRuleList[i]['id'] + "')\"/>"
                data.fraudRuleList[i]['isSelectedPhone'] = "<input type='checkbox' onclick='checkboxclickcheck9()' class='checkbox9' name='BLOCK_PHONE_NUMBER" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
                phoneListAddData.push(data.fraudRuleList[i]);
            }
            else if (data.fraudRuleList[i].fraudType == 'BLOCK_TXN_AMOUNT_VELOCITY') {
                data.fraudRuleList[i]['editBtnTxnAmtVelocity'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule();editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal11'/>";
                data.fraudRuleList[i]['deleteBtnTxnAmtVelocity'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick=\"deleteFraudRule('" + data.fraudRuleList[i]['id'] + "')\"/>"
                txnAmtVelocityAddData.push(data.fraudRuleList[i]);
            }
            else if (data.fraudRuleList[i].fraudType == 'BLOCK_MACK_ADDRESS') {
                data.fraudRuleList[i]['deleteBtn'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
                data.fraudRuleList[i]['isSelectedMack'] = "<input type='checkbox' onclick='checkboxclickcheck14()' class='checkbox14' name='BLOCK_MACK_ADDRESS" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
                blockMackAddData.push(data.fraudRuleList[i]);
            } else if (data.fraudRuleList[i].fraudType == repeatedMopTypesS) {
                data.fraudRuleList[i]['editBtnRepeatedMopType'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule();editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal19'/>";
                data.fraudRuleList[i]['deleteBtnRepeatedMopType'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick=\"deleteFraudRule('" + data.fraudRuleList[i]['id'] + "')\"/>"
                data.fraudRuleList[i]['isSelectedMopType'] = "<input type='checkbox' onclick='checkboxclickcheck()' class='checkbox15' name='REPEATED_MOP_TYPES" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
                notifyRepeatedMopTypeData.push(data.fraudRuleList[i]);
            } else if (data.fraudRuleList[i].fraudType == notifyFirstTransactions) {
                let emailToNotify = data.fraudRuleList[i].emailToNotify;
                // getTagsInputVal("tagemailToNotifyFTxn");
                let amount = data.fraudRuleList[i].transactionAmount;
                $("#transactionAmountFTxn").val(amount);
                $("#emailToNotifyFTxn").val(emailToNotify);
                $("#emailToNotifyFTxn").tagsinput('add', emailToNotify);
            } else if (data.fraudRuleList[i].fraudType == userState) {
                let state = "state";
                let country = "country";
                let countryName = "countryName";
                let countryVal = data.fraudRuleList[i].country;
                data.fraudRuleList[i]['editBtnBlockState'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule(" + JSON.stringify(state) + "," + JSON.stringify(country) + "," + JSON.stringify(countryName) + "," + JSON.stringify(data.fraudRuleList[i]['stateCode']) + "," + JSON.stringify(countryVal) + "," + null + ");editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal17'/>";
                data.fraudRuleList[i]['deleteBtnBlockState'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick=\"deleteFraudRule('" + data.fraudRuleList[i]['id'] + "')\"/>"
                userStateAddData.push(data.fraudRuleList[i]);
            } else if (data.fraudRuleList[i].fraudType == userCity) {
                let state = "state";
                let countryNameCityBlocking = "countryNameCityBlocking";
                let countryValue = data.fraudRuleList[i].country;
                let stateValue = data.fraudRuleList[i].state;
                data.fraudRuleList[i]['editBtnBlockCity'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule(" + JSON.stringify(state) + "," + JSON.stringify(countryNameCityBlocking) + "," + JSON.stringify(countryNameCityBlocking) + "," + JSON.stringify(data.fraudRuleList[i]['city']) + "," + JSON.stringify(countryValue) + "," + JSON.stringify(stateValue) + ");editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal18'/>";
                data.fraudRuleList[i]['deleteBtnBlockCity'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick=\"deleteFraudRule('" + data.fraudRuleList[i]['id'] + "')\"/>"
                userCityAddData.push(data.fraudRuleList[i]);
            } else if (data.fraudRuleList[i].fraudType == repeatedMopTypeForSameDetails) {
                data.fraudRuleList[i]['editBtnBlockRepeatedMop'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule();editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal19'/>";
                data.fraudRuleList[i]['deleteBtnBlockRepeatedMop'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick=\"deleteFraudRule('" + data.fraudRuleList[i]['id'] + "')\"/>"
                repeatedMopTypeAddData.push(data.fraudRuleList[i]);
            }
            else if (data.fraudRuleList[i].fraudType == 'BLOCK_VPA_ADDRESS') {
                data.fraudRuleList[i]['editBtnBlockVpaAddress'] = "<input class='btn btn-primary btn-xs editRules' style='margin-left:10px' disabled='disabled' name='editRules' type='submit' value='Edit' onclick='tagsAddRule();editFraudRule(" + JSON.stringify(data.fraudRuleList[i]) + ")' data-toggle='modal' data-target='#myModal20'/>";
                data.fraudRuleList[i]['deleteBtnBtnBlockVpaAddress'] = "<input class='btn btn-danger btn-xs deleteBtnForRule' style='margin-left:10px' disabled='disabled' name='deleteRules' type='submit' value='Delete' onclick=\"deleteFraudRule('" + data.fraudRuleList[i]['id'] + "')\"/>"
                data.fraudRuleList[i]['isSelectedVpaAddress'] = "<input type='checkbox' onclick='checkboxclickcheck20()' class='checkbox20' name='BLOCK_VPA_ADDRESS" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
                blockVpaAddData.push(data.fraudRuleList[i]);
            }

        }
        downloadDataForVpa = blockVpaAddData;

        ruleCountByBlockType = {
            "onIpBlocking": blockIpAddData.length,
            "onIssuerCountriesBlocking": issuerCountryAddData.length,
            "onUserCountriesBlocking": userCountryAddData.length,
            "onStateBlocking": userStateAddData.length,
            "onCityBlocking": userCityAddData.length,
            "onEmailBlocking": emailListAddData.length,
            "onLimitTxnAmtBlocking": txnAmtAddData.length,
            "onCardRangeBlocking": cardRangeAddData.length,
            "onCardMaskBlocking": cardMaskAddData.length,
            "onLimitCardTxnBlocking": perCardTxnAddData.length,
            "onPhoneNoBlocking": phoneListAddData.length,
            "onTxnAmountVelocityBlocking": txnAmtVelocityAddData.length,
            "onMacBlocking": blockMackAddData.length,
            "onNotifyRepeatedMopType": notifyRepeatedMopTypeData.length,
            "onBlockRepeatedMopTypeForSameDetails": repeatedMopTypeAddData.length,
            "onVpaBlocking": blockVpaAddData.length
        }
        generateBlockIdAddressTable(blockIpAddData);
        generateBlockIssuerCountryTable(issuerCountryAddData);
        generateBlockUserCountryTable(userCountryAddData);
        generateBlockUserStateTable(userStateAddData);
        generateBlockUserCityTable(userCityAddData);
        generateBlockEmailAddressTable(emailListAddData);
        generateBlockTxnAmtTable(txnAmtAddData);
        generateBlockCardRangeTable(cardRangeAddData);
        generateBlockCardMaskTable(cardMaskAddData);
        generateBlockCardTxnAmtTable(perCardTxnAddData);
        generateBlockPhoneNumberTable(phoneListAddData);
        generateBlockTxnAmtVelocityTable(txnAmtVelocityAddData);
        generateBlockMackAddressTable(blockMackAddData);
        generateNotifyRepeatedMopTypeTable(notifyRepeatedMopTypeData);
        generateNotifyRepeatedMopTypeSameDataTable(repeatedMopTypeAddData);
        generateBlockVpaAddressTable(blockVpaAddData);

    })
}

function generateBlockIdAddressTable(blockIpAddData) {
    $('#ipAddListBody').dataTable({
        "aaData": blockIpAddData,
        "columns": ipRuleColumns,
        "searching": false,
        "destroy": true,
        "paginationType": "full_numbers",
    })
}
function generateBlockIssuerCountryTable(issuerCountryAddData) {
    $('#issuerCountryListBody').dataTable({
        "aaData": issuerCountryAddData,
        "columns": issuerCountryColumns,
        "searching": false,
        "destroy": true,
        "paginationType": "full_numbers",
    })
}
function generateBlockUserCountryTable(userCountryAddData) {
    $('#userCountryListBody').dataTable({
        "aaData": userCountryAddData,
        "columns": userCountryColumns,
        "searching": false,
        "destroy": true,
        "paginationType": "full_numbers",
    })
}

function generateBlockUserStateTable(userStateAddData) {
    $('#userStateListBody').dataTable({
        "aaData": userStateAddData,
        "columns": userStateColumns,
        "searching": false,
        "destroy": true,
        "paginationType": "full_numbers",
    })
}

function generateBlockUserCityTable(userCityAddData) {
    $('#userCityListBody').dataTable({
        "aaData": userCityAddData,
        "columns": userCityColumns,
        "searching": false,
        "destroy": true,
        "paginationType": "full_numbers",
    })
}
function generateBlockEmailAddressTable(emailListAddData) {
    $('#emailListBody').dataTable({
        "aaData": emailListAddData,
        "columns": emailRuleColumns,
        "searching": false,
        "destroy": true,
        "paginationType": "full_numbers",
    })
}
function generateBlockTxnAmtTable(txnAmtAddData) {
    $('#txnAmountListBody').dataTable({
        "aaData": txnAmtAddData,
        "columns": txnAmountColumns,
        "searching": false,
        "paging": false,
        "ordering": false,
        "info": false,
        "destroy": true,

    })

    $("#popupButton8").attr("disabled", txnAmtAddData.length ? true : false);
}
function generateBlockCardRangeTable(cardRangeAddData) {
    $('#cardBinListBody').dataTable({
        "aaData": cardRangeAddData,
        "columns": cardBinColumns,
        "searching": false,
        "destroy": true,
        "paginationType": "full_numbers",
    })
}
function generateBlockCardMaskTable(cardMaskAddData) {
    $('#cardNoListBody').dataTable({
        "aaData": cardMaskAddData,
        "columns": cardNoColumns,
        "searching": false,
        "destroy": true,
        "paginationType": "full_numbers",
    })
}
function generateBlockCardTxnAmtTable(perCardTxnAddData) {
    $('#perCardTxnsListBody').dataTable({
        "aaData": perCardTxnAddData,
        "columns": perCardTxnsColumns,
        "searching": false,
        "destroy": true,
        "paginationType": "full_numbers",
    })
}
function generateBlockPhoneNumberTable(phoneListAddData) {
    $('#phoneListBody').dataTable({
        "aaData": phoneListAddData,
        "columns": phoneRuleColumns,
        "searching": false,
        "processing": true,
        "destroy": true,
        "paginationType": "full_numbers",
    })
}

function generateBlockTxnAmtVelocityTable(txnAmtVelocityAddData) {
    $('#txnAmountVelocityListBody').dataTable({
        "aaData": txnAmtVelocityAddData,
        "columns": txnAmountVelocityColumns,
        "searching": false,
        "paging": false,
        "ordering": false,
        "info": false,
        "destroy": true,
    })
    $("#popupButton11").attr("disabled", txnAmtVelocityAddData.length ? true : false);
}

function generateBlockMackAddressTable(blockMackAddData) {
    $('#mackAddListBody').dataTable({
        "aaData": blockMackAddData,
        "columns": mackRuleColumns,
        "searching": false,
        "destroy": true,
        "paginationType": "full_numbers",
    })
}

function generateNotifyRepeatedMopTypeTable(notifyMopTypeData) {
    $('#repeatedMopTypeListBody').dataTable({
        "aaData": notifyMopTypeData,
        "columns": notifyMopTypeRuleColumns,
        "searching": false,
        "destroy": true,
        "paginationType": "full_numbers",
    })
}

function generateNotifyRepeatedMopTypeSameDataTable(notifyMopTypeData) {
    $('#repetedMopTypeListBody').dataTable({
        "aaData": notifyMopTypeData,
        "columns": notifyMopTypeSameDetailsRuleColumns,
        "searching": false,
        "destroy": true,
        "paginationType": "full_numbers",
    })
}

function generateBlockVpaAddressTable(blockVpaAddData) {
    $('#vpaAddListBody').dataTable({
        "aaData": blockVpaAddData,
        "columns": vpaRuleColumns,
        "searching": false,
        "destroy": true,
        "paginationType": "full_numbers",
    })
}
// function fetchFraudRuleList(payIdValue,ruleId){
//    $.ajax({
//      url : 'ruleListAction',
//      type : 'post',
//      data : {
//        token : document.getElementsByName("token")[0].value,
//        payId : (payIdValue == 'ALL')? payIdValue :getFieldValue('payId'),
//        ruleId : ruleId,
//      },
//      success : function(data){
//         var fraudRuleList = data.fraudRuleList;
//         var noOfRules = fraudRuleList.length;
//         fraudRuleList.forEach(function(item, index){
//           writeFraudTable(item, index);
//         });
//      },
//      error :  function(data){
//        alert('soemthing went wrong'+data.response);
//      }
//    });
// }

//clear all the old displayed fraud rules on merchant changed
// function clearFraudRules(tableNames){
//  var index=0;
//  while(index<10){
//    for(var i=2;;i++){
//    //  var element = document.getElementById(tablenames[index]).childNodes[1].childNodes[i];
//       var element = document.getElementById(tableNames[index]).childNodes[3].childNodes[i];
//         if(element!=null){
//           element.innerHTML='';
//         }else{
//           break;
//         }
//    }
//    index++;
//  }
// }

function showDialog(data) {
    $("#dialogBox").dialog({
        modal: true,
        draggable: false,
        resizable: false,
        position: ['center', 'center'],
        show: 'blind',
        hide: 'blind',
        width: 328,
        height: 72,
        buttons: [
            {
                text: "Ok, Proceed",
                click: function () {
                    $(this).dialog("close");
                }
            },
        ],
        open: function () {
            $("#dialogBox").css("overflow", "hidden");
        },
        dialogClass: 'ui-dialog-osx',
    });
}

$(document).ready(function () {
    //by default fraud rules for ALL MERCHANTS will be displayed
    //fetchFraudRuleList('ALL');
    $('#currencyId').change(function (event) {
        enableButtons();
        var currencySelected = $("#currencyId").val();
        console.log("Currency Type: " + currencySelected)
        // clearFraudRules(columnsRules);
        fetchFraudRuleList('payId', currencySelected);
    });
});
function enableButtons() {
    $("#blockIp").attr("disabled", false);
    $("#blockWhiteIp").attr("disabled", false);
    $("#blockIssuer").attr("disabled", false);
    $("#blockUserCountry").attr("disabled", false);
    $("#blockCardRange").attr("disabled", false);
    $("#blockEmail").attr("disabled", false);
    $("#blockCardNo").attr("disabled", false);
    $("#blockTxnAmt").attr("disabled", false);
    $("#blockPhone").attr("disabled", false);
    $("#blockTxnAmtVel").attr("disabled", false);
    $("#percardTransactionBtn").attr("disabled", false);
    $("#blockVpa").attr("disabled", false);
}

function makeCardMaskini() {

    var element = document.getElementById('negativeCard');
    var initialDigits = document.getElementById('cardIntialDigits').value;
    var lastDigits = document.getElementById('cardLastDigits').value;
    value = element.value;
    element.value = initialDigits + "******" + lastDigits;

    if (initialDigits != "" && initialDigits != null) {
        if (initialDigits.length == 6) {
            var arra = element.value.split("");
            if (arra[0] == "2" || arra[0] == "3" || arra[0] == "4" || arra[0] == "5" || arra[0] == "6") {
                $('#validate_crdIn').text('Valid Card Length');
                document.getElementById("validate_crdIn").classList.add("cardIniSuccess");
                document.getElementById("validate_crdIn").classList.remove("cardIniError");
                document.getElementById("validate_crdIn").style.display = "block";
                document.getElementById("blockCardNo").disabled = false;
                return true;
            }
            else {
                $('#validate_crdIn').text('Enter card number starts with 2,3,4,5 and 6 only');
                document.getElementById("validate_crdIn").classList.add("cardIniError");
                document.getElementById("validate_crdIn").classList.remove("cardIniSuccess");
                document.getElementById("validate_crdIn").style.display = "block";
                document.getElementById("blockCardNo").disabled = true;
                return false;
            }
        }
        else {
            $('#validate_crdIn').text('Only 6 digits Allowed');
            document.getElementById("validate_crdIn").classList.remove("cardIniSuccess");
            document.getElementById("validate_crdIn").classList.add("cardIniError");
            document.getElementById("validate_crdIn").style.display = "block";
            document.getElementById("blockCardNo").disabled = true;
            return false;
        }
    }
    else {
        $('#validate_crdIn').text('Only 6 digits Allowed');
        document.getElementById("validate_crdIn").classList.remove("cardIniSuccess");
        document.getElementById("validate_crdIn").classList.add("cardIniError");
        document.getElementById("validate_crdIn").style.display = "block";
        document.getElementById("blockCardNo").disabled = true;
        return false;
    }
}

function makeCardMasklst() {

    var element = document.getElementById('negativeCard');
    var initialDigits = document.getElementById('cardIntialDigits').value;
    var lastDigits = document.getElementById('cardLastDigits').value;
    value = element.value;
    if (lastDigits.length == 4) {
        element.value = initialDigits + "******" + lastDigits;
        $('#validate_crdL').text('Valid Card Length');
        document.getElementById("validate_crdL").classList.add("cardLstSuccess");
        document.getElementById("validate_crdL").classList.remove("cardLstError");
        document.getElementById("validate_crdL").style.display = "block";
        return true;
    }
    else {
        $('#validate_crdL').text(' Enter last 4-digits');
        document.getElementById("validate_crdL").classList.add("cardLstError");
        document.getElementById("validate_crdL").classList.remove("cardLstSuccess");
        document.getElementById("validate_crdL").style.display = "block";
        return false;
    }
}
function iniCardMask() {

    var element = document.getElementById('prenegativeCard');
    var preinitialDigits = document.getElementById('precardIntialDigits').value;
    var prelastDigits = document.getElementById('precardLastDigits').value;
    value = element.value;
    element.value = preinitialDigits + "******" + prelastDigits;

    if (preinitialDigits != "" || preinitialDigits != null) {
        if (preinitialDigits.length == 6) {
            var arra = element.value.split("");
            if (arra[0] == "2" || arra[0] == "3" || arra[0] == "4" || arra[0] == "5" || arra[0] == "6") {
                $('#validate_crdInpre').text('Valid Card Length');
                document.getElementById("validate_crdInpre").classList.add("cardIniSuccess");
                document.getElementById("validate_crdInpre").classList.remove("cardIniError1");
                document.getElementById("validate_crdInpre").style.display = "block";
                document.getElementById("percardTransactionBtn").disabled = false;
                return true;
            }
            else {
                $('#validate_crdInpre').text('Only number 2,3,4,5 and 6 digit allowed');
                document.getElementById("validate_crdInpre").classList.add("cardIniError1");
                document.getElementById("validate_crdInpre").classList.remove("cardIniSuccess");
                document.getElementById("validate_crdInpre").style.display = "block";
                document.getElementById("percardTransactionBtn").disabled = true;
                return false;
            }
        }
        else {
            $('#validate_crdInpre').text('Only 6 digits Allowed');
            document.getElementById("validate_crdInpre").classList.remove("cardIniSuccess");
            document.getElementById("validate_crdInpre").classList.add("cardIniError1");
            document.getElementById("validate_crdInpre").style.display = "block";
            document.getElementById("percardTransactionBtn").disabled = true;
            return false;
        }
    }
    else {
        $('#validate_crdInpre').text('Only 6 digits Allowed');
        document.getElementById("validate_crdInpre").classList.remove("cardIniSuccess");
        document.getElementById("validate_crdInpre").classList.add("cardIniError1");
        document.getElementById("validate_crdInpre").style.display = "block";
        document.getElementById("percardTransactionBtn").disabled = true;
        return false;
    }
}

function lastCardMask() {

    var element = document.getElementById('prenegativeCard');
    var preinitialDigits = document.getElementById('precardIntialDigits').value;
    var prelastDigits = document.getElementById('precardLastDigits').value;
    value = element.value;
    element.value = preinitialDigits + "******" + prelastDigits;

    if (prelastDigits.length == 4) {
        $('#validate_crdLpre').text('Valid Card Length');
        document.getElementById("validate_crdLpre").classList.add("cardLstSuccess");
        document.getElementById("validate_crdLpre").classList.remove("cardLstError");
        document.getElementById("validate_crdLpre").style.display = "block";
        //document.getElementById("percardTransactionBtn").disabled = false;
        return true;
    }
    else {
        $('#validate_crdLpre').text(' Enter last 4-digits');
        document.getElementById("validate_crdLpre").classList.add("cardLstError");
        document.getElementById("validate_crdLpre").classList.remove("cardLstSuccess");
        document.getElementById("validate_crdLpre").style.display = "block";
        //document.getElementById("percardTransactionBtn").disabled = true;
        return false;
    }
}

function getFieldValue(fieldName, start = 0, end = 6) {
    var element = document.getElementById(fieldName);
    var tagName = element.tagName;
    if (tagName == 'INPUT' || tagName == "INPUT") {
        var fieldValue = element.value;
        var finalValue = (fieldValue != '') ? fieldValue : ''; //TODO regex
        return finalValue;
    } else if (element.tagName == 'SELECT') {
        var fieldValue = element.options[element.selectedIndex].value;
        var finalValue = (!fieldValue.match('SELECT')) ? fieldValue : '';
        return finalValue;
    } else if (element.tagName == 'UL') {
        var optionName = element.getAttribute('data-name');
        var finalValue = [];
        if (optionName == 'country' || optionName == 'userCountry' || optionName == 'stateCode' || optionName == 'city') {
            $('input[name="' + optionName + '"]:checked').each(function (_Index) {
                finalValue.push($(this).val());
            });
        }
        else {
            for (var i = start; i <= end; i++) {
                if ($('#repeatDays' + i).is(":checked")) {
                    var day = '';
                    if (i == 0 || i == 7 || i == 14 || i == 21 || i == 28 || i == 35 || i == 42 || i == 49 || i == 56 || i == 70 || i == 77) {
                        day = 'SUN';
                    } else if (i == 1 || i == 8 || i == 15 || i == 22 || i == 29 || i == 36 || i == 43 || i == 50 || i == 57 || i == 71 || i == 78) {
                        day = 'MON';
                    } else if (i == 2 || i == 9 || i == 16 || i == 23 || i == 30 || i == 37 || i == 44 || i == 51 || i == 58 || i == 72 || i == 79) {
                        day = 'TUE';
                    } else if (i == 3 || i == 10 || i == 17 || i == 24 || i == 31 || i == 38 || i == 45 || i == 52 || i == 59 || i == 73 || i == 80) {
                        day = 'WED';
                    } else if (i == 4 || i == 11 || i == 18 || i == 25 || i == 32 || i == 39 || i == 46 || i == 53 || i == 60 || i == 74 || i == 81) {
                        day = 'THU';
                    } else if (i == 5 || i == 12 || i == 19 || i == 26 || i == 33 || i == 40 || i == 47 || i == 54 || i == 61 || i == 75 || i == 82) {
                        day = 'FRI';
                    } else if (i == 6 || i == 13 || i == 20 || i == 27 || i == 34 || i == 41 || i == 48 || i == 55 || i == 62 || i == 76 || i == 83) {
                        day = 'SAT';
                    }
                    finalValue.push(day);
                }
            }
        }
        // $('input[name="'+optionName+'"]:checked').each(function(){
        //  finalValue.push($(this).val());
        // });
        return finalValue;
    } else if (element.tagName == 'DIV') {
        var optionName = element.getAttribute('data-name');
        var finalValue = [];
        $('input[name="' + optionName + '"]').each(function () {
            finalValue.push($(this).val());
        });
        return finalValue;
    }
}


function fraudFieldValidate(fieldName, fraudType) {
    if (fieldName == 'payId') {
        return getFieldValue(fieldName);
    }
    switch (fraudType) {
        case ipAddressS: {
            debugger;
            if (ajaxValidationFlag) {
                break;
            }

            var alwaysOnFlag = getFieldValue("alwaysOnFlag1");

            if (fieldName == "ipAddress") {

                var ipAddress = getFieldValue("ipAddress");
                if (!(/^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/).test(ipAddress)) {
                    console.log("Invalid IP Address");
                    alert("Enter Valid IP Address");
                    ajaxValidationFlag = true;
                    break;
                }
                else if (ipAddress != '' && ipAddress != null) {
                    ajaxValidationFlag = false;
                    return ipAddress;
                } else {
                    ajaxValidationFlag = true;
                    break;
                }

            } else if (fieldName == "dateActiveFrom") {

                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var dateActiveFrom = getFieldValue("dateActiveFrom");
                    if (dateActiveFrom != '' && dateActiveFrom != null) {
                        ajaxValidationFlag = false;
                        return dateActiveFrom;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }

            } else if (fieldName == "dateActiveTo") {

                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var dateActiveTo = getFieldValue("dateActiveTo");
                    if (dateActiveTo != '' && dateActiveTo != null) {
                        ajaxValidationFlag = false;
                        return dateActiveTo;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }

                }
            } else if (fieldName == "startTime") {

                if (alwaysOnFlag == "true") {
                    break;
                }
                else {

                    var startTime = getFieldValue("startTime");
                    if (startTime != '' && startTime != null) {
                        ajaxValidationFlag = false;
                        return startTime;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }


            } else if (fieldName == "endTime") {

                if (alwaysOnFlag == "true") {
                    break;
                }
                else {

                    var endTime = getFieldValue("endTime");
                    if (endTime != '' && endTime != null) {
                        ajaxValidationFlag = false;
                        return endTime;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }

            } else if (fieldName == "repeatDays") {

                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var value = getFieldValue(fieldName, 0, 6).join(",");
                    if (value != '' && value != null) {
                        ajaxValidationFlag = false;
                        return value;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }

                }
            }
            else if (fieldName == "alwaysOnFlag") {

                return alwaysOnFlag;
            }
        } break;
        case mackAddressS: {
            if (ajaxValidationFlag) {
                break;
            }

            var alwaysOnFlag = getFieldValue("alwaysOnFlag14");
            if (fieldName == "mackAddress") {
                var mackAddressss = getFieldValue("mackAddress");
                if (mackAddressss != '' && mackAddressss != null) {
                    ajaxValidationFlag = false;
                    return mackAddressss;
                } else {
                    ajaxValidationFlag = true;
                    break;
                }

            } else if (fieldName == "dateActiveFrom") {

                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var dateActiveFrom = getFieldValue("dateActiveFrom14");
                    if (dateActiveFrom != '' && dateActiveFrom != null) {
                        ajaxValidationFlag = false;
                        return dateActiveFrom;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }

            } else if (fieldName == "dateActiveTo") {

                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var dateActiveTo = getFieldValue("dateActiveTo14");
                    if (dateActiveTo != '' && dateActiveTo != null) {
                        ajaxValidationFlag = false;
                        return dateActiveTo;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }

                }
            } else if (fieldName == "startTime") {

                if (alwaysOnFlag == "true") {
                    break;
                }
                else {

                    var startTime = getFieldValue("startTime14");
                    if (startTime != '' && startTime != null) {
                        ajaxValidationFlag = false;
                        return startTime;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }


            } else if (fieldName == "endTime") {

                if (alwaysOnFlag == "true") {
                    break;
                }
                else {

                    var endTime = getFieldValue("endTime14");
                    if (endTime != '' && endTime != null) {
                        ajaxValidationFlag = false;
                        return endTime;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }

            } else if (fieldName == "repeatDays") {

                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var value = getFieldValue(fieldName, 70, 76).join(",");
                    if (value != '' && value != null) {
                        ajaxValidationFlag = false;
                        return value;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }

                }
            }
            else if (fieldName == "alwaysOnFlag") {
                return alwaysOnFlag;
            }

        } break;
        case emailS: {
            if (ajaxValidationFlag) {
                break;
            }

            var alwaysOnFlag = getFieldValue("alwaysOnFlag4");

            if (fieldName == "email") {
                var email = getFieldValue("email");
                if (!(/^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/).test(email)) {
                    alert("Enter Valid Email");
                    ajaxValidationFlag = true
                    break;
                }
                else if (email != '' && email != null) {
                    ajaxValidationFlag = false;
                    return email;
                } else {
                    ajaxValidationFlag = true;
                    break;
                }

            } else if (fieldName == "dateActiveFrom") {

                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var dateActiveFrom = getFieldValue("dateActiveFrom3");
                    if (dateActiveFrom != '' && dateActiveFrom != null) {
                        ajaxValidationFlag = false;
                        return dateActiveFrom;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }

            } else if (fieldName == "dateActiveTo") {
                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var dateActiveTo = getFieldValue("dateActiveTo3");
                    if (dateActiveTo != '' && dateActiveTo != null) {
                        ajaxValidationFlag = false;
                        return dateActiveTo;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }

            } else if (fieldName == "startTime") {
                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var startTime = getFieldValue("startTime3");
                    if (startTime != '' && startTime != null) {
                        ajaxValidationFlag = false;
                        return startTime;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }

            } else if (fieldName == "endTime") {
                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var endTime = getFieldValue("endTime3");
                    if (endTime != '' && endTime != null) {
                        ajaxValidationFlag = false;
                        return endTime;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }

            } else if (fieldName == "repeatDays") {

                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var value = getFieldValue(fieldName, 21, 27).join(",");
                    if (value != '' && value != null) {
                        ajaxValidationFlag = false;
                        return value;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }
            } else if (fieldName == "alwaysOnFlag") {

                return alwaysOnFlag;

            }
        } break;
        case userCountry: {
            if (fieldName == "userCountry") {
                var value = getFieldValue(fieldName).join(",");
                //alert(value);
                if (value != '' && value != null) {
                    ajaxValidationFlag = false;
                    return value;
                } else {
                    ajaxValidationFlag = true;
                    break;
                }
            }
        } break;
        case issuerCountry: {
            if (fieldName == "issuerCountry") {
                var value = getFieldValue(fieldName).join(",");
                //alert(value);
                if (value != '' && value != null) {
                    ajaxValidationFlag = false;
                    return value;
                } else {
                    ajaxValidationFlag = true;
                    break;
                }
            }
        } break;
        case numberOfTransaction: {
            if (fieldName == "timeDuration") {
                return getFieldValue(fieldName);
            }
            if (fieldName == "minutesTxnLimit") {
                var minutesTxnLimit = getFieldValue("minutesTxnLimit");
                if (minutesTxnLimit != '' && minutesTxnLimit != null) {
                    ajaxValidationFlag = false;
                    return minutesTxnLimit;
                } else {
                    ajaxValidationFlag = true;
                    break;
                }
            } else if (fieldName == "perCardTransactionAllowed") {
                var perCardTransactionAllowed = getFieldValue("perCardTransactionAllowed");
                if (perCardTransactionAllowed != '' && perCardTransactionAllowed != null) {
                    ajaxValidationFlag = false;
                    return perCardTransactionAllowed;
                } else {
                    ajaxValidationFlag = true;
                    break;
                }
            }
        } break;
        case transactionAmount: {
            if (fieldName == "timeDuration") {
                return getFieldValue(fieldName);
            }
            if (fieldName == "fixedAmountFlag") {
                return document.getElementById(fieldName).checked;
            }
            if (fieldName == "transactionAmount" || fieldName == "minTransactionAmount" || fieldName == "maxTransactionAmount" || fieldName == "repetationCount") {
                var minAmount = getFieldValue("minTransactionAmount");
                var maxAmount = getFieldValue("maxTransactionAmount");
                var fixAmount = getFieldValue("transactionAmount");
                var fixedAmountFlag = getFieldValue("fixedAmountFlag");
                var repetationCounts = getFieldValue("repetationCount");
                var value = getFieldValue(fieldName);
                if ((fixedAmountFlag && (fixAmount.trim() != null || Number(repetationCounts) > 1)) || !fixedAmountFlag && (minAmount.trim() != null && maxAmount.trim() != null) && (parseFloat(minAmount.trim()) <= parseFloat(maxAmount.trim()))) {
                    ajaxValidationFlag = false;
                    return value;
                } else {
                    ajaxValidationFlag = true;
                    break;
                }
            }
        } break;
        case negativeBinS: {
            if (ajaxValidationFlag) {
                break;
            }
            var alwaysOnFlag = getFieldValue("alwaysOnFlag3");

            if (fieldName == "negativeBin") {

                var negativeBin = getFieldValue("negativeBin");
                if (negativeBin != '' && negativeBin != null) {
                    ajaxValidationFlag = false;
                    return negativeBin;
                } else {
                    ajaxValidationFlag = true;
                    break;
                }

            } else if (fieldName == "dateActiveFrom") {
                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var dateActiveFrom = getFieldValue("dateActiveFrom4");
                    if (dateActiveFrom != '' && dateActiveFrom != null) {
                        ajaxValidationFlag = false;
                        return dateActiveFrom;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }

            } else if (fieldName == "dateActiveTo") {

                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var dateActiveTo = getFieldValue("dateActiveTo4");
                    if (dateActiveTo != '' && dateActiveTo != null) {
                        ajaxValidationFlag = false;
                        return dateActiveTo;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }

            } else if (fieldName == "startTime") {
                if (alwaysOnFlag == "true") {
                    break;
                }
                else {

                    var startTime = getFieldValue("startTime4");
                    if (startTime != '' && startTime != null) {
                        ajaxValidationFlag = false;
                        return startTime;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }

            } else if (fieldName == "endTime") {
                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var endTime = getFieldValue("endTime4");
                    if (endTime != '' && endTime != null) {
                        ajaxValidationFlag = false;
                        return endTime;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }
            } else if (fieldName == "repeatDays") {
                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var value = getFieldValue(fieldName, 14, 20).join(",");
                    if (value != '' && value != null) {
                        ajaxValidationFlag = false;
                        return value;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }
            } else if (fieldName == "alwaysOnFlag") {

                return alwaysOnFlag;

            }
        } break;
        case negativeCardS: {
            if (ajaxValidationFlag) {
                break;
            }
            var alwaysOnFlag = getFieldValue("alwaysOnFlag6");

            if (fieldName == "negativeCard") {

                var negativeCard = getFieldValue("negativeCard");
                if (negativeCard != '' && negativeCard != null) {
                    ajaxValidationFlag = false;
                    return negativeCard;
                } else {
                    ajaxValidationFlag = true;
                    break;
                }

            } else if (fieldName == "dateActiveFrom") {
                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var dateActiveFrom = getFieldValue("dateActiveFrom5");
                    if (dateActiveFrom != '' && dateActiveFrom != null) {
                        ajaxValidationFlag = false;
                        return dateActiveFrom;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }

            } else if (fieldName == "dateActiveTo") {
                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var dateActiveTo = getFieldValue("dateActiveTo5");
                    if (dateActiveTo != '' && dateActiveTo != null) {
                        ajaxValidationFlag = false;
                        return dateActiveTo;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }

            } else if (fieldName == "startTime") {
                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var startTime = getFieldValue("startTime5");
                    if (startTime != '' && startTime != null) {
                        ajaxValidationFlag = false;
                        return startTime;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }
            } else if (fieldName == "endTime") {
                if (alwaysOnFlag == "true") {
                    break;
                }
                else {

                    var endTime = getFieldValue("endTime5");
                    if (endTime != '' && endTime != null) {
                        ajaxValidationFlag = false;
                        return endTime;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }

            } else if (fieldName == "repeatDays") {
                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var value = getFieldValue(fieldName, 35, 41).join(",");
                    if (value != '' && value != null) {
                        ajaxValidationFlag = false;
                        return value;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }
            } else if (fieldName == "alwaysOnFlag") {

                return alwaysOnFlag;

            }
        } break;
        case phoneS: {
            if (ajaxValidationFlag) {
                break;
            }

            var alwaysOnFlag = getFieldValue("alwaysOnFlag12");

            if (fieldName == "phone") {
                var phone = getFieldValue("phone");
                if (!(/^[+0-9]{8,13}$/).test(phone)) {
                    alert("Enter Valid Phone Number");
                    ajaxValidationFlag = true;
                    break;
                }
                else if (phone != '' && phone != null) {
                    ajaxValidationFlag = false;
                    return phone;
                } else {
                    ajaxValidationFlag = true;
                    break;
                }

            } else if (fieldName == "dateActiveFrom") {

                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var dateActiveFrom = getFieldValue("dateActiveFrom12");
                    if (dateActiveFrom != '' && dateActiveFrom != null) {
                        ajaxValidationFlag = false;
                        return dateActiveFrom;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }

            } else if (fieldName == "dateActiveTo") {
                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var dateActiveTo = getFieldValue("dateActiveTo12");
                    if (dateActiveTo != '' && dateActiveTo != null) {
                        ajaxValidationFlag = false;
                        return dateActiveTo;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }

            } else if (fieldName == "startTime") {
                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var startTime = getFieldValue("startTime12");
                    if (startTime != '' && startTime != null) {
                        ajaxValidationFlag = false;
                        return startTime;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }

            } else if (fieldName == "endTime") {
                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var endTime = getFieldValue("endTime12");
                    if (endTime != '' && endTime != null) {
                        ajaxValidationFlag = false;
                        return endTime;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }

            } else if (fieldName == "repeatDays") {

                if (alwaysOnFlag == "true") {
                    break;
                }

                else {
                    var value = getFieldValue(fieldName, 49, 55).join(",");
                    if (value != '' && value != null) {
                        ajaxValidationFlag = false;
                        return value;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }
            } else if (fieldName == "alwaysOnFlag") {

                return alwaysOnFlag;

            }
        } break;
        case perCardTransactionAllowedS: {

            if (fieldName == "timeDuration") {
                return getFieldValue(fieldName);
            }
            if (ajaxValidationFlag) {
                break;
            }

            var alwaysOnFlag = getFieldValue("alwaysOnFlag7");

            if (fieldName == "negativeCard") {

                var negativeCard = getFieldValue("prenegativeCard");
                if (negativeCard != '' && negativeCard != null) {
                    ajaxValidationFlag = false;
                    return negativeCard;
                } else {
                    ajaxValidationFlag = true;
                    break;
                }

            } else if (fieldName == "perCardTransactionAllowed") {

                var perCardTransactionAllowed = getFieldValue("perCardTransactionAllowed");
                if (perCardTransactionAllowed != '' && perCardTransactionAllowed != null) {
                    ajaxValidationFlag = false;
                    return perCardTransactionAllowed;
                } else {
                    ajaxValidationFlag = true;
                    break;
                }


            } else if (fieldName == "dateActiveFrom") {
                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var dateActiveFrom = getFieldValue("dateActiveFrom7");
                    if (dateActiveFrom != '' && dateActiveFrom != null) {
                        ajaxValidationFlag = false;
                        return dateActiveFrom;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }

            } else if (fieldName == "dateActiveTo") {
                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var dateActiveTo = getFieldValue("dateActiveTo7");
                    if (dateActiveTo != '' && dateActiveTo != null) {
                        ajaxValidationFlag = false;
                        return dateActiveTo;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }

            } else if (fieldName == "startTime") {
                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var startTime = getFieldValue("startTime7");
                    if (startTime != '' && startTime != null) {
                        ajaxValidationFlag = false;
                        return startTime;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }
            } else if (fieldName == "endTime") {
                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var endTime = getFieldValue("endTime7");
                    if (endTime != '' && endTime != null) {
                        ajaxValidationFlag = false;
                        return endTime;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }

            } else if (fieldName == "repeatDays") {
                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var value = getFieldValue(fieldName, 42, 48).join(",");
                    if (value != '' && value != null) {
                        ajaxValidationFlag = false;
                        return value;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }
            } else if (fieldName == "alwaysOnFlag") {

                return alwaysOnFlag;

            }
        } break;
        case repeatedMopTypesS: {
            if (fieldName == "timeDuration" || fieldName == "emailToNotify" || fieldName == "paymentType" || fieldName == "percentageOfRepeatedMop") {
                var value = getFieldValue(fieldName);
                if ((fieldName != 'percentageOfRepeatedMop' && value != null && value != '') || (fieldName == 'percentageOfRepeatedMop' && value != null && value != '' && (Number(value) > 0) && (Number(value) <= 100))) {
                    ajaxValidationFlag = false;
                    return value;
                } else {
                    ajaxValidationFlag = true;
                    return value;
                }
            }
        } break;
        case transactionAmountVelocity: {
            if (fieldName == "timeDuration" || fieldName == "repetationCount") {
                return getFieldValue(fieldName + "Velocity");
            }
            if (fieldName == "maxTransactionAmount" || fieldName == "currency" || fieldName == "emailToNotify") {
                //var txnAmount = getFieldValue("txnAmountVelocity");
                //var currency = getFieldValue("currency");
                var value = "";
                if (fieldName == "emailToNotify") {
                    value = getFieldValue("emailToNotifyVelocity");
                } else if (fieldName == "currency") {
                    value = getFieldValue("currencyId");
                } else {
                    value = getFieldValue("txnAmountVelocity");
                    if (value == undefined || value == null || value == '') {
                        value = 0;
                    }
                    return value;
                }
                if (value) {
                    ajaxValidationFlag = false;
                    return value;
                } else {
                    ajaxValidationFlag = true;
                    break;
                }
            }
        } break;
        case vpaAddressS: {
            if (ajaxValidationFlag) {
                break;
            }
            //        	if(!alwaysOnFlag){
            //        		var vpaAddress = document.getElementById("vpaAddress").value;
            //        		var dateActiveFrom = document.getElementById("dateActiveFrom").value;
            //        		var dateActiveTo = document.getElementById("dateActiveTo").value;
            //        		var startTime = document.getElementById("startTime").value;
            //        		var dateActiveTo = document.getElementById("endTime").value;
            //        		var repeatDays = document.getElementById("repeatDays").value;
            //        		alert()
            //        	}
            var alwaysOnFlag = getFieldValue("alwaysOnFlag20");
            var vpaAddress = getFieldValue("vpaAddress");
            console.log("VPA Address " + vpaAddress);
            if (!(/^[\w.-]+@[\w.-]+$/).test(vpaAddress)) {
                alert("Enter valid VPA Address");
                ajaxValidationFlag = true;
                break;

            }
            if (fieldName == "vpaAddress") {

                var vpaAddress = getFieldValue("vpaAddress");
                if (vpaAddress != '' && vpaAddress != null) {
                    ajaxValidationFlag = false;
                    return vpaAddress;
                } else {
                    ajaxValidationFlag = true;
                    break;
                }

            } else if (fieldName == "dateActiveFrom") {

                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var dateActiveFrom = getFieldValue("dateActiveFromVpa");
                    if (dateActiveFrom != '' && dateActiveFrom != null) {
                        ajaxValidationFlag = false;
                        return dateActiveFrom;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }

            } else if (fieldName == "dateActiveTo") {

                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var dateActiveTo = getFieldValue("dateActiveToVpa");
                    if (dateActiveTo != '' && dateActiveTo != null) {
                        ajaxValidationFlag = false;
                        return dateActiveTo;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }

                }
            } else if (fieldName == "startTime") {

                if (alwaysOnFlag == "true") {
                    break;
                }
                else {

                    var startTime = getFieldValue("startTimeVpa");
                    if (startTime != '' && startTime != null) {
                        ajaxValidationFlag = false;
                        return startTime;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }


            } else if (fieldName == "endTime") {

                if (alwaysOnFlag == "true") {
                    break;
                }
                else {

                    var endTime = getFieldValue("endTimeVpa");
                    if (endTime != '' && endTime != null) {
                        ajaxValidationFlag = false;
                        return endTime;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }
                }

            } else if (fieldName == "repeatDays") {

                if (alwaysOnFlag == "true") {
                    break;
                }
                else {
                    var value = getFieldValue(fieldName, 77, 83).join(",");
                    if (value != '' && value != null) {
                        ajaxValidationFlag = false;
                        return value;
                    } else {
                        ajaxValidationFlag = true;
                        break;
                    }

                }
            }
            else if (fieldName == "alwaysOnFlag") {

                return alwaysOnFlag;
            }
        } break;
    }
}

function ajaxFraudRequest(fraudType) {
    ajaxValidationFlag = false;
    let id = $("#ruleId").val();
    var alwaysOnFlag = fraudFieldValidate('alwaysOnFlag', fraudType);
    var phone = fraudFieldValidate('phone', fraudType);
    var dateActiveFrom = fraudFieldValidate('dateActiveFrom', fraudType);
    var dateActiveTo = fraudFieldValidate('dateActiveTo', fraudType);
    var startTime = fraudFieldValidate('startTime', fraudType);
    var endTime = fraudFieldValidate('endTime', fraudType);
    var repeatDays = fraudFieldValidate('repeatDays', fraudType);
    var ipAddress = fraudFieldValidate('ipAddress', fraudType);
    var token = document.getElementsByName("token")[0].value;
    var payId = fraudFieldValidate('payId', fraudType);
    var issuerCountry = fraudFieldValidate('issuerCountry', fraudType);
    var email = fraudFieldValidate('email', fraudType);
    var negativeBin = fraudFieldValidate('negativeBin', fraudType);
    var negativeCard = fraudFieldValidate('negativeCard', fraudType);
    var fixedAmountFlag = fraudFieldValidate('fixedAmountFlag', fraudType);
    var currency = fraudFieldValidate('currency', fraudType);
    var txnAmount = fraudFieldValidate('transactionAmount', fraudType);
    var repetationCount = fraudFieldValidate('repetationCount', fraudType);
    var minTransactionAmount = fraudFieldValidate('minTransactionAmount', fraudType);
    var maxTransactionAmount = fraudFieldValidate('maxTransactionAmount', fraudType);
    var userCountry = fraudFieldValidate('userCountry', fraudType);
    var minutesTxnLimit = fraudFieldValidate('minutesTxnLimit', fraudType);
    var perCardTransactionAllowed = fraudFieldValidate('perCardTransactionAllowed', fraudType);
    var mackAddresss = fraudFieldValidate('mackAddress', fraudType);
    var blockTimeUnits = fraudFieldValidate('timeDuration', fraudType);
    var percentageOfRepeatedMop = fraudFieldValidate('percentageOfRepeatedMop', fraudType);
    var paymentType = fraudFieldValidate('paymentType', fraudType);
    var userIdentifier = fraudFieldValidate('userIdentifier', fraudType);
    var statusVelocity = $('#statusVelocity').val();
    var emailToNotify = getFieldValue("emailToNotify");
    //Added By Sweety
    var vpaAddress = fraudFieldValidate('vpaAddress', fraudType);
    if (fraudType == transactionAmountVelocity) {
        emailToNotify = getFieldValue("emailToNotifyVelocity");
    }
    var userIdentifier = $('input[name=userIdentifier]:checked').val();
    var monitoringType = "";
    $("input:checkbox[name=monitoringType]:checked").each(function () {
        monitoringType = monitoringType + this.value + ",";
    });
    if (monitoringType != '') {
        monitoringType = monitoringType.substring(0, monitoringType.length - 1);
    }
    if ((fraudType != "BLOCK_CARD_ISSUER_COUNTRY") && (fraudType != "BLOCK_USER_COUNTRY") && (fraudType != "BLOCK_TXN_AMOUNT") && (fraudType != "BLOCK_TXN_AMOUNT_VELOCITY") && (fraudType != repeatedMopTypesS)) {
        if (alwaysOnFlag != "true") {
            if ((dateActiveFrom == "" || dateActiveFrom == null) || (dateActiveTo == "" || dateActiveTo == null) || (startTime == "" || startTime == null) || (endTime == "" || endTime == null) || (repeatDays == null || repeatDays == "")) {
                alert("Please Enter Valid Values");
                return false;
            }
            else {
            }
        }
    }
    if (fraudType == repeatedMopTypesS || fraudType == numberOfTransaction || fraudType == transactionAmountVelocity) {
        if (blockTimeUnits == null || blockTimeUnits == '') {
            alert("Please select time duration");
            return false;
        }
    }
    if (fraudType == "BLOCK_TXN_AMOUNT") {
        if (fixedAmountFlag) {
            if (txnAmount == null || txnAmount == '' || txnAmount < 1) {
                alert("Amount can not be less than 1");
                return false;
            }
            if (repetationCount == null || repetationCount == '' || repetationCount < 1) {
                alert("Repetion count can not be less than 1");
                return false;
            }
        } else {
            if (minTransactionAmount == maxTransactionAmount) {
                alert("Min and Max amount can not be same");
                return false;
            }
            if (minTransactionAmount < 1) {
                alert("Min amount can not be less than 1");
                return false;
            }
            if (maxTransactionAmount < 1) {
                alert("Max amount can not be less than 1");
                return false;
            }
        }
    }
    if (ajaxValidationFlag && (fraudType == repeatedMopTypesS || fraudType == transactionAmountVelocity)) {
        if (emailToNotify == null || emailToNotify == '') {
            alert("Please provide email address");
            return false;
        }
    }
    if (fraudType == repeatedMopTypesS) {
        if (emailToNotify == null || emailToNotify == '') {
            alert("Please provide email address");
            return false;
        }
        var isValid = isValidInputTags(emailToNotify.split(","), 'emailToNotify', false);
        if (!isValid) {
            alert("Please provide valid email address");
            return false;
        }
    }
    if (fraudType == transactionAmountVelocity) {

        if (monitoringType == null || monitoringType == '') {
            alert("Please select the type");
            return false;
        }

        if (userIdentifier == null || userIdentifier == '') {
            alert("Please select the rule");
            return false;
        }

        if (repetationCount == null || repetationCount == '' || repetationCount <= 0) {
            alert("Please provide transaction count.");
            return false;
        }

        if (emailToNotify == null || emailToNotify == '') {
            alert("Please provide email address");
            return false;
        }

        var isValid = isValidInputTags(emailToNotify.split(","), 'emailToNotifyVelocity', false);
        if (!isValid) {
            alert("Please provide valid email address");
            return false;
        }
    }

    if (ajaxValidationFlag && fraudType == repeatedMopTypesS) {
        if (paymentType == null || paymentType == '') {
            alert("Please select payment type");
            return false;
        }
        if (percentageOfRepeatedMop == null || percentageOfRepeatedMop == '') {
            alert("Please provide percentage");
            return false;
        }
    }
    if (fraudType == repeatedMopTypesS && (percentageOfRepeatedMop <= 0 || percentageOfRepeatedMop > 100)) {
        alert("Please provide valid percentage");
        return false;
    }

    if (ajaxValidationFlag && (fraudType == repeatedMopTypesS || fraudType == transactionAmountVelocity)) {
        if (emailToNotify == null || emailToNotify == '') {
            alert("Please provide email address");
            return false;
        }
    }
    if (fraudType == vpaAddressS) {

        if (vpaAddress == null || vpaAddress == '') {
            alert("Please provide VPA address");
            return false;
        }
        var isValid = isValidInputTags(vpaAddress.split(","), 'vpaAddress', true);
        if (!isValid) {
            alert("Please provide valid VPA address");
            return false;
        }


    }

    if (!ajaxValidationFlag) {
        $("#blockIp").attr("disabled", true);
        $("#blockWhiteIp").attr("disabled", true);
        $("#blockIssuer").attr("disabled", true);
        $("#blockUserCountry").attr("disabled", true);
        $("#blockCardRange").attr("disabled", true);
        $("#blockEmail").attr("disabled", true);
        $("#blockCardNo").attr("disabled", true);
        $("#blockTxnAmt").attr("disabled", true);
        $("#blockPhone").attr("disabled", true);
        $("#blockTxnAmtVel").attr("disabled", true);
        $("#percardTransactionBtn").attr("disabled", true);
        $("#blockVpa").attr("disabled", true);

        //take currency selected by Merchant
        var currencyArray = [];
        var currencyMop = $("#currencyId").val();

        if (currencyMop == null || currencyMop == "") {
            alert("Please select currency type");
            return false;
        }

        for (var i in currencyMop) {
            currencyArray.push(currencyMop[i]);
            //      console.log(currencyMop[i]);
        }

        var finalCurrencyArray = JSON.stringify(currencyArray);
        console.log(currencyMop);
        console.log(currencyMop);
        console.log(currencyMop);


        var ajaxRequest = $.ajax({
            url: 'addFraudRule',
            type: 'post',
            data: {
                id: id,
                ipAddress: ipAddress,
                mackAddress: mackAddresss,
                token: token,
                payId: payId,
                issuerCountry: issuerCountry,
                email: email,
                negativeBin: negativeBin,
                dateActiveFrom: dateActiveFrom,
                dateActiveTo: dateActiveTo,
                startTime: startTime,
                endTime: endTime,
                repeatDays: repeatDays,
                alwaysOnFlag: alwaysOnFlag,
                negativeCard: negativeCard,
                currency: finalCurrencyArray,
                minTransactionAmount: minTransactionAmount,
                maxTransactionAmount: maxTransactionAmount,
                userCountry: userCountry,
                fraudType: fraudType,
                perCardTransactionAllowed: perCardTransactionAllowed,
                minutesTxnLimit: minutesTxnLimit,
                phone: phone,
                userIdentifier: userIdentifier,
                fixedAmountFlag: fixedAmountFlag,
                repetationCount: repetationCount,
                transactionAmount: txnAmount,
                blockTimeUnits: blockTimeUnits,
                emailToNotify: emailToNotify,
                paymentType: paymentType,
                percentageOfRepeatedMop: percentageOfRepeatedMop,
                statusVelocity: statusVelocity,
                monitoringType: monitoringType,
                vpaAddress: vpaAddress
                //     merchantCurrency:finalCurrencyArray,


            },
            success: function (data) {
                $("#blockIp").attr("disabled", false);
                $("#blockWhiteIp").attr("disabled", false);
                $("#blockIssuer").attr("disabled", false);
                $("#blockUserCountry").attr("disabled", false);
                $("#blockCardRange").attr("disabled", false);
                $("#blockEmail").attr("disabled", false);
                $("#blockCardNo").attr("disabled", false);
                $("#blockTxnAmt").attr("disabled", false);
                $("#blockPhone").attr("disabled", false);
                $("#blockTxnAmtVel").attr("disabled", false);
                $("#percardTransactionBtn").attr("disabled", false);
                $("#blockVpa").attr("disabled", false);
                var result = data;
                if (result != null) {
                    var errorFieldMap = data["Invalid request"];
                    if (errorFieldMap != null) {
                        var error;
                        for (key in errorFieldMap) {
                            (error != null) ? (error + ',' + key) : (error = key);
                        }
                        alert(errorFieldMap[error]);
                    }
                    else {
                        $("#blockIp").attr("disabled", false);
                        $("#blockWhiteIp").attr("disabled", false);
                        $("#blockIssuer").attr("disabled", false);
                        $("#blockUserCountry").attr("disabled", false);
                        $("#blockCardRange").attr("disabled", false);
                        $("#blockEmail").attr("disabled", false);
                        $("#blockCardNo").attr("disabled", false);
                        $("#blockTxnAmt").attr("disabled", false);
                        $("#blockPhone").attr("disabled", false);
                        $("#blockTxnAmtVel").attr("disabled", false);
                        $("#percardTransactionBtn").attr("disabled", false);
                        $("#blockVpa").attr("disabled", false);
                        //TODO
                        alert(data.responseMsg);
                        window.location.reload();
                        //alert(data.responseMsg);
                    }
                    showDialog(data);
                } else {
                }
            },
            error: function (data) {
                alert('Try Again, Something went wrong! ');
            }
        });
    }
    //else (startTime || endTime || repeatDays == ""){
    //alert('Please Enter Valid Values');
    //}
}

function saveNotifyFTxn(fraudType) {
    let ruleId = $("#ruleId").val();
    let emailToNotify = getFieldValue("emailToNotifyFTxn");
    if (fraudType == repeatedMopTypeForSameDetails) {
        emailToNotify = getFieldValue("emailToNotifyRepeatedMop");
    }
    let amount = getFieldValue("transactionAmountFTxn");
    let repetationCount = $("#repetationCountRepeatedMop").val();
    let currencyMop = $("#currencyId").val();

    let timeDuration = $("#timeDurationRepeatedMop").val();
    var monitoringType = "";

    if (currencyMop == '' || currencyMop == null) {
        alert("Please select currency");
        return false;
    }
    if (fraudType == repeatedMopTypeForSameDetails) {
        $("input:checkbox[name=monitoringTypeForRepeatedMop]:checked").each(function () {
            monitoringType = monitoringType + this.value + ",";
        });
        if (monitoringType != '') {
            monitoringType = monitoringType.substring(0, monitoringType.length - 1);
        }

        if (repetationCount == null || repetationCount == '') {
            alert("Please provide transaction count");
            return false;
        }
        if (Number(repetationCount) <= 0) {
            alert("Please provide valid transaction count");
            return false;
        }
    }
    if (fraudType == notifyFirstTransactions) {
        if (amount == null || amount == '') {
            alert("Please provide amount");
            return false;
        }
        if (Number(amount) <= 0) {
            alert("Please provide valid amount");
            return false;
        }
    }
    if (emailToNotify == null || emailToNotify == '') {
        alert("Please provide email address");
        return false;
    }

    let elementId = fraudType == repeatedMopTypeForSameDetails ? "emailToNotifyRepeatedMop" : "emailToNotifyFTxn";
    var isValid = isValidInputTags(emailToNotify.split(","), elementId, false);
    if (!isValid) {
        alert("Please provide valid email address");
        return false;
    }

    var token = document.getElementsByName("token")[0].value;
    var ajaxRequest = $.ajax({
        url: 'addFraudRule',
        type: 'post',
        data: {
            id: ruleId,
            payId: $("#payId").val(),
            token: token,
            fraudType: fraudType,
            transactionAmount: amount,
            emailToNotify: emailToNotify,
            monitoringType: monitoringType,
            repetationCount: repetationCount,
            blockTimeUnits: timeDuration,
            currency: JSON.stringify(currencyMop)
        },
        success: function (data) {
            var result = data;
            if (result != null) {
                var errorFieldMap = data["Invalid request"];
                if (errorFieldMap != null) {
                    var error;
                    for (key in errorFieldMap) {
                        (error != null) ? (error + ',' + key) : (error = key);
                    }
                    alert(errorFieldMap[error]);
                }
                else {
                    alert(data.responseMsg);
                    window.location.reload();
                }
                showDialog(data);
            }
        },
        error: function (data) {
            alert('Try Again, Something went wrong! ');
        }
    });
}

function saveGeoLocationBlockingData(fraudType) {
    let ruleId = $("#ruleId").val();
    let countryName = $("#countryName").val();
    let currency = $("#currencyId").val();
    //console.log("Currency selected: "+currency);
    if (fraudType == userCity) {
        countryName = $("#countryNameCityBlocking").val();
    }
    if (currency == "" || currency == null) {
        alert("Please Select currency");
        return false;
    }
    let stateName = $("#stateNameCityBlocking").val();
    let states = "";
    $("input:checkbox[name=stateCode]:checked").each(function () {
        states = states + this.value + ",";
    });
    if (states != '') {
        states = states.substring(0, states.length - 1);
    }
    if (fraudType == userState && (states == null || states == '')) {
        alert("Please Select State");
        return false;
    }
    let cities = "";
    $("input:checkbox[name=city]:checked").each(function () {
        cities = cities + this.value + ",";
    });
    if (cities != '') {
        cities = cities.substring(0, cities.length - 1);
    }
    if (fraudType == userCity && (cities == null || cities == '')) {
        alert("Please Select City");
        return false;
    }
    var token = document.getElementsByName("token")[0].value;
    var ajaxRequest = $.ajax({
        url: 'addFraudRule',
        type: 'post',
        data: {
            id: ruleId,
            payId: $("#payId").val(),
            token: token,
            fraudType: fraudType,
            stateCode: states,
            city: cities,
            country: countryName,
            currency: JSON.stringify(currency),
            state: stateName
        },
        success: function (data) {
            var result = data;
            if (result != null) {
                var errorFieldMap = data["Invalid request"];
                if (errorFieldMap != null) {
                    var error;
                    for (key in errorFieldMap) {
                        (error != null) ? (error + ',' + key) : (error = key);
                    }
                    alert(errorFieldMap[error]);
                }
                else {
                    alert(data.responseMsg);
                    window.location.reload();
                }
                showDialog(data);
            }
        },
        error: function (data) {
            alert('Try Again, Something went wrong! ');
        }
    });
}

function isValidInputTags(values, elementId, isVpa) {
    debugger
    var regex = isVpa ? /^[\w.-]+@[\w.-]+$/ : /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;// Email address
    let isValid = true;
    for (let i = 0; i < values.length; i++) {
        if (!regex.test(values[i])) {
            $("#" + elementId).tagsinput("remove", values[i]);
            isValid = false;
        }
    }
    return isValid;
}


function getTagsInputVal(divId) {
    var element = document.getElementById(divId);
    var spans = $(element).find('span');
    var finalVal = "";
    for (let j = 0; j < spans.length; j++) {
        var value = spans[j].innerHTML;
        value = value.replace('<span data-role="remove"></span>', "");
        if (value != '') {
            finalVal = finalVal + value + ",";
        }
    }
    if (finalVal != "") {
        finalVal = finalVal.substring(0, finalVal.length - 1);
    }
    return finalVal;
}


/*For Currency based on PayID/Merchant Name*/

function getCurrencyByPayID() {
    var payId = $("#payId").val();
    $('.switch').each(function () {
        this.style.display = "none";
    });
    //Remove already selected value
    //For uncheck dropdown list
    $("#currencyId option:selected").prop("selected", false);
    //For removing dropdown text filed
    $("#select2-currencyId-container li").remove();

    console.log(" " + $("#payId").val());
    if (payId == 'ALL') {
        return getDefaultCurrency();
    }
    $.post("getCurrencyByID",
        {
            merchantPayId: payId
        },
        function (response) {
            console.log(response.currencyByPayID);
            var s = '<option  value="ALL">All</option>';
            for (let key in response.currencyByPayID) {
                s += '<option value="' + key + '"">' + response.currencyByPayID[key] + '</option>';
            }
            $("#currencyId").html(s);
        });
}


function getDefaultCurrency() {
    $.get(
        "getDefaultCurrencyList"
        ,
        function (response) {
            var value = JSON.stringify(response.defultCurrencyMapList);
            console.log(response);
            console.log("Currency Value List: " + value);
            var s = '<option value="ALL">All</option>';
            for (let key in response.defultCurrencyMapList) {
                s += '<option value="' + key + '">' + response.defultCurrencyMapList[key] + '</option>';

            }

            $("#currencyId").html(s);
        }
    );
}



function displaySwitch(payIdEle) {
    checkCurrency(payIdEle);
    var currencySelected = JSON.stringify($("#currencyId").val());
    console.log(typeof currencySelected);
    let token = document.getElementsByName("token")[0].value;
    if (payIdEle.value != '') {
        var payId = $("#payId").val();
        $.ajax({
            type: "POST",
            url: "manageFmsConfig",
            data: {
                payId: payId,
                currency: currencySelected,
                token: token,
                "struts.token.name": "token"
            },
            success: function (data, status) {
                let configurations = data.configuration;
                // console.log("Configurations: "+configurations);
                if (configurations != null) {
                    for (let configKey in configurations) {
                        let configEle = document.getElementById(configKey);
                        if (configEle && configEle != null) {
                            configEle.checked = configurations[configKey];
                        }
                    }
                }
            }
        });
    }
}


function updateConfig(switchElement) {

    let key = switchElement.name;
    let value = switchElement.checked;

    if (ruleCountByBlockType[key] == 0 && value) {
        $(switchElement).removeAttr("checked");
        alert("Please add fraud rule to activate blocking.");
        return false;
    }
    let payId = $("#payId").val();
    let currency = $("#currencyId").val();
    currency = JSON.stringify(currency);
    //  console.log("Currency :"+$('#currencyId option: selected').text());
    //  console.log("currency code: "+currency+" Name "+$("#currencyId option:selected").text());
    let token = document.getElementsByName("token")[0].value;
    $.ajax({
        type: "POST",
        url: "updateFmsConfig",
        data: {
            payId: payId,
            currency: currency,
            key: key,
            value: value,
            token: token,
            "struts.token.name": "token"
        },
        success: function (data, status) {
        }
    });
}

function downloadData() {

    let finalData = [];
    downloadDataForVpa.forEach(function (item, index) {
        //console.log(item.payId +"\t" + item.vpaAddress + "\t" + item.createDate)
        // console.log(item.payId);
        // console.log(item.currency);
        finalData[index] = item.payId + "," + item.vpaAddress + "," + item.createDate;

    });

    JSONToCSVConvertor(finalData, "Block_VpaAddress_List", true);

}

function checkCurrency(payIdEle) {
    var curr = $("#currencyId").val();
    if (jQuery.inArray("ALL", curr) !== -1) {
        $("#currencyId option:selected").prop("selected", false);
        $("#select2-currencyId-container li:not(:first-child)").remove();
        $("#currencyId option[value='ALL']").prop("selected", true);

        //  $("#currencyId").val('ALL');
    }
    let visibility = payIdEle.value == '' ? "none" : "block";
    $('.switch').each(function () {
        this.style.display = visibility;
    });
    if (curr == '') {
        return false;
    }


}

function JSONToCSVConvertor(arrData, ReportTitle, ShowLabel) {

    var CSV = '';

    //This condition will generate the Label/Header
    if (ShowLabel) {
        var row = "PAYID,VPA_ADDRESS,CREATE_DATE";

        //append Label row with line break
        CSV += row + '\r\n';
    }

    //1st loop is to extract each row
    for (var i = 0; i < arrData.length; i++) {
        var row = "";

        row = "'" + arrData[i];

        //add a line break after each row
        CSV += row + '\r\n';
    }

    if (CSV == '') {
        alert("Invalid data");
        return;
    }

    //Generate a file name
    var fileName = ReportTitle.replace(/ /g, "_");
    //this will remove the blank-spaces from the title and replace it with an underscore


    //Initialize file format you want csv or xls
    var uri = 'data:text/csv;charset=utf-8,' + escape(CSV);

    var link = document.createElement("a");
    link.href = uri;

    //set the visibility hidden so it will not effect on your web-layout
    link.style = "visibility:hidden";
    link.download = fileName + ".csv";

    //this part will append the anchor tag and remove it after automatic click
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}


document.addEventListener('DOMContentLoaded', function() {
    // Initialize flatpickr for date inputs
    document.getElementById('dateActiveTo').addEventListener('change', calculateDateDifference);

    function calculateDateDifference() {
        // Get date values from inputs
        const startDateStr = document.getElementById('dateActiveFrom').value;
        const endDateStr = document.getElementById('dateActiveTo').value;

        if (!startDateStr || !endDateStr) {
            // If either date is missing, do not calculate
            alert('Please select both dates');
            return;
        }

        // Parse the date strings
        const [startDay, startMonth, startYear] = startDateStr.split('-').map(Number);
        const [endDay, endMonth, endYear] = endDateStr.split('-').map(Number);

        // Create Date objects (month is 0-based, so subtract 1 from month)
        const startDate = new Date(startYear, startMonth - 1, startDay);
        const endDate = new Date(endYear, endMonth - 1, endDay);

        // Calculate the difference in milliseconds
        const diffTime = Math.abs(endDate - startDate);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

        // Display the result
        console.log(`Difference: ${diffDays} days`);
        if (diffDays > 1) {
            const row = document.getElementById('ipTrTable');

            if (row) { // Check if the element exists
                row.style.display = 'table-row'; // Show the row
                // Find and display all dates between start and end
                const dateList = getAllDatesBetween(startDate, endDate);
                displayDates(dateList,'');
            } else {
                console.error('Element with id "ipTrTable" not found.');
            }
        } else {
            const row = document.getElementById('ipTrTable');
            if (row) { // Check if the element exists
                row.style.display = 'table-row'; // Show the row
                // Find and display all dates between start and end
                const dateList = getAllDatesBetween(startDate, endDate);
                displayDates(dateList,'checked');
            } else {
                console.error('Element with id "ipTrTable" not found.');
            }
        }
    }

    function getAllDatesBetween(startDate, endDate) {
        const dates = [];
        let currentDate = new Date(startDate);

        while (currentDate <= endDate) {
            dates.push(new Date(currentDate)); // Add a copy of currentDate to the list
            currentDate.setDate(currentDate.getDate() + 1); // Increment the date
        }

        return dates;
    }

    function displayDates(dates,isChecked) {
        const repeatDaysList = document.getElementById('repeatDays');
        if (!repeatDaysList) return;
        // Clear existing dates
        repeatDaysList.innerHTML = '';
        // Find which days of the week are present in the date range
        const daysOfWeek = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'];
        const daysPresent = new Set(dates.map(date => daysOfWeek[date.getDay()]));
        // Display the days present
        daysOfWeek.forEach((day, index) => {
            if (daysPresent.has(day)) {
                const li = document.createElement('li');
                li.innerHTML = `
                    <label>
                        <input type="checkbox" name="repeatDays" id="repeatDays${index}" value="${day}" class="ip4ClassWeeks" ${isChecked}>
                        <span>${day}</span>
                    </label>
                `;
                repeatDaysList.appendChild(li);
            }
        });
    }
});