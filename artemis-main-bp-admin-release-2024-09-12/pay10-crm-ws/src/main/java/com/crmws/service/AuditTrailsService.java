package com.crmws.service;

import java.io.ByteArrayInputStream;

public interface AuditTrailsService {

	public String diffHighlight(long id);

	public ByteArrayInputStream generatePdf(long id);
}
