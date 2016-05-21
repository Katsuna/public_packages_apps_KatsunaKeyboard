package gr.crystalogic.keyboard.ui;

import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodSubtype;

public class LatinKeyboardView extends KeyboardView {

    // TODO: Move this into android.inputmethodservice.Keyboard
    static final int KEYCODE_LANGUAGE_SWITCH = -101;

    public LatinKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LatinKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    void setSubtypeOnSpaceKey(final InputMethodSubtype subtype) {
        final LatinKeyboard keyboard = (LatinKeyboard) getKeyboard();
        keyboard.setSpaceIcon(ResourcesCompat.getDrawable(getResources(), subtype.getIconResId(), null));
        invalidateAllKeys();
    }
}