package com.android.music.glide.fetcher;

import com.android.music.http.itunes.ItunesResult;
import com.android.music.model.ArtworkProvider;

import java.io.IOException;

class ItunesFetcher extends BaseRemoteFetcher {

    private static final String TAG = "ItunesFetcher";

    ItunesFetcher(ArtworkProvider artworkProvider) {
        super(artworkProvider);
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    String getUrl() throws IOException {
        retrofitCall = artworkProvider.getItunesArtwork();
        if (retrofitCall == null) return null;
        return ((ItunesResult) retrofitCall.execute().body()).getImageUrl();
    }
}