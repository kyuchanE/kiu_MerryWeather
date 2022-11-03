package kiu.dev.merryweather.ui.widget

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.animation.RotateAnimation
import android.widget.RemoteViews
import androidx.room.Room
import com.bumptech.glide.Glide
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kiu.dev.merryweather.R
import kiu.dev.merryweather.config.C
import kiu.dev.merryweather.data.BasicApi
import kiu.dev.merryweather.data.local.WidgetId
import kiu.dev.merryweather.data.local.WidgetIdDataBase
import kiu.dev.merryweather.ui.activity.MainActivity
import kiu.dev.merryweather.utils.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class SmallAppWidgetProvider : AppWidgetProvider(),  KoinComponent{

    companion object {
        var mContext: Context? = null

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            t: String,
            s: String
        ) {
            L.d("onUpdate updateAppWidget : $appWidgetId")
            val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
                .let { intent ->
                    // TODO chan PendingIntent flags Issue
                    // Targeting S+ (version 31 and above) requires that one of FLAG_IMMUTABLE
                    // or FLAG_MUTABLE be specified when creating a PendingIntent.
                    PendingIntent.getActivity(context, 0, intent, FLAG_IMMUTABLE)
                }

            // Test setTime
            val str = "YYYYMMdd HH:mm".getTimeNow()

            val views: RemoteViews = RemoteViews(
                context.packageName,
                R.layout.widget_small
            ).apply {
                setTextViewText(R.id.tv_now, t)
                setTextViewText(R.id.tv_widget_text, str)
                setTextViewText(R.id.tv_sky, s)
                setImageViewResource(R.id.iv_refresh, R.drawable.loading)
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

        mContext = context
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

                // WeatherData
                getWeatherData(appWidgetId)

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
                    .putExtra("WIDGET_ID", appWidgetId)
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
                context?.let { mContext ->
                    val id = i.getIntExtra("WIDGET_ID", 0)
                    L.d("onReceive Widget ID : $id")
                    updateRefreshImage(
                        mContext,
                        AppWidgetManager.getInstance(mContext),
                        id
                    )

                }

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

    private fun getWeatherData(id: Int) {
        var itemJsonArray: List<JsonElement> = listOf()

        var nowDate: String = "YYYYMMdd".getTimeNow()
        val nowTimeHour: Int = "HH".getTimeNow().toInt()
        val nowTimeMinute: Int = "mm".getTimeNow().toInt()

        val baseTime: String = if (nowTimeMinute >= 30){
            String.format("%02d", nowTimeHour) + String.format("%02d", nowTimeMinute)
        } else {
            if (nowTimeHour == 0) {
                nowDate = "YYYYMMdd".getYesterday()
                "2330"
            } else {
                String.format("%02d", nowTimeHour-1) + "55"
            }
        }
        L.d("@@@@@ getWeatherData baseTime : $baseTime")
        get<BasicApi>().getApi(
            url = C.WeatherApi.WEATHER_ULTRA_NOW,
            mapOf(
                "ServiceKey" to C.WeatherApi.API_KEY,
                "dataType" to "JSON",
                "pageNo" to "1",
                "numOfRows" to "50",
                "base_date" to nowDate,
                "base_time" to baseTime,
                "nx" to C.WeatherData.Location.Seoul["nx"],
                "ny" to C.WeatherData.Location.Seoul["ny"]
            )
        ).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .doOnError { e ->
                L.d("@@@@@ e $e")
            }
            .doOnNext { json ->
                if (isWeatherSuccess(json)) {
                    itemJsonArray =
                        json.asJsonObject("response")
                            .asJsonObject("body")
                            .asJsonObject("items")
                            .asJsonArray("item")
                            .filter {
                                it.asJsonObject.asString("category") == "T1H" ||   // 기온
                                it.asJsonObject.asString("category") == "SKY"
                            }

                }
            }
            .doFinally {
                if (itemJsonArray.size > 0) {
                    var tmp = ""
                    var sky = ""

                    kotlin.run {
                        itemJsonArray.forEach {
                            if (it.asJsonObject.asString("category") == "T1H") {
                                tmp = it.asJsonObject.asString("fcstValue")
                                return@run
                            }
                        }
                    }

                    kotlin.run {
                        itemJsonArray.forEach {
                            if (it.asJsonObject.asString("category") == "SKY") {
                                sky = it.asJsonObject.asString("fcstValue")
                                return@run
                            }
                        }
                    }

                    mContext?.let {
                        updateAppWidget(
                            it,
                            AppWidgetManager.getInstance(it),
                            id,
                            tmp,
                            sky
                        )
                    }

                }

            }
            .subscribe()


    }

    /**
     * 기상청 Api ResultCode
     * @param data  json data
     * @return 00:정상, 01:어플리케이션 에러, 02:DB에러, 03:데이터 없음,
     * 04:HTTP에러, 05:서비스 연결 실패, 10:잘못된 요청 파라미터, 11:필수요청 에러,
     * 20:서비스 접근 거부, 21:사용할 수 없는 키, 22:서비스 요청제한 횟수 초과,
     * 30:등록되지 않은 키, 31:기한만료된 키, 32:등록되지 않은 IP, 33: 서명하지 않은 호출
     * 99:기타
     */
    private fun isWeatherSuccess(data: JsonObject): Boolean {
        val resultCode: String = try {
            data.asJsonObject("response")
                .asJsonObject("header")
                .asString("resultCode")
        } catch (e: Exception) {
            ""
        }

        return resultCode == "00"
    }

    private fun updateRefreshImage(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
    ) {
        val views: RemoteViews = RemoteViews(
            context.packageName,
            R.layout.widget_small
        ).apply {
            setImageViewResource(R.id.iv_refresh, R.drawable.ic_launcher_background)
        }
        getWeatherData(appWidgetId)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

}