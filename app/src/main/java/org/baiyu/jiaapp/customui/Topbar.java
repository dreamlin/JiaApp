package org.baiyu.jiaapp.customui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.baiyu.jiaapp.R;
import org.baiyu.jiaapp.util.DisplayUtil;


/**
 * Created by baiyu on 2015-9-22.
 */
public class Topbar extends RelativeLayout {

    private ImageView leftImageView;//左边按钮
    private ImageView rightImageView;//右边按钮
    private TextView tvTitle;//标题
    private View lineView;//头底部的细线

    private Drawable leftBackground;//左边按钮图片
    private Drawable rightBackground;//右边按钮图片

    private float titleTextSize;//标题字体大小
    private int titleTextColor;//标题字体颜色
    private String titleText;//标题内容

    private int backgroundColor;//头的背景颜色
    private int lineColor;//细线的颜色

    private LayoutParams leftParams, rightParams, titleParams, lineParams;

    private TopbarClickListener listener;

    public interface TopbarClickListener {
        public void leftClick();

        public void rightClick();
    }

    public void setOnTopbarClickListener(TopbarClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NewApi")
    public Topbar(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Topbar);
        leftBackground = ta.getDrawable(R.styleable.Topbar_tbLeftBackground);
        rightBackground = ta.getDrawable(R.styleable.Topbar_tbRightBackground);

        titleTextSize = ta.getDimension(R.styleable.Topbar_tbTitleTextSize, 0);
        titleTextColor = ta.getColor(R.styleable.Topbar_tbTitleTextColor, 0);
        titleText = ta.getString(R.styleable.Topbar_tbTitleText);

        backgroundColor = ta.getColor(R.styleable.Topbar_tbBackgroundColor, 0);
        lineColor = ta.getColor(R.styleable.Topbar_tbLineColor, 0);

        ta.recycle();

        leftImageView = new ImageView(context);
        rightImageView = new ImageView(context);
        tvTitle = new TextView(context);

        if (leftBackground != null)
            leftImageView.setBackground(leftBackground);
        else
            setLeftIsVisable(false);

        if (rightBackground != null)
            rightImageView.setBackground(rightBackground);
        else
            setRightIsVisable(false);

        tvTitle.setTextSize(titleTextSize);
        if (titleTextColor != 0)
            tvTitle.setTextColor(titleTextColor);
        tvTitle.setText(titleText);
        tvTitle.setGravity(Gravity.CENTER);

        if (backgroundColor != 0)
            setBackgroundColor(backgroundColor);

        leftParams = new LayoutParams(DisplayUtil.dp2px(context, 40), DisplayUtil.dp2px(context, 40));
        leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        leftParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        leftParams.setMargins(DisplayUtil.dp2px(context, 10), 0, 0, 0);
        addView(leftImageView, leftParams);

        rightParams = new LayoutParams(DisplayUtil.dp2px(context, 40), DisplayUtil.dp2px(context, 40));
        rightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        rightParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        rightParams.setMargins(0, 0, DisplayUtil.dp2px(context, 10), 0);

        addView(rightImageView, rightParams);

        titleParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        titleParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        addView(tvTitle, titleParams);

        lineParams = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
        lineParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

        lineView = new View(context);
        if (lineColor != 0)
            lineView.setBackgroundColor(lineColor);

        addView(lineView, lineParams);

        leftImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                listener.leftClick();
            }
        });

        rightImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                listener.rightClick();
            }
        });
    }

    public void setLeftIsVisable(boolean flag) {
        if (flag) {
            leftImageView.setVisibility(View.VISIBLE);
        } else {
            leftImageView.setVisibility(View.GONE);
        }
    }

    public void setRightIsVisable(boolean flag) {
        if (flag) {
            rightImageView.setVisibility(View.VISIBLE);
        } else {
            rightImageView.setVisibility(View.GONE);
        }
    }
}