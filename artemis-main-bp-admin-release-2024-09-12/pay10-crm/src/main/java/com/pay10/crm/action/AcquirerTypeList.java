package com.pay10.crm.action;

import com.pay10.commons.util.AcquirerTypeUI;

import java.util.Arrays;
import java.util.List;

public class AcquirerTypeList extends AbstractSecureAction {
    private List<AcquirerTypeUI> acquirerTypes;

    @Override
    public String execute() {
        acquirerTypes = Arrays.asList(AcquirerTypeUI.values());
        return SUCCESS;
    }

    public List<AcquirerTypeUI> getAcquirerTypes() {
        return acquirerTypes;
    }
}
