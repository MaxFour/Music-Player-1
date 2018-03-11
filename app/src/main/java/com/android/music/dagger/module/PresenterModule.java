package com.android.music.dagger.module;

import com.android.music.search.SearchPresenter;
import com.android.music.search.SearchView;
import com.android.music.ui.presenters.PlayerPresenter;
import com.android.music.ui.presenters.Presenter;
import com.android.music.ui.presenters.QueuePagerPresenter;
import com.android.music.ui.presenters.QueuePresenter;
import com.android.music.ui.views.PlayerView;
import com.android.music.ui.views.QueuePagerView;
import com.android.music.ui.views.QueueView;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class PresenterModule {

    @Binds
    abstract Presenter<PlayerView> bindPlayerPresenter(PlayerPresenter playerPresenter);

    @Binds
    abstract Presenter<QueuePagerView> bindQueuePagerPresenter(QueuePagerPresenter queuePagerPresenter);

    @Binds
    abstract Presenter<QueueView> bindQueuePresenter(QueuePresenter queuePresenter);

    @Binds
    abstract Presenter<SearchView> bindSearchPresenter(SearchPresenter queuePresenter);
}