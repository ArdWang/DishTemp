package com.dt.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dt.R;

public class HeaderBar extends FrameLayout {

    private boolean isShowBack = true;

    private boolean isShowRight = false;

    private ImageView mLeftIv;

    private TextView mTitleTv;

    private TextView mRightTv;

    private ImageView mRightIv;

    //title文字
    private String titleText;
    //右侧文字
    private String rightText;

    private Integer rightImgView;

    public HeaderBar(Context context) {
        super(context);
    }

    public HeaderBar(Context context,AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HeaderBar);
        isShowBack = typedArray.getBoolean(R.styleable.HeaderBar_isShowBack,true);
        isShowRight = typedArray.getBoolean(R.styleable.HeaderBar_isShowRight,false);
        titleText = typedArray.getString(R.styleable.HeaderBar_titleText);
        rightText = typedArray.getString(R.styleable.HeaderBar_rightText);
        rightImgView = typedArray.getResourceId(R.styleable.HeaderBar_rightView,R.drawable.ble);
        initView(context);
        typedArray.recycle();
    }

    public HeaderBar(Context context,AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(final Context context){
        View view = View.inflate(context,R.layout.layout_header_bar,this);
        mLeftIv = view.findViewById(R.id.mLeftIv);
        mTitleTv = view.findViewById(R.id.mTitleTv);
        mRightTv = view.findViewById(R.id.mRightTv);
        mRightIv = view.findViewById(R.id.mRightIv);

        if(isShowBack){
            mLeftIv.setVisibility(View.VISIBLE);
        }else{
            mLeftIv.setVisibility(View.GONE);
        }

        if(isShowRight){
            mRightIv.setVisibility(View.VISIBLE);
        }else{
            mRightIv.setVisibility(View.GONE);
        }

        mRightIv.setImageResource(rightImgView);

        if(!titleText.isEmpty()){
            mTitleTv.setText(titleText);
        }

        if(rightText!=null) {
            if (!rightText.isEmpty()) {
                mRightTv.setText(rightText);
                mRightTv.setVisibility(View.VISIBLE);
            }
        }


        /*
         * 点击按钮关闭当前的Activity
         */
        mLeftIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否为同一个对象
                if(context instanceof Activity){
                    ((Activity)context).finish();
                }
            }
        });
    }


    /**
     * 获取左侧视图
     * @return n
     */
    public ImageView getLeftView(){
        return mLeftIv;
    }

    /**
     * 获取右侧视图
     * @return n
     */
    public TextView getRightView(){
        return mRightTv;
    }

    /**
     * 获取右侧的文字
     * @return n
     */
    public String getRightText(){
        return mRightTv.getText().toString();
    }

}
