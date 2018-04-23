package com.tttrtcaudiogame.callback;

import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.tttrtcaudiogame.LocalConstans;
import com.tttrtcaudiogame.bean.JniObjs;
import com.tttrtcaudiogame.utils.MyLog;

public class PhoneListener extends PhoneStateListener {
    private Context mContext;

    public PhoneListener(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        try {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:   //来电
                    MyLog.d("来电话了 : " + incomingNumber);
                    Intent i = new Intent();
                    i.setAction(MyTTTRtcEngineEventHandler.TAG);
                    i.putExtra(MyTTTRtcEngineEventHandler.MSG_TAG,
                            new JniObjs(LocalConstans.CALL_BACK_ON_PHONE_LISTENER_COME, new Object[]{}));
                    mContext.sendBroadcast(i);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:  //挂掉电话
                    MyLog.d("挂掉电话 : " + incomingNumber);
                    Intent i1 = new Intent();
                    i1.setAction(MyTTTRtcEngineEventHandler.TAG);
                    i1.putExtra(MyTTTRtcEngineEventHandler.MSG_TAG,
                            new JniObjs(LocalConstans.CALL_BACK_ON_PHONE_LISTENER_IDLE, new Object[]{}));
                    mContext.sendBroadcast(i1);
                    break;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}