package com.android.music.glide.fetcher;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.android.music.MusicApplication;
import com.android.music.model.ArtworkProvider;
import com.android.music.model.UserSelectedArtwork;
import com.android.music.utils.SettingsManager;
import com.android.music.utils.MPlayerUtils;

import java.io.File;
import java.io.InputStream;

public class MultiFetcher implements DataFetcher<InputStream> {

    private static final String TAG = "MultiFetcher";

    private DataFetcher<InputStream> dataFetcher;

    private ArtworkProvider artworkProvider;

    private boolean allowOfflineDownload = false;

    public MultiFetcher(ArtworkProvider artworkProvider, boolean allowOfflineDownload) {
        this.artworkProvider = artworkProvider;
        this.allowOfflineDownload = allowOfflineDownload;
    }

    private InputStream loadData(DataFetcher<InputStream> dataFetcher, Priority priority) {
        InputStream inputStream;
        try {
            inputStream = dataFetcher.loadData(priority);
        } catch (Exception e) {
            if (dataFetcher != null) {
                dataFetcher.cleanup();
            }
            inputStream = null;
        }
        return inputStream;
    }

    @Override
    public InputStream loadData(Priority priority) throws Exception {

        InputStream inputStream = null;

        //Custom/user selected artwork. Loads from a specific source.
        UserSelectedArtwork userSelectedArtwork = MusicApplication.getInstance().userSelectedArtwork.get(artworkProvider.getArtworkKey());
        if (userSelectedArtwork != null) {
            switch (userSelectedArtwork.type) {
                case ArtworkProvider.Type.MEDIA_STORE:
                    dataFetcher = new MediaStoreFetcher(artworkProvider);
                    break;
                case ArtworkProvider.Type.FOLDER:
                    dataFetcher = new FolderFetcher(artworkProvider, new File(userSelectedArtwork.path));
                    break;
                case ArtworkProvider.Type.TAG:
                    dataFetcher = new TagFetcher(artworkProvider);
                    break;
                case ArtworkProvider.Type.LAST_FM:
                    dataFetcher = new LastFmFetcher(artworkProvider);
                    break;
                case ArtworkProvider.Type.ITUNES:
                    dataFetcher = new ItunesFetcher(artworkProvider);
                    break;
            }
            inputStream = loadData(dataFetcher, priority);
        }

        //No user selected artwork. Check local then remote sources, according to user's preferences.

        //Check the MediaStore
        if (inputStream == null && !SettingsManager.getInstance().ignoreMediaStoreArtwork()) {
            dataFetcher = new MediaStoreFetcher(artworkProvider);
            inputStream = loadData(dataFetcher, priority);
        }

        if (inputStream == null) {
            if (SettingsManager.getInstance().preferEmbeddedArtwork()) {
                //Check tags
                if (!SettingsManager.getInstance().ignoreEmbeddedArtwork()) {
                    dataFetcher = new TagFetcher(artworkProvider);
                    inputStream = loadData(dataFetcher, priority);
                }
                //Check folders
                if (inputStream == null && !SettingsManager.getInstance().ignoreFolderArtwork()) {
                    dataFetcher = new FolderFetcher(artworkProvider, null);
                    inputStream = loadData(dataFetcher, priority);
                }
            } else {
                //Check folders
                if (!SettingsManager.getInstance().ignoreFolderArtwork()) {
                    dataFetcher = new FolderFetcher(artworkProvider, null);
                    inputStream = loadData(dataFetcher, priority);
                }
                //Check tags
                if (inputStream == null && !SettingsManager.getInstance().ignoreEmbeddedArtwork()) {
                    dataFetcher = new TagFetcher(artworkProvider);
                    inputStream = loadData(dataFetcher, priority);
                }
            }
        }

        if (inputStream == null) {
            if (allowOfflineDownload
                    || (SettingsManager.getInstance().canDownloadArtworkAutomatically()
                    && MPlayerUtils.isOnline(true))) {

                //Last FM
                if (SettingsManager.getInstance().preferLastFM()) {
                    dataFetcher = new LastFmFetcher(artworkProvider);
                    inputStream = loadData(dataFetcher, priority);
                } else {
                    dataFetcher = new ItunesFetcher(artworkProvider);
                    inputStream = loadData(dataFetcher, priority);
                }
            }
        }
        return inputStream;
    }

    @Override
    public void cleanup() {
        if (dataFetcher != null) {
            dataFetcher.cleanup();
        }
    }

    @Override
    public void cancel() {
        if (dataFetcher != null) {
            dataFetcher.cancel();
        }
    }

    private String getCustomArtworkSuffix() {
        if (MusicApplication.getInstance().userSelectedArtwork.containsKey(artworkProvider.getArtworkKey())) {
            UserSelectedArtwork userSelectedArtwork = MusicApplication.getInstance().userSelectedArtwork.get(artworkProvider.getArtworkKey());
            return "_" + userSelectedArtwork.type + "_" + (userSelectedArtwork.path == null ? "" : userSelectedArtwork.path.hashCode());
        }
        return "";
    }

    @Override
    public String getId() {
        return artworkProvider.getArtworkKey() + getCustomArtworkSuffix();
    }
}
