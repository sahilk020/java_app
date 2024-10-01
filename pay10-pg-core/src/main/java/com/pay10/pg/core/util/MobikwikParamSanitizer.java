package com.pay10.pg.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobikwikParamSanitizer {

	public MobikwikParamSanitizer() {
	}

	public static String sanitizeParam(String param) {
		if (param == null) {
			return null;
		}
		String ret = param.replaceAll("[>><>(){}?&* ~`!#$%^=+|\\:'\";,\\x5D\\x5B]+", " ");
		return ret;
	}

	public static String SanitizeURLParam(String url) {
		if (url == null) {
			return "";
		}

		// Pattern urlPattern =
		// Pattern.compile("^(http?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
		Pattern urlPattern = Pattern.compile("^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
		Matcher matcher = urlPattern.matcher(url);
		if (matcher.matches()) {
			return url;
		}
		return "";
	}
}
