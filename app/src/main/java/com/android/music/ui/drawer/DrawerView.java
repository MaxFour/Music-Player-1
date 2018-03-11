package com.android.music.ui.drawer;

import com.android.music.ui.views.PurchaseView;

import java.util.List;

public interface DrawerView extends PurchaseView {

    void setPlaylistItems(List<DrawerChild> drawerChildren);

    void closeDrawer();

    void setDrawerItemSelected(@DrawerParent.Type int type);
    }
