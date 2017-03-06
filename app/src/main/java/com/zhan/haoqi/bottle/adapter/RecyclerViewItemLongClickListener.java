package com.zhan.haoqi.bottle.adapter;

import android.view.View;

import com.zhan.haoqi.bottle.data.Comment;

/**
 * Created by zah on 2017/2/27.
 */

public interface RecyclerViewItemLongClickListener<T> {
    void onItemLongClick(View view, T t);
}
