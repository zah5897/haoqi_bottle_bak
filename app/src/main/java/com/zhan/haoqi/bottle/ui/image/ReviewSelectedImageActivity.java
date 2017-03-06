package com.zhan.haoqi.bottle.ui.image;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;

import com.bumptech.glide.Glide;
import com.zhan.haoqi.bottle.R;
import com.zhan.haoqi.bottle.util.ImageUtils;
import com.zhan.haoqi.bottle.view.photoview.HQImageCache;
import com.zhan.haoqi.bottle.view.photoview.HQLoadLocalBigImgTask;
import com.zhan.haoqi.bottle.view.photoview.HQPhotoView;

import java.io.File;

/**
 * Created by zah on 2016/11/22.
 */
public class ReviewSelectedImageActivity extends Activity {
    private HQPhotoView image;
    private int default_res = R.mipmap.hq_default_image;
    private String localFilePath;
    private String url;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.hq_activity_show_big_image);
        super.onCreate(savedInstanceState);

        image = (HQPhotoView) findViewById(R.id.image);
        default_res = getIntent().getIntExtra("default_image", R.mipmap.hq_default_image);
        image.setImageResource(default_res);
        localFilePath = getIntent().getExtras().getString("localUrl");
        url = getIntent().getExtras().getString("url");
        if (!TextUtils.isEmpty(localFilePath) && new File(localFilePath).exists()) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            bitmap = HQImageCache.getInstance().get(localFilePath);
            if (bitmap == null) {
                HQLoadLocalBigImgTask task = new HQLoadLocalBigImgTask(this, localFilePath, image, ImageUtils.SCALE_IMAGE_WIDTH,
                        ImageUtils.SCALE_IMAGE_HEIGHT);
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    task.execute();
                }
            } else {
                image.setImageBitmap(bitmap);
            }
        } else if (!TextUtils.isEmpty(url)) {
            downloadImage(url);
        } else {
            image.setImageResource(default_res);
        }
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * download image
     */
    @SuppressLint("NewApi")
    private void downloadImage(final String url) {
        Glide.with(this).load(url).override(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels).into(image);
    }
}
