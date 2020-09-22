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
/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

/**
 * This is a part of the inputmethod-common static Java library.
 * The original source code can be found at frameworks/opt/inputmethodcommon of Android Open Source
 * Project.
 */

package com.android.inputmethodcommon;

import android.graphics.drawable.Drawable;

/**
 * InputMethodSettingsInterface is the interface for adding IME related preferences to
 * PreferenceActivity or PreferenceFragment.
 */
public interface InputMethodSettingsInterface {
    /**
     * Sets the title for the input method settings category with a resource ID.
     * @param resId The resource ID of the title.
     */
    void setInputMethodSettingsCategoryTitle(int resId);

    /**
     * Sets the title for the input method settings category with a CharSequence.
     * @param title The title for this preference.
     */
    void setInputMethodSettingsCategoryTitle(CharSequence title);

    /**
     * Sets the title for the input method enabler preference for launching subtype enabler with a
     * resource ID.
     * @param resId The resource ID of the title.
     */
    void setSubtypeEnablerTitle(int resId);

    /**
     * Sets the title for the input method enabler preference for launching subtype enabler with a
     * CharSequence.
     * @param title The title for this preference.
     */
    void setSubtypeEnablerTitle(CharSequence title);

    /**
     * Sets the icon for the preference for launching subtype enabler with a resource ID.
     * @param resId The resource id of an optional icon for the preference.
     */
    void setSubtypeEnablerIcon(int resId);

    /**
     * Sets the icon for the Preference for launching subtype enabler with a Drawable.
     * @param drawable The drawable of an optional icon for the preference.
     */
    void setSubtypeEnablerIcon(Drawable drawable);
}
