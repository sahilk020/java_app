package com.pay10.batch.commons;

import java.util.List;

import com.pay10.batch.commons.util.DateCreater;
import com.pay10.batch.exception.DatabaseException;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/** Getting the previous history from DB 
 * for a given Id and processing it.
 */
@Service
public class Historian {
	private static Logger logger = Logger.getLogger(Historian.class.getName());
	/*Reading prevoius values from DB for various combinations*/
	public void findPrevious(Fields fields)
		    throws DatabaseException
	{
		String pgRefNum = fields.get(FieldType.PG_REF_NUM.getName());
		if (null != pgRefNum) {
			String txnType = fields.get(FieldType.RECO_TXNTYPE.getName());

			if (txnType.equals(TransactionType.RECO.getName())) {
  				if (fields.get(FieldType.REFUND_FLAG.getName()) != null) {
					if (Constants.RECO_TRUE.getValue().equalsIgnoreCase(fields.get(FieldType.IS_SALE_CAPTURED.getName()))) {
						List<Fields> saleList = fields.getPreviousSaleOrRecoForPgRefNum(fields.get(FieldType.SALE_PG_REF_NUM.getName()));
						for (Fields previous : saleList) {
							if (fields.get(FieldType.SALE_PG_REF_NUM.getName()).equals(previous.get(FieldType.PG_REF_NUM.getName()))) {
								 if (isSaleCapturePresent(previous)) {
									fields.put(FieldType.IS_SALE_CAPTURED.name(), Constants.RECO_TRUE.getValue());
								}
							}
							fields.setPrevious(previous);
						}
					}					  
				}
				else if (fields.get(FieldType.RESPONSE_CODE.getName()) != null)  {
					// Settled/Sale/Pending Data
					List<Fields> saleList = fields.getPreviousSettlementOrSaleForPgRefNum(fields.get(FieldType.PG_REF_NUM.getName()));
					//&& previous.get(FieldType.STATUS.getName()).equals(StatusType.FAILED.getName())
					for (Fields previous : saleList) {
						previous.logAllFields("DB Previous Txn Record : ");
						//condition applied for fixing empty ACQ_ID in Failed txn
						if(previous.get(FieldType.RECO_ACQ_ID.getName())!=null)
						{
							if (previous.get(FieldType.RECO_TXNTYPE.getName()).equals(TransactionType.SALE.getName())
									&& previous.get(FieldType.STATUS.getName()).equals(StatusType.CAPTURED.getName())
									|| previous.get(FieldType.STATUS.getName()).equals(StatusType.FAILED.getName())
									|| previous.get(FieldType.STATUS.getName()).equals(StatusType.DECLINED.getName())
									|| previous.get(FieldType.STATUS.getName()).equals(StatusType.REJECTED.getName())
									|| previous.get(FieldType.STATUS.getName()).equals(StatusType.ERROR.getName())
									|| previous.get(FieldType.STATUS.getName()).equals(StatusType.TIMEOUT.getName())
									|| previous.get(FieldType.STATUS.getName())
											.equals(StatusType.BROWSER_CLOSED.getName())
									|| previous.get(FieldType.STATUS.getName()).equals(StatusType.CANCELLED.getName())
									|| previous.get(FieldType.STATUS.getName()).equals(StatusType.DENIED.getName())
									|| previous.get(FieldType.STATUS.getName()).equals(StatusType.DUPLICATE.getName())
									|| previous.get(FieldType.STATUS.getName())
											.equals(StatusType.AUTHENTICATION_FAILED.getName())
									|| previous.get(FieldType.STATUS.getName())
											.equals(StatusType.DENIED_BY_FRAUD.getName())
									|| previous.get(FieldType.STATUS.getName())
											.equals(StatusType.ACQUIRER_DOWN.getName())
									|| previous.get(FieldType.STATUS.getName())
											.equals(StatusType.FAILED_AT_ACQUIRER.getName())
									|| previous.get(FieldType.STATUS.getName())
											.equals(StatusType.ACQUIRER_TIMEOUT.getName())
									|| previous.get(FieldType.STATUS.getName()).equals(StatusType.USER_INACTIVE
											.getName())
											&& previous.get(FieldType.RECO_ACQ_ID.getName())
													.equals(fields.get(FieldType.RECO_ACQ_ID.getName()))) {
								fields.setPrevious(previous);
							}
						} else {
							if (previous.get(FieldType.RECO_TXNTYPE.getName()).equals(TransactionType.SALE.getName())
									&& previous.get(FieldType.STATUS.getName()).equals(StatusType.CAPTURED.getName())
									|| previous.get(FieldType.STATUS.getName()).equals(StatusType.FAILED.getName())
									|| previous.get(FieldType.STATUS.getName()).equals(StatusType.DECLINED.getName())
									|| previous.get(FieldType.STATUS.getName()).equals(StatusType.REJECTED.getName())
									|| previous.get(FieldType.STATUS.getName()).equals(StatusType.ERROR.getName())
									|| previous.get(FieldType.STATUS.getName()).equals(StatusType.TIMEOUT.getName())
									|| previous.get(FieldType.STATUS.getName())
											.equals(StatusType.BROWSER_CLOSED.getName())
									|| previous.get(FieldType.STATUS.getName()).equals(StatusType.CANCELLED.getName())
									|| previous.get(FieldType.STATUS.getName()).equals(StatusType.DENIED.getName())
									|| previous.get(FieldType.STATUS.getName()).equals(StatusType.DUPLICATE.getName())
									|| previous.get(FieldType.STATUS.getName())
											.equals(StatusType.AUTHENTICATION_FAILED.getName())
									|| previous.get(FieldType.STATUS.getName())
											.equals(StatusType.DENIED_BY_FRAUD.getName())
									|| previous.get(FieldType.STATUS.getName())
											.equals(StatusType.ACQUIRER_DOWN.getName())
									|| previous.get(FieldType.STATUS.getName())
											.equals(StatusType.FAILED_AT_ACQUIRER.getName())
									|| previous.get(FieldType.STATUS.getName())
											.equals(StatusType.ACQUIRER_TIMEOUT.getName())
									|| previous.get(FieldType.STATUS.getName())
											.equals(StatusType.USER_INACTIVE.getName())) {
								fields.setPrevious(previous);
							}
						}
						logger.info("REC_TXNTYPE: "+ previous.get(FieldType.RECO_TXNTYPE.getName()) + ", STATUS: "+
								previous.get(FieldType.STATUS.getName())+ " , RECO_ACQ_ID: "+ previous.get(FieldType.RECO_ACQ_ID.getName()));

						/*if(previous.get(FieldType.RECO_TXNTYPE.getName()).equals(TransactionType.REFUND.getName()) &&
								previous.get(FieldType.STATUS.getName()).equals(StatusType.PENDING.getName())) {
							fields.setRefundPrevious(previous);
						}*/
						
						
						if ((isRecoSettledPresent(previous)) && (fields.get(FieldType.RECO_ACQ_ID.getName()).equals(previous.get(FieldType.RECO_ACQ_ID.getName())))) {
							fields.put(FieldType.IS_RECO_SETTLED.name(), Constants.RECO_TRUE.getValue());
						}else if(isRecoForceCapturedPresentForFailed(previous)&& (fields.get(FieldType.RECO_ACQ_ID.getName()).equals(previous.get(FieldType.RECO_ACQ_ID.getName())))) {
							fields.put(FieldType.IS_RECO_FORCE_CAPRURED.name(), Constants.RECO_TRUE.getValue());	
						}
							
							
							else if (isSaleCapturePresent(previous)) {
							/*if(Boolean.parseBoolean(fields.get(FieldType.IS_SALE_CAPTURED.getName())) == true) {
								fields.put(FieldType.IS_DOUBLE_DEBIT.name(), Constants.RECO_TRUE.getValue());
							}*/
							fields.put(FieldType.IS_SALE_CAPTURED.name(), Constants.RECO_TRUE.getValue());
							
						} /*else if(isRecoPendingPresent(previous)) {
							fields.put(FieldType.IS_RECO_PENDING.name(), Constants.RECO_TRUE.getValue());
						} else if(isRefundPendingPresent(previous)) {
							fields.put(FieldType.IS_REFUND_PENDING.name(), Constants.RECO_TRUE.getValue());
						}*/
					}
					/*if((Boolean.parseBoolean(fields.get(FieldType.IS_RECO_SETTLED.getName())) == true) 
							&& (Boolean.parseBoolean(fields.get(FieldType.IS_DOUBLE_DEBIT.getName())) == true)) {
						for (Fields previous : saleList) {
							if((isSaleCapturePresent(previous)) && (fields.get(FieldType.RECO_ACQ_ID.getName()).equals(previous.get(FieldType.RECO_ACQ_ID.getName())))) {
								fields.setPrevious(previous);
							}
						}
					}*/
					if(saleList.size() == 0) {
						// Enroll/Sale and Enrolled/Sent to Bank/Timeout Data
						//List<Fields> txnList = fields.getPreviousEnrolledOrSaleForPgRefNum(fields.get(FieldType.PG_REF_NUM.getName()));
						List<Fields> txnList = fields.getPreviousSaleForPgRefNum(fields.get(FieldType.PG_REF_NUM.getName()));
						/*for (Fields previous : txnList) {*/
						if(txnList.size() >0) {
							fields.setPrevious(txnList.get(0));
							fields.put(FieldType.IS_SALE_TIMEOUT.name(), Constants.RECO_TRUE.getValue());
						}
						/*for (Fields previous : txnList) {
							fields.setPrevious(previous);
							if ((isEnrollEnrolledPresent(previous)) && (fields.get(FieldType.PG_REF_NUM.getName()).equals(previous.get(FieldType.PG_REF_NUM.getName())))) {
								fields.put(FieldType.IS_ENROLL_ENROLLED.name(), Constants.RECO_TRUE.getValue());
							} 
							else if ((isSaleTimeoutPresent(previous)) && (fields.get(FieldType.PG_REF_NUM.getName()).equals(previous.get(FieldType.PG_REF_NUM.getName())))) {
								fields.put(FieldType.IS_SALE_TIMEOUT.name(), Constants.RECO_TRUE.getValue());
							}
							else if ((isSaleSentToBankPresent(previous)) && (fields.get(FieldType.PG_REF_NUM.getName()).equals(previous.get(FieldType.PG_REF_NUM.getName())))) {
								fields.put(FieldType.IS_SALE_SENT_TO_BANK.name(), Constants.RECO_TRUE.getValue());
							}
						}*/
					}
				}
				else  {
					// Sale Reco changes
					List<Fields> settlementList = fields.getPreviousSettlementOrRecoForPgRefNum(fields.get(FieldType.PG_REF_NUM.getName()));
					for (Fields previous : settlementList) {
						if(previous.get(FieldType.RECO_TXNTYPE.getName()).equals(TransactionType.SALE.getName()) &&
								previous.get(FieldType.STATUS.getName()).equals(StatusType.CAPTURED.getName())) {
							fields.setPrevious(previous);
						}
						/*if(previous.get(FieldType.RECO_TXNTYPE.getName()).equals(TransactionType.REFUND.getName()) &&
								previous.get(FieldType.STATUS.getName()).equals(StatusType.PENDING.getName())) {
							fields.setRefundPrevious(previous);
						}*/
						/*if ((isRecoReconciledPresent(previous)) && (fields.get(FieldType.PG_REF_NUM.getName()).equals(previous.get(FieldType.PG_REF_NUM.getName())))) {
							fields.put(FieldType.IS_RECO_RECONCILED.name(), Constants.RECO_TRUE.getValue());
						} */
						else if ((isRecoSettledPresent(previous)) && (fields.get(FieldType.PG_REF_NUM.getName()).equals(previous.get(FieldType.PG_REF_NUM.getName())))) {
							fields.put(FieldType.IS_RECO_SETTLED.name(), Constants.RECO_TRUE.getValue());
						}
						else if ((isRecoForceCapturedPresentForFailed(previous)) && (fields.get(FieldType.PG_REF_NUM.getName()).equals(previous.get(FieldType.PG_REF_NUM.getName())))) {
							fields.put(FieldType.IS_RECO_FORCE_CAPRURED.name(), Constants.RECO_TRUE.getValue());
						}
						else if ((isSaleCapturePresent(previous)) && (fields.get(FieldType.PG_REF_NUM.getName()).equals(previous.get(FieldType.PG_REF_NUM.getName())))) {
							fields.put(FieldType.IS_SALE_CAPTURED.name(), Constants.RECO_TRUE.getValue());
						}
						/*else if ((isRecoPendingPresent(previous)) && (fields.get(FieldType.PG_REF_NUM.getName()).equals(previous.get(FieldType.PG_REF_NUM.getName())))) {
							fields.put(FieldType.IS_RECO_PENDING.name(), Constants.RECO_TRUE.getValue());
						}
						else if ((isRefundPendingPresent(previous)) && (fields.get(FieldType.PG_REF_NUM.getName()).equals(previous.get(FieldType.PG_REF_NUM.getName())))) {
							fields.put(FieldType.IS_REFUND_PENDING.name(), Constants.RECO_TRUE.getValue());
						}*/
					}
				}
			}
			else if (txnType.equals(TransactionType.REFUNDRECO.getName())) {
				// Refund Reco changes
				List<Fields> refundList = fields.getPreviousRefundOrRecoForPgRefNum(fields.get(FieldType.PG_REF_NUM.getName()));
				for (Fields previous : refundList) {
					if (isRefundRecorSettledPresent(previous)) {
						fields.put(FieldType.IS_REFUND_RECO_SETTLED.name(), Constants.RECO_TRUE.getValue());
					} else if (isRefundCapturePresent(previous)) {
						fields.put(FieldType.IS_REFUND_CAPTURED.name(), Constants.RECO_TRUE.getValue());
					} /*else if (isRefundTimeoutPresent(previous)) {
						fields.put(FieldType.IS_REFUND_TIMEOUT.name(), Constants.RECO_TRUE.getValue());
					}*/
					fields.setPrevious(previous);
				}
				if(refundList.size() == 0) {
					
					List<Fields> refundTxnList = fields.getPreviousRefundTxnForPgRefNum(fields.get(FieldType.PG_REF_NUM.getName()));
					if(refundTxnList.size() > 0) {
						fields.setPrevious(refundTxnList.get(refundTxnList.size() - 1));
						fields.put(FieldType.IS_REFUND_TIMEOUT.name(), Constants.RECO_TRUE.getValue());
					}
					/*for (Fields previous : txnList) {
						fields.setPrevious(previous);
						if ((isEnrollEnrolledPresent(previous)) && (fields.get(FieldType.PG_REF_NUM.getName()).equals(previous.get(FieldType.PG_REF_NUM.getName())))) {
							fields.put(FieldType.IS_ENROLL_ENROLLED.name(), Constants.RECO_TRUE.getValue());
						} 
						else if ((isSaleTimeoutPresent(previous)) && (fields.get(FieldType.PG_REF_NUM.getName()).equals(previous.get(FieldType.PG_REF_NUM.getName())))) {
							fields.put(FieldType.IS_SALE_TIMEOUT.name(), Constants.RECO_TRUE.getValue());
						}
						else if ((isSaleSentToBankPresent(previous)) && (fields.get(FieldType.PG_REF_NUM.getName()).equals(previous.get(FieldType.PG_REF_NUM.getName())))) {
							fields.put(FieldType.IS_SALE_SENT_TO_BANK.name(), Constants.RECO_TRUE.getValue());
						}
					}*/
				}
		} else if (txnType.equals(TransactionType.REFUND.getName())) {
				// Refund changes
			if(fields.contains(FieldType.REFUND_TICKETING_FLAG.getName())) {
				if(fields.get(FieldType.REFUND_TICKETING_FLAG.getName()).equals(Constants.Y_FLAG.getValue())) {
					List<Fields> refundList = fields.getPreviousRefundForRefundOrderId(fields.get(FieldType.REFUND_ORDER_ID.getName()), fields.get(FieldType.RECO_ORDER_ID.getName()));
					if(refundList.size()>0) {
						fields.setPrevious(refundList.get(0));
						fields.put(FieldType.REFUND_DATE_TIME.getName(),DateCreater.formatDBRequestDate(refundList.get(0).get(FieldType.REQUEST_DATE.getName())));
					} else {
					/*for (Fields previous : refundList) {
						if (isRecoSettledPresent(previous)) {
							fields.put(FieldType.IS_RECO_SETTLED.name(), Constants.RECO_TRUE.getValue());
						} else if (isSaleCapturePresent(previous)) {
							fields.put(FieldType.IS_SALE_CAPTURED.name(), Constants.RECO_TRUE.getValue());
						}				
						fields.setPrevious(previous);
					}*/ 
					
					List<Fields> recoList = fields.getPreviousRecoSettledForPgRefNum(fields.get(FieldType.PG_REF_NUM.getName()));
						for (Fields previous : recoList) {
							if (isRecoSettledPresent(previous)) {
								fields.put(FieldType.IS_RECO_SETTLED.name(), Constants.RECO_TRUE.getValue());
							} else if (isSaleCapturePresent(previous)) {
								fields.put(FieldType.IS_SALE_CAPTURED.name(), Constants.RECO_TRUE.getValue());
							}else if ((isRecoForceCapturedPresentForFailed(previous))) {
								fields.put(FieldType.IS_RECO_FORCE_CAPRURED.name(), Constants.RECO_TRUE.getValue());
							}		
							fields.setPrevious(previous);
						} 
						
					} 
					}
			} else {
							
					List<Fields> recoList = fields.getPreviousRecoSettledForPgRefNum(fields.get(FieldType.PG_REF_NUM.getName()));
					for (Fields previous : recoList) {
						if (isRecoSettledPresent(previous)) {
							fields.put(FieldType.IS_RECO_SETTLED.name(), Constants.RECO_TRUE.getValue());
						} else if (isSaleCapturePresent(previous)) {
							fields.put(FieldType.IS_SALE_CAPTURED.name(), Constants.RECO_TRUE.getValue());
						}else if ((isRecoForceCapturedPresentForFailed(previous))) {
							fields.put(FieldType.IS_RECO_FORCE_CAPRURED.name(), Constants.RECO_TRUE.getValue());
						}		
						fields.setPrevious(previous);
					}				
				}
			}			
		}		
	}
	
	/*Checking if RECO RECONCILED transaction is already present for current transaction*/
	 /*private boolean isRecoReconciledPresent(Fields previous)
	  {
	    if ((previous != null) && 
	      ((previous.get(FieldType.TXN_ID.getName()) != null) || (previous.get(FieldType.TXN_ID.getName()) != "")) && 
	      (previous.get(FieldType.RECO_TXNTYPE.getName()).equalsIgnoreCase(TransactionType.RECO.getName()) || previous.get(FieldType.RECO_TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUNDRECO.getName())) &&
	      (previous.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.RECONCILED.getName()))) {
	      return true;
	    }
	    return false;
	  }*/
	 
	 /*Checking if REFUNDRECO SETTLED transaction is already present for current transaction*/
	 private boolean isRefundRecorSettledPresent(Fields previous)
	  {
	    if ((previous != null) && 
	      ((previous.get(FieldType.TXN_ID.getName()) != null) || (previous.get(FieldType.TXN_ID.getName()) != "")) && 
	      (previous.get(FieldType.RECO_TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUNDRECO.getName())) &&
	   // Done By chetan nagaria for change in settlement process to mark transaction as RNS
//	      (previous.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.SETTLED.getName()))) {
	    	(previous.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.SETTLED_RECONCILLED.getName()))) {
	      return true;
	      
	      
	      
	    }
	    return false;
	  }
	  
	 /*Checking if SALE CAPTURED transaction is present for current transaction*/
	  private boolean isSaleCapturePresent(Fields previous)
	  {
	    if ((previous != null) && 
	      ((previous.get(FieldType.TXN_ID.getName()) != null) || (previous.get(FieldType.TXN_ID.getName()) != "")) && 
	      (previous.get(FieldType.RECO_TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())) &&
	      (previous.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.CAPTURED.getName()))) {
	      return true;
	    }
	    return false;
	  }
	  
	  /*Checking if REFUND CAPTURED transaction is present for current transaction*/
	  private boolean isRefundCapturePresent(Fields previous)
	  {
	    if ((previous != null) && 
	      ((previous.get(FieldType.TXN_ID.getName()) != null) || (previous.get(FieldType.TXN_ID.getName()) != "")) && 
	      (previous.get(FieldType.RECO_TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getName())) &&
	      (previous.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.CAPTURED.getName()))) {
	      return true;
	    }
	    return false;
	  }
	  
	  /*Checking if REFUND Timeout transaction is present for current transaction*/
	  private boolean isRefundTimeoutPresent(Fields previous)
	  {
	    if ((previous != null) && 
	      ((previous.get(FieldType.TXN_ID.getName()) != null) || (previous.get(FieldType.TXN_ID.getName()) != "")) && 
	      (previous.get(FieldType.RECO_TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getName())) &&
	      (previous.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.TIMEOUT.getName()))) {
	      return true;
	    }
	    return false;
	  }
	  
	  /*Checking if RECO SETTLED transaction is already present for current transaction*/
		 private boolean isRecoSettledPresent(Fields previous)
		  {
		    if ((previous != null) && 
		      ((previous.get(FieldType.TXN_ID.getName()) != null) || (previous.get(FieldType.TXN_ID.getName()) != "")) && 
		      (previous.get(FieldType.RECO_TXNTYPE.getName()).equalsIgnoreCase(TransactionType.RECO.getName())) &&
		   // Done By chetan nagaria for change in settlement process to mark transaction as RNS
//		      (previous.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.SETTLED.getName()))) {
		    	(previous.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.SETTLED_RECONCILLED.getName()))) {
		      return true;
		    }
		    return false;
		  }
		 private boolean isRecoForceCapturedPresentForFailed(Fields previous)
		  {
		    if ((previous != null) && 
		      ((previous.get(FieldType.TXN_ID.getName()) != null) || (previous.get(FieldType.TXN_ID.getName()) != "")) && 
		      (previous.get(FieldType.RECO_TXNTYPE.getName()).equalsIgnoreCase(TransactionType.RECO.getName())) &&
		   // Done By chetan nagaria for change in settlement process to mark transaction as RNS
//		      (previous.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.SETTLED.getName()))) {
		    	(previous.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.FORCE_CAPTURED.getName()))) {
		      return true;
		    }
		    return false;
		  }
		 
		 /*Checking if RECO PENDING transaction is present for current transaction*/
		  /*private boolean isRecoPendingPresent(Fields previous)
		  {
		    if ((previous != null) && 
		      ((previous.get(FieldType.TXN_ID.getName()) != null) || (previous.get(FieldType.TXN_ID.getName()) != "")) && 
		      (previous.get(FieldType.RECO_TXNTYPE.getName()).equalsIgnoreCase(TransactionType.RECO.getName())) &&
		      (previous.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.PENDING.getName()))) {
		      return true;
		    }
		    return false;
		  }*/
		  
		  /*Checking if REFUND CAPTURED transaction is present for current transaction*/
		  /*private boolean isRefundPendingPresent(Fields previous)
		  {
		    if ((previous != null) && 
		      ((previous.get(FieldType.TXN_ID.getName()) != null) || (previous.get(FieldType.TXN_ID.getName()) != "")) && 
		      (previous.get(FieldType.RECO_TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getName())) &&
		      (previous.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.PENDING.getName()))) {
		      return true;
		    }
		    return false;
		  }*/
		  
		  /*Checking if ENROLL ENROLLED transaction is present for current transaction*/
		  private boolean isEnrollEnrolledPresent(Fields previous)
		  {
		    if ((previous != null) && 
		      ((previous.get(FieldType.TXN_ID.getName()) != null) || (previous.get(FieldType.TXN_ID.getName()) != "")) && 
		      (previous.get(FieldType.RECO_TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) &&
		      (previous.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.ENROLLED.getName()))) {
		      return true;
		    }
		    return false;
		  }
		  
		  /*Checking if SALE TIMEOUT transaction is present for current transaction*/
		  private boolean isSaleTimeoutPresent(Fields previous)
		  {
		    if ((previous != null) && 
		      ((previous.get(FieldType.TXN_ID.getName()) != null) || (previous.get(FieldType.TXN_ID.getName()) != "")) && 
		      (previous.get(FieldType.RECO_TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())) &&
		      (previous.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.TIMEOUT.getName()))) {
		      return true;
		    }
		    return false;
		  }
		  
		  /*Checking if SALE SENT_TO_BANK transaction is present for current transaction*/
		  private boolean isSaleSentToBankPresent(Fields previous)
		  {
		    if ((previous != null) && 
		      ((previous.get(FieldType.TXN_ID.getName()) != null) || (previous.get(FieldType.TXN_ID.getName()) != "")) && 
		      (previous.get(FieldType.RECO_TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())) &&
		      (previous.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.SENT_TO_BANK.getName()))) {
		      return true;
		    }
		    return false;
		  }
}
