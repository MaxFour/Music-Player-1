package com.android.music.ui.settings;

import android.support.annotation.NonNull;

import com.android.music.BuildConfig;
import com.android.music.ui.presenters.Presenter;

import javax.inject.Inject;

public class AboutPresenter extends Presenter<AboutView> {

    @Inject
    public AboutPresenter() {

    }

    @Override
    public void bindView(@NonNull AboutView view) {
        super.bindView(view);

        setAppVersion();
    }

    private void setAppVersion() {
        AboutView aboutView = getView();
        if (aboutView != null) {
            aboutView.setVersion(BuildConfig.VERSION_NAME);
        }
    }
}