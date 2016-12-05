package com.keyun.pan.data;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cunguoyao on 2016/7/10.
 */
@Table(name="repository")
public class Repository extends RespFile implements Serializable {

    private boolean checkboxVisualable;
    private boolean checked;

    @Column(name = "repository_id", isId = true)
    private String repositoryID;
    @Column(name = "authority")
    private int authority;
    @Column(name = "name")
    private String name;
    @Column(name = "max_space")
    private long maxSpace;
    @Column(name = "used_space")
    private long usedSpace;
    @Column(name = "creator")
    private String creatorName;
    @Column(name = "create_time")
    private String createTime;
    @Column(name = "description")
    private String description;
    @Column(name = "file_count")
    private int fileCount;
    @Column(name = "is_delete")
    private int isDeleted;
    @Column(name = "is_update")
    private int isUpdate;

    @Column(name = "user_name")
    private String userName;

    public boolean isCheckboxVisualable() {
        return checkboxVisualable;
    }

    public void setCheckboxVisualable(boolean checkboxVisualable) {
        this.checkboxVisualable = checkboxVisualable;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getRepositoryID() {
        return repositoryID;
    }

    public void setRepositoryID(String repositoryID) {
        this.repositoryID = repositoryID;
    }

    public int getAuthority() {
        return authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMaxSpace() {
        return maxSpace;
    }

    public void setMaxSpace(long maxSpace) {
        this.maxSpace = maxSpace;
    }

    public long getUsedSpace() {
        return usedSpace;
    }

    public void setUsedSpace(long usedSpace) {
        this.usedSpace = usedSpace;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public int getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(int isUpdate) {
        this.isUpdate = isUpdate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public static Repository parseJson(JSONObject json) {
        if (json == null) return null;
        Repository info = new Repository();
        info.setRepositoryID(json.optString("RepositoryID"));
        info.setName(json.optString("Name"));
        info.setAuthority(json.optInt("Authority"));
        info.setCreateTime(json.optString("CreateTime"));
        info.setCreatorName(json.optString("CreatorName"));
        info.setDescription(json.optString("Description"));
        info.setFileCount(json.optInt("FileCount"));
        info.setIsUpdate(json.optInt("IsUpdate"));
        info.setIsDeleted(json.optInt("IsDeleted"));
        info.setMaxSpace(json.optLong("MaxSpace"));
        info.setUsedSpace(json.optLong("UsedSpace"));
        return info;
    }

    public static List<Repository> parseJson(JSONArray jsonArray) {
        if(jsonArray == null)return null;
        List<Repository> list = new ArrayList<Repository>();
        if(jsonArray.length() == 0)return list;
        for(int i=0;i<jsonArray.length();i++) {
            JSONObject json = jsonArray.optJSONObject(i);
            Repository fileItem = parseJson(json);
            if(fileItem != null) {
                list.add(fileItem);
            }
        }
        return list;
    }
}
