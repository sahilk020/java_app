package com.crmws.service;

import org.bson.Document;

import java.util.List;
import java.util.Map;

public interface DynamicReportService {
    public List<Map> getReportData(Map<String,String> inputMap) throws Exception;
    public List<Map<String,Object>> getScreen(Map<String,String> inputMap) throws Exception;
    public List<Map<String,Object>> getKeyValues(String SQL);
}
