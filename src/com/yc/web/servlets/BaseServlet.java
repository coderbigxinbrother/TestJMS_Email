package com.yc.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * 适配器模式 基础操作
 * 
 * @author wx
 *
 */
@WebServlet("/BaseServlet")
public abstract class BaseServlet extends HttpServlet {

    private static final long serialVersionUID = -6283150993632897449L;
    protected String op;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    // 输出JSON的字符串
    protected static void responseOut(HttpServletResponse response, Object obj) throws IOException {
        String jsonStr = toJson(obj);
        PrintWriter out = response.getWriter();
        out.println(jsonStr);
        out.flush();
        out.close();
    }

    // 获取文件名和
    // http://localhost:8080/ordering/login.jsp?usrname=a&pwd=a
    protected static String getRequestFlag(HttpServletRequest request) {
        String requestURI = request.getRequestURI(); // uri
                                                     // ordering/login.jsp?usrname=a&pwd=a
        int start = requestURI.lastIndexOf("/") + 1; // 找到login的起始位置
        return requestURI.substring(start); // login.jsp?usrname=a&pwd=a
    }

    // 把对象转为JSON的字符串
    protected static String toJson(Object obj) {
        Gson g = new Gson();
        return g.toJson(obj);
    }

    /**
     * 
     * @param req
     *            请求处理对象
     * @param class1
     *            要转换成的实体类对象
     * @return 返回实体类对象
     */
    protected <T> T getReqParamObj(HttpServletRequest req, Class<T> class1) {
        T t = null;
        // System.out.println(req.getAttribute("sectionid"));
        // username [a]
        // passward [a]
        // 将request中的数据封装成一个Map<String, String[]>
        Map<String, String[]> map = req.getParameterMap();
        op = map.get("op")[0];
        //System.out.println("OP ： " + op);
        map = pareseRequest(map); // 在Map<属性名, 属性值[]> 的key字段拼接set构造set方法得到
        //System.out.println(map);                          // Map<set方法, 属性值[]>
        try {
            t = class1.newInstance(); // 取到指定类的对象
            Method[] ms = class1.getMethods(); // 取到指定类对象的类的方法
            for (Method m : ms) { // 遍历所有的方法
                String mathodName = m.getName(); // 取到每个方法的名字 （setXXX getXXX）
                for (Map.Entry<String, String[]> entry : map.entrySet()) {// 遍历Map<set方法,
                                                                          // 属性值[]>
                    String key = entry.getKey(); // 取得set方法名
                    String[] values = entry.getValue(); // 取到所有参数的值
                    if (values != null && values.length > 0) {
                        if (key.equals(mathodName)) { // 匹配是否是set方法
                            // 取得此set方法的参数类型
                            Class parameterType = m.getParameterTypes()[0];
                            if (parameterType.getName().startsWith("[")) {// 判断参数是否是数组
                                // 以[开头的是数组
                                extractArrayParameterValue(t, m, values, parameterType);
                            } else {
                                // 没有【开头的是单个的值
                                extractParameterValue(t, m, values, parameterType);
                            }
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return t;
    }

    /**
     * 取单个参数值
     * 
     * @param t
     * @param m
     * @param values
     * @param parameterType
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ParseException
     */
    private <T> void extractParameterValue(T t, Method m, String[] values, Class parameterType) throws IllegalAccessException, InvocationTargetException,
            ParseException {
        String s = values[0]; // 取到参数数组的第一个值
        if(s == null || "".equals(s)){
            return;
        }
        String parameterTypeName = parameterType.getName(); // 取到参数的类型
        if ("int".equalsIgnoreCase(parameterTypeName) || "java.lang.Integer".equalsIgnoreCase(parameterTypeName)) {
            m.invoke(t, Integer.parseInt(s));
        } else if ("float".equalsIgnoreCase(parameterTypeName) || "java.lang.Float".equalsIgnoreCase(parameterTypeName)) {
            m.invoke(t, Float.parseFloat(s));
        } else if ("double".equalsIgnoreCase(parameterTypeName) || "java.lang.Double".equalsIgnoreCase(parameterTypeName)) {
            m.invoke(t, Double.parseDouble(s));
        } else if ("byte".equalsIgnoreCase(parameterTypeName) || "java.lang.Byte".equalsIgnoreCase(parameterTypeName)) {
            m.invoke(t, Byte.parseByte(s));
        } else if ("java.util.Date".equalsIgnoreCase(parameterTypeName)) {// Date类型转换
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            m.invoke(t, sdf.parse(s));
        } else {
            m.invoke(t, s);
        }
    }

    /**
     * 取数组类型的参数
     * 
     * @param t
     * @param m
     * @param values
     * @param parameterType
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ParseException
     */
    private <T> void extractArrayParameterValue(T t, Method m, String[] values, Class parameterType) throws IllegalAccessException, InvocationTargetException,
            ParseException {
        // String s = values[0];
        String parameterTypeName = parameterType.getName();
        if ("[I".equalsIgnoreCase(parameterTypeName)) {
            int[] intarray = new int[values.length];
            for (int i = 0; i < values.length; i++) {
                intarray[i] = Integer.parseInt(values[i]);
            }
            m.invoke(t, intarray);
        } else if ("[F".equalsIgnoreCase(parameterTypeName)) {
            Float[] farray = new Float[values.length];
            for (int i = 0; i < values.length; i++) {
                farray[i] = Float.parseFloat(values[i]);
            }
            m.invoke(t, farray);
        } else if ("[D".equalsIgnoreCase(parameterTypeName)) {
            Double[] darray = new Double[values.length];
            for (int i = 0; i < values.length; i++) {
                darray[i] = Double.parseDouble(values[i]);
            }
            m.invoke(t, darray);
        } else if ("[B".equalsIgnoreCase(parameterTypeName)) {
            Byte[] barray = new Byte[values.length];
            for (int i = 0; i < values.length; i++) {
                barray[i] = Byte.parseByte(values[i]);
            }
            m.invoke(t, barray);
        } else if ("[Ljava.lang.Integer;".equalsIgnoreCase(parameterTypeName)) {
            int[] intarray = new int[values.length];
            for (int i = 0; i < values.length; i++) {
                intarray[i] = Integer.parseInt(values[i]);
            }
            m.invoke(t, (Object) intarray);
        } else if ("[Ljava.lang.Float;".equalsIgnoreCase(parameterTypeName)) {
            Float[] farray = new Float[values.length];
            for (int i = 0; i < values.length; i++) {
                farray[i] = Float.parseFloat(values[i]);
            }
            m.invoke(t, (Object) farray);
        } else if ("[Ljava.lang.Double;".equalsIgnoreCase(parameterTypeName)) {
            Double[] darray = new Double[values.length];
            for (int i = 0; i < values.length; i++) {
                darray[i] = Double.parseDouble(values[i]);
            }
            m.invoke(t, (Object) darray);
        } else if ("[Ljava.lang.Byte;".equalsIgnoreCase(parameterTypeName)) {
            Byte[] barray = new Byte[values.length];
            for (int i = 0; i < values.length; i++) {
                barray[i] = Byte.parseByte(values[i]);
            }
            m.invoke(t, (Object) barray);
        } else if ("[Ljava.lang.String;".equalsIgnoreCase(parameterTypeName)) {
            /*
             * String[] sarray = new String[values.length]; for(int i = 0; i <
             * values.length;i ++){ sarray[i] = values[i]; }
             */
            // 在方法中的String[] 数组 类型的参数，当成可变长的数组参，必须将它转变成对象类型
            m.invoke(t, (Object) values);
        }
    }

    // 在键名前加set
    private Map<String, String[]> pareseRequest(Map<String, String[]> map) {
        Map<String, String[]> newmap = new HashMap<String, String[]>();
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            String key = entry.getKey();
            key = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
            newmap.put(key, entry.getValue());
        }
        return newmap;
    }
}
