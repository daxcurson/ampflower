package ar.com.strellis.ampflower;

import ar.com.strellis.ampflower.ui.Constant;

public class Config {

    //load more pagination
    public static final int PAGINATION = 10;

    //radio will stop when receiving a phone call and will resume when the call ends
    public static final boolean RESUME_RADIO_ON_PHONE_CALL = true;

    //number of columns in a row category
    public static final int CATEGORY_COLUMN_COUNT = 4;

    //splash screen duration in millisecond
    public static final int SPLASH_DURATION = 1000;

    //set true if you want to enable RTL (Right To Left) mode, e.g : Arabic Language
    public static final boolean ENABLE_RTL_MODE = false;

    //default theme in the first launch : Constant.THEME_PRIMARY or Constant.THEME_LIGHT or Constant.THEME_DARK
    public static final int DEFAULT_THEME = Constant.THEME_PRIMARY;

    //GDPR EU Consent
    public static final boolean USE_LEGACY_GDPR_EU_CONSENT = true;

}
