package com.crmws.service;

import com.pay10.commons.dto.CryptoDetailsDTO;
import com.pay10.commons.entity.CryptoDetails;

import java.util.List;
import java.util.Map;

public interface CryptoDetailsService {

    public String save(CryptoDetailsDTO cryptoDetailsDTO);
    public List<CryptoDetailsDTO> cryptoDetailsList(String payId,String currency);
    public String delete(long id,String currency);
    public Map<String, String> getCurrencyList(String  payId);

  //  public String checkCurrencyMappingStatus(String payId);
}
