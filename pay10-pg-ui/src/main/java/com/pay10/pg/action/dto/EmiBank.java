package com.pay10.pg.action.dto;

import java.util.List;

public class EmiBank {
    private String bankName;
    private List<TenureDetails> emis;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public List<TenureDetails> getEmis() {
        return emis;
    }

    public void setEmis(List<TenureDetails> emis) {
        this.emis = emis;
    }
}
