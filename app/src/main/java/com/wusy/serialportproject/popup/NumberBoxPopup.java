package com.wusy.serialportproject.popup;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import com.wusy.serialportproject.R;
import com.wusy.wusylibrary.view.NumberKeyBoxView;
import com.wusy.wusylibrary.view.PwdIndicator;
import razerdp.basepopup.BasePopupWindow;

/**
 * Created by XIAO RONG on 2019/1/4.
 */

public class NumberBoxPopup extends BasePopupWindow {
    private PwdIndicator pwdIndicator;
    private NumberKeyBoxView numberKeyBoxView;

    public NumberBoxPopup(Context context) {
        super(context);
        setBlurBackgroundEnable(true);
        findView();
    }
    private void findView(){
        pwdIndicator=getContentView().findViewById(R.id.pwdindicator);
        numberKeyBoxView=getContentView().findViewById(R.id.numberkeyboxview);
    }
    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.dialog_numberkeybox);
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return getDefaultScaleAnimation(true);
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return getDefaultScaleAnimation(false);
    }

    public PwdIndicator getPwdIndicator() {
        return pwdIndicator;
    }

    public NumberKeyBoxView getNumberKeyBoxView() {
        return numberKeyBoxView;
    }
}
