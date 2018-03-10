package com.android.music.lyrics;

import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.cantrowitz.rxbroadcast.RxBroadcast;
import com.android.music.MusicApplication;
import com.android.music.model.Query;
import com.android.music.model.Song;
import com.android.music.playback.MusicService;
import com.android.music.sql.SqlUtils;
import com.android.music.ui.presenters.Presenter;
import com.android.music.utils.LogUtils;
import com.android.music.utils.MusicUtils;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;

class LyricsPresenter extends Presenter<LyricsView> {

    private static final String TAG = "LyricsPresenter";

    @Override
    public void bindView(@NonNull LyricsView view) {
        super.bindView(view);

        updateLyrics();

        addDisposable(RxBroadcast.fromBroadcast(MusicApplication.getInstance(), new IntentFilter(MusicService.InternalIntents.META_CHANGED))
                .toFlowable(BackpressureStrategy.LATEST)
                .subscribe(intent -> updateLyrics(), error -> LogUtils.logException(TAG, "Error receiving meta changed", error)));
    }

    void downloadOrLaunchQuickLyric() {
        LyricsView lyricsView = getView();
        if (lyricsView != null) {
            if (QuickLyricUtils.isQLInstalled()) {
                Song song = MusicUtils.getSong();
                if (song != null) {
                    lyricsView.launchQuickLyric(song);
                }
            } else {
                lyricsView.downloadQuickLyric();
            }
        }
    }

    void showQuickLyricInfoDialog() {
        LyricsView lyricsView = getView();
        if (lyricsView != null) {
            lyricsView.showQuickLyricInfoDialog();
        }
    }

    private void updateLyrics() {

        addDisposable(Observable.fromCallable(() -> {

            String lyrics = "";
            String path = MusicUtils.getFilePath();

            if (TextUtils.isEmpty(path)) {
                return lyrics;
            }

            if (path.startsWith("content://")) {
                Query query = new Query.Builder()
                        .uri(Uri.parse(path))
                        .projection(new String[]{MediaStore.Audio.Media.DATA})
                        .build();

                Cursor cursor = SqlUtils.createQuery(MusicApplication.getInstance(), query);
                if (cursor != null) {
                    try {
                        int colIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                        if (cursor.moveToFirst()) {
                            path = cursor.getString(colIndex);
                        }
                    } finally {
                        cursor.close();
                    }
                }
            }

            File file = new File(path);
            if (file.exists()) {
                try {
                    AudioFile audioFile = AudioFileIO.read(file);
                    if (audioFile != null) {
                        Tag tag = audioFile.getTag();
                        if (tag != null) {
                            String tagLyrics = tag.getFirst(FieldKey.LYRICS);
                            if (tagLyrics != null && tagLyrics.length() != 0) {
                                lyrics = tagLyrics.replace("\r", "\n");
                            }
                        }
                    }
                } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException | UnsupportedOperationException ignored) {
                }
            }

            return lyrics;
        }).subscribe(lyrics -> {
            LyricsView lyricsView = getView();
            if (lyricsView != null) {
                lyricsView.updateLyrics(lyrics);
                lyricsView.showNoLyricsView(TextUtils.isEmpty(lyrics));
                lyricsView.showQuickLyricInfoButton(!QuickLyricUtils.isQLInstalled());
            }
        }, error -> LogUtils.logException(TAG, "Error getting lyrics", error)));
    }
}