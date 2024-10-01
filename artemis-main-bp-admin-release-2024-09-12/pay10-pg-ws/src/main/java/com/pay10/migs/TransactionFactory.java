package com.pay10.migs;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;


/**
 * @author Sunil
 *
 */

public class TransactionFactory {

	@Autowired
	@Qualifier("propertiesManager")
	private static PropertiesManager propertiesManager ;
	
	public static String getInstance(Fields fields) throws SystemException{
		String request="";
			
		// Fetching Details from Property file

		Map<String, String> treeMap = new TreeMap<String, String>(propertiesManager.getAllProperties(PropertiesManager.getAmexpropertiesfile()));
		
		for (String key : treeMap.keySet()) {
			String value = treeMap.get(key);
			if (value.startsWith(Constants.CONFIG_SEPARATOR)){		
				value = value.replace(Constants.CONFIG_SEPARATOR, "");				
			}else{
				value = fields.get(value);
			}
			
			if(key.equals(Constants.CARDEXP)){				
				value=MigsUtil.parseDate(value);
			}
			treeMap.put(key, value);
		}
		
		StringBuilder allFields = new StringBuilder();
	//	allFields.append(getSecureSecret());

		for (String key : treeMap.keySet()) {				
			String value = treeMap.get(key);
					
			allFields.append(key);
			allFields.append(Constants.EQUATOR);
			allFields.append(value);
		allFields.append(Constants.SEPARATOR);
		}

		String temp = allFields.toString();
	   int length = temp.length();
		String allFields1 = allFields.substring(0,length-1);
		
		String secureHash = MigsUtil.calculateMac( allFields1,fields.get(FieldType.TXN_KEY.getName()));
		
		request = allFields1 +"&" + Constants.SECUREHASH + "=" + secureHash +"&"+ Constants.SECURE_HASH_TYPE +"="+ Constants.HASHALGO;
		
		return request;
	}
}
