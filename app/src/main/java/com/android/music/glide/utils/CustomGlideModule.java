package com.android.music.glide.utils;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.module.GlideModule;
import com.android.music.glide.loader.ArtworkModelLoader;
import com.android.music.model.ArtworkProvider;

import java.io.InputStream;

public class CustomGlideModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(ArtworkProvider.class, InputStream.class, new ArtworkModelLoader.Factory());
    }
}