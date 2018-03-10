package com.android.music.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.android.music.BuildConfig;
import com.android.music.MusicApplication;
import com.android.music.R;
import com.android.music.ui.presenters.Presenter;
import com.android.music.utils.SettingsManager;
import com.android.music.utils.MPlayerUtils;

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

    public void siteClicked() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://github.com/MaxFour"));
        AboutView aboutView = getView();
        if (aboutView != null) {
            aboutView.visitSite(intent);
        }
    }
}