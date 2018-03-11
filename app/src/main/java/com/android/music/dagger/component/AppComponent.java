package com.android.music.dagger.component;


import com.android.music.dagger.module.AppModule;
import com.android.music.dagger.module.DrawerModule;
import com.android.music.dagger.module.FragmentModule;
import com.android.music.dagger.module.ActivityModule;
import com.android.music.dagger.module.ModelsModule;
import com.android.music.dagger.module.MultiSheetModule;
import com.android.music.ui.activities.MainActivity;
import com.android.music.ui.fragments.LibraryController;
import com.android.music.ui.fragments.MainController;
import com.android.music.ui.views.UpNextView;
import com.android.music.ui.views.multisheet.CustomMultiSheetView;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        AppModule.class,
        ModelsModule.class,
        MultiSheetModule.class,
        DrawerModule.class})

public interface AppComponent {

    FragmentComponent plus(FragmentModule module);

    ActivityComponent plus(ActivityModule module);

    void inject(MainActivity target);

    void inject(CustomMultiSheetView target);

    void inject(MainController target);

    void inject(LibraryController target);

    void inject(UpNextView target);
}