package com.pay10.crm.action;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pay10.commons.mongo.SettlementPORepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.pay10.commons.dto.SettlementDTO;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.MultCurrencyCode;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.PayoutBankCodeconfigurationDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.TransactionManager;
import com.pay10.crm.po.service.MerchantSettlementPOService;

@SuppressWarnings("serial")
public class MerchantSettlementPOAction extends AbstractSecureAction {

	private static Logger logger = LoggerFactory.getLogger(MerchantSettlementPOAction.class.getName());


	@Autowired
	private MultCurrencyCodeDao multiCurrencyCodeDao;
	@Autowired
	private MerchantSettlementPOService merchantSettlementPOService;
	@Autowired
	private UserDao userDao;
	private List<Merchants> merchantList = new ArrayList<Merchants>();
	private List<MultCurrencyCode> currencyList = new ArrayList<>();
	private List<Object> aaData = new ArrayList<Object>();
	private User sessionUser = null;

	private String merchant;
	private String currency;
	private String amount;
	private String balance;

	private File file;
	private String fileUploadMsg;
	private String txnId;

	private SettlementDTO settlementDTO;
	
	@Autowired
	private PayoutBankCodeconfigurationDao payoutBankCodeconfigurationDao;
	

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String response;
	
	private Map<String, String> bankDetails=new HashMap<>();

	@Override
	public String execute() {
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public String merchantPage() {


		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		if (!sessionUser.getUserType().name().equalsIgnoreCase("MERCHANT")) {

			List<User> users = userDao.fetchUsersBasedOnPOEnable();
			users.stream().forEach(user -> {
				Merchants merchants=new Merchants();
				merchants.setMerchant(user);
				merchantList.add(merchants);
			});

			setCurrencyList(multiCurrencyCodeDao.getCurrencyCode());
			
		} else {
			List<User> users = userDao.fetchUsersBasedOnPOEnable();
			users.stream().forEach(user -> {
				if(sessionUser.getPayId().equalsIgnoreCase(user.getPayId())) {
					Merchants merchants=new Merchants();
					merchants.setMerchant(sessionUser);
					merchantList.add(merchants);
				}
			});

			List<String> currency1 = userDao.findCurrencyByPayId(sessionUser.getPayId());
			for(String data: currency1){
				MultCurrencyCode currencyData = multiCurrencyCodeDao.findByCode(data);
				currencyList.add(currencyData);
			}
		}


		return SUCCESS;
	}

	public String createSettlementPayout() {
		logger.info("createSettlementPayout>>>>> : " + settlementDTO.getPayId());
		User user = userDao.findByEmailId(settlementDTO.getPayId());
		if (user != null) {
			settlementDTO.setPayId(user.getPayId());
			settlementDTO.setUid(TransactionManager.getNewTransactionId());
			settlementDTO.setMerchantName(user.getBusinessName());
			settlementDTO.setMode(StringUtils.isNotBlank(settlementDTO.getMode())?settlementDTO.getMode():"NA");
			settlementDTO.setAccountHolderName(StringUtils.isNotBlank(settlementDTO.getAccountHolderName())?settlementDTO.getAccountHolderName():"NA");
			settlementDTO.setAccountNo(StringUtils.isNotBlank(settlementDTO.getAccountNo())?settlementDTO.getAccountNo():"NA");
			settlementDTO.setBankName(StringUtils.isNotBlank(settlementDTO.getBankName())?settlementDTO.getBankName():"NA");
			settlementDTO.setIfsc(StringUtils.isNotBlank(settlementDTO.getIfsc())?settlementDTO.getIfsc():"NA");
			settlementDTO.setStatus("PENDING");
			settlementDTO.setSettlementDate(dateFormat.format(new Date()));
			settlementDTO.setSubmitDate(dateFormat.format(new Date()));

			setResponse(merchantSettlementPOService.createPayout(settlementDTO,request));
		} else {
			setResponse("There is some Problem");
		}
		return SUCCESS;
	}
	
	
	public String createSettlement() {
		logger.info("createSettlement>>>>> : " + settlementDTO.getPayId());
		User user = userDao.findByEmailId(settlementDTO.getPayId());
		if (user != null) {
			settlementDTO.setPayId(user.getPayId());
			settlementDTO.setUid(TransactionManager.getNewTransactionId());
			settlementDTO.setMerchantName(user.getBusinessName());
			settlementDTO.setMode(StringUtils.isNotBlank(settlementDTO.getMode())?settlementDTO.getMode():"NA");
			settlementDTO.setAccountHolderName(StringUtils.isNotBlank(settlementDTO.getAccountHolderName())?settlementDTO.getAccountHolderName():"NA");
			settlementDTO.setAccountNo(StringUtils.isNotBlank(settlementDTO.getAccountNo())?settlementDTO.getAccountNo():"NA");
			settlementDTO.setBankName(StringUtils.isNotBlank(settlementDTO.getBankName())?settlementDTO.getBankName():"NA");
			settlementDTO.setIfsc(StringUtils.isNotBlank(settlementDTO.getIfsc())?settlementDTO.getIfsc():"NA");
			settlementDTO.setStatus("PENDING");
			settlementDTO.setSettlementDate(dateFormat.format(new Date()));
			settlementDTO.setSubmitDate(dateFormat.format(new Date()));

			setResponse(merchantSettlementPOService.createSettlementFromPanel(settlementDTO,request));
		} else {
			setResponse("There is some Problem");
		}
		return SUCCESS;
	}
	
	

	public String settlementMerchantRequest() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		User user = userDao.findByEmailId(merchant);
		if (!currency.equalsIgnoreCase("ALL")) {
			currency = multiCurrencyCodeDao.getCurrencyNamebyCode(currency);
		}
//		logger.info("settlementMerchantRequest : " + new Gson().toJson(merchantSettlementPOService.getMerchantSettlementDetails(user, currency)));
		setAaData(merchantSettlementPOService.getMerchantSettlementDetails(user, currency));

		return SUCCESS;
	}

	public String merchantFund() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		User user=userDao.findByEmailId(merchant);
		setBalance(merchantSettlementPOService.getMerchantAvailableBalance(user.getPayId(),multiCurrencyCodeDao.getCurrencyNamebyCode(currency)));
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public String adminPage() {
		setCurrencyList(multiCurrencyCodeDao.getCurrencyCode());
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		if (!sessionUser.getUserType().name().equalsIgnoreCase("MERCHANT")) {
			setMerchantList(userDao.getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(),
					sessionUser.getRole().getId()));
		} else {
			Merchants merchants = new Merchants();
			merchants.setMerchant(sessionUser);
			merchantList.clear();
			merchantList.add(merchants);
			setMerchantList(merchantList);
		}
		return SUCCESS;
	}

	public String settlementAdminRequest() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		User user=null;
		currency="ALL";
		setAaData(merchantSettlementPOService.getAdminSettlementDetailsPO(user, currency));
		return SUCCESS;
	}
	
	public String settlementAdminRequestDetails() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		User user=null;
		currency="ALL";
		setAaData(merchantSettlementPOService.getAdminSettlementDetails(user, currency));
		return SUCCESS;
	}

	public String settlementAdminApprove() {
		String payId = request.getParameter("payId");
		String pgRefNum= request.getParameter("pgRefNum");
		logger.info(payId + pgRefNum);
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		String response = merchantSettlementPOService.approveSettlementByTxnId(txnId, payId, pgRefNum, sessionUser.getEmailId());
		setFileUploadMsg(response);
		return SUCCESS;
	}
	
	public String settlementAdminApproveDetail() {
		String payId = request.getParameter("payId");
		String currency= request.getParameter("currency");
		String uid = request.getParameter("uid");
		logger.info("PayId :"+payId + "Currency :"+currency + "Uid :"+uid);
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		String response = merchantSettlementPOService.approveSettlementByTxnIdDetail(txnId, payId, currency, sessionUser.getEmailId(),uid);
		setFileUploadMsg(response);
		return SUCCESS;
	}
	
	public String settlementAdminReject() {
		String payId = request.getParameter("payId");
		String pgRefNum= request.getParameter("pgRefNum");
		String amount = request.getParameter("amount");
		String currency = request.getParameter("currency");
		logger.info(payId + pgRefNum);
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());

		String response = merchantSettlementPOService.rejectSettlementByUid(payId, pgRefNum, sessionUser.getEmailId(),amount,currency);
		setFileUploadMsg(response);

		return SUCCESS;
	}
	
	public String settlementAdminRejectDetail() {
		String payId = request.getParameter("payId");
		String currency= request.getParameter("currency");
		String uid = request.getParameter("uid");
		logger.info("PayId :"+payId + "Currency :"+currency + "Uid :"+uid);
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());

		String response = merchantSettlementPOService.rejectSettlementByUidDetail(payId, currency, sessionUser.getEmailId(),uid);
		setFileUploadMsg(response);

		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public String singlePayoutPageMerchant() {

		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		List<String> currency1 = userDao.findCurrencyByPayId(sessionUser.getPayId());
		for(String data: currency1){
			MultCurrencyCode currencyData = multiCurrencyCodeDao.findByCode(data);
			currencyList.add(currencyData);
		}
		bankDetails.clear();
		payoutBankCodeconfigurationDao.getDetailForUI().stream().forEach(bankDetail->{
			bankDetails.put(bankDetail.getPgbankcode(), bankDetail.getBankName());
		});

		Merchants merchants = new Merchants();
		merchants.setMerchant(sessionUser);
		merchantList.clear();
		merchantList.add(merchants);
		setMerchantList(merchantList);

		return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	public String singlePayoutPageAdmin() {
		setCurrencyList(multiCurrencyCodeDao.getCurrencyCode());
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		if (!sessionUser.getUserType().name().equalsIgnoreCase("MERCHANT")) {
			setMerchantList(userDao.getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(),
					sessionUser.getRole().getId()));
		}
		return SUCCESS;
	}

	public List<Merchants> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}

	public List<MultCurrencyCode> getCurrencyList() {
		return currencyList;
	}

	public void setCurrencyList(List<MultCurrencyCode> currencyList) {
		this.currencyList = currencyList;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public List<Object> getAaData() {
		return aaData;
	}

	public void setAaData(List<Object> aaData) {
		this.aaData = aaData;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileUploadMsg() {
		return fileUploadMsg;
	}

	public void setFileUploadMsg(String fileUploadMsg) {
		this.fileUploadMsg = fileUploadMsg;
	}

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public SettlementDTO getSettlementDTO() {
		return settlementDTO;
	}

	public void setSettlementDTO(SettlementDTO settlementDTO) {
		this.settlementDTO = settlementDTO;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public Map<String, String> getBankDetails() {
		return bankDetails;
	}

	public void setBankDetails(Map<String, String> bankDetails) {
		this.bankDetails = bankDetails;
	}
	
}
