package com.pay10.pg.core.util;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;

@Service
public class MerchantHostedUtils {

	@Autowired
	private TransactionControllerServiceProvider txnControllerSP;
	
	public String encryptMerchantResponse(Fields fields) throws SystemException {
		StringBuilder allFields = new StringBuilder();
		for(Entry<String,String> entry: fields.getFields().entrySet()) {
			allFields.append(ConfigurationConstants.FIELD_SEPARATOR.getValue());
			allFields.append(entry.getKey());
			allFields.append(ConfigurationConstants.FIELD_EQUATOR.getValue());
			allFields.append(entry.getValue());
		}
		allFields.deleteCharAt(0); 
		String encryptedString = txnControllerSP.encrypt(fields.get(FieldType.PAY_ID.getName()), allFields.toString())
															.get(FieldType.ENCDATA.getName());
		return encryptedString;
	}
	
	public Map<String, String> decryptMerchantResponse(String encData, String payId) throws SystemException {
		System.out.println("-------- Request Received --------- ");
		return txnControllerSP.decrypt(payId, encData);
	}
	
	public static void main(String[] args) throws SystemException {
		String encData = "T2dg5FD0jB8eYhIz92YUAFurt2lXEyyuOCMYYpeNMgdpymEi4xVWXjhhSLFjHhgAjQ2/oruU2yaoqaaHCHZ8ob8rmOXh6hMc0sxiItqJm6JqjsdUkDAyTNRCkNUI8E1Q0uYpu4VaiOhcbaqjX3yQbkoAlSgzE1hv6hdn+1M+KUUiCp3ePCZUh0rVpqyfPj9sXa8PN1ddTeec/O0EBcfHcPBUKpT7PaC9f79Zwae4hUG9ZnyyUm40mTl1tvs++A3PXSPZRLFR5NHRURCTBdFv2+E2sKiXnA4EGbeT+s6VJgIYKzI4mVGtrkMW6TJG6brGJsI58GnIKh6Lbze10mHNtTL08g2Zh45C98V+uP4ytsiqM5a/4y61mjiY69/AwA9PFqlbgka/Lg6PGXTTtGMXBBUPPEmUGlQn+rC9ybOTbXtRbyrWN1JMpgvQ1mRN5ciZwPOetfUy+zkhPa6hXgvrt1IBEC2rOtAo7AeHELfJjpT1yZzqoAXDH/y3FgRlSYKYvrXetFbIO14Jg07LNRakaoMUjjoxF5I+AgJBKXgAGkhU+7nB/0GgIzohB3xJk1j9qqcBo7s4pxARRu+PMcTjlMxkDCgLotHL+9dNo/RGgPNkK96w2Xi/v8Bk3iqeaEUnCNBwATXZDPBXRd7/TCdgsE+I2X2tbPSJFfYrBbm3hdmYBLGXUdbQ+YgtPB4aOP0AcNrpysSh077EWTh5lbRj1R1+wMuee5rrS3SFOG0hqm0vhF4Te74Pe54V6TJ8Iw9PnjtC/+wVJYIdbJA3F05qQI7ByhTyj8W2FGV2YXq04pQJ9icXkikbVxIqmbPSbHWReJ46GAe+RjJ/JEzv1NUrnSh6PDdsnpBNj/+05qjdG2neMdFBJEf6z6KPtLSY7yICdibj7RdbBbw3mx1nHcXkRgTrkwraXznTFaWBH6O+z4U";
		String payId = "1002120114121053";
		System.out.println("--------$$ ------"+new MerchantHostedUtils().decryptMerchantResponse(payId, encData));
	}
	
}
