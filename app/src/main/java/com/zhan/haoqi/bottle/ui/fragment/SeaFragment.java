package com.zhan.haoqi.bottle.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.zhan.haoqi.bottle.R;
import com.zhan.haoqi.bottle.data.Bottle;
import com.zhan.haoqi.bottle.data.type.BottleType;
import com.zhan.haoqi.bottle.http.BaseSubscriber;
import com.zhan.haoqi.bottle.http.BottleHttpManager;
import com.zhan.haoqi.bottle.ui.bottle.BottleShowActivity;
import com.zhan.haoqi.bottle.ui.bottle.EditBottleActivity;
import com.zhan.haoqi.bottle.ui.listener.BottleClickListener;
import com.zhan.haoqi.bottle.util.DensityUtil;
import com.zhan.haoqi.bottle.util.To;
import com.zhan.haoqi.bottle.view.BottlePoolLayout;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zah on 2016/10/21.
 */
public class SeaFragment extends Fragment {
    @BindView(R.id.bottle_container)
    BottlePoolLayout bottleContainer;
    @BindView(R.id.net_bottle)
    ImageView netBottle;
    ImageView leftRightNet;
    @BindView(R.id.sea)
    RelativeLayout seaLayout;

    private final int netSize = 130;//dp

    boolean netting = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sea, container, false);
        ButterKnife.bind(this, view);
        bottleContainer.setBottleClickListener(bottleClickListener);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bottleContainer.onDestroy();
    }

    @OnClick({R.id.throw_bottle, R.id.catch_bottle})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.throw_bottle:
                startActivity(new Intent(getActivity(), EditBottleActivity.class));
                break;
            case R.id.catch_bottle:
                netBottle();
                break;
        }
    }

    private void netBottle() {

        if (netting) {
            return;
        }
        netting = true;
        startNetAnim();
    }

    private void startNetAnim() {
        netBottle.setVisibility(View.VISIBLE);
        int top = bottleContainer.getCenterY();
        int bottom = (int) (getResources().getDisplayMetrics().heightPixels - (DensityUtil.dip2px(getActivity(), 115) + DensityUtil.dip2px(getActivity(), netSize) * 0.85));
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, top - bottom);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                netBottle.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                netBottle.setVisibility(View.GONE);
                startLeftRightAnim();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        netBottle.startAnimation(animation);
    }

    private void startLeftRightAnim() {
        if (leftRightNet == null) {
            leftRightNet = new ImageView(getActivity());
            leftRightNet.setImageResource(R.mipmap.bottle_netting);
            int height = (int) (DensityUtil.dip2px(getActivity(), netSize) / 1.36f);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(height / 2, height);
            params.setMargins(getResources().getDisplayMetrics().widthPixels / 2 - height / 4 + 20, bottleContainer.getCenterY() - height, 0, 0);
            seaLayout.addView(leftRightNet, params);
        }
        leftRightNet.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.netting_bottle);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (bottleContainer.getBottleCount() == 0) {
                    BottleHttpManager.netBottle(netBottleSubscriber);
                } else {
                    onNetBottle(bottleContainer.getBottle());
                }

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onNetBottle(null);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                if (bottleContainer.getBottleCount() > 0) {
                    onNetBottle(bottleContainer.getBottle());
                }
            }
        });
        leftRightNet.startAnimation(animation);
    }


    void onNetBottle(Bottle bottle) {
        netting = false;
        if (bottle != null) {
            bottleClickListener.onClick(bottle);
        }
        if (leftRightNet != null) {
            leftRightNet.clearAnimation();
            leftRightNet.setVisibility(View.GONE);
        }
    }


    private void openBottle(Bottle bottle) {
        Intent show = new Intent(getActivity(), BottleShowActivity.class);
        show.putExtra("bottle", bottle);
        startActivity(show);
    }

    private BottleClickListener bottleClickListener = new BottleClickListener() {
        @Override
        public void onClick(Bottle bottle) {
            openBottle(bottle);
        }
    };


    private BaseSubscriber<JSONObject> netBottleSubscriber = new BaseSubscriber<JSONObject>() {
        @Override
        public void onCompleted() {
            onNetBottle(null);
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            onNetBottle(null);
        }

        @Override
        public void onNext(JSONObject jsonObject) {
            onNetBottle(BottleHttpManager.praseBottle(jsonObject));
        }
    };
}
