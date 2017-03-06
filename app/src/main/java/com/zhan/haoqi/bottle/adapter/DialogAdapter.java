package com.zhan.haoqi.bottle.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhan.haoqi.bottle.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogAdapter
        extends BaseAdapter {
    private Context context;
    private ArrayList<String> list;
    private Resources rs;

    public DialogAdapter(Context paramContext, ArrayList<String> paramArrayList) {
        this.context = paramContext;
        this.list = paramArrayList;
        this.rs = paramContext.getResources();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.dialog_as_listitem_1, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.itemDialogTv.setText(getItem(i));
        return view;
    }

    static class ViewHolder {
        @BindView(R.id.item_dialog_tv)
        TextView itemDialogTv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
