package com.example.paperdemo.view;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * desc
 *
 * @author xiongyl 2019/12/11 15:36
 */
public class AlphaImage extends AppCompatImageView {
    public AlphaImage(Context context) {
        super(context);
    }

    public AlphaImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlphaImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ObjectAnimator objectAnimator;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                objectAnimator = ObjectAnimator.ofFloat(
                        this, "alpha", 1, 0.5f);
                objectAnimator.start();

                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_UP:
                objectAnimator = ObjectAnimator.ofFloat(
                        this, "alpha", 0.5f, 1);
                objectAnimator.start();
                break;
        }

        return super.onTouchEvent(event);
    }
}
