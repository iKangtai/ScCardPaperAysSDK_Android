package com.example.cardpaperdemo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.example.cardpaperdemo.R;
import com.ikangtai.cardpapersdk.util.LogUtils;

public class FeedbackDialog {
    private Context context;
    private Dialog dialog;
    private TextView txt_title;
    private TextView txt_msg;
    private TextView txt_msg_left;
    private Button btn_neg;
    private Button btn_pos;
    private ImageView img_line;
    private Display display;
    private boolean showImageTitle = false;
    private boolean showTitle = true;
    private boolean showMsg = false;
    private boolean showMsgLeft = false;
    private boolean showPosBtn = false;
    private boolean showNegBtn = false;

    private LinearLayout lLayout_bg;
    private ImageView iv_title;
    private boolean showText1 = false;//这个是横排还是竖排的标记，默认是横排
    private boolean showText2 = false;


    public FeedbackDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public FeedbackDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                R.layout.paper_alert_dialog, null);

        // 获取自定义Dialog布局中的控件
        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        iv_title = (ImageView) view.findViewById(R.id.iv_title);
        iv_title.setVisibility(View.GONE);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_title.setVisibility(View.GONE);
        txt_msg = (TextView) view.findViewById(R.id.txt_msg);
        txt_msg.setVisibility(View.GONE);
        txt_msg_left = (TextView) view.findViewById(R.id.txt_msg_left);
        txt_msg_left.setVisibility(View.GONE);
        btn_neg = (Button) view.findViewById(R.id.btn_neg);
        btn_neg.setVisibility(View.GONE);
        btn_pos = (Button) view.findViewById(R.id.btn_pos);
        btn_pos.setVisibility(View.GONE);
        img_line = (ImageView) view.findViewById(R.id.img_line);
        img_line.setVisibility(View.GONE);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.PaperUtilAlertDialogStyle);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        // 调整dialog背景大小
        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                .getWidth() * 0.85), LayoutParams.WRAP_CONTENT));

        return this;
    }

    public FeedbackDialog setImageTitle(int title) {
        showImageTitle = true;
        if (title != 0) {
            iv_title.setImageResource(title);
        }
        return this;
    }

    public FeedbackDialog setTitle(String title) {
        showTitle = true;
        if (TextUtils.isEmpty(title)) {
            txt_title.setText("");
            txt_title.setVisibility(View.GONE);
        } else {
            txt_title.setVisibility(View.VISIBLE);
            txt_title.setText(title);
        }
        return this;
    }

    public FeedbackDialog setTitleBold(boolean bold) {
        if (bold) {
            txt_title.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            txt_title.setTypeface(Typeface.DEFAULT);
        }
        return this;
    }

    public FeedbackDialog setMsg(String msg) {
        showMsg = true;
        if ("".equals(msg)) {
            txt_msg.setText("");
        } else {
            txt_msg.setText(msg);
        }
        return this;
    }

    public FeedbackDialog setMsgLeft(String msg) {
        showMsgLeft = true;
        if ("".equals(msg)) {
            txt_msg_left.setText("");
        } else {
            txt_msg_left.setText(msg);
        }
        return this;
    }

    public FeedbackDialog setMsgSpanned(Spanned msg) {
        showMsg = true;
        if ("".equals(msg)) {
            txt_msg.setText("");
        } else {
            txt_msg.setText(msg);
        }
        return this;
    }

    public FeedbackDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public FeedbackDialog setPositiveButton(String text,
                                            final OnClickListener listener) {
        showPosBtn = true;
        if ("".equals(text)) {
            btn_pos.setText(context.getString(R.string.paper_util_done));
        } else {
            btn_pos.setText(text);
        }
        btn_pos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public FeedbackDialog setPositiveButton(int color, String text,
                                            final OnClickListener listener) {
        showPosBtn = true;
        btn_pos.setTextColor(color);
        if ("".equals(text)) {
            btn_pos.setText(context.getString(R.string.paper_util_done));
        } else {
            btn_pos.setText(text);
        }
        btn_pos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public FeedbackDialog setNegativeButton(int color, String text,
                                            final OnClickListener listener) {
        showNegBtn = true;
        btn_neg.setTextColor(color);
        if ("".equals(text)) {
            btn_neg.setText(context.getString(R.string.paper_util_cancel));
        } else {
            btn_neg.setText(text);
        }
        btn_neg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public FeedbackDialog setNegativeButton( String text,
                                            final OnClickListener listener) {
        showNegBtn = true;
        if ("".equals(text)) {
            btn_neg.setText(context.getString(R.string.paper_util_cancel));
        } else {
            btn_neg.setText(text);
        }
        btn_neg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    private void setLayout() {
        if (!showTitle && !showMsg) {
            txt_title.setText("");
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showImageTitle) {
            iv_title.setVisibility(View.VISIBLE);
        }

        if (showTitle) {
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showMsg) {
            txt_msg.setVisibility(View.VISIBLE);
        }

        if (showMsgLeft) {
            txt_msg_left.setVisibility(View.VISIBLE);
        }

        if (!showPosBtn && !showNegBtn) {
            btn_pos.setText("");
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.paper_alertdialog_single_selector);
            btn_pos.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        if (showPosBtn && showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.paper_alertdialog_right_selector);
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.paper_alertdialog_left_selector);
            img_line.setVisibility(View.VISIBLE);
        }

        if (showPosBtn && !showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.paper_alertdialog_single_selector);
        }

        if (!showPosBtn && showNegBtn) {
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.paper_alertdialog_single_selector);
        }
    }

    public void show() {
        try {
            setLayout();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i("AlertDialog show 失败 " + e.getMessage());
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        }
        return false;
    }

    public void showErrorDlg(String msg) {
        builder().setCancelable(true)
                .setTitle(context.getResources().getString(R.string.paper_util_tips))
                .setTitleBold(true)
                .setMsg(msg)
                .setPositiveButton(context.getResources().getString(R.string.paper_util_done), new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
    }

}
