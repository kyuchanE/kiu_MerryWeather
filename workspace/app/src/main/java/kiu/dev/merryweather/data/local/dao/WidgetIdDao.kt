package kiu.dev.merryweather.data.local.dao

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import kiu.dev.merryweather.data.local.WidgetId

@Dao
interface WidgetIdDao {

    @Query("SELECT * FROM widget_id")
    fun getWidgetId(): Flowable<List<WidgetId>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWidgetId(vararg id: WidgetId):Completable

    @Update
    fun updateWidgetId(id: WidgetId): Completable

    @Query("DELETE FROM widget_id")
    fun deleteAll(): Completable

    // TODO chan 단일 위젯 아이디 삭제
}