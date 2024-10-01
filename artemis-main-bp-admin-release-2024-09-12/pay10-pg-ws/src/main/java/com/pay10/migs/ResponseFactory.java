package com.pay10.migs;

import org.springframework.stereotype.Service;

import com.pay10.commons.util.Fields;
import com.pay10.migs.Transaction;

/**
 * @author Sunil
 *
 */
@Service
public class ResponseFactory {
	Transaction transaction = new Transaction();
	public Transaction createResponse(Fields fields){
		
			for(String key:fields.keySet()){
				String value=fields.get(key);
				
				switch(key){
				case("vpc_TxnResponseCode"):
					transaction.setResponseCode(value);
					break;
				case("vpc_Amount"):
					transaction.setAmount(value);
					break;
				case("vpc_BatchNo"):
					transaction.setBatchNo(value);
					break;				
				case("vpc_OrderInfo"):
					transaction.setOrderInfo(value);
					break;
				case("vpc_VerStatus"):
					transaction.setVerStatus(value);
					break;
				case("vpc_3DSECI"):
					transaction.setEciFlag(value);
					break;
				case("vpc_Command"):
					transaction.setCommand(value);
					break;
				case("vpc_Merchant"):
					transaction.setMerchantId(value);
					break;
				case("vpc_Message"):
					transaction.setMessage(value);
					break;
				case("vpc_3DSstatus"):
					transaction.setThreeDSstatus(value);
					break;
				case("vpc_SecureHash"):
					transaction.setSecureHash(value);
					break;				
				case("vpc_SecureHashType"):
					transaction.setSecureHashType(value);
					break;
				case("vpc_3DSenrolled"):
					transaction.setThreeDSenrolled(value);
					break;
				case("vpc_MerchTxnRef"):
					transaction.setMerchTxnRef(value);
					break;
				case("vpc_TransactionNo"):
					transaction.setPgTransactionNo(value);
					break;
				case("vpc_VerType"):
					transaction.setVerType(value);
					break;
				case("vpc_VerSecurityLevel"):
					transaction.setVerSecurityLevel(value);
					break;				
				case("vpc_3DSXID"):
					transaction.setThreeDSXID(value);
					break;			
				case("vpc_VerToken"):
					transaction.setVerToken(value);
					break;
				
				default:
					break;
				}
			}
		
		return transaction;
	}
	
	public void prepareFields(){
		
	}
}
