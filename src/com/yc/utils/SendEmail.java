package com.yc.utils;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.yc.bean.EmailInfo;
import com.yc.bean.OpType;

public class SendEmail {
    public static void sendHttpEmail(String receiver,EmailInfo emailInfo, String sender, String pwd) throws MessagingException{
        //设置属性集
        Properties props = new Properties();
        props.setProperty("mail.debug", "");
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.transport.protocol", "smtp");
        //创建会话
        Session session = Session.getInstance(props);
        session.setDebug(true);
        //创建消息
        Message message = new MimeMessage(session);
        //邮件体
        MimeMultipart mmt = new MimeMultipart();
        //创建一个具体的邮件体
        MimeBodyPart bodyPart = new MimeBodyPart();
        //拼接信息
        StringBuffer email = new StringBuffer("用户" + emailInfo.getAccount().getAccountid() + ":\r\n");
        String stringop = "";
        if(emailInfo.getOp() == OpType.deposite){
            stringop = "存入";
        }else if(emailInfo.getOp() == OpType.withdraw){
            stringop = "取出";
        }else {
            stringop = "转出";
        }
        email.append("您"+stringop+":" + emailInfo.getMoney() +", 当前余额是：" + emailInfo.getAccount().getBalance());
        bodyPart.setContent(email.toString(), "text/html;charset=utf-8");
        //给邮件体添加邮件
        mmt.addBodyPart(bodyPart);
        
        message.setContent(mmt);
        message.setSubject("账户操作");
        message.setFrom(new InternetAddress(sender));
        
        //设置传输层
        Transport transport = session.getTransport();
        transport.connect("smtp.163.com", 25, sender, pwd);
        transport.sendMessage(message, new Address[]{
                new InternetAddress(receiver)
        });
        transport.close();
    }
}
