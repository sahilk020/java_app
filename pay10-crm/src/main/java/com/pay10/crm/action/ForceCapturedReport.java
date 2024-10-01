package com.pay10.crm.action;

import com.pay10.commons.action.AbstractAction;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.TDRBifurcationReportDetails;
import com.pay10.commons.user.TransactionSearchNew;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DataEncoder;
import com.pay10.crm.mongoReports.TransactionStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ForceCapturedReport extends AbstractSecureAction {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2501457775436460044L;
	//    List<String> statuss = new ArrayList<>();
    List<Merchants> merchantList = new ArrayList<>();
    List<String> ruleTypee = new ArrayList<>();
    private int draw;
    private int length;
    private int start;

    private BigInteger recordsTotal = BigInteger.ZERO;
    private BigInteger recordsFiltered = BigInteger.ZERO;

    private String merchant;
    private String dateRange;
    private String status;
    private String ruleType;
    private String acquirer;
    private String dateFrom;
    private String dateTo;

    @Autowired
    private TransactionStatus transactionStatus;
    @Autowired
    private DataEncoder encoder;
    @Autowired
    private UserDao userDao;

    HttpServletRequest request = ServletActionContext.getRequest();

    HttpServletResponse response = ServletActionContext.getResponse();
    //tdrBifurcationReportDetails
    

    private List<TransactionSearchNew> aaData = new ArrayList<TransactionSearchNew>();
    @Override
    public String execute() {

        String segment = (String) sessionMap.get(Constants.SEGMENT.getValue());
        User user = (User) sessionMap.get(Constants.USER.getValue());
        long roleId = user.getRole().getId();
        setMerchantList(userDao.getActiveMerchant(segment, roleId));
        return SUCCESS;
    }

    public List<Merchants> getMerchantList() {
        return merchantList;
    }

    public void setMerchantList(List<Merchants> merchantList) {
        this.merchantList = merchantList;
    }
    public List<Merchants> merchantList() {
        return merchantList;
    }

//    public ForceCapturedReport setMerchantList(List<Merchants> merchantList) {
//        this.merchantList = merchantList;
//        return this;
//    }

    public List<String> getRuleTypee() {
        return ruleTypee;
    }

    public void setRuleTypee(List<String> ruleTypee) {
        this.ruleTypee = ruleTypee;
    }


    public String getAcquirer() {
        return acquirer;
    }

    public void setAcquirer(String acquirer) {
        this.acquirer = acquirer;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getDateRange() {
        return dateRange;
    }

    public void setDateRange(String dateRange) {
        this.dateRange = dateRange;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public BigInteger getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(BigInteger recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public BigInteger getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(BigInteger recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public List<TransactionSearchNew> getAaData() {
        return aaData;
    }

    public void setAaData(List<TransactionSearchNew> aaData) {
        this.aaData = aaData;
    }

}