package com.tttrtcaudiogame.bean;

import java.io.Serializable;

/**
 * Created by wangzhiguo on 17/10/13.
 */

public class JniObjs implements Serializable {

    public int mJniType;
    public Object[] mObjs;

    public JniObjs(int mJniType, Object[] mObjs) {
        this.mJniType = mJniType;
        this.mObjs = mObjs;
    }
}
