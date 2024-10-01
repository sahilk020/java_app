package com.pay10.commons.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Helper {

	public static Collection<String> parseFields(String commaSepratedList){
		String[] fieldNames = commaSepratedList.split(",");

		List<String> list = new ArrayList<String>();
		for (String name : fieldNames) {
			name = name.trim();
			if (!name.isEmpty()) {
				list.add(name);
			}
		}

		return list;
	}


//	public static void removeFieldsByPropertyFile(Fields fields, String applicationFileKey) {
////		String commaSeparatedKeys = propertiesManager.getSystemProperty(applicationFileKey);
//		String commaSeparatedKeys = "RESPONSE_CODE,RESPONSE_MESSAGE,STATUS,PAY_ID";
//		Collection<String> keys = (Helper.parseFields(commaSeparatedKeys));
//		Fields orginalFields = new Fields(fields);
//		for(String key : keys){
//			orginalFields.remove(key);
//		}
//		for(String key : orginalFields.keySet()){
//			fields.remove(key);
//		}
//	}
//
//	public static void main(String[] args) {
//		Fields fields = new Fields();
//		fields.put("RESPONSE_CODE","123");
//		fields.put("RESPONSE_MESSAGE","xyz");
//		fields.put("UDF1","1dfsf");
//		fields.put("UDF2","fdgsdfdf");
//		fields.put("TEST","sdasds");
//		fields.put("STATUS","CAPTURED");
//		fields.put("Sdsad","sdasd");
//		fields.put("PAY_ID","12342414343");
//		removeFieldsByPropertyFile(fields, "TEST");
//		System.out.println(fields.getFields());
//	}
}
