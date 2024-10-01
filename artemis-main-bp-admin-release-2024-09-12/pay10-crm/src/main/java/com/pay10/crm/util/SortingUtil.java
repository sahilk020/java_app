package com.pay10.crm.util;

import com.pay10.commons.user.Merchants;

import java.util.*;
import java.util.stream.Collectors;

public class SortingUtil {

    public static void sortMerchantNames(List<Merchants> merchantList) {
        merchantList.sort((m1, m2) -> {
            boolean isS1Digit = Character.isDigit(m1.getBusinessName().charAt(0));
            boolean isS2Digit = Character.isDigit(m2.getBusinessName().charAt(0));
            if (isS1Digit && !isS2Digit) {
                return 1;
            } else if (!isS1Digit && isS2Digit) {
                return -1;
            } else {
                return m1.getBusinessName().compareTo(m2.getBusinessName());
            }
        });

    }

    public static Map<String, String> sortMerchantNames(Map<String, String> merchantList) {
        return merchantList.entrySet().stream().sorted(Map.Entry.comparingByValue((name1, name2) -> {
            boolean isS1Digit = !name1.isEmpty() && Character.isDigit(name1.charAt(0));
            boolean isS2Digit = !name2.isEmpty() && Character.isDigit(name2.charAt(0));
            if (isS1Digit && !isS2Digit) {
                return 1;
            } else if (!isS1Digit && isS2Digit) {
                return -1;
            } else {
                return name1.compareTo(name2);
            }
        })).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

}



