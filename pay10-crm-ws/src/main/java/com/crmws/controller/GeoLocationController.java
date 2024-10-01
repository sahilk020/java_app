package com.crmws.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmws.service.impl.CityService;
import com.pay10.commons.dao.CountryDao;
import com.pay10.commons.dao.StateDao;
import com.pay10.commons.entity.Country;
import com.pay10.commons.entity.State;

@RestController
public class GeoLocationController {

	@Autowired
	private CountryDao countryDao;

	@Autowired
	private StateDao stateDao;

	@Autowired
	private CityService cityService;

	@GetMapping("/getCountries")
	public List<Country> getAllCountries() {
		return countryDao.getCountries();
	}

	@GetMapping("/getStates")
	public List<State> getAllStatesByCountry(@RequestParam String countryName) {
		return stateDao.getByCountry(countryName);
	}

	@GetMapping("/getCities")
	public List<String> getAllCitiesByState(@RequestParam String countryName, @RequestParam String stateName) {
		return cityService.getAllCitiesByState(countryName, stateName);
	}
}
