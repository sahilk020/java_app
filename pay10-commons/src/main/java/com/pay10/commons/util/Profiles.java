package com.pay10.commons.util;

public enum Profiles {

    PROD("Production",true),
    PRE_PROD("Pre-Production",true),
    UAT("Uat",false);

    String name;

    boolean alerts;

    Profiles(String name,boolean alerts) {
        this.name = name;
        this.alerts =alerts;
    }

    public String getName() {
        return name;
    }

    public boolean getAlerts() {
        return alerts;
    }


}
