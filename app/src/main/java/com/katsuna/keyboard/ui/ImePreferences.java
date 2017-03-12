package com.katsuna.keyboard.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import com.android.inputmethodcommon.InputMethodSettingsFragment;
import com.katsuna.commons.entities.PreferenceKey;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.utils.ProfileReader;
import com.katsuna.keyboard.R;

public class ImePreferences extends PreferenceActivity {
    @Override
    public Intent getIntent() {
        final Intent modIntent = new Intent(super.getIntent());
        modIntent.putExtra(EXTRA_SHOW_FRAGMENT, Settings.class.getName());
        modIntent.putExtra(EXTRA_NO_HEADERS, true);
        return modIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We overwrite the title of the activity, as the default one is "Voice Search".
        setTitle(R.string.settings_name);
    }

    @Override
    protected boolean isValidFragment(final String fragmentName) {
        return Settings.class.getName().equals(fragmentName);
    }

    public static class Settings extends InputMethodSettingsFragment {

        private UserProfileContainer mUserProfileContainer;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setInputMethodSettingsCategoryTitle(R.string.language_selection_title);
            setSubtypeEnablerTitle(R.string.select_language);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.ime_preferences);

            loadProfile();

            if (!mUserProfileContainer.hasKatsunaServices()) {
                PreferenceScreen root = getPreferenceScreen();
                createRightHandSetting(root);
            }
        }

        private void loadProfile() {
            mUserProfileContainer = ProfileReader.getKatsunaUserProfile(getContext());
        }

        private void createRightHandSetting(PreferenceScreen root) {
            // Checkbox preference
            CheckBoxPreference checkboxPref = new CheckBoxPreference(getActivity());
            checkboxPref.setKey(PreferenceKey.RIGHT_HAND);
            checkboxPref.setTitle(R.string.common_right_hand_setting);
            checkboxPref.setDefaultValue(true);


            root.addPreference(checkboxPref);
        }

    }


}
