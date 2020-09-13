package com.example.paperdemo.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.paperdemo.R;
import com.ikangtai.cardpapersdk.util.PxDxUtil;

/**
 * desc
 *
 * @author xiongyl 2019/11/29 17:09
 */
public class TopBar extends RelativeLayout {
    /**
     * 最左侧按钮
     */
    private AlphaButton mLeftButton;
    private LinearLayout mLinearLayout;
    /**
     * 中间文本左侧按钮
     */
    private AlphaButton mMidLeftButton;
    /**
     * 中间文本
     */
    private TextView mMidTextView;
    /**
     * 中间文本右侧按钮
     */
    private AlphaButton mMidRightButton;
    /**
     * 最右侧按钮
     */
    private AlphaButton mRightButton;

    /**
     * 最右侧倒数第二个
     */
    private AlphaButton mRightSecondButton;

    private OnTopBarClickListener mListener;

    public TopBar(Context context) {
        super(context);
    }

    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        float density = context.getResources().getDisplayMetrics().density;
        setLeft(context, attrs, density);
        setMiddle(context, attrs, density);
        setRight(context, attrs, density);
    }

    private void setLeft(Context context, AttributeSet attrs, float density) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopBar);

        float leftBtnWidth = (ta.getDimension(R.styleable.TopBar_leftBtnWidth, 30)) / density;
        float leftBtnHeight = (ta.getDimension(R.styleable.TopBar_leftBtnHeight, 30)) / density;
        Drawable leftBtnBg = ta.getDrawable(R.styleable.TopBar_leftBtnBg);
        CharSequence leftBtnText = ta.getText(R.styleable.TopBar_leftBtnText);
        int leftTextColor = ta.getColor(R.styleable.TopBar_leftTextColor, 0);
        float leftBtnTextSize = (ta.getDimension(R.styleable.TopBar_leftBtnTextSize, 20)) / density;
        boolean leftBtnVisible = ta.getBoolean(R.styleable.TopBar_leftBtnVisible, true);

        ta.recycle();

        mLeftButton = new AlphaButton(context);
        mLeftButton.setBackground(leftBtnBg);
        mLeftButton.setText(leftBtnText);
        mLeftButton.setTextColor(leftTextColor);
        mLeftButton.setTextSize(leftBtnTextSize);
        mLeftButton.setVisibility(leftBtnVisible ? VISIBLE : INVISIBLE);

        int leftBtnWidthParam = PxDxUtil.dip2px(getContext(), leftBtnWidth);
        int leftBtnHeightParam = PxDxUtil.dip2px(getContext(), leftBtnHeight);
        LayoutParams mLeftParams = new LayoutParams(leftBtnWidthParam, leftBtnHeightParam);
        mLeftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        mLeftParams.addRule(RelativeLayout.CENTER_VERTICAL);
        addView(mLeftButton, mLeftParams);

        mLeftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.leftClick();
                }
            }
        });
    }


    public void setLeftDrawable(int resId, int w, int h) {
        if (mLeftButton != null) {
            w = PxDxUtil.dip2px(getContext(), w);
            h = PxDxUtil.dip2px(getContext(), h);
            mLeftButton.setBackgroundResource(resId);
            RelativeLayout.LayoutParams params = new LayoutParams(w, h);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.setMargins(16, 0, 0, 0);
            mLeftButton.setLayoutParams(params);
        }
    }

    public void setRightDrawable(int resId, int w, int h) {
        if (mRightButton != null) {
            w = PxDxUtil.dip2px(getContext(), w);
            h = PxDxUtil.dip2px(getContext(), h);
            mRightButton.setBackgroundResource(resId);
            RelativeLayout.LayoutParams params = new LayoutParams(w, h);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(0, 0, 16, 0);
            mRightButton.setLayoutParams(params);
        }
    }


    private void setMiddle(Context context, AttributeSet attrs, float density) {
        mLinearLayout = new LinearLayout(context);
        LayoutParams mLinearLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mLinearLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        mLinearLayout.setGravity(Gravity.CENTER);
        mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        addView(mLinearLayout, mLinearLayoutParams);

        addMidLeftButton(context, attrs, density);
        addMidTextView(context, attrs, density);
        addMidRightButton(context, attrs, density);
    }

    private void addMidLeftButton(Context context, AttributeSet attrs, float density) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopBar);

        Drawable midLeftBtnBg = ta.getDrawable(R.styleable.TopBar_midLeftBtnBg);
        float midLeftBtnWidth = (ta.getDimension(R.styleable.TopBar_midLeftBtnWidth, 30)) / density;
        float midLeftBtnHeight = (ta.getDimension(R.styleable.TopBar_midLeftBtnHeight, 30)) / density;
        boolean midLeftBtnVisible = ta.getBoolean(R.styleable.TopBar_midLeftBtnVisible, true);

        ta.recycle();

        mMidLeftButton = new AlphaButton(context);
        mMidLeftButton.setBackground(midLeftBtnBg);
        mMidLeftButton.setVisibility(midLeftBtnVisible ? VISIBLE : INVISIBLE);

        int midLeftBtnWidthParam = PxDxUtil.dip2px(getContext(), midLeftBtnWidth);
        int midLeftBtnHeightParam = PxDxUtil.dip2px(getContext(), midLeftBtnHeight);
        LayoutParams mMidLeftParams = new LayoutParams(midLeftBtnWidthParam, midLeftBtnHeightParam);
        mLinearLayout.addView(mMidLeftButton, mMidLeftParams);

        mMidLeftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.midLeftClick();
                }
            }
        });
    }

    public void setMidTextColor(int color) {
        mMidTextView.setTextColor(color);
    }

    public TextView getmMidTextView() {
        return mMidTextView;
    }

    private void addMidTextView(Context context, AttributeSet attrs, float density) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopBar);

        CharSequence midText = ta.getText(R.styleable.TopBar_midText);
        int midTextColor = ta.getColor(R.styleable.TopBar_midTextColor, 0);
        float midTextSize = (ta.getDimension(R.styleable.TopBar_midTextSize, 5)) / density;

        ta.recycle();

        mMidTextView = new TextView(context);
        mMidTextView.setText(midText);
        mMidTextView.setTextColor(midTextColor);
        mMidTextView.setTextSize(midTextSize);
        mMidTextView.setMaxEms(13);
        mMidTextView.setMaxLines(1);
        mMidTextView.setEllipsize(TextUtils.TruncateAt.END);

        MarginLayoutParams mMidParams = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mMidParams.leftMargin = PxDxUtil.dip2px(getContext(), 10);
        mMidParams.rightMargin = PxDxUtil.dip2px(getContext(), 10);
        mMidTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        mLinearLayout.addView(mMidTextView, mMidParams);
    }

    private void addMidRightButton(Context context, AttributeSet attrs, float density) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopBar);

        Drawable midRightBtnBg = ta.getDrawable(R.styleable.TopBar_midRightBtnBg);
        float midRightBtnWidth = (ta.getDimension(R.styleable.TopBar_midRightBtnWidth, 30)) / density;
        float midRightBtnHeight = (ta.getDimension(R.styleable.TopBar_midRightBtnHeight, 30)) / density;
        boolean midRightBtnVisible = ta.getBoolean(R.styleable.TopBar_midRightBtnVisible, true);

        ta.recycle();

        mMidRightButton = new AlphaButton(context);
        mMidRightButton.setBackground(midRightBtnBg);
        mMidRightButton.setVisibility(midRightBtnVisible ? VISIBLE : INVISIBLE);

        int midRightBtnWidthParam = PxDxUtil.dip2px(getContext(), midRightBtnWidth);
        int midRightBtnHeightParam = PxDxUtil.dip2px(getContext(), midRightBtnHeight);
        LayoutParams mRightParams = new LayoutParams(midRightBtnWidthParam, midRightBtnHeightParam);
        mLinearLayout.addView(mMidRightButton, mRightParams);

        mMidRightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.midRightClick();
                }
            }
        });
    }

    private void setRight(Context context, AttributeSet attrs, float density) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopBar);
        float rightBtnWidth = (ta.getDimension(R.styleable.TopBar_rightBtnWidth, 30)) / density;
        float rightBtnHeight = (ta.getDimension(R.styleable.TopBar_rightBtnHeight, 30)) / density;
        Drawable rightBtnBg = ta.getDrawable(R.styleable.TopBar_rightBtnBg);
        CharSequence rightBtnText = ta.getText(R.styleable.TopBar_rightBtnText);
        int rightBtnTextColor = ta.getColor(R.styleable.TopBar_rightBtnTextColor, 0);
        float rightBtnTextSize = (ta.getDimension(R.styleable.TopBar_rightBtnTextSize, 20)) / density;
        boolean rightBtnVisible = ta.getBoolean(R.styleable.TopBar_rightBtnVisible, true);


        mRightButton = new AlphaButton(context);
        mRightButton.setBackground(rightBtnBg);
        mRightButton.setText(rightBtnText);
        mRightButton.setAllCaps(false);
        mRightButton.setTextColor(rightBtnTextColor);
        mRightButton.setTextSize(rightBtnTextSize);
        mRightButton.setMaxLines(1);
        mRightButton.setVisibility(rightBtnVisible ? VISIBLE : INVISIBLE);
        mRightButton.setGravity(Gravity.CENTER);
        mRightButton.setPadding(0,0,0,0);

        int rightBtnWidthParam = PxDxUtil.dip2px(getContext(), rightBtnWidth);
        int rightBtnHeightParam = PxDxUtil.dip2px(getContext(), rightBtnHeight);
        final LayoutParams mRightParams = new LayoutParams(rightBtnWidthParam, rightBtnHeightParam);
        mRightParams.rightMargin = PxDxUtil.dip2px(getContext(), 5);
        mRightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mRightParams.addRule(RelativeLayout.CENTER_VERTICAL);
        addView(mRightButton, mRightParams);
        mRightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.rightClick();
                }
            }
        });

        float rightSecondBtnWidth = (ta.getDimension(R.styleable.TopBar_rightSecondBtnWidth, 30)) / density;
        float rightSecondBtnHeight = (ta.getDimension(R.styleable.TopBar_rightSecondBtnHeight, 30)) / density;
        Drawable rightSecondBtnBg = ta.getDrawable(R.styleable.TopBar_rightSecondBtnBg);
        boolean rightSecondBtnVisible = ta.getBoolean(R.styleable.TopBar_rightSecondBtnVisible, true);
        mRightSecondButton = new AlphaButton(context);
        mRightSecondButton.setBackground(rightSecondBtnBg);
        mRightSecondButton.setVisibility(rightSecondBtnVisible ? VISIBLE : INVISIBLE);
        int rightSecondBtnWidthParam = PxDxUtil.dip2px(getContext(), rightSecondBtnWidth);
        int rightSecondBtnHeightParam = PxDxUtil.dip2px(getContext(), rightSecondBtnHeight);
        LayoutParams mRightSecondBtnParams = new LayoutParams(rightSecondBtnWidthParam, rightSecondBtnHeightParam);
        mRightSecondBtnParams.rightMargin = PxDxUtil.dip2px(getContext(), rightBtnWidth + 10);
        mRightSecondBtnParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mRightSecondBtnParams.addRule(RelativeLayout.CENTER_VERTICAL);

        addView(mRightSecondButton, mRightSecondBtnParams);
        mRightSecondButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null && mListener instanceof IEvent) {
                    IEvent event = (IEvent) mListener;
                    event.rightSecondBtnClick();
                }

            }
        });

        ta.recycle();

    }

    public interface OnTopBarClickListener {
        void leftClick();

        void midLeftClick();

        void midRightClick();

        void rightClick();
    }


    public interface IEvent extends OnTopBarClickListener {
        void rightSecondBtnClick();
    }


    public void setOnTopBarClickListener(OnTopBarClickListener mListener) {
        this.mListener = mListener;
    }


    public void setLeftButtonVisible(boolean visible) {
        mLeftButton.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public void setRightButtonVisible(boolean visible) {
        mRightButton.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }


    public void setRightSecondButtonVisible(boolean visible) {
        mRightSecondButton.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public void setText(String text) {
        mMidTextView.setText(text);
    }

    public void setText(SpannableString text) {
        mMidTextView.setText(text);
    }

    public String getText() {
        return mMidTextView.getText().toString();
    }
}

