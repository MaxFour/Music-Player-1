package com.android.music.dagger.module;

import com.android.music.ui.views.multisheet.MultiSheetEventRelay;
import com.android.music.ui.views.multisheet.MultiSheetSlideEventRelay;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MultiSheetModule {

    @Provides
    @Singleton
    MultiSheetEventRelay provideMultiSheetEventRelay() {
        return new MultiSheetEventRelay();
    }

    @Provides
    @Singleton
    MultiSheetSlideEventRelay provideMultiSheetSlideEventRelay() {
        return new MultiSheetSlideEventRelay();
    }

}