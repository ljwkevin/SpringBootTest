package org.spring.springboot.domain;

public class GateWay {
	private String app_id;
	private String start;
	private Integer last;
	private String product;
	private String service;
	private String iP;
	private String client_key;
	private String end;
	private Integer beg_rec_no;
	private Integer max_rec_cnt;
	private Integer entry_node;
	public String getiP() {
		return iP;
	}
	public void setiP(String iP) {
		this.iP = iP;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public Integer getBeg_rec_no() {
		return beg_rec_no;
	}
	public void setBeg_rec_no(Integer beg_rec_no) {
		this.beg_rec_no = beg_rec_no;
	}
	public Integer getMax_rec_cnt() {
		return max_rec_cnt;
	}
	public void setMax_rec_cnt(Integer max_rec_cnt) {
		this.max_rec_cnt = max_rec_cnt;
	}
	public Integer getEntry_node() {
		return entry_node;
	}
	public void setEntry_node(Integer entry_node) {
		this.entry_node = entry_node;
	}
	public String getApp_id() {
		return app_id;
	}
	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public Integer getLast() {
		return last;
	}
	public void setLast(Integer last) {
		this.last = last;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getIP() {
		return iP;
	}
	public void setIP(String iP) {
		this.iP = iP;
	}
	public String getClient_key() {
		return client_key;
	}
	public void setClient_key(String client_key) {
		this.client_key = client_key;
	}
	
}
