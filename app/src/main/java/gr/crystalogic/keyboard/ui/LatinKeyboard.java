package gr.crystalogic.keyboard.ui;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.view.inputmethod.EditorInfo;

import gr.crystalogic.keyboard.R;

public class LatinKeyboard extends Keyboard {

    private Key mEnterKey;
    /**
     * Stores the current state of the mode change key. Its width will be dynamically updated to
     * match the region of mModeChangeKey when mModeChangeKey becomes invisible.
     */
    private Key mModeChangeKey;
    /**
     * Stores the current state of the language switch key (a.k.a. globe key). This should be
     * visible while InputMethodManager#shouldOfferSwitchingToNextInputMethod(IBinder)
     * returns true. When this key becomes invisible, its width will be shrunk to zero.
     */
    private Key mLanguageSwitchKey;
    /**
     * Stores the size and other information of {@link #mModeChangeKey} when
     * {@link #mLanguageSwitchKey} is visible. This should be immutable and will be used only as a
     * reference size when the visibility of {@link #mLanguageSwitchKey} is changed.
     */
    private Key mSavedModeChangeKey;
    /**
     * Stores the size and other information of {@link #mLanguageSwitchKey} when it is visible.
     * This should be immutable and will be used only as a reference size when the visibility of
     * {@link #mLanguageSwitchKey} is changed.
     */
    private Key mSavedLanguageSwitchKey;

    private Key mShiftKey;

    public LatinKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
    }

    @Override
    protected Key createKeyFromXml(Resources res, Row parent, int x, int y,
                                   XmlResourceParser parser) {
        Key key = new Key(res, parent, x, y, parser);
        if (key.codes[0] == -4) {
            mEnterKey = key;
        } else if (key.codes[0] == Keyboard.KEYCODE_MODE_CHANGE) {
            mModeChangeKey = key;
            mSavedModeChangeKey = new Key(res, parent, x, y, parser);
        } else if (key.codes[0] == Constants.KEYCODE_LANGUAGE_SWITCH) {
            mLanguageSwitchKey = key;
            mSavedLanguageSwitchKey = new Key(res, parent, x, y, parser);
        } else if (key.codes[0] == Keyboard.KEYCODE_SHIFT) {
            mShiftKey = key;
        }
        return key;
    }

    /**
     * Dynamically change the visibility of the language switch key (a.k.a. globe key).
     *
     * @param visible True if the language switch key should be visible.
     */
    void setLanguageSwitchKeyVisibility(boolean visible) {
        if (visible) {
            // The language switch key should be visible. Restore the size of the mode change key
            // and language switch key using the saved layout.
            if (mModeChangeKey != null) {
                mModeChangeKey.width = mSavedModeChangeKey.width;
                mModeChangeKey.x = mSavedModeChangeKey.x;
            }

            if (mLanguageSwitchKey != null) {
                mLanguageSwitchKey.width = mSavedLanguageSwitchKey.width;
                mLanguageSwitchKey.icon = mSavedLanguageSwitchKey.icon;
                mLanguageSwitchKey.iconPreview = mSavedLanguageSwitchKey.iconPreview;
            }
        } else {
            // The language switch key should be hidden. Change the width of the mode change key
            // to fill the space of the language key so that the user will not see any strange gap.
            if (mModeChangeKey != null) {
                mModeChangeKey.width = mSavedModeChangeKey.width + mSavedLanguageSwitchKey.width;
            }

            if (mLanguageSwitchKey != null) {
                mLanguageSwitchKey.width = 0;
                mLanguageSwitchKey.icon = null;
                mLanguageSwitchKey.iconPreview = null;
            }
        }
    }

    /**
     * This looks at the ime options given by the current editor, to set the
     * appropriate label on the keyboard's enter key (if it has one).
     */
    void setImeOptions(Resources res, int options) {
        if (mEnterKey == null) {
            return;
        }

        switch (options & (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
            case EditorInfo.IME_ACTION_GO:
                mEnterKey.label = res.getText(R.string.label_go_key);
                break;
            case EditorInfo.IME_ACTION_NEXT:
                mEnterKey.label = res.getText(R.string.label_next_key);
                break;
            case EditorInfo.IME_ACTION_SEARCH:
                mEnterKey.label = res.getText(R.string.label_search_key);
                break;
            case EditorInfo.IME_ACTION_SEND:
                mEnterKey.label = res.getText(R.string.label_send_key);
                break;
            default:
                mEnterKey.label = res.getText(R.string.label_done_key);
                break;
        }
    }

    void setShiftIcon(final Drawable icon) {
        if (mShiftKey != null) {
            mShiftKey.icon = icon;
        }
    }
}
