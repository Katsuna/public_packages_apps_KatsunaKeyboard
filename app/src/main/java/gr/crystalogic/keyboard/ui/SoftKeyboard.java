package gr.crystalogic.keyboard.ui;

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

import gr.crystalogic.keyboard.R;
import gr.crystalogic.keyboard.utils.Log;

public class SoftKeyboard extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    private InputMethodManager mInputMethodManager;

    private LatinKeyboardView mInputView;

    private int mLastDisplayWidth;

    private LatinKeyboard mSymbolsKeyboard;
    private LatinKeyboard mSymbolsShiftedKeyboard;
    private LatinKeyboard mQwertyKeyboard;
    private LatinKeyboard mQwertyGrKeyboard;
    private LatinKeyboard mPhoneKeyboard;

    private LatinKeyboard mCurKeyboard;
    private EditorInfo mCurrentEditorInfo;

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
        mQwertyGrKeyboard = new LatinKeyboard(this, R.xml.qwerty_gr);
        mSymbolsKeyboard = new LatinKeyboard(this, R.xml.symbols);
        mSymbolsShiftedKeyboard = new LatinKeyboard(this, R.xml.symbols_shift);
        mPhoneKeyboard = new LatinKeyboard(this, R.xml.phone);
    }

    /**
     * Called by the framework when your view for creating input needs to
     * be generated.  This will be called the first time your input method
     * is displayed, and every time it needs to be re-created such as due to
     * a configuration change.
     */
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
        final boolean shouldSupportLanguageSwitchKey;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            shouldSupportLanguageSwitchKey = mInputMethodManager.shouldOfferSwitchingToNextInputMethod(getToken());
            nextKeyboard.setLanguageSwitchKeyVisibility(shouldSupportLanguageSwitchKey);
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

                // We also want to look at the current state of the editor
                // to decide whether our alphabetic keyboard should start out
                // shifted.
                updateShiftKeyState(attribute);
                break;
            default:
                // For all unknown input types, default to the alphabetic
                // keyboard with no special features.
                subtype = mInputMethodManager.getCurrentInputMethodSubtype();
                mCurKeyboard = getKeyboard(subtype);
                updateShiftKeyState(attribute);
        }

        //mark input as password input
        int inputTypeVariationMasked = attribute.inputType & InputType.TYPE_MASK_VARIATION;
        passwordInput = inputTypeVariationMasked == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                inputTypeVariationMasked == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                inputTypeVariationMasked == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD;

        // Update the label on the enter key, depending on what the application
        // says it will do.
        //mCurKeyboard.setImeOptions(getResources(), attribute.imeOptions);
    }

    private boolean passwordInput;

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        Log.d(this, "onStartInputView");

        super.onStartInputView(info, restarting);

        mCurrentEditorInfo = info;

        // Apply the selected keyboard to the input view.
        setLatinKeyboard(mCurKeyboard);
        mInputView.closing();
    }

    @Override
    public void onCurrentInputMethodSubtypeChanged(InputMethodSubtype subtype) {
        Log.d(this, "onCurrentInputMethodSubtypeChanged");

        mCurKeyboard = getKeyboard(subtype);
        setLatinKeyboard(mCurKeyboard);
    }

    private LatinKeyboard getKeyboard(InputMethodSubtype subtype) {
        if (subtype.getLocale().equals("en_GB")) {
            return mQwertyGrKeyboard;
        } else {
            return mQwertyKeyboard;
        }
    }

    /**
     * Helper to update the shift state of our keyboard based on the initial
     * editor state.
     */
    private void updateShiftKeyState(EditorInfo attr) {
        if (attr != null
                && mInputView != null
                && (mQwertyKeyboard == mInputView.getKeyboard() || mQwertyGrKeyboard == mInputView.getKeyboard()) ) {
            int caps = 0;
            EditorInfo ei = getCurrentInputEditorInfo();
            if (ei != null && ei.inputType != InputType.TYPE_NULL) {
                caps = getCurrentInputConnection().getCursorCapsMode(attr.inputType);
            }
            mInputView.setShiftKey(caps != 0, true);
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
            handleBackspace();
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
            handleShift();
        } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
            handleClose();
        } else if (primaryCode == Constants.KEYCODE_LANGUAGE_SWITCH) {
            handleLanguageSwitch();
        } else if (primaryCode == Keyboard.KEYCODE_DONE) {
            performEditorAction(mCurrentEditorInfo);
        } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE
                && mInputView != null) {
            Keyboard current = mInputView.getKeyboard();
            if (current == mSymbolsKeyboard || current == mSymbolsShiftedKeyboard) {
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
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private void handleBackspace() {
        keyDownUp(KeyEvent.KEYCODE_DEL);
        updateShiftKeyState(getCurrentInputEditorInfo());
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
        if (mQwertyKeyboard == currentKeyboard || mQwertyGrKeyboard == currentKeyboard) {
            // Alphabet keyboard
            mInputView.setShiftKey(!mInputView.isShifted(), true);
        } else if (currentKeyboard == mSymbolsKeyboard) {
            mInputView.setShiftKey(true, false);
            setLatinKeyboard(mSymbolsShiftedKeyboard);
            mInputView.setShiftKey(true, false);
        } else if (currentKeyboard == mSymbolsShiftedKeyboard) {
            mInputView.setShiftKey(false, false);
            setLatinKeyboard(mSymbolsKeyboard);
            mInputView.setShiftKey(false, false);
        }
    }

    private void handleCharacter(int primaryCode) {
        if (isInputViewShown()) {
            //force capital if isShift or field is not Password field and rules apply by setNextCharToCapital
            if (mInputView.isShifted() || (!passwordInput && setNextCharToCapital())) {
                primaryCode = Character.toUpperCase(primaryCode);
            }
        }
        getCurrentInputConnection().commitText(String.valueOf((char) primaryCode), 1);
    }

    private boolean setNextCharToCapital() {
        boolean output = false;
        String currentText = getCurrentText();
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
        mInputMethodManager.showInputMethodPicker();
        //mInputMethodManager.switchToNextInputMethod(getToken(), true);
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

