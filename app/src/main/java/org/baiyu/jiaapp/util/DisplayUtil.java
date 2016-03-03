package org.baiyu.jiaapp.util;

import android.content.Context;

/**
 * Created by baiyu on 2015-9-22.
 * dp与px互转
 */
public class DisplayUtil {
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
