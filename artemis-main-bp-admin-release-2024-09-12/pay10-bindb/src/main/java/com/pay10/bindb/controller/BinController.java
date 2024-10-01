package com.pay10.bindb.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pay10.bindb.core.BinRangeProvider;
@RestController
public class BinController {
	
	@Autowired
	private BinRangeProvider binRangeProvider;
	
	@RequestMapping(method = RequestMethod.GET,value = "/findbin/{binRange}")
	public Map<String,String> findBin(@PathVariable String binRange){
		return binRangeProvider.findBinRange(binRange);	
	}

}
