
package com.pay10.commons.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionManager {

	public static final String DATE_FORMAT = "MMddHHmmss";
	public static final String UPI_DATE_FORMAT = "ddHHmmss";
	public static final int MIN_TRANSACTION_ID = 100;
	public static final int MAX_TRANSACTION_ID = 999;

	public static final TransactionIdGenerator transactionIdGenerator = new TransactionIdGenerator(MIN_TRANSACTION_ID,
			MAX_TRANSACTION_ID);
	public static String serverId = "";

	public TransactionManager() {
	}

	private static Logger logger = LoggerFactory.getLogger(TransactionManager.class.getName());

	public static String getNewTransactionId() {

		if (serverId.equals("")) {

			InetAddress ip;
			try {

				ip = InetAddress.getLocalHost();
				String cleanIp = ip.getHostAddress().replace(".", "");
				serverId = cleanIp.substring(cleanIp.length() - 2, cleanIp.length());

			} catch (UnknownHostException e) {

				logger.error("Exception in TransactionManager", e);

			}

		}

		final LocalDateTime currentTime = LocalDateTime.now();
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
		final StringBuilder transactionId = new StringBuilder();
		transactionId.append(transactionIdGenerator.next());
		transactionId.append(serverId);
		transactionId.append((Year.now().toString()).substring(3));
		transactionId.append(currentTime.format(formatter));
		return transactionId.toString();
	}

	public static String getId() {

		final LocalDateTime currentTime = LocalDateTime.now();
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(UPI_DATE_FORMAT);
		final StringBuilder transactionId = new StringBuilder();
		transactionId.append(transactionIdGenerator.next());
		// transactionId.append(serverId);
		transactionId.append((Year.now().toString()).substring(3));
		transactionId.append(currentTime.format(formatter));
		return transactionId.toString();
	}

	/*public static void main(String[] args) {
		IntStream.range(0,300).forEach(x->System.out.println(getTimestampId()));
		;
	}
*/
	public synchronized static String getTimestampId() {

		final Instant currentTime = Instant.now();
		final StringBuilder transactionId = new StringBuilder();
		transactionId.append(transactionIdGenerator.atomicNext());
		//transactionId.append((Year.now().toString()).substring(3));
		//transactionId.append(new Date().getTime());
		transactionId.append(System.nanoTime());
		return transactionId.toString();
	}
}
