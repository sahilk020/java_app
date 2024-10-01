package com.pay10.crm.audittrail;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailDao;
import com.pay10.commons.util.DataEncoder;
import com.pay10.crm.action.AbstractSecureAction;

/**
 * @author Jay Gajera
 *
 */
public class AuditTrailDetailAction extends AbstractSecureAction {

	private static final long serialVersionUID = -137317101627343799L;
	private static final Logger logger = LoggerFactory.getLogger(AuditTrailDetailAction.class.getName());

	@Autowired
	private AuditTrailDao auditTrailDao;

	@Autowired
	private DataEncoder encoder;

	private List<AuditTrail> aaData = new ArrayList<>();
	private String emailId;
	private String dateFrom;
	private String dateTo;

	// field related to manage pagination
	private int draw;
	private int length;
	private int start;
	private BigInteger recordsTotal;
	private BigInteger recordsFiltered;

	@Override
	public String execute() {
		try {
			long totalCount = auditTrailDao.getCountByFilter(getEmailId(), getDateFrom(), getDateTo());
			setRecordsTotal(BigInteger.valueOf(totalCount));
			if (getLength() == -1) {
				setLength(getRecordsTotal().intValue());
			}
			setAaData(encoder.encodeAuditTrail(
					auditTrailDao.getByFilter(getEmailId(), getDateFrom(), getDateTo(), getStart(), getLength())));
			recordsFiltered = recordsTotal;
			return SUCCESS;
		} catch (Exception ex) {
			logger.error("Exception:", ex);
			return ERROR;
		}
	}

	public List<AuditTrail> getAaData() {
		return aaData;
	}

	public void setAaData(List<AuditTrail> aaData) {
		this.aaData = aaData;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
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
}
