
package com.crmws.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmws.service.ChargebackBulkMailService;
import com.google.gson.Gson;
import com.pay10.commons.api.VelocityEmailer;
import com.pay10.commons.repository.ChargebackBulkmailRepository;
import com.pay10.commons.repository.DMSEntity;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;

@Service
public class ChargebackBulkMailServiceImpl implements ChargebackBulkMailService {

	private static final Logger logger = LoggerFactory.getLogger(ChargebackBulkMailServiceImpl.class.getName());
	final String mailForChargeback = PropertiesManager.propertiesMap.get(Constants.MAILTOFORCHARGEBACK.getValue());

	@Autowired
	private ChargebackBulkmailRepository bulkmailRepository;

	@Autowired
	private VelocityEmailer emailer;
	
	@Autowired
	private UserDao dao;

	@Override
	public String sendBulkMail() {
		
		//date format -> "2023-06-13"
			String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		Map<String, ArrayList<Document>> map = bulkmailRepository.getAllChargebackList(date);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy h:mm a");

		String[] mailTo = mailForChargeback.split(",");

		int length = mailTo.length;
		String[] newArray = null;
		List<String>m=null;
		Set<String> nemailSet  =new HashSet<>();
		for (Map.Entry<String, ArrayList<Document>> entry : map.entrySet()) {
			nemailSet  =new HashSet<>();
			nemailSet.addAll(Arrays.asList(mailForChargeback));
			m=new ArrayList<>();
			String key = entry.getKey();
			ArrayList<Document> val = entry.getValue();
			if(val.size()>0) {
			StringBuffer body = new StringBuffer();
			
			User user=dao.findByPayId(key);
			newArray = Arrays.copyOf(mailTo, length + 1);
			newArray[length] = user.getEmailId();
			for (int i = 0; i < newArray.length; i++) {
				m.add(newArray[i]);
			}
			String subject = "URGENT- PAY10 CHARGEBACK INTIMATION - TOTAL CASES [" + val.size() + "] ["
					+ dateFormat.format(new Date()) + "]";
			String merchantName=user.getBusinessName()==null?"":user.getBusinessName();
			String date1=new SimpleDateFormat("dd-MMM-yyyy").format(new Date());
			body.append("<h1 style=\"color: red; text-align: center;\">CHARGEBACK INTIMATION</h1>\r\n"
					+ "<hr style=\"background-color: red; width: 100%; height: 3px; border: none;\">\r\n"
					+ "<p>Merchant Name : "+merchantName+"</p>\r\n" + "<p>Date : "+date1+"</p>\r\n" + "<br>\r\n"
					+ "<p>Dear Merchant</p>\r\n" + "<p>Greetings from Pay10!</p>\r\n"
					+ "<h4 style=\"color: red; \">We wish to inform you that Schemes has revised the Dispute Timeframe for Document submission. Please find the below revised timeframe. Revised timeframe docs attached for reference. </h4>\r\n"
					+ "<table border=\"1\">\r\n" + "    \r\n" + "    \r\n" + "    <tbody>\r\n" + "        <tr>\r\n"
					+ "            <td colspan=\"2\" style=\"text-align: center; font-weight: bold;color: white;background-color: rgb(104, 104, 253);\">CHARGEBACK</td>\r\n"
					+ "        </tr>\r\n"
					+ "        <tr style=\"text-align: center; font-weight: bold;color: black;background-color: rgb(160, 160, 168);\">\r\n"
					+ "            <td>\r\n" + "                Chargeback Types\r\n" + "            </td>\r\n"
					+ "            <td>\r\n" + "                Timeframe\r\n" + "            </td>\r\n"
					+ "        </tr>\r\n" + "        <tr>\r\n" + "            <td>\r\n"
					+ "                Chargeback  Intimation\r\n" + "            </td>\r\n" + "            <td>\r\n"
					+ "                1 Day to 4 Day (Calender Days)\r\n" + "            </td>\r\n"
					+ "        </tr>\r\n" + "        <tr>\r\n" + "            <td>\r\n"
					+ "                Pre-arbitration\r\n" + "            </td>\r\n" + "            <td>\r\n"
					+ "                1 Day to 2 Day (Calender Days)\r\n" + "            </td>\r\n"
					+ "        </tr>\r\n" + "        <tr>\r\n" + "            <td>\r\n"
					+ "               Pre-Compliance, Arbitration\r\n" + "            </td>\r\n"
					+ "            <td>\r\n" + "                1 Day (Calender Days)\r\n" + "            </td>\r\n"
					+ "        </tr>\r\n" + "    </tbody>\r\n" + "</table>\r\n"
					+ "<p>Please be advised that we have received chargeback cases for the transactions mentioned below.</p>\r\n"
					+ "<ul style=\"list-style-type: disc;\">\r\n"
					+ "    <li>Total number of new chargebacks received: "+val.size()+"</li>\r\n" + "</ul>\r\n" + "\r\n"
					+ "<table border=\"1\">\r\n" + "    <tbody>\r\n" + "        <tr>\r\n"
					+ "            <td>Sr No.</td>\r\n" + "            <td>Pg Ref Num</td>\r\n"
					+ "            <td>Acquirer</td>\r\n" + "            <td>Date</td>\r\n"
					+ "            <td>Order Id</td>\r\n" + "            <td>Payment Method</td>\r\n"
					+ "            <td>Txn Type</td>\r\n" + "            <td>Status</td>\r\n"
					+ "            <td>Total Amount</td>\r\n" + "            <td>ACQ ID</td>\r\n"
					+ "            <td>RRN</td>\r\n" + "            <td>Card Mask</td>\r\n" + "            \r\n"
				    + "            <td>Ip Address</td>\r\n" + "            \r\n    </tr>");
			
			System.out.println(body);
			int count = 1;
			for (Document string : val) {
				
				
				if(!nemailSet.contains(string.getString("EMAIL_NOTIFICATION"))) {
					if(!StringUtils.isBlank(string.getString("EMAIL_NOTIFICATION")))
					m.add(string.getString("EMAIL_NOTIFICATION"));	
					nemailSet.add(string.getString("EMAIL_NOTIFICATION"));
				}
				
				String pgRefnum=string.getString(FieldType.PG_REF_NUM.getName())==null?"N/A":string.getString(FieldType.PG_REF_NUM.getName());
				String acquirerType=string.getString(FieldType.ACQUIRER_TYPE.getName())==null?"":string.getString(FieldType.ACQUIRER_TYPE.getName()) ;
				String createDate=string.getString(FieldType.CREATE_DATE.getName())==null?"":string.getString(FieldType.CREATE_DATE.getName());
				String orderId=string.getString(FieldType.ORDER_ID.getName())==null?"":string.getString(FieldType.ORDER_ID.getName());
				String paymentType=PaymentType.getpaymentName(string.getString(FieldType.PAYMENT_TYPE.getName()))==null?"":PaymentType.getpaymentName(string.getString(FieldType.PAYMENT_TYPE.getName())) ;
				String txnType=string.getString(FieldType.TXNTYPE.getName())==null?"":string.getString(FieldType.TXNTYPE.getName()) ;
				String status=string.getString(FieldType.STATUS.getName())==null?"":string.getString(FieldType.STATUS.getName());
				String totalAmount=string.getString(FieldType.TOTAL_AMOUNT.getName())==null?"": string.getString(FieldType.TOTAL_AMOUNT.getName()) ;
				String acq=string.getString(FieldType.ACQ_ID.getName())==null?"":string.getString(FieldType.ACQ_ID.getName());
				String rrn=string.getString(FieldType.RRN.getName())==null?"":string.getString(FieldType.RRN.getName());
				String cardMask=string.getString(FieldType.CARD_MASK.getName())==null?"":string.getString(FieldType.CARD_MASK.getName()) ;
				String ipAddress=string.getString(FieldType.INTERNAL_CUST_IP.getName())==null?"":string.getString(FieldType.INTERNAL_CUST_IP.getName()) ;
				body.append("<tr>\r\n" 
				+ "            <td>" + count + "</td>\r\n" + "            <td>"
						+ pgRefnum + "</td>\r\n" + "            <td>"
						+acquirerType + "</td>\r\n" + "            <td>"
						+ createDate + "</td>\r\n" + "            <td>"
						+ orderId + "</td>\r\n" + "            <td>"
						+ paymentType+ "</td>\r\n" + "            <td>"
						+ txnType+ "</td>\r\n" + "            <td>"
						+ status + "</td>\r\n" + "            <td>"
						+ totalAmount+ "</td>\r\n" + "            <td>"
						+ acq + "</td>\r\n" + "            <td>"
						+ rrn + "</td>\r\n" + "            <td>"
						+ cardMask+ "</td>\r\n" + "\r\n" + "            <td>"
						+ ipAddress+ "</td>\r\n" + "\r\n" 
						+ "        </tr>");
				count++;
			}
			
			body.append(
					"</tbody></table><p>Please visit the Pay10 chargeback merchant dashboard available within the Pay10 merchant panel and provide your response against each case carefully. </p>\r\n"
							+ "<p>NOTE: In case the merchant fails to submit the required documents to the acquiring bank, the bank will refund the amount to the customer's account and close the case as per existing chargeback guidelines. </p>\r\n"
							+ "<p>** PLEASE DO NOT USE THIS EMAIL ID FOR SUBMITTING CHARGEBACK RESPONSE. For any questions or concerns, please drop an email risk@pay10.com. You may also contact us via one of the listed phone numbers. </p>\r\n"
							+ "<br>\r\n" + "<p>Regards,</p>\r\n" + "<p>Risk & Compliance,</p>\r\n" + "<p>Pay10</p>");
			
			
			emailer.sendEmailChargeback(body.toString(), subject,m.toArray(new String[m.size()]), "CHARGEBACK INTIMATION");
			}
		}
		
		return "SuccessFully Send"; 
	}
	
	//New Method for Best Pay Charge Back added created by Deep Singh
	public String sendChargeBackMail(DMSEntity dmsEntity) {
		logger.info("Request Data for sendChargeBackMail : " + new Gson().toJson(dmsEntity));
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy h:mm a");
		Document document=bulkmailRepository.getChargebackByPGRefNumber(dmsEntity.getPgRefNo());
		StringBuffer body = new StringBuffer();
		String subject = "URGENT- BESTPAY CHARGEBACK INTIMATION - TOTAL CASES [" + 1 + "] ["
				+ dateFormat.format(new Date()) + "]";
		String merchantName=dmsEntity.getBusinessName().toUpperCase();
		String currentStatus=dmsEntity.getStatus().name().toUpperCase();
		String notificationEmail=dmsEntity.getNemail();
		String date1=new SimpleDateFormat("dd-MMM-yyyy").format(new Date());
		body.append("<h1 style=\"color: #202F4B; text-align: center;\">CHARGEBACK INTIMATION</h1>\r\n"
				+ "<hr style=\"background-color: #202F4B; width: 100%; height: 3px; border: none;\">\r\n"
				+ "<p>Merchant Name : "+merchantName+"</p>\r\n" + "<p>Date : "+date1+"</p>\r\n" + "<br>\r\n"
				+ "<p>Dear Merchant</p>\r\n" + "<p>Greetings from <b>BestPay!</b></p>\r\n"
				+ "<h4 style=\"color: #202F4B; \">We wish to inform you that Schemes has revised the Dispute Timeframe for Document submission. Please find the below revised timeframe. Revised timeframe docs attached for reference. </h4>\r\n"
				+ "<table border=\"1\">\r\n" + "    \r\n" + "    \r\n" + "    <tbody>\r\n" + "        <tr>\r\n"
				+ "            <td colspan=\"2\" style=\"text-align: center; font-weight: bold;color: white;background-color: rgb(104, 104, 253);\">CHARGEBACK</td>\r\n"
				+ "        </tr>\r\n"
				+ "        <tr style=\"text-align: center; font-weight: bold;color: black;background-color: rgb(160, 160, 168);\">\r\n"
				+ "            <td>\r\n" + "                Chargeback Types\r\n" + "            </td>\r\n"
				+ "            <td>\r\n" + "                Timeframe\r\n" + "            </td>\r\n"
				+ "        </tr>\r\n" + "        <tr>\r\n" + "            <td>\r\n"
				+ "                Chargeback  Intimation\r\n" + "            </td>\r\n" + "            <td>\r\n"
				+ "                1 Day to 4 Day (Calender Days)\r\n" + "            </td>\r\n"
				+ "        </tr>\r\n" + "        <tr>\r\n" + "            <td>\r\n"
				+ "                Pre-arbitration\r\n" + "            </td>\r\n" + "            <td>\r\n"
				+ "                1 Day to 2 Day (Calender Days)\r\n" + "            </td>\r\n"
				+ "        </tr>\r\n" + "        <tr>\r\n" + "            <td>\r\n"
				+ "               Pre-Compliance, Arbitration\r\n" + "            </td>\r\n"
				+ "            <td>\r\n" + "                1 Day (Calender Days)\r\n" + "            </td>\r\n"
				+ "        </tr>\r\n" + "    </tbody>\r\n" + "</table>\r\n"
				+ "<p>Please be advised that we have received chargeback cases for the transactions mentioned below.</p>\r\n"
				+ "<ul style=\"list-style-type: disc;\">\r\n"
				+ "<li>Total number of new chargebacks received: <b>"+1+"</b></li>"
				+ "<li>Current Status of chargeback: <b>"+currentStatus+"</b></li>\r\n" + "</ul>\r\n" + "\r\n"
				+ "<table border=\"1\">\r\n" + "    <tbody>\r\n" + "        <tr>\r\n"
				+ "            <td>Sr No.</td>\r\n" + "            <td>Pg Ref Num</td>\r\n"
				+ "            <td>Acquirer</td>\r\n" + "            <td>Date</td>\r\n"
				+ "            <td>Initiate Date</td>\r\n" + "            <td>Deadline Date</td>\r\n"
				+ "            <td>Order Id</td>\r\n" + "            <td>Payment Method</td>\r\n"
				+ "            <td>Txn Type</td>\r\n" + "            <td>Status</td>\r\n"
				+ "            <td>Total Amount</td>\r\n" + "            <td>ACQ ID</td>\r\n"
				+ "            <td>RRN</td>\r\n" + "            <td>Card Mask</td>\r\n" + "            \r\n"
			    + "            <td>Ip Address</td>\r\n" + "            \r\n    </tr>");
			
		
		String pgRefnum=document.getString(FieldType.PG_REF_NUM.getName())==null?"N/A":document.getString(FieldType.PG_REF_NUM.getName());
		String acquirerType=document.getString(FieldType.ACQUIRER_TYPE.getName())==null?"":document.getString(FieldType.ACQUIRER_TYPE.getName()) ;
		String createDate=document.getString(FieldType.CREATE_DATE.getName())==null?"":document.getString(FieldType.CREATE_DATE.getName());
		String orderId=document.getString(FieldType.ORDER_ID.getName())==null?"":document.getString(FieldType.ORDER_ID.getName());
		String paymentType=PaymentType.getpaymentName(document.getString(FieldType.PAYMENT_TYPE.getName()))==null?"":PaymentType.getpaymentName(document.getString(FieldType.PAYMENT_TYPE.getName())) ;
		String txnType=document.getString(FieldType.ORIG_TXNTYPE.getName())==null?"":document.getString(FieldType.ORIG_TXNTYPE.getName()) ;
		String status=document.getString(FieldType.STATUS.getName())==null?"":document.getString(FieldType.STATUS.getName());
		String totalAmount=document.getString(FieldType.TOTAL_AMOUNT.getName())==null?"": document.getString(FieldType.TOTAL_AMOUNT.getName()) ;
		String acq=document.getString(FieldType.ACQ_ID.getName())==null?"":document.getString(FieldType.ACQ_ID.getName());
		String rrn=document.getString(FieldType.RRN.getName())==null?"":document.getString(FieldType.RRN.getName());
		String cardMask=document.getString(FieldType.CARD_MASK.getName())==null?"":document.getString(FieldType.CARD_MASK.getName()) ;
		String ipAddress=document.getString(FieldType.INTERNAL_CUST_IP.getName())==null?"":document.getString(FieldType.INTERNAL_CUST_IP.getName()) ;
		
		String initiatedDate=dmsEntity.getCbInitiatedDate();
		String deadlineDate=dmsEntity.getCbDdlineDate();
		
		body.append("<tr>\r\n" 
					+ "<td>" + 1 + "</td>\r\n" + "<td>"
					+ pgRefnum + "</td>\r\n" + "<td>"
					+ acquirerType + "</td>\r\n" + "<td>"
					+ createDate + "</td>\r\n" + "<td>"
					+ initiatedDate + "</td>\r\n" + "<td>"
					+ deadlineDate + "</td>\r\n" + "<td>"
					+ orderId + "</td>\r\n" + "<td>"
					+ paymentType+ "</td>\r\n" + "<td>"
					+ txnType+ "</td>\r\n" + "<td>"
					+ status + "</td>\r\n" + "<td>"
					+ totalAmount+ "</td>\r\n" + "<td>"
					+ acq + "</td>\r\n" + "<td>"
					+ rrn + "</td>\r\n" + "<td>"
					+ cardMask+ "</td>\r" + "\r" + "<td>"
					+ ipAddress+ "</td>\r\n" + "\r\n"+ "</tr>");
		
		body.append(
				"</tbody></table><p>Please visit the <b>BestPay</b> chargeback merchant dashboard available within the <b>BestPay</b> merchant panel and provide your response against each case carefully. </p>\r\n"
						+ "<p>NOTE: In case the merchant fails to submit the required documents to the acquiring bank, the bank will refund the amount to the customer's account and close the case as per existing chargeback guidelines. </p>\r\n"
						+ "<p>** PLEASE DO NOT USE THIS EMAIL ID FOR SUBMITTING CHARGEBACK RESPONSE. For any questions or concerns, please drop an email risk@bpgate.net. You may also contact us via one of the listed phone numbers. </p>\r\n"
						+ "<br>\r\n" + "<p>Regards,</p>\r\n" + "<p>Risk & Compliance,</p>\r\n" + "<p><b>BestPay</b></p>");
		
		
		logger.info("Final Chargeback Email Body Here : " + body);
		String mailTo[]= {notificationEmail};
		emailer.sendEmailChargeback(body.toString(), subject,mailTo, "CHARGEBACK INTIMATION");
	
	return "SuccessFully Send"; 
}
}
