package com.katsuna.keyboard.ui;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;

import com.katsuna.keyboard.Constants;

class LatinKeyboard extends Keyboard {

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
    private Key mSpaceKey;
    private Key mSavedSpaceKey;

    public LatinKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
    }

    @Override
    protected Key createKeyFromXml(Resources res, Row parent, int x, int y,
                                   XmlResourceParser parser) {
        Key key = new Key(res, parent, x, y, parser);
        if (key.codes[0] == Keyboard.KEYCODE_MODE_CHANGE) {
            mModeChangeKey = key;
            mSavedModeChangeKey = new Key(res, parent, x, y, parser);
        } else if (key.codes[0] == Constants.KEYCODE_LANGUAGE_SWITCH) {
            mLanguageSwitchKey = key;
            mSavedLanguageSwitchKey = new Key(res, parent, x, y, parser);
        } else if (key.codes[0] == Keyboard.KEYCODE_SHIFT) {
            mShiftKey = key;
        } else if (key.codes[0] == 32) {
            mSpaceKey = key;
            mSavedSpaceKey = new Key(res, parent, x, y, parser);
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
            if (mSpaceKey != null) {
                mSpaceKey.width = mSavedSpaceKey.width;
                mSpaceKey.x = mSavedSpaceKey.x;
            }

            if (mLanguageSwitchKey != null) {
                mLanguageSwitchKey.width = mSavedLanguageSwitchKey.width;
                mLanguageSwitchKey.icon = mSavedLanguageSwitchKey.icon;
                mLanguageSwitchKey.iconPreview = mSavedLanguageSwitchKey.iconPreview;
            }
        } else {
            // The language switch key should be hidden. Change the width of the mode change key
            // to fill the space of the language key so that the user will not see any strange gap.
            if (mSpaceKey != null) {
                int mSavedLanguageSwitchKeyWidth = mSavedLanguageSwitchKey == null ? 0 :
                        mSavedLanguageSwitchKey.width;
                mSpaceKey.width = mSavedSpaceKey.width + mSavedLanguageSwitchKeyWidth;

                if (mLanguageSwitchKey != null) {
                    // check if switch language key is before space key
                    if (mLanguageSwitchKey.x < mSpaceKey.x) {
                        mSpaceKey.x = mSavedSpaceKey.x - mSavedLanguageSwitchKeyWidth;
                    } else {
                        mSpaceKey.x = mSavedSpaceKey.x;
                    }
                }
            }

            if (mLanguageSwitchKey != null) {
                mLanguageSwitchKey.width = 0;
                mLanguageSwitchKey.icon = null;
                mLanguageSwitchKey.iconPreview = null;
            }
        }
    }

    void setShiftIcon(final Drawable icon) {
        if (mShiftKey != null) {
            mShiftKey.icon = icon;
        }
    }
}
