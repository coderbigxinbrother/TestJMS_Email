package com.yc.dao;     //  dao:  database access object   数据库访问对象


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//这样就个自定义的MyProperties类就有Properties功能
//再设计成单例:  
//  MyProperties.getInstance().getProperty( "键");
public class EmailProperties extends Properties {
	private static EmailProperties emailProperties;
	
	private EmailProperties() throws IOException{
		//在这里完成读取email.properties文件
			InputStream iis=EmailProperties.class.getClassLoader().getResourceAsStream( "email.properties");
			this.load( iis );
	}
	
	public static EmailProperties getInstance() throws IOException{
		if( emailProperties==null){
		    emailProperties=new EmailProperties();
		}
		return emailProperties;
	}
}
