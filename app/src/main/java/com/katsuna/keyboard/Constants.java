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
package com.katsuna.keyboard;

public class Constants {
    public static final int KEYCODE_LANGUAGE_SWITCH = -101;
    public static final int KEYCODE_DELETE = -5;
    public static final int KEYCODE_RETURN = -4;
    public static final int KEYCODE_SYMBOLS = -2;
    public static final int KEYCODE_SHIFT = -1;
    public static final int KEYCODE_SPACE = 32;

    // key logging constants
    static final int PASSWORD_CODE = -9999999;
    static final String PASSWORD_WILDCARD =  "🂡";
    static final int KEY_NUMBER = Integer.MIN_VALUE;
    static final int KEY_LETTER = Integer.MIN_VALUE + 1;
    static final int KEY_OTHER = Integer.MIN_VALUE + 2;
}
