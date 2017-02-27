package com.yc.bean;

import java.io.Serializable;

public class Account implements Serializable {
    private static final long serialVersionUID = 4966315533970042016L;
    private Integer accountid;
    private Double balance;
    private String email;
    public Account() {
        super();
        // TODO 自动生成的构造函数存根
    }
    public Account(Integer accountid, Double balance, String email) {
        super();
        this.accountid = accountid;
        this.balance = balance;
        this.email = email;
    }
    public Integer getAccountid() {
        return accountid;
    }
    public void setAccountid(Integer accountid) {
        this.accountid = accountid;
    }
    public Double getBalance() {
        return balance;
    }
    public void setBalance(Double balance) {
        this.balance = balance;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    @Override
    public String toString() {
        return "Account [accountid=" + accountid + ", balance=" + balance + ", email=" + email + "]";
    }
}
