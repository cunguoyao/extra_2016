package com.keyun.pan.data;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by cunguoyao on 2016/11/7.
 */
@Table(name="push_message")
public class PushMessage implements Serializable {

    @Column(name = "id", isId = true)
    private long id;
    @Column(name = "type")
    private int type;//1、提交审批的推送 2、审批状态发生变化时的推送 3、资料库的权限发生变化时 4、文件共享时 （分享到资料库时通知资料库中的所有人）5、文件共享时（分享到用户）
    @Column(name = "title")
    private String title;
    @Column(name = "go_url")
    private String goUrl;
    @Column(name = "time")
    private String time;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGoUrl() {
        return goUrl;
    }

    public void setGoUrl(String goUrl) {
        this.goUrl = goUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
