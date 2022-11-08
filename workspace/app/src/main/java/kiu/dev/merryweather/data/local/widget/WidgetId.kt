package kiu.dev.merryweather.data.local.widget

import androidx.room.Entity
import androidx.room.PrimaryKey
import kiu.dev.merryweather.config.C
import java.io.Serializable

@Entity(tableName = C.RoomTableName.WIDGET_ID)
data class WidgetId(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
) : Serializable