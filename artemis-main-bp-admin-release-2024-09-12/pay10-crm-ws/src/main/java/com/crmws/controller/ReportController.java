package com.crmws.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmws.dto.UserDTO;
import com.crmws.dto.UserData;

@RestController
@RequestMapping("/SmartReport")
public class ReportController {
	static final Logger logger = LoggerFactory.getLogger(ReportController.class.getName());

	@Autowired
	JdbcTemplate jdbcTamplate;
 
	@GetMapping("/user/{userType}")
	public ResponseEntity<List<UserDTO>> getMerchantList(@PathVariable String userType) {
		String sql = "Select payId, businessName from User U where U.userType = ? and U.userStatus='ACTIVE ' or U.userStatus='TRANSACTION_BLOCKED' order by businessName";

		List<UserDTO> userList = jdbcTamplate.query(sql, new Object[] { userType }, new RowMapper() {
			@Override
			public UserDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				  String payId =rs.getString("payId");
				String businessName = rs.getString("businessName");
				UserDTO dto = new UserDTO();
				/*
				 * dto.setEmailId(rs.getString("emailId")); dto.setPayId(rs.getString("payId"));
				 */
				dto.setBusinessName(businessName);
				dto.setPayId(payId);
			return dto;
			}
		});
		return new ResponseEntity<>(userList, HttpStatus.OK);
	}
	
	@GetMapping("/router")
	public ResponseEntity<List<UserData>> findAll(@RequestParam Long payId) {
		logger.info("SmartReport  router");
		String sql = "SELECT * FROM RouterConfiguration where status='ACTIVE' and merchant="+payId+"";
		List<UserData> userData = jdbcTamplate.query(sql, BeanPropertyRowMapper.newInstance(UserData.class));
          
		logger.info("User data" + userData.toString());
		return new ResponseEntity<>(userData, HttpStatus.OK);

	}
	
}
