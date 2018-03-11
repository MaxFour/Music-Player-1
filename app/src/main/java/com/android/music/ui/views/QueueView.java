package com.android.music.ui.views;

import com.android.music.model.Song;
import com.android.music.tagger.TaggerDialog;
import com.android.music.ui.dialog.DeleteDialog;
import com.android.music.ui.modelviews.SongView;
import com.simplecityapps.recycler_adapter.model.ViewModel;

import java.util.List;

public interface QueueView {

    void loadData(List<ViewModel> items, int position);

    void updateQueuePosition(int position, boolean fromUser);

    void showToast(String message, int duration);

    void startDrag(SongView.ViewHolder holder);

    void showTaggerDialog(TaggerDialog taggerDialog);

    void showDeleteDialog(DeleteDialog deleteDialog);

    void removeFromQueue(int position);

    void removeFromQueue(List<Song> songs);

    void moveQueueItem(int from, int to);

    void showUpgradeDialog();
}
