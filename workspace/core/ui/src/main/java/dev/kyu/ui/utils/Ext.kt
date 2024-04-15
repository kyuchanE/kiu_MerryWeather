package dev.kyu.ui.utils

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import dev.kyu.ui.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

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


////////////////////////////// View //////////////////////////////

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

fun View.setShowOrGone(isShow: Boolean): View {
    visibility = if (isShow) {
        View.VISIBLE
    } else {
        View.GONE
    }
    return this
}

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


fun Date.dateToString(format: String): String {
    val simpleDateFormat = SimpleDateFormat(format)
    return simpleDateFormat.format(this)
}

fun String.getToday(): String = Date(System.currentTimeMillis()).dateToString(this)

fun getNextHour(): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.HOUR, 1)
    return calendar.time.dateToString("yyyyMMddHH") + "00"
}

fun Context.getSkyDrawable(pty: String, sky: String): Drawable? {
    return if (pty == "0") {
        when(sky) {
            "1" -> this.getDrawable(R.drawable.icon_sunny)
            "3" -> this.getDrawable(R.drawable.icon_cloudy_a_lot)
            "4" -> this.getDrawable(R.drawable.icon_cloudy)
            else -> this.getDrawable(R.drawable.icon_sunny)
        }
    } else {
        when(pty) {
            "1" -> this.getDrawable(R.drawable.icon_rainny)
            "2" -> this.getDrawable(R.drawable.icon_rainny)
            "3" -> this.getDrawable(R.drawable.icon_sunny)           // TODO chan 눈
            "4" -> this.getDrawable(R.drawable.icon_rainny)
            "5" -> this.getDrawable(R.drawable.icon_rainny)
            "6" -> this.getDrawable(R.drawable.icon_rainny)           // TODO chan 눈
            "7" -> this.getDrawable(R.drawable.icon_rainny)           // TODO chan 눈
            else -> this.getDrawable(R.drawable.icon_sunny)
        }
    }
}