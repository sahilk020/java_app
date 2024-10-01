package com.crmws.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashBoardTxnCountRequest {

	private String dateFrom;
	private String dateTo;
	private String emailId;
	private String currency;
	private String paymentType;
	private String mopType;
	private String txnType;
	private String acquirer;

}
