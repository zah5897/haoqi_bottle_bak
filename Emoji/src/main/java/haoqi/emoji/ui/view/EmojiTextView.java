package haoqi.emoji.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import haoqi.emoji.R;
import haoqi.emoji.util.AnimatedGifDrawable;
import haoqi.emoji.util.AnimatedImageSpan;
import haoqi.emoji.util.EmojiUtils;

/**
 * @author way
 */
public class EmojiTextView extends TextView {
    private static final String START_CHAR = "[";
    private static final String END_CHAR = "]";
    private boolean mIsDynamic;

    public EmojiTextView(Context context) {
        super(context);
        init(null);
    }

    public EmojiTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EmojiTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs == null) {
            mIsDynamic = false;
        } else {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Emojicon);
            mIsDynamic = a.getBoolean(R.styleable.Emojicon_isDynamic, false);
            a.recycle();
        }
        setText(getText());
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text != null) {
            // 解决只有表情图片时，表情图片上半部分显示不完全的问题
            text = text + " ";
            SpannableString content = new SpannableString(text);
            emotifySpannable(content);
            super.setText(content, BufferType.SPANNABLE);
        } else {
            super.setText(text, type);
        }
    }

    /**
     * Work through the contents of the string, and replace any occurrences of [icon] with the imageSpan
     *
     * @param spannable
     */
    private void emotifySpannable(Spannable spannable) {
        int length = spannable.length();
        int position = 0;
        int tagStartPosition = 0;
        int tagLength = 0;
        StringBuilder buffer = new StringBuilder();
        boolean inTag = false;
        if (length <= 0)
            return;
        do {
            String c = spannable.subSequence(position, position + 1).toString();
            if (!inTag && c.equals(START_CHAR)) {
                buffer = new StringBuilder();
                tagStartPosition = position;
                inTag = true;
                tagLength = 0;
            }
            if (inTag) {
                buffer.append(c);
                tagLength++;
                if (c.equals(END_CHAR)) {
                    inTag = false;
                    String tag = buffer.toString();
                    int tagEnd = tagStartPosition + tagLength;
                    int lastIndex = tag.lastIndexOf(START_CHAR);
                    if (lastIndex > 0) {
                        tagStartPosition = tagStartPosition + lastIndex;
                        tag = tag.substring(lastIndex, tag.length());
                    }
                    ImageSpan imageSpan = getImageSpan(tag);
                    if (imageSpan != null)
                        spannable.setSpan(imageSpan, tagStartPosition, tagEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            position++;
        } while (position < length);
    }

    /**
     * 解析字符串表情对应的表情png图片
     *
     * @param content
     * @return
     */
    private ImageSpan getImageSpan(String content) {
        return EmojiUtils.getInstance().getEmojiSpan(getContext(), content);
    }
}
