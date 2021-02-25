package ru.isantur.santurshop.fragment.profile

import android.app.AlertDialog
import android.os.Bundle
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.parseAsHtml
import androidx.fragment.app.Fragment
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.alert_dialog.view.*
import kotlinx.android.synthetic.main.include_appbar.*
import kotlinx.android.synthetic.main.include_status.*
import kotlinx.android.synthetic.main.info_delivery.*
import kotlinx.android.synthetic.main.info_delivery.delivery_pb
import kotlinx.android.synthetic.main.profile_change_pass.*
import kotlinx.android.synthetic.main.profile_data.*
import kotlinx.android.synthetic.main.profile_data.view.*
import kotlinx.android.synthetic.main.profile_input_email.*
import kotlinx.android.synthetic.main.profile_registration.*
import moxy.InjectViewState
import moxy.MvpAppCompatFragment
import moxy.MvpPresenter
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.isantur.santurshop.Data.Iam
import ru.isantur.santurshop.activity.Main
import ru.isantur.santurshop.R
import ru.isantur.santurshop.Retrofit.myGET
import ru.isantur.santurshop.Utils
import ru.isantur.santurshop.di.BackendModule
import ru.isantur.santurshop.fragment.profile.profile_data.Companion.v
import ru.isantur.santurshop.varApp
import javax.inject.Inject

@StateStrategyType(value = OneExecutionStateStrategy::class)
interface ProfileCnangePass_ViewState: MvpView {

    fun onSuccess()
    fun noInternet()
    fun onMessage (message: String)



}





class profile_change_pass : MvpAppCompatFragment(), ProfileCnangePass_ViewState {

    @InjectPresenter lateinit var profile_change_pass_presenter: profile_change_pass_Presenter

    companion object {

        fun instance (): Fragment {
            return profile_change_pass()
        }

        lateinit var v: View

    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        v =  inflater.inflate(R.layout.profile_change_pass, container, false)
        return v

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onSuccess()
        onEvents()


    }


    fun onEvents () {

        //TODO back click:
        some_addbar_view_back.setOnClickListener {
            varApp.delete_step_from_BackStack(varApp.supportFragmentManager, step = this::class.java.simpleName)
        }

        //TODO update click:
        status_update.setOnClickListener {
            onSuccess()
            onEvents()
        }

        //TODO focus\click old pass:
        profile_change_pass_old_pass.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) { profile_change_pass_txt_old_pass.error = null } }
        profile_change_pass_old_pass.setOnClickListener { profile_change_pass_txt_old_pass.error = null }

        //TODO focus\click new pass:
        profile_change_pass_new_pass.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) { profile_change_pass_txt_new_pass.error = null } }
        profile_change_pass_new_pass.setOnClickListener { profile_change_pass_txt_new_pass.error = null }

        //TODO focus\click confirm pass:
        profile_change_pass_confirm_pass.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) { profile_change_pass_txt_confirm_pass.error = null } }
        profile_change_pass_confirm_pass.setOnClickListener { profile_change_pass_txt_confirm_pass.error = null }


        //TODO change pass click:
        profile_change_pass_btn.setOnClickListener {

            reset_errors()

            var error = false

            // old pass (проверка на пустое значение):
            if (profile_change_pass_old_pass.text.toString() == "" ) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(profile_change_pass_txt_old_pass);  profile_change_pass_txt_old_pass.error = "Старый пароль должен быть заполнен";  error = true; return@setOnClickListener;
            }

            //не правильно введен старый пароль (сравниваем с сохраненным в shared preference):
            if (varApp.getPassword_Preferences() != profile_change_pass_old_pass.text.toString() ) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(profile_change_pass_txt_old_pass);  profile_change_pass_txt_old_pass.error = "Неверный старый пароль";  error = true; return@setOnClickListener;
            }

            // new pass (проверка на пустое значение):
            if (profile_change_pass_new_pass.text.toString() == "") {
                YoYo.with(Techniques.Shake).duration(1000).playOn(profile_change_pass_txt_new_pass);  profile_change_pass_txt_new_pass.error = "Необходимо заполнить новый пароль";  error = true; return@setOnClickListener;
            }

            //Введите более надежный пароль – не менее 6 символов:
            if ( profile_change_pass_new_pass.text.toString().length < 6 ) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(profile_change_pass_txt_new_pass);  profile_change_pass_txt_new_pass.error = "Введите более надежный пароль – не менее 6 символов";  error = true; return@setOnClickListener;
            }

            // confirm pass ((проверка на пустое значение):
            if (profile_change_pass_confirm_pass.text.toString() == "") {
                YoYo.with(Techniques.Shake).duration(1000).playOn(profile_change_pass_txt_confirm_pass);  profile_change_pass_txt_confirm_pass.error = "Повторите пароль";  error = true; return@setOnClickListener;
            }

            //новый и старый пароли не совпадают:
            if (profile_change_pass_new_pass.text.toString() != profile_change_pass_confirm_pass.text.toString() ) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(profile_change_pass_txt_confirm_pass);  profile_change_pass_txt_confirm_pass.error = "Пароли не совпадают";  error = true; return@setOnClickListener;
            }

            //нет ошибок, записываем пароли:
            if (! error) {
                profile_change_pass_presenter.save_pass(oldpass = profile_change_pass_old_pass.text.toString(), newpass = profile_change_pass_new_pass.text.toString() )
            }

        }

    }


    fun reset_errors () {
        profile_change_pass_txt_old_pass.error = null
        profile_change_pass_txt_new_pass.error = null
        profile_change_pass_txt_confirm_pass.error = null
    }

    override fun onSuccess() {

        //if not internet, then show page no internet:
        if (! varApp.isNetwork( varApp.appcontext ) ) {
            noInternet()
            return
        }

        profile_change_pass_pb.visibility = View.GONE
        profile_change_pass_status.visibility = View.GONE

        profile_change_pass_page.visibility = View.VISIBLE
        profile_change_pass_appbar.visibility = View.VISIBLE

        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = profile_change_pass_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = true,
            some_title_text = "Изменение пароля",
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)

        varApp.nav_view.visibility = View.VISIBLE

    }

    override fun noInternet() {

        profile_change_pass_pb.visibility = View.GONE
        profile_change_pass_page.visibility = View.GONE

        profile_change_pass_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar (
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = profile_change_pass_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = varApp.heading_no_internet,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false
        )


        profile_change_pass_status.visibility = View.VISIBLE
        varApp.nav_view.visibility = View.GONE


    }

    override fun onMessage(message: String) {
        varApp.showDialog(v, v.context, (activity as AppCompatActivity).supportActionBar!!, "Изменение пароля", message)
    }


}






//region PRESENTER

@InjectViewState
class profile_change_pass_Presenter : MvpPresenter<ProfileCnangePass_ViewState>() {

    private val disposeBag = CompositeDisposable()

    @Inject lateinit var backendModule: BackendModule

    fun save_pass (oldpass: String, newpass: String) {
        varApp.appComponent.inject(this)

//        changepsw/?oldpsw=старыйпароль&newpsw=новый&sid=

        disposeBag.add(
            backendModule.provideMyGet().changepsw(oldpass, newpass, varApp.SID)
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    println("ok")
                    if (it == "ok") {
                        reconnect(email = varApp.getLogin_Preferences(), psw = newpass)
                    } else {
                        viewState.onMessage("Произошла ошибка при изменении пароля. Попробуйте еще раз.")
                    }

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

                            varApp.clear_LoginPassword_Preferences()
                            varApp.setLoginPassword_Preferences( mapOf( email to psw ) )
                            varApp.setiam()

                            viewState.onMessage("Пароль изменен!")

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