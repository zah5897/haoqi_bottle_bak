package haoqi.emoji.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import haoqi.emoji.R;
import haoqi.emoji.main.EaseEmojicon;


public class EmojiAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private ViewHolder viewHolder;
    List<EaseEmojicon> emojicons;

    public EmojiAdapter(Context context, List<EaseEmojicon> emojicons) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.emojicons = emojicons;
    }

    @Override
    public int getCount() {
        return emojicons.size();
    }

    @Override
    public EaseEmojicon getItem(int position) {
        return emojicons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (convertView == null) {
            rowView = mLayoutInflater.inflate(R.layout.emoji_cell, null);
            viewHolder = new ViewHolder((ImageView) rowView);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }
        EaseEmojicon emojicon = getItem(position);

        if (EmojiUtils.DELETE_KEY.equals(emojicon.getEmojiText())) {
            viewHolder.imageView.setImageResource(R.drawable.btn_emotion_delete);
        } else {
            if (emojicon.getIcon() != 0) {
                viewHolder.imageView.setImageResource(emojicon.getIcon());

                viewHolder.imageView.setTag(viewHolder.imageView.getId(), emojicon.getIcon());

            }
        }
        return rowView;
    }

    static class ViewHolder {
        public ImageView imageView;

        public ViewHolder(ImageView imageView) {
            this.imageView = imageView;
        }

        ;
    }
}