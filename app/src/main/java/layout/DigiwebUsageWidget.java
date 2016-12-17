package layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import ie.fio.dave.digiwebusage.DigiWebAsync;
import ie.fio.dave.digiwebusage.DigiwebShared;
import ie.fio.dave.digiwebusage.DigiwebUsage;
import ie.fio.dave.digiwebusage.R;


public class DigiwebUsageWidget extends AppWidgetProvider
{
  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
  {
    CharSequence widgetText;
    RemoteViews views;

    new DigiWebAsync().execute("");

    for (int appWidgetId : appWidgetIds)
    {
      widgetText = DigiWebAsync.getLastupdate();
      views = new RemoteViews(context.getPackageName(), R.layout.digiweb_usage_widget);
      views.setTextViewText(R.id.appwidget_text, widgetText);

      Intent intent = new Intent(context, DigiwebUsageWidget.class);
      intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

      PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
      views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);
      appWidgetManager.updateAppWidget(appWidgetId, views);
    }
  }

  @Override
  public void onEnabled(Context context)
  {
    SharedPreferences credentials = DigiwebShared.appcontext.getSharedPreferences("digiweb_preferences", DigiwebShared.appcontext.MODE_PRIVATE);
    // Enter relevant functionality for when the first widget is created
    if( credentials.getString("forumusernameeditbox", "").compareTo("")==0 || credentials.getString("broadbandusernameeditbox", "").compareTo("")==0)
    {
      Intent i = new Intent(context, DigiwebUsage.class);
      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(i);
    }
  }

  @Override
  public void onDisabled(Context context)
  {
    // Enter relevant functionality for when the last widget is disabled
  }
}

