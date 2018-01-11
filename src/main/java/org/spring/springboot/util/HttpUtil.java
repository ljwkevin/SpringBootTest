package org.spring.springboot.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import kafka.utils.Json;

public class HttpUtil {

	private static Logger LOG = LoggerFactory.getLogger(HttpUtil.class);

	/* 创建查询语句并返回查询结果 */
	public static String createQueryResult(String queryString, String url) {
		String result = null;
//		System.out.println("queryURL:"+url);
//		System.out.println("queryString:"+queryString);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(url);
		try {
			// 服务器在接收和接受请求之后返回一个HTTP响应信息
			httppost.addHeader("Content-type", "application/json; charset=utf-8");
			httppost.setHeader("Accept", "application/json");
			httppost.setEntity(new StringEntity(queryString, Charset.forName("UTF-8")));
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				// 获取响应消息实体
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					result = EntityUtils.toString(entity, "UTF-8").trim();
					return result;
				}
				return null;
			} finally {
				response.close();
			}
		} catch (Exception e) {
			LOG.info("Error query");
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/* 对UV查询结果进行组装json格式 */
	public static JSONObject parseJsonUV(String queryResult, String app_id) {
		JSONArray resultArray = new JSONArray();
		JSONObject lastJson = new JSONObject();
		JSONObject hitJson = JSON.parseObject(queryResult);
		lastJson.put("total",hitJson.getJSONObject("hits").getInteger("total"));
		JSONArray hitArray = hitJson.getJSONObject("hits").getJSONArray("hits");
		if (!hitArray.isEmpty() && hitArray.size() > 0){
			for (int i=0; i< hitArray.size();i++){
				JSONObject hitsJson = hitArray.getJSONObject(i);
				JSONObject sourceJson = hitsJson.getJSONObject("_source");
				JSONObject resultJson = new JSONObject();
				resultJson.put("time",sourceJson.getString("timestamp"));
				resultJson.put("app_id", app_id);
				resultJson.put("node", sourceJson.getString("NodeName"));
				resultJson.put("value", sourceJson.getInteger("count"));
				resultJson.put("value2",0);
				resultArray.add(resultJson);
			}
			lastJson.put("data", resultArray);
		}else{
			lastJson.put("data", "[]");
		}
		return lastJson;
	}
	
	/* 对PV查询结果进行组装json格式 */
	public static JSONObject parseJsonPV(String queryResult, String app_id) {
		JSONArray resultArray = new JSONArray();
		JSONObject lastJson = new JSONObject();
		JSONObject hitJson = JSON.parseObject(queryResult);
		lastJson.put("total",hitJson.getJSONObject("hits").getInteger("total"));
		JSONArray hitArray = hitJson.getJSONObject("hits").getJSONArray("hits");
		if (!hitArray.isEmpty() && hitArray.size() > 0){
			for (int i=0; i< hitArray.size();i++){
				JSONObject hitsJson = hitArray.getJSONObject(i);
				JSONObject sourceJson = hitsJson.getJSONObject("_source");
				JSONObject resultJson = new JSONObject();
				resultJson.put("time",sourceJson.getString("timestamp"));
				resultJson.put("app_id", app_id);
				resultJson.put("node", sourceJson.getString("NodeName"));
				resultJson.put("value", sourceJson.getInteger("count"));
				resultArray.add(resultJson);
			}
			lastJson.put("data", resultArray);
		}else{
			lastJson.put("data", "[]");
		}
		return lastJson;
	}
	
	/* 对Succ查询结果进行组装json格式 */
	public static JSONObject parseJsonSucc(String queryResult, String app_id) {
		JSONArray resultArray = new JSONArray();
		JSONObject lastJson = new JSONObject();
		JSONObject hitJson = JSON.parseObject(queryResult);
		lastJson.put("total",hitJson.getJSONObject("hits").getInteger("total"));
		JSONArray hitArray = hitJson.getJSONObject("hits").getJSONArray("hits");
		if (!hitArray.isEmpty() && hitArray.size() > 0){
			for (int i=0; i< hitArray.size();i++){
				JSONObject hitsJson = hitArray.getJSONObject(i);
				JSONObject sourceJson = hitsJson.getJSONObject("_source");
				JSONObject resultJson = new JSONObject();
				resultJson.put("time",sourceJson.getString("timestamp"));
				resultJson.put("app_id", app_id);
				resultJson.put("node", sourceJson.getString("NodeName"));
				resultJson.put("value", sourceJson.getInteger("count"));
				resultArray.add(resultJson);
			}
			lastJson.put("data", resultArray);
		}else{
			lastJson.put("data", "[]");
		}
		return lastJson;
	}
	
	/* 对查询结果进行组装json格式 */
	public static JSONObject parseJsonAvg(String queryResult, String app_id) {
		JSONArray resultArray = new JSONArray();
		JSONObject lastJson = new JSONObject();
		JSONObject hitJson = JSON.parseObject(queryResult);
		lastJson.put("total",hitJson.getJSONObject("hits").getInteger("total"));
		JSONArray hitArray = hitJson.getJSONObject("hits").getJSONArray("hits");
		if (!hitArray.isEmpty() && hitArray.size() > 0){
			for (int i=0; i< hitArray.size();i++){
				JSONObject hitsJson = hitArray.getJSONObject(i);
				JSONObject sourceJson = hitsJson.getJSONObject("_source");
				JSONObject resultJson = new JSONObject();
				resultJson.put("time",sourceJson.getString("timestamp"));
				resultJson.put("app_id", app_id);
				resultJson.put("node", sourceJson.getString("NodeName"));
				resultJson.put("value", sourceJson.getInteger("avgResponseTime"));
				resultArray.add(resultJson);
			}
			lastJson.put("data", resultArray);
		}else{
			lastJson.put("data", "[]");
		}
		return lastJson;
	}

	/*
	 * 对错误节点查询结果进行组装json格式
	 * 
	 * @paramter:listJson存放所有查询ES的数据
	 * 
	 * @nodeMap: 存放节点映射关系
	 */
	public static List<JSONObject> parseJsonError(String listJson, Map<String, String> nodeMap) {
		// 最后返回的结果集
		List<JSONObject> lastJson = new ArrayList<JSONObject>();
		JSONObject result = JSON.parseObject(listJson);
		JSONObject aggrResult = result.getJSONObject("aggregations");
		if (aggrResult != null) {
			JSONObject dateResult = aggrResult.getJSONObject("time");
			if (dateResult != null) {
				JSONArray dateBucket = dateResult.getJSONArray("buckets");
				for (int j = 0; j < dateBucket.size(); j++) {
					JSONObject bucketJson = new JSONObject();
					JSONObject nodeJson = dateBucket.getJSONObject(j).getJSONObject("nodeName");
					JSONArray nodeArray = nodeJson.getJSONArray("buckets");
					JSONObject data = new JSONObject();
					/* 保存data中JSONObject的数据 */
					JSONArray dataArr = new JSONArray();
					/* 组装data中的内容 */
					data = dealData(nodeArray, nodeMap);
					if (!"{}".equals(data.toJSONString())){
						dataArr.add(data);
						bucketJson.put("offset", j);
						bucketJson.put("data", dataArr);
					}
					if (null != bucketJson && !"{}".equals(bucketJson.toJSONString())) {
						lastJson.add(bucketJson);
					}
				}
			}
		}
		return lastJson;
	}

	/* 组装错误节点中的data内容 */
	private static JSONObject dealData(JSONArray nodeArray, Map<String, String> nodeMap) {
		JSONObject res = new JSONObject();
		int[] value = new int[3];
		String[] proList = new String[]{};
		for (int i = 0; i < nodeArray.size(); i++) {
			JSONObject nodeObj = nodeArray.getJSONObject(i);
			for (Map.Entry<String, String> nodeEntry : nodeMap.entrySet()) {
				String node = nodeEntry.getKey();
				String proMapp = nodeEntry.getValue();
				if (node.equals(nodeObj.getString("key")) ) {
					JSONArray bucketArr = JSON.parseObject(nodeObj.getString("type")).getJSONArray("buckets");
					if (null != bucketArr && !"[]".equals(bucketArr.toJSONString())){
						for (int j=0;j<bucketArr.size();j++){
							JSONObject bucketObj = bucketArr.getJSONObject(j);
							if ("error".equals(bucketObj.getString("key"))){
								int errorValue = JSON.parseObject(bucketObj.getString("metric")).getInteger("value");
								value[0] = errorValue;
							}
							if("fail".equals(bucketObj.getString("key"))){
								int failValue = JSON.parseObject(bucketObj.getString("metric")).getInteger("value");
								value[1] = failValue;
							}
							if("timeout".equals(bucketObj.getString("key"))){
								int timeValue = JSON.parseObject(bucketObj.getString("metric")).getInteger("value");
								value[2] = timeValue;
							}
						}
						proList = proMapp.split("#");
						res.put("err_node", node);
						res.put("err_cate", value);
						res.put("biz_attr", proList);
					}
				}
			}
		}
		return res;
	}
}