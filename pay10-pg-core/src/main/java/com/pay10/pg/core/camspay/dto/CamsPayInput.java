package com.pay10.pg.core.camspay.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CamsPayInput {

	private String custid;
	private String custname;
	private String trxnid;
	private String amount;
	private String subbillerid;
	private String remarks;
	private String accno;
	private String acctype;
	private String accholdername;
	private String bankcode;
	private String bankname;
	private String bankbranch;
	private String ifsc;
	private String currency;
	private String currcode;
	private String vpa;
	private String successurl;
	private String failureurl;
	private String ud1;
	private String ud2;
	private String ud3;
	private String ud4;
	private String ud5;
	private String ud6;
	private String ud7;
	private String ud8;
	private String ud9;
	private String ud10;
	private String ud11;
	private String ud12;
	private String ud13;
	private String ud14;
	private String ud15;
	private String ud16;
	private String ud17;
	private String ud18;
	private String ud19;
	private String ud20;
	private String reqdt;
	private String devicetype;
	private String sessionid;
	private String ip;
	private String deviceid;
	private String osid;
	private String applicationname;
	private String iptallowed;
	private String intentcall;
	private String UpiCollectExpTime;
	private Schemes schemes;

	public String getCustid() {
		return custid;
	}

	public void setCustid(String custid) {
		this.custid = custid;
	}

	public String getCustname() {
		return custname;
	}

	public void setCustname(String custname) {
		this.custname = custname;
	}

	public String getTrxnid() {
		return trxnid;
	}

	public void setTrxnid(String trxnid) {
		this.trxnid = trxnid;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getSubbillerid() {
		return subbillerid;
	}

	public void setSubbillerid(String subbillerid) {
		this.subbillerid = subbillerid;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getAccno() {
		return accno;
	}

	public void setAccno(String accno) {
		this.accno = accno;
	}

	public String getAcctype() {
		return acctype;
	}

	public void setAcctype(String acctype) {
		this.acctype = acctype;
	}

	public String getAccholdername() {
		return accholdername;
	}

	public void setAccholdername(String accholdername) {
		this.accholdername = accholdername;
	}

	public String getBankcode() {
		return bankcode;
	}

	public void setBankcode(String bankcode) {
		this.bankcode = bankcode;
	}

	public String getBankname() {
		return bankname;
	}

	public void setBankname(String bankname) {
		this.bankname = bankname;
	}

	public String getBankbranch() {
		return bankbranch;
	}

	public void setBankbranch(String bankbranch) {
		this.bankbranch = bankbranch;
	}

	public String getIfsc() {
		return ifsc;
	}

	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getCurrcode() {
		return currcode;
	}

	public void setCurrcode(String currcode) {
		this.currcode = currcode;
	}

	public String getVpa() {
		return vpa;
	}

	public void setVpa(String vpa) {
		this.vpa = vpa;
	}

	public String getSuccessurl() {
		return successurl;
	}

	public void setSuccessurl(String successurl) {
		this.successurl = successurl;
	}

	public String getFailureurl() {
		return failureurl;
	}

	public void setFailureurl(String failureurl) {
		this.failureurl = failureurl;
	}

	public String getUd1() {
		return ud1;
	}

	public void setUd1(String ud1) {
		this.ud1 = ud1;
	}

	public String getUd2() {
		return ud2;
	}

	public void setUd2(String ud2) {
		this.ud2 = ud2;
	}

	public String getUd3() {
		return ud3;
	}

	public void setUd3(String ud3) {
		this.ud3 = ud3;
	}

	public String getUd4() {
		return ud4;
	}

	public void setUd4(String ud4) {
		this.ud4 = ud4;
	}

	public String getUd5() {
		return ud5;
	}

	public void setUd5(String ud5) {
		this.ud5 = ud5;
	}

	public String getUd6() {
		return ud6;
	}

	public void setUd6(String ud6) {
		this.ud6 = ud6;
	}

	public String getUd7() {
		return ud7;
	}

	public void setUd7(String ud7) {
		this.ud7 = ud7;
	}

	public String getUd8() {
		return ud8;
	}

	public void setUd8(String ud8) {
		this.ud8 = ud8;
	}

	public String getUd9() {
		return ud9;
	}

	public void setUd9(String ud9) {
		this.ud9 = ud9;
	}

	public String getUd10() {
		return ud10;
	}

	public void setUd10(String ud10) {
		this.ud10 = ud10;
	}

	public String getUd11() {
		return ud11;
	}

	public void setUd11(String ud11) {
		this.ud11 = ud11;
	}

	public String getUd12() {
		return ud12;
	}

	public void setUd12(String ud12) {
		this.ud12 = ud12;
	}

	public String getUd13() {
		return ud13;
	}

	public void setUd13(String ud13) {
		this.ud13 = ud13;
	}

	public String getUd14() {
		return ud14;
	}

	public void setUd14(String ud14) {
		this.ud14 = ud14;
	}

	public String getUd15() {
		return ud15;
	}

	public void setUd15(String ud15) {
		this.ud15 = ud15;
	}

	public String getUd16() {
		return ud16;
	}

	public void setUd16(String ud16) {
		this.ud16 = ud16;
	}

	public String getUd17() {
		return ud17;
	}

	public void setUd17(String ud17) {
		this.ud17 = ud17;
	}

	public String getUd18() {
		return ud18;
	}

	public void setUd18(String ud18) {
		this.ud18 = ud18;
	}

	public String getUd19() {
		return ud19;
	}

	public void setUd19(String ud19) {
		this.ud19 = ud19;
	}

	public String getUd20() {
		return ud20;
	}

	public void setUd20(String ud20) {
		this.ud20 = ud20;
	}

	public String getReqdt() {
		return reqdt;
	}

	public void setReqdt(String reqdt) {
		this.reqdt = reqdt;
	}

	public String getDevicetype() {
		return devicetype;
	}

	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}

	public String getOsid() {
		return osid;
	}

	public void setOsid(String osid) {
		this.osid = osid;
	}

	public String getApplicationname() {
		return applicationname;
	}

	public void setApplicationname(String applicationname) {
		this.applicationname = applicationname;
	}

	public String getIptallowed() {
		return iptallowed;
	}

	public void setIptallowed(String iptallowed) {
		this.iptallowed = iptallowed;
	}

	public String getIntentcall() {
		return intentcall;
	}

	public void setIntentcall(String intentcall) {
		this.intentcall = intentcall;
	}

	public String getUpiCollectExpTime() {
		return UpiCollectExpTime;
	}

	public void setUpiCollectExpTime(String upiCollectExpTime) {
		UpiCollectExpTime = upiCollectExpTime;
	}

	public Schemes getSchemes() {
		return schemes;
	}

	public void setSchemes(Schemes schemes) {
		this.schemes = schemes;
	}

	@JsonInclude(Include.NON_NULL)
	public static class Schemes {
		private String tt;
		private String sc;
		private String amt;
		private String scount;
		private List<SchemeDetails> sdtl;

		public String getTt() {
			return tt;
		}

		public void setTt(String tt) {
			this.tt = tt;
		}

		public String getSc() {
			return sc;
		}

		public void setSc(String sc) {
			this.sc = sc;
		}

		public String getAmt() {
			return amt;
		}

		public void setAmt(String amt) {
			this.amt = amt;
		}

		public String getScount() {
			return scount;
		}

		public void setScount(String scount) {
			this.scount = scount;
		}

		public List<SchemeDetails> getSdtl() {
			return sdtl;
		}

		public void setSdtl(List<SchemeDetails> sdtl) {
			this.sdtl = sdtl;
		}
	}

	@JsonInclude(Include.NON_NULL)
	public static class SchemeDetails {
		private String ac;
		private String sc;
		private String amt;

		public String getAc() {
			return ac;
		}

		public void setAc(String ac) {
			this.ac = ac;
		}

		public String getSc() {
			return sc;
		}

		public void setSc(String sc) {
			this.sc = sc;
		}

		public String getAmt() {
			return amt;
		}

		public void setAmt(String amt) {
			this.amt = amt;
		}
	}
}
