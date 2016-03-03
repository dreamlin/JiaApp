package org.baiyu.jiaapp;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by baiyu on 2016-1-7.
 * 桌面时钟小组件定时器
 */
public class TimerService extends Service {

    private Timer timer;
    private SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateViews();
            }
        }, 0, 1000);
    }

    private void updateViews() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        if (dayOfWeek < 0)
            dayOfWeek = 0;

        String day = sdfDay.format(new Date()) + " " + weekDays[dayOfWeek];
        String time = sdfTime.format(new Date());
        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.widget);
        rv.setTextViewText(R.id.widget_tv_day, day);
        rv.setTextViewText(R.id.widget_tv_time, time);
        AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());
        ComponentName cn = new ComponentName(getApplicationContext(), WidgetProvider.class);
        manager.updateAppWidget(cn, rv);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer = null;
    }
}
