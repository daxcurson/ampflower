package ar.com.strellis.ampflower;

import ar.com.strellis.ampflower.ui.Constant;

public class Config {

    //put your admin panel url
    public static final String ADMIN_PANEL_URL = "https://solodroid.net/demo/your_radio_app";

    //put your api key which obtained from admin panel
    public static final String API_KEY = "cda11lHY0ZafN2nrti4U5QAKMDhTw7Czm1xoSsyVLduvRegkqE";

    //load more pagination
    public static final int PAGINATION = 10;

    //radio will stop when receiving a phone call and will resume when the call ends
    public static final boolean RESUME_RADIO_ON_PHONE_CALL = true;

    //number of columns in a row category
    public static final int CATEGORY_COLUMN_COUNT = 2;

    public static final boolean DISPLAY_RADIO_COUNT_ON_CATEGORY_LIST = false;

    //splash screen duration in millisecond
    public static final int SPLASH_DURATION = 1000;

    //set true if you want to enable RTL (Right To Left) mode, e.g : Arabic Language
    public static final boolean ENABLE_RTL_MODE = false;

    //default theme in the first launch : Constant.THEME_PRIMARY or Constant.THEME_LIGHT or Constant.THEME_DARK
    public static final int DEFAULT_THEME = Constant.THEME_PRIMARY;

    //GDPR EU Consent
    public static final boolean USE_LEGACY_GDPR_EU_CONSENT = true;

    //push notification handle when open url
    public static final boolean OPEN_NOTIFICATION_LINK_IN_EXTERNAL_BROWSER = true;

    //social menu open url
    public static final boolean OPEN_SOCIAL_MENU_IN_EXTERNAL_BROWSER = true;

}
