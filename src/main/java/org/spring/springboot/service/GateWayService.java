
package org.spring.springboot.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spring.springboot.domain.GateWay;

import com.alibaba.fastjson.JSONObject;

/* 定义接口查询*/
public interface GateWayService {

	
   
	
    /*指定时间段在线用户数    /api/v1/his_stat_uv	 */
    
	JSONObject searchOnlineUVCount(String url,GateWay gate,String queryString,String queryNodeString,String indexName,String entryNode);
	
	/*指定时间段访问量变化数     /api/v1/his_stat_pv  */
	
	JSONObject searchOnlinePVCount(String url,GateWay gate,String queryString,String queryNodeString,String indexName,String entryNode);
	
	/*指定时间段访问成功率  /api/v1/his_stat_suc_ratio	 */
	
	JSONObject searchAccessSuccRatio(String url,GateWay gate,String queryString,String queryNodeString,String indexName,String entryNode);
	
	/*指定时间段平均访问时间   /api/v1/his_stat_avg_rsp_time */
	
	JSONObject searchAvgRespTime(String url,GateWay gate,String queryString,String queryNodeString,String indexName,String entryNode);
	
	/*指定时间段错误节点，错误类别  /api/v1/his_stat_err_node_cate	  */
	
	List<JSONObject> searchErrorNodeCate(String url,GateWay gate,String queryString,String indexName,Map<String,String> nodeMap);
	
}