package com.zokbet.betdd.dto;


import java.util.List;

import com.zokbet.betdd.widget.expandpop.KeyValueBean;

public class ConfigListDTO extends BaseDTO {
	private List<KeyValueBean> info;

	public List<KeyValueBean> getInfo() {
		return info;
	}

	public void setInfo(List<KeyValueBean> info) {
		this.info = info;
	}
}
