package com.android.music.ui.settings;

import android.content.Intent;

public interface AboutView {

    void setVersion(String version);

    void visitSite(Intent intent);

    void visitProfile(Intent intent);

}
