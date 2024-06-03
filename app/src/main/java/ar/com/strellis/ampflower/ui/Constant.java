package ar.com.strellis.ampflower.ui;

import androidx.media3.exoplayer.ExoPlayer;

import java.io.Serializable;
import java.util.ArrayList;

public class Constant implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final int PERMISSIONS_REQUEST = 102;

    public static final String JSON_ARRAY_NAME = "YourRadioApp";
    public static final String CATEGORY_ID = "cid";
    public static final String CATEGORY_NAME = "category_name";
    public static final String CATEGORY_IMAGE = "category_image";
    public static final String RADIO_COUNT = "radio_count";
    public static final String RECENT_RADIO_ID = "radio_id";
    public static final String RECENT_RADIO_NAME = "radio_name";
    public static final String RECENT_RADIO_IMAGE = "radio_image";
    public static final String RECENT_RADIO_URL = "radio_url";
    public static final String RECENT_CATEGORY_NAME = "category_name";

    public static final int DELAY_PROGRESS = 100;
    public static final int DELAY_REFRESH_SHORT = 1000;
    public static final int DELAY_REFRESH_MEDIUM = 2000;

    public static String metadata;
    public static ExoPlayer simpleExoPlayer;
    public static Boolean is_playing = false;
    public static Boolean radio_type = true;
    public static Boolean is_app_open = false;
    public static int position = 0;

    public static final String AD_STATUS_ON = "on";
    public static final String ADMOB = "admob";
    public static final String FAN = "fan";
    public static final String STARTAPP = "startapp";
    public static final String UNITY = "unity";

    //startapp native ad image parameters
    public static final int STARTAPP_IMAGE_XSMALL = 1; //for image size 100px X 100px
    public static final int STARTAPP_IMAGE_SMALL = 2; //for image size 150px X 150px
    public static final int STARTAPP_IMAGE_MEDIUM = 3; //for image size 340px X 340px
    public static final int STARTAPP_IMAGE_LARGE = 4; //for image size 1200px X 628px

    //unity banner ad size
    public static final int UNITY_ADS_BANNER_WIDTH = 320;
    public static final int UNITY_ADS_BANNER_HEIGHT = 50;

    public static final int MAX_NUMBER_OF_NATIVE_AD_DISPLAYED = 10;

    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;
    public static final int THEME_PRIMARY = 2;

}