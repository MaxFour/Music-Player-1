package com.android.music.lyrics;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.music.model.Song;

interface LyricsView {

    void updateLyrics(@Nullable String lyrics);

    void showNoLyricsView(boolean show);

    void showQuickLyricInfoButton(boolean show);

    void showQuickLyricInfoDialog();

    void downloadQuickLyric();

    void launchQuickLyric(@NonNull Song song);
}
