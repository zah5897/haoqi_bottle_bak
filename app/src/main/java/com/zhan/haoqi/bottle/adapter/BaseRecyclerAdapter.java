package com.zhan.haoqi.bottle.adapter;

import android.support.v7.widget.RecyclerView;

/**
 * Created by zah on 2017/2/27.
 */

public abstract class BaseRecyclerAdapter extends RecyclerView.Adapter {
    protected  RecyclerViewItemClickListener itemClickListener;
    protected  RecyclerViewItemLongClickListener itemLongClickListener;
    public void setRecyclerItemClickListener(RecyclerViewItemClickListener itemClickListener) {
        this.itemClickListener=itemClickListener;
    }

    public void setRecyclerItemLongClickListener(RecyclerViewItemLongClickListener itemLongClickListener) {
        this.itemLongClickListener=itemLongClickListener;
    }
}
