package com.katsuna.keyboard.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;

import com.katsuna.keyboard.R;

public class LatinKeyboardView extends KeyboardView {

    private Context mContext;

    public LatinKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public LatinKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public boolean setShiftKey(boolean shifted, boolean qwertyKeyboard) {
        LatinKeyboard keyboard = (LatinKeyboard) getKeyboard();
        if (keyboard != null) {
            if (keyboard.setShifted(shifted)) {

                //shift state changed => show proper icon
                if (shifted) {
                    if (qwertyKeyboard) {
                        keyboard.setShiftIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.small_pressed_t, null));
                    } else {
                        keyboard.setShiftIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_keyboard_capslock_teala200_24dp, null));
                    }
                } else {
                    if (qwertyKeyboard) {
                        keyboard.setShiftIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.capital_pressed_t, null));
                    } else {
                        keyboard.setShiftIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_keyboard_capslock_black38_24dp, null));
                    }
                }

                // The whole keyboard probably needs to be redrawn
                invalidateAllKeys();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Keyboard.Key key : getKeyboard().getKeys()) {
            if (key.codes[0] == Constants.KEYCODE_SPACE) {

                int margin = 4;
                Drawable dr = ContextCompat.getDrawable(mContext, R.drawable.space_bar_bg);
                dr.setBounds(key.x + margin, key.y + margin, key.x + key.width - margin, key.y + key.height - margin);
                dr.draw(canvas);

                //calculate bounds
                Drawable dr2 = ContextCompat.getDrawable(mContext, R.drawable.ic_space_bar_black_36dp);
                int left = key.x + key.width / 2 - dr2.getIntrinsicWidth() / 2;
                int top = key.y + key.height / 2 - dr2.getIntrinsicHeight() / 2;

                dr2.setBounds(left, top, left + dr2.getIntrinsicWidth(), top + dr2.getIntrinsicHeight());
                dr2.draw(canvas);
            }
        }
    }
}