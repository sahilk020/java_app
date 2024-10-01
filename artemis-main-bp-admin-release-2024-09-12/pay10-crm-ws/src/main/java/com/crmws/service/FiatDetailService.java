package com.crmws.service;

import com.pay10.commons.dto.FiatDetailsDTO;

import java.util.List;

public interface FiatDetailService {

    public void save(FiatDetailsDTO fiatDetailsDTO);
    public  void delete(long id);
    public List<FiatDetailsDTO> getFiatDetails(String payId,String currency);

  //  public String checkMappingStatusForBank(String payId);
}

