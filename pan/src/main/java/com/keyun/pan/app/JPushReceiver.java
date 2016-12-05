package com.keyun.pan.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.keyun.pan.activity.MyMessageActivity;
import com.keyun.pan.data.PushMessage;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;
/**
 * Created by cunguoyao on 2016/10/18.
 */
public class JPushReceiver extends BroadcastReceiver {

    private static final String TAG = "JPush";
    protected DbManager db;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
        PushMessage message = getTypeFromCustomMessage(bundle);

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
            /**Add for inserting data into database**/
            db = x.getDb(((BaseApplication)context.getApplicationContext()).getDaoConfig());
            if(message != null && message.getType() != 0) {
                try {
                    db.save(message);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

            //打开自定义的Activity
            if (message != null) {
                Intent i = new Intent(context, MyMessageActivity.class);
                if(message.getType() != 0) {
                    i.putExtra(MyMessageActivity.EXTRA_TYPE, message.getType());
                }
                i.putExtras(bundle);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
            }
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            }else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it =  json.keys();
                    sb.append("\n has EXTRA_EXTRA");
                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " +json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    private PushMessage getTypeFromCustomMessage(Bundle bundle) {
        PushMessage message = new PushMessage();
        int type = 0;
        if(bundle.containsKey(JPushInterface.EXTRA_EXTRA)) {
            try {
                JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                Iterator<String> it =  json.keys();
                while (it.hasNext()) {
                    String myKey = it.next().toString();
                    if("type".equals(myKey)) {
                        type = json.optInt(myKey);
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, "Get message extra JSON error!");
            }
        }
        message.setType(type);
        if(bundle.containsKey(JPushInterface.EXTRA_NOTIFICATION_TITLE)) {
            String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            message.setTitle(title);
        }
        if(bundle.containsKey(JPushInterface.EXTRA_ALERT)) {
            String content = bundle.getString(JPushInterface.EXTRA_ALERT);
            message.setTitle(content);
        }
        message.setGoUrl("");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        message.setTime(format.format(new Date()));
        return message;
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
        /*if (MainActivity.isForeground) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
            msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
            if (!ExampleUtil.isEmpty(extras)) {
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    if (null != extraJson && extraJson.length() > 0) {
                        msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
                    }
                } catch (JSONException e) {

                }

            }
            context.sendBroadcast(msgIntent);
        }*/
    }
}
