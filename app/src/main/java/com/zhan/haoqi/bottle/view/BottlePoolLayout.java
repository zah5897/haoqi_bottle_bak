package com.zhan.haoqi.bottle.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.zhan.haoqi.bottle.data.Bottle;
import com.zhan.haoqi.bottle.http.BottleHttpManager;
import com.zhan.haoqi.bottle.ui.listener.BottleClickListener;
import com.zhan.haoqi.bottle.ui.listener.BottleViewClickListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rx.Subscriber;

/**
 * Created by zah on 2016/11/30.
 */

public class BottlePoolLayout extends RelativeLayout {

    public static final String BOTTLE_REFRESH = "bottle.sea.refresh";

    public static final int TIME_DELAY = 2 * 60 * 1000;
    private List<BottleView> bottles = new ArrayList<>();
    private boolean reSize = false;
    private int screenWidth;
    private int viewHeight;

    private BottleClickListener bottleClickListener;
    private Handler handler;

    public BottlePoolLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        handler = new Handler();
        handler.post(refreshBottle);
    }


    private int centerY;

    public int getCenterY() {
        return centerY;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        if (width > 0) {
            screenWidth = width;
            resize();
        }
    }

    private void resize() {
        if (reSize) {
            return;
        }
        reSize = true;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int top = (int) (screenHeight * 0.40);

        int height = (int) (screenHeight * 0.65f) - top;
        RelativeLayout.LayoutParams
                params = (RelativeLayout.LayoutParams) getLayoutParams();
        params.height = viewHeight = height;
        params.topMargin = top;
        centerY = top + viewHeight / 2;
        setLayoutParams(params);
    }

    public static int getRandom(int min, int max) {
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return s;
    }

    public int getBottleCount() {
        return bottles.size();
    }

    public void addBottle(Bottle bottle) {
        BottleView bottleView = new BottleView(getContext(), bottle);
        bottles.add(bottleView);
        int x = getRandom(0, screenWidth - 135);
        int y = getRandom(0, viewHeight - 60);

        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(-2, -2);
        param.setMargins(x, y, 0, 0);
        addView(bottleView, param);
        bottleView.setBottleViewClickListener(bottleViewClickListener);
    }

    public void setBottleClickListener(BottleClickListener bottleClickListener) {
        this.bottleClickListener = bottleClickListener;
    }

    private BottleViewClickListener bottleViewClickListener = new BottleViewClickListener() {
        @Override
        public void onClick(BottleView bottleView, Bottle bottle) {
            bottles.remove(bottleView);
            removeView(bottleView);
            if (bottleClickListener != null) {
                bottleClickListener.onClick(bottle);
            }
        }
    };

    public Bottle getBottle() {
        if (bottles.size() > 0) {
            BottleView bottleView = bottles.get(0);
            bottles.remove(bottleView);
            removeView(bottleView);
            return bottleView.getBottle();
        } else {
            refreshBottle();
        }
        return null;
    }

    private void refreshBottle() {
        handler.postDelayed(refreshBottle, TIME_DELAY);
    }

    public void onDestroy() {

    }

    private Runnable refreshBottle = new Runnable() {
        @Override
        public void run() {
            BottleHttpManager.refreshBottles(refresh);
        }
    };


    private Subscriber<JSONObject> refresh = new Subscriber<JSONObject>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(JSONObject jsonObject) {
            List<Bottle> newBottles = BottleHttpManager.praseBottles(jsonObject);
            if (newBottles != null) {
                for (Bottle bottle : newBottles) {
                    addBottle(bottle);
                }
            }
        }
    };
}