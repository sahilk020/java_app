package com.pay10.crm.audittrail;

import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.action.AbstractSecureAction;

public class DiffHighlights extends AbstractSecureAction {

	private static final long serialVersionUID = 9100046133467825825L;
	private long diffId;
	private String diffPdfApiUrl;

	@Override
	public String execute() {
		setDiffPdfApiUrl(PropertiesManager.propertiesMap.get(Constants.DIFF_PDF_API.getValue()));
		return INPUT;
	}

	public long getDiffId() {
		return diffId;
	}

	public void setDiffId(long diffId) {
		this.diffId = diffId;
	}

	public String getDiffPdfApiUrl() {
		return diffPdfApiUrl;
	}

	public void setDiffPdfApiUrl(String diffPdfApiUrl) {
		this.diffPdfApiUrl = diffPdfApiUrl;
	}
}
