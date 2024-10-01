package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.pay10.commons.dao.AcquirerMappingPoDao;
import com.pay10.commons.payoutEntity.PayoutAcquirer;
import com.pay10.commons.payoutEntity.PayoutAcquirerMappingDTO;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;

public class MerchantMappingActionPO extends AbstractSecureAction {

	@Autowired
	private UserDao userDao;

	@Autowired
	private AcquirerMappingPoDao acquirerMappingPoDao;
	
	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;
	
	public HashMap<String, String> acquirerList = new HashMap<>();
	
	private List<String> channelList = new ArrayList<>();
	
	private List<User> merchantList = new ArrayList<User>();
	
	private String acquirer;
	
	private String channel;
	
	private List<PayoutAcquirerMappingDTO>acquirerMappingDTOs=new ArrayList<>();
	
	@Override
	@SuppressWarnings("unchecked")
	public String execute() {
		setMerchantList(userDao.fetchUsersBasedOnPOEnable());
		
		List<String>list=acquirerMappingPoDao.findDistinctAcquirer();
		
		for (String string : list) {
			acquirerList.put(string, string);
		}
		return SUCCESS;
	}
	
	public String getChannel() {
		setChannelList(acquirerMappingPoDao.findDistinctChannel(acquirer));
		return SUCCESS;
	}
	public String getMappedString() {
		
		List<PayoutAcquirer>list=acquirerMappingPoDao.findMappedString(acquirer, channel);
		list.stream().forEach(x->{
			PayoutAcquirerMappingDTO mappingDTO=new PayoutAcquirerMappingDTO();
			mappingDTO.setCurrencyName(multCurrencyCodeDao.getCurrencyNamebyCode(x.getCurrencyCode()));
			
			String []mops=x.getAllowedMop().split(",");
			List<String>mopList=new ArrayList<>();
			for (String mop : mops) {
				mopList.add(mop);
			}
			mappingDTO.setAllowedMop(mopList);
			acquirerMappingDTOs.add(mappingDTO);
		});
		
		System.out.println(new Gson().toJson(acquirerMappingDTOs));
		return SUCCESS;
	}
	public HashMap<String, String> getAcquirerList() {
		return acquirerList;
	}
	public void setAcquirerList(HashMap<String, String> acquirerList) {
		this.acquirerList = acquirerList;
	}
	public List<User> getMerchantList() {
		return merchantList;
	}
	public void setMerchantList(List<User> merchantList) {
		this.merchantList = merchantList;
	}

	public List<String> getChannelList() {
		return channelList;
	}

	public void setChannelList(List<String> channelList) {
		this.channelList = channelList;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public List<PayoutAcquirerMappingDTO> getAcquirerMappingDTOs() {
		return acquirerMappingDTOs;
	}

	public void setAcquirerMappingDTOs(List<PayoutAcquirerMappingDTO> acquirerMappingDTOs) {
		this.acquirerMappingDTOs = acquirerMappingDTOs;
	}
	
	
}
