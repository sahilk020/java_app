package com.pay10.errormapping;



public interface ErrorMappingService {

    public ErrorMappingDTO getErrorMappingByPGCode(String pgCode, String acquier);
    public ErrorMappingDTO getErrorMappingByAcqCode(String acuierStatusCode,String acquier);
}
