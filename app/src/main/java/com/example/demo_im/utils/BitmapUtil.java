package com.example.demo_im.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Created by huangmingliang on 2017/5/8.
 */

public class BitmapUtil {
    private static String TAG=BitmapUtil.class.getSimpleName();
    //使用BitmapFactory.Options的inSampleSize参数来缩放
    public static Bitmap createBitmap(String path,
                                        int width)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//不加载bitmap到内存中
        BitmapFactory.decodeFile(FileUtil.getCacheFilePath(path),options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = 1;

        if (outWidth != 0 && outHeight != 0 && width != 0)
        {
            int sampleSize=outWidth/width;
            Log.d(TAG, "sampleSize = " + sampleSize);
            options.inSampleSize = sampleSize;
        }

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(FileUtil.getCacheFilePath(path), options);
    }
}
