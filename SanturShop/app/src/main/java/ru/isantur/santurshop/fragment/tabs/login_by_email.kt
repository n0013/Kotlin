package ru.isantur.santurshop.fragment.tabs

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.text.parseAsHtml
import androidx.fragment.app.Fragment
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import khttp.get
import kotlinx.android.synthetic.main.include_status.*
import kotlinx.android.synthetic.main.info_about_company.*
import kotlinx.android.synthetic.main.profile_input.view.*
import kotlinx.android.synthetic.main.profile_input_email.*
import moxy.InjectViewState
import moxy.MvpAppCompatFragment
import moxy.MvpPresenter
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.isantur.santurshop.*
import ru.isantur.santurshop.Data.ListGoods
import ru.isantur.santurshop.Retrofit.myGET
import ru.isantur.santurshop.activity.Main
import ru.isantur.santurshop.di.BackendModule
import ru.isantur.santurshop.fragment.info.InfoAboutCompany_ViewState
import ru.isantur.santurshop.fragment.products.product_list
import ru.isantur.santurshop.fragment.profile.profile_change_pass
import ru.isantur.santurshop.fragment.profile.profile_my
import ru.isantur.santurshop.fragment.profile.profile_recovery_password
import ru.isantur.santurshop.fragment.profile.profile_registration
import ru.isantur.santurshop.fragment.tabs.login_by_email.Companion.v
import ru.isantur.santurshop.varApp.Companion.iam
import javax.inject.Inject


@StateStrategyType(value = OneExecutionStateStrategy::class)
interface ByEmail_ViewState: MvpView {

    fun onSuccess()
    fun noInternet ()
    fun onError(error: String)

    fun success_login(resp: String)
    fun success_loginSID(resp: String)
    fun success_connect(resp: String)




}


class login_by_email: MvpAppCompatFragment(), ByEmail_ViewState {

    @InjectPresenter lateinit var by_email_presenter: by_email_Presenter

    companion object {

        fun instance(): MvpAppCompatFragment {
            return login_by_email()
        }

        fun instance(args: Bundle?): MvpAppCompatFragment {
            val by_email = login_by_email()
            by_email.arguments = args
            return by_email
        }

        lateinit var v: View

    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        v = inflater.inflate(R.layout.profile_input_email, container, false)

        return v

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onLoad()

        //TODO click input by Email:
        btnLoginEmail.setOnClickListener {
            login_by_email()
        }

        //TODO go page recovery passowrd:
        byEmail_recovery.setOnClickListener {
            varApp.replaceFragment(varApp.supportFragmentManager, profile_recovery_password.instance(), addAnimation = true, animation =  anim.from_right_in_left, addToBackStack = true)
        }

        //TODO go page registration:
        byEmail_registration.setOnClickListener {
            varApp.replaceFragment(varApp.supportFragmentManager, profile_registration.instance(), addAnimation = true, animation =  anim.from_right_in_left, addToBackStack = true)
        }

        //TODO focus\click login:
        byEmail_email.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) {byEmail_email_txt.error = null } }
        byEmail_email.setOnClickListener { byEmail_email_txt.error = null }

        //TODO focus\click psw:
        byEmail_psw.setOnFocusChangeListener { v, hasFocus ->   if (hasFocus) { byEmail_psw_txt.error = null } }
        byEmail_psw.setOnClickListener { byEmail_psw_txt.error = null }


        //TODO change (write) psw:

        byEmail_psw.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (s.toString().contains("\n")) {
                    byEmail_psw.setText(s.toString().replace("\n", ""))
                    Utils.hideKeyboard(v.context, v)
                    login_by_email()
                }
            }

        })




    }



    fun onLoad () {

        (byEmail_page.parent.parent.parent as ConstraintLayout).profile_input_status.visibility = View.GONE
        (byEmail_page.parent.parent.parent as ConstraintLayout).profile_input_pb.visibility = View.GONE
        (byEmail_page.parent.parent.parent as ConstraintLayout).profile_input_appbar.visibility = View.VISIBLE


        varApp.nav_view.visibility = View.VISIBLE
        (byEmail_page.parent.parent as CoordinatorLayout).visibility = View.VISIBLE
    }

    fun login_by_email () {

        //animation:
        if (byEmail_email.text.toString() == "") { YoYo.with(Techniques.Shake).duration(1000).playOn(byEmail_email_txt);  byEmail_email_txt.error = "Введите Email" }
        if (byEmail_psw.text.toString() == "") { YoYo.with(Techniques.Shake).duration(1000).playOn(byEmail_psw_txt); byEmail_psw_txt.error = "Введите пароль" }

        //input in profile:
        if ( byEmail_email.text.toString() != "" && byEmail_psw.text.toString() != "" ) {

            (byEmail_page.parent.parent.parent as ConstraintLayout).profile_input_status.visibility = View.GONE
            (byEmail_page.parent.parent.parent as ConstraintLayout).profile_input_appbar.visibility = View.GONE
            (byEmail_page.parent.parent as CoordinatorLayout).visibility = View.GONE
            (byEmail_page.parent.parent.parent as ConstraintLayout).profile_input_pb.visibility = View.VISIBLE


            by_email_presenter.login(email = "${byEmail_email.text}", psw = "${byEmail_psw.text}")



        }


    }

    override fun onSuccess() {

        //TODO go page profile:
        if ( iam[0].id != 0 ) {
            varApp.clearBackStack(varApp.supportFragmentManager)
            varApp.replaceFragment(varApp.supportFragmentManager, profile_my.instance(), addAnimation = true, animation = anim.from_left_in_right)
        }

    }



    override fun success_login(resp: String) {


        if ( ! resp.contains("ok") ) {
            //authorization TODO failed:
            val alert = AlertDialog.Builder(this.context)
            with(alert) {
                setTitle("Авторизация")
                setMessage("Неверный логин или пароль. Повторите еще раз!")
                setPositiveButton("OK", null)
                show()
            }
            onLoad()

        } else {
            //authorization was TODO successful:
            by_email_presenter.connect()

        }
    }

    override fun success_connect(resp: String) {

        //TODO save SID and sessionId in PREF:
        varApp.setSID( resp )
        varApp.setSessionId_Preferences(varApp.SID)

        //TODO login with a new SID:
        by_email_presenter.loginSID(email = "${byEmail_email.text}", psw = "${byEmail_psw.text}")



    }

    override fun success_loginSID(resp: String) {

        //TODO save PREF:
        varApp.setLoginPassword_Preferences( mapOf(byEmail_email.text.toString() to byEmail_psw.text.toString()) )

        //TODO save iam:
        by_email_presenter.iam()


    }

    override fun noInternet() {

        (byEmail_page.parent.parent.parent as ConstraintLayout).profile_input_pb.visibility = View.GONE
        (byEmail_page.parent.parent as CoordinatorLayout).visibility = View.GONE

        (byEmail_page.parent.parent.parent as ConstraintLayout).profile_input_appbar.visibility = View.VISIBLE

        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = (byEmail_page.parent.parent.parent as ConstraintLayout).profile_input_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = varApp.heading_no_internet,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)


        (byEmail_page.parent.parent.parent as ConstraintLayout).profile_input_status.visibility = View.VISIBLE
        varApp.nav_view.visibility = View.GONE
    }

    override fun onError(error: String) {
        varApp.showDialog(v, v.context, (activity as AppCompatActivity).supportActionBar!!, "Авторизация", error)
    }




}



//region PRESENTER

@InjectViewState
class by_email_Presenter : MvpPresenter<ByEmail_ViewState>() {

    private val disposeBag = CompositeDisposable()

    @Inject
    lateinit var backendModule: BackendModule


    fun login (email: String, psw: String) {
        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().login(email, psw)
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    viewState.success_login(it)
                }, {
                    viewState.noInternet()
                })
        )

    }

    fun loginSID (email: String, psw: String) {
        varApp.appComponent.inject(this)

        disposeBag.add(

            backendModule.provideRetrofit(url = "${varApp.url}login/?u=${email}&p=${psw}&sid=${varApp.SID}").create(myGET::class.java) .setOk()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.success_loginSID(it)
                },{
                    viewState.noInternet()
                })

        )


    }

    fun connect () {
        varApp.appComponent.inject(this)

//        varApp.setSID( get("${varApp.url}connect").text)

        disposeBag.add(
            backendModule.provideMyGet().connect()
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    viewState.success_connect(it)
                }, {
                    viewState.noInternet()
                })
        )

    }

    fun iam () {
        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().iam(varApp.SID)
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    iam.clear()
                    iam.add(it)
                    if (backendModule.getCartBadge() == AppResponse.noInternet) viewState.noInternet()
                    if (backendModule.getFavoriteBadge() == AppResponse.noInternet) viewState.noInternet()
                    viewState.onSuccess()
                }, {
                    viewState.noInternet()
                })
        )

    }



    fun reconnect (email: String, psw: String) {
        varApp.appComponent.inject(this)

        disposeBag.add(

            backendModule.provideMyGet().connect()
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({


                    varApp.setSID(it)
                    varApp.setSessionId_Preferences(it)



                    backendModule.provideRetrofit(url = "${varApp.url}login/?u=${email}&p=${psw}&sid=${varApp.SID}").create(myGET::class.java) .setOk()
                        .delay(varApp.delay_default, varApp.typeUnit)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({

                            if (it != "ok") {
                                varApp.clear_LoginPassword_Preferences()
                                varApp.setLoginPassword_Preferences(mapOf(email to psw))
                                varApp.setiam()
                            } else {

                            }

                        },{
                            viewState.noInternet()
                        })


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
