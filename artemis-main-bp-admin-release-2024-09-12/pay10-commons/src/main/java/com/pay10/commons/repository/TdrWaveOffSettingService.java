package com.pay10.commons.repository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.TdrWaveOffSettingRepository;
import com.pay10.commons.entity.TdrWaveOffSettingEntity;

@Service
public class TdrWaveOffSettingService {
	
	@Autowired
	private TdrWaveOffSettingRepository tdrWaveOffSettingRepository;
	
	public double getCalculateAmountTdr(String payId, double amount) {
	
		double totalAmountWithTDRAndGst=0;
	
		
		TdrWaveOffSettingEntity tdrWaveOffSettingEntity=tdrWaveOffSettingRepository.getTdrFromPayIdAndMinAndMaxAmount(payId, amount);
//		if (tdrWaveOffSettingEntity.getIsExampted().equalsIgnoreCase("y")) {
//			totalAmountWithTDRAndGst=0;
//		}else {
//			double percentage=tdrWaveOffSettingEntity.getPercentage();
//			double totalTdrAmount=percentage*amount;
//			double totalGSTOnTotalTdrAmount=(18/100)*totalTdrAmount;
//			totalAmountWithTDRAndGst=amount+totalTdrAmount+totalGSTOnTotalTdrAmount;
//		}
		
		return  totalAmountWithTDRAndGst;
	}
}
