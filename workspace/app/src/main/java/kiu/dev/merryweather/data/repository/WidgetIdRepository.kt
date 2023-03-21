package kiu.dev.merryweather.data.repository

import io.reactivex.Completable
import io.reactivex.Flowable
import kiu.dev.merryweather.data.local.dao.WidgetIdDao
import kiu.dev.merryweather.data.local.widget.WidgetId
import javax.inject.Inject

class WidgetIdRepository @Inject constructor(
    private val widgetIdDao: WidgetIdDao
) {

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

    /**
     * 위젯 ID 삭제
     */
    fun deleteWidgetId(id: WidgetId) : Completable =
        widgetIdDao.deleteWidgetId(id)
}