package kiu.dev.merryweather.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.google.gson.JsonObject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kiu.dev.merryweather.R
import kiu.dev.merryweather.databinding.LoadingBinding
import kiu.dev.merryweather.listener.RequestSubscriber
import kiu.dev.merryweather.utils.*
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.atomic.AtomicInteger

abstract class BaseFragment<B : ViewDataBinding> : Fragment()  {
    protected lateinit var binding: B

    abstract val layoutId: Int

    // loading
    private lateinit var loadingBinding: LoadingBinding
    // 로딩 뷰 사용 카운트
    private val loadingCount = AtomicInteger()

    // 기본 에러 핸들러 on/off
    var useHandleError = true

    // Rx 핸들러
    private val compositeDisposable = CompositeDisposable()

    val baseActivity: BaseActivity<B>
        get() = activity as BaseActivity<B>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.bind(layoutId, container)
        binding.setOnEvents()
        binding.lifecycleOwner = this

        loadingBinding = bindView(R.layout.loading)
        (binding.root as ViewGroup).addView(
            loadingBinding.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        return binding.root
    }

    fun disposableClear() {
        compositeDisposable.clear()
    }

    override fun onDestroyView() {
        compositeDisposable.clear()
        super.onDestroyView()
    }

    /**
     * Rx 핸들을 핸들러에 등록
     *
     * @param disposable Rx 핸들
     */
    fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    /**
     * Rx 핸들을 핸들러에 제외
     *
     * @param disposable Rx 핸들
     */
    fun deleteDisposable(disposable: Disposable) {
        compositeDisposable.delete(disposable)
    }

    /**
     * Rx 핸들을 중지하고 핸들러에서 제외
     *
     * @param disposable Rx 핸들
     */
    fun removeDisposable(disposable: Disposable) {
        compositeDisposable.remove(disposable)
    }


    /**
     * 버튼 이벤트 처리
     *
     * @param v
     */
    open fun onBtnEvents(v: View) {}

    open fun initObserve() {}

    open fun onBackBtnEvent() {

    }

//    /**
//     * 로딩표시
//     */
//    fun showFragmentLoading() {
//        if (loadingCount.incrementAndGet() == 1) {
//            ThreadUtils.runOnUiThread {
//                loadingBinding.root.show()
//            }
//        }
//    }
//
//    /**
//     * 로딩숨김
//     */
//    fun hideFragmentLoading() {
//        if (loadingCount.decrementAndGet() == 0) {
//            ThreadUtils.runOnUiThread {
//                loadingBinding.root.gone()
//            }
//        }
//    }

    /**
     * 서버 에러코드 처리
     */
    fun handleServerCode(result: JsonObject) {
        val status = result.asInt("returnStatus")
        val code = result.asString("returnCode")
        val message = result.asString("returnMessage")

        // (!200 || !SUCCESS) && !401 && message
        if (((status != 200 || !code.equals("SUCCESS", true)) && code != "0000" && status != 401 && message.isNotEmpty())) {

        }
    }

//    /**
//     * 공통 속성을 정의한 Subscriber
//     *
//     * @param useLoading 로딩 사용여부
//     * @return
//     */
//    fun <T> buildSubscriber(useLoading: Boolean = true) = object : RequestSubscriber<T>() {
//        override fun onStart() {
//            super.onStart()
//            if (useLoading) showFragmentLoading()
//        }
//
//        override fun onError(t: Throwable) {
//            if (!skipErrorHandle) handleError(t)
//            if (useLoading) hideFragmentLoading()
//        }
//
//        override fun onNext(t: T) {
//            if (!skipErrorHandle) {
//                if (t is JsonObject) {
//                    handleServerCode(t)
//                }
//            }
//        }
//
//        override fun onComplete() {
//            if (useLoading) hideFragmentLoading()
//        }
//    }

    /**
     * 예상되는 예외처리
     *
     * @param t
     */
    fun handleError(t: Throwable) {
        if (useHandleError) {
            when (t) {
                is HttpException -> {
                    // 에러코드별 핸들링
                    when (t.code()) {
                        401 -> {
                        }
                        400, 404, 503 -> {
                        }
                        500 -> {
                        }
                        else -> {
                        }
                    }

                }
                is ConnectException -> {
                    L.e("ConnectException")
                }
                is UnknownHostException -> {
                    L.e("UnknownHostException")
                }
                is SocketTimeoutException -> {
                    L.e("SocketTimeoutException")
                }
                else -> {
                }
            }
        }

    }
}