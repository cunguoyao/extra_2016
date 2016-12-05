package com.keyun.pan.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cunguoyao on 2016/7/8.
 */
public class ApprovalDetail implements Serializable {

    private String requestID;//申请单ID；
    private String requestTitle;//申请标题；
    private int ruleModel;//0在线审批1离线审批
    private int ruleModeID;//审批方式ID（1：表示任一审批，2：表示逐级审批）
    private int ruleTypeID;//审批类型ID（1：表示外发审批）；
    private String requestTime;//申请时间；
    private String requestReason;//申请事由；
    private String userID;//申请者ID；
    private String userName;//申请者用户名；
    private String creator;//创建者离线审批创建者；
    private String creatorTime;//创建时间 "2014-10-29 13:36:58"
    private int approvalStatus;//申请单状态（0：表示审批中，1：表示批准，-1：表示拒绝）；
    private int currentState;//邮件外发状态（没有邮件外发时内容为空），(0:提交邮件审批,1:邮件审批通过, 2:邮件审批拒绝,  3:已发送任务邮件
            //4:已下载邮件内容 5:发送失败,等待下次重发 6:邮件发送失败,请检查邮件是否正确,
            //7:用户存储不在主服务器上,不能使用邮件外发,8:下载文件失败 9:解密文件失败 10:上传外发文件包失败 11:生成外失败 )；

    private String ApprovalTime;
    private String FinalApprover;//最终审批者
    private List<ApprovalFile> FileList;
    private String md5;//审批码/批准码

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getRequestTitle() {
        return requestTitle;
    }

    public void setRequestTitle(String requestTitle) {
        this.requestTitle = requestTitle;
    }

    public int getRuleModel() {
        return ruleModel;
    }

    public void setRuleModel(int ruleModel) {
        this.ruleModel = ruleModel;
    }

    public int getRuleTypeID() {
        return ruleTypeID;
    }

    public void setRuleTypeID(int ruleTypeID) {
        this.ruleTypeID = ruleTypeID;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getRequestReason() {
        return requestReason;
    }

    public void setRequestReason(String requestReason) {
        this.requestReason = requestReason;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public int getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(int approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getApprovalTime() {
        return ApprovalTime;
    }

    public void setApprovalTime(String approvalTime) {
        ApprovalTime = approvalTime;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public int getRuleModeID() {
        return ruleModeID;
    }

    public void setRuleModeID(int ruleModeID) {
        this.ruleModeID = ruleModeID;
    }

    public String getCreatorTime() {
        return creatorTime;
    }

    public void setCreatorTime(String creatorTime) {
        this.creatorTime = creatorTime;
    }

    public String getFinalApprover() {
        return FinalApprover;
    }

    public void setFinalApprover(String finalApprover) {
        FinalApprover = finalApprover;
    }

    public List<ApprovalFile> getFileList() {
        return FileList;
    }

    public void setFileList(List<ApprovalFile> fileList) {
        FileList = fileList;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public static ApprovalDetail parseJson(JSONObject json) {
        if (json == null) return null;
        ApprovalDetail approval = new ApprovalDetail();
        approval.setRequestID(json.optString("RequestID"));
        approval.setRequestTitle(json.optString("RequestTitle"));
        approval.setRuleModel(json.optInt("RuleModel"));
        approval.setRuleModeID(json.optInt("RuleModeID"));
        approval.setRuleTypeID(json.optInt("RuleTypeID"));
        approval.setCreatorTime(json.optString("CreatorTime"));
        approval.setRequestTime(json.optString("RequestTime"));
        approval.setRequestReason(json.optString("RequestReason"));
        approval.setUserID(json.optString("UserID"));
        approval.setUserName(json.optString("UserName"));
        approval.setCreator(json.optString("Creator"));
        approval.setApprovalStatus(json.optInt("ApprovalStatus"));
        approval.setCurrentState(json.optInt("currentState"));
        approval.setApprovalTime(json.optString("ApprovalTime"));
        approval.setFinalApprover(json.optString("FinalApprover"));
        approval.setMd5(json.optString("md5"));
        List<ApprovalFile> approvalFiles = ApprovalFile.parseJson(json.optJSONArray("FileList"));
        if(approvalFiles != null)
            approval.setFileList(approvalFiles);
        return approval;
    }

    public static List<ApprovalDetail> parseJson(JSONArray jsonArray) {
        if(jsonArray == null)return null;
        List<ApprovalDetail> list = new ArrayList<ApprovalDetail>();
        if(jsonArray.length() == 0)return list;
        for(int i=0;i<jsonArray.length();i++) {
            JSONObject json = jsonArray.optJSONObject(i);
            ApprovalDetail item = parseJson(json);
            if(item != null) {
                list.add(item);
            }
        }
        return list;
    }
}
