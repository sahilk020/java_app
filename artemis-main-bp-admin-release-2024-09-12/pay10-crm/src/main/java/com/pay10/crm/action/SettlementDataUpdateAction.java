package com.pay10.crm.action;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.SettlementDataUpdateDao;
import com.pay10.commons.user.SettlementDataUpdate;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.SettledTransactionDataService;

/**
 * @author Shaiwal
 *
 */

public class SettlementDataUpdateAction extends AbstractSecureAction {

	private static final long serialVersionUID = 4747578787024374486L;

	private static Logger logger = LoggerFactory.getLogger(SettlementDataUpdateAction.class.getName());

	private int draw;
	private int length;
	private int start;
	private BigInteger recordsTotal;
	public BigInteger recordsFiltered;
	private String response;
	private List<SettlementDataUpdate> aaData;
	private User sessionUser = new User();
	private String fromDate;
	private String toDate;
	
	@Autowired
	private SettlementDataUpdateDao  SettlementDataUpdateDao;
	
	@Autowired
	private SettledTransactionDataService settledTransactionDataService;
	
	@Override
	@SuppressWarnings("deprecation")
	public String execute() {
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
		
		logger.info("Inside SettlementDataUpdateAction");
		try {
			
		//SettlementDataUpdate SettlementDataUpdate = SettlementDataUpdateDao.findLastUpload();
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		
		settledTransactionDataService.updateSettledData(fromDate, toDate);
			/*if (SettlementDataUpdate == null) {
				
				//String fromDate = "2019-05-02 00:00:00";
				//String toDate = "2019-05-02 23:59:00";
				Date dateFrom =  format.parse(fromDate);
				Date dateTo =   format.parse(toDate);
				
				SettlementDataUpdate SettlementDataUpdateUpdate = new SettlementDataUpdate();
				SettlementDataUpdateUpdate.setCreatedDate(dateTo);
				SettlementDataUpdateUpdate.setFromDate(dateFrom);
				SettlementDataUpdateUpdate.setToDate(dateTo);
				SettlementDataUpdateUpdate.setRequestedBy(sessionUser.getEmailId());
				SettlementDataUpdateUpdate.setStatus(TDRStatus.PENDING.getName());
				SettlementDataUpdateDao.create(SettlementDataUpdateUpdate);
				
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String dateTextFrom = dateFormat.format(dateFrom);
				String dateTextTo = dateFormat.format(dateTo);
				
				settledTransactionDataService.updateSettledData(dateTextFrom, dateTextTo);
				setResponse("Upload Complete");
				
				Session session = null;
				Long id;
				try {
					
					SettlementDataUpdate settlementDataUpdateLast = SettlementDataUpdateDao.findLastUpload();
					logger.info("Found old settlementDataUpdateLast ");
					session = HibernateSessionProvider.getSession();
					Transaction tx = session.beginTransaction();
					id = settlementDataUpdateLast.getId();
					session.load(settlementDataUpdateLast, settlementDataUpdateLast.getId());
					SettlementDataUpdate settlementDataUpdate = (SettlementDataUpdate) session.get(SettlementDataUpdate.class, id);

					settlementDataUpdate.setStatus(TDRStatus.ACCEPTED.getName());
					session.update(settlementDataUpdate);
					tx.commit();
					session.close();
					logger.info("Updated old settlementDataUpdateLast ");
				} catch (HibernateException e) {
					logger.error("settlementDataUpdateLast edit failed");
					return SUCCESS;
				}
				
				return SUCCESS;
			}
			
			else {
				
				if (SettlementDataUpdate.getStatus().equalsIgnoreCase(TDRStatus.PENDING.getName())) {
					setResponse("Upload already in progress");
					return SUCCESS;
				}
				else {
				
					Date dateFrom;
					Date dateTo;
					
					if (StringUtils.isNotBlank(fromDate)) {
						fromDate = DateCreater.toDateTimeformatCreater(fromDate);
						dateFrom =  format.parse(fromDate);
						
					}
					else {
						dateFrom =   SettlementDataUpdate.getToDate();
						dateFrom.setSeconds(dateFrom.getSeconds()+1);
					}
					
					if (StringUtils.isNotBlank(toDate)) {
						toDate = DateCreater.toDateTimeformatCreater(toDate);
						dateTo =  format.parse(toDate);
					}
					else {
						dateTo =   new Date();
					}
					
					
					
					SettlementDataUpdate SettlementDataUpdateUpdate = new SettlementDataUpdate();
					SettlementDataUpdateUpdate.setCreatedDate(new Date());
					SettlementDataUpdateUpdate.setFromDate(dateFrom);
					SettlementDataUpdateUpdate.setToDate(dateTo);
					SettlementDataUpdateUpdate.setRequestedBy(sessionUser.getEmailId());
					SettlementDataUpdateUpdate.setStatus(TDRStatus.PENDING.getName());
					SettlementDataUpdateDao.create(SettlementDataUpdateUpdate);
					
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String dateTextFrom = dateFormat.format(dateFrom);
					String dateTextTo = dateFormat.format(dateTo);
					
					settledTransactionDataService.updateSettledData(dateTextFrom, dateTextTo);
					setResponse("Upload Complete");
					Session session = null;
					Long id;
					try {
						
						SettlementDataUpdate settlementDataUpdateLast = SettlementDataUpdateDao.findLastUpload();
						logger.info("Found old settlementDataUpdateLast ");
						session = HibernateSessionProvider.getSession();
						Transaction tx = session.beginTransaction();
						id = settlementDataUpdateLast.getId();
						session.load(settlementDataUpdateLast, settlementDataUpdateLast.getId());
						SettlementDataUpdate settlementDataUpdate = (SettlementDataUpdate) session.get(SettlementDataUpdate.class, id);

						settlementDataUpdate.setStatus(TDRStatus.ACCEPTED.getName());
						session.update(settlementDataUpdate);
						tx.commit();
						session.close();
						logger.info("Updated old settlementDataUpdateLast ");
					} catch (HibernateException e) {

						logger.error("settlementDataUpdateLast edit failed");
						return ERROR;
					}
					return SUCCESS;
				}
				
			}*/
		}
		catch(Exception e) {
			logger.error("Exception "+e);
		}
		return SUCCESS;
	}
			
		

	public String getHistory() {
		
		logger.info("Inside SettlementDataUpdateAction , getHistory()");
		int totalCount;
		
		try {
			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			List<SettlementDataUpdate> SettlementDataUpdateList = new ArrayList<SettlementDataUpdate>();
			SettlementDataUpdateList = SettlementDataUpdateDao.findAll();
			
					totalCount = SettlementDataUpdateList.size();
					BigInteger bigInt = BigInteger.valueOf(totalCount);
					setRecordsTotal(bigInt);
					if (getLength() == -1) {
						setLength(getRecordsTotal().intValue());
					}
					aaData = SettlementDataUpdateList;
					recordsFiltered = recordsTotal;
					return SUCCESS;
		}
		
		catch(Exception e) {
			logger.error("Exception in getHistory "+e);
			return SUCCESS;
		}
	
		
	}
	

	
	@Override
	public void validate() {

	}


	public List<SettlementDataUpdate> getAaData() {
		return aaData;
	}

	public void setAaData(List<SettlementDataUpdate> aaData) {
		this.aaData = aaData;
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

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}



	public String getFromDate() {
		return fromDate;
	}



	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

}
