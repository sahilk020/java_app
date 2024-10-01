package com.crmws.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmws.service.AuditTrailsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailDao;

@Service
public class AuditTrailServiceImpl implements AuditTrailsService {

	private static final Logger logger = LoggerFactory.getLogger(AuditTrailServiceImpl.class.getName());

	@Autowired
	private AuditTrailDao auditTrailDao;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private AuditTrailPdfUtil auditTrailPdfUtil;

	static Map<Long, ByteArrayInputStream> cacheMap = new HashMap<>();

	@Override
	public String diffHighlight(long id) {

		try {
			AuditTrail auditTrail = auditTrailDao.getAuditTrail(id);
			boolean isDelete = StringUtils.containsIgnoreCase(auditTrail.getActionMessageByAction().getActionMessage(),
					"delete");
			return highlightDiff(auditTrail.getPayload(), auditTrail.getPreviousValue(), isDelete);
		} catch (Exception ex) {
			ex.printStackTrace();
			// no-op
		}
		return null;
	}

	private String highlightDiff(String payloads, String prevValues, boolean isDelete) throws IOException {
		Map<String, Object> payloadMap = transformToFlat(payloads);
		String prevVal = StringUtils.isBlank(prevValues) ? payloads : prevValues;
		Map<String, Object> prevMap = transformToFlat(prevVal);
		String html = "{</br>";
		for (String key : prevMap.keySet()) {
			String value = String.valueOf(prevMap.get(key));
			String nValue = String.valueOf(payloadMap.get(key));

			if(key.equalsIgnoreCase("updateDate")){
				value=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(value)));
				nValue=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(nValue)));;
			}

			html = StringUtils.join(html, "\"", key, "\"", " : ");
			if (StringUtils.isBlank(prevValues)) {
				if (isDelete) {
					html = StringUtils.join(html, appendRemoveTag(value), ",", "</br>");
					continue;
				}
				html = StringUtils.join(html, appendAddTag(value), ",", "</br>");
				continue;
			}
			if (StringUtils.equals(value, nValue)) {
				html = StringUtils.join(html, value, ",", "</br>");
				continue;
			}
			if (StringUtils.equals(nValue, "null")) {
				html = StringUtils.join(html, appendRemoveTag(value), ",</br>");
				continue;
			}
			html = StringUtils.join(html, appendRemoveTag(value), appendAddTag(mapper.writeValueAsString(nValue)),
					",</br>");
		}

		html = StringUtils.substring(html, 0, html.length() - 6);
		html = html.concat("</br>}");
		return html;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> transformToFlat(String value) throws IOException {
		JsonNode valueNode = mapper.readTree(value);
		Map<String, Object> result = new HashMap<>();
		if (valueNode.isArray()) {
			for (int i = 0; i < valueNode.size(); i++) {
				result.putAll(convertObjToMap("", mapper.convertValue(valueNode.get(i), HashMap.class)));
			}
			return result;
		}
		result = convertObjToMap("", mapper.convertValue(valueNode, HashMap.class));
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, Object> convertObjToMap(String key, Map<String, Object> value) throws JsonProcessingException {
		Map<String, Object> result = new HashMap<>();
		for (String vKey : value.keySet()) {
			String iKey = StringUtils.isBlank(key) ? vKey : StringUtils.join(key, ".", vKey);
			String innerValue = mapper.writeValueAsString(value.get(vKey));
			if (value.get(vKey) instanceof List || StringUtils.startsWith(innerValue, "[")) {
				int count = 0;
				for (Object obj : (List) value.get(vKey)) {
					result.putAll(
							convertObjToMap(StringUtils.join(iKey, count), mapper.convertValue(obj, HashMap.class)));
					count++;
				}
				continue;
			}
			if (StringUtils.startsWith(innerValue, "{")) {
				result.putAll(convertObjToMap(iKey, mapper.convertValue(value.get(vKey), HashMap.class)));
				continue;
			}
			result.put(iKey, value.get(vKey));
		}

		return result;
	}

	private String appendAddTag(String value) {
		return StringUtils.join("<ins>", value, "</ins>");
	}

	private String appendRemoveTag(String value) {
		return StringUtils.join("<del>", value, "</del>");
	}

	@Override
	public ByteArrayInputStream generatePdf(long id) {
		AuditTrail auditTrail = auditTrailDao.getAuditTrail(id);
		try {
			return auditTrailPdfUtil.createPdf(transformToFlat(auditTrail.getPayload()),
					transformToFlat(auditTrail.getPreviousValue()));
		} catch (IOException ex) {
			logger.error("Exception:", ex);
			return null;
		}
	}
}
