package kiu.dev.merryweather.ui.widget

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.room.Room
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kiu.dev.merryweather.R
import kiu.dev.merryweather.config.C
import kiu.dev.merryweather.data.local.WidgetId
import kiu.dev.merryweather.data.local.WidgetIdDataBase
import kiu.dev.merryweather.ui.activity.MainActivity
import kiu.dev.merryweather.utils.L
import kiu.dev.merryweather.utils.getTimeNow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class SmallAppWidgetProvider : AppWidgetProvider(),  KoinComponent{

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, t: String) {
            L.d("onUpdate updateAppWidget : $appWidgetId")
            val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
                .let { intent ->
                    // TODO chan PendingIntent flags Issue
                    // Targeting S+ (version 31 and above) requires that one of FLAG_IMMUTABLE
                    // or FLAG_MUTABLE be specified when creating a PendingIntent.
                    PendingIntent.getActivity(context, 0, intent, FLAG_IMMUTABLE)
                }

            val views: RemoteViews = RemoteViews(
                context.packageName,
                R.layout.widget_small
            ).apply {
                setTextViewText(R.id.tv_now, t)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)

        L.d("onEnaled ")
    }

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
//        super.onUpdate(context, appWidgetManager, appWidgetIds)

        // Perform this loop procedure for each App Widget that belongs to this provider
        appWidgetIds?.forEach { appWidgetId ->
            L.d("onUpdate appWidgetId : $appWidgetId")
            context?.let {
                // Save Widget ID

                get<WidgetIdDataBase>()
                    .widgetIdDAO()
                    .insertWidgetId(
                        WidgetId(id = appWidgetId)
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .subscribe()

                // Test setTime
                val str = "YYYYMMdd HH:mm".getTimeNow()

                // Create an Intent to launch Activity
                val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
                    .let { intent ->
                        // TODO chan PendingIntent flags Issue
                        // Targeting S+ (version 31 and above) requires that one of FLAG_IMMUTABLE
                        // or FLAG_MUTABLE be specified when creating a PendingIntent.
                        PendingIntent.getActivity(context, 0, intent, FLAG_IMMUTABLE)
                    }

                val refreshIntent: PendingIntent = Intent(context, SmallAppWidgetProvider::class.java)
                    .setAction("REFRESH_BTN_CLICK")
                    .let { intent ->
                        PendingIntent.getBroadcast(context, 0, intent, FLAG_IMMUTABLE)
                    }

                // Get the layout for the App Widget and attach an on-click listener
                // to the button
                val views: RemoteViews = RemoteViews(
                    it.packageName,
                    R.layout.widget_small
                ).apply {
                    setOnClickPendingIntent(R.id.fl_widget_container, pendingIntent)
                    setOnClickPendingIntent(R.id.iv_refresh, refreshIntent)
                    setTextViewText(R.id.tv_widget_text, str)
                }

                // Tell the AppWidgetManager to perform an update on the current app widget
                appWidgetManager?.updateAppWidget(appWidgetId, views)
            }

        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        intent?.let { i ->
            val action = i.action

            if (action == "REFRESH_BTN_CLICK"){
                L.d("onReceive REFRESH_BTN_CLICK")
            }
        }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        L.d("onDeleted ")

        context?.let {
            L.d("@@@@@@@@@@@@@@@@@@")
            var idList: List<WidgetId>? = null

            /// get Widget ID
            get<WidgetIdDataBase>()
                .widgetIdDAO()
                .getWidgetId()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnError { e ->
                    L.d("e : $e")
                }
                .doOnNext { list ->
                    L.d("idList : $idList")
                    list?.let {
                        deleteWidgetId(appWidgetIds, it)
                    }

                }
                .subscribe()

        }

    }

    private fun deleteWidgetId(ids: IntArray?, list: List<WidgetId>) {
        if (list.isNotEmpty()) {
            ids?.forEach { id ->
                L.d("onDeleted appWidgetIds id : $id")

                get<WidgetIdDataBase>()
                    .widgetIdDAO()
                    .deleteWidgetId(WidgetId(id))
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .doOnError { e ->
                        L.d("e : $e")
                    }
                    .subscribe()
            }
        }
    }

}