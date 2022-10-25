package kiu.dev.merryweather.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "widget_id")
data class WidgetId(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var name: String? = null
) : Serializable