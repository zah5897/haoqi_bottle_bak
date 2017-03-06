package com.zhan.haoqi.bottle.ui.bottle;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhan.haoqi.bottle.R;
import com.zhan.haoqi.bottle.adapter.CommentAdapter;
import com.zhan.haoqi.bottle.adapter.RecyclerViewItemLongClickListener;
import com.zhan.haoqi.bottle.adapter.SimpleItemDecoration;
import com.zhan.haoqi.bottle.data.Bottle;
import com.zhan.haoqi.bottle.data.Comment;
import com.zhan.haoqi.bottle.data.UserManager;
import com.zhan.haoqi.bottle.http.BaseSubscriber;
import com.zhan.haoqi.bottle.http.BottleHttpManager;
import com.zhan.haoqi.bottle.util.AppUtils;
import com.zhan.haoqi.bottle.util.ImageShowUtil;
import com.zhan.haoqi.bottle.util.JSONToBeanUtil;
import com.zhan.haoqi.bottle.util.To;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import haoqi.emoji.main.EaseEmojicon;
import haoqi.emoji.ui.view.EmojiEditText;
import haoqi.emoji.ui.view.EmojiKeyboard;

/**
 * Created by zah on 2016/12/16.
 */

public class BottleResponseActivity extends Activity implements EmojiKeyboard.EventListener, RecyclerViewItemLongClickListener {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.at_user)
    TextView atUserView;
    @BindView(R.id.bottle_detail_comments_et)
    EmojiEditText bottleDetailCommentsEt;
    @BindView(R.id.bottle_detail_face)
    EmojiKeyboard bottleDetailFace;
    @BindView(R.id.bottle_detail_face_container)
    LinearLayout bottleDetailFaceContainer;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    private Bottle bottle;

    private CommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bottle = getIntent().getParcelableExtra("bottle");
        if (bottle == null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_respond_bottle);
        ButterKnife.bind(this);
        setRecyclerview();
        bottleDetailFace.setEventListener(this);
        title.setText(bottle.getSender().nick_name);
        loadComments(false);
    }


    private void setRecyclerview() {
        recyclerview.setHasFixedSize(true);
        adapter = new CommentAdapter(this, bottle);
        recyclerview.addItemDecoration(new SimpleItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerview.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
//        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));//这里用线性宫格显示 类似于grid view
//        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));//这里用线性宫格显示 类似于瀑布流
        recyclerview.setAdapter(adapter);
        adapter.setRecyclerItemLongClickListener(this);
    }

    @OnClick({R.id.back, R.id.bottle_detail_face_ib})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.bottle_detail_face_ib:
                bottleDetailFaceContainer.setVisibility(View.VISIBLE);
                break;
            case R.id.bottle_detail_comments_et:
                bottleDetailFaceContainer.setVisibility(View.GONE);
                AppUtils.showSoftInputIsShow(this, bottleDetailCommentsEt);
                break;
            case R.id.bottle_detail_send_ib:

                String text = bottleDetailCommentsEt.getText().toString();
                if (TextUtils.isEmpty(text.trim())) {
                    To.show("评论内容不能为空！");
                    return;
                }
                AppUtils.hideSoftInputIsShow(this, bottleDetailCommentsEt);
                submitComment(text);

                break;
        }
    }

    private void submitComment(String comment) {

        final Object obj = atUserView.getTag();
        long at_comment_id = 0;
        if (obj != null) {
            at_comment_id=((Comment)obj).getId();
        }
        BottleHttpManager.submitComment(bottle.getId(), bottle.getSender().id, comment,at_comment_id, new BaseSubscriber<JSONObject>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onNext(JSONObject o) {
                int code = o.optInt("code");
                if (code == 0) {
                    JSONObject commentObj = o.optJSONObject("comment");
                    Comment comment = JSONToBeanUtil.jsonToComment(commentObj);
                    comment.setUser(UserManager.getInstance().getUser());
                    comment.setAt_comment((Comment) obj);
                    bottleDetailCommentsEt.setText("");
                    adapter.addComment(comment);
                    recyclerview.smoothScrollToPosition(adapter.getItemCount());
                    atUserView.setText("");
                    atUserView.setTag(null);
                } else {
                    To.show("发送失败！");
                }
            }
        });
    }

    private void loadComments(final boolean isLoadMore) {
        Comment lastComment = adapter.getLastComment();
        long lastId = 0;
        if (lastComment != null) {
            lastId = lastComment.getId();
        }
        BottleHttpManager.reloadComments(bottle.getId(), isLoadMore ? lastId : 0, new BaseSubscriber<JSONObject>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onNext(JSONObject jsonObject) {
                int code = jsonObject.optInt("code");
                if (code == 0) {
                    List<Comment> comments = JSONToBeanUtil.toComments(jsonObject.optJSONArray("comments"));
                    if (comments != null) {
                        if (isLoadMore) {
                            adapter.add(comments);
                        } else {
                            adapter.resetData(comments);
                        }
                    } else {
                        if (isLoadMore) {
                            // adapter.add(comments); 沒有更多了
                        } else {
                            adapter.clear();
                        }
                    }
                } else {
                    To.show("网络异常，请稍刷新！");
                }
            }
        });
    }

    @Override
    public void onBackspace() {
        EmojiKeyboard.backspace(this.bottleDetailCommentsEt);
    }

    @Override
    public void onEmojiSelected(EaseEmojicon emojicon) {
        EmojiKeyboard.input(this.bottleDetailCommentsEt, emojicon);
    }

    @Override
    public void onItemLongClick(View view, Object comment) {
        if (comment != null) {
            Comment commentOjb = (Comment) comment;
            atUserView.setText("@" + commentOjb.getUser().nick_name);
            atUserView.setTag(comment);
        }
    }
}
