package com.yc.threadpool;

import javax.mail.MessagingException;

import com.yc.bean.EmailInfo;
import com.yc.utils.SendEmail;

public class SendEmailTask implements Runnable {
    private EmailInfo emailInfo;
    private String emailName;
    private String emailPassword;
    
    public SendEmailTask(EmailInfo emailInfo) {
        this.emailInfo = emailInfo;
    }
    
    public SendEmailTask(EmailInfo emailInfo, String emailName, String emailPassword) {
        this.emailInfo = emailInfo;
        this.emailName = emailName;
        this.emailPassword = emailPassword;
    }
    
    @Override
    public void run() {
        try {
            SendEmail.sendHttpEmail(emailInfo.getAccount().getEmail(), emailInfo, this.emailName, this.emailPassword);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
