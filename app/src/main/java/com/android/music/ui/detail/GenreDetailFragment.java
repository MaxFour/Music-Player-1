package com.android.music.ui.detail;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.music.model.Album;
import com.android.music.model.Genre;
import com.android.music.model.Song;
import com.android.music.utils.Operators;
import com.android.music.utils.PlaceholderProvider;
import com.android.music.utils.SortManager;

import java.util.List;

import io.reactivex.Single;

public class GenreDetailFragment extends BaseDetailFragment {

    public static String ARG_GENRE = "genre";

    private Genre genre;

    public static GenreDetailFragment newInstance(Genre genre) {
        Bundle args = new Bundle();
        GenreDetailFragment fragment = new GenreDetailFragment();
        args.putSerializable(ARG_GENRE, genre);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        genre = (Genre) getArguments().getSerializable(ARG_GENRE);
    }

    @Override
    boolean canPlaySlideshow() {
        return true;
    }

    @Override
    void setSongSortOrder(int sortOrder) {
        SortManager.getInstance().setGenreDetailSongsSortOrder(sortOrder);
    }

    @Override
    int getSongSortOrder() {
        return SortManager.getInstance().getGenreDetailSongsSortOrder();
    }

    @Override
    void setSongsAscending(boolean ascending) {
        SortManager.getInstance().setGenreDetailSongsAscending(ascending);
    }

    @Override
    boolean getSongsAscending() {
        return SortManager.getInstance().getGenreDetailSongsAscending();
    }

    @Override
    void setAlbumSortOrder(int sortOrder) {
        SortManager.getInstance().setGenreDetailAlbumsSortOrder(sortOrder);
    }

    @Override
    int getAlbumSort() {
        return SortManager.getInstance().getGenreDetailAlbumsSortOrder();
    }

    @Override
    void setAlbumsAscending(boolean ascending) {
        SortManager.getInstance().setGenreDetailAlbumsAscending(ascending);
    }

    @Override
    boolean getAlbumsAscending() {
        return SortManager.getInstance().getGenreDetailAlbumsAscending();
    }

    @NonNull
    @Override
    public Single<List<Song>> getSongs() {
        return genre.getSongsObservable().map(songs -> {
            sortSongs(songs);
            return songs;
        });
    }

    @NonNull
    @Override
    public Single<List<Album>> getAlbums() {
        return getSongs().map(Operators::songsToAlbums).map(albums -> {
            sortAlbums(albums);
            return albums;
        });
    }

    @NonNull
    @Override
    protected String getToolbarTitle() {
        return genre.name;
    }

    @NonNull
    @Override
    Drawable getPlaceHolderDrawable() {
        return PlaceholderProvider.getInstance().getPlaceHolderDrawable(genre.name, true);
    }

    @Override
    protected String screenName() {
        return "GenreDetailFragment";
    }

    @Override
    public void showUpgradeDialog(MaterialDialog upgradeDialog) {
        upgradeDialog.show();
    }
}
