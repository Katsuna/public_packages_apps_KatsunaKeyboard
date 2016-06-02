package gr.crystalogic.keyboard.ui;

import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;

import gr.crystalogic.keyboard.R;

public class LatinKeyboardView extends KeyboardView {

    public LatinKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LatinKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean setShifted(boolean shifted) {
        LatinKeyboard keyboard = (LatinKeyboard) getKeyboard();
        if (keyboard != null) {
            if (keyboard.setShifted(shifted)) {

                //shift state changed => show proper icon
                if (shifted) {
                    keyboard.setShiftIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_keyboard_capslock_teala200_24dp, null));
                } else {
                    keyboard.setShiftIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_keyboard_capslock_black38_24dp, null));
                }

                // The whole keyboard probably needs to be redrawn
                invalidateAllKeys();
                return true;
            }
        }
        return false;
    }
}