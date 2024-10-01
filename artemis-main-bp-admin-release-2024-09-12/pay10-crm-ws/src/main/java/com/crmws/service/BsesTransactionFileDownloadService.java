package com.crmws.service;

import com.pay10.commons.entity.TdrWaveOffSettingEntity;

public interface BsesTransactionFileDownloadService {
	public String getDownloadTransactionWiseReport();
	public String downloadTxtFile();
	public String downloadMisReport();
	public String save(TdrWaveOffSettingEntity entity);
	public String sendMailBses();
}
