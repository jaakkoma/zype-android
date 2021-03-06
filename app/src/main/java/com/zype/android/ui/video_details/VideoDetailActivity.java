package com.zype.android.ui.video_details;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Subscribe;
import com.zype.android.R;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.events.AuthorizationErrorEvent;
import com.zype.android.core.events.ForbiddenErrorEvent;
import com.zype.android.core.provider.DataHelper;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.Helpers.AutoplayHelper;
import com.zype.android.ui.Helpers.IPlaylistVideos;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.base.BaseVideoActivity;
import com.zype.android.ui.player.PlayerFragment;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.DialogHelper;
import com.zype.android.utils.ListUtils;
import com.zype.android.utils.Logger;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.events.ErrorEvent;
import com.zype.android.webapi.events.download.DownloadAudioEvent;
import com.zype.android.webapi.events.download.DownloadVideoEvent;
import com.zype.android.webapi.events.favorite.FavoriteEvent;
import com.zype.android.webapi.events.favorite.UnfavoriteEvent;
import com.zype.android.webapi.events.player.PlayerAudioEvent;
import com.zype.android.webapi.events.player.PlayerVideoEvent;
import com.zype.android.webapi.events.zobject.ZObjectEvent;
import com.zype.android.webapi.model.consumers.ConsumerFavoriteVideoData;
import com.zype.android.webapi.model.player.Advertising;
import com.zype.android.webapi.model.player.AdvertisingSchedule;
import com.zype.android.webapi.model.player.Analytics;
import com.zype.android.webapi.model.player.AnalyticsDimensions;
import com.zype.android.webapi.model.player.File;
import com.zype.android.webapi.model.video.Thumbnail;
import com.zype.android.webapi.model.video.VideoData;
import com.zype.android.webapi.model.zobjects.ZobjectData;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class VideoDetailActivity extends BaseVideoActivity implements IPlaylistVideos {
    public static final String TAG = VideoDetailActivity.class.getSimpleName();

    private VideoDetailPager mViewPager;
    private TabLayout mTabLayout;

    private FrameLayout layoutImage;
    private ImageView imageVideo;

    public static void startActivity(Activity activity, String videoId, String playlistId) {
        Intent intent = new Intent(activity, VideoDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BundleConstants.VIDEO_ID, videoId);
        bundle.putString(BundleConstants.PLAYLIST_ID, playlistId);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.d("onCreate()");
        super.onCreate(savedInstanceState);
        initUI();
        updateDownloadUrls();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Logger.d("onNewIntent()");
        super.onNewIntent(intent);
        initUI();
    }

    @Override
    protected Class<?> getActivityClass() {
        return VideoDetailActivity.class;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_detail;
    }

    @Override
    public void onFullscreenChanged() {
        int orientation = getResources().getConfiguration().orientation;
        boolean isFullscreen = false;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isFullscreen = true;
        }
        if (isFullscreen) {
            mTabLayout.setVisibility(View.GONE);
            mViewPager.setVisibility(View.GONE);
            mActionBar.hide();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            findViewById(R.id.layoutVideo).setLayoutParams(params);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
        else {
            mTabLayout.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.VISIBLE);
            mActionBar.show();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.height = (int) getResources().getDimension(R.dimen.episode_video_height);
            findViewById(R.id.layoutVideo).setLayoutParams(params);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }

    @Override
    protected String getActivityName() {
        return TAG;
    }

    // //////////
    // UI
    //
    private void initUI() {

        getSupportActionBar().setTitle(VideoHelper.getFullData(getContentResolver(), mVideoId).getTitle());

        VideoDetailPagerAdapter videoDetailPagerAdapter = new VideoDetailPagerAdapter(this, getSupportFragmentManager(), mVideoId);
        mViewPager = (VideoDetailPager) findViewById(R.id.pager);
        mViewPager.setAdapter(videoDetailPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        layoutImage = (FrameLayout) findViewById(R.id.layoutImage);
        imageVideo = (ImageView) findViewById(R.id.imageVideo);
        imageVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogHelper.showLoginAlert(VideoDetailActivity.this);
            }
        });
    }

    private void showVideoThumbnail() {
        VideoData videoData = VideoHelper.getFullData(getContentResolver(), mVideoId);
        List<Thumbnail> thumbnails = videoData.getThumbnails();
        layoutImage.setVisibility(View.VISIBLE);
        if (thumbnails.size() > 0) {
            UiUtils.loadImage(this, thumbnails.get(1).getUrl(), R.drawable.placeholder_video, imageVideo, getVideoProgressBar());
        }
        else {
            imageVideo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.placeholder_video));
        }
    }

    private void hideVideoThumbnail() {
        layoutImage.setVisibility(View.GONE);
    }

    // //////////
    // Actions
    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BundleConstants.REQ_LOGIN:
                if (resultCode == RESULT_OK) {
                    hideVideoThumbnail();
                    VideoDetailActivity.startActivity(this, mVideoId, playlistId);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //
    // 'IPlaylistVideos' implementation
    //
    @Override
    public void onNext() {
        hideVideoLayout();
        showProgress();
        autoplay = true;
        AutoplayHelper.playNextVideo(this, mVideoId, playlistId);
    }

    @Override
    public void onPrevious() {
        hideVideoLayout();
        showProgress();
        autoplay = true;
        AutoplayHelper.playPreviousVideo(this, mVideoId, playlistId);
    }

    // //////////
    // Data
    //
    private void updateDownloadUrls() {
        VideoData videoData = VideoHelper.getFullData(getContentResolver(), mVideoId);
        if (!videoData.isOnAir()) {
            if (ZypeConfiguration.isDownloadsEnabled(this)
                    && (ZypeConfiguration.isDownloadsForGuestsEnabled(this) || SettingsProvider.getInstance().isLoggedIn())) {
                getDownloadUrls(mVideoId);
            }
        }
    }

    // //////////
    // Subscribe
    //
    @Subscribe
    public void handleFavoriteEvent(FavoriteEvent event) {
        Logger.d("FavoriteEvent");
        List<ConsumerFavoriteVideoData> data = new ArrayList<>();
        data.add(event.getEventData().getModelData().getResponse());
        DataHelper.insertFavorites(getContentResolver(), data);
        DataHelper.setFavoriteVideo(getContentResolver(), event.getEventData().getModelData().getResponse().getVideoId(), true);
    }

    @Subscribe
    public void handleUnfavoriteEvent(UnfavoriteEvent event) {
        Logger.d("UnfavoriteEvent");
        DataHelper.setFavoriteVideo(getContentResolver(), event.getVideoId(), false);
        DataHelper.deleteFavorite(getContentResolver(), event.getVideoId());
    }

    @Subscribe
    public void handleDownloadVideo(DownloadVideoEvent event) {
        Logger.d("handleDownloadVideo");
        File file = ListUtils.getStringWith(event.getEventData().getModelData().getResponse().getBody().getFiles(), "mp4");
        String url;
        if (file != null) {
            url = file.getUrl();
            String fileId = event.mFileId;
            DataHelper.saveVideoUrl(getContentResolver(), fileId, url);

            initUI();
        }
        else {
            Logger.e("Server response must contains \"mp\" but server has returned:" + Logger.getObjectDump(event.getEventData().getModelData().getResponse().getBody().getFiles()));
        }
    }

    @Subscribe
    public void handleDownloadAudio(DownloadAudioEvent event) {
        File file = ListUtils.getStringWith(event.getEventData().getModelData().getResponse().getBody().getFiles(), "m4a");
        String url;
        if (file != null) {
            url = file.getUrl();
            String fileId = event.mFileId;
            DataHelper.saveAudioUrl(getContentResolver(), fileId, url);

            initUI();
        }
        else {
            Logger.e("Server response must contains \"m4a\" but server has returned:" + Logger.getObjectDump(event.getEventData().getModelData().getResponse().getBody().getFiles()));
        }
    }

    @Subscribe
    public void handleError(ErrorEvent err) {
        Logger.e("handleError");
        if (err instanceof ForbiddenErrorEvent) {
            if (err.getEventData() == WebApiManager.Request.PLAYER_VIDEO) {
                hideProgress();
                if (VideoHelper.getFullData(getContentResolver(), mVideoId).isSubscriptionRequired()) {
                    if (ZypeConfiguration.isNativeSubscriptionEnabled(this)) {
                        NavigationHelper.getInstance(this).switchToSubscriptionScreen(this);
                    }
                    else {
                        if (SettingsProvider.getInstance().isLoggedIn()) {
                            DialogHelper.showSubscriptionAlertIssue(this);
                        }
                        else {
                            showVideoThumbnail();
                            DialogHelper.showLoginAlert(this);
                        }
                    }
                }
            }
        }
        else if (err instanceof AuthorizationErrorEvent) {
            // TODO: Handle 401 error
        }
        else {
            if (err.getEventData() != WebApiManager.Request.UN_FAVORITE) {
                UiUtils.showErrorSnackbar(findViewById(R.id.root_view), err.getErrMessage());
            }
        }
    }

    @Subscribe
    public void handleZObject(ZObjectEvent event) {
        List<ZobjectData> list = event.getEventData().getModelData().getResponse();
        if (list != null && list.size() > 0) {
            DataHelper.saveGuests(getContentResolver(), mVideoId, list);
        }
    }

    @Subscribe
    public void handleVideoPlayer(PlayerVideoEvent event) {
        Logger.d("handlePlayer");
        String url = event.getEventData().getModelData().getResponse().getBody().getFiles().get(0).getUrl();
        DataHelper.saveVideoPlayerLink(getContentResolver(), mVideoId, url);
        Advertising advertising = event.getEventData().getModelData().getResponse().getBody().getAdvertising();
        Analytics analytics = event.getEventData().getModelData().getResponse().getBody().getAnalytics();

        if (advertising != null) {
            List<AdvertisingSchedule> schedule = advertising.getSchedule();
            DataHelper.updateAdSchedule(getContentResolver(), mVideoId, schedule);
            // TODO: Ad tags saved in separate table by 'updateAdSchedule'. Probably we don't need to keep the tag
            // in the 'Video' table. But we do this now because the tags are the same for all ad cue points
            if (schedule != null && !schedule.isEmpty()) {
                String adTag = advertising.getSchedule().get(0).getTag();
                DataHelper.saveAdVideoTag(getContentResolver(), mVideoId, adTag);
            }
        }

        if (analytics != null) {
            String beacon = analytics.getBeacon();
            AnalyticsDimensions dimensions = analytics.getDimensions();

            if (beacon != null && dimensions != null) {
                DataHelper.updateAnalytics(getContentResolver(), beacon, dimensions);
            }
        }

        mType = PlayerFragment.TYPE_VIDEO_WEB;
        changeFragment(isChromecastConntected());
        hideProgress();
    }

    @Subscribe
    public void handleAudioPlayer(PlayerAudioEvent event) {
        Logger.d("handlePlayer");
        String url = event.getEventData().getModelData().getResponse().getBody().getFiles().get(0).getUrl();
        DataHelper.saveAudioPlayerLink(getContentResolver(), mVideoId, url);
        mType = PlayerFragment.TYPE_AUDIO_WEB;
        changeFragment(isChromecastConntected());
    }
}
