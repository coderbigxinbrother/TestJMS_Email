package com.yc.web.model;

import java.io.Serializable;

public class AccountModel implements Serializable {
    private static final long serialVersionUID = 6921741325500437658L;
    private Integer accountId;
    private double money;
    private Integer inAccountId;
    public AccountModel() {
        super();
    }
    public AccountModel(Integer accountId, double money, Integer inAccountId) {
        super();
        this.accountId = accountId;
        this.money = money;
        this.inAccountId = inAccountId;
    }
    public Integer getAccountId() {
        return accountId;
    }
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
    public double getMoney() {
        return money;
    }
    public void setMoney(double money) {
        this.money = money;
    }
    public Integer getInAccountId() {
        return inAccountId;
    }
    public void setInAccountId(Integer inAccountId) {
        this.inAccountId = inAccountId;
    }
}
