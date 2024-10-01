package com.crmws.controller;

import com.crmws.service.impl.MopConfiguarytionServiceImpl;
import com.pay10.commons.user.QuomoCurrencyConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MopConfigurationController {


    @Autowired
    private MopConfiguarytionServiceImpl mopConfiguarytionService;

    @GetMapping("/getAllMop")
    public List<QuomoCurrencyConfiguration> findAllMopConfig(@RequestParam String acquirer, @RequestParam String currency, @RequestParam String paymentType){

        List<QuomoCurrencyConfiguration> getMop = mopConfiguarytionService.findMopConfiguration(acquirer,currency,paymentType);

        return getMop;
    }

    @PutMapping("/editSingleMop")
    public void editSingleMop(@RequestParam String id, @RequestParam String bankMopType, @RequestParam String bankName){

        mopConfiguarytionService.editMopConfiguration(id,bankMopType,bankName);
    }

    @PostMapping("/saveSingleMop")
    public void saveSingleMop(@RequestParam String acquirer,@RequestParam String currency, @RequestParam String currencyCode, @RequestParam String paymentType, @RequestParam String mopType, @RequestParam String bankMopType, @RequestParam String bankName, @RequestParam String userName){
        mopConfiguarytionService.saveMopConfiguration(acquirer,currency,currencyCode,bankMopType,bankName,paymentType,mopType,userName);
    }
}
