package kiu.dev.merryweather.base

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseDialog<B: ViewDataBinding>(
    val activity: BaseActivity<*, *>, private val cancelable: Boolean = false
): Dialog(activity) {

    // data binding layoutId
    abstract val layoutId: Int

    // data binding
    protected lateinit var binding: B
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE) //타이틀 바 삭제

        binding = DataBindingUtil.inflate(layoutInflater, layoutId, null, false)

        setCancelable(cancelable)
        setContentView(binding.root)

        if (cancelable){
            binding.root.setOnClickListener {
                hideDialog()
            }
        }

        with(window!!) {
            setBackgroundDrawableResource(android.R.color.transparent) // 그림자 삭제
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
    }

    open fun showDialog() {
        activity.showDialog(this)
    }

    open fun hideDialog() {
        activity.hideDialog(this)
    }
}