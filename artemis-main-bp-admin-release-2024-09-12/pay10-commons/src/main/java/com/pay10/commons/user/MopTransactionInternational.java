package com.pay10.commons.user;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.pay10.commons.util.TransactionType;
import com.pay10.commons.util.TxnType;

@Entity
public class MopTransactionInternational implements Serializable {

	private static final long serialVersionUID = -5319143866033620832L;
		
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;
	
	@Enumerated(EnumType.STRING)
	private TxnType txnType ;
	
	public MopTransactionInternational(){
		
	}
	public MopTransactionInternational(TransactionType transactionType){
		this.transactionType= transactionType;
	}
	
	public TransactionType getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}
	public TxnType getTxnType() {
		return txnType;
	}
	public void setTxnType(TxnType txnType) {
		this.txnType = txnType;
	}
		

}
