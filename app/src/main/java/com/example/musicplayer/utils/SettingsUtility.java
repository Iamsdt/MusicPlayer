package com.example.musicplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

public final class SettingsUtility {

    private static final String SHARED_PREFERENCES_FILE_NAME = "configs";
    private static final String SONG_DIRS_KEY = "song_dirs";
    private static final String SONG_SORT_ORDER_KEY = "song_sort_order";
    private static final String ALBUM_SORT_ORDER_KEY = "album_sort_order";
    private static final String ALBUM_SONG_SORT_ORDER_KEY = "album_song_sort_order";
    private static final String ARTIST_SORT_ORDER_KEY = "album_song_sort_order";
    private static final String LAST_OPTION_SELECTED_KEY = "last_option_selected";
    private static final String LIGHT_THEME_KEY = "light_theme";
    private static final String CURRENT_THEME_KEY = "current_theme";
    private static final String CURRENT_COLOR_ACCENT_KEY = "current_accent_color";

    private static SharedPreferences sPreferences;
    private static SettingsUtility settingsUtility;

    private SettingsUtility(Context context) {
        sPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static SettingsUtility getInstance(Context context) {
        if (settingsUtility == null)
            settingsUtility = new SettingsUtility(context);
        return settingsUtility;
    }

    public ArrayList<Uri> getSongDirs() {
        ArrayList<Uri> songDirs = new ArrayList<>();
        String readValue;
        SharedPreferences prefs = sPreferences;
        readValue = prefs.getString(SONG_DIRS_KEY, "null");
        if (readValue.equals("null") || readValue.equals("")) {
            songDirs.add(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            setSongDirs(songDirs);
        } else {
            String[] songs = readValue.split("<,>");
            for (String song : songs) {
                songDirs.add(Uri.parse(song));
            }
        }
        return songDirs;
    }

    private void setSongDirs(ArrayList<Uri> songDirs) {
        SharedPreferences.Editor editor = sPreferences.edit();
        StringBuilder dirs = new StringBuilder();
        for (int i = 0; i < songDirs.size(); i++) {
            if (i > 0) {
                dirs.append("<,>").append(songDirs.get(i).toString());
            } else {
                dirs.append(songDirs.get(i).toString());
            }
        }
        editor.putString(SONG_DIRS_KEY, dirs.toString());
        editor.apply();
    }

    public int getStartPageIndexSelected() {
        return sPreferences.getInt(LAST_OPTION_SELECTED_KEY, 0);
    }

    public void setStartPageIndexSelected(int value) {
        setPreference(LAST_OPTION_SELECTED_KEY, value);
    }

    public String getSongSortOrder() {
        return sPreferences.getString(SONG_SORT_ORDER_KEY, SortModes.SongModes.SONG_A_Z);
    }

    public void setSongSortOrder(String value) {
        setPreference(SONG_SORT_ORDER_KEY, value);
    }

    public String getAlbumSortOrder() {
        return sPreferences.getString(ALBUM_SORT_ORDER_KEY, SortModes.AlbumModes.ALBUM_SONGS_LIST);
    }

    public void setAlbumSortOrder(String value) {
        setPreference(ALBUM_SORT_ORDER_KEY, value);
    }

    public String getCurrentTheme() {
        return sPreferences.getString(CURRENT_THEME_KEY, PlayerConstants.LIGHT_THEME);
    }

    public void setCurrentTheme(String value) {
        setPreference(CURRENT_THEME_KEY, value);
    }

    public String getAlbumSongSortOrder() {
        return sPreferences.getString(ALBUM_SONG_SORT_ORDER_KEY, SortModes.SongModes.SONG_TRACK);
    }

    public void setAlbumSongSortOrder(String value) {
        setPreference(ALBUM_SONG_SORT_ORDER_KEY, value);
    }

    public int getCurrentColorAccent() {
        return sPreferences.getInt(CURRENT_COLOR_ACCENT_KEY, Color.parseColor("#7874D1"));
    }

    public void setCurrentColorAccent(int value) {
        setPreference(CURRENT_COLOR_ACCENT_KEY, value);
    }

    public String getCurrentSongSelected() {
        return sPreferences.getString(PlayerConstants.SONG_KEY, PlayerConstants.NO_DATA);
    }

    public void setCurrentSongSelected(String value) {
        setPreference(PlayerConstants.SONG_KEY, value);
    }

    public String getArtistSortOrder() {
        return sPreferences.getString(ARTIST_SORT_ORDER_KEY, SortModes.ArtistModes.ARTIST_Album_LIST);
    }

    public void setArtistSortOrder(String value) {
        setPreference(ARTIST_SORT_ORDER_KEY, value);
    }

    private void setPreference(String key, String value) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void setPreference(String key, int value) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private void setPreference(String key, boolean state) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putBoolean(key, state);
        editor.apply();
    }
}
