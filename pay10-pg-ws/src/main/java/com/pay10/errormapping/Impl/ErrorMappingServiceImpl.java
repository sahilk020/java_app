package com.pay10.errormapping.Impl;

import com.pay10.errormapping.ErrorMappingDAO;
import com.pay10.errormapping.ErrorMappingDTO;
import com.pay10.errormapping.ErrorMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
//@Component("errorMappingDAO")
public class ErrorMappingServiceImpl implements ErrorMappingService
{

    @Autowired
    private ErrorMappingDAO errorMappingDAO;


    @Override
    public ErrorMappingDTO getErrorMappingByPGCode(String pgCode, String acquier) {
        return errorMappingDAO.getErrorMappingByPGCode(pgCode,acquier);
    }

    @Override
    public ErrorMappingDTO getErrorMappingByAcqCode(String acuierStatusCode, String acquier) {
        return errorMappingDAO.getErrorMappingByAcqCode(acuierStatusCode,acquier);
    }
}
