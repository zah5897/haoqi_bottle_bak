package com.zhan.haoqi.bottle.util;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zhan.haoqi.bottle.MyApplication;


/**
 * Created by zah on 2016/11/18.
 */
public class ImageShowUtil {

    public static void display(String imageUrl, ImageView imageView, boolean isRound) {
        toDisplay(MyApplication.getApp(), imageUrl, imageView, isRound);
    }

    public static void display(Uri imageUri, ImageView imageView, boolean isRound) {
        toDisplay(MyApplication.getApp(), imageUri, imageView, isRound);
    }

    public static void display(Context context, String imageUrl, ImageView imageView, boolean isRound) {
        toDisplay(context, imageUrl, imageView, isRound);
    }

    public static void display(Fragment fragment, String imageUrl, ImageView imageView, boolean isRound) {
        if (isRound) {
            Glide.with(fragment).load(imageUrl).transform(new GlideCircleTransform(fragment.getActivity())).into(imageView);
        } else {
            Glide.with(fragment).load(imageUrl).into(imageView);
        }
    }

    public static void display(Context context, Uri imageUri, ImageView imageView, boolean isRound) {
        toDisplay(context, imageUri, imageView, isRound);
    }

    private static void toDisplay(Context context, String imageUrl, ImageView imageView, boolean isRound) {
        if (isRound) {
            Glide.with(context).load(imageUrl).transform(new GlideCircleTransform(context)).into(imageView);
        } else {
            Glide.with(context).load(imageUrl).into(imageView);
        }

    }

    private static void toDisplay(Context context, Uri imageUrl, ImageView imageView, boolean isRound) {
        if (isRound) {
            Glide.with(context).load(imageUrl).transform(new GlideCircleTransform(context)).into(imageView);
        } else {
            Glide.with(context).load(imageUrl).into(imageView);
        }
    }


}
