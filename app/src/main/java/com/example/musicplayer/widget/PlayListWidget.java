package com.example.musicplayer.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.musicplayer.R;
import com.example.musicplayer.data.model.Playlist;
import com.example.musicplayer.ui.play.PlayActivity;
import com.example.musicplayer.utils.Constants;
import com.example.musicplayer.utils.SPUtils;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link PlayListWidgetConfigureActivity PlayListWidgetConfigureActivity}
 */
public class PlayListWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Playlist playlist = SPUtils.Companion.getPLaylistInfo(context, appWidgetId);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.play_list_widget);
        views.setTextViewText(R.id.appwidget_text, playlist.getName());

        //request for play
        Intent wordIntent = new Intent(context, PlayActivity.class);
        wordIntent.putExtra(Constants.Type.Type, Constants.Type.TypePlaylist);
        wordIntent.putExtra(Constants.Songs.ID, playlist.getId());
        wordIntent.putExtra(Constants.Songs.Name, playlist.getName());
        wordIntent.putExtra("playlist", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, wordIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.playlist_widget, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

