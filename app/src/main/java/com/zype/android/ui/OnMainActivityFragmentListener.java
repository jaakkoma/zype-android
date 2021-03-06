package com.zype.android.ui;

public interface OnMainActivityFragmentListener {
    void onLatestVideoClick(String videoId);

    void onFavoriteVideoClick(String videoId, boolean isFavorite);

    void onDownloadedVideoClick(String videoId);

    void onSearch(String text);

    void onPlaylist(String parentId, String parentImg, String parentDescription);

    void onPlaylistWithVideos(String parentId, String parentImg, String parentDescription);

}
