package org.baiyu.jiaapp.util;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;

import org.baiyu.jiaapp.app.MyApplication;

/**
 * Created by baiyu on 2015-9-23.
 * 图片下载（队列）
 */
public class SingletonImageLoader {
    private volatile static SingletonImageLoader _MeObj;

    public static SingletonImageLoader getInstance() {
        if (_MeObj == null) {
            synchronized (SingletonImageLoader.class) {
                if (_MeObj == null) {
                    _MeObj = new SingletonImageLoader();
                }
            }
        }
        return _MeObj;
    }

    private ImageLoader imageLoader;

    private SingletonImageLoader() {
        imageLoader = new ImageLoader(MyApplication.getHttpQueues(), new BitmapCache());
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public void addImageListener(String url, ImageView view, int defaultImageResId, int errorImageResId) {
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(view, defaultImageResId, errorImageResId);
        imageLoader.get(url, listener);
    }

    private class BitmapCache implements ImageLoader.ImageCache {
        public LruCache<String, Bitmap> cache;
        // 缓存图片大小为10M，如果超出会自动回收
        public int max = 10 * 1024 * 1024;

        public BitmapCache() {
            cache = new LruCache<String, Bitmap>(max) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getRowBytes() * value.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return cache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            cache.put(url, bitmap);
        }
    }
}
