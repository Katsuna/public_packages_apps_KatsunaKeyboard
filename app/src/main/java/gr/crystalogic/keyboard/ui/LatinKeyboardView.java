package gr.crystalogic.keyboard.ui;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import java.util.HashMap;
import java.util.Map;

import gr.crystalogic.keyboard.R;

public class LatinKeyboardView extends KeyboardView {

    public LatinKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LatinKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setPreviewEnabled(false);
        mMiniKeyboardCache = new HashMap<>();

        mPopupLayout = R.layout.popup_keyboard;
        mPopupKeyboard = new PopupWindow(this.getContext());
        mPopupKeyboard.setBackgroundDrawable(null);
    }

    private PopupWindow popup;

    private View mMiniKeyboardContainer;
    private Map<Keyboard.Key,View> mMiniKeyboardCache;
    private int mPopupLayout;
    private KeyboardView mMiniKeyboard;
    private final int[] mCoordinates = new int[2];
    private int mPopupX;
    private int mPopupY;
    private int mPaddingLeft = 10;
    private int mPaddingTop = 10;
    private PopupWindow mPopupKeyboard;
    private boolean mMiniKeyboardOnScreen;

    @Override
    protected boolean onLongPress(final Keyboard.Key popupKey) {
        int popupKeyboardId = popupKey.popupResId;

        if (popupKeyboardId != 0) {
            mMiniKeyboardContainer = mMiniKeyboardCache.get(popupKey);
            if (mMiniKeyboardContainer == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                mMiniKeyboardContainer = inflater.inflate(mPopupLayout, null);
                mMiniKeyboard = (KeyboardView) mMiniKeyboardContainer.findViewById(R.id.mykeyboard);
                mMiniKeyboard.setPreviewEnabled(false);
/*                View closeButton = mMiniKeyboardContainer.findViewById(
                        com.android.internal.R.id.closeButton);
                if (closeButton != null) closeButton.setOnClickListener(this);
*/
                mMiniKeyboard.setOnKeyboardActionListener(new OnKeyboardActionListener() {
                    public void onKey(int primaryCode, int[] keyCodes) {
                        getOnKeyboardActionListener().onKey(primaryCode, keyCodes);
                        dismissPopupKeyboard();
                    }

                    public void onText(CharSequence text) {
                        getOnKeyboardActionListener().onText(text);
                        dismissPopupKeyboard();
                    }

                    public void swipeLeft() { }
                    public void swipeRight() { }
                    public void swipeUp() { }
                    public void swipeDown() { }
                    public void onPress(int primaryCode) {
                        getOnKeyboardActionListener().onPress(primaryCode);
                    }
                    public void onRelease(int primaryCode) {
                        getOnKeyboardActionListener().onRelease(primaryCode);
                    }
                });
                //mInputView.setSuggest(mSuggest);
                Keyboard keyboard;
                if (popupKey.popupCharacters != null) {
                    keyboard = new Keyboard(getContext(), popupKeyboardId,
                            popupKey.popupCharacters, -1, getPaddingLeft() + getPaddingRight());
                } else {
                    keyboard = new Keyboard(getContext(), popupKeyboardId);
                }
                mMiniKeyboard.setKeyboard(keyboard);
                mMiniKeyboard.setPopupParent(this);
                mMiniKeyboardContainer.measure(
                        MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
                        MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));

                mMiniKeyboardCache.put(popupKey, mMiniKeyboardContainer);
            } else {
                mMiniKeyboard = (KeyboardView) mMiniKeyboardContainer.findViewById(R.id.mykeyboard);
            }
            getLocationInWindow(mCoordinates);
            mPopupX = popupKey.x + mPaddingLeft;
            mPopupY = popupKey.y + mPaddingTop;
            mPopupX = mPopupX + popupKey.width - mMiniKeyboardContainer.getMeasuredWidth();
            mPopupY = mPopupY - mMiniKeyboardContainer.getMeasuredHeight();
            final int x = mPopupX + mMiniKeyboardContainer.getPaddingRight() + mCoordinates[0];
            final int y = mPopupY + mMiniKeyboardContainer.getPaddingBottom() + mCoordinates[1];
            mMiniKeyboard.setPopupOffset(x < 0 ? 0 : x, y);
            mMiniKeyboard.setShifted(isShifted());
            mPopupKeyboard.setContentView(mMiniKeyboardContainer);
            mPopupKeyboard.setWidth(mMiniKeyboardContainer.getMeasuredWidth());
            mPopupKeyboard.setHeight(mMiniKeyboardContainer.getMeasuredHeight());
            mPopupKeyboard.showAtLocation(this, Gravity.NO_GRAVITY, x, y);
            mMiniKeyboardOnScreen = true;
            //mMiniKeyboard.onTouchEvent(getTranslatedEvent(me));
            invalidateAllKeys();
            return true;
        }
        return false;

/*        final View custom = LayoutInflater.from(this.getContext())
                .inflate(R.layout.null_layout, new FrameLayout(this.getContext()));
        popup = new PopupWindow(this.getContext());
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.showAtLocation(this, Gravity.NO_GRAVITY, popupKey.x, popupKey.y-50);
        return true;*/
    }

    private void dismissPopupKeyboard() {
        if (mPopupKeyboard.isShowing()) {
            mPopupKeyboard.dismiss();
            mMiniKeyboardOnScreen = false;
            invalidateAllKeys();
        }
    }

    public boolean handleBack() {
        if (mPopupKeyboard.isShowing()) {
            dismissPopupKeyboard();
            return true;
        }
        return false;
    }

}