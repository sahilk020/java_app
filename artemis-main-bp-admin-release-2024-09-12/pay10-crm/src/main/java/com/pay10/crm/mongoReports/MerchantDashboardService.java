package com.pay10.crm.mongoReports;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bson.BsonNull;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.crm.actionBeans.MerchantDashboardBean;
import com.pay10.crm.actionBeans.TransactionStatusBean;

@Service
public class MerchantDashboardService {
	@Autowired
	private MongoInstance mongoInstance;
	private static final String prefix = "MONGO_DB_";
	private static Logger logger = LoggerFactory.getLogger(MerchantDashboardService.class.getName());
	List<TransactionStatusBean> transactionStatusBeans = null;

	public String[] getDateBetween(String date_range) {
		// 07/06/2022 00:00 - 06/07/2022 23:59
		String[] date = new String[2];
		String splitdate[] = date_range.split(" ");
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Date date1 = dateFormat.parse(splitdate[0] + " " + splitdate[1]);
			date1.setSeconds(00);

			Date date2 = dateFormat.parse(splitdate[3] + " " + splitdate[4]);
			date2.setSeconds(59);

			date[0] = dateFormat1.format(date1);
			date[1] = dateFormat1.format(date2);

		} catch (Exception e) {
			logger.info("Exception Occur in class MerchantDashboardService in getDateBetween(): " + e.getMessage());
			e.printStackTrace();
		}
		return date;
	}

	public String getDashBoardDetail(String date_range, String merchant, String acquirer, String paymentMethods,
			List<String> payIdList, String moptype) {
		logger.info("MerchantDashboardServices in getDashBoardDetail() \ndate_range : " + date_range + "\tmerchant : "
				+ merchant + "\tacquirer: " + acquirer + "\tPayment_Type : " + paymentMethods + "\t Pay_ID Size :"
				+ payIdList.size() + "\tMop Type : " + moptype);

		MerchantDashboardBean merchantDashboardBean = null;
		try {
			DecimalFormat decimalFormat = new DecimalFormat();
			decimalFormat.setMaximumFractionDigits(2);
			String[] date = getDateBetween(date_range);
			logger.info("MerchantDashboardServices in getDashBoardDetail(): \nstarting Date : " + date[0]
					+ "\t Ending Date : " + date[1] + "\tmerchant : " + merchant + "\tacquirer: " + acquirer
					+ "\tPayment_Type : " + paymentMethods + "\t Pay_ID Size :" + payIdList.size() + "\tMop Type : "
					+ moptype);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			MongoCursor<Document> capture = getCaptureTxn(merchant, acquirer, paymentMethods, date, collection,
					payIdList, moptype);
			MongoCursor<Document> pending = getPendingTxn(merchant, acquirer, paymentMethods, date, collection,
					payIdList, moptype);
			MongoCursor<Document> harddecline = getHardDeclineTxn(merchant, acquirer, paymentMethods, date, collection,
					payIdList, moptype);
			MongoCursor<Document> softdecline = getSoftDeclineTxn(merchant, acquirer, paymentMethods, date, collection,
					payIdList, moptype);

			merchantDashboardBean = new MerchantDashboardBean();

			logger.info("MerchantDashboardServices in getDashBoardDetail()-------------Iterate capture transaction");
			if (capture != null) {
				while (capture.hasNext()) {
					Document document = capture.next();
					String payment_type = document.getString("PAYMENT_TYPE");
					if (payment_type != null) {
						if (payment_type.equalsIgnoreCase("NB")) {
							merchantDashboardBean.setCapture_nb_count(document.getLong("TotalCount"));
							merchantDashboardBean.setCapture_nb_amount(Double.parseDouble(
									decimalFormat.format(document.get("TotalAmount")).replaceAll(",", "")));
						} else if (payment_type.equalsIgnoreCase("UP")) {
							merchantDashboardBean.setCapture_upi_count(document.getLong("TotalCount"));
							merchantDashboardBean.setCapture_upi_amount(Double.parseDouble(
									decimalFormat.format(document.get("TotalAmount")).replaceAll(",", "")));
						} else if (payment_type.equalsIgnoreCase("DC")) {
							merchantDashboardBean.setCapture_dc_count(document.getLong("TotalCount"));
							merchantDashboardBean.setCapture_dc_amount(Double.parseDouble(
									decimalFormat.format(document.get("TotalAmount")).replaceAll(",", "")));
						} else if (payment_type.equalsIgnoreCase("CC")) {
							merchantDashboardBean.setCapture_cc_count(document.getLong("TotalCount"));
							merchantDashboardBean.setCapture_cc_amount(Double.parseDouble(
									decimalFormat.format(document.get("TotalAmount")).replaceAll(",", "")));
						} else if (payment_type.equalsIgnoreCase("WL")) {
							merchantDashboardBean.setCapture_wallet_count(document.getLong("TotalCount"));
							merchantDashboardBean.setCapture_wallet_amount(Double.parseDouble(
									decimalFormat.format(document.get("TotalAmount")).replaceAll(",", "")));
						}
					}
				}
				capture.close();
				merchantDashboardBean.setCapture_total_count(merchantDashboardBean.getCapture_cc_count()
						+ merchantDashboardBean.getCapture_dc_count() + merchantDashboardBean.getCapture_nb_count()
						+ merchantDashboardBean.getCapture_wallet_count()
						+ merchantDashboardBean.getCapture_upi_count());
				merchantDashboardBean
						.setCapture_total_amount(
								Double.parseDouble(
										decimalFormat
												.format(merchantDashboardBean.getCapture_cc_amount()
														+ merchantDashboardBean.getCapture_dc_amount()
														+ merchantDashboardBean.getCapture_nb_amount()
														+ merchantDashboardBean.getCapture_wallet_amount()
														+ merchantDashboardBean.getCapture_upi_amount())
												.replaceAll(",", "")));
			}

			logger.info(
					"MerchantDashboardServices in getDashBoardDetail()-------------Iterate hard declined transaction");
			if (harddecline != null) {
				while (harddecline.hasNext()) {
					Document document = harddecline.next();
					String payment_type = document.getString("PAYMENT_TYPE");
					if (payment_type == null) {
						merchantDashboardBean.setHarddecline_null_count(document.getLong("TotalCount"));
						merchantDashboardBean.setHarddecline_null_amount(Double.parseDouble(
								decimalFormat.format(document.get("TotalAmount")).replaceAll(",", "")));
					} else {
						if (payment_type.equalsIgnoreCase("NB")) {
							merchantDashboardBean.setHarddecline_nb_count(document.getLong("TotalCount"));
							merchantDashboardBean.setHarddecline_nb_amount(Double.parseDouble(
									decimalFormat.format(document.get("TotalAmount")).replaceAll(",", "")));
						} else if (payment_type.equalsIgnoreCase("UP")) {
							merchantDashboardBean.setHarddecline_upi_count(document.getLong("TotalCount"));
							merchantDashboardBean.setHarddecline_upi_amount(Double.parseDouble(
									decimalFormat.format(document.get("TotalAmount")).replaceAll(",", "")));
						} else if (payment_type.equalsIgnoreCase("DC")) {
							merchantDashboardBean.setHarddecline_dc_count(document.getLong("TotalCount"));
							merchantDashboardBean.setHarddecline_dc_amount(Double.parseDouble(
									decimalFormat.format(document.get("TotalAmount")).replaceAll(",", "")));
						} else if (payment_type.equalsIgnoreCase("CC")) {
							merchantDashboardBean.setHarddecline_cc_count(document.getLong("TotalCount"));
							merchantDashboardBean.setHarddecline_cc_amount(Double.parseDouble(
									decimalFormat.format(document.get("TotalAmount")).replaceAll(",", "")));
						} else if (payment_type.equalsIgnoreCase("WL")) {
							merchantDashboardBean.setHarddecline_wallet_count(document.getLong("TotalCount"));
							merchantDashboardBean.setHarddecline_wallet_amount(Double.parseDouble(
									decimalFormat.format(document.get("TotalAmount")).replaceAll(",", "")));
						}
					}
				}
				harddecline.close();
				merchantDashboardBean.setHarddecline_total_count(merchantDashboardBean.getHarddecline_cc_count()
						+ merchantDashboardBean.getHarddecline_dc_count()
						+ merchantDashboardBean.getHarddecline_null_count()
						+ merchantDashboardBean.getHarddecline_nb_count()
						+ merchantDashboardBean.getHarddecline_wallet_count()
						+ merchantDashboardBean.getHarddecline_upi_count());
				merchantDashboardBean

						.setHarddecline_total_amount(Double.parseDouble(decimalFormat

								.format(merchantDashboardBean.getHarddecline_cc_amount()
										+ merchantDashboardBean.getHarddecline_dc_amount()
										+ merchantDashboardBean.getHarddecline_null_amount()
										+ merchantDashboardBean.getHarddecline_nb_amount()
										+ merchantDashboardBean.getHarddecline_wallet_amount()
										+ merchantDashboardBean.getHarddecline_upi_amount())

								.replaceAll(",", "")));
			}

			logger.info(
					"MerchantDashboardServices in getDashBoardDetail()-------------Iterate soft declined transaction");
			if (softdecline != null) {
				while (softdecline.hasNext()) {
					Document document = softdecline.next();
					String payment_type = document.getString("PAYMENT_TYPE");
					if (payment_type == null) {
						merchantDashboardBean.setSoftdecline_null_count(document.getLong("TotalCount"));
						merchantDashboardBean.setSoftdecline_null_amount(Double.parseDouble(
								decimalFormat.format(document.get("TotalAmount")).replaceAll(",", "")));
					} else {
						if (payment_type.equalsIgnoreCase("NB")) {
							merchantDashboardBean.setSoftdecline_nb_count(document.getLong("TotalCount"));
							merchantDashboardBean.setSoftdecline_nb_amount(Double.parseDouble(
									decimalFormat.format(document.get("TotalAmount")).replaceAll(",", "")));
						} else if (payment_type.equalsIgnoreCase("UP")) {
							merchantDashboardBean.setSoftdecline_upi_count(document.getLong("TotalCount"));
							merchantDashboardBean.setSoftdecline_upi_amount(Double.parseDouble(
									decimalFormat.format(document.get("TotalAmount")).replaceAll(",", "")));
						} else if (payment_type.equalsIgnoreCase("DC")) {
							merchantDashboardBean.setSoftdecline_dc_count(document.getLong("TotalCount"));
							merchantDashboardBean.setSoftdecline_dc_amount(Double.parseDouble(
									decimalFormat.format(document.get("TotalAmount")).replaceAll(",", "")));
						} else if (payment_type.equalsIgnoreCase("CC")) {
							merchantDashboardBean.setSoftdecline_cc_count(document.getLong("TotalCount"));
							merchantDashboardBean.setSoftdecline_cc_amount(Double.parseDouble(
									decimalFormat.format(document.get("TotalAmount")).replaceAll(",", "")));
						} else if (payment_type.equalsIgnoreCase("WL")) {
							merchantDashboardBean.setSoftdecline_wallet_count(document.getLong("TotalCount"));
							merchantDashboardBean.setSoftdecline_wallet_amount(Double.parseDouble(
									decimalFormat.format(document.get("TotalAmount")).replaceAll(",", "")));
						}
					}
				}
				softdecline.close();
				merchantDashboardBean.setSoftdecline_total_count(merchantDashboardBean.getSoftdecline_cc_count()
						+ merchantDashboardBean.getSoftdecline_dc_count()
						+ merchantDashboardBean.getSoftdecline_null_count()
						+ merchantDashboardBean.getSoftdecline_nb_count()
						+ merchantDashboardBean.getSoftdecline_wallet_count()
						+ merchantDashboardBean.getSoftdecline_upi_count());
				merchantDashboardBean
						.setSoftdecline_total_amount(
								Double.parseDouble(decimalFormat
										.format(merchantDashboardBean.getSoftdecline_cc_amount()
												+ merchantDashboardBean.getSoftdecline_dc_amount()
												+ merchantDashboardBean.getSoftdecline_null_amount()
												+ merchantDashboardBean.getSoftdecline_nb_amount()
												+ merchantDashboardBean.getSoftdecline_wallet_amount()
												+ merchantDashboardBean.getSoftdecline_upi_amount())
										.replaceAll(",", "")));
			}

			logger.info("MerchantDashboardServices in getDashBoardDetail()-------------Iterate pending transaction");
			if (pending != null) {
				while (pending.hasNext()) {
					Document document = pending.next();
					String payment_type = document.getString("PAYMENT_TYPE");
					if (payment_type == null) {
						merchantDashboardBean.setPending_null_count(document.getLong("TotalCount"));
						merchantDashboardBean.setPending_null_amount(Double.parseDouble(
								decimalFormat.format(document.get("TotalAmount")).replaceAll(",", "")));
					} else {
						if (payment_type.equalsIgnoreCase("NB")) {
							merchantDashboardBean.setPending_nb_count(document.getLong("TotalCount"));
							merchantDashboardBean.setPending_nb_amount(Double.parseDouble(
									decimalFormat.format(document.get("TotalAmount")).replaceAll(",", "")));
						} else if (payment_type.equalsIgnoreCase("UP")) {
							merchantDashboardBean.setPending_upi_count(document.getLong("TotalCount"));
							merchantDashboardBean.setPending_upi_amount(Double.parseDouble(
									decimalFormat.format(document.get("TotalAmount")).replaceAll(",", "")));
						} else if (payment_type.equalsIgnoreCase("DC")) {
							merchantDashboardBean.setPending_dc_count(document.getLong("TotalCount"));
							merchantDashboardBean.setPending_dc_amount(Double.parseDouble(
									decimalFormat.format(document.get("TotalAmount")).replaceAll(",", "")));
						} else if (payment_type.equalsIgnoreCase("CC")) {
							merchantDashboardBean.setPending_cc_count(document.getLong("TotalCount"));
							merchantDashboardBean.setPending_cc_amount(Double.parseDouble(
									decimalFormat.format(document.get("TotalAmount")).replaceAll(",", "")));
						} else if (payment_type.equalsIgnoreCase("WL")) {
							merchantDashboardBean.setPending_wallet_count(document.getLong("TotalCount"));
							merchantDashboardBean.setPending_wallet_amount(Double.parseDouble(
									decimalFormat.format(document.get("TotalAmount")).replaceAll(",", "")));
						}
					}
				}
				pending.close();
				merchantDashboardBean.setPending_total_count(merchantDashboardBean.getPending_cc_count()
						+ merchantDashboardBean.getPending_dc_count() + merchantDashboardBean.getPending_null_count()
						+ merchantDashboardBean.getPending_nb_count() + merchantDashboardBean.getPending_wallet_count()
						+ merchantDashboardBean.getPending_upi_count());
				merchantDashboardBean
						.setPending_total_amount(
								Double.parseDouble(
										decimalFormat
												.format(merchantDashboardBean.getPending_cc_amount()
														+ merchantDashboardBean.getPending_dc_amount()
														+ merchantDashboardBean.getPending_null_amount()
														+ merchantDashboardBean.getPending_nb_amount()
														+ merchantDashboardBean.getPending_wallet_amount()
														+ merchantDashboardBean.getPending_upi_amount())
												.replaceAll(",", "")));
			}

			logger.info("MerchantDashboardServices in getDashBoardDetail()-------------Iterate summary nb transaction");
			long sumarry_nb_harddecline = merchantDashboardBean.getHarddecline_nb_count();
			long summary_nb_soft_declinecount = merchantDashboardBean.getSoftdecline_nb_count();
			long summary_nb_capture_count = merchantDashboardBean.getCapture_nb_count();
			long summary_nb_pending_count = merchantDashboardBean.getPending_nb_count();
			long summary_nb_total_count = 0;

			merchantDashboardBean.setSummary_nb_capture_count(summary_nb_capture_count);
			merchantDashboardBean.setSummary_nb_pending_count(summary_nb_pending_count);
			merchantDashboardBean.setSummary_nb_hard_decline_count(sumarry_nb_harddecline);
			merchantDashboardBean.setSummary_nb_soft_decline_count(summary_nb_soft_declinecount);

			summary_nb_total_count = summary_nb_capture_count + summary_nb_pending_count + sumarry_nb_harddecline
					+ summary_nb_soft_declinecount;

			if (summary_nb_capture_count == 0) {
				merchantDashboardBean.setSummary_nb_capture_volume(0);
			} else {
				merchantDashboardBean.setSummary_nb_capture_volume(Double.parseDouble(decimalFormat
						.format((((double) summary_nb_capture_count * (double) 100) / (double) summary_nb_total_count))
						.replaceAll(",", "")));
			}

			if (summary_nb_pending_count == 0) {
				merchantDashboardBean.setSummary_nb_pending_volume(0);
			} else {
				merchantDashboardBean.setSummary_nb_pending_volume(Double.parseDouble(decimalFormat
						.format((((double) summary_nb_pending_count * (double) 100) / (double) summary_nb_total_count))
						.replaceAll(",", "")));
			}

			if (sumarry_nb_harddecline == 0) {
				merchantDashboardBean.setSummary_nb_hard_decline_volume(0);
			} else {
				merchantDashboardBean.setSummary_nb_hard_decline_volume(Double.parseDouble(decimalFormat
						.format((((double) sumarry_nb_harddecline * (double) 100) / (double) summary_nb_total_count))
						.replaceAll(",", "")));
			}

			if (summary_nb_soft_declinecount == 0) {
				merchantDashboardBean.setSummary_nb_soft_decline_volume(0);
			} else {
				merchantDashboardBean.setSummary_nb_soft_decline_volume(Double.parseDouble(decimalFormat.format(
						(((double) summary_nb_soft_declinecount * (double) 100) / (double) summary_nb_total_count))
						.replaceAll(",", "")));
			}

			logger.info(
					"MerchantDashboardServices in getDashBoardDetail()-------------Iterate summary upi transaction");

			long sumarry_upi_harddecline = merchantDashboardBean.getHarddecline_upi_count();
			long summary_upi_soft_declinecount = merchantDashboardBean.getSoftdecline_upi_count();
			long summary_upi_capture_count = merchantDashboardBean.getCapture_upi_count();
			long summary_upi_pending_count = merchantDashboardBean.getPending_upi_count();
			long summary_upi_total_count = 0;

			merchantDashboardBean.setSummary_upi_capture_count(summary_upi_capture_count);
			merchantDashboardBean.setSummary_upi_pending_count(summary_upi_pending_count);
			merchantDashboardBean.setSummary_upi_hard_decline_count(sumarry_upi_harddecline);
			merchantDashboardBean.setSummary_upi_soft_decline_count(summary_upi_soft_declinecount);

			summary_upi_total_count = summary_upi_capture_count + summary_upi_pending_count + sumarry_upi_harddecline
					+ summary_upi_soft_declinecount;

			if (summary_upi_capture_count == 0) {
				merchantDashboardBean.setSummary_upi_capture_volume(0);
			} else {
				merchantDashboardBean.setSummary_upi_capture_volume(Double.parseDouble(decimalFormat.format(
						(((double) summary_upi_capture_count * (double) 100) / (double) summary_upi_total_count))
						.replaceAll(",", "")));
			}

			if (summary_upi_pending_count == 0) {
				merchantDashboardBean.setSummary_upi_pending_volume(0);
			} else {
				merchantDashboardBean.setSummary_upi_pending_volume(Double.parseDouble(decimalFormat.format(
						(((double) summary_upi_pending_count * (double) 100) / (double) summary_upi_total_count))
						.replaceAll(",", "")));
			}

			if (sumarry_upi_harddecline == 0) {
				merchantDashboardBean.setSummary_upi_hard_decline_volume(0);
			} else {
				merchantDashboardBean.setSummary_upi_hard_decline_volume(Double.parseDouble(decimalFormat
						.format((((double) sumarry_upi_harddecline * (double) 100) / (double) summary_upi_total_count))
						.replaceAll(",", "")));
			}

			if (summary_upi_soft_declinecount == 0) {
				merchantDashboardBean.setSummary_upi_soft_decline_volume(0);
			} else {
				merchantDashboardBean.setSummary_upi_soft_decline_volume(Double.parseDouble(decimalFormat.format(
						(((double) summary_upi_soft_declinecount * (double) 100) / (double) summary_upi_total_count))
						.replaceAll(",", "")));
			}

			logger.info(
					"MerchantDashboardServices in getDashBoardDetail()-------------Iterate summary cc/dc transaction");

			long sumarry_cc_dc_harddecline = merchantDashboardBean.getHarddecline_cc_count()
					+ merchantDashboardBean.getHarddecline_dc_count();
			long summary_cc_dc_soft_declinecount = merchantDashboardBean.getSoftdecline_cc_count()
					+ merchantDashboardBean.getSoftdecline_dc_count();
			long summary_cc_dc_capture_count = merchantDashboardBean.getCapture_cc_count()
					+ merchantDashboardBean.getCapture_dc_count();
			long summary_cc_dc_pending_count = merchantDashboardBean.getPending_cc_count()
					+ merchantDashboardBean.getPending_dc_count();
			long summary_cc_dc_total_count = 0;

			merchantDashboardBean.setSummary_cc_dc_capture_count(summary_cc_dc_capture_count);
			merchantDashboardBean.setSummary_cc_dc_pending_count(summary_cc_dc_pending_count);
			merchantDashboardBean.setSummary_cc_dc_hard_decline_count(sumarry_cc_dc_harddecline);
			merchantDashboardBean.setSummary_cc_dc_soft_decline_count(summary_cc_dc_soft_declinecount);

			summary_cc_dc_total_count = summary_cc_dc_capture_count + summary_cc_dc_pending_count
					+ sumarry_cc_dc_harddecline + summary_cc_dc_soft_declinecount;

			if (summary_cc_dc_capture_count == 0) {
				merchantDashboardBean.setSummary_cc_dc_capture_volume(0);
			} else {
				merchantDashboardBean.setSummary_cc_dc_capture_volume(Double.parseDouble(decimalFormat.format(
						(((double) summary_cc_dc_capture_count * (double) 100) / (double) summary_cc_dc_total_count))
						.replaceAll(",", "")));
			}

			if (summary_cc_dc_pending_count == 0) {
				merchantDashboardBean.setSummary_cc_dc_pending_volume(0);
			} else {
				merchantDashboardBean.setSummary_cc_dc_pending_volume(Double.parseDouble(decimalFormat.format(
						(((double) summary_cc_dc_pending_count * (double) 100) / (double) summary_cc_dc_total_count))
						.replaceAll(",", "")));
			}

			if (sumarry_cc_dc_harddecline == 0) {
				merchantDashboardBean.setSummary_cc_dc_hard_decline_volume(0);
			} else {
				merchantDashboardBean.setSummary_cc_dc_hard_decline_volume(Double.parseDouble(decimalFormat.format(
						(((double) sumarry_cc_dc_harddecline * (double) 100) / (double) summary_cc_dc_total_count))
						.replaceAll(",", "")));
			}

			if (summary_cc_dc_soft_declinecount == 0) {
				merchantDashboardBean.setSummary_cc_dc_soft_decline_volume(0);
			} else {
				merchantDashboardBean.setSummary_cc_dc_soft_decline_volume(Double
						.parseDouble(decimalFormat.format((((double) summary_cc_dc_soft_declinecount * (double) 100)
								/ (double) summary_cc_dc_total_count)).replaceAll(",", "")));
			}

			logger.info(
					"MerchantDashboardServices in getDashBoardDetail()-------------Iterate summary wallet transaction");

			long sumarry_wallet_harddecline = merchantDashboardBean.getHarddecline_wallet_count();
			long summary_wallet_soft_declinecount = merchantDashboardBean.getSoftdecline_wallet_count();
			long summary_wallet_capture_count = merchantDashboardBean.getCapture_wallet_count();
			long summary_wallet_pending_count = merchantDashboardBean.getPending_wallet_count();
			long summary_wallet_total_count = 0;

			merchantDashboardBean.setSummary_wallet_capture_count(summary_wallet_capture_count);
			merchantDashboardBean.setSummary_wallet_pending_count(summary_wallet_pending_count);
			merchantDashboardBean.setSummary_wallet_hard_decline_count(sumarry_wallet_harddecline);
			merchantDashboardBean.setSummary_wallet_soft_decline_count(summary_wallet_soft_declinecount);

			summary_wallet_total_count = summary_wallet_capture_count + summary_wallet_pending_count
					+ sumarry_wallet_harddecline + summary_wallet_soft_declinecount;

			if (summary_wallet_capture_count == 0) {
				merchantDashboardBean.setSummary_wallet_capture_volume(0);
			} else {
				merchantDashboardBean.setSummary_wallet_capture_volume(Double.parseDouble(decimalFormat.format(
						(((double) summary_wallet_capture_count * (double) 100) / (double) summary_wallet_total_count))
						.replaceAll(",", "")));
			}

			if (summary_wallet_pending_count == 0) {
				merchantDashboardBean.setSummary_wallet_pending_volume(0);
			} else {
				merchantDashboardBean.setSummary_wallet_pending_volume(Double.parseDouble(decimalFormat.format(
						(((double) summary_wallet_pending_count * (double) 100) / (double) summary_wallet_total_count))
						.replaceAll(",", "")));
			}

			if (sumarry_wallet_harddecline == 0) {
				merchantDashboardBean.setSummary_wallet_hard_decline_volume(0);
			} else {
				merchantDashboardBean.setSummary_wallet_hard_decline_volume(Double.parseDouble(decimalFormat.format(
						(((double) sumarry_wallet_harddecline * (double) 100) / (double) summary_wallet_total_count))
						.replaceAll(",", "")));
			}

			if (summary_wallet_soft_declinecount == 0) {
				merchantDashboardBean.setSummary_wallet_soft_decline_volume(0);
			} else {
				merchantDashboardBean.setSummary_wallet_soft_decline_volume(Double
						.parseDouble(decimalFormat.format((((double) summary_wallet_soft_declinecount * (double) 100)
								/ (double) summary_wallet_total_count)).replaceAll(",", "")));
			}

			logger.info("MerchantDashboardServices in getDashBoardDetail()-------------Iterate Total Transaction");
			merchantDashboardBean.setTotal_cc_count(merchantDashboardBean.getCapture_cc_count()
					+ merchantDashboardBean.getHarddecline_cc_count() + merchantDashboardBean.getSoftdecline_cc_count()
					+ merchantDashboardBean.getPending_cc_count());

			merchantDashboardBean
					.setTotal_cc_amount(
							Double.parseDouble(
									decimalFormat
											.format(merchantDashboardBean.getCapture_cc_amount()
													+ merchantDashboardBean.getHarddecline_cc_amount()
													+ merchantDashboardBean.getSoftdecline_cc_amount()
													+ merchantDashboardBean.getPending_cc_amount())
											.replaceAll(",", "")));

			merchantDashboardBean.setTotal_dc_count(merchantDashboardBean.getCapture_dc_count()
					+ merchantDashboardBean.getHarddecline_dc_count() + merchantDashboardBean.getSoftdecline_dc_count()
					+ merchantDashboardBean.getPending_dc_count());

			merchantDashboardBean
					.setTotal_dc_amount(
							Double.parseDouble(
									decimalFormat
											.format(merchantDashboardBean.getCapture_dc_amount()
													+ merchantDashboardBean.getHarddecline_dc_amount()
													+ merchantDashboardBean.getSoftdecline_dc_amount()
													+ merchantDashboardBean.getPending_dc_amount())
											.replaceAll(",", "")));

			merchantDashboardBean.setTotal_nb_count(merchantDashboardBean.getCapture_nb_count()
					+ merchantDashboardBean.getHarddecline_nb_count() + merchantDashboardBean.getSoftdecline_nb_count()
					+ merchantDashboardBean.getPending_nb_count());

			merchantDashboardBean
					.setTotal_nb_amount(
							Double.parseDouble(
									decimalFormat
											.format(merchantDashboardBean.getCapture_nb_amount()
													+ merchantDashboardBean.getHarddecline_nb_amount()
													+ merchantDashboardBean.getSoftdecline_nb_amount()
													+ merchantDashboardBean.getPending_nb_amount())
											.replaceAll(",", "")));

			merchantDashboardBean.setTotal_wallet_count(merchantDashboardBean.getCapture_wallet_count()
					+ merchantDashboardBean.getHarddecline_wallet_count()
					+ merchantDashboardBean.getSoftdecline_wallet_count()
					+ merchantDashboardBean.getPending_wallet_count());

			merchantDashboardBean
					.setTotal_wallet_amount(
							Double.parseDouble(
									decimalFormat
											.format(merchantDashboardBean.getCapture_wallet_amount()
													+ merchantDashboardBean.getHarddecline_wallet_amount()
													+ merchantDashboardBean.getSoftdecline_wallet_amount()
													+ merchantDashboardBean.getPending_wallet_amount())
											.replaceAll(",", "")));

			merchantDashboardBean.setTotal_upi_count(merchantDashboardBean.getCapture_upi_count()
					+ merchantDashboardBean.getHarddecline_upi_count()
					+ merchantDashboardBean.getSoftdecline_upi_count() + merchantDashboardBean.getPending_upi_count());

			merchantDashboardBean
					.setTotal_upi_amount(
							Double.parseDouble(
									decimalFormat
											.format(merchantDashboardBean.getCapture_upi_amount()
													+ merchantDashboardBean.getHarddecline_upi_amount()
													+ merchantDashboardBean.getSoftdecline_upi_amount()
													+ merchantDashboardBean.getPending_upi_amount())
											.replaceAll(",", "")));

			merchantDashboardBean.setTotal_null_count(merchantDashboardBean.getHarddecline_null_count()
					+ merchantDashboardBean.getSoftdecline_null_count()
					+ merchantDashboardBean.getPending_null_count());

			merchantDashboardBean
					.setTotal_null_amount(
							Double.parseDouble(
									decimalFormat
											.format(merchantDashboardBean.getHarddecline_null_amount()
													+ merchantDashboardBean.getSoftdecline_null_amount()
													+ merchantDashboardBean.getPending_null_amount())
											.replaceAll(",", "")));

			merchantDashboardBean.setTotal_total_count(
					merchantDashboardBean.getTotal_cc_count() + merchantDashboardBean.getTotal_dc_count()
							+ merchantDashboardBean.getTotal_nb_count() + merchantDashboardBean.getTotal_wallet_count()
							+ merchantDashboardBean.getTotal_upi_count() + merchantDashboardBean.getTotal_null_count());
			merchantDashboardBean.setTotal_total_amount(
					Double.parseDouble(decimalFormat.format(merchantDashboardBean.getTotal_cc_amount()
							+ merchantDashboardBean.getTotal_dc_amount() + merchantDashboardBean.getTotal_nb_amount()
							+ merchantDashboardBean.getTotal_wallet_amount()
							+ merchantDashboardBean.getTotal_upi_amount()
							+ merchantDashboardBean.getTotal_null_amount()).replaceAll(",", "")));

			logger.info(

					"MerchantDashboardServices in getDashBoardDetail()-------------Iterate Overall Total Transaction");
			long overall_total_count = merchantDashboardBean.getCapture_total_count()
					+ merchantDashboardBean.getHarddecline_total_count()
					+ merchantDashboardBean.getSoftdecline_total_count()
					+ merchantDashboardBean.getPending_total_count();

			if (overall_total_count != 0) {
				merchantDashboardBean.setSummary_overall_capture_count(merchantDashboardBean.getCapture_total_count());
				merchantDashboardBean.setSummary_overall_capture_volume(Double.parseDouble(decimalFormat.format(
						(merchantDashboardBean.getCapture_total_count() * (double) 100) / (double) overall_total_count)
						.replaceAll(",", "")));
				merchantDashboardBean
						.setSummary_overall_hard_decline_count(merchantDashboardBean.getHarddecline_total_count());
				merchantDashboardBean.setSummary_overall_hard_decline_volume(Double.parseDouble(
						decimalFormat.format((merchantDashboardBean.getHarddecline_total_count() * (double) 100)
								/ (double) overall_total_count).replaceAll(",", "")));
				merchantDashboardBean
						.setSummary_overall_soft_decline_count(merchantDashboardBean.getSoftdecline_total_count());
				merchantDashboardBean.setSummary_overall_soft_decline_volume(Double.parseDouble(
						decimalFormat.format((merchantDashboardBean.getSoftdecline_total_count() * (double) 100)
								/ (double) overall_total_count).replaceAll(",", "")));
				merchantDashboardBean.setSummary_overall_pending_count(merchantDashboardBean.getPending_total_count());
				merchantDashboardBean.setSummary_overall_pending_volume(Double.parseDouble(decimalFormat
						.format((merchantDashboardBean.getPending_total_count() * 100) / (double) overall_total_count)
						.replaceAll(",", "")));
			} else {
				merchantDashboardBean.setSummary_overall_capture_count(0);
				merchantDashboardBean.setSummary_overall_capture_volume(0);
				merchantDashboardBean.setSummary_overall_hard_decline_count(0);
				merchantDashboardBean.setSummary_overall_hard_decline_volume(0);
				merchantDashboardBean.setSummary_overall_soft_decline_count(0);
				merchantDashboardBean.setSummary_overall_soft_decline_volume(0);
				merchantDashboardBean.setSummary_overall_pending_count(0);
				merchantDashboardBean.setSummary_overall_pending_volume(0);
			}
		} catch (Exception e) {
			logger.info("Exception Occur in class MerchantDashboardService in getDashBoardDetail(): " + e.getMessage());
			e.printStackTrace();
		}

		return new Gson().toJson(merchantDashboardBean);
	}

	public MongoCursor<Document> getCaptureTxn(String merchant, String acquirer, String paymentMethods, String[] date,
			MongoCollection<Document> collection, List<String> payIdList, String moptype) {
		MongoCursor<Document> capture = null;
		try {
			BasicDBObject match = new BasicDBObject();
			match.put(FieldType.CREATE_DATE.getName(), new BasicDBObject("$gte", date[0]).append("$lte", date[1]));
			match.put(FieldType.PAYMENT_TYPE.getName(), new BasicDBObject("$ne", new BsonNull()));
			match.put(FieldType.STATUS.getName(),
					new BasicDBObject("$in", Arrays.asList(StatusType.CAPTURED.getName(), StatusType.SETTLED_SETTLE.getName(),StatusType.SETTLED_RECONCILLED.getName())));
			match.put(FieldType.TXNTYPE.getName(),
					new BasicDBObject("$in", Arrays.asList(TransactionType.SALE.getName(), TransactionType.RECO.getName())));

			if (!acquirer.equalsIgnoreCase("All")) {
				match.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
			}
			if (!paymentMethods.equalsIgnoreCase("All")) {
				match.put(FieldType.PAYMENT_TYPE.getName(), paymentMethods);
			}
			if (!moptype.equalsIgnoreCase("All")) {
				match.put(FieldType.MOP_TYPE.getName(), moptype);
			}
			if (!merchant.equalsIgnoreCase("All")) {
				match.put(FieldType.PAY_ID.getName(), merchant);
			} else {
				match.put(FieldType.PAY_ID.getName(), new Document("$in", payIdList));
			}

			List<BasicDBObject> Capture_Query = Arrays.asList(new BasicDBObject("$match", match),
					new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("PAYMENT_TYPE", "$PAYMENT_TYPE"))
									.append("COUNT(*)", new BasicDBObject("$sum", 1L))
									.append("SUM(AMOUNT)", new BasicDBObject("$sum", new Document("$toDouble", "$AMOUNT")))),
					new BasicDBObject("$project",
							new BasicDBObject("PAYMENT_TYPE", "$_id.PAYMENT_TYPE").append("TotalCount", "$COUNT(*)")
									.append("TotalAmount", "$SUM(AMOUNT)").append("_id", 0L)));
			logger.info("Capture Query : " + Capture_Query);
			capture = collection.aggregate(Capture_Query).allowDiskUse(true).iterator();

		} catch (Exception e) {
			logger.info("Exception Occur in class MerchantDashboardService in getCaptureTxn() : " + e.getMessage());
			e.printStackTrace();
		}
		return capture;
	}

	public MongoCursor<Document> getHardDeclineTxn(String merchant, String acquirer, String paymentMethods,
			String[] date, MongoCollection<Document> collection, List<String> payIdList, String moptype) {
		MongoCursor<Document> harddecline = null;
		try {

			BasicDBObject match = new BasicDBObject();
			match.put(FieldType.PG_REF_NUM.getName(), new BasicDBObject("$ne", new BsonNull()));
			match.put(FieldType.CREATE_DATE.getName(), new BasicDBObject("$gte", date[0]).append("$lte", date[1]));
			match.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			match.put(FieldType.STATUS.getName(),
					new BasicDBObject("$in",
							Arrays.asList("User Inactive", "Failed at Acquirer", "FAILED", "Rejected",
									"Authentication Failed", "Failed", "Declined", "Invalid", "Error",
									"Cancelled by user", "Cancelled")));

			if (!acquirer.equalsIgnoreCase("All")) {
				match.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
			}
			if (!paymentMethods.equalsIgnoreCase("All")) {
				match.put(FieldType.PAYMENT_TYPE.getName(), paymentMethods);
			}
			if (!moptype.equalsIgnoreCase("All")) {
				match.put(FieldType.MOP_TYPE.getName(), moptype);
			}
			if (!merchant.equalsIgnoreCase("All")) {
				match.put(FieldType.PAY_ID.getName(), merchant);
			} else {
				match.put(FieldType.PAY_ID.getName(), new Document("$in", payIdList));
			}
			List<BasicDBObject> getHardDeclineTxnQuery = Arrays.asList(new BasicDBObject("$match", match),
					new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("PAYMENT_TYPE", "$PAYMENT_TYPE"))
									.append("COUNT(*)", new BasicDBObject("$sum", 1L))
									.append("SUM(AMOUNT)", new BasicDBObject("$sum", new Document("$toDouble", "$AMOUNT")))),
					new BasicDBObject("$project",
							new BasicDBObject("PAYMENT_TYPE", "$_id.PAYMENT_TYPE").append("TotalCount", "$COUNT(*)")
									.append("TotalAmount", "$SUM(AMOUNT)").append("_id", 0L)));

			logger.info("HardDeclineTxn Query : " + getHardDeclineTxnQuery);
			harddecline = collection.aggregate(getHardDeclineTxnQuery).allowDiskUse(true).iterator();

		} catch (Exception e) {
			logger.info("Exception Occure : " + e.getMessage());
			e.printStackTrace();
		}
		return harddecline;
	}

	public MongoCursor<Document> getSoftDeclineTxn(String merchant, String acquirer, String paymentMethods,
			String[] date, MongoCollection<Document> collection, List<String> payIdList, String moptype) {
		MongoCursor<Document> softdecline = null;
		try {
			BasicDBObject match = new BasicDBObject();
			match.put(FieldType.CREATE_DATE.getName(), new BasicDBObject("$gte", date[0]).append("$lte", date[1]));
			match.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			
			match.put(FieldType.STATUS.getName(),
					new BasicDBObject("$in",
							Arrays.asList("Denied by risk","Denied due to fraud")));
		
			if (!acquirer.equalsIgnoreCase("All")) {
				match.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
			}
			if (!paymentMethods.equalsIgnoreCase("All")) {
				match.put(FieldType.PAYMENT_TYPE.getName(), paymentMethods);
			}
			if (!moptype.equalsIgnoreCase("All")) {
				match.put(FieldType.MOP_TYPE.getName(), moptype);
			}
			if (!merchant.equalsIgnoreCase("All")) {
				match.put(FieldType.PAY_ID.getName(), merchant);
			} else {
				match.put(FieldType.PAY_ID.getName(), new Document("$in", payIdList));
			}
			List<BasicDBObject> softDeclineQuery = Arrays.asList(new BasicDBObject("$match", match),
					new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("PAYMENT_TYPE", "$PAYMENT_TYPE"))
									.append("COUNT(*)", new BasicDBObject("$sum", 1L))
									.append("SUM(AMOUNT)", new BasicDBObject("$sum", new BasicDBObject("$toDouble", "$AMOUNT")))),
					new BasicDBObject("$project",
							new BasicDBObject("PAYMENT_TYPE", "$_id.PAYMENT_TYPE").append("TotalCount", "$COUNT(*)")
									.append("TotalAmount", "$SUM(AMOUNT)").append("_id", 0L)));

			logger.info("SoftDecline Query : " + softDeclineQuery);
			softdecline = collection.aggregate(softDeclineQuery).allowDiskUse(true).iterator();

		} catch (Exception e) {
			logger.info("Exception Occur : " + e.getMessage());
			e.printStackTrace();
		}
		return softdecline;
	}

	public MongoCursor<Document> getPendingTxn(String merchant, String acquirer, String paymentMethods, String[] date,
			MongoCollection<Document> collection, List<String> payIdList, String moptype) {
		MongoCursor<Document> pending = null;
		try {

			BasicDBObject match = new BasicDBObject();
			match.put(FieldType.CREATE_DATE.getName(), new BasicDBObject("$gte", date[0]).append("$lte", date[1]));
			match.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());

			if (!acquirer.equalsIgnoreCase("All")) {
				match.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
			}
			if (!paymentMethods.equalsIgnoreCase("All")) {
				match.put(FieldType.PAYMENT_TYPE.getName(), paymentMethods);
			}
			if (!moptype.equalsIgnoreCase("All")) {
				match.put(FieldType.MOP_TYPE.getName(), moptype);
			}
			if (!merchant.equalsIgnoreCase("All")) {
				match.put(FieldType.PAY_ID.getName(), merchant);
			} else {
				match.put(FieldType.PAY_ID.getName(), new Document("$in", payIdList));
			}
			List<BasicDBObject> pendingTxnQuery = Arrays.asList(new BasicDBObject("$match", match),
					new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("PG_REF_NUM", "$PG_REF_NUM"))
									.append("PG_REF_NUM", new BasicDBObject("$first", "$PG_REF_NUM"))
									.append("PAYMENT_TYPE", new BasicDBObject("$first", "$PAYMENT_TYPE"))
									.append("AMOUNT", new BasicDBObject("$first", "$AMOUNT"))
									.append("STATUS", new BasicDBObject("$addToSet", "$STATUS"))
									.append("count", new BasicDBObject("$sum", 1L))),
					new BasicDBObject("$project", new Document("_id", 0L)),
					new BasicDBObject("$match",
							new Document("STATUS", new BasicDBObject("$in", Arrays.asList("Sent to Bank"))).append("count",
									new BasicDBObject("$lte", 1L))),
					new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("PAYMENT_TYPE", "$PAYMENT_TYPE"))
									.append("transactionCount", new Document("$sum", 1L)).append("transactionAmountSum",
											new BasicDBObject("$sum", new BasicDBObject("$toDouble", "$AMOUNT")))),
					new BasicDBObject("$project",
							new BasicDBObject("PAYMENT_TYPE", "$_id.PAYMENT_TYPE").append("TotalCount", "$transactionCount")
									.append("TotalAmount", "$transactionAmountSum").append("_id", 0L)));
			logger.info("Pending Transaction Query : " + pendingTxnQuery);
			pending = collection.aggregate(pendingTxnQuery).allowDiskUse(true).iterator();

		} catch (Exception e) {
			logger.info("Exception Occure : " + e.getMessage());
			e.printStackTrace();
		}

		return pending;
	}

	public List<TransactionStatusBean> getEachTransactionDetail(String mode, String merchant, String acquirer,
			String paymentMethods, String date_range, List<String> payIdList, String moptype) {
		logger.info("Inside MerchantDashboardService in getEachTransactionDetail() : mode : " + mode + "\tmerchant : "
				+ merchant + "\tacquirer : " + acquirer + "\tpaymentMethods : " + paymentMethods + "\tdate_range : "
				+ date_range + "\tMop Type : " + moptype);
		List<TransactionStatusBean> transactionStatusBeans = null;
		try {
			String[] date = getDateBetween(date_range);
			logger.info("Inside MerchantDashboardService in getEachTransactionDetail() : date range split start date : "
					+ date[0] + "\t End Date : " + date[1]);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			List<BasicDBObject> query = null;
			BasicDBObject match = new BasicDBObject();
			match.put(FieldType.CREATE_DATE.getName(), new BasicDBObject("$gte", date[0]).append("$lte", date[1]));

			if (mode.equalsIgnoreCase("Captured")) {
				match.put(FieldType.PAYMENT_TYPE.getName(), new BasicDBObject("$ne", new BsonNull()));
				match.put(FieldType.STATUS.getName(), new BasicDBObject("$in",
						Arrays.asList(StatusType.CAPTURED.getName(), StatusType.SETTLED_SETTLE.getName())));
				match.put(FieldType.TXNTYPE.getName(), new BasicDBObject("$in",
						Arrays.asList(TransactionType.SALE.getName(), TransactionType.RECO.getName())));

				query = Arrays.asList(new BasicDBObject("$match", match));
			} else if (mode.equalsIgnoreCase("hardDecline")) {
				match.put(FieldType.PG_REF_NUM.getName(), new BasicDBObject("$ne", new BsonNull()));
				match.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
				match.put(FieldType.STATUS.getName(),
						new BasicDBObject("$in",
								Arrays.asList("User Inactive", "Failed at Acquirer", "FAILED", "Rejected",
										"Authentication Failed", "Failed", "Declined", "Invalid", "Error",
										"Cancelled by user", "Cancelled")));
				query = Arrays.asList(new BasicDBObject("$match", match));
			} else if (mode.equalsIgnoreCase("softDecline")) {
				match.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
				match.put(FieldType.STATUS.getName(),
						new BasicDBObject("$in",
								Arrays.asList("Denied by risk","Denied due to fraud")));
				query = Arrays.asList(new BasicDBObject("$match", match));
				
			} else {
				if (mode.equalsIgnoreCase("pending")) {
					match.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
					query = Arrays.asList(new BasicDBObject("$match", match),
							new BasicDBObject("$group",
									new BasicDBObject("_id", "$PG_REF_NUM")
											.append("PG_REF_NUM", new BasicDBObject("$first", "$PG_REF_NUM"))
											.append("PAYMENT_TYPE", new BasicDBObject("$first", "$PAYMENT_TYPE"))
											.append("AMOUNT", new BasicDBObject("$first", "$AMOUNT"))
											.append("STATUS", new BasicDBObject("$addToSet", "$STATUS"))
											.append("Count", new BasicDBObject("$sum", 1L))
											.append("record", new BasicDBObject("$first", "$$ROOT"))),
							new BasicDBObject("$match", new BasicDBObject("STATUS", "Sent to Bank").append("Count", 1L)),
							new BasicDBObject("$replaceRoot", new BasicDBObject("newRoot", "$record")));
				}
			}
			if (!acquirer.equalsIgnoreCase("All")) {
				match.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
			}
			if (!paymentMethods.equalsIgnoreCase("All")) {
				match.put(FieldType.PAYMENT_TYPE.getName(), paymentMethods);
			}
			if (!moptype.equalsIgnoreCase("All")) {
				match.put(FieldType.MOP_TYPE.getName(), moptype);
			}
			if (!merchant.equalsIgnoreCase("All")) {
				match.put(FieldType.PAY_ID.getName(), merchant);
			} else {
				match.put(FieldType.PAY_ID.getName(), new Document("$in", payIdList));
			}

			logger.info("Query : " + query);
			MongoCursor<Document> mongoCursor = collection.aggregate(query).allowDiskUse(true).iterator();

			transactionStatusBeans = new ArrayList<>();

			while (mongoCursor.hasNext()) {
				Document document = (Document) mongoCursor.next();
				TransactionStatusBean bean = new Gson().fromJson(document.toJson(), TransactionStatusBean.class);
				transactionStatusBeans.add(bean);
			}
		} catch (Exception e) {
			logger.error("Exception Occure in getEachTransactionDetail ()" + e);
		}
		logger.info("Inside MerchantDashboardService in getEachTransactionDetail() return size : "
				+ transactionStatusBeans.size());
		return transactionStatusBeans;
	}

}
