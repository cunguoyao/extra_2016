package com.zokbet.betdd.dto;


import java.util.List;

import com.zokbet.betdd.widget.expandpop.KeyValueBean;

@SuppressWarnings("serial")
public class ConfigAreaDTO extends BaseDTO {
    private String key;
    private String value;
    private List<KeyValueBean> businessCircle;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<KeyValueBean> getBusinessCircle() {
        return businessCircle;
    }

    public void setBusinessCircle(List<KeyValueBean> businessCircle) {
        this.businessCircle = businessCircle;
    }

}
