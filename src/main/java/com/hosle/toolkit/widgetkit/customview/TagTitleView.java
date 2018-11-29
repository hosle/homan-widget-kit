package com.hosle.toolkit.widgetkit.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hosle.toolkit.widgetkit.R;

/**
 * Created by tanjiahao on 2017/11/20
 * Original Project ho-widget-kit
 */

public class TagTitleView extends RelativeLayout {
    private RelativeLayout content;
    private TextView btnLeft;
    private TextView btnRight;
    private TextView tvTitle;
    private String titleContent;

    private LinearLayout subTitle;
    private TextView subTitleView;

    public TagTitleView(Context context) {
        super(context);
        initView(context);
    }

    public TagTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.cs_title_bar, this);
        btnLeft = (TextView) findViewById(R.id.btn_left);
        btnRight = (TextView) findViewById(R.id.btn_right);
        content = (RelativeLayout) findViewById(R.id.content);
        subTitle = findViewById(R.id.sub_title);
        initTitleBar();
    }

    private void initTitleBar() {
        tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(titleContent);
    }

    public void setBackgroundColorID(int colorID){
        content.setBackgroundColor(colorID);
    }


    public void setLeftDrawable(int drawableId) {
        btnLeft.setBackgroundResource(drawableId);
    }

    public void setTitleContent(String title) {
        tvTitle.setText(title);
    }

    public void setLeftOnClickListener(OnClickListener onClickListener) {
        btnLeft.setOnClickListener(onClickListener);
    }

    public void setTitleColor(int color){
        tvTitle.setTextColor(color);
    }

    public TextView getTitle() {
        return tvTitle;
    }

    public TextView getBtnLeft() {
        return btnLeft;
    }

    public void setRightDrawable(int drawableId) {
        btnRight.setBackgroundResource(drawableId);
    }

    public void setRightText(CharSequence text){
        btnRight.setText(text);
    }

    public void setRightTextColor(int color){
        btnRight.setTextColor(color);
    }

    public void setRightOnClickListener(OnClickListener listener){
        btnRight.setOnClickListener(listener);
    }

    public void addSubTitle(View view){
        subTitleView  = (TextView) view;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subTitle.removeAllViews();
        subTitle.addView(view,layoutParams);
    }

    public void removeSubTitle(){
        subTitle.removeAllViews();
    }

    public TextView getSubTitle(){
        return subTitleView;
    }
}
