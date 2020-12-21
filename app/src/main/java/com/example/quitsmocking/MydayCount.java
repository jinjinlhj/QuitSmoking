package com.example.quitsmocking;

import android.app.Application;

public class MydayCount extends Application {
    private int mMonCount, mTuseCount, mWendsCount, mThursCount, mFriCount,mSatCount,mSunCount;
    private long mDate;
    public int getmMonCount()
    {
        return mMonCount;
    }
    public void setmMonCount(int MonCount)
    {
        this.mMonCount = MonCount;
    }


    public int getmTuseCount()
    {
        return mTuseCount;
    }
    public void setmTuseCount(int TuseCount)
    {
        this.mTuseCount = TuseCount;
    }


    public int getmWendsCount()
    {
        return mWendsCount;
    }
    public void setmWendsCount(int WendsCount)
    {
        this.mWendsCount = WendsCount;
    }

    public int getmThursCount()
    {
        return mThursCount;
    }
    public void setmThursCount(int ThursCount)
    {
        this.mThursCount = ThursCount;
    }


    public int getmFriCount()
    {
        return mFriCount;
    }
    public void setmFriCount(int FriCount)
    {
        this.mFriCount = FriCount;
    }


    public int getmSatCount()
    {
        return mSatCount;
    }
    public void setmSatCount(int SatCount)
    {
        this.mSatCount = SatCount;
    }


    public int getmSunCount()
    {
        return mSunCount;
    }
    public void setmSunCount(int SunCount)
    {
        this.mSunCount = SunCount;
    }


    public long getmDate()
    {
        return mDate;
    }
    public void setmDate(long Date)
    {
        this.mDate = Date;
    }
}
