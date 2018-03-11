package com.android.music.utils;

import android.support.annotation.NonNull;

import com.annimon.stream.Stream;
import com.android.music.R;
import com.android.music.MusicApplication;
import com.android.music.ui.modelviews.SelectableViewModel;
import com.android.music.ui.views.ContextualToolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextualToolbarHelper<T> {

    public interface Callback {
        void notifyItemChanged(int position, SelectableViewModel viewModel);
        void notifyDatasetChanged();
    }

    private final Map<SelectableViewModel, T> map = new HashMap<>(0);

    @NonNull
    private final ContextualToolbar contextualToolbar;
    @NonNull
    private final Callback callback;

    private boolean isActive;
    private boolean canChangeTitle = true;

    public ContextualToolbarHelper(@NonNull ContextualToolbar contextualToolbar, @NonNull Callback callback) {
        this.contextualToolbar = contextualToolbar;
        this.callback = callback;
    }

    public void setCanChangeTitle(boolean canChangeTitle) {
        this.canChangeTitle = canChangeTitle;
    }

    public void start() {
        contextualToolbar.show();
        contextualToolbar.setNavigationOnClickListener(v -> finish());
        isActive = true;
    }

    public void finish() {
        if (!map.isEmpty()) {
            Stream.of(map.keySet()).forEach(viewModel -> viewModel.setSelected(false));
            callback.notifyDatasetChanged();
        }

        map.clear();

        contextualToolbar.hide();
        contextualToolbar.setNavigationOnClickListener(null);
        isActive = false;
    }

    private void updateCount() {
        if (canChangeTitle) {
            contextualToolbar.setTitle(MusicApplication.getInstance().getString(R.string.action_mode_selection_count, map.size()));
        }
    }

    private void addOrRemoveItem(SelectableViewModel viewModel, T items) {
        if (map.keySet().contains(viewModel)) {
            map.remove(viewModel);
            viewModel.setSelected(false);
        } else {
            map.put(viewModel, items);
            viewModel.setSelected(true);
        }

        updateCount();

        if (map.isEmpty()) {
            finish();
        }
    }

    public boolean handleClick(int position, SelectableViewModel selectableViewModel, T item) {
        if (isActive) {
            addOrRemoveItem(selectableViewModel, item);
            callback.notifyItemChanged(position, selectableViewModel);
            return true;
        }
        return false;
    }

    public boolean handleLongClick(int position, SelectableViewModel selectableViewModel, T item) {
        if (!isActive) {
            start();
            addOrRemoveItem(selectableViewModel, item);
            callback.notifyItemChanged(position, selectableViewModel);
            return true;
        }
        return false;
    }

    public List<T> getItems() {
        return new ArrayList<>(map.values());
    }

}
