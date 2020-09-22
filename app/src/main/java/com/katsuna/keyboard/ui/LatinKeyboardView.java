/**
* Copyright (C) 2020 Manos Saratsis
*
* This file is part of Katsuna.
*
* Katsuna is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Katsuna is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Katsuna.  If not, see <https://www.gnu.org/licenses/>.
*/
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