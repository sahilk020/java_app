package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.AcquirerSchadular;
import com.pay10.commons.user.AcquirerSchadulardao;
import com.pay10.commons.util.TDRStatus;

public class AcquirerScheduler extends AbstractSecureAction{
	private static final Logger logger = LoggerFactory.getLogger(AcquirerScheduler.class.getName());

	private String acquirer;
	private Long id;
	private String response;
	private String Maxtime;
	private String Mintime;
	private Long idedit;


	private List<AcquirerSchadular> acquirerSchadular = new ArrayList<AcquirerSchadular>();
	private List<AcquirerSchadular> searchSchadular = new ArrayList<AcquirerSchadular>();

	@Autowired
	AcquirerSchadulardao acquirerSchadulardao;
	
	public String execute() {		
		return INPUT;
	}
	
	
	public String SaveDetail() {
		AcquirerSchadular	acquirerschadular =new AcquirerSchadular() ;
		acquirerschadular.setAcquirer(acquirer);	
		acquirerschadular.setStartTime(Maxtime);
		acquirerschadular.setEndTime(Mintime);
		acquirerschadular.setStatus("ACTIVE");
		AcquirerSchadular findRule=acquirerSchadulardao.getMatchingRule(acquirerschadular);
		if (findRule != null) {
			setResponse(ErrorType.duplicated_record_cant_be_save.getResponseMessage());

			return  SUCCESS;	
		}
		acquirerSchadulardao.createAndUpdate(acquirerschadular);
		setResponse(ErrorType.Acquirer_record_save.getResponseMessage());

		return  SUCCESS;
		
			}
	
	public String getSchadularDetails() {
		setAcquirerSchadular(acquirerSchadulardao.getAcquirerDetails());

		return SUCCESS;
		
	}
	
	
	
	@SuppressWarnings("unchecked")
public String deleteAcquirerRule() {
	AcquirerSchadular	acquirerschadular =new AcquirerSchadular() ;
			acquirerschadular.setId(id);
		acquirerschadular.setStatus("INACTIVE");
		acquirerSchadulardao.createAndUpdate(acquirerschadular);
		setResponse(ErrorType.ReMove_successful.getResponseMessage());
		return SUCCESS;
	}

	public String SaveDetailEdited() {
		AcquirerSchadular	acquirerschadular =new AcquirerSchadular() ;
		acquirerschadular.setId(idedit);
		acquirerschadular.setAcquirer(acquirer);	
		acquirerschadular.setStartTime(Maxtime);
		acquirerschadular.setEndTime(Mintime);
		acquirerschadular.setStatus("ACTIVE");
		acquirerSchadulardao.createAndUpdate(acquirerschadular);
		return  SUCCESS;
			}
	
	public String SearchDetailsdata() {
		setSearchSchadular(acquirerSchadulardao.getSearchDetails(acquirer));
		return SUCCESS;
		
	}
	


	public String getAcquirer() {
		return acquirer;
	}


	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}


	public List<AcquirerSchadular> getAcquirerSchadular() {
		return acquirerSchadular;
	}


	public void setAcquirerSchadular(List<AcquirerSchadular> acquirerSchadular) {
		this.acquirerSchadular = acquirerSchadular;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getResponse() {
		return response;
	}


	public void setResponse(String response) {
		this.response = response;
	}


	public String getMaxtime() {
		return Maxtime;
	}


	public void setMaxtime(String maxtime) {
		Maxtime = maxtime;
	}


	public String getMintime() {
		return Mintime;
	}


	public void setMintime(String mintime) {
		Mintime = mintime;
	}


	public Long getIdedit() {
		return idedit;
	}


	public void setIdedit(Long idedit) {
		this.idedit = idedit;
	}


	public List<AcquirerSchadular> getSearchSchadular() {
		return searchSchadular;
	}


	public void setSearchSchadular(List<AcquirerSchadular> searchSchadular) {
		this.searchSchadular = searchSchadular;
	}
	


}
