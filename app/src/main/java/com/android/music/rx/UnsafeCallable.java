package com.android.music.rx;

import java.util.concurrent.Callable;

/**
 * A callable which does not throw on error.
 */
public interface UnsafeCallable<T> extends Callable<T> {

    @Override
    T call();
}