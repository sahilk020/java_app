package com.pay10.bindb.service;

import java.io.IOException;
import java.util.Map;
// Added by RR Date 26-Nov-2021
public interface BinRangeService {

    public BinRangeDTO getCommunicator(String requestUrl, String cardBin) throws IOException;
}
