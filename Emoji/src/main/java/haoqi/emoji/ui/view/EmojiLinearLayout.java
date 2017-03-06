package haoqi.emoji.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import haoqi.emoji.R;
import haoqi.emoji.main.EaseEmojicon;
import haoqi.emoji.ui.pagerindicator.CirclePageIndicator;
import haoqi.emoji.util.EmojiAdapter;
import haoqi.emoji.util.EmojiUtils;
import haoqi.emoji.util.EmojiViewPageAdapter;


public class EmojiLinearLayout extends LinearLayout implements
        OnItemClickListener {
    // 横屏时
    private static final int LAND_COLUMN = 11;// 11列
    private static final int LAND_ROW = 2;// 2行
    // 竖屏时
    private static final int PORT_COLUMN = 7;// 7列
    private static final int PORT_ROW = 3;// 3行
    private ViewPager mPager;
    private int mViewPagerNum;

    private OnEmojiClickedListener mOnEmojiClickedListener;

    public void setOnEmojiClickedListener(
            OnEmojiClickedListener onEmojiClickedListener) {
        mOnEmojiClickedListener = onEmojiClickedListener;
    }

    public interface OnEmojiClickedListener {
        void onEmojiClicked(EaseEmojicon emoji);
    }

    public EmojiLinearLayout(Context context) {
        super(context);
    }

    public EmojiLinearLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPager = (ViewPager) findViewById(R.id.child_pager);
        mViewPagerNum = getEmojiSize();
        List<GridView> lv = new ArrayList<GridView>();
        for (int i = 0; i < mViewPagerNum; ++i) {
            lv.add(getGridView(LayoutInflater.from(getContext()), i));
        }

        EmojiViewPageAdapter adapter = new EmojiViewPageAdapter(lv);
        mPager.setAdapter(adapter);
        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
    }

    private int getEmojiSize() {
        int size = EmojiUtils.getInstance().getEmojiIconSize();
        if (size % (PORT_COLUMN * PORT_ROW) == 0)// 刚好被整除
        {
            return size / (PORT_COLUMN * PORT_ROW);
        } else {
            return (size / (PORT_COLUMN * PORT_ROW)) + 1;
        }
    }

    private GridView getGridView(LayoutInflater inflater, int i) {
        GridView gv = (GridView) inflater.inflate(R.layout.emoji_grid, null);
        int itemSize = PORT_COLUMN * PORT_ROW;
        List<EaseEmojicon> emojiconList = Arrays.asList(EmojiUtils.getInstance().getEmojiIcons());

        int pageSize = getEmojiSize();
        List<EaseEmojicon> list = new ArrayList<EaseEmojicon>();
        if (i != pageSize - 1) {
            list.addAll(emojiconList.subList(i * itemSize, (i + 1) * itemSize));
        } else {
            list.addAll(emojiconList.subList(i * itemSize, EmojiUtils.getInstance().getEmojiIconSize()));
        }
        gv.setAdapter(new EmojiAdapter(getContext(), list));
        gv.setOnTouchListener(forbidenScroll());
        gv.setOnItemClickListener(this);
        return gv;
    }

    // 防止乱pageview乱滚动
    private OnTouchListener forbidenScroll() {
        return new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        int iconId = (int) view.getTag(view.getId());
        EaseEmojicon icon = EmojiUtils.getInstance().getIconById(iconId);
        if (icon != null && mOnEmojiClickedListener != null) {
            mOnEmojiClickedListener.onEmojiClicked(icon);
        }
    }
}
