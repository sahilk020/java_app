package com.pay10.scheduler;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pay10.commons.api.Hasher;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.entity.PGWebHookPostConfigURL;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.repository.PGWebHookPost;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.scheduler.commons.BulkRefundProvider;
import com.pay10.scheduler.commons.ConfigurationProvider;
import com.pay10.scheduler.commons.IrctcRefundProvider;
import com.pay10.scheduler.commons.TransactionDataProvider;
import com.pay10.scheduler.core.ServiceControllerProvider;
import com.pay10.scheduler.jobs.AcquirerMasterSwitch;

@Service
public class TransactionStatusEnquiryService {

	  @Autowired
	    private AcquirerMasterSwitch acquirerMasterSwitch;
	  
    @Autowired
    private ConfigurationProvider configurationProvider;

    @Autowired
    private TransactionDataProvider transactionDataProvider;

    @Autowired
    private ServiceControllerProvider serviceControllerProvider;
    
   
    
    @Autowired  BulkRefundProvider bulkRefundProvider;
    
  //For chargebackBulkUploadUrl-
  	private String chargebackApiUrl;
  	//private String bsesMailApiUrl;

   // private List<Document> transactionEnquirySet = new ArrayList<Document>();
 
	private String commissionCalculateApi;
	private String monthlyPayoutApi;
	private String quaterlyPayoutApi;
	private String chargebackAutoCloseUrl;
	

    private static final Logger logger = LoggerFactory.getLogger(TransactionStatusEnquiryService.class);

	private static final ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	private FieldsDao fieldsDao;
	
	@Autowired
	IrctcRefundProvider irctcRefundProvider ;
	
	@Autowired
	PGWebHookPost pgWebHookPost;
	
   @Scheduled(fixedDelay = 60*1000) //every minute
    private void fetchAcquirerData() {

        try {

            logger.info("[TransactionStatusEnquiryService] Started fetching transaction data");
            Set<String> pgRefSet = transactionDataProvider.fetchTransactionData();
            String bankStatusEnquiryUrl = configurationProvider.getEnquiryApiUrl();

            for (String pgRef : pgRefSet) {

                logger.info("Sending status enquiry request , URL = " + bankStatusEnquiryUrl + " 	pgRef == " + pgRef);

                JSONObject data = new JSONObject();
                data.put(FieldType.PG_REF_NUM.getName(), pgRef);

                serviceControllerProvider.bankStatusEnquiry(data, bankStatusEnquiryUrl);
            }

        }

        catch (Exception e) {
            logger.error("Exception in bank status enquiry from scheduler", e);
        }

    }
   
   
   
	/*
	 * @Scheduled(fixedDelay = 30 * 60 * 1000) //every minute private void
	 * payuFetchData() {
	 * 
	 * try {
	 * 
	 * logger.
	 * info("[TransactionStatusEnquiryService] Started fetching Payu Refund  data");
	 * Set<String> refundOrderIdSet = transactionDataProvider.FetchPayuRefundData();
	 * String bankStatusEnquiryUrl =
	 * configurationProvider.getPayuStatusEnquiryApiUrl();
	 * 
	 * for (String orderId : refundOrderIdSet) {
	 * 
	 * logger.info("Sending Payu status refund enquiry request , URL = " +
	 * bankStatusEnquiryUrl + " 	Refund Order Id == " + orderId);
	 * 
	 * JSONObject data = new JSONObject();
	 * data.put(FieldType.REFUND_ORDER_ID.getName(), orderId);
	 * 
	 * serviceControllerProvider.bankStatusEnquiry(data, bankStatusEnquiryUrl); }
	 * 
	 * }
	 * 
	 * catch (Exception e) {
	 * logger.error("Exception in bank status enquiry from scheduler", e); }
	 * 
	 * }
	 */
  
   
   
   @Scheduled(fixedDelay = 1200*1000) //add by abhishek for mongodb alert
   private void schadularMessageAlert() {

       try {

           logger.info("[TransactionStatusEnquiryService] Started fetching for mongodb alert data");
           transactionDataProvider.fetchtransactionForAlert();
           

       }

       catch (Exception e) {
           logger.error("Exception in mongodb alert  from scheduler", e);
       }

   }
   
    
//    @Scheduled(cron = "0 0 0 * * *") // every day at 12:00 A.M
	@Scheduled(cron = "0 */5 * * * *")
//   @Scheduled(fixedDelay = 60*1000)
	private void fetchresellerdata() {

		try {
			String data = null;
			commissionCalculateApi= configurationProvider.getCommissionSchedulerApiUrl();
			logger.info("commissionCalculateApi...={}",commissionCalculateApi);
			ServiceControllerProvider.resellerseculdra(data, commissionCalculateApi);

		} catch (Exception e) {
			logger.error("Exception in bank status enquiry from scheduler", e);
		}

	}
    
   
//    @Scheduled(cron = "0 0 0 1 * ?")          // On the first day of every month
	@Scheduled(cron = "0 */10 * * * *")          // Temporary for
	private void fetchresellerpayout() {

		try {
			String data = null;
			monthlyPayoutApi=configurationProvider.getResellerMonthlyPayoutSchedulerApiUrl();
			logger.info("monthlyPayoutApi...={}",monthlyPayoutApi);
			ServiceControllerProvider.resellerseculdra(data, monthlyPayoutApi);

		} catch (Exception e) {
			logger.error("Exception in bank status enquiry from scheduler", e);
		}

	}

	 
	@Scheduled(cron = "0 0 0 1 */3 *")                //quarterly basis  
	private void fetchresellerpayoutmonth() {
		Calendar data = Calendar.getInstance();
		data.setTime(new Date());
		data.set(Calendar.MONTH, Integer.parseInt(new SimpleDateFormat("M").format(data.getTime())) - 2);
		data.set(Calendar.DAY_OF_MONTH, data.getActualMaximum(Calendar.DAY_OF_MONTH));
		String endDate = new SimpleDateFormat("MM").format(data.getTime());
		logger.info(endDate);

		if (endDate.equalsIgnoreCase("04") || endDate.equalsIgnoreCase("07") || endDate.equalsIgnoreCase("10")
				|| endDate.equalsIgnoreCase("01")) {

		try {

			String data1 = null;
			quaterlyPayoutApi=configurationProvider.getResellerQuaterlyPayoutSchedulerApiUrl();
			logger.info("quaterlyPayoutApi...={}",quaterlyPayoutApi);
			ServiceControllerProvider.resellerseculdra(data1, quaterlyPayoutApi);

		 } catch (Exception e) {
			logger.error("Exception in bank status enquiry from scheduler", e);
		 }
		} else {
			logger.info("only quaterly month will be run");
		}
	}
	
	@Scheduled(cron= "0 0 1 * * *")
	public void fetchPendingTxnDataFirstTime() {

		try {

			logger.info("PendingTransactionStatusEnquiryService");
			List<Document> pendingDocs = transactionDataProvider.fetchPendingTransactionData();
			logger.info(" Pending transaction data....={}", pendingDocs);
			String bankStatusEnquiryUrl = configurationProvider.getEnquiryApiUrl();
			logger.info("bankStatusEnquiryUrl= {}",bankStatusEnquiryUrl);

			pendingDocs.forEach(doc -> {
				String pgRefNum = doc.getString(FieldType.PG_REF_NUM.getName());
				String acquirer = doc.getString(FieldType.ACQUIRER_TYPE.getName());
				
				logger.info("pgRefNum={}, acquirer={}",pgRefNum,acquirer);
				if (StringUtils.isBlank(acquirer) || StringUtils.equals(pgRefNum, "0")) {
					Map<String, String> data = mapper.convertValue(doc, new TypeReference<Map<String, String>>() {
					});
					Fields fields = new Fields(data);
					fields.put(FieldType.TXN_ID.getName(), TransactionManager.getNewTransactionId());
					fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
					fields.put(FieldType.PG_TXN_MESSAGE.getName(), "No Such Transaction found");

					fields.put(FieldType.RESPONSE_MESSAGE.getName(), "No Such Transaction found");
					fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.NO_SUCH_TRANSACTION.getCode());
					try {
						fieldsDao.insertTransaction(fields);
					} catch (SystemException e) {
						logger.error("fetchPendingTxnData:: failed to save transaction. fields={}",
								fields.getFieldsAsString(), e);
					}
					return;
				}
				logger.info("fetchPendingTxnData:: Initialized. pgRef={}, acquirer={}", pgRefNum, acquirer);
				JSONObject data = new JSONObject();
				data.put(FieldType.PG_REF_NUM.getName(), pgRefNum);
				
				try {
					serviceControllerProvider.bankPendingTxnStatusEnquiry(data, bankStatusEnquiryUrl);
				} catch (Exception ex) {
					logger.error("fetchPendingTxnData:: failed. pgRefNo={}", pgRefNum, ex);
				}
			});
		} catch (Exception e) {
			logger.error("Exception in bank status enquiry from scheduler", e);
		}

	}
	
	@Scheduled(cron= "0 0 2 * * *")
	public void fetchPendingTxnDataSecondTime() {

		try {

			logger.info("PendingTransactionStatusEnquiryService");
			List<Document> pendingDocs = transactionDataProvider.fetchPendingTransactionData();
			logger.info(" Pending transaction data....={}", pendingDocs);
			String bankStatusEnquiryUrl = configurationProvider.getEnquiryApiUrl();
			logger.info("bankStatusEnquiryUrl= {}",bankStatusEnquiryUrl);

			pendingDocs.forEach(doc -> {
				String pgRefNum = doc.getString(FieldType.PG_REF_NUM.getName());
				String acquirer = doc.getString(FieldType.ACQUIRER_TYPE.getName());
				logger.info("pgRefNum={}, acquirer={}",pgRefNum,acquirer);
				if (StringUtils.isBlank(acquirer) || StringUtils.equals(pgRefNum, "0")) {
					Map<String, String> data = mapper.convertValue(doc, new TypeReference<Map<String, String>>() {
					});
					Fields fields = new Fields(data);
					fields.put(FieldType.TXN_ID.getName(), TransactionManager.getNewTransactionId());
					fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
					fields.put(FieldType.RESPONSE_MESSAGE.getName(), "No Such Transaction found");
					fields.put(FieldType.PG_TXN_MESSAGE.getName(), "No Such Transaction found");

					fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.NO_SUCH_TRANSACTION.getCode());
					try {
						fieldsDao.insertTransaction(fields);
					} catch (SystemException e) {
						logger.error("fetchPendingTxnData:: failed to save transaction. fields={}",
								fields.getFieldsAsString(), e);
					}
					return;
				}
				logger.info("fetchPendingTxnData:: Initialized. pgRef={}, acquirer={}", pgRefNum, acquirer);
				JSONObject data = new JSONObject();
				data.put(FieldType.PG_REF_NUM.getName(), pgRefNum);
				
				try {
					serviceControllerProvider.bankPendingTxnStatusEnquiry(data, bankStatusEnquiryUrl);
				} catch (Exception ex) {
					logger.error("fetchPendingTxnData:: failed. pgRefNo={}", pgRefNum, ex);
				}
			});
		} catch (Exception e) {
			logger.error("Exception in bank status enquiry from scheduler", e);
		}

	}
	
	@Scheduled(cron= "0 0 3 * * *")
	public void fetchPendingTxnDataThirdTime() {

		try {

			logger.info("PendingTransactionStatusEnquiryService");
			List<Document> pendingDocs = transactionDataProvider.fetchPendingTransactionData();
			logger.info(" Pending transaction data....={}", pendingDocs);
			String bankStatusEnquiryUrl = configurationProvider.getEnquiryApiUrl();
			logger.info("bankStatusEnquiryUrl= {}",bankStatusEnquiryUrl);

			pendingDocs.forEach(doc -> {
				String pgRefNum = doc.getString(FieldType.PG_REF_NUM.getName());
				String acquirer = doc.getString(FieldType.ACQUIRER_TYPE.getName());
				String oid= doc.getString(FieldType.OID.getName());
				logger.info("pgRefNum={}, acquirer={} ,oid={}",pgRefNum,acquirer,oid);
				if (StringUtils.isBlank(acquirer) || StringUtils.equals(pgRefNum, "0")) {
					Map<String, String> data = mapper.convertValue(doc, new TypeReference<Map<String, String>>() {
					});
					Fields fields = new Fields(data);
					fields.put("UPDATED_BY_SCHEDULER", "Y");
					fields.put(FieldType.TXN_ID.getName(), TransactionManager.getNewTransactionId());
					fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
					fields.put(FieldType.PG_TXN_MESSAGE.getName(), "No Such Transaction found");

					fields.put(FieldType.RESPONSE_MESSAGE.getName(), "No Such Transaction found");
					fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.NO_SUCH_TRANSACTION.getCode());
					try {
						fieldsDao.insertTransaction(fields);
					} catch (SystemException e) {
						logger.error("fetchPendingTxnData:: failed to save transaction. fields={}",
								fields.getFieldsAsString(), e);
					}
					return;
				}
				logger.info("fetchPendingTxnData:: Initialized. pgRef={}, acquirer={}", pgRefNum, acquirer);
				JSONObject data = new JSONObject();
				data.put(FieldType.PG_REF_NUM.getName(), pgRefNum);
				
				try {
					serviceControllerProvider.bankPendingTxnStatusEnquiryThirdTime(data, bankStatusEnquiryUrl,oid);
				} catch (Exception ex) {
					logger.error("fetchPendingTxnData:: failed. pgRefNo={}", pgRefNum, ex);
				}
			});
		} catch (Exception e) {
			logger.error("Exception in bank status enquiry from scheduler", e);
		}

	}
	
	/*
	 * @Scheduled(fixedDelay = 60 * 60 * 1000) // add by abhishek for mongodb alert
	 * private void bulkRefundTrnsaction() { try {
	 * 
	 * logger.info("get Schdular for Bulk refundtrnsactionb");
	 * bulkRefundProvider.getBulkRefundTrnsaction();
	 * 
	 * }
	 * 
	 * catch (Exception e) {
	 * logger.error("Exception in mongodb alert  from scheduler", e); }
	 * 
	 * }
	 */
//	@Scheduled(cron= "0 0 * * * *")
	@Scheduled(cron= "0 45 8 * * *") // every 15 minutes
	private void chargebackBulkEmail() {
		try {

			chargebackApiUrl=configurationProvider.getChargebackApiUrl();
			logger.info("chargebackApiUrl...={}",chargebackApiUrl);
			serviceControllerProvider.chargebackBulkUpload(chargebackApiUrl);

		}

		catch (Exception e) {
			logger.error("Exception in chargebackBulkEmail from scheduler", e);
		}

	}
	
//	@Scheduled(cron= "0 45 8 * * *")
	/*
	 * @Scheduled(fixedDelay = 15 * 60 * 1000) // every 15 minutes private void
	 * bsesMail() { try {
	 * 
	 * bsesMailApiUrl=configurationProvider.getBsesEmailApiUrl();
	 * logger.info("bsesMailApiUrl...={}",bsesMailApiUrl);
	 * serviceControllerProvider.bsesMail(bsesMailApiUrl);
	 * 
	 * }
	 * 
	 * catch (Exception e) {
	 * logger.error("Exception in chargebackBulkEmail from scheduler", e); }
	 * 
	 * }
	 */
	
	
	/*
	 * @Scheduled(fixedDelay = 120 * 1000) // every minute private void
	 * getIrctcRefund() {
	 * 
	 * try { logger.info("scheduler start for irctc refund");
	 * irctcRefundProvider.getBulkRefundTrnsaction();
	 * 
	 * }
	 * 
	 * catch (Exception e) { logger.error("exception in getIrctcRefund ", e); }
	 * 
	 * }
	 */
	
	
	 @Scheduled(fixedDelay =  900 * 1000) //every minute
	    private void getAcquirerMasterSwitch() {

	        try {

	        	acquirerMasterSwitch.getAcquirerMasterSwitch();
	        }

	        catch (Exception e) {
	            logger.error("exception in getAcquirerMasterSwitch ", e);
	        }

	    }
//	@Scheduled(cron= "0 30 1 * * *")
@Scheduled(fixedDelay = 15 * 60 * 1000) // every 15 minutes
	private void chargebackAutoClose() {
		try {

			chargebackAutoCloseUrl = configurationProvider.getChargebackAutoCloseUrl();
			logger.info("chargebackAutoCloseUrl...={}",chargebackAutoCloseUrl);
			serviceControllerProvider.chargebackAutoClose(chargebackAutoCloseUrl);

		}

		catch (Exception e) {
			logger.error("Exception in chargebackAutoClose from scheduler", e);
		}

	}
	
	
	 @Scheduled(fixedDelay = 60*1000) //every 10 minute
	   private void fetchPayoutData() {

	       try {

	           logger.info("########################################################333333333333333333");

	           logger.info("[TransactionStatusEnquiryService] Started fetching payout data");
	           Set<String> orderIdSet = transactionDataProvider.fetchTransactionPayoutData();
	           String salePayoutUrl = configurationProvider.getSalePayoutUrl();
logger.info("Order Id size"+orderIdSet.size());
	           for (String orderId : orderIdSet) {

	               logger.info("Sending Payout request , URL = " + salePayoutUrl + " 	orderId== " + orderId);
	               Fields fields = fieldsDao.getFieldsByorderIdPayout(orderId);
	             
	               serviceControllerProvider.getApiCall(fields, salePayoutUrl);
	           }

	       }

	       catch (Exception e) {
	           logger.error("Exception in bank status enquiry from scheduler", e);
	       }

	   }
	 
	 @Scheduled(fixedDelay = 60*1000) //every minute
	   private void fetchAcquirerDataPayout() {

	       try {

	           logger.info("[TransactionStatusEnquiryService] Started fetching transaction data for Payout");
	           Set<String> pgRefSet = transactionDataProvider.fetchTransactionDataPayout();
	           String bankStatusEnquiryUrl = configurationProvider.getPayuStatusEnquiryApiUrl();

	           for (String pgRef : pgRefSet) {

	               logger.info("Sending status Payout enquiry request , URL = " + bankStatusEnquiryUrl + " 	pgRef == " + pgRef);

	               JSONObject data = new JSONObject();
	               data.put(FieldType.PG_REF_NUM.getName(), pgRef);

	               serviceControllerProvider.bankStatusEnquiry(data, bankStatusEnquiryUrl);
	           }

	       }

	       catch (Exception e) {
	           logger.error("Exception in bank status enquiry from scheduler", e);
	       }

	   }
	   
	  
	   
	   
		@Scheduled(fixedDelay = 60 * 1000) // every 10 minute
		private void fetchPayoutDataStatusCheck() {

			try {

				logger.info("########################################################333333333333333333");

				logger.info("[fetchPayoutDataStatusCheck] Started fetching payout data");
				Set<String> orderIdSet = transactionDataProvider.fetchTransactionPayoutDataStatusCheck();
				String salePayoutUrl = configurationProvider.getEnquiryApiUrlPayout();
				logger.info("Order Id size" + orderIdSet.size());
				for (String orderId : orderIdSet) {

					logger.info("Sending Payout request , URL = " + salePayoutUrl + " 	orderId== " + orderId);
					Fields fields = new Fields();
					fields.put(FieldType.PG_REF_NUM.getName(), orderId);
					serviceControllerProvider.getApiCall(fields, salePayoutUrl);
				}

			}

			catch (Exception e) {
				logger.error("Exception in bank status enquiry from scheduler", e);
			}

		}
	 
//	@Scheduled(cron = "*/15 * * * * *")
//	private void payIn_WebHookPostScheduler() {
//		logger.info("++++++++++++++++++++++++ Start payIn_WebHookPostScheduler +++++++++++++++++++++++++++++++++");
//		String webhookFailedCnt = configurationProvider.getWebhookFailedCount();
//		String webhookType = "PAYIN";
//		List<Document> documents = pgWebHookPost.fetchPGWebHookDetails(webhookFailedCnt, webhookType);
//		logger.info("payIn_WebHookPostScheduler, Document size for posting webhook response to merchant, Documents Size={}", documents.size());
//		documents.stream().forEach(doc -> {
//			PGWebHookPostConfigURL pgWebHookPostConfigURL = pgWebHookPost
//					.fetchPGWebHookPostConfigURLByPayId(doc.getString(FieldType.PAY_ID.getName()), webhookType);
//			try {
//
//				String configurationURL = pgWebHookPostConfigURL.getWebhookUrl();
//				Fields fieldObj = wehbookResponse(doc, webhookType);
//
//				fieldObj.put(FieldType.HASH.getName(), Hasher.getHash(fieldObj));
//				boolean result = httpsPosting(fieldObj, configurationURL);
//				if (result) {
//					pgWebHookPost.updateFlagAndFailedCount(doc, result, webhookType);
//				} else {
//					pgWebHookPost.updateFlagAndFailedCount(doc, result, webhookType);
//				}
//			} catch (Exception e) {
//				logger.error("payIn_WebHookPostScheduler, ERROR OCCURED, pgWebHookPostScheduler, Exception={}", e);
//				pgWebHookPost.updateFlagAndFailedCount(doc, false, webhookType);
//				return;
//			}
//		});
//
//		logger.info("+++++++++++++++++++++++++++ End payIn_WebHookPostScheduler +++++++++++++++++++++++++++++++++++++++++");
//
//	}
//
//	@Scheduled(cron = "*/15 * * * * *")
//	private void payOut_WebHookPostScheduler() {
//		logger.info("++++++++++++++++++++++++ Start payOut_WebHookPostScheduler +++++++++++++++++++++++++++++++++");
//		String webhookFailedCnt = configurationProvider.getWebhookFailedCount();
//		String webhookType = "PAYOUT";
//		List<Document> documents = pgWebHookPost.fetchPGWebHookDetails(webhookFailedCnt, webhookType);
//		logger.info("payOut_WebHookPostScheduler, Document size for posting webhook response to merchant, Documents Size={}", documents.size());
//		documents.stream().forEach(doc -> {
//			PGWebHookPostConfigURL pgWebHookPostConfigURL = pgWebHookPost
//					.fetchPGWebHookPostConfigURLByPayId(doc.getString(FieldType.PAY_ID.getName()), webhookType);
//			try {
//
//				String configurationURL = pgWebHookPostConfigURL.getWebhookUrl();
//				Fields fieldObj = wehbookResponse(doc, webhookType);
//				resolveStatus(fieldObj);
//				fieldObj.put(FieldType.HASH.getName(), Hasher.getHash(fieldObj));
//				boolean result = httpsPosting(fieldObj, configurationURL);
//				if (result) {
//					pgWebHookPost.updateFlagAndFailedCount(doc, result, webhookType);
//				} else {
//					pgWebHookPost.updateFlagAndFailedCount(doc, result, webhookType);
//				}
//			} catch (Exception e) {
//				logger.error("payOut_WebHookPostScheduler, ERROR OCCURED, pgWebHookPostScheduler, Exception={}", e);
//				pgWebHookPost.updateFlagAndFailedCount(doc, false, webhookType);
//				return;
//			}
//		});
//
//		logger.info("+++++++++++++++++++++++++++ End payOut_WebHookPostScheduler +++++++++++++++++++++++++++++++++++++++++");
//
//	}
//
//	private Fields wehbookResponse(Document doc, String webhookType) {
//		Fields fields = new Fields();
//
//		String webhookResponseFields;
//		if (webhookType.equalsIgnoreCase("PAYIN")) {
//			webhookResponseFields = configurationProvider.getWebhookPayInResponseFields();
//		} else {
//			webhookResponseFields = configurationProvider.getWebhookPayOutResponseFields();
//		}
//
//		String webhookResponseFieldsArray[] = webhookResponseFields.split(",");
//
//		if (null != doc) {
//			for (int j = 0; j < doc.size(); j++) {
//				for (String columnName : webhookResponseFieldsArray) {
//					if (doc.get(columnName) != null) {
//						fields.put(columnName, doc.get(columnName).toString());
//					} else {
//
//					}
//
//				}
//			}
//		}
//		return fields;
//	}
//
//	public boolean httpsPosting(Fields fields, String hostUrl) throws SystemException {
//		HttpsURLConnection connection = null;
//		ObjectMapper objectMapper = new ObjectMapper();
//
//		try {
//			String json = objectMapper.writeValueAsString(fields.getFields());
//
//			logger.info(
//					"Final Webhook Response Posting To Merchant-Request, PG_REF_NUM={}, ORDER_ID={}, Merchant_Webhook_URL={}, Payload={}",
//					fields.get(FieldType.PG_REF_NUM.getName()), fields.get(FieldType.ORDER_ID.getName()), hostUrl,
//					json);
//			URL url = new URL(hostUrl);
//			connection = (HttpsURLConnection) url.openConnection();
//			connection.setRequestMethod("POST");
//			connection.setRequestProperty("Content-Type", "application/json");
//			connection.setUseCaches(false);
//			connection.setDoOutput(true);
//			connection.setDoInput(true);
//
//			// Send request
//			OutputStream outputStream = connection.getOutputStream();
//			DataOutputStream wr = new DataOutputStream(outputStream);
//			wr.writeBytes(json);
//			wr.close();
//
//			// Get Response
//			InputStream is = connection.getInputStream();
//			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//
//			int code = ((HttpURLConnection) connection).getResponseCode();
//			logger.info(
//					"Final Webhook Response Posting To Merchant-Response, PG_REF_NUM={}, ORDER_ID={}, RESPONSE_CODE={}",
//					fields.get(FieldType.PG_REF_NUM.getName()), fields.get(FieldType.ORDER_ID.getName()), code);
//
//			if (code == 200) {
//				return true;
//			} else {
//				return false;
//			}
//
//		} catch (Exception e) {
//			logger.error("ERROR OCCURED in webhook response posting to merchant, msg={}, exception={}", e.getMessage(),
//					e);
//			return false;
//		} finally {
//			if (connection != null) {
//				connection.disconnect();
//			}
//		}
//	}
	
		
	@Scheduled(cron = "*/15 * * * * *")
	private void payIn_WebHookPostScheduler() {
		logger.info("WebhookEnable Flag : " + configurationProvider.getWebhookEnable());
		if(StringUtils.isNotBlank(configurationProvider.getWebhookEnable())&&configurationProvider.getWebhookEnable().equalsIgnoreCase("Y")) {
		logger.info("++++++++++++++++++++++++ Start payIn_WebHookPostScheduler +++++++++++++++++++++++++++++++++");
		String webhookFailedCnt = configurationProvider.getWebhookFailedCount();
		String webhookType = "PAYIN";
		List<Document> documents = pgWebHookPost.fetchPGWebHookDetails(webhookFailedCnt, "PAYIN");
		logger.info("Document size for posting webhook response to merchant, Documents Size={}, For PAYIN", documents.size());
		documents.stream().forEach((doc) -> {
			PGWebHookPostConfigURL pgWebHookPostConfigURL = pgWebHookPost.fetchPGWebHookPostConfigURLByPayId(doc.getString(FieldType.PAY_ID.getName()), webhookType);

			try {
				String configurationURL = pgWebHookPostConfigURL.getWebhookUrl();
				Fields fieldObj = wehbookResponse(doc, webhookType);
				this.resolveStatus(fieldObj);
				logger.info("PAYOUT, Webhook Response +++++++++++++++ " + fieldObj.getFieldsAsString());
				fieldObj.put(FieldType.HASH.getName(), Hasher.getHash(fieldObj));
				boolean result = httpsPosting(fieldObj, configurationURL);
				if (result) {
					this.pgWebHookPost.updateFlagAndFailedCount(doc, result);
				} else {
					this.pgWebHookPost.updateFlagAndFailedCount(doc, result);
				}

			} catch (Exception var7) {
				logger.error("ERROR OCCURED IN PAYIN, pgWebHookPostScheduler, Exception={}", var7);
				this.pgWebHookPost.updateFlagAndFailedCount(doc, false);
			}
		});
		logger.info("+++++++++++++++++++++++++++ End payIn_WebHookPostScheduler +++++++++++++++++++++++++++++++++++++++++");
		}
	}

	@Scheduled(cron = "*/15 * * * * *")
	private void payOut_WebHookPostScheduler() {
		logger.info("WebhookEnable Flag : " + configurationProvider.getWebhookEnable());
		if(StringUtils.isNotBlank(configurationProvider.getWebhookEnable())&&configurationProvider.getWebhookEnable().equalsIgnoreCase("Y")) {
		logger.info("++++++++++++++++++++++++ Start payOut_WebHookPostScheduler +++++++++++++++++++++++++++++++++");
		String webhookFailedCnt = configurationProvider.getWebhookFailedCount();
		String webhookType = "PAYOUT";
		List<Document> documents = pgWebHookPost.fetchPGWebHookDetails(webhookFailedCnt, webhookType);
		logger.info("Document size for posting webhook response to merchant, Documents Size={}, For PAYOUT",
				documents.size());
		documents.stream().forEach((doc) -> {
			PGWebHookPostConfigURL pgWebHookPostConfigURL = pgWebHookPost.fetchPGWebHookPostConfigURLByPayId(doc.getString(FieldType.PAY_ID.getName()), webhookType);

			try {
				String configurationURL = pgWebHookPostConfigURL.getWebhookUrl();
				Fields fieldObj = this.wehbookResponse(doc, webhookType);
				this.resolveStatus(fieldObj);
				logger.info("PAYOUT, Webhook Response +++++++++++++++ " + fieldObj.getFieldsAsString());
				fieldObj.put(FieldType.HASH.getName(), Hasher.getHash(fieldObj));
				boolean result = this.httpsPosting(fieldObj, configurationURL);
				if (result) {
					this.pgWebHookPost.updateFlagAndFailedCount_payOut(doc, result);
				} else {
					this.pgWebHookPost.updateFlagAndFailedCount_payOut(doc, result);
				}

			} catch (Exception var7) {
				logger.error("ERROR OCCURED IN PAYOUT, payOut_WebHookPostScheduler, Exception={}", var7);
				this.pgWebHookPost.updateFlagAndFailedCount(doc, false);
			}
		});
		logger.info("+++++++++++++++++++++++++++ End pgWebHookPostScheduler +++++++++++++++++++++++++++++++++++++++++");
		}
	}

	private Fields wehbookResponse(Document doc, String webhookType) {
		Fields fields = new Fields();
		String webhookResponseFields;
		if (webhookType.equalsIgnoreCase("PAYIN")) {
			webhookResponseFields = configurationProvider.getWebhookPayInResponseFields();
		} else {
			webhookResponseFields = configurationProvider.getWebhookPayOutResponseFields();
		}

		String[] webhookResponseFieldsArray = webhookResponseFields.split(",");
		if (null != doc) {
			for (int j = 0; j < doc.size(); ++j) {
				String[] var7 = webhookResponseFieldsArray;
				int var8 = webhookResponseFieldsArray.length;

				for (int var9 = 0; var9 < var8; ++var9) {
					String columnName = var7[var9];
					if (doc.get(columnName) != null) {
						if (columnName.equalsIgnoreCase(FieldType.AMOUNT.getName())) {
							fields.put(FieldType.AMOUNT.getName(),
									Amount.formatAmount(String.valueOf(doc.get(FieldType.AMOUNT.getName())),
											String.valueOf(doc.get(FieldType.CURRENCY_CODE.getName()))));
						} else if (columnName.equalsIgnoreCase(FieldType.TOTAL_AMOUNT.getName())) {
							fields.put(FieldType.TOTAL_AMOUNT.getName(),
									Amount.formatAmount(String.valueOf(doc.get(FieldType.TOTAL_AMOUNT.getName())),
											String.valueOf(doc.get(FieldType.CURRENCY_CODE.getName()))));
						} else {
							fields.put(columnName, doc.get(columnName).toString());
						}
					}
				}
			}
		}

		return fields;
	}

//	public boolean httpsPosting(Fields fields, String hostUrl) throws SystemException {
//		HttpsURLConnection connection = null;
//		ObjectMapper objectMapper = new ObjectMapper();
//
//		try {
//			String json = objectMapper.writeValueAsString(fields.getFields());
//			logger.info("Final Webhook Response Posting To Merchant-Request, PG_REF_NUM={}, ORDER_ID={}, Merchant_Webhook_URL={}, Payload={}",
//					new Object[] { fields.get(FieldType.PG_REF_NUM.getName()), fields.get(FieldType.ORDER_ID.getName()),
//							hostUrl, json });
//			URL url = new URL(hostUrl);
//			if(hostUrl.startsWith("https:")) {
//			connection = (HttpsURLConnection) url.openConnection();
//			}else {
//				connection = (HttpsURLConnection) url.openConnection();
//			}
//			connection.setRequestMethod("POST");
//			connection.setRequestProperty("Content-Type", "application/json");
//			connection.setRequestProperty("User-Agent", "bestpay");
//			connection.setUseCaches(false);
//			connection.setDoOutput(true);
//			connection.setDoInput(true);
//			OutputStream outputStream = connection.getOutputStream();
//			DataOutputStream wr = new DataOutputStream(outputStream);
//			wr.writeBytes(json);
//			wr.close();
//			InputStream is = connection.getInputStream();
//			new BufferedReader(new InputStreamReader(is));
//			int code = connection.getResponseCode();
//			logger.info("Final Webhook Response Posting To Merchant-Response, PG_REF_NUM={}, ORDER_ID={}, RESPONSE_CODE={}", fields.get(FieldType.PG_REF_NUM.getName()), fields.get(FieldType.ORDER_ID.getName()), code );
//			boolean var12;
//			if (code != 200) {
//				var12 = false;
//				return var12;
//			}
//
//			var12 = true;
//			return var12;
//		} catch (Exception e) {
//			logger.error("ERROR OCCURED in webhook response posting to merchant, msg={}, exception={}", e.getMessage(), e);
//			return false;
//		} finally {
//			if (connection != null) {
//				connection.disconnect();
//			}
//
//		}
//	}
	
	public boolean httpsPosting(Fields fields, String hostUrl) throws SystemException {
	    HttpURLConnection httpConnection = null;
	    HttpsURLConnection httpsConnection = null;
	    ObjectMapper objectMapper = new ObjectMapper();
	    int responseCode = -1;

	    try {
	        String json = objectMapper.writeValueAsString(fields.getFields());
	        logger.info("Final Webhook Response Posting To Merchant-Request, PG_REF_NUM={}, ORDER_ID={}, Merchant_Webhook_URL={}, Payload={}",
	                fields.get(FieldType.PG_REF_NUM.getName()), fields.get(FieldType.ORDER_ID.getName()), hostUrl, json);

	        URL url = new URL(hostUrl);
	        
	        if (hostUrl.startsWith("https:")) {
	            logger.info("Using HTTPS connection for URL: {}", hostUrl);
	            httpsConnection = (HttpsURLConnection) url.openConnection();
	            httpsConnection.setRequestMethod("POST");
	            httpsConnection.setRequestProperty("Content-Type", "application/json");
	            httpsConnection.setRequestProperty("User-Agent", "bestpay");
	            httpsConnection.setUseCaches(false);
	            httpsConnection.setDoOutput(true);
	            httpsConnection.setDoInput(true);

	    	    try (OutputStream outputStream = httpsConnection.getOutputStream();
	    	         DataOutputStream wr = new DataOutputStream(outputStream)) {
	    	        wr.writeBytes(json);
	    	    }
	            responseCode = httpsConnection.getResponseCode();
	        } else if (hostUrl.startsWith("http:")) {
	            logger.info("Using HTTP connection for URL: {}", hostUrl);
	            httpConnection = (HttpURLConnection) url.openConnection();
	            httpConnection.setRequestMethod("POST");
	            httpConnection.setRequestProperty("Content-Type", "application/json");
	            httpConnection.setRequestProperty("User-Agent", "bestpay");
	            httpConnection.setUseCaches(false);
	            httpConnection.setDoOutput(true);
	            httpConnection.setDoInput(true);

	    	    try (OutputStream outputStream = httpConnection.getOutputStream();
	    	         DataOutputStream wr = new DataOutputStream(outputStream)) {
	    	        wr.writeBytes(json);
	    	    }
	            responseCode = httpConnection.getResponseCode();
	        } else {
	            throw new IllegalArgumentException("Invalid URL scheme: " + hostUrl);
	        }

	        logger.info("Final Webhook Response Posting To Merchant-Response, PG_REF_NUM={}, ORDER_ID={}, RESPONSE_CODE={}",
	                fields.get(FieldType.PG_REF_NUM.getName()), fields.get(FieldType.ORDER_ID.getName()), responseCode);

	        return responseCode == HttpURLConnection.HTTP_OK;

	    } catch (Exception e) {
	        logger.error("ERROR OCCURRED in webhook response posting to merchant, msg={}, exception={}", e.getMessage(), e);
	        return false;
	    } finally {
	        if (httpConnection != null) {
	            httpConnection.disconnect();
	        }
	        if (httpsConnection != null) {
	            httpsConnection.disconnect();
	        }
	    }
	}

	public void resolveStatus(Fields fields) {
		String status = fields.get(FieldType.STATUS.getName());
		logger.info("resolveStatus :" + status);
		if (StringUtils.isNotBlank(status) && (status.equalsIgnoreCase(StatusType.CAPTURED.getName())
				|| status.equalsIgnoreCase(StatusType.FAILED.getName())
				|| status.equalsIgnoreCase("REFUND_INITIATED"))) {
		} else if(StringUtils.isNotBlank(status) && (status.equalsIgnoreCase(StatusType.FORCE_CAPTURED.getName())
				|| status.equalsIgnoreCase(StatusType.SETTLED_RECONCILLED.getName())
				|| status.equalsIgnoreCase(StatusType.SETTLED_SETTLE.getName()))){
			fields.put(FieldType.STATUS.getName(), "Captured");
			fields.put(FieldType.RESPONSE_CODE.getName(), "000");
			fields.put(FieldType.TXNTYPE.getName(), "SALE");
		} else if (StringUtils.isNotBlank(status) && (status.equalsIgnoreCase(StatusType.DENIED.getName()))) {
			fields.put(FieldType.STATUS.getName(), "REQUEST ACCEPTED");
			fields.put(FieldType.RESPONSE_CODE.getName(), "026");
		} else if (status.equalsIgnoreCase("REQUEST ACCEPTED") || status.equalsIgnoreCase("Pending")
				|| status.equalsIgnoreCase(StatusType.SENT_TO_BANK.getName())) {
			fields.put(FieldType.STATUS.getName(), "REQUEST ACCEPTED");
			fields.put(FieldType.RESPONSE_CODE.getName(), "000");
		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
		}
	}
}
