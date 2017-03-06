package com.zhan.haoqi.bottle.ui;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhan.haoqi.bottle.R;
import com.zhan.haoqi.bottle.ui.fragment.MineFragment;
import com.zhan.haoqi.bottle.ui.fragment.MsgFragment;
import com.zhan.haoqi.bottle.ui.fragment.SeaFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends FragmentActivity {

    public static final String ACTION_THROW_BOTTLE = "action.bottle";
    @BindView(R.id.msg_txt)
    TextView msgTxt;
    @BindView(R.id.mine_txt)
    TextView mineTxt;
    @BindView(R.id.throw_bottle_view)
    ImageView throwBottle;
    @BindView(R.id.main_root)
    RelativeLayout mainRoot;


    private String currentTagName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        showSeaTab();

        IntentFilter filter = new IntentFilter(ACTION_THROW_BOTTLE);
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void showSeaTab() {
        if (SeaFragment.class.getSimpleName().equals(currentTagName)) {
            return;
        }
        hideFragment(currentTagName);
        currentTagName = SeaFragment.class.getSimpleName();
        Fragment fragment = getFragmentManager().findFragmentByTag(currentTagName);
        if (fragment != null) {
            getFragmentManager().beginTransaction().show(fragment).commit();
        } else {
            getFragmentManager().beginTransaction().add(R.id.frame_layout, new SeaFragment(), currentTagName).commit();
        }

    }


    private void showMsgTab() {
        if (currentTagName.equals(MsgFragment.class.getSimpleName())) {
            return;
        }
        hideFragment(currentTagName);
        currentTagName = MsgFragment.class.getSimpleName();
        Fragment fragment = getFragmentManager().findFragmentByTag(currentTagName);
        if (fragment != null) {
            getFragmentManager().beginTransaction().show(fragment).commit();
        } else {
            getFragmentManager().beginTransaction().add(R.id.frame_layout, new MsgFragment(), currentTagName).commit();
        }
    }

    private void showMineTab() {
        if (currentTagName.equals(MineFragment.class.getSimpleName())) {
            return;
        }
        hideFragment(currentTagName);
        currentTagName = MineFragment.class.getSimpleName();
        Fragment fragment = getFragmentManager().findFragmentByTag(currentTagName);
        if (fragment != null) {
            getFragmentManager().beginTransaction().show(fragment).commit();
        } else {
            getFragmentManager().beginTransaction().add(R.id.frame_layout, new MineFragment(), currentTagName).commit();
        }
    }


    private void hideFragment(String tag) {
        if (tag == null) {
            return;
        }
        Fragment fragment = getFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            getFragmentManager().beginTransaction().hide(fragment).commit();
        }
    }

    @OnClick({R.id.msg_tab, R.id.sea_tab, R.id.mine_tab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.msg_tab:
                showMsgTab();
                msgTxt.setTextColor(getResources().getColor(R.color.app_color));
                mineTxt.setTextColor(getResources().getColor(R.color.black_60));
                break;
            case R.id.sea_tab:
                showSeaTab();
                msgTxt.setTextColor(getResources().getColor(R.color.black_60));
                mineTxt.setTextColor(getResources().getColor(R.color.black_60));
                break;
            case R.id.mine_tab:
                showMineTab();
                msgTxt.setTextColor(getResources().getColor(R.color.black_60));
                mineTxt.setTextColor(getResources().getColor(R.color.app_color));
                break;
        }
    }


    private void throwOutAnim() {
        if (!SeaFragment.class.getSimpleName().equals(currentTagName)) {
            return;
        }
        ObjectAnimator localObjectAnimator4 = ObjectAnimator.ofFloat(throwBottle, "translationX", new float[]{0.0F, -0.4F * MainActivity.this.mainRoot.getWidth()});
        ObjectAnimator localObjectAnimator1 = ObjectAnimator.ofFloat(throwBottle, "translationY", new float[]{0.0F, -0.54F * MainActivity.this.mainRoot.getHeight()});
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(throwBottle, "scaleX", new float[]{1.0F, 0.4F});
        ObjectAnimator localObjectAnimator3 = ObjectAnimator.ofFloat(throwBottle, "scaleY", new float[]{1.0F, 0.4F});
        ObjectAnimator localObjectAnimator2 = ObjectAnimator.ofFloat(throwBottle, "rotation", new float[]{0.0F, -1080.0F});
        float f1 = MainActivity.this.getResources().getDimensionPixelOffset(R.dimen.main_ani_bottle_h);
        float f2 = MainActivity.this.getResources().getDimensionPixelOffset(R.dimen.main_ani_bottle_w);
        throwBottle.setPivotY(f1 / 2.0F);
        throwBottle.setPivotX(f2 / 2.0F);
        AnimatorSet localAnimatorSet = new AnimatorSet();
        localAnimatorSet.setDuration(1500L);
        localAnimatorSet.playTogether(new Animator[]{localObjectAnimator4, localObjectAnimator1, localObjectAnimator2, objectAnimator, localObjectAnimator3});
        throwBottle.setVisibility(View.VISIBLE);
        localAnimatorSet.start();
        localAnimatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator paramAnonymousAnimator) {
                super.onAnimationEnd(paramAnonymousAnimator);
                throwBottle.setVisibility(View.GONE);
                int[] param = new int[2];
                throwBottle.getLocationInWindow(param);
                final ImageView localImageView = new ImageView(MainActivity.this);
                int i = MainActivity.this.getResources().getDimensionPixelSize(R.dimen.main_ani_spray_w);
                int j = MainActivity.this.getResources().getDimensionPixelSize(R.dimen.main_ani_spray_h);
                RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(i, j);
                localLayoutParams.leftMargin = (param[0] - i / 2);
                localLayoutParams.topMargin = (param[1] - j / 2);
                localImageView.setLayoutParams(localLayoutParams);
                MainActivity.this.mainRoot.addView(localImageView);
                localImageView.setImageResource(R.drawable.spray);
                ((AnimationDrawable) localImageView.getDrawable()).start();
                MainActivity.this.handler.postDelayed(new Runnable() {
                    public void run() {
                        MainActivity.this.mainRoot.removeView(localImageView);
                    }
                }, 450L);
            }
        });
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (ACTION_THROW_BOTTLE.equals(intent.getAction())) {
                    throwOutAnim();
                }
            }
        }
    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
}
