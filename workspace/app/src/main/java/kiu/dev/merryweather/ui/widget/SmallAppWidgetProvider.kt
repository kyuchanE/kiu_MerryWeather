package kiu.dev.merryweather.ui.widget

//import android.app.PendingIntent
//import android.appwidget.AppWidgetManager
//import android.appwidget.AppWidgetProvider
//import android.content.Context
//import android.content.Intent
//import android.widget.RemoteViews
//import dagger.hilt.android.AndroidEntryPoint
//import kiu.dev.merryweather.R
//import kiu.dev.merryweather.config.C
//import kiu.dev.merryweather.data.local.widget.WidgetId
//import dev.kyu.main.ui.MainActivity
//import kiu.dev.merryweather.viewmodel.WidgetViewModel
//import dev.kyu.ui.utils.L
//import kiu.dev.merryweather.utils.getTimeNow
//import kiu.dev.merryweather.utils.getYesterday
//import javax.inject.Inject

//@AndroidEntryPoint
//class SmallAppWidgetProvider: AppWidgetProvider() {
//    @Inject lateinit var widgetViewModel: WidgetViewModel
//
//    companion object {
//        var mContext: Context? = null
//        var mAppWidgetManager: AppWidgetManager? = null
//
//        fun updateAppWidget(
//            context: Context? = mContext,
//            appWidgetManager: AppWidgetManager? = mAppWidgetManager,
//            appWidgetId: Int,
//            tmp: String = "",
//            sky: String = "",
//            pty: String = "",
//            air: String = ""
//        ) {
//            // Create an Intent to launch Activity
//            val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
//                .let { intent ->
//                    // Targeting S+ (version 31 and above) requires that one of FLAG_IMMUTABLE
//                    // or FLAG_MUTABLE be specified when creating a PendingIntent.
//                    PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//                }
//
//            val refreshIntent: PendingIntent = Intent(context, SmallAppWidgetProvider::class.java)
//                .setAction("REFRESH_BTN_CLICK")
//                .putExtra("WIDGET_ID", appWidgetId)
//                .let { intent ->
//                    PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//                }
//
//            // Test setTime
//            val str = "yyyyMMdd HH:mm".getTimeNow()
//
//            // TODO chan 날씨 아이콘 로직 수정 필요
//            var skyId = R.drawable.loading
//            skyId = when(sky) {
//                "1" -> { R.drawable.icon_sunny }
//                "2", "3" -> { R.drawable.icon_cloudy_a_lot }
//                "4" -> { R.drawable.icon_cloudy }
//                else -> { R.drawable.loading }
//            }
//
//            skyId = when(pty) {
//                "1" -> { R.drawable.icon_rainny }
//                "5" -> { R.drawable.icon_rainny }
//                else -> skyId
//            }
//
//            val views: RemoteViews = RemoteViews(
//                context?.packageName,
//                R.layout.widget_small
//            ).apply {
//                if (tmp.isNotEmpty())
//                    setTextViewText(R.id.tv_now, "${tmp}°")
//                if (str.isNotEmpty())
//                    setTextViewText(R.id.tv_widget_text, str)
//                if (air.isNotEmpty())
//                    setTextViewText(R.id.tv_air, air)
//
//                setImageViewResource(R.id.iv_sky, skyId)
//                setOnClickPendingIntent(R.id.ll_widget_container, pendingIntent)
//                setOnClickPendingIntent(R.id.iv_refresh, refreshIntent)
//            }
//
//            appWidgetManager?.updateAppWidget(appWidgetId, views)
//        }
//    }
//
//
//
//    override fun onEnabled(context: Context?) {
//        super.onEnabled(context)
//        L.d("SmallAppWidgetProvider onEnabled")
//        // TODO chan 위젯 첫 생성시 데이터 만들어 노출 시켜야함
//        // TODO chan 위젯 크기 고정? 태블릿일 경우?
//    }
//
//    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
//        super.onDeleted(context, appWidgetIds)
//        L.d("SmallAppWidgetProvider onDeleted appWidgetIds : $appWidgetIds")
//        appWidgetIds?.forEach { appWidgetId ->
//            // Delete Widget ID
//            widgetViewModel.deleteWidgetId(WidgetId(id = appWidgetId))
//        }
//
//    }
//
//    override fun onUpdate(
//        context: Context?,
//        appWidgetManager: AppWidgetManager?,
//        appWidgetIds: IntArray?
//    ) {
////        super.onUpdate(context, appWidgetManager, appWidgetIds)
//
//        mContext = context
//        mAppWidgetManager = appWidgetManager
//        // Perform this loop procedure for each App Widget that belongs to this provider
//        appWidgetIds?.forEach { appWidgetId ->
//            L.d("SmallAppWidgetProvider onUpdate appWidgetId: $appWidgetId")
//            context?.let {
//                // Save Widget ID
//                widgetViewModel.saveWidgetId(WidgetId(id = appWidgetId))
//
//                // Test setTime
//                val str = "yyyyMMdd HH:mm".getTimeNow()
//
//                // get WeatherData
//                getWeatherData()
//
//                // Create an Intent to launch Activity
//                val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
//                    .let { intent ->
//                        // Targeting S+ (version 31 and above) requires that one of FLAG_IMMUTABLE
//                        // or FLAG_MUTABLE be specified when creating a PendingIntent.
//                        PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//                    }
//
//                val refreshIntent: PendingIntent = Intent(context, SmallAppWidgetProvider::class.java)
//                    .setAction("REFRESH_BTN_CLICK")
//                    .putExtra("WIDGET_ID", appWidgetId)
//                    .let { intent ->
//                        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//                    }
//
//                // Get the layout for the App Widget and attach an on-click listener
//                // to the button
//                val views: RemoteViews = RemoteViews(
//                    it.packageName,
//                    R.layout.widget_small
//                ).apply {
//                    setOnClickPendingIntent(R.id.ll_widget_container, pendingIntent)
//                    setOnClickPendingIntent(R.id.iv_refresh, refreshIntent)
//                    setTextViewText(R.id.tv_widget_text, str)
//                }
//
//                // Tell the AppWidgetManager to perform an update on the current app widget
//                appWidgetManager?.updateAppWidget(appWidgetId, views)
//            }
//        }
//    }
//
//    override fun onReceive(context: Context?, intent: Intent?) {
//        super.onReceive(context, intent)
//        intent?.let { intent ->
//            val action = intent.action
//
//            if (action == "REFRESH_BTN_CLICK") {
//                context?.let { mContext->
//                    val id = intent.getIntExtra("WIDGET_ID", 0)
//                    L.d("SmallAppWidgetProvider onReceive Widget ID : $id")
//                    getWeatherData()
//                }
//            }
//        }
//    }
//
//    // Weather Api
//    private fun getWeatherData() {
//        L.d("SmallAppWidgetProvider getWeatherData")
//        var nx: String = C.WeatherData.Location.Seoul["nx"] ?: ""
//        var ny: String = C.WeatherData.Location.Seoul["ny"] ?: ""
//
//        var nowDate: String = "yyyyMMdd".getTimeNow()
//        val nowTimeHour: Int = "HH".getTimeNow().toInt()
//        val nowTimeMinute: Int = "mm".getTimeNow().toInt()
//
//        L.d("reqWeatherUltraNow : $nowDate , hour : $nowTimeHour , minute : $nowTimeMinute")
//
//        val baseTime: String = if (nowTimeMinute >= 30){
//            String.format("%02d", nowTimeHour) + String.format("%02d", nowTimeMinute)
//        } else {
//            if (nowTimeHour == 0) {
//                nowDate = "yyyyMMdd".getYesterday()
//                "2330"
//            } else {
//                String.format("%02d", nowTimeHour-1) + "55"
//            }
//        }
//
//        L.d("SmallAppWidgetProvider getWeatherData $nowDate : $baseTime")
//
//        // TODO chan numOfRows 더 큰 값 필요
//        widgetViewModel.updateWeatherData(
//            mapOf(
//                "ServiceKey" to C.WeatherApi.API_KEY,
//                "dataType" to "JSON",
//                "pageNo" to "1",
//                "numOfRows" to "1000",
//                "base_date" to nowDate,
//                "base_time" to baseTime,
//                "nx" to nx,
//                "ny" to ny
//            )
//        )
//    }
//
//}