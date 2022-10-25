package kiu.dev.merryweather.data.repository

import io.reactivex.Completable
import io.reactivex.Flowable
import kiu.dev.merryweather.data.local.WidgetId
import kiu.dev.merryweather.data.local.dao.WidgetIdDao
import org.koin.core.component.KoinComponent
import java.util.concurrent.Flow

class WidgetIdRepository(
    private val widgetIdDao: WidgetIdDao
) : KoinComponent {

    /**
     * 생성된 위젯 ID 조회
     */
    fun getWidgetId() : Flowable<List<WidgetId>> =
            widgetIdDao.getWidgetId()


    /**
     * 위젯 ID 저장
     */
    fun saveWidgetId(vararg id: WidgetId) : Completable =
        widgetIdDao.insertWidgetId(*id)
}