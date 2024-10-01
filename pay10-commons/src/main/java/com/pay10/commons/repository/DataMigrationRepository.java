package com.pay10.commons.repository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.dto.Account_ChargingDetail;
import com.pay10.commons.user.ChargingDetails;
import com.pay10.commons.user.SurchargeDetails;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.TDRStatus;

@Service
public class DataMigrationRepository {
	private static Logger logger = LoggerFactory.getLogger(DataMigrationRepository.class.getName());

	public void makeQuery() {
		try {
			File tdrSettingFile = new File("C:/Users/Ajay Pal/Desktop/query/tdrSetting.txt");
			File account_tdrSettingFile = new File("C:/Users/Ajay Pal/Desktop/query/Account_TdrSetting.txt");
			File log = new File("C:/Users/Ajay Pal/Desktop/query/log.txt");
			if (tdrSettingFile.exists()) {
				tdrSettingFile.delete();
				tdrSettingFile.createNewFile();
			}

			if (account_tdrSettingFile.exists()) {
				account_tdrSettingFile.delete();
				account_tdrSettingFile.createNewFile();
			}

			if (log.exists()) {
				log.delete();
				log.createNewFile();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		try (Session session = HibernateSessionProvider.getSession();
				BufferedWriter tdrSetting = new BufferedWriter(
						new FileWriter("C:/Users/Ajay Pal/Desktop/query/tdrSetting.txt", true));
				BufferedWriter account_tdrSetting = new BufferedWriter(
						new FileWriter("C:/Users/Ajay Pal/Desktop/query/Account_TdrSetting.txt", true));
				BufferedWriter log = new BufferedWriter(
						new FileWriter("C:/Users/Ajay Pal/Desktop/query/log.txt", true));) {

			String query = "SELECT * FROM account_chargingdetails";
			query = query.replaceAll("\\[", "");
			query = query.replaceAll("\\]", "");
			List account_ChargingDetails = session.createNativeQuery(query).getResultList();

			List<Account_ChargingDetail> details = new ArrayList<>();

			for (int i = 0; i < account_ChargingDetails.size(); i++) {
				Account_ChargingDetail account_ChargingDetail = new Account_ChargingDetail();
				Object[] objects = (Object[]) account_ChargingDetails.get(i);
				account_ChargingDetail.setAccountId(Long.parseLong("" + objects[0]));
				account_ChargingDetail.setChargingDetails_id(Long.parseLong("" + objects[1]));
				details.add(account_ChargingDetail);
			}

			account_tdrSetting.write("INSERT INTO Account_TdrSetting (`Account_id`,`tdrSetting_id`) VALUES ");
			account_tdrSetting.newLine();
			account_tdrSetting.flush();

			tdrSetting.write(
					"INSERT INTO TdrSetting (`id`,`acquirerName`,`minTransactionAmount`,`maxTransactionAmount`,`bankPreference`,`bankTdr`,`bankMinTdrAmt`,`bankMaxTdrAmt`,`merchantPreference`,`merchantTdr`,`merchantMinTdrAmt`,`merchantMaxTdrAmt`,`currency`,`enableSurcharge`,`fromDate`,`igst`,`mopType`,`payId`,`paymentRegion`,`paymentType`,`status`,`tdrStatus`,`toDate`,`transactionType`,`type`,`updatedBy`) VALUES ");
			tdrSetting.newLine();
			tdrSetting.flush();

			int iD = 1000000;
			for (long i = 0; i < details.size(); i++) {
				Account_ChargingDetail account_ChargingDetail = details.get((int) i);

				long size = details.size() - 1;

				CriteriaBuilder cb = session.getCriteriaBuilder();
				CriteriaQuery<ChargingDetails> cq = cb.createQuery(ChargingDetails.class);
				Root<ChargingDetails> root1 = cq.from(ChargingDetails.class);

				cq.select(root1).where(cb.equal(root1.get("id"), account_ChargingDetail.getChargingDetails_id()));

				List<ChargingDetails> resultList = session.createQuery(cq).getResultList();
				ChargingDetails chargingDetails = resultList.get(0);

				String payId = chargingDetails.getPayId();

				String querySur = "SELECT * FROM SurchargeDetails WHERE payId='" + payId + "' and status='" + "ACTIVE"
						+ "' and paymentType='" + PaymentType.getpaymentName(chargingDetails.getPaymentType().getCode()) + "'";

				List resultList1 = session.createNativeQuery(querySur).getResultList();

				long id = chargingDetails.getId();
				String acquirerName = chargingDetails.getAcquirerName();

				double merchantTdr = chargingDetails.getMerchantTDR();
				double bankTdr = chargingDetails.getBankTDR();
				double merchantFixCharge = chargingDetails.getMerchantFixCharge();
				double bankFixCharge = chargingDetails.getBankFixCharge();

				double merchantTDRDomComm = chargingDetails.getMerchantTDRDomComm();
				double bankTDRDomComm = chargingDetails.getBankTDRDomComm();
				double merchantFixChargeDomComm = chargingDetails.getMerchantFixChargeDomComm();
				double bankFixChargeDomComm = chargingDetails.getBankFixChargeDomComm();

				String dateFrom = chargingDetails.getCreatedDate() != null
						? new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(chargingDetails.getCreatedDate())
						: new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
				String dateTo = "2024-03-31 23:59:59";

				double minTransactionAmount = 0;
				double maxTransactionAmount = 10000000;

				String currency = chargingDetails.getCurrency();
				int enableSurcharge = resultList1.size() > 0 ? 1 : 0;

				double igst = chargingDetails.getMerchantServiceTax();
				String mopType = chargingDetails.getMopType().toString();
				String paymentType = chargingDetails.getPaymentType().toString();
				String status = chargingDetails.getStatus().toString();
				String tdrStatus = chargingDetails.getTdrStatus().toString();
				String transactionType = chargingDetails.getTransactionType().toString();
				String updatedBy = "admin@pay10.com";

				String logInfo = "Id : " + id + "   merchantTdr : " + merchantTdr + "   bankTdr:  " + bankTdr
						+ "     merchantFixCharge : " + merchantFixCharge + "    bankFixCharge :" + bankFixCharge
						+ "     merchantTDRDomComm : " + merchantTDRDomComm + "  bankTDRDomComm :" + bankTDRDomComm
						+ "   merchantFixChargeDomComm :"+merchantFixChargeDomComm+"    bankFixChargeDomComm : "+bankFixChargeDomComm;
				log.write(logInfo);
				log.newLine();
				log.flush();

				if (merchantTdr != 0 || bankTdr != 0) {
					tdrSetting.write("(" + id + ",'" + acquirerName + "'," + "" + minTransactionAmount + "," + ""
							+ 10000000 + "," + "'" + "PERCENTAGE" + "'," + "" + bankTdr + "," + "" + 0 + "," + ""
							+ 10000000 + "," + "'" + "PERCENTAGE" + "'," + "" + merchantTdr + "," + "" + 0 + ","
							+ "" + 10000000 + "," + "'" + currency + "'," + "" + enableSurcharge + "," + "'" + dateFrom
							+ "'," + "" + igst + "," + "'" + mopType + "'," + "'" + payId + "'," + "'" + "DOMESTIC"
							+ "'," + "'" + paymentType + "'," + "'" + status + "'," + "'" + tdrStatus + "'," + "'"
							+ dateTo + "'," + "'" + transactionType + "'," + "'" + "CONSUMER" + "'," + "'" + updatedBy
							+ "'" + "),");
					tdrSetting.newLine();
					tdrSetting.flush();
					
					account_tdrSetting.write("(" + account_ChargingDetail.getAccountId() + ","
							+ account_ChargingDetail.getChargingDetails_id() + "),");
					account_tdrSetting.newLine();
					account_tdrSetting.flush();
				} else {
					if (merchantFixCharge != 0 || bankFixCharge != 0) {
						tdrSetting.write("(" + id + ",'" + acquirerName + "'," + "" + minTransactionAmount + "," + ""
								+ 10000000 + "," + "'" + "FLAT" + "'," + "" + bankFixCharge + "," + "" + 0 + "," + ""
								+ 10000000 + "," + "'" + "FLAT" + "'," + "" + merchantFixCharge + "," + "" + 0 + ","
								+ "" + 10000000 + "," + "'" + currency + "'," + "" + enableSurcharge + "," + "'"
								+ dateFrom + "'," + "" + igst + "," + "'" + mopType + "'," + "'" + payId + "'," + "'"
								+ "DOMESTIC" + "'," + "'" + paymentType + "'," + "'" + status + "'," + "'" + tdrStatus
								+ "'," + "'" + dateTo + "'," + "'" + transactionType + "'," + "'" + "CONSUMER" + "',"
								+ "'" + updatedBy + "'" + "),");
						tdrSetting.newLine();
						tdrSetting.flush();
						
						account_tdrSetting.write("(" + account_ChargingDetail.getAccountId() + ","
								+ account_ChargingDetail.getChargingDetails_id() + "),");
						account_tdrSetting.newLine();
						account_tdrSetting.flush();
					}
				}

				if ((merchantTdr != 0 && merchantTDRDomComm != 0) || (bankTdr != 0 && bankTDRDomComm != 0)) {
					tdrSetting.write("(" + iD + ",'" + acquirerName + "'," + "" + minTransactionAmount + "," + ""
							+ 10000000 + "," + "'" + "PERCENTAGE" + "'," + "" + bankTDRDomComm + "," + "" + 0 + "," + ""
							+ 10000000 + "," + "'" + "PERCENTAGE" + "'," + "" + merchantTDRDomComm + "," + "" + 0 + ","
							+ "" + 10000000 + "," + "'" + currency + "'," + "" + enableSurcharge + "," + "'" + dateFrom
							+ "'," + "" + igst + "," + "'" + mopType + "'," + "'" + payId + "'," + "'" + "DOMESTIC"
							+ "'," + "'" + paymentType + "'," + "'" + status + "'," + "'" + tdrStatus + "'," + "'"
							+ dateTo + "'," + "'" + transactionType + "'," + "'" + "COMMERCIAL" + "'," + "'" + updatedBy
							+ "'" + "),");
					tdrSetting.newLine();
					tdrSetting.flush();
					
					
					
					account_tdrSetting.write("(" + account_ChargingDetail.getAccountId() + "," + iD + "),");
					account_tdrSetting.newLine();
					account_tdrSetting.flush();
					iD++;
				} else {
					if ((merchantFixCharge != 0 && merchantFixChargeDomComm != 0)
							|| (bankFixCharge != 0 && bankFixChargeDomComm != 0)) {
						tdrSetting.write("(" + iD + ",'" + acquirerName + "'," + "" + minTransactionAmount + "," + ""
								+ 10000000 + "," + "'" + "FLAT" + "'," + "" + bankFixChargeDomComm + "," + "" + 0 + "," + ""
								+ 10000000 + "," + "'" + "FLAT" + "'," + "" + merchantFixChargeDomComm + "," + "" + 0 + ","
								+ "" + 10000000 + "," + "'" + currency + "'," + "" + enableSurcharge + "," + "'"
								+ dateFrom + "'," + "" + igst + "," + "'" + mopType + "'," + "'" + payId + "'," + "'"
								+ "DOMESTIC" + "'," + "'" + paymentType + "'," + "'" + status + "'," + "'" + tdrStatus
								+ "'," + "'" + dateTo + "'," + "'" + transactionType + "'," + "'" + "COMMERCIAL" + "',"
								+ "'" + updatedBy + "'" + "),");
						tdrSetting.newLine();
						tdrSetting.flush();
						
						
						
						account_tdrSetting.write("(" + account_ChargingDetail.getAccountId() + "," + iD + "),");
						account_tdrSetting.newLine();
						account_tdrSetting.flush();
						iD++;
					}
				}
				if (merchantTdr == 0 && bankTdr == 0 && merchantTDRDomComm == 0 && bankTDRDomComm == 0
						&& merchantFixCharge == 0 && bankFixCharge == 0 && merchantFixChargeDomComm == 0
						&& bankFixChargeDomComm == 0) {
					tdrSetting.write("(" + id + ",'" + acquirerName + "'," + "" + minTransactionAmount + "," + ""
							+ 10000000 + "," + "'" + "PERCENTAGE" + "'," + "" + bankTdr + "," + "" + 0 + "," + ""
							+ 10000000 + "," + "'" + "PERCENTAGE" + "'," + "" + merchantTdr + "," + "" + 0 + "," + ""
							+ 10000000 + "," + "'" + currency + "'," + "" + enableSurcharge + "," + "'" + dateFrom
							+ "'," + "" + igst + "," + "'" + mopType + "'," + "'" + payId + "'," + "'" + "DOMESTIC"
							+ "'," + "'" + paymentType + "'," + "'" + status + "'," + "'" + tdrStatus + "'," + "'"
							+ dateTo + "'," + "'" + transactionType + "'," + "'" + "CONSUMER" + "'," + "'" + updatedBy
							+ "'" + "),");
					tdrSetting.newLine();
					tdrSetting.flush();
					account_tdrSetting.write("(" + account_ChargingDetail.getAccountId() + ","
							+ account_ChargingDetail.getChargingDetails_id() + "),");
					account_tdrSetting.newLine();
					account_tdrSetting.flush();
				}
//				if (i != size) {
//					account_tdrSetting.write("(" + account_ChargingDetail.getAccountId() + ","
//							+ account_ChargingDetail.getChargingDetails_id() + "),");
//					account_tdrSetting.newLine();
//					account_tdrSetting.flush();
//				} else {
//					account_tdrSetting.write("(" + account_ChargingDetail.getAccountId() + ","
//							+ account_ChargingDetail.getChargingDetails_id() + ");");
//					account_tdrSetting.newLine();
//					account_tdrSetting.flush();
//				}
			}
			System.out.println("DONE");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
