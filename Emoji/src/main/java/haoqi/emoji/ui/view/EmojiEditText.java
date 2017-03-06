package haoqi.emoji.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.EditText;

import haoqi.emoji.util.AnimatedGifDrawable;
import haoqi.emoji.util.AnimatedImageSpan;
import haoqi.emoji.util.EmojiUtils;

public class EmojiEditText extends EditText {
    private static final String START_CHAR = "[";
    private static final String END_CHAR = "]";
    private boolean load = true;

    public EmojiEditText(Context context) {
        super(context);
        init();
    }

    public EmojiEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EmojiEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text != null) {
            Spannable span = EmojiUtils.getInstance().getSmiledText(getContext(), text);
            // 设置内容
            super.setText(span, BufferType.SPANNABLE);
        } else {
            super.setText(text, type);
        }
    }

    private void init() {
        setTextIsSelectable(true);
        this.addTextChangedListener(new TextWatcher() {
            private int subStart = -1;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (subStart == -1) {
                    subStart = start;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                if (!load) {
//                    subStart = -1;
//                } else {
//                    if (subStart != -1) {
//                        emotifySpannable(s, new SpannableString(s.subSequence(subStart, s.length())), subStart, s.length());
//                        subStart = -1;
//                    }
//                }
            }
        });
    }

    private void emotifySpannable(Editable s, Spannable spannable, int st, int en) {
        int length = spannable.length();
        if (length <= 0) {
            return;
        }

        int position = 0;
        int tagStartPosition = 0;
        int tagLength = 0;
        StringBuilder buffer = new StringBuilder();
        boolean inTag = false;
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
                // Have we reached end of the tag?
                if (c.equals(END_CHAR)) {
                    inTag = false;

                    String tag = buffer.toString();
                    int tagEnd = tagStartPosition + tagLength;

                    // start by liweiping for 去除首部有多个“[”符号
                    int lastIndex = tag.lastIndexOf(START_CHAR);

                    if (lastIndex > 0) {
                        tagStartPosition = tagStartPosition + lastIndex;
                        tag = tag.substring(lastIndex, tag.length());
                    }

                    Spannable imageSpan = getSpan(tag);
                    // DynamicDrawableSpan imageSpan = getDynamicImageSpan(tag);
                    if (imageSpan != null) {
                        spannable.setSpan(imageSpan, tagStartPosition, tagEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        if (s != null) {
                            load = false;
                            s.delete(st, en);
                            s.append(spannable);
                            load = true;
                        }
                    }
                }
            }
            position++;
        } while (position < length);
    }

    private Spannable getSpan(String tag) {
        return EmojiUtils.getInstance().getSmiledText(getContext(), tag);
    }


}

