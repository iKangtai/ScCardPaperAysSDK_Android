package com.example.cardpaperdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;

import com.example.cardpaperdemo.R;
import com.ikangtai.cardpapersdk.util.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * 手动拍照测量坐标线
 *
 * @author
 */
public class ManualSmartPaperMeasureLayout extends FrameLayout {
    private TextPaint textPaint = new TextPaint();
    private TextPaint linePaint = new TextPaint();
    private TextPaint bgPaint = new TextPaint();
    private TextPaint lineEffectPaint = new TextPaint();
    private Context context;
    private int width;
    private int height;
    private Data data = new Data();
    private Bitmap originSquareBitmap;
    private Bitmap sampleBitmap;
    private int destBitmapHeight;
    private int destBitmapWidth;

    public void scanPaperCoordinatesData(Bitmap originSquareBitmap) {
        this.originSquareBitmap = originSquareBitmap;
        invalidate();
    }

    private void drawBitmap(Canvas canvas) {

        if (originSquareBitmap != null) {
            int left = 0;
            int top = 0;
            int right = getWidth();
            int bottom = getHeight();
            Rect rect = new Rect(left, top, right, bottom);
            canvas.drawBitmap(originSquareBitmap, null, rect, null);
        }
    }

    public ManualSmartPaperMeasureLayout(@NonNull Context context) {
        super(context);
        this.initData(context);
    }

    public ManualSmartPaperMeasureLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initData(context);
    }

    public ManualSmartPaperMeasureLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initData(context);
    }

    private void initData(Context context) {
        this.context = context;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        this.width = dm.widthPixels;
        this.height = this.width;
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(Utils.sp2px(context, 13f));
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(Color.parseColor("#90000000"));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.parseColor("#79FA1E"));
        linePaint.setStrokeWidth(Utils.dp2px(context, 3));
        lineEffectPaint.setAntiAlias(true);
        lineEffectPaint.setColor(Color.parseColor("#79FA1E"));
        lineEffectPaint.setStrokeWidth(Utils.dp2px(context, 3));
        int effectWidth = Utils.sp2px(context, 10);
        lineEffectPaint.setPathEffect(new DashPathEffect(new float[]{effectWidth, effectWidth / 2}, 0));
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setBackgroundColor(Color.TRANSPARENT);
        data.outerWidth = this.width;
        data.outerHeight = this.width;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int resolvedHeight = View.resolveSize(width, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, resolvedHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBitmap(canvas);
        drawLine(canvas);
        drawPaper(canvas);
    }

    private void drawPaper(Canvas canvas) {
        int padding = Utils.dp2px(context, 10);
        float top = width / 2 - destBitmapHeight;

        int startX = (int) (destBitmapWidth * 212 / 355 + padding - lineEffectPaint.getStrokeWidth() / 2);
        data.tagLineLoc = startX + lineEffectPaint.getStrokeWidth() / 2;
        canvas.drawLine(startX, width / 2 - destBitmapHeight + 30 * destBitmapHeight / 101, startX, width / 2 + Utils.dp2px(context, 10) + destBitmapHeight * 70 / 101, lineEffectPaint);
    }

    private void drawLine(Canvas canvas) {
        //linePaint.setPathEffect(new DashPathEffect(new float[]{12, 12}, 0));
        int padding = Utils.dp2px(context, 10);
        int paperHeight = width * 2 / 7;
        float paperTop = width / 2 + Utils.dp2px(context, 10);

        //绘制黑色蒙板
        canvas.drawRect(0, 0, width, paperTop, bgPaint);
        canvas.drawRect(0, paperTop + paperHeight, width, width, bgPaint);
        //画试纸参考条
        if (sampleBitmap == null) {
            sampleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.paper_confirm_sample_pic);
        }
        destBitmapWidth = width - padding * 2;
        destBitmapHeight = destBitmapWidth * 2 / 7;
        float left = padding;
        float top = width / 2 - destBitmapHeight;
        float right = destBitmapWidth + left;
        float bottom = width / 2;
        RectF rectF = new RectF(left, top, right, bottom);
        canvas.drawBitmap(sampleBitmap, null, rectF, null);

        int lineWidth = Utils.dp2px(context, 10);

        //第一角标
        float centerX = padding + destBitmapWidth * 142 / 355f;
        float centerY = top + destBitmapHeight * 30 / 101f;
        float startX = centerX + lineWidth;
        float startY = centerY;
        float stopX = centerX;
        float stopY = centerY + lineWidth;
        canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        //第二角标
        centerX = padding + destBitmapWidth * 142 / 355f;
        centerY = top + destBitmapHeight * 70 / 101f;
        startX = centerX;
        startY = centerY - lineWidth;
        stopX = centerX + lineWidth;
        stopY = centerY;
        canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        //第三角标
        centerX = padding + destBitmapWidth * 236 / 355f;
        centerY = top + destBitmapHeight * 70 / 101f;
        startX = centerX - lineWidth;
        startY = centerY;
        stopX = centerX;
        stopY = centerY - lineWidth;
        canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        //第四角标
        centerX = padding + destBitmapWidth * 236 / 355f;
        centerY = top + destBitmapHeight * 30 / 101f;
        startX = centerX - padding;
        startY = centerY;
        stopX = centerX;
        stopY = centerY + lineWidth;
        canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);


        float fixY = (paperHeight - destBitmapHeight) / 2;
        //第5角标
        centerX = padding + destBitmapWidth * 142 / 355f;
        centerY = paperTop + fixY + destBitmapHeight * 30 / 101f;
        startX = centerX + lineWidth;
        startY = centerY;
        stopX = centerX;
        stopY = centerY + lineWidth;
        canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        //第6角标
        centerX = padding + destBitmapWidth * 142 / 355f;
        centerY = paperTop + fixY + destBitmapHeight * 70 / 101f;
        startX = centerX;
        startY = centerY - lineWidth;
        stopX = centerX + lineWidth;
        stopY = centerY;
        canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        //第7角标
        centerX = padding + destBitmapWidth * 236 / 355f;
        centerY = paperTop + fixY + destBitmapHeight * 70 / 101f;
        startX = centerX - lineWidth;
        startY = centerY;
        stopX = centerX;
        stopY = centerY - lineWidth;
        canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        //第8角标
        centerX = padding + destBitmapWidth * 236 / 355f;
        centerY = paperTop + fixY + destBitmapHeight * 30 / 101f;
        startX = centerX - padding;
        startY = centerY;
        stopX = centerX;
        stopY = centerY + lineWidth;
        canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        data.innerLeft = (int) (destBitmapWidth * 142 / 355f) + padding;
        data.innerTop = (int) (paperTop + fixY + destBitmapHeight * 30 / 101f);
        data.innerRight = (int) (destBitmapWidth * 236 / 355f) + padding;
        data.innerBottom = (int) (paperTop + fixY + destBitmapHeight * 70 / 101f);
        data.innerWidth = data.innerRight - data.innerLeft;
        data.innerHeight = data.innerBottom - data.innerTop;
        data.outerWidth = destBitmapWidth;
        data.outerHeight = destBitmapHeight;
    }

    private Path handleLinePath(float startX, float startY, float centerX, float centerY, float endX, float endY) {
        Path p = new Path();
        p.moveTo(startX, startY);
        p.lineTo(centerX, centerY);
        p.lineTo(endX, endY);
        return p;
    }

    public Data getData() {
        return data;
    }

    public static final class Data {
        /**
         * 最外层宽度
         */
        public int outerWidth;
        /**
         * 最外层高度
         */
        public int outerHeight;
        /**
         * 裁剪框宽度
         */
        public int innerWidth;
        /**
         * 裁剪框高度
         */
        public int innerHeight;
        /**
         * 裁剪框左上角left坐标
         */
        public int innerLeft;
        /**
         * 裁剪框左上角top坐标
         */
        public int innerTop;
        /**
         * 裁剪框右下角right坐标
         */
        public int innerRight;
        /**
         * 裁剪框右下角bottom坐标
         */
        public int innerBottom;

        public float tagLineLoc;
        /**
         * 裁剪框左上角left坐标
         */
        public int enlargeLeft;
        /**
         * 裁剪框左上角top坐标
         */
        public int enlargeTop;
        /**
         * 裁剪框右下角right坐标
         */
        public int enlargeRight;
        /**
         * 裁剪框右下角bottom坐标
         */
        public int enlargeBottom;
        /**
         * 裁剪框宽度
         */
        public int enlargeWidth;
        /**
         * 裁剪框高度
         */
        public int enlargeHeight;

        public String getPointPath() {
            String flag = "_";
            StringBuilder pointPathBuilder = new StringBuilder();
            pointPathBuilder.append(innerLeft);
            pointPathBuilder.append(flag);
            pointPathBuilder.append(innerTop);

            pointPathBuilder.append(flag);
            pointPathBuilder.append(innerRight);
            pointPathBuilder.append(flag);
            pointPathBuilder.append(innerTop);

            pointPathBuilder.append(flag);
            pointPathBuilder.append(innerRight);
            pointPathBuilder.append(flag);
            pointPathBuilder.append(innerBottom);

            pointPathBuilder.append(flag);
            pointPathBuilder.append(innerLeft);
            pointPathBuilder.append(flag);
            pointPathBuilder.append(innerBottom);

            return pointPathBuilder.toString();
        }

        @Override
        public String toString() {
            return "Data{" +
                    "outerWidth=" + outerWidth +
                    ", outerHeight=" + outerHeight +
                    ", innerWidth=" + innerWidth +
                    ", innerHeight=" + innerHeight +
                    ", innerLeft=" + innerLeft +
                    ", innerTop=" + innerTop +
                    ", innerRight=" + innerRight +
                    ", innerBottom=" + innerBottom +
                    '}';
        }
    }
}
