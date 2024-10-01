package com.pay10.migs;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;


@Service("hashComparator")
public class HashComparator {

	public void compareHash(Fields fields) throws SystemException{
		
		String secureHash =fields.get(Constants.SECUREHASH);
		String generatedHash = generateHash(fields);
		
		if(!generatedHash.equals(secureHash)){
			throw new SystemException(ErrorType.SIGNATURE_MISMATCH,ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
		}
	}
	
	public String generateHash(Fields fields) throws SystemException{
		StringBuilder inputHashString = new StringBuilder();
		Map<String,String> treeMap = new TreeMap<String,String>();
		
		for(String key:fields.keySet()){			
			if(key.startsWith("vpc_")){
				treeMap.put(key, fields.get(key));
			}
		}
		treeMap.remove(Constants.SECURE_HASH_TYPE);
		treeMap.remove(Constants.SECUREHASH);
		for(String key:treeMap.keySet()){						
			inputHashString.append(key);
			inputHashString.append(Constants.EQUATOR);
			inputHashString.append(treeMap.get(key));
			inputHashString.append(Constants.SEPARATOR);			
		}
		inputHashString.deleteCharAt(inputHashString.length()-1);
		return MigsUtil.calculateMac(inputHashString.toString(),fields.get(FieldType.TXN_KEY.getName()));
	}
}
