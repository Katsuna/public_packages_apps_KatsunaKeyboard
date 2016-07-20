package com.katsuna.keyboard.ui;

import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;

import com.katsuna.keyboard.R;

public class LatinKeyboardView extends KeyboardView {

    public LatinKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LatinKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
}