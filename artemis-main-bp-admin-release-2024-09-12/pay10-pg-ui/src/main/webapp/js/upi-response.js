var timeCount = 0,
    sleepResponse = function (_val, token, requestType) {
        setTimeout(function() {
            timeCount += 10000;

            if(timeCount > 480000) {
                self.postMessage("cancel");
            } else {
                verifyUpiResponseReceived(_val, token, requestType)
            }
        }, 10000);
    };

function verifyUpiResponseReceived(_val, token, requestType) {
    var _formData = new FormData();
    _formData.append("token", token);
    if(requestType == "UPI") {
        _formData.append("pgRefNum", _val);
    } else {
        _formData.append("oid", _val);
    }

    xhrUpi = new XMLHttpRequest();
    xhrUpi.open("POST", "/pgui/jsp/verifyUpiResponse", !0);

    xhrUpi.onload = function () {
        null == this && sleepResponse(_val, token, requestType);
        var obj = JSON.parse(this.response);
        if (null != obj) {
            var _transactionStatus,
                _map = new Map();
                
                _transactionStatus = obj.transactionStatus;
                _map = obj.responseFields;
                
                if(_transactionStatus == "Sent to Bank" || _transactionStatus == "Pending") {
                    sleepResponse(_val, token, requestType);
                } else {
                    self.postMessage(_map);
                }
        } else {
            sleepResponse(_val, token, requestType);
        }
    };

    xhrUpi.send(_formData);
}

self.onmessage = function (obj) {
    if (void 0 !== obj.data) {
        var response = obj.data;

        verifyUpiResponseReceived(response._value, response.token, response.requestType);
    }
};
