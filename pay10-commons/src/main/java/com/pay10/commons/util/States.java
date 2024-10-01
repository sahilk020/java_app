package com.pay10.commons.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rajendra
 *
 */

public enum States {
	SELECT_STATE		    (-1,"Select State", "-1","SS",false),
	JAMMU_AND_KASHMIR		(1,"JAMMU AND KASHMIR", "01","JK",false),
	HIMACHAL_PRADESH		(2,"HIMACHAL PRADESH", "02","HP",false),
	PUNJAB			        (3,"PUNJAB", "03","PB",false),
	CHANDIGARH			    (4,"CHANDIGARH","04","CH",false),
	UTTARAKHAND			    (5,"UTTARAKHAND", "05","UT",false),
	HARYANA					(6,"HARYANA", "06","HR",false),
	DELHI					(7,"DELHI", "07","DL",false),
	RAJASTHAN				(8,"RAJASTHAN", "08","RJ",false),
	UTTAR_PRADESH			(9,"UTTAR PRADESH", "09","UP",false),
	BIHAR					(10,"BIHAR", "10","BH",false),
	SIKKIM					(11,"SIKKIM", "11","SK",false),
	ARUNACHAL_PRADESH	    (12,"ARUNACHAL PRADESH","12","AR",false),
	NAGALAND			    (13,"NAGALAND", "13","NL",false),
	MANIPUR					(14,"MANIPUR", "14","MN",false),
	MIZORAM					(15,"MIZORAM", "15","MI",false),
	TRIPURA				    (16,"TRIPURA", "16","TR",false),
	MEGHLAYA			    (17,"MEGHLAYA", "17","ME",false),
	ASSAM					(18,"ASSAM", "18","AS",false),
	WEST_BENGAL				(19,"WEST BENGAL", "19","WB",false),
	JHARKHAND				(20,"JHARKHAND", "20","JH",false),
	ODISHA					(21,"ODISHA", "21","OR",false),
	CHATTISGARH			    (22,"CHATTISGARH","22","CT",false),
	MADHYA_PRADESH			(23,"MADHYA PRADESH", "23","MP",false),
	GUJARAT			        (24,"GUJARAT", "24","GJ",false),
	DAMAN_AND_DIU			(25,"DAMAN AND DIU","25","DD",false),
	DADRA_AND_NAGAR_HAVELI	(26,"DADRA AND NAGAR HAVELI","26","DN",false),
	MAHARASHTRA				(27,"MAHARASHTRA", "27","MH",false),
	//ANDHRA_PRADESH_BD		(28,"ANDHRA PRADESH(BEFORE DIVISION)","28","AP",false),
	KARNATAKA				(29,"KARNATAKA", "29","KA",false),
	GOA					    (30,"GOA", "30","GA",false),
	LAKSHWADEEP	            (31,"LAKSHWADEEP", "31","LD",false),
	KERALA			        (32,"KERALA", "32","KL",false),
	TAMIL_NADU				(33,"TAMIL NADU", "33","TN",false),
	PUDUCHERRY				(34,"PUDUCHERRY", "34","PY",false),
	ANDAMAN_AND_NICOBAR_ISLANDS (35,"ANDAMAN AND NICOBAR ISLANDS","AN", "35",false),
	TELANGANA			    (36,"TELANGANA", "36","TS",false),
	ANDHRA_PRADESH			(37,"ANDHRA PRADESH", "37","AD",false);
	
	private final String name;
	private final String gstCode;
	private final String stateCode;
	private final int id;
	private final boolean isInternal;
	private States(int id,String name, String gstCode,String stateCode,boolean isInternal){
		this.id = id;
		this.name = name;
		this.gstCode = gstCode;
		this.stateCode = stateCode;
		this.isInternal = isInternal;
		
	}
	
	public String getName() {
		return name;
	}
	
	public String getGstCode(){
		return gstCode;
	}
	public int getId() {
		return id;
	}

	public boolean isInternal() {
		return isInternal;
	}
	
	public static States getInstance(String name){
		States[] statesTypes = States.values();
		for(States statesType : statesTypes){
			if(statesType.getName().equals(name)){
				return statesType;
			}
		}
		
		return null;
	}
	
	public static String getStateCodeByName(String name){
		States[] statesTypes = States.values();
		for(States statesType : statesTypes){
			if(statesType.getName().equalsIgnoreCase(name)){
				return statesType.getGstCode();
			}
		}		
		return "-1";
	}
	
	public static States getInstanceFromCode(String code){
		States[] statesTypes = States.values();
		for(States statesType : statesTypes){
			if(statesType.getGstCode().equals(code)){
				return statesType;
			}
		}
		
		return null;
	}
	
	public static List<States> getStates(){
		List<States> statesTypes = new ArrayList<States>();						
		for(States statesType:States.values()){
			if(!statesType.isInternal())
				statesTypes.add(statesType);
		}
	  return statesTypes;
	}
	
	public static List<String> getStatesNames(){
		List<String> statesTypes = new ArrayList<String>();						
		for(States statesType:States.values()){
			if(!statesType.isInternal())
				statesTypes.add(statesType.getName());
		}
	  return statesTypes;
	}
	
}
