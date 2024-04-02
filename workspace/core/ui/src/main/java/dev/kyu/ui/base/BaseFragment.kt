package dev.kyu.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import dev.kyu.ui.utils.bind
import dev.kyu.ui.utils.navigationHeight
import dev.kyu.ui.utils.statusBarHeight

abstract class BaseFragment<B: ViewDataBinding>: Fragment() {
    protected lateinit var binding: B

    abstract val layoutId: Int

    val baseActivity: BaseActivity<B>
        get() = activity as BaseActivity<B>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.bind(layoutId, container)
        binding.lifecycleOwner = this

        return binding.root
    }

    /**
     * 상태바 숨김시 해당 높이값을 구해서 패딩 적용
     */
    open fun defaultPadding(container: ConstraintLayout) {
        container.setPadding(
            0,
            baseActivity.statusBarHeight(),
            0,
            baseActivity.navigationHeight()
        )
    }
}