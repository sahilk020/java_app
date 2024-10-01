package com.pay10.pg.security;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class YBNApiAuthenticator {

	public void authenticateToken(HttpServletRequest req) {
		///TODO add token to yml file
		String reqToken = req.getHeader("Authorization");
		String uatToken = "Basic eWVzdGVzdHVzZXI6akJMQ3ZIbXZaeE1vcXBOYnNmeW0=";
		if (!(StringUtils.isNotBlank(reqToken) && reqToken.equals(uatToken))) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
		}
	}
}
