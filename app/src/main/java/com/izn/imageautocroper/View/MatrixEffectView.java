package com.izn.imageautocroper.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.Random;
import android.graphics.*;
import com.izn.imageautocroper.R;
import android.util.*;

public class MatrixEffectView extends View {

    private static final Random RANDOM = new Random();
    private int width, height;
    private Canvas canvas;
    private Bitmap canvasBmp;
    private int fontSize=60;
    private int columnSize;
	private char[] cars= "101010101010".toCharArray();
    private int[] txtPosByColumn;
    private Paint paintTxt, paintBg, paintBgBmp, paintInitBg;
	private Bitmap bgBitmap;
    public MatrixEffectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paintTxt = new Paint();
        paintTxt.setStyle(Paint.Style.FILL);
        paintTxt.setColor(0xff01acfc);
        paintTxt.setTextSize(fontSize);
		bgBitmap=BitmapFactory.decodeResource(getResources(),R.drawable.bg_j);
        paintBg = new Paint();
        paintBg.setAlpha(5);
		fontSize=spToPx(13.333f,context);
        paintBgBmp = new Paint();
        paintInitBg = new Paint();
        paintInitBg.setAlpha(255);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        canvasBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(canvasBmp);
        canvas.drawBitmap(bgBitmap,0, 0,paintInitBg);
        columnSize = width / fontSize;
        txtPosByColumn = new int[columnSize + 1];
        for (int x = 0; x < columnSize; x++) {
            txtPosByColumn[x] = RANDOM.nextInt(height / 2) + 1;
        }
    }

    private void drawText() {
        for (int i = 0; i < txtPosByColumn.length; i++) {
            canvas.drawText("" + cars[RANDOM.nextInt(cars.length)], i * fontSize, txtPosByColumn[i] * fontSize, paintTxt);

            if (txtPosByColumn[i] * fontSize > height && Math.random() > 0.975) {
                txtPosByColumn[i] = 0;
            }
            txtPosByColumn[i]++;
        }
    }

    private void drawCanvas() {
		canvas.drawBitmap(bgBitmap,0,0,paintBg);
        fontSize=spToPx(13.333f,getContext());
		drawText();
    }
	public static int spToPx(float sp, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
	}
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(canvasBmp, 0, 0, paintBgBmp);
        drawCanvas();
        invalidate();
    }
}
