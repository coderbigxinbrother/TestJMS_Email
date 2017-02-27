package com.yc.utils;

import org.apache.log4j.Logger;


public class YcUtils {
	
	
	
	/**
	 * 这是log4j的日志工具对象,logger对象中有   debug()   info()   error() 
	 * 通过这个方法就可以对不同级别的信息进行记录操作台
	 */
	public static Logger logger=Logger.getLogger(  YcUtils.class );
	
	private static StringBuffer transferException(   Exception e){
		StringBuffer sb=new StringBuffer();
		for( StackTraceElement ste:  e.getStackTrace()  ){
			sb.append(    ste.toString()+"\n");
		}
		return sb;
	}
	
	public static void error(  Exception e    ){
		logger.error(  transferException( e) );
	}
	
	public static void info(  Exception e    ){
		logger.info(  transferException( e) );
	}
	
	public static void info(  String  str    ){
		logger.info(  str );
	}
	
	public static void warn(  Exception e    ){
		logger.warn(  transferException( e) );
	}
	
}
