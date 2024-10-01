package com.crmws.controller;


import com.crmws.service.FiatDetailService;
import com.pay10.commons.dto.FiatDetailsDTO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fiatDetails")
public class FiatDetailsController {

    @Autowired
    FiatDetailService fiatDetailService;
    Logger logger= LoggerFactory.getLogger(FiatDetailsController.class.getName());

    @GetMapping("/getFiatDetails")
    public List<FiatDetailsDTO> getFiatList(@RequestParam String payId,@RequestParam String currency){
        return fiatDetailService.getFiatDetails(payId,currency);
    }

    @PostMapping("/saveFiatDetails")
    public String saveFiatDetails(@RequestBody FiatDetailsDTO fiatDetailsDTO){
        fiatDetailService.save(fiatDetailsDTO);
        return "SUCCESS";
    }

    @PostMapping("/deleteFiatDetails")
    public String deleteFiatEntry(@RequestParam long id){
        fiatDetailService.delete(id);
        return "SUCCESS";
    }

  /*  @GetMapping("/checkMappingStatus")
    public String checkStatus(@RequestParam String  payId){
     return fiatDetailService.checkMappingStatusForBank(payId);
    }
*/
}
