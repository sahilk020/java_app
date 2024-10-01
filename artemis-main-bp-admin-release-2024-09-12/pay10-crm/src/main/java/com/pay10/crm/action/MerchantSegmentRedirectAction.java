package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.RoleDao;
import com.pay10.commons.user.Segment;
import com.pay10.commons.user.SegmentInfo;
import com.pay10.commons.util.BusinessType;

public class MerchantSegmentRedirectAction extends AbstractSecureAction {
	private static Logger logger = LoggerFactory.getLogger(MerchantSegmentRedirectAction.class.getName());
	private static final long serialVersionUID = -2541609533197706532L;
	private Map<String, String> industryTypes = new TreeMap<String, String>();
	@Autowired
	private RoleDao roleDao;
	private List<Segment> segments;
	private String segmentName;
	private List<SegmentInfo> segmentInfo;

	@Override
	public String execute() {
		try {

			logger.info("segmentName : " + segmentName);

			setSegments(roleDao.getAllSegment());
			Map<String, String> industryCategoryLinkedMap = BusinessType.getIndustryCategoryList();
			industryTypes.putAll(industryCategoryLinkedMap);
			
			if (segmentName == null) {
				setSegmentInfo(new ArrayList<SegmentInfo>());
			} else {
				setSegmentInfo(roleDao.getSegmentInfo(segmentName));
				//logger.info("SegmentInfo : "+getSegmentInfo().toString());
			}

			return INPUT;
		} catch (Exception exception) {
			logger.error("Exception ", exception);
			return ERROR;
		}
	}

	public String getSegmentName() {
		return segmentName;
	}

	public void setSegmentName(String segmentName) {
		this.segmentName = segmentName;
	}

	public List<SegmentInfo> getSegmentInfo() {
		return segmentInfo;
	}

	public void setSegmentInfo(List<SegmentInfo> segmentInfo) {
		this.segmentInfo = segmentInfo;
	}

	public Map<String, String> getIndustryTypes() {
		return industryTypes;
	}

	public void setIndustryTypes(Map<String, String> industryTypes) {
		this.industryTypes = industryTypes;
	}

	public List<Segment> getSegments() {
		return segments;
	}

	public void setSegments(List<Segment> segments) {
		this.segments = segments;
	}

}