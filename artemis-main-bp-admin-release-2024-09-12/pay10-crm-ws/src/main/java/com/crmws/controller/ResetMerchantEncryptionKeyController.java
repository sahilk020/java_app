package com.crmws.controller;

import com.pay10.commons.service.intf.ResetMerchantKeyService;
import com.crmws.worker.ResetMerchantApiKeyScheduler;
import com.pay10.commons.dto.ResetMerchantKeyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resetEncryptionKey")
public class ResetMerchantEncryptionKeyController {
    @Autowired
    ResetMerchantKeyService resetMerchantKeyService;
    @Autowired
    ResetMerchantApiKeyScheduler resetMerchantApiKeyScheduler;

    @GetMapping("/getEncryptionKey")
    public ResponseEntity<List<String>> getEncryptionKey(@RequestParam String payId){
        return ResponseEntity.ok( resetMerchantKeyService.getEncryptionKey(payId));
    }

    @GetMapping("/getEncryptionList")
    public ResponseEntity<List<ResetMerchantKeyDTO>> getEncryptionKeyList(@RequestParam String payId){
        return ResponseEntity.ok(resetMerchantKeyService.getMerchantResetEncryptionKeyList(payId));
    }

    @PutMapping("/setResetEncryptionList")
    public String setNewEncryptionKey(@RequestParam String payId,
                                      @RequestParam String encyKey,
                                      @RequestParam String salt,
                                      @RequestParam String keySalt,
                                      @RequestParam String date,
                                      @RequestParam String time){


         resetMerchantKeyService.saveResetEncryptionkeyList(payId,encyKey,salt,keySalt,date,time);
        return "SUCCESS";
    }

    @GetMapping("/reset")
    public ResponseEntity<Void> resetResponseEntity(){
        resetMerchantApiKeyScheduler.resetMerchantAPI();
        return ResponseEntity.ok().build();
    }


}
