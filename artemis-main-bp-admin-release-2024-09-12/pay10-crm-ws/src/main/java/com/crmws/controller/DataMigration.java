package com.crmws.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crmws.service.impl.DataMigrationServiceImpl;

@CrossOrigin
@RestController
@RequestMapping("DataMigration")
public class DataMigration{
	@Autowired
	private DataMigrationServiceImpl dataMigrationServiceImpl;
	
	@GetMapping("/MakeQuery")
	public void makeQuery() {
		dataMigrationServiceImpl.makeQuery();
	}
}