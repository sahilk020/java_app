package com.pay10.crm.action;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.BinRangeDao;
import com.pay10.commons.user.User;
import com.pay10.commons.util.BinRange;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DataEncoder;
import com.pay10.crm.actionBeans.BinRangeFilter;

/**
 * @ Neeraj
 */

public class BinRangeDisplayAction extends AbstractSecureAction {

	@Autowired
	private DataEncoder encoder;

	@Autowired
	private CrmValidator validator;

	private static final long serialVersionUID = 730021546498302127L;
	private static Logger logger = LoggerFactory.getLogger(BinRangeDisplayAction.class.getName());
	private List<BinRange> aaData = new ArrayList<BinRange>();
	private User sessionUser = new User();
	private String cardType;
	private String mopType;
	private int draw;
	private int length;
	private int start;
	private BigInteger recordsTotal;
	public BigInteger recordsFiltered;
	public String binRangeLow;
	public String binRangeHigh;
	public String countryType;
//	private String issuerName;
	
	@Autowired
	private BinRangeDao binRangeDao;
	
	public String getBinRangList() {
	
		//BinRangeDisplayServices binRangeDisplayServices = new BinRangeDisplayServices();
		BinRangeFilter binRangeFilter = new BinRangeFilter();
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
	
		try {
			String paymentType = binRangeFilter.getPaymentType(sessionUser, cardType);
			String mopTypes = binRangeFilter.getMopType(sessionUser, mopType);
			//String issuerNames = binRangeFilter.getIssuerName(sessionUser,issuerName);
	
			setRecordsTotal(binRangeDao.getBinRangeTotal(paymentType, mopTypes,binRangeLow,binRangeHigh,countryType, sessionUser));
			if (getLength() == -1) {
				setLength(getRecordsTotal().intValue());
			}
			
			setAaData(encoder.encodeBinRange(binRangeDao.getBinRangeDisplay(paymentType, mopTypes,binRangeLow,binRangeHigh,countryType,
					sessionUser, getStart(), getLength())));
			recordsFiltered = recordsTotal;
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	@Override
	public void validate() {
		if ((validator.validateBlankField(cardType))) {
			addFieldError(CrmFieldType.CARD_TYPE.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.CARD_TYPE, getCardType()))) {
			addFieldError(CrmFieldType.CARD_TYPE.getName(), validator.getResonseObject().getResponseMessage());
		}
		if ((validator.validateBlankField(getMopType()))) {
			addFieldError(CrmFieldType.MOP_TYPE.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.MOP_TYPE, getMopType()))) {
			addFieldError(CrmFieldType.MOP_TYPE.getName(), validator.getResonseObject().getResponseMessage());
		}
		
		if ((validator.validateBlankField(getBinRangeHigh()))) {
		} else if (!(validator.validateField(CrmFieldType.BIN_RANGE_HIGH, getBinRangeHigh()))) {
			addFieldError(CrmFieldType.BIN_RANGE_HIGH.getName(), validator.getResonseObject().getResponseMessage());
		}
		if ((validator.validateBlankField(getBinRangeLow()))) {
		} else if (!(validator.validateField(CrmFieldType.BIN_RANGE_LOW, getBinRangeLow()))) {
			addFieldError(CrmFieldType.BIN_RANGE_LOW.getName(), validator.getResonseObject().getResponseMessage());
		}

	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getMopType() {
		return mopType;
	}

	public void setMopType(String mopType) {
		this.mopType = mopType;
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

	public List<BinRange> getAaData() {
		return aaData;
	}

	public void setAaData(List<BinRange> aaData) {
		this.aaData = aaData;
	}

	public BigInteger getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(BigInteger recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}

	public BigInteger getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(BigInteger recordsTotal) {
		this.recordsTotal = recordsTotal;
	}
	public String getBinRangeLow() {
		return binRangeLow;
	}

	public void setBinRangeLow(String binRangeLow) {
		this.binRangeLow = binRangeLow;
	}
	public String getBinRangeHigh() {
		return binRangeHigh;
	}

	public void setBinRangeHigh(String binRangeHigh) {
		this.binRangeHigh = binRangeHigh;
	}
	public String getCountryType() {
		return countryType;
	}

	public void setCountryType(String countryType) {
		this.countryType = countryType;
	}
	
}
