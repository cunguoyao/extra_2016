package com.keyun.pan.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cunguoyao on 2016/7/8.
 */
public class Approval implements Serializable {

    public static final int TYPE_TODO_APPLY = 0;
    public static final int TYPE_FINISH_APPLY = 1;
    public static final int TYPE_TODO_REQ = 2;
    public static final int TYPE_FINISH_REQ = 3;

    private String requestID;//申请单ID；
    private String requestTitle;//申请标题；
    private int ruleModel;//提交方式（0：在线提交，1：离线提交，2：微信提交）；
    private int ruleTypeID;//审批类型ID（1：表示外发审批）；
    private String requestTime;//申请时间；
    private String requestReason;//申请事由；
    private String userID;//申请者ID；
    private String userName;//申请者用户名；
    private String creator;//创建者离线审批创建者；
    private int approvalStatus;//申请单状态（0：表示审批中，1：表示批准，-1：表示拒绝）；
    private int currentState;//邮件外发状态（没有邮件外发时内容为空），(0:提交邮件审批,1:邮件审批通过, 2:邮件审批拒绝,  3:已发送任务邮件
            //4:已下载邮件内容 5:发送失败,等待下次重发 6:邮件发送失败,请检查邮件是否正确,
            //7:用户存储不在主服务器上,不能使用邮件外发,8:下载文件失败 9:解密文件失败 10:上传外发文件包失败 11:生成外失败 )；


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

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public static Approval parseJson(JSONObject json) {
        if (json == null) return null;
        Approval approval = new Approval();
        approval.setRequestID(json.optString("RequestID"));
        approval.setRequestTitle(json.optString("RequestTitle"));
        approval.setRuleModel(json.optInt("RuleModel"));
        approval.setRuleTypeID(json.optInt("RuleTypeID"));
        approval.setRequestTime(json.optString("RequestTime"));
        approval.setRequestReason(json.optString("RequestReason"));
        approval.setUserID(json.optString("UserID"));
        approval.setUserName(json.optString("UserName"));
        approval.setCreator(json.optString("Creator"));
        approval.setApprovalStatus(json.optInt("ApprovalStatus"));
        approval.setCurrentState(json.optInt("currentState"));
        return approval;
    }

    public static List<Approval> parseJson(JSONArray jsonArray) {
        if(jsonArray == null)return null;
        List<Approval> list = new ArrayList<Approval>();
        if(jsonArray.length() == 0)return list;
        for(int i=0;i<jsonArray.length();i++) {
            JSONObject json = jsonArray.optJSONObject(i);
            Approval item = parseJson(json);
            if(item != null) {
                list.add(item);
            }
        }
        return list;
    }
}
