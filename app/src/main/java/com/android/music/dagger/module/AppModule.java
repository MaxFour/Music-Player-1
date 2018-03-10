package com.android.music.dagger.module;

import android.content.Context;

import com.android.music.MusicApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private MusicApplication application;

    public AppModule(MusicApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return application;
    }

}