package com.crmws.worker.dto;

import com.crmws.worker.intf.WorkerIntf;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkerStore {
	
	private String slug;
	
	private WorkerIntf worker;

}
