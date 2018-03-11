package com.android.music.ui.dialog;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.afollestad.materialdialogs.MaterialDialog;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.android.music.R;
import com.android.music.model.CategoryItem;
import com.android.music.ui.fragments.LibraryController;
import com.android.music.ui.modelviews.TabViewModel;
import com.android.music.ui.recyclerview.ItemTouchHelperCallback;
import com.android.music.utils.MPlayerUtils;
import com.simplecityapps.recycler_adapter.adapter.ViewModelAdapter;
import com.simplecityapps.recycler_adapter.model.ViewModel;

import java.util.List;

public class TabChooserDialog {

    private TabChooserDialog() {
        //no instance
    }

    public static MaterialDialog getDialog(Activity activity) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        ViewModelAdapter adapter = new ViewModelAdapter();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelperCallback(
                        adapter::moveItem,
                        (fromPosition, toPosition) -> {
                        },
                        () -> {
                        }
                ));

        TabViewModel.Listener listener = new TabViewModel.Listener() {
            @Override
            public void onStartDrag(TabViewModel.ViewHolder holder) {
                itemTouchHelper.startDrag(holder);
            }

            @Override
            public void onFolderChecked(TabViewModel tabViewModel, TabViewModel.ViewHolder viewHolder) {
                if (!MPlayerUtils.isUpgraded()) {
                    viewHolder.checkBox.setChecked(false);
                    tabViewModel.categoryItem.isChecked = false;
                    UpgradeDialog.getUpgradeDialog(activity).show();
                }
            }
        };

        List<ViewModel> items = Stream.of(CategoryItem.getCategoryItems(sharedPreferences))
                .map(categoryItem -> {
                    TabViewModel tabViewModel = new TabViewModel(categoryItem);
                    tabViewModel.setListener(listener);
                    return tabViewModel;
                })
                .collect(Collectors.toList());
        adapter.setItems(items);

        RecyclerView recyclerView = new RecyclerView(activity);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);

        itemTouchHelper.attachToRecyclerView(recyclerView);

        return new MaterialDialog.Builder(activity)
                .title(R.string.pref_title_choose_tabs)
                .customView(recyclerView, false)
                .positiveText(R.string.button_done)
                .onPositive((dialog, which) -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Stream.of(adapter.items)
                            .indexed()
                            .forEach(viewModelIntPair -> {
                                ((TabViewModel) viewModelIntPair.getSecond()).categoryItem.sortOrder = viewModelIntPair.getFirst();
                                ((TabViewModel) viewModelIntPair.getSecond()).categoryItem.savePrefs(editor);
                            });
                    LocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent(LibraryController.EVENT_TABS_CHANGED));
                })
                .negativeText(R.string.close)
                .build();
    }
}
