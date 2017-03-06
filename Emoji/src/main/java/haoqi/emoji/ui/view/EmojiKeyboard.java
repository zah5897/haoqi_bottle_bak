package haoqi.emoji.ui.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import haoqi.emoji.R;
import haoqi.emoji.main.EaseEmojicon;
import haoqi.emoji.util.EmojiUtils;

public class EmojiKeyboard extends FrameLayout {
    private final static String EMOJI_PREFERENCE = "emoji_preferences";
    private final static String PREF_KEY_LAST_TAB = "last_tab";
    private final static String PREF_KEY_RECENT_EMOJI = "recent_remoji";
    private final static int[] mIcons = {R.drawable.ic_emoji_recent_light, R.drawable.ic_emoji_people_light};
    private ViewPager mPager;
    private ImageButton mBackSpace;
    private PagerSlidingTabStrip mTabs;
    private View mEmptyView;
    private GridView mRecentGridView;
    private SharedPreferences mPreference;
    private ArrayList<View> mViews;
    private EmojiPagerAdapter mPagerAdapter;
    private Handler mHandler = new Handler();
    private boolean mContinueDel;

    private EventListener mListener;

    public void setEventListener(EventListener listener) {
        this.mListener = listener;
    }

    public static interface EventListener {
        public void onBackspace();

        public void onEmojiSelected(EaseEmojicon emojicon);
    }

    public EmojiKeyboard(Context context) {
        super(context);
        init();
    }

    public EmojiKeyboard(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    private void init() {
        mPreference = getContext().getSharedPreferences(EMOJI_PREFERENCE, Context.MODE_PRIVATE);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View root = inflater.inflate(R.layout.emoji_keyboard, this);
        mTabs = (PagerSlidingTabStrip) root.findViewById(R.id.tabs);
        mPager = (ViewPager) root.findViewById(R.id.parent_pager);
        mBackSpace = (ImageButton) root.findViewById(R.id.back_space);
        mViews = new ArrayList<View>();
        for (int i = 0; i < mIcons.length; i++) {
            if (i == 0) {
                EmojiGridAdapter emojiGridAdapter = new EmojiGridAdapter(getContext(), new ArrayList<EaseEmojicon>());
                View mRecentsWrap = inflater.inflate(R.layout.tab_emoji_recent, null);
                mRecentGridView = (GridView) mRecentsWrap.findViewById(R.id.grid);
                mRecentGridView.setAdapter(emojiGridAdapter);
                mRecentGridView.setOnItemClickListener(mRecentItemClickListener);
                mEmptyView = mRecentsWrap.findViewById(R.id.no_recent);
                mViews.add(mRecentsWrap);
            } else {
                EmojiLinearLayout emojiView = (EmojiLinearLayout) inflater.inflate(R.layout.tab_emoji_qq, null);
                emojiView.setOnEmojiClickedListener(mOnEmojiClickedListener);
                mViews.add(emojiView);
            }
        }
        loadRecent();
        mPagerAdapter = new EmojiPagerAdapter();
        mPager.setAdapter(mPagerAdapter);
        mTabs.setOnPageChangeListener(mOnPageChangeListener);
        mTabs.setViewPager(mPager);
        mPager.setCurrentItem(mPreference.getInt(PREF_KEY_LAST_TAB, 1));
        mBackSpace.setOnClickListener(mBackSpaceClickListener);
        mBackSpace.setOnLongClickListener(mBackSpaceLongClickListener);
        mBackSpace.setOnTouchListener(mBackSpaceTouchListener);
    }

    private OnItemClickListener mRecentItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            int iconId = (int) view.getTag(view.getId());
            EaseEmojicon icon = EmojiUtils.getInstance().getIconById(iconId);
            if (icon != null && mListener != null)
                mListener.onEmojiSelected(icon);
        }
    };
    private OnClickListener mBackSpaceClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null)
                mListener.onBackspace();
        }
    };
    private OnLongClickListener mBackSpaceLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (mListener == null) {
                return false;
            }
            mContinueDel = true;
            mHandler.post(mContinueDelRunnable);
            return false;
        }
    };

    private OnTouchListener mBackSpaceTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            if ((action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) && mContinueDel) {
                mContinueDel = false;
                mHandler.removeCallbacks(mContinueDelRunnable);
            }
            return false;
        }
    };

    private Runnable mContinueDelRunnable = new Runnable() {
        @Override
        public void run() {
            if (mContinueDel) {
                mListener.onBackspace();
                mHandler.postDelayed(this, 50);
            }
        }
    };

    private EmojiLinearLayout.OnEmojiClickedListener mOnEmojiClickedListener = new EmojiLinearLayout.OnEmojiClickedListener() {

        @Override
        public void onEmojiClicked(EaseEmojicon emoji) {
            if (mListener != null)
                mListener.onEmojiSelected(emoji);
            addToRecent(emoji);
        }
    };
    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int item) {
            mPreference.edit().putInt(PREF_KEY_LAST_TAB, item).commit();
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };


    private void loadRecent() {
        String recentEmoji = mPreference.getString(PREF_KEY_RECENT_EMOJI, null);
        if (!TextUtils.isEmpty(recentEmoji)) {
            mEmptyView.setVisibility(View.GONE);
            String[] recentEmojis = recentEmoji.split(",");
            Collections.reverse(Arrays.asList(recentEmojis));
            EmojiGridAdapter recentAdapter = (EmojiGridAdapter) mRecentGridView.getAdapter();
            List<EaseEmojicon> recents = new ArrayList<>();
            for (String sid : recentEmojis) {
                EaseEmojicon icon = EmojiUtils.getInstance().getIconById(Integer.parseInt(sid));
                if (icon != null) {
                    recents.add(icon);
                }
            }
            recentAdapter.setEmoji(recents);
            recentAdapter.notifyDataSetChanged();
        }
    }


    private void addToRecent(EaseEmojicon selected) {
        String recentEmoji = mPreference.getString(PREF_KEY_RECENT_EMOJI, null);
        if (TextUtils.isEmpty(recentEmoji)) {
            recentEmoji = selected.getIcon() + ",";
        } else {
            String[] recs = recentEmoji.split(",");
            List<String> list = Arrays.asList(recs);
            List<String> newList = new ArrayList<String>(list);
            String selected_id = String.valueOf(selected.getIcon());
            for (int i = newList.size() - 1; i >= 0; i--) {
                if (newList.get(i).equals(selected_id)) {
                    newList.remove(i);
                    break;
                }
            }
            newList.add(selected_id);
            if (newList.size() > 21)
                newList.remove(0);// 大于21个表情时，删除最后一个
            StringBuilder builder = new StringBuilder();
            for (String str : newList) {
                builder.append(str).append(",");
            }
            recentEmoji = builder.toString();
        }
        mPreference.edit().putString(PREF_KEY_RECENT_EMOJI, recentEmoji).commit();
        loadRecent();
    }

    public static void input(EditText editText, EaseEmojicon emojicon) {
        if (editText == null || emojicon == null) {
            return;
        }

        Spannable spannable = EmojiUtils.getInstance().getSmiledText(editText.getContext(), emojicon.getEmojiText());
        int end = editText.getSelectionEnd();
        int len = editText.getText().length();
        if (end == len) {
            editText.append(spannable);
        } else {
            editText.getEditableText().insert(end, spannable);
        }
    }

    public static void backspace(EditText editText) {
        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
        editText.dispatchKeyEvent(event);
    }

    class EmojiGridAdapter extends BaseAdapter {
        private List<EaseEmojicon> mEmojis;
        private LayoutInflater mInflater;
        private ViewHolder viewHolder;

        public EmojiGridAdapter(Context c, List<EaseEmojicon> emojis) {
            mInflater = LayoutInflater.from(c);
            mEmojis = emojis;
        }

        public void setEmoji(List<EaseEmojicon> emojis) {
            mEmojis = emojis;
        }

        public int getCount() {
            return mEmojis.size();
        }

        public EaseEmojicon getItem(int position) {
            return mEmojis.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (convertView == null) {
                rowView = mInflater.inflate(R.layout.emoji_cell, null);
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
//                else if(emojicon.getIconPath() != null){
//                    Glide.with(getContext()).load(emojicon.getIconPath()).placeholder(R.drawable.ease_default_expression).into(imageView);
//                }
            }
            return rowView;
        }
    }

    static class ViewHolder {
        public ImageView imageView;

        public ViewHolder(ImageView imageView) {
            this.imageView = imageView;
        }

        ;
    }

    private class EmojiPagerAdapter extends PagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

        private EmojiPagerAdapter() {
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object paramObject) {
            container.removeView(mViews.get(position));
        }

        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public int getPageIconResId(int paramInt) {
            return mIcons[paramInt];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View gridView = mViews.get(position);
            container.addView(gridView);
            return gridView;
        }

        @Override
        public boolean isViewFromObject(View paramView, Object paramObject) {
            return paramView == paramObject;
        }
    }
}
