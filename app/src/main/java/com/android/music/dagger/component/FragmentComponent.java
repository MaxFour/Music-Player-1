package com.android.music.dagger.component;

import com.android.music.dagger.module.FragmentModule;
import com.android.music.dagger.module.PresenterModule;
import com.android.music.dagger.scope.FragmentScope;
import com.android.music.search.SearchFragment;
import com.android.music.ui.fragments.AlbumArtistFragment;
import com.android.music.ui.fragments.AlbumFragment;
import com.android.music.ui.fragments.BaseFragment;
import com.android.music.ui.fragments.MiniPlayerFragment;
import com.android.music.ui.fragments.PlayerFragment;
import com.android.music.ui.fragments.QueueFragment;
import com.android.music.ui.fragments.QueuePagerFragment;
import com.android.music.ui.fragments.SuggestedFragment;
import com.android.music.ui.presenters.PlayerPresenter;

import dagger.Subcomponent;

@FragmentScope
@Subcomponent(modules = {
        FragmentModule.class,
        PresenterModule.class})

public interface FragmentComponent {

    void inject(BaseFragment target);

    void inject(PlayerFragment target);

    void inject(MiniPlayerFragment target);

    void inject(PlayerPresenter target);

    void inject(QueuePagerFragment target);

    void inject(QueueFragment target);

    void inject(AlbumArtistFragment target);

    void inject(AlbumFragment target);

    void inject(SuggestedFragment target);

    void inject(SearchFragment target);
}