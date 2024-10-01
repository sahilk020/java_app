package com.pay10.errormapping;

public interface ErrorMappingDAO {

    public ErrorMappingDTO getErrorMappingByPGCode(String pgCode, String acquier);
    public ErrorMappingDTO getErrorMappingByAcqCode(String acuierStatusCode, String acquier);
}
