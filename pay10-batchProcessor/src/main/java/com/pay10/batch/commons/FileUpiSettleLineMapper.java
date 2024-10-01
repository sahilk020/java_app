package com.pay10.batch.commons;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.util.FieldType;


/**
 * Mapping per lines read from file (strings) to {@links Fields} objects.
 * Mainly implemented to get line number & set them to {@links Fields} 
 */
public class FileUpiSettleLineMapper<T> implements LineMapper<Fields>, InitializingBean {
	
	private static final Logger logger = LoggerFactory.getLogger(FileUpiSettleLineMapper.class);
	
	private String[] fileParameter;

	private LineTokenizer tokenizer;

	private FieldSetMapper<FieldSet> fieldSetMapper;
	
	@Autowired
	private Fields fields;
	
	/*Mapping line data to the Fields object*/
	public Fields mapLine(String line, int lineNumber) throws Exception {
		Map<String, String> value = new HashMap<String, String>();
		
		logger.info("Parsing line number: " + lineNumber + " having data: " + line);

		FieldSet fieldSet = fieldSetMapper.mapFieldSet(tokenizer.tokenize(line));
		
		for (String fieldName : fileParameter) {
			value.put(fieldName, fieldSet.readString(fieldName).replace("\'", ""));
		}
 		value.put(FieldType.FILE_LINE_NO.getName(), String.valueOf(lineNumber));
		value.put(FieldType.FILE_LINE_DATA.getName(), line);
		fields.setFields(value);
		fields.setPrevious(null);
		fields.removeExtAcqFields();
		return fields;
	}

	public void setFileParameter(String[] fileParameter) {
		this.fileParameter = fileParameter;
	}

	public void setLineTokenizer(LineTokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}

	public void setFieldSetMapper(FieldSetMapper<FieldSet> fieldSetMapper) {
		this.fieldSetMapper = fieldSetMapper;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// Not Required
	}
}