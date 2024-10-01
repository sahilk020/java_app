package com.crmws.controller;

import com.crmws.service.CryptoDetailsService;
import com.pay10.commons.dto.CryptoDetailsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cryptoDetails")
public class CryptoDetailsController {

    Logger logger= LoggerFactory.getLogger(CryptoDetailsController.class.getName());
    @Autowired
    CryptoDetailsService cryptoDetailsService;

    @GetMapping("/getCryptoList")
    public ResponseEntity<List<CryptoDetailsDTO>> getCyptoDetailsList(@RequestParam String payId, @RequestParam String currency){
        return ResponseEntity.ok(cryptoDetailsService.cryptoDetailsList(payId,currency));
    }

    @PostMapping("/saveCryptoDetails")
         public void saveCryptoDetails(@RequestBody CryptoDetailsDTO cryptoDetailsDTO){
         cryptoDetailsService.save(cryptoDetailsDTO);
    }
    @PostMapping("/deleteCryptoDetail")
    public String deleteCryptoDetails(@RequestParam Long id, @RequestParam String currency){
       //  logger.info("CaseId To Delete:"+caseId);
        return cryptoDetailsService.delete(id,currency);
    }

    @GetMapping("/getCurrencyList")
    public Map<String,String> getCurrencyList(@RequestParam String payId){
        return cryptoDetailsService.getCurrencyList(payId);
    }

//    @GetMapping("/getMappingStatus")
//    public String getMappingStatus(@RequestParam String payId){
//        return  cryptoDetailsService.checkCurrencyMappingStatus(payId);
//    }

}
