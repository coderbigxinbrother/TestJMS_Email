package com.yc.dao;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;

import com.yc.utils.YcUtils;

public class DBHelper {
	// 静态块: 生命周期长，程序一加载就会执行静态块内容.
	static {
		try {
			Class.forName(MyProperties.getInstance().getProperty("driverName"));
		} catch (Exception e) {
			e.printStackTrace();
			YcUtils.error(e);
		}
	}

	/**
	 * 查询: T看成一个BankAccount c是BankAccount的反射
	 * 
	 * @throws SQLException
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws NamingException
	 */
	public <T> List<T> findMultiObject(Class<T> c, String sql, List<Object> params)
			throws SQLException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NamingException {
		List<T> list = new ArrayList<T>();
		T obj = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = this.getCon();
			pstmt = con.prepareStatement(sql);
			setParams(pstmt, params);
			rs = pstmt.executeQuery();
			List<String> columneName = getAllColumeNames(rs); // 取出所有的列名
			List<String> setMethodName = transferMethodNames(columneName, "set"); // 将所有的列名转为对应的方法名.
			// setXxxx()
			Method[] methods = c.getDeclaredMethods();
			while (rs.next()) {
				obj = getObject(c, rs, columneName, setMethodName, methods);
				list.add(obj);
			}
		} finally {
			closeAll(pstmt, con, rs);
		}
		return list;
	}

	private <T> T getObject(Class<T> c, ResultSet rs, List<String> columneName, List<String> setMethodName,
			Method[] methods)
					throws InstantiationException, IllegalAccessException, SQLException, InvocationTargetException {
		T obj;
		obj = (T) c.newInstance(); // 通过c创建一个对象 BankAccount ba=new
		// BankAccount();

		for (int i = 0; i < columneName.size(); i++) {
			String cn = columneName.get(i); // 取出当前的列名
			String smn = setMethodName.get(i); // 取出方法名
			for (int j = 0; j < methods.length; j++) {
				Method method = methods[j];
				if (method.getName().equals(smn)) {
					// 取出这个列的数据类型
					if (method.getParameterTypes()[0].getName().equals("java.lang.Integer")
							|| method.getParameterTypes()[0].getName().equals("int")) {
						int r = rs.getInt(cn); // 取出值
						// 激活方法，传进去值
						method.invoke(obj, r);
						break;
					} else if (method.getParameterTypes()[0].getName().equals("java.lang.Double")
							|| method.getParameterTypes()[0].getName().equals("double")) {
						double r = rs.getDouble(cn); // 取出值
						// 激活方法，传进去值
						method.invoke(obj, r);
						break;
					} else if (method.getParameterTypes()[0].getName().equals("java.lang.Float")
							|| method.getParameterTypes()[0].getName().equals("float")) {
						float r = rs.getFloat(cn); // 取出值
						// 激活方法，传进去值
						method.invoke(obj, r);
						break;
					} else {
						String r = rs.getString(cn);
						method.invoke(obj, r);
						break;
					}
				}
			}

		}
		return obj;
	}

	/**
	 * 查询: T看成一个BankAccount c是BankAccount的反射
	 * 
	 * @throws SQLException
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws NamingException
	 */
	public <T> T findSingleObject(Class<T> c, String sql, List<Object> params)
			throws SQLException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NamingException {
		T obj = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = this.getCon();
			pstmt = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			setParams(pstmt, params);
			rs = pstmt.executeQuery();
			List<String> columneName = getAllColumeNames(rs); // 取出所有的列名
			List<String> setMethodName = transferMethodNames(columneName, "set"); // 将所有的列名转为对应的方法名.
			// setXxxx()
			Method[] methods = c.getDeclaredMethods();
			if (rs.next()) {
				if (rs.isLast()) {
					obj = getObject(c, rs, columneName, setMethodName, methods);

				} else {
					throw new RuntimeException("查询的数据有多条，请使用本类中的  findMultiObject()");
				}
			}
		} finally {
			closeAll(pstmt, con, rs);
		}
		return obj;
	}

	/**
	 * 
	 * @param columneName
	 * @param methodType
	 *            : 标准javabean的方法名的前缀, 如: "get" / "set"
	 * @return
	 */
	private List<String> transferMethodNames(List<String> columneName, String methodType) {
		List<String> setMethodName = new ArrayList<String>();
		if (columneName != null && columneName.size() > 0) {
			for (String cn : columneName) {
				StringBuffer sb = new StringBuffer(methodType);
				sb.append(cn.substring(0, 1).toUpperCase()).append(cn.substring(1).toLowerCase());
				setMethodName.add(sb.toString());
			}
		}
		return setMethodName;
	}

	// 获取联接的方法
	public Connection getCon() {
		// 1. jdbc方案 效率低
		// Connection con =
		// DriverManager.getConnection(MyProperties.getInstance()
		// .getProperty("url"), MyProperties.getInstance());

		// 2. tomcat 的jndi提供的联接池方案 , 产品用
		// Context initCtx = new InitialContext();
		// java:comp/env 默认的context名字
		// Context envCtx = (Context) initCtx.lookup("java:comp/env");
		// jdbc/bbs_oracle 子context名字
		// DataSource ds = (DataSource) envCtx.lookup("jdbc/bbs_oracle");
		// Connection con = ds.getConnection();

		// 3. 测试方案: 自带dbcp联接池
		// MyProperties 用来读取 db.properties数据库联接池配置文件.
		// BasicDataSourceFactory 通过这个工厂创建 一个 DataSource
		Connection con = null;
		try {
			DataSource ds = BasicDataSourceFactory.createDataSource(MyProperties.getInstance());
			con = ds.getConnection();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return con;
	}

	/**
	 * 查询: 返回一个Map对象, 只查一个对象 如果有多个对象，则不能用这个方法 select * from 表名 where id=1;
	 * 
	 * @throws SQLException
	 * @throws IOException
	 * @throws NamingException
	 */
	public List<Map<String, String>> findMultiObject(String sql, List<Object> params)
			throws SQLException, IOException, NamingException {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = this.getCon();
			pstmt = con.prepareStatement(sql);
			setParams(pstmt, params);
			rs = pstmt.executeQuery();
			List<String> columneName = getAllColumeNames(rs);

			while (rs.next()) {

				map = new HashMap<String, String>();
				for (String cn : columneName) {
					map.put(cn, rs.getString(cn));
				}
				list.add(map);
			}
		} finally {
			closeAll(pstmt, con, rs);
		}
		return list;
	}

	/**
	 * 查询: 返回一个Map对象, 只查一个对象 如果有多个对象，则不能用这个方法 select * from 表名 where id=1;
	 * 
	 * @throws SQLException
	 * @throws IOException
	 * @throws NamingException
	 */
	public Map<String, String> findSingleObject(String sql, List<Object> params)
			throws SQLException, IOException, NamingException {
		Map<String, String> map = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = this.getCon();
			pstmt = con.prepareStatement(sql);
			setParams(pstmt, params);
			rs = pstmt.executeQuery();
			List<String> columneName = getAllColumeNames(rs);

			if (rs.next()) {
				// if (rs.isLast()) {
				map = new HashMap<String, String>();
				for (String cn : columneName) {
					map.put(cn, rs.getString(cn));
				}
				// } else {
				// throw new RuntimeException(
				// "查询的数据有多条，请使用本类中的 findMultiObject()");
				// }
			}
		} finally {
			closeAll(pstmt, con, rs);
		}
		return map;
	}

	/**
	 * 从结果集中取出所有的列名，存到一个集合list中 : 技术点: jdbc2.0取元数据
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private List<String> getAllColumeNames(ResultSet rs) throws SQLException {
		List<String> columneName = new ArrayList<String>();
		if (rs != null) {
			for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
				columneName.add(rs.getMetaData().getColumnName(i + 1));
			}
		}
		return columneName;
	}

	/**
	 * 聚合函数查询
	 * 
	 * @throws IOException
	 * @throws SQLException
	 * @throws NamingException
	 */
	public double findDouble(String sql, List<Object> params) throws SQLException, IOException, NamingException {
		Connection con = null;
		PreparedStatement pstmt = null;
		double result = 0;
		ResultSet rs = null;
		try {
			con = this.getCon();
			pstmt = con.prepareStatement(sql);
			setParams(pstmt, params);

			rs = pstmt.executeQuery();
			if (rs.next()) {
				result = rs.getDouble(1);
			}
		} finally {
			closeAll(pstmt, con, rs);
		}
		return result;
	}

	/**
	 * 增删改的操作 多条语句 sql: 语句, 有可能有?,也有可能没有 params: 参数值的集合
	 * 
	 * @throws Exception
	 */
	public int doUpdate(List<String> sqls, List<List<Object>> params) throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		int result = -1;
		try {
			con = this.getCon();
			con.setAutoCommit(false); // 关闭隐式事务
			if (sqls != null && sqls.size() > 0) {
				for (int i = 0; i < sqls.size(); i++) {
					String sql = sqls.get(i);
					pstmt = con.prepareStatement(sql);
					setParams(pstmt, params.get(i));
					result = pstmt.executeUpdate();
				}
			}
			con.commit();
		} catch (Exception ex) {
			con.rollback();
			ex.printStackTrace(); // 这种方式只能将异常的堆zhan信息输出到控制台. 不能永久保存
			YcUtils.error(ex); // 使用日志，则可将信息保存
			throw ex;
		} finally {
			con.setAutoCommit(true); // 恢复现场.
			closeAll(pstmt, con, null);
		}
		return result;
	}

	/**
	 * 增删改的操作 单条语句 sql: 语句, 有可能有?,也有可能没有 params: 参数值的集合
	 * 
	 * @throws SQLException
	 * @throws IOException
	 * @throws NamingException
	 */
	public int doUpdate(String sql, List<Object> params) throws SQLException, IOException, NamingException {
		Connection con = null;
		PreparedStatement pstmt = null;
		int result = -1;
		try {
			con = this.getCon();
			pstmt = con.prepareStatement(sql);
			setParams(pstmt, params);
			result = pstmt.executeUpdate();
		} finally {
			closeAll(pstmt, con, null);
		}
		return result;
	}

	private void closeAll(PreparedStatement pstmt, Connection con, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
				YcUtils.error(e);

			}
		}
		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				YcUtils.error(e);
			}
		}
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
				YcUtils.error(e);
			}
		}
	}

	/**
	 * 给pstmt对象设置参数的方法
	 * 
	 * @throws SQLException
	 */
	private void setParams(PreparedStatement pstmt, List<Object> params) throws SQLException {
		if (params != null && params.size() > 0) {
			for (int i = 0; i < params.size(); i++) {
				pstmt.setString(i + 1, params.get(i).toString());
			}
		}
	}

	/**
	 * @param t
	 *            插入数据的对象
	 * @return 受影响的行数
	 * @throws NamingException
	 * @throws IOException
	 * @throws SQLException
	 */
	public <T> int save(T t) throws SQLException, IOException, NamingException {
		SqlParam sqlParam = getSaveSqlParam(t);
		return doUpdate(sqlParam.sql, sqlParam.params);
	}

	/**
	 * 
	 * @param t
	 *            插入数据的对象
	 * @param seqName
	 *            序列名
	 * @param idColumn
	 *            主键名
	 * @return 受影响的行数
	 */
	public <T> SqlParam getSaveSqlParam(T t, String seqName, String idColumn) {
		Class<?> c = t.getClass();
		List<Object> params = new ArrayList<Object>();
		String tableName = c.getName().substring(c.getName().lastIndexOf(".") + 1);
		Field[] fs = c.getFields(); // 取到指定类对象的类的属性
		String sql = "insert into " + tableName + "(";
		String sqlValue = " values(";
		try {
			for (Field f : fs) {
				String fn = f.getName(); // 取到属性名
				String mn = "get" + fn.substring(0, 1).toUpperCase() + fn.substring(1); // 对应属性的set方法名
				Method m = null;
				try {
					m = c.getMethod(mn);
				} catch (Exception e) {
					continue;
				}

				Object obj = m.invoke(t);
				if (obj != null) {
					sql += fn + ",";
					sqlValue += "?,";
					params.add(obj);
					// System.out.println(fn + "====" + obj);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		if (seqName != null && idColumn != null) {
			sql = sql + idColumn + ")";
			sqlValue = sqlValue + seqName + ".nextval)";
		} else {
			sql = sql.substring(0, sql.length() - 1) + ")";
			sqlValue = sqlValue.substring(0, sqlValue.length() - 1) + ")";
		}
		sql = sql + sqlValue;
		DBHelper.SqlParam sqlParam = new DBHelper().new SqlParam();
		sqlParam.sql = sql;
		sqlParam.params = params;
		return sqlParam;
	}

	/**
	 * @param t
	 *            插入数据的对象
	 * @param seqName
	 *            序列名
	 * @param idColumn
	 *            主键名
	 * @return 受影响的行数
	 */
	public <T> SqlParam getSaveSqlParam(T t) {
		Class<?> c = t.getClass();
		List<Object> params = new ArrayList<Object>();
		String tableName = c.getName().substring(c.getName().lastIndexOf(".") + 1);
		Field[] fs = c.getFields(); // 取到指定类对象的类的属性
		String sql = "insert into " + tableName + "(";
		String sqlValue = " values(";
		try {
			for (Field f : fs) {
				String fn = f.getName(); // 取到属性名
				String mn = "get" + fn.substring(0, 1).toUpperCase() + fn.substring(1); // 对应属性的set方法名
				Method m = null;
				try {
					m = c.getMethod(mn);
				} catch (Exception e) {
					continue;
				}

				Object obj = m.invoke(t);
				if (obj != null) {
					sql += fn + ",";
					sqlValue += "?,";
					params.add(obj);
					// System.out.println(fn + "====" + obj);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		sql = sql.substring(0, sql.length() - 1) + ")";
		sqlValue = sqlValue.substring(0, sqlValue.length() - 1) + ")";
		sql = sql + sqlValue;
		// System.out.println(sql);

		DBHelper.SqlParam sqlParam = new DBHelper().new SqlParam();
		sqlParam.sql = sql;
		sqlParam.params = params;
		return sqlParam;
	}

	/**
	 * 
	 * @param t
	 *            插入数据的对象
	 * @param seqName
	 *            序列名
	 * @param idColumn
	 *            主键名
	 * @return 受影响的行数
	 * @throws NamingException
	 * @throws IOException
	 * @throws SQLException
	 */
	public <T> int save(T t, String seqName, String idColumn) throws SQLException, IOException, NamingException {
		SqlParam sqlParam = getSaveSqlParam(t);
		return doUpdate(sqlParam.sql, sqlParam.params);
	}

	public <T> T query(T t) {
		Class<?> c = t.getClass();
		List<Object> params = new ArrayList<Object>();
		String tableName = c.getName().substring(c.getName().lastIndexOf(".") + 1);
		Field[] fs = c.getFields(); // 取到指定类对象的类的属性
		String sql = "select * from " + tableName;
		String sqlValue = "";
		try {
			for (Field f : fs) {
				String fn = f.getName(); // 取到属性名
				String mn = "get" + fn.substring(0, 1).toUpperCase() + fn.substring(1); // 对应属性的set方法名
				Method m = null;
				try {
					m = c.getMethod(mn);
				} catch (Exception e) {
					continue;
				}

				Object obj = m.invoke(t);
				if (obj != null) {
					sqlValue += fn + "=? and ";
					// System.out.println(obj + "--->>>>");
					params.add(obj);
				}

			}
		} catch (Exception e) {
			t = null;
			throw new RuntimeException(e);
		}
		sqlValue = sqlValue.length() == 0 ? "" : (" where " + sqlValue.substring(0, sqlValue.length() - 5));
		sql = sql + sqlValue;
		// System.out.println( doSelect(sql, params).size());
		try {
			Map<String, String> results = findSingleObject(sql, params);
			// System.out.println(results.size());
			for (Field f : fs) {
				String fn = f.getName(); // 取到属性名
				String mn = "set" + fn.substring(0, 1).toUpperCase() + fn.substring(1); // 对应属性的set方法名

				Method m = null;
				try {
					m = c.getMethod(mn, f.getType());
				} catch (Exception e) {
					continue;
				}

				if (f.getType().getName().equals(Integer.class.getName())) {
					m.invoke(t, Integer.valueOf(results.get(fn.toUpperCase())));
				} else if (f.getType().getName().equals(Double.class.getName())) {
					m.invoke(t, Double.valueOf(results.get(fn.toUpperCase())));
				} else {
					m.invoke(t, results.get(fn.toUpperCase()));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return t;
	}

	public <T> List<T> queryList(T t) {
		Class<?> c = t.getClass();
		List<T> ts = null;
		List<Object> params = new ArrayList<Object>();
		String tableName = c.getName().substring(c.getName().lastIndexOf(".") + 1);
		Field[] fs = c.getFields(); // 取到指定类对象的类的属性
		String sql = "select * from " + tableName;
		String sqlValue = "";
		try {
			for (Field f : fs) {
				String fn = f.getName(); // 取到属性名

				String mn = "get" + fn.substring(0, 1).toUpperCase() + fn.substring(1); // 对应属性的set方法名
				Method m = null;
				try {
					m = c.getMethod(mn);
				} catch (Exception e) {
					continue;
				}

				Object obj = m.invoke(t);
				// System.out.println(obj);
				if (obj != null) {
					sqlValue += fn + "=? and ";
					params.add(obj);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		sqlValue = sqlValue.length() == 0 ? "" : (" where " + sqlValue.substring(0, sqlValue.length() - 5));
		sql = sql + sqlValue;
		// System.out.println(sql);
		try {
			List<Map<String, String>> rs = findMultiObject(sql, params);
			ts = new ArrayList<T>();
			for (Map<String, String> results : rs) {
				T temp = (T) c.newInstance();
				for (Field f : fs) {
					String fn = f.getName(); // 取到属性名
					String mn = "set" + fn.substring(0, 1).toUpperCase() + fn.substring(1); // 对应属性的set方法名

					Method m = null;
					try {
						m = c.getDeclaredMethod(mn, f.getType());
					} catch (Exception e) {
						continue;
					}

					if (f.getType().getName().equals(Integer.class.getName())) {
						m.invoke(temp, Integer.valueOf(results.get(fn.toUpperCase())));
					} else if (f.getType().getName().equals(Double.class.getName())) {
						m.invoke(temp, Double.valueOf(results.get(fn.toUpperCase())));
					} else {
						m.invoke(temp, results.get(fn.toUpperCase()));
					}
				}
				ts.add(temp);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return ts;
	}

	// 更新信息的操作
	public <T> boolean update(T t, String idColumn) throws SQLException, IOException, NamingException {
		Class<?> c = t.getClass(); // Class<Integer> cla;与Class<?> cl;
									// 前一个表示cla只能指向Integer这种类型，而后一个cl表示可以指向任意类型。
									// cla = Integer.class 可以，但cla =
									// Double.class就不可以。
									// 但是cl = Integer.class 可以，cl =
									// Double.class也可以 、
									// ? 是 java通配符。
		List<Object> params = new ArrayList<Object>();
		String tableName = c.getName().substring(c.getName().lastIndexOf(".") + 1);
		Field[] fs = c.getFields(); // 取到指定类对象的类的属性
		String sql = "update " + tableName + " set ";
		String whereSql = " where " + idColumn;
		// String sqlValue = "";
		try {
			for (Field f : fs) {
				String fn = f.getName(); // 取到属性名
				String mn = "get" + fn.substring(0, 1).toUpperCase() + fn.substring(1); // 对应属性的get方法名
				Method m;

				try {
					m = c.getMethod(mn);
					// System.out.println("m 1= " +m.toString() );
				} catch (Exception e) {
					continue;
				}
				Object obj = m.invoke(t);

				if (fn.equals(idColumn) && obj == null) {
					throw new RuntimeException("没有找到当主键字段值");
				}

				if (fn.equals(idColumn)) {
					whereSql += "=" + obj;
					continue;
				}
				if (obj != null) {
					sql += fn + "=?,";
					params.add(obj);
				}
			}
		} catch (Exception e) {
			t = null;
			throw new RuntimeException(e);
		}
		sql = sql.substring(0, sql.length() - 1);
		sql += whereSql;
		// System.out.println( sql );
		return doUpdate(sql, params) > 0;
	}

	/**
	 * 使用内部类来封装一条sql语句与它的参数值集合
	 * @author happy
	 * @date 2016年3月19日 -- 下午9:48:53
	 *
	 */
	public class SqlParam {
		public String sql;
		public List<Object> params;
	}

}
