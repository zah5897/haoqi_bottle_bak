package com.zhan.haoqi.bottle.ui.bottle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhan.haoqi.bottle.R;
import com.zhan.haoqi.bottle.data.Bottle;
import com.zhan.haoqi.bottle.data.UserManager;
import com.zhan.haoqi.bottle.http.HttpHelper;
import com.zhan.haoqi.bottle.ui.MainActivity;
import com.zhan.haoqi.bottle.ui.image.ReviewSelectedImageActivity;
import com.zhan.haoqi.bottle.ui.user.UserInfoActiviy;
import com.zhan.haoqi.bottle.util.ImageShowUtil;
import com.zhan.haoqi.bottle.util.MediaManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zah on 2016/12/7.
 */

public class BottleShowActivity extends Activity {
    Bottle bottle;
    @BindView(R.id.nick_name)
    TextView nickName;
    @BindView(R.id.gender)
    ImageView gender;
    @BindView(R.id.create_time)
    TextView createTime;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.sender_avatar)
    ImageView senderAvatar;
    @BindView(R.id.content_layout)
    RelativeLayout contentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bottle = getIntent().getParcelableExtra("bottle");
        if (bottle == null) {
            finish();
            return;
        }
        if (bottle.getSender() == null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_bottle_show);
        ButterKnife.bind(this);
        if (TextUtils.isEmpty(bottle.getImage())) {
            image.setVisibility(View.GONE);
        } else {
            image.setVisibility(View.VISIBLE);
            ImageShowUtil.display(this, HttpHelper.getImgThumbFix(bottle.getImage()), image, false);
        }

        if (!TextUtils.isEmpty(bottle.getContent())) {
            content.setText(bottle.getContent(), TextView.BufferType.SPANNABLE);
        }

        createTime.setText(bottle.getCreate_time());
        if (!TextUtils.isEmpty(bottle.getSender().avatar)) {
            ImageShowUtil.display(this, HttpHelper.getAvatarProFix(bottle.getSender().avatar), senderAvatar, false);
        }

        nickName.setText(bottle.getSender().nick_name);
    }

    @OnClick({R.id.sender_info_click, R.id.report, R.id.throw_out, R.id.say_hi, R.id.image})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sender_info_click:
                Intent userInfo = new Intent(this, UserInfoActiviy.class);
                userInfo.putExtra("user", bottle.getSender());
                startActivity(userInfo);
                break;
            case R.id.report:
                finish();
                break;
            case R.id.throw_out:
                sendBroadcast(new Intent(MainActivity.ACTION_THROW_BOTTLE));
                finish();
                break;
            case R.id.say_hi:

                if(!UserManager.getInstance().isLogin()){
                    UserManager.getInstance().toLogin(BottleShowActivity.this);
                    return ;
                }
                Intent toResponse=new Intent(this,BottleResponseActivity.class);
                toResponse.putExtra("bottle",bottle);
                startActivity(toResponse);
                finish();
                break;
            case R.id.image:
                Intent review = new Intent(this, ReviewSelectedImageActivity.class);
                review.putExtra("url", HttpHelper.getImgOriginFix(bottle.getImage()));
                startActivityForResult(review, MediaManager.REQUEST_REVIEW);
                break;
        }
    }
}
