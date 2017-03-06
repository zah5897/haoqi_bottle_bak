package com.zhan.haoqi.bottle.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.zhan.haoqi.bottle.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MediaManager {
    public static final int REQUEST_CAMERA = 100;
    public static final int REQUEST_GALLERY = 200;
    public static final int REQUEST_REVIEW = 300;


    public static int getRatioMax(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        float f = paramInt1 * 1.0F / paramInt3;
        return (int) Math.ceil(Math.max(paramInt2 * 1.0F / paramInt4, f));
    }


    public static File saveBitmap(Bitmap bitmap) {
        File fileToWrite = new File(getDirPathImg(), System.currentTimeMillis() + ".jpg");
        if (fileToWrite.exists()) {
            fileToWrite.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(fileToWrite);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            recycleBitmap(bitmap);
        }
        return fileToWrite;
    }


    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        bitmap = null;
    }

    public static String getDirPathImg() {
        File localFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "hqbottle", "img");
        localFile.mkdirs();
        return localFile.getAbsolutePath();
    }

    public static String getDirPathLog() {
        File localFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "hqbottle", "log");
        localFile.mkdirs();
        return localFile.getAbsolutePath();
    }

    public static void getPhotoFromAlbum(Activity paramActivity) {
        getPhotoFromAlbum(paramActivity, REQUEST_GALLERY);
    }

    public static void getPhotoFromAlbum(Activity activity, int paramInt) {
        try {
            Intent intent = new Intent("android.intent.action.PICK");
            intent.setType("image/*");
            activity.startActivityForResult(Intent.createChooser(intent, activity.getResources().getString(R.string.dialog_select_img)), paramInt);
            return;
        } catch (Exception paramActivity) {
        }
    }

    public static File getPhotoFromCamera(Activity activity) {
        File mTmpFile = null;
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(activity.getPackageManager()) != null) {
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            mTmpFile = createTmpFile(activity);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
            activity.startActivityForResult(cameraIntent, REQUEST_CAMERA);
        } else {
            To.show("没有系统相机");
        }
        return mTmpFile;
    }

    public static void recycle(Bitmap paramBitmap) {
        if ((paramBitmap != null) && (!paramBitmap.isRecycled())) {
            paramBitmap.recycle();
            System.gc();
        }
    }

    public static File createTmpFile(Context context) {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 已挂载

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
            String fileName = "tmp_" + timeStamp + "";
            File tmpFile = new File(getDirPathImg(), fileName + ".jpg");
            return tmpFile;
        } else {
            File cacheDir = context.getCacheDir();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
            String fileName = "tmp_" + timeStamp + "";
            File tmpFile = new File(cacheDir, fileName + ".jpg");
            return tmpFile;
        }
    }

    public static void deleteFile(File file) {
        if (file != null && file.exists()) {
            String name = file.getName();
            if (name.startsWith("tmp_")) {
                boolean bool = file.delete();
            }

        }
    }

}
