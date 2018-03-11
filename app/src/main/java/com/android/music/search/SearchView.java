package com.android.music.search;

import android.support.annotation.NonNull;
import android.view.View;

import com.android.music.model.Album;
import com.android.music.model.AlbumArtist;
import com.android.music.tagger.TaggerDialog;
import com.android.music.ui.dialog.DeleteDialog;
import com.simplecityapps.recycler_adapter.model.ViewModel;

import java.util.List;

import io.reactivex.disposables.Disposable;

public interface SearchView {

    void setLoading(boolean loading);

    void setEmpty(boolean empty);

    Disposable setItems(@NonNull List<ViewModel> items);

    void setFilterFuzzyChecked(boolean checked);

    void setFilterArtistsChecked(boolean checked);

    void setFilterAlbumsChecked(boolean checked);

    void showToast(String message);

    void showTaggerDialog(@NonNull TaggerDialog taggerDialog);

    void showDeleteDialog(@NonNull DeleteDialog deleteDialog);

    void goToArtist(AlbumArtist albumArtist, View transitionView);

    void goToAlbum(Album album, View transitionView);

    void showUpgradeDialog();
}