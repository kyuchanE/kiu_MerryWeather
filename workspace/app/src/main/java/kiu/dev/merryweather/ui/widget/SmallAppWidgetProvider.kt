package kiu.dev.merryweather.ui.widget

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import kiu.dev.merryweather.R
import kiu.dev.merryweather.ui.MainActivity

class SmallAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
//        super.onUpdate(context, appWidgetManager, appWidgetIds)

        // Perform this loop procedure for each App Widget that belongs to this provider
        appWidgetIds?.forEach { appWidgetId ->

            context?.let {
                // Create an Intent to launch Activity
                val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
                    .let { intent ->
                        // TODO chan PendingIntent flags Issue
                        // Targeting S+ (version 31 and above) requires that one of FLAG_IMMUTABLE
                        // or FLAG_MUTABLE be specified when creating a PendingIntent.
                        PendingIntent.getActivity(context, 0, intent, FLAG_IMMUTABLE)
                    }

                // Get the layout for the App Widget and attach an on-click listener
                // to the button
                val views: RemoteViews = RemoteViews(
                    it.packageName,
                    R.layout.widget_small
                ).apply {
                    setOnClickPendingIntent(R.id.tv_widget_text, pendingIntent)
                }

                // Tell the AppWidgetManager to perform an update on the current app widget
                appWidgetManager?.updateAppWidget(appWidgetId, views)
            }

        }
    }
}