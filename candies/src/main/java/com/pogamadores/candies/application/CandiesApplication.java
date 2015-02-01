package com.pogamadores.candies.application;

import android.app.Application;

import com.pogamadores.candies.util.Util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by igorcferreira on 2/1/15.
 */
public class CandiesApplication extends Application
{
    private Date lastNotificationDate;
    private static CandiesApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }

    public static CandiesApplication get() {
        return mApplication;
    }

    public Date getLastNotificationDate() {
        return lastNotificationDate;
    }

    public void setLastNotificationDate(Date lastNotificationDate) {
        this.lastNotificationDate = lastNotificationDate;
    }

    public boolean shouldNotificate() {

        if(lastNotificationDate == null) return true;

        Calendar calendar = Util.getDefaultIntervalCalendar(lastNotificationDate.getTime());
        return calendar.getTimeInMillis() >= System.currentTimeMillis();
    }
}
