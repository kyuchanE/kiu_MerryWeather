package kiu.dev.merryweather.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import kiu.dev.merryweather.R
import kiu.dev.merryweather.config.C
import kiu.dev.merryweather.data.repository.WeatherRepository
import kiu.dev.merryweather.ui.activity.MainActivity
import kiu.dev.merryweather.ui.viewmodel.WidgetViewModel
import kiu.dev.merryweather.utils.L
import kiu.dev.merryweather.utils.getTimeNow
import kiu.dev.merryweather.utils.getYesterday
import javax.inject.Inject

class SmallAppWidgetProvider: AppWidgetProvider() {
    @Inject lateinit var widgetViewModel: WidgetViewModel

    companion object {
        var mContext: Context? = null
        var mAppWidgetManager: AppWidgetManager? = null

        fun updateAppWidget(
            context: Context? = mContext,
            appWidgetManager: AppWidgetManager? = mAppWidgetManager,
            appWidgetId: Int,
            t: String,
            s: String
        ) {
            L.d("onUpdate updateAppwidget : $appWidgetId")
            val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
                .let { intent ->
                    // TODO chan PendingIntent flags Issue
                    // Targeting S+ (version 31 and above) requires that one of FLAG_IMMUTABLE
                    // or FLAG_MUTABLE be specified when creating a PendingIntent.
                    PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                }

            // Test setTime
            val str = "YYYYMMdd HH:mm".getTimeNow()

            val views: RemoteViews = RemoteViews(
                context?.packageName,
                R.layout.widget_small
            ).apply {
                setTextViewText(R.id.tv_now, t)
                setTextViewText(R.id.tv_widget_text, str)
                setTextViewText(R.id.tv_sky, s)
                setImageViewResource(R.id.iv_refresh, R.drawable.loading)
            }

            appWidgetManager?.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)

        L.d("onEnabled")
    }

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
//        super.onUpdate(context, appWidgetManager, appWidgetIds)

        mContext = context
        mAppWidgetManager = appWidgetManager
        // Perform this loop procedure for each App Widget that belongs to this provider
        appWidgetIds?.forEach { appWidgetId ->
            L.d("onUpdate appWidgetId: $appWidgetId")
            context?.let {
                // Save Widget ID

                // Test setTime
                val str = "YYYYMMdd HH:mm".getTimeNow()

                // get WeatherData
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        intent?.let { intent ->
            val action = intent.action

            if (action == "REFRESH_BTN_CLICK") {
                context?.let { mContext->
                    val id = intent.getIntExtra("WIDGET_ID", 0)
                    L.d("onReceive Widget ID : $id")

                }
            }
        }
    }

    // Weather Api
    private fun getWeatherData(id: Int) {
        var nx: String = C.WeatherData.Location.Seoul["nx"] ?: ""
        var ny: String = C.WeatherData.Location.Seoul["ny"] ?: ""

        var nowDate: String = "YYYYMMdd".getTimeNow()
        val nowHour: String = "HH".getTimeNow()
        val nowTime: String = "HHmm".getTimeNow()
        var baseTime = ""

        kotlin.run {
            C.WeatherData.WEATHER_NOW_GET_DATA_TIME.forEachIndexed { index, item ->
                L.d("reqWeatherNow nowTime : ${nowTime.toInt()}  , item : ${item.toInt()}")
                if (nowHour == "00" || nowHour == "01") {
                    nowDate = "YYYYMMdd".getYesterday()
                    baseTime = "2310"
                    return@run
                } else if (nowTime.toInt() in 200..210){
                    nowDate = "YYYYMMdd".getYesterday()
                    baseTime = "2310"
                    return@run
                } else if (item.toInt() > nowTime.toInt()) {
                    baseTime = C.WeatherData.WEATHER_NOW_GET_DATA_TIME[index-1]
                    L.d("@@@@@@@ baseTime : $baseTime")
                    return@run
                } else {
                    baseTime = nowTime
                }
            }
        }

        widgetViewModel.updateWeatherData(
            mapOf(
                "ServiceKey" to C.WeatherApi.API_KEY,
                "dataType" to "JSON",
                "pageNo" to "1",
                "numOfRows" to "1000",
                "base_date" to nowDate,
                "base_time" to baseTime,
                "nx" to nx,
                "ny" to ny
            )
        )
    }

}