package com.yc.biz.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import com.yc.bean.Account;
import com.yc.biz.AccountBiz;
import com.yc.dao.DBHelper;

public class AccountBizImpl implements AccountBiz {
    private DBHelper dbHelper = new DBHelper();
    
    @Override
    public Account finfAccount(int accountid) throws Exception {
        String sql = "select * from accounts where accountid = ?";
        List<Object> params = new ArrayList<Object>();
        params.add(accountid);
        Account account= dbHelper.findSingleObject(Account.class, sql, params);
        return account;
    }

    @Override  //存款  事物
    public Account deposite(int accountid, double money) throws Exception {
        Account account = finfAccount(accountid);
        if(account == null){
            throw new RuntimeException("查无此账户：" + accountid);
        }
        account.setBalance(account.getBalance() + money);
        String sql1 = "update accounts set balance = ? where accountid = ?";
        List<Object> params1 = new ArrayList<Object>();
        params1.add(account.getBalance());
        params1.add(account.getAccountid());
        String sql2 = "insert into oprecord values(seq_oprecord.nextval, ?,?,sysdate)";
        List<Object> params2 = new ArrayList<Object>();
        params2.add(account.getAccountid());
        params2.add(money);
        
        List<String> sqls = new ArrayList<String>();
        sqls.add(sql1);
        sqls.add(sql2);
        List<List<Object>> paramss = new ArrayList<List<Object>>();
        paramss.add(params1);
        paramss.add(params2);
        dbHelper.doUpdate(sqls, paramss);
        return account;
    }

    @Override
    public Account withdraw(int accountid, double money) throws Exception {
        Account account = finfAccount(accountid);
        if(account == null){
            throw new RuntimeException("查无此账户：" + accountid);
        }
        if(account.getBalance() < money){
            throw new RuntimeException("用户余额不足，当前余额："+account.getBalance()+" 不能转账。");
        }
        account.setBalance(account.getBalance() - money);
        String sql1 = "update accounts set balance = ? where accountid = ?";
        List<Object> params1 = new ArrayList<Object>();
        params1.add(account.getBalance());
        params1.add(account.getAccountid());
        String sql2 = "insert into oprecord values(seq_oprecord.nextval, ?,?,sysdate)";
        List<Object> params2 = new ArrayList<Object>();
        params2.add(account.getAccountid());
        params2.add(-money);
        
        List<String> sqls = new ArrayList<String>();
        sqls.add(sql1);
        sqls.add(sql2);
        List<List<Object>> paramss = new ArrayList<List<Object>>();
        paramss.add(params1);
        paramss.add(params2);
        dbHelper.doUpdate(sqls, paramss);
        return account;
    }

    @Override
    public Account transfer(int inAccountid, int outAccountid, double money) throws Exception {
        Account outAccount = finfAccount(outAccountid);
        if(outAccount == null){
            throw new RuntimeException("查无此账户：" + outAccountid +"不能转账");
        }
        if(outAccount.getBalance() < money){
            throw new RuntimeException("用户余额不足，当前余额："+outAccount.getBalance()+" 不能转账。");
        }
        
        Account inAccount = finfAccount(inAccountid);
        if(inAccount == null){
            throw new RuntimeException("查无此账户：" + inAccountid+"不能转账");
        }

        outAccount.setBalance(outAccount.getBalance() - money);
        String sql1 = "update accounts set balance = ? where accountid = ?";
        List<Object> params1 = new ArrayList<Object>();
        params1.add(outAccount.getBalance());
        params1.add(outAccount.getAccountid());
        String sql2 = "insert into oprecord values(seq_oprecord.nextval, ?,?,sysdate)";
        List<Object> params2 = new ArrayList<Object>();
        params2.add(outAccount.getAccountid());
        params2.add(-money);
        
        inAccount.setBalance(inAccount.getBalance() + money);
        String sql3 = "update accounts set balance = ? where accountid = ?";
        List<Object> params3 = new ArrayList<Object>();
        params3.add(inAccount.getBalance());
        params3.add(inAccount.getAccountid());
        String sql4 = "insert into oprecord values(seq_oprecord.nextval, ?,?,sysdate)";
        List<Object> params4 = new ArrayList<Object>();
        params4.add(inAccount.getAccountid());
        params4.add(money);
        
        List<String> sqls = new ArrayList<String>();
        sqls.add(sql1);
        sqls.add(sql2);
        sqls.add(sql3);
        sqls.add(sql4);
        List<List<Object>> paramss = new ArrayList<List<Object>>();
        paramss.add(params1);
        paramss.add(params2);
        paramss.add(params3);
        paramss.add(params4);
        dbHelper.doUpdate(sqls, paramss);
        return outAccount;
    }

}
