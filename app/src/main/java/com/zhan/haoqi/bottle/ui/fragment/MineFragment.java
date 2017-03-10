package com.zhan.haoqi.bottle.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhan.haoqi.bottle.R;
import com.zhan.haoqi.bottle.data.User;
import com.zhan.haoqi.bottle.data.UserManager;
import com.zhan.haoqi.bottle.http.HttpHelper;
import com.zhan.haoqi.bottle.ui.LoginActivity;
import com.zhan.haoqi.bottle.ui.setting.SettingActivity;
import com.zhan.haoqi.bottle.ui.user.UserInfoEditActivity;
import com.zhan.haoqi.bottle.util.ImageShowUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zah on 2016/10/21.
 */
public class MineFragment extends Fragment {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.avatar)
    ImageView avatar;
    @BindView(R.id.nick_name)
    TextView nickName;
    @BindView(R.id.signature)
    TextView signature;
    @BindView(R.id.integral)
    TextView integral;
    private User user;
    View layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.activity_mine, container, false);
        ButterKnife.bind(this, layout);
        title.setText("我");
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setInfo();
        setInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
        setInfo();
    }

    private void setInfo() {
        if (!UserManager.getInstance().isLogin()) {
            layout.findViewById(R.id.user_info_item_layout).setVisibility(View.GONE);
            layout.findViewById(R.id.no_login_tip).setVisibility(View.VISIBLE);
            avatar.setImageResource(R.mipmap.bottle);
        } else {
            layout.findViewById(R.id.user_info_item_layout).setVisibility(View.VISIBLE);
            layout.findViewById(R.id.no_login_tip).setVisibility(View.GONE);
            user = UserManager.getInstance().getUser();
            setUserInfo();
        }

    }


    private void setUserInfo() {

        if (!TextUtils.isEmpty(user.avatar)) {
            if (user.avatar.startsWith("http")) {
                ImageShowUtil.display(this, user.avatar, avatar, false);
            } else {
                ImageShowUtil.display(this, HttpHelper.getAvatarProFix(user.avatar), avatar, false);
            }
        } else {
            avatar.setImageResource(R.mipmap.bottle);
        }


        nickName.setText(user.nick_name);
        Drawable drawable = getResources().getDrawable(user.gender == 0 ? R.mipmap.user_gender_female : R.mipmap.user_gender_male);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        nickName.setCompoundDrawables(null, null, drawable, null);


        if (TextUtils.isEmpty(user.signature)) {
            layout.findViewById(R.id.signature_layout).setVisibility(View.GONE);
        } else {
            layout.findViewById(R.id.signature_layout).setVisibility(View.VISIBLE);
            signature.setText(user.signature);
        }

        integral.setText("金币：" + user.integral);


    }

    @OnClick({R.id.user_info_layout, R.id.my_bottle, R.id.my_tool, R.id.setting})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_info_layout:
                if (!UserManager.getInstance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), UserInfoEditActivity.class));
                }
                break;
            case R.id.my_bottle:
                break;
            case R.id.my_tool:
                break;
            case R.id.setting:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
        }

    }
}
