package org.baiyu.jiaapp;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/**
 * Created by baiyu on 2016-1-7.
 * 桌面时钟小组件
 */
public class WidgetProvider extends AppWidgetProvider {
    /**
     * 组件被从屏幕移除
     *
     * @param context
     * @param appWidgetIds
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    /**
     * 最后一个组件被从屏幕移除
     *
     * @param context
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        context.stopService(new Intent(context, TimerService.class));
    }

    /**
     * 第一个组件添加到屏幕上执行
     *
     * @param context
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        context.startService(new Intent(context, TimerService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        //一般不会重写此方法
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        //刷新
        //RemoteView和AppWidgetManager进行更新操作时触发
    }
}
