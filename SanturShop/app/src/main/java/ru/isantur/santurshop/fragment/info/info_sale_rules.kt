package ru.isantur.santurshop.fragment.info

import android.os.Bundle
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.fragment.app.Fragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.include_appbar.*
import kotlinx.android.synthetic.main.include_status.*
import kotlinx.android.synthetic.main.info_sale_rules.*
import kotlinx.android.synthetic.main.info_sale_rules.view.*
import moxy.InjectViewState
import moxy.MvpAppCompatFragment
import moxy.MvpPresenter
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.isantur.santurshop.activity.Main
import ru.isantur.santurshop.R
import ru.isantur.santurshop.di.BackendModule
import ru.isantur.santurshop.varApp
import javax.inject.Inject


@StateStrategyType(value = OneExecutionStateStrategy::class)
interface InfoSaleRules_ViewState: MvpView {

    fun onSuccess(resp: Spanned)
    fun onError ()

}



class info_sale_rules : MvpAppCompatFragment(), InfoSaleRules_ViewState {

    companion object {

        fun instance (): Fragment {
            return info_sale_rules()
        }

    }


    @InjectPresenter lateinit var info_sale_rules_Presenter: info_sale_rules_Presenter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {


        return inflater.inflate(R.layout.info_sale_rules, container, false)


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        info_sale_rules_Presenter.showOnLoad(view)
        info_sale_rules_Presenter.hideOnLoad(view)
        info_sale_rules_Presenter.onLoad()

        //click back:
        some_addbar_view_back.setOnClickListener {
//            findNavController().navigate(R.id.go_fromInfoPayment_inProfileInfo)
            varApp.delete_step_from_BackStack(varApp.supportFragmentManager, step = this::class.java.simpleName)
        }

        //click update:
        status_update.setOnClickListener {
            info_sale_rules_Presenter.showOnLoad(view)
            info_sale_rules_Presenter.hideOnLoad(view)
            info_sale_rules_Presenter.onLoad()
        }


    } //onViewCreated



    override fun onSuccess(resp: Spanned) {


        rules_pb.visibility = View.GONE
        rules_status.visibility = View.GONE

        rules_body.text = resp
        rules_body.visibility = View.VISIBLE

        rules_appbar.visibility = View.VISIBLE

        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = rules_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = true,
            some_title_text = "Правила продажи",
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)

        varApp.nav_view.visibility = View.VISIBLE


    }

    override fun onError() {

        rules_pb.visibility = View.GONE
        rules_body.visibility = View.GONE



        rules_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = rules_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = varApp.heading_no_internet,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)

        rules_status.visibility = View.VISIBLE
//        varApp.nav_view.visibility = View.VISIBLE


    }

} //Class






@InjectViewState
class info_sale_rules_Presenter : MvpPresenter<InfoSaleRules_ViewState>() {

    private val disposeBag = CompositeDisposable()

    @Inject
    lateinit var backendModule: BackendModule

    fun showOnLoad (v: View) {

        v.rules_pb.visibility = View.VISIBLE

    }

    fun hideOnLoad (v: View) {
        v.rules_appbar.visibility = View.GONE
        v.rules_status.visibility = View.GONE
        varApp.nav_view.visibility = View.GONE
    }

    fun onLoad () {

        varApp.appComponent.inject(this)


        disposeBag.add(
            backendModule.provideMyGet().paypolitic(varApp.SID)
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    viewState.onSuccess(it.parseAsHtml())
                }, {
                    viewState.onError()
                })
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        disposeBag.dispose()
        disposeBag.clear()
    }


}