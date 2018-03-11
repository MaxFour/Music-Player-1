package com.android.music.model;

import com.android.music.MusicApplication;
import com.android.music.interfaces.FileType;
import com.android.music.utils.FileHelper;
import com.android.music.utils.StringUtils;

public class FileObject extends BaseFileObject {

    public String extension;

    public TagInfo tagInfo;

    private long duration = 0;

    public FileObject() {
        this.fileType = FileType.FILE;
    }

    public String getTimeString() {
        if (duration == 0) {
            duration = FileHelper.getDuration(MusicApplication.getInstance(), this);
        }
        return StringUtils.makeTimeString(MusicApplication.getInstance(), duration / 1000);
    }

    @Override
    public String toString() {
        return "FileObject{" +
                "extension='" + extension + '\'' +
                ", size='" + size + '\'' +
                "} " + super.toString();
    }
}
