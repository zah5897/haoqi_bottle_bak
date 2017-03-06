/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhan.haoqi.bottle.view.photoview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;

import com.zhan.haoqi.bottle.R;
import com.zhan.haoqi.bottle.util.ImageUtils;


public class HQLoadLocalBigImgTask extends AsyncTask<Void, Void, Bitmap> {

    private HQPhotoView photoView;
    private String path;
    private int width;
    private int height;
    private Context context;

    public HQLoadLocalBigImgTask(Context context, String path, HQPhotoView photoView,
                                 int width, int height) {
        this.context = context;
        this.path = path;
        this.photoView = photoView;
        this.width = width;
        this.height = height;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        int degree = ImageUtils.readPictureDegree(path);
        if (degree != 0) {
            photoView.setVisibility(View.INVISIBLE);
        } else {
            photoView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap bitmap = ImageUtils.decodeScaleImage(path, width, height);
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        photoView.setVisibility(View.VISIBLE);
        if (result != null)
            HQImageCache.getInstance().put(path, result);
        else
            result = BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.hq_default_image);
        photoView.setImageBitmap(result);
    }
}
