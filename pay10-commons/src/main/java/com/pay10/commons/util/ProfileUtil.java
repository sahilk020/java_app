package com.pay10.commons.util;

public class ProfileUtil {

    private static final String ENV = PropertiesManager.propertiesMap.get("Environment");

    public static Profiles getActiveProfile()
    {
        return Profiles.valueOf(ENV);
    }

    public static boolean isAlertEnabledForActiveProfile()
    {
        return Profiles.valueOf(ENV).getAlerts();
    }


}
