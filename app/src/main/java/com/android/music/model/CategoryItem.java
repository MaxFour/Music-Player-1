package com.android.music.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import com.android.music.R;
import com.android.music.ui.fragments.AlbumArtistFragment;
import com.android.music.ui.fragments.AlbumFragment;
import com.android.music.ui.fragments.FolderFragment;
import com.android.music.ui.fragments.GenreFragment;
import com.android.music.ui.fragments.PlaylistFragment;
import com.android.music.ui.fragments.SongFragment;
import com.android.music.ui.fragments.SuggestedFragment;
import com.android.music.utils.ComparisonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryItem {

    public @interface Type {
        int ALBUMS = 0;
        int ARTISTS = 1;
        int SONGS = 2;
        int GENRES = 3;
        int PLAYLISTS = 4;
        int SUGGESTED = 5;
        int FOLDERS = 6;
    }

    @Type public int type;

    public int sortOrder;

    public boolean isChecked;

    private CategoryItem(@Type int type, SharedPreferences sharedPreferences) {
        this.type = type;
        isChecked = sharedPreferences.getBoolean(getEnabledKey(), isEnabledByDefault());
        sortOrder = sharedPreferences.getInt(getSortKey(), 0);
    }

    public static List<CategoryItem> getCategoryItems(SharedPreferences sharedPreferences) {
        List<CategoryItem> items = new ArrayList<>();
        items.add(new CategoryItem(Type.ALBUMS, sharedPreferences));
        items.add(new CategoryItem(Type.ARTISTS, sharedPreferences));
        items.add(new CategoryItem(Type.SONGS, sharedPreferences));
        items.add(new CategoryItem(Type.GENRES, sharedPreferences));
        items.add(new CategoryItem(Type.PLAYLISTS, sharedPreferences));
        items.add(new CategoryItem(Type.SUGGESTED, sharedPreferences));
        items.add(new CategoryItem(Type.FOLDERS, sharedPreferences));

        Collections.sort(items, (a, b) -> ComparisonUtils.compareInt(a.sortOrder, b.sortOrder));
        return items;
    }

    public void savePrefs(SharedPreferences.Editor editor) {
        editor.putBoolean(getEnabledKey(), isChecked);
        editor.putInt(getSortKey(), sortOrder);
        editor.apply();
    }

    @StringRes
    public int getTitleResId() {
        switch (type) {
            case Type.ALBUMS:
                return R.string.albums_title;
            case Type.ARTISTS:
                return R.string.artists_title;
            case Type.SONGS:
                return R.string.tracks_title;
            case Type.GENRES:
                return R.string.genres_title;
            case Type.PLAYLISTS:
                return R.string.playlists_title;
            case Type.SUGGESTED:
                return R.string.suggested_title;
            case Type.FOLDERS:
                return R.string.folders_title;

        }
        return -1;
    }

    public String getKey() {
        switch (type) {
            case Type.ALBUMS:
                return "albums";
            case Type.ARTISTS:
                return "artists";
            case Type.SONGS:
                return "songs";
            case Type.GENRES:
                return "genres";
            case Type.PLAYLISTS:
                return "playlists";
            case Type.SUGGESTED:
                return "suggested";
            case Type.FOLDERS:
                return "folders";

        }
        return null;
    }

    public boolean isEnabledByDefault() {
        switch (type) {
            case Type.ALBUMS:
                return true;
            case Type.ARTISTS:
                return true;
            case Type.SONGS:
                return true;
            case Type.GENRES:
                return true;
            case Type.PLAYLISTS:
                return false;
            case Type.SUGGESTED:
                return false;
            case Type.FOLDERS:
                return false;

        }
        return true;
    }

    public String getSortKey() {
        return getKey() + "_sort";
    }

    public String getEnabledKey() {
        return getKey() + "_enabled";
    }

    public Fragment getFragment(Context context) {
        switch (type) {
            case Type.ARTISTS:
                return AlbumArtistFragment.newInstance(context.getString(getTitleResId()));
            case Type.ALBUMS:
                return AlbumFragment.newInstance(context.getString(getTitleResId()));
            case Type.SONGS:
                return SongFragment.newInstance(context.getString(getTitleResId()));
            case Type.GENRES:
                return GenreFragment.newInstance(context.getString(getTitleResId()));
            case Type.PLAYLISTS:
                return PlaylistFragment.newInstance(context.getString(getTitleResId()));
            case Type.SUGGESTED:
                return SuggestedFragment.newInstance(context.getString(getTitleResId()));
            case Type.FOLDERS:
                return FolderFragment.newInstance(context.getString(getTitleResId()), true);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryItem that = (CategoryItem) o;

        return type == that.type;
    }

    @Override
    public int hashCode() {
        return type;
    }
}