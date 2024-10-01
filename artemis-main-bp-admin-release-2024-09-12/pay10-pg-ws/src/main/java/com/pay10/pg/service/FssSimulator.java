package com.pay10.pg.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

	/**
	 * @author Surender
	 *
	 */

	
	/*@WebService(serviceName = "fssSimulator", targetNamespace = "http://www.27programs.com/spring-cxf-rest/services")
	@Consumes({ MediaType.APPLICATION_XML})
	@Produces({ MediaType.APPLICATION_XML })*/
	@RestController
	public class FssSimulator {

		private static final String response = "<result>APPROVED</result><auth>999999</auth><ref>503029434256</ref><avr>N</avr><postdate>0131</postdate><tranid>679921481950301</tranid><trackid>1501300748511000</trackid><payid>-1</payid><udf1>1501300748511000</udf1><udf2>test@pay10.com</udf2><udf4>1501300748511000</udf4><udf5>1501300748511000</udf5><amt>1.0</amt>";
		
		/*@POST
		@Consumes(MediaType.APPLICATION_XML)
		@Produces(MediaType.APPLICATION_XML)
		@Path("/simulate")
		public String transact(String request) {
				return response;
		}*/
		
		@RequestMapping("transaction")
		public String transact(@RequestParam (value="name", defaultValue="World") String name){
			return response;
		}
	}


