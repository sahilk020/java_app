package com.pay10.bindb.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import com.pay10.bindb.bindao.BinRangeDao;
import com.pay10.commons.util.BinRange;
import com.pay10.commons.util.ConfigurationConstants;


@Service
@Scope(value="session", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class BinRangeProvider {

	@Autowired
	@Qualifier("binDbBinRangeDao")
	private BinRangeDao binRangeDao;
	@Autowired
	private BinRangeParser binRangeParser;

	@Autowired
	private ApiBinRangeProvider apiBinRangeProvider;

	private static Logger logger = LoggerFactory.getLogger(BinRangeProvider.class.getName());
	public static Map<String, List<BinRange>> binCodeMap = new HashMap<String, List<BinRange>>(); 

	public Map<String, String> findBinRange(String cardBin) {

		Map<String, String> binMap = new HashMap<String, String>();
		try {
			// use dataBase binRange
			BinRange binRange = new BinRange();

			if (cardBin.length() == 9) {
				List<BinRange> binRangeList = new ArrayList<BinRange>();
				//binRangeList = binCodeMap.get(cardBin.substring(0,6));

				binRangeList = binRangeDao.findBinCodeLow(cardBin.substring(0,6));

				if (binRangeList.size() == 0 ) {
					logger.info("No bins found in DB for bin code = " + cardBin.substring(0,6));
					if(ConfigurationConstants.BIN_API_CALL_FLAG.getValue().equals(Constant.FLAG_ON)) {
						logger.info("Fetch from API");

						binRange = apiBinRangeProvider.getBinRangeViaBinCodeAPI(cardBin.substring(0,6));
						//add to db
						binRangeDao.create(binRange);
						binRangeList.add(binRange);
					}					
				}

				//if bin not found return blank map
				if (binRangeList.size() == 0 ) {
					return binMap;
				}else if (binRangeList.size() == 1 ) {

					for (BinRange binRangeDB : binRangeList) {
						binMap = binRangeParser.parseToMap(binRangeDB);
						return binMap;
					}
				}
				else {
					// for size greater than 1 multiple bins
					for (BinRange binRangeDB : binRangeList) {

						int lowBinRange = Integer.valueOf(binRangeDB.getBinRangeLow().substring(0, 9));
						int highBinRange = Integer.valueOf(binRangeDB.getBinRangeHigh().substring(0, 9));
						int cardBinValue = Integer.valueOf(cardBin);

						if (cardBinValue >= lowBinRange && cardBinValue <= highBinRange) {
							binMap = binRangeParser.parseToMap(binRangeDB);
							return binMap;
						}

					}

				}
			}
			else if (cardBin.length() == 6) {

				List<BinRange> binRangeList = binRangeDao.findBinCodeLow(cardBin);

				// If only one result found , send response
				if (binRangeList.size() == 1 ) {

					for (BinRange binRangeDB : binRangeList) {
						binMap = binRangeParser.parseToMap(binRangeDB);
						return binMap;
					}
				}else if(binRangeList.size() == 0 ) {
					logger.info("No bins found in DB for bin code = " + cardBin);
					if(ConfigurationConstants.BIN_API_CALL_FLAG.getValue().equals(Constant.FLAG_ON)) {
						logger.info("Fetch from API");

						binRange = apiBinRangeProvider.getBinRangeViaBinCodeAPI(cardBin.substring(0,6));
						binMap = binRangeParser.parseToMap(binRange);
						//add to db
						binRangeDao.create(binRange);
					}
					//return if flag off or not found
					return binMap;
				}else if(binRangeList.size() > 0 ){
					//return first
					for (BinRange binRangeDB : binRangeList) {
						binMap = binRangeParser.parseToMap(binRangeDB);
						return binMap;
					}
				}
				else {
					//return blank map
					return binMap;
				}
			}

			else {
				//return blank for bin length other than 6 or 9
				return binMap;
			}

			if (null == binRange) {

			} else {
				binMap = binRangeParser.parseToMap(binRange);
			}
		} catch (Exception exception) {
			logger.error("Unable to Process Bin API Request", exception);
		}
		// TODO.... handle exception
		return binMap;

	}

}
