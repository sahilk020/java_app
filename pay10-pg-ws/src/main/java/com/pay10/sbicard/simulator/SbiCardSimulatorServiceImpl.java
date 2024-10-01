package com.pay10.sbicard.simulator;

import bsh.This;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pay10.commons.util.PropertiesManager;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class SbiCardSimulatorServiceImpl implements SbiCardSimulatorService {

    private static final Logger logger = LoggerFactory.getLogger(This.class.getName());
    private static final ObjectMapper mapper = new ObjectMapper();

    private HttpServletResponse httpServletResponse;

    public SbiCardSimulatorServiceImpl(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }

    @Override
    public String pvReq(String acqId, String txnId, Map<String, Object> req) {
        return preparePvResponseJson(String.valueOf(req.get("var1")), acqId);
    }

    private String prepareHtml(Map<String, Object> req, String acqId) {
        String returnUrl = PropertiesManager.propertiesMap.get("SBI_CARD_PVRQ_RETURN_URL");
        StringBuilder httpRequest = new StringBuilder();
        httpRequest.append("<HTML>");
        httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
        httpRequest.append("<form name=\"form1\" action=\"");
        httpRequest.append(returnUrl);
        httpRequest.append("\" method=\"post\">");
        httpRequest.append("<input type=\"hidden\" name=\"");
        httpRequest.append("data");
        httpRequest.append("\" value=\"");
        httpRequest.append(preparePvResponseJson(String.valueOf(req.get("var1")), acqId));
        httpRequest.append("\">");
        httpRequest.append("</form>");
        httpRequest.append("<script language=\"JavaScript\">");
        httpRequest.append("function OnLoadEvent()");
        httpRequest.append("{document.form1.submit();}");
        httpRequest.append("</script>");
        httpRequest.append("</BODY>");
        httpRequest.append("</HTML>");
        return httpRequest.toString();
    }

    private String preparePvResponseJson(String merchantTxnId, String acqId) {
        JSONObject response = new JSONObject();
        response.put("messageType", "pVrs");
        response.put("deviceChannel", "02");
        response.put("p_messageVersion", "2.1.0");
        response.put("acsStartVersion", "2.1.0");
        response.put("acsEndVersion", "2.1.0");
        response.put("dsStartVersion", "2.1.0");
        response.put("dsEndVersion", "2.1.0");

        JSONArray array = new JSONArray();
        array.put("01");
        array.put("02");
        array.put("03");
        array.put("04");

        response.put("acsInfoInd", array);
        response.put("scheme", "Visa");
        response.put("directoryServerID", "scheme directory server id");
        response.put("merchantTransID", merchantTxnId);
        response.put("threeDSServerTransID", "5e56dd9b-9eae-4977-a2ea-230a1bb205ca");
        String name = "GJTest123456";
        String verPaRqUrl = StringUtils.join("http://localhost:8080/pgws/sbi/card/api/paReq/",
                name, "/", acqId);
        response.put("threeDSServerPaRqURL", verPaRqUrl);
        response.put("threeDSACSMethodURL", "https://3ds2-ui-acsdev.com/v1/acs/services/threeDSMethod/9111?cardType=V");
        response.put("threeDSJSBrowserDetailFetchURL", "https://3ds2-api-3dsserverdev.com/static/client.min.js");

        JSONObject threeDsMethodData = new JSONObject();
        threeDsMethodData.put("threeDSMethodData",
                "eyJ0aHJlZURTU2VydmVyVHJhbnNJRCI6IjNhYzdjYWE3LWFhNDItMjY2My03OTFiLTJhYzA1YTU0MmM0YSIsInRocmVlRFNNZXRob2ROb3RpZmljYXRpb25VUkwiOiJ0aHJlZURTTWV0aG 9kTm90aWZpY2F0aW9uVVJMIn0");
        response.put("threeDSMethodDataFormPost", threeDsMethodData);
        response.put("errorCode", "000");
        response.put("errorDesc", "");
        return response.toString();
    }

    @Override
    public Map<String, Object> paReq(String name, String acqId, Map<String, Object> req) {

        return preparePaResponse(name, acqId, String.valueOf(req.get("var1")));
    }

    private Map<String, Object> preparePaResponse(String name, String acqId, String merchantTxnId) {
        JSONObject response = new JSONObject();
        response.put("messageVersion", "2.1.0");
        response.put("p_messageVersion", "2.1.0");
        response.put("messageType", "pVrs");
        response.put("threeDSServerTransID", "5e56dd9b-9eae-4977-a2ea-230a1bb205ca");
        response.put("acsTransID", "93f0d055-2610-48f2-8eee-324af4ec2da5");
        response.put("dsTransID", "828ba429-cf6b-405c-bda3-4d7d5e0b9367");
        response.put("merchantTransID", merchantTxnId);
        response.put("eci", "05");
        response.put("authenticationValue", "AABBCCDDEEFFAABBCCDDEEFFAAA=");
        response.put("transStatus", "Y");
        response.put("acsChallengeMandated", "Y");
        response.put("acsDecConInd", "N");
        response.put("whiteListStatus", "Y");
        response.put("whiteListStatusSource", "03");
        response.put("messageExtension", prepareMsgExtension());
        String authUrl = StringUtils.join("http://localhost:8080/pgws/sbi/card/api/paFinalReq/",
                name, "/", acqId, "/", merchantTxnId);
        response.put("authenticationUrl", authUrl);
        response.put("merchantCancelChallengeAuthUrl", authUrl);
        response.put("acsChallengeReqUrl", authUrl);

        JSONObject acsChallengeReqData = new JSONObject();
        acsChallengeReqData.put("creq", "");

        response.put("acsChallengeReqFormData", acsChallengeReqData);
        response.put("sdkTransID", "5f27d9d4-95c1-4b74-ab41-906859312847");
        response.put("sdkAppID", "34c8fefe-f694-4569-9869-c4029ce60971");
        response.put("acsSignedContent",
                "eyJ4NWMiOlsiTUlJRmpqQ0NBM2FnQXdJQkFnSUdBV0dacEZEN01BMEdDU3FHU0liM0RRRUJDd1VBTUh3eEN6QUpCZ05WQkFZVEFrNU1NU2t3SndZRFZRUUtEQ0JWVENCVWNtRnVjMkZqZEdsdmJpQlRaV04xY21sMGVTQmthWFpwYzJsdmJqRWdNQjRHQTFVRUN3d1hWVXdnVkZNZ00wUXRVMl" +
                        "ZqZFhKbElGSlBUMVFnUTBFeElEQWVCZ05WQkFNTUYxVk1JRlJUSURORUxWTmxZM1Z5WlNCU1QwOVVJRU5CTUI0WERURTRNREl4TlRFME1qVXpPRm9YRFRFNU1ESXhOVEUwTWpVek9Gb3dnWUF4Q3pBSkJnTlZCQVlUQWs1TU1Ta3dKd1lEVlFYK-8l6cHRnTjNmiFyODDOzi4EslBikT0-jOIN8mpZ9h7YjNgBHIj7zHqdGLqZ4XL4c3iSbQBk6YTcW6GwAQ_ZYCWRmyTqsZxtskpSynjWBHfTlLINkQc-JA-FVL3U86NfIRGBft80L5Nk5nJQHIfEJktbmXdmURUcrPC7UyI1-" +
                        "NT_-vVmMoTpyKcQy3arLgXk2XzjwWMTGgFvoASb-gO6L-q4i-JvQFyEf50hYU9DdTRgsrMwnB1RilSuOWH85M8t2svb5V4hpWnaLejSvGSzWB6ik7iKQmTxbJsJm0mTmbfSPs5m IWeNhevdpGDp8FPyQAaYAceCIwxbCrBtEZQ");

        JSONObject acsRenderingType = new JSONObject();
        acsRenderingType.put("acsInterface", "01");
        acsRenderingType.put("acsUiTemplate", "02");

        response.put("acsRenderingType", acsRenderingType);
        response.put("errorCode", "000");
        response.put("errorDesc", "");
        try {
            return mapper.readValue(response.toString(), HashMap.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JSONArray prepareMsgExtension() {
        JSONArray extensionArray = new JSONArray();
        JSONObject extensionObj = new JSONObject();
        extensionObj.put("name", "name");
        extensionObj.put("id", "id");
        extensionObj.put("criticalityIndicator", false);

        JSONObject data = new JSONObject();
        data.put("valueOne", "value1");
        data.put("valueTwo", "value2");
        extensionObj.put("data", data);
        extensionArray.put(extensionObj);
        return extensionArray;
    }

    @Override
    public Map<String, Object> finalAuthReq(String name, String acqId, String txnId, Map<String, Object> req) {
        return prepareFinalRes(txnId);
    }

    private Map<String, Object> prepareFinalRes(String merchantTxnId) {

        JSONObject response = new JSONObject();
        response.put("messageVersion", "2.1.0");
        response.put("p_messageVersion", "2.1.0");
        response.put("messageType", "pRsFr");
        response.put("threeDSServerTransID", "5e56dd9b-9eae-4977-a2ea-230a1bb205ca");
        response.put("acsTransID", "93f0d055-2610-48f2-8eee-324af4ec2da5");
        response.put("dsTransID", "828ba429-cf6b-405c-bda3-4d7d5e0b9367");
        response.put("sdkTransID", "5f27d9d4-95c1-4b74-ab41-906859312847");
        response.put("merchantTransID", merchantTxnId);
        response.put("authenticationValue", "AABBCCDDEEFFAABBCCDDEEFFAAA=");
        response.put("transStatus", "Y");
        response.put("errorCode", "000");
        response.put("errorDesc", "");
        try {
            return mapper.readValue(response.toString(), HashMap.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
