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
package com.katsuna.keyboard.ui.activities;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.katsuna.commons.controls.KatsunaNavigationView;
import com.katsuna.commons.entities.UserProfile;
import com.katsuna.commons.ui.KatsunaActivity;
import com.katsuna.commons.utils.BrowserUtils;
import com.katsuna.commons.utils.ColorAdjuster;
import com.katsuna.commons.utils.SizeAdjuster;
import com.katsuna.keyboard.R;

import static com.katsuna.commons.utils.Constants.KATSUNA_PRIVACY_URL;
import static com.katsuna.commons.utils.Constants.KATSUNA_TERMS_OF_USE;

public class MainActivity extends KatsunaActivity {

    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private Button mEnableKeyboard;
    private Button mSetKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initControls();
    }

    @Override
    protected void onResume() {
        super.onResume();

        applyProfiles();
    }

    private void applyProfiles() {
        UserProfile profile = mUserProfileContainer.getActiveUserProfile();

        ViewGroup topViewGroup = (ViewGroup) findViewById(android.R.id.content);
        SizeAdjuster.applySizeProfile(this, topViewGroup, profile.opticalSizeProfile);

        ColorAdjuster.adjustPrimaryButton(this, profile.colorProfile, mEnableKeyboard);
        ColorAdjuster.adjustPrimaryButton(this, profile.colorProfile, mSetKeyboard);



    }

    @Override
    protected void showPopup(boolean flag) {
        // no op here
    }

    private void initControls() {
        initToolbar();

        initDrawer();

        // init action buttons
        mEnableKeyboard = (Button) findViewById(R.id.enable_keyboard);
        mEnableKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivityForResult(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS), 0);
                } catch (ActivityNotFoundException ex) {
                    Log.w(TAG, "No activity found to open ACTION_INPUT_METHOD_SETTINGS. " +
                        "Plan b initiated");

                    try {
                        openInputMethodSettingsPlanB();
                    } catch (ActivityNotFoundException ex2) {
                        Log.w(TAG, "Plan b initiated failed. " + ex2.getMessage());
                    }
                }
            }
        });

        mSetKeyboard = (Button) findViewById(R.id.set_keyboard);
        mSetKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager mgr =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showInputMethodPicker();
            }
        });

        // calculate version
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

            String version = getString(R.string.common_version);
            TextView appVersion = (TextView) findViewById(R.id.app_version);
            appVersion.setText(version + " " + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void openInputMethodSettingsPlanB() {
        // credits go to
        // https://stackoverflow.com/q/2192419
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        ComponentName com = new ComponentName("com.android.settings",
            "com.android.settings.LanguageSettings");
        intent.setComponent(com);
        startActivity(intent);
    }

    private void initDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.common_navigation_drawer_open,
                R.string.common_navigation_drawer_close);
        assert mDrawerLayout != null;
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        setupDrawerLayout();
    }

    private void setupDrawerLayout() {
        KatsunaNavigationView view = findViewById(R.id.katsuna_navigation_view);
        assert view != null;
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                mDrawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.drawer_info:
                        startActivity(new Intent(MainActivity.this, InfoActivity.class));
                        break;
                    case R.id.drawer_privacy:
                        BrowserUtils.openUrl(MainActivity.this, KATSUNA_PRIVACY_URL);
                        break;
                    case R.id.drawer_terms:
                        BrowserUtils.openUrl(MainActivity.this, KATSUNA_TERMS_OF_USE);
                        break;
                }

                return true;
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
            }
        });
    }

}
