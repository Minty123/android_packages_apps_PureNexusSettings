/*
 * Copyright (C) 2015 The Pure Nexus Project
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
 * limitations under the License.
 */

package com.android.purenexussettings;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.design.widget.Snackbar;

public class LockscreenFragment extends PreferenceFragment {
    public LockscreenFragment(){}

    public static final int IMAGE_PICK = 1;

    private static final String KEY_WALLPAPER_SET = "lockscreen_wallpaper_set";
    private static final String KEY_WALLPAPER_CLEAR = "lockscreen_wallpaper_clear";
    private static final String WALLPAPER_PACKAGE_NAME = "com.slim.wallpaperpicker";
    private static final String WALLPAPER_CLASS_NAME = "com.slim.wallpaperpicker.WallpaperCropActivity";

    private Preference mSetWallpaper;
    private Preference mClearWallpaper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean slimWallInstalled;

        addPreferencesFromResource(R.xml.lockscreen_fragment);

        mSetWallpaper = (Preference) findPreference(KEY_WALLPAPER_SET);
        mClearWallpaper = (Preference) findPreference(KEY_WALLPAPER_CLEAR);

        PreferenceCategory mPrefCat = (PreferenceCategory) findPreference("lockscreen_wallpaper");

        PreferenceScreen prefScreen = getPreferenceScreen();

        // check if wallpaper app installed
        try {
            PackageInfo pi = getActivity().getPackageManager().getPackageInfo(WALLPAPER_PACKAGE_NAME, 0);
            slimWallInstalled = pi.applicationInfo.enabled;
        } catch (PackageManager.NameNotFoundException e) {
            slimWallInstalled = false;
        }

        // if not remove wallpaper options
        if (!slimWallInstalled) {
            prefScreen.removePreference(mPrefCat);
        }

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mSetWallpaper) {
            setKeyguardWallpaper();
            return true;
        } else if (preference == mClearWallpaper) {
            clearKeyguardWallpaper();
            Snackbar.make(getActivity().findViewById(R.id.frame_container), getString(R.string.reset_lockscreen_wallpaper),
            Snackbar.LENGTH_LONG).show();
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                Intent intent = new Intent();
                intent.setClassName(WALLPAPER_PACKAGE_NAME, WALLPAPER_CLASS_NAME);
                intent.putExtra("keyguardMode", "1");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }

    private void setKeyguardWallpaper() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK);
    }

    private void clearKeyguardWallpaper() {
        WallpaperManager wallpaperManager = null;
        wallpaperManager = WallpaperManager.getInstance(getActivity());
        wallpaperManager.clearKeyguardWallpaper();
    }
}
