package com.pay10.crm.actionBeans;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pay10.commons.dao.DataAccessObject;
import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.User;
import com.pay10.commons.util.BinRange;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;

public class BinRangeDisplayServices extends HibernateAbstractDao {

	
	private static Logger logger = LoggerFactory.getLogger(BinRangeDisplayServices.class.getName());
	
	private Connection getConnection() throws SQLException {
		return DataAccessObject.getBasicConnection();
	}
	
	public List<BinRange> getBinRangDisplay(String cardType, String mopType, User user,int start,int length) throws SystemException {
		List<BinRange> binRangList = new ArrayList<BinRange>();
			try (Connection connection = getConnection()) {
				try (PreparedStatement prepStmt = connection
						.prepareStatement("{call binRange_Records(?,?,?,?,?)}")) {
					prepStmt.setString(1, cardType);
					prepStmt.setString(2, mopType);
					prepStmt.setString(3, user.getUserType().toString());
					prepStmt.setInt(4, start);
					prepStmt.setInt(5, length);
					try (ResultSet rs = prepStmt.executeQuery()) {
						while (rs.next()) {
							BinRange binRange = new BinRange();
							binRange.setBinCodeLow(rs.getString("binCodeLow"));
							binRange.setBinCodeHigh(rs.getString("binCodeHigh"));
							binRange.setBinRangeLow(rs.getString("binRangeLow"));
							binRange.setBinRangeHigh(rs.getString("binRangeHigh"));
							binRange.setCardType(PaymentType.getInstanceIgnoreCase(rs.getString("cardType")));
							binRange.setMopType(MopType.getInstanceIgnoreCase(rs.getString("mopType")));
							binRange.setIssuerBankName(rs.getString("issuerBankName"));
							binRange.setIssuerCountry(rs.getString("issuerCountry"));
							binRange.setGroupCode(rs.getString("groupCode"));
							binRange.setProductName(rs.getString("productName"));
							binRange.setRfu1(rs.getString("rfu1"));
							binRange.setRfu2(rs.getString("rfu2"));
							binRangList.add(binRange);
						}
					}
				}
			
		} catch (SQLException exception) {
			throw new SystemException(ErrorType.DATABASE_ERROR,
					ErrorType.DATABASE_ERROR.getResponseMessage());
		}
		return binRangList;
		}

	public BigInteger getBinRangTotal(String cardType, String mopType, User user) throws SystemException{
		BigInteger total = null;
		try (Connection connection = getConnection()) {
			try (PreparedStatement prepStmt = connection
					.prepareStatement("{call binRange_Records_count(?,?,?)}")) {
				prepStmt.setString(1, cardType);
				prepStmt.setString(2, mopType);
				prepStmt.setString(3, user.getUserType().toString());
				try (ResultSet rs = prepStmt.executeQuery()) {
					while (rs.next()) {
						total = rs.getBigDecimal(FieldType.COUNT.getName())
								.toBigInteger();
					}
				}
				}
				} catch (SQLException exception) {
					throw new SystemException(ErrorType.DATABASE_ERROR,
							ErrorType.DATABASE_ERROR.getResponseMessage());
				}
	     	return total;
	    	
		}
	
	
	
	

}


