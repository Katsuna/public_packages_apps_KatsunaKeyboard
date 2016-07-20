package com.katsuna.keyboard;

import com.katsuna.keyboard.utils.FontsOverride;

public final class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/Roboto-Medium.ttf");
    }
}
