package com.pay10.commons.dao;

public class CacheProviderFactory {

	public CacheProviderFactory() {
	}

	public static CacheProvider getCacheProvider() {
		return new DefaultCacheProvider();
	}

}
