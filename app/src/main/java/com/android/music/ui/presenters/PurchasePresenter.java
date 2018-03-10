package com.android.music.ui.presenters;

import android.app.Activity;

import com.android.music.ui.dialog.UpgradeDialog;
import com.android.music.ui.views.PurchaseView;

public class PurchasePresenter<V extends PurchaseView> extends Presenter<V> {

    private Activity activity;

    public PurchasePresenter(Activity activity) {
        this.activity = activity;
    }

}