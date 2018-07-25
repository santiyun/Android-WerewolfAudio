package com.tttrtcaudiogame;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;
import com.tttrtcaudiogame.callback.MyTTTRtcEngineEventHandler;
import com.wushuangtech.wstechapi.TTTRtcEngineForGamming;

public class MainApplication extends Application {

    public MyTTTRtcEngineEventHandler mMyTTTRtcEngineEventHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        //1.初始化SDK
        TTTRtcEngineForGamming mTTTEngine = TTTRtcEngineForGamming.create(getApplicationContext(), "a967ac491e3acf92eed5e1b5ba641ab7", null);
        if (mTTTEngine == null) {
            System.exit(0);
        }
//        //2.设置SDK的回调接收类
        mMyTTTRtcEngineEventHandler = new MyTTTRtcEngineEventHandler(getApplicationContext());
        mTTTEngine.setTTTRtcEngineForGammingEventHandler(mMyTTTRtcEngineEventHandler);

        CrashReport.initCrashReport(getApplicationContext(), "7b29a7f16b", true);
    }
}
