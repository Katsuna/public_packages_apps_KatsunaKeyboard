package com.katsuna.keyboard.ui;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;

import com.android.inputmethodservice.KatsunaKeyboardView;
import com.katsuna.keyboard.R;

public class LatinKeyboardView extends KatsunaKeyboardView {

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
                    }
                } else {
                    if (qwertyKeyboard) {
                        keyboard.setShiftIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.capital_pressed_t, null));
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