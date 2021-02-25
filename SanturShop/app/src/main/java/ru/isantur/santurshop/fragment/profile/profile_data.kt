package ru.isantur.santurshop.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.android.synthetic.main.profile_data.*
import kotlinx.android.synthetic.main.profile_data.view.*
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
import ru.isantur.santurshop.activity.Main
import ru.isantur.santurshop.di.BackendModule
import ru.isantur.santurshop.fragment.profile.profile_data.Companion.v
import ru.isantur.santurshop.varApp
import javax.inject.Inject


@StateStrategyType(value = OneExecutionStateStrategy::class)
interface ProfileData_ViewState: MvpView {

    fun onSuccess()
    fun noInternet()
    fun onSuccessSaveProfile(resp: String)

}





class profile_data : MvpAppCompatFragment(), ProfileData_ViewState {

    @InjectPresenter lateinit var profile_data_presenter: profile_data_Presenter

    companion object {

        fun instance (): Fragment {
            return profile_data()
        }

        lateinit var v: View

    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        v =  inflater.inflate(R.layout.profile_data, container, false)
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

        //TODO focus\click name
        profile_data_name.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) { profile_data_txt_name.error = null } }
        profile_data_name.setOnClickListener { profile_data_txt_name.error = null }

        //TODO focus\click phone
        profile_data_phone.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) { profile_data_txt_phone.error = null } }
        profile_data_phone.setOnClickListener { profile_data_txt_phone.error = null }

        //TODO focus\click email
        profile_data_email.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) { profile_data_txt_email.error = null } }
        profile_data_email.setOnClickListener { profile_data_txt_email.error = null }




        //TODO save profile click:
        profile_data_btn.setOnClickListener {
            var error = false

            // name
            if (profile_data_name.text.toString() == "") {
                YoYo.with(Techniques.Shake).duration(1000).playOn(profile_data_txt_name);  profile_data_txt_name.error = "Введите ваше имя";  error = true
            }

            // phone
            if (profile_data_phone.text.toString() == "") {
                YoYo.with(Techniques.Shake).duration(1000).playOn(profile_data_txt_phone);  profile_data_txt_phone.error = "Введите ваш телефон";  error = true
            }

            // email
            if (profile_data_email.text.toString() == "") {
                YoYo.with(Techniques.Shake).duration(1000).playOn(profile_data_txt_email);  profile_data_txt_email.error = "Введите ваш email";  error = true
            }


            if (! error) {
                profile_data_presenter.save_profile(name = profile_data_name.text.toString(), phone = profile_data_phone.text.toString(), email = profile_data_email.text.toString())
            }

        }

    }

    override fun onSuccess() {

        //if not internet, then show page no internet:
        if (! varApp.isNetwork(varApp.appcontext) ) {
            noInternet()
            return
        }

        profile_data_name.setText(varApp.iam[0].name)


        profile_data_phone.setText(varApp.iam[0].phone)
        profile_data_email.setText(varApp.iam[0].email)


        profile_data_pb.visibility = View.GONE
        profile_data_status.visibility = View.GONE

        profile_data_page.visibility = View.VISIBLE
        profile_data_appbar.visibility = View.VISIBLE

        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = profile_data_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = true,
            some_title_text = "Данные профиля",
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)

        varApp.nav_view.visibility = View.VISIBLE

    }

    override fun noInternet() {

        profile_data_pb.visibility = View.GONE
        profile_data_page.visibility = View.GONE

        profile_data_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = profile_data_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = varApp.heading_no_internet,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)


        profile_data_status.visibility = View.VISIBLE
        varApp.nav_view.visibility = View.GONE


    }

    override fun onSuccessSaveProfile(resp: String) {

        if (resp == "phone is already taken") {
            varApp.showDialog(v, v.context, (activity as AppCompatActivity).supportActionBar!!, "Сохранение профиля", "Данный номер телефона уже занят. Попробуйте указать другой номер.")
        } else if (resp == "ok") {

            profile_data_presenter.reconnect(email = profile_data_email.text.toString(), psw = varApp.getPassword_Preferences())

            varApp.showDialog(v, v.context, (activity as AppCompatActivity).supportActionBar!!, "Сохранение профиля", "Профиль сохранен")

        }
    }


}






//region PRESENTER

@InjectViewState
class profile_data_Presenter : MvpPresenter<ProfileData_ViewState>() {

    private val disposeBag = CompositeDisposable()

    @Inject lateinit var backendModule: BackendModule

    fun save_profile(name: String, phone: String, email: String) {
        varApp.appComponent.inject(this)

//        changeprofile/?name=имя&phone=телефон&email=мыло&sid=
//        http://isantur.ru/mobile/changeprofile/?name=Евгений&email=ne0013@yandex.ru&phone=89530576532&sid=zwiwgbhyf0zvhwoiqpowsv1v

        disposeBag.add(backendModule.provideMyGet().changeprofile(name, phone, email, varApp.SID)
            .delay(varApp.delay_default, varApp.typeUnit)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState.onSuccessSaveProfile(it)
            }, {
                viewState.noInternet()
            }))

    }


    fun reconnect(email: String, psw: String) {
        varApp.appComponent.inject(this)


        disposeBag.add(

            backendModule.provideMyGet().connect().delay(varApp.delay_default, varApp.typeUnit).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe({

                varApp.setSID(it)
                varApp.setSessionId_Preferences(it)

                backendModule.provideRetrofit(url = "${varApp.url}login/?u=${email}&p=${psw}&sid=${varApp.SID}").create(myGET::class.java).setOk().delay(varApp.delay_default, varApp.typeUnit)
                    .subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe({

                        varApp.setiam()

                        v.profile_data_name.setText(varApp.iam[0].name)
                        v.profile_data_phone.setText(varApp.iam[0].phone)
                        v.profile_data_email.setText(varApp.iam[0].email)

                    }, {
                        viewState.noInternet()
                    })


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