package com.zhan.haoqi.bottle.adapter;

import android.view.View;

/**
 * Created by zah on 2017/2/27.
 */

public interface RecyclerViewItemClickListener<T> {
    void onItemClick(View view , T t);
}
