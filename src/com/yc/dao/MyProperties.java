package com.yc.dao;     //  dao:  database access object   数据库访问对象


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//这样就个自定义的MyProperties类就有Properties功能
//再设计成单例:  
//  MyProperties.getInstance().getProperty( "键");
public class MyProperties extends Properties {
	private static MyProperties myProperties;
	
	private MyProperties() throws IOException{
		//在这里完成读取db.properties文件
			InputStream iis=MyProperties.class.getClassLoader().getResourceAsStream( "jdbc.properties");
			this.load(   iis );
	}
	
	public static MyProperties getInstance() throws IOException{
		if( myProperties==null){
			 myProperties=new MyProperties();
		}
		return myProperties;
	}
}
