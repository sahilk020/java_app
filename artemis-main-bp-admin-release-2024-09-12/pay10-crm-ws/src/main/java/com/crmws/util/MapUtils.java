package com.crmws.util;

import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class MapUtils {
    private static final Logger LOGGER = Logger.getLogger(MapUtils.class.getName());

    @SafeVarargs
    public static <K, V> Map<K, V> of(Object... keyValuePairs) {
        if (keyValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("Number of arguments must be even");
        }

        Map<K, V> map = new HashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            K key = (K) keyValuePairs[i];
            V value = (V) keyValuePairs[i + 1];
            map.put(key, value);
        }

        return map;
    }

    public static class MapBuilder<K, V> {
        private final Map<K, V> map;

        private MapBuilder() {
            this.map = new HashMap<>();
        }

        public static <K, V> MapBuilder<K, V> builder() {
            return new MapBuilder<K, V>();
        }

        public MapBuilder<K, V> put(K key, V value) {
            map.put(key, value);
            return this;
        }

        public Map<K, V> build() {
            return map;
        }

        @Override
        public String toString() {
            return new GsonBuilder().disableHtmlEscaping().create().toJson(this);
        }
    }


    public static void main(String[] args) {
        Map<String, Object> apiResponse = MapUtils.<String, Object>of(
                "extTransactionId", "122223",
                "payeeName", "Akash",
                "payeeAcNum", "",
                "payeeIfsc", "",
                "payeeAmount", "12.00",
                "senderEmail", "akash@gmail.com",
                "payeeMobile", "919311549210",
                "paymentMethod", "NEFT",
                "remarks", "Payout via NEFT",
                "name", "Pritam Ray"
        );
        System.out.println(apiResponse);

        Map<String, Object> map = MapUtils.<String, Object>of("1", "GFG", "2", "Geek", "3", "GeeksForGeeks", "4", "G", "5", "e", "6", "e", "7", "k", "8", "s", "9", "f", "10", 1, "key", 1);

        System.out.println(map);

        Map<String, Object> builtMap = MapBuilder.<String, Object>builder()
                .put("name", "Pritam Ray")
                .put("city", "Ropar")
                .put("email", "ray@gmail.com")
                .build();

        builtMap.forEach((k, v) -> System.out.println(k + "-" + v));

        System.out.println(builtMap);

    }
}
