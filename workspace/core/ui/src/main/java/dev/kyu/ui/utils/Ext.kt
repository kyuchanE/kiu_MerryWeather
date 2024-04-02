package dev.kyu.ui.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

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