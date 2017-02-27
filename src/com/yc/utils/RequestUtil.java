package com.yc.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class RequestUtil {

	// 参数名 setXXXX 值
	public <T> T parseRequest(HttpServletRequest request, Class<T> c)
			throws UnsupportedEncodingException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		request.setCharacterEncoding("utf-8");
		T t = c.newInstance();
		Map<String, RequestBean> parametermap = parseRequest(request); // 从request取出所有的参数信息，包装成
		// Map
		List<Method> setMethods = getAllSetMethod(c); // 取出所有的set方法
		for (Method m : setMethods) {
			String setmethodname = m.getName();
			RequestBean rb = parametermap.get(setmethodname);
			String methodparametertype = m.getParameterTypes()[0].getName();
			if (rb == null) {
				continue;
			}
			String ss = ((String[]) rb.getObj())[0];
			if ("int".equals(methodparametertype)
					|| "java.lang.Integer".equals(methodparametertype)) {
				m.invoke(t, Integer.parseInt(ss));
			} else if ("double".equals(methodparametertype)
					|| "java.lang.Double".equals(methodparametertype)) {
				m.invoke(t, Double.parseDouble(ss));
			} else if ("float".equals(methodparametertype)
					|| "java.lang.Float".equals(methodparametertype)) {
				m.invoke(t, Float.parseFloat(ss));
			} else if ("long".equals(methodparametertype)
					|| "java.lang.Long".equals(methodparametertype)) {
				m.invoke(t, Long.parseLong(ss));
			} else if ("java.lang.String".equals(methodparametertype)) {
				m.invoke(t, ss);
			} else {
				m.invoke(t, rb.getObj());
			}

		}

		return t;
	}

	private <T> List<Method> getAllSetMethod(Class<T> c) {
		List<Method> list = new ArrayList<Method>();
		Method[] ms = c.getMethods();
		for (Method m : ms) {
			if (m.getName().startsWith("set")) {
				list.add(m);
			}
		}
		return list;
	}

	private Map<String, RequestBean> parseRequest(HttpServletRequest request) {
		Enumeration<String> enu = request.getParameterNames();
		Map<String, RequestBean> map = new HashMap<String, RequestBean>();
		while (enu.hasMoreElements()) {
			String pname = enu.nextElement();
			RequestBean rb = new RequestBean();
			rb.setParameterName(pname); // 参数名
			rb.setMethodName(getSetMethodName(pname)); // set方法名
			rb.setObj(request.getParameterValues(pname));
			map.put(getSetMethodName(pname), rb);
		}
		return map;
	}

	private String getSetMethodName(String parameterName) {
		return "set" + parameterName.substring(0, 1).toUpperCase()
				+ parameterName.substring(1);
	}

	class RequestBean {
		private String parameterName;
		private String methodName;
		private Object obj;

		public String getParameterName() {
			return parameterName;
		}

		public void setParameterName(String parameterName) {
			this.parameterName = parameterName;
		}

		public String getMethodName() {
			return methodName;
		}

		public void setMethodName(String methodName) {
			this.methodName = methodName;
		}

		public Object getObj() {
			return obj;
		}

		public void setObj(Object obj) {
			this.obj = obj;
		}

	}

}
