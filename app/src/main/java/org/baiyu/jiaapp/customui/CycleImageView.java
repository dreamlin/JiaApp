package org.baiyu.jiaapp.customui;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.baiyu.jiaapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baiyu on 2015-9-25.
 * 轮播图控件
 */
public class CycleImageView extends LinearLayout {
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 图片轮播视图
     */
    private ViewPager mViewPager = null;
    /**
     * 滚动图片视图适配
     */
    private CycleImageAdapter mAdapter;
    /**
     * 图片轮播指示器控件
     */
    private ViewGroup mGroup;

    /**
     * 图片轮播指示个图
     */
    private ImageView mImageView = null;

    /**
     * 滚动图片指示视图列表
     */
    private ImageView[] mImageViews = null;

    /**
     * 手机密度
     */
    private float mScale;
    private boolean isStop;

    public CycleImageView(Context context) {
        super(context);
    }

    public CycleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mScale = context.getResources().getDisplayMetrics().density;
        LayoutInflater.from(context).inflate(R.layout.cycle_image_view, this);
        mViewPager = (ViewPager) findViewById(R.id.civ_viewpager);
        mViewPager.addOnPageChangeListener(new GuidePageChangeListener());
        mViewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        // 开始图片滚动
                        startImageTimerTask();
                        break;
                    default:
                        // 停止图片滚动
                        stopImageTimerTask();
                        break;
                }
                return false;
            }
        });
        // 滚动图片右下指示器视
        mGroup = (ViewGroup) findViewById(R.id.civ_group);
    }

    /**
     * 装填图片数据
     *
     * @param imageUrlList
     * @param CycleImageViewListener
     */
    public void setImageResources(List<String> imageUrlList, CycleImageViewListener CycleImageViewListener) {
        // 清除
        mGroup.removeAllViews();
        // 图片广告数量
        final int imageCount = imageUrlList.size();
        mImageViews = new ImageView[imageCount];
        for (int i = 0; i < imageCount; i++) {
            mImageView = new ImageView(mContext);
            int imageParams = (int) (mScale * 10 + 0.5f);// XP与DP转换，适应应不同分辨率
            int imagePadding = (int) (mScale * 5 + 0.5f);
            LayoutParams params = new LayoutParams(imageParams, imageParams);
            params.leftMargin = 30;
            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImageView.setLayoutParams(params);
            mImageView.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);

            mImageViews[i] = mImageView;
            if (i == 0) {
                mImageViews[i].setBackgroundResource(R.drawable.loop_check);
            } else {
                mImageViews[i].setBackgroundResource(R.drawable.loop_uncheck);
            }
            mGroup.addView(mImageViews[i]);
        }

        mAdapter = new CycleImageAdapter(mContext, imageUrlList, CycleImageViewListener);
        mViewPager.setAdapter(mAdapter);
        startImageTimerTask();
    }

    /**
     * 图片轮播(手动控制自动轮播与否，便于资源控件）
     */
    public void startImageCycle() {
        //下面这行代码用于实现界面刚打开时右划时出现最后一张图片
        mViewPager.setCurrentItem((10000 / mImageViews.length) * mImageViews.length);
        startImageTimerTask();
    }

    /**
     * 暂停轮播—用于节省资源
     */
    public void pushImageCycle() {
        stopImageTimerTask();
    }

    /**
     * 图片滚动任务
     */
    private void startImageTimerTask() {
        stopImageTimerTask();
        // 图片滚动
        mHandler.postDelayed(mImageTimerTask, 3000);
    }

    /**
     * 停止图片滚动任务
     */
    private void stopImageTimerTask() {
        isStop = true;
        mHandler.removeCallbacks(mImageTimerTask);
    }

    private Handler mHandler = new Handler();

    /**
     * 图片自动轮播Task
     */
    private Runnable mImageTimerTask = new Runnable() {
        @Override
        public void run() {
            if (mImageViews != null) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                if (!isStop) {  //if  isStop=true   //当你退出后 要把这个给停下来 不然 这个一直存在 就一直在后台循环
                    mHandler.postDelayed(mImageTimerTask, 3000);
                }
            }
        }
    };

    /**
     * 轮播图片监听
     *
     * @author minking
     */
    private final class GuidePageChangeListener implements ViewPager.OnPageChangeListener {

        //是否在自动切换中
        //boolean isAutoPlay = false;

        @Override
        public void onPageScrollStateChanged(int state) {

            switch (state) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                    // 手势滑动
                    //isAutoPlay = false;
                    break;
                case ViewPager.SCROLL_STATE_SETTLING:
                    // 手势未滑动
                    //isAutoPlay = true;
                    break;
                case ViewPager.SCROLL_STATE_IDLE:
                    //手势滑动结束
                    startImageTimerTask();
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        /**
         * @param index
         */
        @Override
        public void onPageSelected(int index) {
            //当前显示的图片位置
            index = index % mImageViews.length;
            // 设置图片滚动指示器背
            mImageViews[index].setBackgroundResource(R.drawable.loop_check);
            for (int i = 0; i < mImageViews.length; i++) {
                if (index != i) {
                    mImageViews[i].setBackgroundResource(R.drawable.loop_uncheck);
                }
            }
        }
    }

    private class CycleImageAdapter extends PagerAdapter {

        /**
         * 图片视图缓存列表
         */
        private List<ImageView> mImageViewCacheList;

        /**
         * 图片资源列表
         */
        private List<String> imageUrlList;

        /**
         * 广告图片点击监听
         */
        private CycleImageViewListener mCycleImageViewListener;

        private Context mContext;

        public CycleImageAdapter(Context context, List<String> imageUrlList, CycleImageViewListener cycleImageViewListener) {
            this.mContext = context;
            this.imageUrlList = imageUrlList;
            mCycleImageViewListener = cycleImageViewListener;
            mImageViewCacheList = new ArrayList<ImageView>();
        }

        @Override
        public int getCount() {
            //用于无限轮询
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            String imageUrl = imageUrlList.get(position % imageUrlList.size());
            ImageView imageView = null;
            if (mImageViewCacheList.isEmpty()) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = mImageViewCacheList.remove(0);
            }
            // 设置图片点击监听
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCycleImageViewListener.onImageClick(position % imageUrlList.size(), v);
                }
            });
            imageView.setTag(imageUrl);
            container.addView(imageView);
            mCycleImageViewListener.displayImage(imageUrl, imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView view = (ImageView) object;
            mViewPager.removeView(view);
            mImageViewCacheList.add(view);
        }
    }

    /**
     * 轮播控件的监听事件
     *
     * @author minking
     */
    public static interface CycleImageViewListener {
        /**
         * 加载图片资源
         *
         * @param imageURL
         * @param imageView
         */
        public void displayImage(String imageURL, ImageView imageView);

        /**
         * 单击图片事件
         *
         * @param position
         * @param imageView
         */
        public void onImageClick(int position, View imageView);
    }
}
