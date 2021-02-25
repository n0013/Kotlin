package ru.isantur.santurshop.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.include_appbar.*
import kotlinx.android.synthetic.main.include_status.*
import kotlinx.android.synthetic.main.profile_registration.*
import moxy.InjectViewState
import moxy.MvpAppCompatFragment
import moxy.MvpPresenter
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.isantur.santurshop.R
import ru.isantur.santurshop.Retrofit.myGET
import ru.isantur.santurshop.Utils
import ru.isantur.santurshop.activity.Main
import ru.isantur.santurshop.anim
import ru.isantur.santurshop.di.BackendModule
import ru.isantur.santurshop.varApp
import javax.inject.Inject

@StateStrategyType(value = OneExecutionStateStrategy::class)
interface ProfileQuickRegistration_ViewState: MvpView {

    fun onSuccess()
    fun onSuccess_AccountReg (resp: String)
    fun noInternet()


}


class profile_quick_registration : MvpAppCompatFragment(), ProfileQuickRegistration_ViewState {

    @InjectPresenter
    lateinit var profile_quick_registration_presenter: profile_quick_registration_Presenter

    companion object {

        fun instance (): Fragment {
            return profile_quick_registration()
        }

        lateinit var v: View
        var js = ""

        var FirstName = ""
        var LastName = ""
        var Phone = ""
        var Email = ""


    }




    override fun onCreateView( inflater: LayoutInflater,  container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        v = inflater.inflate(R.layout.profile_registration, container, false)

        return v

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onSuccess()

        onEvents()


    }

    override fun onSuccess() {

        reg_status.visibility = View.GONE
        reg_pb.visibility = View.GONE
        reg_pb_body.visibility = View.GONE

        reg_page.visibility = View.VISIBLE
        reg_appbar.visibility = View.VISIBLE

        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = reg_appbar,
            customAppBar_backgroundColor = R.color.white,
            some_appbar_img_back = true,
            some_title_text = "Данные покупателя",
            some_title_textColor = R.color.type_black,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.type_black,
            some_btn_clear_visible = false
        )

        varApp.nav_view.visibility = View.VISIBLE

    }


    override fun onSuccess_AccountReg(resp: String) {

        onSuccess()

        if (resp.contains("ok")) {
            //success:

            //Показать диалоговое окно, что все ок(на почту пришел пароль) и перейти на форму входа

            //диалоговое окно:
            val builder = AlertDialog.Builder(v.context)
            builder.setTitle("Пароль\n").setMessage("На эл.адрес \"${reg_email.text}\" выслан пароль. \n\n Используйте его для входа в личный кабинет.").setCancelable(false)
                .setPositiveButton("Ок") { dlg, _ ->
                    dlg.dismiss()
                    //переходим на форму входа:
                    varApp.clearBackStack(varApp.supportFragmentManager)
                    varApp.nav_view.menu.getItem(4).setChecked(true)
                    varApp.replaceFragment(varApp.supportFragmentManager, profile_home.instance(), addAnimation = true, animation = anim.from_left_in_right)
                }

            val alert = builder.create()
            alert.show()

            //Exist
        } else if (resp.contains("exist")) {
            //Exist:  ( //уже зарегистрирован. Показываем error с кнопкой "Войти" )

            reg_base_error.visibility = View.VISIBLE
            reg_phone_error_2.visibility = View.VISIBLE
            reg_phone_error_3.visibility = View.VISIBLE
            reg_phone_error.text = "Профиль с таким номером уже существует."

            //TODO click error - input:
            reg_base_error.setOnClickListener {
                varApp.clearBackStack(varApp.supportFragmentManager)
                varApp.nav_view.menu.getItem(4).setChecked(true)
                varApp.replaceFragment(varApp.supportFragmentManager, profile_home.instance(), addAnimation = true, animation = anim.from_left_in_right)


            }

        } else {
            varApp.Toast(v.context, resp)
        }

    }


    override fun noInternet() {
        reg_pb.visibility = View.GONE
        reg_pb_body.visibility = View.GONE
        reg_page.visibility = View.GONE

        reg_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = reg_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = varApp.heading_no_internet,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)


        varApp.nav_view.visibility = View.GONE
        reg_status.visibility = View.VISIBLE
    }


    fun onProgress() {
        reg_pb.visibility = View.VISIBLE
        reg_pb_body.visibility = View.VISIBLE
        reg_page.visibility = View.GONE
        reg_appbar.visibility = View.GONE
        varApp.nav_view.visibility = View.GONE
        reg_status.visibility = View.GONE
    }


    fun onEvents() {

        //TODO click back
        some_appbar_img_back.setOnClickListener {
            varApp.delete_step_from_BackStack(varApp.supportFragmentManager, step = this::class.java.simpleName)
        }

        //TODO focus\click name
        reg_name.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) { reg_txt_name.error = null } }
        reg_name.setOnClickListener { reg_txt_name.error = null }

        //TODO focus\click phone
        reg_phone.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) { reg_base_error.visibility = View.GONE } }
        reg_phone.setOnClickListener { reg_base_error.visibility = View.GONE }

        //TODO focus\click email
        reg_email.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) { reg_txt_email.error = null } }
        reg_email.setOnClickListener { reg_txt_email.error = null }

        //TODO click update:
        status_update.setOnClickListener {
            onSuccess()
            onEvents()
        }

        //TODO click registration:
        reg_btn_reg.setOnClickListener {

            var error = false

            //сначала проверим все поля и выводим ошибки:

            // name
            if (reg_name.text.toString() == "") {
                YoYo.with(Techniques.Shake).duration(1000).playOn(reg_txt_name);  reg_txt_name.error = "Введите ваше имя";  error = true
            }

            // phone
            if (reg_phone.text.toString() == "") {
                YoYo.with(Techniques.Shake).duration(1000).playOn(reg_txt_phone)
                reg_base_error.visibility = View.VISIBLE
                reg_phone_error_2.visibility = View.GONE
                reg_phone_error_3.visibility = View.GONE
                YoYo.with(Techniques.Shake).duration(1000).playOn(reg_base_error)
                reg_phone_error.text = "Введите телефон"
                error = true
            }

            // email
            if (reg_email.text.toString() == "") {
                YoYo.with(Techniques.Shake).duration(1000).playOn(reg_txt_email);     reg_txt_email.error = "Введите Email";     error = true
            }


            if (! error) {

                onProgress()

                Utils.hideKeyboard(v.context, v)

                profile_quick_registration_presenter.AccountReg( reg_name.text.toString() , reg_email.text.toString(), reg_phone.text.toString() )


            }


        }

    }

}








//region PRESENTER

@InjectViewState
class profile_quick_registration_Presenter : MvpPresenter<ProfileQuickRegistration_ViewState>() {

    private val disposeBag = CompositeDisposable()

    @Inject
    lateinit var backendModule: BackendModule


    fun AccountReg (name: String, email: String, phone: String) {

        varApp.appComponent.inject(this)

//        if (! error) {
//            profile_quick_registration.js = khttp.get("${varApp.url}quickreg/?name=${reg_name.text}&email=${reg_email.text}&phone=${reg_phone.text}").text
//
//            if (profile_quick_registration.js.contains("ok")  ) {
//                //success:
//
//                varApp.clear_SessionId_Preferences()
//
//                //Connect:
//                val sessionId_new = get("${varApp.url}connect").text
//                varApp.setSID( sessionId_new )
//                varApp.setSessionId_Preferences( sessionId_new )
//
//                //Login:
//                val login = get("${varApp.url}login/?u=${reg_name.text}&p=${byEmail_psw.text}&sid=${varApp.SID}").text
//                if (login.contains("ok")) {
//                    println("login and password correct!!!")
//                    val map = mapOf(byEmail_email.text.toString() to byEmail_psw.text.toString())
//                    varApp.setLoginPassword_Preferences(map)
//
//                    //save iam:
//                    varApp.setiam()
//
//                    //успешно. На форму профиля
//                    findNavController().navigate(R.id.go_fromProfileRegistration_inProfileMy)
//                }
//
//
//
//
//
//            } else if (profile_quick_registration.js.contains("exist") ) {
//                //уже зарегистрирован. На форму входа.
//
//                reg_base_error.visibility = View.VISIBLE
//                reg_phone_error_2.visibility = View.VISIBLE
//                reg_phone_error_3.visibility = View.VISIBLE
//                reg_phone_error.text = "Профиль с таким номером уже существует."
//
//                reg_base_error.setOnClickListener {
//                    findNavController().navigate(R.id.go_fromProfileQuickRegistration_inProfileInput)
//                }
//
//
//            } else {
//                varApp.Toast(profile_quick_registration.v.context, profile_quick_registration.js)
//            }
//        }

        val query = "${varApp.url}quickreg/?name=${name}&email=${email}&phone=${phone}&sid=${varApp.SID}"

        disposeBag.add(
            backendModule.provideRetrofit(url = query )
                .create(myGET::class.java)
                .setOk()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it != "") {
                        viewState.onSuccess_AccountReg (it)
                    } else {
                        //что-то пошло не так
                    }

                },{
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