package org.spring.springboot.domain;

public class TagDef {

	public static String STATUS = "status";
	public static String STATUS_SUCC = "success";
	public static String STATUS_ERROR = "error";
	
	public static String ERROR = "error";
	public static String RETURN_ERROR = "请求超时";
	public static String RETURN_NULL = "内部错误";
	public static String RETURN_ERROR_PARAMETER = "入参非法";
	
	public static String ERROR_TYPE = "error_type";
	public static String ERROR_DESCRIBE_DATE = "入参起止日期间隔超出设定的timespan值";
	public static String ERROR_DESCRIBE_APPID = "入参app_id为空";
	public static String ERROR_DESCRIBE_START = "入参起始时间为空";
	public static String ERROR_DESCRIBE_END = "入参结束时间为空";
	public static String ERROR_DESCRIBE_BEGCNT = "入参起始记录号为空";
	public static String ERROR_DESCRIBE_SERVICE = "入参服务为空";
	public static String ERROR_DESCRIBE_PRODUCT = "入参产品为空";
	public static String ERROR_DESCRIBE_STARTEND = "起始时间大于结束时间";
	public static String ERROR_DESCRIBE_FINDAPPID = "查询app_id不存在";
	public static String ERROR_DESCRIBE_FINDPRODUCT = "查询product不存在";
	public static String ERROR_DESCRIBE_FINDSERVICE = "查询service不存在";
	public static String ERROR_DESCRIBE_MAXRECCNT = "单次查询最大返回条数超出设定的max_rec_cnt值";
	public static String ERROR_DESCRIBE_STARTBIGENG = "开始时间大于结束时间";
	public static String ERROR_DESCRIBE_ESTIMEOUT = "查询ES超时";
	public static String ERROR_DESCRIBE_ESCLUSTER = "ES不稳定";
}
