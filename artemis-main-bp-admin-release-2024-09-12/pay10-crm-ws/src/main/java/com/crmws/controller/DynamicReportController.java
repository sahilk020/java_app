package com.crmws.controller;

import com.crmws.dto.ApiResponse;
import com.crmws.service.DynamicReportService;
import com.google.gson.Gson;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dReport")
public class DynamicReportController {

    private static final Logger logger = LoggerFactory.getLogger(DynamicReportController.class.getName());
    @Autowired
    private DynamicReportService dynamicReportService;

    @PostMapping("/fetchReport")
    public ResponseEntity<ApiResponse> fetchReport(@RequestBody Map<String,String> reportParams)
    {
        // reportId Key always mandatory
        logger.info("/dReport/fetchReport API called. Params: {}",reportParams);
        ApiResponse response = new ApiResponse();
        List<Map> reportData=null;
        try {
          reportData =  dynamicReportService.getReportData(reportParams);
          
          //reportData=reportData.subList(1, 5);
          if(reportData.size() > 0){
        	  
          response.setStatus(true);
          response.setData(reportData);
          response.setMessage("Report fetched");
          logger.info("/dReport/fetchReport API report data size: {}",reportData.size());
          }else{
              response.setStatus(false);
              response.setData(reportData);
              response.setMessage("No data found");
          }

        }catch (Exception e){
            response.setStatus(false);
            response.setMessage("Error while fetching report. "+e.getMessage());
            e.printStackTrace();
        }
        logger.info("Data report :\n"+reportData);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/fetchScreen")
    public ResponseEntity<ApiResponse> fetchScreen(@RequestBody Map<String,String> reportParams)
    {
        // reportId Key always mandatory
        logger.info("/dReport/fetchScreen API called. Params: {}",reportParams);
        ApiResponse response = new ApiResponse();
        try {
            List<Map<String,Object>> reportData =  dynamicReportService.getScreen(reportParams);
            for(Map<String,Object> a : reportData){
               // System.out.println(a);
                if("select".equalsIgnoreCase(a.get("control_type").toString())){
                   List<Map<String,Object>> listValues =  dynamicReportService.getKeyValues(a.get("control_default_value").toString());
                   a.put("list_of_values",listValues);
                }
                if("mselect".equalsIgnoreCase(a.get("control_type").toString())){
                    List<Map<String,Object>> listValues =  dynamicReportService.getKeyValues(a.get("control_default_value").toString());
                    a.put("list_of_values",listValues);
                 }
               
            }

            response.setStatus(true);
            response.setData(reportData);
            response.setMessage("Report screen fetched");
            logger.info("/dReport/fetchReport API report data size: {}",reportData.size());
        }catch (Exception e){
            response.setStatus(false);
            response.setMessage("Error while fetching report. "+e.getMessage());
            e.printStackTrace();
        }
        
      // logger.info("/dReport/fetchReport API respone: {}=====",response);
        return ResponseEntity.ok(response);
    }
    /*
    1- fetch form Mysql
    2- convert Mysql Data as Mongo params
    3- Fetch from Mongo
     */
}
