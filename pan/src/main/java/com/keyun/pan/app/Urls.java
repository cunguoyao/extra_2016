package com.keyun.pan.app;

import android.content.Context;

import com.keyun.pan.utils.SharedPreferencesUtils;

/**
 * Created by cunguoyao on 2016/5/23.
 */
public class Urls {

    private Context context;

    public Urls(Context context) {
        this.context = context;
    }

    public static final String BASE_URL = "http://121.40.41.9";

    private String getBaseUrl() {
        String base_server = SharedPreferencesUtils.fetchByKey(context, "SERVER", "BASE_URL");
        String base_server_ip = SharedPreferencesUtils.fetchByKey(context, "SERVER", "BASE_URL_IP");
        if(base_server == null || "".equals(base_server) || !base_server.startsWith("http://")) {
            if(base_server_ip == null || "".equals(base_server_ip) || !base_server_ip.startsWith("http://")) {
                return BASE_URL;
            }else {
                return base_server_ip;
            }
        }else {
            return base_server;
        }
    }

    public String BASE() {
        return getBaseUrl() + "/CloudDisk/api/api.php";
    }
    public String BASE_C() {
        return getBaseUrl() + "/CloudDisk/capi/api.php";
    }
    public String BASE_RF() {
        return getBaseUrl() + "/redirect.php/API/File";
    }
    public String BASE_RR() {
        return getBaseUrl() + "/redirect.php/API/Repository";
    }
    public String BASE_M() {
        return getBaseUrl() + "/redirect.php/API/Member";
    }
    public String BASE_AP() {
        return getBaseUrl() + "/redirect.php/API/Approval";
    }
    public String BASE_S() {
        return getBaseUrl() + "/Storage/api/api.php";
    }
    public String BASE_A() {
        return getBaseUrl() + "/Approval/api/api.php";
    }

    public String version() {
        return BASE() + "/app/version";
    }
    public String accountInfo() {
        return BASE() + "?type=AccountInfo";
    }
    public String updateUserPwd() {
        return BASE() + "?type=UpdateUserPWD";
    }

    public String login() {
        return BASE_C() + "?ReqFun=UserLogon";
    }
    public String pushregister() {
        return getBaseUrl() + "/redirect.php/API/Login/pushregister";
    }
    public String logout() {
        return BASE_C() + "?ReqFun=UserLogoff";
    }
    public String getUserDownloadParam() {
        return BASE_C() + "?ReqFun=GetUserDownloadParam";
    }
    public String downloadFile() {
        return BASE_S() + "?ReqFun=DownloadFile";
    }
    public String checkFileExist() {
        return BASE_C() + "?ReqFun=CheckFileExist";
    }
    public String createFile() {
        return BASE_C() + "?ReqFun=CreateFile";
    }
    public String uploadFile() {
        return BASE_S() + "?ReqFun=UploadFile";//上传小文件
    }
    public String uploadFileChunks() {
        return BASE_S() + "?ReqFun=UploadFileChunks";//分块上传文件
    }

    public String listDir() {
        return BASE_RF() + "/listAll";
    }
    public String createDir() {
        return BASE_RF() + "/createDir";
    }
    public String updateMoveFileDir() {
        return BASE_RF() + "/updateMoveFileDir";
    }
    public String delete() {
        return BASE_RF() + "/delete";
    }
    public String searchFileDir() {
        return BASE_RF() + "/searchFileDir";
    }

    public String createRepository() {
        return BASE_RR() + "/CreateRepository";
    }
    public String updateRepository() {
        return BASE_RR() + "/UpdateRepository";
    }
    public String deleteRepository() {
        return BASE_RR() + "/DeleteRepository";
    }
    public String createRepositoryFile() {
        return BASE_RR() + "/CreateRepositoryFile";
    }
    public String moveRepositoryFile() {
        return BASE_RR() + "/MoveRepositoryFile";
    }
    public String deleteRepositoryFile() {
        return BASE_RR() + "/DeleteRepositoryFile";
    }
    public String listUserRepository() {
        return BASE_RR() + "/ListUserRepository";
    }
    public String listRepositoryFiles() {
        return BASE_RR() + "/ListRepositoryFiles";
    }
    public String downloadRepositoryFile() {
        return BASE_RR() + "/DownloadFile";
    }

    public String GetApprovalRequest() {
        return BASE_A() + "?type=GetApprovalRequest";
    }
    public String GetApprovalRequestInfo() {
        return BASE_A() + "?type=GetApprovalRequestInfo";
    }
    public String ApproverApprove() {
        return BASE_A() + "?type=ApproverApprove";
    }

    public String CreateCloudFileShareLink() {
        return BASE_C() + "?ReqFun=CreateCloudFileShareLink";
    }
    public String getDirsInRepositoryFiles() {
        return BASE_RR() + "/GetDirsInRepositoryFiles";
    }
    public String ShareFileIntoRepository() {
        return BASE_RR() + "/ShareFileIntoRepository";
    }
    public String GetCloudUserInfo() {
        return BASE_M() + "/GetCloudUserInfo";
    }
    public String CreateP2PShareFileLink() {
        return BASE_C() + "?ReqFun=CreateP2PShareFileLink";
    }
    public String RequestApproval() {
        return BASE_AP() + "/RequestApproval";
    }
}
