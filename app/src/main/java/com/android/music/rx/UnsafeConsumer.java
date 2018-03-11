package com.android.music.rx;

import io.reactivex.functions.Consumer;

/**
 * A Consumer which does not throw on error.
 */
public interface UnsafeConsumer<T> extends Consumer<T> {

    @Override
    void accept(T t);
}