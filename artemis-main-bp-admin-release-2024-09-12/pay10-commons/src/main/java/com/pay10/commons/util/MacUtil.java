package com.pay10.commons.util;

import java.net.InetAddress;
import java.net.NetworkInterface;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Component which is provide the mack details by ip.
 * 
 * @author Gajera Jay
 *
 */
@Service
public class MacUtil {

	/**
	 * This method provide mack(physical) address for specified IP.
	 * 
	 * @param ip ip address for which need to retrieve mack.
	 * @return {@link String} of mack.
	 */
	public String getMackByIp(String ip) {
		if (StringUtils.isBlank(ip)) {
			throw new IllegalArgumentException("Please provide the ip address");
		}
		try {
			InetAddress address = InetAddress.getByName(ip);
			NetworkInterface face = NetworkInterface.getByInetAddress(address);
			StringBuilder sb = new StringBuilder(18);
			for (byte b : face.getHardwareAddress()) {
				if (sb.length() > 0)
					sb.append(':');
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (Exception ex) {
			return null;
		}

	}

}
