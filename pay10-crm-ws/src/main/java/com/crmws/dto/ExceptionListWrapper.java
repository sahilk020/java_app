package com.crmws.dto;

import java.util.List;

public class ExceptionListWrapper {
	List<String> pgrefno;

	public List<String> getPgrefno() {
		return pgrefno;
	}

	public void setPgrefno(List<String> pgrefno) {
		this.pgrefno = pgrefno;
	}
	
	public void add(List<String> list) {
		pgrefno.addAll(list);
	}

	


	

}
