package com.zhan.haoqi.bottle.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.zhan.haoqi.bottle.R;
import com.zhan.haoqi.bottle.ui.ChatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zah on 2016/10/21.
 */
public class MsgFragment extends EaseConversationListFragment {
    @BindView(R.id.title)
    TextView title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout=super.onCreateView(inflater, container, savedInstanceState);
        layout.findViewById(R.id.title_bar).setBackgroundColor(getResources().getColor(R.color.title_bar_color));
        ((TextView)layout.findViewById(R.id.title)).setText("消息");
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setConversationListItemClickListener(new EaseConversationListItemClickListener() {
            @Override
            public void onListItemClicked(EMConversation conversation) {
                String username = conversation.conversationId();
                if (username.equals(EMClient.getInstance().getCurrentUser()))
                    Toast.makeText(getActivity(), R.string.Cant_chat_with_yourself, Toast.LENGTH_SHORT).show();
                else {
                    startActivity(new Intent(getActivity(), ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, conversation.conversationId()));
                }
            }
        });
    }
}
