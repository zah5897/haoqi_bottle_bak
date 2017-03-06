package com.zhan.haoqi.bottle.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zah on 2017/2/17.
 */

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bindData(int position);
}