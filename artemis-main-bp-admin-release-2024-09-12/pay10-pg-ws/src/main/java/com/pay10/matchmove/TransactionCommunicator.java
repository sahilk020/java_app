package com.pay10.matchmove;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;


@Service("matchMoveTransactionCommunicator")
public class TransactionCommunicator {
	
	
private static Logger logger = LoggerFactory.getLogger(TransactionCommunicator.class.getName());
	
	public void updateSaleResponse(Fields fields, String request) {
        logger.info("Request Parameter for MatchMove: " + request);
		fields.put(FieldType.MATCH_MOVE_FINAL_REQUEST.getName(), request);
		fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
	}
	
	@SuppressWarnings("incomplete-switch")
	public String getResponse(String request, Fields fields) throws SystemException {
		String response = "";
		String hostUrl = "";

		TransactionType transactionType = TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()));
			switch (transactionType) {
			case SALE:
				response = initiateMatchMoverstResponse(request, fields);
				break;
			case AUTHORISE:
				break;
			case ENROLL:
				break;
			case CAPTURE:
				break;
			case REFUND:
				response = executeRefund(request, fields);
				
				break;
			case STATUS:
//				hostUrl = PropertiesManager.propertiesMap.get(Constant.STATUS_ENQ_REQUEST_URL);
				break;
			}
				
			
				return response;
			}

	
	public String initiateMatchMoverstResponse(String request,Fields fields) throws SystemException {
		logger.info("inside initiateMatchMoverstResponse matchmvoe matchMoveTransactionCommunicator class " +request);
		String matchMoveUrl = PropertiesManager.propertiesMap.get("MatchMoveUrl");
		String merchantId=fields.get(FieldType.MERCHANT_ID.getName());
		StringBuilder requestURL=new StringBuilder();
		requestURL.append(matchMoveUrl);
		requestURL.append(Constant.SLASH);
		requestURL.append(Constant.MERCHANT);
		requestURL.append(Constant.SLASH);
		requestURL.append(merchantId);
		requestURL.append(Constant.SLASH);
		requestURL.append(Constant.CHECKOUT);
	   

		String response = "";
		 try
	        {
			URL url = new URL(requestURL.toString());
			logger.info("inside inital matchmove request: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = " + fields.get(FieldType.TXN_ID.getName()) + " " + request);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setUseCaches(false);
			conn.setDoInput(true);
			conn.setDoOutput(true);

			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
         	OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(request);
			wr.flush();
			InputStream is=conn.getInputStream();
			
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is));
			String decodedString;
		
			while ((decodedString = bufferedreader.readLine()) != null) {
				response = response + decodedString;
			}
	      	bufferedreader.close();
			logger.info("Response mesage from matchmove: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = " + fields.get(FieldType.TXN_ID.getName()) + " " + response);
	        return response;
			
	        }
	        catch (Exception ex) {
	        	fields.put(FieldType.STATUS.getName(), StatusType.ERROR.getName());
				logger.error("Network Exception with matchmove", ex);
				throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, ex,
						"Network Exception with matchmove " + requestURL.toString());
	        
				// TODO: handle exception
			}
	
	
	}
	
	
	public String executeRefund(String request, Fields fields) throws SystemException {
		
		logger.info("inside matchmove refund request  TransactionCommunicator " +request);
		String matchMoveUrl = PropertiesManager.propertiesMap.get("MatchMoveUrl");
		String merchantId=fields.get(FieldType.MERCHANT_ID.getName());
		StringBuilder requestURL=new StringBuilder();
		requestURL.append(matchMoveUrl);
		requestURL.append(Constant.SLASH);
		requestURL.append(Constant.MERCHANT);
		requestURL.append(Constant.SLASH);
		requestURL.append(merchantId);
		requestURL.append(Constant.SLASH);
		requestURL.append(Constant.CHECKOUT);
		requestURL.append(Constant.SLASH);
		requestURL.append(Constant.TRANSACTIONS);

		String response = "";
		HttpURLConnection conn = null;
		 try
	        {
			    URL url = new URL(requestURL.toString());
				logger.info("inside matchmove refund request: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = " + fields.get(FieldType.TXN_ID.getName()) + " " + request);
			    conn = (HttpURLConnection) url.openConnection();

			    conn.setRequestMethod("DELETE");
			   
			    conn.setRequestProperty("Content-Type", "application/json");
			 

			    conn.setUseCaches(false);
			    conn.setDoOutput(true);
			    conn.setDoInput(true);

				// Send request
				OutputStream outputStream = conn.getOutputStream();
				DataOutputStream wr = new DataOutputStream(outputStream);
				wr.writeBytes(request.toString());
				wr.close();

				// Get Response
				InputStream is = conn.getInputStream();
				BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is));
				String decodedString;
			
				while ((decodedString = bufferedreader.readLine()) != null) {
					response = response + decodedString;
				}
		      	bufferedreader.close();
		      	logger.info("matchmove refund response: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = " + fields.get(FieldType.TXN_ID.getName()) + " " + response);
		        return response;

			} catch (Exception ex) {
				fields.put(FieldType.STATUS.getName(), StatusType.ERROR.getName());
				logger.error("Network Exception with matchmove", ex);
				throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, ex,
						"Network Exception with matchmove " + requestURL.toString());
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}
		   
		}
	
	
}
