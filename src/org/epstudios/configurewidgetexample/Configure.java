package org.epstudios.configurewidgetexample;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Configure extends Activity {

	private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private static final String PREFS_NAME = "org.epstudios.configurewidgetexample.Configure";
	public static final String USER_NAME_KEY = "user_name";
	private EditText labelEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the appWidgetId of the calling appWidget
		// stored in the extras bundle.
		Intent launchIntent = getIntent();
		Bundle extras = launchIntent.getExtras();
		appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);

		setContentView(R.layout.configure);
		final Context context = Configure.this;

		// We'll have a User Name that can be configured in our widget
		labelEditText = (EditText) findViewById(R.id.user_name);
		String userName = loadUserName(context, appWidgetId);
		if (userName != null) {
			labelEditText.setText(userName);
		}
		// We have one button to update the User Name
		Button ok = (Button) findViewById(R.id.ok_button);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveUserName(context, appWidgetId, labelEditText.getText()
						.toString());
				// we need to broadcast an APPWIDGET_UPDATE to our appWidget
				AppWidgetManager appWidgetManager = AppWidgetManager
						.getInstance(context);
				ComponentName thisAppWidget = new ComponentName(context
						.getPackageName(), MainActivity.class.getName());
				Intent updateIntent = new Intent(context, MainActivity.class);
				int[] appWidgetIds = appWidgetManager
						.getAppWidgetIds(thisAppWidget);
				updateIntent
						.setAction("android.appwidget.action.APPWIDGET_UPDATE");
				updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
						appWidgetIds);
				context.sendBroadcast(updateIntent);
				finish();
			}
		});

	}

	static void saveUserName(Context context, int appWidgetId, String userName) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				PREFS_NAME, 0).edit();
		prefs.putString(USER_NAME_KEY + appWidgetId, userName);
		prefs.commit();
	}

	static String loadUserName(Context context, int appWidgetId) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		String userName = prefs.getString(USER_NAME_KEY + appWidgetId,
				context.getString(R.string.user_name_label));
		return userName;
	}

}
