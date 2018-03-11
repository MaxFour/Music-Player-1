package com.android.music.dagger.module;

import com.android.music.ui.drawer.NavigationEventRelay;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DrawerModule {

    @Provides
    @Singleton
    NavigationEventRelay provideDrawerEventRelay() {
        return new NavigationEventRelay();
    }

}