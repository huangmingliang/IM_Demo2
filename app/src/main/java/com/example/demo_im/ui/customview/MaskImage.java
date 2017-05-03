package com.example.demo_im.ui.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.example.demo_im.R;

/**
 * author:chosen
 * date:2016/10/17 16:13
 * email:812219713@qq.com
 */

public class MaskImage extends ImageView {

    private String TAG=MaskImage.class.getSimpleName();

    int mMaskSource = 0;  // 遮罩图片id
    RuntimeException mException;
    Drawable mask;
    int source;

    public void setSource(int source) {
        this.source = source;
    }

    public MaskImage(Context context,int source) {
        this(context, null,source);
    }

    public MaskImage(Context context, AttributeSet attrs,int source) {
        super(context, attrs);

        //TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaskImage);
        //source = typedArray.getInt(R.styleable.MaskImage_maskSource, -1);
        //typedArray.recycle();
        if (source == 0) {
            mMaskSource = R.drawable.bubble_green;
        } else if (source == 1) {
            mMaskSource = R.drawable.bubble_white;
        }
        mask = getResources().getDrawable(mMaskSource);
        if (mException != null)
            throw mException;
    }

    private static final Paint paintMask = createMaskPaint();


    private static final Paint createMaskPaint() {
        Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        return paint;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mask != null) {
            // bounds
            int width = getWidth();
            int height = getHeight();
            // create blend layer
            canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
            //
            // mask
            //
            if (mask != null) {
                mask.setBounds(0, 0, width, height);
                mask.draw(canvas);
            }
            //
            // source
            //
            {
                canvas.saveLayer(0, 0, width, height, paintMask, Canvas.ALL_SAVE_FLAG);
                super.onDraw(canvas);
                canvas.restore();
            }
            // apply blend layer
            canvas.restore();
        } else {
            super.onDraw(canvas);
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                setAlpha(0.8f);

                break;
            case MotionEvent.ACTION_UP:

                setAlpha(1f);

                break;
            case MotionEvent.ACTION_CANCEL:

                setAlpha(1f);

                break;
            case MotionEvent.ACTION_MOVE:

                setAlpha(0.8f);

                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }


}
