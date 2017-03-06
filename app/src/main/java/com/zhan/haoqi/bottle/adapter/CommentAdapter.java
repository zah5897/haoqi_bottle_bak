package com.zhan.haoqi.bottle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhan.haoqi.bottle.R;
import com.zhan.haoqi.bottle.data.Bottle;
import com.zhan.haoqi.bottle.data.Comment;
import com.zhan.haoqi.bottle.util.ImageShowUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zah on 2017/1/13.
 */

public class CommentAdapter extends BaseRecyclerAdapter implements View.OnClickListener, View.OnLongClickListener {
    private LayoutInflater inflater;
    private List<Comment> comments;
    Bottle bottle;
    Comment bottleMain;
    Context context;

    public CommentAdapter(Context context, Bottle bottle) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.bottle = bottle;
        comments = new ArrayList<>();
        bottleMain = new Comment();
        bottleMain.setId(-1);
        comments.add(bottleMain);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout;
        RecyclerView.ViewHolder viewHolder;
        if (viewType == 0) {
            layout = inflater.inflate(R.layout.item_bottle_comment_main, parent, false);
            viewHolder = new BottleHolder(context, layout, bottle);
            layout.setOnClickListener(null);
        } else if (viewType == 1) {
            layout = inflater.inflate(R.layout.item_bottle_comment_single, parent, false);
            viewHolder = new CommentHolder(context, layout);
            layout.setOnClickListener(this);
            layout.setOnLongClickListener(this);
        } else {
            layout = inflater.inflate(R.layout.item_bottle_comment_at, parent, false);
            viewHolder = new CommentHolder(context, layout);
            layout.setOnClickListener(this);
            layout.setOnLongClickListener(this);
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BottleHolder) {
            ((BottleHolder) holder).bindData(position);
        } else if (holder instanceof CommentHolder) {
             Comment comment = comments.get(position);
            ((CommentHolder) holder).bindData(position, comment);
        }

    }

    @Override
    public int getItemViewType(int position) {
        long id = getItemId(position);

        if (id == -1) {
            return 0;
        } else {
            Comment comment = comments.get(position);
            if (comment.getAt_comment() == null) {
                return 1;
            } else {
                return 2;
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return comments.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void add(List<Comment> comments) {
        this.comments.addAll(comments);
        notifyDataSetChanged();
    }


    public void resetData(List<Comment> data) {
        this.comments.clear();
        this.comments.add(0, bottleMain);
        this.comments.addAll(data);
        notifyDataSetChanged();
    }

    public void clear() {
        comments.clear();
        comments.add(0, bottleMain);
        notifyDataSetChanged();
    }

    public Comment getLastComment() {
        if (comments.size() > 1) {
            return comments.get(1);
        } else {
            return null;
        }
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (itemClickListener != null) {
            itemClickListener.onItemClick(v, v.getTag());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (itemLongClickListener != null) {
            itemLongClickListener.onItemLongClick(v, v.getTag());
            return true;
        }
        return false;
    }

    static class CommentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.avatar)
        ImageView avatar;
        @BindView(R.id.nick_name)
        TextView nickName;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.comment_time)
        TextView commentTime;
        Context context;

        TextView atNickName;
        TextView atContentTxt;
        TextView atTime;

        public CommentHolder(Context context, View itemView) {
            this(itemView);
            ButterKnife.bind(this, itemView);

            atNickName = (TextView) itemView.findViewById(R.id.at_nick_name);
            atContentTxt = (TextView) itemView.findViewById(R.id.at_content);
            atTime = (TextView) itemView.findViewById(R.id.at_comment_time);

            this.context = context;
        }

        public CommentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(itemView);
        }

        public void bindData(int position, Comment comment) {
            itemView.setTag(comment);
            avatar.setImageResource(R.mipmap.bottle);
            if (!TextUtils.isEmpty(comment.getUser().avatar)) {
                ImageShowUtil.display(context, comment.getUser().avatar, avatar, true);
            }
            nickName.setText(comment.getUser().nick_name);
            content.setText(comment.getContent());
            commentTime.setText(comment.getComment_time());

            if (comment.getAt_comment() != null) {
                atNickName.setText(comment.getAt_comment().getUser().nick_name);
                atContentTxt.setText(comment.getAt_comment().getContent());
                atTime.setText(comment.getAt_comment().getComment_time());
            }
        }
    }

    static class BottleHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.author_user_avatar)
        ImageView authorUserAvatar;
        @BindView(R.id.nick_name)
        TextView nickName;
        @BindView(R.id.gender)
        ImageView gender;
        @BindView(R.id.content_txt)
        TextView contentTxt;
        @BindView(R.id.content_img)
        ImageView contentImg;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.comment_count)
        TextView commentCount;
        @BindView(R.id.child)
        LinearLayout child;
        Bottle bottle;
        Context context;

        public BottleHolder(Context context, View itemView, Bottle bottle) {
            this(itemView);
            ButterKnife.bind(this, itemView);
            this.context = context;
            this.bottle = bottle;
        }

        public BottleHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(itemView);
        }

        public void bindData(int position) {
            authorUserAvatar.setImageResource(R.mipmap.bottle);
            if (!TextUtils.isEmpty(bottle.getSender().avatar)) {
                ImageShowUtil.display(context, bottle.getSender().avatar, authorUserAvatar, true);
            }
            nickName.setText(bottle.getSender().nick_name);

            gender.setImageResource(bottle.getSender().gender == 0 ? R.mipmap.user_gender_female : R.mipmap.user_gender_male);
            time.setText(bottle.getCreate_time());
            commentCount.setText(String.valueOf(0) + "个评论");

            if (!TextUtils.isEmpty(bottle.getContent())) {
                contentTxt.setText(bottle.getContent());
            }
            if (!TextUtils.isEmpty(bottle.getImage())) {
                ImageShowUtil.display(context, bottle.getImage(), contentImg, true);
            }
        }
    }
}
