package com.pay10.atom;

import org.springframework.stereotype.Service;

@Service
public class Transaction {

	private String login;
	private String pass;
	private String ttype;
	private String prodid;
	private String amt;
	private String txncurr;
	private String txnscamt;
	private String Clientcode;

	private String txnid;
	private String date;
	private String custacc;
	private String mdd;
	private String ru;
	private String udf1;
	private String udf2;
	private String udf3;
	private String udf4;
	private String udf5;
	private String udf6;
	private String udf9;
	private String udf;
	private String bankid;
	
	private String reqHashKey;
	private String respHashKey;
	private String IV;
	private String key;
	
	private String mmp_txn;
	private String mer_txn;
	private String prod;
	private String bank_txn;
	private String f_code;
	private String bank_name;
	private String merchant_id;
	private String discriminator;
	private String desc;
	private String auth_code;
	private String ipg_txn_id;
	private String surcharge;
	private String CardNumber;
	private String signature;
	
	private String data;
	private String encData;
	
	private String statusCode;
	private String statusMessage;
	private String atomRefundId;
	
	private String merefundref;
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public String getTtype() {
		return ttype;
	}
	public void setTtype(String ttype) {
		this.ttype = ttype;
	}
	public String getProdid() {
		return prodid;
	}
	public void setProdid(String prodid) {
		this.prodid = prodid;
	}
	public String getAmt() {
		return amt;
	}
	public void setAmt(String amt) {
		this.amt = amt;
	}
	public String getTxncurr() {
		return txncurr;
	}
	public void setTxncurr(String txncurr) {
		this.txncurr = txncurr;
	}
	public String getTxnscamt() {
		return txnscamt;
	}
	public void setTxnscamt(String txnscamt) {
		this.txnscamt = txnscamt;
	}
	public String getClientcode() {
		return Clientcode;
	}
	public void setClientcode(String clientcode) {
		Clientcode = clientcode;
	}
	public String getTxnid() {
		return txnid;
	}
	public void setTxnid(String txnid) {
		this.txnid = txnid;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getCustacc() {
		return custacc;
	}
	public void setCustacc(String custacc) {
		this.custacc = custacc;
	}
	public String getMdd() {
		return mdd;
	}
	public void setMdd(String mdd) {
		this.mdd = mdd;
	}
	public String getRu() {
		return ru;
	}
	public void setRu(String ru) {
		this.ru = ru;
	}
	public String getUdf1() {
		return udf1;
	}
	public void setUdf1(String udf1) {
		this.udf1 = udf1;
	}
	public String getUdf2() {
		return udf2;
	}
	public void setUdf2(String udf2) {
		this.udf2 = udf2;
	}
	public String getUdf3() {
		return udf3;
	}
	public void setUdf3(String udf3) {
		this.udf3 = udf3;
	}
	public String getUdf4() {
		return udf4;
	}
	public void setUdf4(String udf4) {
		this.udf4 = udf4;
	}
	public String getUdf5() {
		return udf5;
	}
	public void setUdf5(String udf5) {
		this.udf5 = udf5;
	}
	public String getUdf6() {
		return udf6;
	}
	public void setUdf6(String udf6) {
		this.udf6 = udf6;
	}
	public String getUdf9() {
		return udf9;
	}
	public void setUdf9(String udf9) {
		this.udf9 = udf9;
	}
	public String getUdf() {
		return udf;
	}
	public void setUdf(String udf) {
		this.udf = udf;
	}
	public String getBankid() {
		return bankid;
	}
	public void setBankid(String bankid) {
		this.bankid = bankid;
	}
	public String getReqHashKey() {
		return reqHashKey;
	}
	public void setReqHashKey(String reqHashKey) {
		this.reqHashKey = reqHashKey;
	}
	public String getRespHashKey() {
		return respHashKey;
	}
	public void setRespHashKey(String respHashKey) {
		this.respHashKey = respHashKey;
	}
	public String getIV() {
		return IV;
	}
	public void setIV(String iV) {
		IV = iV;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getMmp_txn() {
		return mmp_txn;
	}
	public void setMmp_txn(String mmp_txn) {
		this.mmp_txn = mmp_txn;
	}
	public String getMer_txn() {
		return mer_txn;
	}
	public void setMer_txn(String mer_txn) {
		this.mer_txn = mer_txn;
	}
	public String getProd() {
		return prod;
	}
	public void setProd(String prod) {
		this.prod = prod;
	}
	public String getBank_txn() {
		return bank_txn;
	}
	public void setBank_txn(String bank_txn) {
		this.bank_txn = bank_txn;
	}
	public String getF_code() {
		return f_code;
	}
	public void setF_code(String f_code) {
		this.f_code = f_code;
	}
	public String getBank_name() {
		return bank_name;
	}
	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}
	public String getMerchant_id() {
		return merchant_id;
	}
	public void setMerchant_id(String merchant_id) {
		this.merchant_id = merchant_id;
	}
	public String getDiscriminator() {
		return discriminator;
	}
	public void setDiscriminator(String discriminator) {
		this.discriminator = discriminator;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getAuth_code() {
		return auth_code;
	}
	public void setAuth_code(String auth_code) {
		this.auth_code = auth_code;
	}
	public String getIpg_txn_id() {
		return ipg_txn_id;
	}
	public void setIpg_txn_id(String ipg_txn_id) {
		this.ipg_txn_id = ipg_txn_id;
	}
	public String getSurcharge() {
		return surcharge;
	}
	public void setSurcharge(String surcharge) {
		this.surcharge = surcharge;
	}
	public String getCardNumber() {
		return CardNumber;
	}
	public void setCardNumber(String cardNumber) {
		CardNumber = cardNumber;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getEncData() {
		return encData;
	}
	public void setEncData(String encData) {
		this.encData = encData;
	}
	public String getMerefundref() {
		return merefundref;
	}
	public void setMerefundref(String merefundref) {
		this.merefundref = merefundref;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	public String getAtomRefundId() {
		return atomRefundId;
	}
	public void setAtomRefundId(String atomRefundId) {
		this.atomRefundId = atomRefundId;
	}
	@Override
	public String toString() {
		return "Transaction [login=" + login + ", pass=" + pass + ", ttype=" + ttype + ", prodid=" + prodid + ", amt="
				+ amt + ", txncurr=" + txncurr + ", txnscamt=" + txnscamt + ", Clientcode=" + Clientcode + ", txnid="
				+ txnid + ", date=" + date + ", custacc=" + custacc + ", mdd=" + mdd + ", ru=" + ru + ", udf1=" + udf1
				+ ", udf2=" + udf2 + ", udf3=" + udf3 + ", udf4=" + udf4 + ", udf5=" + udf5 + ", udf6=" + udf6
				+ ", udf9=" + udf9 + ", udf=" + udf + ", bankid=" + bankid + ", reqHashKey=" + reqHashKey
				+ ", respHashKey=" + respHashKey + ", IV=" + IV + ", key=" + key + ", mmp_txn=" + mmp_txn + ", mer_txn="
				+ mer_txn + ", prod=" + prod + ", bank_txn=" + bank_txn + ", f_code=" + f_code + ", bank_name="
				+ bank_name + ", merchant_id=" + merchant_id + ", discriminator=" + discriminator + ", desc=" + desc
				+ ", auth_code=" + auth_code + ", ipg_txn_id=" + ipg_txn_id + ", surcharge=" + surcharge
				+ ", CardNumber=" + CardNumber + ", signature=" + signature + ", data=" + data + ", encData=" + encData
				+ ", statusCode=" + statusCode + ", statusMessage=" + statusMessage + ", atomRefundId=" + atomRefundId
				+ ", merefundref=" + merefundref + "]";
	}
	
	
}
