package com.tttrtcaudiogame.callback;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tttrtcaudiogame.bean.JniObjs;
import com.wushuangtech.bean.RtcStats;
import com.wushuangtech.wstechapi.TTTRtcEngineEventHandler;

import java.util.ArrayList;
import java.util.List;

import static com.tttrtcaudiogame.LocalConstans.CALL_BACK_ON_AUDIO_VOLUME_INDICATION;
import static com.tttrtcaudiogame.LocalConstans.CALL_BACK_ON_ENTER_ROOM;
import static com.tttrtcaudiogame.LocalConstans.CALL_BACK_ON_ERROR;
import static com.tttrtcaudiogame.LocalConstans.CALL_BACK_ON_SEI;
import static com.tttrtcaudiogame.LocalConstans.CALL_BACK_ON_USER_JOIN;
import static com.tttrtcaudiogame.LocalConstans.CALL_BACK_ON_USER_MUTE_VIDEO;
import static com.tttrtcaudiogame.LocalConstans.CALL_BACK_ON_USER_OFFLINE;

/**
 * Created by wangzhiguo on 17/10/24.
 */

public class MyTTTRtcEngineEventHandler extends TTTRtcEngineEventHandler {

    public static final String TAG = "MyTTTRtcEngineEventHandler";
    public static final String MSG_TAG = "MyTTTRtcEngineEventHandlerMSG";
    private boolean mIsSaveCallBack;
    private List<JniObjs> mSaveCallBack;
    private Context mContext;

    public MyTTTRtcEngineEventHandler(Context mContext) {
        this.mContext = mContext;
        mSaveCallBack = new ArrayList<>();
    }

    @Override
    public void onJoinChannelSuccess(String channel, long uid) {
        Log.i("wzg", "onJoinChannelSuccess.... channel ： " + channel + " | uid : " + uid);
        sendMessage(new JniObjs(CALL_BACK_ON_ENTER_ROOM, new Object[]{channel, uid}));
        mIsSaveCallBack = true;
    }

    @Override
    public void onError(final int errorType) {
        Log.i("wzg", "onError.... errorType ： " + errorType + "mIsSaveCallBack : " + mIsSaveCallBack);
        if (mIsSaveCallBack) {
            saveCallBack(new JniObjs(CALL_BACK_ON_ERROR, new Object[]{errorType}));
        } else {
            sendMessage(new JniObjs(CALL_BACK_ON_ERROR, new Object[]{errorType}));
        }
    }

    @Override
    public void onUserJoined(long nUserId, int identity) {
        Log.i("wzg", "onUserJoined.... nUserId ： " + nUserId + " | identity : " + identity
                + " | mIsSaveCallBack : " + mIsSaveCallBack);
        if (mIsSaveCallBack) {
            saveCallBack(new JniObjs(CALL_BACK_ON_USER_JOIN, new Object[]{nUserId, identity}));
        } else {
            sendMessage(new JniObjs(CALL_BACK_ON_USER_JOIN, new Object[]{nUserId, identity}));
        }
    }

    @Override
    public void onUserOffline(long nUserId, int reason) {
        Log.i("wzg", "onUserOffline.... nUserId ： " + nUserId + " | reason : " + reason);
        if (mIsSaveCallBack) {
            saveCallBack(new JniObjs(CALL_BACK_ON_USER_OFFLINE, new Object[]{nUserId, reason}));
        } else {
            sendMessage(new JniObjs(CALL_BACK_ON_USER_OFFLINE, new Object[]{nUserId, reason}));
        }
    }

    @Override
    public void onConnectionLost() {
        Log.i("wzg", "onConnectionLost.... ");
        if (mIsSaveCallBack) {
            saveCallBack(new JniObjs(CALL_BACK_ON_ERROR, new Object[]{100}));
        } else {
            sendMessage(new JniObjs(CALL_BACK_ON_ERROR, new Object[]{100}));
        }
    }

    @Override
    public void onUserEnableVideo(long uid, boolean muted) {
        Log.i("wzg", "onUserMuteVideo.... uid : " + uid + " | mute : " + muted);
        if (mIsSaveCallBack) {
            saveCallBack(new JniObjs(CALL_BACK_ON_USER_MUTE_VIDEO, new Object[]{uid, muted}));
        } else {
            sendMessage(new JniObjs(CALL_BACK_ON_USER_MUTE_VIDEO, new Object[]{uid, muted}));
        }
    }

    @Override
    public void onAudioVolumeIndication(long nUserID, int audioLevel, int audioLevelFullRange) {
        if (mIsSaveCallBack) {
            saveCallBack(new JniObjs(CALL_BACK_ON_AUDIO_VOLUME_INDICATION, new Object[]{nUserID, audioLevel}));
        } else {
            sendMessage(new JniObjs(CALL_BACK_ON_AUDIO_VOLUME_INDICATION, new Object[]{nUserID, audioLevel}));
        }
    }

    @Override
    public void onAudioRouteChanged(int routing) {
        Log.i("wzg", "onAudioRouteChanged.... routing : " + routing);
    }

    @Override
    public void onLeaveChannel(RtcStats stats) {
        Log.i("wzg", "onLeaveChannel....");
    }

    @Override
    public void onFirstRemoteVideoFrame(long uid, int width, int height) {
        Log.i("wzg", "onFirstRemoteVideoFrame.... uid ： " + uid + " | width : " + width + " | height : " + height);
    }

    @Override
    public void onSetSEI(String sei) {
        Log.i("wzg", "onSei.... sei : " + sei);
        if (mIsSaveCallBack) {
            saveCallBack(new JniObjs(CALL_BACK_ON_SEI, new Object[]{sei}));
        } else {
            sendMessage(new JniObjs(CALL_BACK_ON_SEI, new Object[]{sei}));
        }
    }

    private synchronized void sendMessage(JniObjs mJniObjs) {
        Intent i = new Intent();
        i.setAction(TAG);
        i.putExtra(MSG_TAG, mJniObjs);
        mContext.sendBroadcast(i);
    }

    public synchronized void setIsSaveCallBack(boolean mIsSaveCallBack) {
        this.mIsSaveCallBack = mIsSaveCallBack;
        if (!mIsSaveCallBack) {
            for (int i = 0; i < mSaveCallBack.size(); i++) {
                sendMessage(mSaveCallBack.get(i));
            }
            mSaveCallBack.clear();
        }
    }

    private synchronized void saveCallBack(JniObjs mJniObjs) {
        if (mIsSaveCallBack) {
            mSaveCallBack.add(mJniObjs);
        }
    }
}
