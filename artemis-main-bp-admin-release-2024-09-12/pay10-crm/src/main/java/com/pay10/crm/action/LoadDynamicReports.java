package com.pay10.crm.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.ActionSupport;
import com.pay10.commons.util.PropertiesManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  This component is used to load the screens of dynamic reports with
 *  details of its filter components.
 *
 * @author Gajera Jaykumar
 */
public class LoadDynamicReports extends ActionSupport {

    private static final Logger logger = LoggerFactory.getLogger(LoadDynamicReports.class.getName());
    private static final ObjectMapper mapper = new ObjectMapper();
    private long screenId;

    private transient List<?> componentDetails;

    private String controlsAsArray;

    @Override
    public String execute() throws Exception {
        logger.info("execute:: invoked the methods screenId={}", getScreenId());
        invokeComponentsApi();
        return super.execute();
    }

    private void invokeComponentsApi() {
        try {
            String urlStr = PropertiesManager.propertiesMap.get("DRU_FETCH_CONTROLS_API_URL");
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(urlStr);
            Map<String, Long> map = new HashMap<>();
            map.put("screenId", getScreenId());
            StringEntity params = new StringEntity(new JSONObject(map).toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse resp = httpClient.execute(request);
            String responseBody = EntityUtils.toString(resp.getEntity());
            JSONObject resJson = mapper.readValue(responseBody, JSONObject.class);
            setComponentDetails(mapper.convertValue(resJson.get("data"), ArrayList.class));
            setControlsAsArray(resJson.toJSONString());
        } catch (Exception ex) {
            logger.error("invokeComponentsApi:: failed", ex);
        }
    }

    public long getScreenId() {
        return screenId;
    }

    public void setScreenId(long screenId) {
        this.screenId = screenId;
    }

    public List<?> getComponentDetails() {
        return componentDetails;
    }

    public void setComponentDetails(List<?> componentDetails) {
        this.componentDetails = componentDetails;
    }

    public String getControlsAsArray() {
        return controlsAsArray;
    }

    public void setControlsAsArray(String controlsAsArray) {
        this.controlsAsArray = controlsAsArray;
    }
}
