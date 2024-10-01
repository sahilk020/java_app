package com.pay10.commons.dao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class DefaultCacheProvider implements CacheProvider {

	Map<String, String> store = new ConcurrentHashMap<String, String>();

	public DefaultCacheProvider() {
	}

	@Override
	public void put(String key, String value) {
		store.put(key, value);
	}

	@Override
	public void putAll(Map<String, String> pairs) {
		store.putAll(pairs);
	}

	@Override
	public String get(String key) {
		return store.get(key);
	}

	@Override
	public Map<String, String> getAll() {
		return store;
	}

	@Override
	public void clear() {
		store.clear();
	}

	@Override
	public String remove(String key) {
		return store.remove(key);
	}
}
