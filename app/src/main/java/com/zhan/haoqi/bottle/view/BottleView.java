package com.zhan.haoqi.bottle.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.zhan.haoqi.bottle.R;
import com.zhan.haoqi.bottle.data.Bottle;
import com.zhan.haoqi.bottle.ui.listener.BottleViewClickListener;

/**
 * Created by Administrator on 2016/11/30 0030.
 */

public class BottleView extends ImageView {
    private Bottle bottle;

    public BottleView(Context context) {
        super(context);
    }

    public Bottle getBottle() {
        return bottle;
    }

    public BottleView(Context context, final Bottle bottle) {
        this(context);
        this.bottle = bottle;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottleClickListener != null) {
                    bottleClickListener.onClick(BottleView.this, bottle);
                }
            }
        });
        setImageResource(R.mipmap.bottle_h);
    }


    private BottleViewClickListener bottleClickListener;

    public void setBottleViewClickListener(BottleViewClickListener bottleViewClickListener) {
        this.bottleClickListener = bottleViewClickListener;
    }
}
