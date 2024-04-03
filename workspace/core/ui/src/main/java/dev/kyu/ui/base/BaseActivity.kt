package dev.kyu.ui.base

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ViewDataBinding
import dev.kyu.ui.utils.L
import dev.kyu.ui.utils.bind
import dev.kyu.ui.utils.navigationHeight
import dev.kyu.ui.utils.statusBarHeight

abstract class BaseActivity<B: ViewDataBinding>: AppCompatActivity() {

    /**
     * view data binding
     */
    protected lateinit var binding: B
        private set

    /**
     * data binding layoutId
     */
    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = bind(layoutId)
        binding.lifecycleOwner = this
        init()
    }

    abstract fun init()

    /**
     * 상태바 숨김시 해당 높이값을 구해서 패딩 적용
     */
    open fun defaultPadding(container: ConstraintLayout) {
        container.setPadding(
            0,
            statusBarHeight(),
            0,
            navigationHeight()
        )
    }

    fun isIntentAvailable(intent: Intent): Boolean {
        val list: List<ResolveInfo> = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        L.d("isIntentAvailable : ${list.isNotEmpty()} ")
        return list.isNotEmpty()
    }
}