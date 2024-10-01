package com.pay10.commons.dao;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pay10.commons.entity.Country;

@Service
public class CountryDao extends HibernateAbstractDao {

	@SuppressWarnings("unchecked")
	public List<Country> getCountries() {
		List<Country> countries = super.findAll(Country.class);
		return countries.stream().sorted(Comparator.comparing(Country::getName)).collect(Collectors.toList());
	}
}
