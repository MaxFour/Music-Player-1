package com.android.music.dagger.module;

import android.support.annotation.NonNull;

import com.android.music.model.PlaylistsModel;

import dagger.Module;
import dagger.Provides;

@Module
public class ModelsModule {

    @Provides
    @NonNull
    public PlaylistsModel providePlaylistsModel() {
        return new PlaylistsModel();
    }

}