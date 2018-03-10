package com.android.music.ui.views;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.MediaRouteButton;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;

import com.afollestad.aesthetic.Aesthetic;
import com.android.music.R;
import com.android.music.ui.dialog.UpgradeDialog;
import com.android.music.utils.MPlayerUtils;

public class CustomMediaRouteButton extends MediaRouteButton {

    @Nullable
    private Activity activity;

    public CustomMediaRouteButton(Context context) {
        this(context, null);
    }

    public CustomMediaRouteButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.mediaRouteButtonStyle);
    }

    public CustomMediaRouteButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(getThemedContext(context), attrs, defStyleAttr);
    }

    private static Context getThemedContext(Context context) {
        return Aesthetic.get(context).isDark().blockingFirst() ?
                new ContextThemeWrapper(new ContextThemeWrapper(context, R.style.Theme_AppCompat), R.style.Theme_MediaRouter) :
                new ContextThemeWrapper(new ContextThemeWrapper(context, R.style.Theme_AppCompat_Light), R.style.Theme_MediaRouter);
    }

    public void setActivity(@NonNull Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean showDialog() {

        if (!MPlayerUtils.isUpgraded()) {
            if (activity != null) {
                UpgradeDialog.getUpgradeDialog(activity).show();
            }
            return false;
        }

        return super.showDialog();
    }
}