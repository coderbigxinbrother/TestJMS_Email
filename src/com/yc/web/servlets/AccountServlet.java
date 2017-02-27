package com.yc.web.servlets;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yc.bean.Account;
import com.yc.bean.EmailInfo;
import com.yc.bean.JsonModel;
import com.yc.bean.OpType;
import com.yc.biz.AccountBiz;
import com.yc.biz.impl.AccountBizImpl;
import com.yc.threadpool.SendEmailTask;
import com.yc.threadpool.ThreadPoolManager;
import com.yc.web.model.AccountModel;

/**
 * Servlet implementation class AccountServlet
 */
@WebServlet("/account")
public class AccountServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
    private AccountModel accountModel;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		accountModel = super.getReqParamObj(request, AccountModel.class);
		//System.out.println(accountModel);
	
		if(op != null && !"".equals(op)){
		    if("deposite".equals(op)){
		        depositeOp(request, response, accountModel);
		    }else if("withdraw".equals(op)){
		        withdrawOp(request, response, accountModel);
            }else if("transfer".equals(op)){
                transferOp(request, response, accountModel);
            }
		}
	}

    private void transferOp(HttpServletRequest request, HttpServletResponse response, AccountModel accountModel) {
        AccountBiz accountBiz = new AccountBizImpl();
        JsonModel jm = new JsonModel();
        Account account = null;
        try {
            account = accountBiz.transfer(accountModel.getInAccountId(), accountModel.getAccountId(), accountModel.getMoney());
            jm.setCode(1);
            jm.setObj(account);
        } catch (Exception e) {
            jm.setCode(0);
            jm.setMsg(e.getMessage());
        }
        
        //从线程池中取一个线程，用于发送邮件
        ThreadPoolManager threadPoolManager =(ThreadPoolManager) request.getSession().getServletContext().getAttribute("ThreadPoolManager");
        //创建一个信息
        EmailInfo emailInfo = new EmailInfo();
        emailInfo.setAccount(account);
        emailInfo.setMoney(accountModel.getMoney());
        emailInfo.setOp(OpType.transfer);
        
        //从application 中取出邮件服务的用户名和密码
        String emailName = (String) request.getSession().getServletContext().getAttribute("emailName");
        String emailPassword = (String) request.getSession().getServletContext().getAttribute("emailPassword");
        //通过
        threadPoolManager.process(new SendEmailTask(emailInfo,emailName, emailPassword));
        
        try {
            super.responseOut(response, jm);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void withdrawOp(HttpServletRequest request, HttpServletResponse response, AccountModel accountModel) {
        AccountBiz accountBiz = new AccountBizImpl();
        JsonModel jm = new JsonModel();
        Account account = null;
        try {
            account = accountBiz.withdraw(accountModel.getAccountId(), accountModel.getMoney());
            jm.setCode(1);
            jm.setObj(account);
        } catch (Exception e) {
            jm.setCode(0);
            jm.setMsg(e.getMessage());
        }
        
      //从线程池中取一个线程，用于发送邮件
        ThreadPoolManager threadPoolManager =(ThreadPoolManager) request.getSession().getServletContext().getAttribute("ThreadPoolManager");
        //创建一个信息
        EmailInfo emailInfo = new EmailInfo();
        emailInfo.setAccount(account);
        emailInfo.setMoney(accountModel.getMoney());
        emailInfo.setOp(OpType.withdraw);
        
        //从application 中取出邮件服务的用户名和密码
        String emailName = (String) request.getSession().getServletContext().getAttribute("emailName");
        String emailPassword = (String) request.getSession().getServletContext().getAttribute("emailPassword");
        
        threadPoolManager.process(new SendEmailTask(emailInfo,emailName, emailPassword));
        
        try {
            super.responseOut(response, jm);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void depositeOp(HttpServletRequest request, HttpServletResponse response, AccountModel accountModel) {
        AccountBiz accountBiz = new AccountBizImpl();
        JsonModel jm = new JsonModel();
        Account account = null;
        try {
            account = accountBiz.deposite(accountModel.getAccountId(), accountModel.getMoney());
            jm.setCode(1);
            jm.setObj(account);
        } catch (Exception e) {
            jm.setCode(0);
            jm.setMsg(e.getMessage());
        }
        //从线程池中取一个线程，用于发送邮件
        ThreadPoolManager threadPoolManager =(ThreadPoolManager) request.getSession().getServletContext().getAttribute("ThreadPoolManager");
        //创建一个信息
        EmailInfo emailInfo = new EmailInfo();
        emailInfo.setAccount(account);
        emailInfo.setMoney(accountModel.getMoney());
        emailInfo.setOp(OpType.deposite);
        
        //从application 中取出邮件服务的用户名和密码
        String emailName = (String) request.getSession().getServletContext().getAttribute("emailName");
        String emailPassword = (String) request.getSession().getServletContext().getAttribute("emailPassword");
        
        threadPoolManager.process(new SendEmailTask(emailInfo,emailName, emailPassword));
        
        try {
            super.responseOut(response, jm);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
