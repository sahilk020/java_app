package com.pay10.batch;

import java.util.List;

import com.pay10.batch.commons.Fields;
import com.pay10.batch.commons.util.Amount;
import com.pay10.batch.exception.DatabaseException;
import com.pay10.batch.exception.ErrorType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Write the supplied {@link Fields} type object to the MongoDB. Will not be
 * called with any null items. Also includes default retry behavior when any
 * exception occurs on write(). A retry implies a rollback, so we needs to
 * restore the FieldType.RECO_AMOUNT formatting for retried operations.
 * 
 * @param items
 *            of {@link Fields} type is the item to be written.
 * @exception will
 *                throw exception if find some problem while writing to DB.
 */
public class MongoDbWriter implements ItemWriter<Fields> {

	private static final Logger logger = LoggerFactory.getLogger(MongoDbWriter.class);

	@Autowired
	private Amount amount;

	/*Inserting Reco and RecoReporting data in MongoDB*/
	@Override
	 public void write(List<? extends Fields> items) throws Exception
	{
		if ((items != null) && (!items.isEmpty())) {
			List<Fields> fieldsList = (List<Fields>) items;
			for (Fields fields : fieldsList) {
				try {
					String pg_ref_num = fields.get(FieldType.PG_REF_NUM.getName());
					if ((pg_ref_num != null) && (pg_ref_num != "") && (fields.get(FieldType.RESPONSE_CODE.getName()).equals(ErrorType.SUCCESS.getResponseCode()))) {
						logger.info("Writing Reco Transaction data to Mongo DB adapter: " + fields.getFieldsAsString());
						fields.insertNewRecoRecord();
					} else if (!Constants.RECO_TRUE.getValue().equalsIgnoreCase(fields.get(FieldType.NO_REPORTING_REQUIRED.getName()))) {
						logger.info("Writing Reco Reporting data to Mongo DB adapter: " + fields.getFieldsAsString());
						fields.insertNewRecoReportingRecord();
					}
				} catch (DatabaseException e) {
					logger.error("Exception while writing data to DB: " + e.getMessage() + " , for fields: " + fields.getFieldsAsString());
					fields.put(FieldType.RECO_AMOUNT.getName(), amount.formatAmount(fields.get(FieldType.RECO_BOOKING_AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName())));
					throw e;
				}
			}
		}
	}
}
