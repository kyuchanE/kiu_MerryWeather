package kiu.dev.merryweather.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kiu.dev.merryweather.base.BaseActivity
import kiu.dev.merryweather.di.GlideApp
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

fun <T : ViewDataBinding> Fragment.bindView(layoutId: Int, parent: ViewGroup? = null, attachToRoot: Boolean = false): T {
    return DataBindingUtil.inflate(layoutInflater, layoutId, parent, attachToRoot)
}

fun ViewDataBinding.setOnEvents(activity: BaseActivity<*>? = null) = root.setOnEvents(activity)


////////////////////////////// View //////////////////////////////

val View.isClick get() = tag == "click"

val View.activity: BaseActivity<*>?
    get() {
        var ctx = context
        while (ctx is ContextWrapper) {
            if (ctx is BaseActivity<*>) {
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

fun View.setOnEvents(baseActivity: BaseActivity<*>? = null): View {
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

fun Any?.toast(handler: BaseActivity<*>) {
    val str: String = when (this) {
        is Boolean, is Int, is Long, is Float, is Double -> this.toString()
        is Throwable -> this.toString()
        is String -> this
        else -> this.toString()
    }
    Toast.makeText(handler, str, Toast.LENGTH_SHORT).show()
}


////////////////////////////// String //////////////////////////////

fun String.getTimeNow(): String {
    return try {
        val date = Date(System.currentTimeMillis())
        val simpleDateFormat = SimpleDateFormat(this)
        simpleDateFormat.format(date)
    } catch (e: Exception) {
        L.e(e.message)
        ""
    }
}

fun String.getYesterday() : String {
    return try {
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.add(Calendar.DATE, -1)

        val simpleDateFormat = SimpleDateFormat(this)
        simpleDateFormat.format(cal.time)
    } catch (e: Exception) {
        L.e(e.message)
        ""
    }
}

fun String.getFutureDate(plusDate: Int): String {
    return try {
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.add(Calendar.DATE, plusDate)

        val simpleDateFormat = SimpleDateFormat(this)
        simpleDateFormat.format(cal.time)
    } catch (e: Exception) {
        L.e(e.message)
        ""
    }
}

////////////////////////////// ImageView //////////////////////////////

fun ImageView.load(url: String): ImageView {
    if (url.isNotEmpty()) {
        GlideApp.with(context)
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    }
    return this
}

fun ImageView.loadRound(url: String, round: Int): ImageView {
    if (url.isNotEmpty()) {
        GlideApp.with(context)
            .load(url)
            .transform(CenterCrop(), RoundedCorners(round.dp2px))
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    }
    return this
}

fun ImageView.loadRound(drawable: Drawable, round: Int): ImageView {
    GlideApp.with(context)
        .load(drawable)
        .transform(CenterCrop(), RoundedCorners(round.dp2px))
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
    return this
}

fun ImageView.loadRoundTop(url: String, round: Int): ImageView {
    if (url.isNotEmpty()) {
        GlideApp.with(context)
            .load(url)
            .transform(CenterCrop(), RoundedCornersTransformation(round.dp2px, 0, RoundedCornersTransformation.CornerType.TOP))
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    }
    return this
}

fun ImageView.loadCircle(url: String): ImageView {
    if (url.isNotEmpty()) {
        GlideApp.with(context)
            .load(url)
            .apply(RequestOptions().circleCrop())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    }
    return this
}

fun ImageView.loadCircle(d: Drawable?): ImageView {
    d?.let {
        GlideApp.with(context)
            .load(d)
            .apply(RequestOptions().circleCrop())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    }
    return this
}

////////////////////////////// Int //////////////////////////////

val Int.digit get() = if (this < 10) "0${toString()}" else toString()
val Int.px2dp get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.dp2px get() = (this * Resources.getSystem().displayMetrics.density).toInt()
val Int.boolean get() = this > 0
val Int.count get() = String.format(Locale.KOREA, "%,d", this)

////////////////////////////// Long //////////////////////////////

val Long.count get() = String.format(Locale.KOREA, "%,d", this)

////////////////////////////// Float //////////////////////////////

val Float.px2dp get() = (this / Resources.getSystem().displayMetrics.density)
val Float.dp2px get() = (this * Resources.getSystem().displayMetrics.density)

////////////////////////////// Double //////////////////////////////

val Double.count get() = String.format(Locale.KOREA, "%,.1f", this)
//val Double.count get() = String.format(Locale.KOREA, "%,.2f", this)


////////////////////////////// Boolean //////////////////////////////

val Boolean.visible get() = if (this) View.VISIBLE else View.GONE
val Boolean.bit get() = if (this) 1 else 0
val Boolean.yn get() = if (this) "Y" else "N"



////////////////////////////// Activity //////////////////////////////

// status bar 투명 (숨김)
fun Activity.setStatusBarTransparent() {
    window.apply {
        setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
    if(Build.VERSION.SDK_INT >= 30) {	// API 30 에 적용
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}
// status bar 되돌리기
fun Activity.setStatusBarOrigin() {
    window.apply {
        clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
    if(Build.VERSION.SDK_INT >= 30) {	// API 30 에 적용
        WindowCompat.setDecorFitsSystemWindows(window, true)
    }
}

// 상태바 높이 값
fun Context.statusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")

    return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
    else 0
}
// 하단 네비게이션 높이 값
fun Context.navigationHeight(): Int {
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")

    return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
    else 0
}