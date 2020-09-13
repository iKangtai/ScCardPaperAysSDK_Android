package com.example.paperdemo.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatButton;

/**
 * desc
 *
 * @author xiongyl 2019/11/29 17:10
 */
public class AlphaButton extends AppCompatButton {
    public AlphaButton(Context context) {
        super(context);
    }

    public AlphaButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlphaButton(Context context, AttributeSet attrs, int defStyleAttr) {
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

