package org.spring.springboot.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.spring.springboot.domain.GateWay;
import org.spring.springboot.service.GateWayService;
import org.spring.springboot.util.HttpUtil;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

/**
 * 业务逻辑实现类
 * <p>
 */
@Service
public class GateWayESServiceImpl implements GateWayService {

	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(GateWayESServiceImpl.class);

	/* 指定时间段在线用户数 /api/v1/his_stat_uv */
	@Override
	public JSONObject searchOnlineUVCount(String esUrl, GateWay gate, String queryString, String queryNodeString,
			String indexName, String entryNode) {
		JSONObject listJson = new JSONObject();
		esUrl = esUrl.replace("indexName", indexName);
		String queryRes = null;
		if (entryNode == null || "".equals(entryNode)) {
			// 转换为查询所有语句
			queryRes = dealReplace(gate, queryString, null);
		} else {
			// 查入口节点
			queryRes = dealReplace(gate, queryNodeString, entryNode);
		}
		String query = HttpUtil.createQueryResult(queryRes, esUrl);
		if (null != query) {
			listJson = HttpUtil.parseJsonUV(query, gate.getApp_id());
		}
		return listJson;
	}

	/* 指定时间段访问量变化数 /api/v1/his_stat_pv */
	@Override
	public JSONObject searchOnlinePVCount(String esUrl, GateWay gate, String queryString, String queryNodeString,
			String indexName, String entryNode) {
		JSONObject listJson = new JSONObject();
		esUrl = esUrl.replace("indexName", indexName);
		String queryRes = null;
		if (entryNode == null || "".equals(entryNode)) {
			// 转换为查询所有语句
			queryRes = dealReplace(gate, queryString, null);
		} else {
			// 查入口节点
			queryRes = dealReplace(gate, queryNodeString, entryNode);
		}
		String query = HttpUtil.createQueryResult(queryRes, esUrl);
		if (null != query) {
			listJson = HttpUtil.parseJsonPV(query, gate.getApp_id());
		}
		return listJson;
	}

	/* 指定时间段访问成功率 /api/v1/his_stat_suc_ratio */
	@Override
	public JSONObject searchAccessSuccRatio(String esUrl, GateWay gate, String queryString, String queryNodeString,
			String indexName, String entryNode) {
		JSONObject listJson = new JSONObject();
		esUrl = esUrl.replace("indexName", indexName);
		String queryRes = null;
		if (entryNode == null || "".equals(entryNode)) {
			// 转换为查询所有语句
			queryRes = dealReplace(gate, queryString, null);
		} else {
			// 查入口节点
			queryRes = dealReplace(gate, queryNodeString, entryNode);
		}
		String query = HttpUtil.createQueryResult(queryRes, esUrl);
		if (null != query) {
			listJson = HttpUtil.parseJsonSucc(query, gate.getApp_id());
		}
		return listJson;
	}

	/* 指定时间段平均访问时间 /api/v1/his_stat_avg_rsp_time */
	@Override
	public JSONObject searchAvgRespTime(String esUrl, GateWay gate, String queryString, String queryNodeString,
			String indexName, String entryNode) {
		JSONObject listJson = new JSONObject();
		esUrl = esUrl.replace("indexName", indexName);
		String queryRes = null;
		if (entryNode == null || "".equals(entryNode)) {
			// 转换为查询所有语句
			queryRes = dealReplace(gate, queryString, null);
		} else {
			// 查入口节点
			queryRes = dealReplace(gate, queryNodeString, entryNode);
		}
		String query = HttpUtil.createQueryResult(queryRes, esUrl);
		if (null != query) {
			listJson = HttpUtil.parseJsonAvg(query, gate.getApp_id());
		}
		return listJson;
	}

	/* 指定时间段错误节点，错误类别 /api/v1/his_stat_err_node_cate */
	@Override
	public List<JSONObject> searchErrorNodeCate(String esUrl, GateWay gate, String queryString, String indexName,
			Map<String, String> nodeMap) {
		// TODO Auto-generated method stub
		List<JSONObject> listJson = new ArrayList<JSONObject>();
		esUrl = esUrl.replace("indexName", indexName);
		String queryRes = dealReplaceError(gate, queryString);
		String query = HttpUtil.createQueryResult(queryRes, esUrl);
		if (null != query) {
			listJson = HttpUtil.parseJsonError(query, nodeMap);
		}
		return listJson;
	}

	// 将查询时间范围替换
	private String dealReplaceError(GateWay gate, String queryString) {
		String startTime = gate.getStart() + ".000+0800";
		String queryRes = null;
		String endTime = gate.getEnd() + ".000+0800";
		queryRes = queryString.replace("startTime", startTime).replace("endTime", endTime);
		return queryRes;
	}

	// 将查询时间范围替换
	private String dealReplace(GateWay gate, String queryString, String entryNode) {
		String startTime = gate.getStart() + ".000+0800";
		String queryRes = null;
		String endTime = gate.getEnd() + ".000+0800";
		Integer startPos = gate.getBeg_rec_no();
		Integer endPos = gate.getMax_rec_cnt();
		if (!queryString.contains("NodeNameStyle")) {
			// 所有节点
			if (endPos != null) {
					//如果传入了单次查询返回最大条数 则用传入值，反之返回200
					queryRes = queryString.replace("startTime", startTime).replace("endTime", endTime)
							.replace("fromType", startPos.toString()).replace("sizeType", endPos.toString());
			} else {
					queryRes = queryString.replace("startTime", startTime).replace("endTime", endTime)
							.replace("fromType", startPos.toString()).replace("sizeType", String.valueOf(200));
			}

		} else {
			// 入口节点
			if (endPos != null) {
					//如果传入了单次查询返回最大条数 则用传入值，反之返回200
					queryRes = queryString.replace("startTime", startTime).replace("endTime", endTime)
							.replace("fromType", startPos.toString()).replace("sizeType", endPos.toString())
							.replace("NodeNameStyle", entryNode);
			} else {
					queryRes = queryString.replace("startTime", startTime).replace("endTime", endTime)
							.replace("fromType", startPos.toString()).replace("sizeType", String.valueOf(200))
							.replace("NodeNameStyle", entryNode);
			}
		}
		return queryRes;
	}
}
