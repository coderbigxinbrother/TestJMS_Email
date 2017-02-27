package testjms_email;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.yc.bean.Account;
import com.yc.biz.AccountBiz;
import com.yc.biz.impl.AccountBizImpl;

public class TestBiz {
    AccountBiz accountBiz;
    @Before
    public void setUp() throws Exception {
        accountBiz = new AccountBizImpl();
    }

    @Test
    public void testFind() {
        int accountid = 2;
        try {
            Assert.assertNotNull(accountBiz.finfAccount(accountid));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testDeposite() {
        int accountid = 2;
        try {
            Account account = accountBiz.deposite(accountid,100);
            System.out.println(account);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testWithdraw() {
        int accountid = 2;
        try {
            Account account = accountBiz.withdraw(accountid,100);
            System.out.println(account);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testTransfer() {
        try {
            Account account = accountBiz.transfer(2,3,100);
            System.out.println(account);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
