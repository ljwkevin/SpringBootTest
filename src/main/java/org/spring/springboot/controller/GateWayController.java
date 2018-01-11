package org.spring.springboot.controller;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.facelets.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.springboot.domain.GateWay;
import org.spring.springboot.domain.TagDef;
import org.spring.springboot.service.GateWayService;
import org.spring.springboot.util.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.Connection;

/**
 * 提供的接口URL： /api/v1/min_stat /api/v1/his_stat_uv /api/v1/his_stat_pv
 * /api/v1/his_stat_succ /api/v1/his_stat_avg_rsp_time
 * /api/v1/his_stat_err_node_cate
 */
@RestController
public class GateWayController {
	private static final Logger LOGGER = LoggerFactory.getLogger(GateWayController.class);
	private final String TIME_FORMAT2 = "yyyy-MM-dd'T'HH:mm:ss";
	@Autowired
	private GateWayService gateService;
	@Value("${es_url}")
	private String esUrl;
	@Value("${queryString}")
	private String queryString;
	@Value("${queryNodeString}")
	private String queryNodeString;
	@Value("${queryErrNodeCate}")
	private String queryErrNodeCate;
	@Value("${mysqldriver}")
	private String driver;
	@Value("${mysqlurl}")
	private String url;
	@Value("${mysqlusername}")
	private String username;
	@Value("${mysqlpassword}")
	private String passwd;
	@Value("${max_rec_cnt}")
	private int max_rec_cnt;
	@Value("${timespan}")
	private long timespan;
	ControllerUtils util = new ControllerUtils();
	
	/* 指定时间段在线用户数 /api/v1/his_stat_uv */
	@RequestMapping(value = "/api/v1/his_stat_uv", method = RequestMethod.GET)
	public JSONObject queryUVCount(GateWay gate) {
		JSONObject resObj = new JSONObject();
		// 判断入口参数时间范围是否超过timespan
		String startTime = gate.getStart();
		String endTime = gate.getEnd();
		// 入参判断
		if (null == gate.getApp_id()) {
			// 参数不正确
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_APPID);
		} else if (null == startTime) {
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_START);
		} else if (null == endTime) {
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_END);
		} else if (null == gate.getBeg_rec_no()) {
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_BEGCNT);
		} else if (util.compareTime(endTime, startTime,TIME_FORMAT2) >= timespan) {
			// 时间跨度超出
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_DATE+timespan);
		} else if (startTime.compareTo(endTime) > 0) {
			// 开始时间大于结束时间
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_STARTBIGENG);
		} else if (null == (util.findAppID(gate.getApp_id(),driver, url, username,passwd))) {
			// appid不存在
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_FINDAPPID);
		} else if (gate.getMax_rec_cnt()  >  max_rec_cnt) {
			// max_rec_cnt超出
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_MAXRECCNT+max_rec_cnt);
		} else {
			// 根据app_id拆分是否是传递多个应用
			String[] indexArr = util.dealEvery(gate.getApp_id());
			// 处理应用和索引之间的映射关系
			String[] indexMapp = util.dealIndexUVMapp(indexArr);
			// 处理节点是否为入口节点
			String entryNode = util.dealEntryNode(gate.getEntry_node(), gate.getApp_id(), driver, url, username,passwd);
			if (null != indexMapp) {
				for (int i = 0; i < indexMapp.length; i++) {
					resObj = gateService.searchOnlineUVCount(esUrl, gate, queryString, queryNodeString, indexMapp[i],
							entryNode);
					if (resObj != null && !"{}".equals(resObj) && resObj.size() != 0) {
						resObj.put("status", "success");
					}else{
						resObj.put("status", "error");
						resObj.put("error", "请求超时");
						resObj.put("error_type","查询ES超时");
					}
				}
			}
		}
		System.out.println("UV:" + resObj.toJSONString());
		return resObj;
	}

	/* 指定时间段访问量变化数 /api/v1/his_stat_pv */
	@RequestMapping(value = "/api/v1/his_stat_pv", method = RequestMethod.GET)
	public JSONObject queryPVCount(GateWay gate) {
		// 判断入口参数时间范围是否超过一天
		String startTime = gate.getStart();
		String endTime = gate.getEnd();
		JSONObject resObj = new JSONObject();
		// 入参判断
		if (null == gate.getApp_id()) {
			// 参数不正确
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_APPID);
		} else if (null == startTime) {
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_START);
		} else if (null == endTime) {
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_END);
		} else if (null == gate.getBeg_rec_no()) {
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_BEGCNT);
		} else if (util.compareTime(endTime, startTime,TIME_FORMAT2) >= timespan) {
			// 时间跨度超出
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_DATE+timespan);
		} else if (startTime.compareTo(endTime) > 0) {
			// 开始时间大于结束时间
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_STARTBIGENG);
		}else if (null == (util.findAppID(gate.getApp_id(),driver, url, username,passwd))) {
			// appid不存在
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_FINDAPPID);
		} else if (gate.getMax_rec_cnt()  >  max_rec_cnt) {
			// max_rec_cnt超出
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_MAXRECCNT+max_rec_cnt);
		} else {
			// 根据app_id拆分是否是传递多个应用
			String[] indexArr = util.dealEvery(gate.getApp_id());
			// 处理应用和索引之间的映射关系
			String[] indexMapp = util.dealIndexPVMapp(indexArr);
			// 处理节点是否为入口节点
			String entryNode = util.dealEntryNode(gate.getEntry_node(), gate.getApp_id(), driver, url, username,
					passwd);
			if (null != indexMapp) {
				for (int i = 0; i < indexMapp.length; i++) {
					resObj = gateService.searchOnlinePVCount(esUrl, gate, queryString, queryNodeString, indexMapp[i],
							entryNode);
					if (resObj != null && !"{}".equals(resObj) && resObj.size() != 0) {
						resObj.put("status", "success");
					}else{
						resObj.put("status", "error");
						resObj.put("error", "请求超时");
						resObj.put("error_type","查询ES超时");
					}
				}
			}
		}
		System.out.println("PV:" + resObj.toJSONString());
		return resObj;
	}

	/* 指定时间段访问成功率 /api/v1/his_stat_succ */
	@RequestMapping(value = "/api/v1/his_stat_succ", method = RequestMethod.GET)
	public JSONObject querySuccRatio(GateWay gate) {
		// 判断入口参数时间范围是否超过一天
		String startTime = gate.getStart();
		String endTime = gate.getEnd();
		JSONObject resObj = new JSONObject();
		// 入参判断
		if (null == gate.getApp_id()) {
			// 参数不正确
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_APPID);
		} else if (null == startTime) {
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_START);
		} else if (null == endTime) {
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_END);
		} else if (null == gate.getBeg_rec_no()) {
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_BEGCNT);
		} else if (util.compareTime(endTime, startTime,TIME_FORMAT2) >= timespan) {
			// 时间跨度超出
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_DATE+timespan);
		}else if (startTime.compareTo(endTime) > 0) {
			// 开始时间大于结束时间
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_STARTBIGENG);
		} else if (null == (util.findAppID(gate.getApp_id(),driver, url, username,passwd))) {
			// appid不存在
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_FINDAPPID);
		} else if (gate.getMax_rec_cnt()  >  max_rec_cnt) {
			// max_rec_cnt超出
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_MAXRECCNT+max_rec_cnt);
		} else {
			// 根据app_id拆分是否是传递多个应用
			String[] indexArr = util.dealEvery(gate.getApp_id());
			// 处理应用和索引之间的映射关系
			String[] indexMapp = util.dealIndexSuccMapp(indexArr);
			// 处理节点是否为入口节点
			String entryNode = util.dealEntryNode(gate.getEntry_node(), gate.getApp_id(), driver, url, username,
					passwd);
			if (null != indexMapp) {
				for (int i = 0; i < indexMapp.length; i++) {
					resObj = gateService.searchOnlinePVCount(esUrl, gate, queryString, queryNodeString, indexMapp[i],
							entryNode);
					if (resObj != null && !"{}".equals(resObj) && resObj.size() != 0) {
						resObj.put("status", "success");
					}else{
						resObj.put("status", "error");
						resObj.put("error", "请求超时");
						resObj.put("error_type","查询ES超时");
					}
				}
			}
		}
		System.out.println("Succ:" + resObj.toJSONString());
		return resObj;
	}

	/* 指定时间段平均访问时间 /api/v1/his_stat_avg_rsp_time */
	@RequestMapping(value = "/api/v1/his_stat_avg_rsp_time", method = RequestMethod.GET)
	public JSONObject queryAvgRespTime(GateWay gate) {
		// 判断入口参数时间范围是否超过一天
		String startTime = gate.getStart();
		String endTime = gate.getEnd();
		JSONObject resObj = new JSONObject();
		if (null == gate.getApp_id()) {
			// 参数不正确
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_APPID);
		} else if (null == startTime) {
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_START);
		} else if (null == endTime) {
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_END);
		} else if (null == gate.getBeg_rec_no()) {
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_BEGCNT);
		} else if (util.compareTime(endTime, startTime,TIME_FORMAT2) >= timespan) {
			// 时间跨度超出
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_DATE+timespan);
		} else if (startTime.compareTo(endTime) > 0) {
			// 开始时间大于结束时间
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_STARTBIGENG);
		} else if (null == (util.findAppID(gate.getApp_id(),driver, url, username,passwd))) {
			// appid不存在
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_FINDAPPID);
		} else if (gate.getMax_rec_cnt()  >  max_rec_cnt) {
			// max_rec_cnt超出
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_MAXRECCNT+max_rec_cnt);
		} else {
			// 根据app_id拆分是否是传递多个应用
			String[] indexArr = util.dealEvery(gate.getApp_id());
			// 处理应用和索引之间的映射关系
			String[] indexMapp = util.dealIndexSuccMapp(indexArr);
			// 处理节点是否为入口节点
			String entryNode = util.dealEntryNode(gate.getEntry_node(), gate.getApp_id(), driver, url, username,
					passwd);
			if (null != indexMapp) {
				for (int i = 0; i < indexMapp.length; i++) {
					resObj = gateService.searchOnlinePVCount(esUrl, gate, queryString, queryNodeString, indexMapp[i],
							entryNode);
					if (resObj != null && !"{}".equals(resObj) && resObj.size() != 0) {
						resObj.put("status", "success");
					}else{
						resObj.put("status", "error");
						resObj.put("error", "请求超时");
						resObj.put("error_type","查询ES超时");
					}
				}
			}
		}
		System.out.println("Avg:" + resObj.toJSONString());
		return resObj;
	}

	/* 指定时间段错误节点，错误类别 /api/v1/his_stat_err_node_cate */
	@RequestMapping(value = "/api/v1/his_stat_err_node_cate", method = RequestMethod.GET)
	public JSONObject queryErrorNodeCate(GateWay gate) {
		JSONObject resObj = new JSONObject();
		String startTime = gate.getStart();
		String endTime = gate.getEnd();
		// 入参判断
		if (null == gate.getApp_id()) {
			// 参数不正确
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_APPID);
		} else if (null == startTime) {
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_START);
		} else if (null == endTime) {
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_END);
		} else if (null == gate.getProduct()) {
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_PRODUCT);
		}else if (null == gate.getService()) {
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_SERVICE);
		}else if (util.compareTime(endTime, startTime,TIME_FORMAT2) >= timespan) {
			// 时间跨度超出
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_DATE);
		} else if (startTime.compareTo(endTime) > 0) {
			// 开始时间大于结束时间
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_STARTBIGENG);
		}else if (null == (util.findAppID(gate.getApp_id(),driver, url, username,passwd))) {
			// appid不存在
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_FINDAPPID);
		}else if (null == (util.findService(gate.getService(),driver, url, username,passwd))) {
			// appid不存在
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_FINDSERVICE);
		} else if (null == (util.findProduct(gate.getProduct(),driver, url, username,passwd))) {
			// appid不存在
			resObj.put(TagDef.STATUS, TagDef.STATUS_ERROR);
			resObj.put(TagDef.ERROR_TYPE, TagDef.RETURN_ERROR_PARAMETER);
			resObj.put(TagDef.ERROR, TagDef.ERROR_DESCRIBE_FINDPRODUCT);
		} else {

			// 查询数据库获取产品、服务、应用对应的节点关系
			List<String> nodes = util.dealRelation(gate, driver, url, username, passwd);
			// 保存节点和产品、服务、应用之间的映射关系
			Map<String, String> nodeMapp = new HashMap<String, String>();
			nodeMapp = util.dealNodeMapp(nodes, driver, url, username, passwd);
			String[] indexArr = util.dealEvery(gate.getApp_id());
			String[] indexMapp = util.dealIndexErrorMapp(indexArr);
			StringBuilder indexAll = new StringBuilder();
			for (int i = 0; i < indexMapp.length; i++) {
				indexAll.append(indexMapp[i] + ",");
			}
			String indexStr = indexAll.deleteCharAt(indexAll.length() - 1).toString();
			JSONArray list = new JSONArray();
			List<JSONObject> result = gateService.searchErrorNodeCate(esUrl, gate, queryErrNodeCate, indexStr,
					nodeMapp);
			if (result != null && result.size() != 0) {
				for (JSONObject res : result) {
					list.add(res);
				}
				resObj.put("data", list);
				resObj.put("status", "success");
				resObj.put("timestamp", gate.getStart());
			} else {
				resObj.put("status", "success");
				resObj.put("data", "[]");
				resObj.put("timestamp", gate.getStart());
			}
		}
		return resObj;
	}

	/* URL错误对应的返回结果 */
	@RequestMapping(value = "error", method = RequestMethod.GET)
	public JSONObject queryError(GateWay gate) {
		JSONObject resObj = new JSONObject();
		resObj.put("status", "error");
		resObj.put("error", "URL无效");
		resObj.put("error_type", "请求错误");
		return resObj;
	}
}