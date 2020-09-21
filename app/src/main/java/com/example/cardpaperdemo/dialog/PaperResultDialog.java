package com.example.cardpaperdemo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cardpaperdemo.R;
import com.example.cardpaperdemo.view.ManualSmartPaperMeasureLayout;
import com.example.cardpaperdemo.view.PaperResultView;
import com.ikangtai.cardpapersdk.CardPaperAnalysiserClient;
import com.ikangtai.cardpapersdk.Session;
import com.ikangtai.cardpapersdk.event.IBaseAnalysisEvent;
import com.ikangtai.cardpapersdk.event.ISessionResultEvent;
import com.ikangtai.cardpapersdk.http.reqmodel.FeedbackReq;
import com.ikangtai.cardpapersdk.http.respmodel.SaasConfigResp;
import com.ikangtai.cardpapersdk.model.PaperResult;

import java.util.Locale;


/**
 * desc
 *
 * @author xiongyl 2019/10/31 22:01
 */
public class PaperResultDialog {
    protected Dialog dialog;
    private Context context;
    private Display display;
    private ManualSmartPaperMeasureLayout.Data data;
    private Bitmap originSquareBitmap;
    private PaperResultView paperResultView;
    private TextView titleTv, hintTv;
    private ImageView lineImageView;
    private View line1ImageView, line2ImageView, line3ImageView, line1View, line2View, line3View;
    private View closeLine1, closeLine2, closeLine3, addLine1, addLine2, addLine3;
    private Button backButton, confirmButton;
    private int mLineRiskLastX1, mLineRiskLastX2, mLineRiskLastX3;
    private OnPaperResultListener paperResultListener;
    private double lineLoc1;
    private double lineLoc2;
    private double lineLoc3;

    private View contentView;
    private TextView paperFeedback;
    private Session mSession;
    private CardPaperAnalysiserClient cardPaperAnalysiserClient;

    public PaperResultDialog(Context context, CardPaperAnalysiserClient cardPaperAnalysiserClient) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        this.display = windowManager.getDefaultDisplay();
        this.cardPaperAnalysiserClient = cardPaperAnalysiserClient;
    }

    public TextView getTitleTv() {
        return titleTv;
    }

    public TextView getHintTv() {
        return hintTv;
    }

    public ImageView geline3ImageView() {
        return lineImageView;
    }

    public Button getBackButton() {
        return backButton;
    }

    public Button getConfirmButton() {
        return confirmButton;
    }

    public View getContentView() {
        return contentView;
    }

    public TextView getPaperFeedback() {
        return paperFeedback;
    }

    public PaperResultDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_paper_result_dialog, null);
        contentView = view;
        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth(display.getWidth());

        // 获取自定义Dialog布局中的控件
        paperResultView = view.findViewById(R.id.paper_image);
        lineImageView = view.findViewById(R.id.paper_line_image);
        line1ImageView = view.findViewById(R.id.paper_line_view_parent_l1);
        line2ImageView = view.findViewById(R.id.paper_line_view_parent_l2);
        line3ImageView = view.findViewById(R.id.paper_line_view_parent_l3);
        line1View = view.findViewById(R.id.paper_line_view_l1);
        line2View = view.findViewById(R.id.paper_line_view_l2);
        line3View = view.findViewById(R.id.paper_line_view_l3);
        closeLine1 = view.findViewById(R.id.paper_line_image_close1);
        closeLine2 = view.findViewById(R.id.paper_line_image_close2);
        closeLine3 = view.findViewById(R.id.paper_line_image_close3);
        addLine1 = view.findViewById(R.id.paper_line_image_add1);
        addLine2 = view.findViewById(R.id.paper_line_image_add2);
        addLine3 = view.findViewById(R.id.paper_line_image_add3);
        titleTv = view.findViewById(R.id.txt_title);
        hintTv = view.findViewById(R.id.paper_hint_tv);
        backButton = view.findViewById(R.id.paper_return_bt);
        confirmButton = view.findViewById(R.id.paper_confirm_bt);
        paperFeedback = view.findViewById(R.id.paper_feedback);

        line1View.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!view.isShown()) {
                    return false;
                }
                view = line1ImageView;
                int x = (int) motionEvent.getRawX();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLineRiskLastX1 = x;
                        lineImageView.setTranslationX(view.getTranslationX() + view.getWidth() * 72f / 102);
                        lineImageView.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        lineImageView.setVisibility(View.INVISIBLE);
                        refreshLineLoc();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //计算距离上次移动了多远
                        int deltaX = x - mLineRiskLastX1;
                        int translationX = (int) (view.getTranslationX() + deltaX);
                        //左边界
                        if (translationX < data.enlargeLeft) {
                            translationX = data.enlargeLeft;
                        }
                        //右边界
                        if (line3ImageView.isShown()) {
                            if (line2ImageView.isShown()) {
                                if (translationX + view.getWidth() + line2ImageView.getWidth() + line3ImageView.getWidth() > data.enlargeRight) {
                                    translationX = data.enlargeRight - view.getWidth() - line2ImageView.getWidth() - line3ImageView.getWidth();
                                }
                            } else {
                                if (translationX + view.getWidth() + line3ImageView.getWidth() > data.enlargeRight) {
                                    translationX = data.enlargeRight - view.getWidth() - line3ImageView.getWidth();
                                }
                            }
                        } else if (line2ImageView.isShown()) {
                            if (translationX + view.getWidth() + line2ImageView.getWidth() > data.enlargeRight) {
                                translationX = data.enlargeRight - view.getWidth() - line2ImageView.getWidth();
                            }
                        } else {
                            if (translationX + view.getWidth() > data.enlargeRight) {
                                translationX = data.enlargeRight - view.getWidth();
                            }
                        }

                        //使mFloatRiskBtn根据手指滑动平移
                        view.setTranslationX(translationX);
                        lineImageView.setTranslationX(translationX + view.getWidth() * 72f / 102);

                        if (line3ImageView.isShown()) {
                            if (line2ImageView.isShown()) {
                                if (line2ImageView.getTranslationX() < view.getWidth() + translationX) {
                                    line2ImageView.setTranslationX(translationX + view.getWidth());
                                }
                                if (line3ImageView.getTranslationX() < line2ImageView.getWidth() + line2ImageView.getTranslationX()) {
                                    line3ImageView.setTranslationX(line2ImageView.getWidth() + line2ImageView.getTranslationX());
                                }
                            } else {
                                if (line3ImageView.getTranslationX() < view.getWidth() + translationX) {
                                    line3ImageView.setTranslationX(translationX + view.getWidth());
                                }
                            }
                        } else if (line2ImageView.isShown()) {
                            if (line2ImageView.getTranslationX() < view.getWidth() + translationX) {
                                line2ImageView.setTranslationX(translationX + view.getWidth());
                            }
                        }
                        break;
                    default:
                        break;
                }
                //记录上次手指离开时的位置
                mLineRiskLastX1 = x;
                return true;
            }
        });
        line2View.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!view.isShown()) {
                    return false;
                }
                view = line2ImageView;
                int x = (int) motionEvent.getRawX();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLineRiskLastX2 = x;
                        lineImageView.setTranslationX(view.getTranslationX() + view.getWidth() / 2);
                        lineImageView.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        lineImageView.setVisibility(View.INVISIBLE);
                        refreshLineLoc();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //计算距离上次移动了多远
                        int deltaX = x - mLineRiskLastX2;
                        int translationX = (int) (view.getTranslationX() + deltaX);
                        //左边界
                        if (line1ImageView.isShown()) {
                            if (translationX - line1ImageView.getWidth() < data.enlargeLeft) {
                                translationX = data.enlargeLeft + line1ImageView.getWidth();
                            }
                        } else {
                            if (translationX < data.enlargeLeft) {
                                translationX = data.enlargeLeft;
                            }
                        }
                        //右边界

                        if (line3ImageView.isShown()) {
                            if (translationX + view.getWidth() + line3ImageView.getWidth() > data.enlargeRight) {
                                translationX = data.enlargeRight - view.getWidth() - line3ImageView.getWidth();
                            }
                        } else {
                            if (translationX + view.getWidth() > data.enlargeRight) {
                                translationX = data.enlargeRight - view.getWidth();
                            }
                        }

                        //使mFloatRiskBtn根据手指滑动平移
                        view.setTranslationX(translationX);
                        lineImageView.setTranslationX(translationX + view.getWidth() / 2);

                        if (line1ImageView.isShown()) {
                            if (line1ImageView.getTranslationX() + line1ImageView.getWidth() > translationX) {
                                line1ImageView.setTranslationX(translationX - line1ImageView.getWidth());
                            }
                        }
                        if (line3ImageView.isShown()) {
                            if (line3ImageView.getTranslationX() < view.getWidth() + translationX) {
                                line3ImageView.setTranslationX(translationX + view.getWidth());
                            }
                        }
                        break;
                    default:
                        break;
                }
                //记录上次手指离开时的位置
                mLineRiskLastX2 = x;
                return true;
            }
        });
        line3View.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!view.isShown()) {
                    return false;
                }
                view = line3ImageView;
                int x = (int) motionEvent.getRawX();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLineRiskLastX3 = x;
                        lineImageView.setTranslationX(view.getTranslationX() + view.getWidth() * 33f / 105);
                        lineImageView.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        lineImageView.setVisibility(View.INVISIBLE);
                        refreshLineLoc();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //计算距离上次移动了多远
                        int deltaX = x - mLineRiskLastX3;
                        int translationX = (int) (view.getTranslationX() + deltaX);
                        //左边界
                        if (line1ImageView.isShown()) {
                            if (line2ImageView.isShown()) {
                                if (translationX - line1ImageView.getWidth() - line2ImageView.getWidth() < data.enlargeLeft) {
                                    translationX = data.enlargeLeft + line1ImageView.getWidth() + line2ImageView.getWidth();
                                }
                            } else {
                                if (translationX - line1ImageView.getWidth() < data.enlargeLeft) {
                                    translationX = data.enlargeLeft + line1ImageView.getWidth();
                                }
                            }
                        } else if (line2ImageView.isShown()) {
                            if (translationX - line2ImageView.getWidth() < data.enlargeLeft) {
                                translationX = data.enlargeLeft + line2ImageView.getWidth();
                            }
                        } else {
                            if (translationX < data.enlargeLeft) {
                                translationX = data.enlargeLeft;
                            }
                        }
                        //右边界

                        if (translationX + view.getWidth() > data.enlargeRight) {
                            translationX = data.enlargeRight - view.getWidth();
                        }

                        //使mFloatRiskBtn根据手指滑动平移
                        view.setTranslationX(translationX);
                        lineImageView.setTranslationX(translationX + view.getWidth() * 33f / 105);

                        if (line1ImageView.isShown()) {
                            if (line2ImageView.isShown()) {
                                if (line2ImageView.getTranslationX() + line2ImageView.getWidth() > translationX) {
                                    line2ImageView.setTranslationX(translationX - line2ImageView.getWidth());
                                }
                                if (line1ImageView.getTranslationX() + line1ImageView.getWidth() > line2ImageView.getTranslationX()) {
                                    line1ImageView.setTranslationX(line2ImageView.getTranslationX() - line1ImageView.getWidth());
                                }
                            } else {
                                if (line1ImageView.getTranslationX() + line1ImageView.getWidth() > translationX) {
                                    line1ImageView.setTranslationX(translationX - line1ImageView.getWidth());
                                }
                            }
                        } else if (line2ImageView.isShown()) {
                            if (line2ImageView.getTranslationX() + line2ImageView.getWidth() > translationX) {
                                line2ImageView.setTranslationX(translationX - line2ImageView.getWidth());
                            }
                        }
                        break;
                    default:
                        break;
                }
                //记录上次手指离开时的位置
                mLineRiskLastX3 = x;
                return true;
            }
        });

        closeLine1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!line1View.isShown()) {
                    return;
                }
                line1View.setVisibility(View.INVISIBLE);
                addLine1.setVisibility(View.VISIBLE);
                refreshLineLoc();
                refreshHintView();
            }
        });
        closeLine2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!line2View.isShown()) {
                    return;
                }
                line2View.setVisibility(View.INVISIBLE);
                addLine2.setVisibility(View.VISIBLE);
                refreshLineLoc();
                refreshHintView();
            }
        });
        closeLine3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!line3View.isShown()) {
                    return;
                }
                line3View.setVisibility(View.INVISIBLE);
                addLine3.setVisibility(View.VISIBLE);
                refreshLineLoc();
                refreshHintView();
            }
        });

        addLine1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (line1View.isShown()) {
                    return;
                }
                line1View.setVisibility(View.VISIBLE);
                addLine1.setVisibility(View.INVISIBLE);
                refreshLineLoc();
                refreshHintView();
            }
        });
        addLine2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (line2View.isShown()) {
                    return;
                }
                line2View.setVisibility(View.VISIBLE);
                addLine2.setVisibility(View.INVISIBLE);
                refreshLineLoc();
                refreshHintView();
            }
        });
        addLine3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (line3View.isShown()) {
                    return;
                }
                line3View.setVisibility(View.VISIBLE);
                addLine3.setVisibility(View.INVISIBLE);
                refreshLineLoc();
                refreshHintView();
            }
        });

        view.findViewById(R.id.paper_return_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (paperResultListener != null) {
                    paperResultListener.cancel();
                }
            }
        });
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paperResultListener != null) {
                    if (mSession == null) {
                        return;
                    }
                    v.setEnabled(false);


                    if (mSession.getChangedLine1Pos() > 0 || mSession.getChangedLine2Pos() > 0 || mSession.getChangedLine3Pos() > 0) {
                        mSession.setChange(true);
                    }

                    final PaperResult paperResult = new PaperResult();
                    paperResult.setSessionId(mSession.getSessionId());
                    paperResult.setPaperId(mSession.getPaperId());
                    paperResult.setPaperTime(mSession.getPaperTime());
                    paperResult.setPanoramaImgUrl(mSession.getPanoramaImgUrl());
                    paperResult.setUrl(mSession.getUrl());
                    if (mSession.isChange()) {
                        if (line1View.isShown()) {
                            paperResult.setLine1Pos(mSession.getChangedLine1Pos());
                        }
                        if (line2View.isShown()) {
                            paperResult.setLine2Pos(mSession.getChangedLine2Pos());
                        }
                        if (line3View.isShown()) {
                            paperResult.setLine3Pos(mSession.getChangedLine3Pos());
                        }
                    } else {
                        paperResult.setLine1Pos(mSession.getLinePos1());
                        paperResult.setLine2Pos(mSession.getLinePos2());
                        paperResult.setLine3Pos(mSession.getLinePos3());
                    }

                    paperResult.setPaperValue(mSession.getPaperValue());
                    paperResult.setPaperType(mSession.getPaperType());
                    paperResult.setRatioValue(mSession.getRatioValue());
                    paperResult.setErrNo(mSession.getErrNo());
                    paperResult.setErrMsg(mSession.getErrMsg());
                    if (mSession.isChange()) {
                        paperResult.setChangeTcLine(true);
                    } else {
                        paperResult.setChangeTcLine(false);
                    }

                    if (mSession.isChange()) {
                        //保存试纸结果
                        mSession.setPaperValue(-1);
                        paperResult.setPaperValue(-1);
                        if (paperResult.getLine3Pos() == 0) {
                            dialog.dismiss();
                            if (paperResultListener != null) {
                                paperResultListener.save(paperResult);
                            }
                        } else {
                            updateSession(paperResult);
                        }
                    } else {
                        dialog.dismiss();
                        if (paperResultListener != null) {
                            paperResultListener.save(paperResult);
                        }
                    }
                } else {
                    dialog.dismiss();
                }
            }
        };
        view.findViewById(R.id.paper_confirm_bt).setOnClickListener(onClickListener);
        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (cardPaperAnalysiserClient != null) {
                    cardPaperAnalysiserClient.stopPaperSassAnalysis();
                }
            }
        });
        return this;
    }

    public void refreshLineLoc() {
        int paperWidth = data.enlargeWidth;
        lineLoc1 = (line1ImageView.getTranslationX() + line1ImageView.getWidth() * 72f / 102 - data.enlargeLeft) * 1.0 / paperWidth;
        lineLoc2 = (line2ImageView.getTranslationX() + line2ImageView.getWidth() / 2 - data.enlargeLeft) * 1.0 / paperWidth;
        lineLoc3 = (line3ImageView.getTranslationX() + line3ImageView.getWidth() * 33f / 105 - data.enlargeLeft) * 1.0 / paperWidth;

        mSession.setChangedLine1Pos(lineLoc1);
        mSession.setChangedLine2Pos(lineLoc2);
        mSession.setChangedLine3Pos(lineLoc3);

    }

    private void refreshHintView() {
        String temp = "";
        String separator = context.getString(R.string.paper_result_dialog_hit_separator);
        if (!line1View.isShown()) {
            temp = "M" + separator;
        }
        if (!line2View.isShown()) {
            temp = temp + "G" + separator;
        }
        if (!line3View.isShown()) {
            temp = temp + "C" + separator;
        }
        if (TextUtils.isEmpty(temp)) {
            hintTv.setText(context.getString(R.string.paper_result_dialog_hit));
        } else {
            hintTv.setText(context.getString(R.string.paper_result_dialog_hit) + String.format(context.getString(R.string.paper_result_dialog_hit_add), temp.substring(0, temp.length() - 1)));
        }
    }

    public PaperResultDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public PaperResultDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public PaperResultDialog setOnPaperResultListener(OnPaperResultListener paperResultListener) {
        this.paperResultListener = paperResultListener;
        return this;
    }

    public PaperResultDialog setOriginSquareBitmap(Bitmap originSquareBitmap) {
        this.originSquareBitmap = originSquareBitmap;
        return this;
    }

    public PaperResultDialog setPaperData(ManualSmartPaperMeasureLayout.Data paperData) {
        this.data = paperData;
        return this;
    }

    /**
     * 设置条目布局
     */
    private void setSheetItems() {
        paperResultView.drawPaperData(data, originSquareBitmap);
    }

    private void refreshPaperLineView() {
        //{"code":200,"message":"成功","data":{"tLocation":null,"lhPaperAlType":3,"lhValue":-1,"cLocation":null,"tLocationManual":null,"cLocationManual":null}}
        if (lineLoc1 < 0) {
            lineLoc1 = 0;
        }
        if (lineLoc2 < 0) {
            lineLoc2 = 0;
        }
        if (lineLoc3 < 0) {
            lineLoc3 = 0;
        }
        if (lineLoc3 > 1) {
            lineLoc3 = 1;
        }
        if (lineLoc2 > 1) {
            lineLoc2 = 1;
        }
        if (lineLoc1 > 1) {
            lineLoc1 = 1;
        }
        if (lineLoc1 == 0) {
            line1View.setVisibility(View.INVISIBLE);
            addLine1.setVisibility(View.VISIBLE);
        }
        if (lineLoc2 == 0) {
            line2View.setVisibility(View.INVISIBLE);
            addLine2.setVisibility(View.VISIBLE);
        }
        if (lineLoc3 == 0) {
            line3View.setVisibility(View.INVISIBLE);
            addLine3.setVisibility(View.VISIBLE);
        }
        int paperWidth = data.enlargeWidth;
        double loc1 = paperWidth * lineLoc1 - line1ImageView.getWidth() * 72f / 102;
        double loc2 = paperWidth * lineLoc2 - line1ImageView.getWidth() / 2;
        double loc3 = paperWidth * lineLoc3 - line3ImageView.getWidth() * 33f / 105;
        if (lineLoc3==0){
            loc3 = paperWidth * ((data.tagLineLoc - data.innerLeft) / data.innerWidth) - line3ImageView.getWidth() * 33f / 105;
        }
        if (lineLoc2 == 0) {
            loc2 = loc3 - line2ImageView.getWidth();
        }
        if (lineLoc1 == 0) {
            loc1 = loc2 - line1ImageView.getWidth();
        }

        if (loc1 < -line1ImageView.getWidth()) {
            loc1 = -line1ImageView.getWidth();
        }
        if (loc1 + line1ImageView.getWidth() + line3ImageView.getWidth() + line2ImageView.getWidth() > paperWidth + line3ImageView.getWidth()) {
            loc1 = paperWidth - line1ImageView.getWidth() - line2ImageView.getWidth() - line3ImageView.getWidth();
            lineLoc1 = (loc1 + line1ImageView.getWidth() * 72f / 102) * 1.0 / paperWidth;
        }
        if (loc2 + line3ImageView.getWidth() + line2ImageView.getWidth() > paperWidth + line3ImageView.getWidth()) {
            loc2 = paperWidth - line2ImageView.getWidth() - line3ImageView.getWidth();
            lineLoc2 = (loc2 + line1ImageView.getWidth() / 2) * 1.0 / paperWidth;
        }
        if (loc3 + line3ImageView.getWidth() > paperWidth + line3ImageView.getWidth()) {
            loc3 = paperWidth - line3ImageView.getWidth();
            lineLoc3 = (loc3 + line3ImageView.getWidth() * 33f / 105) * 1.0 / paperWidth;
        }
        if (loc2 > loc3 - line2ImageView.getWidth()) {
            loc2 = loc3 - line2ImageView.getWidth();
            lineLoc2 = (loc2 + line2ImageView.getWidth() / 2) * 1.0 / paperWidth;
        }
        if (loc1 > loc2 - line1ImageView.getWidth()) {
            loc1 = loc2 - line1ImageView.getWidth();
            lineLoc1 = (loc1 + line1ImageView.getWidth() * 72f / 102) * 1.0 / paperWidth;
        }
        line1ImageView.setTranslationX((float) loc1 + data.enlargeLeft);
        line2ImageView.setTranslationX((float) loc2 + data.enlargeLeft);
        line3ImageView.setTranslationX((float) loc3 + data.enlargeLeft);
    }


    private void paperSassAnalysis() {
        if (mSession != null) {
            if (mSession.getLinePos1() > 0) {
                lineLoc1 = mSession.getLinePos1();
            }
            if (mSession.getLinePos2() > 0) {
                lineLoc2 = mSession.getLinePos2();
            }
            if (mSession.getLinePos3() > 0) {
                lineLoc3 = mSession.getLinePos3();
            }
        }

        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        paperResultView.post(new Runnable() {
            @Override
            public void run() {
                refreshPaperLineView();
                refreshHintView();
            }
        });
        if (!TextUtils.isEmpty(mSession.getErrMsg())) {
            paperResultListener.saasAnalysisError(mSession.getErrMsg(), mSession.getErrNo());
        }
    }

    public void createSession() {
        if (paperResultListener != null) {
            paperResultListener.showProgressDialog();
        }
        float paperLeft = data.innerLeft * 1f / paperResultView.getPaperWidth();
        float paperTop = data.innerTop * 1f / paperResultView.getPaperWidth();
        float paperRight = data.innerRight * 1f / paperResultView.getPaperWidth();
        float paperBottom = data.innerBottom * 1f / paperResultView.getPaperWidth();
        float tagLineLoc = (data.tagLineLoc - data.innerLeft) / data.innerWidth;
        /**
         * paperLeft,paperTop,paperRight,paperBottom 试纸取景框相对试纸全景图坐标位置
         * tagLineLoc 标记线相对于试纸框位置
         */
        cardPaperAnalysiserClient.startPaperSassAnalysis(originSquareBitmap, paperLeft, paperTop, paperRight, paperBottom, tagLineLoc, new ISessionResultEvent() {
            @Override
            public void onResult(Session session) {
                if (paperResultListener != null) {
                    paperResultListener.dismissProgressDialog();
                }
                mSession = session;
                paperSassAnalysis();
            }
        });
    }

    public void updateSession(final PaperResult paperResult) {
        if (paperResultListener != null) {
            paperResultListener.showProgressDialog();
        }
        float paperLeft = data.innerLeft * 1f / paperResultView.getPaperWidth();
        float paperTop = data.innerTop * 1f / paperResultView.getPaperWidth();
        float paperRight = data.innerRight * 1f / paperResultView.getPaperWidth();
        float paperBottom = data.innerBottom * 1f / paperResultView.getPaperWidth();
        float tagLineLoc = (data.tagLineLoc - data.innerLeft) / data.innerWidth;
        float test1LineLoc = (float) paperResult.getLine1Pos();
        float test2LineLoc = (float) paperResult.getLine2Pos();
        /**
         * paperLeft,paperTop,paperRight,paperBottom 试纸取景框相对试纸全景图坐标位置
         * tagLineLoc 标记线相对于试纸框位置
         * test1LineLoc 第1条测试线相对于试纸框位置
         * test2LineLoc 第1条测试线相对于试纸框位置
         */
        cardPaperAnalysiserClient.updatePaperSassAnalysis(originSquareBitmap, paperLeft, paperTop, paperRight, paperBottom, tagLineLoc, test1LineLoc, test2LineLoc, new ISessionResultEvent() {
            @Override
            public void onResult(Session session) {
                paperResult.setPaperValue(session.getPaperValue());
                if (paperResultListener != null) {
                    paperResultListener.dismissProgressDialog();
                    paperResultListener.save(paperResult);
                }
                dialog.dismiss();
            }
        });
    }

    public void stopSession() {
        cardPaperAnalysiserClient.stopPaperSassAnalysis();
    }

    public void configView(final SaasConfigResp.SaasConfig saasConfig) {
        if (saasConfig != null && saasConfig.getFeedback() != null) {
            String language = Locale.getDefault().getLanguage();
            if (saasConfig.getFeedback().getEnable() == 0) {
                paperFeedback.setVisibility(View.GONE);
            } else {
                final SaasConfigResp.FeedbackConfigInfo feedbackConfigInfo;
                if (TextUtils.equals(language, "zh")) {
                    feedbackConfigInfo = saasConfig.getFeedback().getZh();
                } else if (TextUtils.equals(language, "en")) {
                    feedbackConfigInfo = saasConfig.getFeedback().getEn();
                } else if (TextUtils.equals(language, "de")) {
                    feedbackConfigInfo = saasConfig.getFeedback().getDe();
                } else if (TextUtils.equals(language, "es")) {
                    feedbackConfigInfo = saasConfig.getFeedback().getEs();
                } else if (TextUtils.equals(language, "fr")) {
                    feedbackConfigInfo = saasConfig.getFeedback().getFr();
                } else if (TextUtils.equals(language, "it")) {
                    feedbackConfigInfo = saasConfig.getFeedback().getIt();
                } else if (TextUtils.equals(language, "ja")) {
                    feedbackConfigInfo = saasConfig.getFeedback().getJa();
                } else if (TextUtils.equals(language, "ko")) {
                    feedbackConfigInfo = saasConfig.getFeedback().getKo();
                } else {
                    feedbackConfigInfo = null;
                }
                if (feedbackConfigInfo == null || TextUtils.isEmpty(feedbackConfigInfo.getInfo())) {
                    paperFeedback.setVisibility(View.GONE);
                } else {
                    paperFeedback.setText(Html.fromHtml("<u>" + feedbackConfigInfo.getInfo() + "</u>"));
                    paperFeedback.setVisibility(View.VISIBLE);
                    paperFeedback.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FeedbackDialog feedbackDialog = new FeedbackDialog(context).builder();
                            feedbackDialog.setMsg(feedbackConfigInfo.getDetail());
                            feedbackDialog.setNegativeButton("", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FeedbackReq.feedback(mSession.getSessionId(), mSession.getPaperId(), mSession.getPaperTime(), mSession.getPaperType(), saasConfig.getFeedback().getErrCode());
                                }
                            });
                            feedbackDialog.setPositiveButton("", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //feedback
                                    FeedbackReq.feedback(mSession.getSessionId(), mSession.getPaperId(), mSession.getPaperTime(), mSession.getPaperType(), saasConfig.getFeedback().getRightCode());
                                }
                            });
                            feedbackDialog.show();
                        }
                    });
                }
            }
        } else {
            paperFeedback.setVisibility(View.GONE);
        }
    }

    public PaperResultDialog show() {
        setSheetItems();
        //dialog.show();
        paperResultView.post(new Runnable() {
            @Override
            public void run() {
                refreshPaperLineView();
            }
        });
        createSession();
        return this;
    }


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

    public interface OnPaperResultListener extends IBaseAnalysisEvent {
    }

}
