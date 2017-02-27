package com.yc.web.listeners;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.yc.dao.EmailProperties;
import com.yc.threadpool.ThreadPoolManager;

/**
 * Application Lifecycle Listener implementation class EmailPropertyListener
 *
 */
@WebListener
public class EmailPropertyListener implements ServletContextListener {

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent enent)  { 
         try {
             String emailName = EmailProperties.getInstance().getProperty("emailName");
             String emailPassword = EmailProperties.getInstance().getProperty("emailPassword");
             
             ServletContext application = enent.getServletContext();
             application.setAttribute("emailName", emailName);
             application.setAttribute("emailPassword", emailPassword);
             
             //创建线程池管理类
             ThreadPoolManager manager = ThreadPoolManager.getInstance(40);
             application.setAttribute("ThreadPoolManager", manager);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
    }
	
}
