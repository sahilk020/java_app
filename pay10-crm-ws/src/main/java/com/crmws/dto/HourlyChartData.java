package com.crmws.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HourlyChartData implements Serializable {

	private static final long serialVersionUID = -4645093172456556229L;
	private String txnDate;
	private int totalSuccess;
	private int totalRefund;
	private int totalFailed;
	private int totalCancelled;
}
