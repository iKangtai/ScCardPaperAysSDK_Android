package com.example.cardpaperdemo.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cardpaperdemo.R;


/**
 * Created by ikangtai on 2017/2/20.
 */

public class ProgressDialog {
    private static ImageView spaceshipImage;
    private static TextView tipTextView;
    protected Dialog dialog;

    public void dissmiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public boolean showing() {
        if (dialog != null && dialog.isShowing()) {
            return true;
        }

        return false;
    }


    public ProgressDialog withOverLay() {
        if (Build.VERSION.SDK_INT >= 26) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY - 1);
        }
        return this;
    }

    public static Dialog createLoadingDialog(Context context, String msg) {
        return createLoadingDialog(context, msg, null);
    }

    /**
     * 自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
    public static Dialog createLoadingDialog(Context context, String msg, View.OnClickListener onClickListener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);
        LinearLayout layout = v.findViewById(R.id.dialog_view);
        // main.xml中的ImageView
        spaceshipImage = v.findViewById(R.id.img);
        tipTextView = v.findViewById(R.id.tipTextView);
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.loading_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        if (TextUtils.isEmpty(msg)) {
            tipTextView.setVisibility(View.GONE);
        } else {
            // 设置加载信息
            tipTextView.setText(msg);
        }
        layout.setOnClickListener(onClickListener);
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));
        return loadingDialog;
    }

    public static void setTipTextView(String msg) {
        tipTextView.setText(msg);
    }

    public static void setTipTextVisibility(int visibility) {
        tipTextView.setVisibility(visibility);
    }
}
