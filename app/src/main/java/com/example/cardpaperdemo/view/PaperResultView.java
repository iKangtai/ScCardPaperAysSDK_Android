package com.example.cardpaperdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;

import com.ikangtai.cardpapersdk.util.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 手动拍照测量坐标线
 *
 * @author
 */
public class PaperResultView extends FrameLayout {
    private TextPaint textPaint = new TextPaint();
    private TextPaint linePaint = new TextPaint();
    private Context context;
    private int width;
    private int height;
    private ManualSmartPaperMeasureLayout.Data data;
    private Bitmap originSquareBitmap;

    public void drawPaperData(ManualSmartPaperMeasureLayout.Data data, Bitmap originSquareBitmap) {
        this.data = data;
        this.originSquareBitmap = originSquareBitmap;
        invalidate();
    }

    private void drawBitmap(Canvas canvas) {
        if (originSquareBitmap != null) {
            int paperWidth = data.innerWidth;
            int paperHeight = data.innerHeight;
            int padding = Utils.dp2px(context, 10);
            int left = (int) (width / 2 - paperWidth * 1f);
            int top = padding * 2;
            int right = (int) (width / 2 + paperWidth * 1f);
            int bottom = (int) (top + paperHeight * 2);
            Rect srcRect = new Rect(data.innerLeft, data.innerTop, data.innerRight, data.innerBottom);
            Rect dstRect = new Rect(left, top, right, bottom);
            canvas.drawBitmap(originSquareBitmap, srcRect, dstRect, null);
            canvas.drawRect(left, top, right, bottom, linePaint);
            data.enlargeWidth = (int) (paperWidth * 2f);
            data.enlargeHeight = (int) (paperHeight * 2f);
            data.enlargeLeft = left;
            data.enlargeTop = top;
            data.enlargeRight = right;
            data.enlargeBottom = bottom;
        }
    }

    public PaperResultView(@NonNull Context context) {
        super(context);
        this.initData(context);
    }

    public PaperResultView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initData(context);
    }

    public PaperResultView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initData(context);
    }

    private void initData(Context context) {
        this.context = context;
        int padding = Utils.dp2px(context, 10);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        this.width = dm.widthPixels;
        this.height = padding * 2 + (this.width - padding * 2) * 160 / 707 + padding / 5;
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.parseColor("#4285F7"));
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(Utils.sp2px(context, 14f));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(Utils.dp2px(context, 1));
        linePaint.setColor(Color.parseColor("#4285F7"));

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setBackgroundColor(Color.TRANSPARENT);
    }

    public int getPaperWidth() {
        return width;
    }

    public int getPaperHeight() {
        return height;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int resolvedWidth = View.resolveSize(width, widthMeasureSpec);
        int resolvedHeight = View.resolveSize(height, heightMeasureSpec);
        setMeasuredDimension(resolvedWidth, resolvedHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBitmap(canvas);
        drawLine(canvas);
    }

    private void drawLine(Canvas canvas) {
        int padding = Utils.dp2px(context, 10);
        int paperHeight = data.innerHeight;
        int paperWidth = data.innerWidth;

        //画M G C
        final String M = "M";
        final String G = "G";
        final String C = "C";
        float tcY = padding * 1.5f;
        float mWidth = textPaint.measureText(M);
        float gWidth = textPaint.measureText(G);
        float cWidth = textPaint.measureText(C);
        float mX = width / 2 - paperWidth + paperWidth * 2 * 52.5f / 180 - mWidth / 2;
        float gX = width / 2 - paperWidth + paperWidth * 2 * 91.5f / 180 - gWidth / 2;
        float cX = width / 2 - paperWidth + paperWidth * 2 * 131.5f / 180 - cWidth / 2;
        canvas.drawText(M, mX, tcY, textPaint);
        canvas.drawText(G, gX, tcY, textPaint);
        canvas.drawText(C, cX, tcY, textPaint);
    }
}
