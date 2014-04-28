package org.epstudios.configurewidgetexample;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

public class ClockService extends Service {
	public static final String UPDATE = "update";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Context context = getApplicationContext();
		int appWidgetId = intent.getExtras().getInt(
				AppWidgetManager.EXTRA_APPWIDGET_ID);
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.activity_main);
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		Format formatter = new SimpleDateFormat(
				"EEEE, MMMM d yyyy\nhh:mm:ss a z", Locale.getDefault());
		String currentTime = formatter.format(new Date());
		views.setTextViewText(R.id.time_label, currentTime);

		// Try the app first with the code below commented out.
		// Remove comments below to fix widget

		// Configure.loadUserName(context, appWidgetId);
		// String userName = Configure.loadUserName(context, appWidgetId);
		// if (userName != null) {
		// views.setTextViewText(R.id.user_name_label, userName);
		// }
		// Intent configureIntent = new Intent(context, Configure.class);
		// configureIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
		// appWidgetId);
		// PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
		// configureIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		// views.setOnClickPendingIntent(R.id.update_button, pendingIntent);

		appWidgetManager.updateAppWidget(appWidgetId, views);
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
