package com.yc.bean;

import java.io.Serializable;

public class Oprecord implements Serializable {
    private static final long serialVersionUID = 4634075368407935576L;
    private Integer id;
    private Integer accountid;
    private Double opmoney;
    private String opTime;
    public Oprecord() {
        super();
        // TODO 自动生成的构造函数存根
    }
    public Oprecord(Integer id, Integer accountid, Double opmoney, String opTime) {
        super();
        this.id = id;
        this.accountid = accountid;
        this.opmoney = opmoney;
        this.opTime = opTime;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getAccountid() {
        return accountid;
    }
    public void setAccountid(Integer accountid) {
        this.accountid = accountid;
    }
    public Double getOpmoney() {
        return opmoney;
    }
    public void setOpmoney(Double opmoney) {
        this.opmoney = opmoney;
    }
    public String getOpTime() {
        return opTime;
    }
    public void setOpTime(String opTime) {
        this.opTime = opTime;
    }
    @Override
    public String toString() {
        return "Oprecord [id=" + id + ", accountid=" + accountid + ", opmoney=" + opmoney + ", opTime=" + opTime + "]";
    }
}
