package com.keyun.pan.data;

import java.io.Serializable;

/**
 * Created by cunguoyao on 2016/9/23.
 */
public class FilterTypeFile implements Serializable {

    public String title;
    public String path;

    public FilterTypeFile(String title, String path) {
        this.title = title;
        this.path = path;
    }
}
