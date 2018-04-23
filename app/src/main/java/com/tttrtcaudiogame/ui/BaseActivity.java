package com.tttrtcaudiogame.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import com.jaeger.library.StatusBarUtil;
import com.wushuangtech.wstechapi.TTTRtcEngineForGamming;

/**
 * Created by wangzhiguo on 17/10/12.
 */

public class BaseActivity extends Activity {

    protected TTTRtcEngineForGamming mTTTEngine;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        StatusBarUtil.setTranslucent(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mTTTEngine = TTTRtcEngineForGamming.getInstance();
    }
}
