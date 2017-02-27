package com.yc.utils;

import java.util.regex.Pattern;

public class YcStringUtils{
	 /**
   		* 判断字符串是否null或者""
   		* @param src String
   		* @return boolean
   	*/
  	public static boolean isBlank(String src) {
    		if(null == src || "".equals(src.trim())) {
     			 return true;
    		}
    			return false;
  	}

	/**
   		* 把没有实例化的串(即null)转化为空串(即"")
   		* @param p_in String
   		* @return String
   	*/
  	public static String convertNull(String str) {
    		if(str == null) {
      			return "";
    		} else {
      			return str;
    		}
  	}

	/**
   		* 判断字符串是否由指定的字符组成判断, 例如: 参照字符串strRef可以是:"0123456789",   strIn为"372",则方法返回值为 true,   如果 strIn为"12a",则返回值为false。
   		* @param strIn String
   		* @param strRef String
   		* @return boolean
   	*/
  	public static boolean isSpecificString(String strIn, String strRef) {
    		if(strIn == null || strIn.length() == 0){
      			return false;
		}
    		for(int i = 0; i < strIn.length(); i++) {
      			String strTmp = strIn.substring(i, i + 1);
      			if(strRef.indexOf(strTmp, 0) == -1)
        			return false;
    			}
		
    			return true;
  	}

	  /**
   		* 返回字符串的前len个字符.例如:输入"abcdefg",3 返回 "abc".
   		* @param value String
   		* @param len int
   		* @return String
   	*/
  	public static String getLmtStr(String value, int len) {
    		if(value == null || value.length() <= len)
      			return value;
    		return value.substring(0, len);
  	}

	/**
	 * 功能描述：判断输入的字符串是否为纯汉字   [\u0391-\uFFE5]
	 * 
	 * @param str
	 *            传入的字符窜
	 * @return 如果是纯汉字返回true,否则返回false
	 */
	public static boolean isChinese(String str) {
		Pattern pattern = Pattern.compile("[\u0391-\uFFE5]+$");
		return pattern.matcher(str).matches();
	}

	/**
	 * 功能描述：人民币转成大写    "123"  =>   人民bi壹佰贰拾叁圆整        "123.3"   人民bi壹佰贰拾叁圆叁角
	 * @param str
	 *            数字字符串
	 * @return String 人民币转换成大写后的字符串
	 */
	public static String hangeToBig(String str) {
		double value;
		try {
			value = Double.parseDouble(str.trim());
		} catch (Exception e) {
			return null;
		}
		char[] hunit = { '拾', '佰', '仟' }; // 段内位置表示
		char[] vunit = { '万', '亿' }; // 段名表示
		char[] digit = { '零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖' }; // 数字表示
		long midVal = (long) (value * 100); // 转化成整形
		String valStr = String.valueOf(midVal); // 转化成字符串

		String head = valStr.substring(0, valStr.length() - 2); // 取整数部分
		String rail = valStr.substring(valStr.length() - 2); // 取小数部分

		String prefix = ""; // 整数部分转化的结果
		String suffix = ""; // 小数部分转化的结果
		// 处理小数点后面的数
		if (rail.equals("00")) { // 如果小数部分为0
			suffix = "整";
		} else {
			suffix = digit[rail.charAt(0) - '0'] + "角"
					+ digit[rail.charAt(1) - '0'] + "分"; // 否则把角分转化出来
		}
		// 处理小数点前面的数
		char[] chDig = head.toCharArray(); // 把整数部分转化成字符数组
		char zero = '0'; // 标志'0'表示出现过0
		byte zeroSerNum = 0; // 连续出现0的次数
		for (int i = 0; i < chDig.length; i++) { // 循环处理每个数字
			int idx = (chDig.length - i - 1) % 4; // 取段内位置
			int vidx = (chDig.length - i - 1) / 4; // 取段位置
			if (chDig[i] == '0') { // 如果当前字符是0
				zeroSerNum++; // 连续0次数递增
				if (zero == '0') { // 标志
					zero = digit[0];
				} else if (idx == 0 && vidx > 0 && zeroSerNum < 4) {
					prefix += vunit[vidx - 1];
					zero = '0';
				}
				continue;
			}
			zeroSerNum = 0; // 连续0次数清零
			if (zero != '0') { // 如果标志不为0,则加上,例如万,亿什么的
				prefix += zero;
				zero = '0';
			}
			prefix += digit[chDig[i] - '0']; // 转化该数字表示
			if (idx > 0)
				prefix += hunit[idx - 1];
			if (idx == 0 && vidx > 0) {
				prefix += vunit[vidx - 1]; // 段结束位置应该加上段名如万,亿
			}
		}

		if (prefix.length() > 0)
			prefix += '圆'; // 如果整数部分存在,则有圆的字样
		return prefix + suffix; // 返回正确表示
	}


}
