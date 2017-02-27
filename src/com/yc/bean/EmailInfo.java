package com.yc.bean;

import java.io.Serializable;

public class EmailInfo implements Serializable {
    private static final long serialVersionUID = 4966315533970042016L;
    private Account account;
    private Account inAccount;
    private String op;
    private double money;
    public EmailInfo() {
        super();
        // TODO 自动生成的构造函数存根
    }
    public EmailInfo(Account account, Account inAccount, String op, double money) {
        super();
        this.account = account;
        this.inAccount = inAccount;
        this.op = op;
        this.money = money;
    }
    public EmailInfo(Account account, String op, double money) {
        super();
        this.account = account;
        this.op = op;
        this.money = money;
    }
    public Account getAccount() {
        return account;
    }
    public void setAccount(Account account) {
        this.account = account;
    }
    public Account getInAccount() {
        return inAccount;
    }
    public void setInAccount(Account inAccount) {
        this.inAccount = inAccount;
    }
    public String getOp() {
        return op;
    }
    public void setOp(String op) {
        this.op = op;
    }
    public double getMoney() {
        return money;
    }
    public void setMoney(double money) {
        this.money = money;
    }
}
