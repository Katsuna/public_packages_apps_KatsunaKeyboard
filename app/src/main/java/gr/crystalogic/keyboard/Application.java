package gr.crystalogic.keyboard;

import gr.crystalogic.keyboard.utils.FontsOverride;

public final class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/Roboto-Bold.ttf");
    }
}
