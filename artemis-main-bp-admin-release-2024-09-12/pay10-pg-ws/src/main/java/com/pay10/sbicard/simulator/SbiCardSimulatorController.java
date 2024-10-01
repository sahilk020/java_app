package com.pay10.sbicard.simulator;

import com.pay10.commons.util.PropertiesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/sbi/card/api/")
public class SbiCardSimulatorController {

    @Autowired
    private SbiCardSimulatorService simulatorService;

    @PostMapping("pvReq/{acqId}/{txnId}")
    public RedirectView pvrReq(@PathVariable String acqId, @PathVariable String txnId,
                               @RequestBody Map<String, Object> request) {
        RedirectView view = new RedirectView(PropertiesManager.propertiesMap.get("SBI_CARD_PVRQ_RETURN_URL"));
        Map<String, Object> map = new HashMap<>();
        map.put("data", simulatorService.pvReq(acqId, txnId, request));
        view.setAttributesMap(map);
        return view;
    }

    @PostMapping("paReq/{name}/{acqId}")
    public Map<String, Object> paReq(@PathVariable String name, @PathVariable String acqId,
                                     @RequestBody Map<String, Object> request) {
        return simulatorService.paReq(name, acqId, request);
    }

    @PostMapping("paFinalReq/{name}/{acqId}/{txnId}")
    public Map<String, Object> paFinalReq(@PathVariable String name, @PathVariable String acqId,
                                          @PathVariable String txnId,
                                          @RequestBody Map<String, Object> request) {
        return simulatorService.finalAuthReq(name, acqId, txnId, request);
    }
}
