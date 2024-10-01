package com.pay10.crm.action;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.BeneficiaryAccountsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.BeneficiaryAccounts;
import com.pay10.commons.util.AccountStatus;
import com.pay10.commons.util.AcquirerTypeNodal;
import com.pay10.commons.util.BeneficiaryTypes;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DataEncoder;
import com.pay10.commons.util.NodalPaymentTypes;

public class NodalPayoutSearchAction extends AbstractSecureAction {

	private static final long serialVersionUID = 6882047577679317908L;

	private static Logger logger = LoggerFactory.getLogger(NodalPayoutSearchAction.class.getName());

	@Autowired
	private BeneficiaryAccountsDao beneficiaryAccountsDao;

	@Autowired
	private CrmValidator validator;

	@Autowired
	private DataEncoder encoder;
	
	private int draw;
	private int length;
	private int start;
	private String acquirer;
	private String paymentType;
	private String merchantPayId;
	private String beneficiaryCd;
	private String beneName;
	private String emailId;
	private String mobile;
	private String beneficiaryType;
	private BigInteger recordsTotal = BigInteger.ZERO;
	private BigInteger recordsFiltered = BigInteger.ZERO;
	private List<BeneficiaryAccounts> aaData;

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		logger.info("Loading Beneficiary List for payout");
		try {
			Map<String, Object> map = beneficiaryAccountsDao.getBeneficiaryListGroupedBeneficiaryCode(acquirer,
					merchantPayId, beneficiaryCd, beneName, paymentType, null, getMobile(), getEmailId(), AccountStatus.ACTIVE.getName(), start, length, getBeneficiaryType());
			
			List<String> beneCodeList = (List<String>) map.get("list");
			aaData = new ArrayList<>();
			for (String beneCode : beneCodeList) {
				List<BeneficiaryAccounts> beneAccountList = beneficiaryAccountsDao
						.findByBeneficiaryCdAndStatusAndPaymentTypeAndPayId(beneCode, null, null, AccountStatus.ACTIVE.getName());
				BeneficiaryAccounts finalBeneAccount = null;
				JSONArray benePaymentTypes = new JSONArray();

				for (BeneficiaryAccounts beneAccount : beneAccountList) {
					benePaymentTypes.put(NodalPaymentTypes.getInstancefromCode(beneAccount.getPaymentType().getCode()).getName());
				}
				if (!beneAccountList.isEmpty()) {
					finalBeneAccount = beneAccountList.get(0);
					finalBeneAccount.setActions(benePaymentTypes.toString());
					aaData.add(finalBeneAccount);
				}
			}
//			setAaData(encoder.encodeBeneficiaryAccountObj(aaData));
			BigInteger bigInt = BigInteger.valueOf((int) map.get("count"));
			setRecordsTotal(bigInt);
			setRecordsFiltered(bigInt);
			return SUCCESS;
		} catch (Exception e) {
			logger.error("Failed to load beneficiaries for nodal payout.");
			logger.error(e.getMessage());
			return ERROR;
		}
	}

	@Override
	public void validate() {
//		setBeneficiaryType(BeneficiaryTypes.Vendor.getCode());
		// Bene Code : ALPHANUM are allowed
		if (validator.validateBlankField(getBeneficiaryCd())) {
		} else if (!(validator.validateField(CrmFieldType.BENEFICIARY_CD, getBeneficiaryCd()))
				&& !beneficiaryCd.equalsIgnoreCase("ALL")) {
			addFieldError(CrmFieldType.BENEFICIARY_CD.getName(), validator.getResonseObject().getResponseMessage());
		}
		// Bene name : Alpha numeric
		if (validator.validateBlankField(getBeneName())) {
		} else if (!(validator.validateField(CrmFieldType.BENE_NAME, getBeneName()))
				&& !beneName.equalsIgnoreCase("ALL")) {
			addFieldError(CrmFieldType.BENE_NAME.getName(), validator.getResonseObject().getResponseMessage());
		}

		if (validator.validateBlankField(getAcquirer())) {
		} else if ((AcquirerTypeNodal.getAcquirerName(getAcquirer()) == null) && !acquirer.equalsIgnoreCase("ALL")) {
			addFieldError(CrmFieldType.BENE_NODAL_ACQUIRER.getName(), validator.getResonseObject().getResponseMessage());
		}
		if (validator.validateBlankFields(getPaymentType())) {
		} else if ((NodalPaymentTypes.getInstancefromCode(getPaymentType()) == null)
				&& !paymentType.equalsIgnoreCase("ALL")) {
			addFieldError(CrmFieldType.PAYMENT_TYPE.getName(), validator.getResonseObject().getResponseMessage());
		}
		
		if (validator.validateBlankField(getMobile())) {
		} else if (!(validator.validateField(CrmFieldType.BENE_MOBILE_NUMBER, getMobile()))) {
			addFieldError(CrmFieldType.BENE_MOBILE_NUMBER.getName(), validator.getResonseObject().getResponseMessage());
		}
		
		if (validator.validateBlankField(getEmailId())) {
		} else if (!(validator.validateField(CrmFieldType.BENE_EMAIL_ID, getEmailId()))) {
			addFieldError(CrmFieldType.BENE_EMAIL_ID.getName(), validator.getResonseObject().getResponseMessage());
		}
		
		if (validator.validateBlankField(getBeneficiaryType())) {
			addFieldError(CrmFieldType.BENE_TYPE.getName(), ErrorType.EMPTY_FIELD.getResponseMessage());
		} else if ((BeneficiaryTypes.getInstancefromCode(getBeneficiaryType()) == null)) {
			addFieldError(CrmFieldType.BENE_TYPE.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
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

	public List<BeneficiaryAccounts> getAaData() {
		return aaData;
	}

	public void setAaData(List<BeneficiaryAccounts> aaData) {
		this.aaData = aaData;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getMerchantPayId() {
		return merchantPayId;
	}

	public void setMerchantPayId(String merchantPayId) {
		this.merchantPayId = merchantPayId;
	}

	public String getBeneficiaryCd() {
		return beneficiaryCd;
	}

	public void setBeneficiaryCd(String beneficiaryCd) {
		this.beneficiaryCd = beneficiaryCd;
	}

	public String getBeneName() {
		return beneName;
	}

	public void setBeneName(String beneName) {
		this.beneName = beneName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getBeneficiaryType() {
		return beneficiaryType;
	}

	public void setBeneficiaryType(String beneficiaryType) {
		this.beneficiaryType = beneficiaryType;
	}

}
