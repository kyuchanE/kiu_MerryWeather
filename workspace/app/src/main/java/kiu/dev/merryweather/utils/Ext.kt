package kiu.dev.merryweather.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kiu.dev.merryweather.base.BaseActivity
import java.text.SimpleDateFormat
import java.util.*

////////////////////////////// DataBinding //////////////////////////////
val Context.layoutInflater: LayoutInflater get() = LayoutInflater.from(this)

val View.layoutInflater get() = context.layoutInflater
fun <T : ViewDataBinding> View.bind() = DataBindingUtil.bind<T>(this) as T
fun <T : ViewDataBinding> LayoutInflater.bind(layoutId: Int, parent: ViewGroup? = null, attachToParent: Boolean = false): T {
    return DataBindingUtil.inflate(this, layoutId, parent, attachToParent)
}
fun <T : ViewDataBinding> Activity.bind(layoutId: Int): T {
    return DataBindingUtil.setContentView(this, layoutId)
}
fun <T : ViewDataBinding> Activity.bindView(layoutId: Int, parent: ViewGroup? = null, attachToRoot: Boolean = false): T {
    return DataBindingUtil.inflate(layoutInflater, layoutId, parent, attachToRoot)
}

fun ViewDataBinding.setOnEvents(activity: BaseActivity<*, *>? = null) = root.setOnEvents(activity)


////////////////////////////// View //////////////////////////////

val View.isClick get() = tag == "click"

val View.activity: BaseActivity<*, *>?
    get() {
        var ctx = context
        while (ctx is ContextWrapper) {
            if (ctx is BaseActivity<*, *>) {
                return ctx
            }
            ctx = ctx.baseContext
        }
        return null
    }

val ViewGroup.views: List<View>
    get() {
        val views = mutableListOf<View>()
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is ViewGroup) {
                views.addAll(child.views)
            }

            views.add(child)
        }
        return views
    }

val ViewGroup.eventViews: List<View>
    get() {
        val result = mutableListOf<View>()

        for (view in views) {
            when (view) {
                is Button,
                is ImageButton,
                is CompoundButton,
                is CheckedTextView,
                is RadioButton,
                is CheckBox
                -> result.add(view)
            }
            if (view.isClick) result.add(view)
        }
        return result
    }

fun View.setOnEvents(baseActivity: BaseActivity<*, *>? = null): View {
    var views = mutableListOf<View>()

    if (this is ViewGroup) views.addAll(eventViews)
    else views.add(this)

    val handler = baseActivity ?: activity
    handler?.let { h ->
        views.filter { it.id != View.NO_ID }.forEach {
            when (it) {
                is CompoundButton -> {
                    it.setOnClickListener(h::onRxBtnEvents)
                }
                is Button, is ImageButton -> it.setOnClickListener(h::onRxBtnEvents)
            }

            if (it.isClick) it.setOnClickListener(h::onRxBtnEvents)
        }
    }

    return this
}

fun View.show(): View {
    visibility = View.VISIBLE
    return this
}

fun View.hide(): View {
    visibility = View.INVISIBLE
    return this
}

fun View.gone(): View {
    visibility = View.GONE
    return this
}

////////////////////////////// Long //////////////////////////////

fun Long.changeDate(form: String): String {
    return try {
        val dateFormat = SimpleDateFormat(form)
        val cal = Calendar.getInstance()
        cal.timeInMillis = this*1000L
        dateFormat.format(cal.time)
    } catch (e: Exception) {
        L.d("changeDate Exception : ${e.message}")
        ""
    }
}

////////////////////////////// Double //////////////////////////////

val Double.count get() = String.format(Locale.KOREA, "%,.2f", this)

////////////////////////////// JsonObject //////////////////////////////
fun JsonObject?.asString(key: String, default: String = ""): String = try {
    this?.get(key)?.asString ?: default
} catch (e: Exception) {
    "fun JsonObject?.asString 예외".log()
    default
}

fun JsonObject?.asInt(key: String, default: Int = 0): Int = try {
    this?.get(key)?.asInt ?: default
} catch (e: Exception) {
    "fun JsonObject?.asInt 예외".log()
    default
}

fun JsonObject?.asFloat(key: String, default: Float = 0F): Float = try {
    this?.get(key)?.asFloat ?: default
} catch (e: Exception) {
    "fun JsonObject?.asFloat 예외".log()
    default
}

fun JsonObject?.asDouble(key: String, default: Double = 0.0): Double = try {
    this?.get(key)?.asDouble ?: default
} catch (e: Exception) {
    "fun JsonObject?.asDouble 예외".log()
    default
}

fun JsonObject?.asLong(key: String, default: Long = 0L): Long = try {
    this?.get(key)?.asLong ?: default
} catch (e: Exception) {
    "fun JsonObject?.asLong 예외".log()
    default
}

fun JsonObject?.asBoolean(key: String, default: Boolean = false): Boolean = try {
    this?.get(key)?.asBoolean ?: default
} catch (e: Exception) {
    "fun JsonObject?.asBoolean 예외".log()
    default
}

fun JsonObject?.asJsonObject(key: String, default: JsonObject = JsonObject()): JsonObject = try {
    this?.get(key)?.asJsonObject ?: default
} catch (e: Exception) {
    "fun JsonObject?.asJsonObject 예외".log()
    default
}

fun JsonObject?.asJsonArray(key: String, default: JsonArray = JsonArray()): JsonArray = try {
    this?.get(key)?.asJsonArray ?: default
} catch (e: Exception) {
    "fun JsonObject?.asJsonArray 예외".log()
    default
}

val JsonArray.asList: MutableList<JsonObject> get() = map { it.asJsonObject }.toMutableList()

////////////////////////////// Any //////////////////////////////

fun Any?.log(prefix: String = ""): Any? {
    when (this) {
        is Boolean, is Int, is Long, is Float, is Double -> L.d(prefix + toString())
        is Throwable -> L.e(this)
        is String -> L.d(prefix + this)
        else -> L.d(prefix + this)
    }
    return this
}

fun Any?.toast(handler: BaseActivity<*, *>) {
    val str: String = when (this) {
        is Boolean, is Int, is Long, is Float, is Double -> this.toString()
        is Throwable -> this.toString()
        is String -> this
        else -> this.toString()
    }
    Toast.makeText(handler, str, Toast.LENGTH_SHORT).show()
}
