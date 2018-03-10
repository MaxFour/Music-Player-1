package com.android.music.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.Util;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.RequestManager;
import com.android.music.R;
import com.android.music.MusicApplication;
import com.android.music.dagger.module.FragmentModule;
import com.android.music.model.Song;
import com.android.music.tagger.TaggerDialog;
import com.android.music.ui.dialog.DeleteDialog;
import com.android.music.ui.dialog.UpgradeDialog;
import com.android.music.ui.modelviews.SelectableViewModel;
import com.android.music.ui.modelviews.SongView;
import com.android.music.ui.presenters.PlayerPresenter;
import com.android.music.ui.presenters.QueuePresenter;
import com.android.music.ui.recyclerview.ItemTouchHelperCallback;
import com.android.music.ui.views.ContextualToolbar;
import com.android.music.ui.views.PlayerViewAdapter;
import com.android.music.ui.views.QueueView;
import com.android.music.ui.views.ThemedStatusBarView;
import com.android.music.ui.views.multisheet.MultiSheetSlideEventRelay;
import com.android.music.utils.ContextualToolbarHelper;
import com.android.music.utils.ContextualToolbarHelper.Callback;
import com.android.music.utils.MenuUtils;
import com.android.music.utils.MusicUtils;
import com.android.music.utils.PermissionUtils;
import com.android.music.utils.PlaylistUtils;
import com.android.music.utils.ResourceUtils;
import com.android.music.utils.MPlayerUtils;
import com.simplecity.multisheetview.ui.view.MultiSheetView;
import com.simplecityapps.recycler_adapter.adapter.CompletionListUpdateCallbackAdapter;
import com.simplecityapps.recycler_adapter.adapter.ViewModelAdapter;
import com.simplecityapps.recycler_adapter.model.ViewModel;
import com.simplecityapps.recycler_adapter.recyclerview.RecyclerListener;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class QueueFragment extends BaseFragment implements QueueView {

    private static final String TAG = "QueueFragment";

    private final CompositeDisposable disposables = new CompositeDisposable();

    @BindView(R.id.statusBarView)
    ThemedStatusBarView statusBarView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.line1)
    TextView lineOne;

    @BindView(R.id.line2)
    TextView lineTwo;

    @BindView(R.id.recyclerView)
    FastScrollRecyclerView recyclerView;

    @BindView(R.id.contextualToolbar)
    ContextualToolbar cabToolbar;

    @Inject
    RequestManager requestManager;

    @Inject
    MultiSheetSlideEventRelay multiSheetSlideEventRelay;

    @Inject
    PlayerPresenter playerPresenter;

    QueuePresenter queuePresenter;

    ItemTouchHelper itemTouchHelper;

    ViewModelAdapter adapter;

    ContextualToolbarHelper<Song> cabHelper;

    Disposable loadDataDisposable;

    Unbinder unbinder;

    public static QueueFragment newInstance() {
        Bundle args = new Bundle();
        QueueFragment fragment = new QueueFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MusicApplication.getInstance().getAppComponent()
                .plus(new FragmentModule(this))
                .inject(this);
        setHasOptionsMenu(true);
        adapter = new ViewModelAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_queue, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
        toolbar.inflateMenu(R.menu.menu_queue);

        SubMenu sub = toolbar.getMenu().addSubMenu(0, MusicUtils.Defs.ADD_TO_PLAYLIST, 1, R.string.save_as_playlist);
        disposables.add(PlaylistUtils.createUpdatingPlaylistMenu(sub).subscribe());

        toolbar.setOnMenuItemClickListener(toolbarListener);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setRecyclerListener(new RecyclerListener());
        recyclerView.setAdapter(adapter);

        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(
                (fromPosition, toPosition) ->
                        adapter.moveItem(fromPosition, toPosition), MusicUtils::moveQueueItem,
                () -> {
                    // Nothing to do
                }));

        itemTouchHelper.attachToRecyclerView(recyclerView);

        disposables.add(Aesthetic.get(getContext())
                .colorPrimary()
                .subscribe(color -> {
                    boolean isLight = Util.isColorLight(color);
                    lineOne.setTextColor(isLight ? Color.BLACK : Color.WHITE);
                    lineTwo.setTextColor(isLight ? Color.BLACK : Color.WHITE);
                }));

        // In landscape, we need to adjust the status bar's translation depending on the slide offset of the sheet
        if (MPlayerUtils.isLandscape()) {
            statusBarView.setTranslationY(ResourceUtils.toPixels(16));

            disposables.add(multiSheetSlideEventRelay.getEvents()
                    .filter(multiSheetEvent -> multiSheetEvent.sheet == MultiSheetView.Sheet.SECOND)
                    .filter(multiSheetEvent -> multiSheetEvent.slideOffset >= 0)
                    .subscribe(multiSheetEvent -> {
                        statusBarView.setTranslationY((1 - multiSheetEvent.slideOffset) * ResourceUtils.toPixels(16));
                    }));
        }

        setupContextualToolbar();
        queuePresenter = new QueuePresenter(requestManager, cabHelper);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
        playerPresenter.bindView(playerViewAdapter);
        queuePresenter.bindView(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (loadDataDisposable != null) {
            loadDataDisposable.dispose();
        }
        playerPresenter.unbindView(playerViewAdapter);
        queuePresenter.unbindView(this);
    }

    @Override
    public void onDestroyView() {
        disposables.clear();
        unbinder.unbind();
        super.onDestroyView();
    }

    private void setupContextualToolbar() {
        cabToolbar.getMenu().clear();
        cabToolbar.inflateMenu(R.menu.context_menu_queue);

        final SubMenu sub = cabToolbar.getMenu().findItem(R.id.queue_add_to_playlist).getSubMenu();
        disposables.add(PlaylistUtils.createUpdatingPlaylistMenu(sub).subscribe());
        cabToolbar.setOnMenuItemClickListener(MenuUtils.getQueueMenuClickListener(getContext(),
                Single.fromCallable(() -> cabHelper.getItems()),
                deleteDialog -> deleteDialog.show(getChildFragmentManager()), () -> {
                    queuePresenter.removeFromQueue(cabHelper.getItems());
                    cabHelper.finish();
                }, () -> cabHelper.finish()));

        cabHelper = new ContextualToolbarHelper<>(cabToolbar, new Callback() {
            @Override
            public void notifyItemChanged(int position, SelectableViewModel viewModel) {
                adapter.notifyItemChanged(position, 0);
            }

            @Override
            public void notifyDatasetChanged() {
                adapter.notifyItemRangeChanged(0, adapter.items.size(), 0);
            }
        });
    }

    @Override
    protected String screenName() {
        return TAG;
    }

    @Override
    public void loadData(List<ViewModel> items, int position) {
        PermissionUtils.RequestStoragePermissions(() -> {
            if (getActivity() != null && isAdded()) {
                if (loadDataDisposable != null) {
                    loadDataDisposable.dispose();
                }
                loadDataDisposable = adapter.setItems(items, new CompletionListUpdateCallbackAdapter() {
                    @Override
                    public void onComplete() {
                        updateQueuePosition(position, false);
                        if (recyclerView != null) {
                            recyclerView.scrollToPosition(position);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void showToast(String message, int duration) {
        Toast.makeText(getContext(), message, duration).show();
    }

    @Override
    public void startDrag(SongView.ViewHolder holder) {
        itemTouchHelper.startDrag(holder);
    }


    @Override
    public void updateQueuePosition(int position, boolean fromUser) {
        if (adapter.items.isEmpty() || position >= adapter.items.size() || position < 0) {
            return;
        }
        if (recyclerView == null) {
            return;
        }
        if (!fromUser) {
            recyclerView.scrollToPosition(position);
        }
        int prevPosition = -1;
        int len = adapter.items.size();
        for (int i = 0; i < len; i++) {
            ViewModel viewModel = adapter.items.get(i);
            if (viewModel instanceof SongView) {
                if (((SongView) viewModel).isCurrentTrack()) {
                    prevPosition = i;
                }
                ((SongView) viewModel).setCurrentTrack(i == position);
            }
        }
        ((SongView) adapter.items.get(position)).setCurrentTrack(true);
        adapter.notifyItemChanged(prevPosition, 1);
        adapter.notifyItemChanged(position, 1);
    }

    @Override
    public void showTaggerDialog(TaggerDialog taggerDialog) {
        taggerDialog.show(getChildFragmentManager());
    }

    @Override
    public void showDeleteDialog(DeleteDialog deleteDialog) {
        deleteDialog.show(getChildFragmentManager());
    }

    @Override
    public void removeFromQueue(int position) {
        adapter.removeItem(position);
    }

    @Override
    public void removeFromQueue(List<Song> songs) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void moveQueueItem(int from, int to) {
        adapter.moveItem(from, to);
    }

    @Override
    public void showUpgradeDialog() {
        UpgradeDialog.getUpgradeDialog(getActivity()).show();
    }

    private final PlayerViewAdapter playerViewAdapter = new PlayerViewAdapter() {
        @Override
        public void trackInfoChanged(@Nullable Song song) {
            if (song != null) {
                lineOne.setText(song.name);
                if (song.albumArtistName != null && song.albumName != null) {
                    lineTwo.setText(String.format("%s | %s", song.albumArtistName, song.albumName));
                }
            }
        }

        @Override
        public void showUpgradeDialog(MaterialDialog dialog) {
            dialog.show();
        }
    };

    Toolbar.OnMenuItemClickListener toolbarListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_clear:
                    queuePresenter.clearQueue();
                    return true;
                case MusicUtils.Defs.NEW_PLAYLIST:
                    queuePresenter.saveQueue(getContext());
                    return true;
                case MusicUtils.Defs.PLAYLIST_SELECTED:
                    queuePresenter.saveQueue(getContext(), item);
                    return true;
            }
            return false;
        }
    };

}
