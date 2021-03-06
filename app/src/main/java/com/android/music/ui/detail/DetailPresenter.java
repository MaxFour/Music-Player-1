package com.android.music.ui.detail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.music.model.Playlist;
import com.android.music.tagger.TaggerDialog;
import com.android.music.ui.presenters.Presenter;
import com.android.music.utils.MusicUtils;
import com.android.music.utils.PermissionUtils;
import com.android.music.utils.PlaylistUtils;
import com.android.music.utils.MPlayerUtils;
import com.simplecityapps.recycler_adapter.model.ViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class DetailPresenter extends Presenter<DetailView> {

    @NonNull
    private SongsProvider songsProvider;

    @NonNull
    private AlbumsProvider albumsProvider;

    DetailPresenter(@NonNull SongsProvider songsProvider, @NonNull AlbumsProvider albumsProvider) {
        this.songsProvider = songsProvider;
        this.albumsProvider = albumsProvider;
    }

    void loadData() {
        PermissionUtils.RequestStoragePermissions(() ->
                addDisposable(songsProvider.getSongs().zipWith(albumsProvider.getAlbums(), (songs, albums) -> {
                    List<ViewModel> viewModels = new ArrayList<>();
                    viewModels.addAll(albumsProvider.getAlbumViewModels(albums));
                    viewModels.addAll(songsProvider.getSongViewModels(songs));
                    return viewModels;
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(adaptableItems -> {
                            DetailView detailView = getView();
                            if (detailView != null) {
                                if (adaptableItems.isEmpty()) {
                                    detailView.setEmpty(true);
                                } else {
                                    detailView.itemsLoaded(adaptableItems);
                                }
                            }
                        })));
    }

    void fabClicked() {
        MusicUtils.shuffleAll(songsProvider.getSongs(), message -> {
            DetailView detailView = getView();
            if (detailView != null) {
                detailView.showToast(message);
            }
        });
    }

    void playAll() {
        MusicUtils.playAll(songsProvider.getSongs(), message -> {
            DetailView detailView = getView();
            if (detailView != null) {
                detailView.showToast(message);
            }
        });
    }

    void addToQueue() {
        songsProvider.getSongs().observeOn(AndroidSchedulers.mainThread())
                .subscribe(songs -> MusicUtils.addToQueue(songs, message -> {
                    DetailView detailView = getView();
                    if (detailView != null) {
                        detailView.showToast(message);
                    }
                }));
    }

    void editTags(TaggerDialog taggerDialog, MaterialDialog upgradeDialog) {
        DetailView detailView = getView();
        if (detailView != null) {
            if (!MPlayerUtils.isUpgraded()) {
                detailView.showUpgradeDialog(upgradeDialog);
            } else {
                detailView.showTaggerDialog(taggerDialog);
            }
        }
    }

    void editArtwork(MaterialDialog artworkDialog) {
        DetailView detailView = getView();
        if (detailView != null) {
            detailView.showArtworkDialog(artworkDialog);
        }
    }

    void newPlaylist(Context context, Runnable insertCallback) {
        songsProvider.getSongs().observeOn(AndroidSchedulers.mainThread())
                .subscribe(songs -> PlaylistUtils.createPlaylistDialog(context, songs, insertCallback));
    }

    void playlistSelected(Context context, MenuItem item, Runnable insertCallback) {
        songsProvider.getSongs().observeOn(AndroidSchedulers.mainThread())
                .subscribe(songs -> {
                    Playlist playlist = (Playlist) item.getIntent().getSerializableExtra(PlaylistUtils.ARG_PLAYLIST);
                    PlaylistUtils.addToPlaylist(context, playlist, songs, insertCallback);
                });
    }

    void infoClicked(MaterialDialog dialog) {
        DetailView detailView = getView();
        if (detailView != null) {
            detailView.showInfoDialog(dialog);
        }
    }
}
