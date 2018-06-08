package com.tttrtcaudiogame.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tttrtcaudiogame.LocalConfig;
import com.tttrtcaudiogame.LocalConstans;
import com.tttrtcaudiogame.R;
import com.tttrtcaudiogame.bean.JniObjs;
import com.tttrtcaudiogame.callback.MyTTTRtcEngineEventHandler;
import com.tttrtcaudiogame.dialog.TestDialog;
import com.tttrtcaudiogame.helper.TTTRtcEngineHelper;
import com.tttrtcaudiogame.utils.MyLog;
import com.tttrtcaudiogame.utils.SharedPreferencesUtil;
import com.wushuangtech.jni.RoomJni;
import com.wushuangtech.library.Constants;
import com.wushuangtech.wstechapi.TTTRtcEngine;
import com.yanzhenjie.permission.AndPermission;

import java.io.File;
import java.io.IOException;
import java.util.Random;


public class SplashActivity extends BaseActivity {

    private Context mContext;
    private boolean mIsLoging;
    private EditText mRoomIDET;
    private TestDialog testDialog;
    private MyLocalBroadcastReceiver mLocalBroadcast;
    private TTTRtcEngineHelper mTTTRtcEngineHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        // 权限申请
        AndPermission.with(this)
                .permission(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                .start();

        mContext = this;
        mTTTRtcEngineHelper = new TTTRtcEngineHelper(this);
        initDatas();
        initBroadcast();
        // TODO 设置日志收集,发布时删除以下代码
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        String abs = externalStorageDirectory.toString() + "/TTTGameLog.txt";
        File file = new File(abs);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mTTTEngine.setLogFile(abs);
        MyLog.d("SplashActivity onCreate...");
    }

    private void initDatas() {
        mRoomIDET = findViewById(R.id.room_id);
        // 读取保存的数据
        long roomID = SharedPreferencesUtil.getLong(this, "RoomID", 0);
        // 设置保存的数据
        if (roomID != 0) {
            String s = String.valueOf(roomID);
            mRoomIDET.setText(s);
            mRoomIDET.setSelection(s.length());
        }

        TextView mVersion = findViewById(R.id.version);
        String string = getResources().getString(R.string.version_info);
        String result = String.format(string, TTTRtcEngine.getInstance().getVersion());
        mVersion.setText(result);
    }

    /**
     * Author: wangzg <br/>
     * Time: 2017-11-24 10:56:17<br/>
     * Description: 注册回调函数接收的广播.
     */
    private void initBroadcast() {
        mLocalBroadcast = new MyLocalBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyTTTRtcEngineEventHandler.TAG);
        registerReceiver(mLocalBroadcast, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyLog.d("SplashActivity onStart...");
        // 由于是按键发言，所以在进房间前，本地用户需要静音
        mTTTEngine.muteLocalAudioStream(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.d("SplashActivity onDestroy...");
        unregisterReceiver(mLocalBroadcast);
        TTTRtcEngine.destroy();
        System.exit(0);
    }

    public void onClickEnterButton(View v) {
        boolean checkResult = mTTTRtcEngineHelper.splashCheckSetting(mRoomIDET.getText().toString());
        if (!checkResult) {
            return;
        }

        // TODO 发布时删除以下代码
        if (!TextUtils.isEmpty(LocalConfig.mIP)) {
            Log.i("wzg", "setServerAddress ip : " + LocalConfig.mIP + " | port : " + LocalConfig.mPort);
            RoomJni.getInstance().setServerAddress(LocalConfig.mIP, LocalConfig.mPort);
        }

        Random mRandom = new Random();
        LocalConfig.mLoginUserID = mRandom.nextInt(999999);
        // 保存配置
        SharedPreferencesUtil.saveLong(this, "RoomID", LocalConfig.mLoginRoomID);
        if (mIsLoging) {
            return;
        }
        mIsLoging = true;
        mTTTRtcEngineHelper.splashShowWaittingDialog();
        new Thread(() -> {
            // 设置频道类型
            int results = mTTTEngine.setChannelProfile(Constants.CHANNEL_PROFILE_GAME_FREE_MODE);
            MyLog.d("setChannelProfile result : " + results);
            // 进入频道
            int result = mTTTEngine.joinChannel("", String.valueOf(LocalConfig.mLoginRoomID), LocalConfig.mLoginUserID);
            MyLog.d("joinChannel result : " + result
                    + " | roomID : " + String.valueOf(LocalConfig.mLoginRoomID) + " | user id : " + String.valueOf(LocalConfig.mLoginUserID));
        }).start();

    }


    private class MyLocalBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (MyTTTRtcEngineEventHandler.TAG.equals(action)) {
                JniObjs mJniObjs = (JniObjs) intent.getSerializableExtra(
                        MyTTTRtcEngineEventHandler.MSG_TAG);
                Object[] mObjs = mJniObjs.mObjs;
                switch (mJniObjs.mJniType) {
                    case LocalConstans.CALL_BACK_ON_ENTER_ROOM:
                        mTTTRtcEngineHelper.splashDismissWaittingDialog();
                        mIsLoging = false;
                        //界面跳转
                        startActivity(new Intent(mContext, MainActivity.class));
                        break;
                    case LocalConstans.CALL_BACK_ON_ERROR:
                        mIsLoging = false;
                        mTTTRtcEngineHelper.splashDismissWaittingDialog();
                        final int errorType = (int) mObjs[0];
                        runOnUiThread(() -> {
                            if (errorType == Constants.ERROR_ENTER_ROOM_TIMEOUT) {
                                Toast.makeText(mContext, "超时，10秒未收到服务器返回结果", Toast.LENGTH_SHORT).show();
                            } else if (errorType == Constants.ERROR_ENTER_ROOM_FAILED) {
                                Toast.makeText(mContext, "无法连接服务器", Toast.LENGTH_SHORT).show();
                            } else if (errorType == Constants.ERROR_ENTER_ROOM_VERIFY_FAILED) {
                                Toast.makeText(mContext, "验证码错误", Toast.LENGTH_SHORT).show();
                            } else if (errorType == Constants.ERROR_ENTER_ROOM_BAD_VERSION) {
                                Toast.makeText(mContext, "版本错误", Toast.LENGTH_SHORT).show();
                            } else if (errorType == Constants.ERROR_ENTER_ROOM_UNKNOW) {
                                Toast.makeText(mContext, "该房间不存在", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }
            }
        }
    }

    /**
     * Author: wangzg <br/>
     * Time: 2017-11-24 10:17:28<br/>
     * Description: 测试用，不用关注该函数
     */
    public void onTestButtonClick(View v) {
        if (testDialog == null) {
            testDialog = new TestDialog(this);
            testDialog.setCanceledOnTouchOutside(false);
        } else {
            testDialog.setServerParams();
        }
        testDialog.show();
    }
}
