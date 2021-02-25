package ru.isantur.santurshop.fragment.map


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_ya.*
import kotlinx.android.synthetic.main.fragment_ya.view.*
import moxy.InjectViewState
import moxy.MvpAppCompatFragment
import moxy.MvpPresenter
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.isantur.santurshop.Data.Company
import ru.isantur.santurshop.R
import ru.isantur.santurshop.di.BackendModule
import ru.isantur.santurshop.fragment.map.ya.Companion.arrayCompany
import ru.isantur.santurshop.varApp
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


//class JavaScriptInterface internal constructor(context: Context) {
//
//    var mContext: Context
//
//    @JavascriptInterface
//    fun showToast(coord: String? = null) {
//        val comp = arrayCompany.filter { f -> f.gpscoords == coord }
//
//        Toast.makeText(mContext, comp[0].address, Toast.LENGTH_SHORT).show()
//    }
//
//    init {
//        mContext = context
//    }
//
//}




@StateStrategyType(value = OneExecutionStateStrategy::class)
interface Ya_ViewState: MvpView {

    fun onSuccess()
    fun noInternet()
    fun isEmpty()

}



class ya : MvpAppCompatFragment(), Ya_ViewState {



    @InjectPresenter lateinit var ya_presenter: ya_Presenter

    companion object {

        fun instance (): MvpAppCompatFragment {
            return ya()
        }

        var arguments: Bundle? = null
        fun instance(args: Bundle?): MvpAppCompatFragment {
            val Ya = ya()
            arguments = args
            Ya.arguments = args
            return Ya
        }


        lateinit var v: View

        lateinit var mapFragment: Fragment

        var arrayCompany = ArrayList<Company>()

        lateinit var ya_view: WebView;

    }




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        v = inflater.inflate(R.layout.fragment_ya, container, false)

        return v

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        ya_view = ya_page
//
//        ya_presenter.companyinfo()
//
//        ya_page.settings.javaScriptEnabled = true
//
//        ya_page.loadUrl("file:///android_asset/ya.html");
//
//
//        //send gps coords to javascript:
////        ya_btn.setOnClickListener {
////            //сначала удалим все placemark с карты:
//////            ya_page.loadUrl("javascript:updatePlacemark()")
////
////            //потом добавляем placemark на карту:
////            arrayCompany.forEach { company ->
////
////                ya_page.loadUrl("javascript:initCity('${Gson().toJson(company)}')");
////
////            }
////        }
//
//        //get click from javascript:
//        ya_page.addJavascriptInterface( JavaScriptInterface(view.context), "AndroidFunction");

    }

    override fun onSuccess() {

    }

    override fun noInternet() {

    }

    override fun isEmpty() {

    }


} //Class Map










//region PRESENTER

@InjectViewState
class ya_Presenter : MvpPresenter<Ya_ViewState>() {

    private val disposeBag = CompositeDisposable()

    @Inject lateinit var backendModule: BackendModule


    fun companyinfo () {
        varApp.appComponent.inject(this)


        disposeBag.add (
            backendModule.provideMyGet().companyinfo(varApp.SID)
                .delay(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.size != 0) {
                        arrayCompany = it
                        arrayCompany.forEach { company ->
                            ya.ya_view.loadUrl("javascript:initCity('${Gson().toJson(company)}')");
                        }
                        viewState.onSuccess()
                    } else {
                        viewState.isEmpty()
                    }
                }, {
                    viewState.noInternet()
                }))

    }

    override fun onDestroy() {
        super.onDestroy()
        disposeBag.dispose()
        disposeBag.clear()
    }

}

//endregion
