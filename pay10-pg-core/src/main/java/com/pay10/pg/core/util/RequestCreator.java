
package com.pay10.pg.core.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.fss.plugin.iPayPipe;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Token;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.tokenization.TokenManager;
import com.pay10.pg.core.whitelabel.ReturnUrlCustomizer;

/**
 * @author Sunil
 *
 */
@Service
public class RequestCreator {

	private static Logger logger = LoggerFactory.getLogger(RequestCreator.class.getName());
	private static final ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private TransactionResponser transactionResponser;

	@Autowired
	private ReturnUrlCustomizer returnUrlCustomizer;

	@Autowired
	private UserDao userDao;

	@Autowired
	private TokenManager tokenManager;

	public void EnrollRequest(Fields responseMap) {
		try {
			/************* Enrolled card condition starts here ************/
			String acsurl = responseMap.get(FieldType.ACS_URL.getName());
			String PAReq = responseMap.get(FieldType.PAREQ.getName());
			String paymentid = responseMap.get(FieldType.PAYMENT_ID.getName());
			String termURL = returnUrlCustomizer.customizeReturnUrl(responseMap,
					PropertiesManager.propertiesMap.get("Request3DSURL"));

			PrintWriter out = ServletActionContext.getResponse().getWriter();

			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(acsurl);
			httpRequest.append("\" method=\"post\">");

			if (responseMap.get(FieldType.MOP_TYPE.getName()).equals(MopType.RUPAY.getCode())) {
				httpRequest.append("<input type=\"hidden\" name=\"PaymentID\" value=\"");
				httpRequest.append(paymentid);
				httpRequest.append("\">");
			} else {

				httpRequest.append("<input type=\"hidden\" name=\"PaReq\" value=\"");
				httpRequest.append(PAReq);
				httpRequest.append("\">");

				httpRequest.append("<input type=\"hidden\" name=\"MD\" value=\"");
				httpRequest.append(paymentid);
				httpRequest.append("\">");
				httpRequest.append("<input type=\"hidden\" name=\"TermUrl\" value=\"");
				httpRequest.append(termURL);
				httpRequest.append("\">");
			}

			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");

			out.write(httpRequest.toString());
			/************* Enrolled card condition Ends here ************/
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void FirstDataEnrollRequest(Fields responseMap) {
		try {
			/************* Enrolled card condition starts here ************/
			String acsurl = responseMap.get(FieldType.ACS_URL.getName());
			String PAReq = responseMap.get(FieldType.PAREQ.getName());
			// String termURL =
			// propertiesManager.getSystemProperty("Request3DSURL");
			String termURL = returnUrlCustomizer.customizeReturnUrl(responseMap,
					PropertiesManager.propertiesMap.get("FirstData3DSUrl"));
			String md = responseMap.get(FieldType.MD.getName());
			PrintWriter out = ServletActionContext.getResponse().getWriter();

			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(acsurl);
			httpRequest.append("\" method=\"post\">");

			httpRequest.append("<input type=\"hidden\" name=\"PaReq\" value=\"");
			httpRequest.append(PAReq);
			httpRequest.append("\">");

			httpRequest.append("<input type=\"hidden\" name=\"MD\" value=\"");
			httpRequest.append(md);
			httpRequest.append("\">");
			httpRequest.append("<input type=\"hidden\" name=\"TermUrl\" value=\"");
			httpRequest.append(termURL);
			httpRequest.append("\">");

			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");

			out.write(httpRequest.toString());
			/************* Enrolled card condition Ends here ************/
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void cSourceEnrollRequest(Fields responseMap) {
		try {
			/************* Enrolled card condition starts here ************/
			String acsurl = responseMap.get(FieldType.ACS_URL.getName());
			String PAReq = responseMap.get(FieldType.PAREQ.getName());
			// String termURL =
			// propertiesManager.getSystemProperty("Request3DSURL");
			String termURL = returnUrlCustomizer.customizeReturnUrl(responseMap,
					PropertiesManager.propertiesMap.get("CSource3DSUrl"));
			String md = responseMap.get(FieldType.MD.getName());
			PrintWriter out = ServletActionContext.getResponse().getWriter();

			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(acsurl);
			httpRequest.append("\" method=\"post\">");

			httpRequest.append("<input type=\"hidden\" name=\"PaReq\" value=\"");
			httpRequest.append(PAReq);
			httpRequest.append("\">");

			httpRequest.append("<input type=\"hidden\" name=\"MD\" value=\"");
			httpRequest.append(md);
			httpRequest.append("\">");
			httpRequest.append("<input type=\"hidden\" name=\"TermUrl\" value=\"");
			httpRequest.append(termURL);
			httpRequest.append("\">");

			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");

			out.write(httpRequest.toString());
			/************* Enrolled card condition Ends here ************/
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void generateTMBRequest(Fields fields) {
		try {
			String paymentString = fields.get(FieldType.TMBNB_FINAL_REQUEST.getName());
			logger.info("TMB NB BANK paymentString:++++++++++++++++++++++++++++++++++++++++++++"
					+ paymentString.toString());
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(paymentString);
			httpRequest.append("\" method=\"post\">");

			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");
			out.write(httpRequest.toString());

		} catch (Exception exception) {
			logger.error("Exception while posting TMB NB  payment request ", exception);
		}
	}

	public void generateShivalikRequest(Fields fields) {
		try {
			logger.info("generateShivalikRequest, fields= {}",fields.getFieldsAsString());
			String request = fields.get(FieldType.SHIVALIK_NB_FINAL_REQUEST.getName());
			String hostUrl = PropertiesManager.propertiesMap.get("SHIVALIKNBBANKSaleUrl");
			String api_key = fields.get(FieldType.ADF2.getName());
			logger.info("hostUrl "+hostUrl);
			logger.info("api_key "+api_key);
			System.out.println("generateShivalikRequest NB BANK request:++++++++++++++++++++++++++++++++++++++++++++"+ request);
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(hostUrl);//sale url
			httpRequest.append("\" method=\"post\">");
			logger.info("generateShivalikRequest httpRequest:++++++++++++++++++++++++++++++++++++++++++++" + httpRequest.toString());

			httpRequest.append("<input type=\"hidden\" name=\"api_key\" value=\"");
			httpRequest.append(api_key);
			httpRequest.append("\">");

			httpRequest.append("<input type=\"hidden\" name=\"encrypted_data\" value=\"");
			httpRequest.append(request);
			httpRequest.append("\">");

			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");

			out.write(httpRequest.toString());
			System.out.println("while posting Shivalik NB  payment request httpRequest "+ httpRequest.toString());
			logger.info("while posting Shivalik NB  payment request httpRequest ", httpRequest.toString());

		} catch (Exception exception) {
			logger.error("Exception while posting Shivalik NB  payment request ", exception);
		}
	}
	public void generateFreeChargeRequest(Fields fields) {
		try {
			String requestURL = PropertiesManager.propertiesMap.get("FREECHARGESaleUrl");
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			String finalRequest = fields.get(FieldType.FREECHARGE_FINAL_REQUEST.getName());
			String finalRequestSplit[] = finalRequest.split("~");

			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(requestURL);
			httpRequest.append("\" method=\"post\">");

			for (String entry : finalRequestSplit) {

				String entrySplit[] = entry.split("=");

				httpRequest.append("<input type=\"hidden\" name=\"");
				httpRequest.append(entrySplit[0]);
				httpRequest.append("\" value=\"");
				httpRequest.append(entrySplit[1]);
				httpRequest.append("\">");
			}

			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void generateCosmosupRequest(Fields fields) {
		StringBuilder httpRequest = new StringBuilder();
		try {
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			String finalRequest = fields.get(FieldType.COSMOS_UPI_FINAL_REQUEST.getName());
			String upiWaitPageReq = StringUtils.join("loadUpiWaitPage?orderId=", fields.get("PG_REF_NUM"),
					"&txnType=UP");

			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<center><h1>Please do not refresh this page...</h1></center>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest
					.append("{window.location= '" + fields.get(FieldType.COSMOS_UPI_FINAL_REQUEST.getName()) + "';}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");

			logger.info("cosmos httpRequest logs :: " + httpRequest.toString());
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void iciciMpgsEnrollRequest(Fields responseMap) {
		try {
			/************* Enrolled card condition starts here ************/
			String acsurl = responseMap.get(FieldType.ACS_URL.getName());
			String PAReq = responseMap.get(FieldType.PAREQ.getName());
			// String termURL =
			// propertiesManager.getSystemProperty("Request3DSURL");
			String termURL = PropertiesManager.propertiesMap.get("IciciMpgsReturnUrl");
			String md = responseMap.get(FieldType.MD.getName());
			PrintWriter out = ServletActionContext.getResponse().getWriter();

			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(acsurl);
			httpRequest.append("\" method=\"post\">");

			httpRequest.append("<input type=\"hidden\" name=\"PaReq\" value=\"");
			httpRequest.append(PAReq);
			httpRequest.append("\">");

			httpRequest.append("<input type=\"hidden\" name=\"MD\" value=\"");
			httpRequest.append(md);
			httpRequest.append("\">");
			httpRequest.append("<input type=\"hidden\" name=\"TermUrl\" value=\"");
			httpRequest.append(termURL);
			httpRequest.append("\">");

			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");

			out.write(httpRequest.toString());
			/************* Enrolled card condition Ends here ************/
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void InvalidRequest(Fields fields) {
		try {
			/************* Invalid Request ************/

			PrintWriter out = ServletActionContext.getResponse().getWriter();

			// TO remove internal custom MDC
			fields.removeInternalFields();
			transactionResponser.removeInvalidResponseFields(fields);

			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(fields.get(FieldType.RETURN_URL.getName()));
			httpRequest.append("\" method=\"post\">");
			for (String key : fields.keySet()) {
				httpRequest.append("<input type=\"hidden\" name=\"");
				httpRequest.append(key);
				httpRequest.append("\" value=\"");
				httpRequest.append(fields.get(key));
				httpRequest.append("\">");
			}
			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");

			out.write(httpRequest.toString());
			/************* Invalid Request ************/
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	// public void WebsitePackageRequest(String PAY_ID,String ORDER_ID,String
	// AMOUNT,String TXNTYPE,String CUST_NAME,String CUST_EMAIL,String
	// PRODUCT_DESC,String CURRENCY_CODE,String RETURN_URL,String HASH) {
	public void WebsitePackageRequest(Fields fields) {
		try {
			String requestURL = PropertiesManager.propertiesMap.get("RequestURL");
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(requestURL);
			httpRequest.append("\" method=\"post\">");
			for (String key : fields.keySet()) {
				httpRequest.append("<input type=\"hidden\" name=\"");
				httpRequest.append(key);
				httpRequest.append("\" value=\"");
				httpRequest.append(fields.get(key));
				httpRequest.append("\">");
			}

			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");

			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void generateIPayRequest(Fields fields) {
		String finalRequest = fields.get(FieldType.IPAY_FINAL_REQUEST.getName());
		try {
			PrintWriter out = ServletActionContext.getResponse().getWriter();

			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent() { ");
			httpRequest.append("window.location.assign('");
			httpRequest.append(finalRequest);
			httpRequest.append("') }");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");

			out.write(httpRequest.toString());

			logger.info("Final request sent " + httpRequest);

		} catch (IOException exception) {
			logger.error("Exception", exception);
		}
	}

	public String generateFederalRequest(Fields fields) throws SystemException {

		String finalRequest = fields.get(FieldType.FEDERAL_ENROLL_FINAL_REQUEST.getName());
		PrintWriter out;
		try {
			out = ServletActionContext.getResponse().getWriter();

			out.write(finalRequest);

		} catch (IOException iOException) {
			logger.error("", iOException);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, iOException,
					"Network Exception with Federal for auth");
		}
		return ErrorType.SUCCESS.getResponseMessage();
	}

	public String generateIciciNBRequest(Fields fields) throws SystemException {
		String request = fields.get(FieldType.ICICI_NB_FINAL_REQUEST.getName());
		PrintWriter out;
		try {
			out = ServletActionContext.getResponse().getWriter();

			out.write(request);

		} catch (IOException iOException) {
			logger.error("", iOException);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, iOException, "Network Exception with ICICI NB");
		}
		return ErrorType.SUCCESS.getResponseMessage();
	}

	/*
	 * public void generateSbiRequest(Fields fields) { try { String requestURL =
	 * PropertiesManager.propertiesMap.get("SbiSaleUrl"); PrintWriter out =
	 * ServletActionContext.getResponse().getWriter(); StringBuilder httpRequest =
	 * new StringBuilder(); httpRequest.append("<HTML>");
	 * httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >"); httpRequest.
	 * append("<center><h1>Please do not refresh this page...</h1></center>");
	 * httpRequest.append("<form name=\"form1\" action=\"");
	 * httpRequest.append(requestURL); httpRequest.append("\" method=\"post\">");
	 *
	 * httpRequest.append("<input type=\"hidden\" name=\"");
	 * httpRequest.append("encdata"); httpRequest.append("\" value=\"");
	 * httpRequest.append(fields.get(FieldType.SBI_FINAL_REQUEST.getName()));
	 * httpRequest.append("\">");
	 *
	 * httpRequest.append("<input type=\"hidden\" name=\"");
	 * httpRequest.append("merchant_code"); httpRequest.append("\" value=\"");
	 * httpRequest.append(fields.get(FieldType.MERCHANT_ID.getName()));
	 * httpRequest.append("\">");
	 *
	 * httpRequest.append("</form>");
	 * httpRequest.append("<script language=\"JavaScript\">");
	 * httpRequest.append("function OnLoadEvent()");
	 * httpRequest.append("{document.form1.submit();}");
	 * httpRequest.append("</script>"); httpRequest.append("</BODY>");
	 * httpRequest.append("</HTML>"); out.write(httpRequest.toString()); } catch
	 * (Exception exception) { logger.error("Exception", exception); } }
	 */

	// code added by sonu
	public void generateSbiRequest(Fields fields, String cardSaveFlag, String expiryMonth, String expiryYear) {
		try {

			// logger.info("fields :: " + fields.getFieldsAsString());
			fields.logAllFieldsUsingMasking("Request Fields Foe SBI Request :");

			// String requestURL = PropertiesManager.propertiesMap.get("SbiSaleUrl");
			PrintWriter out = ServletActionContext.getResponse().getWriter();

			if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase("NB")) {

				try {
					String requestURL = PropertiesManager.propertiesMap.get("SbiSaleUrl");
					StringBuilder httpRequest = new StringBuilder();
					httpRequest.append("<HTML>");
					httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
					httpRequest.append("<center><h1>Please do not refresh this page...</h1></center>");
					httpRequest.append("<form name=\"form1\" action=\"");
					httpRequest.append(requestURL);
					httpRequest.append("\" method=\"post\">");

					httpRequest.append("<input type=\"hidden\" name=\"");
					httpRequest.append("encdata");
					httpRequest.append("\" value=\"");
					httpRequest.append(fields.get(FieldType.SBI_FINAL_REQUEST.getName()));
					httpRequest.append("\">");

					httpRequest.append("<input type=\"hidden\" name=\"");
					httpRequest.append("merchant_code");
					httpRequest.append("\" value=\"");
					httpRequest.append(fields.get(FieldType.MERCHANT_ID.getName()));
					httpRequest.append("\">");

					httpRequest.append("</form>");
					httpRequest.append("<script language=\"JavaScript\">");
					httpRequest.append("function OnLoadEvent()");
					httpRequest.append("{document.form1.submit();}");
					httpRequest.append("</script>");
					httpRequest.append("</BODY>");
					httpRequest.append("</HTML>");
					out.write(httpRequest.toString());
				} catch (Exception exception) {
					logger.error("Exception", exception);
				}

			} else if(fields.get(FieldType.MOP_TYPE.getName()).equalsIgnoreCase(MopType.RUPAY.getCode())&&fields.get(FieldType.SBI_OTP_PAGE.getName()).equalsIgnoreCase("Y")) {
				StringBuilder httpRequest = new StringBuilder();
				String responseUrl = PropertiesManager.propertiesMap.get("SBICARDRuPayReturnUrl");

				String requestURL = PropertiesManager.propertiesMap.get("SBICARDOTPUrl")+fields.get(FieldType.ORDER_ID.getName())+"&PostUrl="+responseUrl;
logger.info("request fir rupay otp"+requestURL);
		httpRequest.append("<HTML>");
		httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
		httpRequest.append("<form name=\"form1\" action=\"");
		httpRequest.append(requestURL);
		httpRequest.append("\" method=\"post\">");


		httpRequest.append("</form>");
		httpRequest.append("<script language=\"JavaScript\">");
		httpRequest.append("function OnLoadEvent()");
		httpRequest.append("{document.form1.submit();}");
		httpRequest.append("</script>");
		httpRequest.append("</BODY>");
		httpRequest.append("</HTML>");
		out.write(httpRequest.toString());

		logger.info("<<<<<<<<<<<< httpRequest >>>>>>>>>>>>>>> " + httpRequest.toString());
		
		
		
				
				
				
				
				
			}else if (fields.get(FieldType.MOP_TYPE.getName()).equalsIgnoreCase(MopType.RUPAY.getCode())&&fields.get(FieldType.SBI_OTP_PAGE.getName()).equalsIgnoreCase("N")) {
				String sbiacqUrl = PropertiesManager.propertiesMap.get("SBICARDRuPayACQReturnUrl");
				String	action=fields.get(FieldType.SBI_FINAL_REQUEST.getName());

				String[] data1=action.split("\\?");
				String[] data2=data1[1].split("\\&");
				Map<String,String > map = new HashMap<String ,String>();
				for (int i=0 ;i<data2.length;i++) {
					String[] data3=data2[i].split("\\=");
					
					map.put(data3[0], data3[1]);
					System.out.println(map);

					

				}
				
				StringBuilder httpRequest = new StringBuilder();
				httpRequest.append("<HTML>");
				httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
				httpRequest.append("<form name=\"form1\" action=\"");
				httpRequest.append(data1[0]);
				httpRequest.append("\" method=\"post\">");
				 httpRequest.append("<input type=\"hidden\" name=\"");
				 httpRequest.append("session");
				 httpRequest.append("\" value=\"");
				 httpRequest.append(fields.get(FieldType.SBI_AUTH_RESPONSE.getName()));
				 httpRequest.append("\">");
				 httpRequest.append("<input type=\"hidden\" name=\"");
				 httpRequest.append("AccuGuid");
				 httpRequest.append("\" value=\"");
				 httpRequest.append(map.get("AccuGuid").toString());
				 httpRequest.append("\">");
				 httpRequest.append("<input type=\"hidden\" name=\"");
				 httpRequest.append("AccuRequestId");
				 httpRequest.append("\" value=\"");
				 httpRequest.append(fields.get(FieldType.SBI_CARD_PA_RES.getName()));
				 httpRequest.append("\">");
				 httpRequest.append("<input type=\"hidden\" name=\"");
				 httpRequest.append("AccuReturnURL");
				 httpRequest.append("\" value=\"");
				 httpRequest.append(sbiacqUrl);
				 httpRequest.append("\">");
				 httpRequest.append("<input type=\"hidden\" name=\"");
				 httpRequest.append("AccuCardholderId");
				 httpRequest.append("\" value=\"");
				 httpRequest.append(map.get("AccuCardholderId").toString());
				 httpRequest.append("\">");

				

				httpRequest.append("</form>");
				httpRequest.append("<script language=\"JavaScript\">");
				httpRequest.append("function OnLoadEvent()");
				httpRequest.append("{document.form1.submit();}");
				httpRequest.append("</script>");
				httpRequest.append("</BODY>");
				httpRequest.append("</HTML>");
				logger.info("                    "+httpRequest.toString());

				out.write(httpRequest.toString());
			}
			
			
			else
			
			{

				HttpServletResponse response = ServletActionContext.getResponse();
				String paResponse = fields.get(FieldType.SBI_CARD_PA_RES.getName());
				if (StringUtils.isNotBlank(paResponse)) {
					org.json.simple.JSONObject resJson = mapper.readValue(paResponse,
							org.json.simple.JSONObject.class);
					logger.info("generateSbiRequest:: response={}", resJson);
					if (StringUtils.equalsIgnoreCase(
							resJson.getOrDefault("transStatus", "").toString(),
							"C")) {
						String cReq = resJson.get("cReq").toString();
						String challangeUrl = resJson.get("acsChallengeReqUrl").toString();
						redirectToSbiOtpPage(challangeUrl, cReq);
					}
					return;
				}

				// Save Card detail for Express Payment
//				if (StringUtils.equalsIgnoreCase(cardSaveFlag, "true")) {
				logger.info("-----------------Saving Card Details SBI---------------------------------");
				fields.put(com.pay10.pg.core.payu.util.Constants.CCEXPMON, expiryMonth);
				fields.put(com.pay10.pg.core.payu.util.Constants.CCEXPYR, expiryYear);
				String mopType = fields.get(FieldType.MOP_TYPE.getName());
				fields.put(FieldType.INTERNAL_CARD_ISSUER_BANK.getName(), MopType.getmop(mopType).getName());
				logger.info("Respo Code ={}", fields.get(FieldType.RESPONSE_CODE.getName()));
				logger.info("Txn Type ={}", fields.get(FieldType.TXNTYPE.getName()));
				logger.info("fields : {}", fields.getFieldsAsBlobString());
				if (StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_CODE.getName()))
						&& fields.get(FieldType.RESPONSE_CODE.getName()).equalsIgnoreCase(ErrorType.SUCCESS.getCode())
						&& !fields.get(FieldType.TXNTYPE.getName())
						.equalsIgnoreCase(TransactionType.NEWORDER.getName())) {
					User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
					if (StringUtils.isBlank(user.getCardSaveParam())) {
						user.setCardSaveParam(FieldType.CUST_EMAIL.getName());
					}
					Token token = tokenManager.addToken(fields, user);
					String tokenId = token == null ? null : token.getId();
					fields.put(FieldType.TOKEN_ID.getName(), tokenId);
				}
//				}
				// StringBuilder httpRequest = new StringBuilder();
				// httpRequest.append("<HTML>");
				// httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
				// httpRequest.append("<center><h1>Please do not refresh this
				// page...</h1></center>");
				// httpRequest.append("<form name=\"form1\" action=\"");
				// httpRequest.append(requestURL);
				// httpRequest.append("\" method=\"post\">");

				// httpRequest.append("<input type=\"hidden\" name=\"");
				// httpRequest.append("encdata");
				// httpRequest.append("\" value=\"");
				// httpRequest.append(fields.get(FieldType.SBI_FINAL_REQUEST.getName()));
				// httpRequest.append("\">");

				// httpRequest.append("<input type=\"hidden\" name=\"");
				// httpRequest.append("merchant_code");
				// httpRequest.append("\" value=\"");
				// httpRequest.append(fields.get(FieldType.MERCHANT_ID.getName()));
				// httpRequest.append("\">");

				// httpRequest.append("</form>");
				// httpRequest.append("<script language=\"JavaScript\">");
				// httpRequest.append("function OnLoadEvent()");
				// httpRequest.append("{document.form1.submit();}");
				// httpRequest.append("</script>");
				// httpRequest.append("</BODY>");
				// httpRequest.append("</HTML>");
				// out.write(httpRequest.toString());

				JSONObject reqJson = new JSONObject(fields.get(FieldType.SBI_FINAL_REQUEST.getName()));

				String merchantId = fields.get(FieldType.MERCHANT_ID.getName());
				logger.info("Merchant Id For SBI-Card :: " + merchantId);
				iPayPipe pipe = new iPayPipe();
				String resourcePath = PropertiesManager.propertiesMap.get(merchantId + "_SbiResourcePath");
				String keystorePath = PropertiesManager.propertiesMap.get(merchantId + "_SbikeystorePath");
				String aliasName = PropertiesManager.propertiesMap.get(merchantId + "_SbiAliasName");

				logger.info(
						"resourcePath " + resourcePath + " keystorePath " + keystorePath + " aliasName " + aliasName);

				String action = "1"; // Mandatory

				// String aliasName = "Terminal aliasName"; // Terminal Alias Name. Mandatory
				// String currency = "currency"; // (ex: "512") //Transaction Currency.
				// Mandatory
				// String language = "language"; // (ex: "USA") //Optional.
				// String amount = "10.00"; // Transaction Amount. Mandatory
				// String trackid = "109088888"; // Merchant Track ID. Mandatory
				// String cardNumber = "4000000000000002"; // Mandatory
				// String expmm = "12"; // Mandatory
				// String expyy = "2015"; // Mandatory
				// String name = "Mohammed"; // Mandatory
				// String cvv = "123"; // Mandatory
				// Card type. Mandatory
				// Value for credit card "C", for debit card "D", for Rupay debit card "RDC"
				// String type = "C";
				// User Defined Fields
				// String Udf1 = "Udf1"; // Optional
				// String Udf2 = "Udf2"; // Optional
				// String Udf3 = "Udf3"; // Optional
				// String Udf4 = "Udf4"; // Optional
				// String Udf5 = "Udf5"; // Optional

				boolean expressPay = reqJson.has(FieldType.UDF6.getName())
						&& StringUtils.isNotBlank(reqJson.getString(FieldType.UDF6.getName()));
				// Set Values
				pipe.setTrackId(reqJson.getString("orderId")); // trckId
				pipe.setAlias(aliasName);
				pipe.setResourcePath(resourcePath);
				pipe.setAction(action);
				pipe.setAmt(reqJson.getString("orderAmount"));
				pipe.setCurrency(fields.get(FieldType.CURRENCY_CODE.getName())); // currency
				// reqJson.getString("orderCurrency")
				if (!expressPay) {
					pipe.setCard(reqJson.getString("card_number")); // cardNumber
					pipe.setCvv2(reqJson.getString("card_cvv")); // cvv
					pipe.setExpMonth(reqJson.getString("card_expiryMonth")); // expmm
					pipe.setExpYear(reqJson.getString("card_expiryYear")); // expyy
					pipe.setMember(reqJson.getString("card_holder")); // name
				} else {
					pipe.setUdf6(reqJson.getString(FieldType.UDF6.getName()));
				}

				if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.CREDIT_CARD.getCode())) {
					pipe.setType("C");
				} else if (fields.get(FieldType.PAYMENT_TYPE.getName())
						.equalsIgnoreCase(PaymentType.DEBIT_CARD.getCode())) {

					if (fields.get(FieldType.MOP_TYPE.getName()).equalsIgnoreCase(MopType.RUPAY.getCode())) {
						pipe.setType("RDC"); // Set "RDC" for Rupay debit card
					} else {
						pipe.setType("D");
					}
				}

				// Type
				pipe.setLanguage("USA");
				pipe.setUdf1(null);
				pipe.setUdf2(null);
				pipe.setUdf3(null);
				pipe.setUdf4(null);
				pipe.setUdf5(null);
				pipe.setKeystorePath(keystorePath);
				pipe.setResponseURL(reqJson.getString("returnUrl")); //
				pipe.setErrorURL(reqJson.getString("returnUrl")); // errorURL

				logger.info("Start performVbVTransaction to SBI PG connection for id=" + pipe.getTrackId());
				// The method to be called for connecting Payment Gateway
				int result = pipe.performVbVTransaction();
				// To redirect the web address.
				logger.info("After performVbVTransaction PG connection | result = " + result);

				if (result == 0) {
					logger.info("pipe.getWebAddress() " + pipe.getWebAddress());
					response.sendRedirect(pipe.getWebAddress()); // Redirect to Payment Gateway
				} else {
					logger.info(pipe.getError());
					logger.info(pipe.getError_text());
					logger.info(pipe.getErrorURL());
					logger.info(pipe.getError_service_tag());
					out.println(pipe.getError()); // Problem in connecting Payment Gateway

				}

			}

		} catch (Exception e) {
			logger.error("Exception", e);
		}

	}
	public void redirectToSbiOtpPage(String action, String cReq) {
		try {
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(action);
			httpRequest.append("\" method=\"post\">");

			httpRequest.append("<input type=\"hidden\" name=\"");
			httpRequest.append("creq");
			httpRequest.append("\" value=\"");
			httpRequest.append(cReq);
			httpRequest.append("\">");

			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");

			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}


	public String generateBobRequest(Fields fields) {
		StringBuilder httpRequest = new StringBuilder();
		try {
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			String finalRequest = fields.get(FieldType.BOB_FINAL_REQUEST.getName());

			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{window.location= '" + finalRequest + "';}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return httpRequest.toString();
	}

	public String generateFssRequest(Fields fields) {
		StringBuilder httpRequest = new StringBuilder();
		try {
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			String finalRequest = fields.get(FieldType.FSS_FINAL_REQUEST.getName());

			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{window.location= '" + finalRequest + "';}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return httpRequest.toString();
	}

	public void generateAtlRequest(Fields fields) {
		try {
			String requestURL = PropertiesManager.propertiesMap.get("ATLSaleUrl");
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(requestURL);
			httpRequest.append("\" method=\"post\">");

			httpRequest.append("<input type=\"hidden\" name=\"");
			httpRequest.append("encdata");
			httpRequest.append("\" value=\"");
			httpRequest.append(fields.get(FieldType.ATL_FINAL_REQUEST.getName()));
			httpRequest.append("\">");

			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");

			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void generateKotakRequest(Fields fields) {
		try {

			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();

			logger.info("Request Received For generateKotakRequest " + fields.get(FieldType.PAYMENT_TYPE.getName()));
			if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase("NB")) {
				String requestURL = PropertiesManager.propertiesMap.get("KotakNBSaleUrl");

				httpRequest.append("<HTML>");
				httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
				httpRequest.append("<form name=\"form1\" action=\"");
				httpRequest.append(requestURL);
				httpRequest.append("\" method=\"post\">");

				httpRequest.append("<input type=\"hidden\" name=\"");
				httpRequest.append("msg");
				httpRequest.append("\" value=\"");
				httpRequest.append(fields.get(FieldType.KOTAK_FINAL_REQUEST.getName()));
				httpRequest.append("\">");
				httpRequest.append("<input type=\"hidden\" name=\"");
				httpRequest.append("merchantId");
				httpRequest.append("\" value=\"");
				httpRequest.append(fields.get(FieldType.MERCHANT_ID.getName()));
				httpRequest.append("\">");

				httpRequest.append("</form>");
				httpRequest.append("<script language=\"JavaScript\">");
				httpRequest.append("function OnLoadEvent()");
				httpRequest.append("{document.form1.submit();}");
				httpRequest.append("</script>");
				httpRequest.append("</BODY>");
				httpRequest.append("</HTML>");

				logger.info("<<<<<<<<<<<< httpRequest >>>>>>>>>>>>>>> " + httpRequest.toString());
				out.write(httpRequest.toString());

			} else {
				
			if (StringUtils.isNoneBlank(fields.get(FieldType.KOTAK_FINAL_REQUEST.getName()))) {
				String requestURL = PropertiesManager.propertiesMap.get("KotakSaleUrl");

				

				out.write(fields.get(FieldType.KOTAK_FINAL_REQUEST.getName()).toString());

			}
			else {
				String kotakresponseUrl = PropertiesManager.propertiesMap.get("KOTAKCARDReturnUrl");

						String requestURL = PropertiesManager.propertiesMap.get("KOTAKCARDOtpUrl")+fields.get(FieldType.ORDER_ID.getName())+"&PostUrl="+kotakresponseUrl;
logger.info("request fir rupay otp"+requestURL);
				httpRequest.append("<HTML>");
				httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
				httpRequest.append("<form name=\"form1\" action=\"");
				httpRequest.append(requestURL);
				httpRequest.append("\" method=\"post\">");


				httpRequest.append("</form>");
				httpRequest.append("<script language=\"JavaScript\">");
				httpRequest.append("function OnLoadEvent()");
				httpRequest.append("{document.form1.submit();}");
				httpRequest.append("</script>");
				httpRequest.append("</BODY>");
				httpRequest.append("</HTML>");
				out.write(httpRequest.toString());

				logger.info("<<<<<<<<<<<< httpRequest >>>>>>>>>>>>>>> " + httpRequest.toString());
				
				
				
				
				
				
				
			}
				
			}

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void generateIdbiRequest(Fields fields) {
		try {
			String requestURL = PropertiesManager.propertiesMap.get("IdbiSaleUrl");
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(requestURL);
			httpRequest.append("\" method=\"post\">");

			httpRequest.append("<input type=\"hidden\" name=\"");
			httpRequest.append("merchantRequest");
			httpRequest.append("\" value=\"");
			httpRequest.append(fields.get(FieldType.IDBI_FINAL_REQUEST.getName()));
			httpRequest.append("\">");
			httpRequest.append("<input type=\"hidden\" name=\"");
			httpRequest.append("MID");
			httpRequest.append("\" value=\"");
			httpRequest.append(fields.get(FieldType.MERCHANT_ID.getName()));
			httpRequest.append("\">");
			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void generateDirecpayRequest(Fields fields) {
		try {
			String requestURL = PropertiesManager.propertiesMap.get("DirecpaySaleUrl");
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(requestURL);
			httpRequest.append("\" method=\"post\">");

			httpRequest.append("<input type=\"hidden\" name=\"");
			httpRequest.append("requestParameter");
			httpRequest.append("\" value=\"");
			httpRequest.append(fields.get(FieldType.DIRECPAY_FINAL_REQUEST.getName()));
			httpRequest.append("\">");
			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void generateAtomRequest(Fields fields) {
		try {
			String loginString = "login=" + fields.get(FieldType.MERCHANT_ID.getName());
			String encDataString = "&encdata=" + fields.get(FieldType.ATOM_FINAL_REQUEST.getName());

			String requestURL = PropertiesManager.propertiesMap.get("ATOMSaleUrl");
			requestURL = requestURL + loginString + encDataString;

			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(requestURL);
			httpRequest.append("\" method=\"post\">");
			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}
	
	public void generateQuomoRequest(Fields fields) {
		try {
			String requestURL = fields.get(FieldType.QUOMO_FINAL_REQUEST.getName());

			PrintWriter out = ServletActionContext.getResponse().getWriter();
			if (null != requestURL && requestURL.startsWith("https:")) {
				StringBuilder httpRequest = new StringBuilder();
				httpRequest.append("<HTML>");
				httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
				httpRequest.append("<form name=\"form1\" action=\"");
				httpRequest.append(requestURL);
				httpRequest.append("\" method=\"post\">");
				httpRequest.append("</form>");
				httpRequest.append("<script language=\"JavaScript\">");
				httpRequest.append("function OnLoadEvent()");
				httpRequest.append("{document.form1.submit();}");
				httpRequest.append("</script>");
				httpRequest.append("</BODY>");
				httpRequest.append("</HTML>");
				logger.info("********** generateQuomoRequest (redirect url) ***********" + httpRequest.toString());
				out.write(httpRequest.toString());
			} else {
				logger.info("********** generateQuomoRequest (reload page) ***********" + requestURL);
				out.write(requestURL);
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}
	
	public void generateHtpayRequest(Fields fields) {
		try {
			logger.info("fields generateHtpayRequest="+fields.getFieldsAsString());
			String requestURL = fields.get(FieldType.HTPAY_FINAL_REQUEST.getName());

			PrintWriter out = ServletActionContext.getResponse().getWriter();
				StringBuilder httpRequest = new StringBuilder();
				httpRequest.append("<HTML>");
				httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
				httpRequest.append("<form name=\"form1\" action=\"");
				httpRequest.append(requestURL);
				httpRequest.append("\" method=\"post\">");
				httpRequest.append("</form>");
				httpRequest.append("<script language=\"JavaScript\">");
				httpRequest.append("function OnLoadEvent()");
				httpRequest.append("{document.form1.submit();}");
				httpRequest.append("</script>");
				httpRequest.append("</BODY>");
				httpRequest.append("</HTML>");
				logger.info("********** generateHtpayRequest (redirect url) ***********" + httpRequest.toString());
				out.write(httpRequest.toString());
			
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void generateIngenicoRequest(Fields fields) {
		try {
			String requestURL = fields.get(FieldType.INGENICO_FINAL_REQUEST.getName());

			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(requestURL);
			httpRequest.append("\" method=\"post\">");
			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void generateApblRequest(Fields fields) {
		try {
			String paymentString = fields.get(FieldType.APBL_FINAL_REQUEST.getName());

			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(paymentString);
			httpRequest.append("\" method=\"post\">");
			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception while posting APBL payment request ", exception);
		}
	}

	public void generatePaytmRequest(Fields fields) {
		try {
			String paymentString = fields.get(FieldType.PAYTM_FINAL_REQUEST.getName());

			PrintWriter out = ServletActionContext.getResponse().getWriter();
			out.write(paymentString);

		} catch (Exception exception) {
			logger.error("Exception while posting PAYTM payment request ", exception);
		}
	}

	public void generatePayuRequest(Fields fields) {
		try {

			String paymentString = fields.get(FieldType.PAYU_FINAL_REQUEST.getName());

			logger.info("Request Fields : " + fields.getFieldsAsString());
			logger.info("Request post to payu acquirer paymentString " + paymentString);
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			out.write(paymentString);

		} catch (Exception exception) {
			logger.error("Exception while posting PAYU payment request ", exception);
		}
	}

	public void generatePhonePeRequest(Fields fields) {
		try {
			String paymentString = fields.get(FieldType.PHONEPE_FINAL_REQUEST.getName());

			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(paymentString);
			httpRequest.append("\" method=\"post\">");

			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");
			out.write(httpRequest.toString());

		} catch (Exception exception) {
			logger.error("Exception while posting PhonePe payment request ", exception);
		}
	}

	public void generateMobikwikRequest(Fields fields) {
		try {
			String paymentString = fields.get(FieldType.MOBIKWIK_FINAL_REQUEST.getName());

			String requestURL = PropertiesManager.propertiesMap.get("MobikwikSaleUrl");
			requestURL = requestURL.concat(paymentString);
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(requestURL);
			httpRequest.append("\" method=\"post\">");

			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception while posting mobikwik payment request ", exception);
		}
	}

	public void generateAxisBankRequest(Fields fields) {
		try {
			String requestURL = PropertiesManager.propertiesMap.get("DirecpaySaleUrl");
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(requestURL);
			httpRequest.append("\" method=\"post\">");

			httpRequest.append("<input type=\"hidden\" name=\"");
			httpRequest.append("requestParameter");
			httpRequest.append("\" value=\"");
			httpRequest.append(fields.get(FieldType.DIRECPAY_FINAL_REQUEST.getName()));
			httpRequest.append("\">");
			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void generateAxisBankNBRequest(Fields fields) {
		try {
			// String requestURL = PropertiesManager.propertiesMap.get("AxisbankNBSaleUrl")
			// + "AXBHRT";
			// + fields.get(FieldType.ADF11.getName());

			String requestURL = PropertiesManager.propertiesMap.get("AxisbankNBSaleUrl")
					+ fields.get(FieldType.ADF11.getName());

			logger.info("requestURL " + requestURL);
			String reutrnURL = returnUrlCustomizer.customizeReturnUrl(fields,
					PropertiesManager.propertiesMap.get("AxisbankNBReturnUrl"));
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(requestURL);
			httpRequest.append("\" method=\"post\">");

			httpRequest.append("<input type=\"hidden\" name=\"");
			httpRequest.append("qs");
			httpRequest.append("\" value=\"");
			httpRequest.append(fields.get(FieldType.AXISBANK_NB_FINAL_REQUEST.getName()));
			httpRequest.append("\">");
			httpRequest.append("<input type=\"hidden\" name=\"");
			httpRequest.append("RU");
			httpRequest.append("\" value=\"");
			httpRequest.append(reutrnURL);
			httpRequest.append("\">");
			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	/**
	 * This function will be used to post the data on Federal bank URL
	 */
	public void generateFederalBankNBRequest(Fields fields) {
		try {
			String requestURL = PropertiesManager.propertiesMap.get("FederalbankNBSaleUrl");

			logger.info(fields.get(FieldType.FEDERALBANK_NB_REQUEST.getName())
					+ "========FederalbankNBSaleUrl   requestURL " + requestURL);
			JSONObject reqJson = new JSONObject(fields.get(FieldType.FEDERALBANK_NB_REQUEST.getName()));

			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<center><h1>Please do not refresh this page...</h1></center>");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(requestURL);
			httpRequest.append("\" method=\"post\">");

			for (String keys : reqJson.keySet()) {
				String row = " <input type=\"hidden\" name=\"" + keys + "\" value=\"" + reqJson.get(keys).toString()
						+ "\"/>";
				httpRequest.append(row);
			}

			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void generateBilldeskRequest(Fields fields) {
		try {
			String requestURL = PropertiesManager.propertiesMap.get("BilldeskSaleUrl");
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(requestURL);
			httpRequest.append("\" method=\"post\">");
			httpRequest.append("<input type=\"hidden\" name=\"msg\" value=\"");
			httpRequest.append(fields.get(FieldType.BILLDESK_FINAL_REQUEST.getName()));
			httpRequest.append("\">");
			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");

			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}
	
	public void generateCityUnionBankRequest(Fields fields) {
		try {
			String paymentString = fields.get(FieldType.CITY_UNION_BANK_FINAL_REQUEST.getName());
			String requestURL = PropertiesManager.propertiesMap.get("CITYUNIONBANK_SALE_URL")+"?MDATA=";

//			String clientId = PropertiesManager.propertiesMap.get("CITYUNIONBANK_X-IBM-Client-Id");
//			String ClientSecret = PropertiesManager.propertiesMap.get("CITYUNIONBANKX-IBM-Client-Secret");

			requestURL = requestURL.concat(paymentString);
			logger.info("url request city union bank"+requestURL);
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(requestURL);
			httpRequest.append("\" method=\"post\">");
//			httpRequest.append("<input type=\"hidden\" name=\"X-IBM-Client-Id\" value=\"");
//			httpRequest.append(clientId);
//			httpRequest.append("\">");
//			httpRequest.append("<input type=\"hidden\" name=\"X-IBM-Client-Secret\" value=\"");
//			httpRequest.append(ClientSecret);
//			httpRequest.append("\">");
//			httpRequest.append("<input type=\"hidden\" name=\"MDATA\" value=\"");
//			httpRequest.append(paymentString);
//			httpRequest.append("\">");
			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");
			
//		httpRequest.append(requestURL);

			logger.info(httpRequest.toString());
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception while posting city union bank payment request ", exception);
		}
	}



	public void generateCanaraRequest(Fields fields) {
		try {
			String paymentString = fields.get(FieldType.CANARANB_FINAL_REQUEST.getName());
			logger.info("CANARA NB BANK paymentString:++++++++++++++++++++++++++++++++++++++++++++"
					+ paymentString.toString());
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(paymentString);
			httpRequest.append("\" method=\"post\">");

			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");
			out.write(httpRequest.toString());

		} catch (Exception exception) {
			logger.error("Exception while posting Canara NB  payment request ", exception);
		}
	}
	public void generateTFPRequest(Fields fields) {
		try {

			String requestURL = PropertiesManager.propertiesMap.get("TFP_CAPTURED_URL");
			String TXN_ID = fields.get(FieldType.TFP_FINAL_REQUEST.getName());
			String salt_key = fields.get(FieldType.ADF2.getName());
			String app_ip = fields.get(FieldType.ADF3.getName());
			ArrayList<String> hashParam = new ArrayList<String>();
			hashParam.add(app_ip);
			hashParam.add(TXN_ID);
			String generateHash = TFPUtil.generateHash(hashParam, salt_key);

			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(requestURL);
			httpRequest.append("\" method=\"post\">");
			httpRequest.append("<input type=\"hidden\" name=\"");
			httpRequest.append("APP_ID");
			httpRequest.append("\" value=\"");
			httpRequest.append(app_ip);
			httpRequest.append("<input type=\"hidden\" name=\"");
			httpRequest.append("TXN_ID");
			httpRequest.append("\" value=\"");
			httpRequest.append(TXN_ID);
			httpRequest.append("<input type=\"hidden\" name=\"");
			httpRequest.append("HASH");
			httpRequest.append("\" value=\"");
			httpRequest.append(generateHash);
			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");
			logger.info("Acquirer for TFP"+httpRequest);

			out.write(httpRequest.toString());

		} catch (Exception exception) {
			logger.error("Exception while posting TFP payment request ", exception);
		}
	}

	public void generateIsgpayRequest(Fields fields) {
		try {
			String requestURL = PropertiesManager.propertiesMap.get("ISGPAYSaleUrl");

			logger.info("<<<<< requestURL >>>>>>> " + requestURL);
			logger.info("<<<<<< ISGPAY_FINAL_REQUEST >>>>>> " + fields.get(FieldType.ISGPAY_FINAL_REQUEST.getName()));
			logger.info("<<<<< MERCHANT_ID >>>>>>> " + fields.get(FieldType.MERCHANT_ID.getName()));
			logger.info("<<<<< TERMINAL_ID >>>>>>> " + fields.get(FieldType.TERMINAL_ID.getName()));

			String merchantId = null;
			String terminalId = null;

			merchantId = fields.get(FieldType.MERCHANT_ID.getName());
			terminalId = fields.get(FieldType.TERMINAL_ID.getName());

			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(requestURL);
			httpRequest.append("\" method=\"post\">");

			httpRequest.append("<input type=\"hidden\" name=\"");
			httpRequest.append("MerchantId");
			httpRequest.append("\" value=\"");
			httpRequest.append(merchantId);
			httpRequest.append("\">");
			httpRequest.append("<input type=\"hidden\" name=\"");
			httpRequest.append("TerminalId");
			httpRequest.append("\" value=\"");
			httpRequest.append(terminalId);
			httpRequest.append("\">");
			httpRequest.append("<input type=\"hidden\" name=\"");
			httpRequest.append("BankId");
			httpRequest.append("\" value=\"");
			httpRequest.append(fields.get(FieldType.BANK_ID.getName()));
			httpRequest.append("\">");
			httpRequest.append("<input type=\"hidden\" name=\"");
			httpRequest.append("Version");
			httpRequest.append("\" value=\"");
			httpRequest.append("1");
			httpRequest.append("\">");
			httpRequest.append("<input type=\"hidden\" name=\"");
			httpRequest.append("EncData");
			httpRequest.append("\" value=\"");
			httpRequest.append(fields.get(FieldType.ISGPAY_FINAL_REQUEST.getName()));
			httpRequest.append("\">");
			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void generateMatchMoveRequest(Fields fields) {
		StringBuilder httpRequest = new StringBuilder();
		try {
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			String finalRequest = fields.get(FieldType.MATCH_MOVE_FINAL_REQUEST.getName());

			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{window.location= '" + finalRequest + "';}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}

	}

	public void generateCamsPayRequest(Fields fields) {
		try {
			String paymentString = fields.get(FieldType.CAMSPAY_FINAL_REQUEST.getName());
			logger.info("generateCamsPayRequest:: fields={}", fields.getFieldsAsString());
			logger.info("generateCamsPayRequest:: post to acquirer paymentString={}", paymentString);
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			out.write(paymentString);
		} catch (Exception exception) {
			logger.error("generateCamsPayRequest:: failed", exception);
		}
	}

//	public void generateMatchMoveRequest(Fields fields) {
//		String requestURL = PropertiesManager.propertiesMap.get("MatchMoveSaleUrl");
//		String response = "";
//		 try
//	        {
//			URL url = new URL(requestURL);
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//			conn.setUseCaches(false);
//			conn.setDoInput(true);
//			conn.setDoOutput(true);
//
//			conn.setRequestMethod("POST");
//			conn.setRequestProperty("Content-Type", "application/json");
//         	OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//			wr.write(fields.get(FieldType.MATCH_MOVE_FINAL_REQUEST.getName()));
//			wr.flush();
//			InputStream is=conn.getInputStream();
//
//			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is));
//			String decodedString;
//
//			while ((decodedString = bufferedreader.readLine()) != null) {
//				response = response + decodedString;
//			}
//	      	bufferedreader.close();
//	      	logger.info(response);
//
//	        }
//	        catch (Exception exception) {
//	        	logger.error(exception);
//
//				// TODO: handle exception
//			}
//
//		}

	public String sendMigsEnrollTransaction(Fields fields) throws SystemException {

		String url = ConfigurationConstants.AXIS_MIGS_TRANSACTION_URL.getValue();
		String finalRequest = fields.get(FieldType.MIGS_FINAL_REQUEST.getName());
		PrintWriter out;
		try {
			out = ServletActionContext.getResponse().getWriter();

			out.write(finalRequest);

		} catch (IOException iOException) {
			logger.error("", iOException);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, iOException,
					"Network Exception with Amex for auth" + url.toString());
		}
		return ErrorType.SUCCESS.getResponseMessage();
	}

	public void generateEnrollIdbiRequest(Fields fields) {
		try {

			String returnUrl = returnUrlCustomizer.customizeReturnUrl(fields,
					PropertiesManager.propertiesMap.get("IdbiReturnUrl"));
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			String AcsReq = fields.get(FieldType.ACS_REQ_MAP.getName());
			String value = HtmlUtils.htmlUnescape(AcsReq);
			JSONObject jsonData = new JSONObject(value);
			Map<String, String> requestMap = new HashMap<String, String>();
			for (Object key : jsonData.keySet()) {
				requestMap.put(key.toString(), jsonData.getString(key.toString()));
			}
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(fields.get(FieldType.ACS_URL.getName()));
			httpRequest.append("\" method=\"post\">");

			for (Entry<String, String> entry : requestMap.entrySet()) {
				httpRequest.append("<input type=\"hidden\" name=\"");
				httpRequest.append(entry.getKey());
				httpRequest.append("\" value=\"");
				httpRequest.append(entry.getValue());
				httpRequest.append("\">");
			}
			httpRequest.append("<input type=\"hidden\" name=\"");
			httpRequest.append(fields.get(FieldType.ACS_RETURN_URL.getName()));
			httpRequest.append("\" value=\"");
			httpRequest.append(returnUrl);
			httpRequest.append("\">");

			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void lyraEnrollRequest(Fields responseMap) {
		try {
			/************* Enrolled card condition starts here ************/
			String acsurl = responseMap.get(FieldType.ACS_URL.getName());

			String request = responseMap.get(FieldType.LYRA_FINAL_REQUEST.getName());

			PrintWriter out = ServletActionContext.getResponse().getWriter();

			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<center><h1>Please do not refresh this page...</h1></center>");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(acsurl);
			httpRequest.append("\" method=\"post\">");

			JSONObject object = new JSONObject(request);
			Iterator<String> keys = object.keys();

			while (keys.hasNext()) {
				String key = keys.next();
				String value = object.getString(key);
				httpRequest.append("<input type=\"hidden\" name=");
				httpRequest.append("\"");
				httpRequest.append(key);
				httpRequest.append("\"");
				httpRequest.append(" ");
				httpRequest.append("value=\"");
				httpRequest.append(value);
				httpRequest.append("\">");
			}

			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");

			out.write(httpRequest.toString());
			/************* Enrolled card condition Ends here ************/
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}



	/*
	 * public void generateCashfreeRequest(Fields fields) { try {
	 *
	 * String requestURL ; JSONObject reqJson = new
	 * JSONObject(fields.get(FieldType.CASHFREE_FINAL_REQUEST.getName()));
	 *
	 * String simulatorFlag =
	 * PropertiesManager.propertiesMap.get("CashfreeSimulator");
	 *
	 * if(simulatorFlag.equalsIgnoreCase("Y")) { requestURL =
	 * PropertiesManager.propertiesMap.get("CASHFREESimulatorUrl"); }else {
	 * requestURL = PropertiesManager.propertiesMap.get("CASHFREESaleUrl"); }
	 * requestURL = PropertiesManager.propertiesMap.get("CASHFREESaleUrl");
	 * PrintWriter out = ServletActionContext.getResponse().getWriter();
	 * StringBuilder httpRequestLogs = new StringBuilder(); StringBuilder
	 * httpRequest = new StringBuilder(); httpRequest.append("<HTML>");
	 * httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >"); httpRequest.
	 * append("<center><h1>Please do not refresh this page...</h1></center>");
	 * httpRequest.append("<form name=\"form1\" action=\"");
	 * httpRequest.append(requestURL); httpRequest.append("\" method=\"post\">");
	 *
	 * for (String keys : reqJson.keySet()) { String row =
	 * " <input type=\"hidden\" name=\"" + keys + "\" value=\"" +
	 * reqJson.get(keys).toString() + "\"/>"; httpRequest.append(row);
	 *
	 * if(keys.equalsIgnoreCase("card_number")) { row =
	 * " <input type=\"hidden\" name=\"" + keys + "\" value=\"" +
	 * Fields.maskify(reqJson.get(keys).toString()) + "\"/>"; }
	 * httpRequestLogs.append(row); }
	 *
	 * httpRequest.append("</form>");
	 * httpRequest.append("<script language=\"JavaScript\">");
	 * httpRequest.append("function OnLoadEvent()");
	 * httpRequest.append("{document.form1.submit();}");
	 * httpRequest.append("</script>"); httpRequest.append("</BODY>");
	 * httpRequest.append("</HTML>");
	 *
	 * logger.info("httpRequestLogs "+httpRequestLogs.toString());
	 * out.write(httpRequest.toString()); } catch (Exception exception) {
	 * logger.error("Exception", exception); } }
	 */

	public void generateCashfreeRequest(Fields fields) {
		StringBuilder httpRequest = new StringBuilder();
		try {
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			String finalRequest = fields.get(FieldType.CASHFREE_FINAL_REQUEST.getName());
			String upiWaitPageReq = StringUtils.join("loadUpiWaitPage?orderId=", fields.get("PG_REF_NUM"),
					"&txnType=UP");
			String finalReq = StringUtils.equalsIgnoreCase(fields.get("MOP_TYPE"), "UP") ? upiWaitPageReq
					: finalRequest;
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<center><h1>Please do not refresh this page...</h1></center>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{window.location= '" + finalReq + "';}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");

			logger.info("Cashfree httpRequest logs :: " + httpRequest.toString());
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void generateDemoRequest(Fields fields) {
		try {

			JSONObject reqJson = new JSONObject(fields.get(FieldType.DEMO_FINAL_REQUEST.getName()));
			String requestURL = PropertiesManager.propertiesMap.get("DemoSaleUrl");

			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequestLogs = new StringBuilder();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<center><h1>Please do not refresh this page...</h1></center>");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(requestURL);
			httpRequest.append("\" method=\"post\">");

			for (String keys : reqJson.keySet()) {
				String row = " <input type=\"hidden\" name=\"" + keys + "\" value=\"" + reqJson.get(keys).toString()
						+ "\"/>";
				httpRequest.append(row);

				if (keys.equalsIgnoreCase("card_number")) {
					row = " <input type=\"hidden\" name=\"" + keys + "\" value=\""
							+ Fields.maskify(reqJson.get(keys).toString()) + "\"/>";
				}
				httpRequestLogs.append(row);
			}

			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");

			logger.info("httpRequestLogs " + httpRequestLogs.toString());
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	/*
	 * public void generateCashfreeRequest(Fields fields) { try {
	 *
	 * String requestURL = fields.get(FieldType.CASHFREE_FINAL_REQUEST.getName());
	 *
	 * PrintWriter out = ServletActionContext.getResponse().getWriter();
	 * //StringBuilder httpRequestLogs = new StringBuilder(); StringBuilder
	 * httpRequest = new StringBuilder(); httpRequest.append("<HTML>");
	 * httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >"); httpRequest.
	 * append("<center><h1>Please do not refresh this page...</h1></center>");
	 * httpRequest.append("<form name=\"form1\" action=\"");
	 * httpRequest.append(requestURL); httpRequest.append("\" method=\"post\">");
	 * httpRequest.append("</form>");
	 * httpRequest.append("<script language=\"JavaScript\">");
	 * httpRequest.append("function OnLoadEvent()");
	 * httpRequest.append("{document.form1.submit();}");
	 * httpRequest.append("</script>"); httpRequest.append("</BODY>");
	 * httpRequest.append("</HTML>");
	 *
	 * logger.info("Cashfree httpRequest logs :: " + httpRequest.toString());
	 * out.write(httpRequest.toString()); } catch (Exception exception) {
	 * logger.error("Exception", exception); } }
	 */

	public void generateEasebuzzRequest(Fields fields) {
		try {

			logger.info("Request Received for Easebuzz :: " + fields.getFieldsAsString());
			String requestURL = PropertiesManager.propertiesMap.get("EASEBUZZSaleUrl");
			logger.info("requestURL >>>>>>>>> " + requestURL);
			JSONObject reqJson = new JSONObject(fields.get(FieldType.EASEBUZZ_FINAL_REQUEST.getName()));
			logger.info("reqJson >>>>>> " + reqJson.toString());

			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<center><h1>Please do not refresh this page...</h1></center>");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(requestURL);
			httpRequest.append("\" method=\"post\">");

			for (String keys : reqJson.keySet()) {
				String row = " <input type=\"hidden\" name=\"" + keys + "\" value=\"" + reqJson.get(keys).toString()
						+ "\"/>";
				httpRequest.append(row);
			}

			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");

			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void generateAgreepayRequest(Fields fields) {
		try {
			logger.info("Request Received for Agreepay :: " + fields.getFieldsAsString());
			String requestURL = PropertiesManager.propertiesMap.get("AGREEPAYSaleUrl");
			JSONObject reqJson = new JSONObject(fields.get(FieldType.AGREEPAY_FINAL_REQUEST.getName()));

			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<center><h1>Please do not refresh this page...</h1></center>");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(requestURL);
			httpRequest.append("\" method=\"post\">");

			for (String keys : reqJson.keySet()) {
				String row = " <input type=\"hidden\" name=\"" + keys + "\" value=\"" + reqJson.get(keys).toString()
						+ "\"/>";
				httpRequest.append(row);
			}

			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void generatePinelabsRequest(Fields fields) {
		try {
			String request = fields.get(FieldType.PINELABS_FINAL_REQUEST.getName());
			logger.info("Request Received for Pinelabs :: " + request);
			HttpServletResponse response = ServletActionContext.getResponse();
			response.sendRedirect(request);
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void generateidfcBankNBRequest(Fields fields) {
		try {
			String requestURL = PropertiesManager.propertiesMap.get("IdfcSaleUrl");
			String request = fields.get(FieldType.IDFC_FINAL_REQUEST.getName());
			String MId = fields.get(FieldType.MERCHANT_ID.getName());

			PrintWriter out = ServletActionContext.getResponse().getWriter();

			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(requestURL);
			httpRequest.append("\" method=\"post\">");

			httpRequest.append("<input type=\"hidden\" name=\"isEncReq\" value=\"");
			httpRequest.append("Y");
			httpRequest.append("\">");

			httpRequest.append("<input type=\"hidden\" name=\"mid\" value=\"");
			httpRequest.append(MId);
			httpRequest.append("\">");
			httpRequest.append("<input type=\"hidden\" name=\"encParams\" value=\"");
			httpRequest.append(request);
			httpRequest.append("\">");
			httpRequest.append("<input type=\"hidden\" name=\"serviceName\" value=\"");
			httpRequest.append("payment");
			httpRequest.append("\">");

			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");
			logger.info("http request  idfc" + httpRequest);
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void generateJammuandKashmirRequest(Fields fields) {
		try {
			String paymentString = fields.get(FieldType.JAMMU_AND_KASHMIR_FINAL_REQUEST.getName());

			String requestURL = PropertiesManager.propertiesMap.get("jammuAndKishmirSaleUrl");
			requestURL = requestURL.concat(paymentString);
			logger.info("url request J&K" + requestURL);
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(requestURL);
			httpRequest.append("\" method=\"post\">");

			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception while posting mobikwik payment request ", exception);
		}
	}

	public void generateYesBankNBRequest(Fields fields) {
		try {
			String requestURL = PropertiesManager.propertiesMap.get("YESBANK_SALE_RETURN_URL");
			logger.info(fields.get(FieldType.YESBANKNB_FINAL_REQUEST.getName())
					+ "======== yesbankNBSaleUrl requestURL " + requestURL);
			String reqJson = fields.get(FieldType.YESBANKNB_FINAL_REQUEST.getName());

			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<head> <meta charset=\"UTF-8\"> </head>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<center><h1>Please do not refresh this page...</h1></center>");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(requestURL);
			httpRequest.append("\" method=\"post\">");
			httpRequest.append(" <input type=\"hidden\" name=\"PID\"  value=\""
					+ fields.get(FieldType.MERCHANT_ID.getName()) + "\"/>");
			httpRequest.append(" <input type=\"hidden\" name=\"encdata\"  value=\"" + reqJson + "\"/>");
			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");

			System.out.println("httpRequest.toString()---->" + httpRequest.toString());
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("YESBANKNB generateYesBankNBRequest Exception", exception);
		}
	}

	public void generatePaymentEdgeRequest(Fields fields) {
		try {
			String requestURL = PropertiesManager.propertiesMap.get("PAYMENTEDGESaleUrl");
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(requestURL);
			httpRequest.append("\" method=\"post\">");

			httpRequest.append("<input type=\"hidden\" name=\"");
			httpRequest.append("ENCDATA");
			httpRequest.append("\" value=\"");
			httpRequest.append(fields.get(FieldType.PAYMENTEDGE_FINAL_REQUEST.getName()));
			httpRequest.append("\">");

			httpRequest.append("<input type=\"hidden\" name=\"");
			httpRequest.append("PAY_ID");
			httpRequest.append("\" value=\"");
			httpRequest.append(fields.get(FieldType.MERCHANT_ID.getName()));
			httpRequest.append("\">");


			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");

			logger.info("Final Request to PayTen >> " + httpRequest.toString());
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}

	public void generatePay10Request(Fields fields) {
		try {
			String requestURL = PropertiesManager.propertiesMap.get("PAY10SaleUrl");
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();
			httpRequest.append("<HTML>");
			httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
			httpRequest.append("<form name=\"form1\" action=\"");
			httpRequest.append(requestURL);
			httpRequest.append("\" method=\"post\">");

			httpRequest.append("<input type=\"hidden\" name=\"");
			httpRequest.append("ENCDATA");
			httpRequest.append("\" value=\"");
			httpRequest.append(fields.get(FieldType.PAY10_FINAL_REQUEST.getName()));
			httpRequest.append("\">");
			
			httpRequest.append("<input type=\"hidden\" name=\"");
			httpRequest.append("PAY_ID");
			httpRequest.append("\" value=\"");
			httpRequest.append(fields.get(FieldType.MERCHANT_ID.getName()));
			httpRequest.append("\">");
			
			
			httpRequest.append("</form>");
			httpRequest.append("<script language=\"JavaScript\">");
			httpRequest.append("function OnLoadEvent()");
			httpRequest.append("{document.form1.submit();}");
			httpRequest.append("</script>");
			httpRequest.append("</BODY>");
			httpRequest.append("</HTML>");
			
			logger.info("Final Request to PayTen >> " + httpRequest.toString());
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}


	public void generateNFTRequest(Fields fields) {
		try {
			String transactionUrl = PropertiesManager.propertiesMap.get("TFPSaleCaptureURL");
			String tfpCheckoutCssUrl = PropertiesManager.propertiesMap.get("TFP_CHECKOUT_CSS_URL");
			String tfpCheckoutJsUrl = PropertiesManager.propertiesMap.get("TFP_CHECKOUT_JS_URL");
			String tfpPopupCheckoutClassName = PropertiesManager.propertiesMap.get("TFP_POPUP_CHECKOUT_CLASS_NAME");
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();

			httpRequest.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>")
                    .append("<html xmlns='http://www.w3.org/1999/xhtml'>")
                    .append("<head>")
                    .append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8'>")
                    .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                    .append("<title>Secure Payment</title>")
                    .append("<link rel='stylesheet' href='").append(tfpCheckoutCssUrl).append("'>")
                    .append("<script src='").append(tfpCheckoutJsUrl).append("'></script>")
                    .append("<script>")
                    .append("function submitForm() {")
                    .append("document.form1.submit();}")
                    .append("window.onload = function(){")
                    .append("const loadingTextElements = document.querySelectorAll('.loading-text');")
                    .append("loadingTextElements.forEach(function(element) {")
                    .append("element.style.display = 'flex';});")
                    .append("const closeBtnElement = document.getElementById('checkout-close_btn');")
                    .append("if(closeBtnElement){")
                    .append("closeBtnElement.style.display = 'none'}")
                    .append("submitForm();};")
                    .append("</script>")
                    .append("</head>")
					.append("<body class='").append(tfpPopupCheckoutClassName).append("'>")
					.append("<form name='form1' action='").append(transactionUrl).append("' method='post' onsubmit='return checkoutSubmitHandler(this)' target = 'checkout-iframe' >")
					.append("<input type='hidden' name='APP_ID' value='").append(fields.get(FieldType.ADF3.getName())).append("' >")
					.append("<input type='hidden' name='TXN_ID' value='").append(fields.get(FieldType.RRN.getName())).append("' >")
					.append("<input type='hidden' name='HASH' value='").append(fields.get(FieldType.ACQ_ID.getName())).append("' >")
					.append("</form>")
					.append("</body>")
					.append("</html>");

			logger.info("Final Request to TFP >> {}", httpRequest);
			out.write(httpRequest.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
	}
}
