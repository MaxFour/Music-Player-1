package com.android.music.dagger.component;

import com.android.music.dagger.module.ActivityModule;
import com.android.music.dagger.module.PresenterModule;
import com.android.music.dagger.scope.ActivityScope;
import com.android.music.ui.drawer.DrawerFragment;
import com.android.music.ui.settings.SettingsParentFragment;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {
        ActivityModule.class,
        PresenterModule.class})

public interface ActivityComponent {

    void inject(DrawerFragment target);

    void inject(SettingsParentFragment.SettingsFragment target);
}