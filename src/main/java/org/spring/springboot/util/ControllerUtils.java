package org.spring.springboot.util;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.springboot.controller.GateWayController;
import org.spring.springboot.domain.GateWay;

import com.mysql.jdbc.Connection;

public class ControllerUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(GateWayController.class);

	//查找数据库中appid是否存在
	public String findAppID(String app_id,String driver,String url,String username,String passwd) {
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		Connection conn = null;
		String res = null;
		try {
				String querySql = "select distinct app_id from tbl_node_biz_relation where app_id = '"+ app_id + "'";
				Class.forName(driver); // classLoader,加载对应驱动
				conn = (Connection) DriverManager.getConnection(url, username, passwd);
				pstmt = (PreparedStatement) conn.prepareStatement(querySql);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					res = rs.getString("app_id");
				}else{
					res = null;
				}
		} catch (ClassNotFoundException e) {
			LOGGER.info("数据加载驱动出错:" + e);
		} catch (SQLException e) {
			LOGGER.info("数据连接错误:" + e);
		} catch (Exception e) {
			LOGGER.info("数据库中没有对应的app_id", e.getMessage());
		} finally {
			closeDb(conn, pstmt, rs);
		}

		return res;
	}
	
	//查找数据库中的产品是否存在
	public String findProduct(String product,String driver,String url,String username,String passwd) {
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		Connection conn = null;
		String res = null;
		try {
				String querySql = "select distinct product from tbl_node_biz_relation where product = '"+ product + "'";
				Class.forName(driver); // classLoader,加载对应驱动
				conn = (Connection) DriverManager.getConnection(url, username, passwd);
				pstmt = (PreparedStatement) conn.prepareStatement(querySql);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					res = rs.getString("product");
				}else{
					res = null;
				}
		} catch (ClassNotFoundException e) {
			LOGGER.info("数据加载驱动出错:" + e);
		} catch (SQLException e) {
			LOGGER.info("数据连接错误:" + e);
		} catch (Exception e) {
			LOGGER.info("数据库中没有对应的app_id", e.getMessage());
		} finally {
			closeDb(conn, pstmt, rs);
		}
		return res;
	}
	
	//查找数据库中的服务是否存在
	public String findService(String service,String driver,String url,String username,String passwd) {
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		Connection conn = null;
		String res = null;
		try {
				String querySql = "select distinct service from tbl_node_biz_relation where service = '"+ service + "'";
				Class.forName(driver); // classLoader,加载对应驱动
				conn = (Connection) DriverManager.getConnection(url, username, passwd);
				pstmt = (PreparedStatement) conn.prepareStatement(querySql);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					res = rs.getString("service");
				}else{
					res = null;
				}
		} catch (ClassNotFoundException e) {
			LOGGER.info("数据加载驱动出错:" + e);
		} catch (SQLException e) {
			LOGGER.info("数据连接错误:" + e);
		} catch (Exception e) {
			LOGGER.info("数据库中没有对应的app_id", e.getMessage());
		} finally {
			closeDb(conn, pstmt, rs);
		}
		return res;
	}

	//比较起止时间是否大于一天
	public long compareTime(String endTime, String startTime,String format) {
		 SimpleDateFormat formater = new SimpleDateFormat(format);  
	        long diff=0L;  
	        try {  
	            long end = formater.parse(endTime).getTime();  
	            long start = formater.parse(startTime).getTime();  
	            diff=(Math.abs(end-start) / (1000 * 60 * 60 * 24));  
	        } catch (ParseException e) {  
	        	LOGGER.info("起止时间解析不对",e.getMessage());;  
	        }  
	        return diff; 
	}
	
	/* 处理UV和索引之间的映射关系 */
	public String[] dealIndexUVMapp(String[] indexArr) {
		String[] res = new String[indexArr.length];
		try {
			for (int i = 0; i < indexArr.length; i++) {
				if ("opengw".equals(indexArr[i])) {
					res[i] = "opengw-uv";
				} else if ("all_link_road".equals(indexArr[i])) {
					res[i] = "westsavagestat-uv";
				}
			}
		} catch (Exception e) {
			LOGGER.info("处理UV和索引之间映射关系出错", e.getMessage());
		}
		return res;
	}

	/* 处理PV和索引之间的映射关系 */
	public String[] dealIndexPVMapp(String[] indexArr) {
		String[] res = new String[indexArr.length];
		try {
			for (int i = 0; i < indexArr.length; i++) {
				if ("opengw".equals(indexArr[i])) {
					res[i] = "opengw-pv";
				} else if ("all_link_road".equals(indexArr[i])) {
					res[i] = "westsavagestat-pv";
				}
			}
		} catch (Exception e) {
			LOGGER.info("处理PV和索引之间映射关系出错", e.getMessage());
		}
		return res;
	}

	/* 处理成功和索引之间的映射关系 */
	public String[] dealIndexSuccMapp(String[] indexArr) {
		String[] res = new String[indexArr.length];
		try {
			for (int i = 0; i < indexArr.length; i++) {
				if ("opengw".equals(indexArr[i])) {
					res[i] = "opengw-success";
				} else if ("all_link_road".equals(indexArr[i])) {
					res[i] = "westsavagestat-success";
				}
			}
		} catch (Exception e) {
			LOGGER.info("处理访问成功率和索引之间映射关系出错", e.getMessage());
		}
		return res;
	}

	/* 处理平均响应和索引之间的映射关系 */
	public String[] dealIndexAvgResMapp(String[] indexArr) {
		String[] res = new String[indexArr.length];
		try {
			for (int i = 0; i < indexArr.length; i++) {
				if ("opengw".equals(indexArr[i])) {
					res[i] = "opengw-avgresponsetime";
				} else if ("all_link_road".equals(indexArr[i])) {
					res[i] = "westsavagestat-avgresponsetime";
				}
			}
		} catch (Exception e) {
			LOGGER.info("处理平均响应和索引之间映射关系出错", e.getMessage());
		}
		return res;
	}

	/* 处理错误和索引之间的映射关系 */
	public String[] dealIndexErrorMapp(String[] indexArr) {
		String[] res = new String[] {};
		try {
			if (indexArr.length == 1 && "all_link_road".equals(indexArr[0])) {
				res = new String[] { "westsavagestat-error", "westsavagestat-fail", "westsavagestat-timeout" };
			} else if (indexArr.length == 1 && "opengw".equals(indexArr[0])) {
				res = new String[] { "opengw-error", "opengw-fail", "opengw-timeout" };
			} else if (indexArr.length == 2) {
				res = new String[] { "westsavagestat-error", "westsavagestat-fail", "westsavagestat-timeout",
						"opengw-error", "opengw-fail", "opengw-timeout" };
			}
		} catch (Exception e) {
			LOGGER.info("处理错误节点和索引映射错误:" + e.getMessage());
		}
		return res;
	}

	// 查询对应的节点与产品、服务、应用之间关系
	public Map<String, String> dealNodeMapp(List<String> nodes,String driver,String url,String username,String passwd) {
		Map<String, String> node = new HashMap<String, String>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		Connection conn = null;
		try {
			for (String nodeTemp : nodes) {
				String querySql = "select product,service,app_id from tbl_node_biz_relation where node_name = '"
						+ nodeTemp + "'";
				Class.forName(driver); // classLoader,加载对应驱动
				conn = (Connection) DriverManager.getConnection(url, username, passwd);
				pstmt = (PreparedStatement) conn.prepareStatement(querySql);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					String product = rs.getString("product");
					String service = rs.getString("service");
					String app_id = rs.getString("app_id");
					node.put(nodeTemp, product + "#" + service + "#" + app_id);
				}
			}
		} catch (ClassNotFoundException e) {
			LOGGER.info("数据加载驱动出错:" + e);
		} catch (SQLException e) {
			LOGGER.info("数据连接错误:" + e);
		} catch (Exception e) {
			LOGGER.info("处理对应的节点与产品、服务、应用之间关系出错", e.getMessage());
		} finally {
			closeDb(conn, pstmt, rs);
		}
		return node;
	}

	// 查询数据库获取产品、服务、应用对应的节点关系
	public List<String> dealRelation(GateWay gate,String driver,String url,String username,String passwd) {
		List<String> nodeList = new ArrayList<String>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		Connection conn = null;
		try {
			String product = gate.getProduct();
			String service = gate.getService();
			String app_id = gate.getApp_id();
			String querySql = conCatQuerySql(product, service, app_id);
			String queryLastSql = "select node_name from tbl_node_biz_relation " + " where " + querySql;
			Class.forName(driver); // classLoader,加载对应驱动
			conn = (Connection) DriverManager.getConnection(url, username, passwd);
			pstmt = (PreparedStatement) conn.prepareStatement(queryLastSql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				nodeList.add(rs.getString("node_name"));
			}
		} catch (ClassNotFoundException e) {
			LOGGER.info("数据加载驱动出错:" + e);
		} catch (SQLException e) {
			LOGGER.info("数据连接错误:" + e);
		} catch (Exception e) {
			LOGGER.info("处理产品，服务，应用对应节点关系出错", e.getMessage());
		} finally {
			closeDb(conn, pstmt, rs);
		}
		return nodeList;
	}

	//关闭数据库连接
	public void closeDb(Connection conn, PreparedStatement pstmt, ResultSet rs) {
		try {
			if (rs != null) {// 如果返回的结果集对象不能为空,就关闭连接
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (pstmt != null) {
				pstmt.close();// 关闭预编译对象
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (conn != null) {
				conn.close();// 关闭连接对象
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* 拼接查询语句 */
	public String conCatQuerySql(String product, String service, String app_id) {
		StringBuilder queryString = new StringBuilder();
		try {
			String[] productArr = dealEvery(product);
			if (null != productArr) {
				for (int i = 0; i < productArr.length; i++) {
					queryString.append(" product= '" + productArr[i] + "' or");
				}
			}
			queryString = queryString.deleteCharAt(queryString.length() - 1).deleteCharAt(queryString.length() - 1);
			String[] serviceArr = dealEvery(service);
			if (null != serviceArr) {
				queryString.append(" and ");
				for (int i = 0; i < serviceArr.length; i++) {
					queryString.append(" service= '" + serviceArr[i] + "' or");
				}
			}
			queryString = queryString.deleteCharAt(queryString.length() - 1).deleteCharAt(queryString.length() - 1);
			String[] appArr = dealEvery(app_id);
			if (null != appArr) {
				queryString.append(" and ");
				for (int i = 0; i < appArr.length; i++) {
					queryString.append(" app_id= '" + appArr[i] + "' or");
				}
			}
			queryString = queryString.deleteCharAt(queryString.length() - 1).deleteCharAt(queryString.length() - 1);
		} catch (Exception e) {
			LOGGER.info("拼接数据库查询语句出错", e.getMessage());
		}
		return queryString.toString();
	}

	/* appid是指索引名，如果有多个index,拆分多个index来分别进行查询 */
	public String[] dealEvery(String indexName) {
		String[] indexArr = null;
		try {
			if (indexName.contains(",")) {
				indexArr = new String[indexName.split(",").length];
				for (int i = 0; i < indexArr.length; i++) {
					indexArr[i] = indexName.split(",")[i];
				}
			} else {
				indexArr = new String[] { indexName };
			}
		} catch (Exception e) {
			LOGGER.info("拆分出错", e.getMessage());
		}
		return indexArr;
	}

	// 处理节点是不是入口节点
	public  String dealEntryNode(Integer entry_node, String app_id,String driver,String url,String username,String passwd) {
		String res = null;
		StringBuilder sb = new StringBuilder();
		if (entry_node != null && entry_node == 1) {
			// 查询入口节点
			ResultSet rs = null;
			PreparedStatement pstmt = null;
			Connection conn = null;
			try {
				String querySql = "select node_name from tbl_node_biz_relation where app_id = '" + app_id + "'"
						+ " and entry_node = 1";
				Class.forName(driver); // classLoader,加载对应驱动
				conn = (Connection) DriverManager.getConnection(url, username, passwd);
				pstmt = (PreparedStatement) conn.prepareStatement(querySql);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					sb.append("\"" + rs.getString("node_name") + "\"" + ",");
				}
				res = sb.deleteCharAt(sb.length() - 1).toString();
			} catch (ClassNotFoundException e) {
				LOGGER.info("数据加载驱动出错:" + e);
			} catch (SQLException e) {
				LOGGER.info("数据连接错误:" + e);
			} catch (Exception e) {
				LOGGER.info("获取入口节点错误", e.getMessage());
			} finally {
				closeDb(conn, pstmt, rs);
			}
		} else {
			res = null;
		}
		return res;
	}

}
