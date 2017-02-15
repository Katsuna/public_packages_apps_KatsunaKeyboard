package com.katsuna.keyboard.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.IBinder;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;

import com.katsuna.commons.entities.KeyboardEvent;
import com.katsuna.commons.providers.KeyboardProvider;
import com.katsuna.keyboard.R;
import com.katsuna.keyboard.utils.Log;

public class SoftKeyboard extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    private InputMethodManager mInputMethodManager;

    private LatinKeyboardView mInputView;

    private int mLastDisplayWidth;

    private LatinKeyboard mSymbolsKeyboard;
    private LatinKeyboard mSymbolsShiftedKeyboard;
    private LatinKeyboard mSymbolsShifted2Keyboard;
    private LatinKeyboard mQwertyKeyboard;
    private LatinKeyboard mQwertzDeKeyboard;
    private LatinKeyboard mQwertyEsKeyboard;
    private LatinKeyboard mAzertyFrKeyboard;
    private LatinKeyboard mQwertyGrKeyboard;
    private LatinKeyboard mQwertyItKeyboard;
    private LatinKeyboard mQwertyPtKeyboard;
    private LatinKeyboard mQwertyTrKeyboard;
    private LatinKeyboard mEastSlavicKeyboard;
    private LatinKeyboard mArabicKeyboard;
    private LatinKeyboard mPhoneKeyboard;

    private LatinKeyboard mCurKeyboard;
    private EditorInfo mCurrentEditorInfo;
    private boolean inputWithAutoCapsDisabled;
    private boolean autoShiftOn;
    private boolean mPasswordField;

    @Override
    public void onCreate() {
        super.onCreate();
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    /**
     * This is the point where you can do all of your UI initialization.  It
     * is called after creation and any configuration change.
     */
    @Override
    public void onInitializeInterface() {

        Log.d(this, "onInitializeInterface");

        if (mQwertyKeyboard != null) {
            // Configuration changes can happen after the keyboard gets recreated,
            // so we need to be able to re-build the keyboards if the available
            // space has changed.
            int displayWidth = getMaxWidth();
            if (displayWidth == mLastDisplayWidth) return;
            mLastDisplayWidth = displayWidth;
        }
        mQwertyKeyboard = new LatinKeyboard(this, R.xml.qwerty);
        mQwertzDeKeyboard = new LatinKeyboard(this, R.xml.qwertz_de);
        mQwertyEsKeyboard = new LatinKeyboard(this, R.xml.qwerty_es);
        mAzertyFrKeyboard = new LatinKeyboard(this, R.xml.azerty_fr);
        mQwertyGrKeyboard = new LatinKeyboard(this, R.xml.qwerty_gr);
        mQwertyItKeyboard = new LatinKeyboard(this, R.xml.qwerty_it);
        mQwertyPtKeyboard = new LatinKeyboard(this, R.xml.qwerty_pt);
        mQwertyTrKeyboard = new LatinKeyboard(this, R.xml.qwerty_tr);
        mEastSlavicKeyboard = new LatinKeyboard(this, R.xml.east_slavic);
        mArabicKeyboard = new LatinKeyboard(this, R.xml.arabic);
        mSymbolsKeyboard = new LatinKeyboard(this, R.xml.symbols);
        mSymbolsShiftedKeyboard = new LatinKeyboard(this, R.xml.symbols_shift);
        mSymbolsShifted2Keyboard = new LatinKeyboard(this, R.xml.symbols_shift2);
        mPhoneKeyboard = new LatinKeyboard(this, R.xml.phone);
    }

    /**
     * Called by the framework when your view for creating input needs to
     * be generated.  This will be called the first time your input method
     * is displayed, and every time it needs to be re-created such as due to
     * a configuration change.
     */
    @SuppressLint("InflateParams")
    @Override
    public View onCreateInputView() {
        Log.d(this, "onCreateInputView");

        mInputView = (LatinKeyboardView) getLayoutInflater().inflate(R.layout.input, null);
        mInputView.setOnKeyboardActionListener(this);

        //mCurrentQwertyKeyboard
        InputMethodSubtype subtype = mInputMethodManager.getCurrentInputMethodSubtype();
        setLatinKeyboard(getKeyboard(subtype));
        return mInputView;
    }

    private void setLatinKeyboard(LatinKeyboard nextKeyboard) {
        if (isQwertyKeyboard(nextKeyboard)) {
            // always show keyboard switch key
            nextKeyboard.setLanguageSwitchKeyVisibility(true);
        }
        mInputView.setKeyboard(nextKeyboard);
    }

    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed information
     * about the target of our edits.
     */
    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        Log.d(this, "onStartInput");

        super.onStartInput(attribute, restarting);

        // We are now going to initialize our state based on the type of
        // text being edited.
        InputMethodSubtype subtype;

        int inputTypeMasked = attribute.inputType & InputType.TYPE_MASK_CLASS;
        switch (inputTypeMasked) {
            case InputType.TYPE_CLASS_NUMBER:
            case InputType.TYPE_CLASS_DATETIME:
                // Numbers and dates default to the symbols keyboard, with
                // no extra features.
                mCurKeyboard = mSymbolsKeyboard;
                break;

            case InputType.TYPE_CLASS_PHONE:
                // Phones will also default to the symbols keyboard, though
                // often you will want to have a dedicated phone keyboard.
                mCurKeyboard = mPhoneKeyboard;
                break;

            case InputType.TYPE_CLASS_TEXT:
                // This is general text editing.  We will default to the
                // normal alphabetic keyboard, and assume that we should
                // be doing predictive text (showing candidates as the
                // user types).
                subtype = mInputMethodManager.getCurrentInputMethodSubtype();
                mCurKeyboard = getKeyboard(subtype);

                break;
            default:
                // For all unknown input types, default to the alphabetic
                // keyboard with no special features.
                subtype = mInputMethodManager.getCurrentInputMethodSubtype();
                mCurKeyboard = getKeyboard(subtype);
        }

        //disable auto caps feature for password fields
        int variation = attribute.inputType & InputType.TYPE_MASK_VARIATION;

        // find if input type is password
        switch (variation) {
            case InputType.TYPE_TEXT_VARIATION_PASSWORD:
            case InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD:
            case InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD:
            case InputType.TYPE_NUMBER_VARIATION_PASSWORD:
                mPasswordField = true;
                break;
            default:
                mPasswordField = false;
        }

        inputWithAutoCapsDisabled = mPasswordField;

        //disable auto caps feature uri fields and email fields
        if (variation == EditorInfo.TYPE_TEXT_VARIATION_URI ||
                variation == EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) {
            inputWithAutoCapsDisabled = true;
        }

        // If it's not multiline and the autoCorrect flag is not set, then
        // don't correct
        if ((attribute.inputType & EditorInfo.TYPE_TEXT_FLAG_AUTO_CORRECT) == 0
                && (attribute.inputType & EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE) == 0) {
            inputWithAutoCapsDisabled = true;
        }

        // Update the label on the enter key, depending on what the application
        // says it will do.
        //mCurKeyboard.setImeOptions(getResources(), attribute.imeOptions);
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        Log.d(this, "onStartInputView");

        super.onStartInputView(info, restarting);

        mCurrentEditorInfo = info;

        // Apply the selected keyboard to the input view.
        setLatinKeyboard(mCurKeyboard);
        mInputView.closing();

        setAutoShift();
    }

    @Override
    public void onCurrentInputMethodSubtypeChanged(InputMethodSubtype subtype) {
        Log.d(this, "onCurrentInputMethodSubtypeChanged");

        mCurKeyboard = getKeyboard(subtype);
        setLatinKeyboard(mCurKeyboard);
    }

    private LatinKeyboard getKeyboard(InputMethodSubtype subtype) {
        // Transform if statement to switch, in order to support multiple languages in the future
        // TO-DO: Replace getLocale (depreciated in API 24) with getLanguageTag
        switch (subtype.getLocale()) {
            case "en_US":
                return mQwertyKeyboard;
            case "de_DE":
                return mQwertzDeKeyboard;
            case "es_ES":
                return mQwertyEsKeyboard;
            case "fr_FR":
                return mAzertyFrKeyboard;
            case "el_GR":
                return mQwertyGrKeyboard;
            case "it_IT":
                return mQwertyItKeyboard;
            case "pt_PT":
                return mQwertyPtKeyboard;
            case "tr_TR":
                return mQwertyTrKeyboard;
            case "ar_001":
                return mArabicKeyboard;
            case "ru_RU":
                return mEastSlavicKeyboard;
            default:
                return mQwertyKeyboard;
        }
    }

    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {

        Log.d(this, "onKey:" + primaryCode);

        if (primaryCode == Keyboard.KEYCODE_DELETE) {
            saveKey(primaryCode, "");
            handleBackspace();
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
            saveKey(primaryCode, "");
            handleShift();
        } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
            saveKey(primaryCode, "");
            handleClose();
        } else if (primaryCode == Constants.KEYCODE_LANGUAGE_SWITCH) {
            saveKey(primaryCode, "");
            handleLanguageSwitch();
        } else if (primaryCode == Keyboard.KEYCODE_DONE) {
            saveKey(primaryCode, "");
            performEditorAction(mCurrentEditorInfo);
        } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE
                && mInputView != null) {
            saveKey(primaryCode, "");
            Keyboard current = mInputView.getKeyboard();
            if (current == mSymbolsKeyboard || current == mSymbolsShiftedKeyboard || current == mSymbolsShifted2Keyboard) {
                InputMethodSubtype subtype = mInputMethodManager.getCurrentInputMethodSubtype();
                setLatinKeyboard(getKeyboard(subtype));
            } else {
                setLatinKeyboard(mSymbolsKeyboard);
                mInputView.setShiftKey(false, false);
            }
        } else {
            handleCharacter(primaryCode);
        }
    }

    private void saveKey(int code, String character) {
        // Never store passwords!!
        if (mPasswordField) {
            KeyboardEvent event = new KeyboardEvent(Constants.PASSWORD_CODE, Constants.PASSWORD_WILDCARD);
            KeyboardProvider.save(this, event);
        } else {
            KeyboardEvent event = new KeyboardEvent(code, character);
            KeyboardProvider.save(this, event);
        }
    }

    private void performEditorAction(EditorInfo sEditorInfo) {
        InputConnection ic = getCurrentInputConnection();
        switch (sEditorInfo.imeOptions & (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
            case EditorInfo.IME_ACTION_GO:
                ic.performEditorAction(EditorInfo.IME_ACTION_GO);
                break;
            case EditorInfo.IME_ACTION_NEXT:
                ic.performEditorAction(EditorInfo.IME_ACTION_NEXT);
                break;
            case EditorInfo.IME_ACTION_SEARCH:
                ic.performEditorAction(EditorInfo.IME_ACTION_SEARCH);
                break;
            case EditorInfo.IME_ACTION_SEND:
                ic.performEditorAction(EditorInfo.IME_ACTION_SEND);
                break;
            case EditorInfo.IME_ACTION_DONE:
                ic.performEditorAction(EditorInfo.IME_ACTION_DONE);
                break;
            default:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
        }
    }

    @Override
    public void onText(CharSequence text) {

        Log.d(this, "onText");

        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.beginBatchEdit();
        ic.commitText(text, 0);
        ic.endBatchEdit();
        setAutoShift();
    }

    private void handleBackspace() {
        keyDownUp(KeyEvent.KEYCODE_DEL);
    }

    private String getCurrentText() {
        String output = null;
        ExtractedText extractedText = getCurrentInputConnection().getExtractedText(new ExtractedTextRequest(), 0);
        if (extractedText != null) {
            output = extractedText.text.toString();
        }
        return output;
    }

    private void handleShift() {
        if (mInputView == null) {
            return;
        }

        Keyboard currentKeyboard = mInputView.getKeyboard();
        if (isQwertyKeyboard(currentKeyboard)) {
            // Alphabet keyboard
            mInputView.setShiftKey(!mInputView.isShifted(), true);
        } else if (currentKeyboard == mSymbolsKeyboard) {
            mInputView.setShiftKey(true, false);
            setLatinKeyboard(mSymbolsShiftedKeyboard);
            mInputView.setShiftKey(true, false);
        } else if (currentKeyboard == mSymbolsShiftedKeyboard) {
            mInputView.setShiftKey(false, false);
            setLatinKeyboard(mSymbolsShifted2Keyboard);
            mInputView.setShiftKey(false, false);
        } else if (currentKeyboard == mSymbolsShifted2Keyboard) {
            mInputView.setShiftKey(false, false);
            setLatinKeyboard(mSymbolsKeyboard);
            mInputView.setShiftKey(false, false);
        }
    }

    private void handleCharacter(int primaryCode) {
        if (isInputViewShown()) {
            //force capital if isShift or field is not Password field and rules apply by setNextCharToCapital
            if (mInputView.isShifted()) {
                primaryCode = Character.toUpperCase(primaryCode);
            }
        }
        String character = String.valueOf((char) primaryCode);
        saveKey(primaryCode, character);
        getCurrentInputConnection().commitText(character, 1);
        setAutoShift();
    }

    private void setAutoShift() {
        if (!inputWithAutoCapsDisabled) {
            if (setNextCharToCapital()) {
                Keyboard currentKeyboard = mInputView.getKeyboard();
                if (isQwertyKeyboard(currentKeyboard)) {
                    //Autoshift only if keyboard isn't manually shifted
                    if (!currentKeyboard.isShifted()) {
                        mInputView.setShiftKey(true, true);
                        Log.d(this, "setAutoShift activated!");
                        autoShiftOn = true;
                    } else {
                        Log.d(this, "setAutoShift not activated since it's already active!");
                    }
                }
            } else if (autoShiftOn) {
                if (isQwertyKeyboard(mInputView.getKeyboard())) {
                    mInputView.setShiftKey(false, true);
                    Log.d(this, "setAutoShift deactivated!");
                    autoShiftOn = false;
                }
            }
        }
    }

    private boolean isQwertyKeyboard(Keyboard keyboard) {
        return mSymbolsKeyboard != keyboard && mSymbolsShiftedKeyboard != keyboard && mSymbolsShifted2Keyboard != keyboard;
    }

    private boolean setNextCharToCapital() {
        boolean output = false;
        String currentText = null;
        ExtractedText extractedText = getCurrentInputConnection().getExtractedText(new ExtractedTextRequest(), 0);

        if (extractedText != null) {
            currentText = extractedText.text.toString();
            //turn off auto caps lock if cursor is not at the last of the current text
            if (extractedText.selectionEnd != currentText.length()) {
                return false;
            }
        }

        if (currentText != null) {
            if (currentText.trim().isEmpty()) {
                output = true;
            }

            if (currentText.trim().endsWith(".")) {
                output = true;
            }
        }
        return output;
    }

    private void handleClose() {
        requestHideSelf(0);
        mInputView.closing();
    }

    private IBinder getToken() {
        final Dialog dialog = getWindow();
        if (dialog == null) {
            return null;
        }
        final Window window = dialog.getWindow();
        if (window == null) {
            return null;
        }
        return window.getAttributes().token;
    }

    private void handleLanguageSwitch() {
        //mInputMethodManager.showInputMethodPicker();
        mInputMethodManager.switchToNextInputMethod(getToken(), true);
    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeUp() {
    }

    @Override
    public void onPress(int primaryCode) {
    }

    @Override
    public void onRelease(int primaryCode) {
    }
}

