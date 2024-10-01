package com.pay10.crm.role.action;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.RoleDao;
import com.pay10.commons.user.Segment;
import com.pay10.crm.action.AbstractSecureAction;

public class SegmentByGroupId extends AbstractSecureAction {

	private static final long serialVersionUID = 381384537713089428L;
	private static final Logger logger = LoggerFactory.getLogger(SegmentByGroupId.class.getName());

	@Autowired
	private RoleDao roleDao;

	private long groupId;
	private String type;
	private List<Segment> segments;

	@Override
	public String execute() {

		try {
			setSegments(roleDao.getAllSegment());
			return INPUT;
		} catch (Exception ex) {
			logger.error("Exception", ex);
			return ERROR;
		}
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Segment> getSegments() {
		return segments;
	}

	public void setSegments(List<Segment> segments) {
		this.segments = segments;
	}

	

}
