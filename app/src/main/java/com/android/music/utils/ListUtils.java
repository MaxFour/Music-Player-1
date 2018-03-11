package com.android.music.utils;

import java.util.List;

import static java.util.Collections.emptyList;

public final class ListUtils {

    public static <T> List<T> emptyIfNull(List<T> data) {
        return data == null ? emptyList() : data;
    }

    private ListUtils() {
        throw new IllegalStateException("no instances");
    }

}
