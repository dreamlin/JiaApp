package org.baiyu.jiaapp.app;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.location.LocationClient;

import org.litepal.LitePalApplication;

/**
 * Created by baiyu on 2015-9-14.
 */
public class MyApplication extends LitePalApplication {
    public static RequestQueue queues;
    public LocationClient locationClient = null;

    @Override
    public void onCreate() {
        super.onCreate();
        queues = Volley.newRequestQueue(getApplicationContext());
        locationClient = new LocationClient(getApplicationContext());
    }

    public static RequestQueue getHttpQueues() {
        return queues;
    }
}
