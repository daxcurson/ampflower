package ar.com.strellis.ampflower2.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import ar.com.strellis.ampflower2.data.model.SelectableSong;

public class PlaylistPref {

    private static final String PREFS_NAME = "playlist_state";
    private static final String KEY_PLAYLIST = "playlist";
    private static final String KEY_CURRENT_ITEM = "current_item";
    private static final String KEY_CURRENT_POSITION = "current_position";
    private static final int NO_ITEM = -1;

    private final SharedPreferences prefs;
    private final Gson gson;

    public PlaylistPref(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void savePlaylist(List<SelectableSong> playlist, Integer currentItem, Long currentPosition) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_PLAYLIST, gson.toJson(playlist));
        editor.putInt(KEY_CURRENT_ITEM, currentItem != null ? currentItem : NO_ITEM);
        editor.putLong(KEY_CURRENT_POSITION, currentPosition != null ? currentPosition : 0L);
        editor.apply();
    }

    public List<SelectableSong> loadPlaylist() {
        String json = prefs.getString(KEY_PLAYLIST, null);
        if (json == null) return null;
        Type type = new TypeToken<List<SelectableSong>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public int loadCurrentItem() {
        return prefs.getInt(KEY_CURRENT_ITEM, NO_ITEM);
    }

    public long loadCurrentPosition() {
        return prefs.getLong(KEY_CURRENT_POSITION, 0L);
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}