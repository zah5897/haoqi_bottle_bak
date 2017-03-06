package com.zhan.haoqi.bottle.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by zah on 2016/11/22.
 */
public class ImageUtils {
    public static final int SCALE_IMAGE_WIDTH = 640;
    public static final int SCALE_IMAGE_HEIGHT = 960;

    public static Bitmap getRoundedCornerBitmap(Bitmap paramBitmap) {
        return getRoundedCornerBitmap(paramBitmap, 6.0F);
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap paramBitmap, float paramFloat) {
        Bitmap localBitmap = Bitmap.createBitmap(paramBitmap.getWidth(), paramBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap);
        int i = -12434878;
        Paint localPaint = new Paint();
        Rect localRect = new Rect(0, 0, paramBitmap.getWidth(), paramBitmap.getHeight());
        RectF localRectF = new RectF(localRect);
        localPaint.setAntiAlias(true);
        localCanvas.drawARGB(0, 0, 0, 0);
        localPaint.setColor(-12434878);
        localCanvas.drawRoundRect(localRectF, paramFloat, paramFloat, localPaint);
        localPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        localCanvas.drawBitmap(paramBitmap, localRect, localRect, localPaint);
        return localBitmap;
    }

    public static Bitmap decodeScaleImage(String paramString, int paramInt1, int paramInt2) {
        BitmapFactory.Options localOptions = getBitmapOptions(paramString);
        int i = calculateInSampleSize(localOptions, paramInt1, paramInt2);
        localOptions.inSampleSize = i;
        localOptions.inJustDecodeBounds = false;
        Bitmap localBitmap1 = BitmapFactory.decodeFile(paramString, localOptions);
        int j = readPictureDegree(paramString);
        Bitmap localBitmap2 = null;
        if ((localBitmap1 != null) && (j != 0)) {
            localBitmap2 = rotateImageView(j, localBitmap1);
            localBitmap1.recycle();
            localBitmap1 = null;
            return localBitmap2;
        }
        return localBitmap1;
    }

    public static Bitmap decodeScaleImage(Context paramContext, int paramInt1, int paramInt2, int paramInt3) {
        BitmapFactory.Options localOptions = new BitmapFactory.Options();
        localOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(paramContext.getResources(), paramInt1, localOptions);
        int i = calculateInSampleSize(localOptions, paramInt2, paramInt3);
        localOptions.inSampleSize = i;
        localOptions.inJustDecodeBounds = false;
        Bitmap localBitmap = BitmapFactory.decodeResource(paramContext.getResources(), paramInt1, localOptions);
        return localBitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options paramOptions, int paramInt1, int paramInt2) {
        int i = paramOptions.outHeight;
        int j = paramOptions.outWidth;
        int k = 1;
        if ((i > paramInt2) || (j > paramInt1)) {
            int m = Math.round(i / paramInt2);
            int n = Math.round(j / paramInt1);
            k = m > n ? m : n;
        }
        return k;
    }

    public static String getThumbnailImage(String paramString, int paramInt) {
        Bitmap localBitmap = decodeScaleImage(paramString, paramInt, paramInt);
        try {
            File localFile = File.createTempFile("image", ".jpg");
            FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 60, localFileOutputStream);
            localFileOutputStream.close();
            return localFile.getAbsolutePath();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return paramString;
    }

    public static String getScaledImage(Context paramContext, String paramString) {
        File localFile1 = new File(paramString);
        if (!localFile1.exists())
            return paramString;
        long l = localFile1.length();
        if (l <= 102400L) {
            return paramString;
        }
        Bitmap localBitmap = decodeScaleImage(paramString, 640, 960);
        try {
            File localFile2 = File.createTempFile("image", ".jpg", paramContext.getFilesDir());
            FileOutputStream localFileOutputStream = new FileOutputStream(localFile2);
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 70, localFileOutputStream);
            localFileOutputStream.close();
            return localFile2.getAbsolutePath();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return paramString;
    }

    public static String getScaledImage(Context paramContext, String paramString, int paramInt) {
        File localFile1 = new File(paramString);
        if (localFile1.exists()) {
            long l = localFile1.length();
            if (l > 102400L) {
                Bitmap localBitmap = decodeScaleImage(paramString, 640, 960);
                try {
                    File localFile2 = new File(paramContext.getExternalCacheDir(), "eaemobTemp" + paramInt + ".jpg");
                    FileOutputStream localFileOutputStream = new FileOutputStream(localFile2);
                    localBitmap.compress(Bitmap.CompressFormat.JPEG, 60, localFileOutputStream);
                    localFileOutputStream.close();
                    return localFile2.getAbsolutePath();
                } catch (Exception localException) {
                    localException.printStackTrace();
                }
            }
        }
        return paramString;
    }

    public static Bitmap mergeImages(int paramInt1, int paramInt2, List<Bitmap> paramList) {
        Bitmap localBitmap1 = Bitmap.createBitmap(paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap1);
        localCanvas.drawColor(-3355444);
        int i;
        if (paramList.size() <= 4)
            i = 2;
        else
            i = 3;
        int j = 0;
        int k = (paramInt1 - 4) / i;
        for (int m = 0; m < i; m++)
            for (int n = 0; n < i; n++) {
                Bitmap localBitmap2 = (Bitmap) paramList.get(j);
                Bitmap localBitmap3 = Bitmap.createScaledBitmap(localBitmap2, k, k, true);
                Bitmap localBitmap4 = getRoundedCornerBitmap(localBitmap3, 2.0F);
                localBitmap3.recycle();
                localCanvas.drawBitmap(localBitmap4, n * k + (n + 2), m * k + (m + 2), null);
                localBitmap4.recycle();
                j++;
                if (j == paramList.size())
                    return localBitmap1;
            }
        return localBitmap1;
    }

    public static int readPictureDegree(String paramString) {
        int i = 0;
        try {
            ExifInterface localExifInterface = new ExifInterface(paramString);
            int j = localExifInterface.getAttributeInt("Orientation", 1);
            switch (j) {
                case 6:
                    i = 90;
                    break;
                case 3:
                    i = 180;
                    break;
                case 8:
                    i = 270;
            }
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
        return i;
    }

    public static Bitmap rotateImageView(int degrees, Bitmap bitmap) {
        Matrix localMatrix = new Matrix();
        localMatrix.postRotate(degrees);
        Bitmap localBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), localMatrix, true);
        return localBitmap;
    }

    public static BitmapFactory.Options getBitmapOptions(String paramString) {
        BitmapFactory.Options localOptions = new BitmapFactory.Options();
        localOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(paramString, localOptions);
        return localOptions;
    }
}