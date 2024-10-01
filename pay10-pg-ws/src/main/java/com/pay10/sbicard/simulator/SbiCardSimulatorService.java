package com.pay10.sbicard.simulator;

import java.util.Map;

public interface SbiCardSimulatorService {
    public String pvReq(String acqId, String txnId, Map<String, Object> req);

    public Map<String, Object> paReq(String name, String acqId, Map<String, Object> req);

    public Map<String, Object> finalAuthReq(String name, String acqId, String txnId, Map<String, Object> req);
}
