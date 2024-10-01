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
public class PieChart implements Serializable {

	private static final long serialVersionUID = -931298634037266927L;

	private String label;
	private int count;
	
	private String totalSaleAmount;
	private String totalRefundAmount;
	private String totalCancelledAmount;
	private String totalFailedAmount;
	private String totalFraudAmount;
	private String totalSettleAmount;

	private int totalSaleCount;
	private int totalRefundCount;
	private int totalCancelledCount;
	private int totalFailedCount;
	private int totalFraudCount;
	private int totalTxnCount;
	private int totalSettleCount;


}
