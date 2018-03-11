package com.android.music.ui.settings;

import android.content.Intent;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.android.music.ui.views.PurchaseView;

public interface SettingsView extends PurchaseView {

    // Display

    void showTabChooserDialog(MaterialDialog dialog);

    void showDefaultPageDialog(MaterialDialog dialog);

    // Themes

    void showBaseThemeDialog(MaterialDialog dialog);

    void showPrimaryColorDialog(ColorChooserDialog dialog);

    void showAccentColorDialog(ColorChooserDialog dialog);

    // Artwork

    void showDownloadArtworkDialog(MaterialDialog dialog);

    void showDeleteArtworkDialog(MaterialDialog dialog);

    void showArtworkPreferenceChangeDialog(MaterialDialog dialog);

    // Scrobbling
    void launchDownloadScrobblerIntent(Intent intent);

    // Blacklist/Whitelist

    void showBlacklistDialog(MaterialDialog dialog);

    void showWhitelistDialog(MaterialDialog dialog);
}
