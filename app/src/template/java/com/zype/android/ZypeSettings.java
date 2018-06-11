package com.zype.android;

/**
 * Created by Evgeny Cherkasov on 18.03.2017.
 */

public class ZypeSettings {
    // Zype app key
    public static final String APP_KEY = "T9-gFAuN0l9yuxyR3SHiLSOlZql5E8w2zWQW2o16kjDHgMSGxEI7ePdjJUDIqNc_";
    // OAuth credentials
    public static final String CLIENT_ID = "c95d8435c66d4d43187ea17d7b04622d7104f05c7e2f44be2a4f4944b95e0e33";
    public static final String CLIENT_SECRET = "2bca4c00e38553605be074ff30e87c2b165a9779f7a9d49813d3cb56775fd679";
    // Playlist
    public static final String ROOT_PLAYLIST_ID = "5a736db6d6a6de1227000528";

    // Google Analytics Tracking Id
    // TODO: To use Google Analytics provide your GA tracking id
    public static final String GA_TRACKING_ID = "";

    // Social
    // TODO: Add your real social network ids and web url
    public static final String FACEBOOK_ID = "";
    public static final String INSTAGRAM_ID = "";
    public static final String TWITTER_ID = "";
    public static final String WEB_URL = "";

    // Monetization
    public static final boolean NATIVE_SUBSCRIPTION_ENABLED = false;
    public static final boolean NATIVE_TO_UNIVERSAL_SUBSCRIPTION_ENABLED = false;
    public static final boolean SUBSCRIBE_TO_WATCH_AD_FREE_ENABLED = false;
    public static final boolean UNIVERSAL_SUBSCRIPTION_ENABLED = true;
    public static final boolean UNIVERSAL_TVOD = true;

    // Features
    public static final boolean BACKGROUND_PLAYBACK_ENABLED = true;
    public static final boolean AUTOPLAY = true;
    public static final boolean DOWNLOADS_ENABLED = true;
    public static final boolean DOWNLOADS_ENABLED_FOR_GUESTS = false;
    // TODO: 'Share video' is not currently supported by the app builder. Update these flag if needed.
    public static final boolean SHARE_VIDEO_ENABLED = false;
//    public static final boolean SHARE_VIDEO_ENABLED = Boolean.valueOf("<SHARE_VIDEO_ENABLED>");

    public static final boolean DEVICE_LINKING = false;
    public static final String DEVICE_LINKING_URL = "http://hoi.tv";

    /**
     * Theme of the app.
     * Use following constants for the theme:
     * @see ZypeConfiguration#THEME_LIGHT
     * @see ZypeConfiguration#THEME_DARK
     */
    public static final String THEME = "light";

}

