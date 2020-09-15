package com.example.cardpaperdemo;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cardpaperdemo.dialog.AlertDialog;
import com.example.cardpaperdemo.dialog.PaperResultDialog;
import com.example.cardpaperdemo.util.CameraUtil;
import com.example.cardpaperdemo.view.CameraSurfaceView;
import com.example.cardpaperdemo.view.ManualSmartPaperMeasureLayout;
import com.example.cardpaperdemo.view.ProgressDialog;
import com.example.cardpaperdemo.view.SmartPaperMeasureContainerLayout;
import com.example.cardpaperdemo.view.TopBar;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.ikangtai.cardpapersdk.CardPaperAnalysiserClient;
import com.ikangtai.cardpapersdk.model.PaperResult;
import com.ikangtai.cardpapersdk.util.LogUtils;
import com.ikangtai.cardpapersdk.util.ToastUtils;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


/**
 * 智能试纸
 *
 * @author
 */
public class AutoSmartPaperActivity extends AppCompatActivity {
    private TopBar topBar;

    private CameraSurfaceView surfaceView;
    private SmartPaperMeasureContainerLayout smartPaperMeasureContainerLayout;
    private CameraUtil cameraUtil;
    private TextView ovulationCameraTips, flashTv;
    private ImageView shutterBtn;
    private CardPaperAnalysiserClient cardPaperAnalysiserClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cardPaperAnalysiserClient = new CardPaperAnalysiserClient(this, App.saasAppId, App.saasAppSecret, "xyl@qq.com");
        setContentView(R.layout.activity_auto_smart_paper);
        initView();
        initData();
    }


    private void initView() {
        topBar = findViewById(R.id.topBar);

        topBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void midLeftClick() {

            }

            @Override
            public void midRightClick() {

            }

            @Override
            public void rightClick() {
            }
        });
        findViewById(R.id.camera_scrollview).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        surfaceView = findViewById(R.id.camera_surfaceview);
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (cameraUtil != null) {
                    cameraUtil.focusOnTouch(event);
                }
                return true;
            }
        });
        smartPaperMeasureContainerLayout = findViewById(R.id.paper_scan_content_view);
        ovulationCameraTips = findViewById(R.id.ovulationCameraTips);
        flashTv = findViewById(R.id.paper_flash_tv);
        shutterBtn = findViewById(R.id.shutterBtn);
        ovulationCameraTips.setText(Html.fromHtml(getString(R.string.ovulation_camera_tips)));
        smartPaperMeasureContainerLayout.showManualSmartPaperMeasure();
    }

    private void initData() {
        flashTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d("cameraUtil == null   " + (cameraUtil == null));
                if (cameraUtil == null) {
                    return;
                }
                LogUtils.d("cameraUtil.isOpenFlashLight()   " + (cameraUtil.isOpenFlashLight()));
                if (cameraUtil.isOpenFlashLight()) {
                    LogUtils.d("关闭闪光灯");
                    flashTv.setText(getText(R.string.paper_open_flashlight));
                    flashTv.setTextColor(getResources().getColor(R.color.white));
                    flashTv.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.icon_lamp_close, 0, 0);
                    cameraUtil.closeFlashLight();
                } else {
                    LogUtils.d("打开闪光灯");
                    flashTv.setText(getText(R.string.paper_close_flashlight));
                    flashTv.setTextColor(0xFFF4F400);
                    flashTv.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.icon_lamp_open, 0, 0);
                    cameraUtil.openFlashLight();
                }
            }
        });
        ((View) shutterBtn.getParent()).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        shutterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                LogUtils.d("手动拍照");
                v.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.setEnabled(true);
                    }
                }, 1000);
                //手动拍照
                cameraUtil.takePicture(new CameraUtil.ICameraTakeEvent() {
                    @Override
                    public void takeBitmap(Bitmap originSquareBitmap) {
                        clipPaperDialog(originSquareBitmap);
                    }
                });
            }
        });
    }

    private void init() {
        XXPermissions.with(AutoSmartPaperActivity.this)
                .permission(Permission.Group.STORAGE, Permission.Group.CAMERA)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if (isAll) {
                            handleCamera();
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        if (quick) {
                            ToastUtils.show(AutoSmartPaperActivity.this,
                                    getString(R.string.permission_fail));
                            XXPermissions.gotoPermissionSettings(AutoSmartPaperActivity.this);
                        } else {
                            ToastUtils.show(AutoSmartPaperActivity.this, getString(R.string.permission_success));
                            finish();
                        }
                    }
                });
    }

    private void restartScan(boolean restartOpenCamera) {
        shutterBtn.setVisibility(View.VISIBLE);
        if (smartPaperMeasureContainerLayout != null) {
            smartPaperMeasureContainerLayout.showManualSmartPaperMeasure();
        }
        if (restartOpenCamera) {
            handleCamera();
        }
    }


    private void handleCamera() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtils.d("初始化Camera");
                if (cameraUtil == null) {
                    cameraUtil = new CameraUtil();
                }
                cameraUtil.initCamera(AutoSmartPaperActivity.this, surfaceView, mPreviewCallback);
            }

        }, 200);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
        if (flashTv != null) {
            flashTv.setText(getText(R.string.paper_open_flashlight));
            flashTv.setTextColor(getResources().getColor(R.color.white));
            flashTv.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.icon_lamp_close, 0, 0);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.d("cameraUtil != null  " + (cameraUtil != null));
        if (cameraUtil != null) {
            LogUtils.d("cameraUtil.isOpenFlashLight()  " + (cameraUtil.isOpenFlashLight()));
            if (cameraUtil.isOpenFlashLight()) {
                try {
                    flashTv.setText(getText(R.string.paper_open_flashlight));
                    flashTv.setTextColor(getResources().getColor(R.color.white));
                    flashTv.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.icon_lamp_close, 0, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            LogUtils.d("cameraUtil.stopCamera() start= " + (cameraUtil.isOpenFlashLight()));
            cameraUtil.stopCamera();
            LogUtils.d("cameraUtil.stopCamera() end= " + (cameraUtil.isOpenFlashLight()));
            cameraUtil = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void clipPaperDialog(Bitmap fileBitmap) {
        smartPaperMeasureContainerLayout.showManualSmartPaperMeasure(fileBitmap);
        final ManualSmartPaperMeasureLayout.Data data =
                smartPaperMeasureContainerLayout.getManualSmartPaperMeasuereData();
        final PaperResultDialog paperResultDialog = new PaperResultDialog(this, cardPaperAnalysiserClient);
        paperResultDialog.setOriginSquareBitmap(fileBitmap).setPaperData(data);
        paperResultDialog.setOnPaperResultListener(new PaperResultDialog.OnPaperResultListener() {
            @Override
            public void showProgressDialog() {
                AutoSmartPaperActivity.this.showProgressDialog();
            }

            @Override
            public void dismissProgressDialog() {
                AutoSmartPaperActivity.this.dismissProgressDialog();
            }

            @Override
            public void cancel() {
                restartScan(false);
            }

            @Override
            public void save(PaperResult paperResult) {
                smartPaperMeasureContainerLayout.showManualSmartPaperMeasure(null);
            }

            @Override
            public void saasAnalysisError(String errorResult, int code) {
                showErrorDialog(paperResultDialog, errorResult);
            }
        });
        paperResultDialog.builder().show();

    }

    private void showErrorDialog(final PaperResultDialog paperResultDialog, String message) {
        dismissProgressDialog();
        new AlertDialog(AutoSmartPaperActivity.this).builder().setTitle(getString(R.string.warm_prompt))
                .setMsg(message, Gravity.CENTER)
                .setNegativeButton(getString(R.string.view_guide), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        smartPaperMeasureContainerLayout.showManualSmartPaperMeasure(null);
                    }
                }).setPositiveButton(getString(R.string.take_photo_again), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paperResultDialog != null) {
                    paperResultDialog.dissmiss();
                }
                smartPaperMeasureContainerLayout.showManualSmartPaperMeasure(null);
            }
        }).show();
    }


    /**
     * 实时预览回调
     */
    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(final byte[] data, final Camera camera) {

        }
    };

    protected Dialog progressDialog;

    public void showProgressDialog() {
        progressDialog = ProgressDialog.createLoadingDialog(this, null, null);
        if (progressDialog != null && !progressDialog.isShowing() && !isFinishing()) {
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
