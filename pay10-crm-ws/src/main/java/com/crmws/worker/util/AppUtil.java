package com.crmws.worker.util;

public class AppUtil {
	
	public static final String BASE_PATH = "/api/v1/worker";
	
	public static String generateSlug(String name) {
        return name.trim().replaceAll("\\s+", "-").replaceAll("[^\\w-]", "").replaceAll("--+", "-").toLowerCase();
    }


}
