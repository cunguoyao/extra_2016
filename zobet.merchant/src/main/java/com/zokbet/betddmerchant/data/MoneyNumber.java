package com.zokbet.betddmerchant.data;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by cunguoyao on 2016/6/5.
 */
@Table(name="money_number")
public class MoneyNumber implements Serializable {

    @Column(name = "sid", isId = true)
    private String sid;
    @Column(name = "total_money")
    private String totalMoney;
    @Column(name = "cj_money")
    private String cjMoney;
    @Column(name = "cj_order")
    private String cjOrder;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(String totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getCjMoney() {
        return cjMoney;
    }

    public void setCjMoney(String cjMoney) {
        this.cjMoney = cjMoney;
    }

    public String getCjOrder() {
        return cjOrder;
    }

    public void setCjOrder(String cjOrder) {
        this.cjOrder = cjOrder;
    }
}
