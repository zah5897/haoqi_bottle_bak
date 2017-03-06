package com.zhan.haoqi.bottle.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhan.haoqi.bottle.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zah on 2016/10/21.
 */
public class MsgFragment extends Fragment {
    @BindView(R.id.title)
    TextView title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_msg, container, false);
        ButterKnife.bind(this, view);
        title.setText("消息");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
