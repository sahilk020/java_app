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
public class SettlementChartData implements Serializable {

	private static final long serialVersionUID = 7690438813843478158L;

	private String txnDate;
	private double totalCapturedAmount;
	private double totalSettledAmount;
}
