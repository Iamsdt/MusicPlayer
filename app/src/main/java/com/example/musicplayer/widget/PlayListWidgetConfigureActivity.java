package com.example.musicplayer.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.musicplayer.R;
import com.example.musicplayer.data.model.Playlist;
import com.example.musicplayer.data.repo.PlaylistRepository;
import com.example.musicplayer.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * The configuration screen for the {@link PlayListWidget PlayListWidget} AppWidget.
 */
public class PlayListWidgetConfigureActivity extends Activity implements View.OnClickListener {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private void requestUpdate(Context context) {
        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        PlayListWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    public PlayListWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.play_list_widget_configure);
        findViewById(R.id.add_button).setOnClickListener(this);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
    }

    @Override
    public void onClick(View v) {

        Context context = PlayListWidgetConfigureActivity.this;

        //todo replace empty list
        List<Playlist> mList = PlaylistRepository.Companion.getInstance(this).getPlayLists();

        if (mList.isEmpty()) {
            Toasty.warning(context, "No playlist, please create new and add the widget again", Toast.LENGTH_LONG).show();
            return;
        }

        ArrayList<String> list = new ArrayList<>();

        for (Playlist p : mList) {
            list.add(p.getName());
        }

        CharSequence[] cs = list.toArray(new CharSequence[list.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select a playlist");
        builder.setItems(cs, (dialog, position) -> {
            Playlist play = mList.get(position);
            SPUtils.Companion.savePlaylist(play, context, mAppWidgetId);
            requestUpdate(context);
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

