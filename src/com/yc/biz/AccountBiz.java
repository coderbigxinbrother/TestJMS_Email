package com.yc.biz;

import com.yc.bean.Account;

public interface AccountBiz {
    public Account deposite(int accountid, double money) throws Exception;
    public Account withdraw(int accountid, double money) throws Exception;
    public Account transfer(int inAccountid,int outAccountid, double money) throws Exception;
    public Account finfAccount(int accountid) throws Exception;
}
