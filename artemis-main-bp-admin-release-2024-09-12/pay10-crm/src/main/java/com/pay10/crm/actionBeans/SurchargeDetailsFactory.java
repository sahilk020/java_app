package com.pay10.crm.actionBeans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.ServiceTaxDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.SurchargeDetails;
import com.pay10.commons.user.SurchargeDetailsDao;
import com.pay10.crm.action.AbstractSecureAction;

@Service
public class SurchargeDetailsFactory extends AbstractSecureAction {

	@Autowired
	private ServiceTaxDao serviceTaxDao;

	@Autowired
	private SurchargeDetailsDao surchargeDetailsDao;

	private static final long serialVersionUID = 2500609617979420055L;

	public Map<String, List<SurchargeDetails>> getSurchargeDetails(String payId, String paymentTypeName) {

		List<SurchargeDetails> detail = new ArrayList<SurchargeDetails>();

		BigDecimal serviceTax = serviceTaxDao.findServiceTaxByPayId(payId);
		if (serviceTax == null) {
			addActionMessage(ErrorType.PERMISSION_DENIED.getResponseMessage());
		}

		SurchargeDetails surchargeDetailsDomestic = surchargeDetailsDao.findDetailsByRegion(payId, paymentTypeName,
				AccountCurrencyRegion.DOMESTIC.toString());

		if (surchargeDetailsDomestic != null) {
			surchargeDetailsDomestic.setServiceTax(serviceTax);
			detail.add(surchargeDetailsDomestic);
		} else {
			SurchargeDetails blankDetailDom = new SurchargeDetails();
			blankDetailDom.setServiceTax(serviceTax);
			blankDetailDom.setSurchargeAmount(BigDecimal.ZERO);
			blankDetailDom.setSurchargePercentage(BigDecimal.ZERO);
			blankDetailDom.setMinTransactionAmount(BigDecimal.ZERO);
			blankDetailDom.setPaymentsRegion(AccountCurrencyRegion.DOMESTIC);

			detail.add(blankDetailDom);
		}

		SurchargeDetails surchargeDetailsInternational = surchargeDetailsDao.findDetailsByRegion(payId, paymentTypeName,
				AccountCurrencyRegion.INTERNATIONAL.toString());

		if (surchargeDetailsInternational != null) {
			
			surchargeDetailsInternational.setServiceTax(serviceTax);
			detail.add(surchargeDetailsInternational);
		} else {
			SurchargeDetails blankDetailInt = new SurchargeDetails();
			blankDetailInt.setServiceTax(serviceTax);
			blankDetailInt.setSurchargeAmount(BigDecimal.ZERO);
			blankDetailInt.setSurchargePercentage(BigDecimal.ZERO);
			blankDetailInt.setMinTransactionAmount(BigDecimal.ZERO);
			blankDetailInt.setPaymentsRegion(AccountCurrencyRegion.INTERNATIONAL);

			detail.add(blankDetailInt);

		}

		Map<String, List<SurchargeDetails>> detailsMap = new HashMap<String, List<SurchargeDetails>>();
		detailsMap.put(paymentTypeName, detail);

		return detailsMap;
	}

}
