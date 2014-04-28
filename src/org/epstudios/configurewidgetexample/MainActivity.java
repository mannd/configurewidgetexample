package org.epstudios.configurewidgetexample;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

public class MainActivity extends AppWidgetProvider {
	private static final String LOG_TAG = "CW_EX";

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Log.d(LOG_TAG, "onEnabled");
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Log.d(LOG_TAG, "onReceive");
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.d(LOG_TAG, "onUpdate");
		for (int appWidgetId : appWidgetIds) {
			// onUpdate is only called when the widget starts, since update
			// interval is set to 0.
			// Our Configure activity also calls it when its OK button is
			// touched.
			// We'll start the alarm service to update the Service once a sec
			setAlarm(context, appWidgetId, 1000); // 1000 msec = 1 sec
			Log.d(LOG_TAG, "Alarm started");

			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.activity_main);
			// we'll set up a PendingIntent to open the Configure activity
			// when our button is touched.
			Intent intent = new Intent(context, Configure.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);
			views.setOnClickPendingIntent(R.id.update_button, pendingIntent);

			// Now we'll update the time, but all future updates will be by our
			// ClockService
			Format formatter = new SimpleDateFormat(
					"EEEE, MMMM d yyyy\nhh:mm:ss a z", Locale.getDefault());
			String currentTime = formatter.format(new Date());
			views.setTextViewText(R.id.time_label, currentTime);
			// And the label
			String userName = Configure.loadUserName(context, appWidgetId);
			if (userName != null) {
				views.setTextViewText(R.id.user_name_label, userName);
			}
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	public static void setAlarm(Context context, int appWidgetId, int updateRate) {
		PendingIntent newPending = makeControlPendingIntent(context,
				ClockService.UPDATE, appWidgetId);
		AlarmManager alarms = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		if (updateRate >= 0) {
			alarms.setRepeating(AlarmManager.ELAPSED_REALTIME,
					SystemClock.elapsedRealtime(), updateRate, newPending);
		} else {
			// on a negative updateRate stop the alarm
			alarms.cancel(newPending);
			Log.d(LOG_TAG, "Alarm stopped");
		}
	}

	public static PendingIntent makeControlPendingIntent(Context context,
			String command, int appWidgetId) {
		Intent active = new Intent(context, ClockService.class);
		active.setAction(command);
		active.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		// The Uri data is to make the PendingIntent unique
		Uri data = Uri
				.withAppendedPath(
						Uri.parse("configwidget://widget/id/#" + command
								+ appWidgetId), String.valueOf(appWidgetId));
		active.setData(data);
		return (PendingIntent.getService(context, 0, active,
				PendingIntent.FLAG_UPDATE_CURRENT));
	}

	@Override
	public void onDisabled(Context context) {
		Log.d(LOG_TAG, "onDisabled");
		context.stopService(new Intent(context, ClockService.class));
		super.onDisabled(context);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.d(LOG_TAG, "onDeleted");
		// Stop the Alarm when the widget is deleted.
		for (int appWidgetId : appWidgetIds) {
			setAlarm(context, appWidgetId, -1);
		}
		super.onDeleted(context, appWidgetIds);
	}

}
