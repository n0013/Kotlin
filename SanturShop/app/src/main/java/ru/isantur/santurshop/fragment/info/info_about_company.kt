package ru.isantur.santurshop.fragment.info

import android.os.Bundle
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.include_appbar.*
import kotlinx.android.synthetic.main.include_status.*
import kotlinx.android.synthetic.main.info_about_company.*
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
interface InfoAboutCompany_ViewState: MvpView {

    fun onSuccess(resp: Spanned)
    fun noInternet ()

}



class info_about_company : MvpAppCompatFragment(), InfoAboutCompany_ViewState {

    @InjectPresenter lateinit var info_about_company_presenter: info_about_company_Presenter

    companion object {

        fun instance (): MvpAppCompatFragment {
            return info_about_company()
        }

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {

        return inflater.inflate(R.layout.info_about_company, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onLoad()

        //click back:
        some_addbar_view_back.setOnClickListener {
            varApp.delete_step_from_BackStack(varApp.supportFragmentManager, step = this::class.java.simpleName)
        }

        //click update:
        status_update.setOnClickListener {
            onLoad()
        }



    }


    fun onLoad () {
        iac_pb.visibility = View.VISIBLE
        iac_appbar.visibility = View.GONE
        iac_status.visibility = View.GONE
        varApp.nav_view.visibility = View.GONE
        info_about_company_presenter.onLoad()
    }

    override fun onSuccess(resp: Spanned) {


        iac_pb.visibility = View.GONE
        iac_status.visibility = View.GONE

        iac_body.text = resp
        iac_body.visibility = View.VISIBLE

        iac_appbar.visibility = View.VISIBLE

        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = iac_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = true,
            some_title_text = "О компании",
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)

        varApp.nav_view.visibility = View.VISIBLE


    }

    override fun noInternet() {

        iac_pb.visibility = View.GONE
        iac_body.visibility = View.GONE

        iac_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = iac_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = varApp.heading_no_internet,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)

        iac_status.visibility = View.VISIBLE
        varApp.nav_view.visibility = View.GONE


    }




} //class









//region PRESENTER

@InjectViewState
class info_about_company_Presenter : MvpPresenter<InfoAboutCompany_ViewState>() {

    private val disposeBag = CompositeDisposable()

    @Inject lateinit var backendModule: BackendModule


    fun onLoad () {
        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().AboutCompany(varApp.SID)
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    viewState.onSuccess(it.parseAsHtml())
                }, {
                    viewState.noInternet()
                })
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        disposeBag.dispose()
        disposeBag.clear()
    }

}

//endregion
